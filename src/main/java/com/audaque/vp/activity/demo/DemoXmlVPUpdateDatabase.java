/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vp.utils.XmlUtil;
import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.account.Attribute;
import com.audaque.vpbase.account.AttributeVal;
import com.audaque.vpbase.account.AttributiveAccount;
import com.audaque.vpbase.account.ExtendedAccount;
import com.audaque.vpbase.db.VPUpdateDatabase;
import com.audaque.vpbase.vp.VP;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 *
 * @author Liyu.Cai@audaque.com
 */
public class DemoXmlVPUpdateDatabase implements VPUpdateDatabase {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private final Document baseDocument;
    private final Element rootEle;
    private final String newDocPath;
    /**
     * 数据库是否正在被初始化--即调用addInitVP().
     */
    private boolean initializing;

    public DemoXmlVPUpdateDatabase(String baseXml) {
        baseDocument = XmlUtil.getDocument(baseXml);
        rootEle = baseDocument.getRootElement();
        newDocPath = baseXml + ".updated.xml";
        initializing = false;
    }

    public VP getVP(String uniqueId) {
        checkDBState(false);
        Element vpEle = getVPElement(uniqueId);
        if (vpEle != null) {
            return new DemoElementVP(vpEle, uniqueId);
        } else {
            return null;
        }
    }

    private Element getVPElement(String uniqueId) {
        String xpath = "/root/VP[@id='" + uniqueId + "']";
        return XmlUtil.getUniqueElement(rootEle, xpath);

    }

    @Override
    public boolean createVP(String newVPId, Iterator<Account> accounts) {
        if (getVP(newVPId) != null) {
            refreshVP(newVPId, accounts);
        } else {
            Element vpEle = rootEle.addElement("VP");
            vpEle.addAttribute("id", newVPId);
            vpEle.addAttribute("disable", "false");
            Element accountsEle = vpEle.addElement("Accounts");
            while (accounts.hasNext()) {
                Account a = accounts.next();
                Element accountEle = accountsEle.addElement("Account");
                accountEle.addAttribute("type", a.serviceId());
                accountEle.addAttribute("id", a.userId());
            }
        }
        updateXml();
        return true;
    }

    private void updateXml() {
        XmlUtil.toFile(new File(newDocPath), baseDocument);
    }

    public boolean refreshVP(String vpUniId, Iterator<Account> accounts) {
        Element vpEle = XmlUtil.getUniqueElement(rootEle, "/root/VP[@id='" + vpUniId + "']");
        if (vpEle != null) {
            vpEle.clearContent();
            while (accounts.hasNext()) {
                Account account = accounts.next();
                Element acontEle = vpEle.addElement("Account");
                acontEle.addAttribute("type", account.serviceId());
                acontEle.addAttribute("id", account.userId());
            }
            return true;
        } else {
            throw new IllegalStateException("尝试刷新一个不存在的虚拟人:" + vpUniId); 
        }
    }

    private void checkDBState(boolean legalState) {
        if (initializing != legalState) {
            throw new IllegalStateException("数据库所处初始化状态与期望状态不同，实际初始化状态:" + initializing);
        }
    }

    @Override
    public VP getVP(String serviceId, String userId) {
        checkDBState(false);
        String xpath = "/root/VP[@disable='false']/Accounts/Account[@type='" + serviceId
                + "' and @id='" + userId + "']";
        Element accountEle = XmlUtil.getUniqueElement(baseDocument.getRootElement(), xpath);
        if (accountEle != null) {
            Element vpEle = accountEle.getParent();
            if (vpEle != null) {
                vpEle = vpEle.getParent();
                return new DemoElementVP(vpEle, vpEle.attributeValue("id"));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Iterator<AttributiveAccount> getAttributeChangedAccounts() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean markAttributeChangedDone() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean disableVP(String oldVpId) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ExtendedAccount getExtendAccount(Account account, String extendCode) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Attribute[] getPredefineVPAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<String, String> getPredefinedVPAttributes(String serviceId, String userId) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean onCreate(String parameters) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean onClose(String parameters) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterator<Account> getMatchedAccountBySQL(List<String> sqls, List<List<String>> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean notifyVPMerge(Iterator<String> oldVpIds, String newVpId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean upsertVPAttrs(String vid, Map<String, AttributeVal> destmap, Attribute[] atrisToOutput) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
