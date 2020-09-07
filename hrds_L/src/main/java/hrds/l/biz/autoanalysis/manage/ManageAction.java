package hrds.l.biz.autoanalysis.manage;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.util.JdbcConstants;
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
	public static Map<String, Object> generateTemplateParam(String template_sql) {
		String dbType = JdbcConstants.POSTGRESQL;
		// sql格式化
		String format_sql = SQLUtils.format(template_sql, dbType).trim();
		if (format_sql.endsWith(";")) {
			format_sql = format_sql.substring(0, format_sql.length() - 1);
		}
		List<Auto_tp_cond_info> autoTpCondInfoList = new ArrayList<>();
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

	@Method(desc = "保存模板配置页面的信息包括 模板内容 条件参数 和 结果设置", logicStep = "1.校验自主取数模板字段是否合法" +
			"2.判断模板名称是否已存在" +
			"3.新增模板信息表" +
			"4.校验自主取数模板条件字段是否合法" +
			"5.校验模板结果对象字段是否合法" +
			"6.新增模板条件表")
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
		addAutoTpCondInfo(autoTpCondInfos, auto_tp_info);
		addAutoTpResSet(autoTpResSets, auto_tp_info);
	}

	private void addAutoTpResSet(Auto_tp_res_set[] autoTpResSets, Auto_tp_info auto_tp_info) {
		for (Auto_tp_res_set auto_tp_res_set : autoTpResSets) {
			// 5.校验模板结果对象字段是否合法
			checkAutoTpResSetFields(auto_tp_res_set);
			auto_tp_res_set.setTemplate_res_id(PrimayKeyGener.getNextId());
			auto_tp_res_set.setTemplate_id(auto_tp_info.getTemplate_id());
			auto_tp_res_set.setCreate_date(DateUtil.getSysDate());
			auto_tp_res_set.setCreate_time(DateUtil.getSysTime());
			auto_tp_res_set.setCreate_user(getUserId());
			// 6.新增模板条件表
			auto_tp_res_set.add(Dbo.db());
		}
	}

	private void addAutoTpCondInfo(Auto_tp_cond_info[] autoTpCondInfos, Auto_tp_info auto_tp_info) {
		for (Auto_tp_cond_info auto_tp_cond_info : autoTpCondInfos) {
			// 4.校验自主取数模板条件字段是否合法
			checkAutoTpCondInfoFields(auto_tp_cond_info);
			auto_tp_cond_info.setTemplate_cond_id(PrimayKeyGener.getNextId());
			auto_tp_cond_info.setTemplate_id(auto_tp_info.getTemplate_id());
			// 新增模板条件表
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
	}

	private void checkAutoTpInfoFields(Auto_tp_info auto_tp_info) {
		Validator.notBlank(auto_tp_info.getTemplate_name(), "模板名称不能为空");
		IsFlag.ofEnumByCode(auto_tp_info.getData_source());
		Validator.notBlank(auto_tp_info.getTemplate_sql(), "模板sql不能为空");
		Validator.notBlank(auto_tp_info.getTemplate_desc(), "模板描述不能为空");
		AutoTemplateStatus.ofEnumByCode(auto_tp_info.getTemplate_status());
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
		// 1.根据模板ID获取自主取数模板条件信息
		return Dbo.queryList("select * from " + Auto_tp_cond_info.TableName + " where template_id=?",
				template_id);
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
	@Param(name = "auto_tp_info", desc = "模板信息实体对象数组", range = "与数据库表规则一致", isBean = true)
	public void updateTemplateConfInfo(Auto_tp_cond_info[] autoTpCondInfos, Auto_tp_res_set[] autoTpResSets,
	                                   Auto_tp_info auto_tp_info) {
		// 数据可访问权限处理方式：通过user_id进行访问权限限制
		// 1.校验模板参数合法性
		Validator.notNull(auto_tp_info.getTemplate_id(), "编辑时模板ID不能为空");
		checkAutoTpInfoFields(auto_tp_info);
		if (AutoTemplateStatus.BianJi != AutoTemplateStatus.ofEnumByCode(auto_tp_info.getTemplate_status())) {
			throw new BusinessException("自主取数模板状态不为编辑：" + auto_tp_info.getTemplate_status());
		}
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
		addAutoTpCondInfo(autoTpCondInfos, auto_tp_info);
		// 6.新增模板结果数据
		addAutoTpResSet(autoTpResSets, auto_tp_info);

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
		// 1.根据sql查询数据结果
		new ProcessingData() {
			@Override
			public void dealLine(Map<String, Object> map) {
				resultData.add(map);
			}
		}.getPageDataLayer(template_sql, Dbo.db(), 0, Math.min(showNum, 1000));
		// 2.返回数据结果
		return resultData;
	}

	@Method(desc = "模板配置结果设置点击 解析sq生成 select 部分的内容", logicStep = "")
	@Param(name = "template_sql", desc = "自主取数模板sql", range = "无限制")
	@Return(desc = "", range = "")
	public void getTemplateResult(String template_sql) {
		DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(template_sql);
		Map<String, String> selectColumnMap = druidParseQuerySql.getSelectColumnMap();
	}

	public static void main(String[] args) {
//		String template_sql = "SELECT t1.c_customer_sk, t1.c_customer_id, t1.c_current_cdemo_sk, t1.c_current_hdemo_sk, t1.c_current_addr_sk\n" +
//				"\t, t1.c_first_shipto_date_sk, t1.c_first_sales_date_sk, t1.c_salutation, t1.c_first_name, t1.c_last_name\n" +
//				"\t, t1.c_preferred_cust_flag,t2.*\n" +
//				"FROM CS01_DB01_CUSTOMER t1\n" +
//				"\tLEFT JOIN CS01_DB01_ITEM t2 ON t1.c_customer_sk = t2.i_item_sk\n" +
//				"WHERE t1.c_customer_sk != 1";
		String template_sql = "SELECT t1.*, t2.cs_ship_date_sk AS b\n" +
				"FROM CS01_DB01_ITEM t1\n" +
				"\tLEFT JOIN CS01_DB01_CATALOG_SALES t2 ON t1.i_item_sk = t2.cs_warehouse_sk\n" +
				"WHERE t1.i_item_sk != 1\n" +
				"\tAND t1.i_class_id BETWEEN 1 AND 2";
		System.out.println(JsonUtil.toJson(generateTemplateParam(template_sql)));
//		DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(template_sql);
//		Map<String, String> selectColumnMap = druidParseQuerySql.getSelectColumnMap();
//		selectColumnMap.forEach((k, v) -> System.out.println(k + "-->" + v));
	}

	@Method(desc = "发布自主取数模板", logicStep = "1.更新模板状态为发布")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	public void releaseAutoAnalysisTemplate(long template_id) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 1.更新模板状态为发布
		DboExecute.updatesOrThrow("更新自主取数模板状态为发布失败",
				"update" + Auto_tp_info.TableName + " set template_status=? where template_id=?",
				AutoTemplateStatus.FaBu.getCode(), template_id);
	}

	@Method(desc = "删除自主取数模板", logicStep = "1.更新模板状态为注销")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	public void deleteAutoAnalysisTemplate(long template_id) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 1.更新模板状态为注销
		DboExecute.updatesOrThrow("更新自主取数模板状态为注销失败",
				"update" + Auto_tp_info.TableName + " set template_status=? where template_id=?",
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

	private static void getAutoTpCondInfo(List<Auto_tp_cond_info> autoTpCondInfoList,
	                                      List<Auto_tp_res_set> autoTpResSets, String sql) {
		DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(sql);
		SQLExpr whereInfo = druidParseQuerySql.whereInfo;
		List<String> originalField = druidParseQuerySql.parseSelectOriginalField();
		List<String> aliasField = druidParseQuerySql.parseSelectAliasField();
		for (int i = 0; i < originalField.size(); i++) {
			Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
			if (originalField.get(i).contains(".")) {
				auto_tp_res_set.setSource_table_name(originalField.get(i).substring(originalField.get(i).indexOf(".") + 1));
				auto_tp_res_set.setColumn_en_name(originalField.get(i).substring(originalField.get(i).indexOf(".") + 1));
			}
			auto_tp_res_set.setRes_show_column(aliasField.get(i));
			autoTpResSets.add(auto_tp_res_set);
		}
		if (null != whereInfo) {
			List<String> whereList = StringUtil.split(whereInfo.toString(), "\n");
			for (int i = 0; i < whereList.size(); i++) {
				String cond = whereList.get(i).trim();
				Auto_tp_cond_info auto_tp_cond_info = new Auto_tp_cond_info();
				cond = getCond(autoTpCondInfoList, cond, auto_tp_cond_info);
				if (i + 1 < whereList.size()) {
					String child_cond = whereList.get(i + 1).trim();
					if (child_cond.contains(Constant.RXKH) && cond.contains(WHERE)) {
						getCond(autoTpCondInfoList, cond, auto_tp_cond_info);
					}
				}
			}
		}
	}

	private static String getCond(List<Auto_tp_cond_info> autoTpCondInfoList, String cond,
	                              Auto_tp_cond_info auto_tp_cond_info) {
		if (cond.startsWith(AND) && !cond.contains("BETWEEN")) {
			cond = cond.replace(AND, "");
		}
		if (cond.startsWith(OR)) {
			cond = cond.replace(OR, "");
		}
		if (cond.startsWith(WHERE)) {
			cond = cond.replace(WHERE, "");
		}
		if (cond.contains(BETWEEN)) {
			String[] split = cond.split(BETWEEN);
			String cond_para_name = split[0].trim().replace(AND, "");
			auto_tp_cond_info.setCond_para_name(cond_para_name);
			if (cond_para_name.contains(".")) {
				String column = cond_para_name.substring(cond_para_name.indexOf(".") + 1).trim();
				auto_tp_cond_info.setCond_en_column(column);
				auto_tp_cond_info.setCond_cn_column(column);
			}
			auto_tp_cond_info.setCon_relation(BETWEEN);
			auto_tp_cond_info.setPre_value(split[1].replace(AND, ",")
					.replace(Constant.SPACE, "").trim());
			autoTpCondInfoList.add(auto_tp_cond_info);
		}
		if (cond.contains("!=")) {
			String[] split = cond.split("!=");
			auto_tp_cond_info.setCond_para_name(split[0].trim());
			if (split[0].trim().contains(".")) {
				String column = split[0].trim().substring(split[0].trim().indexOf(".") + 1);
				auto_tp_cond_info.setCond_en_column(column);
				auto_tp_cond_info.setCond_cn_column(column);
			}
			auto_tp_cond_info.setCon_relation("!=");
			auto_tp_cond_info.setPre_value(split[1].trim());
			autoTpCondInfoList.add(auto_tp_cond_info);
		}
		if (cond.contains("=") && !cond.contains("!=") && !cond.contains(">=") && !cond.contains("<=")) {
			String[] split = cond.split("=");
			auto_tp_cond_info.setCond_para_name(split[0].trim());
			if (split[0].trim().contains(".")) {
				String column = split[0].trim().substring(split[0].trim().indexOf(".") + 1).trim();
				auto_tp_cond_info.setCond_en_column(column);
				auto_tp_cond_info.setCond_cn_column(column);
			}
			auto_tp_cond_info.setCon_relation("=");
			auto_tp_cond_info.setPre_value(split[1].trim());
			autoTpCondInfoList.add(auto_tp_cond_info);
		}
		if (cond.contains(">=")) {
			String[] split = cond.split(">=");
			auto_tp_cond_info.setCond_para_name(split[0].trim());
			if (split[0].trim().contains(".")) {
				auto_tp_cond_info.setCond_en_column(split[0].trim().substring(split[0].trim().indexOf(".") + 1));
			}
			auto_tp_cond_info.setCon_relation(">=");
			auto_tp_cond_info.setPre_value(split[1].trim());
			autoTpCondInfoList.add(auto_tp_cond_info);
		}
		if (cond.contains("<=")) {
			String[] split = cond.split("<=");
			auto_tp_cond_info.setCond_para_name(split[0].trim());
			if (split[0].trim().contains(".")) {
				auto_tp_cond_info.setCond_en_column(split[0].trim().substring(split[0].trim().indexOf(".") + 1));
			}
			auto_tp_cond_info.setCon_relation("<=");
			auto_tp_cond_info.setPre_value(split[1].trim());
			autoTpCondInfoList.add(auto_tp_cond_info);
		}
		if (cond.contains(">") && !cond.contains(">=")) {
			String[] split = cond.split(">");
			auto_tp_cond_info.setCond_para_name(split[0].trim());
			if (split[0].trim().contains(".")) {
				auto_tp_cond_info.setCond_en_column(split[0].trim().substring(split[0].trim().indexOf(".") + 1));
			}
			auto_tp_cond_info.setCon_relation(">");
			auto_tp_cond_info.setPre_value(split[1].trim());
			autoTpCondInfoList.add(auto_tp_cond_info);
		}
		if (cond.contains("<") && !cond.contains("<=")) {
			String[] split = cond.split("<");
			auto_tp_cond_info.setCond_para_name(split[0].trim());
			if (split[0].trim().contains(".")) {
				auto_tp_cond_info.setCond_en_column(split[0].trim().substring(split[0].trim().indexOf(".") + 1));
			}
			auto_tp_cond_info.setCon_relation("<");
			auto_tp_cond_info.setPre_value(split[1].trim());
			autoTpCondInfoList.add(auto_tp_cond_info);
		}
		if (cond.contains(NOTIN)) {
			String[] split = cond.split(NOTIN);
			auto_tp_cond_info.setCond_para_name(split[0].trim());
			if (split[0].trim().contains(".")) {
				auto_tp_cond_info.setCond_en_column(split[0].trim().substring(split[0].trim().indexOf(".") + 1));
			}
			auto_tp_cond_info.setCon_relation("IN");
			if (StringUtil.isNotBlank(split[1])) {
				auto_tp_cond_info.setPre_value(split[1].replace(Constant.LXKH, "")
						.replace(Constant.RXKH, "")
						.replace(Constant.SPACE, ""));
			}
			autoTpCondInfoList.add(auto_tp_cond_info);
		}
		if (cond.contains(IN) && !cond.contains(NOTIN)) {
			String[] split = cond.split(IN);
			auto_tp_cond_info.setCond_para_name(split[0].trim());
			if (split[0].trim().contains(".")) {
				auto_tp_cond_info.setCond_en_column(split[0].trim().substring(split[0].trim().indexOf(".") + 1));
			}
			auto_tp_cond_info.setCon_relation(IN);
			if (StringUtil.isNotBlank(split[1].replace(Constant.LXKH, ""))) {
				auto_tp_cond_info.setPre_value(split[1].replace(Constant.LXKH, "")
						.replace(Constant.RXKH, "")
						.replace(Constant.SPACE, ""));
				autoTpCondInfoList.add(auto_tp_cond_info);
			}
		}
		return cond;
	}
}
