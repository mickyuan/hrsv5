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
@Table(tableName = "auto_tp_res_set")
public class AutoTpResSet extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_tp_res_set";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("template_res_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String dese_rule;
	private String column_cn_name;
	private String create_time;
	private String source_table_name;
	private BigDecimal template_res_id;
	private String res_show_column;
	private String last_update_time;
	private String update_user;
	private String column_en_name;
	private BigDecimal template_id;
	private String create_user;
	private String column_type;
	private String create_date;
	private String is_dese;
	private String last_update_date;

	public String getDese_rule() { return dese_rule; }
	public void setDese_rule(String dese_rule) {
		if(dese_rule==null) addNullValueField("dese_rule");
		this.dese_rule = dese_rule;
	}

	public String getColumn_cn_name() { return column_cn_name; }
	public void setColumn_cn_name(String column_cn_name) {
		if(column_cn_name==null) addNullValueField("column_cn_name");
		this.column_cn_name = column_cn_name;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) addNullValueField("create_time");
		this.create_time = create_time;
	}

	public String getSource_table_name() { return source_table_name; }
	public void setSource_table_name(String source_table_name) {
		if(source_table_name==null) addNullValueField("source_table_name");
		this.source_table_name = source_table_name;
	}

	public BigDecimal getTemplate_res_id() { return template_res_id; }
	public void setTemplate_res_id(BigDecimal template_res_id) {
		if(template_res_id==null) throw new BusinessException("Entity : AutoTpResSet.template_res_id must not null!");
		this.template_res_id = template_res_id;
	}

	public String getRes_show_column() { return res_show_column; }
	public void setRes_show_column(String res_show_column) {
		if(res_show_column==null) addNullValueField("res_show_column");
		this.res_show_column = res_show_column;
	}

	public String getLast_update_time() { return last_update_time; }
	public void setLast_update_time(String last_update_time) {
		if(last_update_time==null) addNullValueField("last_update_time");
		this.last_update_time = last_update_time;
	}

	public String getUpdate_user() { return update_user; }
	public void setUpdate_user(String update_user) {
		if(update_user==null) addNullValueField("update_user");
		this.update_user = update_user;
	}

	public String getColumn_en_name() { return column_en_name; }
	public void setColumn_en_name(String column_en_name) {
		if(column_en_name==null) addNullValueField("column_en_name");
		this.column_en_name = column_en_name;
	}

	public BigDecimal getTemplate_id() { return template_id; }
	public void setTemplate_id(BigDecimal template_id) {
		if(template_id==null) throw new BusinessException("Entity : AutoTpResSet.template_id must not null!");
		this.template_id = template_id;
	}

	public String getCreate_user() { return create_user; }
	public void setCreate_user(String create_user) {
		if(create_user==null) addNullValueField("create_user");
		this.create_user = create_user;
	}

	public String getColumn_type() { return column_type; }
	public void setColumn_type(String column_type) {
		if(column_type==null) addNullValueField("column_type");
		this.column_type = column_type;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) addNullValueField("create_date");
		this.create_date = create_date;
	}

	public String getIs_dese() { return is_dese; }
	public void setIs_dese(String is_dese) {
		if(is_dese==null) addNullValueField("is_dese");
		this.is_dese = is_dese;
	}

	public String getLast_update_date() { return last_update_date; }
	public void setLast_update_date(String last_update_date) {
		if(last_update_date==null) addNullValueField("last_update_date");
		this.last_update_date = last_update_date;
	}

}