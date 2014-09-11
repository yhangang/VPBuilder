/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.model;

import com.audaque.vp.activity.demo.DemoXmlEventDatabase;
import com.audaque.vp.activity.demo.DemoXmlVPDatabase;
import com.audaque.vp.activity.session.AccountIndexMapper;
import com.audaque.vp.activity.session.FixWinLenIndexSessionCreatorForGSEvents;
import com.audaque.vp.activity.session.SessionCreator;
import com.audaque.vp.utils.Benchmark;
import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import com.audaque.vp.utils.Filenames;
import com.audaque.vpbase.db.VPDatabase;
import com.audaque.vpbase.db.VPEventDatabase;
import com.audaque.vpbase.event.Event;
import com.google.common.base.Preconditions;
import edu.cmu.graphchi.apps.AdqConnectedComponents;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * ELite Model 仅返回相应"虚拟人ID$SPLIT$",用于初次进行Buck.
 *
 * @deprecated 仅作参考之用
 * @author Liyu.Cai@Audaque.com
 */
public class ActivityAnalysisLiteModel implements VPAnalysisModel<Iterator<Event>> {

    private final VPDatabase vpDb;
    private final VPEventDatabase eventDb;

    public ActivityAnalysisLiteModel(VPDatabase vpDb, VPEventDatabase eventDb) {
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
        final String EVENT_OPTION = "event";
        final String VP_OPTION = "vp";
        final String WINDOW_OPTION = "sec";
        final String NSHARD_OPTION = "n";
        /**
         * For local debugging only
         */
        Options opts = new Options();
        opts.addOption(EVENT_OPTION, true, "路径，原始登陆日志数据的文件路径");
        opts.addOption(VP_OPTION, true, "路径，虚拟人数据记录库");

        opts.addOption(WINDOW_OPTION, true, "秒钟数，在这个时间内终端上的操作都是同一个人做出的");
        opts.addOption(NSHARD_OPTION, true, "optional, the number of shards for componts detection");
        BasicParser parser = new BasicParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(opts, args);
        } catch (ParseException ex) {
            Logger.getLogger(ActivityAnalysisLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        String eventData = cl.getOptionValue(EVENT_OPTION);
        String vpData = cl.getOptionValue(VP_OPTION);

        String windowStr = cl.getOptionValue(WINDOW_OPTION);
        int window = Integer.valueOf(windowStr);

        int nShards = DEFAULT_NSHARDS;
        String nShardsStr = cl.getOptionValue(NSHARD_OPTION);
        if (nShardsStr != null) {
            nShards = Integer.valueOf(nShardsStr);
        }
        if (nShards > MAX_NSHARDS) {
            throw new IllegalArgumentException("nShards= " + nShards + " exceeds the maxNShards=" + MAX_NSHARDS);
        }

        VPEventDatabase eventReader = new DemoXmlEventDatabase(eventData);
        VPDatabase vpDb = new DemoXmlVPDatabase(vpData);

        ActivityAnalysisLiteModel model = new ActivityAnalysisLiteModel(vpDb, eventReader);
//        try {
        return model.process(eventData, window, nShards, DataFormats.SPLIT);
//        } catch (Exception ex) {
//            throw new IllegalStateException(ActivityAnalysisEliteModel.class.getName()+"运行异常:"+ex.getMessage());
//        }
    }

    public String process(String localBuffBase, int windowSeconds, int nShards, String split) {
        /*初始的帐号数目-越接近实际的帐号数目越好*/
        final int initAccountCount = 50000000;

        final int windowLen = windowSeconds * 1 * 1000;//2mins

        AccountIndexMapper mapper = new AccountIndexMapper(initAccountCount);

        Benchmark.start("CreateSessions");
        SessionCreator<String> creator = new FixWinLenIndexSessionCreatorForGSEvents(windowLen, eventDb.getEventsGroupByTerminalSortByTime(), mapper, split);

        String sessions = localBuffBase + ".sessions";
        creator.create(sessions);
        Benchmark.stop("CreateSessions");

        /**
         * 使用Graph-Chi组件进行Connected Components Detection.
         * <p>
         * 输出形如"虚拟人Id$SPLIT$AccountIndex"为行格式的记录文件.
         */
        Benchmark.start("ComponentDetection");
        String componentsByIndex = Filenames.getComponentsFile(sessions);
        AdqConnectedComponents.run(sessions, componentsByIndex, nShards, split);
        Benchmark.stop("ComponentDetection");

        String componToUniId = acontIndexToUniqueId(componentsByIndex, mapper, split);

        return componToUniId;

//        /**
//         * 对上述文件进行外存排序。这样能保证属于同一个虚拟人Id的行记录彼此相邻.
//         */
//        Benchmark.start("MergeIndexFile");
//        String indexMerged = DataFilenames.getMergeByComponents(componentsByIndex);
//        ExternalSort.sort(new File(componentsByIndex), new File(indexMerged));
//        Benchmark.stop("MergeIndexFile");
//
//        Benchmark.start("OutputComponts");
//        String compontes = assembleAccountComponents(indexMerged, mapper, DataFormats.SPLIT);
//        Benchmark.stop("OutputComponts");
//        return compontes;
    }

    /**
     * @deprecated 在本Model中未用到
     * @param mergeIndex
     * @param mapper
     * @param split
     * @return
     */
    private String assembleAccountComponents(String mergeIndex, AccountIndexMapper mapper, String split) {
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

//                                insertNewVpToVpDb(accountBuffer.toString().split(split));
                                compWriter.write(accountBuffer.toString());

                                cComp = comp;
                                accountBuffer.delete(0, accountBuffer.length());
                                accountBuffer.append(accountString).append(split);
                            }
                        } else {
                            cComp = comp;
                            accountBuffer.append(accountString).append(split);

                        }
                    } else {
                        /**
                         * DoNothing,GraphChi-java 存在一个Bug，它会输出多余的ID
                         */
                    }
                } else {
                    throw new IllegalStateException("component and account index read erro:" + compAndIndex);
                }
            }
            if (accountBuffer.length() > 0) {
                accountBuffer.delete(accountBuffer.length() - split.length(), accountBuffer.length()).append("\n");
//                insertNewVpToVpDb(accountBuffer.toString().split(split));
                compWriter.write(accountBuffer.toString());

            }
            compWriter.close();
            idxReader.close();
            return componets;
        } catch (IOException ex) {
            Logger.getLogger(ActivityAnalysisLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String acontIndexToUniqueId(String componentsByIndex,
            AccountIndexMapper mapper, String split) {
        BufferedReader indexReader = FileUtil.getBufferedReader(componentsByIndex);

        String componToUid = Filenames.getToAcontUid(componentsByIndex);
        BufferedWriter uIdWriter = FileUtil.getBufferedWriter(componToUid);

        try {

            String componToIndex = null;
            StringBuilder bd = new StringBuilder(20);
            while ((componToIndex = indexReader.readLine()) != null) {
                String[] fields = componToIndex.split(split);

                Preconditions.checkState(fields.length == 2);

                Integer index = Integer.valueOf(fields[1]);
                if (index <= mapper.maxIndex) {
                    bd.append(fields[0]).append(split)
                            .append(mapper.getAccountString(index)).append("\n");
                    uIdWriter.write(bd.toString());
                    bd.delete(0, bd.length());
                } else {
                    /**
                     * Do nothing. GraphiChi中存在Bug，会输出本来不存在的Id，故过滤这些。
                     */
                }

            }
            uIdWriter.close();
            return componToUid;
        } catch (IOException ex) {
            throw new IllegalStateException("ComponetToIndex文件转换为ComponentToUniId文件错误:" + ex.getMessage());
        }

    }

    @Override
    public boolean done() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean createInitMiddleFile(String middleFile) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getUpdateMiddleFile(Iterator<Event> incrementalData) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
