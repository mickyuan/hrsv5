package hrds.agent.job.biz.utils;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.commons.exception.AppSystemException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

@DocClass(desc = "作业状态处理工具类", author = "zxz", createdate = "2019/12/2 14:19")
public class JobStatusInfoUtil {
	//打印日志
	private static final Log log = LogFactory.getLog(JobStatusInfoUtil.class);

	@Method(desc = "作业开始，获取前一次作业状态", logicStep = "")
	@Param(desc = "作业状态保存路径", name = "statusFilePath", range = "不可为空")
	@Return(desc = "作业状态对象", range = "不会为空")
	public static JobStatusInfo getStartJobStatusInfo(String statusFilePath, String job_id) {
		File file = new File(statusFilePath);
		//JobStatusInfo对象，表示一个作业的状态
		JobStatusInfo jobStatus;
		if (file.exists()) {
			jobStatus = JSONArray.parseObject(FileUtil.readFile2String(file), JobStatusInfo.class);
		} else {
			//不存在创建文件夹
			String parent = file.getParent();
			File parentPath = new File(parent);
			if (!parentPath.exists()) {
				if (!parentPath.mkdirs()) {
					throw new AppSystemException("创建文件夹" + parentPath + "失败！");
				}
			}
			//2.设置作业ID、开始日期和开始时间
			jobStatus = new JobStatusInfo();
			jobStatus.setJobId(job_id);
			jobStatus.setRunStatus(RunStatusConstant.RUNNING.getCode());
			jobStatus.setStartDate(DateUtil.getSysDate());
			jobStatus.setStartTime(DateUtil.getSysTime());
		}
		return jobStatus;
	}

	@Method(desc = "打印每个线程执行情况", logicStep = "打印每个线程执行情况")
	public static void printJobStatusInfo(List<Future<JobStatusInfo>> statusInfoFutureList) throws Exception {
		//打印每个线程执行情况
		for (Future<JobStatusInfo> statusInfoFuture : statusInfoFutureList) {
			JobStatusInfo jobStatusInfo = statusInfoFuture.get();
			if (jobStatusInfo.getUnloadDataStatus() == null || RunStatusConstant.FAILED.getCode()
					== jobStatusInfo.getUnloadDataStatus().getStatusCode()) {
				throw new AppSystemException("卸数执行失败");
			} else {
				log.info("卸数执行成功");
			}
			if (jobStatusInfo.getUploadStatus() == null || RunStatusConstant.FAILED.getCode()
					== jobStatusInfo.getUploadStatus().getStatusCode()) {
				throw new AppSystemException("上传执行失败");
			} else {
				log.info("上传执行成功");
			}
			if (jobStatusInfo.getDataLodingStatus() == null || RunStatusConstant.FAILED.getCode()
					== jobStatusInfo.getDataLodingStatus().getStatusCode()) {
				throw new AppSystemException("加载执行失败");
			} else {
				log.info("加载执行成功");
			}
			if (jobStatusInfo.getCalIncrementStatus() == null || RunStatusConstant.FAILED.getCode()
					== jobStatusInfo.getCalIncrementStatus().getStatusCode()) {
				throw new AppSystemException("增量执行失败");
			} else {
				log.info("增量执行成功");
			}
			if (jobStatusInfo.getDataRegistrationStatus() == null || RunStatusConstant.FAILED.getCode()
					== jobStatusInfo.getDataRegistrationStatus().getStatusCode()) {
				throw new AppSystemException("登记执行失败");
			} else {
				log.info("登记执行成功");
			}
		}
	}

	public static void startStageStatusInfo(StageStatusInfo stageStatusInfo, String job_id,
	                                        int stageNameCode) {
		stageStatusInfo.setStageNameCode(stageNameCode);
		stageStatusInfo.setJobId(job_id);
		stageStatusInfo.setStartDate(DateUtil.getSysDate());
		stageStatusInfo.setStartTime(DateUtil.getSysTime());
	}

	public static void endStageStatusInfo(StageStatusInfo stageStatusInfo, int statusCode, String message) {
		stageStatusInfo.setMessage(message);
		stageStatusInfo.setStatusCode(statusCode);
		stageStatusInfo.setEndDate(DateUtil.getSysDate());
		stageStatusInfo.setEndTime(DateUtil.getSysTime());
	}

	public static void endStageParamInfo(StageParamInfo stageParamInfo, StageStatusInfo stageStatusInfo,
	                                     CollectTableBean collectTableBean, String collectType) {
		stageParamInfo.setStatusInfo(stageStatusInfo);
		stageParamInfo.setAgentId(collectTableBean.getAgent_id());
		stageParamInfo.setSourceId(collectTableBean.getSource_id());
		stageParamInfo.setCollectSetId(Long.parseLong(collectTableBean.getDatabase_id()));
		stageParamInfo.setTaskClassify(collectTableBean.getTable_name());
		stageParamInfo.setCollectType(collectType);
		stageParamInfo.setEtlDate(collectTableBean.getEtlDate());
	}
}
