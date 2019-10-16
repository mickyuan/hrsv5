package hrds.b.biz.agentdepoly;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "Agent部署管理", author = "Mr.Lee", createdate = "2019-08-30 10:01")
public class AgentDeployAction extends BaseAction {

	@Method(desc = "获取当前用户的数据源信息",
					logicStep = "查询当前用户的数据源信息")
	@Return(desc = "返回用户的数据源信息", range = "可以为空,为空表示该用户没有数据源信息")
	public List<Map<String, Object>> getDataSourceInfo() {

		//1: 查询当前用户的数据源信息
		return Dbo.queryList(
						"SELECT t2.source_id,t2.datasource_name FROM " + Agent_info.TableName + " t1 JOIN  " +
										Data_source.TableName + " t2 ON t1.source_id = t2.source_id  " +
										"WHERE t1.user_id = ? GROUP BY t2.source_id,t2.datasource_name",
						getUserId());
	}


	@Method(desc = "获取数据源下对应某种Agent的信息", logicStep = "根据选择的数据源及Agent类型查询其对应的Agent")
	@Param(name = "source_id", desc = "数据源ID", range = "不能为空的整数")
	@Param(name = "agent_type", desc = "Agent类型", range = "不能为空的字符串")
	@Return(desc = "查询的Agent集合数据", range = "可以为空,为空表示为获取到Agent信息")
	public List<Map<String, Object>> getAgentInfo(long source_id, String agent_type) {

		return Dbo.queryList("SELECT * FROM agent_info WHERE source_id = ? AND agent_type = ? AND user_id = ?", source_id, agent_type,
						getUserId());
	}

	@Method(desc = "获取当前部署的Agent信息", logicStep = "根据选择的数据源及Agent类型查询其对应的Agent")
	@Param(name = "source_id", desc = "数据源ID", range = "不能为空的整数")
	@Param(name = "agent_type", desc = "Agent类型", range = "不能为空的字符串")
	@Return(desc = "当前Agent的部署信息", range = "可以为空,因为会出现是第一次部署", isBean = true)
	public Agent_down_info getAgentDownInfo(long agent_id, String agent_type) {

		return Dbo.queryOneObject(Agent_down_info.class, "SELECT * FROM " + Agent_down_info.TableName + " WHERE " +
														"agent_id = ? AND agent_type = ? AND user_id = ?",
										agent_id, agent_type, getUserId()).orElseThrow(()->new BusinessException("部署Agnet信息查询错误!"));
	}

	@Method(desc = "获取Agent的全部类型信息", logicStep = "获取Agent的全部类型信息")
	@Return(desc = "返回Agent的类型集合", range = "不可为空")
	public Map<String, String> getAgentType() {

		Map<String, String> agentTypeMap = new HashMap<>();
		agentTypeMap.put("shujuku", AgentType.ShuJuKu.getCode());
		agentTypeMap.put("shujuwenjian", AgentType.DBWenJian.getCode());
		agentTypeMap.put("feijiegouhua", AgentType.WenJianXiTong.getCode());
		agentTypeMap.put("banjiegouhua", AgentType.DuiXiang.getCode());
		agentTypeMap.put("ftp", AgentType.FTP.getCode());

		return agentTypeMap;
	}

	@Method(desc = "获取当前部署的Agent信息", logicStep = "根据选择的数据源及Agent类型查询其对应的Agent")
	@Param(name = "agent_down_info", desc = "", range = "不能为空的整数", isBean = true)
	public void deployAgent(Agent_down_info agent_down_info) {

	}

	/**
	 * <p>方法描述: 保存此次部署Agent的信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-04</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	@Method(desc = "保存此次部署Agent的信息", logicStep = "保存此次部署Agent的信息")
	@Param(name = "agent_down_info", desc = "", range = "不能为空的整数", isBean = true)
	private void saveAgentDownInfo(Agent_down_info agent_down_info) {

		if( agent_down_info.add(Dbo.db()) != 1 ) {
			throw new BusinessException(ExceptionEnum.AGENT_DOWN_ERROR.getMessage());
		}
	}
}
