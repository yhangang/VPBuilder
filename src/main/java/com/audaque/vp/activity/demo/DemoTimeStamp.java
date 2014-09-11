/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vp.utils.DataFormats;
import com.audaque.vpbase.event.TimeStamp;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DemoTimeStamp extends TimeStamp {

    private final long offSet;

    public DemoTimeStamp(String time) {
        this.offSet = DataFormats.timeToMilliseconds(time);
    }

    public DemoTimeStamp(long offSet) {
        this.offSet = offSet;
    }

    @Override
    public long offSet() {
        return offSet;
    }

    @Override
    public long diff(TimeStamp other) {
        return offSet - other.offSet();
    }

}
