package hrds.b.biz.agent.dbagentconf.tableconf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.CountNum;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.FillingType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.StoreLayerDataSource;
import hrds.commons.codes.UnloadType;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Column_clean;
import hrds.commons.entity.Column_merge;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Dcol_relation_store;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Dtab_relation_store;
import hrds.commons.entity.Table_clean;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Table_storage_info;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "CollTbConfStepAction单元测试类", author = "WangZhengcheng")
public class InitAndDestDataForCollTb {

	//测试数据用户ID
	public final BaseInitData baseInitData = new BaseInitData();
	public final long FAST_COL_ID = PrimayKeyGener.getNextId();
	public final long SECOND_COL_ID = PrimayKeyGener.getNextId();
	public final long FAST_COLUMN_ID = PrimayKeyGener.getNextId();
	public final long SECOND_COLUMN_ID = PrimayKeyGener.getNextId();
	public final long FAST_COLEAN_COLUMN_ID = PrimayKeyGener.getNextId();
	public final long SECOND_COLEAN_COLUMN_ID = PrimayKeyGener.getNextId();
	public void before() {
		//1、构造sys_user表测试数据
//		Sys_user user = BaseInitData.buildSysUserData();

//		2、构造department_info表测试数据
		Department_info departmentInfo = baseInitData.buildDeptInfoData();

		//3、构造data_source表测试数据
		Data_source dataSource = baseInitData.buildDataSourceData();

		//4、构造agent_info表测试数据
		List<Agent_info> agents = baseInitData.buildAgentInfosData();

		//5、构造database_set表测试数据
		Database_set databaseSet = new Database_set();
		databaseSet.setDatabase_id(baseInitData.FIRST_DATABASE_SET_ID);
		databaseSet.setAgent_id(baseInitData.SECOND_DB_AGENT_ID);
		databaseSet.setDatabase_number("lqcs" + baseInitData.threadId);
		databaseSet.setTask_name("real_database_47.103.83.1");
		databaseSet.setDatabase_name("hrsdxg");
		databaseSet.setDatabase_pad("hrsdxg");
		databaseSet.setUser_name("hrsdxg");
		databaseSet.setDatabase_drive("org.postgresql.Driver");
		databaseSet.setDatabase_type(DatabaseType.Postgresql.getCode());
		databaseSet.setDatabase_ip("47.103.83.1");
		databaseSet.setDatabase_port("32001");
		databaseSet.setDb_agent(IsFlag.Fou.getCode());
		databaseSet.setJdbc_url("jdbc:postgresql://47.103.83.1:32001/hrsdxg");

		//以下数据全部设置默认值
//		databaseSet.setIs_load(IsFlag.Shi.getCode());
//		databaseSet.setIs_header(IsFlag.Shi.getCode());
//		databaseSet.setIs_hidden(IsFlag.Shi.getCode());
		databaseSet.setIs_sendok(IsFlag.Shi.getCode());

		//6、构造Collect_job_classify表测试数据
		List<Collect_job_classify> classifies = baseInitData.buildClassifyData();

		/*
		 * 7、构建table_info测试数据
		 *   7-1、sys_user、code_info表是单表配置采集
		 *   7-2、agent_info、data_source是自定义SQL采集
		 * */
		List<Table_info> tableInfos = new ArrayList<>();
		for (int i = 1; i <= 4; i++) {
			long tableId = 0L;
			String tableName = null;
			String tableChName = null;
			String customizeSQL = null;
			String customizFlag = null;
			String parallelFlag = null;
			String pageSql = null;
			String tableCount = null;
			int dataIncrement = 0;
			int pageParallels = 5;
			Table_info tableInfo = new Table_info();
			switch (i) {
				case 1:
					tableId = baseInitData.SYS_USER_TABLE_ID;
					tableName = "sys_user";
					tableChName = "用户表";
					//自定义过滤
					customizeSQL = "select * from sys_user where user_id = " + baseInitData.TEST_USER_ID;
					customizFlag = IsFlag.Fou.getCode();
					parallelFlag = IsFlag.Fou.getCode();
					tableInfo.setUnload_type(UnloadType.QuanLiangXieShu.getCode());
					tableInfo.setIs_customize_sql(IsFlag.Shi.getCode());
					pageSql = "";
					tableCount = "100000";
					break;
				case 2:
					tableId = baseInitData.CODE_INFO_TABLE_ID;
					tableName = "code_info";
					tableChName = "代码信息表";
					customizeSQL = "";
					customizFlag = IsFlag.Fou.getCode();
					parallelFlag = IsFlag.Shi.getCode();
					tableInfo.setUnload_type(UnloadType.QuanLiangXieShu.getCode());
					tableInfo.setIs_customize_sql(IsFlag.Shi.getCode());
					//分页抽取
					pageSql = "select * from code_info limit 10";
					tableCount = "100000";
					dataIncrement = 1000;
					pageParallels = 6;
					break;
				case 3:
					tableId = baseInitData.AGENT_INFO_TABLE_ID;
					tableName = "agent_info";
					tableChName = "Agent信息表";
					//自定义采集
					customizeSQL = "select agent_id, agent_name, agent_type from agent_info where source_id = "
						+ baseInitData.SOURCE_ID;
					customizFlag = IsFlag.Shi.getCode();
					parallelFlag = IsFlag.Fou.getCode();
					tableInfo.setUnload_type(UnloadType.QuanLiangXieShu.getCode());
					tableInfo.setIs_customize_sql(IsFlag.Shi.getCode());
					pageSql = "";
					tableCount = "100000";
					break;
				case 4:
					tableId = baseInitData.DATA_SOURCE_TABLE_ID;
					tableName = "data_source";
					tableChName = "数据源表";
					//自定义采集
					JSONObject sqlObj = new JSONObject();
					sqlObj.put("insert",
						"select source_id, datasource_number, datasource_name from data_source where source_id = "
							+ baseInitData.SOURCE_ID);
					sqlObj.put("update",
						"select source_id, datasource_number, datasource_name from data_source where source_id = "
							+ baseInitData.SOURCE_ID);
					sqlObj.put("delete",
						"select source_id, datasource_number, datasource_name from data_source where source_id = "
							+ baseInitData.SOURCE_ID);
					customizeSQL = sqlObj.toJSONString();
					customizFlag = IsFlag.Shi.getCode();
					parallelFlag = IsFlag.Fou.getCode();
					tableInfo.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
					tableInfo.setIs_customize_sql(IsFlag.Shi.getCode());
					pageSql = "";
					tableCount = "100000";
					break;
			}
			tableInfo.setTable_id(tableId);
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setTable_count(CountNum.ShiWan.getCode());
			tableInfo.setDatabase_id(baseInitData.FIRST_DATABASE_SET_ID);
			tableInfo.setValid_s_date(DateUtil.getSysDate());
			tableInfo.setValid_e_date(Constant.MAXDATE);
			tableInfo.setSql(customizeSQL);
			tableInfo.setIs_user_defined(customizFlag);
			tableInfo.setTi_or(baseInitData.initTableCleanOrder().toJSONString());
			tableInfo.setIs_md5(IsFlag.Shi.getCode());
			tableInfo.setIs_register(IsFlag.Shi.getCode());
			tableInfo.setIs_parallel(parallelFlag);
			tableInfo.setPage_sql(pageSql);
			tableInfo.setTable_count(tableCount);
			tableInfo.setDataincrement(dataIncrement);
			tableInfo.setPageparallels(pageParallels);
			tableInfo.setRec_num_date(DateUtil.getSysDate());
			tableInfo.setRemark("lqcslqcslqcs" + baseInitData.threadId);
			tableInfos.add(tableInfo);
		}

		//8、构建table_column表测试数据
		List<Table_column> sysUsers = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			String primaryKeyFlag = null;
			String columnName = null;
			String columnType = null;
			String columnChName = null;
			switch (i) {
				case 1:
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "user_id";
					columnType = "int8";
					columnChName = "主键";
					break;
				case 2:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "create_id";
					columnType = "int8";
					columnChName = "创建用户者ID";
					break;
				case 3:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "dep_id";
					columnType = "int8";
					columnChName = "部门ID";
					break;
				case 4:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "role_id";
					columnType = "int8";
					columnChName = "角色ID";
					break;
				case 5:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_name";
					columnType = "varchar";
					columnChName = "用户名";
					break;
				case 6:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_password";
					columnType = "varchar";
					columnChName = "密码";
					break;
				case 7:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_email";
					columnType = "varchar";
					columnChName = "邮箱";
					break;
				case 8:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_mobile";
					columnType = "varchar";
					columnChName = "电话";
					break;
				case 9:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "useris_admin";
					columnType = "char";
					columnChName = "是否管理员";
					break;
				case 10:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_type";
					columnType = "char";
					columnChName = "用户类型";
					break;
			}
			Table_column sysUserColumn = new Table_column();
			sysUserColumn.setColumn_id(PrimayKeyGener.getNextId());
			sysUserColumn.setIs_get(IsFlag.Shi.getCode());
			sysUserColumn.setIs_primary_key(primaryKeyFlag);
			sysUserColumn.setColumn_name(columnName);
			sysUserColumn.setColumn_type(columnType);
			sysUserColumn.setColumn_ch_name(columnChName);
			sysUserColumn.setTable_id(baseInitData.SYS_USER_TABLE_ID);
			sysUserColumn.setValid_s_date(DateUtil.getSysDate());
			sysUserColumn.setValid_e_date(Constant.MAXDATE);
			sysUserColumn.setIs_alive(IsFlag.Shi.getCode());
			sysUserColumn.setIs_new(IsFlag.Shi.getCode());
			sysUserColumn.setTc_or(baseInitData.initColumnCleanOrder().toJSONString());
			sysUserColumn.setTc_remark("lqcslqcslqcs" + baseInitData.threadId);
			sysUsers.add(sysUserColumn);
		}

