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
@Table(tableName = "ml_project_status")
public class MlProjectStatus extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_project_status";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("status_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String update_time;
	private BigDecimal status_id;
	private BigDecimal project_id;
	private String end_is_flag;
	private String project_status;
	private String start_is_flag;
	private String update_date;

	public String getUpdate_time() { return update_time; }
	public void setUpdate_time(String update_time) {
		if(update_time==null) throw new BusinessException("Entity : MlProjectStatus.update_time must not null!");
		this.update_time = update_time;
	}

	public BigDecimal getStatus_id() { return status_id; }
	public void setStatus_id(BigDecimal status_id) {
		if(status_id==null) throw new BusinessException("Entity : MlProjectStatus.status_id must not null!");
		this.status_id = status_id;
	}

	public BigDecimal getProject_id() { return project_id; }
	public void setProject_id(BigDecimal project_id) {
		if(project_id==null) throw new BusinessException("Entity : MlProjectStatus.project_id must not null!");
		this.project_id = project_id;
	}

	public String getEnd_is_flag() { return end_is_flag; }
	public void setEnd_is_flag(String end_is_flag) {
		if(end_is_flag==null) throw new BusinessException("Entity : MlProjectStatus.end_is_flag must not null!");
		this.end_is_flag = end_is_flag;
	}

	public String getProject_status() { return project_status; }
	public void setProject_status(String project_status) {
		if(project_status==null) throw new BusinessException("Entity : MlProjectStatus.project_status must not null!");
		this.project_status = project_status;
	}

	public String getStart_is_flag() { return start_is_flag; }
	public void setStart_is_flag(String start_is_flag) {
		if(start_is_flag==null) throw new BusinessException("Entity : MlProjectStatus.start_is_flag must not null!");
		this.start_is_flag = start_is_flag;
	}

	public String getUpdate_date() { return update_date; }
	public void setUpdate_date(String update_date) {
		if(update_date==null) throw new BusinessException("Entity : MlProjectStatus.update_date must not null!");
		this.update_date = update_date;
	}

}