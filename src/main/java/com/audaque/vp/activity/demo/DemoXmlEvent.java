/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.event.Event;
import com.audaque.vpbase.event.InfoFlow;
import com.audaque.vpbase.event.TimeStamp;
import java.util.Collection;
import org.dom4j.Element;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
class DemoXmlEvent implements Event {

    private final TimeStamp ts;
    private final String terminal;
    private final Account account;

    public DemoXmlEvent(Element ele) {
        String time = ele.element("time").attributeValue("val");
        this.ts = new DemoTimeStamp(time);
        this.terminal = ele.element("terminal").attributeValue("val");

        final String type = ele.element("account").attributeValue("type");
        final String uId = ele.element("account").attributeValue("id");
        this.account = new DemoAccount(type, uId);
    }

    @Override
    public TimeStamp getTimeStamp() {
        return this.ts;
    }

    @Override
    public String getTerminalId() {
        return this.terminal;
    }

    @Override
    public Account getAccount() {
        return this.account;
    }

    @Override
    public InfoFlow getFlow() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Collection<Account> objAccounts() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
