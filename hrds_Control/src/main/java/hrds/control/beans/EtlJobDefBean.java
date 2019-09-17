package hrds.control.beans;

import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_job_resource_rela;

import java.util.List;

/**
 * @ClassName: EtlJobDefBean
 * @Description: 用于表述调度作业的实体，该类继承于Etl_job_def类
 * @Author: Tiger.Wang
 * @Date: 2019/9/2 15:55
 * @Since: JDK 1.8
 **/
public class EtlJobDefBean extends Etl_job_def {

	private List<Etl_job_resource_rela> jobResources;

	public EtlJobDefBean() {
		super();
	}

	public List<Etl_job_resource_rela> getJobResources() {

		return jobResources;
	}

	public void setJobResources(List<Etl_job_resource_rela> jobResources) {

		this.jobResources = jobResources;
	}
}
