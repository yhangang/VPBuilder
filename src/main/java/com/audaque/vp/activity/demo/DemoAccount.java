/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vp.utils.DataFormats;
import com.audaque.vpbase.account.Account;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DemoAccount implements Account {

    public static final String split = DataFormats.ACCOUNT_TYPE_ID_SPLIT;

    private final String serviceId;
    private final String userId;

    public DemoAccount(String serviceId, String userId) {
        this.serviceId = serviceId;
        this.userId = userId;
    }

    @Override
    public String serviceId() {
        return this.serviceId;
    }

    @Override
    public String userId() {
        return this.userId;
    }

    @Override
    public String uniqueId() {
        return this.serviceId + split + this.userId;
    }

    @Override
    public double boost() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
