package hrds.agent.job.biz.core.objectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.core.AbstractJobStage;

//XXX 对象采集要不要加卸数，通过看代码，理解出来的以前的对象采集是没有卸数这一步
@DocClass(desc = "半结构化对象采集卸数实现", author = "zxz", createdate = "2019/10/24 11:43")
public class ObjectUnloadDataStageImpl extends AbstractJobStage {

	@Method(desc = "半结构化对象采集，半结构化对象采集卸数实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		return null;
	}

}
