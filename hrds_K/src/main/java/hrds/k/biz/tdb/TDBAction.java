package hrds.k.biz.tdb;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.conf.ConnWay;
import fd.ng.db.conf.DbinfosConf;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.meta.MetaOperator;
import fd.ng.db.meta.TableMeta;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.*;
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
import hrds.k.biz.tdb.bean.NodeRelationBean;
import hrds.k.biz.tdb.bean.TdbTableBean;
import hrds.k.biz.utils.Neo4jUtils;
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

	@Method(desc = "获取数据对标分析的表主键信息", logicStep = "获取数据对标分析的表主键信息")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "表主键信息", range = "表主键信息")
	public Map<String, Object> getPageTablePkData(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT " +
				"  table_code, " +
				"  col_code, " +
				"  col_type, " +
				"  case col_nullable when '0' then '否' else '是' end as col_nullable, " +
				"  case col_pk when '0' then '否' else '是' end as col_pk, " +
				" row_number() over (partition BY table_code ORDER BY col_num) as col_num " +
				" FROM " + Dbm_mmm_field_info_tab.TableName);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
		}
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> tablePkData = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> tablePkDataMap = new HashMap<>();
		tablePkDataMap.put("tablePkData", tablePkData);
		tablePkDataMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return tablePkDataMap;
	}

	@Method(desc = "获取数据对标分析的表联合主键信息", logicStep = "获取数据对标分析的表联合主键信息")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "表联合主键信息", range = "表联合主键信息")
	public Map<String, Object> getPageTableJoinPkData(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" row_number() over(partition BY table_code ORDER BY group_code) col_num," +
				" table_code," +
				" string_agg(col_code,',') AS join_pk_col_code," +
				" group_code" +
				" FROM " + Dbm_joint_pk_tab.TableName);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
		}
		asmSql.addSql(" GROUP BY table_code,group_code");
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> tableJoinPkData = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> tableJoinPkDataMap = new HashMap<>();
		tableJoinPkDataMap.put("tableJoinPkData", tableJoinPkData);
		tableJoinPkDataMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return tableJoinPkDataMap;
	}

	@Method(desc = "获取数据对标分析的表函数依赖的信息", logicStep = "获取数据对标分析的表函数依赖的信息")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "表函数依赖的信息", range = "表函数依赖的信息")
	public Map<String, Object> getPageTableFuncDepData(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" row_number() over(partition BY table_code ORDER BY LENGTH(right_columns)-LENGTH(REPLACE" +
				" (right_columns,',','')) DESC,LENGTH(left_columns)-LENGTH(REPLACE(left_columns,',','')) ) AS" +
				" row_num," +
				" table_code," +
				" left_columns," +
				" right_columns" +
				" FROM" +
				" (" +
				" SELECT" +
				" string_agg(right_columns,',') AS right_columns," +
				" table_code," +
				" left_columns" +
				" FROM " + Dbm_function_dependency_tab.TableName);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
		}
		asmSql.addSql(" GROUP BY" +
				" table_code," +
				" left_columns) temp_dep");
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> tableFuncDepData = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> tableFuncDepDataMap = new HashMap<>();
		tableFuncDepDataMap.put("tableFuncDepData", tableFuncDepData);
		tableFuncDepDataMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return tableFuncDepDataMap;
	}

	@Method(desc = "获取数据对标分析的表字段外键信息", logicStep = "获取数据对标分析的表字段外键信息")
	@Param(name = "fk_table_code", desc = "主表表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "表字段外键信息", range = "表字段外键信息")
	public Map<String, Object> getPageTableFkData(String fk_table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" fk_table_code ," +
				" fk_col_code ," +
				" table_code," +
				" col_code," +
				" row_number() over(partition BY fk_table_code ORDER BY col_code) row_num" +
				" FROM " + Dbm_fk_info_tab.TableName);
		if (StringUtil.isNotBlank(fk_table_code)) {
			asmSql.addLikeParam("fk_table_code", "%" + fk_table_code + "%", "WHERE");
		}
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> tableFkData = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> tableFkDataMap = new HashMap<>();
		tableFkDataMap.put("tableFkData", tableFkData);
		tableFkDataMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return tableFkDataMap;
	}

	@Method(desc = "获取数据对标字段相等类别分析结果", logicStep = "获取数据对标字段相等类别分析结果")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "字段相等类别分析结果", range = "字段相等类别分析结果")
	public Map<String, Object> getPageFieldSameResult(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" dim_order," +
				" table_code," +
				" col_code," +
				" category_same," +
				" rel_type" +
				" FROM " + Dbm_field_same_result.TableName
		);
		if (StringUtil.isNotBlank(table_code)) {
			if (table_code.contains("=")) {
				List<String> split = StringUtil.split(table_code, "=");
				if ("class".equals(split.get(0).trim()) && StringUtil.isNotBlank(split.get(1))) {
					asmSql.addSql("WHERE category_same = " + split.get(1));
				} else if (StringUtil.isNotBlank(split.get(1))) {
					asmSql.addLikeParam("table_code", "%" + split.get(1) + "%", "WHERE");
				}
			} else {
				asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
			}
		}
		asmSql.addSql(" ORDER BY" +
				" category_same," +
				" dim_order");
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> fieldSameResult = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> fieldSameResultMap = new HashMap<>();
		fieldSameResultMap.put("fieldSameResult", fieldSameResult);
		fieldSameResultMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return fieldSameResultMap;
	}

	@Method(desc = "获取数据对标字段特征分析结果", logicStep = "获取数据对标字段特征分析结果")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "字段特征分析结果", range = "字段特征分析结果")
	public Map<String, Object> getColumnFeatureAnalysisResult(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" table_code," +
				" col_code," +
				" col_records," +
				" col_distinct," +
				" max_len," +
				" min_len," +
				" avg_len," +
				" skew_len," +
				" kurt_len," +
				" median_len," +
				" var_len," +
				"case when has_chinese = '0' then '否' else '是' end as has_chinese," +
				"case when tech_cate = '1' then '日期' when tech_cate = '2' then '金额' when tech_cate = '3' then " +
				"'码值' when tech_cate = '4' then '数值' when tech_cate = '5' then '费率' else 'UNK' end as tech_cate" +
				" FROM " + Dbm_feature_tab.TableName
		);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
		}
		asmSql.addSql(" ORDER BY table_code");
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> columnFeatureAnalysisResult = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> columnFeatureAnalysisResultMap = new HashMap<>();
		columnFeatureAnalysisResultMap.put("columnFeatureAnalysisResult", columnFeatureAnalysisResult);
		columnFeatureAnalysisResultMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return columnFeatureAnalysisResultMap;
	}

	@Method(desc = "自定义图计算查询语句查询", logicStep = "")
	@Param(name = "cypher", desc = "查询语句", range = "不能为空")
	@Return(desc = "", range = "")
	public List<NodeRelationBean> searchFromNeo4j(String cypher) {
		Validator.notBlank(cypher, "查询语句不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			return example.searchFromNeo4j(cypher);
		}
	}

	@Method(desc = "LPA社区发现算法", logicStep = "")
	@Param(name = "relationship", desc = "页面传参边的属性", range = "（FK、FD、EQUALS、SAME、BDF）")
	@Param(name = "iterations", desc = "算法迭代次数", range = "不能为空")
	@Param(name = "limitNum", desc = "查询前多少条", range = "可为空，为空则表示查询全部数据", nullable = true)
	@Return(desc = "", range = "")
	public List<Map<String, Object>> searchLabelPropagation(String relationship, int iterations, String limitNum) {
		Validator.notBlank(relationship, "页面传参边的属性不能为空");
		Validator.notNull(iterations, "算法迭代次数不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			return example.searchLabelPropagation(relationship, iterations, limitNum);
		}
	}

	@Method(desc = "查询字段外键关系的图", logicStep = "")
	@Param(name = "limitNum", desc = "查询多少条", range = "无限制")
	@Return(desc = "", range = "")
	public List<NodeRelationBean> searchColumnOfFkRelation(String limitNum) {
		try (Neo4jUtils example = new Neo4jUtils()) {
			return example.searchColumnOfFkRelation(limitNum);
		}
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
