package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "CollTbConfStepAction单元测试类", author = "WangZhengcheng")
public class InitAndDestDataForCollTb {

	//测试数据用户ID
	private static final long TEST_USER_ID = 9997L;
	private static final long TEST_DEPT_ID = 9987L;
	private static final long SOURCE_ID = 1L;

	private static final long FIRST_DATABASESET_ID = 1001L;

	private static final long SECOND_DB_AGENT_ID = 7002L;

	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long CODE_INFO_TABLE_ID = 7002L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
	private static final long DATA_SOURCE_TABLE_ID = 7004L;

	private static final long BASE_SYS_USER_PRIMARY = 2000L;

	private static final long FIRST_STORAGE_ID = 1234L;
	private static final long SECOND_STORAGE_ID = 5678L;

	private static final JSONObject tableCleanOrder = BaseInitData.initTableCleanOrder();
	private static final JSONObject columnCleanOrder = BaseInitData.initColumnCleanOrder();

	private static final long AGENT_DOWN_INFO_ID = 12581L;

	public static void before(){
		//构造sys_user表测试数据
		Sys_user user = BaseInitData.buildSysUserData();

		//构造department_info表测试数据
		Department_info departmentInfo = BaseInitData.buildDeptInfoData();

		//3、构造data_source表测试数据
		Data_source dataSource = BaseInitData.buildDataSourceData();

		//4、构造agent_info表测试数据
		List<Agent_info> agents = BaseInitData.buildAgentInfosData();

		//5、构造database_set表测试数据
		Database_set databaseSet = new Database_set();
		databaseSet.setDatabase_id(FIRST_DATABASESET_ID);
		databaseSet.setAgent_id(SECOND_DB_AGENT_ID);
		databaseSet.setDatabase_number(FIRST_DATABASESET_ID + "");
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
		databaseSet.setIs_load(IsFlag.Shi.getCode());
		databaseSet.setIs_header(IsFlag.Shi.getCode());
		databaseSet.setIs_hidden(IsFlag.Shi.getCode());
		databaseSet.setIs_sendok(IsFlag.Shi.getCode());

		//6、构造Collect_job_classify表测试数据
		List<Collect_job_classify> classifies = BaseInitData.buildClassifyData();

		//7、构建table_info测试数据
		List<Table_info> tableInfos = new ArrayList<>();
		for(int i = 1; i <= 4; i++){
			long tableId;
			String tableName;
			String tableChName;
			String customizeSQL;
			String customizFlag;
			String parallelFlag;
			String pageSql;
			switch (i) {
				case 1:
					tableId = SYS_USER_TABLE_ID;
					tableName = "sys_user";
					tableChName = "用户表";
					//自定义过滤
					customizeSQL = "select * from sys_user where user_id = " + TEST_USER_ID;
					customizFlag = IsFlag.Fou.getCode();
					parallelFlag = IsFlag.Fou.getCode();
					pageSql = "";
					break;
				case 2:
					tableId = CODE_INFO_TABLE_ID;
					tableName = "code_info";
					tableChName = "代码信息表";
					customizeSQL = "";
					customizFlag = IsFlag.Fou.getCode();
					parallelFlag = IsFlag.Shi.getCode();
					//自定义过滤
					pageSql = "select * from code_info limit 10";
					break;
				case 3:
					tableId = AGENT_INFO_TABLE_ID;
					tableName = "agent_info";
					tableChName = "Agent信息表";
					//自定义采集
					customizeSQL = "select agent_id, agent_name, agent_type from agent_info where source_id = " + SOURCE_ID;
					customizFlag = IsFlag.Shi.getCode();
					parallelFlag = IsFlag.Fou.getCode();
					pageSql = "";
					break;
				case 4:
					tableId = DATA_SOURCE_TABLE_ID;
					tableName = "data_source";
					tableChName = "数据源表";
					//自定义采集
					customizeSQL = "select source_id, datasource_number, datasource_name from data_source where source_id = " + SOURCE_ID;
					customizFlag = IsFlag.Shi.getCode();
					parallelFlag = IsFlag.Fou.getCode();
					pageSql = "";
					break;
				default:
					tableId = 0L;
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
					customizFlag = "error_customizFlag";
					parallelFlag = "error_parallelFlag";
					pageSql = "unexpected_pageSql";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_id(tableId);
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setTable_count(CountNum.ShiWan.getCode());
			tableInfo.setDatabase_id(FIRST_DATABASESET_ID);
			tableInfo.setValid_s_date(DateUtil.getSysDate());
			tableInfo.setValid_e_date(Constant.MAXDATE);
			tableInfo.setSql(customizeSQL);
			tableInfo.setIs_user_defined(customizFlag);
			tableInfo.setTi_or(tableCleanOrder.toJSONString());
			tableInfo.setIs_md5(IsFlag.Shi.getCode());
			tableInfo.setIs_register(IsFlag.Shi.getCode());
			tableInfo.setIs_parallel(parallelFlag);
			tableInfo.setPage_sql(pageSql);

			tableInfos.add(tableInfo);
		}

		//8、构建table_column表测试数据
		List<Table_column> sysUsers = new ArrayList<>();
		for(int i = 1; i <= 10; i++){
			String primaryKeyFlag;
			String columnName;
			String columnType;
			String columnChName;
			switch (i){
				case 1 :
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "user_id";
					columnType = "int8";
					columnChName = "主键";
					break;
				case 2 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "create_id";
					columnType = "int8";
					columnChName = "创建用户者ID";
					break;
				case 3 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "dep_id";
					columnType = "int8";
					columnChName = "部门ID";
					break;
				case 4 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "role_id";
					columnType = "int8";
					columnChName = "角色ID";
					break;
				case 5 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_name";
					columnType = "varchar";
					columnChName = "用户名";
					break;
				case 6 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_password";
					columnType = "varchar";
					columnChName = "密码";
					break;
				case 7 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_email";
					columnType = "varchar";
					columnChName = "邮箱";
					break;
				case 8 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_mobile";
					columnType = "varchar";
					columnChName = "电话";
					break;
				case 9 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "useris_admin";
					columnType = "char";
					columnChName = "是否管理员";
					break;
				case 10 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_type";
					columnType = "char";
					columnChName = "用户类型";
					break;
				default:
					primaryKeyFlag = "unexpected_primaryKeyFlag";
					columnName = "unexpected_columnName";
					columnType = "unexpected_columnType";
					columnChName = "unexpected_columnChName";
			}
			Table_column sysUserColumn = new Table_column();
			sysUserColumn.setColumn_id(BASE_SYS_USER_PRIMARY + i);
			sysUserColumn.setIs_get(IsFlag.Shi.getCode());
			sysUserColumn.setIs_primary_key(primaryKeyFlag);
			sysUserColumn.setColume_name(columnName);
			sysUserColumn.setColumn_type(columnType);
			sysUserColumn.setColume_ch_name(columnChName);
			sysUserColumn.setTable_id(SYS_USER_TABLE_ID);
			sysUserColumn.setValid_s_date(DateUtil.getSysDate());
			sysUserColumn.setValid_e_date(Constant.MAXDATE);
			sysUserColumn.setIs_alive(IsFlag.Shi.getCode());
			sysUserColumn.setIs_new(IsFlag.Shi.getCode());
			sysUserColumn.setTc_or(columnCleanOrder.toJSONString());

			sysUsers.add(sysUserColumn);
		}

		List<Table_column> codeInfos = BaseInitData.buildCodeInfoTbColData();

		List<Table_column> dataSources = BaseInitData.buildDataSourceTbColData();

		List<Table_column> agentInfos = BaseInitData.buildAgentInfoTbColData();

		//8、构造table_storage_info表测试数据
		List<Table_storage_info> tableStorageInfos = new ArrayList<>();
		for(int i = 1; i<= 2; i++){
			String fileFormat;
			String storageType;
			long tableId;
			long storageId = i % 2 == 0 ? FIRST_STORAGE_ID : SECOND_STORAGE_ID;
			switch (i){
				case 1 :
					fileFormat = FileFormat.CSV.getCode();
					storageType = StorageType.TiHuan.getCode();
					tableId = SYS_USER_TABLE_ID;
					break;
				case 2 :
					fileFormat = FileFormat.DingChang.getCode();
					storageType = StorageType.ZhuiJia.getCode();
					tableId = CODE_INFO_TABLE_ID;
					break;
				default:
					fileFormat = "unexpected_fileFormat";
					storageType = "unexpected_storageType";
					tableId = 100000000000000000L;
			}
			Table_storage_info tableStorageInfo = new Table_storage_info();
			tableStorageInfo.setStorage_id(storageId);
			tableStorageInfo.setFile_format(fileFormat);
			tableStorageInfo.setStorage_type(storageType);
			tableStorageInfo.setIs_zipper(IsFlag.Shi.getCode());
			tableStorageInfo.setTable_id(tableId);

			tableStorageInfos.add(tableStorageInfo);
		}
		//10、构造table_clean表测试数据
		List<Table_clean> sysUserCleans = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			long id = i % 2 == 0 ? 1357L : 2468L;
			String cleanType;
			switch (i){
				case 1 :
					cleanType = CleanType.ZiFuHeBing.getCode();
					break;
				case 2 :
					cleanType = CleanType.ZiFuTrim.getCode();
					break;
				default:
					cleanType = "unexpected_cleanType";
			}
			Table_clean sysUserClean = new Table_clean();
			sysUserClean.setTable_clean_id(id);
			sysUserClean.setClean_type(cleanType);
			sysUserClean.setTable_id(SYS_USER_TABLE_ID);

			sysUserCleans.add(sysUserClean);
		}

		List<Table_clean> codeInfoCleans = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			long id = i % 2 == 0 ? 1470L : 2581L;
			String cleanType;
			String completeType;
			String completeChar;
			long completeLength = 0L;
			String oldField = null;
			String newField = null;
			switch (i){
				case 1 :
					cleanType = CleanType.ZiFuTiHuan.getCode();
					completeType = "";
					completeChar = "";
					oldField = "abc";
					newField = "def";
					break;
				case 2 :
					cleanType = CleanType.ZiFuBuQi.getCode();
					completeType = FillingType.QianBuQi.getCode();
					completeChar = "beyond";
					completeLength = 6;
					break;
				default:
					cleanType = "unexpected_cleanType";
					completeType = "unexpected_completeType";
					completeChar = "unexpected_completeChar";
					oldField = "unexpected_oldField";
					newField = "unexpected_newField";
			}
			Table_clean codeInfoClean = new Table_clean();
			codeInfoClean.setTable_clean_id(id);
			codeInfoClean.setClean_type(cleanType);
			codeInfoClean.setFilling_type(completeType);
			codeInfoClean.setCharacter_filling(completeChar);
			codeInfoClean.setFilling_length(completeLength);
			codeInfoClean.setField(oldField);
			codeInfoClean.setReplace_feild(newField);
			codeInfoClean.setTable_id(CODE_INFO_TABLE_ID);

			codeInfoCleans.add(codeInfoClean);
		}
		//11、构造column_merge表测试数据
		List<Column_merge> sysUserMerge = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			long id = i % 2 == 0 ? 1573L : 8848L;
			String afterColName;
			String beforeColName;
			String afterColChName;
			String afterColType;
			switch (i){
				case 1 :
					afterColName = "user_create_id";
					beforeColName = "user_id" + "|" + "create_id";
					afterColChName = "用户_创建者_ID";
					afterColType = "varchar(1024)";
					break;
				case 2 :
					afterColName = "user_name_password";
					beforeColName = "user_name" + "|" + "user_password";
					afterColChName = "用户名_密码";
					afterColType = "varchar(1024)";
					break;
				default:
					afterColName = "unexpected_afterColName";
					beforeColName = "unexpected_beforeColName";
					afterColChName = "unexpected_afterColChName";
					afterColType = "unexpected_afterColType";
			}
			Column_merge columnMerge = new Column_merge();
			columnMerge.setCol_merge_id(id);
			columnMerge.setCol_name(afterColName);
			columnMerge.setOld_name(beforeColName);
			columnMerge.setCol_zhname(afterColChName);
			columnMerge.setCol_type(afterColType);
			columnMerge.setValid_s_date(DateUtil.getSysDate());
			columnMerge.setValid_e_date(Constant.MAXDATE);
			columnMerge.setTable_id(SYS_USER_TABLE_ID);

