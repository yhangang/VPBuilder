package com.audaque.vp.attribute.update.match;

import com.audaque.vpbase.account.Attribute;
import com.audaque.vpbase.account.AttributiveAccount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangguancheng on 14-6-30.
 */
public class MatchContext2 {

    //虚拟人的分析属性
    public String[] PREDEFINE_ATTRIBUTES;

    //账号类型->账号表的映射
    private Map<String, String> typeTableMap;

    //表名－> <虚拟人属性列，列名>
    private Map<String, Map<String, String>> tableAnalyticCols;

    //表名->queryAttrSql
    private Map<String, String> queryAttrMap = new HashMap<>();
    
    //存放占位符对应属性的List
    List<List<String>> attrsList = new ArrayList<>(); 

    public Map<String, String> getTypeTableMap() {
        return typeTableMap;
    }

    public void setTypeTableMap(Map<String, String> typeTableMap) {
        this.typeTableMap = typeTableMap;
    }

    public Map<String, Map<String, String>> getTableAnalyticCols() {
        return tableAnalyticCols;
    }

    public void setTableAnalyticCols(Map<String, Map<String, String>> tableAnalyticCols) {
        this.tableAnalyticCols = tableAnalyticCols;
    }

    public Map<String, String> getQueryAttrMap() {
        return queryAttrMap;
    }

    public void setQueryAttrMap(Map<String, String> queryAttrMap) {
        this.queryAttrMap = queryAttrMap;
    }

    public String[] getPREDEFINE_ATTRIBUTES() {
        return PREDEFINE_ATTRIBUTES;
    }

    public void setPREDEFINE_ATTRIBUTES(String[] PREDEFINE_ATTRIBUTES) {
        this.PREDEFINE_ATTRIBUTES = PREDEFINE_ATTRIBUTES;
    }

    /**
     * 返回特定表对应的SQL语句
     *
     * @param type
     * @return
     */
    public String queryAttrSql(String type) {
        String table = typeTableMap.get(type);
        if (table != null) {
            return queryAttrMap.get(table);
        }
        return null;
    }

    /**
     * 将MAP中的sql语句转化为List返回
     *
     * @param rules
     * @param account
     * @param limit
     * @return
     */
    public List<String> getSQLByContext(String[] rules, AttributiveAccount account, int limit) {
        List<String> sqls = new ArrayList<>();
        initAttrQueryMap(rules, account, limit);
        for (Map.Entry<String, String> entry : typeTableMap.entrySet()) {
            if (queryAttrSql(entry.getKey()) != null) {
                sqls.add(queryAttrSql(entry.getKey()));
            }
        }
        return sqls;
    }
    /**
     * 返回占位符属性List
     * @return 
     */
    public List<List<String>> getAttrsListByContext(){
        return attrsList;
    }

    /**
     * 根据MAP映射初始化各表对应的SQL语句
     *
     * @param rules 规则字符串
     * @param account
     * @param limit
     */
    public void initAttrQueryMap(String[] rules, AttributiveAccount account, int limit) {
        Map<String, String> attrmap = getAttrMapFromAccount(account);
        queryAttrMap.clear();
        attrsList.clear();

        if (PREDEFINE_ATTRIBUTES == null || PREDEFINE_ATTRIBUTES.length < 2) {
            throw new IllegalStateException("初始化参数PREDEFINE_ATTRIBUTES错误，长度不足！");
        }

        for (Map.Entry<String, String> entry : typeTableMap.entrySet()) {
            String table = entry.getValue();
            StringBuilder sql = new StringBuilder();
            StringBuilder fieldPart = new StringBuilder();
            StringBuilder conditions = new StringBuilder();

            Map<String, String> analyticCols = tableAnalyticCols.get(table);
            //生成sql语句  select from部分
            fieldPart.append("'").append(entry.getKey()).append("'").append(" as ").append("\"").append(PREDEFINE_ATTRIBUTES[0])
                    .append("\"").append(",");
            fieldPart.append("\"").append(analyticCols.get(PREDEFINE_ATTRIBUTES[1])).append("\"" + " as " + "\"").append(PREDEFINE_ATTRIBUTES[1]).append("\"");

//            for (int i = 0; i < PREDEFINE_ATTRIBUTES.length; i++) {
//                String field = PREDEFINE_ATTRIBUTES[i];
//                if (analyticCols.containsKey(field)) {
//                    fieldPart.append("\"").append(analyticCols.get(field)).append("\"" + " as " + "\"").append(field).append("\"");
//                } else {
//                    fieldPart.append("'' as " + "\"").append(field).append("\"");
//                }
//                if (i < PREDEFINE_ATTRIBUTES.length - 1) {
//                    fieldPart.append(", ");
//                }
//            }
//            /根据rules生成查询条件where部分
            
            List<String> attrList = new ArrayList<>();
            List<String> ruleList = getValidRules(rules, analyticCols);
            for (int j = 0; j < ruleList.size(); j++) {
                String[] field = ruleList.get(j).split(",", -1);

                //检查匹配字段是不是空值，如果是空值则不能匹配，跳过该条件
                boolean isValidMatchData = true;
                for (int i = 0; i < field.length; i++) {
                    if (attrmap.get(field[i]) == null || "".equals(attrmap.get(field[i]))) {
                        isValidMatchData = false;
                    }
                }
                if (!isValidMatchData) {
                    continue;
                }
                
                conditions.append("(");
                for (int i = 0; i < field.length; i++) {
                    conditions.append("\"").append(analyticCols.get(field[i])).append("\"=").append("?");
                    attrList.add(attrmap.get(field[i]));
                    if (i < field.length - 1) {
                        conditions.append(" and ");
                    }
                }
                conditions.append(")");
                conditions.append(" or ");
            }
            //去除最后一个" or "
            if (conditions.length() > 0) {
                conditions = new StringBuilder(conditions.substring(0, conditions.length() - " or ".length()));
            } else {
                continue;
            }

            //该表有有效字段时，拼装SQL语句
            if (conditions.length() > 0) {
                sql.append("select ").append(fieldPart).append(" from ").append("\"").append(table)
                        .append("\"").append(" where ").append(conditions).append(" limit ").append(limit);
                queryAttrMap.put(table, sql.toString());
                //占位符属性
                attrsList.add(attrList);
            }
        }
    }

    /**
     * 将Account转化为MAP类型，方便取值
     *
     * @param account
     * @return
     */
    public Map<String, String> getAttrMapFromAccount(AttributiveAccount account) {
        Map<String, String> attrMap = new HashMap<>();
        Attribute[] attributes = account.attributes();
        String[] attributeValues = account.attributeValues();

        try {
            for (int i = 0; i < attributes.length; i++) {
                attrMap.put(attributes[i].name(), attributeValues[i]);
            }
        } catch (Exception e) {
            throw new IllegalStateException("账户数据转化为MAP异常！");
        }
        return attrMap;
    }

    /**
     * 由于规则中的字段不是每张表都有，所以返回相应表中有的字段
     *
     * @param rules
     * @param analyticCols
     * @return
     */
    public List<String> getValidRules(String[] rules, Map<String, String> analyticCols) {
        List<String> ruleList = new ArrayList<>();
        boolean flag;
        for (int i = 0; i < rules.length; i++) {
            String[] field = rules[i].split(",", -1);
            flag = true;
            for (int j = 0; j < field.length; j++) {
                if (!analyticCols.containsKey(field[j])) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ruleList.add(rules[i]);
            }
        }
        return ruleList;
    }

}
