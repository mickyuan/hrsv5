package hrds.commons.tree.foreground.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.UserType;
import hrds.commons.entity.*;
import hrds.commons.utils.Constant;
import hrds.commons.utils.User;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "贴源层(DCL)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:10")
public class DCLDataQuery {

    @Method(desc = "获取登录用户的批量数据的数据源列表(未使用)",
            logicStep = "1.获取登录用户的数据源列表")
    @Return(desc = "数据源列表", range = "无限制")
    public static List<Map<String, Object>> getDCLDataInfos() {
        List<Map<String, Object>> dclDataInfos = new ArrayList<>();
        Map<String, Object> map;
        map = new HashMap<>();
        //1.添加批量数据子级文件夹
        map.put("batch_id", Constant.DCL_BATCH);
        map.put("name", "批量数据");
        map.put("description", "批量数据管理");
        map.put("rootName", DataSourceType.DCL.getValue());
        map.put("source", Constant.DCL_BATCH);
        map.put("pId", DataSourceType.DCL.getCode());
        map.put("id", Constant.DCL_BATCH);
        map.put("isParent", true);
        dclDataInfos.add(map);
        //2.添加实时数据子级文件夹
//		map = new HashMap<>();
//		map.put("kafka_id", Constant.REALTIME_TYPE);
//		map.put("name", "实时数据");
//		map.put("description", "实时数据管理");
//		map.put("rootName", PathUtil.DCL);
//		map.put("source", Constant.DCL_REALTIME);
//		map.put("pId", DataSourceType.DCL.getCode());
//		map.put("id", Constant.REALTIME_TYPE);
//		map.put("isParent", true);
//		treeDataList.add(map);
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

        asmSql.addSql("select distinct t1.* from " + Data_source.TableName + " t1 left join " + Source_relation_dep.TableName + " t2 on t1.source_id = t2.source_id ");
        //如果不是系统管理员,则过滤部门
        if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
            asmSql.addSql("where t2.dep_id = ? ");
            asmSql.addParam(user.getDepId());
        }
        //如果数据源名称不为空,模糊查询获取数据源信息
        if (!StringUtil.isBlank(dataSourceName)) {
            if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
                asmSql.addSql(" and ");
            } else {
                asmSql.addSql(" where ");
            }
            asmSql.addSql(" ( lower(datasource_name) like ? OR lower(datasource_number) like ? )");
            asmSql.addParam('%' + dataSourceName.toLowerCase() + '%').addParam('%' + dataSourceName.toLowerCase() + '%');
        }
        //3.获取查询结果集
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取批量数据下数据源下分类信息",
            logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
    @Param(name = "source_id", desc = "数据源id", range = "数据源id,唯一")
    @Param(name = "isFileCollection", desc = "是否文件采集", range = "true:是,false:否")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "加工信息列表", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchClassifyInfos(String source_id, User user) {
        return getDCLBatchClassifyInfos(source_id, user, null);
    }

    @Method(desc = "获取批量数据下数据源下分类信息",
            logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
    @Param(name = "source_id", desc = "数据源id", range = "数据源id,唯一")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "加工信息列表", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchClassifyInfos(String source_id, User user,
                                                                     String searchName) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.addSql("select distinct t1.* from " + Collect_job_classify.TableName + " t1 left join " + Agent_info.TableName + " t2 on t1.agent_id = t2.agent_id left join " + Source_relation_dep.TableName
                + " t3 on t2.source_id = t3.source_id");
        //1.获取数据源下分类信息,如果是系统管理员,则不过滤部门
        if (UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
            //如果是有数据源的话 根据数据源搜索
            if (!StringUtils.isEmpty(source_id)) {
                Agent_info agent_info = new Agent_info();
                agent_info.setSource_id(source_id);
                asmSql.addSql(" where t2.source_id = ? ");
                asmSql.addParam(agent_info.getSource_id());
            }
            //如果是模糊查询 根据模糊查询搜索
            else if (!StringUtils.isEmpty(searchName)) {
                asmSql.addSql(" where (lower(t1.classify_num) like ? or lower(t1.classify_name) like ?)");
                asmSql.addParam("%" + searchName.toLowerCase() + "%");
                asmSql.addParam("%" + searchName.toLowerCase() + "%");
            }
        }
        //如果不是系统管理员
        else {
            //如果有source_id 根据source_id 搜索过滤部门
            if (!StringUtils.isEmpty(source_id)) {
                Agent_info agent_info = new Agent_info();
                agent_info.setSource_id(source_id);
                asmSql.addSql(" where t2.source_id = ? and t3.dep_id = ?");
                asmSql.addParam(agent_info.getSource_id());
                asmSql.addParam(user.getDepId());
            }
            //如果是模糊查询，根据模糊查询，过滤部门搜索
            else if (!StringUtils.isEmpty(searchName)) {
                asmSql.addSql(" where (lower(t1.classify_num) like ? or lower(t1.classify_name) like ?) and t3.dep_id = ?");
                asmSql.addParam("%" + searchName.toLowerCase() + "%");
                asmSql.addParam("%" + searchName.toLowerCase() + "%");
                asmSql.addParam(user.getDepId());
            }
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取分类id获取分类下表信息",
            logicStep = "1.获取分类id获取分类下表信息")
    @Param(name = "classify_id", desc = "分类id", range = "分类id,唯一")
    @Param(name = "classify_name", desc = "分类名称", range = "String字符串,512长度")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "分类下表信息", range = "无限制")
    public static List<Map<String, Object>> getDCLBatchTableInfos(String classify_id, String table_name, User user) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        //1.获取分类id获取分类下表信息

        asmSql.addSql("select distinct t1.* from "+Data_store_reg.TableName+" t1 left join "+Collect_job_classify.TableName+" t2 on t1.agent_id = t2.agent_id left join "+Agent_info.TableName+
                " t3 on t1.agent_id = t3.agent_id left join "+Source_relation_dep.TableName+" t4 on t3.source_id = t4.source_id");
        if (UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
            if (!StringUtils.isEmpty(classify_id)) {
                Database_set database_set = new Database_set();
                database_set.setClassify_id(classify_id);
                asmSql.addSql(" where t2.classify_id = ?");
                asmSql.addParam(database_set.getClassify_id());
            } else if (!StringUtils.isEmpty(table_name)) {
                asmSql.addSql(" where lower(t1.hyren_name) like ");
                asmSql.addParam("%" + table_name.toLowerCase() + "%");
            }
        } else {
            if (!StringUtils.isEmpty(classify_id)) {
                Database_set database_set = new Database_set();
                database_set.setClassify_id(classify_id);
                asmSql.addSql(" where t2.classify_id = ? and t4.dep_id = ?");
                asmSql.addParam(database_set.getClassify_id());
                asmSql.addParam(user.getDepId());
            } else if (!StringUtils.isEmpty(table_name)) {
                asmSql.addSql(" where lower(t1.hyren_name) like ?  and t4.dep_id = ?");
                asmSql.addParam("%" + table_name.toLowerCase() + "%");
                asmSql.addParam(user.getDepId());
            }
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

//	@Method(desc = "获取流数据管理的groupId信息",
//			logicStep = "1.获取流数据管理的groupId信息")
//	public static List<Map<String, Object>> getDCLRealTimeDataInfos() {
//		return Dbo.queryList("select distinct(sdm_cons_para_val) groupid from sdm_cons_para where" +
//				" (sdm_conf_para_na = 'groupid' or sdm_conf_para_na = 'application.id')");
//	}
//
//	@Method(desc = "获取kafka_topic信息",
//			logicStep = "1.获取kafka_topic信息")
//	@Param(name = "groupId", desc = "流数据管理的groupId", range = "String类型")
//	@Return(desc = "获取kafka_topic信息", range = "无限制")
//	public static List<Map<String, Object>> getDCLRealTimeTopicInfos(String groupId) {
//
//		return getDCLRealTimeTopicInfos(groupId, null);
//	}
//
//	@Method(desc = "获取kafka_topic信息",
//			logicStep = "1.获取kafka_topic信息")
//	@Param(name = "groupId", desc = "流数据管理的groupId", range = "String类型")
//	@Param(name = "topicName", desc = "kafka的topic名称", range = "String类型")
//	@Return(desc = "获取kafka_topic信息", range = "无限制")
//	public static List<Map<String, Object>> getDCLRealTimeTopicInfos(String groupId, String topicName) {
//		Result result = Dbo.queryResult("select sdm_consum_id from sdm_cons_para where ((sdm_conf_para_na =" +
//				" 'groupid' and sdm_cons_para_val = ? ) or (sdm_conf_para_na = 'application.id' and" +
//				" sdm_cons_para_val = ? ))", groupId, groupId);
//		asmSql.clean();
//		asmSql.addSql("select sdm_cons_para_val,sdm_consum_id from sdm_cons_para where sdm_conf_para_na = 'topic'" +
//				" and sdm_consum_id in (");
//		for (int i = 0; i < result.getRowCount(); i++) {
//			if (i == result.getRowCount() - 1) {
//				asmSql.addSql(" ? )");
//			} else {
//				asmSql.addSql(" ? ,");
//			}
//		}
//		for (int i = 0; i < result.getRowCount(); i++) {
//			Sdm_cons_para sdm_cons_para = new Sdm_cons_para();
//			sdm_cons_para.setSdm_conf_para_id(result.getString(i, "sdm_consum_id"));
//			asmSql.addParam(sdm_cons_para.getSdm_consum_id());
//		}
//		Result queryResult = Dbo.queryResult(asmSql.sql(), asmSql.params());
//		for (int i = 0; i < queryResult.getRowCount(); i++) {
//			result.setObject(i, "groupid", groupId);
//		}
//		return queryResult.toList();
//	}
//
//	@Method(desc = "获取流数据消费到海云内部的表的信息",
//			logicStep = "1.获取流数据消费到海云内部的表的信息")
//	@Param(name = "sdm_consum_id", desc = "流数据消费id", range = "String类型")
//	@Return(desc = "流数据消费到海云内部的表的信息", range = "无限制")
//	public static List<Map<String, Object>> getDCLRealTimeInnerTableInfos(String sdm_consum_id) {
//
//		return getDCLRealTimeInnerTableInfos(sdm_consum_id, null);
//	}
//
//
//	@Method(desc = "获取流数据消费到海云内部的表的信息",
//			logicStep = "1.获取流数据消费到海云内部的表的信息")
//	@Param(name = "sdm_consum_id", desc = "流数据消费id", range = "String类型")
//	@Param(name = "table_name", desc = "流数据消费的内部表表名", range = "String类型")
//	@Return(desc = "流数据消费到海云内部的表的信息", range = "无限制")
//	public static List<Map<String, Object>> getDCLRealTimeInnerTableInfos(String sdm_consum_id, String table_name) {
//		Sdm_consume_des sdm_consume_des = new Sdm_consume_des();
//		List<Map<String, Object>> sdm_inner_tables = null;
//		if (StringUtil.isNotBlank(sdm_consum_id)) {
//			sdm_consume_des.setSdm_consum_id(sdm_consum_id);
//			asmSql.clean();
//			asmSql.addSql("select sdm_des_id,sdm_cons_des,hyren_consumedes from sdm_consume_des where" +
//					" sdm_consum_id = ?").addParam(sdm_consume_des.getSdm_consum_id());
//			Result sdm_consume_desResult = Dbo.queryResult(asmSql.sql(), asmSql.params());
//			ArrayList<String> list = new ArrayList<>();
//			if (!sdm_consume_desResult.isEmpty()) {
//				for (int i = 0; i < sdm_consume_desResult.getRowCount(); i++) {
//					String sdm_des_id = sdm_consume_desResult.getString(i, "sdm_des_id");
//					String sdm_cons_des = sdm_consume_desResult.getString(i, "sdm_cons_des");
//					String hyren_consumedes = sdm_consume_desResult.getString(i, "hyren_consumedes");
//					//TODO ConsDirection代码项 1:内部,2:外部
//					if ("1".equals(sdm_cons_des)) {
//						sdm_consume_des.setSdm_des_id(sdm_des_id);
//						// TODO HyrenConsumeDes代码项 1:HBASE,2:MPP,3:HBASEONSOLR,4:HDFS,5:DRUID,6:SPARKD;
//						if ("1".equals(hyren_consumedes)) {
//							putHbasenameBysdm_des_id(sdm_consume_des, list);
//						} else if ("2".equals(hyren_consumedes)) {
//							putDbnameBysdm_des_id(sdm_consume_des, list);
//						} else if ("4".equals(hyren_consumedes)) {
//							putFilenameBysdm_des_id(sdm_consume_des, list);
//						}
//					}
//				}
//			}
//			if (list.size() > 0) {
//				String[] strs = new String[list.size()];
//				for (int i = 0; i < list.size(); i++) {
//					strs[i] = list.get(i);
//				}
//				asmSql.clean();
//				asmSql.addSql("SELECT table_id,table_en_name,table_cn_name,create_date,create_time FROM sdm_inner_table" +
//						" where 1=1");
//				asmSql.addORParam("table_en_name", strs);
//				sdm_inner_tables = Dbo.queryList(asmSql.sql(), asmSql.params());
//			}
//		}
//		Result rs = new Result();
//		if (StringUtil.isNotBlank(table_name)) {
//			if (null != sdm_inner_tables && !sdm_inner_tables.isEmpty()) {
//				for (Map<String, Object> sdm_inner_table : sdm_inner_tables) {
//					if (sdm_inner_table.get("table_en_name").equals(table_name)) {
//						rs.add((Result) sdm_inner_table);
//					}
//				}
//				for (int i = 0; i < rs.getRowCount(); i++) {
//					rs.setObject(i, "sdm_consum_id", sdm_consum_id);
//				}
//			}
//			return rs.toList();
//		}
//		if (null != sdm_inner_tables && !sdm_inner_tables.isEmpty()) {
//			for (Map<String, Object> sdm_inner_table : sdm_inner_tables) {
//				sdm_inner_table.put("sdm_consum_id", sdm_consum_id);
//			}
//		}
//		return sdm_inner_tables;
//	}
//
//	@Method(desc = "设置英文表名",
//			logicStep = "设置英文表名")
//	@Param(name = "sdm_consume_des", desc = "Sdm_consume_des实体的对象", range = "Sdm_consume_des实体的对象")
//	@Param(name = "list", desc = "文件名的List", range = "String的List集合")
//	private static void putHbasenameBysdm_des_id(Sdm_consume_des sdm_consume_des, ArrayList<String> list) {
//
//		asmSql.clean();
//		asmSql.addSql("SELECT hbase_name FROM sdm_con_hbase where sdm_des_id = ?");
//		asmSql.addParam(sdm_consume_des.getSdm_des_id());
//		String hbase_name = Dbo.queryResult(asmSql.sql(), asmSql.params()).getString(0, "hbase_name");
//		list.add(hbase_name);
//	}
//
//	@Method(desc = "设置英文表名",
//			logicStep = "设置英文表名")
//	@Param(name = "sdm_consume_des", desc = "Sdm_consume_des实体的对象", range = "Sdm_consume_des实体的对象")
//	@Param(name = "list", desc = "文件名的List", range = "String的List集合")
//	private static void putDbnameBysdm_des_id(Sdm_consume_des sdm_consume_des, ArrayList<String> list) {
//		asmSql.clean();
//		asmSql.addSql("SELECT sdm_tb_name_en FROM sdm_con_to_db where sdm_des_id = ?");
//		asmSql.addParam(sdm_consume_des.getSdm_des_id());
//		String sdm_tb_name_en = Dbo.queryResult(asmSql.sql(), asmSql.params())
//				.getString(0, "sdm_tb_name_en");
//		list.add(sdm_tb_name_en);
//	}
//
//	@Method(desc = "设置文件名",
//			logicStep = "设置文件名")
//	@Param(name = "sdm_consume_des", desc = "Sdm_consume_des实体的对象", range = "Sdm_consume_des实体的对象")
//	@Param(name = "list", desc = "文件名的List", range = "String的List集合")
//	private static void putFilenameBysdm_des_id(Sdm_consume_des sdm_consume_des, List<String> list) {
//		asmSql.clean();
//		asmSql.addSql("SELECT file_name FROM sdm_con_file where sdm_des_id = ?");
//		asmSql.addParam(sdm_consume_des.getSdm_des_id());
//		String file_name = Dbo.queryResult(asmSql.sql(), asmSql.params()).getString(0, "file_name");
//		if (file_name.contains(".")) {
//			file_name = file_name.substring(0, (file_name.lastIndexOf(".")));
//		}
//		list.add(file_name);
//	}
}
