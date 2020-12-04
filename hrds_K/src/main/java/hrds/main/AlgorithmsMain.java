package hrds.main;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.Dbm_analysis_conf_tab;
import hrds.commons.entity.Dbm_analysis_schedule_tab;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Stream;

public class AlgorithmsMain {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, String> layerAttr = new HashMap<>();

	static {
		layerAttr.put("ip", "168.168.112.54");
		layerAttr.put("port", "1521");
		layerAttr.put(StorageTypeKey.database_driver, "oracle.jdbc.driver.OracleDriver");
		layerAttr.put(StorageTypeKey.jdbc_url, "jdbc:oracle:thin:@168.168.112.54:1521:ORCL");
		layerAttr.put(StorageTypeKey.user_name, "hyshf");
		layerAttr.put(StorageTypeKey.database_pwd, "hyshf");
		layerAttr.put(StorageTypeKey.database_name, "HYSHF");
		layerAttr.put(StorageTypeKey.database_type, DatabaseType.Oracle10g.getCode());

//		layerAttr.put("ip", "10.180.5.230");
//		layerAttr.put("port", "11254");
//		layerAttr.put(StorageTypeKey.database_driver, "oracle.jdbc.driver.OracleDriver");
//		layerAttr.put(StorageTypeKey.jdbc_url, "jdbc:oracle:thin:@10.180.5.230:11254:ORCL");
//		layerAttr.put(StorageTypeKey.user_name, "hyshf");
//		layerAttr.put(StorageTypeKey.database_pwd, "hyshf");
//		layerAttr.put(StorageTypeKey.database_name, "HYSHF");
//		layerAttr.put(StorageTypeKey.database_type, DatabaseType.Oracle10g.getCode());
	}

	public static void main(String[] args) throws Exception {
		List<String> tableList = readTextFile(args[0]);
		Set<String> validTableSet = new HashSet<>();
		//判断表数据量是否大于10，这里后面是统一配置
		for (String table : tableList) {
			try (DatabaseWrapper databaseWrapper = ConnectionTool.getDBWrapper(layerAttr)) {
				ResultSet resultSet = databaseWrapper.queryGetResultSet("select count(1) as valid_num from "
						+ table + " where rownum <= 10");
				while (resultSet.next()) {
					int valid_num = resultSet.getInt("valid_num");
					if (valid_num > 5) {
						validTableSet.add(table);
					}
				}
			}
		}
		runAlgorithms(validTableSet, args[1]);
	}

