/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vpbase.event.Event;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Audaque
 */
class XmlEventIdSequenceReader implements Iterator<Event> {

    private final Document doc;
    private int eventId = -1;
    private Event nextEventEle = null;

    public XmlEventIdSequenceReader(Document doc) {
        this.doc = doc;
        this.eventId = 0;
        nextEventEle = getEvent(this.eventId);
    }

    @Override
    public boolean hasNext() {
        return nextEventEle != null;
    }

    @Override
    public Event next() {
        Event e = nextEventEle;
        nextEventEle = getEvent(++eventId);
        return e;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private Event getEvent(int eventId) {
        Element ele = getUniqueElement("/root/Events/Event[@id='" + eventId + "']");
        if (ele != null) {
            return new DemoXmlEvent(ele);
        } else {
            return null;
        }
    }

    private Element getUniqueElement(String uniqueXPath) {
        List<Node> nodes = doc.selectNodes(uniqueXPath);
        if (nodes.size() == 1) {
            return (Element) nodes.get(0);
        } else if (nodes.size() > 1) {
            throw new IllegalStateException("UniqueXPath匹配了多个结果:" + uniqueXPath);
        } else {
            return null;
        }
    }

}
