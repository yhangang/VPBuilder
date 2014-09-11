/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.mr;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * 用于运行虚拟人建立/初始化/全量计算的配置文件.
 *
 * <p>
 * 属性匹配源数据文件目前采用固定格式的输入:每一行代表一个帐号的信息，信息的各属性之间使用<code>fieldsSplit</code>分隔.每一行依次包括如下属性:</p>
 * <p>
 * 字段名	字段标识	备注</p>
 * <p>
 * 1	账户id	Acc	</p>
 * <p>
 * 2	账户类型	Acctype	</p>
 * <p>
 * 3	姓名	name	</p>
 * <p>
 * 4	性别	sex	男/女</p>
 * <p>
 * 5	民族	nationality	</p>
 * <p>
 * 6	婚姻状况	marriage	</p>
 * <p>
 * 7	出生时间	birthday	YYYYMMDD</p>
 * <p>
 * 8	身份证号码/证件号码	cardid	</p>
 * <p>
 * 9	手机号码	mbphone	</p>
 * <p>
 * 10	座机号码	homephone	</p>
 * <p>
 * 11	现居地址	presentaddr	</p>
 * <p>
 * 12	故乡/户口所在地	hukou	</p>
 * <p>
 * 13	电子邮箱	email	</p>
 * <p>
 * 14	身份职业	identity	</p>
 * <p>
 * 15	QQ号	QQ	</p>
 *
 * @author Liyu.Cai@Audaque.com
 */
public class VPBuilderInitVPDbConfig {

    public static final String CONFIG_KEY = VPBuilderInitVPDbConfig.class.getName();
    /*属性匹配模块必需信息*/
    /**
     * 属性匹配源数据文件HDFS上的路径.
     */
    private String atrSrcDataHdfsFolder;
    /**
     * 属性之间的分隔符.
     */
    private String fieldsSplit;
    /**
     * 帐号UniqueId里，serviceId和UserId的分隔符-需与属性分隔符不同.
     *
     */
    private String accountTypeIdSplit;

    private String recordLength;

    private String atrHdfsMatchBufferFolder;
    private String atrHdfsMergeBufferFolder;
    private String atrHdfsMergeResultBufferFolder;

    /*行为分析模块必需信息*/
    /**
     * VPEvent数据库名及OnCreate和OnClose时调用的参数.
     */
    private String actVpEventDatabasename;
    private String actVpEventOnCreateParameters;
    private String actVpEventOnCloseParameters;

    private String actNShards;
    private String actWinSeconds;

    private String actLocalBufferFolder;
    private String actHdfsBufferFolder;

    private String dstHdfsFolder;
    /**
     * 匹配规则字符串
     */
    private String[] rules;
    /**
     * 预先定义好的属性组
     */
    private String[] predineAttrbitues;

    private boolean delBuffers;

    public VPBuilderInitVPDbConfig(String atrSrcDataHdfsFolder, String fieldsSplit,
            String accountTypeIdSplit, String actVpEventDatabasename, String actVpEventOnCreateParameters,
            String actVpEventOnCloseParameters, String actNShards, String actWinSeconds,
            String dstHdfsFolder, String recordLength, String[] rules, String[] predineAttrbitues, boolean deleteBuffers) {
        this.atrSrcDataHdfsFolder = atrSrcDataHdfsFolder;
        this.fieldsSplit = Base64.encode(fieldsSplit.getBytes());
        this.accountTypeIdSplit = Base64.encode(accountTypeIdSplit.getBytes());
        this.actVpEventDatabasename = actVpEventDatabasename;
        this.actVpEventOnCreateParameters = actVpEventOnCreateParameters;
        this.actVpEventOnCloseParameters = actVpEventOnCloseParameters;
        this.actNShards = actNShards;
        this.actWinSeconds = actWinSeconds;
        this.dstHdfsFolder = dstHdfsFolder;
        this.recordLength = recordLength;
        this.rules = rules;
        this.predineAttrbitues = predineAttrbitues;
        this.delBuffers = deleteBuffers;
    }

