package hrds.b.biz.agent.dbagentconf.startwayconf;

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
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.FillingType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.codes.Store_type;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Clean_parameter;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Column_clean;
import hrds.commons.entity.Column_merge;
import hrds.commons.entity.Column_split;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Data_store_layer_added;
import hrds.commons.entity.Data_store_layer_attr;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Dcol_relation_store;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Dtab_relation_store;
import hrds.commons.entity.Orig_code_info;
import hrds.commons.entity.Orig_syso_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.entity.Table_clean;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Table_storage_info;
import hrds.commons.utils.Constant;
import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "为StartWayConfActionTest初始化和销毁数据", author = "WangZhengcheng")
/*
 * 由于构造测试数据就有较多代码，所以将这部分代码迁移出来，StartWayConfActionTest中只保存对被测方法的测试用例
 * */
public class InitAndDestDataForStartWay {

	//测试数据用户ID
	private static final long TEST_USER_ID = 9997L;
	private static final long TEST_DEPT_ID = 9987L;

	private static final long SOURCE_ID = 1L;

	private static final long FIRST_DB_AGENT_ID = 7001L;
	private static final long SECOND_DB_AGENT_ID = 7002L;

	private static final long FIRST_DATABASESET_ID = 1001L;

	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long CODE_INFO_TABLE_ID = 7002L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
	private static final long DATA_SOURCE_TABLE_ID = 7004L;

	private static final long BASE_SYS_USER_PRIMARY = 2000L;
	private static final long BASE_EXTRACTION_DEF_ID = 7788L;
	private static final long BASE_TB_STORAGE_ID = 10669588L;
	private static final long BASE_LAYER_ID = 4399L;
	private static final long BASE_LAYER_ARR_ID = 43999L;
	private static final long DATA_STORE_LAYER_ADDED_ID = 439999L;

	private static final long UNEXPECTED_ID = 999999999L;

	private static final JSONObject tableCleanOrder = BaseInitData.initTableCleanOrder();
	private static final JSONObject columnCleanOrder = BaseInitData.initColumnCleanOrder();

