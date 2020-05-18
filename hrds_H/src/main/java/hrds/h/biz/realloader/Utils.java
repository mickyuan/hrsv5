package hrds.h.biz.realloader;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.Datatable_field_info;
import hrds.commons.exception.AppSystemException;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.config.MarketConfUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static hrds.commons.utils.Constant.*;

/**
 * 一些共用的方法
 *
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class);

    static String buildCreateTableColumnTypes(MarketConf conf, boolean isDatabase) {
        List<String> additionalAttrs;
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            additionalAttrs = getAdditionalAttrs(db, conf.getDatatableId(), StoreLayerAdded.ZhuJian);
        }
        final StringBuilder columnTypes = new StringBuilder(300);
        conf.getDatatableFields().forEach(field -> {

            String fieldName = field.getField_en_name();
            columnTypes
                    .append(fieldName)
                    .append(" ")
                    .append(field.getField_type());

            String fieldLength = field.getField_length();
            //写了精度，则添加精度
            if (StringUtil.isNotBlank(fieldLength)) {
                columnTypes
                        .append("(").append(fieldLength).append(")");
            }
            //如果选择了主键，则添加主键
            if (additionalAttrs.contains(fieldName)) {
                columnTypes.append(" primary key");
            }

            columnTypes.append(",");

        });
        //把最后一个逗号给删除掉
        columnTypes.deleteCharAt(columnTypes.length() - 1);

        //如果是database类型 则类型为定长char类型，否则为string类型（默认）
        if (isDatabase) {
            String str = MarketConfUtils.DEFAULT_STRING_TYPE;
            String s = columnTypes.toString();
            s = StringUtil.replaceLast(s, str, "char(32)");
            s = StringUtil.replaceLast(s, str, "char(8)");
            s = StringUtil.replaceLast(s, str, "char(8)");
            return s;
        }
        return columnTypes.toString();


    }

    /**
     * 所有字段，以逗号隔开
     *
     * @return
     */
    static String columns(List<Datatable_field_info> fields) {
        return fields
                .stream()
                .map(Datatable_field_info::getField_en_name)
                .collect(Collectors.joining(","));
    }

    /**
     * 恢复关系型数据库的数据到上次跑批结果
     *
     * @param db
     */
    static void restoreDatabaseData(DatabaseWrapper db, String tableName, String etlDate) {
        if (db.isExistTable(tableName)) {
            db.execute(String.format("DELETE FROM %s WHERE %s = '%s'",
                    tableName, SDATENAME, etlDate));

            db.execute(String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'",
                    tableName, EDATENAME, MAXDATE, EDATENAME, etlDate));
        }
    }

    /**
     * 注册并返回MD5函数的方法名
     * 如果数据库自带MD5函数，则直接返回数据库自带函数名
     * pgsql自带 md5
     * oracle 写存储过程实现MD5函数，函数名为 HYREN_MD5
     * // TODO 未实现从某个数字开始自增
     *
     * @param db
     * @param type
     * @return
     */
    static String registerMd5Function(DatabaseWrapper db, DatabaseType type) {
        if (DatabaseType.Oracle10g.equals(type)) {
            db.execute("CREATE OR REPLACE FUNCTION HYREN_MD5(passwd IN VARCHAR2) \n" +
                    "RETURN VARCHAR2\n" +
                    "IS\n" +
                    " retval varchar2(32);\n" +
                    "BEGIN\n" +
                    " retval := utl_raw.cast_to_raw(DBMS_OBFUSCATION_TOOLKIT.MD5(INPUT_STRING => passwd));\n" +
                    " RETURN retval;\n" +
                    "END;");
            return "HYREN_MD5";
        }
        return "MD5";
    }

    static String registerAutoIncreasingFunction(DatabaseWrapper db, DatabaseType type) {
        if (DatabaseType.Oracle10g.equals(type)) {
            // TODO 实现自增序列函数并返回函数名
        }
        return "auto_id";
    }

    /**
     * 创建表
     * 如果表存在就删除掉
     */
    static void forceCreateTable(DatabaseWrapper db, String tableName, String createTableColumnTypes) {

        if (db.isExistTable(tableName)) {
            db.execute("DROP TABLE " + tableName);
        }
        String createSql = "CREATE TABLE " + tableName + " (" + createTableColumnTypes + ")";
        db.execute(createSql);
    }

    /**
     * 获取带有存储附加属性的列
     *
     * @param db          db实体
     * @param datatableId datatableId
     * @param added       dsla_StoreLayer实体的
     * @return 符合附加属性的表名集合
     */
    static List<String> getAdditionalAttrs(DatabaseWrapper db, String datatableId, StoreLayerAdded added) {
        String sql = "select dfi.field_en_name from datatable_field_info dfi join dm_column_storage dcs on dfi.datatable_field_id = dcs.datatable_field_id join data_store_layer_added dsla on dcs.dslad_id = dsla.dslad_id where dfi.datatable_id = ? and dsla.dsla_storelayer = ?";
        ResultSet resultSet = db.queryGetResultSet(sql, Long.parseLong(datatableId), added.getCode());
        List<String> addAttrCols = new ArrayList<>();
        try {
            while (resultSet.next()) {
                addAttrCols.add(resultSet.getString(1).toLowerCase());
            }
        } catch (SQLException throwables) {
            throw new AppSystemException(throwables);
        }
        return addAttrCols;
    }

    static Optional<List<String>> getFinalWorkSqls(String sqls) {
        if (StringUtil.isBlank(sqls)) {
            logger.info("无后置作业需要执行！");
            return Optional.empty();
        }
        List<String> sqlList = Arrays.stream(sqls.split(";;"))
                .filter(StringUtil::isNotBlank)
                .collect(Collectors.toList());
        if (sqlList.isEmpty()) {
            logger.info("无后置作业需要执行！");
            return Optional.empty();
        }
        return Optional.of(sqlList);
    }

    static void finalWorkWithinTrans(String sqlsJoined, Map<String, String> dbConf) {
        Optional<List<String>> OptionSqls = getFinalWorkSqls(sqlsJoined);
        if (OptionSqls.isPresent()) {
            DatabaseWrapper db = null;
            try {
                db = ConnectionTool.getDBWrapper(dbConf);
                db.beginTrans();
                for (String sql : OptionSqls.get()) {
                    db.execute(sql);
                }
                db.commit();
            } catch (Exception e) {
                if (db != null) {
                    db.rollback();
                }
                throw e;
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
    }
}
