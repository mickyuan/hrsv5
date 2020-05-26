package hrds.g.biz.init;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Interface_use;
import hrds.commons.entity.Sys_user;
import hrds.commons.entity.Sysreg_parameter_info;
import hrds.commons.entity.Table_use_info;
import hrds.commons.utils.Constant;
import hrds.g.biz.bean.QueryInterfaceInfo;
import hrds.g.biz.bean.TokenModel;
import hrds.g.biz.commons.TokenManagerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@DocClass(desc = "接口初始化信息管理类", author = "dhw", createdate = "2020/3/30 16:09")
public class InterfaceManager {

	private static final Logger logger = LogManager.getLogger();
	//根据用户ID获取用户信息
	private static final ConcurrentHashMap<Long, QueryInterfaceInfo> userMap = new ConcurrentHashMap<>();
	//根据用户ID以及接口使用地址获取接口权限
	private static final ConcurrentHashMap<String, QueryInterfaceInfo> interfaceMap = new ConcurrentHashMap<>();
	//根据用户ID以及表名称获取表使用权限
	private static final ConcurrentHashMap<String, QueryInterfaceInfo> tableMap = new ConcurrentHashMap<>();
	//根据用户的Token获取用户信息
	private static final ConcurrentHashMap<String, QueryInterfaceInfo> toKenMap = new ConcurrentHashMap<>();
	//根据用户ID获取请求地址信息
	private static final ConcurrentHashMap<String, List<String>> urlMap = new ConcurrentHashMap<>();
	//用户ID
	private static final Set<Long> userSet = ConcurrentHashMap.newKeySet();
	//报表的编码,只有报表使用
	private static final ConcurrentHashMap<Long, List<String>> reportGraphicMap = new ConcurrentHashMap<>();

	static {
		// 初始化接口
		logger.info("初始化接口开始");
		initAll();
	}

