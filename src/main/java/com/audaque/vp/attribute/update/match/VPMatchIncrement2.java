///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.audaque.vp.attribute.update.match;
//
//import com.audaque.vpbase.account.Account;
//import com.audaque.vpbase.account.AttributiveAccount;
//import com.audaque.vpbase.vp.VP;
//import com.audaque.vpbase.db.VPUpdateDatabase;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
///**
// *
// * @author Administrator
// */
//public class VPMatchIncrement2 {
//
//    public void match(VPUpdateDatabase vpUpdateDatabase, MatchContext matchContext, String[] rules, int recordLimit) {
//
//        boolean isSuccess = true;
//
//        Iterator<AttributiveAccount> changedAccountsIterator = vpUpdateDatabase.getAttributeChangedAccounts();
//
//        //对增量数据进行遍历处理
//        while (changedAccountsIterator.hasNext()) {
//            Set<VP> vpSet = new HashSet<>();
//            Set<Account> accountSet = new HashSet<>();
//
//            boolean belongFormerVP = false;
//
//            //获得一条增量数据
//            AttributiveAccount record = changedAccountsIterator.next();
//            //获得该账户之前所属的VP,可能为null
//            VP formerVP = vpUpdateDatabase.getVP(record.serviceId(), record.userId());
//
//            //根据规则得到所有匹配的uniqueID
//            List<String> sqls = matchContext.getSQLByContext(rules, record, recordLimit);
//            System.out.println(sqls);
//            Iterator<Account> accountsIterator = vpUpdateDatabase.getMatchedAccountBySQL(sqls);
//            //没有查到数据，略过
//            if (accountsIterator == null || !accountsIterator.hasNext()) {
//                continue;
//            }
//            //根据所有匹配的uniqueID得到对应的VID，放入set去重
//            while (accountsIterator.hasNext()) {
//                Account acc = accountsIterator.next();
//                accountSet.add(acc);
//                //将增量账户本身剔除,否则不能知道修改的后的账户是否还属于原虚拟人
//                if (acc.uniqueId().equals(record.uniqueId())) {
//                    continue;
//                }
//                VP vp = vpUpdateDatabase.getVP(acc.serviceId(), acc.userId());
//                if (vp != null) {
//                    vpSet.add(vp);
//                    //判断该增量账户是否还属于之前的虚拟人
//                    if (formerVP != null && vp.uniqueId().equals(formerVP.uniqueId())) {
//                        belongFormerVP = true;
//                    }
//                }
//            }
//            //根据所有的vid得到包含的uniqueID，放入set中
//            for (VP vp : vpSet) {
//                Iterator<Account> vpSetIterator = vp.accounts();
//                while (vpSetIterator.hasNext()) {
//                    accountSet.add(vpSetIterator.next());
//                }
//            }
//            System.out.println("虚拟人共有账户" + accountSet.size() + "个");
//            System.out.println("合并虚拟人" + vpSet.size() + "个");
//            System.out.println(belongFormerVP);
//            //新增的账户，或者修改的账户仍属于以前的虚拟人，直接新建虚拟人
//            if (formerVP == null || belongFormerVP) {
//                //生成新的虚拟人vid
//                String newVid = UUID.randomUUID().toString();
//                if (!vpUpdateDatabase.createVP(newVid, accountSet.iterator())) {
//                    isSuccess = false;
//                }
//                //删除之前的虚拟人
//                for (VP vp : vpSet) {
//                    System.out.println("删除虚拟人：" + vp.uniqueId());
//                    if (!vpUpdateDatabase.disableVP(vp.uniqueId())) {
//                        isSuccess = false;
//                    }
//                }
//            }//修改的账户，不属于以前的虚拟人，还需要更新以前的虚拟人 
//            else {
//                //生成新虚拟人
//                String newVid1 = UUID.randomUUID().toString();
//                if (!vpUpdateDatabase.createVP(newVid1, accountSet.iterator())) {
//                    isSuccess = false;
//                }
//                //删除之前的虚拟人
//                for (VP vp : vpSet) {
//                    if (!vpUpdateDatabase.disableVP(vp.uniqueId())) {
//                        isSuccess = false;
//                    }
//                }
//                //修改之前的虚拟人
//                if (!vpUpdateDatabase.disableVP(formerVP.uniqueId())) {
//                    isSuccess = false;
//                }
//                String newVid2 = UUID.randomUUID().toString();
//                if (!vpUpdateDatabase.createVP(newVid2, getUpdatedAccountIterator(formerVP, record))) {
//                    isSuccess = false;
//                }
//            }
//
//        }
//        //如果所有操作都执行成功，调用markAttributeChangedDone方法，并判断其是否成功
//        if (isSuccess) {
//            if (!vpUpdateDatabase.markAttributeChangedDone()) {
//                throw new IllegalStateException("写入成功状态异常");
//            }
//        }
//    }
//
//    //将vp中的account账户删除，更新其成员
//
//    public Iterator<Account> getUpdatedAccountIterator(VP vp, Account account) {
//        if (account == null) {
//            return vp.accounts();
//        }
//        List<Account> accList = new ArrayList<>();
//        Iterator<Account> vpIterator = vp.accounts();
//        while (vpIterator.hasNext()) {
//            Account acc = vpIterator.next();
//            if (!acc.uniqueId().equals(account.uniqueId())) {
//                accList.add(acc);
//            }
//        }
//        return accList.iterator();
//    }
//}
