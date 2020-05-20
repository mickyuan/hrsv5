package hrds.commons.tree.background.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.UserType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.utils.Constant;
import hrds.commons.utils.User;

import java.util.*;

@DocClass(desc = "贴源层(DCL)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:10")
public class DCLDataQuery {

    @Method(desc = "获取登录用户的贴源层数据信息",
            logicStep = "1.获取登录用户的数据源列表")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    @Return(desc = "数据源列表", range = "无限制")
    public static List<Map<String, Object>> getDCLDataInfos(TreeConf treeConf) {
        List<Map<String, Object>> dclDataInfos = new ArrayList<>();
        Map<String, Object> map;
        map = new HashMap<>();
        //1.添加批量数据子级
        map.put("id", Constant.DCL_BATCH);
        map.put("label", "批量数据");
        map.put("parent_id", DataSourceType.DCL.getCode());
        map.put("description", "批量数据查询");
        map.put("data_layer", DataSourceType.DCL.getCode());
        dclDataInfos.add(map);
        //判断是否显示实时数据菜单
        if (treeConf.getShowDCLRealtime()) {
            //2.添加实时数据子级
            map = new HashMap<>();
            map.put("id", Constant.DCL_REALTIME);
            map.put("label", "实时数据");
            map.put("parent_id", DataSourceType.DCL.getCode());
            map.put("description", "实时数据查询");
            map.put("data_layer", DataSourceType.DCL.getCode());
            dclDataInfos.add(map);
        }
        return dclDataInfos;
    }

    @Method(desc = "获取批量数据的数据源列表",
            logicStep = "1.获取批量数据的数据源列表")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "数据源列表", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchDataInfos(User User) {
        //1.获取登录用户的数据源列表
        return getDCLBatchDataInfos(User, null);
    }

    @Method(desc = "获取批量数据的数据源列表,根据数据源名称模糊查询",
            logicStep = "1.如果数据源名称不为空,模糊查询获取数据源信息" +
                    "2.如果是系统管理员,则不过滤部门" +
                    "3.获取查询结果集")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataSourceName", desc = "查询数据源的名称", range = "String类型字符,512长度", nullable = true)
    @Return(desc = "数据源列表", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchDataInfos(User user, String dataSourceName) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT distinct ds.source_id, ds.datasource_name from source_relation_dep srd JOIN " +
                "data_source ds on srd.SOURCE_ID = ds.SOURCE_ID");
        //1.如果数据源名称不为空,模糊查询获取数据源信息
        if (StringUtil.isNotBlank(dataSourceName)) {
            asmSql.addSql(" AND datasource_name like ? OR datasource_number like ?");
            asmSql.addParam('%' + dataSourceName + '%').addParam('%' + dataSourceName + '%');
        }
        //2.如果不是系统管理员,则过滤部门
        UserType userType = UserType.ofEnumByCode(user.getUserType());
        if (UserType.XiTongGuanLiYuan != userType) {
            asmSql.addSql("where srd.dep_id = ?");
            asmSql.addParam(user.getDepId());
        }
        //3.获取查询结果集
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取批量数据下数据源下分类信息",
            logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
    @Param(name = "source_id", desc = "数据源id", range = "数据源id,唯一")
    @Param(name = "showFileCollection", desc = "是否文件采集", range = "true:是,false:否")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "加工信息列表", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchClassifyInfos(String source_id, Boolean showFileCollection,
                                                                     User user) {
        return getDCLBatchClassifyInfos(source_id, showFileCollection, user, null);
    }

