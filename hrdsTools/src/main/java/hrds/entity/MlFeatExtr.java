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
@Table(tableName = "ml_feat_extr")
public class MlFeatExtr extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_feat_extr";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("featextr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String create_time;
	private BigDecimal featextr_id;
	private String dv_column;
	private String extr_method;
	private String remark;
	private BigDecimal dtable_info_id;
	private BigDecimal k_number;
	private String create_date;
	private BigDecimal percent;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlFeatExtr.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getFeatextr_id() { return featextr_id; }
	public void setFeatextr_id(BigDecimal featextr_id) {
		if(featextr_id==null) throw new BusinessException("Entity : MlFeatExtr.featextr_id must not null!");
		this.featextr_id = featextr_id;
	}

	public String getDv_column() { return dv_column; }
	public void setDv_column(String dv_column) {
		if(dv_column==null) throw new BusinessException("Entity : MlFeatExtr.dv_column must not null!");
		this.dv_column = dv_column;
	}

	public String getExtr_method() { return extr_method; }
	public void setExtr_method(String extr_method) {
		if(extr_method==null) throw new BusinessException("Entity : MlFeatExtr.extr_method must not null!");
		this.extr_method = extr_method;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) addNullValueField("dtable_info_id");
		this.dtable_info_id = dtable_info_id;
	}

	public BigDecimal getK_number() { return k_number; }
	public void setK_number(BigDecimal k_number) {
		if(k_number==null) throw new BusinessException("Entity : MlFeatExtr.k_number must not null!");
		this.k_number = k_number;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlFeatExtr.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getPercent() { return percent; }
	public void setPercent(BigDecimal percent) {
		if(percent==null) throw new BusinessException("Entity : MlFeatExtr.percent must not null!");
		this.percent = percent;
	}

}