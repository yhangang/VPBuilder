/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.demo;

import com.audaque.vp.utils.XmlUtil;
import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.account.Attribute;
import com.audaque.vpbase.account.AttributiveAccount;
import com.audaque.vpbase.account.ExtendedAccount;
import com.audaque.vpbase.db.VPDatabase;
import com.audaque.vpbase.vp.VP;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 *
 * @author Liyu.Cai@audaque.com
 */
public class DemoXmlVPDatabase implements VPDatabase {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private final Document baseDocument;
    private final Element rootEle;
    private final String newDocPath;
    /**
     * 数据库是否正在被初始化--即调用addInitVP().
     */
    private boolean initializing;

    public DemoXmlVPDatabase(String baseXml) {
        baseDocument = XmlUtil.getDocument(baseXml);
        rootEle = baseDocument.getRootElement();
        newDocPath = baseXml + ".updated.xml";
        initializing = false;
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
    public String createVP(Iterator<Account> accounts) {
        String id = java.util.UUID.randomUUID().toString();
        Element vpEle = rootEle.addElement("VP");
        vpEle.addAttribute("id", id);
        vpEle.addAttribute("disable", "false");
        Element accountsEle = vpEle.addElement("Accounts");
        while (accounts.hasNext()) {
            Account a = accounts.next();
            Element accountEle = accountsEle.addElement("Account");
            accountEle.addAttribute("type", a.serviceId());
            accountEle.addAttribute("id", a.userId());
        }
        updateXml();
        return id;
    }

    @Override
    public boolean setVPAttributes(String uniqueId, String[] attributes, String[] values) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Account getAccount(String uniqueId) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ExtendedAccount getExtendAccount(Account account, String extendCode) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterator<AttributiveAccount> getAccounts(String[] attributes) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String[] getAttributes(Account account, Attribute[] attributes) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean disable(VP vp) {
        Element vpEle = XmlUtil.getUniqueElement(rootEle, "/root/VP[@id='" + vp.uniqueId() + "']");
        if (vpEle != null) {
            vpEle.addAttribute("disable", "true");
            updateXml();
            return true;
        } else {
            return false;
        }
    }

    private void updateXml() {
        XmlUtil.toFile(new File(newDocPath), baseDocument);
    }

    @Override
    public boolean addInitVP(String vpInfo, String filedSplit) {
        checkDBState(true);
        String[] fields = vpInfo.split(filedSplit);

        Preconditions.checkArgument(fields.length == 2, "vpInfo格式错误: vpInfo.split(fieldSplit).length!=2:" + vpInfo);

        String vpId = fields[0];
        String typeId = fields[1];
        String accountType = typeId.split(":")[0];
        String accountId = typeId.split(":")[1];

        Element vpEle = XmlUtil.getUniqueElement(rootEle, "/root/VP[@id='" + vpId + "']");
        if (vpEle == null) {
            vpEle = rootEle.addElement("VP");
            vpEle.addAttribute("id", vpId);
        }

        Element acontsEle = XmlUtil.getUniqueElement(rootEle, vpEle.getUniquePath() + "/Accounts");
        if (acontsEle == null) {
            acontsEle = vpEle.addElement("Accounts");
        }

        Element acontEle = XmlUtil.getUniqueElement(rootEle, acontsEle.getUniquePath() + "/Account[@type='" + accountType + "' and @id='" + accountId + "']");
        if (acontEle == null) {
            acontEle = acontsEle.addElement("Account");
            acontEle.addAttribute("type", accountType);
            acontEle.addAttribute("id", accountId);

        }

        updateXml();
        return true;
    }

    @Override
    public boolean refreshVP(VP vp, Iterator<Account> accounts) {
        Element vpEle = XmlUtil.getUniqueElement(rootEle, "/root/VP[@id='" + vp.uniqueId() + "']");
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
            throw new IllegalStateException("尝试刷新一个不存在的虚拟人:" + vp.uniqueId()); 
        }
    }

    private void checkDBState(boolean legalState) {
        if (initializing != legalState) {
            throw new IllegalStateException("数据库所处初始化状态与期望状态不同，实际初始化状态:" + initializing);
        }
    }

    @Override
    public boolean beforeInitVP() {
        checkDBState(false);
        initializing = true;
        /**
         * clean all records
         */
        rootEle.clearContent();
        return true;
    }

    @Override
    public boolean afterInitVP() {
        checkDBState(true);
        initializing = false;
        return true;
    }

    @Override
    public Attribute[] getPredefineVPAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String[] getPredefinedVPAttributeValues(Account account) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getVPUniId(String serviceId, String userId) {
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
    public Map<String, String> getPredefinedVPAttributes(String serviceId, String userId) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
