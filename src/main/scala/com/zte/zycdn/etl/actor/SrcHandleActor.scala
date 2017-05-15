package com.zte.zycdn.etl.actor

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import com.zte.zycdn.etl.metadata._
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.{FileSystems, Paths,  WatchKey}
import java.util
import java.util.HashMap

import org.apache.log4j.Logger

import scala.collection.JavaConverters._

/**
  * Created by 10190203 on 2017/5/10.
  */
class SrcHandleActor extends Actor{
  val logger = Logger.getLogger(classOf[SrcHandleActor])
  val etlTaskHandleActorRefMap = new HashMap[String,ActorRef]()

  val etlFinishFlagMap = new HashMap[String,HashMap[String,Int]]

  var srcCommon:SrcDataMetaData#SrcCommon = null


  def receive = {
    case srcTask:SrcDataMetaData => {
      logger.debug("recieve a srcData monitor msg")
      val etlTasks = srcTask.getEtlTasks
      srcCommon = srcTask.getSrcCommon

      for(i <- 0 until etlTasks.size()){
        val tmp_taskName = etlTasks.get(i).getEtlTaskCommon.getTaskName
        val tmp_exportType = etlTasks.get(i).getEtlTaskCommon.getExporttype
        if(tmp_exportType.equals("merge")){
          val etlTaskHandleActor = context.actorOf(Props[MergeETLHandleActor])
          etlTaskHandleActorRefMap.put(etlTasks.get(i).getEtlTaskCommon.getTaskName,etlTaskHandleActor)
          logger.debug("create a "+tmp_taskName+" Actor")
          etlTaskHandleActor ! srcCommon
          etlTaskHandleActor ! etlTasks.get(i)
        }else if(tmp_exportType.equals("individual")){
          val etlTaskHandleActor = context.actorOf(Props[SingleETLTaskHandleActor])
          etlTaskHandleActorRefMap.put(etlTasks.get(i).getEtlTaskCommon.getTaskName,etlTaskHandleActor)
          logger.debug("create a "+tmp_taskName+" Actor")
          etlTaskHandleActor ! srcCommon
          etlTaskHandleActor ! etlTasks.get(i)
        }
      }

      val monitorPath = Paths.get(srcCommon.getInpath)
      val watchService = FileSystems.getDefault.newWatchService()
      monitorPath.register(watchService,ENTRY_CREATE)

      var watchKey: WatchKey = null
      while(true){
        watchKey = watchService.take()
        for(event <- watchKey.pollEvents().asScala){
          val filename = event.context().toString
          if(filename.endsWith(srcCommon.getInfile_suffix)){
            val filepath = srcCommon.getInpath + "/" + filename
            val tmp_map = new util.HashMap[String,Int]
            for(key:String <- etlTaskHandleActorRefMap.keySet().asScala){
              logger.debug("send fileItor to "+ key +" etlTaskHandleActor")
              etlTaskHandleActorRefMap.get(key) ! FileMsg(filepath,sender())
              tmp_map.put(key,0)
            }
            etlFinishFlagMap.put(filename,tmp_map)
          }
        }
        sender ! FinshSrcMsg(srcCommon.getSrcName,etlFinishFlagMap)
        watchKey.reset()
      }
    }
  }

}

