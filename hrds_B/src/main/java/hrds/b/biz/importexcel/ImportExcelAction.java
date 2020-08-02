package hrds.b.biz.importexcel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import hrds.b.biz.agent.CheckParam;
import hrds.b.biz.agent.dbagentconf.startwayconf.StartWayConfAction;
import hrds.b.biz.agent.tools.ConnUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Job_Status;
import hrds.commons.codes.Pro_Type;
import hrds.commons.codes.UnloadType;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_datatable_source;
import hrds.commons.entity.Etl_dependency;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_sub_sys_list;
import hrds.commons.entity.Etl_sys;
import hrds.commons.entity.Source_relation_dep;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Take_relation_etl;
import hrds.commons.entity.fdentity.ProjectTableEntity.EntityDealZeroException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.ExcelUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@DocClass(desc = "上传Excel的文件导入功能", author = "Mr.Lee", createdate = "2020-07-23 14:16")
public class ImportExcelAction extends BaseAction {

	@Method(desc = "通过上传的Excel导入数据信息", logicStep = "")
	@Param(name = "file", desc = "上传的文件", range = "不可为空,否则你要导入什么数据呢?")
	@Param(name = "upload", nullable = true, valueIfNull = "false", desc = "是否为上传,还是审计", range = "可为空,此时默认为false")
	@UploadFile
	public Map<Object, Object> importDatabaseByExcel(String file, String upload) {

		Workbook workbookFromExcel = null;
		File uploadedFile = null;
		Map<Object, Object> uploadData = new HashMap<>();
		try {
			boolean isUpload = Boolean.parseBoolean(upload);
			uploadedFile = FileUploadUtil.getUploadedFile(file);
			workbookFromExcel = ExcelUtil.getWorkbookFromExcel(uploadedFile);
			DatabaseWrapper db = Dbo.db();

			//---------------------------第一个Sheet页------------------------------
			Sheet sheet = workbookFromExcel.getSheetAt(0);
			//1: 处理第一个Sheet页的部门数据信息
			Department_info department_info = saveDeptInfo(sheet, db, isUpload);
			//2: 处理第一个Sheet页的数据源信息
			Data_source data_source = saveDataSource(sheet, department_info, db, isUpload);
			//3: 处理第一个Sheet页的Agent信息
			Agent_info agent_info = saveAgentInfo(sheet, data_source, db, isUpload);
			//4: 处理第一个Sheet页的分类信息
			Collect_job_classify classify = saveClassifyInfo(sheet, agent_info, db, isUpload);
			//5: 处理第一个Sheet页的任务信息
			Database_set database_set = saveDatabaseSetInfo(sheet, classify, agent_info, db, isUpload);

			//---------------------------第二个Sheet页------------------------------
			sheet = workbookFromExcel.getSheetAt(1);
			//6: 处理第二个Sheet页的表信息
			Map<String, Object> tableMap = saveTableInfo(sheet, database_set, db, isUpload);
			uploadData.put("table", tableMap);

			//---------------------------第三个Sheet页------------------------------
			sheet = workbookFromExcel.getSheetAt(2);
			//7: 生成作业数据信息
			if (isUpload) {
				//如果审计通过时,开始保存数据信息
				saveJobDefInfo(sheet, database_set.getDatabase_id(), data_source.getSource_id(), db,
					agent_info.getUser_id());
			}
			//8: 找到此次表更新影响的后续表信息
			if (!isUpload) {
				List<Object> tableList = (List<Object>) tableMap.get("tableList");
				Map<Object, List<Map<String, Object>>> jobInfo = getJobInfo(tableList, database_set.getDatabase_id(), sheet);
				uploadData.put("etlJob", jobInfo);

				Map<Object, List<Map<String, Object>>> dclTable = getDclTable(tableList, database_set.getDatabase_id(),
					agent_info.getAgent_id(), data_source.getSource_id());
				uploadData.put("dclTable", dclTable);
			}

		} catch (IOException e) {
			CheckParam.throwErrorMsg("未找到上传递的文件(%s)信息", FileUploadUtil.getOriginalFileName(file));
		} finally {
			if (workbookFromExcel != null) {
				//关闭打开的流
				ExcelUtil.close(workbookFromExcel);
				//删除上传后的文件
				uploadedFile.delete();
			}
		}
		return uploadData;
	}

	@Method(desc = "保存部门信息", logicStep = ""
		+ "1: 检查需要创建的部门是否存在 "
		+ "2:如果存在则返回部门ID信息,否则创建部门信息并返回ID信息")
	@Param(name = "sheet", desc = "Excel的页码", range = "不可为空")
	@Return(desc = "返回部门数据信息", range = "不可为空")
	Department_info saveDeptInfo(Sheet sheet, DatabaseWrapper db, boolean isUpload) {

		Row row = sheet.getRow(2);
		Department_info department_info = new Department_info();
		//部门名称
		String dep_name = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(dep_name, "部门名称不能为空");
		department_info.setDep_name(dep_name);
		//描述
		String dep_remark = String.valueOf(ExcelUtil.getValue(row.getCell(4)));
		department_info.setDep_remark(dep_remark);

		// 1: 检查需要创建的部门是否存在
		Optional<Department_info> queryResult = Dbo
			.queryOneObject(Department_info.class, "SELECT * FROM " + Department_info.TableName + " WHERE dep_name = ?",
				dep_name);
		;
		//2:如果存在则返回部门ID信息,否则创建部门信息并返回ID信息
		if (queryResult.isEmpty()) {
			department_info.setDep_id(PrimayKeyGener.getNextId());
			department_info.setCreate_date(DateUtil.getSysDate());
			department_info.setCreate_time(DateUtil.getSysTime());
			if (isUpload) {
				department_info.add(db);
			}
		} else {
			try {
				department_info.setDep_id(queryResult.get().getDep_id());
				if (isUpload) {
					department_info.update(db);
				}
			} catch (Exception e) {
				if (!(e instanceof EntityDealZeroException)) {
					throw new BusinessException(e.getMessage());
				}
			}
		}
		return department_info;
	}

