package hrds.k.biz.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.collection.bean.DbConfBean;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

@DocClass(desc = "存储层操作工具类", author = "BY-HLL", createdate = "2020/7/15 0015 下午 04:34")
public class StorageLayerOperationTools {

    @Method(desc = "根据存储层配置Map信息获取DbConfBean", logicStep = "根据存储层配置Map信息获取DbConfBean")
    public static DbConfBean getDbConfBean(DatabaseWrapper db, long dsl_id) {
        //根据数据层获取存储层配置信息
        List<Map<String, Object>> dataStoreConf =
                SqlOperator.queryList(db, "select * from data_store_layer_attr where dsl_id = ?", dsl_id);
        //获取存储层配置Map信息
        Map<String, String> dbConfigMap = ConnectionTool.getLayerMap(dataStoreConf);
        //根据存储层配置Map信息设置 DbConfBean
        DbConfBean dbConfBean = new DbConfBean();
        dbConfBean.setDatabase_drive(dbConfigMap.get("database_driver"));
        dbConfBean.setJdbc_url(dbConfigMap.get("jdbc_url"));
        dbConfBean.setUser_name(dbConfigMap.get("user_name"));
        dbConfBean.setDatabase_pad(dbConfigMap.get("database_pwd"));
        dbConfBean.setDatabase_type(dbConfigMap.get("database_type"));
        return dbConfBean;
    }

    @Method(desc = "存储层创建数表", logicStep = "存储层创建数表")
    public static void createDataTable(DatabaseWrapper db, DbConfBean dbConfBean, String table_name) {
        //使用存储层配置自定义Bean创建存储层链接
        DatabaseWrapper dbDataConn = null;
        try {
            dbDataConn = ConnectionTool.getDBWrapper(dbConfBean);
            //创建数表
            int i = dbDataConn.ExecDDL("CREATE TABLE " + table_name + " (id int, name varchar(20))");
            if (i != 0) {
                throw new BusinessException("表已经存在! table_name: " + table_name);
            }
            //导入数据
            dbDataConn.execute("INSERT INTO " + table_name + " VALUES (1,'a'),(2,'b')");
            //提交db操作
            dbDataConn.commit();
        } catch (Exception e) {
            if (null != dbDataConn) {
                dbDataConn.rollback();
            }
            e.printStackTrace();
            throw new BusinessException("创建存储层数表发生异常!" + e.getMessage());
        } finally {
            if (null != dbDataConn) {
                dbDataConn.close();
            }
        }
    }

    @Method(desc = "清理存储层数表", logicStep = "清理存储层数表")
    public static void dropDataTable(DatabaseWrapper db, DbConfBean dbConfBean, String table_name) {
        //使用存储层配置自定义Bean创建存储层链接
        DatabaseWrapper dbDataConn = null;
        try {
            dbDataConn = ConnectionTool.getDBWrapper(dbConfBean);
            //删除数表
            int i = dbDataConn.ExecDDL("DROP TABLE IF EXISTS " + table_name);
            if (i != 0) {
                throw new BusinessException("表已经不存在! table_name: " + table_name);
            }
            //提交db操作
            dbDataConn.commit();
        } catch (Exception e) {
            if (null != dbDataConn) {
                dbDataConn.rollback();
            }
            e.printStackTrace();
            throw new BusinessException("删除存储层数表发生异常!" + e.getMessage());
        } finally {
            if (null != dbDataConn) {
                dbDataConn.close();
            }
        }
    }
}
