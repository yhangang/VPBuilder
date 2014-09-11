/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.model;

import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.db.VPDatabase;
import com.audaque.vpbase.vp.VP;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 虚拟人建立的增量算法.
 *
 * @author Liyu.Cai@Audaque.com
 */
public class VPDbUpdateMethods {

    private static final int DEFAULT_NEW_ACCOUNT_BUFFER_SIZE = 10;
    private static final int DEAFULT_OLD_ACCOUNT_BUFFER_SIZE = 50;
    private static final int DEFAULT_OLD_VP_BUFFER_SIZE = 10;

    public static void liteUpdate(VPDatabase vpDb, Iterator<VP> deltaVPs) throws IllegalStateException {
        liteUpdate(vpDb, deltaVPs, DEFAULT_NEW_ACCOUNT_BUFFER_SIZE, DEAFULT_OLD_ACCOUNT_BUFFER_SIZE, DEFAULT_OLD_VP_BUFFER_SIZE);
    }

    public static void liteUpdate(VPDatabase vpDb, Iterator<VP> deltaVPs, int estimateNewAccountCounts, int estimateOldAccountCounts, int estimateOldVPCounts) throws IllegalStateException {
        /**
         * 注意, 基础的Account并不能确保hashCode和equals函数返回的正确性，
         * 故不能用于判断，进而，不可使用HashSet等结构来进行默认去重. TODO: fix this.
         */
        while (deltaVPs.hasNext()) {
            /*注意deltaVP的UniqueID并未经过数据库确认其唯一性，故不可用于判断*/
            VP deltaVP = deltaVPs.next();
            Iterator< Account> deltaAccounts = deltaVP.accounts();

            /*存储更新记录中，以前在数据库中没有存过的帐号*/
            Collection<Account> newBornAccounts = new ArrayList<>(estimateNewAccountCounts);
            /*存储更新记录中已经在数据库中存过的帐号，和这些帐号对应的虚拟人下的其他帐号*/
            Collection<Account> oldAccounts = new ArrayList<>(estimateOldAccountCounts);
            /*存储更新记录中已经在数据库中存过的那些帐号对应的虚拟人*/
            Collection<VP> oldVPs = new HashSet<>(estimateOldVPCounts);

            while (deltaAccounts.hasNext()) {
                Account deltaAccount = deltaAccounts.next();
                VP oldVP = vpDb.getVP(deltaAccount.serviceId(), deltaAccount.userId());
                if (oldVP == null) {
                    //该帐号之前没被存储过.
                    newBornAccounts.add(deltaAccount);
                } else {
                    /*判断该OldVP是否已经存到oldVPs中过*/
                    boolean buffered = false;
                    for (VP vp : oldVPs) {
                        if (vp.uniqueId().equals(oldVP.uniqueId())) {
                            buffered = true;
                            break;
                        }
                    }
                    /*没有的话，将该oldVP旗下所有尚未加入odAccounts的帐号加入进去                        */
                    if (!buffered) {
                        oldVPs.add(oldVP);
                        Iterator<Account> iter = oldVP.accounts();
                        while (iter.hasNext()) {
                            Account oldAccount = iter.next();
                            boolean found = false;
                            for (Account acc : oldAccounts) {
                                if (acc.uniqueId().equals(oldAccount.uniqueId())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                oldAccounts.add(oldAccount);
                            }
                        }
                        /*deltaVP应该已经在oldVP旗下,故不需要再加入*/
                    }
                }
            }
            /**
             * 下面这个if是LiteUpdate与Update()的唯一区别： 当只涉及一个旧的VP时，只是将新生的帐号加入到该VP即可.
             * 但在Update中，旧的VP会标记为过时disable()==true,旗下的 帐号与新生帐号一起构成一个新的虚拟人.
             *
             */
            if (oldVPs.size() == 1) {
                oldAccounts.addAll(newBornAccounts);
                VP oldVP = oldVPs.iterator().next();
                boolean changed = vpDb.refreshVP(oldVP, oldAccounts.iterator());
                if (!changed) {
                    throw new IllegalStateException("更新虚拟人帐号失败:" + oldVP.uniqueId());
                }
            } else if (newBornAccounts.size() > 0
                    || oldVPs.size() > 1) {
                oldAccounts.addAll(newBornAccounts);
                String newVPId = vpDb.createVP(oldAccounts.iterator());
                if (newVPId != null) {
                    /*生成新的虚拟人成功*/
                    if (oldVPs.size() > 0) {
                        for (VP vp : oldVPs) {
                            boolean disabled = vpDb.disable(vp);
                            if (!disabled) {
                                throw new IllegalStateException("数据库返回：注销虚拟人失败, uniqueId=" + vp.uniqueId());
                            }
                        }
                    }
                } else {
                    throw new IllegalStateException("数据库返回：创建新的虚拟人失败");
                }
            }
        }

    }

    public static void update(VPDatabase vpDb, Iterator<VP> deltaVPs) throws IllegalStateException {
        update(vpDb, deltaVPs, DEFAULT_NEW_ACCOUNT_BUFFER_SIZE, DEAFULT_OLD_ACCOUNT_BUFFER_SIZE, DEFAULT_OLD_VP_BUFFER_SIZE);
    }

    /**
     * 增量更新.
     * <p>
     * 注意,可能会出现只有部分VP更新成功的情况--不完整更新.</p>
     *
     * @param deltaVPs 增量VP数据
     * @throws IllegalStateException 更新失败
     */
    public static void update(VPDatabase vpDb, Iterator<VP> deltaVPs, int estimateNewAccountCounts, int estimateOldAccountCounts, int estimateOldVPCounts) throws IllegalStateException {
        /**
         * 注意, 基础的Account并不能确保hashCode和equals函数返回的正确性，
         * 故不能用于判断，进而，不可使用HashSet等结构来进行默认去重. TODO: fix this.
         */
        while (deltaVPs.hasNext()) {
            /*注意deltaVP的UniqueID并未经过数据库确认其唯一性，故不可用于判断*/
            VP deltaVP = deltaVPs.next();
            Iterator< Account> deltaAccounts = deltaVP.accounts();

            /*存储更新记录中，以前在数据库中没有存过的帐号*/
            Collection<Account> newBornAccounts = new ArrayList<Account>(estimateNewAccountCounts);
            /*存储更新记录中已经在数据库中存过的帐号，和这些帐号对应的虚拟人下的其他帐号*/
            Collection<Account> oldAccounts = new ArrayList<Account>(estimateOldAccountCounts);
            /*存储更新记录中已经在数据库中存过的那些帐号对应的虚拟人*/
            Collection<VP> oldVPs = new HashSet<VP>(estimateOldVPCounts);

            while (deltaAccounts.hasNext()) {
                Account deltaAccount = deltaAccounts.next();
                VP oldVP = vpDb.getVP(deltaAccount.serviceId(), deltaAccount.userId());
                if (oldVP == null) {
                    //该帐号之前没被存储过.
                    newBornAccounts.add(deltaAccount);
                } else {
                    /*判断该OldVP是否已经存到oldVPs中过*/
                    boolean buffered = false;
                    for (VP vp : oldVPs) {
                        if (vp.uniqueId().equals(oldVP.uniqueId())) {
                            buffered = true;
                            break;
                        }
                    }
                    /*没有的话，将该oldVP旗下所有尚未加入odAccounts的帐号加入进去                        */
                    if (!buffered) {
                        oldVPs.add(oldVP);
                        Iterator<Account> iter = oldVP.accounts();
                        while (iter.hasNext()) {
                            Account oldAccount = iter.next();
                            boolean found = false;
                            for (Account acc : oldAccounts) {
                                if (acc.uniqueId().equals(oldAccount.uniqueId())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                oldAccounts.add(oldAccount);
                            }
                        }
                        /*deltaVP应该已经在oldVP旗下,故不需要再加入*/
                    }
                }
            }
            /*当新增账户中包含新账户或涉及不止一个原始虚拟人时，才需要更新现有记录.*/
            if (newBornAccounts.size() > 1
                    || oldVPs.size() > 1) {
                oldAccounts.addAll(newBornAccounts);
                String newVPId = vpDb.createVP(oldAccounts.iterator());
                if (newVPId != null) {
                    /*生成新的虚拟人成功*/
                    if (oldVPs.size() > 0) {
                        for (VP vp : oldVPs) {
                            boolean disabled = vpDb.disable(vp);
                            if (!disabled) {
                                throw new IllegalStateException("数据库返回：注销虚拟人失败, uniqueId=" + vp.uniqueId());
                            }
                        }
                    }
                } else {
                    throw new IllegalStateException("数据库返回：创建新的虚拟人失败");
                }
            }
        }

    }
}
