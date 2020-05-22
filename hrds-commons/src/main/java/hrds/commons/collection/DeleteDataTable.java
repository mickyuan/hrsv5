package hrds.commons.collection;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

@DocClass(desc = "删除数据类", author = "BY-HLL", createdate = "2020/5/22 0022 下午 03:18")
public class DeleteDataTable {

    @Method(desc = "根据定义的存储层删除表",
            logicStep = "根据定义的存储层删除表")
    @Param(name = "table_name", desc = "需要删除的表名", range = "String类型")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    public void dropTableByDataLayer(String table_name, DatabaseWrapper db) {
        dropTableByDataLayer(table_name, db, null);
    }

    @Method(desc = "根据定义的存储层删除表",
            logicStep = "根据定义的存储层删除表")
    @Param(name = "sql", desc = "查询数据的sql", range = "String类型")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "intoLayerBean", desc = "LayerBean对象", range = "LayerBean对象")
    public void dropTableByDataLayer(String table_name, DatabaseWrapper db, LayerBean intoLayerBean) {
        //获取sql中解析出来的表属于的存储实体Bean
        List<LayerBean> tableLayers = ProcessingData.getTableLayer(table_name, db);
        if (null == tableLayers) {
            throw new BusinessException("该表未在任何存储层中存在!");
        }
        //根据存储层删除对应存储层下的表
        tableLayers.forEach(tableLayer -> {
            List<Map<String, Object>> dataStoreConfBean = SqlOperator.queryList(db,
                    "select * from data_store_layer_attr where dsl_id = ?", tableLayer.getDsl_id());
            try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(dataStoreConfBean)) {
                SqlOperator.execute(dbDataConn, "drop table " + table_name);
                SqlOperator.commitTransaction(dbDataConn);
            } catch (Exception e) {
                throw new AppSystemException("系统不支持该数据库类型", e);
            }
        });
    }
}
