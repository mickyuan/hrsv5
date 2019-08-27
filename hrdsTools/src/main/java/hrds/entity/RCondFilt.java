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
@Table(tableName = "r_cond_filt")
public class RCondFilt extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "r_cond_filt";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("filter_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String cond_filter;
	private BigDecimal create_id;
	private String create_date;
	private String create_time;
	private BigDecimal filter_id;
	private BigDecimal report_id;

	public String getCond_filter() { return cond_filter; }
	public void setCond_filter(String cond_filter) {
		if(cond_filter==null) throw new BusinessException("Entity : RCondFilt.cond_filter must not null!");
		this.cond_filter = cond_filter;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : RCondFilt.create_id must not null!");
		this.create_id = create_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : RCondFilt.create_date must not null!");
		this.create_date = create_date;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : RCondFilt.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getFilter_id() { return filter_id; }
	public void setFilter_id(BigDecimal filter_id) {
		if(filter_id==null) throw new BusinessException("Entity : RCondFilt.filter_id must not null!");
		this.filter_id = filter_id;
	}

	public BigDecimal getReport_id() { return report_id; }
	public void setReport_id(BigDecimal report_id) {
		if(report_id==null) throw new BusinessException("Entity : RCondFilt.report_id must not null!");
		this.report_id = report_id;
	}

}