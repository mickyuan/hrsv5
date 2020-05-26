package hrds.f.biz.timer;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.AgentStatus;
import hrds.commons.entity.Agent_info;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.agentmonitor.AgentMonitorUtil;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommunicationManager extends TimerTask {

  private static final Log logger = LogFactory.getLog(CommunicationManager.class);

  @Override
  public void run() {

	logger.info("系统Agent开始监听通讯状态...");
	try (DatabaseWrapper db = new DatabaseWrapper()) {
	  getAgent_info(db)
		  .forEach(
			  agentMpa -> {
				boolean agentState =
					AgentMonitorUtil.agentMonitor(
						String.valueOf(agentMpa.get("agent_ip")),
						Integer.parseInt(String.valueOf(agentMpa.get("agent_port"))));
				if (agentState) {
				  updateAgentStatus(
					  Long.parseLong(String.valueOf(agentMpa.get("agent_id"))),
					  db,
					  AgentStatus.YiLianJie.getCode()); // 更改此Agent的连接信息状态
				} else {
				  updateAgentStatus(
					  Long.parseLong(String.valueOf(agentMpa.get("agent_id"))),
					  db,
					  AgentStatus.WeiLianJie.getCode()); // 更改此Agent的连接信息状态
				}
			  });
	} catch (Exception e) {
	  if (e instanceof BusinessException) {
		throw (BusinessException) e;
	  } else {
		throw new AppSystemException(e);
	  }
	}
  }

  private List<Map<String, Object>> getAgent_info(DatabaseWrapper db) {

	return SqlOperator.queryList(
		db,
		"SELECT agent_name, agent_type, agent_ip, agent_port, agent_id, 'collect' AS type FROM "
			+ Agent_info.TableName);
	//    SqlOperator dbo = new SqlOperator();
	//    dbo.addSql(
	//        "SELECT agent_name, agent_type, agent_ip, agent_port,  agent_id, 'collect' AS type
	// FROM agent_info");
	//    dbo.addSql(" UNION");
	//    dbo.addSql(
	//        " SELECT sdj.sdm_agent_name AS agent_name,sdj.sdm_agent_type AS agent_type,
	// sdj.sdm_agent_ip AS agent_ip,sdj.sdm_agent_port");
	//    dbo.addSql(
	//        " AS agent_port,sdj.sdm_agent_id  AS agent_id,'sdm' AS type FROM sdm_agent_info sdj
	// ORDER BY agent_id");
  }

  //  public void saveCommunication_info(Communication_info com_info, SQLExecutor db) {
  //
  //    // sql容器
  //    SqlOperator dbo = new SqlOperator();
  //    com_info.setCom_id(PrimayKeyGener.getNextId());
  //    dbo.add(com_info, db);
  //  }

  private static void updateAgentStatus(long agent_id, DatabaseWrapper db, String agent_status) {

	logger.info("----------监听 Agent( " + agent_id + " )通讯,更新当前Agent的连接状态----------" + agent_status);
	// sql容器
	//    if ("sdm".equals(type)) { // 流Agent
	//      dbo.addSql("UPDATE sdm_agent_info SET sdm_agent_status = ? WHERE sdm_agent_id = ?");
	//      Sdm_agent_info sdmAgent_info = new Sdm_agent_info();
	//      sdmAgent_info.setSdm_agent_status(agent_status);
	//      sdmAgent_info.setSdm_agent_id(agent_id);
	//      dbo.addParam(sdmAgent_info.getSdm_agent_status());
	//      dbo.addParam(sdmAgent_info.getSdm_agent_id());
	//    } else { // 采集Agent
	db.execute("UPDATE " + Agent_info.TableName + " SET agent_status = ? WHERE agent_id = ?", agent_status, agent_id);
  }
}
