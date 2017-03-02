package utils

// import com.typesafe.scalalogging.slf4j
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

import scala.reflect.ClassTag

/**
  * Spark util object to create SparkContext and SparkSession
  */
object Spark {

  private var _sc: Option[SparkContext] = None

  private var _spark: Option[SparkSession] = None

  var AppName = "scala-etl"
  //val Logger = slf4j.Logger(LoggerFactory.getLogger(this.getClass))

  //Logger.warn("Local mode?: {}", IsLocalMode.toString)

  // shall not be called, but through getSc
  private def createContext(): Unit = {
    val conf = new SparkConf().setAppName(AppName) // should load everything from the conf folder if on a cluster
    if (!conf.contains("spark.master")) conf.setMaster("local[4]") // running locally, usually tests

    assignSc(new SparkContext(conf))
    assignSpark(new SparkSession(_sc))
  }

  // should be called in spark-shell
  def assignSc(currentSc: SparkContext): Unit = {
    if (isScAssigned) throw new RuntimeException("Spark context already exists")

    _sc = Some(currentSc)
  }

  // should be called in spark-shell
  def assignSpark(currentSpark: SparkSession): Unit = {
    if (isSparkAssigned) throw new RuntimeException("Spark session already exists")

    _spark = Some(currentSpark)
  }

  def sc: SparkContext = getSc()

  def emptyRDD[T: ClassTag]: RDD[T] = sc.emptyRDD[T]

  // get or create the sc
  def getSc(): SparkContext = {
    if (!isScAssigned) createContext()
    _sc.get
  }

  def getSpark(): SparkSession = {
    if (!isSparkAssigned) createContext()
    _spark.get
  }

  def isScAssigned: Boolean = {
    _sc.nonEmpty
  }

  def isSparkAssigned: Boolean = {
    _spark.nonEmpty
  }

  // useful for teardown
  def stopContexts(): Unit = {
    _spark.foreach(_.close())
    _spark = None
    _sc.foreach(_.stop())
    _sc = None
  }

}
