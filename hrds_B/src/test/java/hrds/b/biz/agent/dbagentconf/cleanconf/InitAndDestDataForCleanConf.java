package hrds.b.biz.agent.dbagentconf.cleanconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.b.biz.agent.dbagentconf.InitBaseData;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "为CleanConfStepActionTest初始化和销毁数据", author = "WangZhengcheng")
/*
 * 由于构造测试数据就有较多代码，所以将这部分代码迁移出来，CleanConfStepActionTest中只保存对被测方法的测试用例
 * */
public class InitAndDestDataForCleanConf {

	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long CODE_INFO_TABLE_ID = 7002L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
	private static final long DATA_SOURCE_TABLE_ID = 7004L;
	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long BASE_SYS_USER_PRIMARY = 2000L;
	private static final long FIRST_DB_AGENT_ID = 7001L;
	private static final long SECOND_DB_AGENT_ID = 7002L;
	private static final long TEST_USER_ID = -9997L;
	private static final long UNEXPECTED_ID = 999999999L;
	private static final String PRE_COMPLE_FLAG = "1";
	private static final String POST_COMPLE_FLAG = "2";
	private static final JSONObject tableCleanOrder = InitBaseData.initTableCleanOrder();
	private static final JSONObject columnCleanOrder = InitBaseData.initColumnCleanOrder();

	public static void before(){
		//3、构造data_source表测试数据
		Data_source dataSource = InitBaseData.buildDataSourceData();

		//4、构造agent_info表测试数据
		List<Agent_info> agents = InitBaseData.buildAgentInfosData();

		//5、构造database_set表测试数据
		List<Database_set> databases = InitBaseData.buildDbSetData();

		//6、构造Collect_job_classify表测试数据
		List<Collect_job_classify> classifies = InitBaseData.buildClassifyData();

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
		for(int i = 1; i <= 11; i++){
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
				case 11 :
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "login_date";
					columnType = "char";
					columnChName = "登录日期";
					remark = "11";
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

		List<Table_column> codeInfos = InitBaseData.buildCodeInfoTbColData();

		//9、构造table_clean表测试数据
		List<Table_clean> tableCleans = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			long tbCleanId;
			String compleType;
			String cleanType;
			long tableId;
			String compleChar;
			long length;
			String oriField;
			String newField;
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


		//10、构造column_clean表测试数据，给create_id和dep_id设置字符补齐
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

		//11、column_clean表测试数据，给user_name设置字符替换
		Column_clean replace = new Column_clean();
		replace.setCol_clean_id(555555L);
		replace.setColumn_id(2005L);
		replace.setClean_type(CleanType.ZiFuTiHuan.getCode());
		replace.setField("ceshi");
		replace.setReplace_feild("test");

		//12、column_clean表测试数据，给login_date设置日期格式化
		Column_clean dateFormat = new Column_clean();
		dateFormat.setCol_clean_id(999999L);
		dateFormat.setColumn_id(2010L);
		dateFormat.setClean_type(CleanType.ShiJianZhuanHuan.getCode());
		dateFormat.setOld_format("YYYY-MM-DD");
		dateFormat.setConvert_format("YYYY-MM");

		//13、column_clean表测试数据，给ci_sp_name（3004L）设置列拆分
		Column_clean spilt = new Column_clean();
		spilt.setCol_clean_id(101010101L);
		spilt.setColumn_id(3004L);
		spilt.setClean_type(CleanType.ZiFuChaiFen.getCode());

		//column_clean表测试数据，给ci_sp_classname（3003L）设置列拆分
		Column_clean spiltTwo = new Column_clean();
		spiltTwo.setCol_clean_id(101010102L);
		spiltTwo.setColumn_id(3003L);
		spiltTwo.setClean_type(CleanType.ZiFuChaiFen.getCode());

		//14、clean_parameter表测试数据
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

		//15、构造column_spilt表测试数据，按照偏移量拆分ci_sp_name
		List<Column_split> offsetSpilts = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			long colSplitId = i % 2 == 0 ? 1111111L : 2222222L;
			String offset = i % 2 == 0 ? "3" : "0";
			String columnName = i % 2 == 0 ? "ci_s" : "p_name";
			String spiltType = "1";
			String columnChName = i % 2 == 0 ? "ci_s_ch" : "p_name_ch";
			String columnType = "varchar(512)";
			long colCleanId = 101010101L;
			long columnId = 3004L;

			Column_split columnSplit = new Column_split();
			columnSplit.setCol_split_id(colSplitId);
			columnSplit.setCol_offset(offset);
			columnSplit.setCol_name(columnName);
			columnSplit.setSplit_type(spiltType);
			columnSplit.setCol_zhname(columnChName);
			columnSplit.setCol_type(columnType);
			columnSplit.setCol_clean_id(colCleanId);
			columnSplit.setColumn_id(columnId);
			columnSplit.setValid_e_date(Constant.MAXDATE);
			columnSplit.setValid_s_date(DateUtil.getSysDate());

			offsetSpilts.add(columnSplit);
		}

		//16、构造column_spilt表测试数据，按照下划线拆分ci_sp_classname
		List<Column_split> underLintSpilts = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			long colSplitId = 0;
			String columnName = null;
			String spiltType = "2";
			String columnChName = null;
			String columnType = "varchar(512)";
			long colCleanId = 101010102L;
			long columnId = 3003L;
			String splitSep = "_";
			long seq = 0;
			switch (i){
				case 0 :
					colSplitId = 101010103L;
					columnName = "ci";
					columnChName = "ci_ch";
					seq = 1;
					break;
				case 1 :
					colSplitId = 101010104L;
					columnName = "sp";
					columnChName = "sp_ch";
					seq = 2;
					break;
				case 2 :
					colSplitId = 101010105L;
					columnName = "classname";
					columnChName = "classname_ch";
					seq = 3;
					break;
			}
			Column_split columnSplit = new Column_split();
			columnSplit.setCol_split_id(colSplitId);
			columnSplit.setCol_name(columnName);
			columnSplit.setSplit_type(spiltType);
			columnSplit.setCol_zhname(columnChName);
			columnSplit.setCol_type(columnType);
			columnSplit.setCol_clean_id(colCleanId);
			columnSplit.setColumn_id(columnId);
			columnSplit.setValid_e_date(Constant.MAXDATE);
			columnSplit.setValid_s_date(DateUtil.getSysDate());
			columnSplit.setSeq(seq);
			columnSplit.setSplit_sep(splitSep);

			underLintSpilts.add(columnSplit);
		}

