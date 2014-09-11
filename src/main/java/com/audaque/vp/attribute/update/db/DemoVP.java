/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.attribute.update.db;

import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.vp.VP;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Administrator
 */
public class DemoVP implements VP {

    private List<Account> accList;

    public DemoVP(final String serviceId, final String userId) {
        accList = new ArrayList<>();
        accList.add(new Account() {

            @Override
            public String serviceId() {
                return serviceId;
            }

            @Override
            public String userId() {
                return userId;
            }

            @Override
            public String uniqueId() {
                return serviceId + ":" + userId;
            }

            @Override
            public double boost() {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) {
                    return false;
                }
                if (obj instanceof Account) {
                    Account account = (Account) obj;
                    if (uniqueId().equals(account.uniqueId())) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public int hashCode() {
                return uniqueId().hashCode();
            }

        });
    }

    @Override
    public String uniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Iterator<Account> accounts() {
        return accList.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof DemoVP) {
            DemoVP demoVP = (DemoVP) obj;
            if (uniqueId().equals(demoVP.uniqueId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uniqueId().hashCode();
    }

}
