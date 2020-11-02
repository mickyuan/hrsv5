package hrds.h.biz.realloader;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.StorageTypeKey;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.running.SparkHandleArgument.HiveArgs;
import hrds.h.biz.spark.running.SparkJobRunner;

import java.sql.SQLException;

import static hrds.commons.utils.Constant.*;


/**
 * 数据入hive实现
 */
public class HiveLoader extends AbstractRealLoader {

    /**
     * spark 作业的配置类
     */
    private final HiveArgs hiveArgs = new HiveArgs();
    /**
     * 创建表的 列名 列类型，以逗号隔开
     */
    private final String createTableColumnTypes;

    protected HiveLoader(MarketConf conf) {
        super(conf);
        initArgs();
        createTableColumnTypes = Utils.buildCreateTableColumnTypes(conf, false);
        tableLayerAttrs.put(StorageTypeKey.database_type, DatabaseType.Hive.getCode());
        //验证啥的
        ConfigReader.getConfiguration();
    }

    private void initArgs() {
        hiveArgs.setHandleType(Store_type.HIVE);
        hiveArgs.setEtlDate(etlDate);
        hiveArgs.setTableName(tableName);
        hiveArgs.setMultipleInput(isMultipleInput);
        hiveArgs.setDatatableId(datatableId);
        hiveArgs.setColumns(Utils.columns(conf.getDatatableFields()));
        hiveArgs.setDatabase(tableLayerAttrs.get(StorageTypeKey.database_name));
    }

    @Override
    public void ensureRelation() {
        try (DatabaseWrapper db = getHiveDb()) {
            createHiveTable(db, tableName);
        }
    }

    @Override
    public void append() {
        hiveArgs.setOverWrite(false);
        SparkJobRunner.runJob(hiveArgs);
    }

    @Override
    public void replace() {
        try (DatabaseWrapper db = getHiveDb()) {
            String replaceTempTable = tableName + "_hyren_r";
            Utils.dropTable(db, replaceTempTable);
            createHiveTable(db, replaceTempTable);
            hiveArgs.setOverWrite(false);
            hiveArgs.setTableName(replaceTempTable);
            SparkJobRunner.runJob(hiveArgs);
            Utils.dropTable(db, tableName);
            Utils.renameTable(db, replaceTempTable, tableName);
        }
    }

    @Override
    public void increment() {
        hiveArgs.setIncrement(true);
        SparkJobRunner.runJob(hiveArgs);
    }

    private DatabaseWrapper getHiveDb() {
        return ConnectionTool.getDBWrapper(tableLayerAttrs);
    }

    private void createHiveTable(DatabaseWrapper hiveDb, String tableName) {
        hiveDb.execute(String.format("CREATE TABLE IF NOT EXISTS %s ( %s ) " +
                "STORED AS PARQUET", tableName, createTableColumnTypes));
    }

    @Override
    public void restore() throws SQLException {
        try (DatabaseWrapper db = getHiveDb()) {
            if (Utils.hasTodayDataLimit(db, tableName, etlDate,
                    datatableId, isMultipleInput, conf.isIncrement())) {
                String after = "CASE " + EDATENAME + " WHEN '" + etlDate + "' THEN '" + MAXDATE + "' ELSE "
                        + EDATENAME + " END " + EDATENAME;
                String join = StringUtil.replace(hiveArgs.getColumns(), EDATENAME, after);
                db.execute("DROP TABLE IF EXISTS "+tableName + "_restore");
                String restoreSql = "CREATE TABLE " + tableName + "_restore AS SELECT  " + join + " FROM " + tableName + " WHERE "
                        + SDATENAME + "<>'" + etlDate + "'";
                db.execute(restoreSql);
                db.execute("DROP TABLE IF EXISTS " + tableName);
                db.execute("ALTER TABLE " + tableName + "_restore RENAME TO " + tableName);
            }
        }
    }

    @Override
    public void finalWork() {
        try (DatabaseWrapper db = getHiveDb()) {
            Utils.computeStatistics(db, tableName);
            Utils.finalWorkWithoutTrans(finalSql, db);
        }
    }
}
