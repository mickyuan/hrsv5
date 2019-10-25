package hrds.trigger.beans;

import hrds.commons.entity.Etl_job_cur;

/**
 * ClassName: EtlJobParaAnaly<br>
 * Description: 用于对redis中数据解析后封装的类。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 13:52<br>
 * Since: JDK 1.8
 **/
public class EtlJobParaAnaly {

	private Etl_job_cur etlJobCur;  //表示一个已登记的作业
	private boolean hasHandle;  //作业是否是干预触发的
	private boolean hasEtlJob;  //作业是否有效

	public Etl_job_cur getEtlJobCur() {
		return etlJobCur;
	}

	public void setEtlJobCur(Etl_job_cur etlJobCur) {
		this.etlJobCur = etlJobCur;
	}

	public boolean isHasHandle() {
		return hasHandle;
	}

	public void setHasHandle(boolean hasHandle) {
		this.hasHandle = hasHandle;
	}

	public boolean isHasEtlJob() {
		return hasEtlJob;
	}

	public void setHasEtlJob(boolean hasEtlJob) {
		this.hasEtlJob = hasEtlJob;
	}
}
