package hrds.commons.collection;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.bean.DbConfBean;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Dq_table_column;
import hrds.commons.entity.Dq_table_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        //根据数据层获取存储层配置信息
        List<Map<String, Object>> dataStoreConfBean = SqlOperator.queryList(db,
                "select * from data_store_layer_attr where dsl_id = ?", intoLayerBean.getDsl_id());
        //获取存储层配置Map信息
        Map<String, String> dbConfigMap = ConnectionTool.getLayerMap(dataStoreConfBean);
        //根据存储层配置Map信息设置 DbConfBean
        DbConfBean dbConfBean = new DbConfBean();
        dbConfBean.setDatabase_drive(dbConfigMap.get("database_driver"));
        dbConfBean.setJdbc_url(dbConfigMap.get("jdbc_url"));
        dbConfBean.setUser_name(dbConfigMap.get("user_name"));
        dbConfBean.setDatabase_pad(dbConfigMap.get("database_pwd"));
        dbConfBean.setDatabase_type(dbConfigMap.get("database_type"));
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
                for (Dq_table_column dqTableColumn : dqTableColumns) {
                    //字段
                    String column_name = dqTableColumn.getColumn_name();
                    //字段类型
                    String column_type = dqTableColumn.getColumn_type();
                    //字段长度
                    String column_length = dqTableColumn.getColumn_length();
                    sql.append(column_name).append(Constant.SPACE).append(column_type);
                    if (!StringUtil.isEmpty(column_length) && !column_type.equals("int")
                            && !column_type.equals("boolean")) {
                        sql.append("(").append(column_length).append(")").append(",");
                    } else {
                        sql.append(",");
                    }
                }
                sql.deleteCharAt(sql.length() - 1);
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
