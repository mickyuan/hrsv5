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

@DocClass(desc = "数据管控-数据回收站数据转化为节点数据", author = "BY-HLL", createdate = "2020/3/30 0030 上午 10:04")
public class DRBDataConvertedNodeData {

    @Method(desc = "转化DCL层批量数据下数据源数据为Node节点数据",
            logicStep = "转化DCL层批量数据下数据源数据为Node节点数据")
    @Param(name = "tableInfos", desc = "贴源层批量数据下数据源信息", range = "贴源层批量数据下数据源信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> conversionStorageLayerTableInfos(List<Map<String, Object>> tableInfos,
                                                                             DataSourceType dataSourceType) {
        //设置为树节点信息
        List<Map<String, Object>> dataNodes = new ArrayList<>();
        tableInfos.forEach(table_info -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", table_info.get("failure_table_id"));
            map.put("label", table_info.get("table_en_name"));
            map.put("parent_id", dataSourceType.getCode() + "_" + table_info.get("dsl_id"));
            map.put("description", "" +
                    "表英文名：" + table_info.get("table_en_name") + "\n" +
                    "表中文名：" + table_info.get("table_cn_name"));
            map.put("data_layer", dataSourceType.getCode());
            map.put("table_name", table_info.get("table_en_name"));
            map.put("original_name", table_info.get("table_cn_name"));
            map.put("hyren_name", table_info.get("table_en_name"));
            map.put("data_own_type", table_info.get("store_type"));
            map.put("file_id", table_info.get("failure_table_id"));
            dataNodes.add(map);
        });
        return dataNodes;
    }
}
