/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.audaque.vp.attr;

import com.audaque.vp.attribute.update.db.DemoAttribute;
import com.audaque.vp.attribute.update.db.DemoAttributiveAccount;
import com.audaque.vp.attribute.update.match.MatchContext;
import com.audaque.vpbase.account.Attribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Administrator
 */
public class VPUpdateGenerateSQLTest {
    
    @Test
    public void test(){
        DemoAttributiveAccount account;
        MatchContext matchContext;
         String[] rules= {"cardid", "email", "name,qq_account", "name,mbphone", "name,homephone"};
         int recordLimit = 1000;
         
         account = new DemoAttributiveAccount("qq", "001");
        Attribute[] attribute = {new DemoAttribute("cardid"), new DemoAttribute("email"), new DemoAttribute("QQ"),
            new DemoAttribute("name"), new DemoAttribute("mbphone"), new DemoAttribute("homephone")};
        String[] attributeValues = {"", "13@qq.com", "", "", "", ""};
        account.setAttribute(attribute);
        account.setAttributeValues(attributeValues);
         
         
        matchContext = new MatchContext();
          String[] PREDEFINE_ATTRIBUTES = new String[]{
            "account_type_id",
            "account_id"};
        String type = "yixun";
        String table = "yixun_";
//        虚拟人属性列->列名
        Map<String, String> analyticCols = new HashMap<String, String>();
        analyticCols.put("account_id", "uid_");
        analyticCols.put("name", "name_");
        analyticCols.put("cardid", "cardid_");
        analyticCols.put("mbphone", "mbphone_");
        analyticCols.put("homephone", "homephone_");
        analyticCols.put("email", "email_");
        analyticCols.put("qq_account", "qq_account_");
        Map<String, String> typeTableMap = new HashMap<String, String>();
        typeTableMap.put(type, table);
        matchContext.setTypeTableMap(typeTableMap);
        //表名－> <虚拟人属性列，列名>
        Map<String, Map<String, String>> tableAnalyticCols = new HashMap<String, Map<String, String>>();
        tableAnalyticCols.put(table, analyticCols);
        matchContext.setTableAnalyticCols(tableAnalyticCols);
        matchContext.setPREDEFINE_ATTRIBUTES(PREDEFINE_ATTRIBUTES);
        
        List<String> sqls = matchContext.getSQLByContext(rules, account, recordLimit);
            System.out.println(sqls);
//            Assert.assertEquals("select 'yixun' as \"account_type_id\",\"uid_\" as \"account_id\" from \"yixun_\" where (\"email_\"='?') limit 1000", sqls.get(0));
    }
    
}
