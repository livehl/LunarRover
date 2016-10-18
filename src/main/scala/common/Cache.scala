package common

import java.io.Serializable
import java.util.{Collections, Date}

import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import concurrent.duration._
import scala.collection.JavaConversions._

/**
  * Created by isaac on 16/2/15.
  */
object Cache {
  lazy val conf = ConfigFactory.load()
  //内存易失性缓存
  private val softCache = Collections.synchronizedMap[String, (Long, AnyRef)](new SoftHashMap[String, (Long, AnyRef)](conf.getInt("cache.size")))
  private val useZ4z=conf.getBoolean("cache.z4z")
  private def setCacheValue(key: String, v: AnyRef,time:Int)={
    val saveTime = if( time== Int.MaxValue) time else System.currentTimeMillis() / 1000 +time
    val cacheData=new CacheData(v,time)
    softCache.put(key, (saveTime, v))
  }
  def getCache(key:String)={
    val now = System.currentTimeMillis() / 1000
    var scv:Tuple3[Long, AnyRef, Boolean]=null
    scv= softCache.get(key).asInstanceOf[Tuple3[Long,AnyRef,Boolean]]
      if(scv!=null){
        if (scv._1 <= now) {
          delCache(key)
          None
        }else
          Some(scv._2)
      } else {
        None
      }
  }

  def setCache(key: String, v: AnyRef,time:Int= -1) = {
    setCacheValue(key,v,time)
  }
  def delCache(key: String) = {
    softCache.remove(key)
  }

}
/**
用于缓存的数据结构
 */
class CacheData(val value:AnyRef,val time:Long)extends  Serializable{
  def getValue={
         value
  }
}







