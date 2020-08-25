package autoanalysismanage;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AutoTemplateStatus;
import hrds.commons.collection.ProcessingData;
import hrds.commons.entity.Auto_tp_cond_info;
import hrds.commons.entity.Auto_tp_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@DocClass(desc = "自主分析管理类", author = "dhw", createdate = "2020/8/21 14:17")
public class AutoAnalysisManageAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	@Method(desc = "获取自主分析模板配置信息", logicStep = "1.查询并返回自主分析模板配置信息")
	@Return(desc = "返回自主分析模板配置信息", range = "无限制")
	public List<Map<String, Object>> getAutoAnalysisTemplateConfInfo() {
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
	public List<Map<String, Object>> getAutoAnalysisTemplateConfInfoByName(String template_name) {
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

//	@Method(desc = "生成自主分析模板配置参数", logicStep = "")
//	@Param(name = "template_sql", desc = "自主取数模板sql", range = "无限制")
//	public void generateAutoAnalysisTemplateParam(String template_sql) {
//		SqlAnalyze sqlAnalyse = new SqlAnalyze();
//		JSONObject result = new JSONObject();
//		JSONArray jsonarray = new JSONArray();
//		sqlAnalyse.setcTableColBeans(new ArrayList<CTableColBean>());// 全局变量重置
//		try {
//			List<Auto_tp_cond_info> condlist = getWhereCondFromSql(sql);
//			List<CTableColBean> list = sqlAnalyse.getcTableColBeans();
//			for (Auto_tp_cond_info auto_tp_cond_info : condlist) {
//				//判断是否为关联条件，如果是则不返回给页面
//				String cond_para_name = auto_tp_cond_info.getCond_para_name();
//				String pre_value = auto_tp_cond_info.getPre_value();
//				if (cond_para_name.contains(".") && pre_value.contains(".")) {
//					String[] strings = cond_para_name.split("\\.");
//					String[] strings2 = pre_value.split("\\.");
//					if (strings[1].equals(strings2[1])) {
//						continue;
//					}
//				}
//
//				JSONObject jsonobject = new JSONObject();
//				// 判断参数是否有别名
//				boolean flag = false;
//				for (CTableColBean everyctablecolbean : list) {
//					if (auto_tp_cond_info.getCond_para_name().equals(everyctablecolbean.getField_en_name())) {
//						String cname = sqlAnalyse.cleanColumnTableName(everyctablecolbean.getField_cn_name());
//						jsonobject.put("cond_cn_column", cname);
//						flag = true;
//						break;
//					}
//				}
//				if (!flag) {
//					jsonobject.put("cond_cn_column",
//							sqlAnalyse.cleanColumnTableName(auto_tp_cond_info.getCond_para_name()));
//				}
//
//				jsonobject.put("cond_para_name", auto_tp_cond_info.getCond_para_name());// 条件参数名称
//				jsonobject.put("cond_en_column",
//						sqlAnalyse.cleanColumnTableName(auto_tp_cond_info.getCond_para_name()));// 英文名称
//				jsonobject.put("con_relation", auto_tp_cond_info.getCon_relation());// 关系
//				jsonobject.put("con_row", auto_tp_cond_info.getCon_row());// 行号
//				jsonobject.put("pre_value", auto_tp_cond_info.getPre_value());// 默认值
//				jsonobject.put("value_type", AutoValueType.ZiFuChuan.toString());// 默认值类型为字符串
//				jsonobject.put("checked", true);// 默认全选
//				jsonobject.put("is_required", "0");// 默认必填
//				// 以下注释代码 请勿轻易删除 TODO
//				// String executesql = "select "
//				// jsonobject.getString("cond_cn_column") + " from (" + sql +
//				// ")";
//				// db = SqlEngine.getSQLEngine(executesql);
//				// ResultSet rs = db.execSQL(executesql);
//				// ResultSetMetaData rsmd = rs.getMetaData();
//				// int size = rsmd.getColumnDisplaySize(1);
//				jsonobject.put("value_size", "64");// TODO 目前先定位 64 这个数值
//				// 目前没有用于什么地方
//				jsonarray.add(jsonobject);
//			}
//			result.put("resultlist", jsonarray);
//		} catch (Exception e) {
//			if (e instanceof JSQLParserException) {
//				String error = "生成参数sql解析错误";
//				result.put("ERROR", error);
//				return result;
//			} else if (e instanceof BusinessException)
//				throw (BusinessException) e;
//			else
//				throw new BusinessException(e);
//		}
//		return result;
//	}

	@Method(desc = "保存自主分析模板配置信息", logicStep = "")
	@Param(name = "", desc = "", range = "")
	@Return(desc = "", range = "")
	public void saveAutoAnalysisTemplateConfInfo() {

	}

//	private List<Auto_tp_cond_info> getWhereCondFromSql(String sql) throws Exception {
//		// 模板条件集合
//		List<Auto_tp_cond_info> tpCondList = new ArrayList<>();
//		String dbType = JdbcConstants.POSTGRESQL;
//		// sql格式化
//		String format_sql = SQLUtils.format(sql, dbType).trim();
//		if (format_sql.endsWith(";")) {
//			format_sql = format_sql.substring(0, format_sql.length() - 1);
//		}
//		// 获取格式化后的每一行sql
//		List<String> formatSqlList = StringUtil.split(format_sql, "\n");
//		// 补充 \t
//		// 由于当where 条件存在于子查询中 且where条件为 where(select )时 本该在select部分的行
//		// 多where行2个\t 但其只有一个 故将其补上 由于之后解析
//		for (int i = 0; i < formatSqlList.size(); i++) {
//			String everySql1 = formatSqlList.get(i);
//			if (everySql1.endsWith("(") && everySql1.contains("WHERE ")) {
//				int j = i + 1;
//				int countEverySql1 = countT(everySql1);
//				String everySql2 = formatSqlList.get(j);
//				if (!everySql2.contains("SELECT ")) {
//					continue;
//				} else {
//					int countEverySql2 = countT(everySql2);
//					if (countEverySql2 == countEverySql1 + 1) {
//						continue;
//					} else {
//						while (!(countEverySql2 == countEverySql1 - 1 && everySql2.trim().startsWith(")"))) {
//							formatSqlList.set(j, "\t" + everySql2);
//							j++;
//							everySql2 = formatSqlList.get(j);
//							countEverySql2 = countT(everySql2);
//						}
//					}
//				}
//			}
//		}
//		for (int i = 0; i < formatSqlList.size(); i++) {
//			String everySql = formatSqlList.get(i).trim();
//			// 遍历的每一行sql
//			if (everySql.startsWith("WHERE ")
//					|| everySql.contains("ON ") && !everySql.contains("UNION ") && !everySql.contains("PARTITION ")) {
//				String WhereOrOn = "WHERE ";
//				boolean onFlag = false;
//				int j = i;
//				String everySql2 = "";
//				if (everySql.contains("ON ")) {
//					onFlag = true;
//					WhereOrOn = "ON ";
//					everySql2 = Arrays.asList((everySql.substring(everySql.indexOf("ON ")).split(","))).get(0);
//				} else {
//					// 如果找到WHERE，那么就遍历从WHERE开始的每一行数据直到WHERE结束
//					everySql2 = formatSqlList.get(i).trim();
//				}
//				while (everySql2.startsWith("AND ") || everySql2.startsWith("OR ") || everySql2.startsWith(WhereOrOn)) {
//					// 因为是格式化过的所以都变成了大写
//					String inJ = "";
//					boolean flagIn = false;
//					// 读取到最后一行之前
//					if (j + 1 < formatSqlList.size()) {
//						int count1 = countT(formatSqlList.get(j));
//						int count2 = countT(formatSqlList.get(j + 1));
//						String orgSql1 = formatSqlList.get(j);
//						String orgSql2 = formatSqlList.get(j + 1);
//						String trimSql1 = orgSql1.trim();
//						String trimSql2 = orgSql2.trim();
//						boolean flag2 = true;
//						// 如果这一行是简单条件且下一行也是简单条件 则不用进入跳行程序
//						if ((trimSql1.startsWith("AND ") || trimSql1.startsWith("OR ") || trimSql1.startsWith("WHERE ")
//								|| trimSql1.startsWith("ON"))
//								&& (trimSql2.startsWith("AND ") || trimSql2.startsWith("OR ")
//								|| trimSql2.startsWith("WHERE "))) {
//							flag2 = false;
//						}
//						// 进入跳行程序
//						while (flag2) {
//							if (count1 <= count2 && orgSql1.contains(" IN ")) {// 如果是in的情况
//								// 就不用继续循环
//								// 直接跳出
//								int l = j;
//								everySql2 = orgSql1;
//								while (!orgSql2.trim().startsWith(")")) {
//									l++;
//									orgSql2 = formatSqlList.get(l);
//									everySql2 += orgSql2.trim();
//								}
//								if (!(everySql2.contains("SELECT") && everySql2.contains("FROM"))) {
//									for (int k = j; k <= l; k++) {
//										inJ += k + ",";
//									}
//									inJ = inJ.substring(0, inJ.length() - 1);
//									j = l;
//									everySql2 = everySql2.trim();
//									flagIn = true;
//									break;
//								}
//							}
//							if (count1 == count2 && orgSql1.contains(" CASE ")) {
//								j++;
//								count2 = countT(formatSqlList.get(j + 1));
//								while (count1 == count2) {
//									j++;
//									count2 = countT(formatSqlList.get(j + 1));
//								}
//							}
//							boolean flag3 = true;// 判断是否缩进去的是and或者or的标志
//							boolean flag4 = true;// 判断缩进去是否存在子查询条件中的where 标志
//							if (count1 < count2) {// 如果下一行缩进去了那么要开始进行判断了
//								while (count1 < count2 && j + 1 < formatSqlList.size() - 1) {// 重复判断直到找到对应的行
//									j++;
//									count2 = countT(formatSqlList.get(j + 1));
//									orgSql1 = formatSqlList.get(j);
//									orgSql2 = formatSqlList.get(j + 1);
//									trimSql1 = orgSql1.trim();
//									trimSql2 = orgSql2.trim();
//									if (trimSql1.startsWith("WHERE ") && flag4) {
//										flag4 = false;
//									}
//									// 如果这一行是and或者or开头的 且前面没有where存在
//									if ((trimSql1.startsWith("AND ") || trimSql1.startsWith("OR ")) && flag4) {
//										flag3 = false;
//										break;
//									}
//								}
//								if (!flag3) {
//									break;
//								}
//								j = j + 2;
//							}
//							if (j + 1 > formatSqlList.size() - 1) {// 判断是否到最后一行了就是说下一行的大小是否大于数组的长度
//								break;
//							}
//							count1 = countT(formatSqlList.get(j));
//							count2 = countT(formatSqlList.get(j + 1));
//							if (count1 >= count2) {
//								break;
//							}
//						}
//					}
//					if (onFlag) {
//						everySql2 = Arrays.asList(everySql2.split(",")).get(0);
//					} else if (!flagIn) {
//						everySql2 = formatSqlList.get(j).trim();
//					}
//					if (!everySql2.startsWith("AND ") && !everySql2.startsWith("OR ") && !everySql2.startsWith("WHERE ")
//							&& !everySql2.startsWith("ON ")) {
//						break;
//					}
//					Auto_tp_cond_info auto_tp_cond_info = new Auto_tp_cond_info();
//					sqlAnalyse.setCriteriaAnalyse(new ArrayList<SqlCriteriaAnalyse>());// 全局变量重置
//					if (everySql2.startsWith("WHERE ")) {
//						everySql2 = everySql2.replaceFirst("WHERE", "").trim();
//					} else if (everySql2.startsWith("AND ")) {
//						everySql2 = everySql2.replaceFirst("AND", "").trim();
//					} else if (everySql2.startsWith("ON ")) {
//						everySql2 = everySql2.replaceFirst("ON", "").trim();
//					} else if (everySql2.startsWith("OR ")) {
//						everySql2 = everySql2.replaceFirst("OR", "").trim();
//					} else {
//						break;
//					}
//					while (everySql2.startsWith("(")) {
//						everySql2 = everySql2.replaceFirst("\\(", "").trim();
//					}
//					while (everySql2.endsWith(")")) {
//						everySql2 = everySql2.substring(0, everySql2.length() - 1);
//					}
//					everySql2 = everySql2.trim();
//					// 单独考虑IN的情况
//					if (everySql2.contains(" IN ")) {
//						everySql2 += ")";
//					}
//					everySql2 = addrightbrackets(everySql2);
//					StringBuffer everySqlResult = new StringBuffer("select * from table where ").append(everySql2);
//					DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(everySqlResult.toString());
//					SQLExpr whereCond = druidParseQuerySql.whereCond;
//					List<SqlCriteriaAnalyse> list_sqlCriteriaAnalyse = sqlAnalyse.getCriteriaAnalyse();
//					SqlCriteriaAnalyse sqlCriteriaAnalyse = new SqlCriteriaAnalyse();
//					if (list_sqlCriteriaAnalyse.size() != 0) {
//						sqlCriteriaAnalyse = sqlAnalyse.getCriteriaAnalyse().get(0);
//						if (list_sqlCriteriaAnalyse.size() != 1) {// 单独处理有效between的情况，因为
//							// between情况，会有两个sqlCriteriaAnalyse
//							SqlCriteriaAnalyse sqlCriteriaAnalyse2 = new SqlCriteriaAnalyse();
//							sqlCriteriaAnalyse2 = sqlAnalyse.getCriteriaAnalyse().get(1);
//							sqlCriteriaAnalyse
//									.setValue(sqlCriteriaAnalyse.getValue() + "," + sqlCriteriaAnalyse2.getValue());
//						}
//						String value = sqlCriteriaAnalyse.getValue();
//						List<String> valuelist = Arrays.asList(value.split(","));
//						value = new String();
//						for (int x = 0; x < valuelist.size(); x++) {
//							String everyvalue = valuelist.get(x);
//							if (everyvalue.startsWith("'")) {
//								everyvalue = everyvalue.replaceFirst("'", "");
//							}
//							if (everyvalue.endsWith("'")) {
//								everyvalue = everyvalue.substring(0, everyvalue.length() - 1);
//							}
//							value += everyvalue + ",";
//						}
//						value = value.substring(0, value.length() - 1);
//						if (flagIn == true) {
//							auto_tp_cond_info.setCon_row(String.valueOf(inJ));
//						} else {
//							auto_tp_cond_info.setCon_row(String.valueOf(j));
//						}
//						auto_tp_cond_info.setCond_para_name(sqlCriteriaAnalyse.getColumn());// 条件参数名称
//						auto_tp_cond_info.setCon_relation(sqlCriteriaAnalyse.getRelation());// 关联关系=、<>等关系
//						auto_tp_cond_info.setPre_value(value);// 预设值
//						resultlist.add(auto_tp_cond_info);
//					}
//					j++;
//					if (formatSqlList.size() > j) {
//						everySql2 = formatSqlList.get(j).trim();
//					}
//				}
//			}
//
//		}
//		return resultlist;
//	}

//	@Method(desc = "判断sql前面有多少个\\t 如果开头是where则count++",logicStep = "")
//	@Param(name = "sql", desc = "sql解析语句", range = "无限制")
//	@Return(desc = "", range = "")
//	private int countT(String sql) {
//		int count = 0;
//		while (sql.startsWith("\t")) {
//			sql = sql.replaceFirst("\t", "");
//			count++;
//		}
//		if (sql.trim().startsWith("WHERE ")) {
//			count++;
//		}
//		return count;
//	}
//	/**
//	 * 处理sql 添加右括号的情况
//	 *
//	 * @param everysql2
//	 * @return
//	 */
//	// TODO
//	private String addrightbrackets(String everysql2) {
//		String sql = everysql2;
//		int countleft = 0;
//		int countright = 0;
//		while (sql.contains("(")) {
//			sql = sql.replaceFirst("\\(", "");
//			countleft++;
//		}
//		while (sql.contains(")")) {
//			sql = sql.replaceFirst("\\)", "");
//			countright++;
//		}
//		if (countleft > countright) {
//			for (int i = countright; i < countleft; i++) {
//				everysql2 += ")";
//			}
//		}
//		return everysql2;
//	}
}
