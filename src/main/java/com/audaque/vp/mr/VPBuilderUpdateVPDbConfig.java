/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.mr;

/**
 * 用于虚拟人增量数据更新的配置文件.
 *
 * @author Audaque
 */
public class VPBuilderUpdateVPDbConfig {

    private String accountTypeIdSplit;
    private String fieldsSplit;

    /**
     * 属性匹配模块用
     */
    /*属性匹配所需增量数据文件所在HDFS目录*/
    private String atrUpateDataHdfsFolder;
    /*属性匹配所产生的中间文件在HDFS上的存放目录*/
    private String atrHdfsBufferFolder;

    /**
     * 行为分析模块用
     */
    private String actVpEventDatabasename;
    private String actVpEventOnCreateParameters;
    private String actVpEventOnCloseParameters;

    private String actWinSeconds;

    private String actLocalBufferFolder;

    /**
     * 两模块共用
     */
    private String localMidFileBufferFolder;
    private String vpDatabasename;
    private String vpDbOnCreateParameters;
    private String vpDbOnCloseParameters;

    public String getAccountTypeIdSplit() {
        return accountTypeIdSplit;
    }

    public void setAccountTypeIdSplit(String accountTypeIdSplit) {
        this.accountTypeIdSplit = accountTypeIdSplit;
    }

    public String getFieldsSplit() {
        return fieldsSplit;
    }

    public void setFieldsSplit(String fieldsSplit) {
        this.fieldsSplit = fieldsSplit;
    }

    public String getAtrUpateDataHdfsFolder() {
        return atrUpateDataHdfsFolder;
    }

    public void setAtrUpateDataHdfsFolder(String atrUpateDataHdfsFolder) {
        this.atrUpateDataHdfsFolder = atrUpateDataHdfsFolder;
    }

    public String getAtrHdfsBufferFolder() {
        return atrHdfsBufferFolder;
    }

    public void setAtrHdfsBufferFolder(String atrHdfsBufferFolder) {
        this.atrHdfsBufferFolder = atrHdfsBufferFolder;
    }

    public String getActVpEventDatabasename() {
        return actVpEventDatabasename;
    }

    public void setActVpEventDatabasename(String actVpEventDatabasename) {
        this.actVpEventDatabasename = actVpEventDatabasename;
    }

    public String getActVpEventOnCreateParameters() {
        return actVpEventOnCreateParameters;
    }

    public void setActVpEventOnCreateParameters(String actVpEventOnCreateParameters) {
        this.actVpEventOnCreateParameters = actVpEventOnCreateParameters;
    }

    public String getActVpEventOnCloseParameters() {
        return actVpEventOnCloseParameters;
    }

    public void setActVpEventOnCloseParameters(String actVpEventOnCloseParameters) {
        this.actVpEventOnCloseParameters = actVpEventOnCloseParameters;
    }

    public String getActWinSeconds() {
        return actWinSeconds;
    }

    public void setActWinSeconds(String actWinSeconds) {
        this.actWinSeconds = actWinSeconds;
    }

    public String getActLocalBufferFolder() {
        return actLocalBufferFolder;
    }

    public void setActLocalBufferFolder(String actLocalBufferFolder) {
        this.actLocalBufferFolder = actLocalBufferFolder;
    }

    public String getLocalMidFileBufferFolder() {
        return localMidFileBufferFolder;
    }

    public void setLocalMidFileBufferFolder(String localMidFileBufferFolder) {
        this.localMidFileBufferFolder = localMidFileBufferFolder;
    }

    public String getVpDatabasename() {
        return vpDatabasename;
    }

    public void setVpDatabasename(String vpDatabasename) {
        this.vpDatabasename = vpDatabasename;
    }

    public String getVpDbOnCreateParameters() {
        return vpDbOnCreateParameters;
    }

    public void setVpDbOnCreateParameters(String vpDbOnCreateParameters) {
        this.vpDbOnCreateParameters = vpDbOnCreateParameters;
    }

    public String getVpDbOnCloseParameters() {
        return vpDbOnCloseParameters;
    }

    public void setVpDbOnCloseParameters(String vpDbOnCloseParameters) {
        this.vpDbOnCloseParameters = vpDbOnCloseParameters;
    }

}
