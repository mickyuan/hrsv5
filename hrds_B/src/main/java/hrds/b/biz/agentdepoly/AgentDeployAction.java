package hrds.b.biz.agentdepoly;

import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;

import java.util.HashMap;
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
	 * <p>方法描述: 获取当前用户的数据源信息</p>
	 * 1 : 查询当前用户的数据源信息
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 *
	 * @return List<Map < String, Object>>
	 * 含义 : 返回当前用户的数据源列表信息
	 * 取值范围 : 可以为空,为空表示当前用户下为定义数据源信息
	 */
	public List<Map<String, Object>> getDataSourceInfo() {

		//1: 查询当前用户的数据源信息
		return Dbo.queryList(
						"SELECT t2.source_id,t2.datasource_name FROM agent_info t1 JOIN  " +
										"data_source t2 ON t1.source_id = t2.SOURCE_ID  " +
										"WHERE t1.user_id = ? GROUP BY t2.source_id,t2.datasource_name",
						2001);
	}

	/**
	 * <p>方法描述: 获取当前用户某种数据源下对应的某种Agent信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 *
	 * @param source_id  含义 : 数据源ID
	 *                   取值范围 : 不能为空
	 * @param agent_type 含义 : agent类型
	 *                   取值范围 : 不能为空,Agent类型
	 * @return　List<Map<String, Object>>
	 * 含义 : 当前用户某一数据源下的某种Agent信息列表
	 * 取值范围 : 可为空,为空表示没有配置Agent信息
	 */
	public List<Map<String, Object>> getAgentInfo(long source_id, String agent_type) {

		return Dbo.queryList("SELECT * FROM agent_info WHERE source_id = ? AND agent_type = ? AND user_id = ?", source_id, agent_type,
						2001);
	}

	/**
	 * <p>方法描述: 获取当前部署的Agent信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-30</p>
	 *
	 * @param agent_id   含义 : AgentID
	 *                   取值范围 : 主键不能为空
	 * @param agent_type 含义 : Agent类型
	 *                   取值范围 : 不能为空
	 * @return Map<String, Object>
	 * 含义 : 当前Agent的部署信息
	 * 取值范围 : 可以为空,因为会出现是第一次部署
	 */
	public Map<String, Object> getAgentDownInfo(long agent_id, String agent_type) {

		return Dbo.queryOneObject("SELECT * FROM " + Agent_down_info.TableName + " WHERE " +
										"agent_id = ? AND agent_type = ? AND user_id = ?",
						agent_id, agent_type, 2001);
	}

	/**
	 * 获取Agent的全部类型信息
	 *
	 * @return Map<String, String>
	 * 含义 : 返回Agent的类型集合
	 * 取值范围 : 不可为空
	 */
	public Map<String, String> getAgentType() {

		Map<String, String> agentTypeMap = new HashMap<>();
		agentTypeMap.put("shujuku", AgentType.ShuJuKu.getCode());
		agentTypeMap.put("shujuwenjian", AgentType.DBWenJian.getCode());
		agentTypeMap.put("feijiegouhua", AgentType.WenJianXiTong.getCode());
		agentTypeMap.put("banjiegouhua", AgentType.DuiXiang.getCode());
		agentTypeMap.put("ftp", AgentType.FTP.getCode());

		return agentTypeMap;
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
	private void saveAgentDownInfo(Agent_down_info agent_down_info) {

		if( agent_down_info.add(Dbo.db()) != 1 ) {
			throw new BusinessException(ExceptionEnum.AGENT_DOWN_ERROR.getMessage());
		}
	}
}
