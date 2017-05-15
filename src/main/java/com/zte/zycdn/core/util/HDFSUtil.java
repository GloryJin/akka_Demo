package com.zte.zycdn.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.zte.zycdn.core.config.SysConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Created by 10190203 on 2017/5/8.
 */
public class HDFSUtil {
    private static final Logger log = Logger.getLogger(HDFSUtil.class);

    public static Configuration getHDFSConf(){
        Configuration conf = new Configuration();
        try{
            FileInputStream fis = new FileInputStream(SysConfig.hdfsconfig);
            //this fileinputstream cannot close here or cause java.io.IOException: Stream Closed
            conf.addResource(fis);
        }catch (IOException e){
            log.debug(e,e);
        }
        return conf;
    }

    public static void chkAndMkdir(FileSystem fs,String hdfspath) throws IOException{
        Path path = new Path(hdfspath);
        if(fs.exists(path) && !hdfspath.contains("#")){
            log.info("hdfs remove dir:" + hdfspath);
        }
        if(!fs.exists(path)){
            log.info("hdfs mkdirs:" + hdfspath);
            fs.mkdirs(path);
        }
    }

    public static void chkandDeldir(FileSystem fs, String hdfspath) throws IOException{
        Path path = new Path(hdfspath);
        if(fs.exists(path))
        {
            log.info("hdfs path exists, so delete:" + hdfspath);
            fs.delete(path, true);
        }
    }

    public static List<String> listpaths(FileSystem fs, String regexPath){
        List<String> result= new ArrayList<String>();
        FileStatus[] fileStatus;
        try
        {
            fileStatus = fs.globStatus(new Path(regexPath));
            Path [] listpaths = org.apache.hadoop.fs.FileUtil.stat2Paths(fileStatus);
            for(int i=0;i<listpaths.length;i++){
                result.add(listpaths[i].toUri().getPath());
            }
        }
        catch(IllegalArgumentException e)
        {
            log.error(e,e);
        }
        catch(IOException e)
        {
            log.error(e,e);
        }
        return result;
    }

    public static void uploadFile(FileSystem fs,String src,String dst){
        Path srcPath = new Path(src); //本地上传文件路径
        Path dstPath = new Path(dst); //hdfs目标路径
        try{
            //调用文件系统的文件复制函数,前面参数是指是否删除原文件，true为删除，默认为false
            fs.copyFromLocalFile(false, srcPath, dstPath);
        }catch(IOException e){
            log.error("can not upload file to hdfs");
            log.error(e,e);
        }
    }

}

