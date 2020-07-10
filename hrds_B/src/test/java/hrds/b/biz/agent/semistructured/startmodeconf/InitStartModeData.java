package hrds.b.biz.agent.semistructured.startmodeconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "半结构化启动方式数据初始化", author = "dhw", createdate = "2020/7/10 9:14")
public class InitStartModeData {

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
	public final String ETL_SYS_CD = "dhwcs" + threadId;
	//作业工程名称
	public final String SUB_SYS_CD = "dhwcs" + threadId;
	//作业程序路径
	public final String AGENT_PATH = "/home/hyshf/";
	//作业日志文件
	public final String AGENT_LOG = "/home/hyshf/log/";
	public final String TEST_NAME = "dhwcs" + threadId;
	public final String AGENT_NAME = "测试Agent" + threadId;
	//对象采集设置表id
	public final long ODC_ID = PrimayKeyGener.getNextId();
	// 对象采集任务编号
	public final long OCS_ID = PrimayKeyGener.getNextId();
	// 数据字典目录
	public final String filepath = FileUtil.getFile(
			"src/test/java/hrds/b/biz/agent/semistructured/dictionary").getAbsolutePath();
	// agent所在机器的操作系统linux|windows

	public void initData() {

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//数据源ID
			Data_source data_source = new Data_source();
			data_source.setSource_id(SOURCE_ID);
			data_source.setDatasource_number(TEST_NAME);
			data_source.setDatasource_name(TEST_NAME);
			data_source.setCreate_date(DateUtil.getSysDate());
			data_source.setCreate_time(DateUtil.getSysTime());
			data_source.setCreate_user_id(WebBaseTestCase.agentInitConfig.getString("user_id", "2001"));
			data_source.add(db);
			//Agent信息
			Agent_info agent_info = new Agent_info();
			agent_info.setAgent_id(AGENT_ID);
			agent_info.setAgent_name(AGENT_NAME);
			agent_info.setAgent_type(AgentType.DuiXiang.getCode());
			agent_info.setAgent_ip(WebBaseTestCase.agentInitConfig.getString("agent_ip", "127.0.0.1"));
			agent_info.setAgent_port(WebBaseTestCase.agentInitConfig.getString("agent_port", "55555"));
			agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
			agent_info.setCreate_date(DateUtil.getSysDate());
			agent_info.setCreate_time(DateUtil.getSysTime());
			agent_info.setUser_id(WebBaseTestCase.agentInitConfig.getString("user_id", "2001"));
			agent_info.setSource_id(SOURCE_ID);
			agent_info.add(db);
			//Agent下载地址
			Agent_down_info down_info = new Agent_down_info();
			down_info.setDown_id(AGENT_DOWN_ID);
			down_info.setAgent_name(AGENT_NAME);
			down_info.setAgent_ip(WebBaseTestCase.agentInitConfig.getString("agent_ip", "127.0.0.1"));
			down_info.setAgent_port(WebBaseTestCase.agentInitConfig.getString("agent_port", "55555"));
			down_info.setUser_name(TEST_NAME);
			down_info.setPasswd(TEST_NAME);
			down_info.setSave_dir(AGENT_PATH);
			down_info.setLog_dir(AGENT_LOG);
			down_info.setDeploy(IsFlag.Fou.getCode());
			down_info.setAgent_context("/agent");
			down_info.setAgent_pattern("/receive/*");
			down_info.setAgent_type(AgentType.ShuJuKu.getCode());
			down_info.setAgent_id(AGENT_ID);
			down_info.setAi_desc(AGENT_PATH);
			down_info.setUser_id(WebBaseTestCase.agentInitConfig.getString("user_id", "2001"));
			down_info.add(db);
			//分类信息
			Collect_job_classify classify = new Collect_job_classify();
			classify.setClassify_id(CLASSIFY_ID);
			classify.setClassify_num(TEST_NAME);
			classify.setClassify_name("测试" + threadId);
			classify.setUser_id(WebBaseTestCase.agentInitConfig.getString("user_id", "2001"));
			classify.setAgent_id(AGENT_ID);
			classify.add(db);
			//初始化采集任务信息
			Database_set database_set = new Database_set();
			database_set.setDatabase_id(DATABASE_ID);
			database_set.setAgent_id(AGENT_ID);
			database_set.setDatabase_number(TEST_NAME);
			database_set.setDatabase_number(TEST_NAME);
			database_set.setDb_agent(IsFlag.Shi.getCode());
			database_set.setIs_sendok(IsFlag.Shi.getCode());
			database_set.setClassify_id(CLASSIFY_ID);
			database_set.add(db);
			// 初始化作业工程信息数据
			Etl_sys etl_sys = new Etl_sys();
			etl_sys.setEtl_sys_cd(ETL_SYS_CD);
			etl_sys.setEtl_sys_name(TEST_NAME);
			etl_sys.setUser_id(WebBaseTestCase.agentInitConfig.getString("user_id", "2001"));
			etl_sys.add(db);
			//初始化作业调度的任务数据
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			etl_sub_sys_list.setEtl_sys_cd(ETL_SYS_CD);
			etl_sub_sys_list.setSub_sys_cd(SUB_SYS_CD);
			etl_sub_sys_list.add(db);
			// 造Object_collect表数据
			Object_collect object_collect = new Object_collect();
			object_collect.setOdc_id(ODC_ID);
			object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
			object_collect.setObj_number(TEST_NAME);
			object_collect.setObj_collect_name(TEST_NAME);
			object_collect.setSystem_name(WebBaseTestCase.agentInitConfig.getString("agent_os_name"));
			object_collect.setHost_name("mine");
			object_collect.setLocal_time(DateUtil.getDateTime());
			object_collect.setServer_date(DateUtil.getSysDate());
			object_collect.setS_date(DateUtil.getSysDate());
			object_collect.setE_date(Constant.MAXDATE);
			object_collect.setDatabase_code(DataBaseCode.UTF_8.getCode());
			object_collect.setFile_path(filepath);
			object_collect.setIs_sendok(IsFlag.Fou.getCode());
			object_collect.setAgent_id(AGENT_ID);
			object_collect.setIs_dictionary(IsFlag.Shi.getCode());
			object_collect.setIs_dictionary(IsFlag.Shi.getCode());
			object_collect.setFile_path(filepath);
			object_collect.setData_date("");
			object_collect.setFile_suffix("json");
			assertThat(Object_collect.TableName + "表初始化测试数据成功", object_collect.add(db), is(1));
			// 造object_collect_task表测试数据
			List<Object_collect_task> objectCollectTaskList = getObject_collect_tasks();
			objectCollectTaskList.forEach(object_collect_task ->
					assertThat(Object_collect_task.TableName + "表初始化测试数据成功",
							object_collect_task.add(db), is(1))
			);
			// 造表测试数据
			List<Obj_relation_etl> objRelationEtlList = getObj_relation_etls(objectCollectTaskList);
			objRelationEtlList.forEach(obj_relation_etl ->
					assertThat(Obj_relation_etl.TableName + "表初始化数据成功", obj_relation_etl.add(db), is(1))
			);
			for (int i = 0; i < objRelationEtlList.size(); i++) {
				Obj_relation_etl obj_relation_etl = objRelationEtlList.get(i);
				Etl_job_def etl_job_def = new Etl_job_def();
				etl_job_def.setEtl_sys_cd(obj_relation_etl.getEtl_sys_cd());
				etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_def.setPro_name(Constant.SHELLCOMMAND);
				etl_job_def.setEtl_job(obj_relation_etl.getEtl_job());
				etl_job_def.setEtl_sys_cd(obj_relation_etl.getEtl_sys_cd());
				etl_job_def.setSub_sys_cd(obj_relation_etl.getSub_sys_cd());
				etl_job_def.setPro_dic(AGENT_PATH);
				etl_job_def.setLog_dic(AGENT_LOG);
				if (i == 0) {
					etl_job_def.setEtl_job_desc(TEST_NAME + "_" + AGENT_NAME
							+ "_" + TEST_NAME + "_" + "t_executedpersons");
				} else {
					etl_job_def.setEtl_job_desc(TEST_NAME + "_" + AGENT_NAME
							+ "_" + TEST_NAME + "_" + "t_executedpersons2");
				}
				etl_job_def.setPro_para("1");
				etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				etl_job_def.setToday_disp(Today_Dispatch_Flag.YES.getCode());
				etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_def.add(db);
			}
			Etl_dependency etl_dependency = new Etl_dependency();
			etl_dependency.setEtl_sys_cd(ETL_SYS_CD);
			etl_dependency.setPre_etl_sys_cd(ETL_SYS_CD);
			etl_dependency.setEtl_job(TEST_NAME + "_" + AGENT_ID
					+ "_" + TEST_NAME + "_" + "t_executedpersons");
			etl_dependency.setPre_etl_job(TEST_NAME + "_" + AGENT_ID
					+ "_" + TEST_NAME + "_" + "t_executedpersons2");
			etl_dependency.setStatus(Status.TRUE.getCode());
			etl_dependency.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etl_dependency.add(db);
			// 提交事务
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Obj_relation_etl> getObj_relation_etls(List<Object_collect_task> objectCollectTaskList) {
		List<Obj_relation_etl> objRelationEtlList = new ArrayList<>();
		for (Object_collect_task object_collect_task : objectCollectTaskList) {
			Obj_relation_etl obj_relation_etl = new Obj_relation_etl();
			obj_relation_etl.setSub_sys_cd(SUB_SYS_CD);
			obj_relation_etl.setEtl_sys_cd(ETL_SYS_CD);
			obj_relation_etl.setEtl_job(TEST_NAME + "_" + AGENT_ID + "_" + TEST_NAME +
					"_" + object_collect_task.getEn_name());
			obj_relation_etl.setOcs_id(object_collect_task.getOcs_id());
			obj_relation_etl.setOdc_id(ODC_ID);
			objRelationEtlList.add(obj_relation_etl);
		}
		return objRelationEtlList;
	}

	public List<Object_collect_task> getObject_collect_tasks() {
		List<Object_collect_task> objectCollectTaskList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(OCS_ID + i);
			object_collect_task.setDatabase_code(DataBaseCode.UTF_8.getCode());
			object_collect_task.setCollect_data_type(CollectDataType.JSON.getCode());
			object_collect_task.setUpdatetype(UpdateType.DirectUpdate.getCode());
			object_collect_task.setOdc_id(ODC_ID);
			if (i == 0) {
				object_collect_task.setZh_name("t_executedpersons");
				object_collect_task.setEn_name("t_executedpersons");
			} else {
				object_collect_task.setZh_name("t_executedpersons2");
				object_collect_task.setEn_name("t_executedpersons2");
			}
			object_collect_task.setFirstline("");
			object_collect_task.setAgent_id(AGENT_ID);
			objectCollectTaskList.add(object_collect_task);
		}
		return objectCollectTaskList;
	}

