package common

import java.io._
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util
import java.util.concurrent.Executors
import java.util.zip.{GZIPInputStream, GZIPOutputStream}
import java.util.{Date, TimeZone, UUID}

import scala.Some
import scala.collection.mutable.{HashMap, ListBuffer}
import scala.collection.parallel.{ForkJoinTaskSupport, ParIterable}
import scala.collection.{Parallelizable, mutable}
import scala.concurrent._
import scala.concurrent.forkjoin.ForkJoinPool
import scala.util.Random

/**
 * Created by 林 on 14-4-3.
 */
object Tool {

  private val chars: Array[Char] = "0123456789ABCDEF".toCharArray
  private val settingObjectCache = new util.Hashtable[String, AnyRef]()
  private val AES_DEFAULT_KEY = "#$#$#^T#$45rw3d4g$%^"

  val pool = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors*2)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(pool)

  /*
   * 霸气侧漏吊炸天的东西,把集合方法直接变成多线程执行
   */
  implicit class ParToMutile[+A](parable: Parallelizable[A, ParIterable[A]]) {
    def mutile(thread: Int = -1) = {
      if (thread == -1) {
        parable.par
      } else {
        val resutl = parable.par
        resutl.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(thread))
        resutl
      }
    }
  }


  def isAESData(s: String) = {
    s.length % 32 == 0 && s.matches("[0-9a-fA-F]+")
  }


  def hex2bytes(hex: String): Array[Byte] = {
    hex.replaceAll("[^0-9A-Fa-f]", "").sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
  }

  def bytes2hex(bytes: Array[Byte], sep: Option[String] = None): String = {
    sep match {
      case None => bytes.map("%02x".format(_)).mkString
      case _ => bytes.map("%02x".format(_)).mkString(sep.get)
    }
  }

  /**
   * md5加密.
   *
   * @param str
	 * the str
   * @return string
   * @throws Exception
	 * the exception
   */
  def md5(str: String): String = {
    val md5: MessageDigest = MessageDigest.getInstance("MD5")
    val sb: StringBuilder = new StringBuilder
    for (b <- md5.digest(str.getBytes("utf-8"))) {
      sb.append(str2HexStr(b))
    }
    return sb.toString
  }

  /**
   * Str to hex str.
   *
   * @param b the b
   * @return the string
   * @author 黄林
   */
  def str2HexStr(b: Byte): String = {
    val r: Array[Char] = new Array[Char](2)
    var bit: Int = (b & 0x0f0) >> 4
    r(0) = chars(bit)
    bit = b & 0x0f
    r(1) = chars(bit)
    val str: String = new String(r)
    return str
  }

  //带重试的区段
  def reTry(count: Int = 5)(f: => Unit) {
    var retryCount = 0
    while (retryCount <= count) {
      try {
        f
        retryCount = count + 1
      } catch {
        case e: Throwable =>
          retryCount += 1
          Thread.sleep(100*retryCount)
          if (retryCount > count) {
            throw e
          }
      }
    }
  }

  def safe[T](f: => T) = {
    try {
      f
    } catch {
      case e: Throwable =>
        println("error")
        e.printStackTrace()
        null.asInstanceOf[T]
    }
  }

  //后台执行
  def run[T](body: => T) = Future[T](body)


  implicit class StringAddMethod[A <: String](bean: A) {

    def md5(): String = Tool.md5(bean)

    def isPhone: Boolean = """^1\d{10}$""".r.pattern.matcher(bean).matches()

    def isNumber: Boolean = """^\d+$""".r.pattern.matcher(bean).matches()

    def toBigDecimal = if (isEmpty(bean)) null else BigDecimal(bean)

    def safeInt(v:Int= -1) = if (isEmpty(bean)) v else bean.toInt

    def safeInt:Int=safeInt(-1)

    def safeDouble = if (isEmpty(bean)) -1d else bean.toDouble

    def safeLong = if (isEmpty(bean)) -1l else bean.toLong

    def toIntList(split:String) = StrtoList[Int](bean, split, _.toInt)

    def toIntList = StrtoList[Int](bean, ",", _.toInt)

    def toLongList = StrtoList[Long](bean, ",", _.toLong)

    def toDoubleList = StrtoList[Double](bean, ",", _.toDouble)

  }


  implicit class NumberAddMethod[A <: BigDecimal](bean: A) {
    def toMoney(): BigDecimal = {
      bean.setScale(2, BigDecimal.RoundingMode.HALF_UP)
    }
  }

  implicit class IntegerAddMethod[A <: Int](bean: A) {
    def checkStr = if (isEmpty(bean)) "" else bean.toString
  }

  //数字自动转字符串  (老子受够了到处写toString)
  implicit def intToString(i: Int): String = i.toString

  def isEmpty(str: String) = {
    (null == str || str.isEmpty)
  }

  def isEmpty(bean: Any): Boolean = {
    bean match {
      case s: String => isEmpty(bean.asInstanceOf[String])
      case i: Int => bean.asInstanceOf[Int] == -1
      case d: Double => bean.asInstanceOf[Double] == -1
      case b: BigDecimal => b == null || b.asInstanceOf[BigDecimal] == -1
      case a: Traversable[_] => a == null || a.asInstanceOf[Traversable[AnyRef]].isEmpty
      case _ => bean == null
    }
  }

  def StrtoList[T](bean: String, split: String, fun: String => T): List[T] = {
    if (isEmpty(bean)) Nil else bean.split(split).map(fun(_)).toList
  }

  def randomStr(len: Int) = {
    val randomValue = randomChars + randomNums
    0 to (len - 1) map (v => randomValue(Random.nextInt(randomValue.length))) mkString
  }

  private val randomChars = "abcdefghjkmnpqrstvwxyABCDEFGHJKLMNPQRSTVWXY2346789"
  private val randomNums = "2346789"

  def gzip(data:Array[Byte])={
    val bos = new ByteArrayOutputStream()
    val gzip = new GZIPOutputStream(bos)
    gzip.write(data)
    gzip.finish()
    gzip.close()
    val gdata = bos.toByteArray()
    bos.close()
    gdata
  }
  def ungzip(gdata:Array[Byte])={
    val bis = new ByteArrayInputStream(gdata)
    val gzip = new GZIPInputStream(bis)
    val buf = new Array[Byte](1024)
    var num = -1
    val  baos = new ByteArrayOutputStream()
    num = gzip.read(buf, 0, buf.length)
    while (num != -1) {
      baos.write(buf, 0, num)
      num = gzip.read(buf, 0, buf.length)
    }
    val data = baos.toByteArray()
    baos.flush()
    baos.close()
    gzip.close()
    bis.close()
    data
  }
  implicit class DataAddMethod[A <: Array[Byte]](data: A) {
    def gzip = Tool.gzip(data)
    def ungzip=Tool.ungzip(data)
  }

  implicit class ListAddMethod[A <: Any,B <:Any](list: Seq[Tuple2[A,B]]) {
    def toHashMap={
      val map=new mutable.HashMap[A,ListBuffer[B]]()
      list.foreach{kv=>
        val (k,v)=kv.asInstanceOf[Tuple2[A,B]]
        if(!map.contains(k)){
          map+=((k,new ListBuffer()))
        }
        map(k).append(v)
      }
      map
    }
  }

  def uuid=UUID.randomUUID().toString.replace("-","")

  def Stream2Byte(is: InputStream)={
    val baos=new  ByteArrayOutputStream
    var b = is.read()
    while (b != -1) {
      baos.write(b)
      b = is.read()
    }
    baos.toByteArray
  }
  def File2Byte(file: File):Array[Byte]={
    Stream2Byte(new FileInputStream(file))
  }

}