		List<Table_column> codeInfos = baseInitData.buildCodeInfoTbColData();

		List<Table_column> dataSources = baseInitData.buildDataSourceTbColData();

		List<Table_column> agentInfos = baseInitData.buildAgentInfoTbColData();

		//9-1、构造sys_user表和code_info表在table_storage_info表测试数据
		List<Table_storage_info> tableStorageInfos = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			String fileFormat = null;
			String storageType = null;
			long tableId = 0L;
			long storageId = i % 2 == 0 ? baseInitData.FIRST_STORAGE_ID : baseInitData.SECOND_STORAGE_ID;
			switch (i) {
				case 1:
					fileFormat = FileFormat.CSV.getCode();
					storageType = StorageType.TiHuan.getCode();
					tableId = baseInitData.SYS_USER_TABLE_ID;
					break;
				case 2:
					fileFormat = FileFormat.DingChang.getCode();
					storageType = StorageType.ZhuiJia.getCode();
					tableId = baseInitData.CODE_INFO_TABLE_ID;
					break;
			}
			Table_storage_info tableStorageInfo = new Table_storage_info();
			tableStorageInfo.setStorage_id(storageId);
			tableStorageInfo.setFile_format(fileFormat);
			tableStorageInfo.setStorage_type(storageType);
			tableStorageInfo.setIs_zipper(IsFlag.Shi.getCode());
			tableStorageInfo.setTable_id(tableId);
			tableStorageInfo.setHyren_name("test_" + baseInitData.threadId);
			tableStorageInfos.add(tableStorageInfo);
		}

		//sys_user和code_info两者同时保存进入关系型数据库，假设dslId为97281表示关系型数据库
		List<Dtab_relation_store> relationTablesOfUserAndCode = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Dtab_relation_store relationTable = new Dtab_relation_store();
			relationTable.setTab_id(i % 2 == 0 ? baseInitData.FIRST_STORAGE_ID : baseInitData.SECOND_STORAGE_ID);
			relationTable.setDsl_id(baseInitData.DATABASE_DSL_ID);
			relationTable.setData_source(StoreLayerDataSource.DBA.getCode());
			relationTablesOfUserAndCode.add(relationTable);
		}

		//9-1、构造agent_info和data_source表在table_storage_info表中的数据，两者同时保存进入关系型数据库
		List<Table_storage_info> tableStorageInfosTwo = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			String fileFormat = null;
			String storageType = null;
			long tableId = 0L;
			long storageId = i % 2 == 0 ? baseInitData.THIRD_STORAGE_ID : baseInitData.FOUTH_STORAGE_ID;
			switch (i) {
				case 1:
					fileFormat = FileFormat.CSV.getCode();
					storageType = StorageType.TiHuan.getCode();
					tableId = baseInitData.DATA_SOURCE_TABLE_ID;
					break;
				case 2:
					fileFormat = FileFormat.DingChang.getCode();
					storageType = StorageType.ZhuiJia.getCode();
					tableId = baseInitData.AGENT_INFO_TABLE_ID;
					break;
			}
			Table_storage_info tableStorageInfo = new Table_storage_info();
			tableStorageInfo.setStorage_id(storageId);
			tableStorageInfo.setFile_format(fileFormat);
			tableStorageInfo.setStorage_type(storageType);
			tableStorageInfo.setIs_zipper(IsFlag.Shi.getCode());
			tableStorageInfo.setTable_id(tableId);
			tableStorageInfo.setHyren_name("test222_" + baseInitData.threadId);
			tableStorageInfosTwo.add(tableStorageInfo);
		}

		//agent_info和data_source两者同时保存进入关系型数据库，假设dslId为97281表示关系型数据库
		List<Dtab_relation_store> relationTables = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Dtab_relation_store relationTable = new Dtab_relation_store();
			relationTable.setTab_id(i % 2 == 0 ? baseInitData.THIRD_STORAGE_ID : baseInitData.FOUTH_STORAGE_ID);
			relationTable.setDsl_id(baseInitData.DATABASE_DSL_ID);
			relationTable.setData_source(StoreLayerDataSource.DBA.getCode());
			relationTables.add(relationTable);
		}

		//10-1、构造采集sys_user表在table_clean表测试数据
		List<Table_clean> sysUserCleans = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			long id = PrimayKeyGener.getNextId();
			String cleanType = null;
			switch (i) {
				case 1:
					cleanType = CleanType.ZiFuHeBing.getCode();
					break;
				case 2:
					cleanType = CleanType.ZiFuTrim.getCode();
					break;
			}
			Table_clean sysUserClean = new Table_clean();
			sysUserClean.setTable_clean_id(id);
			sysUserClean.setClean_type(cleanType);
			sysUserClean.setTable_id(baseInitData.SYS_USER_TABLE_ID);
			sysUserCleans.add(sysUserClean);
		}

		//10-2、构造采集code_info表在table_clean表测试数据
		List<Table_clean> codeInfoCleans = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			long id = PrimayKeyGener.getNextId();
			String cleanType = null;
			String completeType = null;
			String completeChar = null;
			long completeLength = 0L;
			String oldField = null;
			String newField = null;
			switch (i) {
				case 1:
					cleanType = CleanType.ZiFuTiHuan.getCode();
					completeType = "";
					completeChar = "";
					oldField = "abc";
					newField = "def";
					break;
				case 2:
					cleanType = CleanType.ZiFuBuQi.getCode();
					completeType = FillingType.QianBuQi.getCode();
					completeChar = "beyond";
					completeLength = 6;
					break;
			}
			Table_clean codeInfoClean = new Table_clean();
			codeInfoClean.setTable_clean_id(id);
			codeInfoClean.setClean_type(cleanType);
			codeInfoClean.setFilling_type(completeType);
			codeInfoClean.setCharacter_filling(completeChar);
			codeInfoClean.setFilling_length(completeLength);
			codeInfoClean.setField(oldField);
			codeInfoClean.setReplace_feild(newField);
			codeInfoClean.setTable_id(baseInitData.CODE_INFO_TABLE_ID);

			codeInfoCleans.add(codeInfoClean);
		}

		//10-3、构造采集agent_info表在table_clean表测试数据
		List<Table_clean> agentInfoCleans = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			long id = PrimayKeyGener.getNextId();
			String cleanType = null;
			String completeType = null;
			String completeChar = null;
			long completeLength = 0L;
			String oldField = null;
			String newField = null;
			switch (i) {
				case 1:
					cleanType = CleanType.ZiFuTiHuan.getCode();
					completeType = "";
					completeChar = "";
					oldField = "qwe";
					newField = "asd";
					break;
				case 2:
					cleanType = CleanType.ZiFuBuQi.getCode();
					completeType = FillingType.QianBuQi.getCode();
					completeChar = "hongzhi";
					completeLength = 7;
					break;
			}
			Table_clean agentInfoClean = new Table_clean();
			agentInfoClean.setTable_clean_id(id);
			agentInfoClean.setClean_type(cleanType);
			agentInfoClean.setFilling_type(completeType);
			agentInfoClean.setCharacter_filling(completeChar);
			agentInfoClean.setFilling_length(completeLength);
			agentInfoClean.setField(oldField);
			agentInfoClean.setReplace_feild(newField);
			agentInfoClean.setTable_id(baseInitData.AGENT_INFO_TABLE_ID);

			agentInfoCleans.add(agentInfoClean);
		}

		//10-4、构造采集data_source表在table_clean表测试数据
		List<Table_clean> dataSourceCleans = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			long id = PrimayKeyGener.getNextId();
			String cleanType = null;
			String completeType = null;
			String completeChar = null;
			long completeLength = 0L;
			String oldField = null;
			String newField = null;
			switch (i) {
				case 1:
					cleanType = CleanType.ZiFuTiHuan.getCode();
					completeType = "";
					completeChar = "";
					oldField = "uio";
					newField = "jkl";
					break;
				case 2:
					cleanType = CleanType.ZiFuBuQi.getCode();
					completeType = FillingType.QianBuQi.getCode();
					completeChar = "beyond_hongzhi";
					completeLength = 14;
					break;
			}
			Table_clean dataSourceClean = new Table_clean();
			dataSourceClean.setTable_clean_id(id);
			dataSourceClean.setClean_type(cleanType);
			dataSourceClean.setFilling_type(completeType);
			dataSourceClean.setCharacter_filling(completeChar);
			dataSourceClean.setFilling_length(completeLength);
			dataSourceClean.setField(oldField);
			dataSourceClean.setReplace_feild(newField);
			dataSourceClean.setTable_id(baseInitData.DATA_SOURCE_TABLE_ID);

			dataSourceCleans.add(dataSourceClean);
		}

		//11、构造column_merge表测试数据
		List<Column_merge> sysUserMerge = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			long id = PrimayKeyGener.getNextId();
			String afterColName = null;
			String beforeColName = null;
			String afterColChName = null;
			String afterColType = null;
			switch (i) {
				case 1:
					afterColName = "user_create_id";
					beforeColName = "user_id" + "|" + "create_id";
					afterColChName = "用户_创建者_ID";
					afterColType = "varchar(1024)";
					break;
				case 2:
					afterColName = "user_name_password";
					beforeColName = "user_name" + "|" + "user_password";
					afterColChName = "用户名_密码";
					afterColType = "varchar(1024)";
					break;
			}
			Column_merge columnMerge = new Column_merge();
			columnMerge.setCol_merge_id(id);
			columnMerge.setCol_name(afterColName);
			columnMerge.setOld_name(beforeColName);
			columnMerge.setCol_zhname(afterColChName);
			columnMerge.setCol_type(afterColType);
			columnMerge.setValid_s_date(DateUtil.getSysDate());
			columnMerge.setValid_e_date(Constant.MAXDATE);
			columnMerge.setTable_id(baseInitData.SYS_USER_TABLE_ID);

			sysUserMerge.add(columnMerge);
		}

		List<Column_merge> codeInfoMerge = new ArrayList<>();
		Column_merge codeInfo = new Column_merge();
		codeInfo.setCol_merge_id(PrimayKeyGener.getNextId());
		codeInfo.setCol_name("ci_sp_classname_name");
		codeInfo.setOld_name("ci_sp_classname|ci_sp_name");
		codeInfo.setCol_zhname("类别|代码名称");
		codeInfo.setCol_type("VARCHAR(255)");
		codeInfo.setValid_s_date(DateUtil.getSysDate());
		codeInfo.setValid_e_date(Constant.MAXDATE);
		codeInfo.setTable_id(baseInitData.CODE_INFO_TABLE_ID);

		codeInfoMerge.add(codeInfo);

		//12-1、构造sys_user和code_info表data_extraction_def表数据
		List<Data_extraction_def> extractionDefs = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Data_extraction_def def = new Data_extraction_def();
			def.setDed_id(PrimayKeyGener.getNextId());
			def.setTable_id(i % 2 == 0 ? baseInitData.SYS_USER_TABLE_ID : baseInitData.CODE_INFO_TABLE_ID);
			def.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			def.setIs_header(i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode());
			def.setDatabase_code(DataBaseCode.UTF_8.getCode());
			def.setDbfile_format(i % 2 == 0 ? FileFormat.ORC.getCode() : FileFormat.PARQUET.getCode());
			def.setPlane_url(i % 2 == 0 ? "/root" : "/home/hyshf");

			extractionDefs.add(def);
		}

		//12-2、构造data_source表和agent_info表data_extraction_def表数据
		List<Data_extraction_def> extractionDefsTwo = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Data_extraction_def def = new Data_extraction_def();
			def.setDed_id(PrimayKeyGener.getNextId());
			def.setTable_id(i % 2 == 0 ? baseInitData.DATA_SOURCE_TABLE_ID : baseInitData.AGENT_INFO_TABLE_ID);
			def.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			def.setIs_header(i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode());
			def.setDatabase_code(DataBaseCode.UTF_8.getCode());
			def.setDbfile_format(i % 2 == 0 ? FileFormat.CSV.getCode() : FileFormat.FeiDingChang.getCode());
			def.setRow_separator(i % 2 == 0 ? "" : "|");
			def.setDatabase_separatorr(i % 2 == 0 ? "" : "\r");

			extractionDefsTwo.add(def);
		}

		//13、由于该Action类的测试连接功能需要与agent端交互，所以需要配置一条agent_down_info表的记录，用于找到http访问的完整url
		Agent_down_info agentDownInfo = baseInitData.initAgentDownInfoTwo();

