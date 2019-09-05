package hrds.control.beans;

import hrds.commons.entity.Etl_job;

public class EtlJobBean extends Etl_job implements Comparable<EtlJobBean>  {

	// 后一批次作业的调度日期
	private String strNextDate;
	// 前一批次作业是否完成flag
	private boolean preDateFlag;
	// 依赖作业完成flag
	private boolean dependencyFlag;
	// 已经完成的依赖作业个数
	private int DoneDependencyJobCount;
	// 作业调度触发时间,仅在调度触发方式为"T"时有效
	private long executeTime;

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

	@Override
	public int compareTo(EtlJobBean o) {
		if (null == o){
			return 0;
		}
		if (super.getJob_priority_curr() > o.getJob_priority_curr()){
			return -1;
		}else if (super.getJob_priority_curr() == o.getJob_priority_curr() ){
			return 0;
		}else{
			return 1;
		}
	}
}