    public boolean isDelBuffers() {
        return delBuffers;
    }

    public void setDelBuffers(boolean delBuffers) {
        this.delBuffers = delBuffers;
    }

    public String getAtrHdfsMatchBufferFolder() {
        return atrHdfsMatchBufferFolder;
    }

    public void setAtrHdfsMatchBufferFolder(String atrHdfsMatchBufferFolder) {
        this.atrHdfsMatchBufferFolder = atrHdfsMatchBufferFolder;
    }

    public String getAtrSrcDataHdfsFolder() {
        return atrSrcDataHdfsFolder;
    }

    public void setAtrSrcDataHdfsFolder(String atrSrcDataHdfsFolder) {
        this.atrSrcDataHdfsFolder = atrSrcDataHdfsFolder;
    }

    public String getFieldsSplit() {
        return fieldsSplit;
    }

    public void setFieldsSplit(String fieldsSplit) {
        this.fieldsSplit = Base64.encode(fieldsSplit.getBytes());
    }

    public String getAccountTypeIdSplit() {
        return accountTypeIdSplit;
    }

    public void setAccountTypeIdSplit(String accountTypeIdSplit) {
        this.accountTypeIdSplit = Base64.encode(accountTypeIdSplit.getBytes());
    }

    public String getAtrHdfsMergeBufferFolder() {
        return atrHdfsMergeBufferFolder;
    }

    public void setAtrHdfsMergeBufferFolder(String atrHdfsMergeBufferFolder) {
        this.atrHdfsMergeBufferFolder = atrHdfsMergeBufferFolder;
    }

    public String getAtrHdfsMergeResultBufferFolder() {
        return atrHdfsMergeResultBufferFolder;
    }

    public void setAtrHdfsMergeResultBufferFolder(String atrHdfsMergeResultBufferFolder) {
        this.atrHdfsMergeResultBufferFolder = atrHdfsMergeResultBufferFolder;
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

    public String getActNShards() {
        return actNShards;
    }

    public void setActNShards(String actNShards) {
        this.actNShards = actNShards;
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

    public String getActHdfsBufferFolder() {
        return actHdfsBufferFolder;
    }

    public void setActHdfsBufferFolder(String actHdfsBufferFolder) {
        this.actHdfsBufferFolder = actHdfsBufferFolder;
    }

    public String getDstHdfsFolder() {
        return dstHdfsFolder;
    }

    public void setDstHdfsFolder(String dstHdfsFolder) {
        this.dstHdfsFolder = dstHdfsFolder;
    }

    public String getRecordLength() {
        return recordLength;
    }

    public void setRecordLength(String recordLength) {
        this.recordLength = recordLength;
    }

    public String[] getRules() {
        return rules;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
    }

    public String[] getPredineAttrbitues() {
        return predineAttrbitues;
    }

    public void setPredineAttrbitues(String[] predineAttrbitues) {
        this.predineAttrbitues = predineAttrbitues;
    }

    @Override
    public String toString() {
        return "VPBuilderInitVPDbConfig{" + "atrSrcDataHdfsFolder=" + atrSrcDataHdfsFolder + ", fieldsSplit=" + fieldsSplit + ", accountTypeIdSplit=" + accountTypeIdSplit + ", atrHdfsMatchBufferFolder=" + atrHdfsMatchBufferFolder + ", atrHdfsMergeBufferFolder=" + atrHdfsMergeBufferFolder + ", atrHdfsMergeResultBufferFolder=" + atrHdfsMergeResultBufferFolder + ", actVpEventDatabasename=" + actVpEventDatabasename + ", actVpEventOnCreateParameters=" + actVpEventOnCreateParameters + ", actVpEventOnCloseParameters=" + actVpEventOnCloseParameters + ", actNShards=" + actNShards + ", actWinSeconds=" + actWinSeconds + ", actLocalBufferFolder=" + actLocalBufferFolder + ", actHdfsBufferFolder=" + actHdfsBufferFolder + ", dstHdfsFolder=" + dstHdfsFolder + '}';
    }

}
