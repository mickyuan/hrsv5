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
@Table(tableName = "interface_use_log")
public class InterfaceUseLog extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "interface_use_log";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("log_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String remoteaddr;
	private BigDecimal log_id;
	private String request_stime;
	private String request_type;
	private String user_name;
	private String browser_type;
	private String interface_name;
	private String request_etime;
	private String request_state;
	private String protocol;
	private String system_type;
	private BigDecimal user_id;
	private String request_info;
	private String request_mode;
	private BigDecimal response_time;
	private BigDecimal interface_use_id;
	private String browser_version;

	public String getRemoteaddr() { return remoteaddr; }
	public void setRemoteaddr(String remoteaddr) {
		if(remoteaddr==null) addNullValueField("remoteaddr");
		this.remoteaddr = remoteaddr;
	}

	public BigDecimal getLog_id() { return log_id; }
	public void setLog_id(BigDecimal log_id) {
		if(log_id==null) throw new BusinessException("Entity : InterfaceUseLog.log_id must not null!");
		this.log_id = log_id;
	}

	public String getRequest_stime() { return request_stime; }
	public void setRequest_stime(String request_stime) {
		if(request_stime==null) addNullValueField("request_stime");
		this.request_stime = request_stime;
	}

	public String getRequest_type() { return request_type; }
	public void setRequest_type(String request_type) {
		if(request_type==null) addNullValueField("request_type");
		this.request_type = request_type;
	}

	public String getUser_name() { return user_name; }
	public void setUser_name(String user_name) {
		if(user_name==null) addNullValueField("user_name");
		this.user_name = user_name;
	}

	public String getBrowser_type() { return browser_type; }
	public void setBrowser_type(String browser_type) {
		if(browser_type==null) addNullValueField("browser_type");
		this.browser_type = browser_type;
	}

	public String getInterface_name() { return interface_name; }
	public void setInterface_name(String interface_name) {
		if(interface_name==null) throw new BusinessException("Entity : InterfaceUseLog.interface_name must not null!");
		this.interface_name = interface_name;
	}

	public String getRequest_etime() { return request_etime; }
	public void setRequest_etime(String request_etime) {
		if(request_etime==null) addNullValueField("request_etime");
		this.request_etime = request_etime;
	}

	public String getRequest_state() { return request_state; }
	public void setRequest_state(String request_state) {
		if(request_state==null) throw new BusinessException("Entity : InterfaceUseLog.request_state must not null!");
		this.request_state = request_state;
	}

	public String getProtocol() { return protocol; }
	public void setProtocol(String protocol) {
		if(protocol==null) addNullValueField("protocol");
		this.protocol = protocol;
	}

	public String getSystem_type() { return system_type; }
	public void setSystem_type(String system_type) {
		if(system_type==null) addNullValueField("system_type");
		this.system_type = system_type;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : InterfaceUseLog.user_id must not null!");
		this.user_id = user_id;
	}

	public String getRequest_info() { return request_info; }
	public void setRequest_info(String request_info) {
		if(request_info==null) addNullValueField("request_info");
		this.request_info = request_info;
	}

	public String getRequest_mode() { return request_mode; }
	public void setRequest_mode(String request_mode) {
		if(request_mode==null) addNullValueField("request_mode");
		this.request_mode = request_mode;
	}

	public BigDecimal getResponse_time() { return response_time; }
	public void setResponse_time(BigDecimal response_time) {
		if(response_time==null) throw new BusinessException("Entity : InterfaceUseLog.response_time must not null!");
		this.response_time = response_time;
	}

	public BigDecimal getInterface_use_id() { return interface_use_id; }
	public void setInterface_use_id(BigDecimal interface_use_id) {
		if(interface_use_id==null) throw new BusinessException("Entity : InterfaceUseLog.interface_use_id must not null!");
		this.interface_use_id = interface_use_id;
	}

	public String getBrowser_version() { return browser_version; }
	public void setBrowser_version(String browser_version) {
		if(browser_version==null) addNullValueField("browser_version");
		this.browser_version = browser_version;
	}

}