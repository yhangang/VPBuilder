/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.model;

import com.audaque.vp.activity.session.AccountIndexMapper;
import com.audaque.vp.utils.Benchmark;
import com.audaque.vp.activity.session.SessionToSessionIndexTransformer;
import com.audaque.vp.activity.demo.DemoJsonVPEventDatabase;
import com.audaque.vp.activity.demo.DemoOnScreenVPDatabase;
import com.audaque.vp.activity.session.FixWinLenSessionCreatorInGeneral;
import com.audaque.vp.activity.session.SessionCreator;
import com.audaque.vp.utils.Filenames;
import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import com.audaque.vpbase.db.VPDatabase;
import com.audaque.vpbase.db.VPEventDatabase;
import com.google.code.externalsorting.ExternalSort;
import edu.cmu.graphchi.apps.AdqConnectedComponents;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * 该版本为最早的 行为分析模块, 包含了整个流程的版本.
 *
 * @deprecated 性能过时且与新的模块划分不符合. 仅保留作参考之用.
 * @author Liyu.Cai@Audaque.com
 */
public class ActivityAnalysisClassicalModel implements VPAnalysisModel {

    private final VPDatabase vpDb;
    private final VPEventDatabase eventDb;

    public ActivityAnalysisClassicalModel(VPDatabase vpDb, VPEventDatabase eventDb) {
        this.vpDb = vpDb;
        this.eventDb = eventDb;
    }

    private static final int DEFAULT_NSHARDS = 2;
    public static final int MAX_NSHARDS = 100;

    /**
     *
     * @param input
     * @return
     */
    public static String run(String[] args) {
        final String DATA_OPTION = "data";
        final String WINDOW_OPTION = "sec";
        final String NSHARD_OPTION = "n";

        Options opts = new Options();
        opts.addOption(DATA_OPTION, true, "路径，原始登陆日志数据的文件路径");
        opts.addOption(WINDOW_OPTION, true, "秒钟数，在这个时间内终端上的操作都是同一个人做出的");
        opts.addOption(NSHARD_OPTION, true, "optional, the number of shards for componts detection");
        BasicParser parser = new BasicParser();

        CommandLine cl = null;
        try {
            cl = parser.parse(opts, args);
        } catch (ParseException ex) {
            Logger.getLogger(ActivityAnalysisClassicalModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        String baseData = cl.getOptionValue(DATA_OPTION);
        String windowStr = cl.getOptionValue(WINDOW_OPTION);
        int window = Integer.valueOf(windowStr);

        int nShards = DEFAULT_NSHARDS;
        String nShardsStr = cl.getOptionValue(NSHARD_OPTION);
        if (nShardsStr != null) {
            nShards = Integer.valueOf(nShardsStr);
        }

        if (nShards > MAX_NSHARDS) {
            throw new IllegalArgumentException("nShards=" + nShards + " exceeds the maxNShards=" + MAX_NSHARDS);
        }

        VPEventDatabase eventReader = new DemoJsonVPEventDatabase(baseData);
        VPDatabase vpDb = new DemoOnScreenVPDatabase();

        ActivityAnalysisClassicalModel model = new ActivityAnalysisClassicalModel(vpDb, eventReader);
        try {
            return model.process(baseData, window, nShards);
        } catch (IOException ex) {
            throw new IllegalStateException(ActivityAnalysisClassicalModel.class.getName() + " run failures:" + ex.getMessage());
        }
    }

    private String process(String baseData, int window, int nShards) throws IOException {
        final int initAccountCount = 5000;

        //produce session data
        Benchmark.start("LoadRawData");
        final int windowLength = window * 1 * 1000;//2mins

        /**
         * 创建帐号的Session记录文件.
         */
        SessionCreator<String> c = new FixWinLenSessionCreatorInGeneral(baseData, windowLength, eventDb, DataFormats.SPLIT);

        String session = Filenames.getSessionFile(baseData);
        c.create(session);
        Benchmark.stop("LoadRawData");

        /**
         * 将帐号用对应的Index代替，并输出相应的Session记录文件.
         */
        Benchmark.start("SessionIndex");
        AccountIndexMapper mapper = new AccountIndexMapper(initAccountCount);
        SessionToSessionIndexTransformer transformer = new SessionToSessionIndexTransformer(initAccountCount, mapper);
        String indexSession = transformer.forward(session);
        //mapper.toFile(session);
        Benchmark.stop("SessionIndex");

        /**
         * 使用Graph-Chi组建进行Connected Components Detection. 输出形如"虚拟人Id SPLIT
         * 帐号Index"为行格式的记录文件.
         */
        Benchmark.start("ComponentDetection");
        String componentsByIndex = Filenames.getComponentsFile(indexSession);
        AdqConnectedComponents.run(indexSession, componentsByIndex, nShards, DataFormats.SPLIT);
        Benchmark.stop("ComponentDetection");

        /**
         * 对上述文件进行外存排序。这样能保证属于同一个虚拟人Id的行记录彼此相邻.
         */
        Benchmark.start("MergeIndexFile");
        String indexMerged = Filenames.getMergeByComponents(componentsByIndex);
        ExternalSort.sort(new File(componentsByIndex), new File(indexMerged));
        Benchmark.stop("MergeIndexFile");

        Benchmark.start("OutputComponts");
        String compontes = assembleAccountComponents(indexMerged, mapper, DataFormats.SPLIT);
        Benchmark.stop("OutputComponts");
        return compontes;
    }

    public String assembleAccountComponents(String mergeIndex, AccountIndexMapper mapper, String split) {
        BufferedReader idxReader = FileUtil.getBufferedReader(mergeIndex);

        String componets = Filenames.getComponentsFile(mergeIndex);
        BufferedWriter compWriter = FileUtil.getBufferedWriter(componets);
        try {
            StringBuilder accountBuffer = new StringBuilder();
            String compAndIndex = null;

            String cComp = null;
            while ((compAndIndex = idxReader.readLine()) != null) {
                String[] fields = compAndIndex.split(split);
                if (fields.length == 2) {
                    String comp = fields[0];
                    int accountIndex = Integer.valueOf(fields[1]);
                    if (accountIndex <= mapper.maxIndex) {
                        String accountString = mapper.getAccountString(accountIndex);
                        if (cComp != null) {
                            if (cComp.equals(comp)) {
                                accountBuffer.append(accountString).append(split);
                            } else {
                                accountBuffer.delete(
                                        accountBuffer.length() - split.length(), accountBuffer.length())
                                        .append("\n");

                                compWriter.write(accountBuffer.toString());

                                cComp = comp;
                                accountBuffer.delete(0, accountBuffer.length());
                                accountBuffer.append(accountString).append(split);
                            }
                        } else {
                            cComp = comp;
                            accountBuffer.append(accountString).append(split);
                        }
                    }
                } else {
                    throw new IllegalStateException("component and account index read erro:" + compAndIndex);
                }
            }
            if (accountBuffer.length() > 0) {
                accountBuffer.delete(accountBuffer.length() - split.length(), accountBuffer.length()).append("\n");
                compWriter.write(accountBuffer.toString());

            }
            idxReader.close();
            return componets;
        } catch (IOException ex) {
            Logger.getLogger(ActivityAnalysisClassicalModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @param args
     * @return
     */
    public boolean createInitMiddleFile(String middleFile) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean done() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getUpdateMiddleFile(Object incrementalData) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
