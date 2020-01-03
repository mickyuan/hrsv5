package hrds.commons.zTree.utils;

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
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PackageUtil;
import hrds.commons.utils.PathUtil;
import hrds.commons.utils.User;
import hrds.commons.zTree.bean.TreePageSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树数据查询类", author = "BY-HLL", createdate = "2019/12/24 0024 上午 10:26")
public class TreeDataQuery {

	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	@Method(desc = "根据web类型获取源树菜单",
			logicStep = "根据web类型获取源树菜单")
	@Param(name = "treeSource", desc = "树来源", range = "树来源页面TreePageSource.TREESOURCE")
	@Return(desc = "源树菜单数据的List", range = "无限制")
	public static List<Map<String, Object>> getTreeMenuInfos(String treeSource) {
		List<Map<String, Object>> sourceTreeMenuInfos = new ArrayList<>();
		String[] layerName = TreePageSource.TREESOURCE.get(treeSource);
		for (int i = 0; i < layerName.length; i++) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", layerName[i]);
			map.put("agent_layer", layerName[i]);
			map.put("isParent", true);
			map.put("rootName", layerName[i]);
			map.put("id", layerName[i]);
			map.put("pId", "~" + i);
			map.put("description", layerName[i]);
			sourceTreeMenuInfos.add(map);
		}
		return sourceTreeMenuInfos;
	}

	@Method(desc = "获取登录用户的批量数据的数据源列表(未使用)",
			logicStep = "1.获取登录用户的数据源列表")
	@Return(desc = "数据源列表", range = "无限制")
	public static void getDCLDataInfos(List<Map<String, Object>> treeDataList) {
		Map<String, Object> map;
		map = new HashMap<>();
		//1.添加批量数据子级文件夹
		map.put("batch_id", Constant.DCL_BATCH);
		map.put("name", "批量数据");
		map.put("description", "批量数据管理");
		map.put("rootName", PathUtil.DCL);
		map.put("source", Constant.DCL_BATCH);
		map.put("pId", DataSourceType.DCL.getCode());
		map.put("id", Constant.DCL_BATCH);
		map.put("isParent", true);
		treeDataList.add(map);
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
	}

	@Method(desc = "获取批量数据的数据源列表",
			logicStep = "1.获取批量数据的数据源列表")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "数据源列表", range = "无限制")
	static List<Map<String, Object>> getDCLBatchDataInfos(User User) {
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
		asmSql.clean();
		asmSql.addSql("SELECT distinct ds.source_id, ds.datasource_name from source_relation_dep srd JOIN " +
				"data_source ds on srd.SOURCE_ID = ds.SOURCE_ID");
		//1.如果数据源名称不为空,模糊查询获取数据源信息
		if (!StringUtil.isBlank(dataSourceName)) {
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

	@Method(desc = "获取集市信息",
			logicStep = "1.获取集市信息")
	@Return(desc = "集市信息列表", range = "无限制")
	static List<Map<String, Object>> getDMLDataInfos() {
		//1.获取集市信息
		return getDMLDataInfos(null);
	}

	@Method(desc = "获取集市信息",
			logicStep = "1.如果集市名称不为空,模糊查询获取集市信息")
	@Param(name = "marketName", desc = "集市名称", range = "marketName")
	@Return(desc = "集市信息列表", range = "无限制")
	public static List<Map<String, Object>> getDMLDataInfos(String marketName) {
		asmSql.clean();
		asmSql.addSql("SELECT * from data_mart_info");
		//1.如果集市名称不为空,模糊查询获取集市信息
		if (!StringUtil.isBlank(marketName)) {
			asmSql.addSql(" where mart_name like ?").addParam('%' + marketName + '%');
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "获取加工信息",
			logicStep = "1.获取集市信息")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "加工信息列表", range = "无限制")
	static List<Map<String, Object>> getDPLDataInfos(User user) {
		return getDPLDataInfos(user, null);
	}

	@Method(desc = "获取加工信息",
			logicStep = "1.获取加工信息" +
					"2.如果工程名不为空,根据工程名检索加工工程信息")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Param(name = "proName", desc = "加工的工程名", range = "String类型,长度512")
	@Return(desc = "加工信息列表", range = "无限制")
	public static List<Map<String, Object>> getDPLDataInfos(User user, String proName) {
		asmSql.clean();
		asmSql.addSql("SELECT * from edw_modal_project");
		UserType userType = UserType.ofEnumByCode(user.getUserType());
		if (UserType.JiShiJiaGongGuanLiYuan == userType) {
			asmSql.addSql(" where create_id = ?").addParam(user.getUserId());
		}
		if (!StringUtil.isBlank(proName)) {
			asmSql.addSql(" where pro_name like ?").addParam('%' + proName + '%');
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "获取机器学习项目信息（模型层）",
			logicStep = "1.获取机器学习项目信息（模型层）" +
					"注意:只查询机器学习项目状态为'已发布'的项目数据")
	@Return(desc = "模型信息列表", range = "无限制")
	@Deprecated
	static List<Map<String, Object>> getModelProjectInfos() {
		//TODO 参数 publish_status要使用 PublishStatus 代码项 0:未发布,1:已发布,2:已下线,3:已删除
		return Dbo.queryList("SELECT project_id,project_name,project_desc FROM ml_project_info WHERE" +
				" publish_status = ?", 1);
	}

	@Method(desc = "获取数据管控信息",
			logicStep = "1.获取数据管控信息")
	@Return(desc = "数据管控信息列表", range = "无限制")
	static List<Map<String, Object>> getDQCDataInfos() {
		return getDQCDataInfos(null);
	}

	@Method(desc = "获取数据管控信息",
			logicStep = "1.获取数据管控信息" +
					"2.如果查询表名不为空,模糊检索数据管控表信息")
	@Param(name = "tableName", desc = "数据管控表名", range = "String类型,长度64")
	@Return(desc = "数据管控信息列表", range = "无限制")
	public static List<Map<String, Object>> getDQCDataInfos(String tableName) {
		//1.获取数据管控信息
		asmSql.clean();
		asmSql.addSql("SELECT * FROM dq_index3Record");
		//2.如果查询表名不为空,模糊检索数据管控表信息
		if (!StringUtil.isBlank(tableName)) {
			asmSql.addSql(" WHERE lower(table_name) LIKE lower(?)").addParam('%' + tableName + '%');
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "获取系统层信息",
			logicStep = "1.获取系统层信息")
	@Return(desc = "系统层信息列表", range = "无限制")
	static List<Map<String, Object>> getSFLDataInfos() {
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("sysId", Constant.SYS_DATA_TABLE);
		map.put("sysname", "系统数据备份");
		map.put("description", "系统数据备份");
		list.add(map);
		map = new HashMap<>();
		map.put("sysId", Constant.SYS_DATA_BAK);
		map.put("sysname", "系统数据表");
		map.put("description", "系统数据表");
		list.add(map);
		return list;
	}

	@Method(desc = "获取系统表信息",
			logicStep = "1.获取系统表信息")
	@Return(desc = "系统表信息列表", range = "无限制")
	static List<String> getSFLTableInfos() {
		String packageName = "hrds.commons.entity";
		try {
			List<String> classNames = PackageUtil.getClassName(packageName, false);
			List<String> tableInfo = new ArrayList<>();
			if (null != classNames && !classNames.isEmpty()) {
				for (String className : classNames) {
					tableInfo.add(StringUtil.replace(className, packageName + ".", "").toLowerCase());
				}
			}
			return tableInfo;
		} catch (Exception e) {
			throw new BusinessException("获取系统表名失败！");
		}
	}

	@Method(desc = "获取系统表备份信息",
			logicStep = "1.获取系统表备份信息")
	@Return(desc = "系统表备份信息", range = "无限制")
	static List<Map<String, Object>> getSFLDataBakInfos() {
		return Dbo.queryList("SELECT * from sys_dump");
	}

	@Method(desc = "获取批量数据下数据源下分类信息",
			logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
	@Param(name = "source_id", desc = "数据源id", range = "数据源id,唯一")
	@Param(name = "isFileCollection", desc = "是否文件采集", range = "true:是,false:否")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "加工信息列表", range = "无限制")
	static List<Map<String, Object>> getDCLBatchClassifyInfos(String source_id, Boolean isFileCollection,
	                                                          User user) {
		return getDCLBatchClassifyInfos(source_id, isFileCollection, user, null);
	}

	@Method(desc = "获取批量数据下数据源下分类信息",
			logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
	@Param(name = "source_id", desc = "数据源id", range = "数据源id,唯一")
	@Param(name = "isFileCollection", desc = "是否文件采集", range = "true:是,false:否")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "加工信息列表", range = "无限制")
	static List<Map<String, Object>> getDCLBatchClassifyInfos(String source_id, Boolean isFileCollection, User user,
	                                                          String searchName) {
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
			asmSql.addParam("SELECT t3.datasource_name,* FROM collect_job_classify t1 JOIN agent_info t2 ON" +
					" t2.agent_id = t1.agent_id JOIN data_source t3 ON t3.source_id = t2.source_id");
			if (StringUtil.isNotBlank(source_id)) {
				agent_info.setSource_id(source_id);
				asmSql.addSql(" WHERE t2.source_id = ? ").addParam(agent_info.getSource_id());
			}
		}
		if (!isFileCollection) {
			asmSql.addSql(" AND agent_type not in (?,?)").addParam(AgentType.WenJianXiTong.getCode())
					.addParam(AgentType.FTP.getCode());
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}


	@Method(desc = "获取分类id获取分类下表信息",
			logicStep = "1.获取分类id获取分类下表信息")
	@Param(name = "classify_id", desc = "分类id", range = "分类id,唯一")
	@Param(name = "classify_name", desc = "分类名称", range = "String字符串,512长度")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Param(name = "isIntoHBase", desc = "是否进HBase", range = "0:是,1:否")
	@Return(desc = "分类下表信息", range = "无限制")
	static List<Map<String, Object>> getDCLBatchTableInfos(String classify_id, String classify_name,
	                                                       User user, String isIntoHBase) {
		//1.获取分类id获取分类下表信息
		asmSql.clean();
		UserType userType = UserType.ofEnumByCode(user.getUserType());
		Database_set database_set = new Database_set();
		Collect_job_classify classify = new Collect_job_classify();
		if (UserType.XiTongGuanLiYuan != userType) {
			asmSql.addSql("SELECT t2.task_name,t1.*,t3.* FROM source_file_attribute t1 JOIN database_set t2 ON" +
					" t1.collect_set_id = t2.database_id JOIN collect_job_classify t3 ON" +
					" t3.classify_id = t2.classify_id JOIN source_relation_dep t4 ON t1.source_id = t4.source_id");
		} else {
			asmSql.addSql("SELECT t2.task_name,t1.*,t3.* FROM source_file_attribute t1 JOIN database_set t2 ON" +
					" t1.collect_set_id = t2.database_id JOIN collect_job_classify t3 ON" +
					" t3.classify_id = t2.classify_id");
		}
		if (StringUtil.isNotBlank(classify_id)) {
			database_set.setClassify_id(classify_id);
			asmSql.addSql(" WHERE t2.classify_id = ?").addParam(database_set.getClassify_id());
		}
		if (StringUtil.isNotBlank(classify_name)) {
			classify.setClassify_name("%" + classify_name + "%");
			asmSql.addSql(" WHERE t1.table_name like ? OR t1.hbase_name like ? OR t1.original_name like ? OR" +
					" t2.task_name like ? OR t2.database_number like ?");
			asmSql.addParam(classify.getClassify_name()).addParam(classify.getClassify_name())
					.addParam(classify.getClassify_name()).addParam(classify.getClassify_name())
					.addParam(classify.getClassify_name());
		}
		if (UserType.XiTongGuanLiYuan != userType) {
			asmSql.addSql(" AND t4.dep_id = ?").addParam(user.getDepId());
		}
		if (StringUtil.isNotBlank(isIntoHBase)) {
			IsFlag isFlag = IsFlag.ofEnumByCode(isIntoHBase);
			if (IsFlag.Fou == isFlag) {
				asmSql.addORParam("t1.is_in_hbase", new String[]{isIntoHBase, "3"});
			} else {
				asmSql.addSql(" AND t1.is_in_hbase = ?").addParam(isIntoHBase);
			}
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

//	@Method(desc = "根据集市id获取集市下数据表信息",
//			logicStep = "1.根据集市id获取集市下数据表信息")
//	@Param(name = "data_mart_id", desc = "集市id", range = "集市id,唯一")
//	@Param(name = "pageFrom", desc = "页面来源", range = "TreePageSource.webType")
//	@Return(desc = "集市信息", range = "无限制")
//	public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id, String pageFrom) {
//		//1.根据集市id获取集市下数据表信息
//		return getDMLTableInfos(data_mart_id, pageFrom, null);
//	}

//	@Method(desc = "根据集市id获取集市下数据表信息",
//			logicStep = "1.根据集市id获取集市下数据表信息")
//	@Param(name = "data_mart_id", desc = "集市id", range = "集市id,唯一")
//	@Param(name = "pageFrom", desc = "页面来源", range = "TreePageSource.webType")
//	@Param(name = "dataTableName", desc = "集市表名", range = "0:是,1:否")
//	@Return(desc = "集市下数据表信息", range = "无限制")
//	public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id, String pageFrom,
//	                                                         String dataTableName) {
//		//1.根据集市id获取集市下数据表信息
//		Datatable_info datatableInfo = new Datatable_info();
//		asmSql.clean();
//		asmSql.addSql("SELECT * from datatable_info where is_current_cluster = ?").addParam(IsFlag.Shi.getCode());
//		if (StringUtil.isNotBlank(data_mart_id)) {
//			datatableInfo.setData_mart_id(data_mart_id);
//			asmSql.addSql(" AND data_mart_id = ?").addParam(datatableInfo.getData_mart_id());
//		}
//		datatableInfo.setDatatable_due_date(DateUtil.getSysDate());
//		asmSql.addSql(" AND datatable_due_date >=").addParam(datatableInfo.getDatatable_due_date());
//		DataSourceType dataSourceType = DataSourceType.ofEnumByCode(pageFrom);
//		if (DataSourceType.DML == dataSourceType && !TreePageSource.WEBSQL.equalsIgnoreCase(pageFrom)
//				&& !TreePageSource.REPORT.equalsIgnoreCase(pageFrom)) {
//			//TODO JobExecuteState
//			asmSql.addSql(" and ( ? in (hy_success,elk_success,kv_success,solr_success,solrbase_success," +
//					"carbondata_success))").addParam("104");
//			asmSql.addSql(" and datatype = ?").addParam(IsFlag.Shi.getCode());
//		}
//		if (TreePageSource.WEBSQL.equalsIgnoreCase(pageFrom) || TreePageSource.REPORT.equalsIgnoreCase(pageFrom)
//				|| TreePageSource.INTERFACE.equalsIgnoreCase(pageFrom)) {
//			//TODO JobExecuteState
//			asmSql.addSql(" and ( ? in (hy_success,elk_success,kv_success,solr_success,solrbase_success," +
//					"carbondata_success))").addParam("104");
//		}
//		if (StringUtil.isNotBlank(dataTableName)) {
//			datatableInfo.setDatatable_cn_name('%' + dataTableName + '%');
//			asmSql.addSql(" AND lower(datatable_cn_name) like lower(?)").addParam(datatableInfo.getDatatable_cn_name());
//			datatableInfo.setDatatable_en_name(dataTableName);
//			asmSql.addSql(" OR lower(datatable_en_name) like lower(?)").addParam(datatableInfo.getDatatable_en_name());
//		}
//		return Dbo.queryList(asmSql.sql(), asmSql.params());
//	}

//	@Method(desc = "根据加工id获取加工下的分类信息",
//			logicStep = "1.根据加工id获取加工下的分类信息")
//	@Param(name = "modal_pro_id", desc = "加工id", range = "加工,唯一")
//	@Return(desc = "加工下的分类信息", range = "无限制")
//	public static List<Map<String, Object>> getDPLClassifyInfos(String modal_pro_id) {
//		//1.根据加工id获取加工下的分类信息
//		return getDPLClassifyInfos(modal_pro_id, null);
//	}
//
//	@Method(desc = "根据加工id获取加工下的分类信息",
//			logicStep = "1.根据加工id获取加工下的分类信息")
//	@Param(name = "modal_pro_id", desc = "加工id", range = "加工,唯一")
//	@Param(name = "category_name", desc = "分类名称", range = "String类型,长度512")
//	@Return(desc = "加工下的分类信息", range = "无限制")
//	public static List<Map<String, Object>> getDPLClassifyInfos(String modal_pro_id, String category_name) {
//		//1.根据加工id获取加工下的分类信息
//		Edw_modal_category edw_modal_category = new Edw_modal_category();
//		if (StringUtil.isNotBlank(modal_pro_id)) {
//			edw_modal_category.setCategory_id(modal_pro_id);
//			asmSql.clean();
//			asmSql.addSql("SELECT * FROM edw_modal_category WHERE modal_pro_id = ? AND parent_category_id = ?")
//					.addParam(edw_modal_category.getCategory_id()).addParam(edw_modal_category.getCategory_id());
//		} else if (StringUtil.isNotBlank(category_name)) {
//			edw_modal_category.setCategory_name('%' + category_name + '%');
//			edw_modal_category.setCategory_num(category_name);
//			asmSql.clean();
//			asmSql.addSql("SELECT * FROM edw_modal_category WHERE category_name like ? OR category_num = ?")
//					.addParam(edw_modal_category.getCategory_name()).addParam(edw_modal_category.getCategory_num());
//		}
//		return Dbo.queryList(asmSql.sql(), asmSql.params());
//	}

//	@Method(desc = "根据加工的分类id获取分类下的表信息",
//			logicStep = "1.根据加工的分类id获取分类下的表信息")
//	@Param(name = "category_id", desc = "加工分类id", range = "加工分类id,唯一")
//	@Param(name = "pageFrom", desc = "页面来源", range = "TreePageSource.webType")
//	@Return(desc = "加工分类下的表信息", range = "无限制")
//	public static Map<String, Result> getDPLTableInfos(String category_id, String pageFrom) {
//		//1.根据加工的分类id获取分类下的表信息
//		return getDPLTableInfos(category_id, pageFrom, null);
//	}

//	@Method(desc = "根据加工的分类id获取分类下的表信息",
//			logicStep = "1.根据加工的分类id获取分类下的表信息")
//	@Param(name = "category_id", desc = "加工分类id", range = "加工分类id,唯一")
//	@Param(name = "pageFrom", desc = "页面来源", range = "TreePageSource.webType")
//	@Param(name = "modalName", desc = "模型名称", range = "String类型,长度512")
//	@Return(desc = "加工分类下的表信息", range = "无限制")
//	public static Map<String, Result> getDPLTableInfos(String category_id, String pageFrom,
//	                                                   String modalName) {
//		//1.根据加工的分类id获取分类下的表信息
//		Map<String, Result> map = new HashMap<>();
//		DataSourceType dataSourceType = DataSourceType.ofEnumByCode(pageFrom);
//		Edw_modal_category edw_modal_category = new Edw_modal_category();
//		if (StringUtil.isNotBlank(category_id)) {
//			Edw_table edwTable = new Edw_table();
//			edwTable.setCategory_id(category_id);
//			asmSql.clean();
//			asmSql.addSql("select * from edw_table where category_id = ?").addParam(edwTable.getCategory_id());
//			if (DataSourceType.DPL != dataSourceType) {
//				asmSql.addSql(" AND data_in = ?").addParam(IsFlag.Shi.getCode());
//			}
//			map.put("edw_table", Dbo.queryResult(asmSql.sql(), asmSql.params()));
//			asmSql.clean();
//			edw_modal_category.setCategory_id(category_id);
//			asmSql.addSql("SELECT * FROM edw_modal_category WHERE parent_category_id = ?");
//			asmSql.addParam(edw_modal_category.getCategory_id());
//			map.put("edw_modal_category", Dbo.queryResult(asmSql.sql(), asmSql.params()));
//		} else if (StringUtil.isNotBlank(modalName)) {
//			Edw_table edwTable = new Edw_table();
//			edwTable.setTabname('%' + modalName + '%');
//			asmSql.clean();
//			asmSql.addSql("SELECT * FROM edw_table WHERE (lower(tabname) LIKE lower(?)  OR lower(ctname)" +
//					" like lower(?))").addParam(edwTable.getTabname()).addParam(edwTable.getTabname());
//			if (DataSourceType.DPL != dataSourceType) {
//				asmSql.addSql(" AND data_in = ?").addParam(IsFlag.Shi.getCode());
//			}
//			Result edwTableRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
//			map.put("edw_table", edwTableRs);
//			Result categoryResult = new Result();
//			for (int i = 0; i < edwTableRs.getRowCount(); i++) {
//				asmSql.clean();
//				edw_modal_category.setCategory_id(edwTableRs.getString(i, "category_id"));
//				asmSql.addSql("SELECT * FROM edw_modal_category WHERE category_id = ?");
//				asmSql.addParam(edw_modal_category.getCategory_id());
//				Result categoryRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
//				if (!categoryRs.isEmpty()) {
//					Result categoryRsChildes = getCategoryByChild(categoryRs);
//					categoryResult.add(categoryRsChildes);
//				}
//				categoryResult.add(categoryRs);
//			}
//			asmSql.clean();
//			edw_modal_category.setCategory_name('%' + modalName + '%');
//			asmSql.addSql("SELECT * FROM edw_modal_category WHERE category_name like ?");
//			asmSql.addParam(edw_modal_category.getCategory_name());
//			Result categoryRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
//			if (!categoryRs.isEmpty()) {
//				Result categoryRsChildes = getCategoryByChild(categoryRs);
//				categoryResult.add(categoryRsChildes);
//			}
//			categoryResult.add(categoryRs);
//			map.put("edw_modal_category", categoryResult);
//		}
//		return map;
//	}

//	@Method(desc = "加工获取子分类信息",
//			logicStep = "1.加工获取子分类信息")
//	@Param(name = "rs", desc = "当前分类节点结果集", range = "Result值")
//	@Return(desc = "子分类信息", range = "Result值")
//	private static Result getCategoryByChild(Result rs) {
//		Result result = new Result();
//		Edw_modal_category category = new Edw_modal_category();
//		for (int i = 0; i < rs.getRowCount(); i++) {
//			String parent_category_id = rs.getString(i, "parent_category_id");
//			String modal_pro_id = rs.getString(i, "modal_pro_id");
//			category.setCategory_id(parent_category_id);
//			if (!modal_pro_id.equals(parent_category_id)) {
//				asmSql.clean();
//				asmSql.addSql("select * from edw_modal_category where category_id = ?");
//				asmSql.addParam(category.getCategory_id());
//				Result result3 = Dbo.queryResult(asmSql.sql(), asmSql.params());
//				if (!result3.isEmpty()) {
//					Result result4 = getCategoryByChild(result3);
//					result.add(result4);
//				}
//				result.add(result3);
//			}
//		}
//		return result;
//	}

	@Method(desc = "获取流数据管理的groupId信息",
			logicStep = "1.获取流数据管理的groupId信息")
	public static List<Map<String, Object>> getDCLRealTimeDataInfos() {
		return Dbo.queryList("select distinct(sdm_cons_para_val) groupid from sdm_cons_para where" +
				" (sdm_conf_para_na = 'groupid' or sdm_conf_para_na = 'application.id')");
	}

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
