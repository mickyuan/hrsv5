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
	 * 1、判断采集作业类型
	 * 2、根据作业类型创建具体的JobInterface实例
	 *
	 * @Param: jobInfo JobInfo
	 *         含义：包含一个作业的相关信息
	 *         取值范围：JobInfo实体类对象，不为空
	 *
	 * @Param: dbConfig DBConfigBean
	 *         含义：包含数据库连接信息，仅供数据库直连采集作业使用
	 *         取值范围：DBConfigBean实体类对象，不为空
	 *
	 * @Param: jobParam JobParamBean
	 *         含义：包含该作业的相关参数
	 *         取值范围：JobParamBean实体类对象，不为空
	 *
	 * @Param: statusFilePath String
	 *         含义：该作业状态文件的路径
	 *         取值范围：不为空
	 *
	 * @Param: jobStatus JobStatusInfo
	 *         含义：存放该作业的状态信息
	 *         取值范围：JobStatusInfo实体类对象，不为空
	 *
	 * @return: JobInterface
	 *          含义：JobInterface实例，也就是每个采集作业的构造实例
	 *          取值范围：JobInterface接口的实现类对象，不会为null
	 *
	 * */
	public static JobInterface newInstance(JobInfo jobInfo, DBConfigBean dbConfig,
	                                       JobParamBean jobParam, String statusFilePath, JobStatusInfo jobStatus) {

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
