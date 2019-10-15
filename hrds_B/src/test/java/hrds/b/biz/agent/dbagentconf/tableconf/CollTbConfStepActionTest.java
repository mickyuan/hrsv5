package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @Description: CollTbConfStepAction单元测试类
 * @Author: wangz
 * @CreateTime: 2019-10-10-14:16
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.b.biz.agent.dbagentconf.tableconf
 **/
public class CollTbConfStepActionTest extends WebBaseTestCase{

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
	private static final long FIRST_STORAGE_ID = 1234L;
	private static final long SECOND_STORAGE_ID = 5678L;
	private static final long DEFAULT_TABLE_ID = 999999L;

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
	 * 9、构造table_storage_info表测试数据
	 * 10、构造table_clean表测试数据
	 * 11、构造column_merge表测试数据
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
	 *          7-1、table_id:7001,table_name:sys_user,按照画面配置信息进行采集，并且配置了单表过滤SQL,select * from sys_user where user_id = 2001
	 *          7-2、table_id:7002,table_name:code_info,按照画面配置信息进行采集
	 *          7-3、table_id:7003,table_name:agent_info,按照自定义SQL进行采集
	 *          7-4、table_id:7004,table_name:data_source,按照自定义SQL进行采集
	 *      8、table_column表测试数据：只有在画面上进行配置的采集表才会向table_column表中保存数据
	 *          8-1、column_id为2001-2010，模拟采集了sys_user表的前10个列，列名为user_id，create_id，dep_id，role_id，
	 *               user_name，user_password，user_email，user_mobile，useris_admin，user_type
     *          8-2、column_id为3001-3005，模拟采集了code_info表的所有列，列名为ci_sp_code，ci_sp_class，ci_sp_classname，
	 *               ci_sp_name，ci_sp_remark
	 *      9、table_storage_info表测试数据：
	 *          9-1、storage_id为1234，文件格式为CSV，存储方式为替换，table_id为sys_user表的ID
	 *          9-2、storage_id为5678，文件格式为定长文件，存储方式为追加，table_id为code_info表的ID
	 *      10、table_clean表测试数据：
	 *          10-1、模拟数据对sys_user表进行整表清洗，分别做了列合并和首尾去空
	 *          10-2、模拟数据对code_info表进行了整表清洗，分别做了字符替换字符补齐，将所有列值的abc全部替换为def，将所有列值的前面补上beyond字符串
	 *      11、column_merge表测试数据：对sys_user设置了两个列合并
	 *          11-1、模拟数据将user_id和create_id两列合并为user_create_id
	 *          11-2、模拟数据将user_name和user_password两列合并为user_name_password
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Before
	public void before() {
		//1、构造默认表清洗顺序
		JSONObject tableCleanOrder = new JSONObject();
		tableCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		tableCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		tableCleanOrder.put(CleanType.ZiFuHeBing.getCode(), 3);
		tableCleanOrder.put(CleanType.ZiFuTrim.getCode(), 4);
		//2、构造默认列清洗顺序
		JSONObject columnCleanOrder = new JSONObject();
		columnCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		columnCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		columnCleanOrder.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
		columnCleanOrder.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
		columnCleanOrder.put(CleanType.ZiFuChaiFen.getCode(), 5);
		columnCleanOrder.put(CleanType.ZiFuTrim.getCode(), 6);

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
					customizeSQL = "select * from sys_user where user_id = 2001";
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
			String columeName;
			String columeType;
			String columeChName;
			switch (i){
				case 1 :
					primaryKeyFlag = IsFlag.Shi.getCode();
					columeName = "user_id";
					columeType = "int8";
					columeChName = "主键";
					break;
				case 2 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "create_id";
					columeType = "int8";
					columeChName = "创建用户者ID";
					break;
				case 3 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "dep_id";
					columeType = "int8";
					columeChName = "部门ID";
					break;
				case 4 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "role_id";
					columeType = "int8";
					columeChName = "角色ID";
					break;
				case 5 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "user_name";
					columeType = "varchar";
					columeChName = "用户名";
					break;
				case 6 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "user_password";
					columeType = "varchar";
					columeChName = "密码";
					break;
				case 7 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "user_email";
					columeType = "varchar";
					columeChName = "邮箱";
					break;
				case 8 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "user_mobile";
					columeType = "varchar";
					columeChName = "电话";
					break;
				case 9 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "useris_admin";
					columeType = "char";
					columeChName = "是否管理员";
					break;
				case 10 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "user_type";
					columeType = "char";
					columeChName = "用户类型";
					break;
				default:
					primaryKeyFlag = "unexpected_primaryKeyFlag";
					columeName = "unexpected_columeName";
					columeType = "unexpected_columeType";
					columeChName = "unexpected_columeChName";
			}
			Table_column sysUserColumn = new Table_column();
			sysUserColumn.setColumn_id(BASE_SYS_USER_PRIMARY + i);
			sysUserColumn.setIs_get(IsFlag.Shi.getCode());
			sysUserColumn.setIs_primary_key(primaryKeyFlag);
			sysUserColumn.setColume_name(columeName);
			sysUserColumn.setColumn_type(columeType);
			sysUserColumn.setColume_ch_name(columeChName);
			sysUserColumn.setTable_id(SYS_USER_TABLE_ID);
			sysUserColumn.setValid_s_date(DateUtil.getSysDate());
			sysUserColumn.setValid_e_date(Constant.MAXDATE);
			sysUserColumn.setIs_alive(IsFlag.Shi.getCode());
			sysUserColumn.setIs_new(IsFlag.Shi.getCode());
			sysUserColumn.setTc_or(columnCleanOrder.toJSONString());

			sysUsers.add(sysUserColumn);
		}

