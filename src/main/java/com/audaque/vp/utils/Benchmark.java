/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.utils;

import java.util.HashMap;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class Benchmark {

    public static final boolean on = false;

    private static final int INIT_SIZE_FOR_TIMER = 50;
    private static final long INIT_STATUS = -1;
    private static final HashMap<String, TimeCount> records = new HashMap(INIT_SIZE_FOR_TIMER);

    public static void start(String event) {
        if (!on) {
            return;
        }
        long t = System.currentTimeMillis();
        TimeCount r = records.get(event);
        if (r == null) {
            r = new TimeCount();
            records.put(event, r);
        }
        r.start = t;
    }

    public static void stop(String event) {
        if (!on) {
            return;
        }
        long t = System.currentTimeMillis();
        TimeCount r = records.get(event);
        if (r == null
                || r.start == INIT_STATUS) {
            throw new IllegalArgumentException("No such event has been started:" + event);
        }
        r.cost = t - r.start;
    }

    public static long cost(String event) {
        if (!on) {
            throw new IllegalArgumentException("The benchmark is of:" + event);
        }
        TimeCount r = records.get(event);
        if (r == null) {
            throw new IllegalArgumentException("No such event:" + event);
        }
        if (r.cost == INIT_STATUS) {
            throw new IllegalArgumentException("The event is not stoped:" + event);
        }
        return r.cost;
    }

    static class TimeCount {

        public long start = INIT_STATUS;
        public long cost = INIT_STATUS;
    }
}
