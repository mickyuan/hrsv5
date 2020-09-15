package hrds.l.biz.autoanalysis.manage;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AutoTemplateStatus;
import hrds.commons.codes.AutoValueType;
import hrds.commons.codes.IsFlag;
import hrds.commons.collection.ProcessingData;
import hrds.commons.entity.Auto_tp_cond_info;
import hrds.commons.entity.Auto_tp_info;
import hrds.commons.entity.Auto_tp_res_set;
import hrds.commons.entity.fdentity.ProjectTableEntity;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DataTableUtil;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "自主分析管理类", author = "dhw", createdate = "2020/8/21 14:17")
public class ManageAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();
	// SQL关键字
	private static final String AND = "AND";
	private static final String OR = "OR";
	private static final String BETWEEN = "BETWEEN";
	private static final String WHERE = "WHERE";
	private static final String UNION = "UNION";
	private static final String UNIONALL = "UNION ALL";
	private static final String IN = "IN";
	private static final String NOTIN = "NOT IN";
	private static final ArrayList<String> numbersArray = new ArrayList<>();

	static {
		numbersArray.add("int");
		numbersArray.add("int8");
		numbersArray.add("int16");
		numbersArray.add("integer");
		numbersArray.add("tinyint");
		numbersArray.add("smallint");
		numbersArray.add("mediumint");
		numbersArray.add("bigint");
		numbersArray.add("float");
		numbersArray.add("double");
		numbersArray.add("decimal");
	}

	@Method(desc = "获取自主分析模板配置信息", logicStep = "1.查询并返回自主分析模板配置信息")
	@Return(desc = "返回自主分析模板配置信息", range = "无限制")
	public List<Map<String, Object>> getTemplateConfInfo() {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.查询并返回自主分析模板配置信息
		return Dbo.queryList(
				"select * from " + Auto_tp_info.TableName + " where template_status != ? "
						+ " order by create_date desc,create_time desc",
				AutoTemplateStatus.ZhuXiao.getCode());
	}

	@Method(desc = "根据模板名称获取自主分析模板配置信息", logicStep = "1.根据模板名称获取自主分析模板配置信息")
	@Param(name = "template_name", desc = "自主取数模板配置名称", range = "配置模板时生成")
	@Return(desc = "返回自主分析模板配置信息", range = "无限制")
	public List<Map<String, Object>> getTemplateConfInfoByName(String template_name) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.根据模板名称获取自主分析模板配置信息
		return Dbo.queryList(
				"select * from " + Auto_tp_info.TableName + " where template_name like ? " +
						" order by create_date desc,create_time desc",
				"%" + template_name + "%");
	}

	@Method(desc = "获取自主取数树数据", logicStep = "1.配置树不显示文件采集的数据" +
			"2.根据源菜单信息获取节点数据列表")
	@Return(desc = "返回自主取数树数据", range = "无限制")
	public List<Node> getAutoAnalysisTreeData() {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		TreeConf treeConf = new TreeConf();
		// 1.配置树不显示文件采集的数据
		treeConf.setShowFileCollection(Boolean.FALSE);
		// 2.根据源菜单信息获取节点数据列表
		List<Map<String, Object>> dataList = TreeNodeInfo.getTreeNodeInfo(TreePageSource.WEB_SQL, getUser(),
				treeConf);
		return NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
	}

	@Method(desc = "校验sql是否合法", logicStep = "1.判断sql是否以;结尾" +
			"2.校验sql")
	@Param(name = "template_sql", desc = "自主取数模板sql", range = "无限制")
	public void verifySqlIsLegal(String template_sql) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		try {
			// 1.判断sql是否以;结尾
			if (template_sql.endsWith(";")) {
				throw new BusinessException("sql不要使用;结尾");
			}
			// 2.校验sql
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) {
				}
			}.getDataLayer(template_sql, Dbo.db());
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException("sql错误：" + e.getMessage());
		}
	}

	@Method(desc = "生成自主分析模板配置参数", logicStep = "")
	@Param(name = "template_sql", desc = "自主取数模板sql", range = "无限制")
	public Map<String, Object> generateTemplateParam(String template_sql) {
//		verifySqlIsLegal(template_sql);
		String dbType = JdbcConstants.POSTGRESQL;
		// sql格式化
		String format_sql = SQLUtils.format(template_sql, dbType).trim();
		if (format_sql.endsWith(";")) {
			format_sql = format_sql.substring(0, format_sql.length() - 1);
		}
		List<Map<String, Object>> autoTpCondInfoList = new ArrayList<>();
		List<Auto_tp_res_set> autoTpResSets = new ArrayList<>();
		if (format_sql.toUpperCase().contains(UNION) && !format_sql.contains(UNIONALL)) {
			List<String> unionSql = StringUtil.split(format_sql, UNION);
			for (String sql : unionSql) {
				getAutoTpCondInfo(autoTpCondInfoList, autoTpResSets, sql);
			}
		} else if (format_sql.contains(UNIONALL)) {
			List<String> unionSql = StringUtil.split(format_sql, UNIONALL);
			for (String sql : unionSql) {
				getAutoTpCondInfo(autoTpCondInfoList, autoTpResSets, sql);
			}
		} else {
			getAutoTpCondInfo(autoTpCondInfoList, autoTpResSets, format_sql);
		}
		Map<String, Object> templateMap = new HashMap<>();
		templateMap.put("autoTpCondInfo", autoTpCondInfoList);
		templateMap.put("autoTpResSetInfo", autoTpResSets);
		return templateMap;
	}

	@Method(desc = "保存模板配置页面的信息包括模板内容,条件参数和结果设置",
			logicStep = "1.校验自主取数模板字段是否合法"
					+ "2.判断模板名称是否已存在"
					+ "3.新增模板信息表"
					+ "4.新增模板条件表信息"
					+ "5.新增模板结果表数据")
	@Param(name = "autoTpCondInfos", desc = "模板条件实体对象", range = "与数据库表规则一致", isBean = true)
	@Param(name = "autoTpResSets", desc = "模板结果实体对象实体数组", range = "与数据库表规则一致", isBean = true)
	@Param(name = "auto_tp_info", desc = "模板信息实体对象数组", range = "与数据库表规则一致", isBean = true)
	public void saveTemplateConfInfo(Auto_tp_cond_info[] autoTpCondInfos,
	                                 Auto_tp_res_set[] autoTpResSets,
	                                 Auto_tp_info auto_tp_info) {
		// 数据可访问权限处理方式：通过user_id进行访问权限限制
		// 1.校验自主取数模板字段是否合法
		checkAutoTpInfoFields(auto_tp_info);
		// 2.判断模板名称是否已存在
		isTemplateNameExist(auto_tp_info.getTemplate_name());
		auto_tp_info.setTemplate_status(AutoTemplateStatus.BianJi.getCode());
		auto_tp_info.setTemplate_id(PrimayKeyGener.getNextId());
		auto_tp_info.setCreate_date(DateUtil.getSysDate());
		auto_tp_info.setCreate_time(DateUtil.getSysTime());
		auto_tp_info.setCreate_user(getUserId());
		// 3.新增模板信息表
		auto_tp_info.add(Dbo.db());
		// 4.新增模板条件表信息
		addAutoTpCondInfo(autoTpCondInfos, auto_tp_info.getTemplate_id());
		// 5.新增模板结果表数据
		addAutoTpResSet(autoTpResSets, auto_tp_info.getTemplate_id());
	}

	@Method(desc = "新增模板条件信息", logicStep = "1.校验自主取数模板条件字段是否合法" +
			"2.新增模板条件表数据")
	@Param(name = "autoTpResSets", desc = "模板结果实体对象实体数组", range = "与数据库表规则一致", isBean = true)
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	private void addAutoTpResSet(Auto_tp_res_set[] autoTpResSets, long template_id) {
		for (Auto_tp_res_set auto_tp_res_set : autoTpResSets) {
			// 1.校验模板结果对象字段是否合法
			checkAutoTpResSetFields(auto_tp_res_set);
			auto_tp_res_set.setTemplate_res_id(PrimayKeyGener.getNextId());
			auto_tp_res_set.setTemplate_id(template_id);
			auto_tp_res_set.setIs_dese(IsFlag.Fou.getCode());
			auto_tp_res_set.setCreate_date(DateUtil.getSysDate());
			auto_tp_res_set.setCreate_time(DateUtil.getSysTime());
			auto_tp_res_set.setCreate_user(getUserId());
			// 2.新增模板结果表数据
			auto_tp_res_set.add(Dbo.db());
		}
	}

	@Method(desc = "新增模板条件信息", logicStep = "1.校验自主取数模板条件字段是否合法" +
			"2.新增模板条件表数据")
	@Param(name = "autoTpCondInfos", desc = "模板条件实体对象", range = "与数据库表规则一致", isBean = true)
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	private void addAutoTpCondInfo(Auto_tp_cond_info[] autoTpCondInfos, long template_id) {
		for (int i = 0; i < autoTpCondInfos.length; i++) {
			// 1.校验自主取数模板条件字段是否合法
			Auto_tp_cond_info auto_tp_cond_info = autoTpCondInfos[i];
			checkAutoTpCondInfoFields(auto_tp_cond_info);
			auto_tp_cond_info.setTemplate_cond_id(PrimayKeyGener.getNextId());
			auto_tp_cond_info.setTemplate_id(template_id);
			auto_tp_cond_info.setIs_dept_id(IsFlag.Fou.getCode());
			auto_tp_cond_info.setValue_size(auto_tp_cond_info.getValue_size() == null ? String.valueOf(64) :
					auto_tp_cond_info.getValue_size());
			auto_tp_cond_info.setCon_row(String.valueOf(i));
			// 2.新增模板条件表数据
			auto_tp_cond_info.add(Dbo.db());
		}
	}

	private void checkAutoTpResSetFields(Auto_tp_res_set auto_tp_res_set) {
		Validator.notBlank(auto_tp_res_set.getColumn_cn_name(), "字段中文名不能为空");
		Validator.notBlank(auto_tp_res_set.getColumn_en_name(), "字段英文名不能为空");
		Validator.notBlank(auto_tp_res_set.getColumn_type(), "字段类型不能为空");
		Validator.notBlank(auto_tp_res_set.getRes_show_column(), "结果显示字段不能为空");
		Validator.notBlank(auto_tp_res_set.getSource_table_name(), "字段来源表名不能为空");
	}

	private void checkAutoTpCondInfoFields(Auto_tp_cond_info auto_tp_cond_info) {
		Validator.notBlank(auto_tp_cond_info.getCond_en_column(), "条件对应的英文字段不能为空");
		Validator.notBlank(auto_tp_cond_info.getCond_cn_column(), "条件对应的中文字段不能为空");
		Validator.notBlank(auto_tp_cond_info.getCond_para_name(), "条件参数名称不能为空");
		Validator.notBlank(auto_tp_cond_info.getCon_relation(), "条件参数名称不能为空");
		Validator.notBlank(auto_tp_cond_info.getIs_required(), "是否必填不能为空");
	}

	private void checkAutoTpInfoFields(Auto_tp_info auto_tp_info) {
		Validator.notBlank(auto_tp_info.getTemplate_name(), "模板名称不能为空");
		IsFlag.ofEnumByCode(auto_tp_info.getData_source());
		Validator.notBlank(auto_tp_info.getTemplate_sql(), "模板sql不能为空");
		Validator.notBlank(auto_tp_info.getTemplate_desc(), "模板描述不能为空");
	}

	@Method(desc = "根据模板ID获取自主取数模板配置信息", logicStep = "1.根据模板ID获取自主取数模板配置信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回根据模板ID获取自主取数模板配置信息", range = "无限制")
	public Map<String, Object> getAutoTpInfoById(long template_id) {
		// 数据可访问权限处理方式：通过user_id进行访问权限限制
		// 1.根据模板ID获取自主取数模板配置信息
		return Dbo.queryOneObject("select * from " + Auto_tp_info.TableName + " where template_id=?",
				template_id);
	}

	@Method(desc = "根据模板ID获取自主取数模板条件信息", logicStep = "1.根据模板ID获取自主取数模板条件信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回根据模板ID获取自主取数模板条件信息", range = "无限制")
	public List<Map<String, Object>> getAutoTpCondInfoById(long template_id) {
		// 数据可访问权限处理方式：通过user_id进行访问权限限制
		Map<String, Object> autoTpInfo = getAutoTpInfoById(template_id);
		Map<String, Object> paramMap = generateTemplateParam(autoTpInfo.get("template_sql").toString());
		List<Map<String, Object>> autoTpCondInfoList = JsonUtil.toObject(
				JsonUtil.toJson(paramMap.get("autoTpCondInfo")),
				new TypeReference<List<Map<String, Object>>>() {
				}.getType());
		// 1.根据模板ID获取自主取数模板条件信息
		List<Map<String, Object>> auto_tp_cond_infoList =
				Dbo.queryList(
						"select * from " + Auto_tp_cond_info.TableName + " where template_id=?",
						template_id);
		for (Map<String, Object> objectMap : autoTpCondInfoList) {
			objectMap.put("checked", false);
			for (Map<String, Object> map : auto_tp_cond_infoList) {
				if (map.containsValue(objectMap.get("cond_para_name"))) {
					objectMap.put("value_type", map.get("value_type"));
					objectMap.put("value_size", map.get("value_size"));
					objectMap.put("pre_value", map.get("pre_value"));
					objectMap.put("checked", true);
				}
			}
		}
		return autoTpCondInfoList;
	}

	@Method(desc = "根据模板ID获取自主取数模板结果信息", logicStep = "1.根据模板ID获取自主取数模板结果信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回根据模板ID获取自主取数模板结果信息", range = "无限制")
	public List<Map<String, Object>> getAutoTpResSetById(long template_id) {
		// 数据可访问权限处理方式：通过user_id进行访问权限限制
		// 1.根据模板ID获取自主取数模板结果信息
		return Dbo.queryList("select * from " + Auto_tp_res_set.TableName + " where template_id=?",
				template_id);
	}

	@Method(desc = "更新模板配置页面的信息包括 模板内容 条件参数 和 结果设置", logicStep = "1.校验模板参数合法性" +
			"2.更新模板信息表数据" +
			"3.更新后删除模板条件数据" +
			"4.更新后删除模板结果数据" +
			"5.新增模板条件表数据" +
			"6.新增模板结果数据")
	@Param(name = "autoTpCondInfos", desc = "模板条件实体对象", range = "与数据库表规则一致", isBean = true)
	@Param(name = "autoTpResSets", desc = "模板结果实体对象实体数组", range = "与数据库表规则一致", isBean = true)
	@Param(name = "auto_tp_info", desc = "模板信息实体对象", range = "与数据库表规则一致", isBean = true)
	public void updateTemplateConfInfo(Auto_tp_cond_info[] autoTpCondInfos, Auto_tp_res_set[] autoTpResSets,
	                                   Auto_tp_info auto_tp_info) {
		// 数据可访问权限处理方式：通过user_id进行访问权限限制
		// 1.校验模板参数合法性
		Validator.notNull(auto_tp_info.getTemplate_id(), "编辑时模板ID不能为空");
		Validator.notNull(auto_tp_info.getTemplate_status(), "编辑时模板状态不能为空");
		checkAutoTpInfoFields(auto_tp_info);
		// 2.校验模板状态是否为编辑
		checkAutoTemplateStatus(auto_tp_info.getTemplate_status());
		auto_tp_info.setUpdate_user(getUserId());
		auto_tp_info.setLast_update_date(DateUtil.getSysDate());
		auto_tp_info.setLast_update_time(DateUtil.getSysTime());
		auto_tp_info.setTemplate_sql(auto_tp_info.getTemplate_sql().replace(";", ""));
		try {
			// 2.更新模板信息表数据
			auto_tp_info.update(Dbo.db());
		} catch (Exception e) {
			if (!(e instanceof ProjectTableEntity.EntityDealZeroException)) {
				logger.error(e);
				throw new BusinessException(e.getMessage());
			}
		}
		// 3.更新后删除模板条件数据
		Dbo.execute("delete from " + Auto_tp_cond_info.TableName + " where template_id=?",
				auto_tp_info.getTemplate_id());
		// 4.更新后删除模板结果数据
		Dbo.execute("delete from " + Auto_tp_res_set.TableName + " where template_id=?",
				auto_tp_info.getTemplate_id());
		// 5.新增模板条件表数据
		addAutoTpCondInfo(autoTpCondInfos, auto_tp_info.getTemplate_id());
		// 6.新增模板结果数据
		addAutoTpResSet(autoTpResSets, auto_tp_info.getTemplate_id());
	}

	@Method(desc = "校验模板状态是否为编辑", logicStep = "1.校验模板状态是否为编辑")
	@Param(name = "template_status", desc = "自主取数模板状态（使用AutoTemplateStatus代码项）", range = "无限制")
	private void checkAutoTemplateStatus(String template_status) {
		// 1.校验模板状态是否为编辑
		if (AutoTemplateStatus.BianJi != AutoTemplateStatus.ofEnumByCode(template_status)) {
			throw new BusinessException("自主取数模板状态不为编辑：" + template_status);
		}
	}

	@Method(desc = "自主取数模板结果预览数据", logicStep = "1.根据sql查询数据结果" +
			"2.返回数据结果")
	@Param(name = "template_sql", desc = "自主取数模板sql", range = "无限制")
	@Param(name = "showNum", desc = "显示条数", range = "正整数", valueIfNull = "10")
	@Return(desc = "返回数据结果", range = "无限制")
	public List<Map<String, Object>> getPreviewData(String template_sql, int showNum) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 最多只显示1000行
		List<Map<String, Object>> resultData = new ArrayList<>();
		if (showNum > 1000) {
			showNum = 1000;
		}
		// 1.根据sql查询数据结果
		new ProcessingData() {
			@Override
			public void dealLine(Map<String, Object> map) {
				resultData.add(map);
			}
		}.getPageDataLayer(template_sql, Dbo.db(), 1, showNum <= 0 ? 10 : showNum);
		// 2.返回数据结果
		return resultData;
	}

	public static void main(String[] args) {
		String sql = "select * from ZXZ_z2_table_info";
		new ProcessingData() {
			@Override
			public void dealLine(Map<String, Object> map) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					System.out.println(entry.getKey() + "===" + entry.getValue());
				}
			}
		}.getDataLayer(sql, new DatabaseWrapper());
	}

