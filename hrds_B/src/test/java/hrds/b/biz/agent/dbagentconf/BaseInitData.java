package hrds.b.biz.agent.dbagentconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "为数据库直连采集测试用例构造公共的测试数据，仅本功能自用", author = "WangZhengcheng")
public class BaseInitData {

	//测试数据用户ID
	private static final long TEST_USER_ID = 9997L;
	//测试用户密码
	private static final String TEST_USER_PASSWORD = "test_user";
	//测试部门ID
	private static final long TEST_DEPT_ID = 9987L;

	//source_id
	private static final long SOURCE_ID = 1L;

	private static final long FIRST_DB_AGENT_ID = 7001L;
	private static final long SECOND_DB_AGENT_ID = 7002L;

	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long SECOND_DATABASESET_ID = 1002L;

	private static final long FIRST_CLASSIFY_ID = 10086L;
	private static final long SECOND_CLASSIFY_ID = 10010L;

	private static final long BASE_CODE_INFO_PRIMARY = 3000L;

	private static final long CODE_INFO_TABLE_ID = 7002L;

	//构造默认表清洗优先级
	public static JSONObject initTableCleanOrder(){
		JSONObject tableCleanOrder = new JSONObject();

		tableCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		tableCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		tableCleanOrder.put(CleanType.ZiFuHeBing.getCode(), 3);
		tableCleanOrder.put(CleanType.ZiFuTrim.getCode(), 4);

		return tableCleanOrder;
	}