//		public final long FAST_COLUMN_ID = PrimayKeyGener.getNextId();
//		public final long SECOND_COLUMN_ID = PrimayKeyGener.getNextId();
		//14-1、构造列清洗参数表数据,给datasource_number和agent_name设置字符补齐
		List<Column_clean> colCleans = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long colCleanId = PrimayKeyGener.getNextId();
			String cleanType = CleanType.ZiFuBuQi.getCode();
			String compleType = i % 2 == 0 ? FillingType.QianBuQi.getCode() : FillingType.HouBuQi.getCode();
			String compleChar = i % 2 == 0 ? StringUtil.string2Unicode("wzc") : StringUtil.string2Unicode(" ");
			long length = i % 2 == 0 ? 3 : 1;
			long columnId = i % 2 == 0 ? FAST_COLUMN_ID : SECOND_COLUMN_ID;
			Column_clean colComple = new Column_clean();
			colComple.setCol_clean_id(colCleanId);
			colComple.setClean_type(cleanType);
			colComple.setFilling_type(compleType);
			colComple.setCharacter_filling(compleChar);
			colComple.setFilling_length(length);
			colComple.setColumn_id(columnId);

			colCleans.add(colComple);
		}

		//14-2、构造列清洗参数表数据,给user_mobile和ci_sp_code设置字符补齐
		List<Column_clean> colCleansOfUserAndCode = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long colCleanId = PrimayKeyGener.getNextId();
			String cleanType = CleanType.ZiFuBuQi.getCode();
			String compleType = i % 2 == 0 ? FillingType.QianBuQi.getCode() : FillingType.HouBuQi.getCode();
			String compleChar = i % 2 == 0 ? StringUtil.string2Unicode("wzc") : StringUtil.string2Unicode(" ");
			long length = i % 2 == 0 ? 3 : 1;
			long columnId = i % 2 == 0 ? 2008L : 3001L;
			columnId = i % 2 == 0 ? FAST_COLEAN_COLUMN_ID : SECOND_COLEAN_COLUMN_ID;
			Column_clean colComple = new Column_clean();
			colComple.setCol_clean_id(colCleanId);
			colComple.setClean_type(cleanType);
			colComple.setFilling_type(compleType);
			colComple.setCharacter_filling(compleChar);
			colComple.setFilling_length(length);
			colComple.setColumn_id(columnId);

			colCleansOfUserAndCode.add(colComple);
		}

		//15、构造列存储信息表数据，构造data_source表中的source_id字段、sys_user表的user_id字段作为关系型数据库主键，假设85151L
		List<Dcol_relation_store> columnStorageInfos = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Dcol_relation_store storageInfo = new Dcol_relation_store();
			storageInfo.setCol_id(i % 2 == 0 ? 5112L : 2001L);
			storageInfo.setCol_id(i % 2 == 0 ? FAST_COL_ID : SECOND_COL_ID);
			storageInfo.setDslad_id(baseInitData.PRIMARY_KEY_DSLAD_ID);
			storageInfo.setData_source(StoreLayerDataSource.DBA.getCode());
			columnStorageInfos.add(storageInfo);
		}

		//插入数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//插入用户表(sys_user)测试数据
