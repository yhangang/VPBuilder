/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vp.utils.XmlUtil;
import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.account.AccountImple;
import com.audaque.vpbase.vp.VP;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;

/**
 * <b>测试用途</b>
 * 从文本文件中逐行读取一个虚拟人下的所有账户ID
 *
 * @author Liyu.Cai@Audaque.com
 */
public class DemoXmlVPIterator implements Iterator<VP> {

    public static final String ACCOUNT_SPLIT = ",";
    public static final String ACCOUNT_TYPE_ID_SPLIT = ":";
    private final Iterator<VP> vps;

    public DemoXmlVPIterator(String dataXml) {
        vps = getVPs(dataXml).iterator();
    }

    @Override
    public boolean hasNext() {
        return vps.hasNext();
    }

    @Override
    public VP next() {
        return vps.next();
    }

    @Override
    public void remove() {
        vps.remove();
    }

    private List<VP> getVPs(String dataXml) {
        List<Element> vpEles = getVPElements(dataXml);

        List<VP> svps = new ArrayList<VP>(vpEles.size());

        for (Element vpEle : vpEles) {
            final String vpName = vpEle.attributeValue("id");

            List<Element> accountEles = getVPAccountElements(vpEle);
            final List<Account> vpAccounts = new ArrayList<Account>(accountEles.size());
            for (Element accountEle : accountEles) {
                String type = accountEle.attributeValue("type");
                String qqNum = accountEle.attributeValue("id");
                vpAccounts.add(new AccountImple(type, qqNum, 1.0));
            }
            svps.add(new VP() {

                @Override
                public String uniqueId() {
                    return vpName;
                }

                @Override
                public Iterator<Account> accounts() {
                    return vpAccounts.iterator();
                }
            });
        }

        return svps;
    }

    private static List<Element> getVPElements(String dataXml) {
        return XmlUtil.getDocument(dataXml).selectNodes("/root//VP");
    }

    private static List<Element> getVPAccountElements(Element vpEle) {
        return vpEle.selectNodes(vpEle.getUniquePath() + "/Accounts//Account");
    }
}
