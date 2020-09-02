package hrds.b.biz.etljobs;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.b.biz.agent.CheckParam;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DataSourceType;
import hrds.commons.utils.etl.EtlJobUtil;

@DocClass(desc = "生成作业的接口类", author = "Mr.Lee", createdate = "2020-08-26 10:42")
public class EtlJobAction extends BaseAction {

	@Method(desc = "生成作业", logicStep = "")
	@Param(name = "database_id", desc = "任务ID", range = "不可为空")
	@Param(name = "etl_sys_cd", desc = "作业工程编号", range = "不可为空")
	@Param(name = "sub_sys_cd", desc = "作业任务编号", range = "不可为空")
	@Param(name = "agent_type", desc = "采集Agent类型", range = "不可为空")
	public void saveEtlJobs(String database_id, String etl_sys_cd, String sub_sys_cd, String agent_type) {
		AgentType agentType = AgentType.ofEnumByCode(agent_type);
		int executeStatus = EtlJobUtil.saveJob(database_id, DataSourceType.DCL, etl_sys_cd, sub_sys_cd, agentType);
		if (executeStatus == -1) {
			CheckParam.throwErrorMsg("生成作业失败");
		}
	}
}
