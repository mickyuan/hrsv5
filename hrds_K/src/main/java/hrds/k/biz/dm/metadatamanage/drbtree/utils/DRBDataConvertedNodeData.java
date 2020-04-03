package hrds.k.biz.dm.metadatamanage.drbtree.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.DataSourceType;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-数据回收站数据转化为节点数据", author = "BY-HLL", createdate = "2020/3/30 0030 上午 10:04")
public class DRBDataConvertedNodeData {

    @Method(desc = "数据管控-数据回收站转化数据存储层信息为Node节点数据",
            logicStep = "数据管控-数据回收站转化数据存储层信息为Node节点数据")
    @Param(name = "dataStorageLayers", desc = "数据源列表下数据存储层信息List", range = "数据源列表下数据存储层信息List")
    @Return(desc = "存储层信息的Node节点数据", range = "存储层信息的Node节点数据")
    public static List<Map<String, Object>> conversionDCLDataStorageLayers(List<Data_store_layer> dataStorageLayers) {
        //设置为树节点信息
        List<Map<String, Object>> dataStorageLayerNodes = new ArrayList<>();
        dataStorageLayers.forEach(data_store_layer -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", data_store_layer.getDsl_id());
            map.put("label", data_store_layer.getDsl_name());
            map.put("parent_id", DataSourceType.DCL.getCode());
            map.put("description", data_store_layer.getDsl_remark());
            map.put("data_layer", DataSourceType.DCL.getCode());
            map.put("data_own_type", data_store_layer.getStore_type());
            dataStorageLayerNodes.add(map);
        });
        return dataStorageLayerNodes;
    }

    @Method(desc = "转化DCL层批量数据下数据源数据为Node节点数据",
            logicStep = "转化DCL层批量数据下数据源数据为Node节点数据")
    @Param(name = "tableInfos", desc = "贴源层批量数据下数据源信息", range = "贴源层批量数据下数据源信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> conversionDCLStorageLayerTableInfos(List<Map<String, Object>> dclTableInfos) {
        //设置为树节点信息
        List<Map<String, Object>> dclDataNodes = new ArrayList<>();
        dclTableInfos.forEach(table_info -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", table_info.get("failure_table_id"));
            map.put("label", table_info.get("table_cn_name"));
            map.put("parent_id", DataSourceType.DCL.getCode());
            map.put("description", table_info.get("table_cn_name"));
            map.put("data_layer", DataSourceType.DCL.getCode());
            map.put("data_own_type", table_info.get("store_type"));
            map.put("file_id", table_info.get("failure_table_id"));
            dclDataNodes.add(map);
        });
        return dclDataNodes;
    }
}
