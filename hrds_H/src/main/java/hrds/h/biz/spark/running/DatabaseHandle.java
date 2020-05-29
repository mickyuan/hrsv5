package hrds.h.biz.spark.running;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.collection.bean.DbConfBean;
import org.apache.spark.sql.*;

import java.util.Properties;

import static hrds.commons.utils.Constant.*;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class DatabaseHandle extends Handle {

    private final SparkHandleArgument.DatabaseArgs databaseArgs;
    private final Properties connProperties = new Properties();

    DatabaseHandle(SparkSession spark, Dataset<Row> dataset,
                   SparkHandleArgument.DatabaseArgs databaseArgs) {
        super(spark, dataset, databaseArgs.getTableName());
        this.databaseArgs = databaseArgs;
        initProperties();
    }

    private void initProperties() {
        connProperties.setProperty("user", databaseArgs.getUser());
        connProperties.setProperty("password", databaseArgs.getPassword());
    }

    @Override
    public void insert() {

        DataFrameWriter<Row> dataFrameWriter = dataset
                .write()
                //插入数据时，只truncate表，而不删除表重建
                .option("truncate", true);

        if (databaseArgs.isOverWrite()) {
            dataFrameWriter.mode(SaveMode.Overwrite);
        } else {
            dataFrameWriter.mode(SaveMode.Append);
        }
        dataFrameWriter.jdbc(databaseArgs.getUrl(), tableName, connProperties);
    }

    @Override
    public void increment() {
        //本次表数据集
        String currentTable = "current_hyren_" + tableName;

        //表数据集，去除无效历史拉链，去除非本集市表的数据
        Dataset<Row> finalDF = spark.read()
                .jdbc(databaseArgs.getUrl(), tableName, connProperties)
                .filter(EDATENAME + "=" + MAXDATE);
        if (databaseArgs.isMultipleInput()) {
            finalDF = finalDF.filter(TABLE_ID_NAME + "=" + databaseArgs.getDatatableId());
        }

        dataset.createOrReplaceTempView(currentTable);
        finalDF.createOrReplaceTempView(tableName);

        /*
         * 有效数据的df，原样插入即可
         */
        Dataset<Row> validDF = spark.sql(getValidSql(currentTable));
        validDF.write()
                .mode(SaveMode.Append)
                .jdbc(databaseArgs.getUrl(), tableName, connProperties);

        /*
         * 需要关链的数据df，只有一列，就是hyren的MD5值
         * 把该 df 写到最终表同数据库中,表名为 delta_hyren_${tableName}
         * 然后把最终表中的这些MD5记录的edate更新成调度日期
         */
        Dataset<Row> invalidDF = spark.sql(getInvalidSql(currentTable));

        String deltaTable = "delta_hyren_" + tableName;
        invalidDF.write()
                .mode(SaveMode.Overwrite).
                jdbc(databaseArgs.getUrl(), deltaTable, connProperties);

        //jdbc连接数据库进行更新
        DbConfBean dbConfBean = new DbConfBean();
        dbConfBean.setDatabase_drive(databaseArgs.getDriver());
        dbConfBean.setJdbc_url(databaseArgs.getUrl());
        dbConfBean.setUser_name(databaseArgs.getUser());
        dbConfBean.setDatabase_pad(databaseArgs.getPassword());
        dbConfBean.setDatabase_type(databaseArgs.getDatabaseType());
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dbConfBean)) {
            String sql = String.format("UPDATE %s SET %s = '%s' WHERE %s in (SELECT %s FROM %s)",
                    tableName, EDATENAME, databaseArgs.getEtlDate(), MD5NAME, MD5NAME, deltaTable);
            if (databaseArgs.isMultipleInput()) {
                sql = sql + " AND " + TABLE_ID_NAME + " = '" + databaseArgs.getDatatableId() + "'";
            }
            db.execute(sql);
            db.execute("DROP TABLE " + deltaTable);
        }

    }


    /**
     * 获取失效数据集的sql
     *
     * @param currentTable 当前数据集注册表名
     * @return 失效数据集的sql
     */
    private String getInvalidSql(String currentTable) {
        String invalidSql = String.format(
                "SELECT * FROM %s WHERE NOT EXISTS(SELECT 1 FROM %s" +
                        " WHERE %s.%s = %s.%s AND %s.%s = '%s')",
                tableName, currentTable, tableName, MD5NAME,
                currentTable, MD5NAME, tableName, EDATENAME, MAXDATE);
        if (databaseArgs.isMultipleInput()) {
            invalidSql = invalidSql.replace(")", " AND " + TABLE_ID_NAME
                    + " = '" + databaseArgs.getDatatableId() + "')");
        }
        return invalidSql;
    }

    /**
     * 获取有效数据集的sql
     *
     * @param currentTable 当前数据集注册表名
     * @return 有效数据集的sql
     */
    private String getValidSql(String currentTable) {
        String validSql = String.format(
                "SELECT * FROM %s WHERE NOT EXISTS(SELECT 1 FROM %s" +
                        " WHERE %s.%s = %s.%s AND %s.%s = '%s')",
                currentTable, tableName, currentTable, MD5NAME,
                tableName, MD5NAME, tableName, EDATENAME, MAXDATE);
        if (databaseArgs.isMultipleInput()) {
            validSql = validSql.replace(")", " AND " + TABLE_ID_NAME
                    + " = '" + databaseArgs.getDatatableId() + "')");
        }
        return validSql;
    }
}
