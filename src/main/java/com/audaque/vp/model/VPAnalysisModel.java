/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.model;

/**
 *
 * 虚拟人分析模型.输出结果为帐号与帐号之间同属一个虚拟人的可能.
 * <p>
 * 在现在的版本中，将直接输出哪些帐号属于同一个虚拟人.</p>
 *
 * @author Liyu.Cai@Audaque.com
 * @param <IncrementalDataType> 增量数据的类型
 */
public interface VPAnalysisModel<IncrementalDataType> {

    /**
     * 从该模型获取完所需结果后，调用该函数.
     * <p>
     * 通常该函数用来删除所使用到的本地中间结果文件.</p>
     *
     * @return 执行是否成功
     */
    boolean done();

    /**
     * 中间结果文件, 其每一行包含多个帐号，表示这些帐号属于同一个虚拟人.
     * <p>
     * 各帐号之间 用DataFormats.SPLIT分开，帐号的组成为:</p>
     * <p>
     * $帐号类型$DataFormats.ACCOUNT_TYPE_ID_SPLIT$帐号ID$</p>
     *
     * @return 中间文件的路径
     */
    boolean createInitMiddleFile(String middleFile);

    /**
     * 获取增量数据的更新结果.
     *
     * @param incrementalData 增量数据，常见的如Iterator<Account> updatedAccounts, 和
     * Iterator<Event> newEvents.
     * @return 增量数据结果的中间文件.
     */
    String getUpdateMiddleFile(IncrementalDataType incrementalData);
}
