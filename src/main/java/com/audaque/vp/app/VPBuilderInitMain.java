/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.app;

import com.audaque.vp.attribute.run.AttributeRun;
import com.audaque.vp.utils.HdfsUtil;
import com.audaque.vp.model.ActivityAnalysisInitModel;
import com.audaque.vp.model.VPAnalysisModel;
import com.audaque.vp.mr.Container;
import com.audaque.vp.mr.VPBuilderInitVPDbConfig;
import com.audaque.vp.utils.Filenames;
import com.audaque.vpbase.db.VPEventDatabase;
import com.google.common.base.Preconditions;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class VPBuilderInitMain {

//    public static void main(String[] args) throws Exception {
//        String atrSrcDataHdfsFolder = args[0];
//
//        String fieldsSplit = ",";
//        String accountTypeIdSplit = ":";
//        String recordLength = "15";
//
//        String actVpEventDatabasename = DemoXmlEventDatabase.class.getName();
//        String actVpEventOnCreateParameters = "DemoXmlEventData.xml";
//        String actVpEventOnCloseParameters = " ";
//        String actNShards = "2";
//        String actWinSeconds = "3";
//
//        String dstHdfsFolder = args[1];
//
//        StringBuilder actVPEventString = new StringBuilder();
//        try {
//            BufferedReader reader = FileUtils.getBufferedReader(actVpEventOnCreateParameters);
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                actVPEventString.append(line);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(VPBuilderInitVPDbConfig.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        VPBuilderInitVPDbConfig config = new VPBuilderInitVPDbConfig(atrSrcDataHdfsFolder,
//                fieldsSplit, accountTypeIdSplit, actVpEventDatabasename, actVPEventString.toString(),
//                actVpEventOnCloseParameters, actNShards, actWinSeconds, dstHdfsFolder, recordLength);
//
//        VPBuilderInitMain bd = new VPBuilderInitMain();
//        bd.initVPDatabase(config, new Configuration());
//        System.out.println("VPBuilder suc! check output:" + config.getDstHdfsFolder());
//    }
    
    
    public static void main(String[] args) throws Exception {
        System.out.println("hello,kitty!");
        Configuration conf = new Configuration();

        String[] ruleString = {"cardid", "email", "name,QQ", "name,mbphone", "name,homephone"};
        String[] predineAttrbitues = {"Acc", "Acctype", "name", "sex", "nationality", "marriage", "birthday", "cardid", "mbphone", "homephone",
            "presentaddr", "hukou", "email", "identity", "QQ"};

        String filedsSplit = ",";
//        String filedsSplitB64 = Base64.encode(filedsSplit.getBytes());
//        String src = new String(Base64.decode(filedsSplitB64));

        String typeSplit = ":";
//        String typeSplitB64 = Base64.encode(typeSplit.getBytes());

        VPBuilderInitVPDbConfig config = new VPBuilderInitVPDbConfig(args[0], filedsSplit, typeSplit,
                null, null, null,
                null, null, args[1],
                "15", ruleString, predineAttrbitues, true);
        new VPBuilderInitMain().initVPDatabase(config, conf);
    }

    /**
     * 后台调用的接口，调用方法如上面main所示
     *
     * @param config
     * @param conf
     * @return
     * @throws Exception
     */
    public boolean initVPDatabase(VPBuilderInitVPDbConfig config, Configuration conf) throws Exception {
        Container.getContainer().setConf(conf);

        checkInitConfigErro(config);

        ensureInitBufferFoldersNotEmpty(config);

        createActAnalyInitMidFileAndCopyToHdfsBufFolder(config);

        String atrRstPathsSepByComma = createAtrAnalyInitMiddleFiles(config, conf);

        atrRstPathsSepByComma += "," + config.getActHdfsBufferFolder();

        mergeInitMidFilsToRstBufFolder(atrRstPathsSepByComma, config, conf);

        formatInitMidFilesToBuckLoadFiles(config, conf);

        if (config.isDelBuffers()) {
            deleteBufferFolder(config);
        }

        return true;
    }
//
//    public boolean updateVPDatabase(VPBuilderUpdateVPDbConfig config, Configuration conf) {
//        Container.getContainer().setConf(conf);
//
//        checkUpdateConfigErro(config);
//
//        ensureUpdateBufferFoldersNotEmpty(config);
//
//        createActAnalyUpdateMidFile(config);
//
//        /*这部分尚未实现*/
////        String atrRstPathsSepByComma = createAtrAnalyMiddleUpdateFiles(config, conf);
////        copyAtrAnalyMidUpdateFilesToLocal(atrRstPathsSepByComma, config.getLocalMidFileBufferFolder());
//        /**
//         * 现在，所有的中间结果都在config.getLocalMidFileBufferFolder() 中
//         */
//        MutipleMidFileReader rd = new MutipleMidFileReader(config.getLocalMidFileBufferFolder(), false, config.getFieldsSplit());
//
//        VPDatabase vpDb = retriveVPDatabase(config.getVpDatabasename(), config.getVpDbOnCreateParameters());
//
//        VPDbUpdateMethods.liteUpdate(vpDb, rd.VPs());
//
//        return true;
//    }

    private String getActAnalyInitMiddleFile(VPBuilderInitVPDbConfig config) {
        VPEventDatabase vpEvents = retriveVPEventDatabase(config.getActVpEventDatabasename(),
                config.getActVpEventOnCreateParameters());

        int winMillsSecs = Integer.valueOf(config.getActWinSeconds()) * 1000;
        if (winMillsSecs <= 0) {
            throw new IllegalArgumentException("时间间隔需大于0秒");
        }

        String split = config.getFieldsSplit();
        split = new String(Base64.decode(split));
        String localBufferFolder = config.getActLocalBufferFolder();

        VPAnalysisModel model = new ActivityAnalysisInitModel(vpEvents, winMillsSecs, split, localBufferFolder);
        //TODO:Fix this, should not be assigned value like this.Bad API design.
        String midFile = Filenames.getSessionFile(localBufferFolder + "base");
        model.createInitMiddleFile(midFile);
        return midFile;
    }

    private VPEventDatabase retriveVPEventDatabase(String vpDbEvClassname, String onCreateParas) {
        try {
            Class<?> vpClass = Class.forName(vpDbEvClassname);
            VPEventDatabase db = (VPEventDatabase) vpClass.newInstance();
            db.onCreate(onCreateParas);
            return db;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Unkown VPEventDatabase class name:" + vpDbEvClassname);
        }
    }

//    private VPDatabase retriveVPDatabase(String vpDbClassname, String onCreateParas) {
//        try {
//            Class<?> vpClass = Class.forName(vpDbClassname);
//            VPDatabase db = (VPDatabase) vpClass.newInstance();
//            db.onCreate(onCreateParas);
//            return db;
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
//            throw new IllegalArgumentException("Unkown VPEventDatabase class name:" + vpDbClassname);
//        }
//    }
    private String createAtrAnalyInitMiddleFiles(VPBuilderInitVPDbConfig config, Configuration conf) throws Exception {
//        String[] atrMatchRules = {"7", "12", "2,14", "2,8", "2,9"};
        String groupPath = AttributeRun.attributeMatching(config, conf);
        return groupPath;
    }

    /**
     * 对运算过程中使用到的临时Buffer文件夹进行初始化.
     *
     * @param config
     */
    private void ensureInitBufferFoldersNotEmpty(VPBuilderInitVPDbConfig config) {
        String postfix = UUID.randomUUID().toString();

        if (config.getAtrHdfsMatchBufferFolder() == null) {
            config.setAtrHdfsMatchBufferFolder("VPBuilderInit/TempAtrMatchHdfs_" + postfix + File.separator);
        }

        if (config.getAtrHdfsMergeBufferFolder() == null) {
            config.setAtrHdfsMergeBufferFolder("VPBuilderInit/TempAtrMergeHdfs_" + postfix + File.separator);
        }

        if (config.getAtrHdfsMergeResultBufferFolder() == null) {
            config.setAtrHdfsMergeResultBufferFolder("VPBuilderInit/TempAtrMergeResultHdfs_" + postfix + File.separator);
        }

        if (config.getActLocalBufferFolder() == null) {
            config.setActLocalBufferFolder("VPBuilderInit/TempAct_" + postfix + File.separator);
        }

        if (config.getActHdfsBufferFolder() == null) {
            config.setActHdfsBufferFolder("VPBuilderInit/TempActHdfs_" + postfix + File.separator);
        }
    }

    private void createActAnalyInitMidFileAndCopyToHdfsBufFolder(VPBuilderInitVPDbConfig config) throws IOException {
        String dstFolder = config.getActHdfsBufferFolder();
        HdfsUtil.deleteHdfsDir(dstFolder);
        HdfsUtil.mkHdfsDir(dstFolder);

        String actRst = getActAnalyInitMiddleFile(config);
        HdfsUtil.copyFromLocalFile(false, true, actRst, dstFolder);
    }

    private void mergeInitMidFilsToRstBufFolder(String atrRstPathsSepByComma, VPBuilderInitVPDbConfig config, Configuration conf) throws IOException, InterruptedException, ClassNotFoundException {
        long delCount;
        delCount = AttributeRun.middleResultsMerging(atrRstPathsSepByComma, config, conf);
        //迭代次数
        int iteratorTimes = 0;
        iteratorTimes++;
        System.out.println("------------- The " + iteratorTimes + "th iteration complete!------------");
        long startTime = System.currentTimeMillis(); // 获取开始时间
        while (delCount > 0) {
            delCount = AttributeRun.middleResultsMerging(config.getAtrHdfsMergeResultBufferFolder(), config, conf);
            iteratorTimes++;
            System.out.println("------------- The " + iteratorTimes + "th iteration complete!------------");
        }

        System.out.println("-------------Iteration complete,iterating "
                + iteratorTimes + " times!Results are in the " + config.getDstHdfsFolder() + " dir!------------");
        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("It takes " + (endTime - startTime) / 1000.0
                + " seconds to run the mapreduce job. ");
    }

    private void checkInitConfigErro(VPBuilderInitVPDbConfig config) {
        if (config.getAccountTypeIdSplit().equals(config.getFieldsSplit())) {
            throw new IllegalArgumentException("AccountTypeIdSplit can't be as same as FieldsSplit");
        }
    }

    private void formatInitMidFilesToBuckLoadFiles(VPBuilderInitVPDbConfig config, Configuration conf) throws IOException, InterruptedException, ClassNotFoundException {
        AttributeRun.resultFormat(config, conf);
    }

//    private void checkUpdateConfigErro(VPBuilderUpdateVPDbConfig config) {
//        if (config.getAccountTypeIdSplit().equals(config.getFieldsSplit())) {
//            throw new IllegalArgumentException("AccountTypeIdSplit can't be as same as FieldsSplit");
//        }
//    }
//
//    private void ensureUpdateBufferFoldersNotEmpty(VPBuilderUpdateVPDbConfig config) {
//        String postfix = UUID.randomUUID().toString();
//        if (config.getLocalMidFileBufferFolder() == null) {
//            config.setLocalMidFileBufferFolder("VPBuilderUpdate/TempLocalMids_" + postfix + File.separator);
//        }
//        if (config.getAtrHdfsBufferFolder() == null) {
//            config.setAtrHdfsBufferFolder("VPBuilderUpdate/TempAtrMids_" + postfix + File.separator);
//        }
//    }
//
//    private void createActAnalyUpdateMidFile(VPBuilderUpdateVPDbConfig config) {
//
//        VPEventDatabase newEvents = retriveVPEventDatabase(config.getActVpEventDatabasename(),
//                config.getActVpEventOnCreateParameters());
//
//        int winMillsSecs = Integer.valueOf(config.getActWinSeconds()) * 1000;
//        if (winMillsSecs <= 0) {
//            throw new IllegalArgumentException("时间间隔不可低于0秒");
//        }
//
//        String split = config.getFieldsSplit();
//        String localBufferFolder = config.getActLocalBufferFolder();
//
//        VPAnalysisModel model = new ActivityAnalysisInitModel(newEvents, winMillsSecs, split, localBufferFolder);
//        String rst = config.getClass() + "act.mid";
//        model.createInitMiddleFile(rst);
//    }
//
//    private String createAtrAnalyMiddleUpdateFiles(VPBuilderUpdateVPDbConfig config, Configuration conf) {
//        throw new UnsupportedOperationException("Not supported yet."); 
//    }
//
//    private void copyAtrAnalyMidUpdateFilesToLocal(String atrRstPathsSepByComma, String localMidFileBufferFolder) {
//        throw new UnsupportedOperationException("Not supported yet."); 
//    }
    private void deleteBufferFolder(VPBuilderInitVPDbConfig config) {
        String[] folders = new String[]{
            config.getAtrHdfsMatchBufferFolder(),
            config.getAtrHdfsMergeBufferFolder(),
            config.getAtrHdfsMergeResultBufferFolder(),
            config.getActLocalBufferFolder(),
            config.getActHdfsBufferFolder()
        };
        for (String folder : folders) {
            try {
                HdfsUtil.deleteHdfsDir(folder);
            } catch (IOException ex) {
                Preconditions.checkState(false, "删除临时文件异常:\n" + "Folder:" + folder + "\nException:" + ex.getMessage());
            }
        }
    }
}
