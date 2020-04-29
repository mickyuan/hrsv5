package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;

import hrds.commons.codes.ProcessType;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.PropertyParaValue;
import hrds.h.biz.config.MarketConf;

import java.util.List;

import static hrds.commons.utils.Constant.*;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class SameDatabaseLoader extends AbstractRealLoader {

    private final DatabaseWrapper db;
    private final String sql;
    private final String createTableColumnTypes;
    private final String columns;
    private final String currentTableName;
    private final String validTableName;
    private final String invalidTableName;

    SameDatabaseLoader(MarketConf conf) {
        super(conf);
        db = ConnectionTool.getDBWrapper(tableLayerAttrs);
        sql = conf.getCompleteSql();
        createTableColumnTypes = Utils.buildCreateTableColumnTypes(conf.getDatatableFields(), true);
        columns = Utils.columns(conf.getDatatableFields());
        currentTableName = "curr_" + tableName;
        validTableName = "valid_" + tableName;
        invalidTableName = "invalid_" + tableName;
    }


    @Override
    public void firstLoad() {
        this.replace();
    }


    @Override
    public void append() {
        ensureTableExists("追加");
        insertData(tableName);
    }

    @Override
    public void replace() {
        forceCreateTable(tableName);
        insertData(tableName);
    }

    @Override
    public void increment() {
        ensureTableExists("增量");
        //1.创建当前表
        forceCreateTable(currentTableName);
        //2.将执行sql后的结果集插入当前表中
        insertData(currentTableName);
        //3.创建增量表1,装载有效数据
        forceCreateTable(validTableName);
        //3.创建增量表2,装载失效数据
        forceCreateTable(invalidTableName);
        //4.当前表与最终表中的有效数据做比较，执行增量算法计算出 新增的数据 并插入到增量表中，action 字段值为 I
        computeValidDataAndInsert();
        //5.当前表与最终表中的有效数据做比较，执行增量算法计算出 关链的数据 并插入到增量表中，action 字段值为 D
        computeInvalidDataAndInsert();
        //6.根据增量表中存储的拉链结果来更新最终表
        // 开链数据
        db.execute(String.format("INSERT INTO %s SELECT * FROM %s",
                tableName, validTableName));
        // 关链数据
        db.execute(String.format("UPDATE %s SET %s = %s WHERE " +
                        "HYREN_MD5_VAL IN ( SELECT HYREN_MD5_VAL FROM %s )",
                tableName, EDATENAME, conf.getEtlDate(), invalidTableName));

    }

    private void computeValidDataAndInsert() {
        String validDataSql = "INSERT INTO %s select * from %s WHERE NOT EXISTS " +
                "( SELECT 1 from %s WHERE %s.%s = %s.%s AND %s.%s = %s)";
        db.execute(String.format(validDataSql, validTableName, currentTableName, tableName, currentTableName,
                MD5NAME, tableName, MD5NAME, tableName, EDATENAME, MAXDATE));
    }

    private void computeInvalidDataAndInsert() {
        final String invalidDataSql = "INSERT INTO %s select * from %s WHERE NOT EXISTS " +
                "( SELECT 1 from %s WHERE %s.%s = %s.%s) AND %s.%s = %s";
        db.execute(String.format(invalidDataSql, invalidTableName, tableName, currentTableName,
                tableName, MD5NAME, currentTableName, MD5NAME, tableName, EDATENAME, MAXDATE));
    }

    @Override
    public void restore() {
        ensureTableExists("重追加");
        Utils.restoreDatabaseData(db, tableName, conf.getEtlDate());
    }

    @Override
    public void close() {

        if (PropertyParaValue.getBoolean("market.increment.tmptable.delete", true)) {
            dropTempTable();
        }
        if (db != null)
            db.close();
    }

    private void dropTempTable() {
        try {
            db.execute("DROP TABLE " + currentTableName);
            db.execute("DROP TABLE " + validTableName);
            db.execute("DROP TABLE " + invalidTableName);
        } catch (Exception e) {
            logger.warn("删除临时表 " + currentTableName + "," + validTableName +
                    "," + invalidTableName + " 失败");
        }
    }

    private void ensureTableExists(String action) {
        if (!db.isExistTable(tableName)) {
            throw new AppSystemException(String.format("表不存在,无法执行 %s 操作: %s",
                    action, tableName));
        }
    }

    /**
     * 创建表
     * 如果表存在就删除掉
     */
    private void forceCreateTable(String tableName, String... extraColumnTypes) {

        String createTableColumnTypesExtra = createTableColumnTypes;
        if (extraColumnTypes.length > 0) {
            createTableColumnTypesExtra += "," + String.join(",", extraColumnTypes);
        }
        if (db.isExistTable(tableName)) {
            db.execute("DROP TABLE " + tableName);
        }
        String createSql = "CREATE TABLE " + tableName + " (" + createTableColumnTypesExtra + ")";
        db.execute(createSql);
    }

    /**
     * 插入数据
     *
     * @param tableName
     */
    private void insertData(String tableName) {
        db.execute(String.format("INSERT INTO %s ( %s ) SELECT %s FROM ( %s )",
                tableName, columns, realSelectExpr(), sql));
    }

    private String lineMd5Expr(String columnsJoin) {
        return "md5(" + columnsJoin.replace(",", "||") + ")";
    }


    private List<String> getColumnSequence() {
        return new DruidParseQuerySql(sql).parseSelectAliasField();
    }

    /**
     * 返回需要从sql中需要查询的表达式
     * 包括，字段名称，自增函数，定制，hyren自定义字段等
     */
    private String realSelectExpr() {
        final List<String> colSeq = getColumnSequence();
        final StringBuilder selectExpr = new StringBuilder(120);
        //只有映射字段做MD5
        final StringBuilder md5Cols = new StringBuilder(120);
        /**
         * field.getProcess_para()
         * 1.如果是映射，则返回的是改字段对应sql中的真实查询出来的字段序号
         * 2.如果是定值，则返回的是定值的值
         */
        conf.getDatatableFields()
                .stream()
                .filter(field -> !field.getField_en_name().startsWith("hyren_"))
                .forEach(field -> {
                    String processCode = field.getField_process();
                    if (ProcessType.ZiZeng.getCode().equals(processCode)) {
                        selectExpr.append(autoIncreasingExpr());
                    } else if (ProcessType.YingShe.getCode().equals(processCode)) {
                        int mappingIndex = Integer.parseInt(field.getProcess_para());
                        selectExpr.append(colSeq.get(mappingIndex));
                        md5Cols.append(field.getField_en_name()).append(',');
                    } else if (ProcessType.DingZhi.getCode().equals(processCode)) {
                        selectExpr.append("'").append(field.getProcess_para()).append("'");
                    } else {
                        throw new AppSystemException("不支持处理方式码：" + processCode);
                    }
                    selectExpr.append(',');
                });
        //添加自定义字段
        selectExpr.append(conf.getEtlDate())
                .append(',')
                .append(MAXDATE)
                .append(',')
                .append(lineMd5Expr(md5Cols.deleteCharAt(md5Cols.length() - 1).toString()));
        return selectExpr.toString();
    }

    /**
     * 返回自增函数表达式
     */
    private String autoIncreasingExpr() {
        //TODO 假装一下这里是返回了一个自增函数表达式
        return "'increasing'";
    }
}
