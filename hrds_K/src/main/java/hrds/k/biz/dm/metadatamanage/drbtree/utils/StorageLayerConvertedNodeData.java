package hrds.k.biz.dm.metadatamanage.drbtree.utils;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.DataSourceType;
import hrds.commons.entity.Data_store_layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageLayerConvertedNodeData {

    @Method(desc = "数据管控-数据源列表转化DCL数据存储层信息为Node节点数据",
            logicStep = "数据管控-数据源列表转化DCL数据存储层信息为Node节点数据")
    @Param(name = "dataStorageLayers", desc = "数据源列表下数据存储层信息List", range = "数据源列表下数据存储层信息List")
    @Param(name = "dataSourceType", desc = "DataSourceType对象", range = "DataSourceType对象")
    @Return(desc = "存储层信息的Node节点数据", range = "存储层信息的Node节点数据")
    public static List<Map<String, Object>> conversionStorageLayers(List<Data_store_layer> dataStorageLayers,
                                                                    DataSourceType dataSourceType) {
        //设置为树节点信息
        List<Map<String, Object>> dataStorageLayerNodes = new ArrayList<>();
        dataStorageLayers.forEach(data_store_layer -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dataSourceType.getCode() + "_" + data_store_layer.getDsl_id());
            map.put("label", data_store_layer.getDsl_name());
            map.put("parent_id", dataSourceType.getCode());
            map.put("description", data_store_layer.getDsl_remark());
            map.put("data_layer", dataSourceType.getCode());
            dataStorageLayerNodes.add(map);
        });
        return dataStorageLayerNodes;
    }
}
