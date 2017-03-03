import org.apache.spark.sql.DataFrame
import utils.Spark


/**
  * ScalaEtl
  */
class ScalaEtl {

  var deaths: DataFrame = null
  var betterHealth: DataFrame = null
  var betterHealthAndDeath: DataFrame = null

  def loadFilesAndJoin(datasetsPath: String): Unit = {

    val spark = Spark.getSpark()

    deaths = spark.read.
      format("com.databricks.spark.csv").
      option("header", "true").
      option("inferSchema", "true").
      load(s"$datasetsPath/risk/Deaths_in_122_U.S._cities_-_1962-2016._122_Cities_Mortality_Reporting_System.csv").
      select("Year", "State", "All Deaths").groupBy("Year", "State").sum("All Deaths").
      withColumnRenamed("sum(All Deaths)", "deaths").
      withColumnRenamed("State", "StateAbbr")

    deaths.show()

    // load 500_Cities__Local_Data_for_Better_Health.csv
    betterHealth = spark.read.
      format("com.databricks.spark.csv").
      option("header", "true").
      option("inferSchema", "true").
      load(s"$datasetsPath/risk/500_Cities__Local_Data_for_Better_Health.csv")

    betterHealth.show()

    // join betterHealth with deaths
    betterHealthAndDeath = betterHealth.join(deaths, usingColumns = Seq("Year", "StateAbbr"))

    betterHealthAndDeath.printSchema()
    betterHealthAndDeath.select("Year", "StateAbbr", "Short_Question_Text", "deaths").show()
  }

  def joinSearchResultAndSave(aol: Aol, outFolder: String): Unit = {
    val spark = Spark.getSpark()
    import spark.implicits._

    // prepare list of Short_Question (only 28 of them in data)
    // for each Short_Question do lookup in aol DF (DF persisted in memory)
    val questions = betterHealth.select("Short_Question_Text").distinct().rdd.map(r => r(0).toString).collect()

    val urlsDF = questions.map(q => (q, aol.getTop10(q.toLowerCase()))).toList.toDS.toDF.
      withColumnRenamed("_1", "Short_Question_Text").
      withColumnRenamed("_2", "ClickURLs")

    betterHealthAndDeath.join(urlsDF, usingColumns = Seq("Short_Question_Text")).write.json(outFolder)
  }

}