		List<Table_column> codeInfos = new ArrayList<>();
		for(int i = 1; i <= 5; i++){
			String primaryKeyFlag;
			String columeName;
			String columeType;
			String columeChName;
			switch (i){
				case 1 :
					primaryKeyFlag = IsFlag.Shi.getCode();
					columeName = "ci_sp_code";
					columeType = "varchar";
					columeChName = "ci_sp_code";
					break;
				case 2 :
					primaryKeyFlag = IsFlag.Shi.getCode();
					columeName = "ci_sp_class";
					columeType = "varchar";
					columeChName = "ci_sp_class";
					break;
				case 3 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "ci_sp_classname";
					columeType = "varchar";
					columeChName = "ci_sp_classname";
					break;
				case 4 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "ci_sp_name";
					columeType = "varchar";
					columeChName = "ci_sp_name";
					break;
				case 5 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columeName = "ci_sp_remark";
					columeType = "varchar";
					columeChName = "ci_sp_remark";
					break;
				default:
					primaryKeyFlag = "unexpected_primaryKeyFlag";
					columeName = "unexpected_columeName";
					columeType = "unexpected_columeType";
					columeChName = "unexpected_columeChName";
			}
			Table_column codeInfoColumn = new Table_column();
			codeInfoColumn.setColumn_id(BASE_CODE_INFO_PRIMARY + i);
			codeInfoColumn.setIs_get(IsFlag.Shi.getCode());
			codeInfoColumn.setIs_primary_key(primaryKeyFlag);
			codeInfoColumn.setColume_name(columeName);
			codeInfoColumn.setColumn_type(columeType);
			codeInfoColumn.setColume_ch_name(columeChName);
			codeInfoColumn.setTable_id(CODE_INFO_TABLE_ID);
			codeInfoColumn.setValid_s_date(DateUtil.getSysDate());
			codeInfoColumn.setValid_e_date(Constant.MAXDATE);
			codeInfoColumn.setIs_alive(IsFlag.Shi.getCode());
			codeInfoColumn.setIs_new(IsFlag.Shi.getCode());
			codeInfoColumn.setTc_or(columnCleanOrder.toJSONString());

			codeInfos.add(codeInfoColumn);
		}

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
			tableStorageInfo.setIs_everyday(IsFlag.Shi.getCode());
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
		//12、插入数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//插入数据源表(data_source)测试数据
			int dataSourceCount = dataSource.add(db);
			assertThat("数据源测试数据初始化", dataSourceCount, is(1));
			//插入Agent信息表(agent_info)测试数据
			for(Agent_info agentInfo : agents){
				agentInfo.add(db);
			}
			assertThat("Agent测试数据初始化", agents.size(), is(2));
			//插入database_set表测试数据
			for(Database_set databaseSet : databases){
				databaseSet.add(db);
			}
			assertThat("数据库设置测试数据初始化", databases.size(), is(2));
			//插入collect_job_classify表测试数据
			for(Collect_job_classify classify : classifies){
				classify.add(db);
			}
			assertThat("采集任务分类表测试数据初始化", classifies.size(), is(2));
			//插入table_info测试数据
			for(Table_info tableInfo : tableInfos){
				tableInfo.add(db);
			}
			assertThat("数据库对应表测试数据初始化", tableInfos.size(), is(4));
			//插入table_column测试数据
			for(Table_column tableColumn : sysUsers){
				tableColumn.add(db);
			}
			for(Table_column tableColumn : codeInfos){
				tableColumn.add(db);
			}
			assertThat("表对应字段表测试数据初始化", sysUsers.size() + codeInfos.size(), is(15));
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
			assertThat("列合并信息表测试数据初始化", sysUserMerge.size(), is(2));

