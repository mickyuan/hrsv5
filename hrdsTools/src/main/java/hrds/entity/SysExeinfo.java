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
@Table(tableName = "sys_exeinfo")
public class SysExeinfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_exeinfo";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("exe_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String execute_state;
	private String st_date;
	private BigDecimal agent_id;
	private BigDecimal exe_id;
	private String exe_parameter;
	private String ed_date;
	private String job_tablename;
	private String etl_date;
	private String job_name;
	private BigDecimal database_id;
	private String is_valid;
	private String err_info;
	private BigDecimal source_id;

	public String getExecute_state() { return execute_state; }
	public void setExecute_state(String execute_state) {
		if(execute_state==null) throw new BusinessException("Entity : SysExeinfo.execute_state must not null!");
		this.execute_state = execute_state;
	}

	public String getSt_date() { return st_date; }
	public void setSt_date(String st_date) {
		if(st_date==null) throw new BusinessException("Entity : SysExeinfo.st_date must not null!");
		this.st_date = st_date;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : SysExeinfo.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getExe_id() { return exe_id; }
	public void setExe_id(BigDecimal exe_id) {
		if(exe_id==null) throw new BusinessException("Entity : SysExeinfo.exe_id must not null!");
		this.exe_id = exe_id;
	}

	public String getExe_parameter() { return exe_parameter; }
	public void setExe_parameter(String exe_parameter) {
		if(exe_parameter==null) throw new BusinessException("Entity : SysExeinfo.exe_parameter must not null!");
		this.exe_parameter = exe_parameter;
	}

	public String getEd_date() { return ed_date; }
	public void setEd_date(String ed_date) {
		if(ed_date==null) throw new BusinessException("Entity : SysExeinfo.ed_date must not null!");
		this.ed_date = ed_date;
	}

	public String getJob_tablename() { return job_tablename; }
	public void setJob_tablename(String job_tablename) {
		if(job_tablename==null) addNullValueField("job_tablename");
		this.job_tablename = job_tablename;
	}

	public String getEtl_date() { return etl_date; }
	public void setEtl_date(String etl_date) {
		if(etl_date==null) throw new BusinessException("Entity : SysExeinfo.etl_date must not null!");
		this.etl_date = etl_date;
	}

	public String getJob_name() { return job_name; }
	public void setJob_name(String job_name) {
		if(job_name==null) throw new BusinessException("Entity : SysExeinfo.job_name must not null!");
		this.job_name = job_name;
	}

	public BigDecimal getDatabase_id() { return database_id; }
	public void setDatabase_id(BigDecimal database_id) {
		if(database_id==null) throw new BusinessException("Entity : SysExeinfo.database_id must not null!");
		this.database_id = database_id;
	}

	public String getIs_valid() { return is_valid; }
	public void setIs_valid(String is_valid) {
		if(is_valid==null) throw new BusinessException("Entity : SysExeinfo.is_valid must not null!");
		this.is_valid = is_valid;
	}

	public String getErr_info() { return err_info; }
	public void setErr_info(String err_info) {
		if(err_info==null) throw new BusinessException("Entity : SysExeinfo.err_info must not null!");
		this.err_info = err_info;
	}

	public BigDecimal getSource_id() { return source_id; }
	public void setSource_id(BigDecimal source_id) {
		if(source_id==null) throw new BusinessException("Entity : SysExeinfo.source_id must not null!");
		this.source_id = source_id;
	}

}