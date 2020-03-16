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
    public static void ConversionSourceTreeInfos(List<Map<String, Object>> treeMenuInfos,
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
    public static void ConversionDCLSourceInfos(List<Map<String, Object>> dclDataInfos,
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
    public static void ConversionDCLBatchDataInfos(List<Map<String, Object>> dclBatchDataInfos,
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

    @Method(desc = "贴源层实时数据信息转换",
            logicStep = "1.贴源层实时数据信息转换")
    @Param(name = "dclRealTimeDataRs", desc = "贴源层实时数据信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDCLRealTimeDataInfos(List<Map<String, Object>> dclRealTimeDataInfos,
                                                      List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dclRealTimeDataInfo : dclRealTimeDataInfos) {
            Map<String, Object> map = new HashMap<>();
            String groupid = dclRealTimeDataInfo.get("groupid").toString();
            map.put("groupid", groupid);
            map.put("name", groupid);
            map.put("description", groupid);
            map.put("rootName", rootName);
            map.put("pId", Constant.DCL_REALTIME);
            map.put("id", groupid);
            map.put("source", Constant.DCL_REALTIME);
            map.put("isParent", Boolean.TRUE);
            treeDataList.add(map);
        }
    }

    @Method(desc = "贴源层实时数据下Topic信息转换",
            logicStep = "1.贴源层实时数据下Topic信息转换")
    @Param(name = "dclRealTimeTopicRs", desc = "贴源层实时数据下Topic信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDCLRealTimeTopicInfos(List<Map<String, Object>> dclRealTimeTopicInfos,
                                                       List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dclRealTimeTopicInfo : dclRealTimeTopicInfos) {
            Map<String, Object> map = new HashMap<>();
            String sdm_consum_id = dclRealTimeTopicInfo.get("sdm_consum_id").toString();
            String topicName = dclRealTimeTopicInfo.get("sdm_cons_para_val").toString();
            String groupid = dclRealTimeTopicInfo.get("groupid").toString();
            map.put("sdm_consum_id", sdm_consum_id);
            map.put("name", topicName);
            map.put("description", topicName);
            map.put("rootName", rootName);
            map.put("pId", groupid);
            map.put("id", sdm_consum_id);
            map.put("isParent", Boolean.TRUE);
            map.put("source", Constant.DCL_REALTIME);
            map.put("show", Boolean.TRUE);
            treeDataList.add(map);
        }
    }

    @Method(desc = "贴源层批量数据的分类信息转换",
            logicStep = "1.贴源层批量数据的分类信息转换")
    @Param(name = "dclBatchClassifyInfos", desc = "贴源层批量数据的分类信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDCLBatchClassifyInfos(List<Map<String, Object>> dclBatchClassifyInfos,
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
    public static void ConversionDCLBatchTableInfos(List<Map<String, Object>> dclTableInfos,
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

    @Method(desc = "贴源层实时数据下内部表信息转换",
            logicStep = "1.贴源层实时数据下内部表信息转换")
    @Param(name = "dclRealTimeInnerTableRs", desc = "贴源层实时数据下内部表信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDCLRealTimeInnerTableInfos(List<Map<String, Object>> dclRealTimeInnerTableInfos,
                                                            List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dclRealTimeInnerTableInfo : dclRealTimeInnerTableInfos) {
            Map<String, Object> map = new HashMap<>();
            String table_id = dclRealTimeInnerTableInfo.get("table_id").toString();
            String table_en_name = dclRealTimeInnerTableInfo.get("table_en_name").toString();
            String table_cn_name = dclRealTimeInnerTableInfo.get("table_cn_name").toString();
            String sdm_con_sum_id = dclRealTimeInnerTableInfo.get("sdm_consum_id").toString();
            map.put("table_id", table_id);
            if (StringUtil.isEmpty(table_cn_name)) {
                map.put("name", table_en_name);
            } else {
                map.put("name", table_cn_name);
            }
            map.put("description", table_en_name);
            map.put("rootName", rootName);
            map.put("tableName", table_en_name);
            map.put("pId", sdm_con_sum_id);
            map.put("id", table_id);
            map.put("isParent", Boolean.FALSE);
            map.put("source", Constant.DCL_REALTIME);
            map.put("show", Boolean.TRUE);
            treeDataList.add(map);
        }
    }

    @Method(desc = "集市层源信息转换",
            logicStep = "1.集市层源信息转换")
    @Param(name = "dmlDataInfos", desc = "集市源信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    @Param(name = "isShTable", desc = "是否为树的根节点标志", range = "Boolean")
    public static void ConversionDMLSourceInfos(List<Map<String, Object>> dmlDataInfos,
                                                List<Map<String, Object>> treeDataList, String rootName,
                                                String isShTable) {
        for (Map<String, Object> dmlDataInfo : dmlDataInfos) {
            Map<String, Object> map = new HashMap<>();
            String mart_storage_path = dmlDataInfo.get("mart_storage_path").toString();
            String create_date = dmlDataInfo.get("create_date").toString();
            String create_id = dmlDataInfo.get("create_id").toString();
            String remark = dmlDataInfo.get("remark").toString();
            String create_time = dmlDataInfo.get("create_time").toString();
            String mart_name = dmlDataInfo.get("mart_name").toString();
            String data_mart_id = dmlDataInfo.get("data_mart_id").toString();
            String mart_desc = dmlDataInfo.get("mart_desc").toString();
            map.put("mart_storage_path", mart_storage_path);
            map.put("create_id", create_id);
            map.put("create_date", create_date);
            map.put("create_time", create_time);
            map.put("name", mart_name);
            map.put("data_mart_id", data_mart_id);
            map.put("mart_desc", mart_desc);
            map.put("remark", remark);
            map.put("rootName", rootName);
            map.put("pId", rootName);
            map.put("id", data_mart_id);
            map.put("show", Boolean.TRUE);
            map.put("isParent", isShTable);
            map.put("description", mart_desc);
            map.put("source", DataSourceType.DML.getValue());
            treeDataList.add(map);
        }
    }

    @Method(desc = "集市层下表信息转换",
            logicStep = "1.集市层下表信息转换")
    @Param(name = "dmlTableRs", desc = "集市层下表信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDMLTableInfos(List<Map<String, Object>> dmlTableInfos,
                                               List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dmlTableInfo : dmlTableInfos) {
            Map<String, Object> map = new HashMap<>();
            String dtable_info_id = dmlTableInfo.get("dtable_info_id").toString();
            String datatable_id = dmlTableInfo.get("datatable_id").toString();
            String datatable_cn_name = dmlTableInfo.get("datatable_cn_name").toString();
            String datatable_en_name = dmlTableInfo.get("datatable_en_name").toString();
            String data_mart_id = dmlTableInfo.get("data_mart_id").toString();
            String datatype;
            // TODO 0:数据表,1:数据视图
            if ("0" .equals(dmlTableInfo.get("datatype"))) {
                if (IsFlag.Shi.getCode().equals(dmlTableInfo.get("is_solr_db"))) {
                    datatype = " [SolrDB]";
                } else {
                    datatype = " [数据表]";
                }
            } else {
                datatype = " [数据视图]";
            }
            map.put("dtable_info_id", dtable_info_id);
            map.put("name", datatable_cn_name + datatype);
            map.put("tablename", datatable_en_name);
            map.put("rootName", rootName);
            if (IsFlag.Shi.getCode().equals(dmlTableInfo.get("is_solr_db"))) {
                map.put("description", "您选择的" + datatable_en_name + "表为solrDB,请使用全文检索功能查询");
            } else {
                map.put("description", datatable_en_name);
            }
            map.put("pId", data_mart_id);
            map.put("id", datatable_id);
            map.put("source", DataSourceType.DML.getValue());
            map.put("show", Boolean.TRUE);
            map.put("datatable_id", datatable_id);
            map.put("isParent", Boolean.FALSE);
            treeDataList.add(map);
        }
    }

    @Method(desc = "加工层源信息转换",
            logicStep = "1.加工层源信息转换")
    @Param(name = "dplDataRs", desc = "加工层源信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDPLSourceInfos(List<Map<String, Object>> dplDataInfos,
                                                List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dplDataInfo : dplDataInfos) {
            Map<String, Object> map = new HashMap<>();
            String mart_storage_path = dplDataInfo.get("mart_storage_path").toString();
            String create_date = dplDataInfo.get("create_date").toString();
            String create_id = dplDataInfo.get("create_id").toString();
            String pro_desc = dplDataInfo.get("pro_desc").toString();
            String create_time = dplDataInfo.get("create_time").toString();
            String pro_name = dplDataInfo.get("pro_name").toString();
            String modal_pro_id = dplDataInfo.get("modal_pro_id").toString();
            String mart_desc = dplDataInfo.get("mart_desc").toString();
            map.put("mart_storage_path", mart_storage_path);
            map.put("rootName", rootName);
            map.put("create_id", create_id);
            map.put("create_date", create_date);
            map.put("create_time", create_time);
            map.put("name", pro_name);
            map.put("modal_pro_id", modal_pro_id);
            map.put("mart_desc", mart_desc);
            map.put("pId", rootName);
            map.put("id", modal_pro_id);
            map.put("remark", pro_desc);
            map.put("isParent", Boolean.TRUE);
            map.put("description", mart_desc);
            map.put("source", DataSourceType.DPL.getValue());
            treeDataList.add(map);
        }
    }

    @Method(desc = "加工层下分类信息转换",
            logicStep = "1.加工层下分类信息转换")
    @Param(name = "dplClassifyRs", desc = "加工层下分类信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    @Param(name = "isShTable", desc = "是否为树的根节点标志", range = "IsFlag")
    public static void ConversionDPLClassifyInfos(List<Map<String, Object>> dplClassifyInfos,
                                                  List<Map<String, Object>> treeDataList, String rootName,
                                                  String isShTable) {
        for (Map<String, Object> dplClassifyInfo : dplClassifyInfos) {
            Map<String, Object> map = new HashMap<>();
            String category_id = dplClassifyInfo.get("category_id").toString();
            String category_name = dplClassifyInfo.get("category_name").toString();
            String modal_pro_id = dplClassifyInfo.get("modal_pro_id").toString();
            map.put("category_id", category_id);
            map.put("name", category_name);
            map.put("rootName", rootName);
            map.put("description", category_name);
            map.put("pId", modal_pro_id);
            map.put("source", DataSourceType.DPL.getValue());
            map.put("show", Boolean.TRUE);
            map.put("id", category_id);
            map.put("isParent", isShTable);
            treeDataList.add(map);
        }
    }

    @Method(desc = "加工层下分类的子分类信息转换",
            logicStep = "1.加工层下分类的子分类信息转换")
    @Param(name = "dplSubClassifyRs", desc = "加工层下分类的子分类信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDPLSubClassifyInfos(List<Map<String, Object>> dplSubClassifyInfos,
                                                     List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dplSubClassifyInfo : dplSubClassifyInfos) {
            Map<String, Object> map = new HashMap<>();
            String category_id = dplSubClassifyInfo.get("category_id").toString();
            String category_name = dplSubClassifyInfo.get("category_name").toString();
            String parent_category_id = dplSubClassifyInfo.get("parent_category_id").toString();
            map.put("category_id", category_id);
            map.put("name", category_name);
            map.put("description", category_name);
            map.put("rootName", rootName);
            map.put("pId", parent_category_id);
            map.put("show", Boolean.TRUE);
            map.put("id", category_id);
            map.put("isParent", Boolean.TRUE);
            map.put("source", DataSourceType.DPL.getValue());
            treeDataList.add(map);
        }
    }

    @Method(desc = "加工层下分类的表信息转换",
            logicStep = "1.加工层下分类的表信息转换")
    @Param(name = "dplTableRs", desc = "加工层下分类的表信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDPLTableInfos(List<Map<String, Object>> dplTableInfos,
                                               List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dplTableInfo : dplTableInfos) {
            Map<String, Object> map = new HashMap<>();
            String table_id = dplTableInfo.get("table_id").toString();
            String category_id = dplTableInfo.get("category_id").toString();
            String tabname = dplTableInfo.get("tabname").toString();
            String ctname = dplTableInfo.get("ctname").toString();
            ctname = StringUtil.isEmpty(ctname) ? tabname : ctname;
            map.put("name", ctname);
            map.put("id", table_id);
            map.put("pId", category_id);
            map.put("table_id", table_id);
            map.put("show", Boolean.TRUE);
            map.put("tableName", tabname);
            map.put("rootName", rootName);
            map.put("description", ctname);
            map.put("source", DataSourceType.DPL.getValue());
            map.put("isParent", Boolean.FALSE);
            treeDataList.add(map);
        }
    }

    @Method(desc = "数据管控层源信息转换",
            logicStep = "1.数据管控层源信息转换")
    @Param(name = "dqcDataRs", desc = "数据管控层源信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    public static void ConversionDQCSourceInfos(List<Map<String, Object>> dqcDataInfos,
                                                List<Map<String, Object>> treeDataList, String rootName) {
        for (Map<String, Object> dqcDataInfo : dqcDataInfos) {
            Map<String, Object> map = new HashMap<>();
            String table_name = dqcDataInfo.get("table_name").toString();
            String record_id = dqcDataInfo.get("record_id").toString();
            map.put("name", table_name);
            map.put("description", table_name);
            map.put("rootName", rootName);
            map.put("tablename", table_name);
            map.put("pId", rootName);
            map.put("id", record_id);
            map.put("table_id", record_id);
            map.put("isParent", Boolean.FALSE);
            map.put("source", DataSourceType.DQC.getValue());
            map.put("show", Boolean.TRUE);
            treeDataList.add(map);
        }
    }

    @Method(desc = "系统层源信息转换",
            logicStep = "1.系统层源信息转换")
    @Param(name = "sflDataRs", desc = "系统层源信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    public static void ConversionSFLSourceInfos(List<Map<String, Object>> sflDataInfos,
                                                List<Map<String, Object>> treeDataList) {
        for (Map<String, Object> sflDataInfo : sflDataInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("agent_type", sflDataInfo.get("sysId"));
            map.put("name", sflDataInfo.get("sysId"));
            map.put("description", sflDataInfo.get("sysId"));
            map.put("isParent", Boolean.TRUE);
            map.put("source", DataSourceType.SFL.getValue());
            treeDataList.add(map);
        }
    }

    @Method(desc = "系统层表信息转换",
            logicStep = "1.系统层表信息转换")
    @Param(name = "sflTableInfos", desc = "系统层表信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    public static void ConversionSFLTableInfos(List<Object> sflTableInfos, List<Map<String, Object>> treeDataList) {
        for (Object sflTableInfo : sflTableInfos) {
            Map<String, Object> map = new HashMap<>();
            String table_name = (String) sflTableInfo;
            map.put("name", table_name);
            map.put("id", table_name);
            map.put("pId", Constant.SYS_DATA_TABLE);
            map.put("table_id", table_name);
            map.put("show", Boolean.TRUE);
            map.put("tableName", table_name);
            map.put("rootName", DataSourceType.SFL.getValue());
            map.put("description", table_name);
            map.put("source", DataSourceType.SFL.getValue());
            map.put("isParent", Boolean.FALSE);
            treeDataList.add(map);
        }
    }

    @Method(desc = "系统层数据备份信息转换",
            logicStep = "1.系统层数据备份信息转换")
    @Param(name = "sflTableInfos", desc = "系统层数据备份信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    public static void ConversionSFLDataBakInfos(List<Map<String, Object>> sflDataBakInfos,
                                                 List<Map<String, Object>> treeDataList) {
        for (Map<String, Object> sflDataBakInfo : sflDataBakInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", sflDataBakInfo.get("file_name"));
            map.put("id", sflDataBakInfo.get("dump_id"));
            map.put("pId", Constant.SYS_DATA_BAK);
            map.put("table_id", sflDataBakInfo.get("dump_id"));
            map.put("show", Boolean.TRUE);
            map.put("tableName", "sys_dump where dump_id = " + sflDataBakInfo.get("dump_id"));
            map.put("rootName", DataSourceType.SFL.getValue());
            map.put("description", sflDataBakInfo.get("hdfs_path"));
            map.put("isParent", Boolean.FALSE);
            map.put("source", DataSourceType.SFL.getValue());
            treeDataList.add(map);
        }
    }

    @Method(desc = "自定义层下数据库信息转换",
            logicStep = "1.自定义层下数据库信息转换")
    @Param(name = "udlDatabaseInfos", desc = "自定义层下数据库信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    public static void ConversionUDLDatabaseInfos(List<Map<String, Object>> udlDatabaseInfos,
                                                  List<Map<String, Object>> treeDataList) {
        for (Map<String, Object> udlDatabaseInfo : udlDatabaseInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", udlDatabaseInfo.get("id").toString());
            map.put("name", udlDatabaseInfo.get("name").toString());
            map.put("description", udlDatabaseInfo.get("description").toString());
            map.put("rootName", udlDatabaseInfo.get("rootName").toString());
            map.put("pId", udlDatabaseInfo.get("pId").toString());
            map.put("isParent", udlDatabaseInfo.get("isParent").toString());
            map.put("parent_id", udlDatabaseInfo.get("parent_id").toString());
            treeDataList.add(map);
        }
    }

    @Method(desc = "自定义层数据库下表空间信息转换",
            logicStep = "1.自定义层数据库下表空间信息转换")
    @Param(name = "udlDatabaseTableSpaceInfos", desc = "自定义层数据库下表空间信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    @Param(name = "parent_id", desc = "父级菜单id", range = "树中唯一")
    public static void ConversionUDLDatabaseTableSpaceInfos(List<Map<String, Object>> udlDatabaseTableSpaceInfos,
                                                            List<Map<String, Object>> treeDataList, String rootName,
                                                            String parent_id) {
        Map<String, String> map = new HashMap<>();
        for (Map<String, Object> udlDatabaseTableSpaceInfo : udlDatabaseTableSpaceInfos) {
            String tableSpace = udlDatabaseTableSpaceInfo.get("space").toString();
            if (!map.containsKey(tableSpace)) {
                udlDatabaseTableSpaceInfo.put("name", tableSpace);
                udlDatabaseTableSpaceInfo.put("description", tableSpace);
                udlDatabaseTableSpaceInfo.put("space_name", tableSpace);
                udlDatabaseTableSpaceInfo.put("parent_id", parent_id);
                udlDatabaseTableSpaceInfo.put("show", Boolean.TRUE);
                udlDatabaseTableSpaceInfo.put("rootName", rootName);
                udlDatabaseTableSpaceInfo.put("id", rootName.concat("_").concat(tableSpace));
                udlDatabaseTableSpaceInfo.put("isParent", Boolean.TRUE);
                udlDatabaseTableSpaceInfo.put("source", DataSourceType.UDL.getValue());
                map.put(tableSpace, tableSpace);
                treeDataList.add(udlDatabaseTableSpaceInfo);
            }
        }
    }

    @Method(desc = "自定义层数据库下表空间信息转换",
            logicStep = "1.自定义层数据库下表空间信息转换")
    @Param(name = "udlDatabaseTableSpaceInfos", desc = "自定义层数据库下表空间信息", range = "取值范围说明")
    @Param(name = "treeDataList", desc = "转换后的树数据信息", range = "取值范围说明")
    @Param(name = "rootName", desc = "父级菜单名称", range = "取值范围说明")
    @Param(name = "parent_id", desc = "父级菜单id", range = "取值范围说明")
    public static void ConversionUDLTableSpaceTableInfos(List<Map<String, Object>> udlTableSpaceTableInfos,
                                                         List<Map<String, Object>> treeDataList, String rootName,
                                                         String parent_id) {
        udlTableSpaceTableInfos.forEach(udlTableSpaceTableInfo -> {
            String table_name = udlTableSpaceTableInfo.get("table_name").toString();
            String table_ch_name = udlTableSpaceTableInfo.get("table_ch_name").toString();
            table_ch_name = StringUtil.isNotBlank(table_ch_name) ? table_ch_name : table_name;
            String data_space;
            if (Constant.HIVE.equalsIgnoreCase(parent_id)) {
                data_space = udlTableSpaceTableInfo.get("table_space").toString();
            } else if (Constant.HBASE.equalsIgnoreCase(parent_id)) {
                data_space = udlTableSpaceTableInfo.get("table_space").toString();
            } else if (Constant.MPP.equalsIgnoreCase(parent_id)) {
                data_space = "default";
            } else {
                data_space = StringUtil.isBlank(udlTableSpaceTableInfo.get("table_space").toString()) ?
                        Constant.CARBON_DATA : udlTableSpaceTableInfo.get("table_space").toString();
            }
            String id = udlTableSpaceTableInfo.get("id").toString();
            udlTableSpaceTableInfo.put("name", table_ch_name);
            udlTableSpaceTableInfo.put("tableName", table_name);
            udlTableSpaceTableInfo.put("description", table_name);
            udlTableSpaceTableInfo.put("pId", parent_id.concat("_").concat(data_space));
            udlTableSpaceTableInfo.put("file_id", id);
            udlTableSpaceTableInfo.put("parent_id", parent_id);
            udlTableSpaceTableInfo.put("rootName", rootName);
            udlTableSpaceTableInfo.put("source", DataSourceType.UDL.getValue());
            udlTableSpaceTableInfo.put("isParent", false);
            udlTableSpaceTableInfo.put("show", true);
            treeDataList.add(udlTableSpaceTableInfo);
        });
    }
}
