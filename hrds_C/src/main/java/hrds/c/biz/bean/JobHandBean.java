package hrds.c.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "job_hand")
public class JobHandBean extends ProjectTableEntity {

	private static final long serialVersionUID = -3688380256036681819L;

	@DocBean(name = "etl_job", value = "作业名:", dataType = String.class, required = false)
	private String etl_job;
	@DocBean(name = "etl_sys_cd", value = "工程代码:", dataType = String.class, required = false)
	private String etl_sys_cd;
	@DocBean(name = "etl_hand_type", value = "干预类型(Meddle_type):GR-分组级续跑<GRP_RESUME> GP-分组级暂停<GRP_PAUSE> GO-分组级重跑，从源头开始<GRP_ORIGINAL> JT-作业直接跑<JOB_TRIGGER> JS-作业停止<JOB_STOP> JR-作业重跑<JOB_RERUN> JP-作业临时调整优先级<JOB_PRIORITY> JJ-作业跳过<JOB_JUMP> SF-系统日切<SYS_SHIFT> SS-系统停止<SYS_STOP> SP-系统级暂停<SYS_PAUSE> SO-系统级重跑，从源头开始<SYS_ORIGINAL> SR-系统级续跑<SYS_RESUME> ", dataType = String.class, required = false)
	private String etl_hand_type;
	@DocBean(name = "curr_bath_date", value = "批量日期：yyyyMMdd", dataType = String.class, required = false)
	private String curr_bath_date;

	public String getEtl_job() {
		return etl_job;
	}

	public void setEtl_job(String etl_job) {
		this.etl_job = etl_job;
	}

	public String getEtl_sys_cd() {
		return etl_sys_cd;
	}

	public void setEtl_sys_cd(String etl_sys_cd) {
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getEtl_hand_type() {
		return etl_hand_type;
	}

	public void setEtl_hand_type(String etl_hand_type) {
		this.etl_hand_type = etl_hand_type;
	}

	public String getCurr_bath_date() {
		return curr_bath_date;
	}

	public void setCurr_bath_date(String curr_bath_date) {
		this.curr_bath_date = curr_bath_date;
	}
}
