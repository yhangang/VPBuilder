/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.mr;

import org.apache.hadoop.conf.Configuration;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class Container {

    private static Container container = new Container();

    private Configuration conf;

    private Container() {

    }

    public static Container getContainer() {
        return container;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration newConfiguration() {
        return new Configuration(conf);
    }
}
