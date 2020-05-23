package hrds.commons.tree.background.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.DataSourceType;
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
    public static List<Map<String, Object>> conversionDCLBatchDataInfos(List<Map<String, Object>> dclBatchDataInfos) {
        //设置为树节点信息
        List<Map<String, Object>> dclBatchDataNodes = new ArrayList<>();
        dclBatchDataInfos.forEach(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.get("source_id"));
            map.put("label", o.get("datasource_name"));
            map.put("parent_id", Constant.DCL_BATCH);
            map.put("description", o.get("datasource_name"));
            map.put("data_layer", DataSourceType.DCL.getCode());
            map.put("data_own_type", Constant.DCL_BATCH);
            map.put("data_source_id", o.get("source_id"));
            dclBatchDataNodes.add(map);
        });
        return dclBatchDataNodes;
    }

    @Method(desc = "转化DCL层批量数据数据源下分类数据为Node节点数据",
            logicStep = "转化DCL层批量数据数据源下分类数据为Node节点数据")
    @Param(name = "dclBatchClassifyInfos", desc = "贴源层批量数据数据源下分类信息", range = "贴源层批量数据数据源下分类信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> conversionDCLBatchClassifyInfos(
            List<Map<String, Object>> dclBatchClassifyInfos) {
        //设置为树节点信息
        List<Map<String, Object>> dclBatchClassifyNodes = new ArrayList<>();
        dclBatchClassifyInfos.forEach(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.get("classify_id"));
            map.put("label", o.get("classify_name") + "【" + o.get("classify_num").toString() + "】");
            map.put("parent_id", o.get("source_id"));
            map.put("description", o.get("remark"));
            map.put("data_layer", DataSourceType.DCL.getCode());
            map.put("data_own_type", Constant.DCL_BATCH);
            map.put("data_source_id", o.get("source_id"));
            map.put("agent_id", o.get("agent_id"));
            map.put("classify_id", o.get("classify_id"));
            dclBatchClassifyNodes.add(map);
        });
        return dclBatchClassifyNodes;
    }

    @Method(desc = "转化DCL层批量数据数据源分类下的表数据为Node节点数据",
            logicStep = "1.转化DCL层批量数据数据源分类下的表数据为Node节点数据")
    @Param(name = "dclBatchTableRs", desc = "贴源层分类下的表信息", range = "贴源层分类下的表信息")
    public static List<Map<String, Object>> conversionDCLBatchTableInfos(List<Map<String, Object>> dclTableInfos) {
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
            String hyren_name = o.get("hyren_name").toString();
            //所属数据源id
            String source_id = o.get("source_id").toString();
            //所属agent_id
            String agent_id = o.get("agent_id").toString();
            //所属分类id
            String classify_id = o.get("classify_id").toString();
            //文件源属性id
            String file_id = o.get("file_id").toString();
            map.put("id", file_id);
            map.put("label", hyren_name);
            map.put("parent_id", o.get("classify_id"));
            map.put("description", "" +
                    "任务名称：" + task_name + "\n" +
                    "分类名称：" + classify_name + "\n" +
                    "原文件名：" + original_name + "\n" +
                    "原始表名：" + table_name + "\n" +
                    "系统表名：" + hyren_name);
            map.put("data_layer", DataSourceType.DCL.getCode());
            map.put("data_own_type", Constant.DCL_BATCH);
            map.put("data_source_id", source_id);
            map.put("agent_id", agent_id);
            map.put("classify_id", classify_id);
            map.put("file_id", file_id);
            map.put("table_name", table_name);
            map.put("hyren_name", hyren_name);
            map.put("original_name", original_name);
            dclBatchTableNodes.add(map);
        });
        return dclBatchTableNodes;
    }

    @Method(desc = "转化DQC层下表数据为Node节点数据",
            logicStep = "1.转化DQC层下表数据为Node节点数据")
    @Param(name = "dclBatchTableRs", desc = "贴源层分类下的表信息", range = "贴源层分类下的表信息")
    public static List<Map<String, Object>> conversionDQCTableInfos(List<Map<String, Object>> dqcDataInfos) {
        List<Map<String, Object>> dqcTableNodes = new ArrayList<>();
        dqcDataInfos.forEach(o -> {
            Map<String, Object> map = new HashMap<>();
            //文件源属性id
            String file_id = o.get("record_id").toString();
            //表名
            String table_name = o.get("table_name").toString();
            map.put("id", file_id);
            map.put("label", table_name);
            map.put("parent_id", DataSourceType.DQC.getCode());
            map.put("data_layer", DataSourceType.DQC.getCode());
            map.put("file_id", file_id);
            map.put("table_name", table_name);
            map.put("hyren_name", table_name);
            map.put("description", "" +
                    "系统表名：" + table_name);
            dqcTableNodes.add(map);
        });
        return dqcTableNodes;
    }

    @Method(desc = "集市层工程信息转换",
            logicStep = "1.集市层工程信息转换")
    @Param(name = "dmlDataInfos", desc = "集市层工程信息转换", range = "取值范围说明")
    public static List<Map<String, Object>> conversionDMLDataInfos(List<Map<String, Object>> dmlDataInfos) {
        List<Map<String, Object>> dmlDataNodes = new ArrayList<>();
        for (Map<String, Object> dmlDataInfo : dmlDataInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dmlDataInfo.get("data_mart_id"));
            map.put("label", dmlDataInfo.get("mart_name"));
            map.put("parent_id", DataSourceType.DML.getCode());
            map.put("data_layer", DataSourceType.DML.getCode());
            map.put("description", "" +
                    "集市编号：" + dmlDataInfo.get("mart_number") + "\n" +
                    "集市名称：" + dmlDataInfo.get("mart_name") + "\n" +
                    "集市描述：" + dmlDataInfo.get("mart_desc"));
            dmlDataNodes.add(map);
        }
        return dmlDataNodes;
    }

    @Method(desc = "集市层工程信息下表信息转换",
            logicStep = "1.集市层工程信息下表信息转换")
    @Param(name = "dmlTableInfos", desc = "集市层工程信息转换", range = "取值范围说明")
    public static List<Map<String, Object>> conversionDMLTableInfos(List<Map<String, Object>> dmlTableInfos) {
        List<Map<String, Object>> dmlTableNodes = new ArrayList<>();
        for (Map<String, Object> dmlTableInfo : dmlTableInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dmlTableInfo.get("datatable_id"));
            map.put("label", dmlTableInfo.get("datatable_en_name"));
            map.put("parent_id", dmlTableInfo.get("data_mart_id"));
            map.put("file_id", dmlTableInfo.get("datatable_id"));
            map.put("table_name", dmlTableInfo.get("datatable_en_name"));
            map.put("hyren_name", dmlTableInfo.get("datatable_en_name"));
            map.put("original_name", dmlTableInfo.get("datatable_cn_name"));
            map.put("data_layer", DataSourceType.DML.getCode());
            map.put("description", "" +
                    "表英文名：" + dmlTableInfo.get("datatable_en_name") + "\n" +
                    "表中文名：" + dmlTableInfo.get("datatable_cn_name") + "\n" +
                    "表描述：" + dmlTableInfo.get("datatable_desc"));
            dmlTableNodes.add(map);
        }
        return dmlTableNodes;
    }
}
