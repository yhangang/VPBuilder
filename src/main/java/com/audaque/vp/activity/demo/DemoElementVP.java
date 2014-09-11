/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.account.AccountImple;
import com.audaque.vpbase.vp.VP;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DemoElementVP implements VP {

    Element vp;
    String uniqueId;

    public DemoElementVP(Element vpElement, String uniqueId) {
        this.vp = vpElement;
        this.uniqueId = uniqueId;
    }

    @Override
    public String uniqueId() {
        return this.uniqueId;
    }

    @Override
    public Iterator<Account> accounts() {
        final List<Element> eles = vp.selectNodes(vp.getUniquePath() + "//Account");
        return new Iterator<Account>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < eles.size();
            }

            @Override
            public Account next() {
                final Element ele = eles.get(currentIndex++);
                return new AccountImple(ele.attributeValue("type"), ele.attributeValue("id"), 1);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.uniqueId != null ? this.uniqueId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DemoElementVP other = (DemoElementVP) obj;
        if ((this.uniqueId == null) ? (other.uniqueId != null) : !this.uniqueId.equals(other.uniqueId)) {
            return false;
        }
        return true;
    }

}
