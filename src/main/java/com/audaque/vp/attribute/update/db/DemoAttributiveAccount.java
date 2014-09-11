/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.attribute.update.db;

import com.audaque.vpbase.account.Attribute;
import com.audaque.vpbase.account.AttributiveAccount;

/**
 *
 * @author Administrator
 */
public class DemoAttributiveAccount implements AttributiveAccount {

    private String serviceId;
    private String userId;
    private Attribute[] attribute;
    private String[] attributeValues;

    public DemoAttributiveAccount(String serviceId, String userId) {
        this.serviceId = serviceId;
        this.userId = userId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAttribute(Attribute[] attribute) {
        this.attribute = attribute;
    }

    public void setAttributeValues(String[] attributeValues) {
        this.attributeValues = attributeValues;
    }

    public DemoAttributiveAccount() {
    }

    @Override
    public Attribute[] attributes() {
        return attribute;
    }

    @Override
    public String[] attributeValues() {
        return attributeValues;
    }

    @Override
    public String attributeValue(Attribute attribute) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String extendCode() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String serviceId() {
        return serviceId;
    }

    @Override
    public String userId() {
        return userId;
    }

    @Override
    public String uniqueId() {
        return serviceId + ":" + userId;
    }

    @Override
    public double boost() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof DemoAttributiveAccount) {
            DemoAttributiveAccount account = (DemoAttributiveAccount) obj;
            if (uniqueId().equals(account.uniqueId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uniqueId().hashCode();
    }
}
