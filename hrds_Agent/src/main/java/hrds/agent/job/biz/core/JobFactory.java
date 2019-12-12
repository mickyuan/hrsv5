package hrds.agent.job.biz.core;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobParamBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.constant.JobCollectTypeConstant;

@DocClass(desc = "作业工厂类，用于创建不同的作业实例", author = "WangZhengcheng")
public class JobFactory {

	private JobFactory() {
	}

	@Method(desc = "创建作业实例", logicStep = "" +
			"1、判断采集作业类型" +
			"2、根据作业类型创建具体的JobInterface实例")
	@Param(name = "jobInfo", desc = "包含一个作业的相关信息", range = "JobInfo实体类对象，不为空")
	@Param(name = "dbConfig", desc = "包含数据库连接信息，仅供数据库直连采集作业使用", range = "DBConfigBean实体类对象，不为空")
	@Param(name = "jobParam", desc = "包含该作业的相关参数", range = "JobParamBean实体类对象，不为空")
	@Param(name = "statusFilePath", desc = "该作业状态文件的路径", range = "不为空")
	@Param(name = "jobStatus", desc = "存放该作业的状态信息", range = "JobStatusInfo实体类对象，不为空")
	@Return(desc = "JobInterface实例，也就是每个采集作业的构造实例", range = "JobInterface接口的实现类对象，不会为null")
	public static JobInterface newInstance(CollectTableBean collectTableBean, SourceDataConfBean sourceDataConfBean,
	                                       JobParamBean jobParam, String statusFilePath, JobStatusInfo jobStatus) {

		JobInterface job;
		//1、判断采集作业类型
		String collectType = jobParam.getCollect_type();
		//TODO 下面的枚举要换成hrds-commons里面的代码项，并且判断枚举要用==
		/*if (JobCollectTypeConstant.DATA_FILE_COLLECTION.equals(collectType)) {
			job = new DataFileJobImpl(jobInfo, jobParam, statusFilePath, jobStatus);
		} else*/ if (JobCollectTypeConstant.DB_COLLECTION.equals(collectType)) {
			job = new DataBaseJobImpl(sourceDataConfBean, collectTableBean);
		} else {
			throw new IllegalArgumentException("还未支持的采集类型：" + collectType);
		}

		return job;
	}
}
