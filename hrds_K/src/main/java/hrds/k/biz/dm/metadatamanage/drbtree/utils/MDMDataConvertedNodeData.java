package hrds.k.biz.dm.metadatamanage.drbtree.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.DataSourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-数据源列表数据转化为节点数据", author = "BY-HLL", createdate = "2020/4/1 0001 下午 02:43")
public class MDMDataConvertedNodeData {

    @Method(desc = "数据管控-数据源列表DCL层转化数据存储层下的表信息为Node节点数据",
            logicStep = "数据管控-数据源列表DCL层转化数据存储层下的表信息为Node节点数据")
    @Param(name = "tableInfos", desc = "存储层下数据表信息List", range = "存储层下数据表信息List")
    @Return(desc = "存储层下数据表的Node节点数据", range = "存储层下数据表的Node节点数据")
    public static List<Map<String, Object>> conversionDCLStorageLayerTableInfos(List<Map<String, Object>> tableInfos) {
        //设置为树节点信息
        List<Map<String, Object>> storageLayerTableNodes = new ArrayList<>();
        tableInfos.forEach(tableInfo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", DataSourceType.DCL.getCode() + "_" + tableInfo.get("dsl_id") + "_" + tableInfo.get("file_id"));
            map.put("label", tableInfo.get("hyren_name"));
            map.put("parent_id", DataSourceType.DCL.getCode() + "_" + tableInfo.get("dsl_id"));
            map.put("description", "" +
                    "存储层名称：" + tableInfo.get("dsl_name") + "\n" +
                    "数据表名称：" + tableInfo.get("table_name"));
            map.put("data_layer", DataSourceType.DCL.getCode());
            map.put("table_name", tableInfo.get("table_name"));
            map.put("original_name", tableInfo.get("original_name"));
            map.put("hyren_name", tableInfo.get("hyren_name"));
            map.put("data_own_type", tableInfo.get("store_type"));
            map.put("file_id", tableInfo.get("file_id"));
            storageLayerTableNodes.add(map);
        });
        return storageLayerTableNodes;
    }

    @Method(desc = "数据管控-数据源列表DML层转化数据存储层DML下的表信息为Node节点数据",
            logicStep = "数据管控-数据源列表DML层转化数据存储层DML下的表信息为Node节点数据")
    @Param(name = "tableInfos", desc = "存储层下数据表信息List", range = "存储层下数据表信息List")
    @Return(desc = "存储层下数据表的Node节点数据", range = "存储层下数据表的Node节点数据")
    public static List<Map<String, Object>> conversionDMLStorageLayerTableInfos(List<Map<String, Object>> tableInfos) {
        //设置为树节点信息
        List<Map<String, Object>> storageLayerTableNodes = new ArrayList<>();
        tableInfos.forEach(tableInfo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", DataSourceType.DML.getCode() + "_" + tableInfo.get("dsl_id") + "_" + tableInfo.get("datatable_id"));
            map.put("label", tableInfo.get("datatable_cn_name"));
            map.put("parent_id", DataSourceType.DML.getCode() + "_" + tableInfo.get("dsl_id"));
            map.put("description", "" +
                    "存储层名称：" + tableInfo.get("dsl_name") + "\n" +
                    "数据表名称：" + tableInfo.get("datatable_en_name"));
            map.put("data_layer", DataSourceType.DML.getCode());
            map.put("table_name", tableInfo.get("datatable_en_name"));
            map.put("original_name", tableInfo.get("datatable_cn_name"));
            map.put("hyren_name", tableInfo.get("datatable_en_name"));
            map.put("data_own_type", tableInfo.get("store_type"));
            map.put("file_id", tableInfo.get("datatable_id"));
            storageLayerTableNodes.add(map);
        });
        return storageLayerTableNodes;
    }
}
