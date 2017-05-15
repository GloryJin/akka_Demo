package com.zte.zycdn.etl.job

import com.zte.zycdn.core.framework.JobBase
import com.zte.zycdn.etl.actor.{SrcDispatchActor, SrcHandleActor}
import com.zte.zycdn.core.config.SysConfig
import akka.actor.ActorSystem
import akka.actor.Props
import com.zte.zycdn.etl.metadata.SrcsMsg
import org.apache.log4j.{Logger, PropertyConfigurator}
import scala.collection.JavaConverters._
/**
  * Created by 10190203 on 2017/5/10.
  */
class SortJob{

}

object SortJob extends App{
  PropertyConfigurator.configure("conf/log4j.properties")

  val logger = Logger.getLogger(classOf[SortJob])
  val srcDatas = SysConfig.SrcData
  val system = ActorSystem("SortLogSystem")
  // 缺省的Actor构造函数
  val srcDispatchActor = system.actorOf(Props[SrcDispatchActor], name = "srcDispatchActor")
  srcDispatchActor ! SrcsMsg(srcDatas.asScala.toList)


//  for(i <- 0 until srcDatas.size()){
//    srcDispatchActor ! (srcDatas.get(i))
//    logger.debug("send a srcData monitor msg to srcDispatchActor")
//  }
}
