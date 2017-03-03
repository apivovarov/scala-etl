import org.apache.spark.sql.DataFrame
import org.apache.spark.storage.StorageLevel
import utils.Spark

/**
  * Aol
  */
class Aol {

  var aol: DataFrame = null

  def loadAndGroup(datasetsPath: String): Unit = {

    val spark = Spark.getSpark()
    val aol = spark.read.
      format("com.databricks.spark.csv").
      option("delimiter", "\t").
      option("header", "true").
      option("inferSchema", "true").
      load(s"$datasetsPath/search/aol/user-ct-test-collection-01.txt"). // one file for test
      select("Query", "ItemRank", "ClickURL").where("ClickURL is not null").distinct().
      persist(StorageLevel.MEMORY_AND_DISK_SER)

    aol.createOrReplaceTempView("aol");
  }

  def getTop10(query: String): String = {
    val spark = Spark.getSpark()

    spark.sql(s"select ClickURL from aol where Query = '$query' order by ItemRank desc limit 10").
      rdd.map(r => r(0).toString).collect().mkString(",")
  }

}
