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
@Table(tableName = "cb_preaggregate")
public class CbPreaggregate extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "cb_preaggregate";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("agg_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String agg_time;
	private String agg_status;
	private BigDecimal datatable_id;
	private String agg_sql;
	private BigDecimal agg_id;
	private String remark;
	private String agg_name;
	private String agg_date;

	public String getAgg_time() { return agg_time; }
	public void setAgg_time(String agg_time) {
		if(agg_time==null) throw new BusinessException("Entity : CbPreaggregate.agg_time must not null!");
		this.agg_time = agg_time;
	}

	public String getAgg_status() { return agg_status; }
	public void setAgg_status(String agg_status) {
		if(agg_status==null) addNullValueField("agg_status");
		this.agg_status = agg_status;
	}

	public BigDecimal getDatatable_id() { return datatable_id; }
	public void setDatatable_id(BigDecimal datatable_id) {
		if(datatable_id==null) throw new BusinessException("Entity : CbPreaggregate.datatable_id must not null!");
		this.datatable_id = datatable_id;
	}

	public String getAgg_sql() { return agg_sql; }
	public void setAgg_sql(String agg_sql) {
		if(agg_sql==null) throw new BusinessException("Entity : CbPreaggregate.agg_sql must not null!");
		this.agg_sql = agg_sql;
	}

	public BigDecimal getAgg_id() { return agg_id; }
	public void setAgg_id(BigDecimal agg_id) {
		if(agg_id==null) throw new BusinessException("Entity : CbPreaggregate.agg_id must not null!");
		this.agg_id = agg_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getAgg_name() { return agg_name; }
	public void setAgg_name(String agg_name) {
		if(agg_name==null) throw new BusinessException("Entity : CbPreaggregate.agg_name must not null!");
		this.agg_name = agg_name;
	}

	public String getAgg_date() { return agg_date; }
	public void setAgg_date(String agg_date) {
		if(agg_date==null) throw new BusinessException("Entity : CbPreaggregate.agg_date must not null!");
		this.agg_date = agg_date;
	}

}