/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.detector;

import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @deprecated 不再使用该类--直接交由在GraphChi中的定制类来处理
 * @author Liyu.Cai@Audaque.com
 */
@Deprecated
public class ConnectedComponentsDetector {

    private Collection<HashSet<String>> componentsBuffer;
    private final String fixWindowSessionFile;

    public ConnectedComponentsDetector(String fixWindowSessionFile) {
        this.fixWindowSessionFile = fixWindowSessionFile;
        this.componentsBuffer = new ArrayList<HashSet<String>>();
    }

    public static void main(String[] args) throws IOException {
//        String baseFile = DataFilenames.getTestData();
//        final Long windowLength = (long) 5 * 60 * 1000;//2mins
//
//        SortedEventsReader reader = new ReadEventsAndSortByTime(baseFile);
//        FixWindowSessionCreator fwsc = new FixWindowSessionCreator(baseFile, windowLength, reader);
//        fwsc.create();
//
//        ConnectedComponentsDetector ccd = new ConnectedComponentsDetector(DataFilenames2.getSessionFile(baseFile));
//        ccd.process();
    }

    public void process() {
        try {
            BufferedReader br = FileUtil.getBufferedReader(fixWindowSessionFile);
            String line;
            while ((line = br.readLine()) != null) {
                String[] accountIds = DataFormats.sessionStringToAccountIds(line);
                HashSet<String> stored = null;
                for (HashSet<String> components : componentsBuffer) {
                    for (String id : accountIds) {
                        if (components.contains(id)) {
                            stored = components;
                            break;
                        }
                    }
                    if (stored != null) {
                        break;
                    }
                }
                if (stored == null) {
                    stored = new HashSet<>();
                    componentsBuffer.add(stored);
                }
                stored.addAll(Arrays.asList(accountIds));
            }
            try (BufferedWriter wt = FileUtil.getBufferedWriter(fixWindowSessionFile)) {
                for (HashSet<String> ids : componentsBuffer) {
                    wt.write(DataFormats.accountIdsToSessionString(ids));
                    wt.write("\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectedComponentsDetector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
