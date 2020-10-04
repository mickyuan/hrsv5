package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.StorageTypeKey;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.running.SparkJobRunner;

import static hrds.h.biz.spark.running.SparkHandleArgument.*;

public class CarbondataLoader extends AbstractRealLoader{

    private final CarbonArgs carbonArgs = new CarbonArgs();

    private final String createTableColumnTypes;

    protected CarbondataLoader(MarketConf conf) {
        super(conf);
        initArgs();
        createTableColumnTypes = Utils.buildCreateTableColumnTypes(conf, false);
        //验证啥的
        ConfigReader.getConfiguration();
    }

    private void initArgs() {
        carbonArgs.setHandleType(Store_type.HIVE);
        carbonArgs.setEtlDate(etlDate);
        carbonArgs.setTableName(tableName);
        carbonArgs.setMultipleInput(isMultipleInput);
        carbonArgs.setDatatableId(datatableId);
//        carbondataArgs.setColumns(Utils.columns(conf.getDatatableFields()));
//        carbondataArgs.setDatabase(tableLayerAttrs.get(StorageTypeKey.database_name));
    }

    @Override
    public void ensureRelation() {
        try (DatabaseWrapper db = getCarbonDb()) {
            createCarbonTable(db, tableName);
        }
    }

    private void createCarbonTable(DatabaseWrapper carbonDb, String tableName) {
        carbonDb.execute(String.format("CREATE TABLE IF NOT EXISTS %s ( %s ) " +
                "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' " +
                "STORED AS PARQUET", tableName, createTableColumnTypes));
    }

    /**
     * carbondata 就是 spark-thriftserver
     * 目前使用hive代码项
     * @return db连接封装对象
     */
    private DatabaseWrapper getCarbonDb() {
        tableLayerAttrs.put(StorageTypeKey.database_type, DatabaseType.Hive.getCode());
        return ConnectionTool.getDBWrapper(tableLayerAttrs);
    }

    @Override
    public void append() {
        carbonArgs.setOverWrite(false);
        SparkJobRunner.runJob(carbonArgs);
    }

    @Override
    public void replace() {
        try (DatabaseWrapper db = getCarbonDb()) {
            String replaceTempTable = tableName + "_hyren_r";
            Utils.dropTable(db, replaceTempTable);
            createCarbonTable(db, replaceTempTable);
            carbonArgs.setOverWrite(false);
            carbonArgs.setTableName(replaceTempTable);
            SparkJobRunner.runJob(carbonArgs);
            Utils.dropTable(db, tableName);
            Utils.renameTable(db, replaceTempTable, tableName);
        }
    }

    @Override
    public void increment() {
        carbonArgs.setIncrement(true);
        SparkJobRunner.runJob(carbonArgs);
    }
}
