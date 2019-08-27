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
@Table(tableName = "auto_tp_cond_info")
public class AutoTpCondInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_tp_cond_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("template_cond_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String pre_value;
	private String value_type;
	private String value_size;
	private String cond_cn_column;
	private String con_row;
	private String cond_para_name;
	private String show_type;
	private String ci_sp_name;
	private String is_required;
	private String is_dept_id;
	private String ci_sp_class;
	private BigDecimal template_cond_id;
	private BigDecimal template_id;
	private String con_relation;
	private String cond_en_column;

	public String getPre_value() { return pre_value; }
	public void setPre_value(String pre_value) {
		if(pre_value==null) addNullValueField("pre_value");
		this.pre_value = pre_value;
	}

	public String getValue_type() { return value_type; }
	public void setValue_type(String value_type) {
		if(value_type==null) addNullValueField("value_type");
		this.value_type = value_type;
	}

	public String getValue_size() { return value_size; }
	public void setValue_size(String value_size) {
		if(value_size==null) addNullValueField("value_size");
		this.value_size = value_size;
	}

	public String getCond_cn_column() { return cond_cn_column; }
	public void setCond_cn_column(String cond_cn_column) {
		if(cond_cn_column==null) addNullValueField("cond_cn_column");
		this.cond_cn_column = cond_cn_column;
	}

	public String getCon_row() { return con_row; }
	public void setCon_row(String con_row) {
		if(con_row==null) addNullValueField("con_row");
		this.con_row = con_row;
	}

	public String getCond_para_name() { return cond_para_name; }
	public void setCond_para_name(String cond_para_name) {
		if(cond_para_name==null) addNullValueField("cond_para_name");
		this.cond_para_name = cond_para_name;
	}

	public String getShow_type() { return show_type; }
	public void setShow_type(String show_type) {
		if(show_type==null) addNullValueField("show_type");
		this.show_type = show_type;
	}

	public String getCi_sp_name() { return ci_sp_name; }
	public void setCi_sp_name(String ci_sp_name) {
		if(ci_sp_name==null) addNullValueField("ci_sp_name");
		this.ci_sp_name = ci_sp_name;
	}

	public String getIs_required() { return is_required; }
	public void setIs_required(String is_required) {
		if(is_required==null) addNullValueField("is_required");
		this.is_required = is_required;
	}

	public String getIs_dept_id() { return is_dept_id; }
	public void setIs_dept_id(String is_dept_id) {
		if(is_dept_id==null) addNullValueField("is_dept_id");
		this.is_dept_id = is_dept_id;
	}

	public String getCi_sp_class() { return ci_sp_class; }
	public void setCi_sp_class(String ci_sp_class) {
		if(ci_sp_class==null) addNullValueField("ci_sp_class");
		this.ci_sp_class = ci_sp_class;
	}

	public BigDecimal getTemplate_cond_id() { return template_cond_id; }
	public void setTemplate_cond_id(BigDecimal template_cond_id) {
		if(template_cond_id==null) throw new BusinessException("Entity : AutoTpCondInfo.template_cond_id must not null!");
		this.template_cond_id = template_cond_id;
	}

	public BigDecimal getTemplate_id() { return template_id; }
	public void setTemplate_id(BigDecimal template_id) {
		if(template_id==null) throw new BusinessException("Entity : AutoTpCondInfo.template_id must not null!");
		this.template_id = template_id;
	}

	public String getCon_relation() { return con_relation; }
	public void setCon_relation(String con_relation) {
		if(con_relation==null) addNullValueField("con_relation");
		this.con_relation = con_relation;
	}

	public String getCond_en_column() { return cond_en_column; }
	public void setCond_en_column(String cond_en_column) {
		if(cond_en_column==null) addNullValueField("cond_en_column");
		this.cond_en_column = cond_en_column;
	}

}