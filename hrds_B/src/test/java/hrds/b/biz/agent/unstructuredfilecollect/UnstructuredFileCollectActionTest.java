package hrds.b.biz.agent.unstructuredfilecollect;

import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * description: UnstructuredFileCollectAction类测试用例 <br>
 * date: 2019/9/12 16:03 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class UnstructuredFileCollectActionTest extends WebBaseTestCase {
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
	public static void beforeTest(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long agent_id = 10000001L;
			long source_id = 20000001L;
			String create_date = DateUtil.getSysDate();
			String create_time = DateUtil.getSysTime();
			Long user_id = 1001L;
			String agent_name = "数据库agent";
			String agent_type = AgentType.ShuJuKu.getCode();
			HttpServerConfBean test = HttpServerConf.getHttpServer("agentServerInfo");
			String agent_ip = test.getHost();
			int agent_port = test.getHttpPort();
			String agent_status = AgentStatus.YiLianJie.getCode();
			// agent_info表信息
			SqlOperator.execute(db, "insert into agent_info values(?,?,?,?,?,?,?,?,?,?)", agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date
					, create_time, source_id, user_id);

			SqlOperator.commitTransaction(db);
		}
	}
	
	/**
	 * description: 测试addFileCollect方法 <br>
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
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("agent_id", 10000001L)          // 每个 addData 为一个"名/值"对
				.post(getActionUrl("searchFileCollect"));   // getActionUrl中传入“被测试的Action方法名字”
		System.out.println(resVal.getBodyString());
	}

	@AfterClass
	public static void afterTest(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long agent_id = 10000001L;
			SqlOperator.execute(db, "delete from agent_info where agent_id = ?", agent_id);
			SqlOperator.commitTransaction(db);
		}
	}
}
