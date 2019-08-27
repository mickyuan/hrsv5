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
@Table(tableName = "ml_t_hypothesis_test")
public class MlTHypothesisTest extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_t_hypothesis_test";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("t_hypotest_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String two_sampcs;
	private String create_time;
	private String two_sampcf;
	private String indesamp_style;
	private BigDecimal t_hypotest_id;
	private BigDecimal singsamp_mean;
	private String remark;
	private BigDecimal dtable_info_id;
	private String singaamp_colu;
	private String t_hypo_type;
	private String create_date;

	public String getTwo_sampcs() { return two_sampcs; }
	public void setTwo_sampcs(String two_sampcs) {
		if(two_sampcs==null) addNullValueField("two_sampcs");
		this.two_sampcs = two_sampcs;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlTHypothesisTest.create_time must not null!");
		this.create_time = create_time;
	}

	public String getTwo_sampcf() { return two_sampcf; }
	public void setTwo_sampcf(String two_sampcf) {
		if(two_sampcf==null) addNullValueField("two_sampcf");
		this.two_sampcf = two_sampcf;
	}

	public String getIndesamp_style() { return indesamp_style; }
	public void setIndesamp_style(String indesamp_style) {
		if(indesamp_style==null) addNullValueField("indesamp_style");
		this.indesamp_style = indesamp_style;
	}

	public BigDecimal getT_hypotest_id() { return t_hypotest_id; }
	public void setT_hypotest_id(BigDecimal t_hypotest_id) {
		if(t_hypotest_id==null) throw new BusinessException("Entity : MlTHypothesisTest.t_hypotest_id must not null!");
		this.t_hypotest_id = t_hypotest_id;
	}

	public BigDecimal getSingsamp_mean() { return singsamp_mean; }
	public void setSingsamp_mean(BigDecimal singsamp_mean) {
		if(singsamp_mean==null) addNullValueField("singsamp_mean");
		this.singsamp_mean = singsamp_mean;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlTHypothesisTest.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getSingaamp_colu() { return singaamp_colu; }
	public void setSingaamp_colu(String singaamp_colu) {
		if(singaamp_colu==null) addNullValueField("singaamp_colu");
		this.singaamp_colu = singaamp_colu;
	}

	public String getT_hypo_type() { return t_hypo_type; }
	public void setT_hypo_type(String t_hypo_type) {
		if(t_hypo_type==null) throw new BusinessException("Entity : MlTHypothesisTest.t_hypo_type must not null!");
		this.t_hypo_type = t_hypo_type;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlTHypothesisTest.create_date must not null!");
		this.create_date = create_date;
	}

}