/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import com.audaque.vpbase.db.VPEventDatabase;
import com.audaque.vpbase.event.Event;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * 它从原始的Json数据中读取事件数据，并按照发生的时间进行排序.
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DemoJsonVPEventDatabase implements VPEventDatabase {

    private final String source;

    public DemoJsonVPEventDatabase(String source) {
        this.source = source;
    }

    private static final Comparator EVENT_TIME_COMPARATOR = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            if (o1 != null && o2 != null) {
                return o1.getTimeStamp().compareTo(o2.getTimeStamp());
            } else {
                throw new IllegalArgumentException("o1 or o2 is null");
            }
        }
    };

    @Override
    public Iterator<Event> getEventsGroupByTerminalSortByTime() {
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();

        BufferedReader json = FileUtil.getBufferedReader(source);

        JsonElement jsonElement = jsonParser.parse(json);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Iterator it = jsonArray.iterator();

        TreeSet events = new TreeSet(EVENT_TIME_COMPARATOR);
        while (it.hasNext()) {
            jsonElement = (JsonElement) it.next();
            String jsonString = jsonElement.toString();
            DemoJsonEvent event = gson.fromJson(jsonString, DemoJsonEvent.class);
            event.longTime = DataFormats.timeToMilliseconds(event.time);
            events.add(event);
        }
        return events.iterator();
    }

    @Override
    public boolean onCreate(String parameters) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean onClose(String parameters) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
