/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.utils;

import java.util.UUID;

/**
 *
 * @author Audaque
 */
public class DataUtil {

    public static String getUniqueString() {
        return UUID.randomUUID().toString();
    }

}
