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
@Table(tableName = "collect_transfer")
public class CollectTransfer extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_transfer";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ct_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal ct_id;
	private BigDecimal collect_set_id;
	private String run_way;
	private BigDecimal file_size_link;
	private BigDecimal agent_id;
	private String collect_type;
	private BigDecimal rely_job_id;
	private BigDecimal failure_count;
	private String remark;
	private String comp_id;
	private String is_parallel;
	private String is_biglink;

	public BigDecimal getCt_id() { return ct_id; }
	public void setCt_id(BigDecimal ct_id) {
		if(ct_id==null) throw new BusinessException("Entity : CollectTransfer.ct_id must not null!");
		this.ct_id = ct_id;
	}

	public BigDecimal getCollect_set_id() { return collect_set_id; }
	public void setCollect_set_id(BigDecimal collect_set_id) {
		if(collect_set_id==null) throw new BusinessException("Entity : CollectTransfer.collect_set_id must not null!");
		this.collect_set_id = collect_set_id;
	}

	public String getRun_way() { return run_way; }
	public void setRun_way(String run_way) {
		if(run_way==null) throw new BusinessException("Entity : CollectTransfer.run_way must not null!");
		this.run_way = run_way;
	}

	public BigDecimal getFile_size_link() { return file_size_link; }
	public void setFile_size_link(BigDecimal file_size_link) {
		if(file_size_link==null) addNullValueField("file_size_link");
		this.file_size_link = file_size_link;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : CollectTransfer.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getCollect_type() { return collect_type; }
	public void setCollect_type(String collect_type) {
		if(collect_type==null) throw new BusinessException("Entity : CollectTransfer.collect_type must not null!");
		this.collect_type = collect_type;
	}

	public BigDecimal getRely_job_id() { return rely_job_id; }
	public void setRely_job_id(BigDecimal rely_job_id) {
		if(rely_job_id==null) addNullValueField("rely_job_id");
		this.rely_job_id = rely_job_id;
	}

	public BigDecimal getFailure_count() { return failure_count; }
	public void setFailure_count(BigDecimal failure_count) {
		if(failure_count==null) addNullValueField("failure_count");
		this.failure_count = failure_count;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) throw new BusinessException("Entity : CollectTransfer.comp_id must not null!");
		this.comp_id = comp_id;
	}

	public String getIs_parallel() { return is_parallel; }
	public void setIs_parallel(String is_parallel) {
		if(is_parallel==null) addNullValueField("is_parallel");
		this.is_parallel = is_parallel;
	}

	public String getIs_biglink() { return is_biglink; }
	public void setIs_biglink(String is_biglink) {
		if(is_biglink==null) addNullValueField("is_biglink");
		this.is_biglink = is_biglink;
	}

}