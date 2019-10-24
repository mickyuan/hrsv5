package hrds.b.biz.agent.dbagentconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "为数据库直连采集测试用例构造公共的测试数据，仅本功能自用", author = "WangZhengcheng")
public class InitBaseData {

	//测试数据用户ID
	private static final long TEST_USER_ID = -9997L;
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

		return databases;
	}

	public static List<Collect_job_classify> buildClassifyData(){
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
			codeInfoColumn.setIs_new(IsFlag.Shi.getCode());
			codeInfoColumn.setTc_or(initColumnCleanOrder().toJSONString());

			codeInfos.add(codeInfoColumn);
		}

		return codeInfos;
	}
}
