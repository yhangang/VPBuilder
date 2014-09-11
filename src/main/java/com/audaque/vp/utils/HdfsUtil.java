package com.audaque.vp.utils;

import com.audaque.vp.mr.Container;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;

/**
 * 分光数据匹配--工具类
 *
 * 主要职能: 1)对HDFS进行操作,比如创建/删除目录or文件
 *
 */
public class HdfsUtil {
//	//test
//	public static void main(String[] args) throws Exception {
//		
////		//文件大小--9,505,585,907
////		List<String> srcPaths = HdfsUtil.getAllFilePaths("hdfs://tydic152:9010/user/ceshi/wll_inputdata/20121210/13");
////		String dstPath = "hdfs://tydic152:9010/user/ceshi/wll_inputdata/20121210/copy_13";
////		long startTime = System.currentTimeMillis();
////		HdfsUtil.copy(srcPaths, dstPath,false,true);
////		long endTime = System.currentTimeMillis();
////		System.out.println( "execute time [" + (new Double(endTime - startTime) / 1000) + " s].");
//		
//		List<String> srcPaths = HdfsUtil.getAllFilePaths("hdfs://tydic152:9010/user/ceshi/wll_inputdata/20121210/copy_13");
//		String dstPath = "hdfs://tydic152:9010/user/ceshi/wll_inputdata/20121210/rename_13";
//		long startTime = System.currentTimeMillis();
//		HdfsUtil.rename(srcPaths, dstPath);
//		long endTime = System.currentTimeMillis();
//		System.out.println( "execute time [" + (new Double(endTime - startTime) / 1000) + " s].");
//	}//end main()

    private static final Log LOG = LogFactory.getLog(HdfsUtil.class);

    /**
     * HDFS内文件转移
     *
     * 多个文件==>目录
     *
     * @param srcFiles 多个文件list
     * @param dstDir 目录
     */
    public static boolean rename(List<String> srcFiles, String dstDir) throws IOException {
        LOG.info("HdfsUtil.rename() start.");
        long startTime = System.currentTimeMillis();

        if (srcFiles.size() == 0) {
            return true;
        }
        if (dstDir.endsWith("/")) {
            dstDir = dstDir.substring(0, dstDir.length() - 1);
        }

        Configuration conf = null;
        FileSystem hdfs = null;
        Boolean resultStatus = false;
        try {
            conf = Container.getContainer().newConfiguration();
            hdfs = FileSystem.newInstance(conf);
            //源
            Path[] srcFilePaths = new Path[srcFiles.size()];
            for (int i = 0; i < srcFiles.size(); i++) {
                srcFilePaths[i] = new Path(srcFiles.get(i));
            }

            //目标
            Path dstDirPath = new Path(dstDir);
            if (!hdfs.exists(dstDirPath)) {//不存在则,创建
                hdfs.mkdirs(dstDirPath);
            }

            for (int i = 0; i < srcFilePaths.length; i++) {
                Path srcFilePath = srcFilePaths[i];
                Path dstFilePath = new Path(dstDir + "/" + srcFilePath.getName());

                resultStatus = hdfs.rename(srcFilePath, dstFilePath);
                if (!resultStatus) {
                    return false;
                }
            }
        } finally {
            if (hdfs != null) {
                hdfs.close();
            }
        }

        long endTime = System.currentTimeMillis();
        LOG.info("HdfsUtil.rename() end.");
        LOG.info("HdfsUtil.rename() execution time [" + (new Double(endTime - startTime) / 1000) + " s].");
        return resultStatus;
    }