//			int userCount = user.add(db);
//			assertThat("用户表测试数据初始化", userCount, is(1));

//			插入部门表(department_info)测试数据
			int deptCount = departmentInfo.add(db);
			assertThat("部门表测试数据初始化", deptCount, is(1));

			//插入数据源表(data_source)测试数据
			int dataSourceCount = dataSource.add(db);
			assertThat("数据源测试数据初始化", dataSourceCount, is(1));

			//插入Agent信息表(agent_info)测试数据
			int agentInfoCount = 0;
			for (Agent_info agentInfo : agents) {
				int count = agentInfo.add(db);
				agentInfoCount += count;
			}
			assertThat("Agent测试数据初始化", agentInfoCount, is(2));

			//插入database_set表测试数据
			int databaseSetCount = databaseSet.add(db);
			assertThat("数据库设置测试数据初始化", databaseSetCount, is(1));

			//插入collect_job_classify表测试数据
			int classifyCount = 0;
			for (Collect_job_classify classify : classifies) {
				int count = classify.add(db);
				classifyCount += count;
			}
			assertThat("采集任务分类表测试数据初始化", classifyCount, is(2));

			//插入table_info测试数据
			int tableInfoCount = 0;
			for (Table_info tableInfo : tableInfos) {
				int count = tableInfo.add(db);
				tableInfoCount += count;
			}
			assertThat("数据库对应表测试数据初始化", tableInfoCount, is(4));

			//插入table_column测试数据
			int sysUserCount = 0;
			for (Table_column tableColumn : sysUsers) {
				int count = tableColumn.add(db);
				sysUserCount += count;
			}
			assertThat("sys_user表对应字段表测试数据初始化", sysUserCount, is(10));
			int codeInfoCount = 0;
			for (Table_column tableColumn : codeInfos) {
				int count = tableColumn.add(db);
				codeInfoCount += count;
			}
			assertThat("code_info表对应字段表测试数据初始化", codeInfoCount, is(5));
			int agentInfosCount = 0;
			for (Table_column tableColumn : agentInfos) {
				int count = tableColumn.add(db);
				agentInfosCount += count;
			}
			assertThat("agent_info表对应字段表测试数据初始化", agentInfosCount, is(3));
			int dataSourcesCount = 0;
			for (Table_column tableColumn : dataSources) {
				int count = tableColumn.add(db);
				dataSourcesCount += count;
			}
			assertThat("data_source表对应字段表测试数据初始化", dataSourcesCount, is(3));

			//插入table_storage_info测试数据
			int tableStorageInfoCount = 0;
			for (Table_storage_info storageInfo : tableStorageInfos) {
				int count = storageInfo.add(db);
				tableStorageInfoCount += count;
			}
			assertThat("<sys_user和code_info>表存储信息表测试数据初始化", tableStorageInfoCount, is(2));
			int tableStorageInfosTwoCount = 0;
			for (Table_storage_info storageInfo : tableStorageInfosTwo) {
				int count = storageInfo.add(db);
				tableStorageInfosTwoCount += count;
			}
			assertThat("<data_source和agent_info>表存储信息表测试数据初始化", tableStorageInfosTwoCount, is(2));

			//插入table_clean测试数据
			int sysUserCleansCount = 0;
			for (Table_clean tableClean : sysUserCleans) {
				int count = tableClean.add(db);
				sysUserCleansCount += count;
			}
			assertThat("<sys_user>表清洗参数信息表测试数据初始化", sysUserCleansCount, is(2));
			int codeInfoCleansCount = 0;
			for (Table_clean tableClean : codeInfoCleans) {
				int count = tableClean.add(db);
				codeInfoCleansCount += count;
			}
			assertThat("<code_info>表清洗参数信息表测试数据初始化", codeInfoCleansCount, is(2));
			int agentInfoCleansCount = 0;
			for (Table_clean tableClean : agentInfoCleans) {
				int count = tableClean.add(db);
				agentInfoCleansCount += count;
			}
			assertThat("<agent_info>表清洗参数信息表测试数据初始化", agentInfoCleansCount, is(2));
			int dataSourceCleansCount = 0;
			for (Table_clean tableClean : dataSourceCleans) {
				int count = tableClean.add(db);
				dataSourceCleansCount += count;
			}
			assertThat("<data_source>表清洗参数信息表测试数据初始化", dataSourceCleansCount, is(2));

			//插入column_merge测试数据
			int sysUserMergeCount = 0;
			for (Column_merge columnMerge : sysUserMerge) {
				int count = columnMerge.add(db);
				sysUserMergeCount += count;
			}
			assertThat("<sys_user>列合并信息表测试数据初始化", sysUserMergeCount, is(2));
			int codeInfoMergeCount = 0;
			for (Column_merge columnMerge : codeInfoMerge) {
				int count = columnMerge.add(db);
				codeInfoMergeCount += count;
			}
			assertThat("<code_info>列合并信息表测试数据初始化", codeInfoMergeCount, is(1));

			//插入agent_down_info表测试数据
			int agentDownInfoCount = agentDownInfo.add(db);
			assertThat("Agent下载信息表测试数据初始化", agentDownInfoCount, is(1));

			//插入data_extraction_def表测试数据
			int extractionDefCount = 0;
			for (Data_extraction_def def : extractionDefs) {
				int count = def.add(db);
				extractionDefCount += count;
			}
			assertThat("<sys_user和code_info>数据抽取定义表测试数据初始化", extractionDefCount, is(2));
			int extractionDefTwoCount = 0;
			for (Data_extraction_def def : extractionDefsTwo) {
				int count = def.add(db);
				extractionDefTwoCount += count;
			}
			assertThat("<agent_info和data_source>数据抽取定义表测试数据初始化", extractionDefTwoCount, is(2));

			//插入column_clean表测试数据
			int colCleanCount = 0;
			for (Column_clean columnClean : colCleans) {
				int count = columnClean.add(db);
				colCleanCount += count;
			}
			assertThat("<agent_info表和data_source表>列清洗参数表测试数据初始化", colCleanCount, is(2));

			int colCleanTwoCount = 0;
			for (Column_clean columnClean : colCleansOfUserAndCode) {
				int count = columnClean.add(db);
				colCleanTwoCount += count;
			}
			assertThat("<sys_user表和code_info表>列清洗参数表测试数据初始化", colCleanTwoCount, is(2));

			//插入Dtab_relation_store表数据
			int relationTablesCount = 0;
			for (Dtab_relation_store relationTable : relationTables) {
				int count = relationTable.add(db);
				relationTablesCount += count;
			}
			assertThat("<agent_info表和data_source表>数据存储关系表测试数据初始化", relationTablesCount, is(2));

			int relationTablesTwoCount = 0;
			for (Dtab_relation_store relationTable : relationTablesOfUserAndCode) {
				int count = relationTable.add(db);
				relationTablesTwoCount += count;
			}
			assertThat("<code_info表和sys_user表>数据存储关系表测试数据初始化", relationTablesTwoCount, is(2));

			//插入Dcol_relation_store表数据
			int storageCount = 0;
			for (Dcol_relation_store storageInfo : columnStorageInfos) {
				int count = storageInfo.add(db);
				storageCount += count;
			}
			assertThat("<data_source表的source_id>字段存储信息表测试数据初始化", storageCount, is(2));
			assertThat("<sys_user表的user_id>字段存储信息表测试数据初始化", storageCount, is(2));

			SqlOperator.commitTransaction(db);
		}
	}

	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
