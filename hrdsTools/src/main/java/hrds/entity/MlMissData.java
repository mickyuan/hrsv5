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
@Table(tableName = "ml_miss_data")
public class MlMissData extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_miss_data";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("missvalue_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal k_num;
	private String create_time;
	private String missdata_column;
	private BigDecimal missvalue_id;
	private String newcolumn_name;
	private String remark;
	private BigDecimal dtable_info_id;
	private String missproc_type;
	private String create_date;
	private String missvalue_proc;

	public BigDecimal getK_num() { return k_num; }
	public void setK_num(BigDecimal k_num) {
		if(k_num==null) addNullValueField("k_num");
		this.k_num = k_num;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlMissData.create_time must not null!");
		this.create_time = create_time;
	}

	public String getMissdata_column() { return missdata_column; }
	public void setMissdata_column(String missdata_column) {
		if(missdata_column==null) throw new BusinessException("Entity : MlMissData.missdata_column must not null!");
		this.missdata_column = missdata_column;
	}

	public BigDecimal getMissvalue_id() { return missvalue_id; }
	public void setMissvalue_id(BigDecimal missvalue_id) {
		if(missvalue_id==null) throw new BusinessException("Entity : MlMissData.missvalue_id must not null!");
		this.missvalue_id = missvalue_id;
	}

	public String getNewcolumn_name() { return newcolumn_name; }
	public void setNewcolumn_name(String newcolumn_name) {
		if(newcolumn_name==null) throw new BusinessException("Entity : MlMissData.newcolumn_name must not null!");
		this.newcolumn_name = newcolumn_name;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlMissData.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getMissproc_type() { return missproc_type; }
	public void setMissproc_type(String missproc_type) {
		if(missproc_type==null) addNullValueField("missproc_type");
		this.missproc_type = missproc_type;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlMissData.create_date must not null!");
		this.create_date = create_date;
	}

	public String getMissvalue_proc() { return missvalue_proc; }
	public void setMissvalue_proc(String missvalue_proc) {
		if(missvalue_proc==null) throw new BusinessException("Entity : MlMissData.missvalue_proc must not null!");
		this.missvalue_proc = missvalue_proc;
	}

}