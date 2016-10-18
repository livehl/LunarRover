package main

import akka.actor._
import common.VenusException

/**
  * Created by admin on 10/18/2016.
  */
class LunarRover(lines:List[LineInfo],name:String,control:ActorRef){
    def start(): Unit ={
        control ! LunarStartRoverInfo(name,lines.head,lines.last)
        lines.drop(1).dropRight(1).foreach{line=>
            Thread.sleep(1000)
            control ! LunarRoverInfo(name,line)
        }
      control ! LunarEndRoverInfo(name)
    }
}