//			//删除用户表(sys_user)测试数据
//			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//删除部门表(department_info)测试数据
			SqlOperator
				.execute(db, "delete from " + Department_info.TableName + " WHERE dep_id = ?", baseInitData.TEST_DEPT_ID);
			//1、删除数据源表(data_source)测试数据
			SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE create_user_id = ?",
				baseInitData.TEST_USER_ID);
			//2、删除Agent信息表(agent_info)测试数据
			SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", baseInitData.TEST_USER_ID);
			//3、删除database_set表测试数据
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?",
				baseInitData.SECOND_DB_AGENT_ID);
			//4、删除collect_job_classify表测试数据
			SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE user_id = ?",
				baseInitData.TEST_USER_ID);
			//5、删除table_info表测试数据
			SqlOperator.execute(db, "delete from " + Table_info.TableName + " where database_id = ? ",
				baseInitData.FIRST_DATABASE_SET_ID);
			//6、删除table_column表测试数据
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ",
				baseInitData.SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ",
				baseInitData.CODE_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ",
				baseInitData.AGENT_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ",
				baseInitData.DATA_SOURCE_TABLE_ID);
			//7、删除table_storage_info表测试数据
			SqlOperator
				.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ",
					baseInitData.SYS_USER_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ",
					baseInitData.CODE_INFO_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ",
					baseInitData.AGENT_INFO_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ",
					baseInitData.DATA_SOURCE_TABLE_ID);
			//8、删除table_clean表测试数据
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ",
				baseInitData.SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ",
				baseInitData.CODE_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ",
				baseInitData.AGENT_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ",
				baseInitData.DATA_SOURCE_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_clean_id = ? ", 3915);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_clean_id = ? ", 8547);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_clean_id = ? ", 1695L);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_clean_id = ? ", 1659);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_clean_id = ? ", 1470L);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_clean_id = ? ", 2581L);
			//9、删除column_merge表测试数据
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ? ",
				baseInitData.SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ? ",
				baseInitData.CODE_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where Col_merge_id = ? ", 1573L);
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where Col_merge_id = ? ", 8848L);
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where Col_merge_id = ? ", 8963L);
			//10、删除agent_down_info表测试数据
			SqlOperator
				.execute(db, "delete from " + Agent_down_info.TableName + " where down_id = ? ",
					baseInitData.AGENT_DOWN_INFO_ID + 1);
			//11、删除data_extraction_def表测试数据
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ",
					baseInitData.SYS_USER_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ",
					baseInitData.CODE_INFO_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ",
					baseInitData.AGENT_INFO_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ",
					baseInitData.DATA_SOURCE_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where ded_id = ? ", 6963);
			SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where ded_id = ? ", 12086);
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where ded_id in (?,?,?,?,?) ", 3001, 3002,
					3003, 3004, 3005);
			//12、删除column_clean表测试数据
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 3113);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 5113);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", FAST_COLEAN_COLUMN_ID);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", SECOND_COLEAN_COLUMN_ID);

			//13、删除Dtab_relation_store表数据
			SqlOperator
				.execute(db, "delete from " + Dtab_relation_store.TableName + " where tab_id = ? ",
					baseInitData.THIRD_STORAGE_ID);
			SqlOperator
				.execute(db, "delete from " + Dtab_relation_store.TableName + " where tab_id = ? ",
					baseInitData.FOUTH_STORAGE_ID);
			SqlOperator
				.execute(db, "delete from " + Dtab_relation_store.TableName + " where tab_id = ? ",
					baseInitData.FIRST_STORAGE_ID);
			SqlOperator
				.execute(db, "delete from " + Dtab_relation_store.TableName + " where tab_id = ? ",
					baseInitData.SECOND_STORAGE_ID);
			//14、删除Dcol_relation_store表数据
			SqlOperator.execute(db, "delete from " + Dcol_relation_store.TableName + " where col_id = ? ", 5112);
			SqlOperator.execute(db, "delete from " + Dcol_relation_store.TableName + " where col_id = ? ", 2001);

			//15 删除测试时的数据
			SqlOperator.execute(db, "delete from table_storage_info where storage_id in (?,?,?,?)", 1234, 5678, 1273, 4288);
			//提交事务
			SqlOperator.commitTransaction(db);
		}
	}

}
