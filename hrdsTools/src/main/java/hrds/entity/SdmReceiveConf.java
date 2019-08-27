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
@Table(tableName = "sdm_receive_conf")
public class SdmReceiveConf extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_receive_conf";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_receive_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String sdm_email;
	private String is_file_attr_ip;
	private String is_data_partition;
	private String sdm_receive_name;
	private String code;
	private String remark;
	private String is_obj;
	private BigDecimal sdm_receive_id;
	private String cus_des_type;
	private String run_way;
	private String file_initposition;
	private String file_match_rule;
	private String sdm_bus_pro_cla;
	private String create_date;
	private String msgtype;
	private String snmp_ip;
	private String read_type;
	private String is_full_path;
	private String create_time;
	private BigDecimal sdm_agent_id;
	private String sdm_dat_delimiter;
	private String sdm_partition_name;
	private String is_file_size;
	private String file_handle;
	private String msgheader;
	private BigDecimal thread_num;
	private Integer check_cycle;
	private String fault_alarm_mode;
	private String read_mode;
	private String sdm_rec_port;
	private String ra_file_path;
	private String file_readtype;
	private String file_read_num;
	private String is_file_name;
	private String sdm_partition;
	private String monitor_type;
	private String is_file_time;
	private String is_line_num;
	private String sdm_rec_des;

	public String getSdm_email() { return sdm_email; }
	public void setSdm_email(String sdm_email) {
		if(sdm_email==null) addNullValueField("sdm_email");
		this.sdm_email = sdm_email;
	}

	public String getIs_file_attr_ip() { return is_file_attr_ip; }
	public void setIs_file_attr_ip(String is_file_attr_ip) {
		if(is_file_attr_ip==null) throw new BusinessException("Entity : SdmReceiveConf.is_file_attr_ip must not null!");
		this.is_file_attr_ip = is_file_attr_ip;
	}

	public String getIs_data_partition() { return is_data_partition; }
	public void setIs_data_partition(String is_data_partition) {
		if(is_data_partition==null) throw new BusinessException("Entity : SdmReceiveConf.is_data_partition must not null!");
		this.is_data_partition = is_data_partition;
	}

	public String getSdm_receive_name() { return sdm_receive_name; }
	public void setSdm_receive_name(String sdm_receive_name) {
		if(sdm_receive_name==null) throw new BusinessException("Entity : SdmReceiveConf.sdm_receive_name must not null!");
		this.sdm_receive_name = sdm_receive_name;
	}

	public String getCode() { return code; }
	public void setCode(String code) {
		if(code==null) addNullValueField("code");
		this.code = code;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_obj() { return is_obj; }
	public void setIs_obj(String is_obj) {
		if(is_obj==null) throw new BusinessException("Entity : SdmReceiveConf.is_obj must not null!");
		this.is_obj = is_obj;
	}

	public BigDecimal getSdm_receive_id() { return sdm_receive_id; }
	public void setSdm_receive_id(BigDecimal sdm_receive_id) {
		if(sdm_receive_id==null) throw new BusinessException("Entity : SdmReceiveConf.sdm_receive_id must not null!");
		this.sdm_receive_id = sdm_receive_id;
	}

	public String getCus_des_type() { return cus_des_type; }
	public void setCus_des_type(String cus_des_type) {
		if(cus_des_type==null) throw new BusinessException("Entity : SdmReceiveConf.cus_des_type must not null!");
		this.cus_des_type = cus_des_type;
	}

	public String getRun_way() { return run_way; }
	public void setRun_way(String run_way) {
		if(run_way==null) throw new BusinessException("Entity : SdmReceiveConf.run_way must not null!");
		this.run_way = run_way;
	}

	public String getFile_initposition() { return file_initposition; }
	public void setFile_initposition(String file_initposition) {
		if(file_initposition==null) addNullValueField("file_initposition");
		this.file_initposition = file_initposition;
	}

	public String getFile_match_rule() { return file_match_rule; }
	public void setFile_match_rule(String file_match_rule) {
		if(file_match_rule==null) addNullValueField("file_match_rule");
		this.file_match_rule = file_match_rule;
	}

	public String getSdm_bus_pro_cla() { return sdm_bus_pro_cla; }
	public void setSdm_bus_pro_cla(String sdm_bus_pro_cla) {
		if(sdm_bus_pro_cla==null) addNullValueField("sdm_bus_pro_cla");
		this.sdm_bus_pro_cla = sdm_bus_pro_cla;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SdmReceiveConf.create_date must not null!");
		this.create_date = create_date;
	}

	public String getMsgtype() { return msgtype; }
	public void setMsgtype(String msgtype) {
		if(msgtype==null) addNullValueField("msgtype");
		this.msgtype = msgtype;
	}

	public String getSnmp_ip() { return snmp_ip; }
	public void setSnmp_ip(String snmp_ip) {
		if(snmp_ip==null) addNullValueField("snmp_ip");
		this.snmp_ip = snmp_ip;
	}

	public String getRead_type() { return read_type; }
	public void setRead_type(String read_type) {
		if(read_type==null) throw new BusinessException("Entity : SdmReceiveConf.read_type must not null!");
		this.read_type = read_type;
	}

	public String getIs_full_path() { return is_full_path; }
	public void setIs_full_path(String is_full_path) {
		if(is_full_path==null) throw new BusinessException("Entity : SdmReceiveConf.is_full_path must not null!");
		this.is_full_path = is_full_path;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : SdmReceiveConf.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getSdm_agent_id() { return sdm_agent_id; }
	public void setSdm_agent_id(BigDecimal sdm_agent_id) {
		if(sdm_agent_id==null) addNullValueField("sdm_agent_id");
		this.sdm_agent_id = sdm_agent_id;
	}

	public String getSdm_dat_delimiter() { return sdm_dat_delimiter; }
	public void setSdm_dat_delimiter(String sdm_dat_delimiter) {
		if(sdm_dat_delimiter==null) addNullValueField("sdm_dat_delimiter");
		this.sdm_dat_delimiter = sdm_dat_delimiter;
	}

	public String getSdm_partition_name() { return sdm_partition_name; }
	public void setSdm_partition_name(String sdm_partition_name) {
		if(sdm_partition_name==null) addNullValueField("sdm_partition_name");
		this.sdm_partition_name = sdm_partition_name;
	}

	public String getIs_file_size() { return is_file_size; }
	public void setIs_file_size(String is_file_size) {
		if(is_file_size==null) throw new BusinessException("Entity : SdmReceiveConf.is_file_size must not null!");
		this.is_file_size = is_file_size;
	}

	public String getFile_handle() { return file_handle; }
	public void setFile_handle(String file_handle) {
		if(file_handle==null) addNullValueField("file_handle");
		this.file_handle = file_handle;
	}

	public String getMsgheader() { return msgheader; }
	public void setMsgheader(String msgheader) {
		if(msgheader==null) addNullValueField("msgheader");
		this.msgheader = msgheader;
	}

	public BigDecimal getThread_num() { return thread_num; }
	public void setThread_num(BigDecimal thread_num) {
		if(thread_num==null) addNullValueField("thread_num");
		this.thread_num = thread_num;
	}

	public Integer getCheck_cycle() { return check_cycle; }
	public void setCheck_cycle(Integer check_cycle) {
		if(check_cycle==null) addNullValueField("check_cycle");
		this.check_cycle = check_cycle;
	}

	public String getFault_alarm_mode() { return fault_alarm_mode; }
	public void setFault_alarm_mode(String fault_alarm_mode) {
		if(fault_alarm_mode==null) addNullValueField("fault_alarm_mode");
		this.fault_alarm_mode = fault_alarm_mode;
	}

	public String getRead_mode() { return read_mode; }
	public void setRead_mode(String read_mode) {
		if(read_mode==null) throw new BusinessException("Entity : SdmReceiveConf.read_mode must not null!");
		this.read_mode = read_mode;
	}

	public String getSdm_rec_port() { return sdm_rec_port; }
	public void setSdm_rec_port(String sdm_rec_port) {
		if(sdm_rec_port==null) addNullValueField("sdm_rec_port");
		this.sdm_rec_port = sdm_rec_port;
	}

	public String getRa_file_path() { return ra_file_path; }
	public void setRa_file_path(String ra_file_path) {
		if(ra_file_path==null) addNullValueField("ra_file_path");
		this.ra_file_path = ra_file_path;
	}

	public String getFile_readtype() { return file_readtype; }
	public void setFile_readtype(String file_readtype) {
		if(file_readtype==null) addNullValueField("file_readtype");
		this.file_readtype = file_readtype;
	}

	public String getFile_read_num() { return file_read_num; }
	public void setFile_read_num(String file_read_num) {
		if(file_read_num==null) addNullValueField("file_read_num");
		this.file_read_num = file_read_num;
	}

	public String getIs_file_name() { return is_file_name; }
	public void setIs_file_name(String is_file_name) {
		if(is_file_name==null) throw new BusinessException("Entity : SdmReceiveConf.is_file_name must not null!");
		this.is_file_name = is_file_name;
	}

	public String getSdm_partition() { return sdm_partition; }
	public void setSdm_partition(String sdm_partition) {
		if(sdm_partition==null) throw new BusinessException("Entity : SdmReceiveConf.sdm_partition must not null!");
		this.sdm_partition = sdm_partition;
	}

	public String getMonitor_type() { return monitor_type; }
	public void setMonitor_type(String monitor_type) {
		if(monitor_type==null) throw new BusinessException("Entity : SdmReceiveConf.monitor_type must not null!");
		this.monitor_type = monitor_type;
	}

	public String getIs_file_time() { return is_file_time; }
	public void setIs_file_time(String is_file_time) {
		if(is_file_time==null) throw new BusinessException("Entity : SdmReceiveConf.is_file_time must not null!");
		this.is_file_time = is_file_time;
	}

	public String getIs_line_num() { return is_line_num; }
	public void setIs_line_num(String is_line_num) {
		if(is_line_num==null) throw new BusinessException("Entity : SdmReceiveConf.is_line_num must not null!");
		this.is_line_num = is_line_num;
	}

	public String getSdm_rec_des() { return sdm_rec_des; }
	public void setSdm_rec_des(String sdm_rec_des) {
		if(sdm_rec_des==null) addNullValueField("sdm_rec_des");
		this.sdm_rec_des = sdm_rec_des;
	}

}