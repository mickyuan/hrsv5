package hrds.main;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.Dbm_analysis_conf_tab;
import hrds.commons.entity.Dbm_analysis_schedule_tab;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PropertyParaValue;
import hrds.commons.utils.StorageTypeKey;
import hrds.k.biz.algorithms.conf.AlgorithmsConf;
import hrds.k.biz.algorithms.impl.ImportHyFdData;
import hrds.k.biz.algorithms.impl.ImportHyUCCData;
import hrds.k.biz.algorithms.main.SparkJobRunner;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

public class AlgorithmsMain {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Map<String, String> layerAttr = new HashMap<>();

	public static void main(String[] args) throws Exception {
		if (args.length < 6) {
			LOGGER.info("请填写必要参数；参数一：需要分析的表名称的文件全路径；参数二：数据库的连接方式的文件全路径；" +
					"参数三：系统分类编码的名称；参数四：需要进行哪些步骤的分析，多个用英文逗号隔开，如：1,2,3,4,5,6(1：字段特征分析，" +
					"2：函数依赖，3：主键分析，4：单一/联合外键分析，5：维度划分，6：相等类别分析)；参数五：是否重新生成进度表（true或者false）；" +
					"参数六：函数依赖需要跳过的表名称的文件全路径");
			return;
		}
		//获取所有要分析的表
		List<String> lineList = readTextFile(args[0]);
		//获取需要分析的库的连接方式
		setDataBaseProperties(args[1]);
		List<String> validTableSet = new ArrayList<>();
		List<String> etl_datesList = new ArrayList<>();
		List<String> end_dateList = new ArrayList<>();
		ResultSet resultSet = null;
		PreparedStatement curPstmt = null;
		Connection connection = null;
		//判断表数据量是否大于10，这里后面是统一配置
		try {
			connection = ConnectionTool.getDBWrapper(layerAttr).getConnection();
			for (String line : lineList) {
				if (!StringUtil.isEmpty(line)) {
					List<String> splitLine = StringUtil.split(line, "|");
					curPstmt = connection.prepareStatement("select count(1) as valid_num from "
							+ splitLine.get(0) + " where rownum <= 10");
					resultSet = curPstmt.executeQuery();
					while (resultSet.next()) {
						int valid_num = resultSet.getInt("valid_num");
						if (valid_num > 5) {
							validTableSet.add(splitLine.get(0).trim());
							if (splitLine.size() > 2) {
								etl_datesList.add(splitLine.get(1).trim());
								end_dateList.add(splitLine.get(2).trim());
							} else if (splitLine.size() > 1) {
								etl_datesList.add(splitLine.get(1).trim());
								end_dateList.add(" ");
							} else {
								etl_datesList.add(" ");
								end_dateList.add(" ");
							}
						}
					}
					resultSet.close();
					curPstmt.close();
				}
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (curPstmt != null)
				curPstmt.close();
			if (connection != null)
				connection.close();
		}
		//获取函数依赖需要跳过分析的表
		List<String> jumpTableList = readTextFile(args[5]);
		runAlgorithms(validTableSet, args[2], args[3].split(","), Boolean.parseBoolean(args[4]),
				jumpTableList, etl_datesList, end_dateList);
	}

	private static void runAlgorithms(List<String> tableList, String sys_class_code, String[] checkList, boolean flag,
									  List<String> jumpTableList, List<String> etl_datesList, List<String> end_dateList) {
		//数据校验
		if (tableList.size() == 0) {
			throw new AppSystemException("表信息列表不能为空!");
		}
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//保存数据分析配置表、分析进度表
			if (flag) {
				saveDbmAnalysisConfAndScheduleTab(tableList, sys_class_code, etl_datesList, end_dateList, db);
			}
			//调用数据对标接口或代码
			for (String code : checkList) {
				//遍历所有要进行数据对标的表
				//需要分析表所在存储层连接属性
				switch (code) {
					case "1":
						//字段特征分析
						analysis_feature(sys_class_code, tableList, etl_datesList, end_dateList);
						break;
					case "2":
						func_dependency_analysis(sys_class_code, tableList, jumpTableList, db, etl_datesList, end_dateList);
						break;
					case "3":
						pk_analysis(sys_class_code, tableList, db, etl_datesList, end_dateList);
						break;
					case "4":
						//单一外键分析
						analyse_table_fk(sys_class_code, tableList, db, etl_datesList, end_dateList);
						//联合外键分析
						if (isJoinPk(sys_class_code, db)) {
							LOGGER.info("---------------------------------------------开始联合外键分析");
							analyse_joint_fk(sys_class_code);
						}
						break;
					case "5":
						//最后进行维度划分
						LOGGER.info("---------------------------------------------开始进行维度划分");
						analyse_dim_division();
					case "6":
						//最后进行相等类别分析
						LOGGER.info("---------------------------------------------开始进行相等类别分析");
						analyse_dim_cluster(sys_class_code);
				}
			}
		}
	}