    @Method(desc = "获取批量数据下数据源下分类信息",
            logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
    @Param(name = "source_id", desc = "数据源id", range = "数据源id,唯一")
    @Param(name = "showFileCollection", desc = "是否获取文件采集任务采集数据", range = "true:是,false:否")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "加工信息列表", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchClassifyInfos(String source_id, Boolean showFileCollection,
                                                                     User user, String searchName) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        UserType userType = UserType.ofEnumByCode(user.getUserType());
        Agent_info agent_info = new Agent_info();
        //1.获取数据源下分类信息,如果是系统管理员,则不过滤部门
        if (UserType.XiTongGuanLiYuan != userType) {
            asmSql.addSql("SELECT * FROM agent_info ai join data_source ds on ai.source_id = ds.source_id JOIN" +
                    " source_relation_dep srd ON ds.source_id = srd.source_id JOIN collect_job_classify cjc" +
                    " ON ai.agent_id = cjc.agent_id where srd.dep_id = ?").addParam(user.getDepId());
            if (StringUtil.isNotBlank(source_id)) {
                agent_info.setSource_id(source_id);
                asmSql.addSql(" AND ds.source_id = ?").addParam(agent_info.getSource_id());
            }
        } else {
            asmSql.addSql("SELECT t3.datasource_name,* FROM collect_job_classify t1 JOIN agent_info t2 ON" +
                    " t2.agent_id = t1.agent_id JOIN data_source t3 ON t3.source_id = t2.source_id");
            if (StringUtil.isNotBlank(source_id)) {
                agent_info.setSource_id(source_id);
                asmSql.addSql(" WHERE t2.source_id = ? ").addParam(agent_info.getSource_id());
            }
        }
        if (!showFileCollection) {
            asmSql.addSql(" AND agent_type not in (?,?)").addParam(AgentType.WenJianXiTong.getCode())
                    .addParam(AgentType.FTP.getCode());
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取分类id获取分类下表信息",
            logicStep = "1.获取分类id获取分类下表信息")
    @Param(name = "classify_id", desc = "分类id", range = "分类id,唯一")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "分类下表信息", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchTableInfos(String classify_id, User user) {
        return getDCLBatchTableInfos(classify_id, null, user);
    }

    @Method(desc = "获取分类id获取分类下表信息", logicStep = "1.获取分类id获取分类下表信息")
    @Param(name = "classify_id", desc = "分类id", range = "分类id,唯一")
    @Param(name = "classify_name", desc = "分类名称", range = "String字符串,512长度")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "分类下表信息", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchTableInfos(String classify_id, String classify_name, User user) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        //1.根据分类id获取分类下表信息
        asmSql.clean();
        UserType userType = UserType.ofEnumByCode(user.getUserType());
        Database_set database_set = new Database_set();
        Collect_job_classify classify = new Collect_job_classify();
        if (UserType.XiTongGuanLiYuan != userType) {
            asmSql.addSql("SELECT t2.task_name,t1.*,t3.* FROM data_store_reg t1 JOIN database_set t2 ON" +
                    " t1.database_id = t2.database_id JOIN collect_job_classify t3 ON" +
                    " t3.classify_id = t2.classify_id JOIN source_relation_dep t4 ON t1.source_id = t4.source_id "
                + " WHERE t1.collect_type = ?");
        } else {
            asmSql.addSql("SELECT t2.task_name,t1.*,t3.* FROM data_store_reg t1 JOIN database_set t2 ON" +
                    " t1.database_id = t2.database_id JOIN collect_job_classify t3 ON" +
                    " t3.classify_id = t2.classify_id WHERE t1.collect_type = ?");
        }
        asmSql.addParam(AgentType.DBWenJian.getCode());
        if (StringUtil.isNotBlank(classify_id)) {
            database_set.setClassify_id(classify_id);
            asmSql.addSql(" AND t2.classify_id = ?").addParam(database_set.getClassify_id());
        }
        if (StringUtil.isNotBlank(classify_name)) {
            classify.setClassify_name("%" + classify_name + "%");
            asmSql.addSql(" AND t1.table_name like ? OR t1.hyren_name like ? OR t1.original_name like ? OR" +
                    " t2.task_name like ? OR t2.database_number like ?");
            asmSql.addParam(classify.getClassify_name()).addParam(classify.getClassify_name())
                    .addParam(classify.getClassify_name()).addParam(classify.getClassify_name())
                    .addParam(classify.getClassify_name());
        }
        if (UserType.XiTongGuanLiYuan != userType) {
            asmSql.addSql(" AND t4.dep_id = ?").addParam(user.getDepId());
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "根据表id获取DCL层批量表信息", logicStep = "根据表id获取DCL层批量表信息")
    @Param(name = "file_id", desc = "表源属性id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Map<String, Object> getDCLBatchTableInfo(String file_id) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT dsr.file_id,ti.table_ch_name,ti.table_name,ti.remark,ti.table_id,dsr.source_id,dsr" +
                ".agent_id,ti.database_id,dsr.original_update_date FROM data_store_reg dsr JOIN table_info ti" +
                " ON dsr.database_id = ti.database_id AND dsr.table_name = ti.table_name WHERE dsr.file_id =?");
        asmSql.addParam(file_id);
        return Dbo.queryOneObject(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "根据表id获取DCL层批量表字段",
            logicStep = "根据表id获取DCL层批量表字段")
    @Param(name = "file_id", desc = "表源属性id", range = "String字符串,唯一")
    @Return(desc = "表字段信息列表", range = "返回值取值范围")
    public static List<Map<String, Object>> getDCLBatchTableColumns(String file_id) {
        //设置 Data_store_reg 对象
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(file_id);
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT tc.column_ch_name,tc.column_name,tc.tc_remark,tc.column_type,tc.is_primary_key," +
                "tc.column_id,ti.database_id,dsr.agent_id,dsr.source_id FROM data_store_reg dsr JOIN " +
                "table_info ti ON dsr.database_id = ti.database_id AND dsr.table_name = ti.table_name JOIN " +
                "table_column tc ON ti.table_id = tc.table_id  WHERE dsr.file_id = ?").addParam(dsr.getFile_id());
        List<Map<String, Object>> column_list = Dbo.queryList(asmSql.sql(), asmSql.params());
        if (column_list.isEmpty()) {
            throw new BusinessException("表的Mate信息查询结果为空!");
        }
        return column_list;
    }

