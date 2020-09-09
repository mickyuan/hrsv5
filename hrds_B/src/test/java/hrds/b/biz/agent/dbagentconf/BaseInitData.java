package hrds.b.biz.agent.dbagentconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Orig_code_info;
import hrds.commons.entity.Orig_syso_info;
import hrds.commons.entity.Table_column;
import hrds.commons.utils.Constant;
import hrds.commons.utils.ParallerTestUtil;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.ArrayList;
import java.util.List;


@DocClass(desc = "为数据库直连采集测试用例构造公共的测试数据，仅本功能自用", author = "WangZhengcheng")
public class BaseInitData {

	/**
	 * 测试线程ID
	 */
	public final long threadId = Thread.currentThread().getId();
	/**
	 * 测试数据用户ID
	 */
	public final long TEST_USER_ID = ParallerTestUtil.TESTINITCONFIG.getLong("user_id", 2001) + threadId;
	/**
	 * 测试部门ID
	 */
	public final long TEST_DEPT_ID = PrimayKeyGener.getNextId();
	/**
	 * 数据源ID
	 */

	public final long SOURCE_ID = PrimayKeyGener.getNextId();
	/**
	 * 第二个DB AgentID
	 */
	public final long SECOND_DB_AGENT_ID = PrimayKeyGener.getNextId();
	/**
	 * 第一个DB AgentID
	 */
	public final long FIRST_DB_AGENT_ID = PrimayKeyGener.getNextId();
	/**
	 * 第一个任务采集ID
	 */
	public final long FIRST_DATABASE_SET_ID = PrimayKeyGener.getNextId();
	/**
	 * 第二个任务采集ID
	 */
	public final long SECOND_DATABASE_SET_ID = PrimayKeyGener.getNextId();
	/**
	 * 第一个分类ID
	 */
	public final long FIRST_CLASSIFY_ID = PrimayKeyGener.getNextId();
	/**
	 * 第二个分类ID
	 */
	public final long SECOND_CLASSIFY_ID = PrimayKeyGener.getNextId();
	/**
	 * 采集表:AGENT_INFO表的ID
	 */
	public final long AGENT_INFO_TABLE_ID = PrimayKeyGener.getNextId();
	/**
	 * 采集表:DATA_SOURCE表的ID
	 */
	public final long DATA_SOURCE_TABLE_ID = PrimayKeyGener.getNextId();
	/**
	 * 采集表:CODE_INFO表的ID
	 */
	public final long CODE_INFO_TABLE_ID = PrimayKeyGener.getNextId();
	/**
	 * 采集表:SYS_USER表的ID
	 */
	public final long SYS_USER_TABLE_ID = PrimayKeyGener.getNextId();
	/**
	 * Agent下载ID信息
	 */
	public final long AGENT_DOWN_INFO_ID = PrimayKeyGener.getNextId();

	/**
	 * 存储表信息ID1
	 */
	public final long FIRST_STORAGE_ID = PrimayKeyGener.getNextId();
	/**
	 * 存储表信息ID２
	 */
	public final long SECOND_STORAGE_ID = PrimayKeyGener.getNextId();
	/**
	 * 存储表信息ID３
	 */
	public final long THIRD_STORAGE_ID = PrimayKeyGener.getNextId();
	/**
	 * 存储表信息ID４
	 */
	public final long FOUTH_STORAGE_ID = PrimayKeyGener.getNextId();
	/**
	 * 表抽取定义ID
	 */
	public final long BASE_DATA_EXTRACTION_DEF = PrimayKeyGener.getNextId();

	/**
	 * 表字段存储关系附加信息ID
	 */
	public final long PRIMARY_KEY_DSLAD_ID = PrimayKeyGener.getNextId();
	/**
	 * 数据表存储关系表配置主键
	 */
	public final long DATABASE_DSL_ID = PrimayKeyGener.getNextId();
	/**
	 * 表抽取定义ID
	 */
	public final long BASE_SYS_USER_PRIMARY = PrimayKeyGener.getNextId();

	/**
	 * 构造默认表清洗优先级
	 */
	public JSONObject initTableCleanOrder() {
		JSONObject tableCleanOrder = new JSONObject();

		tableCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		tableCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		tableCleanOrder.put(CleanType.ZiFuHeBing.getCode(), 3);
		tableCleanOrder.put(CleanType.ZiFuTrim.getCode(), 4);

		return tableCleanOrder;
	}

