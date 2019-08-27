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
@Table(tableName = "dq_ext_job_rule_rela")
public class DqExtJobRuleRela extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_ext_job_rule_rela";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ext_job_id");
		__tmpPKS.add("task_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal ext_job_id;
	private BigDecimal task_id;
	private String dl_time;

	public BigDecimal getExt_job_id() { return ext_job_id; }
	public void setExt_job_id(BigDecimal ext_job_id) {
		if(ext_job_id==null) throw new BusinessException("Entity : DqExtJobRuleRela.ext_job_id must not null!");
		this.ext_job_id = ext_job_id;
	}

	public BigDecimal getTask_id() { return task_id; }
	public void setTask_id(BigDecimal task_id) {
		if(task_id==null) throw new BusinessException("Entity : DqExtJobRuleRela.task_id must not null!");
		this.task_id = task_id;
	}

	public String getDl_time() { return dl_time; }
	public void setDl_time(String dl_time) {
		if(dl_time==null) throw new BusinessException("Entity : DqExtJobRuleRela.dl_time must not null!");
		this.dl_time = dl_time;
	}

}