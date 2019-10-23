package hrds.b.biz.agent.dbagentconf.cleanconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "清洗规则Action测试类", author = "WangZhengcheng")
public class CleanConfStepActionTest extends WebBaseTestCase{

	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long CODE_INFO_TABLE_ID = 7002L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
	private static final long DATA_SOURCE_TABLE_ID = 7004L;
	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long SECOND_DATABASESET_ID = 1002L;
	private static final long BASE_SYS_USER_PRIMARY = 2000L;
	private static final long BASE_CODE_INFO_PRIMARY = 3000L;
	private static final long FIRST_DB_AGENT_ID = 8001L;
	private static final long SECOND_DB_AGENT_ID = 8002L;
	private static final long TEST_USER_ID = -9997L;
	private static final long SOURCE_ID = 1L;
	private static final long FIRST_CLASSIFY_ID = 10086L;
	private static final long SECOND_CLASSIFY_ID = 10010L;
	private static final long UNEXPECTED_ID = 999999999L;
	private static final String PRE_COMPLE_FLAG = "1";
	private static final String POST_COMPLE_FLAG = "2";
	private static final JSONObject tableCleanOrder = new JSONObject();
	private static final JSONObject columnCleanOrder = new JSONObject();

	static{
		tableCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		tableCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		tableCleanOrder.put(CleanType.ZiFuHeBing.getCode(), 3);
		tableCleanOrder.put(CleanType.ZiFuTrim.getCode(), 4);

		columnCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		columnCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		columnCleanOrder.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
		columnCleanOrder.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
		columnCleanOrder.put(CleanType.ZiFuChaiFen.getCode(), 5);
		columnCleanOrder.put(CleanType.ZiFuTrim.getCode(), 6);
	}

