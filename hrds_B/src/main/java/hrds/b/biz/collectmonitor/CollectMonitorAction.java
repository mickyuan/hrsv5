package hrds.b.biz.collectmonitor;

import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_case;
import hrds.commons.entity.Source_file_attribute;

import java.util.List;
import java.util.Map;

/**
 * <p>标    题: 海云数服 V5.0</p>
 * <p>描    述: 采集首页的监控信息</p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-09-04 12:09</p>
 * <p>version: JDK 1.8</p>
 */
public class CollectMonitorAction extends BaseAction {

	/**
	 * <p>方法描述: 查询Agent,数据源配置数量(数据源存在着Agent的时候才会显示具体数量)信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-04</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public Map<String, Object> getAgentNumAndSourceNum() {

		return Dbo.queryOneObject("SELECT COUNT(agent_id) agentNum,COUNT(DISTINCT(source_id)) sourceNum FROM " + Agent_info.TableName
						+ " WHERE user_id = ?", getUserId());
	}

	/**
	 * <p>方法描述: 获取当前用户采集的任务信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-04</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public List<Map<String, Object>> getDatabaseSet() {

		return Dbo.queryList("select task_name taskname ,database_id taskid,task.Agent_id,agent_type from database_set task join agent_info ai" +
										"on task.Agent_id = ai.agent_id where user_id = ? and task.is_sendok = ? and agent_type in (?,?) order by taskid desc ",
						getUserId(), IsFlag.Shi.getCode(), AgentType.ShuJuKu.getCode(), AgentType.DBWenJian.getCode());
	}

	/**
	 * <p>方法描述: 获取数据采集信息总况</p>
	 * <p>1 : 获取已采集的文件数据量大小(DB文件和数据库采集),这里显示的bytes可能需要进行转换单位</p>
	 * <p>2 : 获取当前用户的全部采集任务数(只要是整个流程完成的,不区分是否采集过文件)</p>
	 * <p>3 : 将数据信息合并,并返回</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-04</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public Map<String, Object> getDataCollectInfo() {

		//文件采集的代码项值
		//FIXME 为什么要提前定义变量？
		String fileCode = CollectType.WenJianCaiJi.getCode();

		//1 : 获取已采集的文件数据量大小(DB文件和数据库采集),这里显示的bytes可能需要进行转换单位
		//TODO 这里缺少文件字节的转换,待修改
		Map<String, Object> dataCollectInfo = Dbo
						.queryOneObject(" select sum((case when collect_type = ? then file_size else 0 end)) fileSize," +
										"sum((case when collect_type <> ? then file_size else 0 end)) dbSize FROM " + Source_file_attribute.TableName
										+ "join  agent_info ai on sfa.agent_id = ai.agent_id where user_id = ?", fileCode, fileCode, getUserId());
		//FIXME collect_type不等于fileCode就是dbSize？

		//2 : 获取当前用户的全部采集任务数(只要是整个流程完成的,不区分是否采集过文件)
		Map<String, Object> taskNum = Dbo.queryOneObject(
						"SELECT COUNT( 1 ) taskNum FROM" +
										"( SELECT database_id id, agent_id, is_sendok FROM database_set UNION ALL SELECT fcs_id id, agent_id, is_sendok FROM file_collect_set ) A" +
										" WHERE " +
										" EXISTS ( SELECT 1 FROM agent_info ai WHERE ai.user_id = ? AND ai.agent_id = A.Agent_id ) " +
										" AND is_sendok = ?", getUserId(), IsFlag.Shi.getCode());
		//3 : 将数据信息合并,并返回
		taskNum.putAll(dataCollectInfo);
		return taskNum;
	}

	/**
	 * <p>方法描述: 获取当前任务的采集作业信息</p>
	 * <p>1 : 获取相应的采集情况</p>
	 * <p>2 : 将作业的数据信息处理为有错误的优先显示在前面</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-05</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public static void currentTaskJob(Long database_id) {

		// 1: 获取相应的采集情况
		List<Collect_case> collectJobList = Dbo.queryList(Collect_case.class,
						"SELECT table_name,collect_type,job_type,execute_state,collect_s_date,collect_s_time,collect_e_date,collect_e_time,cc_remark " +
										"FROM collect_case WHERE collect_set_id = ? AND collect_s_date = (select max(collect_s_date) from collect_case where " +
										"collect_set_id = ? ) ORDER BY table_name",
						database_id, database_id);
		//2 : 将作业的数据信息处理为有错误的优先显示在前面
		JobDetails.getDetails(collectJobList);
	}

}
