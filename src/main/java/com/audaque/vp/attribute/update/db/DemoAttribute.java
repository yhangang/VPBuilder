/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.attribute.update.db;

import com.audaque.vpbase.account.Attribute;

/**
 *
 * @author Administrator
 */
public class DemoAttribute implements Attribute {

    private String name;

    public DemoAttribute(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public double boost() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
