package hrds.l.biz.autoanalysis.manage;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
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

import java.util.*;

@DocClass(desc = "自主分析管理类", author = "dhw", createdate = "2020/8/21 14:17")
public class ManageAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();
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
	public List<Map<String, Object>> verifySqlIsLegal(String template_sql) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		try {
			// 1.判断sql是否以;结尾
			if (template_sql.endsWith(";")) {
				throw new BusinessException("sql不要使用;结尾");
			}
			List<Map<String, Object>> resultList = new ArrayList<>();
			// 2.校验sql
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) {
					resultList.add(map);
				}
			}.getPageDataLayer(template_sql, Dbo.db(),1,10);
			return resultList;
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException("sql错误：" + e.getMessage());
		}
	}

	@Method(desc = "生成自主分析模板配置参数", logicStep = "1.校验sql是否正确" +
			"2.sql格式化并处理sql去除;结尾" +
			"3.判断sql是否为union 或者union all解析sql获取where条件以及select条件" +
			"4.返回SQL WHERE 条件以及SQL SELECT条件")
	@Param(name = "template_sql", desc = "自主取数模板sql", range = "无限制")
	public Map<String, Object> generateTemplateParam(String template_sql) {
		// 1.校验sql是否正确
		verifySqlIsLegal(template_sql);
		String dbType = JdbcConstants.ORACLE;
		// 2.sql格式化并处理sql去除;结尾
		String format_sql = SQLUtils.format(template_sql, dbType).trim();
		if (format_sql.endsWith(";")) {
			format_sql = format_sql.substring(0, format_sql.length() - 1);
		}
		Set<Map<String, Object>> autoTpCondInfoList = new HashSet<>();
		Set<Auto_tp_res_set> autoTpResSets = new HashSet<>();
		DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(format_sql);
		if (druidParseQuerySql.leftWhere != druidParseQuerySql.rightWhere) {
			if (null != druidParseQuerySql.rightWhere) {
				setAutoTpCond(autoTpCondInfoList, druidParseQuerySql.rightWhere);
			}
		}
		if (null != druidParseQuerySql.leftWhere) {
			setAutoTpCond(autoTpCondInfoList, druidParseQuerySql.leftWhere);
		}
		// 获取模板结果配置信息
		getAutoTpResSet(autoTpResSets, format_sql, druidParseQuerySql.selectList);
		Map<String, Object> templateMap = new HashMap<>();
		templateMap.put("autoTpCondInfo", autoTpCondInfoList);
		templateMap.put("autoTpResSetInfo", autoTpResSets);
		// 4.返回SQL WHERE 条件以及SQL SELECT条件
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
					objectMap.put("is_required", map.get("is_required"));
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
		}.getPageDataLayer(template_sql, Dbo.db(), 1, showNum <= 0 ? 100 : showNum);
		return resultData;
	}

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

	private void setAutoTpCond(Set<Map<String, Object>> autoTpCondInfoList, SQLExpr sqlExpr) {
		Map<String, Object> condInfoMap = new HashMap<>();
		if (sqlExpr instanceof SQLBetweenExpr) {
			SQLBetweenExpr sqlBetweenExpr = (SQLBetweenExpr) sqlExpr;
			SQLExpr testExpr = sqlBetweenExpr.testExpr;
			setSqlPropertyExpr(condInfoMap, testExpr);
			condInfoMap.put("con_relation", "BETWEEN");
			// fixme 这个原来默认给64，不知道这个作用是什么
			condInfoMap.put("value_size", 64);
			SQLExpr beginExpr = sqlBetweenExpr.beginExpr;
			String pre_value = beginExpr.toString() + "," + sqlBetweenExpr.endExpr.toString();
			setAutoTpCondBySqlExpr(autoTpCondInfoList, condInfoMap, beginExpr, pre_value);
		} else if (sqlExpr instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
			SQLExpr exprLeft = sqlBinaryOpExpr.getLeft();
			SQLExpr exprRight = sqlBinaryOpExpr.getRight();
			if (exprLeft instanceof SQLIdentifierExpr || exprLeft instanceof SQLPropertyExpr) {
				setSqlPropertyExpr(condInfoMap, exprLeft);
				condInfoMap.put("con_relation", sqlBinaryOpExpr.getOperator().name);
				condInfoMap.put("value_size", 64);
				SQLExpr right = sqlBinaryOpExpr.getRight();
				setAutoTpCondBySqlExpr(autoTpCondInfoList, condInfoMap, right, right.toString());
			} else {
				setAutoTpCond(autoTpCondInfoList, exprLeft);
				setAutoTpCond(autoTpCondInfoList, exprRight);
			}
		} else if (sqlExpr instanceof SQLInListExpr) {
			SQLInListExpr sqlInListExpr = (SQLInListExpr) sqlExpr;
			setSqlPropertyExpr(condInfoMap, sqlInListExpr.getExpr());
			condInfoMap.put("con_relation", "IN");
			condInfoMap.put("value_size", 64);
			List<SQLExpr> targetList = sqlInListExpr.getTargetList();
			StringBuilder sb = new StringBuilder();
			for (SQLExpr expr : targetList) {
				sb.append(expr.toString()).append(",");
			}
			setAutoTpCondBySqlExpr(autoTpCondInfoList, condInfoMap, sqlInListExpr.getExpr(),
					sb.deleteCharAt(sb.length() - 1).toString());
		}
	}

	private void setAutoTpCondBySqlExpr(Set<Map<String, Object>> autoTpCondInfoList,
	                                    Map<String, Object> condInfoMap, SQLExpr sqlExpr, String pre_value) {
		if (sqlExpr instanceof SQLIntegerExpr) {
			condInfoMap.put("value_type", AutoValueType.ShuZhi.getCode());
		} else if (sqlExpr instanceof SQLDateExpr) {
			condInfoMap.put("value_type", AutoValueType.RiQi.getCode());
		} else {
			condInfoMap.put("value_type", AutoValueType.ZiFuChuan.getCode());
		}
		condInfoMap.put("is_required", IsFlag.Shi.getCode());
		condInfoMap.put("pre_value", pre_value);
		condInfoMap.put("checked", true);
		autoTpCondInfoList.add(condInfoMap);
	}

	private void setSqlPropertyExpr(Map<String, Object> condInfoMap, SQLExpr sqlExpr) {
		if (sqlExpr instanceof SQLPropertyExpr) {
			SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlExpr;
			condInfoMap.put("cond_para_name", sqlPropertyExpr.toString());
			condInfoMap.put("cond_en_column", sqlPropertyExpr.getName());
			condInfoMap.put("cond_cn_column", sqlPropertyExpr.getName());
		} else {
			condInfoMap.put("cond_para_name", sqlExpr.toString());
			condInfoMap.put("cond_en_column", sqlExpr.toString());
			condInfoMap.put("cond_cn_column", sqlExpr.toString());
		}
	}

	private void getAutoTpResSet(Set<Auto_tp_res_set> autoTpResSets, String sql,
	                             List<SQLSelectItem> selectList) {
		// 获取表名对应别名，key为原表名，value为表别名
		Map<String, String> sourceAndAliasName = getTableAndAliasName(sql);
		for (SQLSelectItem sqlSelectItem : selectList) {
			String expr = sqlSelectItem.getExpr().toString();
			String alias = sqlSelectItem.getAlias();
			if (sqlSelectItem.getExpr() instanceof SQLAllColumnExpr) {
				// 所有列
				getAllColumnsBySql(autoTpResSets, sql);
			} else if (sqlSelectItem.getExpr() instanceof SQLPropertyExpr) {
				// 字段带表别名
				SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlSelectItem.getExpr();
				// 子段名
				String column_en_name = sqlPropertyExpr.getName();
				String table_alias = sqlPropertyExpr.getOwner().toString().toUpperCase();
				String table_name = sourceAndAliasName.get(table_alias);
				List<Map<String, Object>> columns = DataTableUtil.getColumnByTableName(Dbo.db(), table_name);
				if (column_en_name.equals("*")) {
					setAllColumns(autoTpResSets, table_name, columns);
				} else {
					Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
					auto_tp_res_set.setSource_table_name(table_name);
					auto_tp_res_set.setColumn_en_name(column_en_name);
					auto_tp_res_set.setRes_show_column(StringUtil.isBlank(alias) ? column_en_name : alias);
					setAutoTpResSet(autoTpResSets, auto_tp_res_set, columns);
				}
			} else if (sqlSelectItem.getExpr() instanceof SQLAggregateExpr) {
				// 聚合函数
				Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
				auto_tp_res_set.setSource_table_name("UNKNOW");
				auto_tp_res_set.setColumn_en_name(expr);
				auto_tp_res_set.setColumn_cn_name(expr);
				auto_tp_res_set.setRes_show_column(StringUtil.isBlank(alias) ? expr : alias);
				auto_tp_res_set.setColumn_type(AutoValueType.ShuZhi.getCode());
				autoTpResSets.add(auto_tp_res_set);
			} else if (sqlSelectItem.getExpr() instanceof SQLIdentifierExpr) {
				// 字段不带表别名
				SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlSelectItem.getExpr();
				Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
				String exprName = sqlIdentifierExpr.getName();
				auto_tp_res_set.setColumn_en_name(exprName);
				auto_tp_res_set.setRes_show_column(StringUtil.isBlank(alias) ? exprName : alias);
				List<String> tableList = DruidParseQuerySql.parseSqlTableToList(sql);
				// 获取所有表对应字段信息
				Map<String, String> columnByTable = getColumnByTable(tableList);
				String tableAndChColumnName = columnByTable.get(exprName.toUpperCase());
				List<String> tableAndColumn = StringUtil.split(tableAndChColumnName, Constant.METAINFOSPLIT);
				auto_tp_res_set.setSource_table_name(tableAndColumn.get(0));
				auto_tp_res_set.setColumn_cn_name(tableAndColumn.get(1));
				if (numbersArray.contains(tableAndColumn.get(2))) {
					auto_tp_res_set.setColumn_type(AutoValueType.ShuZhi.getCode());
				} else {
					auto_tp_res_set.setColumn_type(AutoValueType.ZiFuChuan.getCode());
				}
				autoTpResSets.add(auto_tp_res_set);
			} else {
				throw new BusinessException(sqlSelectItem.getExpr() + "未开发 有待开发");
			}
		}
	}

	private Map<String, String> getColumnByTable(List<String> tableList) {
		Map<String, String> columnByTable = new HashMap<>();
		for (String table_name : tableList) {
			List<Map<String, Object>> columns = DataTableUtil.getColumnByTableName(Dbo.db(),
					table_name);
			for (Map<String, Object> column : columns) {
				// 列英文名对应表名，列中文名，列类型
				columnByTable.put(column.get("column_name").toString(), table_name
						+ Constant.METAINFOSPLIT + column.get("column_ch_name").toString()
						+ Constant.METAINFOSPLIT + column.get("column_type").toString());
			}
		}
		return columnByTable;
	}

	private void getAllColumnsBySql(Set<Auto_tp_res_set> autoTpResSets, String sql) {
		List<String> tableList = DruidParseQuerySql.parseSqlTableToList(sql);
		if (tableList.isEmpty()) {
			throw new BusinessException("该sql有误请检查");
		}
		// 查询select *
		for (String table_name : tableList) {
			List<Map<String, Object>> columns = DataTableUtil.getColumnByTableName(Dbo.db(),
					table_name);
			setAllColumns(autoTpResSets, table_name, columns);
		}
	}

	private void setAutoTpResSet(Set<Auto_tp_res_set> autoTpResSets, Auto_tp_res_set auto_tp_res_set,
	                             List<Map<String, Object>> columns) {
		for (Map<String, Object> column : columns) {
			if (auto_tp_res_set.getColumn_en_name().equals(column.get("column_name").toString())) {
				auto_tp_res_set.setColumn_cn_name(column.get("column_ch_name").toString());
				if (numbersArray.contains(column.get("column_type").toString())) {
					auto_tp_res_set.setColumn_type(AutoValueType.ShuZhi.getCode());
				} else {
					auto_tp_res_set.setColumn_type(AutoValueType.ZiFuChuan.getCode());
				}
				autoTpResSets.add(auto_tp_res_set);
			}
		}
	}

	private void setAllColumns(Set<Auto_tp_res_set> autoTpResSets, String table_name,
	                           List<Map<String, Object>> columns) {
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
			autoTpResSets.add(auto_tp_res_set);
		}
	}

	@Method(desc = "获取表名对应别名", logicStep = "1.格式化sql" +
			"2.获取sql片段" +
			"3.根据条件获取表名以及表别名")
	@Param(name = "sql", desc = "查询sql", range = "无限制")
	@Return(desc = "返回表名对应别名", range = "无限制")
	private Map<String, String> getTableAndAliasName(String sql) {
		// 1.格式化sql
		String format = SQLUtils.format(sql, JdbcConstants.ORACLE);
		// 2.获取sql片段
		List<String> sqlPartList = StringUtil.split(format, "\n");
		Map<String, String> map = new HashMap<>();
		// 3.根据条件获取表名以及表别名
		for (String sqlPart : sqlPartList) {
			if (sqlPart.startsWith("FROM")) {
				List<String> tableAlias = StringUtil.split(sqlPart, Constant.SPACE);
				if (tableAlias.size() == 3) {
					// 获取表名对应别名，key为表别名，value为原表名
					map.put(tableAlias.get(2).toUpperCase(), tableAlias.get(1));
				} else {
					map.put(tableAlias.get(1).toUpperCase(), tableAlias.get(1));
				}
			} else if (sqlPart.contains("JOIN")) {
				List<String> tableAlias = StringUtil.split(sqlPart, Constant.SPACE);
				// 获取表名对应别名，key为表别名，value为原表名
				map.put(tableAlias.get(3).toUpperCase(), tableAlias.get(2));
			}
		}
		return map;
	}

	@Method(desc = "根据ID查询列信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.根据不同数据源类型查询表的列信息并返回" +
			"2.1贴源层" +
			"2.2集市层")
	@Param(name = "file_id", desc = "表ID", range = "无限制", nullable = true)
	@Param(name = "data_layer", desc = "数据层，树根节点", range = "无限制")
	@Return(desc = "根据不同数据源类型查询表的列信息并返回", range = "无限制")
	public Map<String, Object> searchFieldById(String file_id, String data_layer) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		return DataTableUtil.getTableInfoAndColumnInfo(data_layer, file_id);
	}
}
