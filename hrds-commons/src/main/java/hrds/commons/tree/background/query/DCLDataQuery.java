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
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UserType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.utils.Constant;
import hrds.commons.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Method(desc = "获取批量数据的数据源列表,根据数据源名称模糊查询",
		logicStep = "1.如果是系统管理员,则不过滤部门" +
			"2.获取查询结果集")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Param(name = "dataSourceName", desc = "查询数据源的名称", range = "String类型字符,512长度", nullable = true)
	@Return(desc = "数据源列表", range = "无限制")
	public static List<Map<String, Object>> getDCLBatchDataInfos(User user) {
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT distinct ds.source_id, ds.datasource_name from source_relation_dep srd JOIN " +
			"data_source ds on srd.SOURCE_ID = ds.SOURCE_ID");
		//1.如果不是系统管理员,则过滤部门
		UserType userType = UserType.ofEnumByCode(user.getUserType());
		if (UserType.XiTongGuanLiYuan != userType) {
			asmSql.addSql("where srd.dep_id = ?");
			asmSql.addParam(user.getDepId());
		}
		//2.获取查询结果集
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "获取批量数据下数据源下分类信息",
		logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
	@Param(name = "source_id", desc = "数据源id", range = "数据源id,唯一")
	@Param(name = "showFileCollection", desc = "是否获取文件采集任务采集数据", range = "true:是,false:否")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "加工信息列表", range = "无限制")
	public static List<Map<String, Object>> getDCLBatchClassifyInfos(String source_id, Boolean showFileCollection, User user) {
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

	@Method(desc = "获取批量数据下半结构化采集任务信息",
		logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
	@Param(name = "source_id", desc = "数据源id", range = "数据源id,唯一")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "对象采集信息列表", range = "无限制")
	public static List<Map<String, Object>> getDCLBatchObjectCollectInfos(String source_id, User user) {
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		UserType userType = UserType.ofEnumByCode(user.getUserType());
		Agent_info agent_info = new Agent_info();
		//1.获取数据源下分类信息,如果是系统管理员,则不过滤部门
		if (UserType.XiTongGuanLiYuan != userType) {
			asmSql.addSql(
				"SELECT oc.obj_collect_name as classify_name,oc.odc_id as classify_id," +
					"oc.obj_number as classify_num,oc.agent_id,ds.source_id,oc.remark"
					+ " FROM " + Object_collect.TableName + " oc"
					+ " JOIN " + Agent_info.TableName + " ai ON oc.agent_id=ai.agent_id"
					+ " JOIN " + Data_source.TableName + " ds" + " ON ai.source_id = ds.source_id"
					+ " JOIN " + Source_relation_dep.TableName + " srd ON ds.source_id = srd.source_id"
					+ " where srd.dep_id = ?").addParam(user.getDepId());
			if (StringUtil.isNotBlank(source_id)) {
				agent_info.setSource_id(source_id);
				asmSql.addSql(" AND ds.source_id = ?").addParam(agent_info.getSource_id());
			}
		} else {
			asmSql.addSql(
				"SELECT oc.obj_collect_name as classify_name,oc.odc_id as classify_id," +
					"oc.obj_number as classify_num,oc.agent_id,ds.source_id,oc.remark"
					+ " FROM " + Object_collect.TableName + " oc"
					+ " JOIN " + Agent_info.TableName + " ai ON oc.agent_id = ai.agent_id"
					+ " JOIN " + Data_source.TableName + " ds ON ds.source_id = ai.source_id");
			if (StringUtil.isNotBlank(source_id)) {
				agent_info.setSource_id(source_id);
				asmSql.addSql(" WHERE ds.source_id = ? ").addParam(agent_info.getSource_id());
			}
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "获取分类id获取分类下表信息", logicStep = "1.获取分类id获取分类下表信息")
	@Param(name = "classify_id", desc = "分类id", range = "分类id,唯一")
	@Param(name = "classify_name", desc = "分类名称", range = "String字符串,512长度")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "分类下表信息", range = "无限制")
	public static List<Map<String, Object>> getDCLBatchTableInfos(String classify_id, User user) {
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		//1.根据分类id获取分类下表信息
		asmSql.clean();
		UserType userType = UserType.ofEnumByCode(user.getUserType());
		Database_set database_set = new Database_set();
		database_set.setClassify_id(classify_id);
		Collect_job_classify classify = new Collect_job_classify();
		if (UserType.XiTongGuanLiYuan != userType) {
			asmSql.addSql("SELECT t2.task_name,t1.*,t3.* FROM data_store_reg t1 JOIN database_set t2 ON" +
				" t1.database_id = t2.database_id JOIN collect_job_classify t3 ON" +
				" t3.classify_id = t2.classify_id JOIN source_relation_dep t4 ON t1.source_id = t4.source_id "
				+ " WHERE t2.classify_id = ?");
		} else {
			asmSql.addSql("SELECT t2.task_name,t1.*,t3.* FROM data_store_reg t1 JOIN database_set t2 ON" +
				" t1.database_id = t2.database_id JOIN collect_job_classify t3 ON" +
				" t3.classify_id = t2.classify_id WHERE t2.classify_id = ?");
		}
		asmSql.addParam(database_set.getClassify_id());
		if (UserType.XiTongGuanLiYuan != userType) {
			asmSql.addSql(" AND t4.dep_id = ?").addParam(user.getDepId());
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "根据对象采集id获取任务下表信息", logicStep = "1.根据对象采集id获取任务下表信息")
	@Param(name = "classify_id", desc = "对象采集id", range = "对象采集id,唯一")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "分类下表信息", range = "无限制")
	public static List<Map<String, Object>> getDCLBatchObjectCollectTableInfos(String odc_id, User user) {
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		//1.根据对象采集id获取任务下表信息
		asmSql.clean();
		UserType userType = UserType.ofEnumByCode(user.getUserType());
		Object_collect object_collect = new Object_collect();
		object_collect.setOdc_id(odc_id);
		if (UserType.XiTongGuanLiYuan != userType) {
			asmSql.addSql(
				"SELECT t1.*,t2.obj_collect_name as classify_name,t2.odc_id as classify_id,t2" +
					".obj_collect_name as task_name,t2.obj_number as classify_num,t2.remark"
					+ " FROM " + Data_store_reg.TableName + " t1"
					+ " JOIN " + Object_collect.TableName + " t2 ON t1.database_id = t2.odc_id"
					+ " JOIN " + Agent_info.TableName + " t3 ON t3.agent_id = t2.agent_id"
					+ " JOIN " + Data_source.TableName + " t4 ON t4.source_id = t3.source_id"
					+ " JOIN " + Source_relation_dep.TableName + " t5 ON t5.source_id = t4.source_id"
					+ " WHERE t2.odc_id = ?");
		} else {
			asmSql.addSql(
				"SELECT t1.*,t2.obj_collect_name as classify_name,t2.odc_id as classify_id,t2" +
					".obj_collect_name as task_name,t2.obj_number as classify_num,t2.remark"
					+ " FROM " + Data_store_reg.TableName + " t1"
					+ " JOIN " + Object_collect.TableName + " t2 ON t1.database_id = t2.odc_id"
					+ " JOIN " + Agent_info.TableName + " t3 ON t3.agent_id = t2.agent_id"
					+ " JOIN " + Data_source.TableName + " t4 ON t4.source_id = t3.source_id"
					+ " WHERE t2.odc_id = ?");
		}
		asmSql.addParam(object_collect.getOdc_id());
		if (UserType.XiTongGuanLiYuan != userType) {
			asmSql.addSql(" AND t5.dep_id = ?").addParam(user.getDepId());
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "根据表id获取DCL层批量表信息", logicStep = "根据表id获取DCL层批量表信息")
	@Param(name = "file_id", desc = "表源属性id", range = "String字符串,唯一")
	@Return(desc = "返回值说明", range = "返回值取值范围")
	public static Map<String, Object> getDCLBatchTableInfo(String file_id) {
		return Dbo.queryOneObject(
			"SELECT dsr.*,ti.table_ch_name FROM " + Data_store_reg.TableName + " dsr"
				+ " JOIN " + Table_info.TableName + " ti"
				+ " ON dsr.database_id = ti.database_id AND dsr.table_name = ti.table_name"
				+ " WHERE dsr.file_id =?"
				+ " UNION"
				+ " SELECT dsr.*,oct.zh_name as table_ch_name FROM " + Data_store_reg.TableName + " dsr"
				+ " JOIN " + Object_collect_task.TableName + " oct"
				+ " ON dsr.database_id = oct.odc_id AND dsr.table_name = oct.en_name" +
				" WHERE dsr.file_id =?", file_id, file_id);
	}

	@Method(desc = "根据表id获取DCL层批量表字段",
		logicStep = "根据表id获取DCL层批量表字段")
	@Param(name = "file_id", desc = "表源属性id", range = "String字符串,唯一")
	@Return(desc = "表字段信息列表", range = "返回值取值范围")
	public static List<Map<String, Object>> getDCLBatchTableColumns(String file_id) {
		//设置 Data_store_reg 对象
		Data_store_reg dsr = new Data_store_reg();
		dsr.setFile_id(file_id);
		//查询Sql
		List<Map<String, Object>> column_list = Dbo.queryList(
			"SELECT tc.column_id as column_id, tc.column_name as column_name,tc.column_ch_name as column_ch_name," +
				" tc.tc_remark as remark,tc.column_type as column_type,tc.is_primary_key as is_primary_key" +
				" FROM " + Data_store_reg.TableName + " dsr" +
				" JOIN " + Table_info.TableName + " ti ON dsr.database_id=ti.database_id AND dsr.table_name=ti.table_name" +
				" JOIN " + Table_column.TableName + " tc ON ti.table_id=tc.table_id" +
				" WHERE dsr.file_id = ?" +
				" UNION" +
				" SELECT ocs.struct_id as column_id, ocs.column_name as column_name," +
				"ocs.data_desc as column_ch_name,ocs.remark,ocs.column_type as column_type,"
				+ "'" + IsFlag.Fou.getCode() + "'" + " as is_primary_key" +
				" FROM " + Data_store_reg.TableName + " dsr"
				+ " JOIN " + Object_collect_task.TableName + " oct"
				+ " ON dsr.database_id=oct.odc_id AND dsr.table_name=oct.en_name"
				+ " JOIN " + Object_collect_struct.TableName + " ocs ON oct.ocs_id=ocs.ocs_id" +
				" WHERE dsr.file_id = ?", dsr.getFile_id(), dsr.getFile_id());
		if (column_list.isEmpty()) {
			throw new BusinessException("表的Mate信息查询结果为空!");
		}
		return column_list;
	}

	@Method(desc = "根据表名和数据库设置id获取DCL层批量表字段",
		logicStep = "根据表名和数据库设置id获取DCL层批量表字段")
	@Param(name = "collect_set_id", desc = "数据库设置ID或文件设置ID", range = "long类型")
	@Param(name = "table_id", desc = "表id", range = "String字符串,唯一")
	@Return(desc = "表字段信息列表", range = "返回值取值范围")
	public static List<Map<String, Object>> getDCLBatchTableColumns(long table_id) {
		//设置 Data_store_reg 对象
		Data_store_reg dsr = new Data_store_reg();
		dsr.setTable_id(table_id);
		//查询Sql
		List<Map<String, Object>> column_list = Dbo.queryList(
			"SELECT tc.column_id as column_id, tc.column_name as column_name,tc.column_ch_name as column_ch_name," +
				" tc.tc_remark as remark,tc.column_type as column_type,tc.is_primary_key as is_primary_key" +
				" FROM " + Table_info.TableName + " ti" +
				" JOIN " + Table_column.TableName + " tc ON ti.table_id=tc.table_id WHERE ti.table_id = ?",
			dsr.getTable_id());
		if (column_list.isEmpty()) {
			throw new BusinessException("表的Mate信息查询结果为空!");
		}
		return column_list;
	}
}
