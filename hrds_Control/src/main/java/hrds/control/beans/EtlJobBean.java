package hrds.control.beans;

import hrds.commons.entity.Etl_job_cur;

public class EtlJobBean extends Etl_job_cur implements Comparable<EtlJobBean>  {

	//后一批次作业的调度日期
	private String strNextDate;
	//前一批次作业是否完成flag
	private boolean preDateFlag;
	//依赖作业完成flag
	private boolean dependencyFlag;
	//已经完成的依赖作业个数
	private int DoneDependencyJobCount;
	//作业调度触发时间,仅在调度触发方式为"T"时有效
	private long executeTime;
	//作业开始被调度的时间
	private long jobStartTime;

	public EtlJobBean() {
		super();
	}

	public String getStrNextDate() {
		return strNextDate;
	}

	public void setStrNextDate(String strNextDate) {
		this.strNextDate = strNextDate;
	}

	public boolean isPreDateFlag() {
		return preDateFlag;
	}

	public void setPreDateFlag(boolean preDateFlag) {
		this.preDateFlag = preDateFlag;
	}

	public boolean isDependencyFlag() {
		return dependencyFlag;
	}

	public void setDependencyFlag(boolean dependencyFlag) {
		this.dependencyFlag = dependencyFlag;
	}

	public int getDoneDependencyJobCount() {
		return DoneDependencyJobCount;
	}

	public void setDoneDependencyJobCount(int doneDependencyJobCount) {
		DoneDependencyJobCount = doneDependencyJobCount;
	}

	public long getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(long executeTime) {
		this.executeTime = executeTime;
	}

	public long getJobStartTime() {
		return jobStartTime;
	}

	public void setJobStartTime(long jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	@Override
	public int compareTo(EtlJobBean otherEtlJob) {
		if (null == otherEtlJob){
			return 0;
			//FIXME 当前对象不空，去和一个NULL做比较返回相等，为什么。
			// 因为该方法用于对象数组的排序。
			// 逻辑上讲，为null的对象不应该参与比较，但为了'万一'的情况出现了null，则不做任何动作。
		}
		//FIXME 为什么对象比较用的是这一个字段？
		// 1、重写该方法用于对象数组的排序；2、对象间的比较总要有个依据，
		// 而这个字段在业务角度表示为一个作业的优先级，所以使用这个字段。
		return otherEtlJob.getJob_priority_curr().compareTo(super.getJob_priority_curr());
	}
}