//	@Method(desc = "模板配置结果设置点击 解析sq生成 select 部分的内容", logicStep = "")
//	@Param(name = "template_sql", desc = "自主取数模板sql", range = "无限制")
//	@Return(desc = "", range = "")
//	public List<Map<String, Object>> getTemplateResult(String template_sql) {
//		List<String> tableNameList = new ArrayList<>();
//		List<String> columnNameList = new ArrayList<>();
//		List<String> columnTypeList = new ArrayList<>();
//		List<String> resultColumnNameList = new ArrayList<>();
//		List<String> tableNames = DruidParseQuerySql.parseSqlTableToList(template_sql);
//		for (String table_name : tableNames) {
//			List<Map<String, Object>> columns = DataTableUtil.getColumnByTableName(Dbo.db(), table_name);
//			for (Map<String, Object> column : columns) {
//				Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
//				auto_tp_res_set.setSource_table_name(table_name);
//				auto_tp_res_set.setColumn_en_name(column.get("column_name").toString());
//				auto_tp_res_set.setColumn_cn_name(column.get("column_name").toString());
////				auto_tp_res_set.setRes_show_column(entry.getKey().trim());
////				List<Map<String, Object>> columnList =
////						DataTableUtil.getColumnByTableName(Dbo.db(), auto_tp_res_set.getSource_table_name());
////				for (Map<String, Object> columnMap : columnList) {
////					if (columnMap.get("table_name").equals(auto_tp_res_set.getSource_table_name())) {
////						auto_tp_res_set.setColumn_type(columnMap.get("column_type")==null?
////								AutoValueType.ZiFuChuan.getCode(),);
////					}
////				}
//				auto_tp_res_set.setColumn_type(AutoValueType.ZiFuChuan.getCode());
////				if (!autoTpResSets.contains(auto_tp_res_set)) {
////					autoTpResSets.add(auto_tp_res_set);
////				}
////				// 对相同列名加序号
////				if (resultColumnNameList.contains(column.get("column_name").toString())) {
////					throw new BusinessException("查询字段中存在字段名相同的列，请设置别名");
////				}
//				resultColumnNameList.add(column.get("column_name").toString());
//				columnTypeList.add(column.get("column_type").toString());
//			}
//		}
//		getTableNameAndColumnName(template_sql, tableNameList, columnNameList, columnTypeList, resultColumnNameList);
//		List<Map<String, Object>> resultSetList = new ArrayList<>();
//		for (int i = 0; i < tableNameList.size(); i++) {
//			Map<String, Object> resultSetMap = new HashMap<>();
//			resultSetMap.put("source_table_name", tableNameList.get(i));
//			resultSetMap.put("column_en_name", columnNameList.get(i));
//			resultSetMap.put("column_cn_name", resultColumnNameList.get(i));
//			resultSetMap.put("res_show_column", resultColumnNameList.get(i));
//			if (numbersArray.contains(columnTypeList.get(i))) {
//				resultSetMap.put("column_type", AutoValueType.ShuZhi.toString());
//			} else {
//				resultSetMap.put("column_type", AutoValueType.ZiFuChuan.toString());
//			}
//			resultSetList.add(resultSetMap);
//		}
//		return resultSetList;
//	}

	@Method(desc = "发布自主取数模板", logicStep = "1.更新模板状态为发布")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	public void releaseAutoAnalysisTemplate(long template_id) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 1.更新模板状态为发布
		DboExecute.updatesOrThrow("更新自主取数模板状态为发布失败",
				"update " + Auto_tp_info.TableName + " set template_status=? where template_id=?",
				AutoTemplateStatus.FaBu.getCode(), template_id);
	}

	@Method(desc = "删除自主取数模板", logicStep = "1.更新模板状态为注销")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	public void deleteAutoAnalysisTemplate(long template_id) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 1.更新模板状态为注销
		DboExecute.updatesOrThrow("更新自主取数模板状态为注销失败",
				"update " + Auto_tp_info.TableName + " set template_status=? where template_id=?",
				AutoTemplateStatus.ZhuXiao.getCode(), template_id);
	}

	@Method(desc = "判断模板名称是否已存在", logicStep = "1.判断模板名称是否已存在")
	@Param(name = "template_name", desc = "模板名称", range = "无限制")
	private void isTemplateNameExist(String template_name) {
		// 1.判断模板名称是否已存在
		if (Dbo.queryNumber(
				"select count(1) from " + Auto_tp_info.TableName
						+ " where template_name = ? and template_status != ?",
				template_name, AutoTemplateStatus.ZhuXiao.getCode())
				.orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
			throw new BusinessException("模板名称已存在");
		}
	}

	private void getAutoTpCondInfo(List<Map<String, Object>> autoTpCondInfoList,
	                               List<Auto_tp_res_set> autoTpResSets, String sql) {
		DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(sql);
		SQLExpr whereInfo = druidParseQuerySql.whereInfo;
		// key为字段别名，value为原字段名称
		List<Map<String, String>> sourceAndAliasName = getSourceAndAliasName(sql);
		for (Map<String, String> map : sourceAndAliasName) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				// 表名
				String table_name = entry.getKey();
				// 列名
				String column_name = entry.getValue();
				List<Map<String, Object>> columns = DataTableUtil.getColumnByTableName(Dbo.db(),
						table_name);
				if (column_name.contains("*")) {
					for (Map<String, Object> column : columns) {
						Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
						auto_tp_res_set.setSource_table_name(table_name);
						auto_tp_res_set.setColumn_en_name(column.get("column_name").toString());
						auto_tp_res_set.setColumn_cn_name(column.get("column_ch_name").toString());
						auto_tp_res_set.setRes_show_column(column.get("column_name").toString());
						if (numbersArray.contains(column.get("column_type").toString())) {
							auto_tp_res_set.setColumn_type(AutoValueType.ShuZhi.getCode());
						} else {
							auto_tp_res_set.setColumn_type(AutoValueType.ZiFuChuan.getCode());
						}
						if (!autoTpResSets.contains(auto_tp_res_set)) {
							autoTpResSets.add(auto_tp_res_set);
						}
					}
				} else {
					Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
					auto_tp_res_set.setSource_table_name(table_name);
					if (column_name.contains(".")) {
						column_name = StringUtil.split(column_name, ".").get(1);
					}
					for (Map<String, Object> column : columns) {
						if (column.get("column_name").toString().equals(column_name)) {
							auto_tp_res_set.setColumn_en_name(column_name);
							auto_tp_res_set.setColumn_cn_name(column.get("column_ch_name").toString());
							auto_tp_res_set.setRes_show_column(column.get("column_name").toString());
							auto_tp_res_set.setColumn_type(AutoValueType.ZiFuChuan.getCode());
							if (numbersArray.contains(column.get("column_type").toString())) {
								auto_tp_res_set.setColumn_type(AutoValueType.ShuZhi.getCode());
							} else {
								auto_tp_res_set.setColumn_type(AutoValueType.ZiFuChuan.getCode());
							}
							// fixme 字段类型应该判断
							if (!autoTpResSets.contains(auto_tp_res_set)) {
								autoTpResSets.add(auto_tp_res_set);
							}
						}
					}
				}
			}
		}
		if (null != whereInfo) {
			List<String> whereList = StringUtil.split(whereInfo.toString(), "\n");
			for (int i = 0; i < whereList.size(); i++) {
				String cond = whereList.get(i).trim();
				Map<String, Object> condInfoMap = new HashMap<>();
				condInfoMap.put("con_row", i);
				cond = getCond(autoTpCondInfoList, cond, condInfoMap);
				if (i + 1 < whereList.size()) {
					String child_cond = whereList.get(i + 1).trim();
					if (child_cond.contains(Constant.RXKH) && cond.contains(WHERE)) {
						getCond(autoTpCondInfoList, cond, condInfoMap);
					}
				}
			}
		}
	}

	@Method(desc = "", logicStep = "")
	@Param(name = "sql", desc = "查询sql", range = "无限制")
	@Return(desc = "", range = "")
	public List<Map<String, String>> getSourceAndAliasName(String sql) {
		String format = SQLUtils.format(sql, JdbcConstants.ORACLE);
		List<String> split = StringUtil.split(format, "\n");
		DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(sql);
		List<String> originalField = druidParseQuerySql.parseSelectOriginalField();
		List<Map<String, String>> list = new ArrayList<>();
		for (String s : split) {
			Map<String, String> map = new HashMap<>();
			if (s.startsWith("FROM")) {
				List<String> split1 = StringUtil.split(s, Constant.SPACE);
				for (String field : originalField) {
					if (field.contains(split1.get(2))) {
						map.put(split1.get(1), field);
					}
				}
				list.add(map);
			} else if (s.contains("ON")) {
				List<String> split1 = StringUtil.split(s, Constant.SPACE);
				for (String field : originalField) {
					if (field.contains(split1.get(3))) {
						map.put(split1.get(2), field);
					}
				}
				list.add(map);
			}
		}
		return list;
	}

	private String getCond(List<Map<String, Object>> autoTpCondInfoList, String cond,
	                       Map<String, Object> condInfoMap) {
		if (cond.startsWith(AND) && !cond.contains(BETWEEN)) {
			cond = cond.replace(AND, "");
		}
		if (cond.startsWith(OR)) {
			cond = cond.replace(OR, "");
		}
		if (cond.startsWith(WHERE)) {
			cond = cond.replace(WHERE, "");
		}
		if (cond.contains(BETWEEN)) {
			setAutoCondInfo(cond, condInfoMap, BETWEEN);
			autoTpCondInfoList.add(condInfoMap);
		}
		if (cond.contains("!=")) {
			setAutoCondInfo(cond, condInfoMap, "!=");
			autoTpCondInfoList.add(condInfoMap);
		}
		if (cond.contains("=") && !cond.contains("!=") && !cond.contains(">=") && !cond.contains("<=")) {
			setAutoCondInfo(cond, condInfoMap, "=");
			autoTpCondInfoList.add(condInfoMap);
		}
		if (cond.contains(">=")) {
			setAutoCondInfo(cond, condInfoMap, ">=");
			autoTpCondInfoList.add(condInfoMap);
		}
		if (cond.contains("<=")) {
			setAutoCondInfo(cond, condInfoMap, "<=");
			autoTpCondInfoList.add(condInfoMap);
		}
		if (cond.contains(">") && !cond.contains(">=")) {
			setAutoCondInfo(cond, condInfoMap, ">");
			autoTpCondInfoList.add(condInfoMap);
		}
		if (cond.contains("<") && !cond.contains("<=")) {
			setAutoCondInfo(cond, condInfoMap, "<");
			autoTpCondInfoList.add(condInfoMap);
		}
		if (cond.contains(NOTIN)) {
			setAutoCondInfo(cond, condInfoMap, NOTIN);
			autoTpCondInfoList.add(condInfoMap);
		}
		if (cond.contains(IN) && !cond.contains(NOTIN)) {
			setAutoCondInfo(cond, condInfoMap, IN);
			autoTpCondInfoList.add(condInfoMap);
		}
		return cond;
	}

	private void setAutoCondInfo(String cond, Map<String, Object> condInfoMap, String s) {
		List<String> condList = StringUtil.split(cond, s);
		String cond_para_name = condList.get(0).trim();
		if (BETWEEN.equals(s)) {
			cond_para_name = cond_para_name.replace(AND, "").trim();
			condInfoMap.put("pre_value", condList.get(1).replace(AND, ",")
					.replace(Constant.SPACE, "").trim());
		} else if (NOTIN.equals(s) || IN.equals(s)) {
			if (StringUtil.isNotBlank(condList.get(1))) {
				condInfoMap.put("pre_value", condList.get(1).replace(Constant.LXKH, "")
						.replace(Constant.RXKH, "")
						.replace(Constant.SPACE, ""));
			}
		} else {
			condInfoMap.put("pre_value", condList.get(1).trim());
		}
		condInfoMap.put("value_size", 64);
		condInfoMap.put("cond_para_name", cond_para_name);
		if (cond_para_name.contains(".")) {
			String column = cond_para_name.substring(cond_para_name.indexOf(".") + 1);
			condInfoMap.put("cond_en_column", column);
			condInfoMap.put("cond_cn_column", column);
		} else {
			condInfoMap.put("cond_en_column", cond_para_name);
			condInfoMap.put("cond_cn_column", cond_para_name);
		}
		condInfoMap.put("con_relation", s);
		condInfoMap.put("value_type", AutoValueType.ZiFuChuan.getCode());
		condInfoMap.put("is_required", IsFlag.Shi.getCode());
		condInfoMap.put("checked", true);
	}

