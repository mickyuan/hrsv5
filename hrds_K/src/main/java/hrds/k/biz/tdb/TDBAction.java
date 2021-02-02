package hrds.k.biz.tdb;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.conf.ConnWay;
import fd.ng.db.conf.DbinfosConf;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.meta.MetaOperator;
import fd.ng.db.meta.TableMeta;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.Dbm_analysis_conf_tab;
import hrds.commons.entity.Dbm_analysis_schedule_tab;
import hrds.commons.entity.Dbm_dtable_info;
import hrds.commons.entity.Dbm_normbm_detect;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.TreeData;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.*;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;
import hrds.k.biz.algorithms.conf.AlgorithmsConf;
import hrds.k.biz.algorithms.impl.ImportHyFdData;
import hrds.k.biz.algorithms.impl.ImportHyUCCData;
import hrds.k.biz.algorithms.main.SparkJobRunner;
import hrds.k.biz.tdb.bean.TdbTableBean;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "表数据对标(TableDataBenchmarking)", author = "BY-HLL", createdate = "2020/7/20 0020 上午 09:40")
public class TDBAction extends BaseAction {

	@Method(desc = "获取表数据对标树", logicStep = "获取表数据对标树")
	@Return(desc = "树信息", range = "树信息")
	public List<Node> getTDBTreeData() {
		//配置树不显示文件采集的数据
		TreeConf treeConf = new TreeConf();
		treeConf.setShowFileCollection(Boolean.FALSE);
		//返回分叉树列表
		return TreeData.initTreeData(TreePageSource.DATA_BENCHMARKING, treeConf, getUser());
	}

	@Method(desc = "保存此次对标记录对应的表信息", logicStep = "保存此次对标记录对应的表信息")
	@Param(name = "tdb_table_bean_s", desc = "待保存的表信息Bean数据", range = "TdbTableBean[]", isBean = true)
	@Param(name = "checkList", desc = "数据对标需要做的步骤", range = "TdbTableBean[]")
	@Param(name = "sys_class_code", desc = "数据对标系统分类名称", range = "不可为空")
	@Return(desc = "对标记录id", range = "long类型,唯一")
	public void generateDataBenchmarking(TdbTableBean[] tdb_table_bean_s, String[] checkList,
	                                     String sys_class_code) throws Exception {
		//检查要进行数据对标分析的表是否在同一存储层，是否是数据库类型的表，并返回存储层的信息。
		LayerBean layerBean = checkStoreLayer(tdb_table_bean_s);
		//数据校验
		if (tdb_table_bean_s.length == 0) {
			throw new BusinessException("表信息列表不能为空!");
		}
		//保存数据分析配置表、分析进度表
		saveDbmAnalysisConfAndScheduleTab(tdb_table_bean_s, sys_class_code);
		//调用数据对标接口或代码
		for (String code : checkList) {
			//遍历所有要进行数据对标的表
			for (TdbTableBean tdbTableBean : tdb_table_bean_s) {
				//需要分析表所在存储层连接属性
				Map<String, String> layerAttr = layerBean.getLayerAttr();
				//解析ip和端口
				Map<String, String> jdbcUrlInfo = ConnUtil.getJDBCUrlInfo(layerAttr.get(StorageTypeKey.jdbc_url),
						layerAttr.get(StorageTypeKey.database_type));
				layerAttr.putAll(jdbcUrlInfo);
				//给hive默认的数据库类型
				if (layerAttr.get("store_type") != null && Store_type.HIVE.getCode().equals(layerAttr.get("store_type"))) {
					layerAttr.put(StorageTypeKey.database_type, DatabaseType.Hive.getCode());
				}
				//获取表字段信息
				List<Map<String, Object>> columnByFileId = DataTableUtil.getColumnByFileId(tdbTableBean.getData_layer(),
						Constant.DCL_BATCH, tdbTableBean.getFile_id());
				//将该表的信息配置填充到Conf
				AlgorithmsConf algorithmsConf = getTableInfoToConf(layerAttr,
						tdbTableBean.getHyren_name(), sys_class_code, columnByFileId);
				//序列化algorithmsConf类的数据到临时文件夹
				FileUtils.write(new File(Constant.ALGORITHMS_CONF_SERIALIZE_PATH
								+ algorithmsConf.getTable_name()), JSONObject.toJSONString(algorithmsConf),
						StandardCharsets.UTF_8, false);
				if ("1".equals(code)) {
					//字段特征分析
					analysis_feature(sys_class_code, algorithmsConf.getTable_name(), layerAttr);
				} else if ("2".equals(code)) {
					//函数依赖分析
					SparkJobRunner.runJob("hrds.k.biz.algorithms.main.HyFDMain",
							algorithmsConf.getTable_name());
					ImportHyFdData.importDataToDatabase(algorithmsConf, Dbo.db());
				} else if ("3".equals(code)) {
					//主键分析
					SparkJobRunner.runJob("hrds.k.biz.algorithms.main.HyUCCMain",
							algorithmsConf.getTable_name());
					ImportHyUCCData.importDataToDatabase(algorithmsConf, Dbo.db());
				} else if ("4".equals(code)) {
					//单一、联合主键分析
					//查询表是联合主键还是单一主键
					if (isJoinPk(sys_class_code, algorithmsConf.getTable_name())) {
						analyse_joint_fk(sys_class_code, layerAttr);
					} else {
						analyse_table_fk(sys_class_code, algorithmsConf.getTable_name(), layerAttr);
					}
				} else if ("5".equals(code)) {
					//维度划分
					analyse_dim_division(layerAttr);
				} else if ("6".equals(code)) {
					//相等类别分析
					analyse_dim_cluster(sys_class_code, layerAttr);
				}
			}

		}
	}

