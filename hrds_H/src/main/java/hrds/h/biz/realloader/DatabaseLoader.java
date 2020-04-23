package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.running.SparkHandleArgument.DatabaseArgs;
import hrds.h.biz.spark.running.SparkJobRunner;

import static hrds.commons.codes.StorageTypeKey.*;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class DatabaseLoader extends AbstractRealLoader {

    private final DatabaseArgs databaseArgs = new DatabaseArgs(Store_type.DATABASE);
//    private

    DatabaseLoader(MarketConf conf) {
        super(conf);
        initArgs();
    }

    private void initArgs() {
        databaseArgs.setTableName(conf.getTableName());
        databaseArgs.setDriver(tableLayerAttrs.get(database_driver));
        databaseArgs.setUrl(tableLayerAttrs.get(jdbc_url));
        databaseArgs.setUser(tableLayerAttrs.get(user_name));
        databaseArgs.setPassword(tableLayerAttrs.get(database_pwd));
        databaseArgs.setDatabaseType(tableLayerAttrs.get(database_type));
        databaseArgs.setCreateTableColumnTypes(Utils.buildCreateTableColumnTypes(
                conf.getDatatableFields(), true));
    }

    @Override
    public void firstLoad() {
        databaseArgs.setOverWrite(true);
        SparkJobRunner.runJob(conf.getDatatableId(), databaseArgs);

    }

    @Override
    public void append() {
        databaseArgs.setOverWrite(false);
        SparkJobRunner.runJob(conf.getDatatableId(), databaseArgs);

    }

    @Override
    public void replace() {
        databaseArgs.setOverWrite(true);
        SparkJobRunner.runJob(conf.getDatatableId(), databaseArgs);
    }

    @Override
    public void increment() {
        databaseArgs.setIncrement(true);
        SparkJobRunner.runJob(conf.getDatatableId(), databaseArgs);
    }

    @Override
    public void reappend() {
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(tableLayerAttrs)) {
            Utils.restoreDatabaseData(db,tableName,conf.getEtlDate());
        }
        databaseArgs.setOverWrite(false);
        SparkJobRunner.runJob(conf.getDatatableId(), databaseArgs);
    }

    @Override
    public void close() {

    }
}
