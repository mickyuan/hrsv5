package hrds.agent.job.biz.core;


import hrds.agent.job.biz.bean.JobStatusInfo;

/**
 * ClassName: JobInterface <br/>
 * Function: 采集作业顶层接口. <br/>
 * Reason: 数据库直连,DB文件,半结构化文件
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public interface JobInterface extends MetaInfoInterface {
	/**
	 * 作业执行接口
	 *
	 * @return com.beyondsoft.agent.beans.JobStatusInfo，用于表示作业状态
	 * @author 13616
	 * @date 2019/8/7 11:59
	 */
	JobStatusInfo runJob();
}

