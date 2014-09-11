/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.session;

import com.audaque.vp.utils.FileUtil;
import com.audaque.vpbase.event.Event;
import com.audaque.vpbase.event.TimeStamp;
import com.google.common.base.Preconditions;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class FixWinLenIndexSessionCreatorForGSEvents implements SessionCreator<String> {

    private final String split;
    /**
     * 时长,以毫秒计数.
     */
    private final int windowLength;
    private final Iterator<Event> gsEvents;
    private final AccountIndexMapper accountIndexMapper;

    public FixWinLenIndexSessionCreatorForGSEvents(int windowLength, Iterator<Event> gsEvents, AccountIndexMapper mapper, String split) {
        this.windowLength = windowLength;
        this.gsEvents = gsEvents;
        this.accountIndexMapper = mapper;
        this.split = split;
    }

    @Override
    public boolean create(String sFile) {
        BufferedWriter sessions = null;
        try {
            sessions = FileUtil.getBufferedWriter(sFile);

            int lastAccount = -1;
            String lastTerminal = null;
            TimeStamp lastWindTS = null;

            ArrayList<Integer> accountBuffer = new ArrayList<>(30);

            while (gsEvents.hasNext()) {
                Event event = gsEvents.next();
                if (isInvalidEvent(event)) {
                    //若为非有效事件则直接忽略.
                    continue;
                }

                if (lastTerminal == null) {
                    Preconditions.checkArgument(lastWindTS == null, "初始化异常");

                    lastAccount = accountIndexMapper.putIndex(event.getAccount().uniqueId());
                    accountBuffer.add(lastAccount);

                    lastTerminal = event.getTerminalId();
                    lastWindTS = event.getTimeStamp();
                    continue;
                }

                int account = accountIndexMapper.putIndex(event.getAccount().uniqueId());
                String terminal = event.getTerminalId();
                TimeStamp ts = event.getTimeStamp();

                if (terminal.equals(lastTerminal)) {
                    if (ts.diff(lastWindTS) > windowLength) {
                        createSession(accountBuffer, sessions);

                        accountBuffer.clear();
                        accountBuffer.add(account);

                        lastAccount = account;
                        lastWindTS = ts;

                    } else {
//                        if (account != lastAccount) {
                        accountBuffer.add(account);
//                        }
                    }
                } else {
                    createSession(accountBuffer, sessions);

                    accountBuffer.clear();
                    accountBuffer.add(account);

                    lastAccount = account;
                    lastTerminal = terminal;
                    lastWindTS = ts;
                }
            }
            if (accountBuffer.size() > 0) {
                createSession(accountBuffer, sessions);
            }
            sessions.flush();
            sessions.close();
            return true;
        } catch (IOException ex) {
            throw new IllegalStateException("创建Session文件失败:" + ex.getMessage());
        }

    }

    /**
     * 将Accounts 的信息以 AdjList 的格式创建新的Session行，并写入到磁盘上.
     *
     * @param accounts 在同一个Session 的多个帐号
     * @param sessions 磁盘写文件
     */
    private void createSession(ArrayList<Integer> accounts, BufferedWriter sessions) throws IOException {
        if (accounts.size() == 1) {
            sessions.write(accounts.get(0) + this.split + "1" + this.split + accounts.get(0));
            sessions.write("\n");
        } else if (accounts.size() > 1) {
            StringBuilder bd = new StringBuilder();
            bd.append(accounts.get(0)).append(this.split).append(accounts.size() - 1);
            for (int i = 1; i < accounts.size(); i++) {
                bd.append(this.split).append(accounts.get(i));
            }
            sessions.write(bd.toString());
            sessions.write("\n");
        }
    }

    private boolean isInvalidEvent(Event event) {
        return event.getAccount() == null
                || event.getTerminalId() == null
                || event.getTimeStamp() == null;
    }
}
