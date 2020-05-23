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

    @Method(desc = "删除存储层下的数据表",
            logicStep = "删除存储层下的数据表")
    @Param(name = "tableName", desc = "查询数据的sql", range = "String类型")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    public static void dropTableByDataLayer(String tableName, DatabaseWrapper db) {
        //获取sql中解析出来的表属于的存储实体Bean
        List<LayerBean> tableLayers = ProcessingData.getTableLayer(tableName, db);
        if (null == tableLayers) {
            throw new BusinessException("该表未在任何存储层中存在!");
        }
        //根据存储层删除对应存储层下的表
        tableLayers.forEach(tableLayer -> dropTableByDataLayer(tableName, db, tableLayer));
    }

    @Method(desc = "根据存储层id删除表",
            logicStep = "根据存储层id删除表")
    @Param(name = "tableName", desc = "查询数据的sql", range = "String类型")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "dsl_id", desc = "存储层配置id", range = "long类型")
    public static void dropTableByDataLayer(String tableName, DatabaseWrapper db, String dsl_id) {
        LayerBean intoLayerBean = new LayerBean();
        intoLayerBean.setDsl_id(dsl_id);
        dropTableByDataLayer(tableName, db, intoLayerBean);
    }

    @Method(desc = "根据定义的存储层删除表",
            logicStep = "根据定义的存储层删除表")
    @Param(name = "tableName", desc = "查询数据的sql", range = "String类型")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "intoLayerBean", desc = "LayerBean对象", range = "LayerBean对象")
    public static void dropTableByDataLayer(String tableName, DatabaseWrapper db, LayerBean intoLayerBean) {
        List<Map<String, Object>> dataStoreConfBean = SqlOperator.queryList(db,
                "select * from data_store_layer_attr where dsl_id = ?", intoLayerBean.getDsl_id());
        try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(dataStoreConfBean)) {
            SqlOperator.execute(dbDataConn, "drop table " + tableName);
            SqlOperator.commitTransaction(dbDataConn);
        } catch (Exception e) {
            throw new AppSystemException("删除表的sql执行失败!,请检查表名是否存在!", e);
        }
    }
}
