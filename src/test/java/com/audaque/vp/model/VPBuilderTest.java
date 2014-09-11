///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.audaque.vp.model;
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
//public class VPBuilderTest {
//
//    public VPBuilderTest() {
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
//     * Test of initVPDatabase method, of class VPBuilder.
//     */
//    @Test
//    public void testInitVPDatabase() {
//        System.out.println("initVPDatabase");
//
//        String vpData = Datanames.getBaseVPDatabase();
//        VPDatabase vpDb = new DemoXmlVPDatabase(vpData);
//
//        String eventData = Datanames.getEventsXml();
//        VPEventDatabase vpEvents = new DemoXmlEventDatabase(eventData);
//
//        String radomFolder = "ForVPBuildTest_" + UUID.randomUUID().toString();
//
//        VPAnalysisModel model = new ActivityAnalysisInitModel(vpEvents, 3, "\t", "C:\\" + radomFolder + "_mod");
//
//        VPBuilder instance = new VPBuilder("C:\\" + radomFolder + "_bd", model);
//        boolean expResult = true;
//        boolean result = instance.initVPDatabase(vpDb, vpEvents);
//        assertEquals(expResult, result);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }
//
////    /**
////     * Test of updateVPDatabase method, of class VPBuilder.
////     */
////    @Test
////    public void testUpdateVPDatabase() {
////        System.out.println("updateVPDatabase");
////        VPDatabase vpDb = null;
////        VPEventDatabase vpEventDb = null;
////        Iterator<Account> updatedAccounts = null;
////        Iterator<Event> newEvents = null;
////        VPBuilder instance = null;
////        boolean expResult = false;
////        boolean result = instance.updateVPDatabase(vpDb, vpEventDb, updatedAccounts, newEvents);
////        assertEquals(expResult, result);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
//
//}
