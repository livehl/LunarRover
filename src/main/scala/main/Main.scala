package main

import java.io.File
import java.net.URL

import akka.actor.{ActorSystem, Props}
import common.Tool._

import scala.io.Source
/**
  * Created by admin on 2016/9/9.
  */
object Main {
  def main(args: Array[String]): Unit = {

    val actorSystem = ActorSystem.create("LunarRover")
    val control=actorSystem.actorOf(Props[ControlCenter], name = "controlCenter")
    //转换函数
    def getLineInfo(str: String) = {
      val array = str.trim.split(",")
      new LineInfo(new Point(array(0).toInt, array(1).toInt), array(2).toInt, array(3).toInt, array(4).toInt)
    }
    //将文本数据转化为线路信息  跳过空行和注释
    val lines = new File(getClassPath + "/lines/").listFiles().map(f =>Source.fromFile(f).getLines().filter(!_.trim.isEmpty).filter(!_.trim.startsWith("#")).map(getLineInfo).toList)
    //启动月球车执行任务
    0 until lines.size map (v=>new LunarRover(lines(v),"月球车"+v,control ))  foreach( lr=> run(lr.start()))
  }

  private def getClassPath()={
    val url = Main.getClass.getResource("/application.conf")
    new File(url.toURI()).getParentFile().getAbsolutePath
  }

}