	public void deleteInitData() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			SqlOperator.execute(db, "DELETE FROM " + Data_source.TableName + " WHERE source_id = ?", SOURCE_ID);
			SqlOperator.execute(db, "DELETE FROM " + Agent_info.TableName + " WHERE agent_id = ?", AGENT_ID);
			SqlOperator.execute(db, "DELETE FROM " + Agent_down_info.TableName + " WHERE down_id = ?", AGENT_DOWN_ID);
			SqlOperator.execute(db, "DELETE FROM " + Collect_job_classify.TableName + " WHERE classify_id = ?", CLASSIFY_ID);
			SqlOperator.execute(db, "DELETE FROM " + Database_set.TableName + " WHERE database_id = ?", DATABASE_ID);
			SqlOperator.execute(db, "DELETE FROM " + Obj_relation_etl.TableName + " WHERE ocs_id in ("
					+ "SELECT ocs_id FROM " + Object_collect_task.TableName + " WHERE odc_id = ?)", ODC_ID
			);
			SqlOperator.execute(db, "DELETE FROM " + Object_collect.TableName + " WHERE odc_id =?",
					ODC_ID);
			SqlOperator.execute(db, "DELETE FROM " + Object_collect_task.TableName + " WHERE odc_id =?",
					ODC_ID);
			SqlOperator.execute(db, "DELETE FROM " + Etl_sys.TableName + " WHERE etl_sys_cd = ?", ETL_SYS_CD);
			SqlOperator.execute(db, "DELETE FROM " + Etl_sub_sys_list.TableName + " WHERE etl_sys_cd = ? AND sub_sys_cd = ?",
					ETL_SYS_CD, SUB_SYS_CD);
			SqlOperator
					.execute(db, "DELETE FROM " + Etl_job_def.TableName + " WHERE etl_sys_cd = ? AND sub_sys_cd = ?", ETL_SYS_CD,
							SUB_SYS_CD);
			SqlOperator.execute(db, "DELETE FROM " + Etl_dependency.TableName + " WHERE etl_sys_cd = ?", ETL_SYS_CD);
			SqlOperator
					.execute(db, "DELETE FROM " + Take_relation_etl.TableName
									+ " WHERE etl_sys_cd = ? AND sub_sys_cd = ? AND database_id = ?", ETL_SYS_CD,
							SUB_SYS_CD, DATABASE_ID);
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			throw e;
		}
	}

}
