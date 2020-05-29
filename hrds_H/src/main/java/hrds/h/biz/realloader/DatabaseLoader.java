package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.running.SparkHandleArgument.DatabaseArgs;
import hrds.h.biz.spark.running.SparkJobRunner;

import static hrds.commons.utils.StorageTypeKey.*;

/**
 * 异配置关系型数据进行数据交互的实现
 *
 * @Author: Mick Yuan
 * @Since jdk1.8
 */
public class DatabaseLoader extends AbstractRealLoader {
    /**
     * spark 作业的配置类
     */
    private final DatabaseArgs databaseArgs = new DatabaseArgs();
    /**
     * 创建表的 列名 列类型，以逗号隔开
     */
    private final String createTableColumnTypes;

    DatabaseLoader(MarketConf conf) {
        super(conf);
        initArgs();
        createTableColumnTypes = Utils.buildCreateTableColumnTypes(conf,
                true, databaseArgs.isMultipleInput());
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
        databaseArgs.setMultipleInput(isMultipleInput);
        databaseArgs.setDatatableId(datatableId);
    }

    @Override
    public void ensureRelation() {
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(tableLayerAttrs)) {
            Utils.softCreateTable(db, tableName, createTableColumnTypes);
        }
    }

    @Override
    public void append() {
        databaseArgs.setOverWrite(false);
        SparkJobRunner.runJob(datatableId, databaseArgs);

    }

    /**
     * 1.创建临时表
     * 2.把数据导入到临时表
     * 3.删除最终表
     * 4.把临时表重命名成最终表
     */
    @Override
    public void replace() {
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(tableLayerAttrs)) {
            String replaceTempTable = tableName + "_hyren_r";
            Utils.forceCreateTable(db, replaceTempTable, createTableColumnTypes);
            databaseArgs.setOverWrite(true);
            databaseArgs.setTableName(replaceTempTable);
            SparkJobRunner.runJob(datatableId, databaseArgs);
            Utils.dropTable(db, tableName);
            Utils.renameTable(db, replaceTempTable, tableName);
        }
    }

    @Override
    public void increment() {
        databaseArgs.setIncrement(true);
        SparkJobRunner.runJob(datatableId, databaseArgs);
    }

    @Override
    public void restore() {
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(tableLayerAttrs)) {
            Utils.restoreDatabaseData(db, tableName, conf.getEtlDate(),
                    conf.getDatatableId(), conf.isMultipleInput());
        }
    }

    @Override
    public void finalWork() {
        Utils.finalWorkWithinTrans(finalSql, tableLayerAttrs);
    }


}
