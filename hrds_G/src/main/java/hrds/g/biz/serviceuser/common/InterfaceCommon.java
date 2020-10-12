package hrds.g.biz.serviceuser.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.InterfaceState;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.Interface_file_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.hbaseindexer.bean.HbaseSolrField;
import hrds.commons.hadoop.hbaseindexer.type.TypeFieldNameMapper;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.solr.ISolrOperator;
import hrds.commons.hadoop.solr.SolrFactory;
import hrds.commons.hadoop.solr.SolrParam;
import hrds.commons.hadoop.solr.utils.CollectionUtil;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.PropertyParaValue;
import hrds.g.biz.bean.CheckParam;
import hrds.g.biz.bean.QueryInterfaceInfo;
import hrds.g.biz.bean.SingleTable;
import hrds.g.biz.bean.TableData;
import hrds.g.biz.commons.LocalFile;
import hrds.g.biz.enumerate.AsynType;
import hrds.g.biz.enumerate.DataType;
import hrds.g.biz.enumerate.OutType;
import hrds.g.biz.enumerate.StateType;
import hrds.g.biz.init.InterfaceManager;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@DocClass(desc = "接口公共方法类", author = "dhw", createdate = "2020/4/9 17:34")
public class InterfaceCommon {

	private static final Logger logger = LogManager.getLogger();
	// 没有字段的函数添加列表
	private static final List<String> notCheckFunction = new ArrayList<>();
	private static long lineCounter = 0;
	// 接口响应信息集合
	private static Map<String, Object> responseMap = new HashMap<>();

	static {
		notCheckFunction.add("count(*)");
		notCheckFunction.add("count(1)");
	}

	private static final Type type = new TypeReference<List<String>>() {
	}.getType();
	private static final Type mapType = new TypeReference<Map<String, Object>>() {
	}.getType();

