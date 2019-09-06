package hrds.b.biz.agentdepoly;

import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;

import java.util.List;
import java.util.Map;

/**
 * <p>标    题: 海云数服 V5.0</p>
 * <p>描    述: Agent部署管理</p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-08-30 10:01</p>
 * <p>version: JDK 1.8</p>
 */
public class AgentDeployAction extends BaseAction {

	/**
	 * <p>字段描述: 部署Agent时,FTP的默认端口</p>
	 */
//	private final static int FTP_PORT = 22;//TODO 页面提供传入

	/**
	 * <p>方法描述: 获取当前用户的数据源信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public List<Map<String, Object>> getDataSourceInfo() {
		//查询当前用户的数据源信息
		return Dbo.queryList(
						"SELECT t2.source_id,t2.datasource_name " +
										"FROM agent_info t1 JOIN  data_source t2 ON t1.source_id = t2.SOURCE_ID  " +
										"WHERE t1.user_id = ? GROUP BY t2.source_id,t2.datasource_name",
						getUserId());
	}

	/**
	 * <p>方法描述: 获取数据源对应的某种Agent信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数: {source_id : 数据源ID,agent_type : agent类型}</p>
	 * <p>return:  </p>
	 */
	public Result getAgentInfo(Long source_id, String agent_type) {

		return Dbo.queryResult("SELECT * FROM agent_info WHERE source_id = ? AND agent_type = ? AND user_id = ?", source_id, agent_type,
						getUserId());
	}

	/**
	 * <p>方法描述: 获取当前部署的Agent信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public Result getAgentDownInfo(Long agent_id, String agent_type) {

		return Dbo.queryResult("SELECT * FROM agent_down_info WHERE agent_id = ? AND agent_type = ? AND user_id = ?", agent_id, agent_type,
						getUserId());
	}

	/**
	 * <p>方法描述: 部署Agent</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public void deployAgent(@RequestBean Agent_down_info agent_down_info) {


	}

	/**
	 * <p>方法描述: 保存此次部署Agent的信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-04</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public void saveAgentDownInfo(Agent_down_info agent_down_info) {

		if( agent_down_info.add(Dbo.db()) != 1 ) {
			throw new BusinessException(ExceptionEnum.AGENT_DOWN_ERROR.getMessage());
		}
	}
}
