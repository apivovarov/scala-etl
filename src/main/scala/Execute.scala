import utils.Spark

/**
  * Execute
  */
object Execute {

  def main(args: Array[String]) {
    Spark.AppName = "scala-etl"

    run()
  }

  def run(): Unit = {

    try {
      Spark.getSc()
      Spark.getSpark()
      val datasetsPath = "/hart/datasets"
      val outputPath = "/hart/output"

      val etl = new ScalaEtl()
      etl.loadFilesAndJoin(datasetsPath)

      val aol = new Aol()
      aol.loadAndGroup(datasetsPath)

      etl.joinSearchResultAndSave(aol, outputPath)

    } finally {
      Spark.stopContexts()
    }
  }

}
