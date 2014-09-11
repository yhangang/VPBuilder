/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.session;

/**
 * Session 指在某一时间段都有过在同一终端上活动的多个帐号的合集
 *
 * @author Liyu.Cai@Audaque.com
 * @param <R> Session返回的形式
 */
public interface SessionCreator<R> {

    /**
     * 创建 Session,若创建失败则R设置为null.
     *
     */
    public boolean create(R r);
}