	@Method(desc = "初始化接口所有信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.初始化用户的权限" +
					"3.初始化接口使用权限" +
					"4.初始化接口数据表权限")
	public static void initAll() {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		logger.info("初始化接口所有信息开始。。。。。。。。。");
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 2.初始化用户的权限
			userInfo(db);
			// 3.初始化接口使用权限
			userInterface(db);
			// 4.初始化接口数据表权限
			userTableInfo(db);
			db.commit();
		}
		logger.info("初始化接口所有信息结束。。。。。。。。。");
	}

	@Method(desc = "初始化接口用户信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.初始化接口使用权限")
	public static void initUser(DatabaseWrapper db) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.初始化接口使用权限
		userInfo(db);
		db.commit();
	}

	@Method(desc = "初始化接口权限信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.初始化接口使用权限")
	public static void initInterface(DatabaseWrapper db) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.初始化接口使用权限
		userInterface(db);
	}

	@Method(desc = "初始化数据表使用权限",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.初始化接口数据表权限")
	public static void initTable(DatabaseWrapper db) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.初始化接口数据表权限
		userTableInfo(db);
	}

	@Method(desc = "根据用户id获取用户信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.根据用户id获取用户信息")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Return(desc = "返回根据用户id获取用户信息", range = "无限制")
	public static QueryInterfaceInfo getUserTokenInfo(Long user_id) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.根据用户id获取用户信息
		return userMap.get(user_id);
	}

	@Method(desc = "获取接口的使用权限信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.根据用户ID与接口请求地址获取表使用信息")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "url", desc = "接口请求地址", range = "无限制")
	@Return(desc = "返回根据用户ID与接口请求地址获取表使用信息", range = "无限制")
	public static QueryInterfaceInfo getInterfaceUseInfo(Long user_id, String url) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.根据用户ID与接口请求地址获取表使用信息
		return interfaceMap.get(String.valueOf(user_id).concat(url));
	}

	@Method(desc = "根据ID获取接口的url地址信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.根据ID获取接口的url地址信息")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Return(desc = "返回根据用户ID与接口请求地址获取表使用信息", range = "无限制")
	public static List<String> getUrlInfo(String user_id) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.根据ID获取接口的url地址信息
		return urlMap.get(user_id);
	}

	@Method(desc = "获取接口表的使用权限信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.根据用户ID表名称获取表使用信息")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "tableName", desc = "表名称", range = "无限制")
	@Return(desc = "返回接口表的使用权限信息", range = "无限制")
	public static QueryInterfaceInfo getUserTableInfo(Long user_id, String tableName) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.根据用户ID表名称获取表使用信息
		return tableMap.get(String.valueOf(user_id).concat(tableName.toUpperCase()));
	}

	@Method(desc = "根据token获取获取用户信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.根据token获取获取用户信息")
	@Param(name = "toKen", desc = "访问接口令牌", range = "通过获取token接口获得")
	@Return(desc = "返回根据token获取获取用户信息", range = "无限制")
	public static QueryInterfaceInfo getUserByToken(String toKen) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.根据token获取获取用户信息
		return toKenMap.get(toKen);
	}

	@Method(desc = "获取当前用户表使用信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.遍历表使用信息并封装所需信息" +
					"3.返回所需表使用信息集合")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Return(desc = "返回所需表使用信息集合", range = "无限制")
	public static List<Map<String, Object>> getTableList(Long user_id) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		List<Map<String, Object>> list = new ArrayList<>();
		// 2.遍历表使用信息并封装表信息
		tableMap.forEach((key, value) -> {
			Map<String, Object> map;
			if (key.contains(String.valueOf(user_id))) {
				map = new HashMap<>();
				map.put("SYSREG_NAME", value.getSysreg_name());
				map.put("ORIGINAL_NAME", value.getOriginal_name());
				map.put("TABLE_BLSYSTEM", value.getTable_blsystem());
				if (!map.isEmpty()) {
					list.add(map);
				}
			}
		});
		// 3.返回所需表使用信息集合
		return list;
	}

	@Method(desc = "从内存中删除接口权限信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.从接口使用信息中移除当前用户对应的接口信息")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "url", desc = "接口请求地址", range = "无限制")
	public static void removeUserInterInfo(String user_id, String url) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.从接口使用信息中移除当前用户对应的接口信息
		interfaceMap.remove(String.valueOf(user_id).concat(url));
	}

	@Method(desc = "从内存中删除接口表的使用权限信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.从表使用信息中移除当前用户对应的表信息")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "tableName", desc = "表名称", range = "无限制")
	public static void removeUserTableInfo(String user_id, String tableName) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.从表使用信息中移除当前用户对应的表信息
		tableMap.remove(String.valueOf(user_id).concat(tableName.toUpperCase()));
	}

	@Method(desc = "检测用户的Token是否存在",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.判断用户的Token是否存在，如果不存在,将加载一次内存..加载完后再检查一次用户权限" +
					"3.返回用户的Token是否存在标志")
	@Param(name = "toKen", desc = "访问接口令牌", range = "通过获取token接口获得")
	@Return(desc = "返回用户的Token是否存在标志", range = "无限制")
	public static boolean existsToken(String toKen) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.判断用户的Token是否存在，如果不存在,将加载一次内存..加载完后再检查一次用户权限
		if (!toKenMap.containsKey(toKen)) {
			initUser(new DatabaseWrapper());
		}
		// 3.返回用户的Token是否存在标志
		return toKenMap.containsKey(toKen);
	}

	@Method(desc = "检测报表编码是否存",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.返回报表编码是否存在标志")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "interface_code", desc = "接口代码", range = "无限制")
	@Return(desc = "", range = "")
	public static boolean existsReportGraphic(Long user_id, String interface_code) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.返回报表编码是否存在标志
		return reportGraphicMap.get(user_id).contains(interface_code);
	}

	@Method(desc = "检查表是否有使用权限",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.判断表使用信息是否存在，如果不存在,将加载一次内存..加载完后再检查一次确定是否有表的使用权限" +
					"3.返回表使用信息是否存在标志")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "tableName", desc = "表名称", range = "无限制")
	@Return(desc = "返回接口是否存在标志", range = "无限制")
	public static boolean existsTable(DatabaseWrapper db, Long user_id, String tableName) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.判断表使用信息是否存在，如果不存在,将加载一次内存..加载完后再检查一次确定是否有表的使用权限
		String tableKey = String.valueOf(user_id).concat(tableName.toUpperCase());
		if (!tableMap.containsKey(tableKey)) {
			initTable(db);
		}
		// 3.返回表使用信息是否存在标志
		return tableMap.containsKey(tableKey);
	}

	@Method(desc = "监测接口是否存在",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.判断接口信息是否存在，如果不存在,将加载一次内存..加载完后再检查一次确定是否有接口的使用权限" +
					"3.返回接口是否存在标志")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "url", desc = "接口请求地址", range = "无限制")
	@Return(desc = "返回接口是否存在标志", range = "无限制")
	public static boolean existsInterface(DatabaseWrapper db, Long user_id, String url) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.判断接口信息是否存在，如果不存在,将加载一次内存..加载完后再检查一次确定是否有接口的使用权限
		String interfaceKey = String.valueOf(user_id).concat(url);
		if (!interfaceMap.containsKey(interfaceKey)) {
			initInterface(db);
		}
		// 3.返回接口是否存在标志
		return interfaceMap.containsKey(interfaceKey);
	}

	@Method(desc = "获取/更新初始化内存的用户信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.查询接口用户信息" +
					"3.判断接口用户信息是否为空" +
					"4.清空上次的用户信息" +
					"5.清空上次的token" +
					"6.清空上次的数据信息,在添加信息的信息" +
					"7.判断密码是否加密" +
					"7.1国密" +
					"7.2用户密码" +
					"8.获取Token信息" +
					"9.封装用户ID，token，查询接口信息到对应集合中")
	private static void userInfo(DatabaseWrapper db) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		// 2.查询接口用户信息
		Result userResult = SqlOperator.queryResult(db, "SELECT user_id,user_password,user_name FROM "
						+ Sys_user.TableName + " WHERE user_type = ? OR usertype_group LIKE ?",
				UserType.RESTYongHu.getCode(), "%" + UserType.RESTYongHu.getCode() + "%");
		// 3.判断接口用户信息是否为空
		if (!userResult.isEmpty()) {
			// 4.清空上次的用户信息
			userSet.clear();
			// 5.清空上次的token
			toKenMap.clear();
			// 6.清空上次的数据信息,在添加信息的信息
			userMap.clear();
			QueryInterfaceInfo queryInterfaceInfo;
			TokenManagerImpl tokenManager = new TokenManagerImpl();
			for (int i = 0; i < userResult.getRowCount(); i++) {
				queryInterfaceInfo = new QueryInterfaceInfo();
				Long user_id = userResult.getLong(i, "user_id");
				queryInterfaceInfo.setUser_id(user_id);
				queryInterfaceInfo.setUser_name(userResult.getString(i, "user_name"));
				String user_password = userResult.getString(i, "user_password");
				queryInterfaceInfo.setUser_password(Base64.getEncoder().encodeToString(user_password.getBytes()));
				// 7.判断密码是否加密
				if (user_password.endsWith("==")) {
					// 7.1国密
					queryInterfaceInfo.setUser_password(new String(Base64.getDecoder().decode(user_password)));
				} else {
					// 7.2用户密码
					queryInterfaceInfo.setUser_password(user_password);
				}
				// 8.获取Token信息
				TokenModel createToken = tokenManager.createToken(db, user_id, user_password);
				queryInterfaceInfo.setToken(createToken.getToken());
				// 9.封装用户ID，token，查询接口信息到对应集合中
				userSet.add(user_id);
				toKenMap.put(createToken.getToken(), queryInterfaceInfo);
				userMap.put(user_id, queryInterfaceInfo);
			}
		}
	}

	@Method(desc = "获取/更新用户的接口使用权限信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.查询接口使用信息" +
					"3.判断接口使用信息是否为空，不为空处理数据" +
					"3.1报表接口会使用,清空报表编码信息" +
					"3.2清空上次的接口信息" +
					"3.3遍历接口使用信息结果集" +
					"3.4判断报表接口代码是否存在，如果存在，替换，不存在新增，封装报表的编码信息" +
					"3.5为了确保key唯一，接口与用户是多对多关系，封装接口信息")
	private static void userInterface(DatabaseWrapper db) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("select url,start_use_date,use_valid_date,user_id,use_state,interface_code," +
				"interface_name,interface_id,interface_use_id from " + Interface_use.TableName
				+ " where ");
		assembler.addORParam("user_id", userSet.toArray(), "");
		// 2.查询接口使用信息
		Result useResult = SqlOperator.queryResult(db, assembler.sql(), assembler.params());
		// 3.判断接口使用信息是否为空，不为空处理数据
		if (!useResult.isEmpty()) {
			// 3.1报表接口会使用,清空报表编码信息
			reportGraphicMap.clear();
			// 3.2清空上次的接口信息
			interfaceMap.clear();
			urlMap.clear();
			QueryInterfaceInfo queryInterfaceInfo;
			List<String> interfaceCodeList;
			List<String> urlList;
			// 3.3遍历接口使用信息结果集，添加新的接口信息
			for (int i = 0; i < useResult.getRowCount(); i++) {
				queryInterfaceInfo = new QueryInterfaceInfo();
				// 用户
				Long user_id = useResult.getLong(i, "user_id");
				queryInterfaceInfo.setUser_id(user_id);
				// 接口URL
				String url = useResult.getString(i, "url");
				queryInterfaceInfo.setUrl(url);
				// 接口开始使用日期
				queryInterfaceInfo.setStart_use_date(useResult.getString(i, "start_use_date"));
				// 接口有效日期
				queryInterfaceInfo.setUse_valid_date(useResult.getString(i, "use_valid_date"));
				// 接口状态
				queryInterfaceInfo.setUse_state(useResult.getString(i, "use_state"));
				// 接口名称
				queryInterfaceInfo.setInterface_name(useResult.getString(i, "interface_name"));
				// 接口ID
				queryInterfaceInfo.setInterface_id(useResult.getString(i, "interface_id"));
				// 接口使用ID
				queryInterfaceInfo.setInterface_use_id(useResult.getString(i, "interface_use_id"));
				// 接口代码
				String interface_code = useResult.getString(i, "interface_code");
				// 3.4判断当前用户报表接口代码是否存在，如果存在，替换，不存在新增，封装报表的编码信息
				if (reportGraphicMap.containsKey(user_id)) {
					reportGraphicMap.get(user_id).add(interface_code);
				} else {
					interfaceCodeList = new ArrayList<>();
					interfaceCodeList.add(interface_code);
					reportGraphicMap.put(user_id, interfaceCodeList);
				}
				// 3.5为了确保key唯一，接口与用户是多对多关系，封装接口信息
				interfaceMap.put(String.valueOf(user_id).concat(url), queryInterfaceInfo);
				// 3.6判断当前用户接口请求地址是否存在，如果存在替换，不存在新增
				if (urlMap.containsKey(String.valueOf(user_id))) {
					urlMap.get(String.valueOf(user_id)).add(url);
				} else {
					urlList = new ArrayList<>();
					urlList.add(url);
					urlMap.put(String.valueOf(user_id), urlList);
				}
			}
		}
	}

	@Method(desc = "获取/更新用户使用表权限信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制" +
					"2.查询表使用信息" +
					"3.判断表使用信息是否为空，不为空处理结果集" +
					"3.1清空上次的表数据信息" +
					"3.2遍历表使用信息" +
					"3.3新增表使用信息")
	private static void userTableInfo(DatabaseWrapper db) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("select * from " + Table_use_info.TableName + " where ");
		assembler.addORParam("user_id", userSet.toArray(), "");
		// 2.查询表使用信息
		Result tableResult = SqlOperator.queryResult(db, assembler.sql(), assembler.params());
		// 3.判断表使用信息是否为空，不为空处理结果集
		if (!tableResult.isEmpty()) {
			// 3.1清空上次的表数据信息
			tableMap.clear();
			QueryInterfaceInfo queryInterfaceInfo;
			// 3.2遍历表使用信息
			for (int i = 0; i < tableResult.getRowCount(); i++) {
				queryInterfaceInfo = new QueryInterfaceInfo();
				// 用户id
				Long user_id = tableResult.getLong(i, "user_id");
				queryInterfaceInfo.setUser_id(user_id);
				// 表名称
				String sysreg_name = tableResult.getString(i, "sysreg_name");
				long use_id = tableResult.getLong(i, "use_id");
				queryInterfaceInfo.setSysreg_name(sysreg_name);
				List<Sysreg_parameter_info> parameterInfos = SqlOperator.queryList(db,
						Sysreg_parameter_info.class,
						"select table_ch_column,table_en_column from " + Sysreg_parameter_info.TableName
								+ " where use_id=? and user_id=?", use_id, user_id);
				StringBuilder chColumns = new StringBuilder();
				StringBuilder enColumns = new StringBuilder();
				if (parameterInfos != null && parameterInfos.size() > 0) {
					for (Sysreg_parameter_info parameterInfo : parameterInfos) {
						chColumns.append(parameterInfo.getTable_ch_column()).append(Constant.METAINFOSPLIT);
						enColumns.append(parameterInfo.getTable_en_column()).append(Constant.METAINFOSPLIT);
					}
					queryInterfaceInfo.setTable_ch_column(chColumns.deleteCharAt(chColumns.length() - 1).toString());
					queryInterfaceInfo.setTable_en_column(enColumns.deleteCharAt(enColumns.length() - 1).toString());
				} else {
					queryInterfaceInfo.setTable_ch_column("");
					queryInterfaceInfo.setTable_en_column("");
				}
				// 表字段列
				// 表remark列(其实存的是字段类型对应的json字符串)
				queryInterfaceInfo.setTable_type_name(tableResult.getString(i, "remark"));
				// 表中文名
				queryInterfaceInfo.setOriginal_name(tableResult.getString(i, "original_name"));
				// 表来源
				queryInterfaceInfo.setTable_blsystem(tableResult.getString(i, "table_blsystem"));
				// 表使用ID
				queryInterfaceInfo.setUse_id(tableResult.getString(i, "use_id"));
				// 3.3新增表使用信息
				tableMap.put(String.valueOf(user_id).concat(sysreg_name.toUpperCase()), queryInterfaceInfo);
			}
		}
	}

}
