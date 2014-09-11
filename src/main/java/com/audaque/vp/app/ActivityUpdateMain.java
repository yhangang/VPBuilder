/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.app;

import com.audaque.vp.activity.session.AccountIndexMapper;
import com.audaque.vp.activity.session.FixWinLenIndexSessionCreatorForGSEvents;
import com.audaque.vp.activity.session.SessionCreator;
import com.audaque.vp.newmodel.MutipleMidFileReader;
import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import com.audaque.vp.utils.Filenames;
import com.audaque.vpbase.db.VPEventUpdateDatabase;
import com.audaque.vpbase.db.VPUpdateDatabase;
import com.audaque.vpbase.event.Event;
import com.google.code.externalsorting.ExternalSort;
import com.google.common.base.Preconditions;
import edu.cmu.graphchi.apps.AdqConnectedComponents;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @author Audaque
 */
public class ActivityUpdateMain {
    /*初始的要更新帐号数目-越接近实际的帐号数目越好*/

    private static final int INIT_ACCOUNT_COUNT = 5_000_000;

    public boolean updateVPDb(VPEventUpdateDatabase eDb, VPUpdateDatabase vDb, int windSec, int nShards, String localBufferFolder, boolean delBuffers) {
        checkNotExistsThenCreate(localBufferFolder);

        final int initAccountCount = INIT_ACCOUNT_COUNT;
        final String split = DataFormats.SPLIT;
        String localBuffBase = localBufferFolder + "base";
        Iterator<Event> newEvents = eDb.getUpdateEvents();
        AccountIndexMapper mapper = new AccountIndexMapper(initAccountCount);

        SessionCreator<String> creator = new FixWinLenIndexSessionCreatorForGSEvents(windSec * 1000, newEvents, mapper, split);
        String sessions = Filenames.getSessionFile(localBuffBase);
        creator.create(sessions);

        /*使用Graph-Chi组件进行Connected Components Detection,输出形如"虚拟人Id$SPLIT$AccountIndex"为行格式的记录文件.*/
        String componentsByIndex = Filenames.getComponentsFile(sessions);
        AdqConnectedComponents.run(sessions, componentsByIndex, nShards, split);

        /*对上述文件进行外存排序，使得属于同一个虚拟人Id的行记录彼此相邻.*/
        String indexMerged = Filenames.getMergeByComponents(componentsByIndex);
        try {
            ExternalSort.sort(new File(componentsByIndex), new File(indexMerged));
        } catch (IOException ex) {
            throw new IllegalStateException("ExternalSort exception:" + ex.getMessage());
        }

        String compontes = null;
        try {
            compontes = assembleAccountComponents(indexMerged, mapper, split);
        } catch (IOException ex) {
            throw new IllegalStateException("Assemble account componentes exception:" + ex.getMessage());
        }

        VPDbUpdateMethods.liteUpdate(vDb, new MutipleMidFileReader(compontes, split).VPs());
        if (delBuffers) {
            deleteBufferFolder(localBufferFolder);
        }
        return true;
    }

    private void deleteBufferFolder(String bufferFolder) {
        System.out.println("deleting local buffer folder：" + bufferFolder);
        FileUtil.deleteIfExists(bufferFolder);
    }

    private void checkNotExistsThenCreate(String localBufferFolder) {
        File file = new File(localBufferFolder);
        if (file.exists()) {
            throw new IllegalArgumentException("本地缓存已经存在:" + localBufferFolder);
        } else {
            file.mkdirs();
        }
    }

    /**
     * @param mergeIndex
     * @param mapper
     * @param split
     * @return
     */
    private String assembleAccountComponents(String mergeIndex, AccountIndexMapper mapper, String split) throws IOException {
        BufferedReader idxReader = FileUtil.getBufferedReader(mergeIndex);

        String componets = Filenames.getComponentsFile(mergeIndex);
        BufferedWriter compWriter = FileUtil.getBufferedWriter(componets);
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
                } else {
                    /**
                     * DoNothing,GraphChi-java 存在一个Bug，它会输出多余的ID
                     */
                }
            } else {
                throw new IllegalStateException("Read component and account index erro:" + compAndIndex);
            }
        }
        if (accountBuffer.length() > 0) {
            accountBuffer.delete(accountBuffer.length() - split.length(), accountBuffer.length()).append("\n");
            compWriter.write(accountBuffer.toString());
        }
        compWriter.close();
        idxReader.close();
        return componets;
    }

//    private String acontIndexToUniqueId(String componentsByIndex,
//            AccountIndexMapper mapper, String split) {
//        BufferedReader indexReader = FileUtil.getBufferedReader(componentsByIndex);
//
//        String componToUid = Filenames.getToAcontUid(componentsByIndex);
//        BufferedWriter uIdWriter = FileUtil.getBufferedWriter(componToUid);
//
//        try {
//
//            String componToIndex = null;
//            StringBuilder bd = new StringBuilder(20);
//            while ((componToIndex = indexReader.readLine()) != null) {
//                String[] fields = componToIndex.split(split);
//
//                Preconditions.checkState(fields.length == 2);
//
//                Integer index = Integer.valueOf(fields[1]);
//                if (index <= mapper.maxIndex) {
//                    bd.append(fields[0]).append(split)
//                            .append(mapper.getAccountString(index)).append("\n");
//                    uIdWriter.write(bd.toString());
//                    bd.delete(0, bd.length());
//                } else {
//                    /**
//                     * Do nothing. GraphiChi中存在Bug，会输出本来不存在的Id，故过滤这些。
//                     */
//                }
//
//            }
//            uIdWriter.close();
//            return componToUid;
//        } catch (IOException ex) {
//            throw new IllegalStateException("ComponetToIndex文件转换为ComponentToUniId文件错误:" + ex.getMessage());
//        }
//
//    }
}
