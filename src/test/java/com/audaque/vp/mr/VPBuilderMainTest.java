/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.mr;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Audaque
 */
public class VPBuilderMainTest {

    public VPBuilderMainTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of initVPDatabase method, of class VPBuilderMain.
     */
    @Test
    public void testInitVPDatabase() throws Exception {
//        System.out.println("initVPDatabase");
//        
//        String atrSrcDataHdfsFolder = args[0];
//        String dstHdfsFolder = args[1];
//
//        String fieldsSplit = ",";
//        String accountTypeIdSplit = ":";
//
//        String actVpEventDatabasename = DemoXmlEventDatabase.class.getName();
//        String actVpEventOnCreateParameters = "DemoXmlEventData.xml";
//        String actVpEventOnCloseParameters = " ";
//        String actNShards = "2";
//        String actWinSeconds = "3";
//
//        StringBuilder actVPEventString = new StringBuilder();
//        try {
//            BufferedReader reader = FileUtils.getBufferedReader(actVpEventOnCreateParameters);
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                actVPEventString.append(line);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(VPBuilderInitVPDbConfig.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        VPBuilderInitVPDbConfig config = new VPBuilderInitVPDbConfig(atrSrcDataHdfsFolder, fieldsSplit, accountTypeIdSplit, actVpEventDatabasename, actVPEventString.toString(), actVpEventOnCloseParameters, actNShards, actWinSeconds, dstHdfsFolder);
//
//        VPBuilderMain bd = new VPBuilderMain();
//        bd.initVPDatabase(config, new Configuration());
//        System.out.println("VPBuilder suc! check output:" + config.getDstHdfsFolder());
    }

    /**
     * Test of updateVPDatabase method, of class VPBuilderMain.
     */
    @Test
    public void testUpdateVPDatabase() {
        System.out.println("updateVPDatabase");
//        VPBuilderUpdateVPDbConfig config = null;
//        Configuration conf = null;
//        VPBuilderMain instance = new VPBuilderMain();
//        boolean expResult = false;
//        boolean result = instance.updateVPDatabase(config, conf);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

}
