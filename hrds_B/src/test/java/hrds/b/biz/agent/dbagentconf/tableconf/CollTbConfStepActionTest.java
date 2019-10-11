package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
	 *          7-1、table_id:7001,table_name:sys_user,按照画面配置信息进行采集
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
			String databaseType = i % 2 == 0 ? DatabaseType.Postgresql.getCode() : DatabaseType.DB2.getCode();
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
					customizeSQL = " select * from agent_info";
					customizFlag = IsFlag.Shi.getCode();
					break;
				case 4:
					tableId = DATA_SOURCE_TABLE_ID;
					tableName = "data_source";
					tableChName = "数据源表";
					customizeSQL = " select * from data_source";
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
	 * 测试根据模糊表名和数据库设置id和agentId得到表相关信息功能
	 *
	 * 正确数据访问1：构造正确的colSetId(FIRST_DATABASESET_ID)
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
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

	}

	/**
	 * 测试根据数据库设置id和agentId得到所有表相关信息功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTableInfo(){

	}

	/**
	 * 测试SQL查询设置页面，保存按钮后台方法功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAllTableInfo(){

	}

	/**
	 * 测试SQL查询设置页面操作栏，删除按钮后台方法功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllSQL(){

	}

	/**
	 * 测试SQL查询设置页面操作栏，删除按钮后台方法功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteSQLConf(){

	}

	/**
	 * 测试配置采集表页面，SQL设置按钮后台方法功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAllSQLs(){

	}

	/**
	 * 测试配置采集表页面,定义过滤按钮后台方法，用于回显已经对单表定义好的SQL功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getSingleTableSQL(){

	}

	/**
	 * 测试配置采集表页面,选择列按钮后台功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColumnInfo(){

	}

	/**
	 * 测试保存单个表的采集信息功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCollSingleTbInfo(){

	}

	/**
	 * 测试如果页面只有自定义SQL查询采集，保存该界面配置的所有信息功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的数据访问2：
	 * 错误的数据访问3：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCustomizeCollTbInfo(){

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
		}
	}
}
