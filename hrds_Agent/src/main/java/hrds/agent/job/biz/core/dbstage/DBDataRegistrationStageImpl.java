package hrds.agent.job.biz.core.dbstage;

import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.core.AbstractJobStage;

/**
 * ClassName: DBDataRegistrationStageImpl <br/>
 * Function: 数据登记阶段  <br/>
 * Reason: 数据库直连采集
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DBDataRegistrationStageImpl extends AbstractJobStage {
	@Override
	public StageStatusInfo handleStage() {
		throw new IllegalStateException("这是一个空实现");
	}
}
