///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.audaque.vp.activity.app;
//
//import com.audaque.vp.activity.demo.Datanames;
//import com.audaque.vp.activity.demo.DemoXmlUpdateEventDatabase;
//import com.audaque.vp.activity.demo.DemoXmlVPUpdateDatabase;
//import com.audaque.vp.utils.DataUtil;
//import com.audaque.vpbase.db.VPEventUpdateDatabase;
//import com.audaque.vpbase.db.VPUpdateDatabase;
//import java.io.File;
//import org.junit.After;
//import org.junit.AfterClass;
//import static org.junit.Assert.assertEquals;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// *
// * @author Audaque
// */
//public class ActivityUpdateMainTest {
//
//    public ActivityUpdateMainTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() {
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of getNewVPs method, of class ActivityUpdateMain.
//     */
//    @Test
//    public void testGetNewVPs() {
//        System.out.println("getNewVPs");
//        String newEvents = Datanames.getEventUpdatesXml();
//        VPEventUpdateDatabase eDb = new DemoXmlUpdateEventDatabase(newEvents);
//
//        String vpXml = Datanames.getBaseVPDatabase();
//        VPUpdateDatabase vDb = new DemoXmlVPUpdateDatabase(vpXml);
//
//        int windSec = 30;
//        int nShards = 2;
//        String localBuffFolder = "C:\\TEMP_" + DataUtil.getUniqueString() + File.separator;
//
//        ActivityUpdateMain instance = new ActivityUpdateMain();
//        boolean expResult = true;
//        boolean result = instance.updateVPDb(eDb, vDb, windSec, nShards, localBuffFolder);
//        assertEquals(expResult, result);
//    }
//
//}
