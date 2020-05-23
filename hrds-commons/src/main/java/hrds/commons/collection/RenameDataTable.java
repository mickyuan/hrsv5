package hrds.commons.collection;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "重命名数据表", author = "BY-HLL", createdate = "2020/5/22 0022 下午 03:49")
public class RenameDataTable {

    @Method(desc = "根据定义的存储层重命名表",
            logicStep = "根据定义的存储层重命名表")
    @Param(name = "tableSpace", desc = "表空间", range = "String类型")
    @Param(name = "tableName", desc = "需要重命名的表名", range = "String类型")
    @Param(name = "operation_type", desc = "操作类型 remove:删除,restore:恢复", range = "String了类型")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    public static List<String> renameTableByDataLayer(String tableSpace, String tableName, String operation_type,
                                                      DatabaseWrapper db) {
        //初始化返回结果集
        List<String> dsl_id_s = new ArrayList<>();
        //获取sql中解析出来的表属于的存储实体Bean
        List<LayerBean> tableLayers = ProcessingData.getTableLayer(tableName, db);
        if (null == tableLayers) {
            throw new BusinessException("该表未在任何存储层中存在!");
        }
        //根据存储层删除对应存储层下的表
        tableLayers.forEach(tableLayer -> {
            //设置返回结果集
            dsl_id_s.add(tableLayer.getDsl_id().toString());
            renameTableByDataLayer(tableSpace, tableName, operation_type, db, tableLayer);
        });
        return dsl_id_s;
    }

    @Method(desc = "根据定义的存储层重命名表",
            logicStep = "根据定义的存储层重命名表")
    @Param(name = "tableSpace", desc = "表空间", range = "String类型")
    @Param(name = "tableName", desc = "需要重命名的表名", range = "String类型")
    @Param(name = "operation_type", desc = "操作类型 remove:删除,restore:恢复", range = "String了类型")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "intoLayerBean", desc = "LayerBean对象", range = "LayerBean对象")
    public static void renameTableByDataLayer(String tableSpace, String tableName, String operation_type,
                                              DatabaseWrapper db, LayerBean intoLayerBean) {
        //根据数据层获取数据层配置信息
        List<Map<String, Object>> dataStoreConfBean = SqlOperator.queryList(db,
                "select * from data_store_layer_attr where dsl_id = ?", intoLayerBean.getDsl_id());
        try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(dataStoreConfBean)) {
            //设置无效表名
            String invalid_table_name = Constant.DQC_INVALID_TABLE + tableName;
            //初始化重命名sql
            String alterSQL = "";
            //获取当前操作数据层的数据层类型
            Store_type store_type = Store_type.ofEnumByCode(intoLayerBean.getStore_type());
            //set_invalid:设置无效(重命名为带无效标签的表名)
            if (operation_type.equals(Constant.DM_SET_INVALID_TABLE)) {
                //TODO 需要考虑如果添加了表空间,不同类型数据库设置表空间语法不同的情况
                if (store_type == Store_type.DATABASE) {
                    alterSQL = "alter table " + tableName + " rename to " + invalid_table_name;
                } else if (store_type == Store_type.HIVE) {
                    //TODO 重命名 HIVE 层表配置暂未实现!
                } else if (store_type == Store_type.HBASE) {
                    //TODO 重命名 HBASE 层表配置暂未实现!
                } else if (store_type == Store_type.SOLR) {
                    //TODO 重命名 SOLR 层表配置暂未实现!
                } else if (store_type == Store_type.ElasticSearch) {
                    //TODO 重命名 ElasticSearch 层表配置暂未实现!
                } else if (store_type == Store_type.MONGODB) {
                    //TODO 重命名 MONGODB 层表配置暂未实现!
                } else {
                    throw new BusinessException("重命名为无效表时,未找到匹配的存储层类型!");
                }
            }
            //restore:恢复(重命名为带有效的表名)
            else if (operation_type.equals(Constant.DM_RESTORE_TABLE)) {
                //TODO 需要考虑如果添加了表空间,不同类型数据库设置表空间语法不同的情况
                if (store_type == Store_type.DATABASE) {
                    alterSQL = "alter table " + invalid_table_name + " rename to " + tableName;
                } else if (store_type == Store_type.HIVE) {
                    //TODO 重命名 HIVE 层表配置暂未实现!
                } else if (store_type == Store_type.HBASE) {
                    //TODO 重命名 HBASE 层表配置暂未实现!
                } else if (store_type == Store_type.SOLR) {
                    //TODO 重命名 SOLR 层表配置暂未实现!
                } else if (store_type == Store_type.ElasticSearch) {
                    //TODO 重命名 ElasticSearch 层表配置暂未实现!
                } else if (store_type == Store_type.MONGODB) {
                    //TODO 重命名 MONGODB 层表配置暂未实现!
                } else {
                    throw new BusinessException("重命名为有效效表时,未找到匹配的存储层类型!");
                }
            }
            //执行sql
            if (StringUtil.isBlank(alterSQL)) {
                throw new BusinessException("修改数据表表名称的SQL为空!");
            }
            //执行修改sql
            int execute = SqlOperator.execute(dbDataConn, alterSQL);
            //校验修改结果
            if (execute != 0) {
                throw new BusinessException("修改关系型数据库表失败!");
            }
            SqlOperator.commitTransaction(dbDataConn);
        } catch (Exception e) {
            throw new BusinessException("重命名表的sql执行失败!,请检查表名是否存在!");
        }
    }
}