	//构造默认列清洗优先级
	public JSONObject initColumnCleanOrder() {
		JSONObject columnCleanOrder = new JSONObject();

		columnCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		columnCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		columnCleanOrder.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
		columnCleanOrder.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
		columnCleanOrder.put(CleanType.ZiFuChaiFen.getCode(), 5);
		columnCleanOrder.put(CleanType.ZiFuTrim.getCode(), 6);

		return columnCleanOrder;
	}

	//构造sys_user表数据库直连采集配置公共测试数据
//	public static Sys_user buildSysUserData() {
//		Sys_user user = new Sys_user();
//		user.setUser_id(TEST_USER_ID);
//		user.setCreate_id(TEST_USER_ID);
//		user.setDep_id(TEST_DEPT_ID);
//		user.setRole_id("1001");
//		user.setUser_name("超级管理员init-wzc");
//		user.setUser_password(TEST_USER_PASSWORD);
//		user.setUseris_admin("0");
//		user.setUser_type("00");
//		user.setUsertype_group(null);
//		user.setLogin_ip("127.0.0.1");
//		user.setLogin_date("20191001");
//		user.setUser_state("1");
//		user.setCreate_date(DateUtil.getSysDate());
//		user.setCreate_time(DateUtil.getSysTime());
//		user.setUpdate_date(DateUtil.getSysDate());
//		user.setUpdate_time(DateUtil.getSysTime());
//		user.setToken("0");
//		user.setValid_time("0");
//
//		return user;
//	}

	//构造department_info表数据库直连采集配置公共测试数据
	public Department_info buildDeptInfoData() {
		Department_info deptInfo = new Department_info();
		deptInfo.setDep_id(TEST_DEPT_ID);
		deptInfo.setDep_name("测试系统参数类部门init-wzc" + threadId);
		deptInfo.setCreate_date(DateUtil.getSysDate());
		deptInfo.setCreate_time(DateUtil.getSysTime());
		deptInfo.setDep_remark("测试系统参数类部门init-wzc" + threadId);

		return deptInfo;
	}

	//构造data_source表数据库直连采集配置公共测试数据
	public Data_source buildDataSourceData() {
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(SOURCE_ID);
		dataSource.setDatasource_number("ds_" + threadId);
		dataSource.setDatasource_name("wzc_" + threadId);
		dataSource.setDatasource_remark("wzctestremark_" + threadId);
		dataSource.setCreate_date(DateUtil.getSysDate());
		dataSource.setCreate_time(DateUtil.getSysTime());
		dataSource.setCreate_user_id(TEST_USER_ID);

		return dataSource;
	}

	//构造agent_info表测试数据
	public List<Agent_info> buildAgentInfosData() {
		List<Agent_info> agents = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			String agentType = null;
			long agentId = 0L;
			switch (i) {
				case 1:
					agentType = AgentType.ShuJuKu.getCode();
					agentId = FIRST_DB_AGENT_ID;
					break;
				case 2:
					agentType = AgentType.ShuJuKu.getCode();
					agentId = SECOND_DB_AGENT_ID;
					break;
			}
			Agent_info agentInfo = new Agent_info();
			agentInfo.setAgent_id(agentId);
			agentInfo.setAgent_name("agent_" + threadId);
			agentInfo.setAgent_type(agentType);
			agentInfo.setAgent_ip("127.0.0.1");
			agentInfo.setAgent_port("55555");
			agentInfo.setAgent_status(AgentStatus.WeiLianJie.getCode());
			agentInfo.setCreate_date(DateUtil.getSysDate());
			agentInfo.setCreate_time(DateUtil.getSysTime());
			agentInfo.setUser_id(TEST_USER_ID);
			agentInfo.setSource_id(SOURCE_ID);

			agents.add(agentInfo);
		}

