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
@Table(tableName = "auto_comp_group")
public class AutoCompGroup extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_comp_group";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("component_group_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String last_update_time;
	private BigDecimal component_id;
	private String update_user;
	private String create_time;
	private BigDecimal component_group_id;
	private String column_name;
	private String create_user;
	private String create_date;
	private String last_update_date;

	public String getLast_update_time() { return last_update_time; }
	public void setLast_update_time(String last_update_time) {
		if(last_update_time==null) addNullValueField("last_update_time");
		this.last_update_time = last_update_time;
	}

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) addNullValueField("component_id");
		this.component_id = component_id;
	}

	public String getUpdate_user() { return update_user; }
	public void setUpdate_user(String update_user) {
		if(update_user==null) addNullValueField("update_user");
		this.update_user = update_user;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : AutoCompGroup.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getComponent_group_id() { return component_group_id; }
	public void setComponent_group_id(BigDecimal component_group_id) {
		if(component_group_id==null) throw new BusinessException("Entity : AutoCompGroup.component_group_id must not null!");
		this.component_group_id = component_group_id;
	}

	public String getColumn_name() { return column_name; }
	public void setColumn_name(String column_name) {
		if(column_name==null) throw new BusinessException("Entity : AutoCompGroup.column_name must not null!");
		this.column_name = column_name;
	}

	public String getCreate_user() { return create_user; }
	public void setCreate_user(String create_user) {
		if(create_user==null) addNullValueField("create_user");
		this.create_user = create_user;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : AutoCompGroup.create_date must not null!");
		this.create_date = create_date;
	}

	public String getLast_update_date() { return last_update_date; }
	public void setLast_update_date(String last_update_date) {
		if(last_update_date==null) addNullValueField("last_update_date");
		this.last_update_date = last_update_date;
	}

}