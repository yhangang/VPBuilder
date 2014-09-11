/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.attribute.update.match;

import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.account.AttributiveAccount;
import com.audaque.vpbase.vp.VP;
import com.audaque.vpbase.db.VPUpdateDatabase;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author hang.yang@audaque.com
 */
public class VPMatchIncrement {

    public void match(VPUpdateDatabase vpUpdateDatabase, MatchContext matchContext, String[] rules, int recordLimit) {

        boolean isSuccess = true;

        Iterator<AttributiveAccount> changedAccountsIterator = vpUpdateDatabase.getAttributeChangedAccounts();

        //对增量数据进行遍历处理
        while (changedAccountsIterator.hasNext()) {
            Set<VP> vpSet = new HashSet<>();
            Set<Account> accountSet = new HashSet<>();

            //获得一条增量数据
            AttributiveAccount record = changedAccountsIterator.next();
            //加入本条记录
            accountSet.add(record);
            //找到之前所属的虚拟人，可能为null
            VP formerVP = vpUpdateDatabase.getVP(record.serviceId(), record.userId());
            if(formerVP!=null){
                vpSet.add(formerVP);
            }

            //根据规则得到所有匹配的uniqueID
            List<String> sqls = matchContext.getSQLByContext(rules, record, recordLimit);
            System.out.println(sqls);
            List<List<String>> attrsList = matchContext.getAttrsListByContext();
            for(List<String> sttrList:attrsList){
                System.out.println(sttrList);
            }
            Iterator<Account> accountsIterator = vpUpdateDatabase.getMatchedAccountBySQL(sqls,attrsList);
            
            //根据所有匹配的uniqueID得到对应的VID，放入set去重
            while (accountsIterator.hasNext()) {
                Account acc = accountsIterator.next();
                accountSet.add(acc);
                VP vp = vpUpdateDatabase.getVP(acc.serviceId(), acc.userId());
                if (vp != null) {
                    vpSet.add(vp);
                }
            }
            //根据所有的vid得到包含的uniqueID，放入set中
            for (VP vp : vpSet) {
                Iterator<Account> vpSetIterator = vp.accounts();
                while (vpSetIterator.hasNext()) {
                    accountSet.add(vpSetIterator.next());
                }
            }
            //向HBASE插入新的虚拟人记录
            System.out.println("虚拟人共有账户" + accountSet.size() + "个");
            for(Account acc:accountSet){
                System.out.println("账户主键："+acc.uniqueId());
            }
            //生成新的虚拟人vid
            String newVid = UUID.randomUUID().toString();
            if (!vpUpdateDatabase.createVP(newVid, accountSet.iterator())) {
                isSuccess = false;
            }
            //删除之前的虚拟人
            for (VP vp : vpSet) {
                System.out.println("删除虚拟人" + vp.uniqueId());
                if (!vpUpdateDatabase.disableVP(vp.uniqueId())) {
                    isSuccess = false;
                }
            }
            //通知后台哪些虚拟人被合并了
            Set<String> oldVidSet = new HashSet<>();
            for(VP vp:vpSet){
                oldVidSet.add(vp.uniqueId());
            }
            vpUpdateDatabase.notifyVPMerge(oldVidSet.iterator(), newVid);
        }
        //如果所有操作都执行成功，调用markAttributeChangedDone方法，并判断其是否成功
        if (isSuccess) {
            if (!vpUpdateDatabase.markAttributeChangedDone()) {
                throw new IllegalStateException("写入成功状态异常");
            }
        }
    }
}
