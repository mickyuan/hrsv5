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
@Table(tableName = "edw_job")
public class EdwJob extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_job";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("jobcode");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String jobcode;
	private String oper_cycle;
	private String jobtype;
	private String algorithmcode;
	private String job_state;
	private String jobname;
	private String job_template_code;

	public String getJobcode() { return jobcode; }
	public void setJobcode(String jobcode) {
		if(jobcode==null) throw new BusinessException("Entity : EdwJob.jobcode must not null!");
		this.jobcode = jobcode;
	}

	public String getOper_cycle() { return oper_cycle; }
	public void setOper_cycle(String oper_cycle) {
		if(oper_cycle==null) addNullValueField("oper_cycle");
		this.oper_cycle = oper_cycle;
	}

	public String getJobtype() { return jobtype; }
	public void setJobtype(String jobtype) {
		if(jobtype==null) addNullValueField("jobtype");
		this.jobtype = jobtype;
	}

	public String getAlgorithmcode() { return algorithmcode; }
	public void setAlgorithmcode(String algorithmcode) {
		if(algorithmcode==null) throw new BusinessException("Entity : EdwJob.algorithmcode must not null!");
		this.algorithmcode = algorithmcode;
	}

	public String getJob_state() { return job_state; }
	public void setJob_state(String job_state) {
		if(job_state==null) addNullValueField("job_state");
		this.job_state = job_state;
	}

	public String getJobname() { return jobname; }
	public void setJobname(String jobname) {
		if(jobname==null) addNullValueField("jobname");
		this.jobname = jobname;
	}

	public String getJob_template_code() { return job_template_code; }
	public void setJob_template_code(String job_template_code) {
		if(job_template_code==null) addNullValueField("job_template_code");
		this.job_template_code = job_template_code;
	}

}