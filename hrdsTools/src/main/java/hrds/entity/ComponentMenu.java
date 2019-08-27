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
@Table(tableName = "component_menu")
public class ComponentMenu extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "component_menu";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("menu_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String menu_path;
	private String user_type;
	private String comp_id;
	private String menu_remark;
	private BigDecimal menu_id;
	private String menu_name;

	public String getMenu_path() { return menu_path; }
	public void setMenu_path(String menu_path) {
		if(menu_path==null) throw new BusinessException("Entity : ComponentMenu.menu_path must not null!");
		this.menu_path = menu_path;
	}

	public String getUser_type() { return user_type; }
	public void setUser_type(String user_type) {
		if(user_type==null) throw new BusinessException("Entity : ComponentMenu.user_type must not null!");
		this.user_type = user_type;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) throw new BusinessException("Entity : ComponentMenu.comp_id must not null!");
		this.comp_id = comp_id;
	}

	public String getMenu_remark() { return menu_remark; }
	public void setMenu_remark(String menu_remark) {
		if(menu_remark==null) addNullValueField("menu_remark");
		this.menu_remark = menu_remark;
	}

	public BigDecimal getMenu_id() { return menu_id; }
	public void setMenu_id(BigDecimal menu_id) {
		if(menu_id==null) throw new BusinessException("Entity : ComponentMenu.menu_id must not null!");
		this.menu_id = menu_id;
	}

	public String getMenu_name() { return menu_name; }
	public void setMenu_name(String menu_name) {
		if(menu_name==null) throw new BusinessException("Entity : ComponentMenu.menu_name must not null!");
		this.menu_name = menu_name;
	}

}