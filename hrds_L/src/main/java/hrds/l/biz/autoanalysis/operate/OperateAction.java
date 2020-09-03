package hrds.l.biz.autoanalysis.operate;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.collection.ProcessingData;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DataTableUtil;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.l.biz.autoanalysis.bean.ComponentBean;

import java.util.*;

@DocClass(desc = "自主分析操作类", author = "dhw", createdate = "2020/8/24 11:29")
public class OperateAction extends BaseAction {

	private static String TempTableName = " TEMP_TABLE ";

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


	@Method(desc = "查询自主取数模板信息", logicStep = "1.查询并返回自主取数模板信息")
	@Return(desc = "返回自主取数模板信息", range = "无限制")
	public List<Map<String, Object>> getAccessTemplateInfo() {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.查询并返回自主取数模板信息
		return Dbo.queryList(
				"select t1.template_id,t1.template_name,t1.template_desc,t1.create_date,t1.create_time,"
						+ "t1.create_user,count(t2.fetch_sum_id) as count_number " +
						" from " + Auto_tp_info.TableName + " t1 eft join " + Auto_fetch_sum.TableName + " t2"
						+ " on t1.template_id = t2.template_id "
						+ " where template_status = ? group by t1.template_id "
						+ " order by t1.create_date desc,t1.create_time desc",
				AutoTemplateStatus.FaBu.getCode());
	}

