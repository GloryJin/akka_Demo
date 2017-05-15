package com.glory.zycdn.core.config;

import com.glory.zycdn.core.util.HDFSUtil;
import com.glory.zycdn.core.util.ToolUtil;
import com.glory.zycdn.etl.metadata.ETLTaskMetaData;
import com.glory.zycdn.etl.metadata.SrcDataMetaData;
import com.glory.zycdn.etl.metadata.TransMetaData;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.DocumentException;


public class SysConfig{
    private static final Logger log = Logger.getLogger(SysConfig.class);
    private static final String workdir = "/home/mr/zycdnetl";
    private static final String configxml = workdir + "/conf/sort_config.xml";
    private static final String configini = workdir + "/conf/config.ini";
    private static Document configDoc = null;

    public static final String hdfsconfig = workdir + "/conf/hdfs-site.xml";
    //存储维度数据
    public static Map<String,Map<String,String>>  dimenData = new HashMap<>();
    //存储srcData元数据
    public static List<SrcDataMetaData> SrcData = new ArrayList<>();

    public static Map<String,String> configs = new HashMap<>();

    public static Configuration hdfsConf = null;

    public static Configuration getHdfsConf() {
        if(hdfsConf == null){
            hdfsConf = HDFSUtil.getHDFSConf();
        }
        return hdfsConf;
    }

    static{
        init_ini();
//        initDimen();
        initSrcData();
    }

