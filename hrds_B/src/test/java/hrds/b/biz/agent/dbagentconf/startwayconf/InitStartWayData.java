package hrds.b.biz.agent.dbagentconf.startwayconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.utils.Constant;
import hrds.commons.utils.ParallerTestUtil;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "", author = "Mr.Lee", createdate = "2020-06-23 17:05")
public class InitStartWayData {

	//数据源ID
	public final long SOURCE_ID = PrimayKeyGener.getNextId();
	//任务ID
	public final long DATABASE_ID = PrimayKeyGener.getNextId();
	//Agent ID
	public final long AGENT_ID = PrimayKeyGener.getNextId();
	//Agent ID
	public final long AGENT_DOWN_ID = PrimayKeyGener.getNextId();
	//分类 ID
	public final long CLASSIFY_ID = PrimayKeyGener.getNextId();
	//当前进程的ID
	public final long threadId = Thread.currentThread().getId();
	//作业工程名称
	public final String ETL_SYS_CD = "lqcs_" + threadId;
	//作业工程名称
	public final String SUB_SYS_CD = "lqcs_" + threadId;
	//作业程序路径
	public final String AGENT_PATH = "/home/hyshf";
	//作业日志文件
	public final String AGENT_LOG = "/home/hyshf/log.text";
	//卸数文件的ID集合
	public final List<Long> DED_ID = new ArrayList<>();
	//卸数文件的ID集合
	public final List<Long> TABLE_ID_LIST = new ArrayList<>();
	//用户id
	public final String USER_ID = ParallerTestUtil.TESTINITCONFIG.getString("user_id", "2001");

