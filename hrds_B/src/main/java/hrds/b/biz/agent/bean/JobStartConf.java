package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "", author = "dhw", createdate = "2020/6/16 16:32")
@Table(tableName = "job_start_conf")
public class JobStartConf extends ProjectTableEntity {

	private static final long serialVersionUID = -8515207656817077148L;

	@DocBean(name = "pre_etl_job", value = "上游作业", dataType = String[].class, required = false)
	private String[] pre_etl_job;
	@DocBean(name = "etl_job", value = "作业名:", dataType = String.class, required = false)
	private String etl_job;
	@DocBean(name = "ocs_id", value = "对象采集任务编号:", dataType = Long.class, required = false)
	private Long ocs_id;

	public String getEtl_job() {
		return etl_job;
	}

	public void setEtl_job(String etl_job) {
		this.etl_job = etl_job;
	}

	public Long getOcs_id() {
		return ocs_id;
	}

	public void setOcs_id(Long ocs_id) {
		this.ocs_id = ocs_id;
	}

	public String[] getPre_etl_job() {
		return pre_etl_job;
	}

	public void setPre_etl_job(String[] pre_etl_job) {
		this.pre_etl_job = pre_etl_job;
	}
}
