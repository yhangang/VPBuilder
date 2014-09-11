/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vp.utils.XmlUtil;
import com.audaque.vpbase.db.VPEventDatabase;
import com.audaque.vpbase.event.Event;
import java.util.Iterator;
import org.dom4j.Document;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DemoXmlEventDatabase implements VPEventDatabase {

    private Document doc;

    public DemoXmlEventDatabase() {

    }

    public DemoXmlEventDatabase(String eventXml) {
        this.doc = XmlUtil.getDocument(eventXml);
    }

    @Override
    public Iterator<Event> getEventsGroupByTerminalSortByTime() {
        return new XmlEventIdSequenceReader(doc);
    }

    @Override
    public boolean onCreate(String eventsXmlString) {
        this.doc = XmlUtil.getDocumentByString(eventsXmlString);
        if (doc == null) {
            throw new IllegalArgumentException("Blank XML content or format erro:" + eventsXmlString);
        }
        return true;
    }

    @Override
    public boolean onClose(String parameters) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
