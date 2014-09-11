/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.newmodel;

import com.audaque.vp.model.VPDbUpdateMethods;
import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.db.VPDatabase;
import com.audaque.vpbase.db.VPEventDatabase;
import com.audaque.vpbase.event.Event;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 负责对虚拟人数据库进行更新/增量计算.
 *
 * @deprecated 未使用
 * @author Liyu.Cai@Audaque.com
 */
public class VPDbUpdater {

    private final String localBufferFolder;
    private final String localBufferBaseFile;
    private final VPUpdateModel firstModel;
    private final VPUpdateModel[] otherModels;

    public VPDbUpdater(String localBufferFolder, VPUpdateModel firstModel, VPUpdateModel... otherModels) {
        checkIfNotExistsThenCreate(localBufferFolder);

        this.localBufferFolder = localBufferFolder;
        this.localBufferBaseFile = this.localBufferFolder + File.separator + "base";

        this.firstModel = firstModel;
        this.otherModels = otherModels;
    }

    private void checkIfNotExistsThenCreate(String localBufferFolder) {
        File file = new File(localBufferFolder);
        if (file.exists()) {
            throw new IllegalArgumentException("本地缓存已经存在:" + localBufferFolder);
        } else {
            file.mkdirs();
        }
    }

    /**
     * 使用增量数据来对虚拟人数据库进行更新.
     *
     * @param vpDb
     * @param vpEventDb
     * @param updatedAccounts
     * @param newEvents
     * @return
     */
    public boolean updateVPDatabase(VPDatabase vpDb, VPEventDatabase vpEventDb, Iterator<Account> updatedAccounts,
            Iterator<Event> newEvents) {
        /**
         * retrive middle files/results from the models
         */
        ArrayList<String> newMidFiles = new ArrayList<String>(1 + (otherModels == null ? otherModels.length : 0));
        firstModel.beforeModlling();
        newMidFiles.add(firstModel.getUpdateMiddleFile());
        firstModel.afterModlling();

        if (otherModels != null) {
            for (VPUpdateModel model : otherModels) {
                model.beforeModlling();
                newMidFiles.add(model.getUpdateMiddleFile());
                model.afterModlling();
            }
        }

        VPDbUpdateMethods.liteUpdate(vpDb, new MutipleMidFileReader(newMidFiles, "\t").VPs());

        return false;
    }
}
