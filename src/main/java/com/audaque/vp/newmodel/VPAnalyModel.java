/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.newmodel;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public interface VPAnalyModel {

    /**
     * 在进行建模前，该函数会被调用.
     * <p>
     * 通常用于确认与数据库的连接等</p>
     */
    void beforeModlling();

    /**
     * 在完成建模后该函数会被调用.
     * <p>
     * 通常用于清理本地缓存等资源</p>
     */
    void afterModlling();
}