	@Method(desc = "保存数据源信息", logicStep = ""
		+ "1: 保存数据源信息..有则更新,没有新增 "
		+ "2: 保存数据源和部门之间的关系,如果存在了就不在添加")
	@Param(name = "sheet", desc = "Excel的页码", range = "不可为空")
	@Param(name = "dept", desc = "部门信息", range = "不可为空")
	@Return(desc = "返回数据源信息", range = "不可为空")
	Data_source saveDataSource(Sheet sheet, Department_info dept, DatabaseWrapper db, boolean isUpload) {

		Row row = sheet.getRow(5);
		//设置获取数据信息
		Data_source data_source = new Data_source();
		//数据源编号
		String datasource_number = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(datasource_number, "数据源编号不能为空");
		data_source.setDatasource_number(datasource_number);
		//数据源名称
		String datasource_name = ExcelUtil.getValue(row.getCell(4)).toString();
		Validator.notBlank(datasource_name, "数据源名称不能为空");
		data_source.setDatasource_name(datasource_name);
		row = sheet.getRow(6);
		//数据源描述
		String source_remark = ExcelUtil.getValue(row.getCell(2)).toString();
		data_source.setSource_remark(source_remark);

		//查询此次添加的数据信息是否已经存在
		Optional<Data_source> queryResult = Dbo
			.queryOneObject(Data_source.class, "SELECT * FROM " + Data_source.TableName + " WHERE datasource_number = ?",
				datasource_number);
		//1: 保存数据源信息..有则更新,没有新增
		if (queryResult.isEmpty()) {
			data_source.setSource_id(PrimayKeyGener.getNextId());
			data_source.setCreate_date(DateUtil.getSysDate());
			data_source.setCreate_time(DateUtil.getSysTime());
			data_source.setCreate_user_id(getUserId());
			if (isUpload) {
				data_source.add(db);
			}
		} else {
			try {
				data_source.setSource_id(queryResult.get().getSource_id());
				if (isUpload) {
					data_source.update(db);
				}
			} catch (Exception e) {
				if (!(e instanceof EntityDealZeroException)) {
					throw new BusinessException(e.getMessage());
				}
			}
		}

		//2: 保存数据源和部门之间的关系,如果存在了就不在添加
		if (Dbo.queryNumber("SELECT COUNT(1) FROM " + Source_relation_dep.TableName + " WHERE dep_id = ? AND source_id = ?",
			dept.getDep_id(), data_source.getSource_id()).orElseThrow(() -> new BusinessException("获取部门和数据源关系失败")) == 0) {
			Source_relation_dep source_relation_dep = new Source_relation_dep();
			source_relation_dep.setDep_id(dept.getDep_id());
			source_relation_dep.setSource_id(data_source.getSource_id());
			if (isUpload) {
				source_relation_dep.add(Dbo.db());
			}
		}

		return data_source;

	}

	@Method(desc = "保存Agent信息", logicStep = "有则更新,没有新增")
	@Param(name = "sheet", desc = "Excel的页码", range = "不可为空")
	@Param(name = "data_source", desc = "数据源信息", range = "不可为空")
	@Return(desc = "返回Agent信息", range = "不可为空")
	Agent_info saveAgentInfo(Sheet sheet, Data_source data_source, DatabaseWrapper db, boolean isUpload) {
		Row row = sheet.getRow(9);
		//设置Agent数据信息
		Agent_info agent_info = new Agent_info();
		//数据源ID
		agent_info.setSource_id(data_source.getSource_id());
		//Agent名称
		String agent_name = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(agent_name, "Agent名称不能为空");
		agent_info.setAgent_name(agent_name);
		//Agent类别
		String agent_type = ExcelUtil.getValue(row.getCell(4)).toString();
		Validator.notBlank(agent_type, "Agent类型不能为空");
		if (agent_type.equals(AgentType.ShuJuKu.getValue())) {
			agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
		} else if (agent_type.equals(AgentType.DBWenJian.getValue())) {
			agent_info.setAgent_type(AgentType.DBWenJian.getCode());
		} else if (agent_type.equals(AgentType.WenJianXiTong.getValue())) {
			agent_info.setAgent_type(AgentType.WenJianXiTong.getCode());
		} else if (agent_type.equals(AgentType.DuiXiang.getValue())) {
			agent_info.setAgent_type(AgentType.DuiXiang.getCode());
		} else if (agent_type.equals(AgentType.FTP.getValue())) {
			agent_info.setAgent_type(AgentType.FTP.getCode());
		} else {
			throw new BusinessException("请选择正确的Agent类型");
		}
		row = sheet.getRow(10);
		String agent_ip = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(agent_ip, "AgentIp不能为空");
		agent_info.setAgent_ip(agent_ip);

		String agent_port = ExcelUtil.getValue(row.getCell(4)).toString();
		Validator.notBlank(agent_port, "Agent端口不能为空");
		agent_info.setAgent_port(agent_port);

		row = sheet.getRow(11);
		String user_id = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(user_id, "Agent所属用户不能为空");
		agent_info.setUser_id(user_id);

		Optional<Agent_info> queryResult = Dbo.queryOneObject(Agent_info.class,
			"SELECT * FROM " + Agent_info.TableName + " WHERE agent_name = ? AND source_id =? AND agent_type = ?",
			agent_name, data_source.getSource_id(), agent_info.getAgent_type());

		if (queryResult.isEmpty()) {
			agent_info.setAgent_id(PrimayKeyGener.getNextId());
			agent_info.setAgent_status(IsFlag.Fou.getCode());
			agent_info.setCreate_date(DateUtil.getSysDate());
			agent_info.setCreate_time(DateUtil.getSysTime());
			if (isUpload) {
				agent_info.add(db);
			}
		} else {
			try {
				agent_info.setAgent_id(queryResult.get().getAgent_id());
				if (isUpload) {
					agent_info.update(db);
				}
			} catch (Exception e) {
				if (!(e instanceof EntityDealZeroException)) {
					throw new BusinessException(e.getMessage());
				}
			}
		}

		return agent_info;
	}