		//17、由于配置了列拆分，所以要构造模拟数据将拆分后的列加入Table_column表中
		List<Table_column> splitOne = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			long columnId = i % 2 == 0 ? 121212L : 232323L;
			String columnName = i % 2 == 0 ? "ci_s" : "p_name";
			String columnChName = i % 2 == 0 ? "ci_s_ch" : "p_name_ch";

			Table_column tableColumn = new Table_column();
			tableColumn.setTable_id(CODE_INFO_TABLE_ID);
			tableColumn.setIs_new(IsFlag.Shi.getCode());
			tableColumn.setColumn_id(columnId);
			tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
			tableColumn.setColume_name(columnName);
			tableColumn.setColumn_type("varchar(512)");
			tableColumn.setColume_ch_name(columnChName);
			tableColumn.setValid_s_date(DateUtil.getSysDate());
			tableColumn.setValid_e_date(Constant.MAXDATE);

			splitOne.add(tableColumn);
		}

		List<Table_column> splitTwo = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			long columnId = 0;
			String columnName = null;
			String columnChName = null;

			switch (i){
				case 0 :
					columnId = 141414L;
					columnName = "ci";
					columnChName = "ci_ch";
					break;
				case 1 :
					columnId = 151515L;
					columnName = "sp";
					columnChName = "sp_ch";
					break;
				case 2 :
					columnId = 161616L;
					columnName = "classname";
					columnChName = "classname_ch";
					break;
			}

			Table_column tableColumn = new Table_column();
			tableColumn.setTable_id(CODE_INFO_TABLE_ID);
			tableColumn.setIs_new(IsFlag.Shi.getCode());
			tableColumn.setColumn_id(columnId);
			tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
			tableColumn.setColume_name(columnName);
			tableColumn.setColumn_type("varchar(512)");
			tableColumn.setColume_ch_name(columnChName);
			tableColumn.setValid_s_date(DateUtil.getSysDate());
			tableColumn.setValid_e_date(Constant.MAXDATE);

			splitTwo.add(tableColumn);
		}

		//18、column_merge表测试数据，对sys_user表中的user_mobile和useris_admin合并成列，名叫user_mobile_admin
		Column_merge columnMerge = new Column_merge();
		columnMerge.setCol_merge_id(16161616L);
		columnMerge.setTable_id(SYS_USER_TABLE_ID);
		columnMerge.setCol_name("user_mobile_admin");
		columnMerge.setOld_name("user_mobile和useris_admin");
		columnMerge.setCol_zhname("user_mobile_admin_ch");
		columnMerge.setCol_type("varchar(512)");
		columnMerge.setValid_s_date(DateUtil.getSysDate());
		columnMerge.setValid_e_date(Constant.MAXDATE);

		//19、由于配置了列合并，需要把合并后的列入到table_column表中
		Table_column mergeColumn = new Table_column();
		mergeColumn.setColumn_id(1717171717L);
		mergeColumn.setTable_id(SYS_USER_TABLE_ID);
		mergeColumn.setIs_new(IsFlag.Shi.getCode());
		mergeColumn.setIs_primary_key(IsFlag.Fou.getCode());
		mergeColumn.setColume_name("user_mobile_admin");
		mergeColumn.setColumn_type("varchar(512)");
		mergeColumn.setColume_ch_name("user_mobile_admin_ch");
		mergeColumn.setValid_s_date(DateUtil.getSysDate());
		mergeColumn.setValid_e_date(Constant.MAXDATE);

		//插入数据
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
			assertThat("表对应字段表测试数据初始化", tableColumnCount, is(16));

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
			int dateFormatCount = dateFormat.add(db);
			columnCleanCount += dateFormatCount;
			int spiltCount = spilt.add(db);
			columnCleanCount += spiltCount;
			int spiltTwoCount = spiltTwo.add(db);
			columnCleanCount += spiltTwoCount;
			assertThat("列清洗参数信息表测试数据初始化", columnCleanCount, is(6));

			//插入clean_parameter表测试数据
			int cleanParameterCount = 0;
			for(Clean_parameter cleanParameter : cleanParameters){
				int count = cleanParameter.add(db);
				cleanParameterCount += count;
			}
			assertThat("清洗作业参数属性表测试数据初始化", cleanParameterCount, is(2));

			//插入column_spilt表测试数据
			int columnSpiltCount = 0;
			for(Column_split columnSplit : offsetSpilts){
				int count = columnSplit.add(db);
				columnSpiltCount += count;
			}
			for(Column_split columnSplit : underLintSpilts){
				int count = columnSplit.add(db);
				columnSpiltCount += count;
			}
			assertThat("列拆分信息表测试数据初始化", columnSpiltCount, is(5));

			//将列拆分信息加入Table_column表
			int spiltColumnCount = 0;
			for(Table_column tableColumn : splitOne){
				int count = tableColumn.add(db);
				spiltColumnCount += count;
			}
			for(Table_column tableColumn : splitTwo){
				int count = tableColumn.add(db);
				spiltColumnCount += count;
			}
			assertThat("将列拆分信息加入Table_column表", spiltColumnCount, is(5));

			//插入column_merge表
			int mergeCount = columnMerge.add(db);
			assertThat("插入column_merge表测试数据", mergeCount, is(1));

			//由于配置了列合并，需要把合并后的列入到table_column表中
			int mergeColumnCount = mergeColumn.add(db);
			assertThat("把合并后的列入到table_column表中", mergeColumnCount, is(1));

			SqlOperator.commitTransaction(db);
		}
	}

	public static void after(){
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
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2011L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 3004L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 3003L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2010L);
			//9、删除clean_parameter表测试数据
			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? ", FIRST_DATABASESET_ID);
			//10、删除column_spilt表测试数据
			SqlOperator.execute(db, "delete from " + Column_split.TableName + " where column_id = ? ", 3004L);
			SqlOperator.execute(db, "delete from " + Column_split.TableName + " where column_id = ? ", 3003L);
			//11、删除column_merge表测试数据
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			//11、提交事务后
			SqlOperator.commitTransaction(db);
		}
	}
}