	//构造默认列清洗优先级
	public static JSONObject initColumnCleanOrder(){
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
	public static Sys_user buildSysUserData(){
		Sys_user user = new Sys_user();
		user.setUser_id(TEST_USER_ID);
		user.setCreate_id(TEST_USER_ID);
		user.setDep_id(TEST_DEPT_ID);
		user.setRole_id("1001");
		user.setUser_name("超级管理员init-wzc");
		user.setUser_password(TEST_USER_PASSWORD);
		user.setUseris_admin("0");
		user.setUser_type("00");
		user.setUsertype_group(null);
		user.setLogin_ip("127.0.0.1");
		user.setLogin_date("20191001");
		user.setUser_state("1");
		user.setCreate_date(DateUtil.getSysDate());
		user.setCreate_time(DateUtil.getSysTime());
		user.setUpdate_date(DateUtil.getSysDate());
		user.setUpdate_time(DateUtil.getSysTime());
		user.setToken("0");
		user.setValid_time("0");

		return user;
	}
	//构造department_info表数据库直连采集配置公共测试数据
	public static Department_info buildDeptInfoData(){
		Department_info deptInfo = new Department_info();
		deptInfo.setDep_id(TEST_DEPT_ID);
		deptInfo.setDep_name("测试系统参数类部门init-wzc");
		deptInfo.setCreate_date(DateUtil.getSysDate());
		deptInfo.setCreate_time(DateUtil.getSysTime());
		deptInfo.setDep_remark("测试系统参数类部门init-wzc");

		return deptInfo;
	}

	//构造data_source表数据库直连采集配置公共测试数据
	public static Data_source buildDataSourceData(){
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(SOURCE_ID);
		dataSource.setDatasource_number("ds_");
		dataSource.setDatasource_name("wzctest_");
		dataSource.setDatasource_remark("wzctestremark_");
		dataSource.setCreate_date(DateUtil.getSysDate());
		dataSource.setCreate_time(DateUtil.getSysTime());
		dataSource.setCreate_user_id(TEST_USER_ID);

		return dataSource;
	}

	//构造agent_info表测试数据
	public static List<Agent_info> buildAgentInfosData(){
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
			agentInfo.setAgent_name("agent_" + i);
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
	public static List<Database_set> buildDbSetData(){
		List<Database_set> databases = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long agentId = i % 2 == 0 ? FIRST_DB_AGENT_ID : SECOND_DB_AGENT_ID;
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long id = i % 2 == 0 ? FIRST_DATABASESET_ID : SECOND_DATABASESET_ID;
			String databaseType = i % 2 == 0 ? DatabaseType.DB2.getCode() : DatabaseType.Postgresql.getCode();
			String databaseName = i % 2 == 0 ? "" : "postgresql";
			String databasePwd = i % 2 == 0 ? "" : "postgresql";
			String driver = i % 2 == 0 ? "" : "org.postgresql.Driver";
			String ip = i % 2 == 0 ? "" : "127.0.0.1";
			String port = i % 2 == 0 ? "" : "8888";
			String databaseCode = i % 2 == 0 ? "" : "1";
			String url = i % 2 == 0 ? "" : "jdbc:postgresql://127.0.0.1:8888/postgresql";
			String dbfileFormat = i % 2 == 0 ? "1" : "";
			String isHidden = i % 2 == 0 ? IsFlag.Fou.getCode() : IsFlag.Shi.getCode();
			String fileSuffix = i % 2 == 0 ? "dat" : "";
			String planeUrl = i % 2 == 0 ? "/home/hyrenshufu/wzc/test/data" : "";
			String rowSeparator = i % 2 == 0 ? "|" : "";
			String dbFlag = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			String userName = i % 2 == 0 ? "" : "hrsdxg";
			String isSendOk = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(id);
			databaseSet.setAgent_id(agentId);
			databaseSet.setDatabase_number("dbtest" + i);
			databaseSet.setDb_agent(dbFlag);
			databaseSet.setIs_load(IsFlag.Shi.getCode());
			databaseSet.setIs_hidden(isHidden);
			databaseSet.setIs_sendok(isSendOk);
			databaseSet.setIs_header(IsFlag.Shi.getCode());
			databaseSet.setClassify_id(classifyId);
			databaseSet.setTask_name("wzcTaskName" + i);
			databaseSet.setDatabase_type(databaseType);
			databaseSet.setDatabase_name(databaseName);
			databaseSet.setDatabase_drive(driver);
			databaseSet.setDatabase_ip(ip);
			databaseSet.setDatabase_port(port);
			databaseSet.setDatabase_code(databaseCode);
			databaseSet.setJdbc_url(url);
			databaseSet.setDbfile_format(dbfileFormat);
			databaseSet.setFile_suffix(fileSuffix);
			databaseSet.setPlane_url(planeUrl);
			databaseSet.setRow_separator(rowSeparator);
			databaseSet.setDatabase_pad(databasePwd);
			databaseSet.setUser_name(userName);

			databases.add(databaseSet);
		}

		return databases;
	}

	public static List<Collect_job_classify> buildClassifyData(){
		List<Collect_job_classify> classifies = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			Collect_job_classify classify = new Collect_job_classify();
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long agentId = i % 2 == 0 ? FIRST_DB_AGENT_ID : SECOND_DB_AGENT_ID;
			String remark = "remark" + String.valueOf(classifyId);
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

	//构建采集code_info表在table_column表中的数据
	public static List<Table_column> buildCodeInfoTbColData(){
		List<Table_column> codeInfos = new ArrayList<>();
		for(int i = 1; i <= 5; i++){
			String primaryKeyFlag;
			String columnName;
			String columnType;
			String columnChName;
			switch (i){
				case 1 :
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "ci_sp_code";
					columnType = "varchar";
					columnChName = "ci_sp_code";
					break;
				case 2 :
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "ci_sp_class";
					columnType = "varchar";
					columnChName = "ci_sp_class";
					break;
				case 3 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_classname";
					columnType = "varchar";
					columnChName = "ci_sp_classname";
					break;
				case 4 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_name";
					columnType = "varchar";
					columnChName = "ci_sp_name";
					break;
				case 5 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_remark";
					columnType = "varchar";
					columnChName = "ci_sp_remark";
					break;
				default:
					primaryKeyFlag = "unexpected_primaryKeyFlag";
					columnName = "unexpected_columnName";
					columnType = "unexpected_columnType";
					columnChName = "unexpected_columnChName";
			}
			Table_column codeInfoColumn = new Table_column();
			codeInfoColumn.setColumn_id(BASE_CODE_INFO_PRIMARY + i);
			codeInfoColumn.setIs_get(IsFlag.Shi.getCode());
			codeInfoColumn.setIs_primary_key(primaryKeyFlag);
			codeInfoColumn.setColume_name(columnName);
			codeInfoColumn.setColumn_type(columnType);
			codeInfoColumn.setColume_ch_name(columnChName);
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

	public static ActionResult simulatedLogin(){
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", TEST_USER_ID)
				.addData("password", TEST_USER_PASSWORD)
				.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login").getBodyString();
		return JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(() -> new BusinessException("连接失败"));
	}
}