	@Method(desc = "保存分类信息", logicStep = "有则更新,没有新增")
	@Param(name = "sheet", desc = "Excel的页码", range = "不可为空")
	@Param(name = "agent_info", desc = "Agent信息", range = "不可为空")
	@Return(desc = "返回分类信息", range = "不可为空")
	Collect_job_classify saveClassifyInfo(Sheet sheet, Agent_info agent_info, DatabaseWrapper db, boolean isUpload) {

		Row row = sheet.getRow(14);
		//设置分类信息
		Collect_job_classify classify = new Collect_job_classify();
		classify.setUser_id(getUserId());
		classify.setAgent_id(agent_info.getAgent_id());
		//分类编号
		String classify_num = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(classify_num, "分类编号不能为空");
		classify.setClassify_num(classify_num);
		//分类名称
		String classify_name = ExcelUtil.getValue(row.getCell(4)).toString();
		Validator.notBlank(classify_name, "分类名称不能为空");
		classify.setClassify_name(classify_name);
		row = sheet.getRow(15);
		//备注
		String remark = ExcelUtil.getValue(row.getCell(2)).toString();
		classify.setRemark(remark);

		Optional<Collect_job_classify> queryClassify = Dbo.queryOneObject(Collect_job_classify.class,
			"SELECT * FROM " + Collect_job_classify.TableName + " WHERE classify_num = ? AND agent_id =?", classify_num,
			agent_info.getAgent_id());

		if (queryClassify.isEmpty()) {
			classify.setClassify_id(PrimayKeyGener.getNextId());
			if (isUpload) {
				classify.add(db);
			}
		} else {
			try {
				classify.setClassify_id(queryClassify.get().getClassify_id());
				if (isUpload) {
					classify.update(db);
				}
			} catch (Exception e) {
				if (!(e instanceof EntityDealZeroException)) {
					throw new BusinessException(e.getMessage());
				}
			}
		}

		return classify;
	}

	@Method(desc = "保存分类信息", logicStep = "有则更新,没有新增")
	@Param(name = "sheet", desc = "Excel的页码", range = "不可为空")
	@Param(name = "agent_info", desc = "Agent信息", range = "不可为空")
	@Param(name = "classify", desc = "分类信息信息", range = "不可为空")
	@Return(desc = "返回任务信息", range = "不可为空")
	Database_set saveDatabaseSetInfo(Sheet sheet, Collect_job_classify classify, Agent_info agent_info,
		DatabaseWrapper db, boolean isUpload) {

		//采集任务信息
		Database_set database_set = new Database_set();
		database_set.setClassify_id(classify.getClassify_id());
		database_set.setAgent_id(agent_info.getAgent_id());

		Row row = sheet.getRow(18);
		//数据采集任务名
		String task_name = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(task_name, "任务名称不能为空");
		database_set.setTask_name(task_name);
		//采集作业名称
		String database_number = ExcelUtil.getValue(row.getCell(4)).toString();
		Validator.notBlank(database_number, "采集作业名称不能为空");
		database_set.setDatabase_number(database_number);
		row = sheet.getRow(19);
		//数据库类型
		String databaseType = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(databaseType, "数据库类型不能为空");
		String database_type = null;
		if (databaseType.equals(DatabaseType.MYSQL.getValue())) {
			database_type = DatabaseType.MYSQL.getCode();
		} else if (databaseType.equals(DatabaseType.Oracle9i.getValue())) {
			database_type = DatabaseType.Oracle9i.getCode();
		} else if (databaseType.equals(DatabaseType.Oracle10g.getValue())) {
			database_type = DatabaseType.Oracle10g.getCode();
		} else if (databaseType.equals(DatabaseType.SqlServer2000.getValue())) {
			database_type = DatabaseType.SqlServer2000.getCode();
		} else if (databaseType.equals(DatabaseType.SqlServer2005.getValue())) {
			database_type = DatabaseType.SqlServer2005.getCode();
		} else if (databaseType.equals(DatabaseType.DB2.getValue())) {
			database_type = DatabaseType.DB2.getCode();
		} else if (databaseType.equals(DatabaseType.SybaseASE125.getValue())) {
			database_type = DatabaseType.SybaseASE125.getCode();
		} else if (databaseType.equals(DatabaseType.Informatic.getValue())) {
			database_type = DatabaseType.Informatic.getCode();
		} else if (databaseType.equals(DatabaseType.H2.getValue())) {
			database_type = DatabaseType.H2.getCode();
		} else if (databaseType.equals(DatabaseType.ApacheDerby.getValue())) {
			database_type = DatabaseType.ApacheDerby.getCode();
		} else if (databaseType.equals(DatabaseType.Postgresql.getValue())) {
			database_type = DatabaseType.Postgresql.getCode();
		} else if (databaseType.equals(DatabaseType.GBase.getValue())) {
			database_type = DatabaseType.GBase.getCode();
		} else if (databaseType.equals(DatabaseType.TeraData.getValue())) {
			database_type = DatabaseType.TeraData.getCode();
		} else if (databaseType.equals(DatabaseType.Hive.getValue())) {
			database_type = DatabaseType.Hive.getCode();
		} else {
			CheckParam.throwErrorMsg("请选择正确的数据库类型");
		}
		database_set.setDatabase_type(database_type);

		//设置JDBC的Driver信息
		database_set.setDatabase_drive(ConnUtil.getJDBCDriver(database_type));

		//设置任务是否贴源登记为否
		database_set.setIs_reg(IsFlag.Fou.getCode());

		//数据库名称
		String database_name = ExcelUtil.getValue(row.getCell(4)).toString();
		Validator.notBlank(database_name, "数据库名称不能为空");
		database_set.setDatabase_name(database_name);
		row = sheet.getRow(20);
		//数据库IP
		String database_ip = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(database_ip, "数据库IP不能为空");
		database_set.setDatabase_ip(database_ip);
		//数据库端口
		String database_port = ExcelUtil.getValue(row.getCell(4)).toString();
		Validator.notBlank(database_port, "数据库端口不能为空");
		database_set.setDatabase_port(database_port);
		row = sheet.getRow(21);
		//数据库用户名称
		String database_user = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(database_user, "数据库用户名称不能为空");
		database_set.setUser_name(database_user);
		//数据库用户密码
		String database_pwd = ExcelUtil.getValue(row.getCell(4)).toString();
		Validator.notBlank(database_pwd, "数据库用户密码不能为空");
		database_set.setDatabase_pad(database_pwd);
		row = sheet.getRow(22);
		//JDBC的连接信息
		String jdbc_url = ExcelUtil.getValue(row.getCell(2)).toString();
		Validator.notBlank(database_pwd, "JDBC的连接信息不能为空");
		database_set.setJdbc_url(jdbc_url);

		Optional<Database_set> queryDatabase = Dbo.queryOneObject(Database_set.class,
			"SELECT * FROM " + Database_set.TableName
				+ " WHERE agent_id = ? AND classify_id = ? AND task_name = ? AND database_number = ?",
			agent_info.getAgent_id(), classify.getClassify_id(), task_name, database_number);

		if (queryDatabase.isEmpty()) {
			//设置默认的是否为DB采集
			database_set.setDb_agent(IsFlag.Fou.getCode());
			//设置默认的是否发送完成
			database_set.setIs_sendok(IsFlag.Fou.getCode());
			//设置主键
			database_set.setDatabase_id(PrimayKeyGener.getNextId());
			//设置任务默认清洗顺序
			database_set.setCp_or(Constant.DATABASE_CLEAN.toJSONString());
			if (isUpload) {
				database_set.add(db);
			}
		} else {
			try {
				database_set.setDatabase_id(queryDatabase.get().getDatabase_id());
				if (isUpload) {
					database_set.update(db);
				}
			} catch (Exception e) {
				if (!(e instanceof EntityDealZeroException)) {
					throw new BusinessException(e.getMessage());
				}
			}
		}

		return database_set;
	}

