package hrds.b.biz.agent.semistructured.startmodeconf;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.bean.JobStartConf;
import hrds.commons.codes.*;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Obj_relation_etl;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.BeanUtils;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "半结构化采集定义启动方式配置类", author = "dhw", createdate = "2020/7/9 15:39")
public class StartModeConfActionTest extends WebBaseTestCase {

	private final InitStartModeData initStartModeData = new InitStartModeData();

	@Before
	public void before() {
		initStartModeData.initData();
		// 模拟登陆
		ActionResult actionResult = login();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	@After
	public void after() {
		initStartModeData.deleteInitData();
	}


	@Method(desc = "获取半结构化采集作业配置", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc不存在")
	@Test
	public void getEtlJobConfInfoFromObj() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initStartModeData.ODC_ID)
				.post(getActionUrl("getEtlJobConfInfoFromObj"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map> mapList = ar.getDataForEntityList(Map.class);
		List<String> etlJobList = new ArrayList<>();
		List<String> etlJobDescList = new ArrayList<>();
		String etl_job = initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_ID
				+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons";
		String etl_job2 = initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_ID
				+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons2";
		for (Map map : mapList) {
			etlJobList.add(map.get("etl_job").toString());
			etlJobDescList.add(map.get("etl_job_desc").toString());
			assertThat(map.get("etl_sys_cd").toString(), is(initStartModeData.ETL_SYS_CD));
			assertThat(map.get("sub_sys_cd").toString(), is(initStartModeData.SUB_SYS_CD));
			assertThat(map.get("disp_freq").toString(), is(Dispatch_Frequency.DAILY.getCode()));
			assertThat(map.get("pro_name").toString(), is(Constant.SHELLCOMMAND));
			assertThat(map.get("pro_dic").toString(), is(initStartModeData.AGENT_PATH));
			assertThat(map.get("log_dic").toString(), is(initStartModeData.AGENT_LOG));
			assertThat(map.get("pro_type").toString(), is(Pro_Type.SHELL.getCode()));
			if (map.get("etl_job").toString().equals(etl_job)) {
				List<String> preJobList = JsonUtil.toObject(map.get("pre_etl_job").toString(),
						new TypeReference<List<String>>() {
						}.getType());
				assertThat(preJobList.contains(etl_job2), is(true));
			}
		}
		assertThat(etlJobList.contains(etl_job), is(true));
		assertThat(etlJobList.contains(etl_job2), is(true));
		assertThat(etlJobDescList.contains(initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_NAME
						+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons"),
				is(true));
		assertThat(etlJobDescList.contains(initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_NAME
						+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons2"),
				is(true));
		// 2.错误的数据访问1，odc不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.post(getActionUrl("getEtlJobConfInfoFromObj"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "获取当前半结构化采集任务下的作业信息", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc不存在")
	@Test
	public void getPreviewJob() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initStartModeData.ODC_ID)
				.post(getActionUrl("getPreviewJob"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map> mapList = ar.getDataForEntityList(Map.class);
		List<String> etlJobList = new ArrayList<>();
		List<String> etlJobDescList = new ArrayList<>();
		for (Map map : mapList) {
			etlJobList.add(map.get("etl_job").toString());
			etlJobDescList.add(map.get("etl_job_desc").toString());
		}
		assertThat(etlJobList.contains(initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_ID
						+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons"),
				is(true));
		assertThat(etlJobList.contains(initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_ID
						+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons2"),
				is(true));
		assertThat(etlJobDescList.contains(initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_NAME
						+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons"),
				is(true));
		assertThat(etlJobDescList.contains(initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_NAME
						+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons2"),
				is(true));
		// 2.错误的数据访问1，odc不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.post(getActionUrl("getPreviewJob"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "获取任务Agent的部署路径及日志目录", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc不存在")
	@Test
	public void getAgentPath() {
		// 1.正确的数据访问1，无数据字典
		String bodyString = new HttpClient()
				.addData("odc_id", initStartModeData.ODC_ID)
				.post(getActionUrl("getAgentPath"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> agentPathMap = ar.getDataForMap();
		assertThat(agentPathMap.get("log_dic"), is(initStartModeData.AGENT_LOG));
		assertThat(agentPathMap.get("pro_dic"), is(initStartModeData.AGENT_PATH));
		assertThat(agentPathMap.get("pro_name"), is(Constant.SHELLCOMMAND));
		assertThat(agentPathMap.get("etl_sys_cd"), is(initStartModeData.ETL_SYS_CD));
		assertThat(agentPathMap.get("sub_sys_cd"), is(initStartModeData.SUB_SYS_CD));
		assertThat(agentPathMap.get("pro_type"), is(Pro_Type.SHELL.getCode()));
		// 2.错误的数据访问1，odc不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.post(getActionUrl("getAgentPath"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "保存半结构化采集启动方式配置", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc不存在")
	@Test
	public void saveStartModeConfData() {
		List<Etl_job_def> etlJobDefList = new ArrayList<>();
		String etl_job = initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_ID
				+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons";
		String etl_job2 = initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_ID
				+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons2";
		for (int i = 0; i < 2; i++) {
			Etl_job_def etl_job_def = new Etl_job_def();
			etl_job_def.setEtl_sys_cd(initStartModeData.ETL_SYS_CD);
			etl_job_def.setSub_sys_cd(initStartModeData.SUB_SYS_CD);
			etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
			etl_job_def.setPro_name(Constant.SHELLCOMMAND);
			etl_job_def.setPro_dic(initStartModeData.AGENT_PATH);
			etl_job_def.setLog_dic(initStartModeData.AGENT_LOG);
			if (i == 0) {
				etl_job_def.setEtl_job(etl_job);
				etl_job_def.setEtl_job_desc(initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_NAME
						+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons");
			} else {
				etl_job_def.setEtl_job(etl_job2);
				etl_job_def.setEtl_job_desc(initStartModeData.TEST_NAME + "_" + initStartModeData.AGENT_NAME
						+ "_" + initStartModeData.TEST_NAME + "_" + "t_executedpersons2");
			}
			etl_job_def.setPro_para("1");
			etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etl_job_def.setToday_disp(Today_Dispatch_Flag.YES.getCode());
			etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
			etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDefList.add(etl_job_def);
		}
		List<Obj_relation_etl> obj_relation_etls =
				initStartModeData.getObj_relation_etls(initStartModeData.getObject_collect_tasks());
		List<JobStartConf> jobStartConfs = new ArrayList<>();
		for (Obj_relation_etl obj_relation_etl : obj_relation_etls) {
			JobStartConf jobStartConf = new JobStartConf();
			BeanUtils.copyProperties(obj_relation_etl, jobStartConf);
			jobStartConf.setLog_dic(initStartModeData.AGENT_LOG);
			jobStartConf.setPro_dic(initStartModeData.AGENT_PATH);
			jobStartConf.setPre_etl_job(new String[]{etl_job2});
			jobStartConfs.add(jobStartConf);
		}
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initStartModeData.ODC_ID)
				.addData("etlJobDefs", JsonUtil.toJson(etlJobDefList))
				.addData("jobStartConfs", JsonUtil.toJson(jobStartConfs))
				.post(getActionUrl("saveStartModeConfData"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Obj_relation_etl> objRelationEtlList = SqlOperator.queryList(db, Obj_relation_etl.class,
					"select * from " + Obj_relation_etl.TableName + " where odc_id=?",
					initStartModeData.ODC_ID);
			List<String> etlJobList = objRelationEtlList.stream().map(Obj_relation_etl::getEtl_job)
					.collect(Collectors.toList());
			assertThat(etlJobList.contains(etl_job), is(true));
			assertThat(etlJobList.contains(etl_job2), is(true));
			List<Etl_job_def> etl_job_defList = SqlOperator.queryList(db, Etl_job_def.class,
					"select * from " + Etl_job_def.TableName + " where etl_sys_cd=?",
					initStartModeData.ETL_SYS_CD);
			List<String> etlJobList2 = etl_job_defList.stream().map(Etl_job_def::getEtl_job)
					.collect(Collectors.toList());
			assertThat(etlJobList2.contains(etl_job), is(true));
			assertThat(etlJobList2.contains(etl_job2), is(true));
		}
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "111")
				.addData("etlJobDefs", JsonUtil.toJson(etlJobDefList))
				.addData("jobStartConfs", JsonUtil.toJson(jobStartConfs))
				.post(getActionUrl("saveStartModeConfData"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}
}