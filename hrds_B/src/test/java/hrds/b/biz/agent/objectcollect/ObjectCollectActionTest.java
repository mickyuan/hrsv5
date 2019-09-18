package hrds.b.biz.agent.objectcollect;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * description: 对象采集接口测试用例 <br>
 * date: 2019/9/18 16:07 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class ObjectCollectActionTest extends WebBaseTestCase {

	private static final Logger logger = LogManager.getLogger();
	private static String bodyString;
	private static ActionResult ar;
	private static final long OBJECT_COLLECT_ROWS = 1L; // 向object_collect表中初始化的数据条数。
	private static final long OBJECT_COLLECT_TASK_ROWS = 5L; // 向object_collect_task表中初始化的数据条数。
	private static final long OBJECT_STORAGE_ROWS = 5L; // 向object_storage表中初始化的数据条数。
	private static final long OBJECT_COLLECT_STRUCT_ROWS = 25L; // 向object_collect_struct表中初始化的数据条数。
	private static final long AGENT_ID = 10000001L; //Agent信息表id
	private static final long ODC_ID = 20000001L;   //对象采集设置表id
	private static final long OCS_ID = 30000001L;   //对象采集对应信息表任务
	private static final long OBJ_STID = 40000001L;   //对象采集存储设置表存储编号
	private static final long STRUCT_ID = 50000001L;   //对象采集结构信息表结构信息id

	/**
	 * description: 测试用例初始化参数 <br>
	 * date: 2019/9/18 14:19 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@BeforeClass
	public static void beforeTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int num = 0;
			for (int i = 0; i < OBJECT_COLLECT_ROWS; i++) {
				//造agent_info表数据
				HttpServerConfBean test = HttpServerConf.getHttpServer("agentServerInfo");
				Agent_info agent_info = new Agent_info();
				agent_info.setUser_id(1001L);
				agent_info.setSource_id(20000001L);
				agent_info.setAgent_id(AGENT_ID);
				agent_info.setAgent_ip(test.getHost());
				agent_info.setAgent_port(String.valueOf(test.getHttpPort()));
				agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
				agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
				agent_info.setAgent_name("数据库agent");
				agent_info.setCreate_date(DateUtil.getSysDate());
				agent_info.setCreate_time(DateUtil.getSysTime());
				// agent_info表信息
				assertThat("初始化数据成功", agent_info.add(db), is(1));
				Object_collect object_collect = new Object_collect();
				object_collect.setOdc_id(ODC_ID);
				object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
				object_collect.setObj_number("测试对象采集编号");
				object_collect.setObj_collect_name("测试对象采集名称");
				object_collect.setSystem_name("Windows 10");
				object_collect.setHost_name("zhuxi");
				object_collect.setLocal_time(DateUtil.getDateTime());
				object_collect.setServer_date(DateUtil.getSysDate());
				object_collect.setS_date(DateUtil.getSysDate());
				object_collect.setE_date(DateUtil.getSysDate());
				object_collect.setDatabase_code(DataBaseCode.UTF_8.getCode());
				object_collect.setRun_way(ExecuteWay.MingLingChuFa.getCode());
				object_collect.setFile_path("/aaaa/ccc/ddd");
				object_collect.setIs_sendok(IsFlag.Fou.getCode());
				object_collect.setAgent_id(AGENT_ID);
				assertThat("初始化数据成功", object_collect.add(db), is(1));
				for (int j = 0; j < OBJECT_COLLECT_TASK_ROWS; j++) {
					Object_collect_task object_collect_task = new Object_collect_task();
					object_collect_task.setOcs_id(OCS_ID + j);
					object_collect_task.setAgent_id(AGENT_ID);
					object_collect_task.setEn_name("aaa" + i);
					object_collect_task.setZh_name("测试aaa" + i);
					object_collect_task.setCollect_data_type(CollectDataType.JSON.getCode());
					object_collect_task.setDatabase_code(DataBaseCode.UTF_8.getCode());
					object_collect_task.setOdc_id(ODC_ID);
					assertThat("初始化数据成功", object_collect_task.add(db), is(1));
					Object_storage object_storage = new Object_storage();
					object_storage.setObj_stid(OBJ_STID + j);
					object_storage.setIs_hbase(IsFlag.Fou.getCode());
					object_storage.setIs_hdfs(IsFlag.Shi.getCode());
					object_storage.setOcs_id(OCS_ID + j);
					assertThat("初始化数据成功", object_storage.add(db), is(1));
					for (int k = 0; k < OBJECT_COLLECT_TASK_ROWS; k++) {
						Object_collect_struct object_collect_struct = new Object_collect_struct();
						object_collect_struct.setStruct_id(STRUCT_ID + num);
						object_collect_struct.setOcs_id(OCS_ID + j);
						object_collect_struct.setColl_name("testcol" + num);
						object_collect_struct.setData_desc("测试对象中文描述" + num);
						object_collect_struct.setStruct_type(ObjectDataType.ZiFuChuan.getCode());
						num++;
						assertThat("初始化数据成功", object_collect_struct.add(db), is(1));
					}
				}
			}
			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * description: searchObjectCollect方法测试用例1，只有正确的Agent_id，新增逻辑进非结构化采集页面 <br>
	 * date: 2019/9/18 17:41 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void searchObjectCollectTest1() {
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("searchObjectCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		logger.info(ar.getData().toString());
	}

	/**
	 * description: searchObjectCollect方法测试用例2，只有错误的Agent_id，新增逻辑进非结构化采集页面 <br>
	 * date: 2019/9/18 17:41 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void searchObjectCollectTest2() {
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID + 111)
				.post(getActionUrl("searchObjectCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("根据Agent_id" + (AGENT_ID
				+ 111) + "查询不到Agent_info表信息"));
	}

	/**
	 * description: searchObjectCollect方法测试用例3，正确的Agent_id和正确的ODC_ID，编辑逻辑进非结构化采集页面 <br>
	 * date: 2019/9/18 17:41 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void searchObjectCollectTest3() {
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("searchObjectCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		logger.info(ar.getData().toString());
	}

	/**
	 * description: searchObjectCollect方法测试用例4，正确的Agent_id和错误的ODC_ID，编辑逻辑进非结构化采集页面 <br>
	 * date: 2019/9/18 17:41 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void searchObjectCollectTest4() {
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("odc_id", ODC_ID + 111)
				.post(getActionUrl("searchObjectCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("根据odc_id" +
				(ODC_ID + 111) + "查询不到object_collect表信息"));
	}

	/**
	 * description: 测试用例清理数据 <br>
	 * date: 2019/9/18 14:20 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@AfterClass
	public static void afterTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			SqlOperator.execute(db, "DELETE FROM agent_info WHERE agent_id = ?", AGENT_ID);
			SqlOperator.execute(db, "DELETE FROM object_collect WHERE agent_id = ?", AGENT_ID);
			SqlOperator.execute(db, "DELETE FROM object_collect_task WHERE agent_id = ?", AGENT_ID);
			for (int i = 0; i < OBJECT_STORAGE_ROWS; i++) {
				SqlOperator.execute(db, "DELETE FROM object_storage WHERE ocs_id = ?", OCS_ID + i);
			}
			for (int i = 0; i < OBJECT_COLLECT_STRUCT_ROWS; i++) {
				SqlOperator.execute(db, "DELETE FROM object_collect_struct WHERE ocs_id = ?", OCS_ID + i);
			}
			SqlOperator.commitTransaction(db);
		}
	}
}
