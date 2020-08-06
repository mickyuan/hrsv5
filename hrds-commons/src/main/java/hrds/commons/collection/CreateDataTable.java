package hrds.commons.collection;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.bean.DbConfBean;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "创建数据表", author = "BY-HLL", createdate = "2020/7/2 0002 下午 03:41")
public class CreateDataTable {

    private static final Logger logger = LogManager.getLogger();

    public static void createDataTableByStorageLayer(DatabaseWrapper db, Dq_table_info dqTableInfo,
                                                     List<Dq_table_column> dqTableColumns, long dsl_id) {
        //获取存储层信息
        LayerBean layerBean = SqlOperator.queryOneObject(db, LayerBean.class, "select * from " + Data_store_layer.TableName +
                " where dsl_id=?", dsl_id).orElseThrow(() -> (new BusinessException("获取存储层数据信息的SQL失败!")));
        //根据存储层定义创建数据表
        createDataTableByStorageLayer(db, dqTableInfo, dqTableColumns, layerBean);
    }

    public static void createDataTableByStorageLayer(DatabaseWrapper db, Dq_table_info dqTableInfo,
                                                     List<Dq_table_column> dqTableColumns, LayerBean intoLayerBean) {
        //获取存储层配置Map信息
        DbConfBean dbConfBean = ConnectionTool.getDbConfBean(db, intoLayerBean.getDsl_id());
        //使用存储层配置自定义Bean创建存储层链接
        DatabaseWrapper dbDataConn = null;
        try {
            dbDataConn = ConnectionTool.getDBWrapper(dbConfBean);
            //获取表空间
            String table_space = dqTableInfo.getTable_space();
            //获取表名
            String table_name = dqTableInfo.getTable_name();
            Store_type store_type = Store_type.ofEnumByCode(intoLayerBean.getStore_type());
            //根据存储层类型创建存储层下的表
            if (store_type == Store_type.DATABASE) {
                //根据配置的表空间创建SCHEMA
                if (StringUtil.isNotBlank(table_space)) {
                    //TODO 需要考虑不同数据库创建SCHEMA的语法不一样情况
                    int i = dbDataConn.execute("CREATE SCHEMA IF NOT EXISTS " + table_space);
                    if (i != 0) {
                        throw new BusinessException("创建表空间失败! table_space: " + table_space);
                    }
                }
                //如果数据库类型是oarcle的情况下,判断表名长度不能大于30
                DatabaseType databaseType = DatabaseType.ofEnumByCode(dbConfBean.getDatabase_type());
                if (databaseType == DatabaseType.Oracle10g || databaseType == DatabaseType.Oracle9i) {
                    if (table_name.length() > 30) {
                        throw new BusinessException("oracle数据库下表名长度不能超过30位! table_name: " + table_name);
                    }
                }
                //查看数据表是否在存储层中存在
                boolean tableIsExists;
                if (StringUtil.isNotBlank(table_space)) {
                    tableIsExists = dbDataConn.isExistTable(table_space + "." + table_name);
                } else {
                    tableIsExists = dbDataConn.isExistTable(table_name);
                }
                if (tableIsExists) {
                    throw new BusinessException("待创建的表在存储层中已经存在! table_name: " + table_name);
                }
                //根据表信息和字段信息设置建表语句
                StringBuilder sql = new StringBuilder();
                sql.append("CREATE TABLE IF NOT EXISTS");
                if (StringUtil.isNotBlank(table_space)) {
                    sql.append(" ").append(table_space).append(".");
                }
                sql.append(" ").append(table_name);
                sql.append(" (");
                //主键字段信息
                List<String> pk_column_s = new ArrayList<>();
                for (Dq_table_column dqTableColumn : dqTableColumns) {
                    //获取字段的附加信息
                    List<Map<String, Object>> dcol_info_s = SqlOperator.queryList(db,
                            "SELECT * FROM " + Dcol_relation_store.TableName + " dcol_rs" +
                                    " JOIN " + Data_store_layer_added.TableName + " dsl_add ON dcol_rs" + ".dslad_id=dsl_add.dslad_id " +
                                    " WHERE col_id=?", dqTableColumn.getField_id());
                    //设置主键信息
                    for (Map<String, Object> dcol_info : dcol_info_s) {
                        StoreLayerAdded storeLayerAdded = StoreLayerAdded.ofEnumByCode(dcol_info.get("dsla_storelayer").toString());
                        if (storeLayerAdded == StoreLayerAdded.ZhuJian) {
                            pk_column_s.add(dqTableColumn.getColumn_name());
                        }
                    }
                    //字段名
                    String table_column = dqTableColumn.getColumn_name();
                    //字段类型
                    String column_type = dqTableColumn.getColumn_type();
                    //字段长度
                    String column_length = dqTableColumn.getColumn_length();
                    //设置建表语句的字段信息
                    sql.append(table_column).append(Constant.SPACE).append(column_type);
                    if (!StringUtil.isEmpty(column_length) && !column_type.equals("int")
                            && !column_type.equals("boolean")) {
                        sql.append("(").append(column_length).append(")");
                    }
                    //是否可为空标识
                    IsFlag is_null = IsFlag.ofEnumByCode(dqTableColumn.getIs_null());
                    if (is_null == IsFlag.Shi) {
                        sql.append(Constant.SPACE).append("NULL");
                    } else if (is_null == IsFlag.Fou) {
                        sql.append(Constant.SPACE).append("NOT NULL");
                    } else {
                        throw new BusinessException("字段: column_name=" + table_column + " 的是否标记信息不合法!");
                    }
                    //拼接字段分隔 ","
                    sql.append(",");
                }
                //根据字段选择主键标记设置建表语句
                if (!pk_column_s.isEmpty()) {
                    sql.append("CONSTRAINT").append(Constant.SPACE);
                    sql.append(dqTableInfo.getTable_name()).append("_PK").append(Constant.SPACE);
                    sql.append("PRIMARY KEY(").append(String.join(",", pk_column_s)).append(")");
                    sql.append(",");
                }
                //删除最后一个 ","
                sql.deleteCharAt(sql.length() - 1);
                //拼接结束的 ")"
                sql.append(")");
                //执行创建语句
                logger.info("执行创建语句,SQL内容：" + sql);
                int i = dbDataConn.ExecDDL(String.valueOf(sql));
                if (i != 0) {
                    logger.error("指定存储层创建表失败! table_name: " + table_name);
                    throw new BusinessException("表已经存在! table_name: " + table_name);
                }
                //提交db操作
                dbDataConn.commit();
                logger.info("指定存储层创建表成功! table_name: " + table_name);
            } else if (store_type == Store_type.HIVE) {
                //TODO 暂不支持
            } else if (store_type == Store_type.HBASE) {
                //TODO 暂不支持
            } else if (store_type == Store_type.SOLR) {
                //TODO 暂不支持
            } else if (store_type == Store_type.ElasticSearch) {
                //TODO 暂不支持
            } else if (store_type == Store_type.MONGODB) {
                //TODO 暂不支持
            } else {
                throw new BusinessException("不支持的存储层类型!" + store_type.getValue());
            }
        } catch (Exception e) {
            if (null != dbDataConn) {
                dbDataConn.rollback();
                logger.info("创建表时发生异常,回滚此次存储层的db操作!");
            }
            e.printStackTrace();
            throw new BusinessException("创建存储层数表发生异常!" + e.getMessage());
        } finally {
            if (null != dbDataConn) {
                dbDataConn.close();
                logger.info("关闭存储层db连接成功!");
            }
        }
    }
}
