/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.session;

import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import com.audaque.vpbase.db.VPEventDatabase;
import com.audaque.vpbase.event.Event;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 以固定的时长来判断多个帐号是否同时在线。
 * <p>
 * 比如，在同一Terminal上5分钟内都有活动的帐号会被划分到同一个Session中。</p>
 *
 * @author Liyu.Cai@Audaque.com
 */
public class FixWinLenSessionCreatorInGeneral implements SessionCreator<String> {

    private final String baseFile;
    /**
     * 时长,以毫秒计数.
     */
    private final int windowLength;
    private final VPEventDatabase eventReader;
    private final String accountTypeIdSplit;

    public FixWinLenSessionCreatorInGeneral(String baseFile, int windowLength, VPEventDatabase eventsReader, String accountTypeIdSplit) {
        this.baseFile = baseFile;
        this.windowLength = windowLength;
        this.eventReader = eventsReader;
        this.accountTypeIdSplit = accountTypeIdSplit;
    }

    @Override
    public boolean create(String sFile) {
        BufferedWriter sessions = null;
        try {
            sessions = FileUtil.getBufferedWriter(sFile);

            HashMap<String, Long> latestTimeBuffer = new HashMap<String, Long>();
            HashMap<String, ArrayList<String>> sessionBuffer = new HashMap<String, ArrayList<String>>();

            Iterator<Event> it = eventReader.getEventsGroupByTerminalSortByTime();
            while (it.hasNext()) {
                Event e = it.next();
                String terminal = e.getTerminalId();
                Long lastTimeBuffer = latestTimeBuffer.get(terminal);
                if (lastTimeBuffer == null) {
                    sessionBuffer.put(terminal, new ArrayList<String>());
                    lastTimeBuffer = e.getTimeStamp().offSet();
                }
                if (e.getTimeStamp().offSet() - lastTimeBuffer > windowLength) {
                    sessions.write(DataFormats.accountIdsToSessionString(sessionBuffer.get(terminal)));
                    sessions.write("\n");

                    sessionBuffer.get(terminal).clear();
                }
                sessionBuffer.get(terminal).add(e.getAccount().uniqueId());
                latestTimeBuffer.put(terminal, e.getTimeStamp().offSet());
            }

            for (ArrayList<String> rest : sessionBuffer.values()) {
                if (rest.size() > 0) {
                    sessions.write(DataFormats.accountIdsToSessionString(rest));
                    sessions.write("\n");
                }
            }
            sessions.close();
            return true;
        } catch (IOException ex) {
            throw new IllegalStateException("创建中间文件异常");
        }
    }
}
