/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.attribute.run;

import com.audaque.vp.attribute.format.FormatMapper;
import com.audaque.vp.attribute.group.AttributeMatchMapper;
import com.audaque.vp.attribute.group.AttributeMatchReducer;
import com.audaque.vp.attribute.merge.MergeMapperTwo;
import com.audaque.vp.attribute.merge.MergeReducerTwo;
import com.audaque.vp.attribute.merge.MiddleFileMergeMapper;
import com.audaque.vp.attribute.merge.MiddleFileMergeReducer;
import com.audaque.vp.utils.HdfsUtil;
import com.audaque.vp.mr.Container;
import com.audaque.vp.mr.VPBuilderInitVPDbConfig;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 *
 * @author Hang.Liu@Audaque.com
 */
public class AttributeRun {

//    public static long delCount;
//    public static String resultPath = "result";
//    public static String tempPath = "temp";
//    public static void main(String[] args) throws Exception {
//        //匹配规则
//        String[] rules = {"7", "12", "2,14", "2,8", "2,9"};
//
//        String groupPath = "";
//        //reduce输出的记录数
//        long reduceCount;
//        //迭代次数
//        int iteratorTimes = 0;
//        System.out.println("Hello Kitty!");
//        Thread.sleep(1000);
//        System.out.println("Nihao Kitty!");
//        Thread.sleep(1000);
//        System.out.println("ByeBye Kitty!");
//        Thread.sleep(1000);
//        long startTime = System.currentTimeMillis(); // 获取开始时间
//
//        groupPath = attributeMatching(args[0], rules);
//        
//        reduceCount = middleResultsMerging(groupPath);
//        iteratorTimes++;
//        System.out.println("------------- The " + iteratorTimes + "th iteration complete,reduce output " + reduceCount + " records!------------");
//        while (delCount > 0) {
//            reduceCount = middleResultsMerging(resultPath);
//            iteratorTimes++;
//            System.out.println("------------- The " + iteratorTimes + "th iteration complete,reduce output " + reduceCount + " records!------------");
//        }
//
//        System.out.println("-------------Iteration complete,iterating "
//                + iteratorTimes + " times, output " + reduceCount + " records!Results are in the dir " + resultPath + "!------------");
//        long endTime = System.currentTimeMillis(); // 获取结束时间
//        System.out.println("It takes " + (endTime - startTime) / 1000.0
//                + " seconds to run the mapreduce job. ");
//    }
    /**
     * 第一步，按不同规则得出分组记录
     * @param config
     * @param conf
     * @return 属性匹配的结果所在的HDFDS文件夹路径
     * @throws java.io.IOException HDFS读写错误
     * @throws java.lang.InterruptedException
     * @throws java.lang.ClassNotFoundException
     */
    public static String attributeMatching(VPBuilderInitVPDbConfig config, Configuration conf) throws IOException, InterruptedException, ClassNotFoundException {
        String[] ruleString = config.getRules();
        String[] predineAttrbitues = config.getPredineAttrbitues();

        String dataPath = config.getAtrSrcDataHdfsFolder();
        String bufferFolder = config.getAtrHdfsMatchBufferFolder();
        String accountTypeIdSplit = config.getAccountTypeIdSplit();
        String recordLength = config.getRecordLength();
        String filedsSplit = config.getFieldsSplit();
        //规则内部分隔符用","，写死
        String[] rules = convertStringRuleToInteger(ruleString, predineAttrbitues, ",");

        String groupResultPath = "";
        HdfsUtil.delete(bufferFolder, true);

        Container.getContainer().setConf(conf);

        for (int i = 1; i <= rules.length; i++) {
            Configuration con = Container.getContainer().newConfiguration();
            if (null == rules[i - 1] || "".equals(rules[i - 1])) {
                throw new RuntimeException("rule" + i + " is null,len = " + rules.length + ", ~~~~~~~~~~~~~~");

            }
            con.set(AttributeMatchMapper.RULES_CONFIG, rules[i - 1]);
            con.set(FormatMapper.TYPE_ID_CONFIG, accountTypeIdSplit);
            con.set(FormatMapper.FIELDS_SPLIT_CONFIG, filedsSplit);
            con.set(AttributeMatchMapper.RECORD_LENGTH_CONFIG, recordLength);
            String groupPath = bufferFolder + i;
            groupResultPath += groupPath + ",";
            Job job = new Job(con, "group");
            job.setJarByClass(AttributeRun.class);
            job.setMapperClass(AttributeMatchMapper.class);
            job.setReducerClass(AttributeMatchReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job, new Path(dataPath));
            FileOutputFormat.setOutputPath(job, new Path(groupPath));
            System.out.println("-------------rule" + i + " mapreduce begin!------------");
            if (!job.waitForCompletion(true)) {
                throw new IllegalStateException("Task Run Failure!");
            }
        }
        return groupResultPath.substring(0, groupResultPath.length() - 1);
    }