//	@Method(desc = "设置SQL的字段名称，表名和结果字段名称", logicStep = "")
//	@Param(name = "sql", desc = "sql语句", range = "无限制")
//	@Return(desc = "返回SQL的字段名称，表名和结果字段名称", range = "无限制")
//	private Map<String, Object> getTableNameAndColumnName(String sql, List<String> tableName
//			, List<String> columnName, List<String> columnType, List<String> resultColumnName) {
//		Map<String, Object> resultMap = new HashMap<>();
//		boolean flag = false;
//		String dbType = JdbcConstants.ORACLE;
//		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
//		ExportTableAliasVisitor visitor = new ExportTableAliasVisitor();
//		visitor.setFlag(true);
//		visitor.setModelflag(false);
//		visitor.setSelectList(new ArrayList<>());
//		for (SQLStatement stmt : stmtList) {
//			stmt.accept(visitor);
//		}
//		List<SQLSelectItem> selectList = visitor.getSelectList();
//		for (SQLSelectItem item : selectList) {
//			SQLExpr expr = item.getExpr();
//			if (expr instanceof SQLAllColumnExpr) {
//				flag = true;
//				break;
//			}
//		}
//		StringBuilder newSelectPart = new StringBuilder();
//		StringBuilder newSelectPart2 = new StringBuilder();
//		StringBuilder fromParts = new StringBuilder();
//		List<Map<String, String>> fromItems = getFromItems(sql);
//		if (flag) {
//			for (Map<String, String> map : fromItems) {
//				String selectItem = map.get("selectItem");
//				String status = map.get("status");
//				String alias = map.get("alias");
//				List<Map<String, Object>> result = new ArrayList<>();
//				if (status.equals("table")) {// 表示这是一个表
//					List<String> tableNameList = Arrays.asList(selectItem.split(Constant.SPACE));
//					selectItem = tableNameList.get(0);
//					String execSql = "select * from " + selectItem;
//					setColumns(fromParts, execSql, selectItem, result, newSelectPart.append(selectItem), newSelectPart);
//				} else {
//					// 表名这是一个子查询
//					setColumns(fromParts, selectItem, alias, result, newSelectPart.append(selectItem), newSelectPart);
//				}
//			}
//			fromParts = new StringBuilder(fromParts.substring(0, fromParts.length() - 1));
//			sql = getSql(selectList, newSelectPart2, fromParts, newSelectPart.toString());
//			sql = sql.trim();
//		} else {
//			for (Map<String, String> map : fromItems) {
//				String selectItem = map.get("selectItem");
//				String Status = map.get("Status");
//				String alias = map.get("alias");
//				if (Status.equals("table")) {// 表示这是一个表
//					fromParts.append(selectItem).append(",");
//				} else {// 表名这是一个子查询
//					fromParts.append(alias).append(",");
//				}
//			}
//			sql = getSql(selectList, newSelectPart, fromParts, newSelectPart.toString());
//		}
//		// 获取字段名和表名
//		dbType = JdbcConstants.ORACLE;
//		stmtList = SQLUtils.parseStatements(sql, dbType);
//		visitor = new ExportTableAliasVisitor();
//		visitor.setFlag(true);
//		visitor.setModelflag(true);
//		visitor.setColumnname(new ArrayList<>());
//		visitor.setTablename(new ArrayList<>());
//		for (SQLStatement stmt : stmtList) {
//			stmt.accept(visitor);
//		}
//		columnName = visitor.getColumnname();
//		for (int i = 0; i < columnName.size(); i++) {
//			String columnName1 = columnName.get(i);
//			StringBuilder realColumnName = new StringBuilder();
//			List<String> list = StringUtil.split(columnName1, "\n");
//			for (String everySql : list) {
//				realColumnName.append(everySql.trim()).append(Constant.SPACE);
//			}
//			realColumnName = new StringBuilder(realColumnName.toString().trim());
//			columnName.set(i, realColumnName.toString());
//		}
//		tableName = visitor.getTablename();
//		return resultMap;
//	}
//
//	private String getSql(List<SQLSelectItem> selectList, StringBuilder newSelectPart2,
//	                      StringBuilder fromParts, String s) {
//		String sql;
//		for (SQLSelectItem item : selectList) {
//			String everySelectPart;
//			SQLExpr expr = item.getExpr();
//			if (expr instanceof SQLCaseExpr) {
//				everySelectPart = item.getAlias();
//			} else if (expr instanceof SQLAggregateExpr) {
//				SQLAggregateExpr sqlaggregateexpr = (SQLAggregateExpr) expr;
//				List<SQLExpr> arguments = sqlaggregateexpr.getArguments();
//				if (arguments.size() == 0) {
//					everySelectPart = item.toString();
//				} else {
//					everySelectPart = expr.toString();
//				}
//			} else {
//				everySelectPart = expr.toString();
//			}
//			if (expr instanceof SQLAllColumnExpr) {
//				everySelectPart = s;
//			}
//			newSelectPart2.append(everySelectPart).append(",");
//		}
//		newSelectPart2 = new StringBuilder(newSelectPart2.substring(0, newSelectPart2.length() - 1));
//		sql = "select " + newSelectPart2 + " from " + fromParts;
//		return sql;
//	}
//
//	private void setColumns(StringBuilder fromParts, String selectItem, String alias,
//	                        List<Map<String, Object>> result, StringBuilder append, StringBuilder newSelectPart) {
//		new ProcessingData() {
//			@Override
//			public void dealLine(Map<String, Object> map) {
//				result.add(map);
//			}
//		}.getDataLayer(selectItem, Dbo.db());
//		for (Map<String, Object> columnMap : result) {
//			for (Map.Entry<String, Object> entry : columnMap.entrySet()) {
//				append.append(".").append(entry.getKey()).append(",");
//			}
//		}
//		fromParts.append(alias).append(",");
//	}

