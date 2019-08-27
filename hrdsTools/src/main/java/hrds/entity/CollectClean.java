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
@Table(tableName = "collect_clean")
public class CollectClean extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_clean";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cc_id");
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
	private String run_way;
	private BigDecimal agent_id;
	private String collect_type;
	private BigDecimal rely_job_id;
	private String cc_remark;
	private BigDecimal cc_id;
	private String is_filling_column;
	private String comp_id;
	private String is_del_escape;
	private String is_del_space;
	private String is_special_escape;

	public BigDecimal getCollect_set_id() { return collect_set_id; }
	public void setCollect_set_id(BigDecimal collect_set_id) {
		if(collect_set_id==null) throw new BusinessException("Entity : CollectClean.collect_set_id must not null!");
		this.collect_set_id = collect_set_id;
	}

	public String getRun_way() { return run_way; }
	public void setRun_way(String run_way) {
		if(run_way==null) throw new BusinessException("Entity : CollectClean.run_way must not null!");
		this.run_way = run_way;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : CollectClean.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getCollect_type() { return collect_type; }
	public void setCollect_type(String collect_type) {
		if(collect_type==null) throw new BusinessException("Entity : CollectClean.collect_type must not null!");
		this.collect_type = collect_type;
	}

	public BigDecimal getRely_job_id() { return rely_job_id; }
	public void setRely_job_id(BigDecimal rely_job_id) {
		if(rely_job_id==null) addNullValueField("rely_job_id");
		this.rely_job_id = rely_job_id;
	}

	public String getCc_remark() { return cc_remark; }
	public void setCc_remark(String cc_remark) {
		if(cc_remark==null) addNullValueField("cc_remark");
		this.cc_remark = cc_remark;
	}

	public BigDecimal getCc_id() { return cc_id; }
	public void setCc_id(BigDecimal cc_id) {
		if(cc_id==null) throw new BusinessException("Entity : CollectClean.cc_id must not null!");
		this.cc_id = cc_id;
	}

	public String getIs_filling_column() { return is_filling_column; }
	public void setIs_filling_column(String is_filling_column) {
		if(is_filling_column==null) throw new BusinessException("Entity : CollectClean.is_filling_column must not null!");
		this.is_filling_column = is_filling_column;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) throw new BusinessException("Entity : CollectClean.comp_id must not null!");
		this.comp_id = comp_id;
	}

	public String getIs_del_escape() { return is_del_escape; }
	public void setIs_del_escape(String is_del_escape) {
		if(is_del_escape==null) throw new BusinessException("Entity : CollectClean.is_del_escape must not null!");
		this.is_del_escape = is_del_escape;
	}

	public String getIs_del_space() { return is_del_space; }
	public void setIs_del_space(String is_del_space) {
		if(is_del_space==null) throw new BusinessException("Entity : CollectClean.is_del_space must not null!");
		this.is_del_space = is_del_space;
	}

	public String getIs_special_escape() { return is_special_escape; }
	public void setIs_special_escape(String is_special_escape) {
		if(is_special_escape==null) throw new BusinessException("Entity : CollectClean.is_special_escape must not null!");
		this.is_special_escape = is_special_escape;
	}

}