	public void initData() {

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//数据源ID
			Data_source data_source = new Data_source();
			data_source.setSource_id(SOURCE_ID);
			data_source.setDatasource_number("lqcs_" + threadId);
			data_source.setDatasource_name("lqcs_" + threadId);
			data_source.setCreate_date(DateUtil.getSysDate());
			data_source.setCreate_time(DateUtil.getSysTime());
			data_source.setCreate_user_id(USER_ID);
			data_source.add(db);
			//Agent信息
			Agent_info agent_info = new Agent_info();
			agent_info.setAgent_id(AGENT_ID);
			agent_info.setAgent_name("lqcs_agent" + threadId);
			agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
			agent_info.setAgent_ip(ParallerTestUtil.TESTINITCONFIG.getString("agent_ip", "127.0.0.1"));
			agent_info.setAgent_port(ParallerTestUtil.TESTINITCONFIG.getString("agent_port", "55555"));
			agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
			agent_info.setCreate_date(DateUtil.getSysDate());
			agent_info.setCreate_time(DateUtil.getSysTime());
			agent_info.setUser_id(USER_ID);
			agent_info.setSource_id(SOURCE_ID);
			agent_info.add(db);
			//Agent下载地址
			Agent_down_info down_info = new Agent_down_info();
			down_info.setDown_id(AGENT_DOWN_ID);
			down_info.setAgent_name("测试Agent" + threadId);
			down_info.setAgent_ip(ParallerTestUtil.TESTINITCONFIG.getString("agent_ip", "127.0.0.1"));
			down_info.setAgent_port(ParallerTestUtil.TESTINITCONFIG.getString("agent_port", "55555"));
			down_info.setUser_name("lqcs_" + threadId);
			down_info.setPasswd("lqcs_" + threadId);
			down_info.setSave_dir(AGENT_PATH);
			down_info.setLog_dir(AGENT_LOG);
			down_info.setDeploy(IsFlag.Fou.getCode());
			down_info.setAgent_context("/agent");
			down_info.setAgent_pattern("/recive/*");
			down_info.setAgent_type(AgentType.ShuJuKu.getCode());
			down_info.setAgent_id(AGENT_ID);
			down_info.setAi_desc(AGENT_PATH);
			down_info.setUser_id(USER_ID);
			down_info.add(db);
			//分类信息
			Collect_job_classify classify = new Collect_job_classify();
			classify.setClassify_id(CLASSIFY_ID);
			classify.setClassify_num("lqcs" + threadId);
			classify.setClassify_name("测试" + threadId);
			classify.setUser_id(USER_ID);
			classify.setAgent_id(AGENT_ID);
			classify.add(db);
			//初始化采集任务信息
			Database_set database_set = new Database_set();
			database_set.setDatabase_id(DATABASE_ID);
			database_set.setAgent_id(AGENT_ID);
			database_set.setDatabase_number("lqcs_" + threadId);
			database_set.setDatabase_number("lqcs_" + threadId);
			database_set.setDb_agent(IsFlag.Shi.getCode());
			database_set.setIs_sendok(IsFlag.Shi.getCode());
			database_set.setClassify_id(CLASSIFY_ID);
			database_set.setCollect_type(CollectType.ShuJuKuCaiJi.getCode());
			database_set.add(db);
			//初始化表的数据信息
			for (int i = 0; i < 5; i++) {
				Table_info table_info = new Table_info();
				Long table_id = PrimayKeyGener.getNextId();
				table_info.setTable_id(table_id);
				table_info.setTable_name("lqcs_table_" + i + threadId);
				table_info.setTable_ch_name("测试表_" + i);
				table_info.setRec_num_date(DateUtil.getSysDate());
				table_info.setValid_s_date(DateUtil.getSysDate());
				table_info.setValid_e_date(Constant.MAXDATE);
				table_info.setIs_md5(IsFlag.Shi.getCode());
				table_info.setIs_register(IsFlag.Shi.getCode());
				table_info.setIs_customize_sql(IsFlag.Fou.getCode());
				table_info.setIs_parallel(IsFlag.Shi.getCode());
				table_info.setIs_user_defined(IsFlag.Fou.getCode());
				table_info.setDatabase_id(DATABASE_ID);
				table_info.add(db);
				TABLE_ID_LIST.add(table_id);
			}
			//初始化表数据抽取定义 这里的一张表对应的可以是N种卸数文件方式
			TABLE_ID_LIST.forEach(table_id -> {
				for (int i = 0; i < 6; i++) {
					Data_extraction_def def = new Data_extraction_def();
					Long ded_id = PrimayKeyGener.getNextId();
					DED_ID.add(ded_id);
					def.setDed_id(ded_id);
					def.setTable_id(table_id);
					def.setData_extract_type(DataExtractType.YuanShuJuGeShi.getCode());
					def.setIs_header(IsFlag.Shi.getCode());
					def.setDatabase_code(DataBaseCode.UTF_8.getCode());
					def.setRow_separator(StringUtil.string2Unicode("\\n"));
					def.setDatabase_separatorr(StringUtil.string2Unicode("$"));
					if (i == 0) {
						def.setDbfile_format(FileFormat.DingChang.getCode());
						def.setFile_suffix("");
					} else if (i == 1) {
						def.setDbfile_format(FileFormat.FeiDingChang.getCode());
						def.setFile_suffix("");
					} else if (i == 2) {
						def.setDbfile_format(FileFormat.CSV.getCode());
						def.setFile_suffix("csv");
					} else if (i == 3) {
						def.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
						def.setFile_suffix("seq");
					} else if (i == 4) {
						def.setDbfile_format(FileFormat.PARQUET.getCode());
						def.setFile_suffix("par");
					} else {
						def.setDbfile_format(FileFormat.ORC.getCode());
						def.setFile_suffix("orc");
					}
					def.setPlane_url("/home/hyshf");

					def.add(db);
				}
			});
			// 初始化作业工程信息数据
			Etl_sys etl_sys = new Etl_sys();
			etl_sys.setEtl_sys_cd(ETL_SYS_CD);
			etl_sys.setEtl_sys_name("lqcs" + threadId);
			etl_sys.setUser_id(USER_ID);
			etl_sys.add(db);
			//初始化作业调度的任务数据
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			etl_sub_sys_list.setEtl_sys_cd(ETL_SYS_CD);
			etl_sub_sys_list.setSub_sys_cd(SUB_SYS_CD);
			etl_sub_sys_list.add(db);
			SqlOperator.commitTransaction(db);
		}
	}

	public void deleteInitData() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			SqlOperator.execute(db,
					"DELETE FROM " + Data_source.TableName + " WHERE source_id = ?", SOURCE_ID);
			SqlOperator.execute(db,
					"DELETE FROM " + Agent_info.TableName + " WHERE agent_id = ?", AGENT_ID);
			SqlOperator.execute(db,
					"DELETE FROM " + Agent_down_info.TableName + " WHERE down_id = ?", AGENT_DOWN_ID);
			SqlOperator.execute(db,
					"DELETE FROM " + Collect_job_classify.TableName + " WHERE classify_id = ?", CLASSIFY_ID);
			SqlOperator.execute(db,
					"DELETE FROM " + Database_set.TableName + " WHERE database_id = ?", DATABASE_ID);
			SqlOperator.execute(db,
					"DELETE FROM " + Data_extraction_def.TableName + " WHERE table_id in (SELECT table_id" +
							" FROM " + Table_info.TableName + " WHERE database_id = ?)", DATABASE_ID);
			SqlOperator.execute(db,
					"DELETE FROM " + Table_info.TableName + " WHERE database_id = ?", DATABASE_ID);
			SqlOperator.execute(db,
					"DELETE FROM " + Etl_sys.TableName + " WHERE etl_sys_cd = ?", ETL_SYS_CD);
			SqlOperator.execute(db,
					"DELETE FROM " + Etl_sub_sys_list.TableName + " WHERE etl_sys_cd = ? AND sub_sys_cd = ?",
					ETL_SYS_CD, SUB_SYS_CD);
			SqlOperator.execute(db,
					"DELETE FROM " + Etl_job_def.TableName + " WHERE etl_sys_cd = ? AND sub_sys_cd = ?",
					ETL_SYS_CD, SUB_SYS_CD);
			SqlOperator.execute(db, "DELETE FROM " + Take_relation_etl.TableName +
							" WHERE etl_sys_cd = ? AND sub_sys_cd = ? AND database_id = ?",
					ETL_SYS_CD, SUB_SYS_CD, DATABASE_ID);
			SqlOperator.commitTransaction(db);
		}
	}

}
