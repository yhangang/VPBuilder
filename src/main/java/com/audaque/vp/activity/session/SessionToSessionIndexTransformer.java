/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.session;

import com.audaque.vp.utils.Filenames;
import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class SessionToSessionIndexTransformer {

    public final AccountIndexMapper mapper;

    public SessionToSessionIndexTransformer(int initAccountCount, AccountIndexMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * The format of sessionIndex: accountId \t n accountId1 \t ..accountIdn IN
     * FORMATE of "AdjList"
     *
     * @param sessionFile
     * @return
     */
    public String forward(String sessionFile, String... others) {
        try {
            String indexSession = Filenames.getSessionIndexFile(sessionFile);
            BufferedWriter sIdxWriter = null;
            sIdxWriter = FileUtil.getBufferedWriter(indexSession);

            BufferedReader sReader = null;
            sReader = FileUtil.getBufferedReader(sessionFile);

            forward(sReader, sIdxWriter, mapper);

            for (String other : others) {
                sReader = FileUtil.getBufferedReader(other);
                forward(sReader, sIdxWriter, mapper);
            }

            sIdxWriter.close();

            return indexSession;
        } catch (IOException ex) {
            Logger.getLogger(SessionToSessionIndexTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int maxIndex() {
        return mapper.maxIndex;
    }

    private void forward(BufferedReader sReader, BufferedWriter sIdxWriter, AccountIndexMapper mapper) {
        try {
            String session = null;
            StringBuilder bd = new StringBuilder();
            while ((session = sReader.readLine()) != null) {
                String[] aIds = DataFormats.sessionStringToAccountIds(session);

                int firstIndex = mapper.putIndex(aIds[0]);
                if (aIds.length == 1) {
                    bd.append(firstIndex).append(DataFormats.SPLIT).append(1).append(DataFormats.SPLIT).append(firstIndex);
                } else if (aIds.length > 1) {
                    bd.append(firstIndex).append(DataFormats.SPLIT).append(aIds.length - 1);
                    for (int i = 1; i < aIds.length; i++) {
                        bd.append(DataFormats.SPLIT).append(mapper.putIndex(aIds[i]));
                    }
                }
                bd.append("\n");
                sIdxWriter.write(bd.toString());
                bd.delete(0, bd.length());
            }

            sReader.close();
        } catch (IOException ex) {
            Logger.getLogger(SessionToSessionIndexTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
