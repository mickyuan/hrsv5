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
@Table(tableName = "ml_cvar_recode")
public class MlCvarRecode extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_cvar_recode";

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

	private String create_time;
	private String tagencode_info;
	private BigDecimal recode_id;
	private String remark;
	private BigDecimal dtable_info_id;
	private String create_date;
	private String catevar_column;
	private String catevar_code;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlCvarRecode.create_time must not null!");
		this.create_time = create_time;
	}

	public String getTagencode_info() { return tagencode_info; }
	public void setTagencode_info(String tagencode_info) {
		if(tagencode_info==null) addNullValueField("tagencode_info");
		this.tagencode_info = tagencode_info;
	}

	public BigDecimal getRecode_id() { return recode_id; }
	public void setRecode_id(BigDecimal recode_id) {
		if(recode_id==null) throw new BusinessException("Entity : MlCvarRecode.recode_id must not null!");
		this.recode_id = recode_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlCvarRecode.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlCvarRecode.create_date must not null!");
		this.create_date = create_date;
	}

	public String getCatevar_column() { return catevar_column; }
	public void setCatevar_column(String catevar_column) {
		if(catevar_column==null) throw new BusinessException("Entity : MlCvarRecode.catevar_column must not null!");
		this.catevar_column = catevar_column;
	}

	public String getCatevar_code() { return catevar_code; }
	public void setCatevar_code(String catevar_code) {
		if(catevar_code==null) throw new BusinessException("Entity : MlCvarRecode.catevar_code must not null!");
		this.catevar_code = catevar_code;
	}

}