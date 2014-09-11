/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.newmodel;

import com.audaque.vp.activity.session.FixWinLenSessionCreatorForGSEvents;
import com.audaque.vp.activity.session.SessionCreator;
import com.audaque.vp.utils.Filenames;
import com.audaque.vpbase.event.Event;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class ActivityAnalyApproximateUpdateModel implements VPUpdateModel {

    private final Iterator<Event> incrementalEvents;
    private final int windowLen;
    private final String split;
    private final String localBufferFolder;
    private final String localBufferBaseFile;

    public ActivityAnalyApproximateUpdateModel(Iterator<Event> newEvents, int windowLen, String split, String localBufferFolder) {
        checkIsNotExistsThenCreate(localBufferFolder);

        this.incrementalEvents = newEvents;

        this.windowLen = windowLen;
        this.split = split;
        this.localBufferFolder = localBufferFolder;
        this.localBufferBaseFile = localBufferFolder + File.separator + "base";
    }

    @Override
    public String getUpdateMiddleFile() {
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
    public void beforeModlling() {
        System.out.println("ActivityAnalyUpdateModel:beforeModlling");
    }

    @Override
    public void afterModlling() {
        System.out.println("ActivityAnalyUpdateModel:afterModlling");
    }

    private void checkIsNotExistsThenCreate(String localBufferFolder) {
        File file = new File(localBufferFolder);
        if (file.exists()) {
            throw new IllegalArgumentException("本地缓存已经存在:" + localBufferFolder);
        } else {
            file.mkdirs();
        }
    }
}
