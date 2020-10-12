package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.Cb_preaggregate;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.StorageTypeKey;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.running.SparkJobRunner;

import java.sql.SQLException;
import java.util.List;

import static hrds.h.biz.spark.running.SparkHandleArgument.CarbonArgs;

public class CarbondataLoader extends AbstractRealLoader {

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
//        carbonArgs.setColumns(Utils.columns(conf.getDatatableFields()));
        carbonArgs.setDatabase(tableLayerAttrs.get(StorageTypeKey.database_name));
    }

    @Override
    public void ensureRelation() {
        try (DatabaseWrapper db = getCarbonDb()) {
            createCarbonTable(db, tableName);
        }
    }

    private void createCarbonTable(DatabaseWrapper carbonDb, String tableName) {
        carbonDb.execute(String.format("CREATE TABLE IF NOT EXISTS %s ( %s ) " +
                        "STORED AS carbondata TBLPROPERTIES ('LOCAL_DICTIONARY_ENABLE'='true')",
                tableName, createTableColumnTypes));
    }

    /**
     * carbondata 就是 spark-thriftserver
     * 目前使用hive代码项
     *
     * @return db连接封装对象
     */
    private DatabaseWrapper getCarbonDb() {
        tableLayerAttrs.put(StorageTypeKey.database_type, DatabaseType.Hive.getCode());
        DatabaseWrapper dbWrapper = ConnectionTool.getDBWrapper(tableLayerAttrs);
        dbWrapper.execute("use " + carbonArgs.getDatabase());
        return dbWrapper;
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
            db.execute("DROP TABLE IF EXISTS " + replaceTempTable);
            createCarbonTable(db, replaceTempTable);
            carbonArgs.setOverWrite(false);
            carbonArgs.setTableName(replaceTempTable);
            SparkJobRunner.runJob(carbonArgs);
            db.execute("DROP TABLE IF EXISTS " + tableName);
            db.execute("ALTER TABLE " + replaceTempTable + " RENAME TO " + tableName);
        }
    }

    @Override
    public void increment() {
        carbonArgs.setIncrement(true);
        SparkJobRunner.runJob(carbonArgs);
    }

    @Override
    public void restore() throws SQLException {
        try (DatabaseWrapper db = getCarbonDb()) {
            if (Utils.hasTodayDataLimit(db, tableName, etlDate,
                    datatableId, isMultipleInput, conf.isIncrement())) {
                Utils.restoreDatabaseData(db, tableName, etlDate, datatableId,
                        isMultipleInput, conf.isIncrement());
            }
        }
    }

    @Override
    public void finalWork() {
        Utils.finalWorkWithinTrans(finalSql, tableLayerAttrs);

        try (DatabaseWrapper db = new DatabaseWrapper(); DatabaseWrapper carbonDb = getCarbonDb()) {
            List<Cb_preaggregate> cb_preaggregates = SqlOperator.queryList(db, Cb_preaggregate.class,
                    "select * from cb_preaggregate where datatable_id = " + datatableId);
            cb_preaggregates.forEach(a -> {
                String preAggregateSql = "CREATE MATERIALIZED VIEW IF NOT EXISTS "
                        + tableName + "_" + a.getAgg_name() + " AS " + a.getAgg_sql();
                carbonDb.execute(preAggregateSql);
            });
        }

    }
}