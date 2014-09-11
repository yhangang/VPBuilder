/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.utils;

/**
 * 文件命名规则.
 *
 * @author Liyu.Cai@Audaque.com
 */
public class Filenames {

    public static String getSessionFile(String baseFile) {
        return baseFile + ".session";
    }

    public static String getComponentsFile(String baseFile) {
        return baseFile + ".components";
    }

    public static String getSessionIndexFile(String baseFile) {
        return baseFile + ".index";
    }

    public static String getAccountIndex(String baseFile) {
        return baseFile + ".accountIndex";
    }

    public static String getMergeByComponents(String baseFile) {
        return baseFile + ".merge";
    }

    public static String getToAcontUid(String baseFile) {
        return baseFile + ".toUId";
    }
}
