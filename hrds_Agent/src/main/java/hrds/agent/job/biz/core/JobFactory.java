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
	 * @param jobInfo        表示一个作业, 取值范围：JobInfo对象
	 * @param dbConfig       表示数据库连接信息, 取值范围：DBConfigBean对象
	 * @param jobParam       表示作业参数, 取值范围：JobParamBean对象
	 * @param statusFilePath 用于更新运行时状态, 取值范围：作业状态文件地址
	 * @param jobStatus      表示一个作业的状态, 取值范围：JobStatusInfo对象
	 * @return com.beyondsoft.agent.core.job.JobInterface
	 * @author 13616
	 * 步骤：
	 *      1、判断采集作业类型
	 *      2、根据作业类型创建具体的JobInterface实例
	 */
	public static JobInterface newInstance(JobInfo jobInfo, DBConfigBean dbConfig, JobParamBean jobParam, String statusFilePath, JobStatusInfo jobStatus) {

		JobInterface job;
		//1、判断采集作业类型
		String collectType = jobParam.getCollect_type();
		//TODO 下面的枚举要换成hrds-commons里面的代码项，并且判断枚举要用==
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