    /**
     * HDFS内文件转移
     *
     * 说明: 1)单文件==>单文件 2)目录 ==> 目录
     *
     * @param src
     * @param dst 目录
     */
    public static boolean rename(String src, String dst) throws IOException {
        Configuration conf = null;
        FileSystem hdfs = null;
        Boolean resultStatus = false;
        try {
            conf = Container.getContainer().newConfiguration();
            hdfs = FileSystem.newInstance(conf);
            Path srcPath = new Path(src);
            Path dstPath = new Path(dst);

            if (hdfs.exists(dstPath)) {//存在,则删除(目标已经存在,则rename失败)
                hdfs.delete(dstPath, true);
            }
            resultStatus = hdfs.rename(srcPath, dstPath);
            if (!resultStatus) {
                return false;
            }
        } finally {
            if (hdfs != null) {
                hdfs.close();
            }
        }

        return resultStatus;
    }

//	/**HDFS内文件转移
//	 * Copy files between FileSystems.
//	 * 
//	 * copy(FileSystem srcFS, Path[] srcs, FileSystem dstFS, Path dst, boolean deleteSource, boolean overwrite, Configuration conf)
//	 * 
//	 *  说明:
//	 *  多文件==>目录
//	 *  
//	 *  
//	 *  @param dstPath 1)目标path必须是目录;
//	 *  
//	 */
//	private static Boolean copy(List<String> srcPaths,String dstPath,boolean deleteSource,boolean overwrite) throws IOException{
//		if(srcPaths.size() == 0){
//			return true;
//		}
//		
//		Configuration conf = Container.getContainer().newConfiguration();
//		FileSystem hdfs = FileSystem.get(conf);
//		Boolean resultStatus = false;
//		try{
//			//源
//			Path[] srcs = new Path[srcPaths.size()];
//			for(int i = 0;i<srcPaths.size();i++){
//				srcs[i] = new Path(srcPaths.get(i));
//			}
//			//目标
//			Path dst = new Path(dstPath);
//			
//			if(!hdfs.exists(dst)){//不存在则,创建
//				hdfs.mkdirs(dst);
//			}
//			
//			//1)目标path必须是目录;
//			//2)目标path必须存在;
//			resultStatus = FileUtil.copy(hdfs, srcs, hdfs, dst, deleteSource, overwrite, conf);
//		}finally{
//			hdfs.close();
//		}
//		return resultStatus;
//	}
//	
//	/**HDFS内文件转移
//	 * Copy files between FileSystems.
//	 * 
//	 * copy(FileSystem srcFS, Path[] srcs, FileSystem dstFS, Path dst, boolean deleteSource, boolean overwrite, Configuration conf)
//	 * 
//	 *  说明:
//	 *  多文件==>目录
//	 *  
//	 *  
//	 *  @param dstPath 1)目标path必须是目录;
//	 *  
//	 */
//	private static Boolean copy(String[] srcPaths,String dstPath,boolean deleteSource,boolean overwrite) throws IOException{
//		Configuration conf = Container.getContainer().newConfiguration();
//		FileSystem hdfs = FileSystem.get(conf);
//		Boolean resultStatus = false;
//		try{
//			//源
//			Path[] srcs = new Path[srcPaths.length];
//			for(int i = 0;i<srcPaths.length;i++){
//				srcs[i] = new Path(srcPaths[i]);
//			}
//			//目标
//			Path dst = new Path(dstPath);
//			
//			if(!hdfs.exists(dst)){//不存在则,创建
//				hdfs.mkdirs(dst);
//			}
//			
//			//1)目标path必须是目录;
//			//2)目标path必须存在;
//			resultStatus = FileUtil.copy(hdfs, srcs, hdfs, dst, deleteSource, overwrite, conf);
//		}finally{
//			hdfs.close();
//		}
//		return resultStatus;
//	}
    /**
     * HDFS内文件转移
     *
     * 说明: 1)单文件==>单文件 2)目录 ==> 目录
     *
     * copy(FileSystem srcFS, Path src, FileSystem dstFS, Path dst, boolean
     * deleteSource, boolean overwrite, Configuration conf)
     *
     */
    public static Boolean copy(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws IOException {
        Configuration conf = Container.getContainer().newConfiguration();
        FileSystem hdfs = FileSystem.newInstance(conf);
        Boolean resultStatus = false;

        try {
            Path src = new Path(srcPath);//源
            Path dst = new Path(dstPath);//目标
            resultStatus = FileUtil.copy(hdfs, src, hdfs, dst, deleteSource, overwrite, conf);
        } finally {
            hdfs.close();
        }

        return resultStatus;
    }

    /**
     * 判断文件路径是否存在
     *
     * @param pathStr 文件路径
     */
    public static Boolean exists(String pathStr) throws IOException {
        Configuration conf = Container.getContainer().newConfiguration();
        FileSystem hdfs = FileSystem.newInstance(conf);

        Boolean resultStatus = false;
        try {
            Path path = new Path(pathStr);
            resultStatus = hdfs.exists(path);
        } finally {
            hdfs.close();
        }
        return resultStatus;
    }

    /**
     * 创建dir+权限
     *
     * 1)目录如果已经存在,则不创建,程序也不异常; 2)目录的上一层目录没有,也会直接创建;
     *
     * @param dir HDFS dir
     * @param permission 访问权限
     */
    public static void mkdirs(String dir, short permission) throws IOException {
        Configuration conf = Container.getContainer().newConfiguration();
        FileSystem fs = FileSystem.newInstance(conf);
        try {
            //基于默认权限创建一个目录
            Path path = new Path(dir);
            fs.mkdirs(path);
            //设置目录访问权限
            FsPermission fp = new FsPermission(permission);
            fs.setPermission(path, fp);
        } finally {
            fs.close();
        }
    }

    /**
     * 创建dir
     *
     * 1)目录如果已经存在,则不创建,程序也不异常; 2)目录的上一层目录没有,也会直接创建;
     *
     * @param dir HDFS dir
     */
    public static void mkdirs(String dir) throws IOException {
        Configuration conf = Container.getContainer().newConfiguration();
        FileSystem fs = FileSystem.newInstance(conf);
        try {
            //路径
            Path path = new Path(dir);
            fs.mkdirs(path);
        } finally {
            fs.close();
        }
    }

    public static void deleteHdfsDir(String dir) throws IOException {
        Configuration conf = Container.getContainer().newConfiguration();
        FileSystem fs = FileSystem.newInstance(conf);
        try {
            fs.delete(new Path(dir), true);
        } finally {
            fs.close();
        }
    }

    public static void mkHdfsDir(String dir) throws IOException {
        Configuration conf = Container.getContainer().newConfiguration();
        FileSystem fs = FileSystem.newInstance(conf);
        try {
            fs.mkdirs(new Path(dir));
        } finally {
            fs.close();
        }
    }

    /**
     * copy HDFS文件到本地文件系统
     *
     * 1)文件==>文件; 2)文件==>目录;(自动使用文件原有名称) 3)目录==>目录; 4)目录==>文件;(不支持,文件名称作为目录名称)
     * 5)文件名称不支持通配符 *
     *
     * @param delSrc 是否删除源文件
     * @param src 源文件路径
     * @param dst 目标路径
     */
    public static void copyToLocalFile(boolean delSrc, String src, String dst) throws IOException {
        Configuration config = Container.getContainer().newConfiguration();
        FileSystem hdfs = FileSystem.newInstance(config);
        try {
            //源文件s
            Path srcPath = new Path(src);
            //目标文件
            Path dstPath = new Path(dst);

            hdfs.copyToLocalFile(delSrc, srcPath, dstPath);
        } finally {
            hdfs.close();
        }
    }

    /**
     * 删除HDFS上的文件or文件夹
     *
     * 说明: 1)目录or文件不存在,不异常;
     *
     * @param pathStr
     * @param recursive 是否递归 --false,如果文件夹不是空则异常; --该参数对于文件没有影响;
     */
    public static void delete(String pathStr, boolean recursive) throws IOException {
        Configuration conf = Container.getContainer().newConfiguration();
        FileSystem fs = FileSystem.newInstance(conf);
        try {
            fs.delete(new Path(pathStr), recursive);
        } finally {
            fs.close();
        }
    }

    /**
     * copy本地文件到文件系统
     *
     * @param delSrc 是否删除源文件
     * @param overwrite 是否覆盖目标文件: false--如果已经存在则异常;
     * @param srcs 源文件paths
     * @param dst 目标path
     */
    public static void copyFromLocalFile(boolean delSrc, boolean overwrite, String[] srcs, String dst) throws IOException {
        Configuration config = Container.getContainer().newConfiguration();
        FileSystem hdfs = FileSystem.newInstance(config);
        try {
            //源文件s
            Path[] srcPaths = new Path[srcs.length];
            for (int i = 0; i < srcs.length; i++) {
                srcPaths[i] = new Path(srcs[i]);
            }
            //目标文件
            Path dstPath = new Path(dst);
            hdfs.copyFromLocalFile(delSrc, overwrite, srcPaths, dstPath);
        } finally {
            hdfs.close();
        }
    }

    public static void copyFromLocalFile(boolean delSrc, boolean overwrite, String src, String dst) throws IOException {
        Configuration config = Container.getContainer().newConfiguration();
        FileSystem hdfs = FileSystem.newInstance(config);
        try {
            //源文件s
            Path srcPath = new Path(src);
            //目标文件
            Path dstPath = new Path(dst);
            hdfs.copyFromLocalFile(delSrc, overwrite, srcPath, dstPath);
        } finally {
            hdfs.close();
        }
    }

    //获取文件
    /**
     * 获取目录下所有的目录
     *
     * @param path 目录,如果是文件路径则返回空列表
     */
    public static List<String> getAllDirPaths(String path) throws IOException {
        List<String> resultList = new ArrayList<String>();
        if (null == path) {
            return resultList;
        }

        //如果目录不存在--做判断,避免目录不存在抛出异常
        if (!HdfsUtil.exists(path)) {
            return resultList;
        }

        Configuration config = Container.getContainer().newConfiguration();
        FileSystem hdfs = FileSystem.get(config);
        //源文件
        Path srcPath = new Path(path);
        if (hdfs.isFile(srcPath)) {//是文件--直接返回
            return resultList;
        } else if (hdfs.getFileStatus(srcPath).isDir()) {//是目录--获取所有文件and目录
            FileStatus[] status = hdfs.listStatus(srcPath);
            for (FileStatus fileStatus : status) {
                if (fileStatus.isDir()) {
                    resultList.add(fileStatus.getPath().toString());
                }
            }
        }

        return resultList;
    }

    /**
     * 获取一个路径下所有文件的路径--任意深度遍历
     *
     */
    public static List<String> getAllFilePaths(String path) throws IOException {
        List<String> resultList = new ArrayList<String>();
        if (null == path) {
            return resultList;
        }

        //判断路径是否存在,不存在直接返回
        if (!exists(path)) {
            return resultList;
        }

        Configuration config = Container.getContainer().newConfiguration();
        FileSystem hdfs = FileSystem.newInstance(config);
        try {
            setFilePaths(hdfs, resultList, path);
        } finally {
            hdfs.close();
        }
        return resultList;
    }

    private static void setFilePaths(FileSystem hdfs, List<String> filePaths, String path) throws IOException {
        //源文件
        Path srcPath = new Path(path);
        if (hdfs.isFile(srcPath)) {//是文件--直接添加
            //System.out.println("----------" + path);
            filePaths.add(path);
        } else if (hdfs.getFileStatus(srcPath).isDir()) {//是目录--获取所有文件and目录
            FileStatus[] status = hdfs.listStatus(srcPath);
            for (int i = 0; i < status.length; ++i) {
                setFilePaths(hdfs, filePaths, status[i].getPath().toString());
            }
        }
    }

    /**
     * 将一个文件夹里所有的文件复制到输出文件夹
     *
     * @param srcPath
     * @param dstPath
     * @param deleteSource 是否删除源
     * @return
     * @throws IOException
     */
    public static Boolean copyMerge(String srcPath, String dstPath, boolean deleteSource) throws IOException {
        Configuration conf = Container.getContainer().newConfiguration();
        FileSystem hdfs = FileSystem.newInstance(conf);
        Boolean resultStatus = false;

        try {
            Path src = new Path(srcPath);//源
            Path dst = new Path(dstPath);//目标
            resultStatus = FileUtil.copyMerge(hdfs, src, hdfs, dst, false, conf, null);
        } finally {
            hdfs.close();
        }
        return resultStatus;
    }

}//end class
