package hrds.b.biz.agent.dbagentconf.startwayconf;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.alibaba.fastjson.JSON;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.jdbc.SqlOperator.Assembler;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.Object;

@DocClass(desc = "定义启动方式Action测试类", author = "Mr.Lee")
public class StartWayConfActionTest extends WebBaseTestCase {

	private final InitStartWayData initStartWayData = new InitStartWayData();

	@Before
	public void before() {

		initStartWayData.initData();
		//		模拟登陆
		ActionResult actionResult = login();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	@After
	public void after() {
		initStartWayData.deleteInitData();
	}

	@Test
	public void getEtlSysData() {
		String getEtlSysData = new HttpClient().post(getActionUrl("getEtlSysData")).getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new BusinessException("连接异常"));
		//检查是否和初始化的数据结果集一致(这里如果是高并发的时候会出现大于一条的情况)
		assertThat(actionResult.getDataForEntityList(Result.class).size() >= 1, is(true));
	}

	@Test
	public void getEtlSubSysData() {
		String getEtlSysData = new HttpClient().addData("etl_sys_cd", initStartWayData.ETL_SYS_CD)
			.post(getActionUrl("getEtlSubSysData")).getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new BusinessException("连接异常"));
		//检查是否和初始化的数据结果集一致
		assertThat(actionResult.getDataForEntityList(Result.class).size(), is(1));
	}

	@Test
	public void getPreviewJob() {
		String getEtlSysData = new HttpClient().addData("colSetId", initStartWayData.DATABASE_ID)
			.post(getActionUrl("getPreviewJob")).getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new BusinessException("连接异常"));
		/*
		 *	检查是否和初始化的数据结果集一致,初始化的时候创建了5张表数据信息,每张表的初始卸数文件格式有6种.
		 * 	所以这里检查是否符合初始的表数据信息,也就是30条作业任务数据
		 */
		assertThat(actionResult.getDataForEntityList(Result.class).size(), is(30));

		//模拟错误的任务ID,将会请求报错
		getEtlSysData = new HttpClient().addData("colSetId", -123123)
			.post(getActionUrl("getPreviewJob")).getBodyString();
		actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new BusinessException("连接异常"));
		assertThat(actionResult.isSuccess(), is(false));
	}

	/**
	 * 请求采集作业的作业信息
	 * 	1: 请求正确的数据信息后,检查是否和初始化时的数据量是否一致
	 * 	2: 模拟一次任务信息不存在的情况
	 * 	3: 删除一条数据信息后,检查是否和预期的结果一致(也就是29条作业信息)
	 * 	4: 请求正确的数据信息后,检查是否和预期数据量是否一致
	 */
	@Test
	public void getEtlJobData() {

		//1: 请求正确的数据信息后,检查是否和初始化时的数据量是否一致
		String getEtlSysData = new HttpClient().addData("colSetId", initStartWayData.DATABASE_ID)
			.post(getActionUrl("getEtlJobData")).getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new BusinessException("连接异常"));
		/*
		 *	检查是否和初始化的数据结果集一致,初始化的时候创建了5张表数据信息,每张表的初始卸数文件格式有6种.
		 * 	所以这里检查是否符合初始的表数据信息,也就是30条作业任务数据
		 */
		assertThat(actionResult.getDataForEntityList(Result.class).size(), is(30));

		//	2: 模拟一次任务信息不存在的情况
		getEtlSysData = new HttpClient().addData("colSetId", -123123)
			.post(getActionUrl("getPreviewJob")).getBodyString();
		actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new BusinessException("连接异常"));
		assertThat(actionResult.isSuccess(), is(false));

		//3: 删除每张表CSV文件格式的数据信息后,检查是否和预期的结果一致(也就是25条作业信息)
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Assembler assembler = Assembler.newInstance()
				.addSql("DELETE FROM " + Data_extraction_def.TableName + " WHERE dbfile_format = ?")
				.addParam(FileFormat.CSV.getCode());
			assembler.addORParam("table_id", initStartWayData.TABLE_ID_LIST.toArray(new Long[0]));
			SqlOperator
				.execute(db, assembler.sql(), assembler.params());
			SqlOperator.commitTransaction(db);
			//4: 请求正确的数据信息后,检查是否和预期数据量是否一致
			getEtlSysData = new HttpClient().addData("colSetId", initStartWayData.DATABASE_ID)
				.post(getActionUrl("getEtlJobData")).getBodyString();
			actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接异常"));
			assertThat(actionResult.getDataForEntityList(Result.class).size(), is(25));
		}

	}

	/**
	 * 1: 请求正确的数据信息后,检查是否和初始化时的数据量是否一致
	 */
	@Test
	public void getAgentPath() {
		//1: 请求正确的数据信息后,检查是否和初始化时的数据量是否一致
		String getEtlSysData = new HttpClient().addData("colSetId", initStartWayData.DATABASE_ID)
			.post(getActionUrl("getAgentPath")).getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new BusinessException("连接异常"));
		Map<Object, Object> dataForMap = actionResult.getDataForMap();
		//检查获取的Agent部署路径是否和查询出来的一致
		assertThat(dataForMap.get("pro_dic"), is(initStartWayData.AGENT_PATH));
		//检查获取的Agent部署日志路径是否一致
		assertThat(dataForMap.get("log_dic"), is(initStartWayData.AGENT_LOG));
	}

	/**
	 * 1: 根据初始化的数据,获取作业的数据信息 2: 想后端发生保存请求,并模拟未设置工程编号,任务编号及作业程序类型的数据 3: 向后端发生正确的数据信息
	 */
	@Test
	public void saveJobDataToDatabase() {

		//1: 根据初始化的数据,获取作业的数据信息
		String getEtlSysData = new HttpClient().addData("colSetId", initStartWayData.DATABASE_ID)
			.post(getActionUrl("getPreviewJob")).getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new AppSystemException("数据转换异常"));
		List<Etl_job_def> dataForEntityList = actionResult.getDataForEntityList(Etl_job_def.class);
		//2: 想后端发生保存请求,并模拟未设置工程编号,任务编号及作业程序类型的数据
		getEtlSysData = new HttpClient().addData("colSetId", initStartWayData.DATABASE_ID)
			.addData("source_id", initStartWayData.SOURCE_ID).addData("etl_sys_cd", initStartWayData.ETL_SYS_CD)
			.addData("sub_sys_cd", initStartWayData.SUB_SYS_CD).addData("pro_dic", initStartWayData.AGENT_PATH)
			.addData("log_dic", initStartWayData.AGENT_LOG).addData("etlJobs", JSON.toJSONString(dataForEntityList))
			.addData("ded_arr", StringUtils.join(initStartWayData.DED_ID, "^"))
			.post(getActionUrl("saveJobDataToDatabase")).getBodyString();
		actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new BusinessException("连接异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//设置默认的参数信息
		dataForEntityList.forEach(etl_job_def -> {
			etl_job_def.setEtl_sys_cd(initStartWayData.ETL_SYS_CD);
			etl_job_def.setSub_sys_cd(initStartWayData.SUB_SYS_CD);
			etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
		});
		//3: 向后端发生正确的数据信息
		getEtlSysData = new HttpClient().addData("colSetId", initStartWayData.DATABASE_ID)
			.addData("source_id", initStartWayData.SOURCE_ID).addData("etl_sys_cd", initStartWayData.ETL_SYS_CD)
			.addData("sub_sys_cd", initStartWayData.SUB_SYS_CD).addData("pro_dic", initStartWayData.AGENT_PATH)
			.addData("log_dic", initStartWayData.AGENT_LOG).addData("etlJobs", JSON.toJSONString(dataForEntityList))
			.addData("ded_arr", StringUtils.join(initStartWayData.DED_ID, "^"))
			.post(getActionUrl("saveJobDataToDatabase")).getBodyString();
		actionResult = JsonUtil.toObjectSafety(getEtlSysData, ActionResult.class)
			.orElseThrow(() -> new AppSystemException("数据转换异常"));
		assertThat(actionResult.isSuccess(), is(true));
		//4: 检查最终的作业数据量是否和初始化的数量一致
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long countNum = SqlOperator
				.queryNumber(db,
					"SELECT COUNT(1) FROM " + Etl_job_def.TableName + " WHERE etl_sys_cd = ? AND sub_sys_cd = ?",
					initStartWayData.ETL_SYS_CD, initStartWayData.SUB_SYS_CD)
				.orElseThrow(() -> new AppSystemException("查询异常"));
			assertThat(countNum, is(30L));
		}
	}
}
