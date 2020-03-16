package hrds.commons.tree.background.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.IsFlag;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据转化为节点数据", author = "BY-HLL", createdate = "2020/3/12 0012 下午 05:57")
public class DataConvertedNodeData {

    @Method(desc = "转化DCL层批量数据下数据源数据为Node节点数据",
            logicStep = "转化DCL层批量数据下数据源数据为Node节点数据")
    @Param(name = "dclBatchDataInfos", desc = "贴源层批量数据下数据源信息", range = "贴源层批量数据下数据源信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> ConversionDCLBatchDataInfos(List<Map<String, Object>> dclBatchDataInfos) {
        //设置为树节点信息
        List<Map<String, Object>> dclBatchDataNodes = new ArrayList<>();
        dclBatchDataInfos.forEach(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.get("source_id"));
            map.put("label", o.get("datasource_name"));
            map.put("parent_id", Constant.DCL_BATCH);
            map.put("description", o.get("datasource_name"));
            dclBatchDataNodes.add(map);
        });
        return dclBatchDataNodes;
    }

    @Method(desc = "转化DCL层批量数据数据源下分类数据为Node节点数据",
            logicStep = "转化DCL层批量数据数据源下分类数据为Node节点数据")
    @Param(name = "dclBatchClassifyInfos", desc = "贴源层批量数据数据源下分类信息", range = "贴源层批量数据数据源下分类信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> ConversionDCLBatchClassifyInfos(
            List<Map<String, Object>> dclBatchClassifyInfos) {
        //设置为树节点信息
        List<Map<String, Object>> dclBatchClassifyNodes = new ArrayList<>();
        dclBatchClassifyInfos.forEach(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.get("classify_id"));
            map.put("label", o.get("classify_name") + "【" + o.get("classify_num").toString() + "】");
            map.put("parent_id", o.get("source_id"));
            map.put("description", o.get("remark"));
            dclBatchClassifyNodes.add(map);
        });
        return dclBatchClassifyNodes;
    }

    @Method(desc = "转化DCL层批量数据数据源分类下的表数据为Node节点数据",
            logicStep = "1.转化DCL层批量数据数据源分类下的表数据为Node节点数据")
    @Param(name = "dclBatchTableRs", desc = "贴源层分类下的表信息", range = "贴源层分类下的表信息")
    public static List<Map<String, Object>> ConversionDCLBatchTableInfos(List<Map<String, Object>> dclTableInfos) {
        List<Map<String, Object>> dclBatchTableNodes = new ArrayList<>();
        dclTableInfos.forEach(o -> {
            Map<String, Object> map = new HashMap<>();
            //原始表名
            String table_name = o.get("table_name").toString();
            //分类名称
            String classify_name = o.get("classify_name").toString();
            //任务名称
            String task_name = o.get("task_name").toString();
            //原始文件名或中文名称
            String original_name = o.get("original_name").toString();
            //系统内对应表名
            String HBase_name = o.get("hbase_name").toString();
            //文件avro存储路径
            String file_avro_path = o.get("file_avro_path").toString();
            map.put("id", o.get("file_id").toString());
            //如果是入HBase的表则显示原始表名,如果是文件则显示文件名加文件存储的AVRO的HDFS路径
            if (o.get("is_in_hbase").toString().equals(IsFlag.Shi.getCode())) {
                map.put("label", table_name);
            } else {
                if (!StringUtil.isEmpty(file_avro_path)) {
                    map.put("label", table_name + '_' + file_avro_path);
                } else {
                    map.put("label", table_name);
                }
            }
            map.put("parent_id", o.get("classify_id"));
            map.put("description", "分类名称:" + classify_name + "\n" +
                    "任务名称:" + task_name + "\n" +
                    "原始文件或源表名称:" + original_name + "\n" +
                    "原始表名:" + table_name + "\n" +
                    "系统内对应表名:" + HBase_name);
            dclBatchTableNodes.add(map);
        });
        return dclBatchTableNodes;
    }
}
