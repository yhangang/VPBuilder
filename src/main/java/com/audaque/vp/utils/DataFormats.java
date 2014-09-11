/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DataFormats {

    public static String ENCODING = "UTF-8";
    public static String ACCOUNT_TYPE_ID_SPLIT = ":";
    public static final String SPLIT = "\t";//DO NOT CHANGE IT!!!

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

    public static String accountIdsToSessionString(Collection<String> accountIds) {
        StringBuilder bd = new StringBuilder();
        for (String accountId : accountIds) {
            bd.append(accountId).append(SPLIT);
        }
        if (bd.length() > 0) {
            bd.delete(bd.length() - SPLIT.length(), bd.length());
        }
        return bd.toString();
    }

    public static String[] sessionStringToAccountIds(String sessionStr) {
        return sessionStr.split(SPLIT);
    }

    public static Long timeToMilliseconds(String time) {
        try {
            return TIME_FORMATTER.parse(time).getTime();
        } catch (ParseException ex) {
            Logger.getLogger(DataFormats.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
