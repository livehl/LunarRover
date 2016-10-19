
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File, FileOutputStream}
import javax.imageio.ImageIO

import scala.util.Random

/**
  * Created by admin on 10/18/2016.
  */
object MoonMap {
  /**
    * 获取一张地图
    * @param length 大小
    * @param count  陨石坑数量
    * @param maxRadius   最大半径
    * @param minRadius   最小半径
    */
  def getMapData(length:Int,count:Int=10,maxRadius:Int=30,minRadius:Int=10)={
      val array=Array.ofDim[Int](length,length)
      1 to count foreach { i=>
        //陨石坑数量
        val (x,y)=(Random.nextInt(length),Random.nextInt(length))
        val radius=Random.nextInt(maxRadius-minRadius) + minRadius
        createCrater(array,x,y,radius)
      }
      array
  }
  //创建陨石坑
  private def createCrater(array:Array[Array[Int]],x:Int,y:Int,radius:Int){
      0 until (radius*2) foreach { i:Int=>
          0 until (radius*2) foreach {j:Int=>
             val dis=Math.pow(radius -i ,2) + Math.pow(radius-j,2)
             if(dis < Math.pow(radius,2)){
               if((x+i) < array.length && (y+j) < array.head.length) {
                 array(x + i)(y + j) = 1
               }
             }
          }
      }

  }

  def mapToImage(array:Array[Array[Int]])={
    val img = new BufferedImage(array.length, array.head.length, BufferedImage.TYPE_INT_RGB)
    val g = img.getGraphics()
    g.setColor(new Color(250, 250, 250))
    // 填充整个图片的颜色
    g.fillRect(0, 0, array.length,  array.head.length)
    // 汇出陨石坑
    0 until array.length foreach{x=>
      0 until array.head.length foreach{ y=>
        if(array(x)(y)>0)
        img.setRGB(x,y,Color.BLACK.getRGB)
      }
    }
    g.dispose()
    val out = new ByteArrayOutputStream()
    ImageIO.write(img, "PNG", out)
    val data=out.toByteArray
    val fos=new FileOutputStream(new File("z:\\a.png"))
    fos.write(data)
    data

  }

}
