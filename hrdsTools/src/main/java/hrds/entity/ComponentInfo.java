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
@Table(tableName = "component_info")
public class ComponentInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "component_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("comp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String color_info;
	private String comp_state;
	private String comp_name;
	private String comp_type;
	private String comp_remark;
	private String comp_id;
	private String comp_version;
	private String icon_info;

	public String getColor_info() { return color_info; }
	public void setColor_info(String color_info) {
		if(color_info==null) addNullValueField("color_info");
		this.color_info = color_info;
	}

	public String getComp_state() { return comp_state; }
	public void setComp_state(String comp_state) {
		if(comp_state==null) throw new BusinessException("Entity : ComponentInfo.comp_state must not null!");
		this.comp_state = comp_state;
	}

	public String getComp_name() { return comp_name; }
	public void setComp_name(String comp_name) {
		if(comp_name==null) throw new BusinessException("Entity : ComponentInfo.comp_name must not null!");
		this.comp_name = comp_name;
	}

	public String getComp_type() { return comp_type; }
	public void setComp_type(String comp_type) {
		if(comp_type==null) addNullValueField("comp_type");
		this.comp_type = comp_type;
	}

	public String getComp_remark() { return comp_remark; }
	public void setComp_remark(String comp_remark) {
		if(comp_remark==null) addNullValueField("comp_remark");
		this.comp_remark = comp_remark;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) throw new BusinessException("Entity : ComponentInfo.comp_id must not null!");
		this.comp_id = comp_id;
	}

	public String getComp_version() { return comp_version; }
	public void setComp_version(String comp_version) {
		if(comp_version==null) throw new BusinessException("Entity : ComponentInfo.comp_version must not null!");
		this.comp_version = comp_version;
	}

	public String getIcon_info() { return icon_info; }
	public void setIcon_info(String icon_info) {
		if(icon_info==null) addNullValueField("icon_info");
		this.icon_info = icon_info;
	}

}