	private LayerBean checkStoreLayer(TdbTableBean[] tdb_table_bean_s) {
		LayerBean sameLayerBean = null;
		Map<String, List<LayerBean>> map = new HashMap<>();
		for (TdbTableBean tdbTableBean : tdb_table_bean_s) {
			//获取表存储的存储层信息
			List<LayerBean> layerBeans = ProcessingData.getLayerByTable(tdbTableBean.getHyren_name(), Dbo.db());
			//根据存储层的名称，将存储层放到map中
			for (LayerBean layerBean : layerBeans) {
				if (map.get(layerBean.getDsl_name()) == null) {
					List<LayerBean> putList = new ArrayList<>();
					putList.add(layerBean);
					map.put(layerBean.getDsl_name(), putList);
				} else {
					map.get(layerBean.getDsl_name()).add(layerBean);
				}
			}
		}
		//遍历map,当所选表在同一存储层，且为数据库类型，直接返回，否则继续找
		for (String dsl_name : map.keySet()) {
			List<LayerBean> layerBeans = map.get(dsl_name);
			if (tdb_table_bean_s.length == layerBeans.size() && (Store_type.DATABASE.getCode().
					equals(layerBeans.get(0).getStore_type()) || Store_type.HIVE.getCode().
					equals(layerBeans.get(0).getStore_type()))) {
				sameLayerBean = layerBeans.get(0);
				break;
			}
		}
		if (sameLayerBean == null) {
			throw new BusinessException("你所选的表不在同一数据库类型的存储层中");
		}
		return sameLayerBean;
	}

