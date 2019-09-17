package hrds.b.biz.agent.unstructuredfilecollect;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.File_collect_set;
import hrds.commons.entity.File_source;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.apache.calcite.avatica.com.google.protobuf.StringValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * description: UnstructuredFileCollectAction类测试用例 <br>
 * date: 2019/9/12 16:03 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class UnstructuredFileCollectActionTest extends WebBaseTestCase {
	private static String bodyString;
	private static ActionResult ar;
	private static final long FILE_COLLECT_SET_ROWS = 2L; // 向表中初始化的数据条数。
	private static final int FILE_SOURCE_ROWS = 10;
	/**
	 * description: 测试类初始化参数 <br>
	 * date: 2019/9/12 16:41 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@BeforeClass
	public static void beforeTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//造agent_info表数据
			long agent_id = 10000001L;
			HttpServerConfBean test = HttpServerConf.getHttpServer("agentServerInfo");
			Agent_info agent_info = new Agent_info();
			agent_info.setUser_id(1001L);
			agent_info.setSource_id(20000001L);
			agent_info.setAgent_id(agent_id);
			agent_info.setAgent_ip(test.getHost());
			agent_info.setAgent_port(String.valueOf(test.getHttpPort()));
			agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
			agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
			agent_info.setAgent_name("数据库agent");
			agent_info.setCreate_date(DateUtil.getSysDate());
			agent_info.setCreate_time(DateUtil.getSysTime());
			// agent_info表信息
			assertThat("初始化数据成功", agent_info.add(db), is(1));
			//造file_collect_set表数据
			long fcs_id = 10000002L;
			for(int i = 0;i<FILE_COLLECT_SET_ROWS;i++){
				File_collect_set file_collect_set = new File_collect_set();
				file_collect_set.setFcs_id(fcs_id+i);
				file_collect_set.setFcs_name("zxzwjcj"+i);
				file_collect_set.setHost_name("zhuxi");
				file_collect_set.setSystem_type("Windows 10");
				file_collect_set.setIs_sendok(IsFlag.Fou.getCode());
				file_collect_set.setIs_solr(IsFlag.Shi.getCode());
				file_collect_set.setAgent_id(agent_id);
				assertThat("初始化数据成功", file_collect_set.add(db), is(1));
			}
			for(int i = 0;i<FILE_SOURCE_ROWS;i++){
				File_source file_source = new File_source();
				file_source.setFile_source_id(PrimayKeyGener.getNextId());
				file_source.setAgent_id(agent_id);
				file_source.setFcs_id(fcs_id);
				file_source.setFile_source_path("/aaa/bbb/"+i);
				file_source.setIs_audio(IsFlag.Shi.getCode());
				file_source.setIs_image(IsFlag.Shi.getCode());
				file_source.setIs_office(IsFlag.Shi.getCode());
				file_source.setIs_other(IsFlag.Shi.getCode());
				file_source.setIs_pdf(IsFlag.Shi.getCode());
				file_source.setIs_text(IsFlag.Shi.getCode());
				file_source.setIs_video(IsFlag.Shi.getCode());
				assertThat("初始化数据成功", file_source.add(db), is(1));
			}
			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * description: 测试addFileCollect方法新增逻辑 <br>
	 * date: 2019/9/12 16:41 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void searchFileCollectTest1() {
		// 1）提交数据给Action
		bodyString = new HttpClient()
				.addData("agent_id", 10000001L)
				.post(getActionUrl("searchFileCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
	}

	/**
	 * description: 测试addFileCollect方法编辑逻辑 <br>
	 * date: 2019/9/12 16:41 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void searchFileCollectTest2() {
		// 1）提交数据给Action
		bodyString = new HttpClient()
				.addData("agent_id", 10000001L)
				.addData("fcs_id", 10000002L)
				.post(getActionUrl("searchFileCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
	}

	/**
	 * description: 测试保存文件采集新增逻辑 <br>
	 * date: 2019/9/17 10:55 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void saveFileCollectTest1() {
		// 1）提交数据给Action
		bodyString = new HttpClient()
				.addData("agent_id", 10000001L)
				.addData("fcs_name", "zxzwjcj2")
				.addData("host_name", "zhuxi")
				.addData("system_type", "Windows10")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("is_add", IsFlag.Shi.getCode())
				.post(getActionUrl("saveFileCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		System.out.println(ar.isSuccess());
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) count from file_collect_set");
			assertThat("添加数据成功", optionalLong.getAsLong(), is(FILE_COLLECT_SET_ROWS+1));
		}
	}

	/**
	 * description: 测试保存文件采集新增逻辑任务名称重复报错 <br>
	 * date: 2019/9/17 10:55 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void saveFileCollectTest2() {
		bodyString = new HttpClient()
				.addData("agent_id", 10000001L)
				.addData("fcs_name", "zxzwjcj0")
				.addData("host_name", "zhuxi")
				.addData("system_type", "Windows 10")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("is_add", IsFlag.Shi.getCode())
				.post(getActionUrl("saveFileCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("非结构化任务名称重复"));
	}

	/**
	 * description: 测试保存文件采集更新逻辑 <br>
	 * date: 2019/9/17 10:55 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void saveFileCollectTest3() {
		bodyString = new HttpClient()
				.addData("agent_id", 10000001L)
				.addData("fcs_id", 10000002L)
				.addData("fcs_name", "zxzwjcj666")
				.addData("host_name", "zhuxi")
				.addData("system_type", "Windows 10")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("is_add", IsFlag.Fou.getCode())
				.post(getActionUrl("saveFileCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) count from file_collect_set " +
					"where fcs_name = ?", "zxzwjcj666");
			assertThat("更新数据成功", optionalLong.getAsLong(), is(1L));
		}
	}

	/**
	 * description: 测试保存文件采集更新逻辑任务名称重复报错 <br>
	 * date: 2019/9/17 10:55 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void saveFileCollectTest4() {
		bodyString = new HttpClient()
				.addData("agent_id", 10000001L)
				.addData("fcs_id", 10000002L)
				.addData("fcs_name", "zxzwjcj1")
				.addData("host_name", "zhuxi")
				.addData("system_type", "Windows 10")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("is_add", IsFlag.Fou.getCode())
				.post(getActionUrl("saveFileCollect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("非结构化任务名称重复"));
	}
	
	/**
	 * description:  测试根据文件系统设置表的id查询源文件设置表<br>
	 * date: 2019/9/17 15:13 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param  
	 * @return void
	 */
	@Test
	public void searchFileSourceTest1(){
		bodyString = new HttpClient()
				.addData("fcs_id", 10000002L)
				.post(getActionUrl("searchFileSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		assertThat(((JSONArray)ar.getData()).size(),is(FILE_SOURCE_ROWS));
	}
	
	/**
	 * description: 测试根据文件系统设置表的id查询源文件设置表,查询出的数据为空 <br>
	 * date: 2019/9/17 15:17 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param  
	 * @return void
	 */
	@Test
	public void searchFileSourceTest2(){
		bodyString = new HttpClient()
				.addData("fcs_id", 100000077L)
				.post(getActionUrl("searchFileSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		assertThat(((JSONArray)ar.getData()).size(),is(0));
	}
	
	/**
	 * description: 选择文件夹测试，agent_id正确测试 <br>
	 * date: 2019/9/17 15:59 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param  
	 * @return void
	 */ 
	@Test
	public void selectPathTest1(){
		bodyString = new HttpClient()
				.addData("agent_id", 10000001L)
//				.addData("path","")
				.post(getActionUrl("selectPath")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		Object data = ar.getData();
		System.out.println(data.toString());
		assertThat(ar.isSuccess(), is(true));
	}

	/**
	 * description: 选择文件夹测试，agent_id不正确测试 <br>
	 * date: 2019/9/17 15:59 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void selectPathTest2(){

	}

	@AfterClass
	public static void afterTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long agent_id = 10000001L;
			SqlOperator.execute(db, "DELETE FROM agent_info WHERE agent_id = ?", agent_id);
			SqlOperator.execute(db, "DELETE FROM file_collect_set WHERE agent_id = ?", agent_id);
			SqlOperator.execute(db,"DELETE FROM file_source WHERE agent_id = ?",agent_id);
			SqlOperator.commitTransaction(db);
		}
	}
}
