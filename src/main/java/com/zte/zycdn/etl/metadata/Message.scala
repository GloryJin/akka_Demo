package com.zte.zycdn.etl.metadata

import java.io.File
import java.util.HashMap

import akka.actor.ActorRef

/**
  * Created by 10190203 on 2017/5/14.
  */
trait Message {

}

//文件消息
case class FileMsg(filePath:String,actorRef: ActorRef) extends Serializable

case class SortFileMsg(file:File,srcCommon:SrcDataMetaData#SrcCommon,etlTask:ETLTaskMetaData,outfile_path:String)

case class FinshMsg(srcName:String,taskName:String,fileName:String)

case class FinshSrcMsg(srcName:String,finshInfo:HashMap[String,HashMap[String,Int]])


case class SrcsMsg(srcDatas:List[SrcDataMetaData])

case class Hello(hi:String) extends Serializable

