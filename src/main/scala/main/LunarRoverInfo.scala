package main

/**
  * Created by admin on 10/18/2016.
  */
case class LunarRoverInfo(val name:String,line:LineInfo)

case class LunarStartRoverInfo(val name:String,start:LineInfo,end:LineInfo)

case class LunarEndRoverInfo(val name:String)


/**
  * 车辆线路信息
  * @param point   位置
  * @param direction  方向
  * @param speed  速度
  * @param degree  转向角度
  */
case class LineInfo(val point:Point,val direction:Double,val speed:Double,val degree:Double)

class Point(val x:Double,val y:Double)

case class ShowInfo()
