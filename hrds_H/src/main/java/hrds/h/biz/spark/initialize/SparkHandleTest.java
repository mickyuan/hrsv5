package hrds.h.biz.spark.initialize;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class SparkHandleTest {
    public static void main(String[] args) throws InterruptedException {
        Logger.getLogger("org").setLevel(Level.WARN);
        SparkSession.Builder builder = SparkSession.builder()
                //定义spark名称
                .appName("Market_Spark_Test")
//                .master("yarn-cluster")
                //支持连接 hive metastore
                .enableHiveSupport();
        try (SparkSession sparkSession = builder.getOrCreate()) {
            sparkSession.sql("show tables").show();
            Thread.sleep(100000);
            sparkSession.sql("show tables").show();
            Dataset<Row> showTables = sparkSession.sql("show tables");
        }

    }
}