    @Method(desc = "根据表名和数据库设置id获取DCL层批量表字段",
            logicStep = "根据表名和数据库设置id获取DCL层批量表字段")
    @Param(name = "collect_set_id", desc = "数据库设置ID或文件设置ID", range = "long类型")
    @Param(name = "table_name", desc = "原始表名", range = "String类型")
    @Return(desc = "表字段信息列表", range = "返回值取值范围")
    public static List<Map<String, Object>> getDCLBatchTableColumns(long table_id) {
        //设置 Data_store_reg 对象
        Data_store_reg dsr = new Data_store_reg();
        dsr.setTable_id(table_id);
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT tc.column_ch_name,tc.column_name,tc.tc_remark,tc.column_type,tc.is_primary_key," +
                " tc.column_id,ti.database_id FROM table_info ti JOIN table_column tc" +
                " ON ti.table_id = tc.table_id WHERE ti.table_id = ?")
                .addParam(dsr.getTable_id());
        List<Map<String, Object>> column_list = Dbo.queryList(asmSql.sql(), asmSql.params());
        if (column_list.isEmpty()) {
            throw new BusinessException("表的Mate信息查询结果为空!");
        }
        return column_list;
    }

//    @Method(desc = "获取流数据管理的groupId信息",
//            logicStep = "1.获取流数据管理的groupId信息")
//    public static List<Map<String, Object>> getDCLRealTimeDataInfos() {
//        return Dbo.queryList("select distinct(sdm_cons_para_val) groupid from sdm_cons_para where" +
//                " (sdm_conf_para_na = 'groupid' or sdm_conf_para_na = 'application.id')");
//    }
//
//    @Method(desc = "获取kafka_topic信息",
//            logicStep = "1.获取kafka_topic信息")
//    @Param(name = "groupId", desc = "流数据管理的groupId", range = "String类型")
//    @Return(desc = "获取kafka_topic信息", range = "无限制")
//    public static List<Map<String, Object>> getDCLRealTimeTopicInfos(String groupId) {
//
//        return getDCLRealTimeTopicInfos(groupId, null);
//    }
//
//    @Method(desc = "获取kafka_topic信息",
//            logicStep = "1.获取kafka_topic信息")
//    @Param(name = "groupId", desc = "流数据管理的groupId", range = "String类型")
//    @Param(name = "topicName", desc = "kafka的topic名称", range = "String类型")
//    @Return(desc = "获取kafka_topic信息", range = "无限制")
//    public static List<Map<String, Object>> getDCLRealTimeTopicInfos(String groupId, String topicName) {
//        Result result = Dbo.queryResult("select sdm_consum_id from sdm_cons_para where ((sdm_conf_para_na =" +
//                " 'groupid' and sdm_cons_para_val = ? ) or (sdm_conf_para_na = 'application.id' and" +
//                " sdm_cons_para_val = ? ))", groupId, groupId);
//        asmSql.clean();
//        asmSql.addSql("select sdm_cons_para_val,sdm_consum_id from sdm_cons_para where sdm_conf_para_na = 'topic'" +
//                " and sdm_consum_id in (");
//        for (int i = 0; i < result.getRowCount(); i++) {
//            if (i == result.getRowCount() - 1) {
//                asmSql.addSql(" ? )");
//            } else {
//                asmSql.addSql(" ? ,");
//            }
//        }
//        for (int i = 0; i < result.getRowCount(); i++) {
//            Sdm_cons_para sdm_cons_para = new Sdm_cons_para();
//            sdm_cons_para.setSdm_conf_para_id(result.getString(i, "sdm_consum_id"));
//            asmSql.addParam(sdm_cons_para.getSdm_consum_id());
//        }
//        Result queryResult = Dbo.queryResult(asmSql.sql(), asmSql.params());
//        for (int i = 0; i < queryResult.getRowCount(); i++) {
//            result.setObject(i, "groupid", groupId);
//        }
//        return queryResult.toList();
//    }
//
//    @Method(desc = "获取流数据消费到海云内部的表的信息",
//            logicStep = "1.获取流数据消费到海云内部的表的信息")
//    @Param(name = "sdm_consum_id", desc = "流数据消费id", range = "String类型")
//    @Return(desc = "流数据消费到海云内部的表的信息", range = "无限制")
//    public static List<Map<String, Object>> getDCLRealTimeInnerTableInfos(String sdm_consum_id) {
//
//        return getDCLRealTimeInnerTableInfos(sdm_consum_id, null);
//    }
//
//
//    @Method(desc = "获取流数据消费到海云内部的表的信息",
//            logicStep = "1.获取流数据消费到海云内部的表的信息")
//    @Param(name = "sdm_consum_id", desc = "流数据消费id", range = "String类型")
//    @Param(name = "table_name", desc = "流数据消费的内部表表名", range = "String类型")
//    @Return(desc = "流数据消费到海云内部的表的信息", range = "无限制")
//    public static List<Map<String, Object>> getDCLRealTimeInnerTableInfos(String sdm_consum_id, String table_name) {
//        Sdm_consume_des sdm_consume_des = new Sdm_consume_des();
//        List<Map<String, Object>> sdm_inner_tables = null;
//        if (StringUtil.isNotBlank(sdm_consum_id)) {
//            sdm_consume_des.setSdm_consum_id(sdm_consum_id);
//            asmSql.clean();
//            asmSql.addSql("select sdm_des_id,sdm_cons_des,hyren_consumedes from sdm_consume_des where" +
//                    " sdm_consum_id = ?").addParam(sdm_consume_des.getSdm_consum_id());
//            Result sdm_consume_desResult = Dbo.queryResult(asmSql.sql(), asmSql.params());
//            ArrayList<String> list = new ArrayList<>();
//            if (!sdm_consume_desResult.isEmpty()) {
//                for (int i = 0; i < sdm_consume_desResult.getRowCount(); i++) {
//                    String sdm_des_id = sdm_consume_desResult.getString(i, "sdm_des_id");
//                    String sdm_cons_des = sdm_consume_desResult.getString(i, "sdm_cons_des");
//                    String hyren_consumedes = sdm_consume_desResult.getString(i, "hyren_consumedes");
//                    //TODO ConsDirection代码项 1:内部,2:外部
//                    if ("1".equals(sdm_cons_des)) {
//                        sdm_consume_des.setSdm_des_id(sdm_des_id);
//                        // TODO HyrenConsumeDes代码项 1:HBASE,2:MPP,3:HBASEONSOLR,4:HDFS,5:DRUID,6:SPARKD;
//                        if ("1".equals(hyren_consumedes)) {
//                            putHbasenameBysdm_des_id(sdm_consume_des, list);
//                        } else if ("2".equals(hyren_consumedes)) {
//                            putDbnameBysdm_des_id(sdm_consume_des, list);
//                        } else if ("4".equals(hyren_consumedes)) {
//                            putFilenameBysdm_des_id(sdm_consume_des, list);
//                        }
//                    }
//                }
//            }
//            if (list.size() > 0) {
//                String[] strs = new String[list.size()];
//                for (int i = 0; i < list.size(); i++) {
//                    strs[i] = list.get(i);
//                }
//                asmSql.clean();
//                asmSql.addSql("SELECT table_id,table_en_name,table_cn_name,create_date,create_time FROM sdm_inner_table" +
//                        " where 1=1");
//                asmSql.addORParam("table_en_name", strs);
//                sdm_inner_tables = Dbo.queryList(asmSql.sql(), asmSql.params());
//            }
//        }
//        Result rs = new Result();
//        if (StringUtil.isNotBlank(table_name)) {
//            if (null != sdm_inner_tables && !sdm_inner_tables.isEmpty()) {
//                for (Map<String, Object> sdm_inner_table : sdm_inner_tables) {
//                    if (sdm_inner_table.get("table_en_name").equals(table_name)) {
//                        rs.add((Result) sdm_inner_table);
//                    }
//                }
//                for (int i = 0; i < rs.getRowCount(); i++) {
//                    rs.setObject(i, "sdm_consum_id", sdm_consum_id);
//                }
//            }
//            return rs.toList();
//        }
//        if (null != sdm_inner_tables && !sdm_inner_tables.isEmpty()) {
//            for (Map<String, Object> sdm_inner_table : sdm_inner_tables) {
//                sdm_inner_table.put("sdm_consum_id", sdm_consum_id);
//            }
//        }
//        return sdm_inner_tables;
//    }
//
//    @Method(desc = "根据表id获取DCL层实时表字段",
//            logicStep = "根据表id获取DCL层实时表字段")
//    @Param(name = "file_id", desc = "表源属性id", range = "String字符串,唯一")
//    @Return(desc = "表字段信息列表", range = "返回值取值范围")
//    public static List<Map<String, Object>> getDCLBatchRealTimeTableColumnsById(String file_id) {
//        throw new BusinessException("获取实时数据表的字段信息暂未实现!");
//    }
//
//    @Method(desc = "设置英文表名",
//            logicStep = "设置英文表名")
//    @Param(name = "sdm_consume_des", desc = "Sdm_consume_des实体的对象", range = "Sdm_consume_des实体的对象")
//    @Param(name = "list", desc = "文件名的List", range = "String的List集合")
//    private static void putHbasenameBysdm_des_id(Sdm_consume_des sdm_consume_des, ArrayList<String> list) {
//
//        asmSql.clean();
//        asmSql.addSql("SELECT hbase_name FROM sdm_con_hbase where sdm_des_id = ?");
//        asmSql.addParam(sdm_consume_des.getSdm_des_id());
//        String hbase_name = Dbo.queryResult(asmSql.sql(), asmSql.params()).getString(0, "hbase_name");
//        list.add(hbase_name);
//    }
//
//    @Method(desc = "设置英文表名",
//            logicStep = "设置英文表名")
//    @Param(name = "sdm_consume_des", desc = "Sdm_consume_des实体的对象", range = "Sdm_consume_des实体的对象")
//    @Param(name = "list", desc = "文件名的List", range = "String的List集合")
//    private static void putDbnameBysdm_des_id(Sdm_consume_des sdm_consume_des, ArrayList<String> list) {
//        asmSql.clean();
//        asmSql.addSql("SELECT sdm_tb_name_en FROM sdm_con_to_db where sdm_des_id = ?");
//        asmSql.addParam(sdm_consume_des.getSdm_des_id());
//        String sdm_tb_name_en = Dbo.queryResult(asmSql.sql(), asmSql.params())
//                .getString(0, "sdm_tb_name_en");
//        list.add(sdm_tb_name_en);
//    }
//
//    @Method(desc = "设置文件名",
//            logicStep = "设置文件名")
//    @Param(name = "sdm_consume_des", desc = "Sdm_consume_des实体的对象", range = "Sdm_consume_des实体的对象")
//    @Param(name = "list", desc = "文件名的List", range = "String的List集合")
//    private static void putFilenameBysdm_des_id(Sdm_consume_des sdm_consume_des, List<String> list) {
//        asmSql.clean();
//        asmSql.addSql("SELECT file_name FROM sdm_con_file where sdm_des_id = ?");
//        asmSql.addParam(sdm_consume_des.getSdm_des_id());
//        String file_name = Dbo.queryResult(asmSql.sql(), asmSql.params()).getString(0, "file_name");
//        if (file_name.contains(".")) {
//            file_name = file_name.substring(0, (file_name.lastIndexOf(".")));
//        }
//        list.add(file_name);
//    }
}
