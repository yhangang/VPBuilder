/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.model;

import com.audaque.vp.activity.session.FixWinLenSessionCreatorForGSEvents;
import com.audaque.vp.activity.session.SessionCreator;
import com.audaque.vp.newmodel.ActivityAnalyInitModel;
import com.audaque.vp.utils.Filenames;
import com.audaque.vpbase.db.VPEventDatabase;
import com.audaque.vpbase.event.Event;
import java.io.File;
import java.util.Iterator;

/**
 * 基于行为的模块性
 *
 * @deprecated 仅作参考用，将被 com.audaque.vp.newmodel.AtivityAnalyInitModel取代.
 * @see ActivityAnalyInitModel
 * @author Liyu.Cai@Audaque.com
 */
public class ActivityAnalysisInitModel implements VPAnalysisModel<Iterator<Event>> {

    private final VPEventDatabase eventDb;
    private final int windowLen;
    private final String split;
    private final String localBufferPath;
    private final String localBufferBaseFile;

    public ActivityAnalysisInitModel(VPEventDatabase eventDb, int windowLen, String split, String localBufferFolder) {
        checkIsNotExistsThenCreate(localBufferFolder);
        this.eventDb = eventDb;
        this.windowLen = windowLen * 1000;
        this.split = split;
        this.localBufferPath = localBufferFolder;
        this.localBufferBaseFile = this.localBufferPath + File.separator + "base";
    }

    private void checkIsNotExistsThenCreate(String localBufferFolder) {
//        FileUtils.deleteIfExists(localBufferFolder);

        File file = new File(localBufferFolder);
        if (file.exists()) {
            throw new IllegalArgumentException("本地缓存已经存在:" + localBufferFolder);
        } else {
            file.mkdirs();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean createInitMiddleFile(String middleFile) {
        Iterator<Event> events = eventDb.getEventsGroupByTerminalSortByTime();
        SessionCreator<String> creator = new FixWinLenSessionCreatorForGSEvents(localBufferBaseFile, windowLen, events, split);
        if (creator.create(middleFile)) {
            return true;
        }
        return false;
    }

    @Override
    public String getUpdateMiddleFile(Iterator<Event> incrementalEvents) {
        /**
         * 一个近似计算方案，精确计算Delta VP需调回部分已存的记录并重新进行更新.
         *
         */
        SessionCreator<String> creator = new FixWinLenSessionCreatorForGSEvents(localBufferBaseFile, windowLen, incrementalEvents, split);
        String sessions = Filenames.getSessionFile(localBufferBaseFile);
        creator.create(sessions);
        return sessions;
    }

    @Override
    public boolean done() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
