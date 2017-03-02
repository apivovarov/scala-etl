import utils.Spark

/**
  * Created by pivovaa on 3/2/17.
  */
object Execute {

  def main(args: Array[String]) {
    Spark.AppName = "scala-etl"
    org.apache.log4j.Logger.getLogger("akka").setLevel(org.apache.log4j.Level.WARN)

    run()
  }

  def run(): Unit = {

    try {
      Spark.getSc()
      Spark.getSpark()

    } finally {
      Spark.stopContexts()
    }
  }

}
