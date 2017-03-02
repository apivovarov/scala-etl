import org.apache.spark.sql.DataFrame
import org.apache.spark.storage.StorageLevel
import utils.Spark

/**
  * ScalaEtl
  */
class ScalaEtl {

  var deaths: DataFrame = null
  var betterHealth: DataFrame = null
  var betterHealthAndDeath: DataFrame = null

  def loadFiles(): Unit = {

    val spark = Spark.getSpark()
    deaths = spark.read.
      format("com.databricks.spark.csv").
      option("header", "true").
      option("inferSchema", "true").
      load("/hart/datasets/risk/Deaths_in_122_U.S._cities_-_1962-2016._122_Cities_Mortality_Reporting_System.csv").
      select("Year", "State", "All Deaths").groupBy("Year", "State").sum("All Deaths").
      withColumnRenamed("sum(All Deaths)", "deaths").
      withColumnRenamed("State", "StateAbbr")

    deaths.show()

    // deaths.persist(StorageLevel.MEMORY_AND_DISK_SER)

    // load 500_Cities__Local_Data_for_Better_Health.csv
    betterHealth = spark.read.
      format("com.databricks.spark.csv").
      option("header", "true").
      option("inferSchema", "true").
      load("/hart/datasets/risk/500_Cities__Local_Data_for_Better_Health.csv")

    betterHealth.show()

    // join betterHealth with deaths
    betterHealthAndDeath = betterHealth.join(deaths, usingColumns = Seq("Year", "StateAbbr"))

    betterHealthAndDeath.printSchema()
    betterHealthAndDeath.select("Year", "StateAbbr", "Short_Question_Text", "deaths").show()

  }

}
