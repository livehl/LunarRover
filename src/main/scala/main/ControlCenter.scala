package main

import java.text.SimpleDateFormat
import java.util.Date
import akka.actor._
import common.VenusException

/**
  * Created by admin on 10/18/2016.
  */
class ControlCenter  extends Actor with ActorLogging {
  def receive = {
    case lsr:LunarStartRoverInfo=>
      println(s"月球车:${lsr.name} 以速度${lsr.start.speed}从${lsr.start.point.x},${lsr.start.point.y}开始向${lsr.end.point.x},${lsr.end.point.y}移动")
    case lri:LunarRoverInfo=>
      //TODO 预测月球车位置
      println(s"月球车:${lri.name} ,位置${lri.line.point.x},${lri.line.point.y} 以速度${lri.line.speed},角度${lri.line.degree}移动")
    case a: Any =>
      sender ! new VenusException("未识别的对象")
  }
}
