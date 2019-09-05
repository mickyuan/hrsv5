package hrds.b.biz.agentdepoly;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>标    题: 海云数服 V5.0</p>
 * <p>描    述: 测试类</p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-08-30 14:01</p>
 * <p>version: JDK 1.8</p>
 */
public class AgentDeployActionTest extends WebBaseTestCase {

	private static final int Init_Rows = 10; // 向表中初始化的数据条数。
	private static final int CREATE_ID = -9999; //创建用户
	private static final int USER_ID = -9998; //使用用户
	private String save_dir = System.getProperty("user.dir");
	private String log_dir = save_dir + File.separator + "log" + File.separator + "running.log";

	/**
	 * <p>方法描述:进行测试数据的插入(数据源,agent信息)</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	@Before
	public void before() {

		//数据源的批量插入数据集合
		List<Object[]> sourceParams = new ArrayList<>();
		//Agent的批量插入数据集合
		List<Object[]> agentParams = new ArrayList<>();
		//Agent部署数据的批量
		List<Object[]> agentDownParams = new ArrayList<>();
		Data_source data_source = new Data_source();
		//准备数数据源测试数据
		for(int i = -200; i < -190; i++) {
			//数据源信息
			int source_id = i;
			String datasource_number = "ls_" + i;
			String datasource_name = "lqcs_" + i;
			String source_remark = "lqcs_" + i;
			String create_date = DateUtil.getSysDate();
			String create_time = DateUtil.getSysTime();
			Object[] source = new Object[] { source_id, source_remark, datasource_name, datasource_number, create_date, create_time, CREATE_ID };
			sourceParams.add(source);
			//对应的Agent信息
			int agent_id = i;
			String agent_name = "agent_" + i;
			String agent_type = AgentType.ShuJuKu.getCode();//数据库Agent
			String agent_ip = "127.0.0.1";
			String agent_port = "55555";
			String agent_status = AgentStatus.WeiLianJie.getCode();

			Object[] agent = new Object[] { agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date, create_time, source_id,
							USER_ID };
			agentParams.add(agent);
			//Agent部署信息
			int down_id = i;
			String user_name = "root";
			String passwd = "q1w2e3";
			String deploy = IsFlag.Fou.getCode();
			Object[] agentDown = new Object[] { down_id, agent_name, agent_ip, agent_port, user_name, passwd, save_dir, log_dir, deploy,
							agent_type, agent_id,
							USER_ID };
			agentDownParams.add(agentDown);
		}
		//批数据插入
		try (DatabaseWrapper db = new DatabaseWrapper()) {

			int[] sourceNums = db.execBatch(
							"insert into " + Data_source.TableName +
											"( source_id, source_remark, datasource_name, datasource_number, create_date, create_time, user_id) values(?,?,?,?,?,?,?)",
							sourceParams
			);
			assertThat("数据源测试数据初始化", sourceNums.length, is(Init_Rows));

			int[] agentNums = db.execBatch(
							"insert into " + Agent_info.TableName +
											"( agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date, create_time, source_id, user_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
							agentParams
			);
			assertThat("Agent测试数据初始化", agentNums.length, is(Init_Rows));

			int[] agentDownNums = db.execBatch(
							"insert into " + Agent_down_info.TableName +
											"( down_id, agent_name, agent_ip, agent_port, user_name, passwd, save_dir, log_dir, deploy, agent_type, agent_id, user_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
							agentDownParams
			);
			assertThat("Agent部署测试数据初始化", agentDownNums.length, is(Init_Rows));
			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * <p>方法描述: 将测试的数据删除掉</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	@After
	public void after() {

		try (DatabaseWrapper db = new DatabaseWrapper()) {

			//删除测试数据源信息
			int deleteSourceNum = SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE user_id = ?", CREATE_ID);

			//删除agent测试数据
			int deleteNum = SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", USER_ID);

			//删除agent测试数据
			int deleteDownNum = SqlOperator.execute(db, "delete from " + Agent_down_info.TableName + " WHERE user_id = ?", USER_ID);

			//先commit删除的操作
			SqlOperator.commitTransaction(db);

			//在进行查询进行检测数据清除
			long nums = SqlOperator.queryNumber(db, "select count(1) from " + Data_source.TableName + " WHERE user_id = ?", CREATE_ID)
							.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据源数据为:" + deleteSourceNum, nums, is(0));

			long agentNums = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " WHERE user_id = ?", USER_ID)
							.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的Agent数据为:" + deleteNum, agentNums, is(0));

			long agentDownNum = SqlOperator.queryNumber(db, "select count(1) from " + Agent_down_info.TableName + " WHERE user_id = ?", USER_ID)
							.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的Agent部署数据为:" + agentDownNum, agentNums, is(0));

			//删除测试文件路径
			File file = new File(log_dir);
			if( file.exists() ) {
				file.delete();
			}
		}
	}

	/**
	 * <p>方法描述: 测试查询当前用户的数据源信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	@Test
	public void getDataSourceInfo() {

		String bodyString = new HttpClient()
						.addData("user_id", USER_ID)
						.post(getActionUrl("getDataSourceInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		List<Map<String, Object>> result = (List<Map<String, Object>>)ar.getData();
		Map<String, Object> sysParaList = (Map<String, Object>)result.get(0);
		System.out.println(sysParaList);
		assertThat(result.size(), is(Init_Rows));
	}

	/**
	 * <p>方法描述: 测试获取Agent信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	@Test
	public void getAgentInfo() {

		String bodyString = new HttpClient()
						.addData("user_id", USER_ID).addData("source_id", -200).addData("agent_type", AgentType.ShuJuKu.getCode())
						.post(getActionUrl("getAgentInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		Map<String, Object> result = (Map<String, Object>)ar.getData();
		List<Map<String, Object>> sysParaList = (List<Map<String, Object>>)result.get("agentInfo");
		Map<String, Object> row = sysParaList.get(0);
		//当前数据源source_id(-200)下只有一条数据信息
		assertThat(sysParaList.size(), is(1));
	}

	/**
	 * <p>方法描述: 测试获取已部署的Agent信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	@Test
	public void getAgentDownInfo() {

		String bodyString = new HttpClient()
						.addData("user_id", USER_ID).addData("agent_id", -200).addData("agent_type", AgentType.ShuJuKu.getCode())
						.post(getActionUrl("getAgentInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		Map<String, Object> result = (Map<String, Object>)ar.getData();
		List<Map<String, Object>> sysParaList = (List<Map<String, Object>>)result.get("agentDownInfo");
		Map<String, Object> row = sysParaList.get(0);
		//当前agent_id(-200)下只有一条数据信息
		assertThat(sysParaList.size(), is(1));
	}
}
