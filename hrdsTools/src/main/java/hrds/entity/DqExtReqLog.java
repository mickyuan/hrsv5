package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "dq_ext_req_log")
public class DqExtReqLog extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_ext_req_log";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("req_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal req_id;
	private String chk_dt;
	private String chk_time;
	private String fin_tm;
	private String req_tm;
	private String req_re;
	private BigDecimal ext_job_id;
	private BigDecimal task_id;
	private String fin_sts;
	private String ass_req_id;
	private String req_typ;

	public BigDecimal getReq_id() { return req_id; }
	public void setReq_id(BigDecimal req_id) {
		if(req_id==null) throw new BusinessException("Entity : DqExtReqLog.req_id must not null!");
		this.req_id = req_id;
	}

	public String getChk_dt() { return chk_dt; }
	public void setChk_dt(String chk_dt) {
		if(chk_dt==null) throw new BusinessException("Entity : DqExtReqLog.chk_dt must not null!");
		this.chk_dt = chk_dt;
	}

	public String getChk_time() { return chk_time; }
	public void setChk_time(String chk_time) {
		if(chk_time==null) throw new BusinessException("Entity : DqExtReqLog.chk_time must not null!");
		this.chk_time = chk_time;
	}

	public String getFin_tm() { return fin_tm; }
	public void setFin_tm(String fin_tm) {
		if(fin_tm==null) addNullValueField("fin_tm");
		this.fin_tm = fin_tm;
	}

	public String getReq_tm() { return req_tm; }
	public void setReq_tm(String req_tm) {
		if(req_tm==null) addNullValueField("req_tm");
		this.req_tm = req_tm;
	}

	public String getReq_re() { return req_re; }
	public void setReq_re(String req_re) {
		if(req_re==null) addNullValueField("req_re");
		this.req_re = req_re;
	}

	public BigDecimal getExt_job_id() { return ext_job_id; }
	public void setExt_job_id(BigDecimal ext_job_id) {
		if(ext_job_id==null) throw new BusinessException("Entity : DqExtReqLog.ext_job_id must not null!");
		this.ext_job_id = ext_job_id;
	}

	public BigDecimal getTask_id() { return task_id; }
	public void setTask_id(BigDecimal task_id) {
		if(task_id==null) throw new BusinessException("Entity : DqExtReqLog.task_id must not null!");
		this.task_id = task_id;
	}

	public String getFin_sts() { return fin_sts; }
	public void setFin_sts(String fin_sts) {
		if(fin_sts==null) addNullValueField("fin_sts");
		this.fin_sts = fin_sts;
	}

	public String getAss_req_id() { return ass_req_id; }
	public void setAss_req_id(String ass_req_id) {
		if(ass_req_id==null) addNullValueField("ass_req_id");
		this.ass_req_id = ass_req_id;
	}

	public String getReq_typ() { return req_typ; }
	public void setReq_typ(String req_typ) {
		if(req_typ==null) addNullValueField("req_typ");
		this.req_typ = req_typ;
	}

}