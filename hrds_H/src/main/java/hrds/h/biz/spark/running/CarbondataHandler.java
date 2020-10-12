package hrds.h.biz.spark.running;

import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.*;

import static hrds.commons.utils.Constant.*;

public class CarbondataHandler extends Handler {
    Logger logger = LogManager.getLogger();
    private final SparkHandleArgument.CarbonArgs carbonArgs;

    CarbondataHandler(SparkSession spark, Dataset<Row> dataset,
                      SparkHandleArgument.CarbonArgs carbonArgs) {
        super(spark, dataset, carbonArgs);
        this.carbonArgs = carbonArgs;

        setDatabase();
    }

    private void setDatabase() {
        String database = carbonArgs.getDatabase();
        if(StringUtil.isBlank(database)){
            throw new AppSystemException("Can not set database to null string");
        }
        spark.catalog().setCurrentDatabase(database);

    }

    @Override
    public void insert() throws Exception {
        DataFrameWriter<Row> dataFrameWriter = dataset.write().format("carbon");
        if (carbonArgs.isOverWrite()) {
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
        String finalTable = "final_" + tableName;

        //表数据集，去除无效历史拉链，去除非本集市表的数据
        Dataset<Row> finalDF = spark.read()
                .table(tableName)
                .filter(EDATENAME + "=" + MAXDATE);
        if (carbonArgs.isMultipleInput()) {
            finalDF = finalDF.filter(TABLE_ID_NAME + "=" + carbonArgs.getDatatableId());
        }

        dataset.createOrReplaceTempView(currentTable);
        finalDF.createOrReplaceTempView(finalTable);

        /*
         * 有效数据的df，原样插入即可
         */
        Dataset<Row> validDF = spark.sql(getValidSql(currentTable, finalTable));
        validDF.write()
                .mode(SaveMode.Append)
                .insertInto(tableName);

        /*
         * 需要关链的数据df，只有一列，就是hyren的MD5值
         * 把该 df 写到最终表同数据库中,表名为 delta_hyren_${tableName}
         * 然后把最终表中的这些MD5记录的edate更新成调度日期
         */
        Dataset<Row> invalidDF = spark.sql(getInvalidSql(currentTable, finalTable));

        String deltaTable = "delta_hyren_" + tableName;
        invalidDF.createOrReplaceTempView(deltaTable);
        String sql = String.format("UPDATE %s SET (%s) = ('%s') WHERE %s in (SELECT %s FROM %s)",
                tableName, EDATENAME, carbonArgs.getEtlDate(), MD5NAME, MD5NAME, deltaTable);
        if (carbonArgs.isMultipleInput()) {
            sql = sql + " AND " + TABLE_ID_NAME + " = '" + carbonArgs.getDatatableId() + "'";
        }
        spark.sql(sql).show();
        spark.sql("DROP TABLE " + deltaTable).show();

    }


    /**
     * 获取有效数据集的sql
     *
     * @param currentTable 当前数据集注册表名
     * @return 有效数据集的sql
     */
    private String getValidSql(String currentTable, String finalTable) {
        String validSql = String.format(
                "SELECT * FROM %s WHERE NOT EXISTS(SELECT 1 FROM %s" +
                        " WHERE %s.%s = %s.%s AND %s.%s = '%s')",
                currentTable, finalTable, currentTable, MD5NAME,
                finalTable, MD5NAME, finalTable, EDATENAME, MAXDATE);
        if (carbonArgs.isMultipleInput()) {
            validSql = validSql.replace(")", " AND " + TABLE_ID_NAME
                    + " = '" + carbonArgs.getDatatableId() + "')");
        }
        return validSql;
    }

    /**
     * 获取失效数据集的sql
     *
     * @param currentTable 当前数据集注册表名
     * @return 失效数据集的sql
     */
    private String getInvalidSql(String currentTable, String finalTable) {
        String invalidSql = String.format(
                "SELECT * FROM %s WHERE NOT EXISTS(SELECT 1 FROM %s" +
                        " WHERE %s.%s = %s.%s AND %s.%s = '%s')",
                finalTable, currentTable, finalTable, MD5NAME,
                currentTable, MD5NAME, finalTable, EDATENAME, MAXDATE);
        if (carbonArgs.isMultipleInput()) {
            invalidSql = invalidSql.replace(")", " AND " + TABLE_ID_NAME
                    + " = '" + carbonArgs.getDatatableId() + "')");
        }
        return invalidSql;
    }
}
