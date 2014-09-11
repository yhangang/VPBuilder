/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

/**
 * 提供测试数据的路径.
 *
 * @author Liyu.Cai@Audaque.com
 */
public class Datanames {

    public static String getBaseVPDatabase() {
        return "res/data/BaseVPDatabase.xml";
    }

    public static String getVPUpdates() {

        return "res/data/VPUpdates.xml";
    }

    public static String getTestJson() {
        return "res/data/test_data.json";
    }

    public static String getEventsXml() {
        return "res/data/DemoXmlEvents.xml";
    }

    /**
     * 用于增量更新.
     */
    public static String getEventUpdatesXml() {
        return "res/data/DemoXmlEventUpdates.xml";
    }

}
