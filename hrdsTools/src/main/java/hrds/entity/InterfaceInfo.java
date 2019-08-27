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
@Table(tableName = "interface_info")
public class InterfaceInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "interface_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("interface_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String interface_state;
	private String interface_note;
	private BigDecimal interface_id;
	private BigDecimal user_id;
	private String interface_code;
	private String interface_name;
	private String interface_type;
	private String url;

	public String getInterface_state() { return interface_state; }
	public void setInterface_state(String interface_state) {
		if(interface_state==null) throw new BusinessException("Entity : InterfaceInfo.interface_state must not null!");
		this.interface_state = interface_state;
	}

	public String getInterface_note() { return interface_note; }
	public void setInterface_note(String interface_note) {
		if(interface_note==null) addNullValueField("interface_note");
		this.interface_note = interface_note;
	}

	public BigDecimal getInterface_id() { return interface_id; }
	public void setInterface_id(BigDecimal interface_id) {
		if(interface_id==null) throw new BusinessException("Entity : InterfaceInfo.interface_id must not null!");
		this.interface_id = interface_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : InterfaceInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getInterface_code() { return interface_code; }
	public void setInterface_code(String interface_code) {
		if(interface_code==null) throw new BusinessException("Entity : InterfaceInfo.interface_code must not null!");
		this.interface_code = interface_code;
	}

	public String getInterface_name() { return interface_name; }
	public void setInterface_name(String interface_name) {
		if(interface_name==null) throw new BusinessException("Entity : InterfaceInfo.interface_name must not null!");
		this.interface_name = interface_name;
	}

	public String getInterface_type() { return interface_type; }
	public void setInterface_type(String interface_type) {
		if(interface_type==null) throw new BusinessException("Entity : InterfaceInfo.interface_type must not null!");
		this.interface_type = interface_type;
	}

	public String getUrl() { return url; }
	public void setUrl(String url) {
		if(url==null) throw new BusinessException("Entity : InterfaceInfo.url must not null!");
		this.url = url;
	}

}