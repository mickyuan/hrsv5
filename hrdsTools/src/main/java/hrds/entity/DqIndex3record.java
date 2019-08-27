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
@Table(tableName = "dq_index3record")
public class DqIndex3record extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_index3record";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("record_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal record_id;
	private String table_col;
	private BigDecimal table_size;
	private String file_path;
	private String dqc_ts;
	private String record_date;
	private String file_type;
	private String task_id;
	private String table_name;
	private String record_time;

	public BigDecimal getRecord_id() { return record_id; }
	public void setRecord_id(BigDecimal record_id) {
		if(record_id==null) throw new BusinessException("Entity : DqIndex3record.record_id must not null!");
		this.record_id = record_id;
	}

	public String getTable_col() { return table_col; }
	public void setTable_col(String table_col) {
		if(table_col==null) throw new BusinessException("Entity : DqIndex3record.table_col must not null!");
		this.table_col = table_col;
	}

	public BigDecimal getTable_size() { return table_size; }
	public void setTable_size(BigDecimal table_size) {
		if(table_size==null) throw new BusinessException("Entity : DqIndex3record.table_size must not null!");
		this.table_size = table_size;
	}

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) throw new BusinessException("Entity : DqIndex3record.file_path must not null!");
		this.file_path = file_path;
	}

	public String getDqc_ts() { return dqc_ts; }
	public void setDqc_ts(String dqc_ts) {
		if(dqc_ts==null) throw new BusinessException("Entity : DqIndex3record.dqc_ts must not null!");
		this.dqc_ts = dqc_ts;
	}

	public String getRecord_date() { return record_date; }
	public void setRecord_date(String record_date) {
		if(record_date==null) throw new BusinessException("Entity : DqIndex3record.record_date must not null!");
		this.record_date = record_date;
	}

	public String getFile_type() { return file_type; }
	public void setFile_type(String file_type) {
		if(file_type==null) throw new BusinessException("Entity : DqIndex3record.file_type must not null!");
		this.file_type = file_type;
	}

	public String getTask_id() { return task_id; }
	public void setTask_id(String task_id) {
		if(task_id==null) throw new BusinessException("Entity : DqIndex3record.task_id must not null!");
		this.task_id = task_id;
	}

	public String getTable_name() { return table_name; }
	public void setTable_name(String table_name) {
		if(table_name==null) throw new BusinessException("Entity : DqIndex3record.table_name must not null!");
		this.table_name = table_name;
	}

	public String getRecord_time() { return record_time; }
	public void setRecord_time(String record_time) {
		if(record_time==null) throw new BusinessException("Entity : DqIndex3record.record_time must not null!");
		this.record_time = record_time;
	}

}