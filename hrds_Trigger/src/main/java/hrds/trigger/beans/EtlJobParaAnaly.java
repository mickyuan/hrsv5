package hrds.trigger.beans;

import hrds.commons.entity.Etl_job_cur;

/**
 * ClassName: EtlJobParaAnaly<br>
 * Description: <br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 13:52<br>
 * Since: JDK 1.8
 **/
public class EtlJobParaAnaly {

	private Etl_job_cur etlJobCur;
	private boolean hasHandle;
	private boolean hasEtlJob;

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
