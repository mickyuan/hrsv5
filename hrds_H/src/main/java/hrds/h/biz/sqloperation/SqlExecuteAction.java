package hrds.h.biz.sqloperation;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.exception.internal.RawlayerRuntimeException;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.collection.bean.DbConfBean;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Data_store_layer_attr;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.StorageTypeKey;
import java.util.List;
import java.util.Map;

@DocClass(desc = "SQL执行操作服务类", author = "Mr.Lee", createdate = "2020-11-16 10:32")
public class SqlExecuteAction extends BaseAction {

    @Method(desc = "执行提供的SQL", logicStep = "" +
            "1: 根据提供的存储层名称查询DB连接信息" +
            "2: 根据DB连接信息获取对应的DB" +
            "3: 执行输入的SQL信息" +
            "4: 如果异常,则返回错误信息,反正返回正常")
    @Param(name = "sql", desc = "要执行的SQL语句", range = "执行的SQL信息,不能为空")
    @Param(name = "storageType", desc = "存储层名称", range = "需要执行在那个存储层下,不能为空")
    public void sqlExecute(String sql, String storageType) {

        Validator.notBlank(sql, "执行SQL不能为空");
        Validator.notBlank(storageType, "存储层名称不能为空");

//        1: 根据提供的存储层名称查询DB连接信息
        long countNum = Dbo.queryNumber("SELECT COUNT(1) FROM " + Data_store_layer.TableName + " Where dsl_name = ? ",
                storageType).orElseThrow(() -> new BusinessException("SQL查询异常"));
        if (countNum == 0) {
            throw new BusinessException("储存层名称(" + storageType + "),不存在");
        }
        List<Map<String, Object>> databaseInfo = Dbo.queryList("SELECT t2.storage_property_key,t2.storage_property_val FROM  " +
                "" + Data_store_layer.TableName
                + " t1 JOIN "
                + Data_store_layer_attr.TableName
                + " t2 on t1.dsl_id = t2.dsl_id where t1.dsl_name = ?", storageType);

        //2: 根据DB连接信息获取对应的DB
        DbConfBean confBean = new DbConfBean();
        databaseInfo.forEach(item -> {
            item.forEach((k, v) -> {
                //数据库连接Driver
                if (v.equals(StorageTypeKey.database_driver)) {
                    confBean.setDatabase_drive(item.get("storage_property_val").toString());
                }
                //数据库连接 JDBC_URL
                if (v.equals(StorageTypeKey.jdbc_url)) {
                    confBean.setJdbc_url(item.get("storage_property_val").toString());
                }
                //数据库连接端口
                if (v.equals(StorageTypeKey.user_name)) {
                    confBean.setUser_name(item.get("storage_property_val").toString());
                }
                //数据库连接密码
                if (v.equals(StorageTypeKey.database_pwd)) {
                    confBean.setDatabase_pad(item.get("storage_property_val").toString());
                }
                //数据库连接密码
                if (v.equals(StorageTypeKey.database_pwd)) {
                    confBean.setDatabase_pad(item.get("storage_property_val").toString());
                }
                //数据库连接类型
                if (v.equals(StorageTypeKey.database_type)) {
                    confBean.setDatabase_type(item.get("storage_property_val").toString());
                }
                //数据库连接名称
                if (v.equals(StorageTypeKey.database_name)) {
                    confBean.setDatabase_name(item.get("storage_property_val").toString());
                }
            });
        });
//        3: 执行输入的SQL信息
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(confBean)) {
            db.execute(sql);
        } catch (Exception e) {
            throw new BusinessException(((RawlayerRuntimeException) e).getCauseMessage());
        }
    }
}