		return agents;
	}

	//构造database_set表测试数据
	public List<Database_set> buildDbSetData() {
		List<Database_set> databases = new ArrayList<>();
		String dabaseNum = "cs01";
		String dabaseNum2 = "cs02";
		for (int i = 0; i < 2; i++) {
			long agentId = i % 2 == 0 ? FIRST_DB_AGENT_ID : SECOND_DB_AGENT_ID;
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long id = i % 2 == 0 ? FIRST_DATABASE_SET_ID : SECOND_DATABASE_SET_ID;
			String databaseType = i % 2 == 0 ? DatabaseType.DB2.getCode() : DatabaseType.Postgresql.getCode();
			String database_number = (i % 2 == 0 ? dabaseNum : dabaseNum2) + threadId;
			String databaseName = i % 2 == 0 ? "" : "postgresql";
			String databasePwd = i % 2 == 0 ? "" : "postgresql";
			String driver = i % 2 == 0 ? "" : "org.postgresql.Driver";
			String ip = i % 2 == 0 ? "" : "127.0.0.1";
			String port = i % 2 == 0 ? "" : "8888";
			String url = i % 2 == 0 ? "" : "jdbc:postgresql://127.0.0.1:8888/postgresql";
			String planeUrl = i % 2 == 0 ? "/home/hyrenshufu/wzc/test/data" : "";
			String rowSeparator = i % 2 == 0 ? "|" : "";
			String dbFlag = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			String userName = i % 2 == 0 ? "" : "hrsdxg";
			String isSendOk = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(id);
			databaseSet.setAgent_id(agentId);
			databaseSet.setDatabase_number(database_number);
			databaseSet.setDb_agent(dbFlag);
			databaseSet.setIs_sendok(isSendOk);
			databaseSet.setClassify_id(classifyId);
			databaseSet.setTask_name("wzcTaskName" + threadId);
			databaseSet.setDatabase_type(databaseType);
			databaseSet.setDatabase_name(databaseName);
			databaseSet.setDatabase_drive(driver);
			databaseSet.setDatabase_ip(ip);
			databaseSet.setDatabase_port(port);
			databaseSet.setJdbc_url(url);
			databaseSet.setPlane_url(planeUrl);
			databaseSet.setRow_separator(rowSeparator);
			databaseSet.setDatabase_pad(databasePwd);
			databaseSet.setUser_name(userName);
			databaseSet.setCollect_type(CollectType.ShuJuKuChouShu.getCode());
			databases.add(databaseSet);
		}

		return databases;
	}

	public List<Collect_job_classify> buildClassifyData() {
		List<Collect_job_classify> classifies = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Collect_job_classify classify = new Collect_job_classify();
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long agentId = i % 2 == 0 ? FIRST_DB_AGENT_ID : SECOND_DB_AGENT_ID;
			String remark = "remark" + classifyId;
			classify.setClassify_id(classifyId);
			classify.setClassify_num("wzc_test_classify_num" + i);
			classify.setClassify_name("wzc_test_classify_name" + i);
			classify.setUser_id(TEST_USER_ID);
			classify.setAgent_id(agentId);
			classify.setRemark(remark);

			classifies.add(classify);
		}

		return classifies;
	}

	/**
	 * 构建采集agent_info表在table_column表主键集合
	 */
	public final List<Long> AGENT_INFO_COLUMN_ID_LIST = new ArrayList<>();

	//构建采集agent_info表在table_column表中的数据
	public List<Table_column> buildAgentInfoTbColData() {
		List<Table_column> agentInfos = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String columnName = null;
			String columnType = null;
			String columnChName = null;
			switch (i) {
				case 1:
					columnName = "agent_id";
					columnType = "bigint";
					columnChName = "agent_id";
					break;
				case 2:
					columnName = "agent_name";
					columnType = "varchar(512)";
					columnChName = "Agent名称";
					break;
				case 3:
					columnName = "agent_type";
					columnType = "char(1)";
					columnChName = "agent类别";
					break;
			}
			Table_column agentInfoColumn = new Table_column();
			Long nextId = PrimayKeyGener.getNextId();
			AGENT_INFO_COLUMN_ID_LIST.add(nextId);
			agentInfoColumn.setColumn_id(nextId);
			agentInfoColumn.setIs_get(IsFlag.Shi.getCode());
			agentInfoColumn.setIs_primary_key(IsFlag.Fou.getCode());
			agentInfoColumn.setColumn_name(columnName);
			agentInfoColumn.setColumn_type(columnType);
			agentInfoColumn.setColumn_ch_name(columnChName);
			agentInfoColumn.setTable_id(AGENT_INFO_TABLE_ID);
			agentInfoColumn.setValid_s_date(DateUtil.getSysDate());
			agentInfoColumn.setValid_e_date(Constant.MAXDATE);
			agentInfoColumn.setIs_alive(IsFlag.Shi.getCode());
			agentInfoColumn.setIs_new(IsFlag.Fou.getCode());
			agentInfoColumn.setTc_or(initColumnCleanOrder().toJSONString());

			agentInfos.add(agentInfoColumn);
		}

		return agentInfos;
	}

	/**
	 * 构建采集data_source表在table_column表中的主键集合
	 */
	public final List<Long> DATASOURCE_COLUMN_ID_LIST = new ArrayList<>();

	//构建采集data_source表在table_column表中的数据
	public List<Table_column> buildDataSourceTbColData() {
		List<Table_column> dataSources = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String columnName = null;
			String columnType = null;
			String columnChName = null;
			switch (i) {
				case 1:
					columnName = "source_id";
					columnType = "bigint";
					columnChName = "数据源ID";
					break;
				case 2:
					columnName = "datasource_number";
					columnType = "varchar(100)";
					columnChName = "数据源编号";
					break;
				case 3:
					columnName = "datasource_name";
					columnType = "varchar(512)";
					columnChName = "数据源名称";
					break;
			}
			Table_column dataSourceColumn = new Table_column();
			Long nextId = PrimayKeyGener.getNextId();
			DATASOURCE_COLUMN_ID_LIST.add(nextId);
			dataSourceColumn.setColumn_id(nextId);
			dataSourceColumn.setIs_get(IsFlag.Shi.getCode());
			dataSourceColumn.setIs_primary_key(IsFlag.Fou.getCode());
			dataSourceColumn.setColumn_name(columnName);
			dataSourceColumn.setColumn_type(columnType);
			dataSourceColumn.setColumn_ch_name(columnChName);
			dataSourceColumn.setTable_id(DATA_SOURCE_TABLE_ID);
			dataSourceColumn.setValid_s_date(DateUtil.getSysDate());
			dataSourceColumn.setValid_e_date(Constant.MAXDATE);
			dataSourceColumn.setIs_alive(IsFlag.Shi.getCode());
			dataSourceColumn.setIs_new(IsFlag.Fou.getCode());
			dataSourceColumn.setTc_or(initColumnCleanOrder().toJSONString());

			dataSources.add(dataSourceColumn);
		}

		return dataSources;
	}

	/**
	 * 构建采集code_info表在table_column表中的主键集合
	 */
	public final List<Long> CODE_INFO_COLUMN_ID_LIST = new ArrayList<>();

	//构建采集code_info表在table_column表中的数据
	public List<Table_column> buildCodeInfoTbColData() {
		List<Table_column> codeInfos = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			String primaryKeyFlag = null;
			String columnName = null;
			String columnType = null;
			String columnChName = null;
			switch (i) {
				case 1:
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "ci_sp_code";
					columnType = "varchar";
					columnChName = "ci_sp_code";
					break;
				case 2:
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "ci_sp_class";
					columnType = "varchar";
					columnChName = "ci_sp_class";
					break;
				case 3:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_classname";
					columnType = "varchar";
					columnChName = "ci_sp_classname";
					break;
				case 4:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_name";
					columnType = "varchar";
					columnChName = "ci_sp_name";
					break;
				case 5:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_remark";
					columnType = "varchar";
					columnChName = "ci_sp_remark";
					break;
			}
			Table_column codeInfoColumn = new Table_column();
			Long nextId = PrimayKeyGener.getNextId();
			codeInfoColumn.setColumn_id(nextId);
			CODE_INFO_COLUMN_ID_LIST.add(nextId);
			codeInfoColumn.setIs_get(IsFlag.Shi.getCode());
			codeInfoColumn.setIs_primary_key(primaryKeyFlag);
			codeInfoColumn.setColumn_name(columnName);
			codeInfoColumn.setColumn_type(columnType);
			codeInfoColumn.setColumn_ch_name(columnChName);
			codeInfoColumn.setTable_id(CODE_INFO_TABLE_ID);
			codeInfoColumn.setValid_s_date(DateUtil.getSysDate());
			codeInfoColumn.setValid_e_date(Constant.MAXDATE);
			codeInfoColumn.setIs_alive(IsFlag.Shi.getCode());
			codeInfoColumn.setIs_new(IsFlag.Fou.getCode());
			codeInfoColumn.setTc_or(initColumnCleanOrder().toJSONString());

			codeInfos.add(codeInfoColumn);
		}

		return codeInfos;
	}

	/**
	 * 码值转换系统编号集合
	 */
	public List<String> ORIG_LIST = null;

	public List<Orig_syso_info> buildOrigSysInfo() {
		ORIG_LIST = new ArrayList<>();
		List<Orig_syso_info> origSysoInfos = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			String origSysCode = null;
			String origSysName = null;
			String origSysRemark = null;
			switch (i) {
				case 0:
					origSysCode = "origSysCode_one" + threadId;
					origSysName = "origSysName_one" + threadId;
					origSysRemark = "origSysRemark_one" + threadId;
					break;
				case 1:
					origSysCode = "origSysCode_two" + threadId;
					origSysName = "origSysName_two" + threadId;
					origSysRemark = "origSysRemark_two" + threadId;
					break;
				case 2:
					origSysCode = "origSysCode_three" + threadId;
					origSysName = "origSysName_three" + threadId;
					origSysRemark = "origSysRemark_three" + threadId;
					break;
			}
			Orig_syso_info origSysoInfo = new Orig_syso_info();
			origSysoInfo.setOrig_sys_code(origSysCode);
			ORIG_LIST.add(origSysCode);
			origSysoInfo.setOrig_sys_name(origSysName);
			origSysoInfo.setOrig_sys_remark(origSysRemark);

			origSysoInfos.add(origSysoInfo);
		}

		return origSysoInfos;
	}


	/**
	 * 原系统编码主键集合
	 */
	public List<Long> ORIG_CODE_LIST = null;

	public List<Orig_code_info> buildOrigCodeInfo() {
		ORIG_CODE_LIST = new ArrayList<>();
		List<Orig_code_info> origCodeInfos = new ArrayList<>();
		String origSysCode;
		String codeClassify = null;
		String newValue = null;
		String oriValue = null;
		for (int i = 0; i < ORIG_LIST.size(); i++) {
			long id = PrimayKeyGener.getNextId();
			ORIG_CODE_LIST.add(id);
			origSysCode = ORIG_LIST.get(i);
			switch (i) {
				case 0:
					codeClassify = "classify_one" + threadId;
					newValue = "newValue_one";
					oriValue = "oriValue_one";
					break;
				case 1:
					codeClassify = "classify_two" + threadId;
					newValue = "newValue_two" + threadId;
					oriValue = "oriValue_two" + threadId;
					break;
				case 2:
					codeClassify = "classify_three" + threadId;
					newValue = "newValue_three" + threadId;
					oriValue = "oriValue_three" + threadId;
					break;
			}
			Orig_code_info origCodeInfo = new Orig_code_info();
			origCodeInfo.setOrig_id(id);
			origCodeInfo.setOrig_sys_code(origSysCode);
			origCodeInfo.setCode_classify(codeClassify);
			origCodeInfo.setCode_value(newValue);
			origCodeInfo.setOrig_value(oriValue);

			origCodeInfos.add(origCodeInfo);
		}

		return origCodeInfos;
	}

	//由于该Action类的测试连接功能需要与agent端交互，所以需要配置一条agent_down_info表的记录，用于找到http访问的完整url
	public Agent_down_info initAgentDownInfo() {
		Agent_down_info agentDownInfo = new Agent_down_info();
		agentDownInfo.setDown_id(AGENT_DOWN_INFO_ID);
		agentDownInfo.setAgent_id(FIRST_DB_AGENT_ID);
		agentDownInfo.setUser_id(TEST_USER_ID);
		agentDownInfo.setAgent_name("test_agent_down_info_wzc");
		agentDownInfo.setAgent_ip("127.0.0.1");
		agentDownInfo.setAgent_port("55555");
		agentDownInfo.setSave_dir("/test/save/dir");
		agentDownInfo.setLog_dir("/test/log/dir");
		agentDownInfo.setDeploy(IsFlag.Shi.getCode());
		agentDownInfo.setAgent_context("/agent");
		agentDownInfo.setAgent_pattern("/receive/*");
		agentDownInfo.setAgent_type(AgentType.ShuJuKu.getCode());

		return agentDownInfo;
	}

	public Agent_down_info initAgentDownInfoTwo() {
		Agent_down_info agentDownInfo = new Agent_down_info();
		agentDownInfo.setDown_id(AGENT_DOWN_INFO_ID + threadId);
		agentDownInfo.setAgent_id(SECOND_DB_AGENT_ID);
		agentDownInfo.setUser_id(TEST_USER_ID);
		agentDownInfo.setAgent_name("test_agent_down_info_wzc");
		agentDownInfo.setAgent_ip("127.0.0.1");
		agentDownInfo.setAgent_port("55555");
		agentDownInfo.setSave_dir("/test/save/dir");
		agentDownInfo.setLog_dir("/test/log/dir");
		agentDownInfo.setDeploy(IsFlag.Shi.getCode());
		agentDownInfo.setAgent_context("/agent");
		agentDownInfo.setAgent_pattern("/receive/*");
		agentDownInfo.setAgent_type(AgentType.ShuJuKu.getCode());

		return agentDownInfo;
	}

}