//	@Method(desc = "获取sql当中from的部分", logicStep = "")
//	@Param(name = "sql", desc = "sql语句", range = "无限制")
//	@Return(desc = "返回解析sql后的from部分", range = "无限制")
//	private List<Map<String, String>> getFromItems(String sql) {
//
//		List<Map<String, String>> resultList = new ArrayList<>();
//		final String dbType = JdbcConstants.ORACLE;
//		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
//		ExportTableAliasVisitor visitor = new ExportTableAliasVisitor();
//		visitor.setListAllTableName(new ArrayList<>());
//		visitor.setListSubquerySQL(new ArrayList<>());
//		visitor.setListSubqueryAliases(new ArrayList<>());
//		visitor.setIfHaveAliaslist(new ArrayList<>());
//		visitor.setFlag(true);
//		visitor.setModelflag(true);
//		visitor.setCount(1);
//		for (SQLStatement stmt : stmtList) {
//			stmt.accept(visitor);
//		}
//		List<String> listAllTableName = visitor.getListAllTableName();
//		List<String> ListSubquerySQL = visitor.getListSubquerySQL();
//		List<String> listSubqueryAliases = visitor.getListSubqueryAliases();
//		List<String> ifHaveAliasList = visitor.getIfHaveAliaslist();
//		for (int i = 0; i < ListSubquerySQL.size(); i++) {
//			String IfHaveAlias = ifHaveAliasList.get(i);
//			String subQuerySql = ListSubquerySQL.get(i);
//			String subQueryAlias = listSubqueryAliases.get(i);
//			Map<String, String> map = new HashMap<>();
//			map.put("IfHaveAlias", IfHaveAlias);
//			map.put("selectItem", subQuerySql);
//			map.put("Status", "subquery");
//			map.put("alias", subQueryAlias);
//			resultList.add(map);
//		}
//		for (String TableName : listAllTableName) {
//			Map<String, String> map = new HashMap<>();
//			map.put("Status", "table");
//			map.put("selectItem", TableName);
//			resultList.add(map);
//		}
//		return resultList;
//	}
}
