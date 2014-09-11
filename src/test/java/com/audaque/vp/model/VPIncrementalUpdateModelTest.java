/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.model;

import com.audaque.vp.activity.demo.Datanames;
import com.audaque.vp.activity.demo.DemoXmlVPDatabase;
import com.audaque.vp.activity.demo.DemoXmlVPIterator;
import com.audaque.vpbase.db.VPDatabase;
import com.audaque.vpbase.vp.VP;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author alex
 */
public class VPIncrementalUpdateModelTest {

    public VPIncrementalUpdateModelTest() {
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
     * Test of update method, of class VPIncrementalUpdater.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        String updates = Datanames.getVPUpdates();
        Iterator<VP> deltaVPs = new DemoXmlVPIterator(updates);

        String baseData = Datanames.getBaseVPDatabase();
        VPDatabase vDb = new DemoXmlVPDatabase(baseData);

        VPDbUpdateMethods.liteUpdate(vDb, deltaVPs);
    }

}