	private static boolean isJoinPk(String dsl_name, DatabaseWrapper db) {
		try {
			ResultSet resultSet = db.queryGetResultSet("select * from dbm_joint_pk_tab where " +
					"sys_class_code = ? ", dsl_name);
			return resultSet.next();
		} catch (SQLException e) {
			LOGGER.warn(e);
		}
		return false;
	}

	private static boolean isJoinPk(String dsl_name, DatabaseWrapper db, String table_name) {
		try {
			ResultSet resultSet = db.queryGetResultSet("select * from dbm_joint_pk_tab where " +
					"sys_class_code = ? and table_code = ?", dsl_name, table_name);
			return resultSet.next();
		} catch (SQLException e) {
			LOGGER.warn(e);
		}
		return false;
	}

	public static List<String> readTextFile(String file) {
		List<String> tables = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			stream.forEach(tables::add);//输出重定向
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tables;
	}

	public static void setDataBaseProperties(String file) {
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			stream.forEach(info -> {
				String[] split = info.split("=");
				layerAttr.put(split[0], split[1]);
			});//输出重定向
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存数据分析配置表、分析进度表
	 */
	private static void saveDbmAnalysisConfAndScheduleTab(List<String> tableList, String sys_class_code, List<String>
			etl_datesList, List<String> end_dateList, DatabaseWrapper db) {
		for (int i = 0; i < tableList.size(); i++) {
			String tableName = tableList.get(i);
			//先清空已有数据
			db.execute("DELETE FROM dbm_analysis_conf_tab WHERE sys_class_code = ? and ori_table_code = ?",
					sys_class_code, tableName);
			db.execute("DELETE FROM dbm_analysis_schedule_tab WHERE sys_class_code = ? and ori_table_code = ?",
					sys_class_code, tableName);
			Dbm_analysis_conf_tab analysis_conf_tab = new Dbm_analysis_conf_tab();
			analysis_conf_tab.setSys_class_code(sys_class_code);
			analysis_conf_tab.setOri_table_code(tableName);
			analysis_conf_tab.setAna_alg("F5");
			analysis_conf_tab.setFeature_flag(IsFlag.Shi.getCode());
			analysis_conf_tab.setFd_flag(IsFlag.Shi.getCode());
			analysis_conf_tab.setPk_flag(IsFlag.Shi.getCode());
			analysis_conf_tab.setFk_flag(IsFlag.Shi.getCode());
			analysis_conf_tab.setFd_check_flag(IsFlag.Fou.getCode());
			analysis_conf_tab.setDim_flag(IsFlag.Shi.getCode());
			analysis_conf_tab.setIncre_to_full_flag(IsFlag.Shi.getCode());
			analysis_conf_tab.setJoint_fk_flag(IsFlag.Shi.getCode());
			analysis_conf_tab.setFd_sample_count("all");
			analysis_conf_tab.setJoint_fk_ana_mode("all");
			analysis_conf_tab.setFk_ana_mode("all");
			if (!StringUtil.isEmpty(etl_datesList.get(i))) {
				analysis_conf_tab.setEtl_date_filter(etl_datesList.get(i));
			}
			if (!StringUtil.isEmpty(end_dateList.get(i))) {
				analysis_conf_tab.setEnd_date_filter(end_dateList.get(i));
			}
			analysis_conf_tab.add(db);
			Dbm_analysis_schedule_tab analysis_schedule_tab = new Dbm_analysis_schedule_tab();
			analysis_schedule_tab.setSys_class_code(sys_class_code);
			analysis_schedule_tab.setOri_table_code(tableName);
			analysis_schedule_tab.setFeature_sche("0");
			analysis_schedule_tab.setFd_sche("0");
			analysis_schedule_tab.setPk_sche("0");
			analysis_schedule_tab.setFk_sche("0");
			analysis_schedule_tab.setFd_check_sche("0");
			analysis_schedule_tab.setDim_sche("0");
			analysis_schedule_tab.setIncre_to_full_sche("0");
			analysis_schedule_tab.setJoint_fk_sche("0");
			analysis_schedule_tab.add(db);
		}
		db.commit();
	}

	/**
	 * 分析字段特征
	 */
	private static void analysis_feature(String dsl_name, List<String> tableList, List<String> etl_datesList,
										 List<String> end_dateList) {
		String url = PropertyParaValue.getString("algorithms_python_serve", "http://127.0.0.1:33333/")
				+ "execute_feature_main";
		for (int i = 0; i < tableList.size(); i++) {
			LOGGER.info("---------------------------------------------开始字段特征分析表: " + tableList.get(i));
			try {
				//3、httpClient发送请求并接收响应
				HttpClient.ResponseValue resVal = new HttpClient()
						.addData("sys_class_code", dsl_name)
						.addData("table_code", tableList.get(i))
						.addData("etl_date", etl_datesList.get(i))
						.addData("end_date", end_dateList.get(i))
						.addData("alg", "F5")
						.addData("layerAttr", JSONObject.toJSONString(layerAttr))
						.post(url);
				String bodyString = resVal.getBodyString();
				//4、根据响应状态码判断响应是否成功
				ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
						.orElseThrow(() -> new AppSystemException("连接" + url + "服务异常"));
				//5、若响应不成功，记录日志，并抛出异常告知操作失败
				if (!ar.isSuccess()) {
					LOGGER.info((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
				}
			} catch (Exception e) {
				LOGGER.warn(tableList.get(i) + "分析出现错误，跳过");
			}
		}
	}

	/**
	 * 表内函数依赖分析
	 */
	private static void func_dependency_analysis(String sys_class_code, List<String>
			tableList, List<String> jumpTableList, DatabaseWrapper db, List<String> etl_datesList,
												 List<String> end_dateList) {
		for (int i = 0; i < tableList.size(); i++) {
			String tableName = tableList.get(i);
			LOGGER.info("---------------------------------------------开始函数依赖分析表: " + tableName);
			try {
				if (jumpTableList.contains(tableName)) {
					break;
				}
				//获取表字段信息
				//将该表的信息配置填充到Conf
				AlgorithmsConf algorithmsConf = getTableInfoToConf(tableName, sys_class_code,
						getTableColumns(tableName), etl_datesList.get(i), end_dateList.get(i));
				//序列化algorithmsConf类的数据到临时文件夹
				FileUtils.write(new File(Constant.ALGORITHMS_CONF_SERIALIZE_PATH
								+ algorithmsConf.getTable_name()), JSONObject.toJSONString(algorithmsConf),
						StandardCharsets.UTF_8, false);
				//函数依赖分析
				SparkJobRunner.runJob("hrds.k.biz.algorithms.main.HyFDMain",
						algorithmsConf.getTable_name());
				ImportHyFdData.importDataToDatabase(algorithmsConf, db);
			} catch (Exception e) {
				LOGGER.warn(tableName + "分析出现错误，跳过");
			}
		}
	}

	/**
	 * 主键分析
	 */
	private static void pk_analysis(String sys_class_code, List<String> tableList, DatabaseWrapper db,
									List<String> etl_datesList, List<String> end_dateList) {
		for (int i = 0; i < tableList.size(); i++) {
			String tableName = tableList.get(i);
			LOGGER.info("---------------------------------------------开始主键分析: " + tableName);
			try {
				//获取表字段信息
				//将该表的信息配置填充到Conf
				AlgorithmsConf algorithmsConf = getTableInfoToConf(tableName, sys_class_code, getTableColumns(tableName),
						etl_datesList.get(i), end_dateList.get(i));
				//序列化algorithmsConf类的数据到临时文件夹
				FileUtils.write(new File(Constant.ALGORITHMS_CONF_SERIALIZE_PATH
								+ algorithmsConf.getTable_name()), JSONObject.toJSONString(algorithmsConf),
						StandardCharsets.UTF_8, false);
				//主键分析
				SparkJobRunner.runJob("hrds.k.biz.algorithms.main.HyUCCMain",
						algorithmsConf.getTable_name());
				ImportHyUCCData.importDataToDatabase(algorithmsConf, db);
			} catch (Exception e) {
				LOGGER.warn(tableName + "分析出现错误，跳过");
			}
		}
	}

	/**
	 * 单一外键分析
	 */
	private static void analyse_table_fk(String dsl_name, List<String> tableList, DatabaseWrapper db, List<String> etl_datesList,
										 List<String> end_dateList) {
		String url = PropertyParaValue.getString("algorithms_python_serve", "http://127.0.0.1:33333/")
				+ "execute_analyse_table_fk";
		for (int i = 0; i < tableList.size(); i++) {
			String tableName = tableList.get(i);
			if (!isJoinPk(dsl_name, db, tableName)) {
				LOGGER.info("---------------------------------------------开始单一外键分析表: " + tableName);
				try {
					//3、httpClient发送请求并接收响应
					HttpClient.ResponseValue resVal = new HttpClient()
							.addData("sys_class_code", dsl_name)
							.addData("table_code", tableName)
							.addData("etl_date", etl_datesList.get(i))
							.addData("end_date", end_dateList.get(i))
							.addData("mode", dsl_name)
							.addData("alg", "F5")
							.addData("layerAttr", JSONObject.toJSONString(layerAttr))
							.post(url);
					//4、根据响应状态码判断响应是否成功
					ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
							.orElseThrow(() -> new AppSystemException("连接" + url + "服务异常"));
					//5、若响应不成功，记录日志，并抛出异常告知操作失败
					if (!ar.isSuccess()) {
						LOGGER.info((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
					}
				} catch (Exception e) {
					LOGGER.warn(tableName + "单一外键分析出现错误，跳过");
				}
			}
		}
	}

	/**
	 * 联合外键分析
	 */
	private static void analyse_joint_fk(String dsl_name) {
		String url = PropertyParaValue.getString("algorithms_python_serve", "http://127.0.0.1:33333/")
				+ "execute_joint_fk_main";
		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("main_table_code", dsl_name)
				.addData("sub_sys_class_code", dsl_name)
				.addData("layerAttr", JSONObject.toJSONString(layerAttr))
				.post(url);
		//4、根据响应状态码判断响应是否成功
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new AppSystemException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			LOGGER.info((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
//			throw new AppSystemException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	/**
	 * 维度划分
	 */
	private static void analyse_dim_division() {
		String url = PropertyParaValue.getString("algorithms_python_serve", "http://127.0.0.1:33333/")
				+ "execute_dim_division_main";
		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("layerAttr", JSONObject.toJSONString(layerAttr))
				.post(url);
		//4、根据响应状态码判断响应是否成功
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new AppSystemException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			LOGGER.info((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
//			throw new AppSystemException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	/**
	 * 字段关系，类别分析
	 */
	private static void analyse_dim_cluster(String dsl_name) {
		String url = PropertyParaValue.getString("algorithms_python_serve", "http://127.0.0.1:33333/")
				+ "execute_run_dim_cluster_main";
		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("sys_class_code", dsl_name)
				.addData("layerAttr", JSONObject.toJSONString(layerAttr))
				.post(url);
		//4、根据响应状态码判断响应是否成功
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new AppSystemException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			LOGGER.info((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
//			throw new AppSystemException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	private static AlgorithmsConf getTableInfoToConf(String hyren_name, String
			sys_class_code, List<String> columnList, String etl_dates, String end_date) {
		AlgorithmsConf algorithmsConf = new AlgorithmsConf();
		algorithmsConf.setTable_name(hyren_name);
		algorithmsConf.setSys_code(sys_class_code);
		algorithmsConf.setDatabase_name(layerAttr.get(StorageTypeKey.database_name));
		algorithmsConf.setDriver(layerAttr.get(StorageTypeKey.database_driver));
		algorithmsConf.setJdbcUrl(layerAttr.get(StorageTypeKey.jdbc_url));
		algorithmsConf.setPassword(layerAttr.get(StorageTypeKey.database_pwd));
		algorithmsConf.setUser(layerAttr.get(StorageTypeKey.user_name));
		StringBuilder sb = new StringBuilder();
		for (String column_name : columnList) {
			if (!(Constant.SDATENAME.equalsIgnoreCase(column_name)
					|| Constant.EDATENAME.equalsIgnoreCase(column_name)
					|| Constant.MD5NAME.equalsIgnoreCase(column_name)
					|| Constant.HYREN_OPER_DATE.equalsIgnoreCase(column_name)
					|| Constant.HYREN_OPER_PERSON.equalsIgnoreCase(column_name)
					|| Constant.HYREN_OPER_TIME.equalsIgnoreCase(column_name))) {
				sb.append(column_name).append(",");
			}
			if (!StringUtil.isEmpty(etl_dates) && !StringUtil.isEmpty(end_date)) {
				List<String> split = StringUtil.split(etl_dates, "=");
				StringBuilder sb2 = new StringBuilder();
				sb2.append(split.get(0)).append(" IN (");
				for (String value : StringUtil.split(split.get(1), ",")) {
					sb2.append("'").append(value).append("'").append(",");
				}
				sb2.delete(sb2.length() - 1, sb2.length());
				sb2.append(")");
				List<String> split2 = StringUtil.split(end_date, "=");
				algorithmsConf.setPredicates(new String[]{sb2.toString() + " AND " + split2.get(0) + "='" + split2.get(1) + "'"});
			} else if (!StringUtil.isEmpty(etl_dates)) {
				List<String> split = StringUtil.split(etl_dates, "=");
				StringBuilder sb2 = new StringBuilder();
				sb2.append(split.get(0)).append(" IN (");
				for (String value : StringUtil.split(split.get(1), ",")) {
					sb2.append("'").append(value).append("'").append(",");
				}
				sb2.delete(sb2.length() - 1, sb2.length());
				sb2.append(")");
				algorithmsConf.setPredicates(new String[]{sb2.toString()});
			} else if (!StringUtil.isEmpty(end_date)) {
				List<String> split2 = StringUtil.split(end_date, "=");
				algorithmsConf.setPredicates(new String[]{split2.get(0) + "='" + split2.get(1) + "'"});
			}
		}
		sb.delete(sb.length() - 1, sb.length());
		algorithmsConf.setSelectColumnArray(sb.toString().split(","));
		algorithmsConf.setOutputFilePath(CommonVariables.ALGORITHMS_RESULT_ROOT_PATH + "/" + hyren_name + "/");
		return algorithmsConf;
	}

	private static List<String> getTableColumns(String hyren_name) {
		List<String> columns = new ArrayList<>();
		ResultSet rsColumnInfo;
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(layerAttr)) {
			DatabaseMetaData dbMeta = db.getConnection().getMetaData();
//			String database = db.getDbtype().getDatabase(db, dbMeta);
			if (hyren_name.contains(".")) {
				List<String> split = StringUtil.split(hyren_name, ".");
				rsColumnInfo = dbMeta.getColumns(null, split.get(0), split.get(1), "%");
			} else {
				rsColumnInfo = dbMeta.getColumns(null, "%", hyren_name, "%");
			}
			while (rsColumnInfo.next()) {
				String colName = rsColumnInfo.getString("COLUMN_NAME");
				columns.add(colName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columns;
	}


}
