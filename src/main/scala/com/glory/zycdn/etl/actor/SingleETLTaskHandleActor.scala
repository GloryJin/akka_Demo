package com.glory.zycdn.etl.actor

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import com.glory.zycdn.etl.metadata._
import org.apache.log4j.Logger
import com.glory.zycdn.core.util.ToolUtil


class SingleETLTaskHandleActor extends Actor{
  val logger = Logger.getLogger(classOf[SingleETLTaskHandleActor])
  var srcCommon:SrcDataMetaData#SrcCommon = null
  var etlTask:ETLTaskMetaData = null
  var lastActor:ActorRef = null

  override def receive: Receive = {
    case fileMsg:FileMsg => {
      logger.debug("recieve new file itor msg")
      lastActor = fileMsg.actorRef
      val filePath = fileMsg.filePath
      val file = new File(filePath)

      val etlCommon = etlTask.getEtlTaskCommon

      val exporttype = etlCommon.getExporttype
      val outpath = etlCommon.getOutpath
      var outfilename = etlCommon.getOutfilename
      val outfilename_method = etlCommon.getOutfilename_method
      val outfile_suffix = etlCommon.getOutfile_suffix

      outfilename = ToolUtil.callMethod(outfilename_method,Array(file.getName))

      val tmp_outfile = outpath + "/" + outfilename + outfile_suffix
      val fileHandleActor = context.actorOf(Props[FileHandleActor])

      logger.debug("sene a file info to FileHandleActor")
      fileHandleActor ! SortFileMsg(file,srcCommon,etlTask,tmp_outfile)

    }
    case srcCommonInfo: SrcDataMetaData#SrcCommon=> {
      logger.debug("recieve src common info msg")
      srcCommon = srcCommonInfo

    }
    case etlTaskInfo: ETLTaskMetaData => {
      logger.debug("recieve etlTask msg")
      etlTask = etlTaskInfo
    }

    case finshMsg:FinshMsg => {
      lastActor ! finshMsg
    }

  }
}

