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
@Table(tableName = "ml_dreduction")
public class MlDreduction extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_dreduction";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dimeredu_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String kbtest_is_flag;
	private BigDecimal extrfactor_num;
	private String serep_is_flag;
	private String create_time;
	private String dimer_method;
	private String remark;
	private BigDecimal dtable_info_id;
	private String create_date;
	private BigDecimal dimeredu_id;

	public String getKbtest_is_flag() { return kbtest_is_flag; }
	public void setKbtest_is_flag(String kbtest_is_flag) {
		if(kbtest_is_flag==null) addNullValueField("kbtest_is_flag");
		this.kbtest_is_flag = kbtest_is_flag;
	}

	public BigDecimal getExtrfactor_num() { return extrfactor_num; }
	public void setExtrfactor_num(BigDecimal extrfactor_num) {
		if(extrfactor_num==null) addNullValueField("extrfactor_num");
		this.extrfactor_num = extrfactor_num;
	}

	public String getSerep_is_flag() { return serep_is_flag; }
	public void setSerep_is_flag(String serep_is_flag) {
		if(serep_is_flag==null) addNullValueField("serep_is_flag");
		this.serep_is_flag = serep_is_flag;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlDreduction.create_time must not null!");
		this.create_time = create_time;
	}

	public String getDimer_method() { return dimer_method; }
	public void setDimer_method(String dimer_method) {
		if(dimer_method==null) throw new BusinessException("Entity : MlDreduction.dimer_method must not null!");
		this.dimer_method = dimer_method;
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

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlDreduction.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getDimeredu_id() { return dimeredu_id; }
	public void setDimeredu_id(BigDecimal dimeredu_id) {
		if(dimeredu_id==null) throw new BusinessException("Entity : MlDreduction.dimeredu_id must not null!");
		this.dimeredu_id = dimeredu_id;
	}

}