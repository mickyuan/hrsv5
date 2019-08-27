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
@Table(tableName = "job_operation")
public class JobOperation extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "job_operation";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("job_rs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String is_again;
	private String process_id;
	private String job_rs_id;
	private String execute_state;
	private String execute_e_time;
	private String execute_length;
	private String comp_id;
	private String etl_date;
	private BigDecimal job_id;
	private String execute_s_date;
	private String task_opertype;
	private String execute_s_time;
	private String pro_opertype;
	private String execute_e_date;

	public String getIs_again() { return is_again; }
	public void setIs_again(String is_again) {
		if(is_again==null) throw new BusinessException("Entity : JobOperation.is_again must not null!");
		this.is_again = is_again;
	}

	public String getProcess_id() { return process_id; }
	public void setProcess_id(String process_id) {
		if(process_id==null) addNullValueField("process_id");
		this.process_id = process_id;
	}

	public String getJob_rs_id() { return job_rs_id; }
	public void setJob_rs_id(String job_rs_id) {
		if(job_rs_id==null) throw new BusinessException("Entity : JobOperation.job_rs_id must not null!");
		this.job_rs_id = job_rs_id;
	}

	public String getExecute_state() { return execute_state; }
	public void setExecute_state(String execute_state) {
		if(execute_state==null) addNullValueField("execute_state");
		this.execute_state = execute_state;
	}

	public String getExecute_e_time() { return execute_e_time; }
	public void setExecute_e_time(String execute_e_time) {
		if(execute_e_time==null) throw new BusinessException("Entity : JobOperation.execute_e_time must not null!");
		this.execute_e_time = execute_e_time;
	}

	public String getExecute_length() { return execute_length; }
	public void setExecute_length(String execute_length) {
		if(execute_length==null) addNullValueField("execute_length");
		this.execute_length = execute_length;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) throw new BusinessException("Entity : JobOperation.comp_id must not null!");
		this.comp_id = comp_id;
	}

	public String getEtl_date() { return etl_date; }
	public void setEtl_date(String etl_date) {
		if(etl_date==null) throw new BusinessException("Entity : JobOperation.etl_date must not null!");
		this.etl_date = etl_date;
	}

	public BigDecimal getJob_id() { return job_id; }
	public void setJob_id(BigDecimal job_id) {
		if(job_id==null) throw new BusinessException("Entity : JobOperation.job_id must not null!");
		this.job_id = job_id;
	}

	public String getExecute_s_date() { return execute_s_date; }
	public void setExecute_s_date(String execute_s_date) {
		if(execute_s_date==null) throw new BusinessException("Entity : JobOperation.execute_s_date must not null!");
		this.execute_s_date = execute_s_date;
	}

	public String getTask_opertype() { return task_opertype; }
	public void setTask_opertype(String task_opertype) {
		if(task_opertype==null) addNullValueField("task_opertype");
		this.task_opertype = task_opertype;
	}

	public String getExecute_s_time() { return execute_s_time; }
	public void setExecute_s_time(String execute_s_time) {
		if(execute_s_time==null) throw new BusinessException("Entity : JobOperation.execute_s_time must not null!");
		this.execute_s_time = execute_s_time;
	}

	public String getPro_opertype() { return pro_opertype; }
	public void setPro_opertype(String pro_opertype) {
		if(pro_opertype==null) addNullValueField("pro_opertype");
		this.pro_opertype = pro_opertype;
	}

	public String getExecute_e_date() { return execute_e_date; }
	public void setExecute_e_date(String execute_e_date) {
		if(execute_e_date==null) throw new BusinessException("Entity : JobOperation.execute_e_date must not null!");
		this.execute_e_date = execute_e_date;
	}

}