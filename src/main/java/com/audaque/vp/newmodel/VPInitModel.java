/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.newmodel;

/**
 * 对虚拟人数据库VPBase进行初始化(Initialization)的模型.
 *
 * @author Liyu.Cai@Audaque.com
 */
public interface VPInitModel extends VPAnalyModel {

    /**
     * 中间结果文件, 其每一行包含多个帐号，表示这些帐号属于同一个虚拟人.
     * <p>
     * 各帐号之间 用DataFormats.SPLIT分开，帐号的组成为:</p>
     * <p>
     * $帐号类型$DataFormats.ACCOUNT_TYPE_ID_SPLIT$帐号ID$</p>
     *
     * @return 中间文件的路径
     */
    String getInitMiddleFile();
}
