import utils.Spark

Spark.AppName = "scala-etl"

Spark.assignSc(sc)
Spark.assignSpark(spark)

val datasetsPath = "/hart/datasets"
val outputPath = "/hart/output2"

val etl = new ScalaEtl()
etl.loadFilesAndJoin(datasetsPath)

val aol = new Aol()
aol.loadAndGroup(datasetsPath)

etl.joinSearchResultAndSave(aol, outputPath)