	public static void before() {
		//1、构造sys_user表测试数据
		Sys_user user = BaseInitData.buildSysUserData();

		//2、构造department_info表测试数据
		Department_info departmentInfo = BaseInitData.buildDeptInfoData();

		//3、构造data_source表测试数据
		Data_source dataSource = BaseInitData.buildDataSourceData();

		//4、构造agent_info表测试数据
		List<Agent_info> agents = BaseInitData.buildAgentInfosData();

		//5、构造database_set表测试数据
		List<Database_set> databases = BaseInitData.buildDbSetData();

		//6、构造Collect_job_classify表测试数据
		List<Collect_job_classify> classifies = BaseInitData.buildClassifyData();

		//7、构建table_info测试数据
		List<Table_info> tableInfos = new ArrayList<>();
		for (int i = 1; i <= 4; i++) {
			long tableId;
			String tableName;
			String tableChName;
			String customizeSQL;
			String customizFlag;
			String parallelFlag;
			String pageSql;
			String tableCount;
			int dataIncrement = 0;
			int pageParallels = 5;
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
					tableCount = "100000";
					break;
				case 2:
					tableId = CODE_INFO_TABLE_ID;
					tableName = "code_info";
					tableChName = "代码信息表";
					customizeSQL = "";
					customizFlag = IsFlag.Fou.getCode();
					parallelFlag = IsFlag.Shi.getCode();
					pageSql = "select * from code_info limit 10";
					tableCount = "100000";
					dataIncrement = 1000;
					pageParallels = 6;
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
					tableCount = "100000";
					break;
				case 4:
					tableId = DATA_SOURCE_TABLE_ID;
					tableName = "data_source";
					tableChName = "数据源表";
					//自定义采集
					customizeSQL = "select source_id, datasource_number, datasource_name from data_source where source_id = "
						+ SOURCE_ID;
					customizFlag = IsFlag.Shi.getCode();
					parallelFlag = IsFlag.Fou.getCode();
					pageSql = "";
					tableCount = "100000";
					break;
				default:
					tableId = 0L;
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
					customizFlag = "error_customizFlag";
					parallelFlag = "error_parallelFlag";
					pageSql = "unexpected_pageSql";
					tableCount = "unexpected_tableCount";
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
			tableInfo.setIs_register(IsFlag.Fou.getCode());
			tableInfo.setIs_parallel(parallelFlag);
			tableInfo.setPage_sql(pageSql);
			tableInfo.setTable_count(tableCount);
			tableInfo.setDataincrement(dataIncrement);
			tableInfo.setPageparallels(pageParallels);

			tableInfos.add(tableInfo);
		}

		//8、构建table_column表测试数据
		List<Table_column> sysUsers = new ArrayList<>();
		for (int i = 1; i <= 11; i++) {
			String primaryKeyFlag;
			String columnName;
			String columnType;
			String columnChName;
			String remark;
			switch (i) {
				case 1:
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "user_id";
					columnType = "int8";
					columnChName = "主键";
					remark = "1";
					break;
				case 2:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "create_id";
					columnType = "int8";
					columnChName = "创建用户者ID";
					remark = "2";
					break;
				case 3:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "dep_id";
					columnType = "int8";
					columnChName = "部门ID";
					remark = "3";
					break;
				case 4:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "role_id";
					columnType = "int8";
					columnChName = "角色ID";
					remark = "4";
					break;
				case 5:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_name";
					columnType = "varchar";
					columnChName = "用户名";
					remark = "5";
					break;
				case 6:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_password";
					columnType = "varchar";
					columnChName = "密码";
					remark = "6";
					break;
				case 7:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_email";
					columnType = "varchar";
					columnChName = "邮箱";
					remark = "7";
					break;
				case 8:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_mobile";
					columnType = "varchar";
					columnChName = "电话";
					remark = "8";
					break;
				case 9:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "useris_admin";
					columnType = "char";
					columnChName = "是否管理员";
					remark = "9";
					break;
				case 10:
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "user_type";
					columnType = "char";
					columnChName = "用户类型";
					remark = "10";
					break;
				case 11:
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
			sysUserColumn.setColumn_name(columnName);
			sysUserColumn.setColumn_type(columnType);
			sysUserColumn.setColumn_ch_name(columnChName);
			sysUserColumn.setTable_id(SYS_USER_TABLE_ID);
			sysUserColumn.setValid_s_date(DateUtil.getSysDate());
			sysUserColumn.setValid_e_date(Constant.MAXDATE);
			sysUserColumn.setIs_alive(IsFlag.Shi.getCode());
			sysUserColumn.setIs_new(IsFlag.Fou.getCode());
			sysUserColumn.setTc_or(columnCleanOrder.toJSONString());
			sysUserColumn.setTc_remark(remark);

			sysUsers.add(sysUserColumn);
		}

		List<Table_column> codeInfos = BaseInitData.buildCodeInfoTbColData();

		List<Table_column> dataSources = BaseInitData.buildDataSourceTbColData();

		List<Table_column> agentInfos = BaseInitData.buildAgentInfoTbColData();

		//9、构造table_clean表测试数据
		List<Table_clean> tableCleans = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			long tbCleanId;
			String compleType;
			String cleanType;
			long tableId;
			String compleChar;
			long length;
			String oriField;
			String newField;
			switch (i) {
				case 1:
					tbCleanId = 11111L;
					compleType = FillingType.QianBuQi.getCode();
					cleanType = CleanType.ZiFuBuQi.getCode();
					tableId = SYS_USER_TABLE_ID;
					compleChar = StringUtil.string2Unicode("wzc");
					length = 3L;
					oriField = "";
					newField = "";
					break;
				case 2:
					tbCleanId = 111111L;
					compleType = "";
					cleanType = CleanType.ZiFuTiHuan.getCode();
					tableId = SYS_USER_TABLE_ID;
					compleChar = "";
					length = 0;
					oriField = StringUtil.string2Unicode("wzc");
					newField = StringUtil.string2Unicode("wqp");
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
		for (int i = 0; i < 2; i++) {
			long colCleanId = i % 2 == 0 ? 22222L : 33333L;
			String cleanType = CleanType.ZiFuBuQi.getCode();
			String compleType = i % 2 == 0 ? FillingType.QianBuQi.getCode() : FillingType.HouBuQi.getCode();
			String compleChar = i % 2 == 0 ? StringUtil.string2Unicode("wzc") : StringUtil.string2Unicode(" ");
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
		replace.setField(StringUtil.string2Unicode("ceshi"));
		replace.setReplace_feild(StringUtil.string2Unicode("test"));

		//12、column_clean表测试数据，给login_date设置日期格式化
		Column_clean dateFormat = new Column_clean();
		dateFormat.setCol_clean_id(999999L);
		dateFormat.setColumn_id(2011L);
		dateFormat.setClean_type(CleanType.ShiJianZhuanHuan.getCode());
		dateFormat.setOld_format("YYYY-MM-DD");
		dateFormat.setConvert_format("YYYY-MM");

		//column_clean表测试数据，给user_type设置码值转换
		Column_clean codeValue = new Column_clean();
		codeValue.setCol_clean_id(999989L);
		codeValue.setColumn_id(2010L);
		codeValue.setCodename("codeClassify_one");
		codeValue.setCodesys("origSysCode_one");
		codeValue.setClean_type(CleanType.MaZhiZhuanHuan.getCode());

		//column_clean表测试数据，给ci_sp_name（3004L）设置列拆分
		Column_clean spilt = new Column_clean();
		spilt.setCol_clean_id(101010101L);
		spilt.setColumn_id(3004L);
		spilt.setClean_type(CleanType.ZiFuChaiFen.getCode());

		//column_clean表测试数据，给ci_sp_classname（3003L）设置列拆分
		Column_clean spiltTwo = new Column_clean();
		spiltTwo.setCol_clean_id(101010102L);
		spiltTwo.setColumn_id(3003L);
		spiltTwo.setClean_type(CleanType.ZiFuChaiFen.getCode());

		//13、clean_parameter表测试数据
		List<Clean_parameter> cleanParameters = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long cId = i % 2 == 0 ? 666666L : 777777L;
			String cleanType = i % 2 == 0 ? CleanType.ZiFuBuQi.getCode() : CleanType.ZiFuTiHuan.getCode();
			String complChar;
			long compLength;
			String complType = null;
			String oriField;
			String newField;
			switch (i) {
				case 0:
					complChar = "cleanparameter";
					compLength = 14;
					complType = "1";
					oriField = StringUtil.string2Unicode("qwer");
					newField = StringUtil.string2Unicode("asdf");
					break;
				case 1:
					complChar = "";
					compLength = 0;
					oriField = StringUtil.string2Unicode("test_orifield");
					newField = StringUtil.string2Unicode("test_newField");
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

		//14、构造column_spilt表测试数据，按照偏移量拆分ci_sp_name
		List<Column_split> offsetSpilts = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long colSplitId = i % 2 == 0 ? 1111111L : 2222222L;
			String offset = i % 2 == 0 ? "3" : "0";
			String columnName = i % 2 == 0 ? "ci_sp" : "_name";
			String spiltType = "1";
			String columnChName = i % 2 == 0 ? "ci_sp_ch" : "_name_ch";
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

		//15、构造column_spilt表测试数据，按照下划线拆分ci_sp_classname
		List<Column_split> underLintSpilts = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			long colSplitId = 0;
			String columnName = null;
			String spiltType = "2";
			String columnChName = null;
			String columnType = "varchar(512)";
			long colCleanId = 101010102L;
			long columnId = 3003L;
			String splitSep = "_";
			long seq = 0;
			switch (i) {
				case 0:
					colSplitId = 101010103L;
					columnName = "ci";
					columnChName = "ci_ch";
					seq = 1;
					break;
				case 1:
					colSplitId = 101010104L;
					columnName = "sp";
					columnChName = "sp_ch";
					seq = 2;
					break;
				case 2:
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

		//16、由于配置了列拆分，所以要构造模拟数据将拆分后的列加入Table_column表中
		List<Table_column> splitOne = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long columnId = i % 2 == 0 ? 121212L : 232323L;
			String columnName = i % 2 == 0 ? "ci_sp" : "_name";
			String columnChName = i % 2 == 0 ? "ci_sp_ch" : "_name_ch";

			Table_column tableColumn = new Table_column();
			tableColumn.setTable_id(CODE_INFO_TABLE_ID);
			tableColumn.setIs_new(IsFlag.Shi.getCode());
			tableColumn.setColumn_id(columnId);
			tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
			tableColumn.setColumn_name(columnName);
			tableColumn.setColumn_type("varchar(512)");
			tableColumn.setColumn_ch_name(columnChName);
			tableColumn.setValid_s_date(DateUtil.getSysDate());
			tableColumn.setValid_e_date(Constant.MAXDATE);

			splitOne.add(tableColumn);
		}

		List<Table_column> splitTwo = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			long columnId = 0;
			String columnName = null;
			String columnChName = null;

			switch (i) {
				case 0:
					columnId = 141414L;
					columnName = "ci";
					columnChName = "ci_ch";
					break;
				case 1:
					columnId = 151515L;
					columnName = "sp";
					columnChName = "sp_ch";
					break;
				case 2:
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
			tableColumn.setColumn_name(columnName);
			tableColumn.setColumn_type("varchar(512)");
			tableColumn.setColumn_ch_name(columnChName);
			tableColumn.setValid_s_date(DateUtil.getSysDate());
			tableColumn.setValid_e_date(Constant.MAXDATE);

			splitTwo.add(tableColumn);
		}

		//17、column_merge表测试数据，对sys_user表中的user_mobile和useris_admin合并成列，名叫user_mobile_admin
		Column_merge columnMerge = new Column_merge();
		columnMerge.setCol_merge_id(16161616L);
		columnMerge.setTable_id(SYS_USER_TABLE_ID);
		columnMerge.setCol_name("user_mobile_admin");
		columnMerge.setOld_name("user_mobile和useris_admin");
		columnMerge.setCol_zhname("user_mobile_admin_ch");
		columnMerge.setCol_type("varchar(512)");
		columnMerge.setValid_s_date(DateUtil.getSysDate());
		columnMerge.setValid_e_date(Constant.MAXDATE);

		//18、由于配置了列合并，需要把合并后的列入到table_column表中
		Table_column mergeColumn = new Table_column();
		mergeColumn.setColumn_id(1717171717L);
		mergeColumn.setTable_id(SYS_USER_TABLE_ID);
		mergeColumn.setIs_new(IsFlag.Shi.getCode());
		mergeColumn.setIs_primary_key(IsFlag.Fou.getCode());
		mergeColumn.setColumn_name("user_mobile_admin");
		mergeColumn.setColumn_type("varchar(512)");
		mergeColumn.setColumn_ch_name("user_mobile_admin_ch");
		mergeColumn.setValid_s_date(DateUtil.getSysDate());
		mergeColumn.setValid_e_date(Constant.MAXDATE);

		//19、由于需要测试码值转换功能，因为构造码值系统相关测试数据
		List<Orig_syso_info> origSysoInfos = BaseInitData.buildOrigSysInfo();

		List<Orig_code_info> origCodeInfos = BaseInitData.buildOrigCodeInfo();

		//20、构造数据抽取定义相关测试数据
		List<Data_extraction_def> extractionDefs = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			long tableId;
			String extractType;
			String rowSeparator;
			String databaseSeparatorr;
			String fileFormat;
			String planeUrl;
			switch (i) {
				case 0:
					tableId = SYS_USER_TABLE_ID;
					extractType = DataExtractType.ShuJuKuChouQuLuoDi.getCode();
					fileFormat = FileFormat.FeiDingChang.getCode();
					rowSeparator = "\n";
					databaseSeparatorr = "|";
					planeUrl = "/root";
					break;
				case 1:
					tableId = CODE_INFO_TABLE_ID;
					extractType = DataExtractType.ShuJuKuChouQuLuoDi.getCode();
					fileFormat = FileFormat.DingChang.getCode();
					rowSeparator = "";
					databaseSeparatorr = "";
					planeUrl = "/home";
					break;
				case 2:
					tableId = AGENT_INFO_TABLE_ID;
					extractType = DataExtractType.ShuJuKuChouQuLuoDi.getCode();
					fileFormat = FileFormat.ORC.getCode();
					rowSeparator = "";
					databaseSeparatorr = "";
					planeUrl = "";
					break;
				case 3:
					tableId = DATA_SOURCE_TABLE_ID;
					extractType = DataExtractType.ShuJuKuChouQuLuoDi.getCode();
					fileFormat = FileFormat.FeiDingChang.getCode();
					rowSeparator = "|";
					databaseSeparatorr = " ";
					planeUrl = "";
					break;
				default:
					tableId = UNEXPECTED_ID;
					extractType = "unexpected_extractType";
					rowSeparator = "unexpected_rowSeparator";
					databaseSeparatorr = "unexpected_databaseSeparatorr";
					fileFormat = "unexpected_fileFormat";
					planeUrl = "unexpected_planeUrl";
			}
			Data_extraction_def def = new Data_extraction_def();
			def.setDed_id(BASE_EXTRACTION_DEF_ID + i);
			def.setTable_id(tableId);
			def.setData_extract_type(extractType);
			def.setRow_separator(rowSeparator);
			def.setDatabase_separatorr(databaseSeparatorr);
			def.setDatabase_code(DataBaseCode.UTF_8.getCode());
			def.setDbfile_format(fileFormat);
			def.setPlane_url(planeUrl);
			def.setIs_header(IsFlag.Shi.getCode());

			extractionDefs.add(def);
		}

		//21、构造table_storage_info表测试数据
		List<Table_storage_info> tableStorageInfos = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long id = i % 2 == 0 ? BASE_TB_STORAGE_ID : BASE_TB_STORAGE_ID + 1;
			String fileFormat = i % 2 == 0 ? FileFormat.ORC.getCode() : FileFormat.FeiDingChang.getCode();
			String storageType = i % 2 == 0 ? StorageType.ZengLiang.getCode() : StorageType.ZhuiJia.getCode();
			String zipperFlag = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			long tableId = i % 2 == 0 ? AGENT_INFO_TABLE_ID : DATA_SOURCE_TABLE_ID;
			long time = i % 2 == 0 ? 7L : 1L;

			Table_storage_info storageInfo = new Table_storage_info();
			storageInfo.setStorage_id(id);
			storageInfo.setFile_format(fileFormat);
			storageInfo.setStorage_type(storageType);
			storageInfo.setIs_zipper(zipperFlag);
			storageInfo.setStorage_time(time);
			storageInfo.setTable_id(tableId);

			tableStorageInfos.add(storageInfo);
		}

		//22、构造data_store_layer表测试数据
		List<Data_store_layer> storeLayers = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			String dslName;
			String storeType;
			switch (i) {
				case 0:
					dslName = "SOLR";
					storeType = Store_type.SOLR.getCode();
					break;
				case 1:
					dslName = "Oralce";
					storeType = Store_type.DATABASE.getCode();
					break;
				case 2:
					dslName = "ElasticSearch";
					storeType = Store_type.ElasticSearch.getCode();
					break;
				case 3:
					dslName = "HBASE";
					storeType = Store_type.HBASE.getCode();
					break;
				case 4:
					dslName = "MONGODB";
					storeType = Store_type.MONGODB.getCode();
					break;
				default:
					dslName = "unexpected_dslName";
					storeType = "unexpected_storeType";
			}
			Data_store_layer layer = new Data_store_layer();
			layer.setDsl_id(BASE_LAYER_ID + i);
			layer.setDsl_name(dslName);
			layer.setStore_type(storeType);

			storeLayers.add(layer);
		}

		//23、构造data_store_layer_attr表测试数据
		List<Data_store_layer_attr> layerAttrs = new ArrayList<>();
		for (int i = 0; i < 11; i++) {
			String propertyKey;
			String propertyVal;
			long dslId;
			switch (i) {
				case 0:
					propertyKey = "database_name";
					propertyVal = "coll_sto_dest_test_dbname";
					dslId = 4400L;
					break;
				case 1:
					propertyKey = "database_pwd";
					propertyVal = "coll_sto_dest_test_pwd";
					dslId = 4400L;
					break;
				case 2:
					propertyKey = "database_drive";
					propertyVal = "coll_sto_dest_test_driver";
					dslId = 4400L;
					break;
				case 3:
					propertyKey = "user_name";
					propertyVal = "coll_sto_dest_test_username";
					dslId = 4400L;
					break;
				case 4:
					propertyKey = "database_ip";
					propertyVal = "coll_sto_dest_test_ip";
					dslId = 4400L;
					break;
				case 5:
					propertyKey = "database_port";
					propertyVal = "coll_sto_dest_test_port";
					dslId = 4400L;
					break;
				case 6:
					propertyKey = "jdbc_url";
					propertyVal = "coll_sto_dest_test_jdbc_url";
					dslId = 4400L;
					break;
				case 7:
					propertyKey = "SolarUrl";
					propertyVal = "https://SolarUrl";
					dslId = 4399L;
					break;
				case 8:
					propertyKey = "Hbase-site-path";
					propertyVal = "Hbase-site.xml";
					dslId = 4402L;
					break;
				case 9:
					propertyKey = "Core-site-path";
					propertyVal = "Core-site.xml";
					dslId = 4402L;
					break;
				case 10:
					propertyKey = "Hdfs-site-path";
					propertyVal = "Hdfs-site.xml";
					dslId = 4402L;
					break;
				default:
					propertyKey = "unexpected_propertyKey";
					propertyVal = "unexpected_propertyVal";
					dslId = UNEXPECTED_ID;
			}
			Data_store_layer_attr layerAttr = new Data_store_layer_attr();
			layerAttr.setDsla_id(BASE_LAYER_ARR_ID + i);
			layerAttr.setDsl_id(dslId);
			layerAttr.setStorage_property_key(propertyKey);
			layerAttr.setStorage_property_val(propertyVal);

			layerAttrs.add(layerAttr);
		}

		//24、构造data_store_layer_added表测试数据
		List<Data_store_layer_added> layerAddeds = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			long dslId;
			String dslaStorelayer;
			switch (i) {
				case 0:
					dslId = 4400L;
					dslaStorelayer = StoreLayerAdded.ZhuJian.getCode();
					break;
				case 1:
					dslId = 4399L;
					dslaStorelayer = StoreLayerAdded.SuoYinLie.getCode();
					break;
				case 2:
					dslId = 4402L;
					dslaStorelayer = StoreLayerAdded.RowKey.getCode();
					break;
				default:
					dslId = UNEXPECTED_ID;
					dslaStorelayer = "unexpected_dslaStorelayer";
			}
			Data_store_layer_added added = new Data_store_layer_added();
			added.setDslad_id(DATA_STORE_LAYER_ADDED_ID + i);
			added.setDsla_storelayer(dslaStorelayer);
			added.setDsl_id(dslId);

			layerAddeds.add(added);
		}

		//25、构造column_storage_info表测试数据
		List<Dcol_relation_store> columnStorageInfos = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			long dsladId;
			long columnId;
			switch (i) {
				case 0:
					dsladId = 439999L;
					columnId = 3112L;
					break;
				case 1:
					dsladId = 440001L;
					columnId = 3112L;
					break;
				case 2:
					dsladId = 440000L;
					columnId = 5112L;
					break;
				default:
					dsladId = UNEXPECTED_ID;
					columnId = UNEXPECTED_ID;
			}
			Dcol_relation_store storageInfo = new Dcol_relation_store();
			storageInfo.setCol_id(columnId);
			storageInfo.setDslad_id(dsladId);

			columnStorageInfos.add(storageInfo);
		}

