package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.running.SparkHandleArgument.DatabaseArgs;
import hrds.h.biz.spark.running.SparkJobRunner;

import static hrds.commons.utils.StorageTypeKey.*;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class DatabaseLoader extends AbstractRealLoader {

    private final DatabaseArgs databaseArgs = new DatabaseArgs();

    DatabaseLoader(MarketConf conf) {
        super(conf);
        initArgs();
    }

    private void initArgs() {
        databaseArgs.setHandleType(Store_type.DATABASE);
        databaseArgs.setEtlDate(etlDate);
        databaseArgs.setTableName(tableName);
        databaseArgs.setDriver(tableLayerAttrs.get(database_driver));
        databaseArgs.setUrl(tableLayerAttrs.get(jdbc_url));
        databaseArgs.setUser(tableLayerAttrs.get(user_name));
        databaseArgs.setPassword(tableLayerAttrs.get(database_pwd));
        databaseArgs.setDatabaseType(tableLayerAttrs.get(database_type));
    }

    @Override
    public void firstLoad() {
        //手动创建表，可强制指定表类型，spark无法实现非spark支持的类型指定
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(tableLayerAttrs)) {
            String createTableColumnTypes =
                    Utils.buildCreateTableColumnTypes(conf, true);
            Utils.forceCreateTable(db, tableName, createTableColumnTypes);
        }
        databaseArgs.setOverWrite(true);
        SparkJobRunner.runJob(datatableId, databaseArgs);

    }

    @Override
    public void append() {
        databaseArgs.setOverWrite(false);
        SparkJobRunner.runJob(datatableId, databaseArgs);

    }

    @Override
    public void replace() {
        databaseArgs.setOverWrite(true);
        SparkJobRunner.runJob(datatableId, databaseArgs);
    }

    @Override
    public void increment() {
        databaseArgs.setIncrement(true);
        SparkJobRunner.runJob(datatableId, databaseArgs);
    }

    @Override
    public void restore() {
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(tableLayerAttrs)) {
            Utils.restoreDatabaseData(db, tableName, conf.getEtlDate());
        }
    }

}
