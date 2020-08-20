package hrds.agent.job.biz.core.jdbcdirectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.metaparse.CollectTableHandleFactory;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DocClass(desc = "数据库直连采集数据卸数阶段", author = "zxz")
public class JdbcDirectUnloadDataStageImpl extends AbstractJobStage {

	private final static Logger LOGGER = LoggerFactory.getLogger(JdbcDirectUnloadDataStageImpl.class);

	private final SourceDataConfBean sourceDataConfBean;
	private final CollectTableBean collectTableBean;

	public JdbcDirectUnloadDataStageImpl(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		this.sourceDataConfBean = sourceDataConfBean;
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据库直连采集卸数阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "" +
			"1.创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间" +
			"2.根据页面填写，获取数库直连采集的sql,meta信息等" +
			"3.结束给stageParamInfo塞值")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null，StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		//开始时间
		long startTime = System.currentTimeMillis();
		//TODO 这边所有的注释都叫数据库抽数
		LOGGER.info("------------------表" + collectTableBean.getTable_name()
				+ "数据库抽数卸数阶段开始------------------");
		//1.创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.UNLOADDATA.getCode());
		try {
			//2.根据页面填写，获取数库直连采集的sql,meta信息等
			TableBean tableBean = CollectTableHandleFactory.getCollectTableHandleInstance(sourceDataConfBean)
					.generateTableInfo(sourceDataConfBean, collectTableBean);
			stageParamInfo.setTableBean(tableBean);
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + collectTableBean.getTable_name()
					+ "数据库直连采集卸数阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error(collectTableBean.getTable_name() + "数据库直连采集卸数阶段失败：", e);
		}
		//3.结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, AgentType.ShuJuKu.getCode());
		return stageParamInfo;
	}


	@Override
	public int getStageCode() {
		return StageConstant.UNLOADDATA.getCode();
	}

}