			SqlOperator.commitTransaction(db);
		}

	}

	/**
	 * 测试根据colSetId加载页面初始化数据
	 *
	 * 正确数据访问1：构造正确的colSetId(FIRST_DATABASESET_ID)
	 * 错误的数据访问1：构造错误的colSetId
	 * 错误的测试用例未达到三组:getInitInfo方法只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getInitInfo(){
		//正确数据访问1：构造正确的colSetId(FIRST_DATABASESET_ID)
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightData = rightResult.getDataForResult();
		assertThat("根据测试数据，输入正确的colSetId查询到的非自定义采集表信息应该有" + rightData.getRowCount() + "条", rightData.getRowCount(), is(2));

		//错误的数据访问1：构造错误的colSetId
		long wrongColSetId = 99999L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Result wrongData = wrongResult.getDataForResult();
		assertThat("根据测试数据，输入错误的colSetId查询到的非自定义采集表信息应该有" + wrongData.getRowCount() + "条", rightData.getRowCount(), is(2));
	}

	/**
	 * 测试根据数据库设置id得到所有表相关信息功能
	 * 正确数据访问1：构造colSetId为1001，inputString为code的测试数据
	 * 正确数据访问2：构造colSetId为1001，inputString为sys的测试数据
	 * 正确数据访问3：构造colSetId为1001，inputString为sys|code的测试数据
	 * 正确数据访问4：构造colSetId为1001，inputString为wzc的测试数据
	 * 错误的数据访问1：构造colSetId为1003的测试数据
	 * 错误的测试用例未达到三组:已经测试用例已经可以覆盖程序中所有的分支
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTableInfo(){
		//正确数据访问1：构造colSetId为1001，inputString为code的测试数据
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "code")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		List<Result> rightDataOne = rightResultOne.getDataForEntityList(Result.class);
		assertThat("使用code做模糊查询得到的表信息有1条", rightDataOne.size(), is(1));
		assertThat("使用code做模糊查询得到的表名为code_info", rightDataOne.get(0).getString(0, "table_name"), is("code_info"));
		//正确数据访问2：构造colSetId为1001，inputString为sys的测试数据
		String rightStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "sys")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		List<Result> rightDataTwo = rightResultTwo.getDataForEntityList(Result.class);
		assertThat("使用sys做模糊查询得到的表信息有6条", rightDataTwo.size(), is(6));
		//TODO 这种处理方式可以吗？
		for(Result result : rightDataTwo){
			String tableName = result.getString(0, "table_name");
			if(tableName.equalsIgnoreCase("sys_dump")){
				assertThat("使用sys做模糊查询得到的表名有sys_dump", tableName, is("sys_dump"));
			}else if(tableName.equalsIgnoreCase("sys_exeinfo")){
				assertThat("使用sys做模糊查询得到的表名有sys_exeinfo", tableName, is("sys_exeinfo"));
			}else if(tableName.equalsIgnoreCase("sys_para")){
				assertThat("使用sys做模糊查询得到的表名有sys_para", tableName, is("sys_para"));
			}else if(tableName.equalsIgnoreCase("sys_recover")){
				assertThat("使用sys做模糊查询得到的表名有sys_recover", tableName, is("sys_recover"));
			}else if(tableName.equalsIgnoreCase("sys_role")){
				assertThat("使用sys做模糊查询得到的表名有sys_role", tableName, is("sys_role"));
			}else{
				assertThat("使用sys做模糊查询得到的表名有不符合期望的情况，表名为" + tableName, true, is(false));
			}
		}
		//正确数据访问3：构造colSetId为1001，inputString为sys|code的测试数据
		String rightStringThree = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "sys|code")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		List<Result> rightDataThree = rightResultThree.getDataForEntityList(Result.class);
		assertThat("使用sys|code做模糊查询得到的表信息有7条", rightDataThree.size(), is(7));
		for(Result result : rightDataThree){
			String tableName = result.getString(0, "table_name");
			if(tableName.equalsIgnoreCase("sys_dump")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_dump", tableName, is("sys_dump"));
			}else if(tableName.equalsIgnoreCase("sys_exeinfo")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_exeinfo", tableName, is("sys_exeinfo"));
			}else if(tableName.equalsIgnoreCase("sys_para")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_para", tableName, is("sys_para"));
			}else if(tableName.equalsIgnoreCase("sys_recover")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_recover", tableName, is("sys_recover"));
			}else if(tableName.equalsIgnoreCase("sys_role")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_role", tableName, is("sys_role"));
			}else if(tableName.equalsIgnoreCase("code_info")){
				assertThat("使用sys|code做模糊查询得到的表名有code_info", tableName, is("code_info"));
			}else{
				assertThat("使用sys|code做模糊查询得到的表名有不符合期望的情况，表名为" + tableName, true, is(false));
			}
		}
		//正确数据访问4：构造colSetId为1001，inputString为wzc的测试数据
		String rightStringFour = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "wzc")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultFour = JsonUtil.toObjectSafety(rightStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFour.isSuccess(), is(true));
		List<Result> rightDataFour = rightResultFour.getDataForEntityList(Result.class);
		assertThat("使用wzc做模糊查询得到的表信息有7条", rightDataFour.isEmpty(), is(true));
		//错误的数据访问1：构造colSetId为1003的测试数据
		long wrongColSetId = 1003L;
		String wrongColSetIdString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.addData("inputString", "code")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult wrongColSetIdResult = JsonUtil.toObjectSafety(wrongColSetIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongColSetIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据数据库设置id得到所有表相关信息
	 * 正确数据访问1：构造正确的colSetId进行测试
	 * 错误的数据访问1：构造错误的colSetId进行测试
	 * 错误的测试用例未达到三组:getAllTableInfo方法只有一个参数
	 * @Param: 无
	 * @return: 无
	 * TODO 由于目前测试用的数据库是我们的测试库，所以表的数量不固定
	 * */
	@Test
	public void getAllTableInfo(){
		//正确数据访问1：构造正确的colSetId进行测试
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTableInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Result> rightData = rightResult.getDataForEntityList(Result.class);
		assertThat("截止2019.10.15，IP为47.103.83.1的测试库上有70张表",rightData.size(), is(70));
		//错误的数据访问1：构造错误的colSetId进行测试
		long wrongColSetId = 1003L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getAllTableInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试并行采集SQL测试功能
	 * 正确数据访问1：构建正确的colSetId和SQL语句
	 * 错误的数据访问1：构建错误的colSetId和正确的SQL语句
	 * 错误的数据访问2：构建正确的colSetId和错误SQL语句
	 * 错误的测试用例未达到三组:testParallelExtraction只有两个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void testParallelExtraction(){
		//正确数据访问1：构建正确的colSetId和SQL语句
		String pageSQL = "select * from sys_user limit 2 offset 1";
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("sql", pageSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//错误的数据访问1：构建错误的colSetId和正确的SQL语句
		long wrongColSetId = 1003L;
		String wrongColSetIdString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.addData("sql", pageSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult wrongColSetIdResult = JsonUtil.toObjectSafety(wrongColSetIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongColSetIdResult.isSuccess(), is(false));
		//错误的数据访问2：构建正确的colSetId和错误SQL语句
		String wrongSQL = "select * from sys_user limit 10,20";
		String wrongSQLString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("sql", wrongSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult wrongSQLResult = JsonUtil.toObjectSafety(wrongSQLString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongSQLResult.isSuccess(), is(false));
	}

	/**
	 * 测试SQL查询设置页面，保存按钮后台方法功能
	 *
	 * 正确数据访问1：构造两条自定义SQL查询设置数据，测试保存功能
	 * 错误的数据访问1：构造两条自定义SQL查询设置数据，第一条数据的表名为空
	 * 错误的数据访问2：构造两条自定义SQL查询设置数据，第二条数据的中文名为空
	 * 错误的数据访问3：构造两条自定义SQL查询设置数据，第一条数据的sql为空
	 * 错误的数据访问4：构造不存在与测试用例模拟数据中的databaseId
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllSQL(){
		//正确数据访问1：构造两条自定义SQL查询设置数据，测试保存功能
		List<Table_info> tableInfos = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableName = "getHalfStructTaskBySourceId";
					tableChName = "通过数据源ID获得半结构化采集任务";
					customizeSQL = "SELECT fcs.odc_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Object_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ?";
					break;
				case 2 :
					tableName = "getFTPTaskBySourceId";
					tableChName = "通过数据源ID获得FTP采集任务";
					customizeSQL = "SELECT fcs.ftp_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ";
					break;
				default:
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);

			tableInfos.add(tableInfo);
		}
		JSONArray array= JSONArray.parseArray(JSON.toJSONString(tableInfos));
		String rightString = new HttpClient()
				.addData("tableInfoArray", array.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//保存成功，验证数据库中的记录是否符合预期
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Table_info> expectedList = SqlOperator.queryList(db, Table_info.class, "select * from " + Table_info.TableName + " where database_id = ? AND is_user_defined = ?", FIRST_DATABASESET_ID, IsFlag.Shi.getCode());
			assertThat("保存成功后，table_info表中的用户自定义SQL查询数目应该有2条", expectedList.size(), is(2));
			for(Table_info tableInfo : expectedList){
				if(tableInfo.getTable_name().equalsIgnoreCase("getHalfStructTaskBySourceId")){
					assertThat("保存成功后，自定义SQL查询getHalfStructTaskBySourceId的中文名应该是<通过数据源ID获得半结构化采集任务>", tableInfo.getTable_ch_name(), is("通过数据源ID获得半结构化采集任务"));
				}else if(tableInfo.getTable_name().equalsIgnoreCase("getFTPTaskBySourceId")){
					assertThat("保存成功后，自定义SQL查询getFTPTaskBySourceId的中文名应该是<通过数据源ID获得FTP采集任务>", tableInfo.getTable_ch_name(), is("通过数据源ID获得FTP采集任务"));
				}else{
					assertThat("保存出错，出现了不希望出现的数据，表id为" + tableInfo.getTable_id(), true, is(false));
				}
			}
			//验证完毕后，将自己在本方法中构造的数据删除掉
			int firCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_name = ?", "getHalfStructTaskBySourceId");
			assertThat("测试完成后，table_name为getHalfStructTaskBySourceId的测试数据被删除了", firCount, is(1));
			int secCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_name = ?", "getFTPTaskBySourceId");
			assertThat("测试完成后，table_name为getFTPTaskBySourceId的测试数据被删除了", secCount, is(1));
		}



		//错误的数据访问1：构造两条自定义SQL查询设置数据，第一条数据的表名为空
		List<Table_info> errorTableInfosOne = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableName = null;
					tableChName = "通过数据源ID获得半结构化采集任务";
					customizeSQL = "SELECT fcs.odc_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Object_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ?";
					break;
				case 2 :
					tableName = "getFTPTaskBySourceId";
					tableChName = "通过数据源ID获得FTP采集任务";
					customizeSQL = "SELECT fcs.ftp_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ";
					break;
				default:
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);

			errorTableInfosOne.add(tableInfo);
		}
		JSONArray errorArrayOne= JSONArray.parseArray(JSON.toJSONString(errorTableInfosOne));
		String errorStringOne = new HttpClient()
				.addData("tableInfoArray", errorArrayOne.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorResultOne = JsonUtil.toObjectSafety(errorStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorResultOne.isSuccess(), is(false));
		//错误的数据访问2：构造两条自定义SQL查询设置数据，第二条数据的中文名为空
		List<Table_info> errorTableInfosTwo = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableName = "getHalfStructTaskBySourceId";
					tableChName = "通过数据源ID获得半结构化采集任务";
					customizeSQL = "SELECT fcs.odc_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Object_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ?";
					break;
				case 2 :
					tableName = "getFTPTaskBySourceId";
					tableChName = null;
					customizeSQL = "SELECT fcs.ftp_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ";
					break;
				default:
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);

			errorTableInfosTwo.add(tableInfo);
		}
		JSONArray errorArrayTwo= JSONArray.parseArray(JSON.toJSONString(errorTableInfosTwo));
		String errorStringTwo = new HttpClient()
				.addData("tableInfoArray", errorArrayTwo.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorResultTwo = JsonUtil.toObjectSafety(errorStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorResultTwo.isSuccess(), is(false));
		//错误的数据访问3：构造两条自定义SQL查询设置数据，第一条数据的sql为空
		List<Table_info> errortableInfoThree = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableName = "getHalfStructTaskBySourceId";
					tableChName = "通过数据源ID获得半结构化采集任务";
					customizeSQL = null;
					break;
				case 2 :
					tableName = "getFTPTaskBySourceId";
					tableChName = "通过数据源ID获得FTP采集任务";
					customizeSQL = "SELECT fcs.ftp_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ";
					break;
				default:
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);

			errortableInfoThree.add(tableInfo);
		}
		JSONArray errorArrayThree= JSONArray.parseArray(JSON.toJSONString(errortableInfoThree));
		String errorStringThree = new HttpClient()
				.addData("tableInfoArray", errorArrayThree.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorResultThree = JsonUtil.toObjectSafety(errorStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorResultThree.isSuccess(), is(false));

		//错误的数据访问4：构造不存在与测试用例模拟数据中的databaseId
		String errorDatabaseId = new HttpClient()
				.addData("tableInfoArray", array.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorDatabaseIdResult = JsonUtil.toObjectSafety(errorDatabaseId, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorDatabaseIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试SQL查询设置页面操作栏，删除按钮后台方法功能
	 *
	 * 正确数据访问1：模拟删除table_id为7003的自定义SQL采集数据
	 * 错误的数据访问1：模拟删除一个不存在的table_id的自定义SQL采集数据
	 * 错误的测试用例未达到三组: deleteSQLConf()方法只有一个参数
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteSQLConf(){
		//正确数据访问1：模拟删除table_id为7003的自定义SQL采集数据
		//删除前，确认待删除数据是否存在
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除前，table_id为" + AGENT_INFO_TABLE_ID + "的数据确实存在", beforeCount, is(1));
		}
		//构造正确的数据进行删除
		String rightString = new HttpClient()
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.post(getActionUrl("deleteSQLConf")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//删除后，确认数据是否真的被删除了
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除后，table_id为" + AGENT_INFO_TABLE_ID + "的数据不存在了", afterCount, is(0));
		}

		//错误的数据访问1：模拟删除一个不存在的table_id的自定义SQL采集数据
		long errorTableId = 88888L;
		String wrongString = new HttpClient()
				.addData("tableId", errorTableId)
				.post(getActionUrl("deleteSQLConf")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试配置采集表页面，SQL设置按钮后台方法功能，用于回显已经设置的SQL
	 *
	 * 正确数据访问1：构造正确的，且有数据的colSetId(FIRST_DATABASESET_ID)
	 * 正确的数据访问2：构造正确的，但没有数据的colSetId(SECOND_DATABASESET_ID)
	 *
	 * 错误的测试用例未达到三组:getAllSQL()只有一个参数，且只要用户登录，能查到数据就是能查到，查不到就是查不到
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAllSQLs(){
		//正确数据访问1：构造正确的，且有数据的colSetId(FIRST_DATABASESET_ID)
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllSQLs")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		List<Table_info> rightDataOne = rightResultOne.getDataForEntityList(Table_info.class);
		assertThat("在ID为" + FIRST_DATABASESET_ID + "的数据库采集任务下，有2条自定义采集SQL", rightDataOne.size(), is(2));
		for(Table_info tableInfo : rightDataOne){
			if(tableInfo.getTable_id() == AGENT_INFO_TABLE_ID){
				assertThat("在table_id为" + AGENT_INFO_TABLE_ID + "的自定义采集SQL中，自定义SQL为", tableInfo.getSql(), is("select * from agent_info"));
			}else if(tableInfo.getTable_id() == DATA_SOURCE_TABLE_ID){
				assertThat("在table_id为" + DATA_SOURCE_TABLE_ID + "的自定义采集SQL中，自定义SQL为", tableInfo.getSql(), is("select * from data_source"));
			}else{
				assertThat("获取到了不期望获取的数据，该条数据的table_name为" + tableInfo.getTable_name(), true, is(false));
			}
		}
		//正确的数据访问2：构造正确的，但没有数据的colSetId(SECOND_DATABASESET_ID)
		String rightStringTwo = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.post(getActionUrl("getAllSQLs")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		List<Table_info> rightDataTwo = rightResultTwo.getDataForEntityList(Table_info.class);
		assertThat("在ID为" + SECOND_DATABASESET_ID + "的数据库采集任务下，有0条自定义采集SQL", rightDataTwo.size(), is(0));
	}

	/**
	 * 测试配置采集表页面,定义过滤按钮后台方法，用于回显已经对单表定义好的SQL功能
	 *
	 * 正确的数据访问1：模拟回显table_name为sys_user的表定义的对sys_user表的过滤SQL，可以拿到设置的SQL语句select * from sys_user where user_id = 2001
	 * 正确的数据访问2：模拟回显table_name为code_info的过滤SQL，因为测试数据没有设置，所以得到的结果是空字符串
	 * 错误的数据访问1：查询database_id为1002的数据，应该查不到结果，因为在这个数据库采集任务中，没有配置采集表
	 * 错误的测试用例未达到三组:getSingleTableSQL()只有一个参数，且只要用户登录，能查到数据就是能查到，查不到就是查不到
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getSingleTableSQL(){
		//正确的数据访问1：模拟回显table_name为sys_user的表定义的对sys_user表的过滤SQL，可以拿到设置的SQL语句select * from sys_user where user_id = 2001
		String rightTableNameOne = "sys_user";
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableName", rightTableNameOne)
				.post(getActionUrl("getSingleTableSQL")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("使用database_id为" + FIRST_DATABASESET_ID + "和table_name为" + rightTableNameOne + "得到1条数据", rightDataOne.getRowCount(), is(1));
		assertThat("回显table_name为sys_user表定义的过滤SQL", rightDataOne.getString(0, "sql"), is("select * from sys_user where user_id = 2001"));
		//正确的数据访问2：模拟回显table_name为code_info的过滤SQL，因为测试数据没有设置，所以拿不到
		String rightTableNameTwo = "code_info";
		String rightStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableName", rightTableNameTwo)
				.post(getActionUrl("getSingleTableSQL")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("使用database_id为" + FIRST_DATABASESET_ID + "和table_name为" + rightTableNameTwo + "得到1条数据", rightDataTwo.getRowCount(), is(1));
		assertThat("code_info表没有定义过滤SQL",  rightDataTwo.getString(0, "sql"), is(""));

		//错误的数据访问1：查询database_id为1002的数据，应该查不到结果，因为在这个数据库采集任务中，没有配置采集表
		String wrongString = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.addData("tableName", rightTableNameTwo)
				.post(getActionUrl("getSingleTableSQL")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Result wrongData = wrongResult.getDataForResult();
		assertThat("使用database_id为" + SECOND_DATABASESET_ID + "和table_name为" + rightTableNameTwo + "得到0条数据", wrongData.getRowCount(), is(0));
	}

	/**
	 * 测试配置采集表页面,选择列按钮后台功能
	 *
	 * 正确数据访问1：构造tableName为code_info，tableId为7002，colSetId为1001的测试数据
	 * 正确数据访问2：构造tableName为ftp_collect，tableId为999999，colSetId为1001的测试数据
	 * 错误的数据访问1：构造tableName为ftp_collect，tableId为999999，colSetId为1003的测试数据
	 * 错误的数据访问2：构造tableName为wzc_collect，tableId为999999，colSetId为1001的测试数据
	 * 错误的测试用例未达到三组:以上所有测试用例已经可以覆盖处理逻辑中所有的分支和错误处理了
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColumnInfo(){
		//正确数据访问1：构造tableName为code_info，tableId为7002，colSetId为1001的测试数据
		String tableNameOne = "code_info";
		String rightStringOne = new HttpClient()
				.addData("tableName", tableNameOne)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Map<Object, Object> rightDataOne = rightResultOne.getDataForMap();
		for(Map.Entry<Object, Object> entry : rightDataOne.entrySet()){
			String key = (String) entry.getKey();
			if(key.equalsIgnoreCase("tableName")){
				assertThat("返回的结果中，有一对Entry的key为tableName", key, is("tableName"));
				assertThat("返回的结果中，key为tableName的Entry，value为code_info", entry.getValue(), is(tableNameOne));
			}else if(key.equalsIgnoreCase("columnInfo")){
				assertThat("返回的结果中，有一对Entry的key为columnInfo", key, is("columnInfo"));
				List<Table_column> tableColumns = (List<Table_column>) entry.getValue();
				assertThat("返回的结果中，key为columnInfo的Entry，value为List<Table_column>,code_info表中有5列", tableColumns.size(), is(5));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}
		//正确数据访问2：构造tableName为ftp_collect，tableId为999999，colSetId为1001的测试数据
		String tableNameTwo = "ftp_collect";
		String rightStringTwo = new HttpClient()
				.addData("tableName", tableNameTwo)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableId", DEFAULT_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Map<Object, Object> rightDataTwo = rightResultTwo.getDataForMap();
		for(Map.Entry<Object, Object> entry : rightDataTwo.entrySet()){
			String key = (String) entry.getKey();
			if(key.equalsIgnoreCase("tableName")){
				assertThat("返回的结果中，有一对Entry的key为tableName", key, is("tableName"));
				assertThat("返回的结果中，key为tableName的Entry，value为ftp_collect", entry.getValue(), is(tableNameTwo));
			}else if(key.equalsIgnoreCase("columnInfo")){
				assertThat("返回的结果中，有一对Entry的key为columnInfo", key, is("columnInfo"));
				List<Table_column> tableColumns = (List<Table_column>) entry.getValue();
				assertThat("返回的结果中，key为columnInfo的Entry，value为List<Table_column>,ftp_collect表中有22列", tableColumns.size(), is(22));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}
		//错误的数据访问1：构造tableName为ftp_collect，tableId为999999，colSetId为1003的测试数据
		long wrongColSetId = 1003L;
		String wrongColSetIdString = new HttpClient()
				.addData("tableName", tableNameTwo)
				.addData("colSetId", wrongColSetId)
				.addData("tableId", DEFAULT_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongColSetIdResult = JsonUtil.toObjectSafety(wrongColSetIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongColSetIdResult.isSuccess(), is(false));
		//错误的数据访问2：构造tableName为wzc_collect，tableId为999999，colSetId为1001的测试数据
		String notExistTableName = "wzc_collect";
		String wrongTableNameString = new HttpClient()
				.addData("tableName", notExistTableName)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableId", DEFAULT_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongTableNameResult = JsonUtil.toObjectSafety(wrongTableNameString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongTableNameResult.isSuccess(), is(true));
		Map<Object, Object> wrongTableNameData = wrongTableNameResult.getDataForMap();
		for(Map.Entry<Object, Object> entry : wrongTableNameData.entrySet()){
			String key = (String) entry.getKey();
			if(key.equalsIgnoreCase("tableName")){
				assertThat("返回的结果中，有一对Entry的key为tableName", key, is("tableName"));
				assertThat("返回的结果中，key为tableName的Entry，value为ftp_collect", entry.getValue(), is(tableNameTwo));
			}else if(key.equalsIgnoreCase("columnInfo")){
				assertThat("返回的结果中，有一对Entry的key为columnInfo", key, is("columnInfo"));
				List<Table_column> tableColumns = (List<Table_column>) entry.getValue();
				assertThat("返回的结果中，key为columnInfo的Entry，value为List<Table_column>,没有wzc_collect这张表", tableColumns.isEmpty(), is(true));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}
	}

	/**
	 * 测试保存单个表的采集信息功能
	 *
	 * TODO 后面table_info表中加上是否并行抽取和分页SQL字段后，这个测试用例还要继续优化
	 * 正确数据访问1：在database_id为7001的数据库采集任务下构造新增采集ftp_collect表的数据，不选择采集列和列排序
	 * 正确数据访问2：在database_id为7001的数据库采集任务下构造新增采集object_collect表的数据，选择采集列和列排序
	 * 正确数据访问3：在database_id为7001的数据库采集任务下构造修改采集code_info表的数据，选择采集列和列排序
	 * 正确数据访问4：在database_id为7001的数据库采集任务下构造保存采集object_collect表的数据，选择采集列和列排序
	 * 错误的数据访问1：构造缺少表名的采集数据
	 * 错误的数据访问2：构造缺少表中文名的采集数据
	 * 错误的数据访问3：构造在不存在的数据库采集任务中保存采集ftp_collect表数据
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCollSingleTbInfo(){
		//正确数据访问1：在database_id为7001的数据库采集任务下构造新增采集ftp_collect表的数据，不选择采集列和列排序

		//正确数据访问2：在database_id为7001的数据库采集任务下构造新增采集object_collect表的数据，选择采集列和列排序

		//正确数据访问3：在database_id为7001的数据库采集任务下构造修改采集code_info表的数据，选择采集列和列排序

		//正确数据访问4：在database_id为7001的数据库采集任务下构造保存采集object_collect表的数据，选择采集列和列排序

		//错误的数据访问1：构造缺少表名的采集数据

		//错误的数据访问2：构造缺少表中文名的采集数据

		//错误的数据访问3：构造在不存在的数据库采集任务中保存采集ftp_collect表数据
	}

	/**
	 * 测试如果页面只有自定义SQL查询采集，保存该界面配置的所有信息功能
	 *
	 * 正确的数据访问1：模拟这样一种情况，在构造的测试数据的基础上，不采集页面上配置的两张表，然后点击下一步，也就是说现在datavase_id为FIRST_DATABASESET_ID的数据库采集任务全部是采集自定义SQL
	 * 错误的测试用例未达到三组:因为自定义表已经入库了，所以要在table_info表中删除不是自定义SQL的表信息，删除的条数可能为0-N，不关注是否删除了数据和删除的数目
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCustomizeCollTbInfo(){
		//正确的数据访问1：模拟这样一种情况，在构造的测试数据的基础上，不采集页面上配置的两张表，然后点击下一步，也就是说现在datavase_id为FIRST_DATABASESET_ID的数据库采集任务全部是采集自定义SQL
		//删除前，确认待删除数据是否存在
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " WHERE database_id = ? AND valid_e_date = ? AND is_user_defined = ?", FIRST_DATABASESET_ID, Constant.MAXDATE, IsFlag.Fou.getCode()).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("方法调用前，table_info表中的非用户自定义采集有2条", beforeCount, is(2));
		}

		//构造正确的数据访问
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveCustomizeCollTbInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		//删除后，确认数据是否真的被删除了
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " WHERE database_id = ? AND valid_e_date = ? AND is_user_defined = ?", FIRST_DATABASESET_ID, Constant.MAXDATE, IsFlag.Fou.getCode()).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("方法调用后，table_info表中的非用户自定义采集有0条", afterCount, is(0));
		}
	}

	/**
	 * 在测试用例执行完之后，删除测试数据
	 *
	 * 1、删除data_source表测试数据
	 * 2、删除agent_info表测试数据
	 * 3、删除database_set表测试数据
	 * 4、删除Collect_job_classify表测试数据
	 * 5、删除table_info表测试数据
	 * 6、删除table_column表测试数据
	 * 7、删除table_storage_info表测试数据
	 * 8、删除table_clean表测试数据
	 * 9、删除column_merge表测试数据
	 * 10、提交事务后，对数据表中的数据进行检查，断言删除是否成功
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@After
	public void after(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1、删除数据源表(data_source)测试数据
			int deleteSourceNum = SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID);
			//2、删除Agent信息表(agent_info)测试数据
			int deleteAgentNum = SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//3、删除database_set表测试数据
			int deleteDsNumOne = SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", FIRST_DB_AGENT_ID);
			int deleteDsNumTwo = SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", SECOND_DB_AGENT_ID);
			//4、删除collect_job_classify表测试数据
			int deleteCJCNum = SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//5、删除table_info表测试数据
			int delTableInfoNum = SqlOperator.execute(db, "delete from " + Table_info.TableName + " where database_id = ? ", FIRST_DATABASESET_ID);
			//6、删除table_column表测试数据
			int delSysUserTbNum = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			int delCodeInfoTbNum = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			//7、删除table_storage_info表测试数据
			int delSysUserTsiNum = SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			int delCodeInfoTsiNum = SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			//8、删除table_clean表测试数据
			int delSysUserTcNum = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			int delCodeInfoTcNum = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			//9、删除column_merge表测试数据
			int delColumnMergeNum = SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			//10、提交事务后，对数据表中的数据进行检查，断言删除是否成功
			SqlOperator.commitTransaction(db);
			/*
			long dataSources = SqlOperator.queryNumber(db, "select count(1) from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据源数据有:" + deleteSourceNum + "条", dataSources, is(0L));

			long agents = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的Agent数据有:" + deleteAgentNum + "条", agents, is(0L));

			long dataSourceSetsOne = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE agent_id = ?", FIRST_DB_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			long dataSourceSetsTwo = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE agent_id = ?", SECOND_DB_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据库设置表数据有:" + (deleteDsNumOne + deleteDsNumTwo) + "条", dataSourceSetsOne + dataSourceSetsTwo, is(0L));

			long collectJobClassifyNum = SqlOperator.queryNumber(db, "select count(1) from " + Collect_job_classify.TableName + " WHERE user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的采集作业分类表数据有:" + deleteCJCNum + "条", collectJobClassifyNum, is(0L));

			long tableInfos = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where database_id = ? ", FIRST_DATABASESET_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据库对应表数据有:" + delTableInfoNum + "条", tableInfos, is(0L));

			long sysUsers = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ? ", SYS_USER_TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			long codeInfos = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的表对应字段表数据有:" + (delSysUserTbNum + delCodeInfoTbNum) + "条", sysUsers + codeInfos, is(0L));

			long sysUserStorages = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ? ", SYS_USER_TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			long codeInfoStorages = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的表存储信息表数据有:" + (delSysUserTsiNum + delCodeInfoTsiNum) + "条", sysUserStorages + codeInfoStorages, is(0L));

			long sysUserTCs = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? ", SYS_USER_TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			long codeInfoTCs = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的表清洗参数信息表数据有:" + (delSysUserTcNum + delCodeInfoTcNum) + "条", sysUserTCs + codeInfoTCs, is(0L));

			long columnMerges = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where table_id = ? ", SYS_USER_TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的列合并信息表数据有:" + delColumnMergeNum + "条", columnMerges, is(0L));
			*/
		}
	}
}