	/**
	 * 保存数据分析配置表、分析进度表
	 */
	private void saveDbmAnalysisConfAndScheduleTab(TdbTableBean[] tdb_table_bean_s, String sys_class_code) {
		//先清空已有数据
		Dbo.execute("DELETE FROM dbm_analysis_conf_tab WHERE sys_class_code = ?", sys_class_code);
		Dbo.execute("DELETE FROM dbm_analysis_schedule_tab WHERE sys_class_code = ?", sys_class_code);
		for (TdbTableBean tdbTableBean : tdb_table_bean_s) {
			Dbm_analysis_conf_tab analysis_conf_tab = new Dbm_analysis_conf_tab();
			analysis_conf_tab.setSys_class_code(sys_class_code);
			analysis_conf_tab.setOri_table_code(tdbTableBean.getHyren_name());
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
			analysis_conf_tab.add(Dbo.db());
			Dbm_analysis_schedule_tab analysis_schedule_tab = new Dbm_analysis_schedule_tab();
			analysis_schedule_tab.setSys_class_code(sys_class_code);
			analysis_schedule_tab.setOri_table_code(tdbTableBean.getHyren_name());
			analysis_schedule_tab.setFeature_sche("0");
			analysis_schedule_tab.setFd_sche("0");
			analysis_schedule_tab.setPk_sche("0");
			analysis_schedule_tab.setFk_sche("0");
			analysis_schedule_tab.setFd_check_sche("0");
			analysis_schedule_tab.setDim_sche("0");
			analysis_schedule_tab.setIncre_to_full_sche("0");
			analysis_schedule_tab.setJoint_fk_sche("0");
			analysis_schedule_tab.add(Dbo.db());
		}
		Dbo.db().commit();
	}

	private boolean isJoinPk(String dsl_name, String table_name) throws Exception {
		ResultSet resultSet = Dbo.db().queryGetResultSet("select * from dbm_joint_pk_tab where " +
				"sys_class_code = ? and table_code = ?", dsl_name, table_name);
		return resultSet.next();
	}