    /**
     *
     * @param dataPath 中间文件的输入路径
     * @param config
     * @return
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.lang.ClassNotFoundException
     */
    public static long middleResultsMerging(String dataPath, VPBuilderInitVPDbConfig config, Configuration conf) throws IOException, InterruptedException, ClassNotFoundException {
        String mergeBufferFolder = config.getAtrHdfsMergeBufferFolder();
        String mergeResultBufferFolder = config.getAtrHdfsMergeResultBufferFolder();
        String filedsSplit = config.getFieldsSplit();

        long delCount;
        Container.getContainer().setConf(conf);
        Configuration con = Container.getContainer().newConfiguration();
        con.set(FormatMapper.FIELDS_SPLIT_CONFIG, filedsSplit);
        HdfsUtil.delete(mergeBufferFolder, true);
        Job job1 = new Job(con, "merge1");
        job1.setJarByClass(AttributeRun.class);
        job1.setMapperClass(MiddleFileMergeMapper.class);
        job1.setReducerClass(MiddleFileMergeReducer.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);
        FileInputFormat.addInputPaths(job1, dataPath);
        FileOutputFormat.setOutputPath(job1, new Path(mergeBufferFolder));
        if (!job1.waitForCompletion(true)) {
            throw new IllegalStateException("middleResultsMerging fails!");
        }

        HdfsUtil.delete(mergeResultBufferFolder, true);
        Job job2 = new Job(con, "merge2");
        job2.setJarByClass(AttributeRun.class);
        job2.setMapperClass(MergeMapperTwo.class);
        job2.setReducerClass(MergeReducerTwo.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job2, new Path(mergeBufferFolder));
        FileOutputFormat.setOutputPath(job2, new Path(mergeResultBufferFolder));
        if (!job2.waitForCompletion(true)) {

            throw new IllegalStateException("middleResultsMerging failure!");

        }
        delCount = job2.getCounters().findCounter("Flag", "DEL").getValue();
//        totoalRecord = job2.getCounters().findCounter("org.apache.hadoop.mapred.Task$Counter", "REDUCE_OUTPUT_RECORDS").getValue();
        return delCount;
    }

    /**
     * 将虚拟人匹配结果中间格式转化为最终格式（一行多个改为一行一个）
     *
     * @param config
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     */
    public static void resultFormat(VPBuilderInitVPDbConfig config, Configuration conf) throws IOException, InterruptedException, ClassNotFoundException {
        String dataPath = config.getAtrHdfsMergeResultBufferFolder();
        String resultPath = config.getDstHdfsFolder();

        String fieldsSplit = config.getFieldsSplit();
        String typeIdSplit = config.getAccountTypeIdSplit();

        Container.getContainer().setConf(conf);
        Configuration con = Container.getContainer().newConfiguration();

        con.set(FormatMapper.FIELDS_SPLIT_CONFIG, fieldsSplit);
        con.set(FormatMapper.TYPE_ID_CONFIG, typeIdSplit);

        HdfsUtil.delete(resultPath, true);
        Job job = new Job(con, "resultFormat");
        job.setJarByClass(AttributeRun.class);
        job.setMapperClass(FormatMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(0);
        FileInputFormat.addInputPath(job, new Path(dataPath));
        FileOutputFormat.setOutputPath(job, new Path(resultPath));
        if (!job.waitForCompletion(true)) {
            throw new IllegalStateException("Result reformating failure");
        }
    }

    /**
     * 将列名的规则转化为序号的规则，方便全量匹配MR算法使用
     *
     * @param ruleString
     * @param predineAttrbitues
     * @param filedsSplit
     * @return
     */
    private static String[] convertStringRuleToInteger(String[] ruleString, String[] predineAttrbitues, String filedsSplit) {
        String[] rules = new String[ruleString.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruleString.length; i++) {
            String[] colNames = ruleString[i].split(filedsSplit, -1);
            for (int j = 0; j < colNames.length; j++) {
                String index = String.valueOf(getIndexByColumnName(colNames[j], predineAttrbitues));
                sb.append(index).append(filedsSplit);
            }
            sb = new StringBuilder(sb.substring(0, sb.length() - filedsSplit.length()));
            rules[i] = sb.toString();
            sb = new StringBuilder();
        }
        return rules;

    }

    /**
     * 将列名转化为序号
     *
     * @param colName
     * @param predineAttrbitues
     * @return
     */
    private static Integer getIndexByColumnName(String colName, String[] predineAttrbitues) {
        Integer index = -1;
        for (int i = 0; i < predineAttrbitues.length; i++) {
            if (predineAttrbitues[i].equals(colName)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalStateException("规则输入有误，"+colName+"不在预定义属性类中！");
        }
        return index;
    }
//    public static void main(String[] args) {
//        String[] ruleString = {"cardid", "email", "name,QQ", "name,mbphone", "name,homephone"};
//        String[] predineAttrbitues = {"Acc","Acctype","name","sex","nationality","marriage","birthday","cardid","mbphone","homephone",
//        "presentaddr","hukou","email","identity","QQ"};
//        String filedsSplit=",";
//        String[] rules = convertStringRuleToInteger(ruleString, predineAttrbitues,filedsSplit);
//        for(int i=0;i<rules.length;i++){
//            System.out.println(rules[i]);
//        }
//    }
}