	private static void runAlgorithms(Set<String> tableList, String sys_class_code) throws Exception {
		//数据校验
		if (tableList.size() == 0) {
			throw new AppSystemException("表信息列表不能为空!");
		}
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//保存数据分析配置表、分析进度表
			saveDbmAnalysisConfAndScheduleTab(tableList, sys_class_code, db);
//			String[] checkList = {"4"};
//			String[] checkList = {"6"};
			String[] checkList = {"1", "2", "3", "4", "5", "6"};
			//调用数据对标接口或代码
			for (String code : checkList) {
				//遍历所有要进行数据对标的表
				for (String tableName : tableList) {
					//需要分析表所在存储层连接属性
					//获取表字段信息
					List<String> columnList = getTableColumns(tableName);
					//将该表的信息配置填充到Conf
					AlgorithmsConf algorithmsConf = getTableInfoToConf(tableName, sys_class_code, columnList);
					//序列化algorithmsConf类的数据到临时文件夹
					FileUtils.write(new File(Constant.ALGORITHMS_CONF_SERIALIZE_PATH
									+ algorithmsConf.getTable_name()), JSONObject.toJSONString(algorithmsConf),
							StandardCharsets.UTF_8, false);
					switch (code) {
						case "1":
							//字段特征分析
							analysis_feature(sys_class_code, algorithmsConf.getTable_name());
							break;
						case "2":
//							List<String> tables = StringUtil.split("S10_I_AGENT_ACCOUNTS,S10_I_AGENT_ACCOUNTS_RECORD," +
//									"S10_I_ATTACHMENT_FILES,S10_I_AUDIT_VALUATE_COND,S10_I_AUDIT_VALUATE_COND_OPER," +
//									"S10_I_BILL_APP_DETAIL,S10_I_BILL_APP_RANGE,S10_I_BILL_BASE_INFO,S10_I_BILL_REPAIR_INFO," +
//									"S10_I_BLUEPRINT,S10_I_BLUEPRINT_DRAFT,S10_I_CHECK_RESULT_NOT_BALANCE,S10_I_CHOU_ACCT," +
//									"S10_I_CHOU_ACCT_WATER,S10_I_CODE_INFO,S10_I_CPA,S10_I_CPA_HOC,S10_I_CPA_REPORT," +
//									"S10_I_CPA_SECT_DETAIL,S10_I_CPA_SUM,S10_I_CPAY_DETAIL,S10_I_CPAY_SUM,S10_I_CSP,S10_I_CSP_SECT," +
//									"S10_I_CTRADE,S10_I_CTRANIN_UNIT_DETAIL,S10_I_CTRANOUT_HOU_DETAIL,S10_I_CTRANSFER_SUM," +
//									"S10_I_CUNIT,S10_I_CUNIT_ACCT_WATER,S10_I_DEV,S10_I_DIR,S10_I_EASTIMATE_CONTRACT," +
//									"S10_I_EASTIMATE_ORG,S10_I_EASTIMATE_REPORT,S10_I_ESCONTRACT_ITEM,S10_I_ESREPORT_ORGN," +
//									"S10_I_GROUND,S10_I_HOC,S10_I_HOCACCPUBINFO_NEW,S10_I_HOC_ACCT,S10_I_HOCACCTSUM_NEW," +
//									"S10_I_HOC_ACOUNNT_RELATION,S10_I_HOCACTCSPMFPAYDETAIL,S10_I_HOCACTHOCSMFPAYDETAIL," +
//									"S10_I_HOCACTMFEARNDETAILREF,S10_I_HOCARREARAGETOT,S10_I_HOC_DOC_HISTORY,S10_I_HOCMFPAYOUTDETAIL," +
//									"S10_I_HOCMFPAYOUTDETAIL_OTHER,S10_I_HOCPUBACCOUNT_CONTENT,S10_I_HOCPUBINCMSUM," +
//									"S10_I_HOCPUBINCMSUM1,S10_I_HOCPUBINCOME,S10_I_HOCPUBINCOME1,S10_I_HOCPUBLNCFORDETAIL," +
//									"S10_I_HOCTIMEDEPOSITTAKENREF,S10_I_HOCTIMEDEPOSITUNTAKENREF,S10_I_HOCVOTEPROMFPAYDETAIL," +
//									"S10_I_HOCWORKFUNDFORDETAIL,S10_I_HOU_NOTION_SUM,S10_I_HPB,S10_I_HPB_OFF,S10_I_INCREMENT_HOC_ACCT," +
//									"S10_I_INCREMENT_HOC_WATER,S10_I_INCREMENT_HOU_WATER,S10_I_INCREMENT_INFO,S10_I_INCREMENT_OWNER_ACCT," +
//									"S10_I_INCREMENT_TAKEN_DETAIL,S10_I_INCREMENT_VOUCHER,S10_I_INSTALLATION_POSITION," +
//									"S10_I_M_APP_RANGE,S10_I_M_OBJECT_ATTR,S10_I_M_OBJECT_ATTR_OPER,S10_I_M_OBJECT_ITEM," +
//									"S10_I_M_OBJECT_TYPE,S10_I_MO_TYPE_ATTR,S10_I_M_PROJECT_ITEM,S10_I_M_PROJECT_MO,S10_I_M_REPAIR_OBJECT," +
//									"S10_I_OHOC_WATER,S10_I_OHOU_ACCT,S10_I_OHOU_WATER,S10_I_OHOU_WATER_O2O,S10_I_OPAY_SUM,S10_I_OPR_DETAIL," +
//									"S10_I_OTRADE,S10_I_OUNIT,S10_I_PRO,S10_I_PRO_ACCT,S10_I_PRO_ACCT_WATER,S10_I_PROJECT_CONTRACT," +
//									"S10_I_PRO_LICENCE,S10_I_PUB_APP_DETAIL,S10_I_PUB_APP_SUM,S10_I_PUB_BILL_DRAW_DETAIL," +
//									"S10_I_PUBFUNDS_CI,S10_I_PUB_FUNDS_CI_RECORD,S10_I_PUB_ITEM_DETAIL,S10_I_PUB_PRDETAIL," +
//									"S10_I_RESOLUTION_INFO,S10_I_SD_HOC,S10_I_TELLERS,S10_I_T_WORKSPACE,S10_I_UNITFUNDFORSUM," +
//									"S10_I_UNITFUNDPAYOUTDETAIL,S10_I_UNITFUNDPAYOUTDETAIL_OTH,S10_I_WEEK_REPORT,S10_I_WS_PROJECT," +
//									"S10_I_WS_PROJECT_REPORT,S20_I_BXDZ,S20_I_BXDZGL,S20_I_DYBW,S20_I_FH,S20_I_GXRQ,S20_I_JD," +
//									"S20_I_JF,S20_I_JTGS,S20_I_JTWYGX,S20_I_MPZ,S20_I_QX,S20_I_WYGLC,S20_I_WYGS,S20_I_XQ,S20_I_XQJL," +
//									"S20_I_YWH,S20_I_ZRZ,S30_I_ATTACHMENTS,S30_I_CHECK_BIZ,S30_I_CHECK_BIZ_RESULT_ITEM," +
//									"S30_I_CHECK_BIZ_RESULT_SUM,S30_I_CHECK_BIZ_USE_QUOTA_INFO,S30_I_CHECK_CREDIT_RELATE," +
//									"S30_I_CHECK_QUOTA,S30_I_CHECK_QUOTA_ORG_RELATE,S30_I_CHECK_WAY_SECT,S30_I_CODE_INFO," +
//									"S30_I_COMEFROM,S30_I_COMMITTEE,S30_I_COMMUNITYCATEGORY,S30_I_CSP,S30_I_CSP_INFO_COLLECT," +
//									"S30_I_CSP_SECT,S30_I_CSP_SECT_MANGER,S30_I_CX_APPEAL_AUDIT,S30_I_CX_CREDIT_DOCUMENT," +
//									"S30_I_CX_CREDIT_SCORE,S30_I_CX_CREDIT_SCORE_QUOTA,S30_I_CX_CSM_SCORE,S30_I_CX_CSP_SCORE," +
//									"S30_I_CX_SCORE_DETAIL,S30_I_DEV,S30_I_DIR,S30_I_DTSB_ELEVATOR,S30_I_DTSB_M_REPAIR_OBJECT," +
//									"S30_I_ELEVATOR,S30_I_GROUP_FEE,S30_I_HOC,S30_I_HOU,S30_I_HOU_RELATION,S30_I_HPB," +
//									"S30_I_HPB_OFF,S30_I_INDEXS,S30_I_INGLE,S30_I_INGLE_CSP,S30_I_INGLE_TYPE," +
//									"S30_I_INSPECTOR_TEAM,S30_I_LOOP,S30_I_MODETEMP,S30_I_NR_ELEVATOR,S30_I_NR_MONITORING_EQUIPMENT," +
//									"S30_I_NR_PUMP,S30_I_NR_SATELLITE_INFO,S30_I_NR_SECT,S30_I_NR_UNIT,S30_I_OPER_CX_CREDIT_DOCUMENT," +
//									"S30_I_OPER_RECTIFICATION_SUM,S30_I_OPER_RESULT_QUOTA_ITEMS,S30_I_OPER_SECT_PACT_SUM," +
//									"S30_I_ORDER_INFO,S30_I_ORG_INFO,S30_I_PARKING_RATE,S30_I_QCMANAGEMENT,S30_I_QUOTA_ITEMS," +
//									"S30_I_REALTY_GROUP,S30_I_RECTIFICATION_SUM,S30_I_REPORTSOURCE,S30_I_RESULT_COMPLAINTS_ITEMS," +
//									"S30_I_RESULT_QUOTA_ITEMS,S30_I_RP_QUOTA,S30_I_RP_REPORT_ITEM,S30_I_RP_REPORT_MAIN," +
//									"S30_I_RP_REPORT_TOPIC,S30_I_RP_SECTKIND,S30_I_RP_TOPIC_QUOTA", ",");
//							if (tables.contains(tableName)) {
//								LOGGER.info("---------------------函数依赖已经分析过表：" + tableName + "跳过");
//								continue;
//							}
							LOGGER.info("---------------------------------------------开始函数依赖分析表：" + tableName);
							//函数依赖分析
							SparkJobRunner.runJob("hrds.k.biz.algorithms.main.HyFDMain",
									algorithmsConf.getTable_name());
							ImportHyFdData.importDataToDatabase(algorithmsConf, db);
							break;
						case "3":
							LOGGER.info("---------------------------------------------开始主键分析表：" + tableName);
							//主键分析
							SparkJobRunner.runJob("hrds.k.biz.algorithms.main.HyUCCMain",
									algorithmsConf.getTable_name());
							ImportHyUCCData.importDataToDatabase(algorithmsConf, db);
							break;
						case "4":
							//单一、联合主键分析
							//查询表是联合主键还是单一主键
							if (isJoinPk(sys_class_code, algorithmsConf.getTable_name(), db)) {
								analyse_joint_fk(sys_class_code);
							} else {
								analyse_table_fk(sys_class_code, algorithmsConf.getTable_name());
							}
							break;
						case "5":
							//维度划分
							analyse_dim_division();
							break;
						case "6":
							//相等类别分析
							analyse_dim_cluster(sys_class_code);
							break;
					}
				}

			}
		}
	}

	private static boolean isJoinPk(String dsl_name, String table_name, DatabaseWrapper db) throws Exception {
		ResultSet resultSet = db.queryGetResultSet("select * from dbm_joint_pk_tab where " +
				"sys_class_code = ? and table_code = ?", dsl_name, table_name);
		return resultSet.next();
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

	/**
	 * 保存数据分析配置表、分析进度表
	 */
	private static void saveDbmAnalysisConfAndScheduleTab(Set<String> tableList, String sys_class_code,
														  DatabaseWrapper db) {
		for (String tableName : tableList) {
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
	private static void analysis_feature(String dsl_name, String table_name) {
		String url = PropertyParaValue.getString("algorithms_python_serve", "http://127.0.0.1:33333/")
				+ "execute_feature_main";
		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("sys_class_code", dsl_name)
				.addData("table_code", table_name)
				.addData("etl_date", "")
				.addData("date_offset", "")
				.addData("alg", "F5")
				.addData("layerAttr", JSONObject.toJSONString(layerAttr))
				.post(url);
		String bodyString = resVal.getBodyString();
		//4、根据响应状态码判断响应是否成功
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			System.out.println((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
//			throw new AppSystemException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	/**
	 * 单一外键分析
	 */
	private static void analyse_table_fk(String dsl_name, String table_name) {
		String url = PropertyParaValue.getString("algorithms_python_serve", "http://127.0.0.1:33333/")
				+ "execute_analyse_table_fk";
		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("sys_class_code", dsl_name)
				.addData("table_code", table_name)
				.addData("start_date", "")
				.addData("date_offset", "")
				.addData("mode", dsl_name)
				.addData("alg", "F5")
				.addData("layerAttr", JSONObject.toJSONString(layerAttr))
				.post(url);
		//4、根据响应状态码判断响应是否成功
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			System.out.println((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
//			throw new AppSystemException("Agent通讯异常,请检查Agent是否已启动!!!");
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
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			System.out.println((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
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
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			System.out.println((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
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
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			System.out.println((">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage()));
//			throw new AppSystemException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	private static AlgorithmsConf getTableInfoToConf(String hyren_name, String
			sys_class_code, List<String> columnList) {
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
			if (Constant.EDATENAME.equalsIgnoreCase(column_name)) {
				algorithmsConf.setPredicates(new String[]{Constant.EDATENAME + "='" + Constant.MAXDATE + "'"});
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
			String database = db.getDbtype().getDatabase(db, dbMeta);
			rsColumnInfo = dbMeta.getColumns(null, database, hyren_name, "%");
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
