package com.zte.zycdn.etl.actor

import java.io._

import scala.io.Source
import akka.actor.Actor
import com.zte.zycdn.core.util.ToolUtil
import com.zte.zycdn.etl.metadata.{ETLTaskMetaData, FinshMsg, SortFileMsg, SrcDataMetaData}
import org.apache.log4j.Logger
/**
  * Created by 10190203 on 2017/5/11.
  */
class FileHandleActor extends Actor{
  val logger = Logger.getLogger(classOf[FileHandleActor])
  override def receive: Receive = {

    case sortFileMsg:SortFileMsg =>{
      val file = sortFileMsg.file
      val infile_splitby = sortFileMsg.srcCommon.getInfile_splitby
      val etlinfo = sortFileMsg.etlTask
      val outfile_path = sortFileMsg.outfile_path
      FileHandleActor.sortFile(file,infile_splitby,etlinfo,outfile_path)
      sender ! FinshMsg(sortFileMsg.srcCommon.getSrcName,etlinfo.getEtlTaskCommon.getTaskName,file.getName)

      context.stop(self)
    }
  }
}

object FileHandleActor{

  def sortFile(file:File,infile_splitby:String,etlinfo:ETLTaskMetaData,outfile_path:String):Unit= {
    val transFields = etlinfo.getTransFields
    val goalArray = new Array[String](transFields.size())
    val outfile_separator = etlinfo.getEtlTaskCommon.getOutfile_separator
    val outpath = etlinfo.getEtlTaskCommon.getOutpath

    FileHandleActor.checkDir(outpath)
    val pf = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile_path,true),"utf-8"))

    val br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"))
    var line:String= br.readLine()

    while(line != null){
      val fieldsArray = line.split("\\"+infile_splitby,-1)
      for(i <- 0 until transFields.size()){
        val transField = transFields.get(i)
        val srcpos = transField.getSrcpos
        val dstpos = transField.getDstpos
        val methodName = transField.getMethodName
        if(methodName == null){
          goalArray(dstpos) = fieldsArray(srcpos)
        }else{
          goalArray(dstpos) = ToolUtil.callMethod(methodName,Array(fieldsArray(srcpos)))
        }
      }
      pf.append(FileHandleActor.array2String(goalArray,outfile_separator))

      line = br.readLine()
    }

    br.close()
    pf.flush()
    pf.close()
  }

  def array2String(arr:Array[String],spt:String) = {
    val sb = new java.lang.StringBuilder()
    sb.append(arr(0))
    for(i <- 1 until arr.length){
      sb.append(spt + arr(i))
    }
    sb.toString + "\n"
  }
  def checkDir(path:String) = {
    val dir = new File(path)
    if(!dir.exists()){
      dir.mkdirs()
    }
  }
}
