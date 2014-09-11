/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.model;

import com.audaque.vp.activity.session.AccountIndexMapper;
import com.audaque.vp.utils.Benchmark;
import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import com.audaque.vp.utils.Filenames;
import com.audaque.vpbase.db.VPDatabase;
import com.audaque.vpbase.db.VPEventDatabase;
import com.google.common.base.Preconditions;
import edu.cmu.graphchi.apps.AdqConnectedComponents;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * 虚拟人建立--将多个模型的结果结合起来，并完成对VPDatabase的更新.
 *
 * @author Liyu.Cai@Audaque.com
 */
public class VPBuilder {

    private static final int DEFAULT_INIT_ACCOUNT_COUNTS = 500000;
    private static final int DEFAULT_NSHARDS = 3;
    private static final String ACCOUNTS_SPLIT = DataFormats.SPLIT;
    private final String localBufferFolder;
    private final String localBufferBaseFile;
    private final VPAnalysisModel firstModel;
    private final VPAnalysisModel[] otherModels;

    public VPBuilder(String localBufferFolder, VPAnalysisModel firstModel, VPAnalysisModel... otherModels) {
        checkIsNotExistsThenCreate(localBufferFolder);

        this.localBufferFolder = localBufferFolder;
        this.localBufferBaseFile = this.localBufferFolder + File.separator + "base";

        this.firstModel = firstModel;
        this.otherModels = otherModels;
    }

    /**
     * 初始化一个VP数据库--即全量计算.
     *
     * @param vpDb 虚拟人数据库
     * @param vpEvents 虚拟人事件数据库
     * @return 更新是否成功
     */
    public boolean initVPDatabase(VPDatabase vpDb, VPEventDatabase vpEvents) {
        /**
         * retrive middle files/results from the models
         */
        ArrayList<String> midFiles = new ArrayList<>(1 + (otherModels == null ? otherModels.length : 0));

        String middleFile = Filenames.getSessionFile(localBufferBaseFile);
        if (firstModel.createInitMiddleFile(middleFile)) {
            midFiles.add(middleFile);
        }

        if (otherModels != null) {
            for (int i = 0; i < otherModels.length; i++) {
                middleFile = Filenames.getSessionFile(localBufferBaseFile + "." + i);
                if (firstModel.createInitMiddleFile(middleFile)) {
                    midFiles.add(middleFile);
                }
            }
        }

        AccountIndexMapper mapper = new AccountIndexMapper(DEFAULT_INIT_ACCOUNT_COUNTS);//
        String aconIndexAjdList = forward(midFiles, mapper);

        /**
         * 使用Graph-Chi组件进行Connected Components Detection. 输出形如"虚拟人Id SPLIT
         * 帐号Index"为行格式的记录文件.
         */
        Benchmark.start("ComponentDetection");
        String componentsByIndex = Filenames.getComponentsFile(aconIndexAjdList);
        AdqConnectedComponents.run(aconIndexAjdList, componentsByIndex, DEFAULT_NSHARDS, DataFormats.SPLIT);
        Benchmark.stop("ComponentDetection");

        boolean succeed = initVPDatabase(vpDb, componentsByIndex, mapper, DataFormats.SPLIT);

//        FileUtil.deleteIfExists(localBufferFolder);
        return succeed;
    }

    private String forward(Collection<String> midFiles, AccountIndexMapper mapper) {
        try {
            String sessionInIndex = localBufferBaseFile + ".index";
            BufferedWriter bw = FileUtil.getBufferedWriter(sessionInIndex);
            for (String midFile : midFiles) {
                forward(FileUtil.getBufferedReader(midFile), bw, mapper);
            }
            bw.close();
            return sessionInIndex;
        } catch (IOException ex) {
            throw new IllegalStateException("关闭中间文件的Index对象异常:" + ex.getMessage());
        }
    }

    private void forward(BufferedReader sReader, BufferedWriter sIdxWriter, AccountIndexMapper mapper) {
        try {
            String session = null;
            StringBuilder bd = new StringBuilder();
            while ((session = sReader.readLine()) != null) {
                String[] aIds = DataFormats.sessionStringToAccountIds(session);

                int firstIndex = mapper.putIndex(aIds[0]);
                if (aIds.length == 1) {
                    bd.append(firstIndex).append(ACCOUNTS_SPLIT).append(1).append(ACCOUNTS_SPLIT).append(firstIndex);
                } else if (aIds.length > 1) {
                    bd.append(firstIndex).append(ACCOUNTS_SPLIT).append(aIds.length - 1);
                    for (int i = 1; i < aIds.length; i++) {
                        bd.append(ACCOUNTS_SPLIT).append(mapper.putIndex(aIds[i]));
                    }
                }
                bd.append("\n");
                sIdxWriter.write(bd.toString());
                bd.delete(0, bd.length());
            }
            sReader.close();
        } catch (IOException ex) {
            throw new IllegalStateException("合并MiddleFile文件错误:" + ex.getMessage());
        }
    }

    private void checkIsNotExistsThenCreate(String localBufferFolder) {
        File file = new File(localBufferFolder);
        if (file.exists()) {
            throw new IllegalArgumentException("本地缓存已经存在:" + localBufferFolder);
        } else {
            file.mkdirs();
        }
    }

    private boolean initVPDatabase(VPDatabase vpDb, String initialVpToAcont, AccountIndexMapper mapper, String split) {
        try {
            BufferedReader vpToAcontIdxReader = FileUtil.getBufferedReader(initialVpToAcont);

            String vpToAcontIdx = null;
            StringBuilder bd = new StringBuilder(20);

            vpDb.beforeInitVP();
            while ((vpToAcontIdx = vpToAcontIdxReader.readLine()) != null) {
                if (vpToAcontIdx.length() > 2) {
                    String[] fields = vpToAcontIdx.split(split);
                    Preconditions.checkState(fields.length == 2, "输入文件格式错误");

                    int index = Integer.valueOf(fields[1]);
                    if (index <= mapper.maxIndex) {
                        String acontUniId = mapper.getAccountString(Integer.valueOf(fields[1]));
                        Preconditions.checkState(acontUniId != null, "未找到Index对应的Account UniqueId");

                        bd.append(fields[0]).append(split).append(acontUniId);
                        if (!vpDb.addInitVP(bd.toString(), split)) {
                            Logger.getLogger(this.getClass().getName()).info("!!!初始化虚拟人数据库失败");
                            return false;
                        }
                        bd.delete(0, bd.length());
                    }
                }
            }
            vpDb.afterInitVP();

        } catch (IOException ex) {
            throw new IllegalStateException("初始化VPDb失败:" + ex.getMessage());
        }
        return true;
    }
}
