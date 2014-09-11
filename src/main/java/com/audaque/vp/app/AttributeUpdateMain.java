/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.app;

import com.audaque.vp.attribute.update.match.MatchContext;
import com.audaque.vp.attribute.update.match.VPMatchIncrement;
import com.audaque.vpbase.db.VPUpdateDatabase;

/**
 *
 * @author Administrator
 */
public class AttributeUpdateMain {

    /**
     * 调用虚拟人建立增量算法
     *
     * @param vpUpdateDatabase VPUpdateDatabase对象
     * @param matchContext MatchContext对象
     * @param rules 匹配规则字符串
     * @param recordLimit 记录限制数limit
     */
    public static void run(VPUpdateDatabase vpUpdateDatabase, MatchContext matchContext, String[] rules, int recordLimit) {
        VPMatchIncrement vpmi = new VPMatchIncrement();
        vpmi.match(vpUpdateDatabase, matchContext, rules, recordLimit);
    }

//    public static void main(String[] args) {
//        String[] PREDEFINE_ATTRIBUTES = new String[]{
//            "acctype",
//            "id"};
//        String type = "test";
//        String table = "vp_account";
//   //     虚拟人属性列->列名
//        Map<String, String> analyticCols = new HashMap<>();
//        analyticCols.put("id", "id_");
//        analyticCols.put("name", "name_");
//        analyticCols.put("sex", "sex_");
//        analyticCols.put("cardid", "cardid_");
//        analyticCols.put("mbphone", "mbphone_");
//        analyticCols.put("homephone", "homephone_");
//        analyticCols.put("email", "email_");
//        analyticCols.put("QQ", "QQ_");
//
//        MatchContext context = new MatchContext();
//
//        Map<String, String> typeTableMap = new HashMap<>();
//        typeTableMap.put(type, table);
//        context.setTypeTableMap(typeTableMap);
//
//        //表名－> <虚拟人属性列，列名>
//        Map<String, Map<String, String>> tableAnalyticCols = new HashMap<>();
//        tableAnalyticCols.put(table, analyticCols);
//        context.setTableAnalyticCols(tableAnalyticCols);
//
//        context.setPREDEFINE_ATTRIBUTES(PREDEFINE_ATTRIBUTES);
//
//        String[] rules = {"cardid", "email", "name,QQ,sex", "name,mbphone", "name,homephone"};
//        int limit = 1000;
//        VPMatchIncrement vpmi = new VPMatchIncrement();
//        vpmi.match(new DemoVPUpdateDatabase(), context, rules, limit);
//    }
}
