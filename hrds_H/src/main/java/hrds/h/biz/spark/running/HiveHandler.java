package hrds.h.biz.spark.running;

import org.apache.spark.sql.*;

import static hrds.commons.utils.Constant.*;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class HiveHandler extends Handler {

    private final SparkHandleArgument.HiveArgs hiveArgs;

    HiveHandler(SparkSession spark, Dataset<Row> dataset,
                SparkHandleArgument.HiveArgs hiveArgs) {
        super(spark, dataset, hiveArgs);
        this.hiveArgs = hiveArgs;

        setDatabase();
    }

    private void setDatabase() {
        String database = hiveArgs.getDatabase();
        if (spark.catalog().databaseExists(database)) {
            spark.catalog().setCurrentDatabase(database);
        }
    }

    @Override
    public void insert() {
        DataFrameWriter<Row> dataFrameWriter = dataset.write();
        if (hiveArgs.isOverWrite()) {
            dataFrameWriter.mode(SaveMode.Overwrite);
        } else {
            dataFrameWriter.mode(SaveMode.Append);
        }
        dataFrameWriter.insertInto(tableName);
    }

    @Override
    public void increment() {
        //本次表数据集
        String currentTable = "current_hyren_" + tableName;
        dataset.createOrReplaceTempView(currentTable);

        String deltaTable = "delta_hyren_" + tableName;

        spark.sql(String.format("CREATE TABLE %s " +
                "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' " +
                "STORED AS PARQUET " +
                "AS SELECT * FROM %s ", deltaTable, tableName));

        //新增的数据插入delta表
        spark.sql(String.format("INSERT INTO %s SELECT * FROM %s WHERE NOT EXISTS " +
                        "(SELECT * FROM %s WHERE %s.%s = %s.%s AND %s.%s = '%s')",
                deltaTable, currentTable, tableName, currentTable, MD5NAME, tableName,
                MD5NAME, tableName, EDATENAME, MAXDATE));

        //关联的数据 以关联的形式 插入delta表
        String colsMaxEdate = hiveArgs.getColumns().
                replace(EDATENAME, "'" + hiveArgs.getEtlDate() + "'");
        spark.sql(String.format("INSERT INTO %s SELECT %s FROM %s WHERE NOT EXISTS " +
                        "(SELECT * FROM %s WHERE %s.%s = %s.%s AND %s.%s = '%s')",
                deltaTable, colsMaxEdate, tableName, currentTable, currentTable, MD5NAME, tableName,
                MD5NAME, tableName, EDATENAME, MAXDATE));

        //不变的数据（包括历史失效数据） 插入delta表
        spark.sql(String.format("INSERT INTO %s SELECT * FROM %s WHERE %s.%s <> '%s' " +
                        "OR " +
                        "(NOT EXISTS " +
                        "(SELECT %s.%s FROM %s WHERE %s.%s = %s.%s AND %s.%s <> '%s') " +
                        "AND %s.%s = '%s')",
                deltaTable, tableName, tableName, EDATENAME, MAXDATE,
                deltaTable, MD5NAME, deltaTable, tableName, MD5NAME, deltaTable, MD5NAME, deltaTable, EDATENAME, MAXDATE,
                tableName, EDATENAME, MAXDATE));

        spark.sql("DROP TABLE IF EXISTS " + tableName);
        spark.sql("ALTER TABLE " + deltaTable + " RENAME TO " + tableName);
    }

}
