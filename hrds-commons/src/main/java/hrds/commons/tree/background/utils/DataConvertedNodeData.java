package hrds.commons.tree.background.utils;

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
            map.put("description", "" +
                    "数据源名称：" + o.get("datasource_name"));
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
    public static Map<String, Object> conversionDCLBatchClassifyInfos(
            Map<String, Object> dclBatchClassifyInfo) {
        //设置为树节点信息
        Map<String, Object> dclBatchClassifyNode = new HashMap<>();
        dclBatchClassifyNode.put("id", dclBatchClassifyInfo.get("classify_id"));
        dclBatchClassifyNode.put("label", dclBatchClassifyInfo.get("classify_name") + "【" + dclBatchClassifyInfo.get("classify_num").toString() + "】");
        dclBatchClassifyNode.put("parent_id", dclBatchClassifyInfo.get("source_id"));
        dclBatchClassifyNode.put("description", "" +
                "分类名称：" + dclBatchClassifyInfo.get("classify_name") + "\n" +
                "分类描述：" + (dclBatchClassifyInfo.get("remark")));
        dclBatchClassifyNode.put("data_layer", DataSourceType.DCL.getCode());
        dclBatchClassifyNode.put("data_own_type", Constant.DCL_BATCH);
        dclBatchClassifyNode.put("data_source_id", dclBatchClassifyInfo.get("source_id"));
        dclBatchClassifyNode.put("agent_id", dclBatchClassifyInfo.get("agent_id"));
        dclBatchClassifyNode.put("classify_id", dclBatchClassifyInfo.get("classify_id"));
        return dclBatchClassifyNode;
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

    @Method(desc = "加工层工程信息转换", logicStep = "1.加工层工程信息转换")
    @Param(name = "dmlDataInfos", desc = "加工层工程信息转换", range = "取值范围说明")
    public static List<Map<String, Object>> conversionDMLDataInfos(List<Map<String, Object>> dmlDataInfos) {
        List<Map<String, Object>> dmlDataNodes = new ArrayList<>();
        for (Map<String, Object> dmlDataInfo : dmlDataInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dmlDataInfo.get("data_mart_id"));
            map.put("label", dmlDataInfo.get("mart_name"));
            map.put("parent_id", DataSourceType.DML.getCode());
            map.put("data_layer", DataSourceType.DML.getCode());
            map.put("description", "" +
                    "加工编号：" + dmlDataInfo.get("mart_number") + "\n" +
                    "加工名称：" + dmlDataInfo.get("mart_name") + "\n" +
                    "加工描述：" + dmlDataInfo.get("mart_desc"));
            dmlDataNodes.add(map);
        }
        return dmlDataNodes;
    }

    @Method(desc = "加工层工程下分类信息转换", logicStep = "1.加工层工程下分类信息转换")
    @Param(name = "dmlDataInfos", desc = "加工层工程信息转换", range = "取值范围说明")
    public static Map<String, Object> conversionDMLCategoryInfos(Map<String, Object> dmlCategoryInfo) {
        Map<String, Object> dmlDataNode = new HashMap<>();
        dmlDataNode.put("id", dmlCategoryInfo.get("category_id"));
        dmlDataNode.put("label", dmlCategoryInfo.get("category_name"));
        dmlDataNode.put("parent_id", dmlCategoryInfo.get("parent_category_id"));
        dmlDataNode.put("classify_id", dmlCategoryInfo.get("category_id"));
        dmlDataNode.put("data_layer", DataSourceType.DML.getCode());
        dmlDataNode.put("description", "" +
                "分类编号：" + dmlCategoryInfo.get("category_id") + "\n" +
                "分类名称：" + dmlCategoryInfo.get("category_name") + "\n" +
                "分类描述：" + dmlCategoryInfo.get("category_desc"));
        return dmlDataNode;
    }

    @Method(desc = "加工层工程信息下表信息转换",
            logicStep = "1.加工层工程信息下表信息转换")
    @Param(name = "dmlTableInfos", desc = "加工层工程信息转换", range = "取值范围说明")
    public static List<Map<String, Object>> conversionDMLTableInfos(List<Map<String, Object>> dmlTableInfos) {
        List<Map<String, Object>> dmlTableNodes = new ArrayList<>();
        for (Map<String, Object> dmlTableInfo : dmlTableInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dmlTableInfo.get("datatable_id"));
            map.put("label", dmlTableInfo.get("datatable_en_name"));
            map.put("parent_id", dmlTableInfo.get("category_id"));
            map.put("classify_id", dmlTableInfo.get("category_id"));
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

    @Method(desc = "管控层DQC下存储层信息转换", logicStep = "管控层DQC下存储层信息转换")
    @Param(name = "data_store_layer_s", desc = "管控层下存在表的数据存储层", range = "取值范围说明")
    public static List<Map<String, Object>> conversionDQCDataInfos(List<Data_store_layer> data_store_layer_s) {
        List<Map<String, Object>> dqcDataNodes = new ArrayList<>();
        for (Data_store_layer data_store_layer : data_store_layer_s) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", DataSourceType.DQC.getCode() + "_" + data_store_layer.getDsl_id());
            map.put("label", data_store_layer.getDsl_name());
            map.put("parent_id", DataSourceType.DQC.getCode());
            map.put("data_layer", DataSourceType.DQC.getCode());
            map.put("dsl_id", data_store_layer.getDsl_id());
            map.put("description", "" +
                    "存储层编号：" + data_store_layer.getDsl_id() + "\n" +
                    "存储层名称：" + data_store_layer.getDsl_name() + "\n" +
                    "存储层描述：" + data_store_layer.getDsl_remark());
            dqcDataNodes.add(map);
        }
        return dqcDataNodes;
    }

    @Method(desc = "转化DQC层下表数据为Node节点数据",
            logicStep = "1.转化DQC层下表数据为Node节点数据")
    @Param(name = "dclBatchTableRs", desc = "贴源层分类下的表信息", range = "贴源层分类下的表信息")
    public static List<Map<String, Object>> conversionDQCTableInfos(List<Map<String, Object>> dqcTableInfos) {
        List<Map<String, Object>> dqcTableNodes = new ArrayList<>();
        dqcTableInfos.forEach(table_info -> {
            Map<String, Object> map = new HashMap<>();
            //数据层id
            String dsl_id = table_info.get("dsl_id").toString();
            //文件源属性id
            String file_id = table_info.get("record_id").toString();
            //表名
            String table_name = table_info.get("table_name").toString();
            map.put("id", DataSourceType.DQC.getCode() + "_" + dsl_id + "_" + file_id);
            map.put("label", table_name);
            map.put("parent_id", DataSourceType.DQC.getCode() + "_" + dsl_id);
            map.put("description", "" +
                    "存储层名称：" + table_info.get("dsl_name") + "\n" +
                    "登记表名称：" + table_name + "\n" +
                    "表中文名称：" + table_name + "\n" +
                    "原始表名称：" + table_name);
            map.put("data_layer", DataSourceType.DQC.getCode());
            map.put("dsl_id", dsl_id);
            map.put("file_id", file_id);
            map.put("table_name", table_name);
            map.put("hyren_name", table_name);
            dqcTableNodes.add(map);
        });
        return dqcTableNodes;
    }

    @Method(desc = "自定义层(UDL)下存储层信息转换", logicStep = "自定义层(UDL)下存储层信息转换")
    @Param(name = "data_store_layer_s", desc = "自定义层(UDL)下存储层信息", range = "取值范围说明")
    public static List<Map<String, Object>> conversionUDLDataInfos(List<Data_store_layer> data_store_layer_s) {
        List<Map<String, Object>> udlDataNodes = new ArrayList<>();
        for (Data_store_layer data_store_layer : data_store_layer_s) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", DataSourceType.UDL.getCode() + "_" + data_store_layer.getDsl_id());
            map.put("label", data_store_layer.getDsl_name());
            map.put("parent_id", DataSourceType.UDL.getCode());
            map.put("data_layer", DataSourceType.UDL.getCode());
            map.put("dsl_id", data_store_layer.getDsl_id());
            map.put("description", "" +
                    "存储层编号：" + data_store_layer.getDsl_id() + "\n" +
                    "存储层名称：" + data_store_layer.getDsl_name() + "\n" +
                    "存储层描述：" + data_store_layer.getDsl_remark());
            udlDataNodes.add(map);
        }
        return udlDataNodes;
    }

    @Method(desc = "转化UDL层下表数据为Node节点数据", logicStep = "转化UDL层下表数据为Node节点数据")
    @Param(name = "dclBatchTableRs", desc = "贴源层分类下的表信息", range = "贴源层分类下的表信息")
    public static List<Map<String, Object>> conversionUDLTableInfos(List<Map<String, Object>> dqcTableInfos) {
        List<Map<String, Object>> udlTableNodes = new ArrayList<>();
        dqcTableInfos.forEach(table_info -> {
            Map<String, Object> map = new HashMap<>();
            //数据层id
            String dsl_id = table_info.get("dsl_id").toString();
            //文件源属性id
            String file_id = table_info.get("table_id").toString();
            //表名
            String table_name = table_info.get("table_name").toString();
            //表中文名
            String table_ch_name = table_info.get("ch_name").toString();
            map.put("id", DataSourceType.UDL.getCode() + "_" + dsl_id + "_" + file_id);
            map.put("label", table_name);
            map.put("parent_id", DataSourceType.UDL.getCode() + "_" + dsl_id);
            map.put("description", "" +
                    "存储层名称：" + table_info.get("dsl_name") + "\n" +
                    "登记表名称：" + table_name + "\n" +
                    "表中文名称：" + table_ch_name + "\n" +
                    "原始表名称：" + table_name);
            map.put("data_layer", DataSourceType.UDL.getCode());
            map.put("dsl_id", dsl_id);
            map.put("file_id", file_id);
            map.put("table_name", table_name);
            map.put("hyren_name", table_name);
            udlTableNodes.add(map);
        });
        return udlTableNodes;
    }
}
