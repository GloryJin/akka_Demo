package com.zte.zycdn.etl.actor

import java.io.File
import java.util
import java.util.HashMap

import scala.collection.JavaConverters._
import akka.actor.{Actor, Props}
import com.zte.zycdn.etl.metadata.{FinshMsg, FinshSrcMsg, SrcDataMetaData, SrcsMsg}
import org.apache.log4j.Logger


/**
  * Created by 10190203 on 2017/5/14.
  */
class SrcDispatchActor extends Actor{
  val logger = Logger.getLogger(classOf[SrcDispatchActor])
  //这里存etl任务的完成信息[srcname,[filename,[taskname,0]]]
  val etlFinishFlagMap = new HashMap[String,HashMap[String,HashMap[String,Int]]]

  val srcDatasMap= new HashMap[String,SrcDataMetaData]


  override def receive: Receive = {
    case srcsMsg:SrcsMsg => {
      val srcDatas = srcsMsg.srcDatas
        for(i <- 0 until srcDatas.length){
          val srcData = srcDatas(i)
          val srcHandleActor = context.actorOf(Props[SrcHandleActor])
          srcHandleActor ! srcData
          logger.debug("send a srcData monitor msg to srcDispatchActor")
          etlFinishFlagMap.put(srcData.getSrcCommon.getSrcName,new HashMap[String,HashMap[String,Int]]())
          srcDatasMap.put(srcData.getSrcCommon.getSrcName,srcData)
          logger.debug(srcDatasMap.size())
        }
    }
    case finshSrcMsg:FinshSrcMsg => {
      val srcName = finshSrcMsg.srcName
      val finshInfo = finshSrcMsg.finshInfo
      etlFinishFlagMap.get(srcName).putAll(finshInfo)
    }
    case finshMsg:FinshMsg => {
      logger.debug("recieve a etl task file FinshMsg")
      changeSuffix(finshMsg,etlFinishFlagMap)
    }
  }


  def changeSuffix(finshMsg:FinshMsg,fileMap:HashMap[String,HashMap[String,HashMap[String,Int]]]) = {
    val srcName = finshMsg.srcName
    val fileName = finshMsg.fileName
    val taskName = finshMsg.taskName

    val srcCommon = srcDatasMap.get(srcName).getSrcCommon

    val dealFlag = srcCommon.getDealflag
    val dealFileName = fileName.substring(0,fileName.lastIndexOf(".")) + dealFlag

    val dealFilepath = srcCommon.getInpath + "/" + dealFileName
    val filepath = srcCommon.getInpath + "/" + fileName

    val srcfileMap = etlFinishFlagMap.get(srcName)
    srcfileMap.get(fileName).put(taskName,1)

    if(checkFinsh(srcfileMap,fileName)){
      val file = new File(filepath)
      file.renameTo(new File(dealFilepath))
      logger.debug("rename file:" + fileName + " to file:" + dealFileName)
      srcfileMap.remove(fileName)
    }else{
      logger.debug("has task is not finsh,can not change suffix")
    }

  }

  def checkFinsh(FileMap:HashMap[String,HashMap[String,Int]],fileName:String):Boolean = {
    var flag = 1
    for(etl <- FileMap.get(fileName).keySet().asScala){
      flag &=  FileMap.get(fileName).get(etl)
    }
    if(flag == 1) true else false
  }

  def renameFile(srcFile:File,dealFlag:String): Unit = {
    val absolutePath = srcFile.getAbsolutePath
    val rename = absolutePath.substring(0,absolutePath.lastIndexOf(".")) + dealFlag
    srcFile.renameTo(new File(rename))
  }
}
