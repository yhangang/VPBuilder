/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.newmodel;

import com.audaque.vp.utils.Benchmark;
import com.audaque.vp.activity.session.FixWinLenSessionCreatorForGSEvents;
import com.audaque.vp.activity.session.SessionCreator;
import com.audaque.vp.utils.Filenames;
import com.audaque.vpbase.db.VPEventDatabase;
import com.audaque.vpbase.event.Event;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class ActivityAnalyInitModel implements VPInitModel {

    private final VPEventDatabase eventDb;
    private final int windowLen;
    private final String split;
    private final String localBufferPath;
    private final String localBufferBaseFile;

    public ActivityAnalyInitModel(VPEventDatabase eventDb, int windowLen, String split, String localBufferFolder) {
        checkIsNotExistsThenCreate(localBufferFolder);

        this.eventDb = eventDb;
        this.windowLen = windowLen * 1000;

        this.split = split;
        this.localBufferPath = localBufferFolder;
        this.localBufferBaseFile = localBufferFolder + File.separator + "base";
    }

    /**
     *
     * @return
     */
    @Override
    public String getInitMiddleFile() {
        Benchmark.start("CreateSessions");
        Iterator<Event> events = eventDb.getEventsGroupByTerminalSortByTime();
        SessionCreator<String> creator = new FixWinLenSessionCreatorForGSEvents(localBufferBaseFile, windowLen, events, split);

        String sessions = Filenames.getSessionFile(localBufferBaseFile);
        creator.create(sessions);
        return sessions;
    }

    @Override
    public void beforeModlling() {
        System.out.println("ActivityAnalyInitModel:beforeModlling");
    }

    @Override
    public void afterModlling() {
        System.out.println("ActivityAnalyInitModel:afterModlling");
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
