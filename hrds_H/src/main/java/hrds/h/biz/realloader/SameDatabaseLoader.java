package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;

import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.h.biz.config.MarketConf;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class SameDatabaseLoader extends AbstractRealLoader {

    private DatabaseWrapper db;
    private String sql;
    private String createTableColumnTypes;
    private String tableName;

    SameDatabaseLoader(MarketConf conf) {
        super(conf);
        db = ConnectionTool.getDBWrapper(tableLayerAttrs);
        sql = conf.getCompleteSql();
        tableName = conf.getTableName();
        createTableColumnTypes = buildCreateTableColumnTypes();
    }


    @Override
    public void firstLoadData() {
        replaceData();
    }


    @Override
    public void appendData() {
        ensureTableExists("追加");
        insertData();
    }

    @Override
    public void replaceData() {
        forceCreateTable();
        insertData();
    }

    @Override
    public void IncrementData() {
        ensureTableExists("增量");

    }

    @Override
    public void reAppendData() {
        ensureTableExists("重追加");
        restoreData();
        insertData();
    }

    @Override
    public void close() {
        if (db != null)
            db.close();
    }

    private void ensureTableExists(String action) {
        if (!db.isExistTable(tableName)) {
            throw new AppSystemException(String.format("表不存在,无法执行 %s 操作: %s", action, tableName));
        }
    }

    /**
     * 创建表
     * 如果表存在就删除掉
     */
    private void forceCreateTable() {

        if (db.isExistTable(tableName)) {
            db.execute("DROP TABLE " + tableName);
        }
        String createSql = "CREATE TABLE " + tableName + " (" + createTableColumnTypes + ")";
        db.execute(createSql);
    }

    private void insertData() {
        db.execute("INSERT INTO " + tableName + " SELECT * FROM (" + sql + ")");
    }

    private void restoreData() {
        db.execute("DELETE FROM " + tableName + " WHERE " + Constant.SDATENAME
                + " = " + conf.getEtlData());
        db.execute("UPDATE " + tableName + " SET " + Constant.EDATENAME + " = "
                + Constant.MAXDATE + " WHERE " + Constant.EDATENAME + " = " + conf.getEtlData());
    }

}
