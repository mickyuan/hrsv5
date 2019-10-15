package hrds.agent.job.biz.core.dfstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.utils.DateUtil;
import hrds.agent.job.biz.utils.ScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DocClass(desc = "数据文件采集，数据上传阶段实现", author = "WangZhengcheng")
public class DFUploadStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DFUploadStageImpl.class);

	private final String jobId;
	private final String localFile;
	private final String remoteDir;

	/**
	 * 数据文件采集，数据上传阶段实现
	 *
	 * @param jobId     作业编号
	 * @param localFile 本地文件路径
	 * @param remoteDir hdfs文件路径
	 * @author 13616
	 * @date 2019/8/7 11:48
	 */
	public DFUploadStageImpl(String jobId, String localFile, String remoteDir) {
		this.jobId = jobId;
		this.localFile = localFile;
		this.remoteDir = remoteDir;
	}

	@Method(desc = "数据文件采集，数据上传阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageStatusInfo handleStage() {

		StageStatusInfo statusInfo = new StageStatusInfo();
		statusInfo.setStageNameCode(StageConstant.UPLOAD.getCode());
		statusInfo.setJobId(jobId);
		statusInfo.setStartDate(DateUtil.getLocalDateByChar8());
		statusInfo.setStartTime(DateUtil.getLocalTimeByChar6());
		RunStatusConstant status = RunStatusConstant.SUCCEED;
		ScriptExecutor executor = new ScriptExecutor();
		try {
			executor.executeUpload2Hdfs(localFile, remoteDir);
		} catch (IllegalStateException | InterruptedException e) {
			status = RunStatusConstant.FAILED;
			statusInfo.setMessage(FAILD_MSG + "：" + e.getMessage());
			LOGGER.error(FAILD_MSG + "：{}", e.getMessage());
		}

		statusInfo.setStatusCode(status.getCode());
		statusInfo.setEndDate(DateUtil.getLocalDateByChar8());
		statusInfo.setEndTime(DateUtil.getLocalTimeByChar6());
		return statusInfo;
	}
}
