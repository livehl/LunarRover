package main

import java.io.File
import java.net.URL

import akka.actor.{ActorSystem, Props}
import common.Tool._

import scala.io.{Codec, Source}
/**
  * Created by admin on 2016/9/9.
  */
object Main {
  private implicit val code=Codec.UTF8
  def main(args: Array[String]): Unit = {

    val actorSystem = ActorSystem.create("LunarRover")
    val control=actorSystem.actorOf(Props[ControlCenter], name = "controlCenter")
    //转换函数
    def getLineInfo(str: String) = {
      val array = str.trim.split(",")
      new LineInfo(new Point(array(0).toDouble, array(1).toDouble), array(2).toDouble, array(3).toDouble, array(4).toDouble)
    }
    //将文本数据转化为线路信息  跳过空行和注释
    val lines = new File(getClassPath + "/lines/").listFiles().map(f =>  f.getName -> Source.fromFile(f).getLines().filter(!_.trim.isEmpty).filter(!_.trim.startsWith("#")).map(getLineInfo).toList)
    //启动月球车执行任务
    0 until lines.size map (v=>new LunarRover(lines(v)._2,"月球车"+lines(v)._1,control ))  foreach( lr=> run(lr.start()))
    
    while(true){
      Thread.sleep(500)
      control ! ShowInfo
    }
  }

  private def getClassPath()={
    val url = Main.getClass.getResource("/application.conf")
    new File(url.toURI()).getParentFile().getAbsolutePath
  }

}
