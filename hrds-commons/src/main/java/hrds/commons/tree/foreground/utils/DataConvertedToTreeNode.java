package hrds.commons.tree.foreground.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.utils.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据转换为树节点", author = "BY-HLL", createdate = "2019/12/25 0025 下午 04:17")
public class DataConvertedToTreeNode {

    @Method(desc = "源树菜单数据转树数据列表",
            logicStep = "逻辑说明")
    @Param(name = "treeMenuInfos", desc = "树菜单数据", range = "treeMenuInfos")
    @Param(name = "treeDataList", desc = "树数据信息", range = "treeDataList")
    @Return(desc = "源树菜单数据的List", range = "无限制")
    public static void conversionSourceTreeInfos(List<Map<String, Object>> treeMenuInfos,
                                                 List<Map<String, Object>> treeDataList) {
        for (Map<String, Object> treeMenuInfo : treeMenuInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", treeMenuInfo.get("name"));
            map.put("agent_layer", treeMenuInfo.get("agent_layer"));
            map.put("isParent", treeMenuInfo.get("isParent"));
            map.put("rootName", treeMenuInfo.get("rootName"));
            map.put("id", treeMenuInfo.get("id"));
            map.put("pId", treeMenuInfo.get("pId"));
            map.put("description", treeMenuInfo.get("description"));
            treeDataList.add(map);
        }
    }

    @Method(desc = "贴源层下树菜单",
            logicStep = "1.添加批量数据子级文件夹" +
                    "2.添加实时数据子级文件夹")
    @Param(name = "dclDataInfos", desc = "DCL层下数据信息", range = "dclDataInfos")
    @Param(name = "treeDataList", desc = "树菜单数据信息", range = "treeDataList")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void conversionDCLSourceInfos(List<Map<String, Object>> dclDataInfos,
                                                List<Map<String, Object>> treeDataList) {
        for (Map<String, Object> dclDataInfo : dclDataInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("batch_id", dclDataInfo.get("batch_id"));
            map.put("name", dclDataInfo.get("name"));
            map.put("description", dclDataInfo.get("description"));
            map.put("rootName", dclDataInfo.get("rootName"));
            map.put("source", dclDataInfo.get("source"));
            map.put("pId", dclDataInfo.get("pId"));
            map.put("id", dclDataInfo.get("id"));
            map.put("isParent", dclDataInfo.get("isParent"));
            treeDataList.add(map);
        }
    }

