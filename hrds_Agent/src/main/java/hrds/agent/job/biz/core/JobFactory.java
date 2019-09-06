package hrds.agent.job.biz.core;

import hrds.agent.job.biz.bean.DBConfigBean;
import hrds.agent.job.biz.bean.JobInfo;
import hrds.agent.job.biz.bean.JobParamBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.constant.JobCollectTypeConstant;

/**
 * ClassName: JobFactory <br/>
 * Function: 作业工厂类. <br/>
 * Reason: 用于创建不同的作业实例. <br/>
 * Date: 2019/8/5 14:13 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
public class JobFactory {

	private JobFactory() {
	}

	/**
	 * 创建作业实例
	 *
	 * @param jobInfo        JobInfo对象，表示一个作业
	 * @param dbConfig       DBConfigBean对象，表示数据库连接信息
	 * @param jobParam       JobParamBean对象，表示作业参数
	 * @param statusFilePath 作业状态文件地址，用于更新运行时状态
	 * @param jobStatus      JobStatusInfo对象，表示一个作业的状态
	 * @return com.beyondsoft.agent.core.job.JobInterface
	 * @author 13616
	 * @date 2019/8/7 11:55
	 */
	public static JobInterface newInstance(JobInfo jobInfo, DBConfigBean dbConfig, JobParamBean jobParam, String statusFilePath, JobStatusInfo jobStatus) {

		JobInterface job;
		//判断采集类型
		String collectType = jobParam.getCollect_type();
		if (JobCollectTypeConstant.DATA_FILE_COLLECTION.equals(collectType)) {
			job = new DataFileJobImpl(jobInfo, jobParam, statusFilePath, jobStatus);
		} else if (JobCollectTypeConstant.DB_COLLECTION.equals(collectType)) {
			job = new DataBaseJobImpl(jobInfo, dbConfig, statusFilePath, jobStatus);
		} else {
			throw new IllegalArgumentException("还未支持的采集类型：" + collectType);
		}

		return job;
	}
}
