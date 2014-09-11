/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.account.Attribute;
import com.audaque.vpbase.account.AttributiveAccount;
import com.audaque.vpbase.account.ExtendedAccount;
import com.audaque.vpbase.db.VPDatabase;
import com.audaque.vpbase.vp.VP;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 将VP的创建信息直接输出到终端上.
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DemoOnScreenVPDatabase implements VPDatabase {

    private static final String TYPE_ID_SPLIT = ":";

    public DemoOnScreenVPDatabase() {
    }

    @Override
    public VP getVP(String serviceId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public VP getVP(String uniqueId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String createVP(Iterator<Account> accounts) {
        String uid = UUID.randomUUID().toString();

        StringBuilder bd = new StringBuilder();
        bd.append("DemoXmlVPDatabase:New VP:id=").append(uid).append(";accounts=");
        while (accounts.hasNext()) {
            bd.append(accounts.next().uniqueId()).append(",");
        }
        System.out.print(bd.toString());
        return uid;
    }

    @Override
    public Account getAccount(final String uniqueId) {
        return new Account() {
            String[] fileds = uniqueId.split(TYPE_ID_SPLIT);

            @Override
            public String serviceId() {
                return fileds[0];
            }

            @Override
            public String userId() {
                return fileds[1];
            }

            @Override
            public String uniqueId() {
                return uniqueId;
            }

            @Override
            public double boost() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public boolean setVPAttributes(String uniqueId, String[] attributes, String[] values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<AttributiveAccount> getAccounts(String[] attributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getAttributes(Account account, Attribute[] attributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ExtendedAccount getExtendAccount(Account account, String extendCode) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean disable(VP vp) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean addInitVP(String resultOnHDFS, String filedSplit) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean refreshVP(VP vp, Iterator<Account> accounts) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean beforeInitVP() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean afterInitVP() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Attribute[] getPredefineVPAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String[] getPredefinedVPAttributeValues(Account account) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getVPUniId(String serviceId, String userId) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean onCreate(String parameters) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean onClose(String parameters) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<String, String> getPredefinedVPAttributes(String serviceId, String userId) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