    private static void init_ini()
    {
        File file = new File(configini);
        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            String[] lineArray = null;
            while((line = reader.readLine()) != null)
            {
                String linetrim = line.trim();
                if(linetrim.startsWith("#") || StringUtils.isBlank(linetrim))
                {
                    continue;
                }

                lineArray = line.split("=", -1);
                if( lineArray.length == 2 ){
                    String name = lineArray[0].trim();
                    String value = lineArray[1].trim();
                    configs.put(name, value);
                }
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(IOException e)
                {}
            }
        }
    }

    private static Document getConfigDoc(){
        if(configDoc == null){
            File xmlFile = new File(configxml);
            if(!xmlFile.exists()){
                log.error("config xml file is not exists");
            }
            SAXReader saxReader = new SAXReader();
            try{
                configDoc = saxReader.read(xmlFile);
            }catch(DocumentException e){
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return configDoc;
    }

    private static void initDimen(){
        Document doc = getConfigDoc();
        Configuration conf = hdfsConf;
        FileSystem fs = null;
        try{
            fs = FileSystem.get(conf);
        }catch(IOException e){
            log.error("can not connect hdfs");
            e.printStackTrace();
        }
        List list = doc.selectNodes("/configs/dimendata/*");
        for(Object obj:list){
            Element dimenData = (Element)obj;
            readDimeData(dimenData,fs);
        }
    }

    private static void readDimeData(Element dimendata,FileSystem fs){
        String dimenName = dimendata.getName();
        String inpath = ((Element)dimendata.selectObject("inpath")).getTextTrim();
        String infile_splitby = ((Element)dimendata.selectObject("infile_splitby")).getText();
        int keypos = Integer.parseInt(((Element)dimendata.selectObject("key")).attributeValue("pos")) - 1;
        String keyMethod = ((Element)dimendata.selectObject("key")).attributeValue("method");
        int valuepos = Integer.parseInt(((Element)dimendata.selectObject("value")).attributeValue("pos")) - 1;
        String valueMethod = ((Element)dimendata.selectObject("value")).attributeValue("method");

        FSDataInputStream fsr = null;
        BufferedReader bufferedReader = null;
        String lineTxt = null;
        try {
            if(fs.exists(new Path(inpath))){
                fsr = fs.open(new Path(inpath));
                bufferedReader = new BufferedReader(new InputStreamReader(fsr));
                Map<String,String> dimenMap = new HashMap<>();
                while ((lineTxt = bufferedReader.readLine()) != null){
                    String[] fieldsArry = lineTxt.split("\\" + infile_splitby,-1);
                    String key = fieldsArry[keypos];
                    String value = fieldsArry[valuepos];
                    if(StringUtils.isNotBlank(keyMethod)){
                        key = ToolUtil.callMethod(keyMethod);
                    }
                    if(StringUtils.isNotBlank(valueMethod)){
                        value = ToolUtil.callMethod(valueMethod,new String[]{value});
                    }
                    dimenMap.put(key,value);
                }
                dimenData.put(dimenName,dimenMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bufferedReader != null){
                try{
                    bufferedReader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static void initSrcData(){
        Document doc = getConfigDoc();
        List list = doc.selectNodes("/configs/srcdatas/*");
        for(Object obj:list){
            Element srcData = (Element)obj;
            String srcDataNodeName = srcData.getName();

            Node srcData_common = srcData.selectSingleNode("common");
            String infile_splitby= ((Element)srcData_common.selectObject("infile_splitby")).getTextTrim();
            String inpath= ((Element)srcData_common.selectObject("inpath")).getTextTrim();
            String infile_suffix= ((Element)srcData_common.selectObject("infile_suffix")).getTextTrim();
            String dealflag= ((Element)srcData_common.selectObject("dealflag")).getTextTrim();

            List<ETLTaskMetaData> etlTaskMetaDataList = new ArrayList<>();
            List ETLTasksList = srcData.selectNodes("/configs/srcdatas/"+ srcDataNodeName +"/*");
            for(Object taskobj:ETLTasksList){
                Element ETLTasks = (Element)taskobj;
                String etlTaskNodeName = ETLTasks.getName();
                if(etlTaskNodeName.equals("common")){
                    continue;
                }else{
                    Node taskCommonNode = ETLTasks.selectSingleNode("common");
                    Node fieldsNode = ETLTasks.selectSingleNode("fields");
                    etlTaskMetaDataList.add(parseETLTask(taskCommonNode,fieldsNode,srcDataNodeName,etlTaskNodeName));
                }
            }
            SrcDataMetaData srcDataMetaData = new SrcDataMetaData(infile_splitby,infile_suffix,dealflag,inpath,etlTaskMetaDataList,srcDataNodeName);
            SrcData.add(srcDataMetaData);
        }
    }

    private static ETLTaskMetaData parseETLTask(Node common,Node fieldsNode,String srcDataNodeName,String etlTaskNodeName){
        String storetype = ((Element)common.selectObject("storetype")).getTextTrim();
        String outfile_separator = ((Element)common.selectObject("outfile_separator")).getTextTrim();
        String outpath = ((Element)common.selectObject("outpath")).getTextTrim();
        String exporttype = ((Element)common.selectObject("exporttype")).getTextTrim();
        String outfilename = ((Element)common.selectObject("outfilename")).getTextTrim();
        String outsplitnum = ((Element)common.selectObject("outsplitnum")).getTextTrim();
        String outfile_suffix = ((Element)common.selectObject("outfile_suffix")).getTextTrim();
        String outfilename_method = ((Element)common.selectObject("outfilename")).attributeValue("method");

        return new ETLTaskMetaData(storetype,outfile_separator,outpath,exporttype,outfilename,outsplitnum,
                parseTrans(fieldsNode,srcDataNodeName,etlTaskNodeName),outfile_suffix,outfilename_method,etlTaskNodeName);
    }

    private static List<TransMetaData> parseTrans(Node fieldsNode,String srcDataNodeName,String etlTaskNodeName){
        List fieldsList = fieldsNode.selectNodes("/configs/srcdatas/" + srcDataNodeName+ "/" + etlTaskNodeName + "/fields/*");
        List<TransMetaData> tmdList = new ArrayList<>();
        for(Object fieldobj:fieldsList){
            Element fieldsElem = (Element)fieldobj;
            int srcpos = Integer.parseInt(fieldsElem.attributeValue("srcpos")) - 1;
            int dstpos = Integer.parseInt(fieldsElem.attributeValue("dstpos")) - 1;
            String methodName = fieldsElem.attributeValue("method");
            tmdList.add(new TransMetaData(srcpos,dstpos,methodName));
        }
        return tmdList;
    }

    public static void main(String[] args){
//        SysConfig.initDimen();
//        System.out.println(ToolUtil.callMethod("pipe"));
//        SysConfig.initSrcData();
//        System.out.println(dimenData.size());
//        System.out.println(dimenData.get("areainfo").size());
//        System.out.println(dimenData.get("areainfo").get("12"));
        System.out.println("hello world");
    }
}
