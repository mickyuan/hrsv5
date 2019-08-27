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
@Table(tableName = "collect_reduce")
public class CollectReduce extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_reduce";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal collect_set_id;
	private BigDecimal cr_id;
	private BigDecimal agent_id;
	private BigDecimal rely_job_id;
	private String remark;
	private String is_md5check;
	private String comp_id;
	private String reduce_scope;
	private BigDecimal file_size;
	private String is_encrypt;
	private String run_way;
	private String is_reduce;
	private String collect_type;

	public BigDecimal getCollect_set_id() { return collect_set_id; }
	public void setCollect_set_id(BigDecimal collect_set_id) {
		if(collect_set_id==null) throw new BusinessException("Entity : CollectReduce.collect_set_id must not null!");
		this.collect_set_id = collect_set_id;
	}

	public BigDecimal getCr_id() { return cr_id; }
	public void setCr_id(BigDecimal cr_id) {
		if(cr_id==null) throw new BusinessException("Entity : CollectReduce.cr_id must not null!");
		this.cr_id = cr_id;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : CollectReduce.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getRely_job_id() { return rely_job_id; }
	public void setRely_job_id(BigDecimal rely_job_id) {
		if(rely_job_id==null) addNullValueField("rely_job_id");
		this.rely_job_id = rely_job_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_md5check() { return is_md5check; }
	public void setIs_md5check(String is_md5check) {
		if(is_md5check==null) throw new BusinessException("Entity : CollectReduce.is_md5check must not null!");
		this.is_md5check = is_md5check;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) throw new BusinessException("Entity : CollectReduce.comp_id must not null!");
		this.comp_id = comp_id;
	}

	public String getReduce_scope() { return reduce_scope; }
	public void setReduce_scope(String reduce_scope) {
		if(reduce_scope==null) addNullValueField("reduce_scope");
		this.reduce_scope = reduce_scope;
	}

	public BigDecimal getFile_size() { return file_size; }
	public void setFile_size(BigDecimal file_size) {
		if(file_size==null) addNullValueField("file_size");
		this.file_size = file_size;
	}

	public String getIs_encrypt() { return is_encrypt; }
	public void setIs_encrypt(String is_encrypt) {
		if(is_encrypt==null) throw new BusinessException("Entity : CollectReduce.is_encrypt must not null!");
		this.is_encrypt = is_encrypt;
	}

	public String getRun_way() { return run_way; }
	public void setRun_way(String run_way) {
		if(run_way==null) throw new BusinessException("Entity : CollectReduce.run_way must not null!");
		this.run_way = run_way;
	}

	public String getIs_reduce() { return is_reduce; }
	public void setIs_reduce(String is_reduce) {
		if(is_reduce==null) throw new BusinessException("Entity : CollectReduce.is_reduce must not null!");
		this.is_reduce = is_reduce;
	}

	public String getCollect_type() { return collect_type; }
	public void setCollect_type(String collect_type) {
		if(collect_type==null) throw new BusinessException("Entity : CollectReduce.collect_type must not null!");
		this.collect_type = collect_type;
	}

}