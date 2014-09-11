/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.attribute.update.db;

import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.account.Attribute;
import com.audaque.vpbase.account.AttributeVal;
import com.audaque.vpbase.account.AttributiveAccount;
import com.audaque.vpbase.account.ExtendedAccount;
import com.audaque.vpbase.db.VPUpdateDatabase;
import com.audaque.vpbase.vp.VP;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class DemoVPUpdateDatabase implements VPUpdateDatabase {

    private List<AttributiveAccount> vpList;
    private List<Account> queryedAccList;

    public DemoVPUpdateDatabase() {
        vpList = new ArrayList<>();

        DemoAttributiveAccount account = new DemoAttributiveAccount("qq", "001");
        Attribute[] attribute = {new DemoAttribute("cardid"), new DemoAttribute("email"), new DemoAttribute("QQ"),
            new DemoAttribute("name"),  new DemoAttribute("sex"),new DemoAttribute("mbphone"), new DemoAttribute("homephone")};
        String[] attributeValues = {"5654", "123@qq.com", "323212", "hangyang", null,"131111", "022222"};
        account.setAttribute(attribute);
        account.setAttributeValues(attributeValues);

        DemoAttributiveAccount account2 = new DemoAttributiveAccount("qq", "002");
        Attribute[] attribute2 = {new DemoAttribute("cardid"), new DemoAttribute("email"), new DemoAttribute("QQ"),
            new DemoAttribute("name"),new DemoAttribute("sex"), new DemoAttribute("mbphone"), new DemoAttribute("homephone")};
        String[] attributeValues2 = {"4545", "fgfgfg@audaque.com", "8999", "as","male", "33333", "0429999"};
        account2.setAttribute(attribute2);
        account2.setAttributeValues(attributeValues2);

        vpList.add(account);
        vpList.add(account2);

        queryedAccList = new ArrayList<>();
        queryedAccList.add(new Account() {

            @Override
            public String serviceId() {
                return "renren";
            }

            @Override
            public String userId() {
                return "001";
            }

            @Override
            public String uniqueId() {
                return serviceId() + ":" + userId();
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
    public VP getVP(String arg0, String arg1) {
        VP vp = new DemoVP(arg0, arg1);
        return vp;
    }

    @Override
    public Iterator<AttributiveAccount> getAttributeChangedAccounts() {
        return vpList.iterator();
    }

    @Override
    public boolean markAttributeChangedDone() {
        System.out.println("所有操作成功完成！");
        return true;
    }

    @Override
    public boolean disableVP(String oldVpId) {
        System.out.println("虚拟人删除成功！");
        return true;
    }

    @Override
    public boolean createVP(String newVPId, Iterator<Account> accounts) {
        System.out.println("虚拟人ID:" + newVPId + "创建成功！");
        return true;
    }

    @Override
    public ExtendedAccount getExtendAccount(Account arg0, String arg1) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public VP getVP(String uniqueId) {
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
        return queryedAccList.iterator();
    }

    @Override
    public boolean notifyVPMerge(Iterator<String> oldVpIds, String newVpId) {
        System.out.println("notifyVPMerge");
        return true;
    }

    @Override
    public boolean upsertVPAttrs(String vid, Map<String, AttributeVal> destmap, Attribute[] atrisToOutput) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
