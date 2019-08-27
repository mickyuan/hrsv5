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
@Table(tableName = "interface_use")
public class InterfaceUse extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "interface_use";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("interface_use_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String interface_note;
	private String user_name;
	private String classify_name;
	private String interface_code;
	private String interface_name;
	private String their_type;
	private String url;
	private String start_use_date;
	private String use_state;
	private BigDecimal interface_id;
	private BigDecimal create_id;
	private BigDecimal user_id;
	private String use_valid_date;
	private BigDecimal interface_use_id;

	public String getInterface_note() { return interface_note; }
	public void setInterface_note(String interface_note) {
		if(interface_note==null) addNullValueField("interface_note");
		this.interface_note = interface_note;
	}

	public String getUser_name() { return user_name; }
	public void setUser_name(String user_name) {
		if(user_name==null) throw new BusinessException("Entity : InterfaceUse.user_name must not null!");
		this.user_name = user_name;
	}

	public String getClassify_name() { return classify_name; }
	public void setClassify_name(String classify_name) {
		if(classify_name==null) addNullValueField("classify_name");
		this.classify_name = classify_name;
	}

	public String getInterface_code() { return interface_code; }
	public void setInterface_code(String interface_code) {
		if(interface_code==null) throw new BusinessException("Entity : InterfaceUse.interface_code must not null!");
		this.interface_code = interface_code;
	}

	public String getInterface_name() { return interface_name; }
	public void setInterface_name(String interface_name) {
		if(interface_name==null) throw new BusinessException("Entity : InterfaceUse.interface_name must not null!");
		this.interface_name = interface_name;
	}

	public String getTheir_type() { return their_type; }
	public void setTheir_type(String their_type) {
		if(their_type==null) throw new BusinessException("Entity : InterfaceUse.their_type must not null!");
		this.their_type = their_type;
	}

	public String getUrl() { return url; }
	public void setUrl(String url) {
		if(url==null) throw new BusinessException("Entity : InterfaceUse.url must not null!");
		this.url = url;
	}

	public String getStart_use_date() { return start_use_date; }
	public void setStart_use_date(String start_use_date) {
		if(start_use_date==null) throw new BusinessException("Entity : InterfaceUse.start_use_date must not null!");
		this.start_use_date = start_use_date;
	}

	public String getUse_state() { return use_state; }
	public void setUse_state(String use_state) {
		if(use_state==null) throw new BusinessException("Entity : InterfaceUse.use_state must not null!");
		this.use_state = use_state;
	}

	public BigDecimal getInterface_id() { return interface_id; }
	public void setInterface_id(BigDecimal interface_id) {
		if(interface_id==null) throw new BusinessException("Entity : InterfaceUse.interface_id must not null!");
		this.interface_id = interface_id;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : InterfaceUse.create_id must not null!");
		this.create_id = create_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : InterfaceUse.user_id must not null!");
		this.user_id = user_id;
	}

	public String getUse_valid_date() { return use_valid_date; }
	public void setUse_valid_date(String use_valid_date) {
		if(use_valid_date==null) throw new BusinessException("Entity : InterfaceUse.use_valid_date must not null!");
		this.use_valid_date = use_valid_date;
	}

	public BigDecimal getInterface_use_id() { return interface_use_id; }
	public void setInterface_use_id(BigDecimal interface_use_id) {
		if(interface_use_id==null) throw new BusinessException("Entity : InterfaceUse.interface_use_id must not null!");
		this.interface_use_id = interface_use_id;
	}

}