    @Method(desc = "贴源层批量数据下数据源信息转换",
            logicStep = "1.贴源层批量数据下数据源信息转换")
    @Param(name = "dclBatchDataInfos", desc = "贴源层下数据源信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void conversionDCLBatchDataInfos(List<Map<String, Object>> dclBatchDataInfos,
                                                   List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dclBatchDataInfo : dclBatchDataInfos) {
            Map<String, Object> map = new HashMap<>();
            String source_id = dclBatchDataInfo.get("source_id").toString();
            String datasource_name = dclBatchDataInfo.get("datasource_name").toString();
            map.put("source_id", source_id);
            map.put("source", Constant.DCL_BATCH);
            map.put("name", datasource_name);
            map.put("description", datasource_name);
            map.put("rootName", rootName);
            map.put("pId", Constant.DCL_BATCH);
            map.put("id", source_id);
            map.put("isParent", Boolean.TRUE);
            treeDataList.add(map);
        }
    }


    @Method(desc = "贴源层批量数据的分类信息转换",
            logicStep = "1.贴源层批量数据的分类信息转换")
    @Param(name = "dclBatchClassifyInfos", desc = "贴源层批量数据的分类信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void conversionDCLBatchClassifyInfos(List<Map<String, Object>> dclBatchClassifyInfos,
                                                       List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dclBatchClassifyInfo : dclBatchClassifyInfos) {
            Map<String, Object> map = new HashMap<>();
            String agent_id = dclBatchClassifyInfo.get("agent_id").toString();
            //Agent类型
            String agent_type = dclBatchClassifyInfo.get("agent_type").toString();
            //分类名称
            String classify_name = dclBatchClassifyInfo.get("classify_name").toString();
            //分类编号
            String classify_num = dclBatchClassifyInfo.get("classify_num").toString();
            //分类ID
            String classify_id = dclBatchClassifyInfo.get("classify_id").toString();
            String source_id = dclBatchClassifyInfo.get("source_id").toString();
            //数据源名称
            String source_name = dclBatchClassifyInfo.get("datasource_name").toString();
            map.put("agent_id", agent_id);
            map.put("classify_id", classify_id);
            map.put("name", classify_name + "【" + classify_num + "】");
            map.put("agent_type", agent_type);
            map.put("description", source_name);
            map.put("id", classify_id);
            map.put("pId", source_id);
            //决定是否显示该分类下的表信息
            map.put("show", Boolean.TRUE);
            map.put("rootName", rootName);
            map.put("isParent", Boolean.TRUE);
            map.put("source", DataSourceType.DCL.getValue());
            treeDataList.add(map);
        }
    }

    @Method(desc = "贴源层分类下的表信息转换",
            logicStep = "1.贴源层分类下的表信息转换")
    @Param(name = "dclBatchTableRs", desc = "贴源层分类下的表信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void conversionDCLBatchTableInfos(List<Map<String, Object>> dclTableInfos,
                                                    List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dclTableInfo : dclTableInfos) {
            Map<String, Object> map = new HashMap<>();
            //文件id
            String file_id = dclTableInfo.get("file_id").toString();
            //任务名称
            String task_name = dclTableInfo.get("task_name").toString();
            //原始表名
            String originalTableName = dclTableInfo.get("table_name").toString();
            //分类名称
            String classify_name = dclTableInfo.get("classify_name").toString();
            String is_in_hbase = dclTableInfo.get("is_in_hbase").toString();
            //原始文件名或中文名称
            String original_name = dclTableInfo.get("original_name").toString();
            //集群表名
            String HBase_name = dclTableInfo.get("hbase_name").toString();
            //采集任务id
            String collect_set_id = dclTableInfo.get("collect_set_id").toString();
            //所属agent_id
            String agent_id = dclTableInfo.get("agent_id").toString();
            //所属数据源id
            String source_id = dclTableInfo.get("source_id").toString();
            //分类ID(只包括DB任务采集和数据库任务采集)
            String classify_id = dclTableInfo.get("classify_id").toString();
            //文件avro存储路径
            String file_avro_path = dclTableInfo.get("file_avro_path").toString();
            file_avro_path = file_avro_path.substring(file_avro_path.lastIndexOf('/') + 1);
            if (is_in_hbase.equals(IsFlag.Shi.getCode())) {
                map.put("name", original_name);
            } else {
                if (!StringUtil.isEmpty(file_avro_path)) {
                    map.put("name", original_name + '_' + file_avro_path);
                } else {
                    map.put("name", original_name);
                }
            }
            map.put("file_id", file_id);
            map.put("tableName", HBase_name);
            map.put("task_name", task_name);
            map.put("table_name", originalTableName);
            map.put("classify_name", classify_name);
            map.put("isParent", Boolean.FALSE);
            map.put("rootName", rootName);
            map.put("collect_set_id", collect_set_id);
            map.put("show", Boolean.TRUE);
            map.put("agent_id", agent_id);
            map.put("source_id", source_id);
            map.put("source", DataSourceType.DCL.getValue());
            map.put("pId", classify_id);
            treeDataList.add(map);
        }
    }

    @Method(desc = "集市层工程信息转换",
            logicStep = "1.集市层工程信息转换")
    @Param(name = "dmlDataInfos", desc = "集市层工程信息转换", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    @Param(name = "isShTable", desc = "isShTable", range = "取值范围说明")
    public static void ConversionDMLSourceInfos(List<Map<String, Object>> dmlDataInfos, List<Map<String, Object>> treeDataList, String rootName) {
        for (int i = 0; i < dmlDataInfos.size(); i++) {
            Map<String, Object> stringObjectMap = dmlDataInfos.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("rootName",rootName);
            map.put("isParent", Boolean.TRUE);
            map.put("pId",stringObjectMap.get("data_mart_id").toString());
            map.put("data_mart_id",stringObjectMap.get("data_mart_id").toString());
            map.put("show", Boolean.TRUE);
            map.put("source", DataSourceType.DML.getValue());
            map.put("name",stringObjectMap.get("mart_name"));
            map.put("description",stringObjectMap.get("mart_name"));
            treeDataList.add(map);
        }
    }

    @Method(desc = "集市层工程信息转换",
            logicStep = "1.集市层工程信息转换")
    @Param(name = "dmlDataInfos", desc = "集市层工程信息转换", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    @Param(name = "isShTable", desc = "isShTable", range = "取值范围说明")
    public static void ConversionDMLTableInfos(List<Map<String, Object>> dmlTableInfos, List<Map<String, Object>> treeDataList, String rootName) {
        for (int i = 0; i < dmlTableInfos.size(); i++) {
            Map<String, Object> stringObjectMap = dmlTableInfos.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("rootName",rootName);
            map.put("isParent", Boolean.FALSE);
            map.put("pId",stringObjectMap.get("datatable_id").toString());
            map.put("show", Boolean.TRUE);
            map.put("source", DataSourceType.DML.getValue());
            map.put("name",stringObjectMap.get("datatable_en_name"));
            map.put("tableName",stringObjectMap.get("datatable_en_name"));
            map.put("description",stringObjectMap.get("datatable_en_name"));
            treeDataList.add(map);
        }
    }
}