	Map<String, Object> saveTableInfo(Sheet sheet, Database_set database_set, DatabaseWrapper db, boolean isUpload) {
		//第二页的总行数
		int lastRowNum = sheet.getLastRowNum();
		//记录更新的表及原表信息
//		List<Map<String, Object>> updateDataList = new ArrayList<>();
		Row row;
		Table_info table_info;
		Map<String, Object> updateDataMaps = new HashMap<>();
		List<String> tableList = new ArrayList<>();
		for (int i = 1; i <= lastRowNum; i++) {
			JSONObject updateDataObj = new JSONObject(true);
			//-------------------------------表数据处理结束----------------------------------
			table_info = new Table_info();
			row = sheet.getRow(i);
			//表名
			String table_name = ExcelUtil.getValue(row.getCell(0)).toString();
			Validator.notBlank(table_name, "第" + i + "行,表名不能为空");
			table_info.setTable_name(table_name);
			//表中文名
			String table_ch_name = ExcelUtil.getValue(row.getCell(1)).toString();
			Validator.notBlank(table_ch_name, "第" + i + "行,表中文名不能为空");
			table_info.setTable_ch_name(table_ch_name);
			//设置默认的是否已登记
			table_info.setIs_register(IsFlag.Shi.getCode());
			//是否sql抽取设置为默认的否
			table_info.setIs_user_defined(IsFlag.Fou.getCode());
			//卸数方式
			String unload_type = ExcelUtil.getValue(row.getCell(2)).toString();
			Validator.notBlank(unload_type, "第" + i + "行,卸数方式不能为空");
			if (unload_type.equals(UnloadType.ZengLiangXieShu.getValue())) {
				table_info.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
				//增量SQL
				String sql = ExcelUtil.getValue(row.getCell(7)).toString();
				Validator.notBlank(sql, "第" + i + "行,增量SQL不能为空");
				table_info.setSql(sql);
				//因为增量情况下不需要设置并行抽取,所以这里直接设置为否
				table_info.setIs_parallel(IsFlag.Fou.getCode());
				//设置默认的自定义SQL方式为否
				table_info.setIs_customize_sql(IsFlag.Fou.getCode());
			} else if (unload_type.equals(UnloadType.QuanLiangXieShu.getValue())) {
				table_info.setUnload_type(UnloadType.QuanLiangXieShu.getCode());
				//如果卸数方式是全量,则检查是否并行抽取选项
				String is_parallel = ExcelUtil.getValue(row.getCell(3)).toString();
				Validator.notBlank(is_parallel, "第" + i + "行,是否并行抽取不能为空");
				//如果是并行抽取,需要根据自定义SQL的方式来获取是分页SQL还是数据量的方式
				if (is_parallel.equals(IsFlag.Shi.getValue())) {
					table_info.setIs_parallel(IsFlag.Shi.getCode());
					//自定义SQL
					String is_customize_sql = ExcelUtil.getValue(row.getCell(4)).toString();
					Validator.notBlank(is_customize_sql, "第" + i + "行,自定义SQL不能为空");
					//如果并行抽取为是,则获取是否自定义SQL,如果是自定义SQL则获取分页SQL,反之获取数据量
					if (is_customize_sql.equals(IsFlag.Shi.getValue())) {
						//设置自定义SQL默认值
						table_info.setIs_customize_sql(IsFlag.Shi.getCode());
						//自定义SQL-分页SQL
						String page_sql = ExcelUtil.getValue(row.getCell(5)).toString();
						Validator.notBlank(page_sql, "第" + i + "行,分页SQL不能为空");
						table_info.setPage_sql(page_sql);
						//设置数据总量
						table_info.setTable_count("0");
						//设置每日数据量
						table_info.setDataincrement(0);
						//设置分页并行数
						table_info.setPageparallels(0);
					} else if (is_customize_sql.equals(IsFlag.Fou.getValue())) {
						//设置自定义SQL默认值
						table_info.setIs_customize_sql(IsFlag.Fou.getCode());
						//自定义SQL-数据量
						String customizeData = ExcelUtil.getValue(row.getCell(6)).toString();
						Validator.notBlank(customizeData, "第" + i + "行,自定义数据量不能为空");
						if (!customizeData.contains("|")) {
							CheckParam.throwErrorMsg("第" + i + "行,自定义数据量分隔符不正确");
						}
						List<String> customizeDataList = StringUtil.split(customizeData, "|");
						if (customizeDataList.size() != 3) {
							CheckParam.throwErrorMsg("第" + i + "行,自定义数据量参数不正确");
						}
						//设置数据总量
						table_info.setTable_count(customizeDataList.get(0));
						//设置每日数据量
						table_info.setDataincrement(customizeDataList.get(1));
						//设置分页并行数
						table_info.setPageparallels(customizeDataList.get(2));
						//并将分页SQL设置为空
						table_info.setPage_sql("");
					} else {
						CheckParam.throwErrorMsg("第" + i + "行,请选择正确的自定义SQL方式");
					}
				} else if (is_parallel.equals(IsFlag.Fou.getValue())) {
					//设置自定义SQL默认值
					table_info.setIs_customize_sql(IsFlag.Fou.getCode());
					table_info.setIs_parallel(IsFlag.Fou.getCode());
					//设置数据总量
					table_info.setTable_count("0");
					//设置每日数据量
					table_info.setDataincrement(0);
					//设置分页并行数
					table_info.setPageparallels(0);
					//并将分页SQL设置为空
					table_info.setPage_sql("");
				} else {
					CheckParam.throwErrorMsg("第" + i + "行,请选择正确的卸数方式");
				}
			} else {
				CheckParam.throwErrorMsg("第" + i + "行,请选择正确的并行抽取");
			}

			//是否计算md5
			String md5 = ExcelUtil.getValue(row.getCell(8)).toString();
			Validator.notBlank(md5, "第" + i + "行,是否计算MD5不能为空");
			if (md5.equals(IsFlag.Shi.getValue())) {
				table_info.setIs_md5(IsFlag.Shi.getCode());
			} else if (md5.equals(IsFlag.Fou.getValue())) {
				table_info.setIs_md5(IsFlag.Fou.getCode());
			} else {
				CheckParam.throwErrorMsg("第" + i + "行,请选择是否计算MD5");
			}

			Optional<Table_info> queryTableInfo = Dbo.queryOneObject(Table_info.class,
				"SELECT * FROM " + Table_info.TableName + " WHERE database_id = ? AND table_name = ?",
				database_set.getDatabase_id(), table_name);
			//表的新增是新增,那么表的字段信息肯定也是新增,表的抽取方式也是新增
			if (queryTableInfo.isEmpty()) {
				//设置默认的数据日期
				table_info.setRec_num_date(DateUtil.getSysDate());
				//新增表的主键
				table_info.setTable_id(PrimayKeyGener.getNextId());
				//表清洗顺序
				table_info.setTi_or(Constant.DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
				//设置表的开始日期
				table_info.setValid_s_date(DateUtil.getSysDate());
				//设置表的有效日期
				table_info.setValid_e_date(Constant.MAXDATE);
				//设置表的任务ID
				table_info.setDatabase_id(database_set.getDatabase_id());
				if (isUpload) {
					table_info.add(db);
				}
				//记录新增的表信息
				updateDataObj.put("addTableName新增表名", table_info.getTable_name());
				updateDataObj.put("addTableChName新增表中文名", table_info.getTable_ch_name());
				updateDataObj
					.put("addUnloadType新增卸数方式", UnloadType.ofValueByCode(table_info.getUnload_type()));
			} else {
				try {
					//更新表信息
					table_info.setTable_id(queryTableInfo.get().getTable_id());
					if (isUpload) {
						table_info.update(db);
					}
					//说明更新到表信息了,则记录下来
					//记录新增的表信息
					if (!queryTableInfo.get().getTable_ch_name().equals(table_info.getTable_ch_name())) {
						updateDataObj.put("originTableName原表中文名", queryTableInfo.get().getTable_ch_name());
						updateDataObj.put("updateTableChName更新后表中文名", table_info.getTable_ch_name());
					}
					if (!queryTableInfo.get().getUnload_type().equals(table_info.getUnload_type())) {
						updateDataObj
							.put("originUnloadType原卸数方式", UnloadType.ofValueByCode(queryTableInfo.get().getUnload_type()));
						updateDataObj.put("updateUnloadType更新后卸数方式", UnloadType.ofValueByCode(table_info.getUnload_type()));
					}
				} catch (Exception e) {
					if (!(e instanceof EntityDealZeroException)) {
						throw new BusinessException(e.getMessage());
					}
				}
			}
			//-------------------------------表数据处理结束----------------------------------

			//-------------------------------表字段开始----------------------------------------
			tableColumnData(row, i, table_info, db, updateDataObj, isUpload);
			//-------------------------------表字段结束----------------------------------------

			//-------------------------------数据抽取定义开始----------------------------------
			Data_extraction_def extraction_def = new Data_extraction_def();
			//设置数据文件源头
			extraction_def.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			//设置是否有表头
			String is_header = ExcelUtil.getValue(row.getCell(9)).toString();
			Validator.notBlank(is_header, "第" + i + "行,是否有表头不能为空");
			if (is_header.equals(IsFlag.Shi.getValue())) {
				extraction_def.setIs_header(IsFlag.Shi.getCode());
			} else if (is_header.equals(IsFlag.Fou.getValue())) {
				extraction_def.setIs_header(IsFlag.Shi.getCode());
			} else {
				CheckParam.throwErrorMsg("第" + i + "行,请选择正确的是否有表头信息");
			}
			//设置数据抽取落地编码
			String database_code = ExcelUtil.getValue(row.getCell(10)).toString();
			Validator.notBlank(database_code, "第" + i + "行,数据字符集不能为空");
			if (database_code.equals(DataBaseCode.UTF_8.getValue())) {
				extraction_def.setDatabase_code(DataBaseCode.UTF_8.getCode());
			} else if (database_code.equals(DataBaseCode.GBK.getValue())) {
				extraction_def.setDatabase_code(DataBaseCode.GBK.getCode());
			} else if (database_code.equals(DataBaseCode.UTF_16.getValue())) {
				extraction_def.setDatabase_code(DataBaseCode.UTF_16.getCode());
			} else if (database_code.equals(DataBaseCode.GB2312.getValue())) {
				extraction_def.setDatabase_code(DataBaseCode.GB2312.getCode());
			} else if (database_code.equals(DataBaseCode.ISO_8859_1.getValue())) {
				extraction_def.setDatabase_code(DataBaseCode.ISO_8859_1.getCode());
			} else {
				CheckParam.throwErrorMsg("第" + i + "行,请选择正确的数据字符集信息");
			}
			//文件抽取格式
			String dbFile_format = ExcelUtil.getValue(row.getCell(11)).toString();
			Validator.notBlank(dbFile_format, "第" + i + "行,文件抽取格式不能为空");
			if (dbFile_format.equals(FileFormat.CSV.getValue())) {
				extraction_def.setDbfile_format(FileFormat.CSV.getCode());
			} else if (dbFile_format.equals(FileFormat.FeiDingChang.getValue())) {
				extraction_def.setDbfile_format(FileFormat.FeiDingChang.getCode());
			} else if (dbFile_format.equals(FileFormat.DingChang.getValue())) {
				extraction_def.setDbfile_format(FileFormat.DingChang.getCode());
			} else if (dbFile_format.equals(FileFormat.SEQUENCEFILE.getValue())) {
				extraction_def.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
			} else if (dbFile_format.equals(FileFormat.ORC.getValue())) {
				extraction_def.setDbfile_format(FileFormat.ORC.getCode());
			} else if (dbFile_format.equals(FileFormat.PARQUET.getValue())) {
				extraction_def.setDbfile_format(FileFormat.PARQUET.getCode());
			} else {
				CheckParam.throwErrorMsg("第" + i + "行,请选择正确的文件抽取格式信息");
			}

			//设置文件储存路径
			String plane_url = ExcelUtil.getValue(row.getCell(12)).toString();
			Validator.notBlank(plane_url, "第" + i + "行,数据落地目录不能为空");
			extraction_def.setPlane_url(plane_url);

			//设置行分隔符,列分隔符
			if (dbFile_format.equals(FileFormat.CSV.getValue()) || dbFile_format.equals(FileFormat.DingChang.getValue())
				|| dbFile_format.equals(FileFormat.FeiDingChang.getValue())) {
				//设置行分隔符
				String row_separator = ExcelUtil.getValue(row.getCell(13)).toString();
				Validator.notBlank(row_separator, "第" + i + "行,行分隔符不能为空");
				extraction_def.setRow_separator(StringUtil.string2Unicode(row_separator));
				//设置列分隔符
				String database_separator = ExcelUtil.getValue(row.getCell(14)).toString();
				Validator.notBlank(database_separator, "第" + i + "行数据分隔符不能为空");
				extraction_def.setDatabase_separatorr(StringUtil.string2Unicode(database_separator));
			} else {
				//设置行分隔符
				extraction_def.setRow_separator("");
				//设置列分隔符
				extraction_def.setDatabase_separatorr("");
			}
			//设置默认的数据转存为否
			extraction_def.setIs_archived(IsFlag.Fou.getCode());
			Optional<Data_extraction_def> queryData = Dbo.queryOneObject(Data_extraction_def.class,
				"SELECT * FROM " + Data_extraction_def.TableName + " WHERE table_id = ? ",
				table_info.getTable_id());
//			updateDataObj.put("tableName表名", table_info.getTable_name());
			if (queryData.isEmpty()) {
				extraction_def.setDed_id(PrimayKeyGener.getNextId());
				extraction_def.setTable_id(table_info.getTable_id());
				updateDataObj.put("addTableChName新增表中文名", table_info.getTable_ch_name());
				updateDataObj
					.put("addTableType新增表落地方式", FileFormat.ofValueByCode(extraction_def.getDbfile_format()));
				if (isUpload) {
					extraction_def.add(db);
				}
			} else {
				try {
					extraction_def.setDed_id(queryData.get().getDed_id());
					//如果抽取方式改变了,则放入
					if (!extraction_def.getDbfile_format().equals(queryData.get().getDbfile_format())) {
						updateDataObj.put("updateType更新后表落地方式",
							FileFormat.ofValueByCode(extraction_def.getDbfile_format()));
						updateDataObj.put("originType原表落地方式",
							FileFormat.ofValueByCode(queryData.get().getDbfile_format()));
					}
					if (isUpload) {
						extraction_def.update(db);
					}
				} catch (Exception e) {
					if (!(e instanceof EntityDealZeroException)) {
						throw new BusinessException(e.getMessage());
					}
				}
			}
			//-------------------------------数据抽取定义结束----------------------------------
			if (!updateDataObj.isEmpty()) {
				updateDataMaps.put(table_name, updateDataObj);
				tableList.add(table_name);
			}
		}
		updateDataMaps.put("tableList", tableList);
//		updateDataList.add(updateDataMaps);

		return updateDataMaps;
	}

	@Method(desc = "保存表的列数据信息", logicStep = "")
	@Param(name = "row", desc = "Excel的行", range = "可以为空,也许啥也没填")
	@Param(name = "rowNum", desc = "Excel的行数", range = "不可以为空,最起码有一行吧")
	@Param(name = "table_info", desc = "表的数据信息", range = "不可以为空,不然表都没有哪来的列?")
	@Param(name = "db", desc = "数据的DB连接", range = "不可以为空")
	@Return(desc = "", range = "")
	void tableColumnData(Row row, int rowNum, Table_info table_info, DatabaseWrapper db,
		JSONObject updateDataMap, boolean isUpload) {
		//获取表的列字段信息
		String columns = ExcelUtil.getValue(row.getCell(15)).toString();
		Validator.notBlank(columns, "第" + rowNum + "行,字段信息未填写");
		if (!columns.contains("|")) {
			CheckParam.throwErrorMsg("第" + rowNum + "行,字段分隔符填写不正确");
		}
		List<String> columnList = StringUtil.split(columns, "|");
		//获取表的列字段类型
		String columnTypes = ExcelUtil.getValue(row.getCell(16)).toString();
		Validator.notBlank(columnTypes, "第" + rowNum + "行,列字段类型未填写");
		if (!columnTypes.contains("|")) {
			CheckParam.throwErrorMsg("第" + rowNum + "行,字段分隔符填写不正确");
		}
		List<String> columnTypeList = StringUtil.split(columnTypes, "|");
		//获取表的主键信息
		String primaryData = ExcelUtil.getValue(row.getCell(17)).toString();
		Validator.notBlank(primaryData, "第" + rowNum + "行,主键信息未填写");
		if (!primaryData.contains("|")) {
			CheckParam.throwErrorMsg("第" + rowNum + "行,主键分割符填写不正确");
		}
		List<String> primaryList = StringUtil.split(primaryData, "|");

		if (columnTypeList.size() != columnList.size()) {
			CheckParam.throwErrorMsg("第" + rowNum + "行,字段数量(%s)和字段类型数量(%s)不一致", columnList.size(), columnTypeList.size());
		}

		if (primaryList.size() != columnTypeList.size()) {
			CheckParam.throwErrorMsg("第" + rowNum + "行,字段类型数量(%s)和主键数量(%s)不一致", columnTypeList.size(), primaryList.size());
		}

		if (table_info.getUnload_type().equals(UnloadType.ZengLiangXieShu.getCode())) {
			if (!primaryList.contains(IsFlag.Shi.getCode())) {
				CheckParam.throwErrorMsg("第" + rowNum + "行,表(%s)的卸数方式为增量,未设置主键信息", table_info.getTable_name());
			}
		}

		JSONArray array = new JSONArray();
		Table_column table_column = new Table_column();
		for (int i = 0; i < columnList.size(); i++) {
			JSONObject column = new JSONObject(true);
			//设置字段是否获取
			table_column.setIs_get(IsFlag.Shi.getCode());
			//设置字段名称
			table_column.setColumn_name(columnList.get(i));
			//设置字段的中文名称
			table_column.setColumn_ch_name(columnList.get(i));
			//设置字段的类型
			table_column.setColumn_type(columnTypeList.get(i));
			//设置字段是否为新增
			table_column.setIs_new(IsFlag.Fou.getCode());
			//设置字段是否为主键
			table_column.setIs_primary_key(primaryList.get(i));

			Optional<Table_column> queryColumn = Dbo.queryOneObject(Table_column.class,
				"SELECT * FROM " + Table_column.TableName + " WHERE table_id = ? AND column_name = ?",
				table_info.getTable_id(), columnList.get(i));
			if (queryColumn.isEmpty()) {
				//设置列的主键信息
				table_column.setColumn_id(PrimayKeyGener.getNextId());
				//设置列的默认清洗顺序
				table_column.setTc_or(Constant.DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());
				//设置字段的开始日期
				table_column.setValid_s_date(DateUtil.getSysDate());
				//设置字段的有效日期
				table_column.setValid_e_date(Constant.MAXDATE);
				//设置字段的表主键
				table_column.setTable_id(table_info.getTable_id());
				//保存
				if (isUpload) {
					table_column.add(db);
				}
				//新增的字段
				column.put("addColumnName字段", columnList.get(i));
				column.put("addColumn新增字段中文名称", table_column.getColumn_ch_name());
				column.put("addColumnType新增字段类型", table_column.getColumn_type());
			} else {
				try {
					table_column.setColumn_id(queryColumn.get().getColumn_id());
					if (isUpload) {
						table_column.update(db);
					}
					if (!table_column.getColumn_ch_name().equals(queryColumn.get().getColumn_ch_name())) {
						column.put("columnName字段", columnList.get(i));
						column.put("originColumnCh原字段中文名称", queryColumn.get().getColumn_ch_name());
						column.put("updateColumn更新字段中文名称", table_column.getColumn_ch_name());
					}
					if (!table_column.getColumn_type().equals(queryColumn.get().getColumn_type())) {
						column.put("columnName字段", columnList.get(i));
						column.put("originColumnType原字段类型", queryColumn.get().getColumn_type());
						column.put("updateColumnType更新字段类型", table_column.getColumn_type());
					}
				} catch (Exception e) {
					if (!(e instanceof EntityDealZeroException)) {
						throw new BusinessException(e.getMessage());
					}
				}
			}
			if (!column.isEmpty()) {
				array.add(column);
			}
		}
		if (!array.isEmpty()) {
			updateDataMap.put("columnInfo", array);
		}
	}

	void saveJobDefInfo(Sheet sheet, long database_id, long source_id, DatabaseWrapper db, long user_id) {

		Row row = sheet.getRow(1);

		//获取启动方式
		String startType = ExcelUtil.getValue(row.getCell(0)).toString();
		if (startType.equals(IsFlag.Shi.getValue())) {
			//立即启动
		} else if (startType.equals(IsFlag.Fou.getValue())) {
			//生成作业信息

			//------------------------作业工程信息-----------------------
			Etl_sys etl_sys = new Etl_sys();
			//获取作业工程编号
			String etl_sys_cd = ExcelUtil.getValue(row.getCell(2)).toString();
			Validator.notBlank(etl_sys_cd, "作业工程编号不能为空");
			//获取作业工程描述
			String etl_sys_name = ExcelUtil.getValue(row.getCell(3)).toString();
			Validator.notBlank(etl_sys_name, "作业工程描述不能为空");
			//工程名称
			etl_sys.setEtl_sys_name(StringUtil.isBlank(etl_sys_name) ? etl_sys_cd : etl_sys_name);
			//用户ID
			etl_sys.setUser_id(user_id);
			//创建作业工程信息
			Optional<Etl_sys> queryEtlSys = Dbo
				.queryOneObject(Etl_sys.class,
					"SELECT * FROM " + Etl_sys.TableName + " WHERE etl_sys_cd = ? AND user_id = ?", etl_sys_cd,
					user_id);
			if (queryEtlSys.isEmpty()) {
				//工程编号
				etl_sys.setEtl_sys_cd(etl_sys_cd);
				//工程运行状态
				etl_sys.setSys_run_status(Job_Status.STOP.getCode());
				//跑批日期
				etl_sys.setCurr_bath_date(DateUtil.getSysDate());
				//新增工程信息
				etl_sys.add(db);
			} else {
				try {
					etl_sys.setEtl_sys_cd(queryEtlSys.get().getEtl_sys_cd());
					etl_sys.update(db);
				} catch (Exception e) {
					if (!(e instanceof EntityDealZeroException)) {
						throw new BusinessException(e.getMessage());
					}
				}
			}

			//------------------------作业任务信息-----------------------
			//创建作业任务信息
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			//获取作业任务编号
			String sub_sys_cd = ExcelUtil.getValue(row.getCell(4)).toString();
			Validator.notBlank(sub_sys_cd, "作业任务编号不能为空");
			//获取作业任务名称
			String sub_sys_desc = ExcelUtil.getValue(row.getCell(5)).toString();
			Validator.notBlank(sub_sys_desc, "作业任务名称不能为空");
			etl_sub_sys_list.setSub_sys_desc(sub_sys_desc);

			Optional<Etl_sub_sys_list> querySubSys = Dbo
				.queryOneObject(Etl_sub_sys_list.class,
					"SELECT * FROM " + Etl_sub_sys_list.TableName + " WHERE etl_sys_cd = ? AND sub_sys_cd = ?",
					etl_sys_cd, sub_sys_cd);

			if (querySubSys.isEmpty()) {
				//工程编号
				etl_sub_sys_list.setEtl_sys_cd(etl_sys_cd);
				//工程任务编号
				etl_sub_sys_list.setSub_sys_cd(sub_sys_cd);
				//新增工程任务信息
				etl_sub_sys_list.add(db);
			} else {
				try {
					etl_sub_sys_list.setEtl_sys_cd(querySubSys.get().getEtl_sys_cd());
					etl_sub_sys_list.setSub_sys_cd(querySubSys.get().getSub_sys_cd());
					etl_sub_sys_list.update(db);
				} catch (Exception e) {
					if (!(e instanceof EntityDealZeroException)) {
						throw new BusinessException(e.getMessage());
					}
				}
			}

			//------------------------作业信息-----------------------
			//作业程序目录
			String pro_dic = ExcelUtil.getValue(row.getCell(6)).toString();
			Validator.notBlank(pro_dic, "作业程序目录不能为空");
			//作业日志目录
			String log_dic = ExcelUtil.getValue(row.getCell(7)).toString();
			Validator.notBlank(log_dic, "作业日志目录不能为空");
			//获取任务下的作业信息
			StartWayConfAction startWayConfAction = new StartWayConfAction();
			List<Map<String, Object>> previewJob = startWayConfAction.getPreviewJob(database_id);
			System.out.println(previewJob);
			List<Object> ded_id = previewJob.stream().map(item -> item.get("ded_id")).collect(Collectors.toList());
			String ded_arr = StringUtils.join(ded_id, "^");
			List<Etl_job_def> jobDefList = new ArrayList<>();
			previewJob.forEach(itemMap -> {
				Etl_job_def etl_job_def = JSONObject
					.toJavaObject(JSON.parseObject(JSON.toJSONString(itemMap)), Etl_job_def.class);
				etl_job_def.setEtl_sys_cd(etl_sys_cd);
				etl_job_def.setSub_sys_cd(sub_sys_cd);
				etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_def.setPro_name(Constant.SHELLCOMMAND);
				jobDefList.add(etl_job_def);
			});

			startWayConfAction.saveJobDataToDatabase(database_id, source_id, etl_sys_cd, sub_sys_cd, pro_dic, log_dic,
				jobDefList.toArray(new Etl_job_def[jobDefList.size()]), ded_arr, "");

		} else {
			CheckParam.throwErrorMsg("启动方式选择错误");
		}
	}

	Map<Object, List<Map<String, Object>>> getJobInfo(List<Object> tableNameList, long database_id, Sheet sheet) {

		//获取作业工程编号
		String etl_sys_cd = ExcelUtil.getValue(sheet.getRow(1).getCell(2)).toString();
		Validator.notBlank(etl_sys_cd, "作业工程编号不能为空");
		//获取作业任务编号
		String sub_sys_cd = ExcelUtil.getValue(sheet.getRow(1).getCell(4)).toString();
		Validator.notBlank(sub_sys_cd, "作业任务编号不能为空");

		Map<Object, List<Map<String, Object>>> dependDataMap = new LinkedHashMap<>();
		tableNameList.forEach(tableName -> {

			Map<String, Object> tableIdMap = Dbo
				.queryOneObject("SELECT etl_job FROM "
						+ Take_relation_etl.TableName
						+ " t1 JOIN "
						+ Data_extraction_def.TableName
						+ " t2 ON t1.ded_id = t2.ded_id JOIN "
						+ Table_info.TableName
						+ " t3 ON t3.table_id = t2.table_id WHERE t3.table_name = ?"
						+ " AND t3.database_id = ? AND t1.etl_sys_cd = ? AND t1.sub_sys_cd = ?", tableName, database_id,
					etl_sys_cd, sub_sys_cd);

			//先设置树的根节点
			List<Map<String, Object>> treeList = new ArrayList<>();
			Map<String, Object> rootMap = new LinkedHashMap<>();
			rootMap.put("id", tableName);
			rootMap.put("isroot", true);
			rootMap.put("topic", tableName);
			rootMap.put("background-color", "red");
			treeList.add(rootMap);

			//获取叶子节点数据
			treeList.addAll(getTreeData(tableName, etl_sys_cd, tableIdMap.get("etl_job")));
			dependDataMap.put(tableName, treeList);
		});

		return dependDataMap;
	}

	List<Map<String, Object>> getTreeData(Object tableName, String etl_sys_cd, Object etl_job) {

		//查询上游作业
		List<Map<String, Object>> preJob = new ArrayList<>();
		List<Object> preJobList = Dbo.queryOneColumnList(
			"SELECT pre_etl_job from " + Etl_dependency.TableName + " WHERE etl_job = ? AND etl_sys_cd = ?",
			etl_job, etl_sys_cd);
		preJobList.forEach(pre_etl_job -> {
			Map<String, Object> preMap = new LinkedHashMap<>();
			preMap.put("id", pre_etl_job);
			preMap.put("topic", pre_etl_job);
			preMap.put("direction", "left");
			preMap.put("parentid", tableName);
			preMap.put("'background-color'", "green");
			preJob.add(preMap);
		});

		//查询下游作业
		List<Object> nextJobList = Dbo.queryOneColumnList(
			"SELECT etl_job from " + Etl_dependency.TableName + " WHERE pre_etl_job = ? AND etl_sys_cd = ?",
			etl_job, etl_sys_cd);
		nextJobList.forEach(itemJob -> {
			Map<String, Object> nextMap = new LinkedHashMap<>();
			nextMap.put("id", itemJob);
			nextMap.put("topic", itemJob);
			nextMap.put("direction", "right");
			nextMap.put("parentid", tableName);
			nextMap.put("background-color", "#0000ff");
			preJob.add(nextMap);
		});

		return preJob;
	}

	@Method(desc = "表影响", logicStep = "1.获取表影响关系并返回")
	@Param(name = "datatable_en_name", desc = "数据表英文名称", range = "无限制")
	@Return(desc = "返回表影响关系", range = "无限制")
	Map<Object, List<Map<String, Object>>> getDclTable(List<Object> tableNameList, long database_id, long agent_id,
		long source_id) {

		Map<Object, List<Map<String, Object>>> dependDataMap = new LinkedHashMap<>();
		//先设置树的根节点
		List<Map<String, Object>> treeList = new ArrayList<>();
		tableNameList.forEach(tableName -> {

			Map<String, Object> rootMap = new LinkedHashMap<>();
			rootMap.put("id", tableName);
			rootMap.put("isroot", true);
			rootMap.put("topic", tableName);
			rootMap.put("background-color", "red");
			treeList.add(rootMap);

			//找到在系统中的表名称
			Map<String, Object> map = Dbo.queryOneObject("SELECT hyren_name FROM "
					+ Data_store_reg.TableName
					+ " t1 JOIN "
					+ Table_info.TableName
					+ " t2 ON t1.table_id = t2.table_id WHERE t1.agent_id = ? "
					+ " AND t1.source_id = ? AND t1.database_id = ? AND t2.table_name = ?", agent_id, source_id, database_id,
				tableName);

			List<Object> dependTableList = Dbo.queryOneColumnList(
				"SELECT datatable_en_name FROM "
					+ Dm_datatable.TableName
					+ " t1 JOIN "
					+ Dm_datatable_source.TableName
					+ " t2 ON "
					+ "t1.datatable_id = t2.datatable_id WHERE t2.own_source_table_name = ?", map.get("hyren_name"));

			dependTableList.forEach(datatable_en_name -> {
				Map<String, Object> nextMap = new LinkedHashMap<>();
				nextMap.put("id", datatable_en_name);
				nextMap.put("topic", datatable_en_name);
				nextMap.put("direction", "right");
				nextMap.put("parentid", tableName);
				nextMap.put("background-color", "#0000ff");
				treeList.add(nextMap);
			});
			dependDataMap.put(tableName, treeList);
		});

		return dependDataMap;
	}
}
