package hrds.k.biz.dm.metadatamanage.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.commons.codes.Store_type;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.k.biz.dm.metadatamanage.query.MDMDataQuery;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-数据层表操作类", author = "BY-HLL", createdate = "2020/4/2 0002 下午 05:44")
public class DataLayerTableOperation {

    @Method(desc = "根据数据表信息重命名对应存储层表", logicStep = "根据数据表信息重命名对应存储层表")
    @Param(name = "dsr", desc = "Data_store_reg实体", range = "Data_store_reg实体")
    @Param(name = "operation_type", desc = "操作类型 remove:删除,restore:恢复", range = "String了类型")
    public static void renameDCLDataLayerTable(Data_store_reg dsr, String operation_type) {
        //根据 Data_store_reg 查询表的存储层信息,然后重命名(一张表存在多个存储层)
        String tableName = dsr.getHyren_name();
        String invalid_table_name = Constant.DQC_INVALID_TABLE + tableName + Constant._HYREN;
        List<Data_store_layer> tableStorageLayers = MDMDataQuery.getTableStorageLayers(dsr);
        tableStorageLayers.forEach(data_store_layer -> {
            Store_type store_type = Store_type.ofEnumByCode(data_store_layer.getStore_type());
            //关系型数据
            if (store_type == Store_type.DATABASE) {
                //获取存储层链接配置信息
                List<Map<String, Object>> attrList = MDMDataQuery.getDataStoreLayerAttrList(data_store_layer);
                //判断操作类型 remove:重命名为带无效标签的表名,restore:恢复
                //TODO 表空间默认设置为"" 修改表名
                if (operation_type.equals(Constant.DM_SET_INVALID_TABLE)) {
                    //set_invalid:设置无效(重命名为带无效标签的表名)
                    MDMDataQuery.dataBaseTableRename(attrList, "", tableName, invalid_table_name);
                } else if (operation_type.equals(Constant.DM_RESTORE_TABLE)) {
                    //restore:恢复
                    MDMDataQuery.dataBaseTableRename(attrList, "", invalid_table_name, tableName);
                }
            }
            //HIVE
            else if (store_type == Store_type.HIVE) {
                throw new BusinessException("HIVE 层配置暂未实现!");
            }
            //HBASE
            else if (store_type == Store_type.HBASE) {
                throw new BusinessException("HBASE 层配置暂未实现!");
            }
            //SOLR
            else if (store_type == Store_type.SOLR) {
                throw new BusinessException("SOLR 层配置暂未实现!");
            }
            //ElasticSearch
            else if (store_type == Store_type.ElasticSearch) {
                throw new BusinessException("ElasticSearch 层配置暂未实现!");
            }
            //MONGODB
            else if (store_type == Store_type.MONGODB) {
                throw new BusinessException("MONGODB 层配置暂未实现!");
            }
            //未找到匹配的存储层类型
            else {
                throw new BusinessException("未找到匹配的存储层类型!");
            }
        });
    }


    @Method(desc = "彻底删除各存储层下的表", logicStep = "彻底删除各存储层下的表")
    @Param(name = "dsr", desc = "Data_store_reg实体", range = "Data_store_reg实体")
    public static void removeDCLDataLayerTable(Data_store_reg dsr) {
        //根据 Data_store_reg 查询表的存储层信息,然后删除(一张表存在多个存储层)
        String invalid_table_name = Constant.DQC_INVALID_TABLE + dsr.getHyren_name() + Constant._HYREN;
        List<Data_store_layer> tableStorageLayers = MDMDataQuery.getTableStorageLayers(dsr);
        tableStorageLayers.forEach(data_store_layer -> {
            Store_type store_type = Store_type.ofEnumByCode(data_store_layer.getStore_type());
            //关系型数据
            if (store_type == Store_type.DATABASE) {
                //获取存储层链接配置信息
                List<Map<String, Object>> attrList = MDMDataQuery.getDataStoreLayerAttrList(data_store_layer);
                //TODO 表空间默认设置为"" 删除表
                MDMDataQuery.dataBaseTableRemove(attrList, "", invalid_table_name);
            }
            //HIVE
            else if (store_type == Store_type.HIVE) {
                throw new BusinessException("HIVE 层配置暂未实现!");
            }
            //HBASE
            else if (store_type == Store_type.HBASE) {
                throw new BusinessException("HBASE 层配置暂未实现!");
            }
            //SOLR
            else if (store_type == Store_type.SOLR) {
                throw new BusinessException("SOLR 层配置暂未实现!");
            }
            //ElasticSearch
            else if (store_type == Store_type.ElasticSearch) {
                throw new BusinessException("ElasticSearch 层配置暂未实现!");
            }
            //MONGODB
            else if (store_type == Store_type.MONGODB) {
                throw new BusinessException("MONGODB 层配置暂未实现!");
            }
            //未找到匹配的存储层类型
            else {
                throw new BusinessException("未找到匹配的存储层类型!");
            }
        });
    }
}