			sysUserMerge.add(columnMerge);
		}

		List<Column_merge> codeInfoMerge = new ArrayList<>();
		Column_merge codeInfo = new Column_merge();
		codeInfo.setCol_merge_id(8963L);
		codeInfo.setCol_name("ci_sp_classname_name");
		codeInfo.setOld_name("ci_sp_classname|ci_sp_name");
		codeInfo.setCol_zhname("类别|代码名称");
		codeInfo.setCol_type("VARCHAR(255)");
		codeInfo.setValid_s_date(DateUtil.getSysDate());
		codeInfo.setValid_e_date(Constant.MAXDATE);
		codeInfo.setTable_id(CODE_INFO_TABLE_ID);

		codeInfoMerge.add(codeInfo);

		//5、由于该Action类的测试连接功能需要与agent端交互，所以需要配置一条agent_down_info表的记录，用于找到http访问的完整url
		Agent_down_info agentDownInfo = BaseInitData.initAgentDownInfoTwo();

		//12、插入数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//插入用户表(sys_user)测试数据
			int userCount = user.add(db);
			assertThat("用户表测试数据初始化", userCount, is(1));

			//插入部门表(department_info)测试数据
			int deptCount = departmentInfo.add(db);
			assertThat("部门表测试数据初始化", deptCount, is(1));

			//插入数据源表(data_source)测试数据
			int dataSourceCount = dataSource.add(db);
			assertThat("数据源测试数据初始化", dataSourceCount, is(1));

			//插入Agent信息表(agent_info)测试数据
			int agentInfoCount = 0;
			for(Agent_info agentInfo : agents){
				int count = agentInfo.add(db);
				agentInfoCount += count;
			}
			assertThat("Agent测试数据初始化", agentInfoCount, is(2));

			//插入database_set表测试数据
			int databaseSetCount = databaseSet.add(db);
			assertThat("数据库设置测试数据初始化", databaseSetCount, is(1));

			//插入collect_job_classify表测试数据
			int classifyCount = 0;
			for(Collect_job_classify classify : classifies){
				int count = classify.add(db);
				classifyCount += count;
			}
			assertThat("采集任务分类表测试数据初始化", classifyCount, is(2));

			//插入table_info测试数据
			int tableInfoCount = 0;
			for(Table_info tableInfo : tableInfos){
				int count = tableInfo.add(db);
				tableInfoCount += count;
			}
			assertThat("数据库对应表测试数据初始化", tableInfoCount, is(4));

			//插入table_column测试数据
			int sysUserCount = 0;
			for(Table_column tableColumn : sysUsers){
				int count = tableColumn.add(db);
				sysUserCount += count;
			}
			assertThat("sys_user表对应字段表测试数据初始化", sysUserCount, is(10));
			int codeInfoCount = 0;
			for(Table_column tableColumn : codeInfos){
				int count = tableColumn.add(db);
				codeInfoCount += count;
			}
			assertThat("code_info表对应字段表测试数据初始化", codeInfoCount, is(5));
			int agentInfosCount = 0;
			for(Table_column tableColumn : agentInfos){
				int count = tableColumn.add(db);
				agentInfosCount += count;
			}
			assertThat("agent_info表对应字段表测试数据初始化", agentInfosCount, is(3));
			int dataSourcesCount = 0;
			for(Table_column tableColumn : dataSources){
				int count = tableColumn.add(db);
				dataSourcesCount += count;
			}
			assertThat("data_source表对应字段表测试数据初始化", dataSourcesCount, is(3));

			//插入table_storage_info测试数据
			for(Table_storage_info storageInfo : tableStorageInfos){
				storageInfo.add(db);
			}
			assertThat("表存储信息表测试数据初始化", tableStorageInfos.size(), is(2));

			//插入table_clean测试数据
			for(Table_clean tableClean : sysUserCleans){
				tableClean.add(db);
			}
			for(Table_clean tableClean : codeInfoCleans){
				tableClean.add(db);
			}
			assertThat("表清洗参数信息表测试数据初始化", sysUserCleans.size() + codeInfoCleans.size(), is(4));

			//插入column_merge测试数据
			for(Column_merge columnMerge : sysUserMerge){
				columnMerge.add(db);
			}
			for(Column_merge columnMerge : codeInfoMerge){
				columnMerge.add(db);
			}
			assertThat("列合并信息表测试数据初始化", sysUserMerge.size() + codeInfoMerge.size(), is(3));

			//插入agent_down_info表测试数据
			int agentDownInfoCount = agentDownInfo.add(db);
			assertThat("Agent下载信息表测试数据初始化", agentDownInfoCount, is(1));

			SqlOperator.commitTransaction(db);
		}
	}

	public static void after(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//删除用户表(sys_user)测试数据
			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//删除部门表(department_info)测试数据
			SqlOperator.execute(db, "delete from " + Department_info.TableName + " WHERE dep_id = ?", TEST_DEPT_ID);
			//1、删除数据源表(data_source)测试数据
			SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID);
			//2、删除Agent信息表(agent_info)测试数据
			SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//3、删除database_set表测试数据
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", SECOND_DB_AGENT_ID);
			//4、删除collect_job_classify表测试数据
			SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//5、删除table_info表测试数据
			SqlOperator.execute(db, "delete from " + Table_info.TableName + " where database_id = ? ", FIRST_DATABASESET_ID);
			//6、删除table_column表测试数据
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", AGENT_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", DATA_SOURCE_TABLE_ID);
			//7、删除table_storage_info表测试数据
			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			//8、删除table_clean表测试数据
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			//9、删除column_merge表测试数据
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			//10、删除agent_down_info表测试数据
			SqlOperator.execute(db, "delete from " + Agent_down_info.TableName + " where down_id = ? ", AGENT_DOWN_INFO_ID + 1);
			//11、提交事务
			SqlOperator.commitTransaction(db);
		}
	}

}
