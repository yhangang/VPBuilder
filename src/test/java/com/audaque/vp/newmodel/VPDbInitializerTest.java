///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.audaque.vp.newmodel;
//
//import com.audaque.vp.activity.demo.Datanames;
//import com.audaque.vp.activity.demo.DemoXmlEventDatabase;
//import com.audaque.vp.activity.demo.DemoXmlVPDatabase;
//import com.audaque.vpbase.db.VPDatabase;
//import com.audaque.vpbase.db.VPEventDatabase;
//import java.util.UUID;
//import org.junit.After;
//import org.junit.AfterClass;
//import static org.junit.Assert.assertEquals;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// *
// * @author alex
// */
//public class VPDbInitializerTest {
//
//    public VPDbInitializerTest() {
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
//     * Test of initializeVPDb method, of class VPDbInitializer.
//     */
//    @Test
//    public void testInitializeVPDb_VPDatabase_VPEventDatabase() {
//
//        System.out.println("initializeVPDb");
//        String vpData = Datanames.getBaseVPDatabase();
//        VPDatabase vpDb = new DemoXmlVPDatabase(vpData);
//
//        String eventData = Datanames.getEventsXml();
//        VPEventDatabase vpEvents = new DemoXmlEventDatabase(eventData);
//
//        String radomFolder = "ForVPBuildTest_" + UUID.randomUUID().toString();
//        VPInitModel model = new ActivityAnalyInitModel(vpEvents, 3, "\t", "d:\\" + radomFolder + "_mod");
//
//        VPDbInitializer instance = new VPDbInitializer("d:\\" + radomFolder + "_er", model);
//        boolean expResult = true;
//        boolean result = instance.initializeVPDb(vpDb, vpEvents);
//        assertEquals(expResult, result);
//    }
//
//}
