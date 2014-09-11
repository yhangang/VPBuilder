/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.newmodel;

/**
 * 对虚拟人数据库进行更新的模型.
 *
 * @author Liyu.Cai@Audaque.com
 */
public interface VPUpdateModel extends VPAnalyModel {

    /**
     * 获取增量数据的更新结果.
     *
     * @return 增量数据结果的中间文件.
     */
    String getUpdateMiddleFile();
}