	private AlgorithmsConf getTableInfoToConf(Map<String, String> layerAttr, String hyren_name, String
			sys_class_code, List<Map<String, Object>> columnByFileId) {
		AlgorithmsConf algorithmsConf = new AlgorithmsConf();
		algorithmsConf.setTable_name(hyren_name);
		algorithmsConf.setSys_code(sys_class_code);
		algorithmsConf.setDatabase_name(layerAttr.get(StorageTypeKey.database_name));
		algorithmsConf.setDriver(layerAttr.get(StorageTypeKey.database_driver));
		algorithmsConf.setJdbcUrl(layerAttr.get(StorageTypeKey.jdbc_url));
		algorithmsConf.setPassword(layerAttr.get(StorageTypeKey.database_pwd));
		algorithmsConf.setUser(layerAttr.get(StorageTypeKey.user_name));
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> columnMap : columnByFileId) {
			String column_name = (String) columnMap.get("column_name");
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

	@Method(desc = "设置对标检测记录", logicStep = "设置对标检测记录")
	private Dbm_normbm_detect setDbmNormbmDetect() {
		//设置 Dbm_normbm_detect
		Dbm_normbm_detect dbm_normbm_detect = new Dbm_normbm_detect();
		dbm_normbm_detect.setDetect_id(PrimayKeyGener.getNextId());
		dbm_normbm_detect.setDetect_name(String.valueOf(dbm_normbm_detect.getDetect_id()));
		dbm_normbm_detect.setDetect_status(DbmState.NotRuning.getCode());
		dbm_normbm_detect.setDbm_mode(DbmMode.BiaoJieGouDuiBiao.getCode());
		dbm_normbm_detect.setCreate_user(getUserId().toString());
		dbm_normbm_detect.setDetect_sdate(DateUtil.getSysDate());
		dbm_normbm_detect.setDetect_stime(DateUtil.getSysTime());
		dbm_normbm_detect.setDetect_edate(DateUtil.getSysDate());
		dbm_normbm_detect.setDetect_etime(DateUtil.getSysTime());
		dbm_normbm_detect.setDnd_remark("");
		return dbm_normbm_detect;
	}


	@Method(desc = "设置对标检测表信息表", logicStep = "设置对标检测表信息表")
	@Param(name = "detect_id", desc = "检测主键id", range = "long类型")
	@Param(name = "file_id", desc = "表源属性id", range = "String类型")
	@Param(name = "data_layer", desc = "数据层", range = "String类型,DCL,DML")
	private Dbm_dtable_info setDbmDtableInfo(long detect_id, String file_id, String data_layer) {
		//数据校验
		Validator.notBlank(data_layer, "表来源数据层信息不能为空");
		//根据表源属性id获取表信息
		Map<String, Object> tableInfo = DataTableUtil.getTableInfoByFileId(data_layer, file_id);
		if (tableInfo.isEmpty()) {
			throw new BusinessException("查询的表信息已经不存在!");
		}
		//设置 Dbm_dtable_info
		Dbm_dtable_info dbm_dtable_info = new Dbm_dtable_info();
		dbm_dtable_info.setDbm_tableid(PrimayKeyGener.getNextId());
		dbm_dtable_info.setTable_cname(tableInfo.get("table_ch_name").toString());
		dbm_dtable_info.setTable_ename(tableInfo.get("table_name").toString());
		DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
		dbm_dtable_info.setSource_type(dataSourceType.getCode());
		//TODO 是否外部表预留,默认给 0: 否
		dbm_dtable_info.setIs_external(IsFlag.Fou.getCode());
		//如果源表的描述为null,则设置为""
		if (null == tableInfo.get("remark")) {
			dbm_dtable_info.setTable_remark("");
		} else {
			dbm_dtable_info.setTable_remark(tableInfo.get("remark").toString());
		}
		dbm_dtable_info.setDetect_id(detect_id);
		dbm_dtable_info.setTable_id(tableInfo.get("table_id").toString());
		//获取表存储的存储层信息
		List<LayerBean> layerBeans = ProcessingData.getLayerByTable(dbm_dtable_info.getTable_ename(), Dbo.db());
		//TODO 如果有多个存储层,去查询结果的第一条
		long dsl_id = layerBeans.get(0).getDsl_id();
		dbm_dtable_info.setDsl_id(dsl_id);
		return dbm_dtable_info;
	}

	/**
	 * 分析字段特征
	 */
	private void analysis_feature(String dsl_name, String table_name, Map<String, String> layerAttr) {
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
			throw new BusinessException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	/**
	 * 单一外键分析
	 */
	private void analyse_table_fk(String dsl_name, String table_name, Map<String, String> layerAttr) {
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
			throw new BusinessException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	/**
	 * 联合外键分析
	 */
	private void analyse_joint_fk(String dsl_name, Map<String, String> layerAttr) {
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
			throw new BusinessException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	/**
	 * 维度划分
	 */
	private void analyse_dim_division(Map<String, String> layerAttr) {
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
			throw new BusinessException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	/**
	 * 字段关系，类别分析
	 */
	private void analyse_dim_cluster(String dsl_name, Map<String, String> layerAttr) {
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
			throw new BusinessException("Agent通讯异常,请检查Agent是否已启动!!!");
		}
	}

	public static void main(String[] args) {
		DbinfosConf.Dbinfo dbInfo = new DbinfosConf.Dbinfo();
		dbInfo.setName(DbinfosConf.DEFAULT_DBNAME);
		dbInfo.setDriver("oracle.jdbc.OracleDriver");
		dbInfo.setUrl("jdbc:oracle:thin:@172.168.0.100:1521:ORCL");
		dbInfo.setUsername("hyshf");
		dbInfo.setPassword("q1w2e3");
		dbInfo.setWay(ConnWay.JDBC);
		//2、获取数据库类型
		Dbtype dbType = Dbtype.ORACLE;
		dbInfo.setDbtype(dbType);
		dbInfo.setShow_conn_time(true);
		dbInfo.setShow_sql(true);

		dbInfo.setDataBaseName("hyshf");


		DatabaseWrapper db = new DatabaseWrapper.Builder().dbconf(dbInfo).create();
		System.out.println(db.getDatabaseName());

		List<TableMeta> tableMetas = MetaOperator.getTablesWithColumns(db, "COMMUNITY_INFO");
		System.out.println(tableMetas.toString());
	}
}
