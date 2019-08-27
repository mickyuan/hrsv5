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
@Table(tableName = "etl_sys")
public class EtlSys extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_sys";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String main_serv_sync;
	private String etl_sys_name;
	private String comments;
	private String bath_shift_time;
	private String contact_phone;
	private String contact_person;
	private String user_name;
	private String user_pwd;
	private String sys_run_status;
	private String etl_sys_cd;
	private String curr_bath_date;
	private String etl_serv_port;
	private String serv_file_path;
	private String etl_serv_ip;
	private String remarks;

	public String getMain_serv_sync() { return main_serv_sync; }
	public void setMain_serv_sync(String main_serv_sync) {
		if(main_serv_sync==null) addNullValueField("main_serv_sync");
		this.main_serv_sync = main_serv_sync;
	}

	public String getEtl_sys_name() { return etl_sys_name; }
	public void setEtl_sys_name(String etl_sys_name) {
		if(etl_sys_name==null) throw new BusinessException("Entity : EtlSys.etl_sys_name must not null!");
		this.etl_sys_name = etl_sys_name;
	}

	public String getComments() { return comments; }
	public void setComments(String comments) {
		if(comments==null) addNullValueField("comments");
		this.comments = comments;
	}

	public String getBath_shift_time() { return bath_shift_time; }
	public void setBath_shift_time(String bath_shift_time) {
		if(bath_shift_time==null) addNullValueField("bath_shift_time");
		this.bath_shift_time = bath_shift_time;
	}

	public String getContact_phone() { return contact_phone; }
	public void setContact_phone(String contact_phone) {
		if(contact_phone==null) addNullValueField("contact_phone");
		this.contact_phone = contact_phone;
	}

	public String getContact_person() { return contact_person; }
	public void setContact_person(String contact_person) {
		if(contact_person==null) addNullValueField("contact_person");
		this.contact_person = contact_person;
	}

	public String getUser_name() { return user_name; }
	public void setUser_name(String user_name) {
		if(user_name==null) addNullValueField("user_name");
		this.user_name = user_name;
	}

	public String getUser_pwd() { return user_pwd; }
	public void setUser_pwd(String user_pwd) {
		if(user_pwd==null) addNullValueField("user_pwd");
		this.user_pwd = user_pwd;
	}

	public String getSys_run_status() { return sys_run_status; }
	public void setSys_run_status(String sys_run_status) {
		if(sys_run_status==null) addNullValueField("sys_run_status");
		this.sys_run_status = sys_run_status;
	}

	public String getEtl_sys_cd() { return etl_sys_cd; }
	public void setEtl_sys_cd(String etl_sys_cd) {
		if(etl_sys_cd==null) throw new BusinessException("Entity : EtlSys.etl_sys_cd must not null!");
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getCurr_bath_date() { return curr_bath_date; }
	public void setCurr_bath_date(String curr_bath_date) {
		if(curr_bath_date==null) addNullValueField("curr_bath_date");
		this.curr_bath_date = curr_bath_date;
	}

	public String getEtl_serv_port() { return etl_serv_port; }
	public void setEtl_serv_port(String etl_serv_port) {
		if(etl_serv_port==null) addNullValueField("etl_serv_port");
		this.etl_serv_port = etl_serv_port;
	}

	public String getServ_file_path() { return serv_file_path; }
	public void setServ_file_path(String serv_file_path) {
		if(serv_file_path==null) addNullValueField("serv_file_path");
		this.serv_file_path = serv_file_path;
	}

	public String getEtl_serv_ip() { return etl_serv_ip; }
	public void setEtl_serv_ip(String etl_serv_ip) {
		if(etl_serv_ip==null) addNullValueField("etl_serv_ip");
		this.etl_serv_ip = etl_serv_ip;
	}

	public String getRemarks() { return remarks; }
	public void setRemarks(String remarks) {
		if(remarks==null) addNullValueField("remarks");
		this.remarks = remarks;
	}

}