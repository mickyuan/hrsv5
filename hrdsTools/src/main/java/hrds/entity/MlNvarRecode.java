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
@Table(tableName = "ml_nvar_recode")
public class MlNvarRecode extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_nvar_recode";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("recode_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String norm_range;
	private String recode_mode;
	private String numevar_column;
	private String create_time;
	private String disc_info;
	private BigDecimal recode_id;
	private String newcolumn_name;
	private String remark;
	private BigDecimal dtable_info_id;
	private String create_date;

	public String getNorm_range() { return norm_range; }
	public void setNorm_range(String norm_range) {
		if(norm_range==null) addNullValueField("norm_range");
		this.norm_range = norm_range;
	}

	public String getRecode_mode() { return recode_mode; }
	public void setRecode_mode(String recode_mode) {
		if(recode_mode==null) throw new BusinessException("Entity : MlNvarRecode.recode_mode must not null!");
		this.recode_mode = recode_mode;
	}

	public String getNumevar_column() { return numevar_column; }
	public void setNumevar_column(String numevar_column) {
		if(numevar_column==null) throw new BusinessException("Entity : MlNvarRecode.numevar_column must not null!");
		this.numevar_column = numevar_column;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlNvarRecode.create_time must not null!");
		this.create_time = create_time;
	}

	public String getDisc_info() { return disc_info; }
	public void setDisc_info(String disc_info) {
		if(disc_info==null) addNullValueField("disc_info");
		this.disc_info = disc_info;
	}

	public BigDecimal getRecode_id() { return recode_id; }
	public void setRecode_id(BigDecimal recode_id) {
		if(recode_id==null) throw new BusinessException("Entity : MlNvarRecode.recode_id must not null!");
		this.recode_id = recode_id;
	}

	public String getNewcolumn_name() { return newcolumn_name; }
	public void setNewcolumn_name(String newcolumn_name) {
		if(newcolumn_name==null) throw new BusinessException("Entity : MlNvarRecode.newcolumn_name must not null!");
		this.newcolumn_name = newcolumn_name;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlNvarRecode.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlNvarRecode.create_date must not null!");
		this.create_date = create_date;
	}

}