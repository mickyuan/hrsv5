package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.Obj_relation_etl;
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
	@DocBean(name = "obj_relation_etl", value = "对象作业关系实体", dataType = Obj_relation_etl.class,
			required = false)
	private Obj_relation_etl obj_relation_etl;

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

	public Obj_relation_etl getObj_relation_etl() {
		return obj_relation_etl;
	}

	public void setObj_relation_etl(Obj_relation_etl obj_relation_etl) {
		this.obj_relation_etl = obj_relation_etl;
	}

	public String[] getPre_etl_job() {
		return pre_etl_job;
	}

	public void setPre_etl_job(String[] pre_etl_job) {
		this.pre_etl_job = pre_etl_job;
	}
}