		//26、构造data_relation_table表测试数据
		List<Dtab_relation_store> relationTables = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			long storageId;
			long dslId;
			switch (i) {
				case 0:
					storageId = 10669588L;
					dslId = 4400L;
					break;
				case 1:
					storageId = 10669588L;
					dslId = 4402L;
					break;
				case 2:
					storageId = 10669589L;
					dslId = 4399L;
					break;
				default:
					storageId = UNEXPECTED_ID;
					dslId = UNEXPECTED_ID;
			}
			Dtab_relation_store relationTable = new Dtab_relation_store();
			relationTable.setTab_id(storageId);
			relationTable.setDsl_id(dslId);

			relationTables.add(relationTable);
		}

		//插入数据
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
			for (Agent_info agentInfo : agents) {
				int count = agentInfo.add(db);
				agentInfoCount += count;
			}
			assertThat("Agent测试数据初始化", agentInfoCount, is(2));

			//插入database_set表测试数据
			int databaseSetCount = 0;
			for (Database_set databaseSet : databases) {
				int count = databaseSet.add(db);
				databaseSetCount += count;
			}
			assertThat("数据库设置测试数据初始化", databaseSetCount, is(2));

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
			assertThat("sys_user表对应字段表测试数据初始化", sysUserCount, is(11));
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

			//插入table_clean测试数据
			int tableCleanCount = 0;
			for (Table_clean tableClean : tableCleans) {
				int count = tableClean.add(db);
				tableCleanCount += count;
			}
			assertThat("表清洗参数信息表测试数据初始化", tableCleanCount, is(2));

			//插入column_clean测试数据
			int columnCleanCount = 0;
			for (Column_clean colComple : colComples) {
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
			int cvcCount = codeValue.add(db);
			columnCleanCount += cvcCount;
			assertThat("列清洗参数信息表测试数据初始化", columnCleanCount, is(7));

			//插入clean_parameter表测试数据
			int cleanParameterCount = 0;
			for (Clean_parameter cleanParameter : cleanParameters) {
				int count = cleanParameter.add(db);
				cleanParameterCount += count;
			}
			assertThat("清洗作业参数属性表测试数据初始化", cleanParameterCount, is(2));

			//插入column_spilt表测试数据
			int columnSpiltCount = 0;
			for (Column_split columnSplit : offsetSpilts) {
				int count = columnSplit.add(db);
				columnSpiltCount += count;
			}
			for (Column_split columnSplit : underLintSpilts) {
				int count = columnSplit.add(db);
				columnSpiltCount += count;
			}
			assertThat("列拆分信息表测试数据初始化", columnSpiltCount, is(5));

			//将列拆分信息加入Table_column表
			int spiltColumnCount = 0;
			for (Table_column tableColumn : splitOne) {
				int count = tableColumn.add(db);
				spiltColumnCount += count;
			}
			for (Table_column tableColumn : splitTwo) {
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

			//插入orig_syso_info表测试数据
			int origSysoInfoCount = 0;
			for (Orig_syso_info origSysoInfo : origSysoInfos) {
				int count = origSysoInfo.add(db);
				origSysoInfoCount += count;
			}
			assertThat("插入orig_syso_info表测试数据成功", origSysoInfoCount, is(3));

			//插入orig_code_info表测试数据
			int origCodeInfoCount = 0;
			for (Orig_code_info origCodeInfo : origCodeInfos) {
				int count = origCodeInfo.add(db);
				origCodeInfoCount += count;
			}
			assertThat("插入orig_code_info表测试数据成功", origCodeInfoCount, is(3));

			//插入data_extraction_def表数据
			int extractionDefCount = 0;
			for (Data_extraction_def def : extractionDefs) {
				int count = def.add(db);
				extractionDefCount += count;
			}
			assertThat("插入data_extraction_def表测试数据成功", extractionDefCount, is(4));

			//插入table_storage_info表测试数据
			int tsiCount = 0;
			for (Table_storage_info storageInfo : tableStorageInfos) {
				int count = storageInfo.add(db);
				tsiCount += count;
			}
			assertThat("插入table_storage_info表测试数据成功", tsiCount, is(2));
			//插入column_storage_info表测试数据
			int csiCount = 0;
			for (Dcol_relation_store storageInfo : columnStorageInfos) {
				int count = storageInfo.add(db);
				csiCount += count;
			}
			assertThat("插入column_storage_info表测试数据成功", csiCount, is(3));
			//插入data_store_layer表测试数据
			int dslCount = 0;
			for (Data_store_layer layer : storeLayers) {
				int count = layer.add(db);
				dslCount += count;
			}
			assertThat("插入data_store_layer表测试数据成功", dslCount, is(5));
			//插入data_store_layer_attr表测试数据
			int dslaCount = 0;
			for (Data_store_layer_attr layerAttr : layerAttrs) {
				int count = layerAttr.add(db);
				dslaCount += count;
			}
			assertThat("插入data_store_layer_attr表测试数据成功", dslaCount, is(11));
			//插入data_relation_table表测试数据
			int drtCount = 0;
			for (Dtab_relation_store relationTable : relationTables) {
				int count = relationTable.add(db);
				drtCount += count;
			}
			assertThat("插入data_relation_table表测试数据成功", drtCount, is(3));
			//插入data_store_layer_added表测试数据
			int dsladCount = 0;
			for (Data_store_layer_added added : layerAddeds) {
				int count = added.add(db);
				dsladCount += count;
			}
			assertThat("插入data_store_layer_added表测试数据成功", dsladCount, is(3));

			SqlOperator.commitTransaction(db);
		}
	}

	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1、删除用户表(sys_user)测试数据
			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//2、删除部门表(department_info)测试数据
			SqlOperator.execute(db, "delete from " + Department_info.TableName + " WHERE dep_id = ?", TEST_DEPT_ID);
			//3、删除数据源表(data_source)测试数据
			SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID);
			//4、删除Agent信息表(agent_info)测试数据
			SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//5、删除database_set表测试数据
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", FIRST_DB_AGENT_ID);
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", SECOND_DB_AGENT_ID);
			//6、删除collect_job_classify表测试数据
			SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//7、删除table_info表测试数据
			SqlOperator.execute(db, "delete from " + Table_info.TableName + " where database_id = ? ", FIRST_DATABASESET_ID);
			//8、删除table_column表测试数据
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", AGENT_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", DATA_SOURCE_TABLE_ID);
			//9、删除table_clean表测试数据
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			//10、删除column_clean表测试数据
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2002L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2003L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2005L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2011L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 3004L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 3003L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? ", 2010L);
			//11、删除clean_parameter表测试数据
			SqlOperator
				.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? ", FIRST_DATABASESET_ID);
			//12、删除column_spilt表测试数据
			SqlOperator.execute(db, "delete from " + Column_split.TableName + " where column_id = ? ", 3004L);
			SqlOperator.execute(db, "delete from " + Column_split.TableName + " where column_id = ? ", 3003L);
			//13、删除column_merge表测试数据
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			//14、删除orig_syso_info表数据
			SqlOperator
				.execute(db, "delete from " + Orig_syso_info.TableName + " where Orig_sys_code = ? ", "origSysCode_one");
			SqlOperator
				.execute(db, "delete from " + Orig_syso_info.TableName + " where Orig_sys_code = ? ", "origSysCode_two");
			SqlOperator
				.execute(db, "delete from " + Orig_syso_info.TableName + " where Orig_sys_code = ? ", "origSysCode_three");
			//15、删除orig_code_info表数据
			SqlOperator.execute(db, "delete from " + Orig_code_info.TableName + " where orig_id = ? ", 6001L);
			SqlOperator.execute(db, "delete from " + Orig_code_info.TableName + " where orig_id = ? ", 6002L);
			SqlOperator.execute(db, "delete from " + Orig_code_info.TableName + " where orig_id = ? ", 6003L);
			//16、删除data_extraction_def表数据
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ", SYS_USER_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ", CODE_INFO_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ", AGENT_INFO_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ", DATA_SOURCE_TABLE_ID);
			//17、删除table_storage_info表数据
			SqlOperator
				.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ", AGENT_INFO_TABLE_ID);
			SqlOperator
				.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ", DATA_SOURCE_TABLE_ID);
			//18、删除column_storage_info表数据
			SqlOperator.execute(db, "delete from " + Dcol_relation_store.TableName + " where column_id = ? ", 3112L);
			SqlOperator.execute(db, "delete from " + Dcol_relation_store.TableName + " where column_id = ? ", 5112L);
			//19、删除data_store_layer表数据
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ? ", 4399L);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ? ", 4400L);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ? ", 4401L);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ? ", 4402L);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ? ", 4403L);
			//20、删除data_store_layer_attr表数据
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id = ? ", 4399L);
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id = ? ", 4400L);
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id = ? ", 4402L);
			//21、删除data_relation_table表数据
			SqlOperator.execute(db, "delete from " + Dtab_relation_store.TableName + " where dsl_id = ? ", 4402L);
			SqlOperator.execute(db, "delete from " + Dtab_relation_store.TableName + " where dsl_id = ? ", 4399L);
			SqlOperator.execute(db, "delete from " + Dtab_relation_store.TableName + " where dsl_id = ? ", 4400L);
			//22、删除data_store_layer_added表数据
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id = ? ", 4402L);
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id = ? ", 4399L);
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id = ? ", 4400L);
			//23、提交事务后
			SqlOperator.commitTransaction(db);
		}
	}
}
