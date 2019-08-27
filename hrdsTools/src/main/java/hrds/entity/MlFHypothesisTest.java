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
@Table(tableName = "ml_f_hypothesis_test")
public class MlFHypothesisTest extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_f_hypothesis_test";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("f_hypotest_id");
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
	private BigDecimal f_hypotest_id;
	private String f_column_first;
	private String remark;
	private BigDecimal dtable_info_id;
	private String f_column_second;
	private String create_date;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlFHypothesisTest.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getF_hypotest_id() { return f_hypotest_id; }
	public void setF_hypotest_id(BigDecimal f_hypotest_id) {
		if(f_hypotest_id==null) throw new BusinessException("Entity : MlFHypothesisTest.f_hypotest_id must not null!");
		this.f_hypotest_id = f_hypotest_id;
	}

	public String getF_column_first() { return f_column_first; }
	public void setF_column_first(String f_column_first) {
		if(f_column_first==null) throw new BusinessException("Entity : MlFHypothesisTest.f_column_first must not null!");
		this.f_column_first = f_column_first;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlFHypothesisTest.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getF_column_second() { return f_column_second; }
	public void setF_column_second(String f_column_second) {
		if(f_column_second==null) throw new BusinessException("Entity : MlFHypothesisTest.f_column_second must not null!");
		this.f_column_second = f_column_second;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlFHypothesisTest.create_date must not null!");
		this.create_date = create_date;
	}

}