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
public class FixWinLenSessionCreatorForGSEvents implements SessionCreator<String> {

    private final String baseFile;
    private final String split;
    /**
     * 时长,以毫秒计数.
     */
    private final int windowLength;
    private final Iterator<Event> eventIterator;
//    private final VPEventDatabase eventReader;

    public FixWinLenSessionCreatorForGSEvents(String baseFile, int windowLength, Iterator<Event> eventIterator, String split) {
        this.baseFile = baseFile;
        this.windowLength = windowLength;
        this.eventIterator = eventIterator;
        this.split = split;
    }

    @Override
    public boolean create(String sFile) {
        BufferedWriter sessions = null;
        try {
            sessions = FileUtil.getBufferedWriter(sFile);

            Preconditions.checkState(sessions != null, "Init middle file failure,no write permission?:" + sFile);

            String lastAccount = null;
            String lastTerminal = null;
            TimeStamp lastWindTS = null;

            ArrayList<String> accountBuffer = new ArrayList<String>(30);

            while (this.eventIterator.hasNext()) {
                Event event = eventIterator.next();
                if (lastTerminal == null) {
                    Preconditions.checkArgument(lastWindTS == null, "初始化异常");

                    lastAccount = event.getAccount().uniqueId();
                    accountBuffer.add(lastAccount);

                    lastTerminal = event.getTerminalId();
                    lastWindTS = event.getTimeStamp();
                    continue;
                }

                String account = event.getAccount().uniqueId();
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
                        if (!account.equals(lastAccount)) {
                            accountBuffer.add(account);
                        }
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
    private void createSession(ArrayList<String> accounts, BufferedWriter sessions) throws IOException {
        if (accounts.size() == 1) {
            //创建AdjList格式的数据：
//            sessions.write(accounts.get(0) + this.split + "1" + this.split + accounts.get(0));
            //创建Session格式：
            sessions.write(accounts.get(0) + this.split + accounts.get(0));
            sessions.write("\n");
        } else if (accounts.size() > 1) {
            StringBuilder bd = new StringBuilder();
            //创建AdjList格式的数据：
//            bd.append(accounts.get(0)).append(this.split).append(accounts.size() - 1);
            //创建Session格式：
            bd.append(accounts.get(0));
            for (int i = 1; i < accounts.size(); i++) {
                bd.append(this.split).append(accounts.get(i));
            }
            sessions.write(bd.toString());
            sessions.write("\n");
        }
    }
}
