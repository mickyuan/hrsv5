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
	@DocBean(name = "pro_dic", value = "作业程序目录", dataType = String.class, required = false)
	private String pro_dic;
	@DocBean(name = "log_dic", value = "日志目录", dataType = String.class, required = false)
	private String log_dic;
	@DocBean(name = "etl_sys_cd", value = "工程代码:", dataType = String.class, required = false)
	private String etl_sys_cd;
	@DocBean(name = "etl_job", value = "作业名:", dataType = String.class, required = false)
	private String etl_job;
	@DocBean(name = "sub_sys_cd", value = "子系统代码:", dataType = String.class, required = false)
	private String sub_sys_cd;
	@DocBean(name = "ocs_id", value = "对象采集任务编号:", dataType = Long.class, required = false)
	private Long ocs_id;

	public String getEtl_sys_cd() {
		return etl_sys_cd;
	}

	public void setEtl_sys_cd(String etl_sys_cd) {
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getEtl_job() {
		return etl_job;
	}

	public void setEtl_job(String etl_job) {
		this.etl_job = etl_job;
	}

	public String getSub_sys_cd() {
		return sub_sys_cd;
	}

	public void setSub_sys_cd(String sub_sys_cd) {
		this.sub_sys_cd = sub_sys_cd;
	}

	public Long getOcs_id() {
		return ocs_id;
	}

	public void setOcs_id(Long ocs_id) {
		this.ocs_id = ocs_id;
	}

	public String getPro_dic() {
		return pro_dic;
	}

	public void setPro_dic(String pro_dic) {
		this.pro_dic = pro_dic;
	}

	public String getLog_dic() {
		return log_dic;
	}

	public void setLog_dic(String log_dic) {
		this.log_dic = log_dic;
	}

	public String[] getPre_etl_job() {
		return pre_etl_job;
	}

	public void setPre_etl_job(String[] pre_etl_job) {
		this.pre_etl_job = pre_etl_job;
	}
}