	@Method(desc = "获取token值",
			logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
					"2.根据用户id获取用户信息" +
					"3.判断用户信息是否为空，为空返回错误响应信息" +
					"4.检查用户是否存在,密码是否正确" +
					"4.1密码错误，返回错误响应信息" +
					"4.2密码正确，返回正确响应信息")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "user_password", desc = "密码", range = "新增用户时生成")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> getTokenById(DatabaseWrapper db, Long user_id, String user_password) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		// 2.根据用户id获取用户信息
		QueryInterfaceInfo queryInterfaceInfo = InterfaceManager.getUserTokenInfo(db, user_id);
		// 3.判断用户信息是否为空，为空返回错误响应信息
		if (null == queryInterfaceInfo) {
			return StateType.getResponseInfo(StateType.COLUMN_DOES_NOT_EXIST);
		}
		// 4.检查用户是否存在,密码是否正确
		if (!user_password.equals(queryInterfaceInfo.getUser_password())) {
			// 4.1密码错误，返回错误响应信息
			return StateType.getResponseInfo(StateType.UNAUTHORIZED);
		}
		// 4.2密码正确，返回正确响应信息
		Map<String, Object> tokenMap = new HashMap<>();
		tokenMap.put("token", queryInterfaceInfo.getToken());
		tokenMap.put("expires_in", 7200);
		tokenMap.put("use_valid_date", queryInterfaceInfo.getUse_valid_date());
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", StateType.NORMAL.name());
		responseMap.put("message", tokenMap);
		return responseMap;
	}

	@Method(desc = "检查接口响应信息并返回",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.token值为空时检查user_id与user_password是否也为空" +
					"3.user_id与user_password不为空，获取token值" +
					"4.判断获取token值是否成功" +
					"5.获取token值" +
					"6.判断token值是否存在" +
					"6.1 token值存在,检查接口状态,开始日期,结束日期的合法性" +
					"6.2 判断接口是否有效" +
					"7.返回token错误信息")
	@Param(name = "checkParam", desc = "检查接口参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> checkTokenAndInterface(DatabaseWrapper db, CheckParam checkParam) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		String token = checkParam.getToken();
		Map<String, Object> responseMap;
		if (StringUtil.isBlank(token)) {
			// 2.token值为空时检查user_id与user_password是否也为空
			if (checkParam.getUser_id() == null || StringUtil.isBlank(checkParam.getUser_password())) {
				return StateType.getResponseInfo(StateType.ARGUMENT_ERROR.name(),
						"token值为空时，user_id与user_password不能为空");
			}
			// 3.user_id与user_password不为空，获取token值
			responseMap = getTokenById(db, checkParam.getUser_id(), checkParam.getUser_password());
			// 4.判断获取token值是否成功
			if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
				return responseMap;
			}
			// 5.获取token值
			Map<String, Object> message = JsonUtil.toObject(JsonUtil.toJson(responseMap.get("message")),
					new TypeReference<Map<String, Object>>() {
					}.getType());
			token = message.get("token").toString();
		}
		// 6.判断token值是否存在
		if (InterfaceManager.existsToken(db, token)) {
			// 6.1 token值存在,检查接口状态,开始日期,结束日期的合法性
			QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(token);
			// 6.2判断接口是否有效
			responseMap = interfaceInfoCheck(db, userByToken.getUser_id(), checkParam.getUrl(),
					checkParam.getInterface_code());
			responseMap.put("token", token);
			return responseMap;
		}
		// 7.返回token错误信息
		return StateType.getResponseInfo(StateType.TOKEN_ERROR);
	}

	@Method(desc = "创建文件", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.判断文件是否存在且是否是文件夹，不存在创建目录" +
			"3.获取文件路径" +
			"4.判断文件是否存在，不存在创建" +
			"5.开始写文件" +
			"6.返回响应状态信息")
	@Param(name = "responseMap", desc = "接口响应信息", range = "无限制")
	@Param(name = "filepath", desc = "文件路径", range = "无限制")
	@Param(name = "filename", desc = "文件名称", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> createFile(Map<String, Object> responseMap, String filepath, String
			filename) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		BufferedWriter writer = null;
		try {
			File file = new File(filepath);
			// 2.判断文件是否存在且是否是文件夹，不存在创建目录
			if (!file.exists() && !file.isDirectory()) {
				if (!file.mkdirs()) {
					return StateType.getResponseInfo(StateType.CREATE_DIRECTOR_ERROR);
				}
			}
			// 3.获取文件路径
			filepath = filepath + File.separator + filename;
			// 4.判断文件是否存在，不存在创建
			File writeFile = new File(filepath);
			if (!writeFile.exists()) {
				if (!writeFile.createNewFile()) {
					return StateType.getResponseInfo(StateType.CREATE_FILE_ERROR);
				}
			}
			// 5.开始写文件
			writer = new BufferedWriter(new FileWriter(writeFile));
			writer.write(responseMap.toString());
			writer.flush();
			// 6.返回响应状态信息
			return responseMap;
		} catch (IOException e) {
			logger.error(e);
			return StateType.getResponseInfo(StateType.SIGNAL_FILE_ERROR);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.info(e);
				}
			}
		}
	}

	@Method(desc = "请求接口的类型,及使用权限检测",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.判断接口信息是否存在" +
					"3.存在，从内存中获取已加载的接口使用权限信息" +
					"3.2检查接口状态" +
					"3.3得到当前日期和有效日期做比较" +
					"3.4检查接口是否开始生效,num为0表示相等，大于0表示前值大于后值，小于0表示前值小于后值" +
					"3.5检查有效日期是否有效,num为0表示相等，大于0表示前值大于后值，小于0表示前值小于后值" +
					"3.6返回接口正常信息" +
					"4.不存在，返回接口无效错误信息")
	@Param(name = "user_id", desc = "用户ID(与user_password同选）", range = "新增用户时生成", nullable = true)
	@Param(name = "url", desc = "接口请求地址", range = "无限制")
	@Param(name = "interface_code", desc = "接口代码", range = "报表类型的接口使用", nullable = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> interfaceInfoCheck(DatabaseWrapper db, Long user_id, String url, String
			interface_code) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		if (StringUtil.isBlank(url)) {
			return StateType.getResponseInfo(StateType.URL_NOT_EXIST);
		}
		// 2.判断接口信息是否存在
		if (InterfaceManager.existsInterface(db, user_id, url)) {
			// 3.存在，从内存中获取已加载的接口使用权限信息
			QueryInterfaceInfo queryInterfaceInfo = InterfaceManager.getInterfaceUseInfo(user_id, url);
			// 3.1这里只有报表类型的接口才会由此验证
			if (StringUtil.isNotBlank(interface_code)) {
				if (!InterfaceManager.existsReportGraphic(user_id, interface_code)) {
					return StateType.getResponseInfo(StateType.REPORT_CODE_ERROR.name(),
							"报表( " + interface_code + " )编码不正确");
				}
			}
			// 3.2检查接口状态
			if (InterfaceState.JinYong == InterfaceState.ofEnumByCode(queryInterfaceInfo.getUse_state())) {
				return StateType.getResponseInfo(StateType.INTERFACE_STATE_ERROR);
			}
			// 3.3得到当前日期和有效日期做比较
			int num = queryInterfaceInfo.getStart_use_date().compareTo(DateUtil.getSysDate());
			// 3.4检查接口是否开始生效,num为0表示相等，大于0表示前值大于后值，小于0表示前值小于后值
			if (num > 0) {
				return StateType.getResponseInfo(StateType.START_DATE_ERROR);
			}
			num = queryInterfaceInfo.getUse_valid_date().compareTo(DateUtil.getSysDate());
			// 3.5检查有效日期是否有效,num为0表示相等，大于0表示前值大于后值，小于0表示前值小于后值
			if (num < 0) {
				return StateType.getResponseInfo(StateType.EFFECTIVE_DATE_ERROR);
			}
			return StateType.getResponseInfo(StateType.NORMAL);
		}
		// 4.不存在，返回接口无效错误信息
		return StateType.getResponseInfo(StateType.NO_INTERFACE_USE_PERMISSIONS);
	}

	@Method(desc = "检查表", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.验证数据输出类型，数据类型是否合法" +
			"3.判断表名称是否为空" +
			"4.检查表是否有使用权限" +
			"5.从内存中获取当前表的字段信息" +
			"6.判断要查询列是否存在" +
			"7.不存在，查询所有列" +
			"8.检查列信息")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "singleTable", desc = "单表查询实体参数", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> checkTable(DatabaseWrapper db, Long user_id, SingleTable singleTable) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		try {
			// 3.判断表名称是否为空
			if (StringUtil.isBlank(singleTable.getTableName())) {
				return StateType.getResponseInfo(StateType.TABLE_NOT_EXISTENT);

			}
			// 4.检查表是否有使用权限
			if (!InterfaceManager.existsTable(db, user_id, singleTable.getTableName())) {
				return StateType.getResponseInfo(StateType.NO_USR_PERMISSIONS);
			}
			// 5.从内存中获取当前表的字段信息
			String table_en_column = InterfaceManager.getUserTableInfo(Dbo.db(), user_id,
					singleTable.getTableName()).getTable_en_column();
			// 6.判断要查询列是否存在
//			String selectColumn = singleTable.getSelectColumn();
//			if (StringUtil.isBlank(selectColumn)) {
			// 不存在，查询索引的列 fixme 原来为什么要去查询二级索引信息表  目前直接查询当前表登记列
//				Result indexResult = Dbo.queryResult("SELECT indexes_field,indexes_select_field FROM "
//						+ Source_file_attribute.TableName + " s,indexes_info i WHERE s.file_id=i.file_id " +
//						"AND lower(sysreg_name) = lower(?)", singleTable.getTable());
//				StringBuilder sb = new StringBuilder(indexResult.getString(0, "indexes_field"));
//				String indexes_select_field = indexResult.getString(0, "indexes_select_field");
//				if (!StringUtil.isBlank(indexes_select_field)) {
//					sb.append(",").append(indexes_select_field);
//				}
//				// 获取索引字段、查询字段的集合
//				List<String> index = StringUtil.split(",",sb.toString());
			// 获取需要查询的列名的集合
//				List<String> cols = StringUtil.split(",", table_column_name);
//				StringBuilder select = new StringBuilder();
//				// 取交集
//				cols.retainAll(index);
//				cols.forEach(col -> {
//					select.append(",").append(col);
//				});
//				selectColumn = select.deleteCharAt(select.length() - 1).toString();
			// 7.不存在，查询所有列
//				singleTable.setSelectColumn(table_column_name);
//			}
			// 8.检查列信息
			return checkColumn(db, singleTable, table_en_column, user_id);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				return StateType.getResponseInfo(StateType.EXCEPTION.name(), e.getMessage());
			}
			return StateType.getResponseInfo(StateType.EXCEPTION);
		}

	}

	@Method(desc = "检查列是否存在", logicStep = "1.数据可访问权限处理方式,该方法不需要进行访问权限限制" +
			"2.如果不是指定用户将进行字段验证" +
			"3.获取用户需要查询的列名的列" +
			"4.判断列当前表对应数据库的列名称集合是否为空，不为空遍历列名称" +
			"5.判断当前列名称是否有权限" +
			"6.查询列存在，返回null")
	@Param(name = "selectColumn", desc = "需要查询的列名", range = "(selectColumn=column1,column2....等,号隔开)"
			+ "，如果没有，查询所有字段")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "columns", desc = "当前表对应数据库的列名称集合", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> checkColumnsIsExist(String selectColumn, Long user_id, List<String> columns) {
		// 1.数据可访问权限处理方式,该方法不需要进行访问权限限制
		if (StringUtil.isNotBlank(selectColumn)) {
			// 2.如果不是指定用户将进行字段验证
			if (!CommonVariables.AUTHORITY.contains(String.valueOf(user_id))) {
				// 3.获取用户需要查询的列名的列
				List<String> userColumns = StringUtil.split(selectColumn, ",");
				// 4.判断列当前表对应数据库的列名称集合是否为空，不为空遍历列名称
				if (columns != null && columns.size() != 0) {
					for (String userColumn : userColumns) {
						// 5.判断当前列名称是否有权限,没有返回错误响应信息
						if (columnIsExist(userColumn.toLowerCase(), columns)) {
							return StateType.getResponseInfo(StateType.COLUMN_DOES_NOT_EXIST.name(),
									"请求错误,查询列名" + userColumn + "不存在");
						}
					}
				}
			}
		}
		// 6.查询列存在，返回null
		return null;
	}

	@Method(desc = "查找列是否有权限", logicStep = "1.数据可访问权限处理方式,该方法不需要进行访问权限限制" +
			"2.判断没有字段查询的列是否为查询记录数" +
			"3.遍历有权限列，判断当前列是否有权限，有返回true，否则返回false")
	@Param(name = "col", desc = "需要查询的列信息", range = "无限制")
	@Param(name = "columns", desc = "权限的列信息集合", range = "无限制")
	@Return(desc = "返回列是否有权限标志", range = "")
	public static boolean columnIsExist(String col, List<String> columns) {
		// 1.数据可访问权限处理方式,该方法不需要进行访问权限限制
		// 2.判断没有字段查询的列是否为查询记录数
		if (notCheckFunction.contains(col.toLowerCase())) {
			return false;
		}
		// 3.遍历有权限列，判断当前列是否有权限，有返回true，否则返回false
		for (String column : columns) {
			if (column.trim().toLowerCase().equals(col.trim().toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	@Method(desc = "检查列", logicStep = "1.数据可访问权限处理方式,该方法不需要进行访问权限限制" +
			"2.显示条数如果为空默认10条" +
			"3.获取当前表对应数据库的列名称集合" +
			"4.检查需要查询的列名是否存在" +
			"5.判断当前表对应登记列名称是否为空，为空查询所有*" +
			"6.获取查询条件参数，判断查询列是否存在" +
			"7.获取sql查询条件，如果响应状态不为normal返回错误响应信息，如果是获取查询条件" +
			"8.获取查询sql" +
			"9.获取新sql，判断视图" +
			"10.根据sql获取搜索引擎并根据输出数据类型处理数据")
	@Param(name = "singleTable", desc = "单表查询参数实体", range = "无限制")
	@Param(name = "table_en_column", desc = "当前表对应登记列名称通过特殊字符拼接的字符串", range = "无限制")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> checkColumn(DatabaseWrapper db, SingleTable singleTable,
	                                              String table_en_column, Long user_id) {
		// 1.数据可访问权限处理方式,该方法不需要进行访问权限限制
		// 2.显示条数如果为空默认10条
		Integer num = singleTable.getNum();
		if (num == null) {
			num = 10;
		}
		// 3.获取当前表对应数据库的列名称集合
		List<String> columns = StringUtil.split(table_en_column.toLowerCase(), Constant.METAINFOSPLIT);
		String selectColumn = singleTable.getSelectColumn();
		// 4.检查需要查询的列名是否存在
		// 5.判断当前表对应登记列名称是否为空，为空查询所有*
		if (StringUtil.isNotBlank(selectColumn)) {
			Map<String, Object> userColumn = checkColumnsIsExist(selectColumn, user_id, columns);
			if (userColumn != null) return userColumn;
		} else if (StringUtil.isNotBlank(table_en_column.toLowerCase())) {
			selectColumn = String.join(",", columns).toLowerCase();
		} else {
			selectColumn = " * ";
		}
		// 6.获取查询条件参数，判断查询列是否存在
		String whereColumn = singleTable.getWhereColumn();
		String condition = "";
		if (StringUtil.isNotBlank(whereColumn)) {
			// 7.获取sql查询条件，如果响应状态不为normal返回错误响应信息，如果是获取查询条件
			Map<String, Object> sqlSelectCondition = getSqlSelectCondition(columns, whereColumn);
			if (!StateType.NORMAL.name().equals(sqlSelectCondition.get("status").toString())) {
				return sqlSelectCondition;
			}
			condition = sqlSelectCondition.get("condition").toString();
		}
		// 8.获取查询sql
		String sqlSb = "SELECT " + selectColumn + " FROM " + singleTable.getTableName() + condition;
		// 9.获取新sql，判断视图
		DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(sqlSb);
		String newSql = druidParseQuerySql.GetNewSql(sqlSb);
		// 10.根据sql获取搜索引擎并根据输出数据类型处理数据
		return getSqlData(db, singleTable.getOutType(), singleTable.getDataType(), newSql, user_id, num);
	}

	@Method(desc = "根据sql获取搜索引擎并根据输出数据类型处理数据",
			logicStep = "1.数据可访问权限处理方式,该方法不需要进行访问权限限制" +
					"2.根据sql获取搜索引擎并根据输出数据类型处理数据" +
					"3.根据输出数据类型不同处理数据" +
					"4.输出类型为stream，处理数据并返回" +
					"5.输出类型为file，创建本地文件,准备数据的写入" +
					"6.输出类型错误" +
					"7.如果文件是CSV则第一行为列信息" +
					"8.如果输出数据类型为json，则直接输出" +
					"9.返回正常响应信息9.输出数据形式不是stream返回处理后的响应数据")
	@Param(name = "outType", desc = "数据输出形式", range = "stream/file")
	@Param(name = "dataType", desc = "数据输出类型", range = "json/csv")
	@Param(name = "sqlSb", desc = "需要查询的sql语句", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> getSqlData(DatabaseWrapper db, String outType, String dataType,
	                                             String sqlSb, Long user_id, Integer num) {
		// 1.数据可访问权限处理方式,该方法不需要进行访问权限限制
		// 数据类型为json时列对应值信息
		List<Object> streamJson = new ArrayList<>();
		// 数据类型为csv时表对应列信息
		List<String> streamCsv = new ArrayList<>();
		// 数据类型为csv时表对应列值信息
		List<String> streamCsvData = new ArrayList<>();
		// 2.获取UUID
		String uuid = UUID.randomUUID().toString();
		File createFile = LocalFile.createFile(uuid, dataType);
		// 2.根据sql获取搜索引擎并根据输出数据类型处理数据
		try {
			if (OutType.STREAM == OutType.ofEnumByCode(outType)) {
				// 输出数据形式为stream如果num不存在则默认查询100
				if (num == null) {
					num = 100;
				}
				getProcessingData(outType, dataType, streamJson, streamCsv, streamCsvData, createFile)
						.getPageDataLayer(sqlSb, db, 1, num);
			} else {
				// 输出数据形式为file如果num不存在则查询所有
				if (num == null) {
					getProcessingData(outType, dataType, streamJson, streamCsv, streamCsvData, createFile)
							.getDataLayer(sqlSb, db);
				} else {
					getProcessingData(outType, dataType, streamJson, streamCsv, streamCsvData, createFile)
							.getPageDataLayer(sqlSb, db, 1, num);
				}
			}
		} catch (Exception e) {
			return StateType.getResponseInfo(StateType.EXCEPTION.name(), e.getMessage());
		}

		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			return responseMap;
		}
		// 7.输出类型为stream，如果输出数据类型为csv，第一行为列名，按csv格式处理数据并返回
		if (OutType.STREAM == OutType.ofEnumByCode(outType)) {
			if (DataType.csv == DataType.ofEnumByCode(dataType)) {
				Map<String, Object> map = new HashMap<>();
				map.put("column", streamCsv);
				map.put("data", streamCsvData);
				responseMap = StateType.getResponseInfo(StateType.NORMAL.name(),
						JsonUtil.toJson(map));
			} else {
				Map<String, Object> jsonMap = new HashMap<>();
				// 8.如果输出数据类型为json则直接返回数据
				jsonMap.put("data", streamJson);
				responseMap = StateType.getResponseInfo(StateType.NORMAL.name(),
						jsonMap);
			}
		} else {
			// 保存接口文件信息
			if (InterfaceCommon.saveFileInfo(db, user_id, uuid, dataType,
					outType, CommonVariables.RESTFILEPATH) != 1) {
				responseMap = StateType.getResponseInfo(StateType.EXCEPTION.name(),
						"保存接口文件信息失败");
			}
			Map<String, Object> uuidMap = new HashMap<>();
			uuidMap.put("dataType", dataType);
			uuidMap.put("outType", outType);
			uuidMap.put("uuid", uuid);
			responseMap = StateType.getResponseInfo(StateType.NORMAL.name(), uuidMap);
		}
		lineCounter = 0;
		// 9.输出数据形式不是stream返回处理后的响应数据
		return responseMap;
	}

	private static ProcessingData getProcessingData(String outType, String dataType,
	                                                List<Object> streamJson, List<String> streamCsv,
	                                                List<String> streamCsvData, File createFile) {
		return new ProcessingData() {
			@Override
			public void dealLine(Map<String, Object> map) {
				lineCounter++;
				// 数据类型为csv的列值集合
				StringBuffer sbCol = new StringBuffer();
				StringBuffer sbVal = new StringBuffer();
				// 3.根据输出数据类型不同处理数据
				if (OutType.STREAM == OutType.ofEnumByCode(outType)) {
					// 4.输出类型为stream，处理数据并返回
					dealWithStream(map, sbVal, dataType, streamCsv, streamCsvData, streamJson);
				} else {
					// 5.输出类型为file，创建本地文件,准备数据的写入
					dealWithFile(map, sbCol, sbVal, dataType, createFile);
				}
			}
		};
	}

	@Method(desc = "处理输出数据类型为file的数据",
			logicStep = "1.数据可访问权限处理方式,该方法不需要进行访问权限限制" +
					"2.获取UUID" +
					"3.创建存放文件的路径.文件名为UUID+dataType" +
					"4.创建写文件流" +
					"5.根据输出数据类型对数据进行不同的处理" +
					"6.map的key为列名称，value为列名称对应的对象信息" +
					"7.如果文件是CSV则第一行为列信息" +
					"8.如果文件写了24608行进行一次刷新并打印日志" +
					"9.写列对应值数据" +
					"10.如果输出数据类型为json，则直接输出" +
					"11.输出数据类型有误" +
					"12.关闭连接")
	@Param(name = "map", desc = "存放表列与值信息的集合", range = "无限制")
	@Param(name = "sbCol", desc = "拼接列对象", range = "无限制")
	@Param(name = "sbVal", desc = "拼接列对应值对象", range = "无限制")
	@Param(name = "dataType", desc = "输出数据类型", range = "使用（DataType代码项）")
	private static void dealWithFile(Map<String, Object> map, StringBuffer sbCol, StringBuffer sbVal,
	                                 String dataType, File createFile) {
		// 1.数据可访问权限处理方式,该方法不需要进行访问权限限制
		// 3.创建存放文件的路径.文件名为UUID+dataType
		BufferedWriter writer;
		try {
			// 4.创建写文件流
			writer = new BufferedWriter(new FileWriter(createFile, true));
			// 5.根据输出数据类型对数据进行不同的处理
			if (DataType.csv == DataType.ofEnumByCode(dataType)) {
				// 6.map的key为列名称，value为列名称对应的对象信息
				map.forEach((k, v) -> {
					sbCol.append(k).append(",");
					sbVal.append(v).append(",");
				});
				// 7.如果文件是CSV则第一行为列信息
				if (lineCounter == 1) {
					writer.write(sbCol.deleteCharAt(sbCol.length() - 1).toString());
					writer.newLine();
				}
				// 8.如果文件写了24608行进行一次刷新并打印日志
				if (lineCounter % 24608 == 0) {
					logger.info("已经处理了 ：" + lineCounter + " 行数据！");
					writer.flush();
				}
				// 9.写列对应值数据
				writer.write(sbVal.deleteCharAt(sbVal.length() - 1).toString());
				writer.newLine();
				responseMap = StateType.getResponseInfo(StateType.NORMAL);
			} else if (DataType.json == DataType.ofEnumByCode(dataType)) {
				// 10.如果输出数据类型为json，则直接输出
				if (lineCounter % 24608 == 0) {
					writer.flush();
				}
				writer.write(JsonUtil.toJson(map));
				writer.newLine();
				responseMap = StateType.getResponseInfo(StateType.NORMAL);
			} else {
				// 11.输出数据类型有误
				responseMap = StateType.getResponseInfo(StateType.EXCEPTION.name(),
						"不知道什么文件");
			}
			// 12.关闭连接
			writer.flush();
			writer.close();
		} catch (IOException e) {
			responseMap = StateType.getResponseInfo(StateType.EXCEPTION.name(),
					"写文件失败");
		}
	}

	@Method(desc = "处理输出数据类型为stream的数据",
			logicStep = "1.数据可访问权限处理方式,该方法不需要进行访问权限限制" +
					"2.根据输出数据类型对数据进行不同的处理" +
					"3.遍历获取列与值信息" +
					"4.如果文件是CSV则第一行为列信息" +
					"5.循环添加表对应列值信息" +
					"6.数据类型为json，循环添加表信息" +
					"7.输出数据类型有误")
	@Param(name = "map", desc = "存放表列与值信息的集合", range = "无限制")
	@Param(name = "sbCol", desc = "拼接列对象", range = "无限制")
	@Param(name = "sbVal", desc = "拼接列对应值对象", range = "无限制")
	@Param(name = "dataType", desc = "输出数据类型", range = "使用（DataType代码项）")
	@Param(name = "streamCsv", desc = "数据类型为csv时表对应列信息", range = "无限制")
	@Param(name = "streamCsvData", desc = "数据类型为csv时表对应列值信息", range = "无限制")
	@Param(name = "streamJson", desc = "数据类型为json时列对应值信息", range = "无限制")
	private static void dealWithStream(Map<String, Object> map, StringBuffer sbVal,
	                                   String dataType,
	                                   List<String> streamCsv, List<String> streamCsvData,
	                                   List<Object> streamJson) {
		// 1.数据可访问权限处理方式,该方法不需要进行访问权限限制
		// 2.根据输出数据类型对数据进行不同的处理
		if (DataType.csv == DataType.ofEnumByCode(dataType)) {
			// 3.遍历获取列与值信息
			map.forEach((k, v) -> {
				// 4.如果文件是CSV则第一行为列信息
				if (lineCounter == 1) {
					streamCsv.add(k);
				}
				sbVal.append(v).append(",");
			});
			// 5.循环添加表对应列值信息
			streamCsvData.add(sbVal.deleteCharAt(sbVal.length() - 1).toString());
			responseMap = StateType.getResponseInfo(StateType.NORMAL);
		} else if (DataType.json == DataType.ofEnumByCode(dataType)) {
			// 6.数据类型为json，循环添加表信息
			streamJson.add(map);
			responseMap = StateType.getResponseInfo(StateType.NORMAL);
		} else {
			// 7.输出数据类型有误
			responseMap = StateType.getResponseInfo(StateType.DATA_TYPE_ERROR);
		}
	}

	@Method(desc = "获取查询sql条件", logicStep = "1.数据可访问权限处理方式,该方法不需要进行访问权限限制" +
			"2.存在，分隔条件列参数，遍历列参数，获取列名与列值" +
			"3.判断条件列长度是否为2，如果是获取列名、列值" +
			"4.判断列名是否存在,不存在返回错误响应信息" +
			"5.存在，拼接查询条件" +
			"6.删除拼接查询条件的最后一个and,返回查询条件")
	@Param(name = "columns", desc = "权限的列信息集合", range = "无限制")
	@Param(name = "whereColumn", desc = "查询条件(whereColumn=column1=zhangsan,age>=23...等用','号隔开)",
			range = "目前支持>=,<=,<,>,=,!=")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> getSqlSelectCondition(List<String> columns, String whereColumn) {
		// 1.数据可访问权限处理方式,该方法不需要进行访问权限限制
		// 2.存在，分隔条件列参数，遍历列参数，获取列名与列值
		String condition;
		String[] cols = whereColumn.split(",");
		StringBuilder whereSb = new StringBuilder();
		whereSb.append(" where ");
		for (String col : cols) {
			// 将字段的列名称和value分开后,格式为 [name,XX]
			String[] col_name;
			String symbol;
			if (col.contains(">=")) {
				col_name = col.split(">=");
				symbol = ">=";
			} else if (col.contains("<=")) {
				col_name = col.split("<=");
				symbol = "<=";
			} else if (col.contains(">")) {
				col_name = col.split(">");
				symbol = ">";
			} else if (col.contains("<")) {
				col_name = col.split("<");
				symbol = "<";
			} else if (col.contains("!=")) {
				col_name = col.split("!=");
				symbol = "!=";
			} else if (col.contains("=")) {
				col_name = col.split("=");
				symbol = "=";
			} else {
				return StateType.getResponseInfo(StateType.CONDITION_ERROR.name(),
						"请求错误,条件符号错误,暂不支持");
			}
			// 3.判断条件列长度是否为2，如果是获取列名、列值
			if (col_name.length == 2) {
				// 列名
				String colName = col_name[0];
				// 列值
				String colVal = col_name[1];
				// 4.判断列名是否存在,不存在返回错误响应信息
				if (columnIsExist(colName.toLowerCase(), columns)) {
					return StateType.getResponseInfo(StateType.COLUMN_DOES_NOT_EXIST.name(),
							"请求错误,条件列名" + colName + "不存在");
				}
				// 5.存在，拼接查询条件
				whereSb.append(colName).append(symbol).append("'").append(colVal).append("'").append(" and ");
			}
		}
		// 6.删除拼接查询条件的最后一个and,返回查询条件
		condition = whereSb.toString().substring(0, whereSb.toString().lastIndexOf("and"));
		Map<String, Object> responseInfo = StateType.getResponseInfo(StateType.NORMAL);
		responseInfo.put("condition", condition);
		return responseInfo;
	}

	@Method(desc = "校验表权限", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断表是否存在" +
			"3.判断表是否有使用权限")
	@Param(name = "user_id", desc = "用户ID", range = "无限制")
	@Param(name = "table_name", desc = "表名称", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> verifyTable(DatabaseWrapper db, Long user_id, String table_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.判断表是否存在
		if (StringUtil.isBlank(table_name)) {
			return StateType.getResponseInfo(StateType.TABLE_NOT_EXISTENT);
		}
		// 3.判断表是否有使用权限
		if (!InterfaceManager.existsTable(db, user_id, table_name)) {
			return StateType.getResponseInfo(StateType.NO_USR_PERMISSIONS);
		}
		return StateType.getResponseInfo(StateType.NORMAL);

	}

	@Method(desc = "检查回调url地址是否可以连接",
			logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
					"2.通过回调url请求服务" +
					"3.判断请求是否成功" +
					"4.请求成功，返回响应信息")
	@Param(name = "responseMap", desc = "接口响应信息", range = "无限制")
	@Param(name = "backurl", desc = "回调url地址", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> checkBackUrl(Map<String, Object> responseMap, String backurl) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		// 2.通过回调url请求服务
		String response = new HttpClient().addData("backurl", backurl)
				.addData("message", JsonUtil.toJson(responseMap))
				.post(backurl).getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(response, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		// 3.判断请求是否成功
		if (!actionResult.isSuccess()) {
			responseMap = StateType.getResponseInfo(StateType.CALBACK_URL_ERROR);
		}
		return responseMap;
	}

	@Method(desc = "检查接口是否异步状态",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.判断输出数据形式是否合法" +
					"3.判断输出数据形式是否为file" +
					"3.1判断是否为异步状态是否合法" +
					"3.2判断是否为异步回调，如果是判断回调url是否为空" +
					"3.3判断是否为异步轮询，如果是判断filename,filepath是否为空")
	@Param(name = "responseMap", desc = "接口响应状态信息集合", range = "无限制")
	@Param(name = "outType", desc = "输出数据类型", range = "stream,file二选一")
	@Param(name = "asynType", desc = "是否异步状态", range = "0：同步返回，1:异步回调，2：异步轮询")
	@Param(name = "backUrl", desc = "回调utl地址", range = "asynType为1时必传", nullable = true)
	@Param(name = "fileName", desc = "文件名称", range = "asynType为2时必传", nullable = true)
	@Param(name = "filepath", desc = "文件路径", range = "asynType为2时必传", nullable = true)
	@Return(desc = "返回响应状态信息", range = "无限制")
	public static Map<String, Object> checkType(String dataType, String outType, String asynType,
	                                            String backUrl, String filepath, String fileName) {

		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.判断输出数据类型是否合法
		if (!DataType.isDataType(dataType)) {
			return StateType.getResponseInfo(StateType.DATA_TYPE_ERROR);
		}
		// 3.判断输出类型是否合法
		if (!OutType.isOutType(outType)) {
			return StateType.getResponseInfo(StateType.OUT_TYPE_ERROR);
		}
		// 3.判断输出数据形式是否为file
		if (OutType.FILE == OutType.ofEnumByCode(outType)) {
			// 3.1判断是否为异步状态是否合法
			if (!AsynType.isAsynType(asynType)) {
				return StateType.getResponseInfo(StateType.ASYNTYPE_ERROR);
			}
			// 3.2判断是否为异步回调，如果是判断回调url是否为空
			if (AsynType.ASYNCALLBACK == AsynType.ofEnumByCode(asynType)) {
				if (StringUtil.isBlank(backUrl)) {
					return StateType.getResponseInfo(StateType.CALBACK_URL_ERROR);
				}
			}
			// 3.3判断是否为异步轮询，如果是判断filename,filepath是否为空
			if (AsynType.ASYNPOLLING == AsynType.ofEnumByCode(asynType)) {
				if (StringUtil.isBlank(fileName)) {
					return StateType.getResponseInfo(StateType.FILENAME_ERROR);
				}
				if (StringUtil.isBlank(filepath)) {
					return StateType.getResponseInfo(StateType.FILEPARH_ERROR);
				}
			}
		}
		return StateType.getResponseInfo(StateType.NORMAL);
	}

	@Method(desc = "按类型操作处理接口响应信息", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.数据类型选择csv,输出数据类型选择stream" +
			"3.数据类型选择json,输出数据类型选择stream" +
			"4.输出数据类型选择file,asynType选择同步返回显示" +
			"5.输出数据类型选择file,asynType选择异步回调" +
			"6.输出数据类型选择file,asynType选择异步轮询" +
			"7.返回接口响应信息")
	@Param(name = "dataType", desc = "数据类型", range = "json/csv")
	@Param(name = "outType", desc = "输出数据类型", range = "stream/file")
	@Param(name = "asynType", desc = "是否异步标志", range = "0：同步，1；异步回调，2异步轮询")
	@Param(name = "responseMap", desc = "接口响应信息", range = "无限制")
	@Param(name = "filePath", desc = "文件路径", range = "无限制")
	@Param(name = "fileName", desc = "文件名称", range = "无限制")
	@Param(name = "backUrl", desc = "回调url地址", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> operateInterfaceByType(String dataType, String outType, String asynType,
	                                                         String backUrl, String filePath, String fileName,
	                                                         Map<String, Object> responseMap) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		// 2.数据类型选择csv,输出数据类型选择stream
		if (DataType.csv == DataType.ofEnumByCode(dataType)
				&& OutType.STREAM == OutType.ofEnumByCode(outType)
				&& StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			try {
				Map<String, Object> message = JsonUtil.toObject(responseMap.get("message").toString(),
						mapType);
				List<String> dataList = JsonUtil.toObject(message.get("data").toString(), type);
				List<String> columnList = JsonUtil.toObject(message.get("column").toString(), type);
				String data = String.join(System.lineSeparator(), dataList);
				String column = String.join(",", columnList);
				return StateType.getResponseInfo(StateType.NORMAL.name(),
						column + System.lineSeparator() + data);
			} catch (Exception e) {
				return StateType.getResponseInfo(StateType.JSONCONVERSION_EXCEPTION);
			}
		}
		// 3.数据类型选择json,输出数据类型选择stream
		if (DataType.json == DataType.ofEnumByCode(dataType)
				&& OutType.STREAM == OutType.ofEnumByCode(outType)
				&& StateType.NORMAL == StateType.ofEnumByCode(responseMap.get("status").toString())) {
			return responseMap;
		}
		// 4.输出数据类型选择file,asynType选择同步返回显示
		if (AsynType.SYNCHRONIZE == AsynType.ofEnumByCode(asynType)) {
			return responseMap;
		}
		// 5.输出数据类型选择file,asynType选择异步回调
		if (AsynType.ASYNCALLBACK == AsynType.ofEnumByCode(asynType)) {
			responseMap = checkBackUrl(responseMap, backUrl);
		}
		// 6.输出数据类型选择file,asynType选择异步轮询
		if (AsynType.ASYNPOLLING == AsynType.ofEnumByCode(asynType)) {
			responseMap = createFile(responseMap, filePath, fileName);
		}
		// 7.返回接口响应信息
		return responseMap;
	}

	@Method(desc = "保存接口文件生成信息并返回保存状态",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.保存接口文件生成信息并返回保存状态")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "uuid", desc = "文件ID", range = "使用uuid生成")
	@Param(name = "dataType", desc = "输出数据类型", range = "json/csv")
	@Param(name = "outType", desc = "输出数据形式", range = "stream/file")
	@Param(name = "path", desc = "文件路径", range = "生成文件路径")
	@Return(desc = "返回保存文件生成信息是否成功状态", range = "1代表成功，否则失败")
	public static int saveFileInfo(DatabaseWrapper db, Long user_id, String uuid, String dataType,
	                               String outType, String path) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		Interface_file_info file_info = new Interface_file_info();
		file_info.setFile_id(uuid);
		file_info.setFile_path(path);
		file_info.setData_output(outType);
		file_info.setUser_id(user_id);
		file_info.setData_class(dataType);
		// 2.保存接口文件生成信息并返回保存状态
		int num = file_info.add(db);
		db.commit();
		return num;
	}

	@Method(desc = "根据表名称删除表数据", logicStep = "1.根据表名获取存储层的信息" +
			"2.判断当前表对应的存储层信息是否存在" +
			"3.获取当前表对应存储层的db连接" +
			"4.存储层类型为关系型数据库" +
			"4.1从内存中获取当前表的字段信息" +
			"4.2获取当前表对应数据库的列名称集合" +
			"4.3.获取sql查询条件，如果响应状态不为normal返回错误响应信息，如果是获取查询条件" +
			"4.4 拼接删除数据表数据sql" +
			"4.5 删除数据表数据" +
			"5.删除hbase表数据")
	@Param(name = "db", desc = "db连接对象", range = "无限制")
	@Param(name = "tableData", desc = "表数据接口参数实体对象", range = "自定义实体")
	@Param(name = "user_id", desc = "当前登录用户ID", range = "新增用户时生成")
	@Param(name = "responseMap", desc = "接口响应信息", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> deleteTableDataByTableName(DatabaseWrapper db, TableData tableData,
	                                                             Long user_id) {
		// 1.根据表名获取存储层的信息
		List<LayerBean> tableLayerList = ProcessingData.getLayerByTable(tableData.getTableName(), db);
		// 2.判断当前表对应的存储层信息是否存在
		if (tableLayerList.isEmpty()) {
			return StateType.getResponseInfo(StateType.STORAGELAYER_NOT_EXIST_BY_TABLE);
		}
		for (LayerBean layerBean : tableLayerList) {
			// 3.获取当前表对应存储层的db连接
			try (DatabaseWrapper dbWrapper = ConnectionTool.getDBWrapper(db, layerBean.getDsl_id())) {
				String store_type = layerBean.getStore_type();
				if (Store_type.DATABASE == Store_type.ofEnumByCode(store_type)) {
					// 4.存储层类型为关系型数据库
					// 4.1从内存中获取当前表的字段信息
					String table_en_column = InterfaceManager.getUserTableInfo(Dbo.db(), user_id,
							tableData.getTableName()).getTable_en_column();
					// 4.2获取当前表对应数据库的列名称集合
					List<String> columns = StringUtil.split(table_en_column.toLowerCase(), Constant.METAINFOSPLIT);
					String whereColumn = tableData.getWhereColumn();
					String condition = "";
					if (StringUtil.isNotBlank(whereColumn)) {
						// 4.3.获取sql查询条件，如果响应状态不为normal返回错误响应信息，如果是获取查询条件
						responseMap =
								InterfaceCommon.getSqlSelectCondition(columns, whereColumn);
						if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
							return responseMap;
						}
						condition = responseMap.get("condition").toString();
					}
					// 4.4 拼接删除数据表数据sql
					String deleteSql = "delete from " + tableData.getTableName() + condition;
					// 4.5 删除数据表数据
					SqlOperator.execute(dbWrapper, deleteSql);
					SqlOperator.commitTransaction(dbWrapper);
				} else if (Store_type.ofEnumByCode(store_type) == Store_type.HBASE) {
					// 5.删除hbase表数据
					return deleteHbaseData(tableData.getTableName(), tableData.getRowKeys());
				} else {
					return StateType.getResponseInfo(StateType.STORE_TYPE_NOT_EXIST);
				}
			} catch (Exception e) {
				return StateType.getResponseInfo(StateType.DELETE_TABLE_DATA_FAILED);
			}
		}
		return StateType.getResponseInfo(StateType.NORMAL);
	}

	@Method(desc = "删除hbase表数据", logicStep = "")
	@Param(name = "tableName", desc = "表名称", range = "无限制")
	@Param(name = "rowKeys", desc = "hbase rowkey数组", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	private static Map<String, Object> deleteHbaseData(String tableName, String[] rowKeys) {
		try (HBaseHelper helper = HBaseHelper.getHelper()) {
			if (helper.existsTable(tableName)) {
				try (Table table = helper.getTable(tableName)) {
					List<Delete> deleteList = new ArrayList<>();
					List<String> rowkeyList = new ArrayList<>();
					for (String rowkey : rowKeys) {
						Get get = new Get(rowkey.getBytes());
						if (!table.get(get).isEmpty()) {
							// 获取要删除的数据
							Delete delete = new Delete(rowkey.getBytes());
							deleteList.add(delete);
						} else {
							rowkeyList.add(rowkey);
						}
					}
					// 判断根据rowkey要删除的表数据是否存在
					if (rowkeyList.size() > 0) {
						return StateType.getResponseInfo(StateType.TABLE_DATA_NOT_EXIST_BY_ROWKEY);
					} else {
						// 删除hbase数据
						table.delete(deleteList);
					}
				}
			} else {
				return StateType.getResponseInfo(StateType.TABLE_NOT_EXISTENT);
			}
		} catch (Exception e) {
			return StateType.getResponseInfo(StateType.EXCEPTION);
		}
		return StateType.getResponseInfo(StateType.NORMAL);
	}

	public static Map<String, Object> getHbaseSolrQuery(String table_name, String whereColumn,
	                                                    String selectColumn, Integer start, Integer num,
	                                                    String table_column_name, String tableTypeJsonStr,
	                                                    String dsl_name, String platform, String prncipal_name,
	                                                    String hadoop_user_name) {
		try (HBaseHelper helper = HBaseHelper.getHelper(ConfigReader.getConfiguration(
				FileNameUtils.normalize(
						Constant.STORECONFIGPATH + dsl_name + File.separator, true),
				platform, prncipal_name, hadoop_user_name))) {
			// 当前表的有效全部列
			List<String> columns = StringUtil.split(table_column_name.toLowerCase(), Constant.METAINFOSPLIT);
			StringBuilder filter = new StringBuilder();
//			filter.append(ConfigurationUtil.TABLE_NAME_FIELD).append(":").append(table_name).append(" AND ");
			List<String> tableTypeList = StringUtil.split(tableTypeJsonStr, Constant.METAINFOSPLIT);
			if (StringUtil.isNotBlank(whereColumn)) {
				String[] cols = whereColumn.split(",");
				for (String col : cols) {
					// 将字段的列名称和value分开后,格式为 [name,XX]
					List<String> col_name;
					if (col.contains("=")) {
						col_name = StringUtil.split(col, "=");
					} else {
						return StateType.getResponseInfo(StateType.CONDITION_ERROR);
					}
					if (col_name.size() == 2) {
						String colName = col_name.get(0).trim();
						String colVal = col_name.get(1);
						// 查看当前查询列是否为有效
						if (!columns.contains(colName.toLowerCase())) {
							return StateType.getResponseInfo(StateType.COLUMN_DOES_NOT_EXIST);
						}
						for (String tableType : tableTypeList) {
							JSONObject tableTypeJson = JSONObject.parseObject(tableType);
							String column_name = tableTypeJson.getString("column_name");
							if (colName.equals(column_name)) {
								String solrFieldName = solrFieldName(colName,
										tableTypeJson.getString("column_id"));
								filter.append(solrFieldName).append(":").append(colVal).append(" AND ");
							}
						}
					}
				}
			} else {
				return StateType.getResponseInfo(StateType.CONDITION_ERROR);
			}
			// 去掉查询条件中最后一个AND
			String query = filter.substring(0, filter.lastIndexOf(" AND "));
			logger.info("query:" + query);
			// 修改solr连接为长连接
			Map<String, String> params = new HashMap<>();
			params.put("q", query);
			params.put("fl", "id");
			//初始化solr连接
			ISolrOperator iSolrOperator = getISolrOperator(table_name);
			// 查询solr,只获取id的数据
			List<Map<String, Object>> result = iSolrOperator.querySolrPlus(params, start == null ? 0 : start,
					num == null ? 10 : num, false);
			// solr中的id作为hbase的rowkey查询hbase数据
			// 设置hbase表名对象
			Table table = helper.getTable(table_name);
			List<Get> getList = new ArrayList<>();
			for (Map<String, Object> obj : result) {
				// 设置rowkey数组
				String rowkey = StringUtil.replace(obj.get("id").toString(),
						table_name + "@", "");
				logger.info("rowkey: " + rowkey);
				Get get = new Get(rowkey.getBytes());
				getList.add(get);
			}
			List<Map<String, Object>> js = new ArrayList<>();
			// 根据rowkey查询
			Result[] results = table.get(getList);
			Map<String, Object> temp;
			List<String> selectList = StringUtil.split(selectColumn.toLowerCase(), Constant.METAINFOSPLIT);
			// 当前查询的全部列
			for (Result hResult : results) {
				if (hResult.isEmpty()) {
					logger.info("rowkey查询结果为空");
					continue;
				}
				temp = new HashMap<>();
				List<Cell> cs = hResult.listCells();
				for (Cell cell : cs) {
					String column = Bytes.toString(CellUtil.cloneQualifier(cell));// 获取hbase列名
					String value = Bytes.toString(CellUtil.cloneValue(cell));// 获取hbase列值
					if (selectList.contains(column.toLowerCase())) {
						temp.put(column.toLowerCase(), value);
					}
				}
				js.add(temp);
			}
			return StateType.getResponseInfo(StateType.NORMAL.name(), js);
		} catch (Exception e) {
			logger.error("checkColumn wrong...", e);
			if (e instanceof BusinessException) {
				return StateType.getResponseInfo(StateType.EXCEPTION.name(), e.getMessage());
			} else {
				return StateType.getResponseInfo(StateType.EXCEPTION);
			}
		}
	}

	private static ISolrOperator getISolrOperator(String table_name) {
		SolrParam param = new SolrParam();
		String solrZkUrl = PropertyParaValue.getString("zkHost", "cdh063:2181,cdh064:2181,cdh065:2181/solr");
		param.setCollection(CollectionUtil.getCollection(table_name));
		param.setSolrZkUrl(solrZkUrl);
		return SolrFactory.getInstance(param);
	}

	private static String solrFieldName(String hbaseFieldName, String type) {
		if (type == null) {
			logger.info("column " + hbaseFieldName + "'type is null, so using STRING");
			type = "STRING";
		}
		HbaseSolrField hsf = new HbaseSolrField();
		hsf.setHbaseColumnName(hbaseFieldName);
		hsf.setType(type);
		new TypeFieldNameMapper().map(hsf);
		return hsf.getSolrColumnName();
	}
}
