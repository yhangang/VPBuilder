/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.utils;

import com.audaque.vp.newmodel.MultiFileReader;
import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class FileUtil {

    public static BufferedReader getBufferedReader(String readFrom) {
        try {
            return new BufferedReader(new InputStreamReader(
                    new FileInputStream(readFrom), DataFormats.ENCODING));
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Iterator<String> getLineIterator(List<String> filePaths, boolean filesHaveHeaders, int numSkipLines) {
        try {
            return new MultiFileReader(filePaths, filesHaveHeaders, numSkipLines).iterator();
        } catch (Exception ex) {
            throw new IllegalStateException("Create Line Iterator exception:" + ex.getMessage());
        }
    }

    public static BufferedWriter getBufferedWriter(String writeTo) {
        try {
            return new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(writeTo), DataFormats.ENCODING));
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void deleteIfExists(String localBufferFolder) {
        File f = new File(localBufferFolder);
        if (f.exists()) {
            remove(f);
        }
    }

    private static void remove(File f) {
        if (f.isFile()) {
            /*删除失败则抛出状态异常*/
            Preconditions.checkState(f.delete(), "删除文件失败:" + f.getAbsolutePath());
        } else if (f.isDirectory()) {
            for (File subF : f.listFiles()) {
                remove(subF);
            }
            Preconditions.checkState(f.delete(), "删除文件失败:" + f.getAbsolutePath());
        }
    }

}
