/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vp.utils.DataFormats;
import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.event.Event;
import com.audaque.vpbase.event.InfoFlow;
import com.audaque.vpbase.event.TimeStamp;
import java.util.Collection;

/**
 * 从测试用Json文件中直接读取记录形成Event对象
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DemoJsonEvent implements Event {

    public static final String ACCOUNTTYPE_USERID_SPLIT = ":";
    public static final String TERMINAL_SPLIT = ";";
    private final String sn;
    private final String mid;
    private final String account;
    private final String type;
    private final String ip;
    private final String mac;
    public final String time;

    /**
     * this value is null when a Event is just create
     */
    public Long longTime = null;

    public DemoJsonEvent(String sn, String mid, String account, String type, String ip, String mac, String time) {
        this.sn = sn;
        this.mid = mid;
        this.account = account;
        this.type = type;
        this.ip = ip;
        this.mac = mac;
        this.time = time;
        this.longTime = DataFormats.timeToMilliseconds(time);
    }

    public String getAccountId() {
        return type + ACCOUNTTYPE_USERID_SPLIT + account;
    }

    public static String[] parseTerminalId(String terminalId) {
        return parseId(terminalId, TERMINAL_SPLIT, 2);
    }

    public static String[] parseAccountId(String accountId) {
        return parseId(accountId, ACCOUNTTYPE_USERID_SPLIT, 2);
    }

    private static String[] parseId(String expression, String split, int legalMemberNum) {
        String[] members = expression.split(split);
        if (members.length != legalMemberNum) {
            throw new IllegalArgumentException("format erro:" + expression);
        } else {
            return members;
        }
    }

    @Override
    public String toString() {
        return "Event{" + "sn=" + sn + ", mid=" + mid + ", account=" + account + ", type=" + type + ", ip=" + ip + ", mac=" + mac + ", time=" + time + ", longTime=" + longTime + '}';
    }

    @Override
    public TimeStamp getTimeStamp() {
        return new TimeStamp() {
            @Override
            public long offSet() {
                return longTime;
            }

            @Override
            public long diff(TimeStamp other) {
                return longTime - other.offSet();
            }
        };
    }

    @Override
    public String getTerminalId() {
        return sn + TERMINAL_SPLIT + mid;
    }

    @Override
    public Account getAccount() {
        return new Account() {

            @Override
            public String serviceId() {
                return type;
            }

            @Override
            public String userId() {
                return account;
            }

            @Override
            public String uniqueId() {
                return type + ACCOUNTTYPE_USERID_SPLIT + account;
            }

            @Override
            public double boost() {
                return 1;
            }
        };
    }

    @Override
    public InfoFlow getFlow() {
        return InfoFlow.None;
    }

    @Override
    public Collection<Account> objAccounts() {
        return null;
    }

}