	@Method(desc = "模糊查询自主取数模板信息", logicStep = "1.模糊查询自主取数模板信息")
	@Param(name = "template_name", desc = "模板名称", range = "无限制")
	@Return(desc = "返回模糊查询自主取数模板信息", range = "无限制")
	public List<Map<String, Object>> getAccessTemplateInfoByName(String template_name) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.模糊查询自主取数模板信息
		return Dbo.queryList(
				"select t1.template_id,t1.template_name,t1.template_desc,t1.create_date,t1.create_time,"
						+ "t1.create_user,count(t2.fetch_sum_id) as count_number " +
						" from " + Auto_tp_info.TableName + " t1 eft join " + Auto_fetch_sum.TableName + " t2"
						+ " on t1.template_id = t2.template_id "
						+ " where template_status = ? and template_name like ?"
						+ " group by t1.template_id order by t1.create_date desc,t1.create_time desc",
				AutoTemplateStatus.FaBu.getCode(), "%" + template_name + "%");
	}

	@Method(desc = "根据模板ID查询自主取数模板信息", logicStep = "1.根据模板ID查询自主取数信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回根据模板ID查询自主取数信息", range = "无限制")
	public Map<String, Object> getAccessTemplateInfoById(long template_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.根据模板ID查询自主取数信息
		return Dbo.queryOneObject(
				"select template_name,template_desc from " + Auto_tp_info.TableName + " where template_id = ?",
				template_id);
	}

	@Method(desc = "获取自主取数结果字段", logicStep = "1.获取自主取数结果字段")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数过滤条件", range = "无限制")
	public List<Map<String, Object>> getAccessResultFields(long template_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.获取自主取数结果字段
		return Dbo.queryList(
				"select res_show_column,template_res_id from " + Auto_tp_res_set.TableName
						+ " where template_id = ?",
				template_id);
	}

	@Method(desc = "获取自主取数过滤条件", logicStep = "1.获取自主取数过滤条件")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数过滤条件", range = "无限制")
	public List<Map<String, Object>> getAutoAccessFilterCond(long template_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.获取自主取数过滤条件
		return Dbo.queryList(
				"select is_required,cond_cn_column,con_relation,template_cond_id,pre_value,value_type,value_size "
						+ Auto_tp_cond_info.TableName + " from where template_id = ?",
				template_id);
	}

	@Method(desc = "获取自主取数选择历史信息", logicStep = "1.获取自主取数选择历史信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数选择历史信息", range = "无限制")
	public List<Map<String, Object>> getAccessSelectHistory(long template_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.获取自主取数选择历史信息
		return Dbo.queryList(
				"select * from " + Auto_fetch_sum.TableName + " where template_id = ? "
						+ " order by create_date desc ,create_time desc limit 10",
				template_id);
	}

	@Method(desc = "通过选择历史情况 获取之前的条件以及结果配置页面", logicStep = "1.获取自主取数选择历史信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数选择历史信息", range = "无限制")
	public List<Map<String, Object>> getAccessCondFromHistory(long fetch_sum_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 获取到用户之前在页面中 填写的参数值 用模板条件ID进行匹配 template_cond_id
		return Dbo.queryList(
				"select cond_cn_column,con_relation,template_cond_id,pre_value,is_required,value_type,value_size"
						+ " from " + Auto_tp_cond_info.TableName + " t1 left join " + Auto_tp_info.TableName + " t2 "
						+ " on t1.template_id = t2.template_id left join " + Auto_fetch_sum.TableName + " t3 "
						+ " on t2.template_id = t3.template_id where t3.fetch_sum_id = ?",
				fetch_sum_id);
	}

	@Method(desc = "通过选择历史情况 获取之前的条件以及结果配置页面", logicStep = "1.获取自主取数选择历史信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数选择历史信息", range = "无限制")
	public List<Map<String, Object>> getAccessResultFromHistory(long fetch_sum_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 获取用户之前在页面上 勾选的 显示结果 用模板结果ID进行匹配 template_res_id
		return Dbo.queryList(
				"select t1.res_show_column,t1.template_res_id,t3.fetch_sum_id "
						+ " from " + Auto_tp_res_set.TableName + " t1 left join " + Auto_tp_info.TableName + " t2 "
						+ " on t1.template_id = t2.template_id left join " + Auto_fetch_sum.TableName + " t3 "
						+ " on t2.template_id = t3.template_id where t3.fetch_sum_id = ?",
				fetch_sum_id);
	}

	@Method(desc = "获取自主取数清单查询结果", logicStep = "1.根据取数汇总ID获取取数sql" +
			"2.判断取数汇总ID对应的取数sql是否存在" +
			"3.获取自主取数清单查询结果")
	@Param(name = "fetch_sum_id", desc = "取数汇总ID", range = "配置取数模板时生成")
	@Return(desc = "返回主取数清单查询结果", range = "")
	public List<Map<String, Object>> getAutoAccessQueryResult(long fetch_sum_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.根据取数汇总ID获取取数sql
		String fetch_sql = getAccessSql(fetch_sum_id);
		// 3.获取自主取数清单查询结果
		List<Map<String, Object>> accessResult = new ArrayList<>();
		new ProcessingData() {
			@Override
			public void dealLine(Map<String, Object> map) {
				accessResult.add(map);
			}
		}.getDataLayer(fetch_sql, Dbo.db());
		return accessResult;
	}

	@Method(desc = "保存自主取数清单查询入库信息", logicStep = "1.判断模板信息是否已不存在" +
			"2.新增取数汇总表数据" +
			"3.取数条件表入库" +
			"4.取数结果选择情况入库")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Param(name = "autoFetchConds", desc = "自主取数条件对象数组", range = "与数据库对应表规则一致", isBean = true)
	@Param(name = "autoFetchRes", desc = "自主取数结果对象数组", range = "与数据库对应表规则一致", isBean = true)
	public void saveAutoAccessInfo(Auto_fetch_sum auto_fetch_sum, Auto_fetch_cond[] autoFetchConds,
	                               Auto_fetch_res[] autoFetchRes) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.判断模板信息是否已不存在
		Validator.notNull(auto_fetch_sum.getTemplate_id(), "模板ID不能为空");
		Validator.notBlank(auto_fetch_sum.getFetch_name(), "取数名称不能为空");
		isAutoTpInfoExist(auto_fetch_sum.getTemplate_id());
		auto_fetch_sum.setFetch_sum_id(PrimayKeyGener.getNextId());
		auto_fetch_sum.setCreate_date(DateUtil.getSysDate());
		auto_fetch_sum.setCreate_time(DateUtil.getSysTime());
		auto_fetch_sum.setCreate_user(getUserId());
		auto_fetch_sum.setFetch_sql(getWhereSql(auto_fetch_sum.getTemplate_id(), autoFetchConds, autoFetchRes));
		auto_fetch_sum.setFetch_status(AutoFetchStatus.BianJi.getCode());
		// 2.新增取数汇总表数据
		auto_fetch_sum.add(Dbo.db());
		for (Auto_fetch_cond auto_fetch_cond : autoFetchConds) {
			Validator.notBlank(auto_fetch_cond.getCond_value(), "条件值不能为空");
			Validator.notNull(auto_fetch_cond.getTemplate_cond_id(), "模板条件ID不能为空");
			auto_fetch_cond.setFetch_cond_id(PrimayKeyGener.getNextId());
			auto_fetch_cond.setFetch_sum_id(auto_fetch_sum.getFetch_sum_id());
			// 3.取数条件表入库
			auto_fetch_cond.add(Dbo.db());
		}
		for (Auto_fetch_res auto_fetch_res : autoFetchRes) {
			Validator.notBlank(auto_fetch_res.getFetch_res_name(), "取数结果名称不能为空");
			Validator.notNull(auto_fetch_res.getTemplate_res_id(), "模板结果ID不能为空");
			List<String> res_show_column = Dbo.queryOneColumnList(
					"select res_show_column from " + Auto_tp_res_set.TableName
							+ " where template_res_id = ?",
					auto_fetch_res.getFetch_res_id());
			auto_fetch_res.setFetch_res_name(res_show_column.get(0));
			auto_fetch_res.setShow_num(auto_fetch_res.getShow_num() == null ? 0 : auto_fetch_res.getShow_num());
			auto_fetch_res.setFetch_res_id(PrimayKeyGener.getNextId());
			auto_fetch_res.setFetch_sum_id(auto_fetch_sum.getFetch_sum_id());
			// 4.取数结果选择情况入库
			auto_fetch_res.add(Dbo.db());
		}
	}

	private String getWhereSql(long template_id, Auto_fetch_cond[] autoFetchConds,
	                           Auto_fetch_res[] autoFetchRes) {
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		StringBuilder resultSqlSb = new StringBuilder();
		StringBuilder resultSql = new StringBuilder("select ");
		List<String> template_sql = Dbo.queryOneColumnList(
				"select template_sql from auto_tp_info where template_id = ?",
				template_id);
		String dbType = JdbcConstants.POSTGRESQL;
		String format_sql = SQLUtils.format(template_sql.get(0), dbType);
		List<String> formatSqlList = StringUtil.split(format_sql, "\n");
		List<Long> condIdList = Dbo.queryOneColumnList(
				"select template_cond_id from " + Auto_tp_cond_info.TableName + " where template_id = ?",
				template_id);
		for (long condId : condIdList) {// 模板条件ID
			boolean inMoreConRow = false;
			boolean flag = false;
			Auto_tp_cond_info auto_tp_cond_info = new Auto_tp_cond_info();
			auto_tp_cond_info.setTemplate_cond_id(condId);
			List<String> conRowList = Dbo.queryOneColumnList(
					"select con_row from " + Auto_tp_cond_info.TableName
							+ " where template_cond_id = ?", condId);
			List<String> con_row_list = StringUtil.split(conRowList.get(0), ",");
			if (con_row_list.size() != 1) {
				inMoreConRow = true;
			}
			int con_row = Integer.parseInt(con_row_list.get(0));
			for (Auto_fetch_cond auto_fetch_cond : autoFetchConds) {
				// 如果配置了条件而且选了条件
				if (condId == auto_fetch_cond.getTemplate_cond_id()) {
					Auto_tp_cond_info autoTpCondInfo = Dbo.queryOneObject(Auto_tp_cond_info.class,
							"select * from " + Auto_tp_cond_info.TableName + " where template_cond_id = ?",
							auto_fetch_cond.getTemplate_cond_id())
							.orElseThrow(() -> new BusinessException("sql查询错误或者映射实体失败"));
					String cond_value = auto_fetch_cond.getCond_value();
					if (AutoValueType.MeiJu == (AutoValueType.ofEnumByCode(autoTpCondInfo.getValue_type()))) {
						// 本来存的是中文
						// 需要转换成值
						List<String> condValueList = StringUtil.split(cond_value, ",");
						StringBuilder tmpCondValue = new StringBuilder();
						for (String everyCondValue : condValueList) {
							List<String> codeList = Dbo.queryOneColumnList(
									"select ci_sp_code from " + Code_info.TableName
											+ " where ci_sp_classname = ? and ci_sp_name = ?",
									autoTpCondInfo.getValue_size(), everyCondValue);
							tmpCondValue.append(codeList.get(0)).append(",");
						}
						cond_value = tmpCondValue.deleteCharAt(tmpCondValue.length() - 1).toString();
					}
					if (autoTpCondInfo.getCon_relation().equals("IN")) {
						List<String> condValueList = StringUtil.split(cond_value, ",");
						StringBuilder sb = new StringBuilder();
						for (String s : condValueList) {
							sb.append(s).append(",");
						}
						// 去除,添加）
						cond_value = sb.deleteCharAt(sb.length() - 1).toString();
					}
					String everySql = formatSqlList.get(con_row);
					StringBuilder everyResultSql = new StringBuilder();
					StringBuilder condValueParam = new StringBuilder("?");
					// 如果是WHERE开头的 将拼上 WHERE,n个（ 条件+关系+值,n个)
					if (everySql.trim().startsWith("WHERE")) {
						everyResultSql = new StringBuilder("WHERE ");
						everySql = everySql.replaceFirst("WHERE", "").trim();
					}

					// 如果是AND开头的 将拼上 WHERE,n个（ 条件+关系+值,n个)
					else if (everySql.trim().startsWith("AND")) {
						everyResultSql = new StringBuilder("AND ");
						everySql = everySql.replaceFirst("AND", "").trim();
					}
					// 如果是OR开头的 将拼上 WHERE,n个（ 条件+关系+值,n个)
					else if (everySql.trim().startsWith("OR")) {
						everyResultSql = new StringBuilder("OR ");
						everySql = everySql.replaceFirst("OR", "").trim();
					}
					// 考虑（问题
					while (everySql.startsWith("(")) {
						everyResultSql.append("( ");
						everySql = everySql.replaceFirst("\\(", "").trim();
					}
					// 开始判断数值问题 如果是IN
					if (autoTpCondInfo.getCon_relation().equals("IN")) {
						condValueParam = new StringBuilder("(");
						List<String> condValueList = StringUtil.split(cond_value, ",");
						for (String s : condValueList) {
							condValueParam.append("?,");
							assembler.addParam(s);
						}
						condValueParam = new StringBuilder(condValueParam.substring(0, condValueParam.length() - 1));
						if (inMoreConRow) {
							condValueParam.append(")");
						}
					}
					// 如果是BETWEEN
					else if (autoTpCondInfo.getCon_relation().equals("BETWEEN")) {
						condValueParam = new StringBuilder("? AND ?");
						List<String> condValueList = StringUtil.split(cond_value, ",");
						assembler.addParam(condValueList.get(0));
						assembler.addParam(condValueList.get(1));
					} else {
						assembler.addParam(cond_value);
					}
					everyResultSql.append(autoTpCondInfo.getCond_para_name()).append(" ")
							.append(autoTpCondInfo.getCon_relation()).append(" ").append(condValueParam);
					// 考虑）问题
					while (everySql.endsWith(")")) {
						everyResultSql.append(")");
						everySql = everySql.trim();
						everySql = everySql.substring(0, everySql.length() - 1);
					}
					formatSqlList.set(con_row, everyResultSql.toString());
					if (inMoreConRow) {
						// IN在多行的情况下
						for (int l = 1; l < con_row_list.size(); l++) {
							// 替换第一行为in的条件
							// 然后去除别的行
							String everycon_row_String = con_row_list.get(l);
							int everycon_row = Integer.parseInt(everycon_row_String);
							formatSqlList.set(everycon_row, "");
						}
					}
					flag = true;
					break;
				}
			}
			if (!flag) {// 如果配置了条件 但是没有选条件
				String everySql = formatSqlList.get(con_row);
				StringBuilder everyresultsql;
				// 如果以WHERE开头 将条件关系值 替换为 1=1
				if (everySql.trim().startsWith("WHERE")) {
					everyresultsql = new StringBuilder("WHERE ");
					everySql = everySql.trim().replaceFirst("WHERE", "").trim();
					while (everySql.startsWith("(")) {
						everySql = everySql.replaceFirst("\\(", "").trim();
						everyresultsql.append("(");
					}
					everyresultsql.append(" 1=1 ");
					formatSqlList.set(con_row, everyresultsql.toString());
				}
				// 如果以AND开头,判断是否有(,没有则替换为1=1 不然有(则看下一行的判断如果是OR,则为1=0
				else if (everySql.trim().startsWith("AND")) {
					int leftcount = 0;
					int rightcount = 0;
					everyresultsql = new StringBuilder("AND ");
					everySql = everySql.trim().replaceFirst("AND", "").trim();
					while (everySql.startsWith("(")) {
						everySql = everySql.replaceFirst("\\(", "").trim();
						everyresultsql.append("(");
						leftcount++;
					}
					while (everySql.endsWith(")")) {
						everySql = everySql.substring(0, everySql.length() - 1);
						rightcount++;
					}
					if (everySql.contains(" IN ")) {
						rightcount--;
					}
					if (leftcount == 0) {
						everyresultsql.append(" 1=1 ");
					} else {
						if (con_row + 1 < formatSqlList.size()) {
							String nextsql = formatSqlList.get(con_row + 1);
							if (nextsql.startsWith("AND")) {
								everyresultsql.append(" 1=1 ");
							} else {
								everyresultsql.append(" 1=0 ");
							}
						}
					}
					for (int i = 0; i < rightcount; i++) {
						everyresultsql.append(")");
					}
					formatSqlList.set(con_row, everyresultsql.toString());
				}
				// 如果以OR开头,判断是否有(,没有则替换为1=1 不然有(则看下一行的判断如果是OR,则为1=0
				else if (everySql.trim().startsWith("OR")) {
					int leftcount = 0;
					int rightcount = 0;
					everyresultsql = new StringBuilder("OR ");
					everySql = everySql.trim().replaceFirst("OR", "").trim();
					while (everySql.startsWith("(")) {
						everySql = everySql.replaceFirst("\\(", "").trim();
						everyresultsql.append("(");
						leftcount++;
					}
					while (everySql.endsWith(")")) {
						everySql = everySql.substring(0, everySql.length() - 1);
						rightcount++;
					}
					if (everySql.contains(" IN ")) {
						rightcount--;
					}
					if (leftcount == 0) {
						everyresultsql.append(" 1=0 ");
					} else {
						if (con_row + 1 < formatSqlList.size()) {
							String nextSql = formatSqlList.get(con_row + 1);
							if (nextSql.startsWith("AND")) {
								everyresultsql.append(" 1=1 ");
							} else {
								everyresultsql.append(" 1=0 ");
							}
						}
					}
					for (int i = 0; i < rightcount; i++) {
						everyresultsql.append(")");
					}
					formatSqlList.set(con_row, everyresultsql.toString());
				}
				// 如果这一行不是以WHERE或者AND或者OR开头
				else {
					throw new BusinessException("SQL拼接出错");
				}
				if (inMoreConRow) {
					for (int l = 1; l < con_row_list.size(); l++) {// IN在多行的情况下
						// 替换第一行为in的条件
						// 然后去除别的行
						int everyCon_row = Integer.parseInt(con_row_list.get(l));
						if (l == con_row_list.size() - 1) {
							String tempresultsql = formatSqlList.get(everyCon_row);
							everyresultsql = new StringBuilder(tempresultsql.substring(tempresultsql.indexOf(")")));
							formatSqlList.set(everyCon_row, everyresultsql.toString());
						} else {
							formatSqlList.set(everyCon_row, "");
						}
					}
				}
			}
		}
		// 拼接起来 重新格式化一遍
		for (String s : formatSqlList) {
			resultSqlSb.append(" ").append(s.trim());
		}
		String formatResultSql = SQLUtils.format(resultSqlSb.toString(), dbType);
		resultSqlSb = new StringBuilder();
		List<String> formatResultSqlList = Arrays.asList(formatResultSql.split("\n"));
		for (String s : formatResultSqlList) {
			resultSqlSb.append(" ").append(s.trim());
		}
		// 以下为处理1=0和1=1的问题
		// 替换1 = 1 和 1 = 0 为空
		resultSqlSb = new StringBuilder(resultSqlSb.toString().replace("1 = 0", "")
				.replace("1 = 1", ""));
		// 如果有括号（那么就考虑去除1=0 和1=1之后可能的情况
		if (resultSqlSb.toString().contains("(")) {
			// 去除1=1之后，可能存在 （ 空 and/or 或者 and/or 空）的情况
			while (resultSqlSb.toString().contains("( AND ") || resultSqlSb.toString().contains(" AND )")
					|| resultSqlSb.toString().contains("( OR ")
					|| resultSqlSb.toString().contains(" OR )")) {
				resultSqlSb = new StringBuilder(resultSqlSb.toString()
						.replace("( AND ", "(")
						.replace(" AND )", ")")
						.replace("( OR ", "(")
						.replace(" OR )", ")"));
			}
			// 去除完以后就不存在（ and/or 或者 and/or）的情况了，但是可能有（）的情况
			while (resultSqlSb.toString().contains("()")) {
				resultSqlSb = new StringBuilder(resultSqlSb.toString().replace("()", ""));
			}
		}
		// 考虑完（）的情况后 因为去除了（） 所以 还有可能存在 and 空 or等情况 所以 and与or之间有两个空格
		while (resultSqlSb.toString().contains("AND  AND") || resultSqlSb.toString().contains("OR  AND")
				|| resultSqlSb.toString().contains("OR  OR")
				|| resultSqlSb.toString().contains("AND  OR")) {
			resultSqlSb = new StringBuilder(resultSqlSb.toString()
					.replace("AND  AND", "AND")
					.replace("OR  AND", "AND")
					.replace("OR  OR", "OR")
					.replace("AND  OR", "OR"));
		}
		// 最后考虑头和为的情况 存在where and的情况时是由于where 空 and造成的 所以where与and之间一样还是两个空格
		while (resultSqlSb.toString().contains("WHERE  AND") || resultSqlSb.toString().contains("WHERE  OR")) {
			resultSqlSb = new StringBuilder(resultSqlSb.toString()
					.replace("WHERE  AND", "WHERE")
					.replace("WHERE  OR", "WHERE"));
		}
		// 去除尾部的空格
		resultSqlSb = new StringBuilder(resultSqlSb.toString().trim());
		if (resultSqlSb.toString().endsWith("AND")) {
			resultSqlSb = new StringBuilder(resultSqlSb.substring(0, resultSqlSb.length() - 3));
		}
		if (resultSqlSb.toString().endsWith("OR")) {
			resultSqlSb = new StringBuilder(resultSqlSb.substring(0, resultSqlSb.length() - 2));
		}
		resultSqlSb = new StringBuilder(resultSqlSb.toString().trim());
		// 考虑尾部 存在and 空的情况，所以先去除空格然后删去尾部的and或者or
		if (resultSqlSb.toString().contains("AND  ") || resultSqlSb.toString().contains("OR  ")) {
			resultSqlSb = new StringBuilder(resultSqlSb.toString()
					.replace("AND  ", "")
					.replace("OR  ", ""));
			resultSqlSb = new StringBuilder(resultSqlSb.toString().trim());
		}
		// 重新格式化，由于去除((age = 18 ))中多余空格的问题
		formatResultSql = SQLUtils.format(resultSqlSb.toString(), dbType);
		resultSqlSb = new StringBuilder();
		formatResultSqlList = Arrays.asList(formatResultSql.split("\n"));
		for (String s : formatResultSqlList) {
			resultSqlSb.append(" ").append(s.trim());
		}
		// 格式化完成
		for (Auto_fetch_res auto_fetch_res : autoFetchRes) {
			Auto_tp_res_set auto_tp_res_set = Dbo.queryOneObject(Auto_tp_res_set.class,
					"select * from " + Auto_tp_res_set.TableName
							+ " where template_res_id = ?",
					auto_fetch_res.getFetch_res_id())
					.orElseThrow(() -> new BusinessException("sql查询错误或者映射实体失败"));
			// 模板结果中文字段
			String column_cn_name = auto_tp_res_set.getColumn_cn_name();
			String res_show_column = auto_tp_res_set.getRes_show_column();
			if (StringUtil.isNotBlank(res_show_column)) {
				resultSql.append(" `").append(column_cn_name).append("` as `").append(res_show_column).append("` ,");
			} else {
				resultSql.append(" `").append(column_cn_name).append("` ,");
			}
		}
		resultSql = new StringBuilder(resultSql.substring(0, resultSql.length() - 1));
		resultSql.append(" from (").append(resultSqlSb).append(") ").append(TempTableName);
		assembler.addSql(resultSql.toString());
		return assembler.sql();
	}

	@Method(desc = "查看取数sql", logicStep = "1.查询取数sql" +
			"2.格式化取数sql并返回")
	@Param(name = "fetch_sum_id", desc = "取数汇总ID", range = "配置取数模板时生成")
	@Return(desc = "返回取数sql", range = "无限制")
	public String getAccessSql(long fetch_sum_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.查询取数sql
		Auto_fetch_sum auto_fetch_sum = Dbo.queryOneObject(Auto_fetch_sum.class,
				"select fetch_sql from " + Auto_fetch_sum.TableName + " where fetch_sum_id = ?",
				fetch_sum_id)
				.orElseThrow(() -> new BusinessException("sql查询错误或者映射实体失败"));
		// 2.格式化取数sql并返回
		return auto_fetch_sum.getFetch_sql();
	}

	@Method(desc = "判断模板信息是否已不存在", logicStep = "1.判断模板信息是否已不存在")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	private void isAutoTpInfoExist(long template_id) {
		// 1.判断模板信息是否已不存在
		if (Dbo.queryNumber(
				"select count(1) from " + Auto_tp_info.TableName + " where template_id=?",
				template_id)
				.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("当前模板ID:" + template_id + "对应模板信息已不存在");
		}
	}

	@Method(desc = "查询我的取数信息", logicStep = "1.查询我的取数信息")
	@Return(desc = "返回我的取数信息", range = "无限制")
	public List<Map<String, Object>> getMyAccessInfo() {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.查询我的取数信息
		return Dbo.queryList(
				"select * from " + Auto_fetch_sum.TableName + " where user_id = ? and fetch_name is not null "
						+ " order by create_date desc,create_time desc",
				getUserId());
	}

	@Method(desc = "模糊查询我的取数信息", logicStep = "1.模糊查询我的取数信息")
	@Param(name = "fetch_name", desc = "取数名称", range = "新增我的取数信息时生成")
	@Return(desc = "返回模糊查询我的取数信息", range = "无限制")
	public List<Map<String, Object>> getMyAccessInfoByName(String fetch_name) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.模糊查询我的取数信息
		return Dbo.queryList(
				"select * from " + Auto_fetch_sum.TableName + " where user_id = ? and fetch_name like ?"
						+ " order by create_date desc,create_time desc",
				getUserId(), "%" + fetch_name + "%");
	}

	@Method(desc = "查看我的取数信息", logicStep = "1.查看我的取数信息")
	@Param(name = "fetch_sum_id", desc = "取数汇总表ID", range = "新增我的取数信息时生成")
	@Return(desc = "返回查看我的取数信息", range = "无限制")
	public List<Map<String, Object>> getMyAccessInfoById(long fetch_sum_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.查看我的取数信息
		return Dbo.queryList("select fetch_name, fetch_desc from " + Auto_fetch_sum.TableName
						+ " where fetch_sum_id = ?",
				fetch_sum_id);
	}

	@Method(desc = "取数时清单查询 显示条数方法", logicStep = "1.获取取数sql" +
			"2.根据sql查询数据结果")
	@Param(name = "fetch_sum_id", desc = "取数汇总表ID", range = "新增我的取数信息时生成")
	@Param(name = "showNum", desc = "显示条数", range = "正整数", valueIfNull = "10")
	@Return(desc = "返回取数结果", range = "无限制")
	public List<Map<String, Object>> getAccessResultByNumber(long fetch_sum_id, int showNum) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 1.获取取数sql
		String accessSql = getAccessSql(fetch_sum_id);
		List<Map<String, Object>> resultData = new ArrayList<>();
		// 2.根据sql查询数据结果
		new ProcessingData() {
			@Override
			public void dealLine(Map<String, Object> map) {
				resultData.add(map);
			}
		}.getPageDataLayer(accessSql, Dbo.db(), 0, Math.min(showNum, 1000));
		// 2.返回数据结果
		return resultData;
	}

	@Method(desc = "获取数据可视化组件信息", logicStep = "1.获取数据可视化组件信息")
	@Return(desc = "返回获取数据可视化组件信息", range = "无限制")
	public List<Map<String, Object>> getVisualComponentInfo() {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 1.获取数据可视化组件信息
		return Dbo.queryList("SELECT * FROM " + Auto_comp_sum.TableName
				+ " WHERE create_user = ? order by create_date desc,create_time desc", getUserId());
	}

	@Method(desc = "获取自主数据数据集表信息", logicStep = "1.判断是否为自主数据数据集" +
			"2.获取自主数据数据集表信息")
	@Param(name = "data_source", desc = "可视化源对象值", range = "使用(AutoSourceObject代码项)")
	@Return(desc = "返回自主数据数据集表信息", range = "无限制")
	public List<Map<String, Object>> getTAutoDataTableName(String data_source) {
		// 1.判断是否为自主数据数据集
		if (AutoSourceObject.ZiZhuShuJuShuJuJi != AutoSourceObject.ofEnumByCode(data_source)) {
			throw new BusinessException("不是自主数据数据集,请检查" + data_source);
		}
		// 2.获取自主数据数据集表信息
		List<Map<String, Object>> auto_fetch_sums = Dbo.queryList(
				"SELECT * FROM " + Auto_fetch_sum.TableName
						+ " WHERE fetch_status = ?", AutoFetchStatus.WanCheng.getCode());
		for (Map<String, Object> fetchSum : auto_fetch_sums) {
			fetchSum.put("isParent", false);
			fetchSum.put("tablename", fetchSum.get("fetch_name"));
			fetchSum.put("remark", fetchSum.get("fetch_desc"));
		}
		return auto_fetch_sums;
	}

	@Method(desc = "根据表名获取字段信息", logicStep = "1.获取自主数据集字段信息" +
			"2.根据表名获取系统数据集字段信息" +
			"3.返回字段信息")
	@Param(name = "table_name", desc = "表名", range = "无限制")
	@Param(name = "data_source", desc = "可视化源对象值", range = "使用(AutoSourceObject代码项)")
	@Return(desc = "返回字段信息", range = "无限制")
	public Map<String, Object> getColumnByName(String table_name, String data_source) {
		// 数据可访问权限处理方式：该方法不需要进行访问权限限制
		Map<String, Object> columnMap = new HashMap<>();
		// 数字类型列
		List<Map<String, Object>> numColumnList = new ArrayList<>();
		// 度量列
		List<Map<String, Object>> measureColumnList = new ArrayList<>();
		if (AutoSourceObject.ZiZhuShuJuShuJuJi == AutoSourceObject.ofEnumByCode(data_source)) {
			// 1.获取自主数据集字段信息
			List<Map<String, Object>> columnList = Dbo.queryList(
					"SELECT t1.fetch_res_name,t3.column_type as column_type"
							+ " FROM " + Auto_fetch_res.TableName
							+ " t1 left join " + Auto_fetch_sum.TableName
							+ " t2 on t1.fetch_sum_id = t2.fetch_sum_id"
							+ " left join " + Auto_tp_res_set.TableName
							+ " t3 on t1.template_res_id = t3.template_res_id WHERE t2.fetch_name = ?"
							+ " AND t2.fetch_status = ? order by t1.show_num",
					table_name, AutoFetchStatus.WanCheng.getCode());
			for (Map<String, Object> map : columnList) {
				if (numbersArray.contains(map.get("column_type").toString())) {
					numColumnList.add(map);
				}
				// fixme 02是啥？
				if ("02".equals(map.get("column_type").toString())) {
					measureColumnList.add(map);
				}
			}
			columnMap.put("columns", columnList);
			columnMap.put("numColumns", numColumnList);
			columnMap.put("measureColumns", measureColumnList);
		} else if (AutoSourceObject.XiTongJiShuJuJi == AutoSourceObject.ofEnumByCode(data_source)) {
			// 2.根据表名获取系统数据集字段信息
			List<Map<String, Object>> columnList = DataTableUtil.getColumnByTableName(Dbo.db(), table_name);
			for (Map<String, Object> map : columnList) {
				if (numbersArray.contains(map.get("column_type").toString())) {
					numColumnList.add(map);
				}
			}
			columnMap.put("columns", columnList);
			columnMap.put("numColumns", numColumnList);
		}
		// 3.返回字段信息
		return columnMap;
	}

	@Method(desc = "可视化创建组件得到答案", logicStep = "")
	@Param(name = "component_info", desc = "可视化组件参数实体bean", range = "自定义无限制")
	@Param(name = "autoCompConds", desc = "组件条件表对象数组", range = "与数据库表规则一致", isBean = true)
	@Param(name = "autoCompGroups", desc = "组件分组表对象数组", range = "与数据库表规则一致", isBean = true)
	@Param(name = "autoCompDataSums", desc = "组件数据汇总信息表对象数组", range = "与数据库表规则一致", isBean = true)
	@Return(desc = "返回可视化创建组件得到答案", range = "")
	public String getAnswer(ComponentBean componentBean, Auto_comp_cond[] autoCompConds,
	                        Auto_comp_group[] autoCompGroups, Auto_comp_data_sum[] autoCompDataSums) {
		Validator.notNull(componentBean.getFetch_name(), "取数名称不能为空");
		Validator.notNull(componentBean.getData_source(), "数据来源不能为空");
		Validator.notNull(componentBean.getFetch_sum_id(), "取数汇总ID不能为空");
		String fetch_sql;
		if (AutoSourceObject.ZiZhuShuJuShuJuJi == AutoSourceObject.ofEnumByCode(componentBean.getData_source())) {
			// 自主取数数据集
			// 获取自主取数部分的sql
			fetch_sql = getAccessSql(componentBean.getFetch_sum_id());
		} else if (AutoSourceObject.XiTongJiShuJuJi == AutoSourceObject.ofEnumByCode(componentBean.getData_source())) {
			// 系统级数据集
			// 拼接系统数据集sql
			fetch_sql = "SELECT" + Constant.SPACE + "*" + Constant.SPACE +
					"FROM" + Constant.SPACE + componentBean.getFetch_name();
		} else {//数据组件数据集
			throw new BusinessException("暂不支持该种数据集" + componentBean.getData_source());
		}
		// 添加select 部分
		StringBuilder result_sql = new StringBuilder();
		result_sql.append("SELECT").append(Constant.SPACE);
		for (Auto_comp_data_sum auto_comp_data_sum : autoCompDataSums) {
			String column_name = auto_comp_data_sum.getColumn_name();
			String summary_type = auto_comp_data_sum.getSummary_type();
			if (AutoDataSumType.QiuHe == AutoDataSumType.ofEnumByCode(summary_type)) {
				result_sql.append("sum(").append(column_name).append(") ,");
			} else if (AutoDataSumType.QiuPingJun == AutoDataSumType.ofEnumByCode(summary_type)) {
				result_sql.append("avg(").append(column_name).append(") ,");
			} else if (AutoDataSumType.QiuZuiDaZhi == AutoDataSumType.ofEnumByCode(summary_type)) {
				result_sql.append("max(").append(column_name).append(") ,");
			} else if (AutoDataSumType.QiuZuiXiaoZhi == AutoDataSumType.ofEnumByCode(summary_type)) {
				result_sql.append("min(").append(column_name).append(") ,");
			} else if (AutoDataSumType.ZongHangShu == AutoDataSumType.ofEnumByCode(summary_type)) {
				result_sql.append(column_name).append(" ,");
			} else if (AutoDataSumType.YuanShiShuJu == AutoDataSumType.ofEnumByCode(summary_type)) {
				result_sql.append(column_name).append(",");
			} else {
				result_sql.append("*,");
			}
		}
		// 去除,
		result_sql.deleteCharAt(result_sql.length() - 1);
		// 添加子查询
		result_sql.append(Constant.SPACE).append("FROM (").append(fetch_sql).append(") ")
				.append(Constant.SPACE).append(TempTableName);
		// 对最大的N个做单独处理
		ArrayList<String> upArray = new ArrayList<>();
		// 对最小的N个做单独处理
		ArrayList<String> lowArray = new ArrayList<>();
		boolean flag = true;
		if (autoCompConds != null && autoCompConds.length != 0) {
			// 添加 where部分
			for (Auto_comp_cond auto_comp_cond : autoCompConds) {
				String cond_en_column = auto_comp_cond.getCond_en_column();
				String operator = auto_comp_cond.getOperator();
				String cond_value = auto_comp_cond.getCond_value();
				if (AutoDataOperator.ZuiDaDeNGe == AutoDataOperator.ofEnumByCode(operator)) {
					upArray.add(cond_en_column + Constant.SQLDELIMITER + cond_value);
				} else if (AutoDataOperator.ZuiXiaoDeNGe == AutoDataOperator.ofEnumByCode(operator)) {
					lowArray.add(cond_en_column + Constant.SQLDELIMITER + cond_value);
				} else {
					if (flag) {
						result_sql.append(Constant.SPACE).append("WHERE").append(Constant.SPACE);
						flag = false;
					}
				}
			}
			if (StringUtil.isNotBlank(componentBean.getCondition_sql())) {
				result_sql.append(componentBean.getCondition_sql()).append(Constant.SPACE);
			}
		}
		if (autoCompGroups != null && autoCompGroups.length != 0) {
			// 添加 group by
			result_sql.append(Constant.SPACE).append("GROUP BY").append(Constant.SPACE);
			for (Auto_comp_group auto_comp_group : autoCompGroups) {
				String column_name = auto_comp_group.getColumn_name();
				result_sql.append("`").append(column_name).append("`,");
			}
			// 去除,添加limit条件
			result_sql.deleteCharAt(result_sql.length() - 1);
		}
		if (!upArray.isEmpty()) {
			result_sql.append(Constant.SPACE).append("ORDER BY").append(Constant.SPACE)
					.append(upArray.get(0).split(Constant.SQLDELIMITER)[0]).append(Constant.SPACE).append("DESC LIMIT")
					.append(Constant.SPACE).append(upArray.get(0).split(Constant.SQLDELIMITER)[1]);
		}
		if (!lowArray.isEmpty()) {
			result_sql.append(Constant.SPACE).append("ORDER BY").append(Constant.SPACE)
					.append(lowArray.get(0).split(Constant.SQLDELIMITER)[0]).append(Constant.SPACE).append("LIMIT")
					.append(Constant.SPACE).append(lowArray.get(0).split(Constant.SQLDELIMITER)[1]);
		}
		return result_sql.toString();
	}

	@Method(desc = "保存条件逻辑", logicStep = "")
	@Param(name = "autoCompConds", desc = "组件条件表对象数组", range = "与数据库表规则一致", isBean = true)
	@Return(desc = "", range = "")
	public String saveConditionLogic(Auto_comp_cond[] autoCompConds) {
		StringBuilder result_where = new StringBuilder();
		if (autoCompConds != null && autoCompConds.length != 0) {
			// 添加 where部分
			for (Auto_comp_cond auto_comp_cond : autoCompConds) {
				String cond_en_column = auto_comp_cond.getCond_en_column();
				String operator = auto_comp_cond.getOperator();
				String cond_value = auto_comp_cond.getCond_value();
				String arithmetic_logic = auto_comp_cond.getArithmetic_logic();
				// 判断是否为最大的N个或者最小的N个
				if (AutoDataOperator.ZuiDaDeNGe != AutoDataOperator.ofEnumByCode(operator) &&
						AutoDataOperator.ZuiXiaoDeNGe == AutoDataOperator.ofEnumByCode(operator)) {
					if (AutoDataOperator.JieYu == AutoDataOperator.ofEnumByCode(operator)
							|| AutoDataOperator.BuJieYu == AutoDataOperator.ofEnumByCode(operator)) {
						result_where.append("`").append(cond_en_column).append("`").append(Constant.SPACE)
								.append(transOperator(operator)).append(Constant.SPACE).append("(")
								.append(cond_value).append(")").append(Constant.SPACE)
								.append((arithmetic_logic).toUpperCase()).append(Constant.SPACE);
					} else {
						result_where.append("`").append(cond_en_column).append("`").append(Constant.SPACE)
								.append(transOperator(operator)).append(Constant.SPACE).append(cond_value)
								.append(Constant.SPACE).append((arithmetic_logic).toUpperCase())
								.append(Constant.SPACE);
					}
				}
			}
			// 去除and
			result_where.delete(result_where.length() - 4, result_where.length());
		}
		return result_where.toString();
	}

	@Method(desc = "转换sql操作符", logicStep = "1.根据可视化数据操作符代码项转换为sql操作符" +
			"2.返回转换后的sql操作符")
	@Param(name = "operator", desc = "操作符", range = "无限制")
	@Return(desc = "返回转换后的操作符", range = "无限制")
	private String transOperator(String operator) {
		// 1.根据可视化数据操作符代码项转换为sql操作符
		if (AutoDataOperator.BuDengYu == AutoDataOperator.ofEnumByCode(operator)) {
			operator = "!=";
		} else if (AutoDataOperator.BuJieYu == AutoDataOperator.ofEnumByCode(operator)) {
			operator = "NOT IN";
		} else if (AutoDataOperator.DaYu == AutoDataOperator.ofEnumByCode(operator)) {
			operator = ">";
		} else if (AutoDataOperator.DaYuDengYu == AutoDataOperator.ofEnumByCode(operator)) {
			operator = ">=";
		} else if (AutoDataOperator.DengYu == AutoDataOperator.ofEnumByCode(operator)) {
			operator = "=";
		} else if (AutoDataOperator.JieYu == AutoDataOperator.ofEnumByCode(operator)) {
			operator = "IN";
		} else if (AutoDataOperator.WeiKong == AutoDataOperator.ofEnumByCode(operator)) {
			operator = "IS NULL";
		} else if (AutoDataOperator.XiaoYu == AutoDataOperator.ofEnumByCode(operator)) {
			operator = "<";
		} else if (AutoDataOperator.XiaoYuDengYu == AutoDataOperator.ofEnumByCode(operator)) {
			operator = "<";
		} else if (AutoDataOperator.FeiKong == AutoDataOperator.ofEnumByCode(operator)) {
			operator = "IS NOT NULL";
		} else {
			throw new BusinessException("暂不支持此种操作符" + operator);
		}
		// 2.返回转换后的sql操作符
		return operator;
	}

	public static void main(String[] args) {
		String template_sql = "SELECT t1.c_customer_sk, t1.c_customer_id, t1.c_current_cdemo_sk, t1.c_current_hdemo_sk, t1.c_current_addr_sk\n" +
				"\t, t1.c_first_shipto_date_sk, t1.c_first_sales_date_sk, t1.c_salutation, t1.c_first_name, t1.c_last_name\n" +
				"\t, t1.c_preferred_cust_flag,t2.*\n" +
				"FROM CS01_DB01_CUSTOMER t1\n" +
				"\tLEFT JOIN CS01_DB01_ITEM t2 ON t1.c_customer_sk = t2.i_item_sk\n" +
				"WHERE t1.c_customer_sk != 1";
		DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(template_sql);
		Map<String, String> selectColumnMap = druidParseQuerySql.getSelectColumnMap();
		selectColumnMap.forEach((k, v) -> System.out.println(k + "-->" + v));
	}
}
