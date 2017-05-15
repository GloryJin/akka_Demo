package com.zte.zycdn.etl.actor

import java.io.File

import akka.actor.Actor
import com.zte.zycdn.etl.metadata.{ETLTaskMetaData, FileMsg, FinshMsg, SrcDataMetaData}
import org.apache.log4j.Logger

/**
  * Created by 10190203 on 2017/5/14.
  */
class MergeETLHandleActor extends Actor{
  val logger = Logger.getLogger(classOf[MergeETLHandleActor])
  var srcCommon:SrcDataMetaData#SrcCommon = null
  var etlTask:ETLTaskMetaData = null

  override def receive: Receive = {
    case fileMsg:FileMsg => {
      val lastActor = fileMsg.actorRef
      val etlCommon = etlTask.getEtlTaskCommon
      val filePath = fileMsg.filePath

      val outpath = etlCommon.getOutpath
      val outfilename = etlCommon.getOutfilename
      val outfile_suffix = etlCommon.getOutfile_suffix

      val tmp_outfile = outpath + "/" + outfilename + outfile_suffix
      val file = new File(filePath)
      FileHandleActor.sortFile(file,srcCommon.getInfile_splitby,etlTask,tmp_outfile)

      val taskName = etlCommon.getTaskName
      val fileName = file.getName
      lastActor ! FinshMsg(srcCommon.getSrcName,taskName,fileName)

    }

    case srcCommonInfo: SrcDataMetaData#SrcCommon=> {
      logger.debug("recieve src common info msg")
      srcCommon = srcCommonInfo

    }
    case etlTaskInfo: ETLTaskMetaData => {
      logger.debug("recieve etlTask msg")
      etlTask = etlTaskInfo
    }
  }
}
