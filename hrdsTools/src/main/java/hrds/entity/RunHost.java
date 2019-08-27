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
@Table(tableName = "run_host")
public class RunHost extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "run_host";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("run_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String runhost_remark;
	private BigDecimal run_id;
	private BigDecimal big_jobcount;
	private String run_host_ip;
	private String longest_runtime;

	public String getRunhost_remark() { return runhost_remark; }
	public void setRunhost_remark(String runhost_remark) {
		if(runhost_remark==null) addNullValueField("runhost_remark");
		this.runhost_remark = runhost_remark;
	}

	public BigDecimal getRun_id() { return run_id; }
	public void setRun_id(BigDecimal run_id) {
		if(run_id==null) throw new BusinessException("Entity : RunHost.run_id must not null!");
		this.run_id = run_id;
	}

	public BigDecimal getBig_jobcount() { return big_jobcount; }
	public void setBig_jobcount(BigDecimal big_jobcount) {
		if(big_jobcount==null) throw new BusinessException("Entity : RunHost.big_jobcount must not null!");
		this.big_jobcount = big_jobcount;
	}

	public String getRun_host_ip() { return run_host_ip; }
	public void setRun_host_ip(String run_host_ip) {
		if(run_host_ip==null) throw new BusinessException("Entity : RunHost.run_host_ip must not null!");
		this.run_host_ip = run_host_ip;
	}

	public String getLongest_runtime() { return longest_runtime; }
	public void setLongest_runtime(String longest_runtime) {
		if(longest_runtime==null) throw new BusinessException("Entity : RunHost.longest_runtime must not null!");
		this.longest_runtime = longest_runtime;
	}

}