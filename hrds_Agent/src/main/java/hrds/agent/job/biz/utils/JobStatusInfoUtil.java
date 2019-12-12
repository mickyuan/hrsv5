package hrds.agent.job.biz.utils;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.core.FtpCollectJobImpl;
import hrds.commons.exception.AppSystemException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
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
			String jobInfo;
			try {
				jobInfo = FileUtil.readFile2String(file);
			} catch (IOException e) {
				throw new AppSystemException("读取文件失败！" + e.getMessage());
			}
			jobStatus = JSONArray.parseObject(jobInfo, JobStatusInfo.class);
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
			jobStatus.setStartDate(DateUtil.getSysDate());
			jobStatus.setStartTime(DateUtil.getSysTime());
		}
		return jobStatus;
	}

	@Method(desc = "打印每个线程执行情况", logicStep = "打印每个线程执行情况")
	public static void printJobStatusInfo(List<Future<JobStatusInfo>> statusInfoFutureList) {
		//打印每个线程执行情况
		for (Future<JobStatusInfo> statusInfoFuture : statusInfoFutureList) {
			try {
				JobStatusInfo jobStatusInfo = statusInfoFuture.get();
				log.info("作业执行情况：" + jobStatusInfo.toString());
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
}
