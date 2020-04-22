package hrds.h.biz.spark.running;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * @author mick
 * @title: Handle
 * @projectName hrsv5
 * @description: TODO
 * @date 20-4-13下午6:00
 */
public abstract class Handle {
    SparkSession spark;
    Dataset<Row> dataset;
    String tableName;

    Handle(SparkSession spark, Dataset<Row> dataset, String tableName) {
        this.spark = spark;
        this.dataset = dataset;
        this.tableName = tableName;
    }

    public abstract void insert();

    public abstract void increment();
}
