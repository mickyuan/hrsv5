package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.core.AbstractJobStage;

/**
 * ClassName: DBCalIncrementStageImpl <br/>
 * Function: 计算增量阶段  <br/>
 * Reason: 数据库直连采集
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DBCalIncrementStageImpl extends AbstractJobStage {

	@Method(desc = "数据库直连采集计算增量阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageStatusInfo handleStage() {
		throw new IllegalStateException("这是一个空实现");
	}
}
