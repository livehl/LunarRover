package main

import java.text.SimpleDateFormat
import java.util.Date
import akka.actor._
import scala.collection.mutable.Map

/**
  * Created by admin on 10/18/2016.
  */
class ControlCenter  extends Actor with ActorLogging {
  val cars=Map[String,Tuple2[Long,LunarRoverInfo]]()
  def receive = {
    case lsr:LunarStartRoverInfo=>
//      println(s"月球车:${lsr.name} 以速度${lsr.start.speed}从${lsr.start.point.x},${lsr.start.point.y}开始向${lsr.end.point.x},${lsr.end.point.y}移动")
    case lri:LunarRoverInfo=>
      //刷新最后位置
      cars(lri.name)=(System.currentTimeMillis()/500,lri)
//      println(s"月球车:${lri.name} ,位置${lri.line.point.x},${lri.line.point.y} 以速度${lri.line.speed},角度${lri.line.degree}移动")
    case ShowInfo=>
        //打印日志
      cars.foreach{car=>
        val point=prediction(car._2._2.line,car._2._1)
        println(s"月球车:${car._1} ,最后报告位置${car._2._2.line.point.x},${car._2._2.line.point.y},预测位置${point.x},${point.y},方向${car._2._2.line.direction}移动")
      }
    case LunarEndRoverInfo(name)=>
      println(s"月球车:${name} 到达目的地")
      cars.remove(name)
    case a: Any =>
      sender ! new Exception("未识别的对象")
  }
  //预测月球车位置(简单模拟,只计算方向)
  def prediction(line:LineInfo,time:Long)={
    val dis=line.speed * (System.currentTimeMillis()/500 - time) /2

    new Point(BigDecimal(line.point.x - dis * Math.cos(Math.toRadians(line.direction))).setScale(2,BigDecimal.RoundingMode.HALF_UP).toString.toDouble,BigDecimal(line.point.y - dis * Math.cos(Math.toRadians(90-line.direction))).setScale(2,BigDecimal.RoundingMode.HALF_UP).toString.toDouble)
  }
}