	/**
	 * 为每个方法的单元测试初始化测试数据
	 *
	 * 1、构造默认表清洗优先级
	 * 2、构造默认列清洗优先级
	 * 3、构造data_source表测试数据
	 * 4、构造agent_info表测试数据
	 * 5、构造database_set表测试数据
	 * 6、构造Collect_job_classify表测试数据
	 * 7、构建table_info测试数据
	 * 8、构建table_column表测试数据
	 *
	 * 12、插入数据
	 *
	 * 测试数据：
	 *      1、默认表清洗优先级为(字符补齐，字符替换，列合并，首尾去空)
	 *      2、默认列清洗优先级为(字符补齐，字符替换，日期格式转换，码值转换，列拆分，首尾去空)
	 *      3、data_source表：有1条数据，source_id为1
	 *      4、agent_info表：有2条数据,全部是数据库采集Agent，agent_id分别为7001，7002,source_id为1
	 *      5、database_set表：有2条数据,database_id为1001,1002, agent_id分别为7001,7002，1001的classifyId是10086，1002的classifyId是10010
	 *      1001设置完成并发送成功(is_sendok)
	 *      6、collect_job_classify表：有2条数据，classify_id为10086L、10010L，agent_id分别为7001L、7002L,user_id为-9997L
	 *      7、table_info表测试数据共4条，databaseset_id为1001
	 *          7-1、table_id:7001,table_name:sys_user,按照画面配置信息进行采集
	 *          7-2、table_id:7002,table_name:code_info,按照画面配置信息进行采集
	 *          7-3、table_id:7003,table_name:agent_info,按照自定义SQL进行采集
	 *          7-4、table_id:7004,table_name:data_source,按照自定义SQL进行采集
	 *      8、table_column表测试数据：只有在画面上进行配置的采集表才会向table_column表中保存数据
	 *          8-1、column_id为2001-2010，模拟采集了sys_user表的前10个列，列名为user_id，create_id，dep_id，role_id，
	 *               user_name，user_password，user_email，user_mobile，useris_admin，user_type，设置了remark字段，也就是采集顺序，分别是1、2、3、4、5、6、7、8、9、10
	 *          8-2、column_id为3001-3005，模拟采集了code_info表的所有列，列名为ci_sp_code，ci_sp_class，ci_sp_classname，
	 *               ci_sp_name，ci_sp_remark
	 *      9、table_clean表测试数据：
	 *          9-1、对sys_user表设置一个整表字符补齐规则
	 *          9-2、对sys_user表设置一个整表字符替换规则
     *      10、column_clean表测试数据
	 *          10-1、对sys_user表的create_id、dep_id列设置列字符补齐
	 *          10-2、对sys_user表的user_name列设置列字符替换
     *      11、clean_parameter表测试数据
	 *          11-1、对databaseset_id为1001的数据库直连采集作业设置一个全表的字符替换和字符补齐规则
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Before
	public void before(){
		//3、构造data_source表测试数据
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(SOURCE_ID);
		dataSource.setDatasource_number("ds_");
		dataSource.setDatasource_name("wzctest_");
		dataSource.setDatasource_remark("wzctestremark_");
		dataSource.setCreate_date(DateUtil.getSysDate());
		dataSource.setCreate_time(DateUtil.getSysTime());
		dataSource.setCreate_user_id(TEST_USER_ID);

		//4、构造agent_info表测试数据
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

		//5、构造database_set表测试数据
		List<Database_set> databases = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long agentId = i % 2 == 0 ? FIRST_DB_AGENT_ID : SECOND_DB_AGENT_ID;
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long id = i % 2 == 0 ? FIRST_DATABASESET_ID : SECOND_DATABASESET_ID;
			String isSendOk = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			String databaseType = DatabaseType.Postgresql.getCode();
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(id);
			databaseSet.setAgent_id(agentId);
			databaseSet.setDatabase_number("dbtest" + i);
			databaseSet.setDb_agent(IsFlag.Shi.getCode());
			databaseSet.setIs_load(IsFlag.Shi.getCode());
			databaseSet.setIs_hidden(IsFlag.Shi.getCode());
			databaseSet.setIs_sendok(isSendOk);
			databaseSet.setIs_header(IsFlag.Shi.getCode());
			databaseSet.setClassify_id(classifyId);
			databaseSet.setTask_name("wzcTaskName" + i);
			databaseSet.setDatabase_type(databaseType);
			databaseSet.setDatabase_ip("47.103.83.1");
			databaseSet.setDatabase_port("32001");
			databaseSet.setUser_name("hrsdxg");
			databaseSet.setDatabase_pad("hrsdxg");
			databaseSet.setDatabase_name("hrsdxg");
			databaseSet.setDatabase_drive("jdbc:postgresql://47.103.83.1:32001/hrsdxg");

			databases.add(databaseSet);
		}

		//6、构造Collect_job_classify表测试数据
		List<Collect_job_classify> classifies = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			Collect_job_classify classify = new Collect_job_classify();
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long agentId = i % 2 == 0 ? FIRST_DB_AGENT_ID : SECOND_DB_AGENT_ID;
			classify.setClassify_id(classifyId);
			classify.setClassify_num("wzc_test_classify_num" + i);
			classify.setClassify_name("wzc_test_classify_name" + i);
			classify.setUser_id(TEST_USER_ID);
			classify.setAgent_id(agentId);

			classifies.add(classify);
		}

		//7、构建table_info测试数据
		List<Table_info> tableInfos = new ArrayList<>();
		for(int i = 1; i <= 4; i++){
			long tableId;
			String tableName;
			String tableChName;
			String customizeSQL;
			String customizFlag;
			switch (i) {
				case 1:
					tableId = SYS_USER_TABLE_ID;
					tableName = "sys_user";
					tableChName = "用户表";
					customizeSQL = "";
					customizFlag = IsFlag.Fou.getCode();
					break;
				case 2:
					tableId = CODE_INFO_TABLE_ID;
					tableName = "code_info";
					tableChName = "代码信息表";
					customizeSQL = "";
					customizFlag = IsFlag.Fou.getCode();
					break;
				case 3:
					tableId = AGENT_INFO_TABLE_ID;
					tableName = "agent_info";
					tableChName = "Agent信息表";
					customizeSQL = "select * from agent_info";
					customizFlag = IsFlag.Shi.getCode();
					break;
				case 4:
					tableId = DATA_SOURCE_TABLE_ID;
					tableName = "data_source";
					tableChName = "数据源表";
					customizeSQL = "select * from data_source";
					customizFlag = IsFlag.Shi.getCode();
					break;
				default:
					tableId = 0L;
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
					customizFlag = "error_customizFlag";
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

			tableInfos.add(tableInfo);
		}

		//8、构建table_column表测试数据
		List<Table_column> sysUsers = new ArrayList<>();
		for(int i = 1; i <= 10; i++){
			String primaryKeyFlag;
			String columnName;
			String columnType;
			String columnChName;
			String remark;
			switch (i){
				case 1 :
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "user_id";
					columnType = "int8";
					columnChName = "主键";
					remark = "1";
					break;
				case 2 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "create_id";
					columnType = "int8";
					columnChName = "创建用户者ID";
					remark = "2";
					break;
				case 3 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "dep_id";
					columnType = "int8";
					columnChName = "部门ID";
					remark = "3";
					break;
				case 4 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "role_id";
					columnType = "int8";
					columnChName = "角色ID";
					remark = "4";
					break;
				case 5 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_name";
					columnType = "varchar";
					columnChName = "用户名";
					remark = "5";
					break;
				case 6 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_password";
					columnType = "varchar";
					columnChName = "密码";
					remark = "6";
					break;
				case 7 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_email";
					columnType = "varchar";
					columnChName = "邮箱";
					remark = "7";
					break;
				case 8 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_mobile";
					columnType = "varchar";
					columnChName = "电话";
					remark = "8";
					break;
				case 9 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "useris_admin";
					columnType = "char";
					columnChName = "是否管理员";
					remark = "9";
					break;
				case 10 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_type";
					columnType = "char";
					columnChName = "用户类型";
					remark = "10";
					break;
				default:
					primaryKeyFlag = "unexpected_primaryKeyFlag";
					columnName = "unexpected_columnName";
					columnType = "unexpected_columnType";
					columnChName = "unexpected_columnChName";
					remark = "unexpected_remark";
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
			sysUserColumn.setRemark(remark);

			sysUsers.add(sysUserColumn);
		}

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
			codeInfoColumn.setIs_new(IsFlag.Shi.getCode());
			codeInfoColumn.setTc_or(columnCleanOrder.toJSONString());

			codeInfos.add(codeInfoColumn);
		}

		//9、构造table_clean表测试数据
		List<Table_clean> tableCleans = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			long tbCleanId;
			String compleType = null;
			String cleanType = null;
			long tableId;
			String compleChar = null;
			long length;
			String oriField = null;
			String newField = null;
			switch (i){
				case 1 :
					tbCleanId = 11111L;
					compleType = PRE_COMPLE_FLAG;
					cleanType = CleanType.ZiFuBuQi.getCode();
					tableId = SYS_USER_TABLE_ID;
					compleChar = "wzc";
					length = 3L;
					oriField = "";
					newField = "";
					break;
				case 2 :
					tbCleanId = 111111L;
					compleType = "";
					cleanType = CleanType.ZiFuTiHuan.getCode();
					tableId = SYS_USER_TABLE_ID;
					compleChar = "";
					length = 0;
					oriField = "wzc";
					newField = "wqp";
					break;
				default:
					tbCleanId = UNEXPECTED_ID;
					compleType = "unexpected_compleType";
					cleanType = "cleanType";
					tableId = UNEXPECTED_ID;
					compleChar = "unexpected_compleChar";
					length = UNEXPECTED_ID;
					oriField = "unexpected_oriField";
					newField = "unexpected_oriField";
			}
			Table_clean tableClean = new Table_clean();
			tableClean.setTable_clean_id(tbCleanId);
			tableClean.setFilling_type(compleType);
			tableClean.setCharacter_filling(compleChar);
			tableClean.setFilling_length(length);
			tableClean.setClean_type(cleanType);
			tableClean.setTable_id(tableId);
			tableClean.setField(oriField);
			tableClean.setReplace_feild(newField);

			tableCleans.add(tableClean);
		}


		//10、构造column_clean表测试数据
		List<Column_clean> colComples = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			long colCleanId = i % 2 == 0 ? 22222L : 33333L;
			String cleanType = CleanType.ZiFuBuQi.getCode();
			String compleType = i % 2 == 0 ? PRE_COMPLE_FLAG : POST_COMPLE_FLAG;
			String compleChar = i % 2 == 0 ? "wzc" : " ";
			long length = i % 2 == 0 ? 3 : 1;
			long columnId = i % 2 == 0 ? 2002L : 2003L;

			Column_clean colComple = new Column_clean();
			colComple.setCol_clean_id(colCleanId);
			colComple.setClean_type(cleanType);
			colComple.setFilling_type(compleType);
			colComple.setCharacter_filling(compleChar);
			colComple.setFilling_length(length);
			colComple.setColumn_id(columnId);

			colComples.add(colComple);
		}

		//11、column_clean表测试数据
		Column_clean replace = new Column_clean();
		replace.setCol_clean_id(555555L);
		replace.setColumn_id(2005L);
		replace.setClean_type(CleanType.ZiFuTiHuan.getCode());
		replace.setField("ceshi");
		replace.setReplace_feild("test");

		//12、clean_parameter表测试数据
		List<Clean_parameter> cleanParameters = new ArrayList<>();
		for(int i = 0; i < 2 ;i++){
			long cId = i % 2 == 0 ? 666666L : 777777L;
			String cleanType = i % 2 == 0 ? CleanType.ZiFuBuQi.getCode() : CleanType.ZiFuTiHuan.getCode();
			String complChar;
			long compLength;
			String complType = null;
			String oriField;
			String newField;
			switch (i){
				case 0 :
					complChar = "cleanparameter";
					compLength = 14;
					complType = "1";
					oriField = "";
					newField = "";
					break;
				case 1:
					complChar = "";
					compLength = 0;
					oriField = "test_orifield";
					newField = "test_newField";
					break;
				default:
					complChar = "unexpected_complChar";
					compLength = 0;
					oriField = "unexpected_oriField";
					newField = "unexpected_newField";
			}
			Clean_parameter cleanParameter = new Clean_parameter();
			cleanParameter.setC_id(cId);
			cleanParameter.setDatabase_id(FIRST_DATABASESET_ID);
			cleanParameter.setClean_type(cleanType);
			cleanParameter.setFilling_type(complType);
			cleanParameter.setCharacter_filling(complChar);
			cleanParameter.setFilling_length(compLength);
			cleanParameter.setField(oriField);
			cleanParameter.setReplace_feild(newField);

			cleanParameters.add(cleanParameter);
		}

		//12、插入数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
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
			int databaseSetCount = 0;
			for(Database_set databaseSet : databases){
				int count = databaseSet.add(db);
				databaseSetCount += count;
			}
			assertThat("数据库设置测试数据初始化", databaseSetCount, is(2));

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
			int tableColumnCount = 0;
			for(Table_column tableColumn : sysUsers){
				int count = tableColumn.add(db);
				tableColumnCount += count;
			}
			for(Table_column tableColumn : codeInfos){
				int count = tableColumn.add(db);
				tableColumnCount += count;
			}
			assertThat("表对应字段表测试数据初始化", tableColumnCount, is(15));

			//插入table_clean测试数据
			int tableCleanCount = 0;
			for(Table_clean tableClean : tableCleans){
				int count = tableClean.add(db);
				tableCleanCount += count;
			}
			assertThat("表清洗参数信息表测试数据初始化", tableCleanCount, is(2));

			//插入column_clean测试数据
			int columnCleanCount = 0;
			for(Column_clean colComple : colComples){
				int count = colComple.add(db);
				columnCleanCount += count;
			}
			int replaceCount = replace.add(db);
			columnCleanCount += replaceCount;
			assertThat("列清洗参数信息表测试数据初始化", columnCleanCount, is(3));

			//插入clean_parameter表测试数据
			int cleanParameterCount = 0;
			for(Clean_parameter cleanParameter : cleanParameters){
				int count = cleanParameter.add(db);
				cleanParameterCount += count;
			}
			assertThat("清洗作业参数属性表测试数据初始化", cleanParameterCount, is(2));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试根据数据库设置ID获得清洗规则配置页面初始信息
	 *
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到两条数据
	 * 错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
	 * 错误的测试用例未达到三组:getInitInfo只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getInitInfo(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到两条数据
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightData = rightResult.getDataForResult();
		assertThat("根据测试数据，输入正确的colSetId查询到的非自定义采集表信息应该有" + rightData.getRowCount() + "条", rightData.getRowCount(), is(2));

		//错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
		long wrongColSetId = 99999L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Result wrongData = wrongResult.getDataForResult();
		assertThat("根据测试数据，输入错误的colSetId查询到的非自定义采集表信息应该有，但是HTTP访问成功返回" + wrongData.getRowCount() + "条", rightData.getRowCount(), is(0));
	}

	/**
	 * 测试保存单表字符补齐规则
	 *
	 * 正确数据访问1：构造合法的修改数据进行访问，应该可以正确保存(对该表之前设置过字符补齐)
	 * 错误的数据访问1：构造没有补齐字符的访问
	 * 错误的数据访问2：构造没有补齐长度的访问
	 * 错误的数据访问3：构造没有补齐方式的访问
	 * 错误的数据访问4：构造没有关联表信息的访问
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveSingleTbCompletionInfo(){
		//正确数据访问1：构造合法的修改数据进行访问，应该可以正确保存(对该表之前设置过字符补齐)
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from table_clean where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), "wzc").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增之前为sys_user表构造的字符补齐测试数据是存在的", oldCount == 1, is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from table_clean where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增成功，构造的整表字符补齐测试数据被成功保存", count == 1L, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from table_clean where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), "wzc").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增成功，之前为sys_user表构造的字符补齐测试数据已经被删除了", oldCount == 0, is(true));
			int deleteCount = SqlOperator.execute(db, "delete from table_clean where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), "beyond");
			assertThat("测试完成后，删除新增成功的整表字符补齐测试数据", deleteCount == 1, is(true));

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：构造没有补齐字符的访问
		String wrongStringOne = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构造没有补齐长度的访问
		String wrongStringTwo = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：构造没有补齐方式的访问
		String wrongStringThree = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		//错误的数据访问4：构造没有关联表信息的访问
		String wrongStringFour = new HttpClient()
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));
	}

	/**
	 * 测试保存一列的字符补齐规则
	 *
	 * 正确数据访问1：构造合法的新增数据进行访问，应该可以正确保存(对该列之前没有设置过字符补齐)
	 * 错误的数据访问1：构造没有补齐字符的访问
	 * 错误的数据访问2：构造没有补齐长度的访问
	 * 错误的数据访问3：构造没有补齐方式的访问
	 * 错误的数据访问4：构造没有关联列信息的访问
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColCompletionInfo(){
		//正确数据访问1：构造合法的新增数据进行访问，应该可以正确保存(对该列之前没有设置过字符补齐)
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from column_clean where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), "wzc").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐之前为sys_user表的create_id列构造的字符补齐测试数据是存在的", oldCount == 1, is(true));
		}

		String rightString = new HttpClient()
				.addData("column_id", 2002L)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from column_clean where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), "beyond").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐成功，构造的测试数据被成功保存", count == 1, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from column_clean where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), "wzc").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐成功，之前为sys_user表的create_id列构造的字符补齐测试数据已经被删除了", oldCount == 0, is(true));
			int deleteCount = SqlOperator.execute(db, "delete from column_clean where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), "beyond");
			assertThat("测试完成后，删除新增成功的列字符补齐测试数据", deleteCount == 1, is(true));

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：构造没有补齐字符的访问
		String wrongStringOne = new HttpClient()
				.addData("column_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构造没有补齐长度的访问
		String wrongStringTwo = new HttpClient()
				.addData("column_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：构造没有补齐方式的访问
		String wrongStringThree = new HttpClient()
				.addData("column_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		//错误的数据访问4：构造没有关联列信息的访问
		String wrongStringFour = new HttpClient()
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));
	}

	/**
	 * 测试根据列ID获得列字符补齐信息
	 *
	 * 正确数据访问1：构造正确的columnId进行测试(2003，对该列本身就设置过列字符补齐)
	 * 正确数据访问2：构造正确的columnId进行测试(2001，对该列没有设置过字符补齐，但是对其所在的表设置过整表字符补齐)
	 * 正确数据访问3：构造没有设置过列字符补齐，也没有设置过表字符补齐的columnId进行测试(3004)，拿到的应该是空的数据集
	 * 错误的测试用例未达到三组:getColCompletionInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColCompletionInfo(){
		//正确数据访问1：构造正确的columnId进行测试(2003，对该列本身就设置过列字符补齐)
		String rightStringOne = new HttpClient()
				.addData("columnId", 2003L)
				.post(getActionUrl("getColCompletionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("columnId为2003的列字符补齐信息中，col_clean_id为33333", rightDataOne.getLong(0, "col_clean_id"), is(33333L));
		assertThat("columnId为2003的列字符补齐信息中，补齐类型为后补齐", rightDataOne.getLong(0, "filling_type"), is(POST_COMPLE_FLAG));
		assertThat("columnId为2003的列字符补齐信息中，补齐字符为空格", rightDataOne.getLong(0, "character_filling"), is(" "));
		assertThat("columnId为2003的列字符补齐信息中，补齐长度为1", rightDataOne.getLong(0, "filling_length"), is(1));
		assertThat("columnId为2003的列字符补齐信息中，columnId为2003", rightDataOne.getLong(0, "column_id"), is(2003L));

		//正确数据访问2：构造正确的columnId进行测试(2001，对该列没有设置过字符补齐，但是对其所在的表设置过整表字符补齐)
		String rightStringTwo = new HttpClient()
				.addData("columnId", 2001L)
				.post(getActionUrl("getColCompletionInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，table_clean_id为11111", rightDataTwo.getLong(0, "table_clean_id"), is(11111L));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐类型为前补齐", rightDataTwo.getLong(0, "filling_type"), is(PRE_COMPLE_FLAG));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐字符为wzc", rightDataTwo.getLong(0, "character_filling"), is("wzc"));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐长度为3", rightDataTwo.getLong(0, "filling_length"), is(3));

		//正确数据访问3：构造没有设置过列字符补齐，也没有设置过表字符补齐的columnId进行测试(3004)，拿到的应该是空的数据集
		String rightStringThree = new HttpClient()
				.addData("columnId", 3004L)
				.post(getActionUrl("getColCompletionInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		assertThat("columnId为3004的字段，没有设置列字符补齐，其所在表也没有设置字符补齐，所以访问得到的数据集没有数据", rightResultThree.getDataForResult().getRowCount(), is(0));
	}

	/**
	 * 测试根据表ID获取该表的字符补齐信息
	 *
	 * 正确数据访问1：构造正确的tableId进行测试(7001，对该表设置过整表字符补齐)，应该能够得到数据
	 * 正确数据访问2：构造正确的tableId进行测试(7002，没有对该表设置过整表字符补齐)，得不到数据
	 * 错误的测试用例未达到三组:getTbCompletionInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTbCompletionInfo(){
		//正确数据访问1：构造正确的tableId进行测试(7001，对该列本身就设置过列字符补齐)
		String rightStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getTbCompletionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("tableId为7001的表字符补齐信息中，table_clean_id为11111", rightDataOne.getLong(0, "table_clean_id"), is(11111L));
		assertThat("tableId为7001的表字符补齐信息中，补齐类型为前补齐", rightDataOne.getLong(0, "filling_type"), is(PRE_COMPLE_FLAG));
		assertThat("tableId为7001的表字符补齐信息中，补齐字符为wzc", rightDataOne.getLong(0, "character_filling"), is("wzc"));
		assertThat("tableId为7001的表字符补齐信息中，补齐长度为3", rightDataOne.getLong(0, "filling_length"), is(3));
		assertThat("tableId为7001的表字符补齐信息中，tableId为7001", rightDataOne.getLong(0, "tableId"), is(SYS_USER_TABLE_ID));

		//正确数据访问2：构造正确的tableId进行测试(7002，没有对该表设置过整表字符补齐)，得不到数据
		String rightStringTwo = new HttpClient()
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getTbCompletionInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		assertThat("tableId为7001的表，没有设置字符补齐，所以访问得到的数据集没有数据", rightResultTwo.getDataForResult().getRowCount(), is(0));
	}

	/**
	 * 测试保存单个表的字符替换规则
	 *
	 * 正确数据访问1：构造正常的字符替换规则进行保存，由于后端接收json格式字符串，测试用例中使用List集合模拟保存两条字符替换规则
	 * 正确数据访问2：构造特殊字符，如回车
	 * 错误的测试用例未达到三组:saveSingleTbReplaceInfo方法目前还没有明确是否要对原字符和替换后的字符进行校验
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveSingleTbReplaceInfo(){
		//正确数据访问1：构造正常的字符替换规则进行保存
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from table_clean where table_id = ? and clean_type = ? and field = ? and replace_feild = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode(), "wzc", "wqp").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增整表字符补齐之前为sys_user表构造的字符替换测试数据是存在的", oldCount == 1, is(true));
		}
		List<Table_clean> replaceList = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			long tableId;
			String cleanType;
			String oriField;
			String newField;
			switch (i){
				case 1:
					tableId = 333333L;
					cleanType = CleanType.ZiFuTiHuan.getCode();
					oriField = "beyond";
					newField = "hongzhi";
					break;
				case 2:
					tableId = 444444L;
					cleanType = CleanType.ZiFuTiHuan.getCode();
					oriField = "alibaba";
					newField = "tencent";
					break;
				default:
					tableId = UNEXPECTED_ID;
					cleanType = "unexpected_cleanType";
					oriField = "unexpected_oriField";
					newField = "unexpected_newField";
			}
			Table_clean replace = new Table_clean();
			replace.setTable_id(tableId);
			replace.setField(oriField);
			replace.setReplace_feild(newField);
			replace.setClean_type(cleanType);

			replaceList.add(replace);
		}

		String rightStringOne = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("saveSingleTbReplaceInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from table_clean where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增整表字符替换成功，构造的测试数据被成功保存", count == 2, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from table_clean where table_id = ? and field = ? and replace_feild = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode(), "wzc", "wqp").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增整表字符替换成功，之前为sys_user表构造的字符替换测试数据已经被删除了", oldCount == 0, is(true));
			int deleteCount = SqlOperator.execute(db, "delete from table_clean where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat("测试完成后，删除新增成功的整表字符替换测试数据", deleteCount == 2, is(true));

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：构造特殊字符，如回车
		Table_clean enter = new Table_clean();
		enter.setTable_id(SYS_USER_TABLE_ID);
		enter.setClean_type(CleanType.ZiFuTiHuan.getCode());
		enter.setField("\n");
		enter.setReplace_feild("|");

		String rightStringTwo = new HttpClient()
				.addData("replaceString", JSON.toJSONString(enter))
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("saveSingleTbReplaceInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from table_clean where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增整表字符替换成功，构造的测试数据被成功保存", count == 1, is(true));
			Result result = SqlOperator.queryResult(db, "select clean_type, field, replace_feild, table_id from table_clean where table_id = ?", SYS_USER_TABLE_ID);
			assertThat(result.getRowCount() == 1, is(true));
			assertThat(CleanType.ofEnumByCode(result.getString(0, "clean_type")) == CleanType.ZiFuTiHuan, is(true));
			assertThat(result.getString(0, "field").equals("\n"), is(true));
			assertThat(result.getString(0, "replace_feild").equals("|"), is(true));
			int deleteCount = SqlOperator.execute(db, "delete from table_clean where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat("测试完成后，删除新增成功的整表字符替换测试数据", deleteCount == 1, is(true));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试保存单个字段的字符替换规则
	 *
	 * 正确数据访问1：构造正确的列字符替换规则进行保存
	 * 错误的测试用例未达到三组:saveColReplaceInfo方法目前还没有明确是否要对原字符和替换后的字符进行校验
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColReplaceInfo(){
		//正确数据访问1：构造正确的列字符替换规则进行保存
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from column_clean where column_id = ? and clean_type = ? and field = ? and replace_feild = ?", 2005L, CleanType.ZiFuTiHuan.getCode(), "ceshi", "test").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐之前为columnId列构造的字符替换测试数据是存在的", oldCount == 1, is(true));
		}

		List<Column_clean> replaceList = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			long columnId;
			String cleanType;
			String oriField;
			String newField;
			switch (i){
				case 1:
					columnId = 3333333L;
					cleanType = CleanType.ZiFuTiHuan.getCode();
					oriField = "beyond";
					newField = "hongzhi";
					break;
				case 2:
					columnId = 4444444L;
					cleanType = CleanType.ZiFuTiHuan.getCode();
					oriField = "alibaba";
					newField = "tencent";
					break;
				default:
					columnId = UNEXPECTED_ID;
					cleanType = "unexpected_cleanType";
					oriField = "unexpected_oriField";
					newField = "unexpected_newField";
			}
			Column_clean replace = new Column_clean();
			replace.setColumn_id(columnId);
			replace.setField(oriField);
			replace.setReplace_feild(newField);
			replace.setClean_type(cleanType);

			replaceList.add(replace);
		}

		String rightStringOne = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("columnId", 2005L)
				.post(getActionUrl("saveColReplaceInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from column_clean where column_id = ? and clean_type = ?", 2005L, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符替换成功，构造的测试数据被成功保存", count == 2, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from column_clean where column_id = ? and clean_type = ? and field = ? and replace_feild = ?", 2005L, CleanType.ZiFuTiHuan.getCode(), "ceshi", "test").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐成功之后，为columnId列构造的字符替换测试数据被删除了", oldCount == 0, is(true));
			Result result = SqlOperator.queryResult(db, "select clean_type, replace_feild from column_clean where column_id = ? and field = ?", 2005L, "alibaba");
			assertThat(result.getRowCount() == 1, is(true));
			assertThat(CleanType.ofEnumByCode(result.getString(0, "clean_type")) == CleanType.ZiFuTiHuan, is(true));
			assertThat(result.getString(0, "replace_feild").equals("tencent"), is(true));
			int deleteCount = SqlOperator.execute(db, "delete from column_clean where column_id = ? and clean_type = ?", 2005L, CleanType.ZiFuTiHuan.getCode());
			assertThat("测试完成后，删除新增成功的整表字符替换测试数据", deleteCount == 2, is(true));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试根据表ID获取针对该表定义的字符替换规则
	 *
	 * 正确数据访问1：尝试获取对sys_user表设置的字符替换规则，能够获取到一条数据
	 * 正确数据访问2：尝试获取对code_info表设置的字符替换规则，由于初始化测试数据中没有对code_info表设置字符替换，所以获取不到
	 * 错误的测试用例未达到三组:getSingleTbReplaceInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getSingleTbReplaceInfo(){
		//正确数据访问1：尝试获取对sys_user表设置的字符替换规则，能够获取到一条数据
		String rightStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getSingleTbReplaceInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("tableId为7001的表字符替换信息有一条", rightDataOne.getRowCount(), is(1));
		assertThat("tableId为7001的表字符替换信息中，table_clean_id为111111", rightDataOne.getLong(0, "table_clean_id"), is(111111L));
		assertThat("tableId为7001的表字符替换信息中，原字符为wzc", rightDataOne.getLong(0, "field"), is("wzc"));
		assertThat("tableId为7001的表字符替换信息中，替换后字符为wqp", rightDataOne.getLong(0, "replace_feild"), is("wqp"));

		//正确数据访问2：尝试获取对code_info表设置的字符替换规则，由于初始化测试数据中没有对code_info表设置字符替换，所以获取不到
		String rightStringTwo = new HttpClient()
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getSingleTbReplaceInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("tableId为7002的表没有字符替换信息", rightDataTwo.getRowCount(), is(1));
	}

	/**
	 * 测试根据列ID获得列字符替换信息
	 *
	 * 正确数据访问1：尝试获取对sys_user表中，user_name列设置的字符替换规则，能够获取到一条数据
	 * 正确数据访问2：尝试获取对sys_user表中，user_pwd列设置的字符替换规则，由于没有对该列设置过字符替换规则，所以获取不到数据
	 * 正确数据访问3：尝试获取对code_info表中，ci_sp_remark列的字符替换规则，由于在初始化数据中，没有对ci_sp_remark列和code_info表设置字符替换规则，所以无法拿不到任何数据
	 * 错误的测试用例未达到三组:getColReplaceInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColReplaceInfo(){
		//正确数据访问1：尝试获取对sys_user表中，user_name列设置的字符替换规则，能够获取到一条数据
		String rightStringOne = new HttpClient()
				.addData("columnId", 2005L)
				.post(getActionUrl("getColReplaceInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("columnId为2005的列字符替换信息有一条", rightDataOne.getRowCount(), is(1));
		assertThat("columnId为2005的列字符替换信息中，col_clean_id为555555", rightDataOne.getLong(0, "table_clean_id"), is(555555L));
		assertThat("columnId为2005的列字符替换信息中，原字符为ceshi", rightDataOne.getLong(0, "field"), is("ceshi"));
		assertThat("columnId为2005的列字符替换信息中，替换后字符为test", rightDataOne.getLong(0, "replace_feild"), is("test"));
		assertThat("columnId为2005的列字符替换信息中，column_id为2005L", rightDataOne.getLong(0, "replace_feild"), is("test"));

		//正确数据访问2：尝试获取对sys_user表中，user_pwd列设置的字符替换规则，由于没有对该列设置过字符替换规则，但是对sys_user表设置过字符替换，所以能够拿到字符替换规则
		String rightStringTwo = new HttpClient()
				.addData("columnId", 2006L)
				.post(getActionUrl("getColReplaceInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("尝试获取columnId为2006的列字符替换信息有一条", rightDataTwo.getRowCount(), is(1));
		assertThat("尝试获取columnId为2006的列字符替换信息中，table_clean_id为111111", rightDataTwo.getLong(0, "table_clean_id"), is(111111L));
		assertThat("尝试获取columnId为2006的列字符替换信息中，原字符为wzc", rightDataTwo.getLong(0, "field"), is("wzc"));
		assertThat("尝试获取columnId为2006的列字符替换信息中，替换后字符为wqp", rightDataTwo.getLong(0, "replace_feild"), is("wqp"));

		//正确数据访问3：尝试获取对code_info表中，ci_sp_remark列的字符替换规则，由于在初始化数据中，没有对ci_sp_remark列和code_info表设置字符替换规则，所以无法拿不到任何数据
		String rightStringThree = new HttpClient()
				.addData("columnId", 3005L)
				.post(getActionUrl("getColReplaceInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		Result rightDataThree = rightResultThree.getDataForResult();
		assertThat("尝试获取columnId为3005的列字符替换信息，获取不到", rightDataThree.getRowCount(), is(0));
	}

	/**
	 * 测试根据表ID获取该表所有的列清洗信息
	 *
	 * 正确数据访问1：尝试获取tableId为7002的表的所有列
	 * 错误的数据访问1：尝试获取tableId为999999999的表的所有列，由于初始化时没有构造tableId为7006的数据，所以拿不到数据
	 * 错误的测试用例未达到三组: getColumnInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColumnInfo(){
		//正确数据访问1：尝试获取tableId为7002的表的所有列
		String rightString = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result rightData = rightResult.getDataForResult();
		assertThat("尝试获取tableId为7002的表的所有列，得到的结果集中有10条数据", rightData.getRowCount(), is(10));
		assertThat("尝试获取tableId为7002的表的所有列，create_id做了字符补齐", rightData.getString(2, "compflag"), is("yes"));
		assertThat("尝试获取tableId为7002的表的所有列，dep_id做了字符补齐", rightData.getString(3, "compflag"), is("yes"));
		assertThat("尝试获取tableId为7002的表的所有列，user_name做了字符替换", rightData.getString(5, "replaceflag"), is("yes"));

		//错误的数据访问1：尝试获取tableId为7006的表的所有列，由于初始化时没有构造tableId为999999999的数据，所以拿不到数据
		String wrongString = new HttpClient()
				.addData("tableId", UNEXPECTED_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Result wrongData = wrongResult.getDataForResult();
		assertThat("尝试获取tableId为999999999的表的所有列，得到的结果集为空", wrongData.isEmpty(), is(true));
	}

	/**
	 * 测试保存所有表清洗设置字符补齐和字符替换
	 *
	 * 正确数据访问1：模拟只设置全表字符补齐
	 * 正确数据访问2：模拟只设置全表字符替换(设置两条)
	 * 正确数据访问3：模拟既设置全表字符补齐，又这是全表字符替换
	 * 错误的测试用例未达到三组: saveAllTbCleanConfigInfo是一个保存操作，上述三个测试用例已经可以覆盖所有的情况
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllTbCleanConfigInfo(){

	}

	/**
	 * 测试根据数据库设置ID查询所有表清洗设置字符补齐和字符替换规则
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAllTbCleanConfInfo(){

	}

	/**
	 * 测试根据列ID获取针对该列设置的日期格式化规则
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getDateFormatInfo(){

	}

	/**
	 * 测试保存列清洗日期格式化
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveDateFormatInfo(){

	}

	/**
	 * 测试根据columnId查询列拆分信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColSplitInfo(){

	}

	/**
	 * 测试删除一条列拆分规则
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteColSplitInfo(){

	}

	/**
	 * 测试保存列拆分规则
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColSplitInfo(){

	}

	/**
	 * 测试获取列码值转换清洗规则
	 *
	 * TODO 被测方式未完成
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getCVConversionInfo(){

	}

	/**
	 * 测试保存列码值转换清洗规则
	 *
	 * TODO 被测方式未完成
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCVConversionInfo(){

	}

	/**
	 * 测试根据表ID查询针对该表设置的列合并信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColMergeInfo(){

	}

	/**
	 * 测试保存列合并信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColMergeInfo(){

	}

	/**
	 * 测试删除一条列合并信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteColMergeInfo(){

	}

	/**
	 * 测试保存所有表清洗优先级
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllTbCleanOrder(){

	}

	/**
	 * 测试保存整表清洗优先级
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveSingleTbCleanOrder(){

	}

	/**
	 * 测试保存单个字段清洗优先级
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColCleanOrder(){

	}

	/**
	 * 测试保存列清洗信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColCleanConfig(){

	}

	/**
	 * 保存配置数据清洗页面信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveDataCleanConfig(){

	}

	@After
	public void after(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1、删除数据源表(data_source)测试数据
			SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID);
			//2、删除Agent信息表(agent_info)测试数据
			SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//3、删除database_set表测试数据
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", FIRST_DB_AGENT_ID);
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", SECOND_DB_AGENT_ID);
			//4、删除collect_job_classify表测试数据
			SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//5、删除table_info表测试数据
			SqlOperator.execute(db, "delete from " + Table_info.TableName + " where database_id = ? ", FIRST_DATABASESET_ID);
			//6、删除table_column表测试数据
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			//7、删除table_clean表测试数据
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			//8、删除column_clean表测试数据
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2002L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2003L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2005L);
			//9、删除clean_parameter表测试数据
			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? ", FIRST_DATABASESET_ID);
			//10、提交事务后，对数据表中的数据进行检查，断言删除是否成功
			SqlOperator.commitTransaction(db);
		}
	}
}
