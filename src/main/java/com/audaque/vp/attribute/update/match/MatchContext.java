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
public class MatchContext {

    //虚拟人的分析属性
    public String[] PREDEFINE_ATTRIBUTES;

    //账号类型->账号表的映射
    private Map<String, String> typeTableMap;

    //表名－> <虚拟人属性列，列名>
    private Map<String, Map<String, String>> tableAnalyticCols;

    //存放sql语句集合
    private List<String> sqlList = new ArrayList<>();

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

    public String[] getPREDEFINE_ATTRIBUTES() {
        return PREDEFINE_ATTRIBUTES;
    }

    public void setPREDEFINE_ATTRIBUTES(String[] PREDEFINE_ATTRIBUTES) {
        this.PREDEFINE_ATTRIBUTES = PREDEFINE_ATTRIBUTES;
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
        //初始化sql及占位符值
        initAttrQueryMap(rules, account, limit);
        return sqlList;
    }

    /**
     * 返回占位符属性List
     *
     * @return
     */
    public List<List<String>> getAttrsListByContext() {
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
        sqlList.clear();
        attrsList.clear();

        if (PREDEFINE_ATTRIBUTES == null || PREDEFINE_ATTRIBUTES.length < 2) {
            throw new IllegalStateException("初始化参数PREDEFINE_ATTRIBUTES错误，长度不足！");
        }

        for (Map.Entry<String, String> entry : typeTableMap.entrySet()) {
            String table = entry.getValue();
            StringBuilder fieldPart = new StringBuilder();

            Map<String, String> analyticCols = tableAnalyticCols.get(table);
            //生成sql语句  select from部分
            fieldPart.append("'").append(entry.getKey()).append("'").append(" as ").append("\"").append(PREDEFINE_ATTRIBUTES[0])
                    .append("\"").append(",");
            fieldPart.append("\"").append(analyticCols.get(PREDEFINE_ATTRIBUTES[1])).append("\"" + " as " + "\"").append(PREDEFINE_ATTRIBUTES[1]).append("\"");

            
            List<String> conditionsList = new ArrayList<>();
            List<String> ruleList = getValidRules(rules, analyticCols);
            for (int j = 0; j < ruleList.size(); j++) {
                String[] field = ruleList.get(j).split(",", -1);
                StringBuilder conditions = new StringBuilder();
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
                //根据rules生成查询条件where部分
            List<String> attrList = new ArrayList<>();
                for (int i = 0; i < field.length; i++) {
                    conditions.append("\"").append(analyticCols.get(field[i])).append("\"=").append("?");
                    attrList.add(attrmap.get(field[i]));
                    if (i < field.length - 1) {
                        conditions.append(" and ");
                    }
                }
                conditionsList.add(conditions.toString());
                attrsList.add(attrList);
            }

            //该表有有效字段时，拼装SQL语句
            for (int i=0;i<conditionsList.size();i++) {
                StringBuilder sql = new StringBuilder();
                sql.append("select ").append(fieldPart).append(" from ").append("\"").append(table)
                        .append("\"").append(" where ").append(conditionsList.get(i)).append(" limit ").append(limit);
                //占位符属性
                sqlList.add(sql.toString());
                
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
