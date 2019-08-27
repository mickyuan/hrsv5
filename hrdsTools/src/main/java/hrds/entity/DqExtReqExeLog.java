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
@Table(tableName = "dq_ext_req_exe_log")
public class DqExtReqExeLog extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_ext_req_exe_log";

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
	private BigDecimal task_id;
	private String dl_time;

	public BigDecimal getReq_id() { return req_id; }
	public void setReq_id(BigDecimal req_id) {
		if(req_id==null) throw new BusinessException("Entity : DqExtReqExeLog.req_id must not null!");
		this.req_id = req_id;
	}

	public BigDecimal getTask_id() { return task_id; }
	public void setTask_id(BigDecimal task_id) {
		if(task_id==null) throw new BusinessException("Entity : DqExtReqExeLog.task_id must not null!");
		this.task_id = task_id;
	}

	public String getDl_time() { return dl_time; }
	public void setDl_time(String dl_time) {
		if(dl_time==null) throw new BusinessException("Entity : DqExtReqExeLog.dl_time must not null!");
		this.dl_time = dl_time;
	}

}