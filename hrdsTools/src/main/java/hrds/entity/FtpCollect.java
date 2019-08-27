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
@Table(tableName = "ftp_collect")
public class FtpCollect extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ftp_collect";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ftp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String end_date;
	private String ftp_ip;
	private String ftp_password;
	private String ftp_rule_path;
	private BigDecimal agent_id;
	private String ftp_port;
	private String child_file_path;
	private String ftp_username;
	private String local_path;
	private String remark;
	private String is_sendok;
	private String run_way;
	private String ftp_dir;
	private String child_time;
	private String file_suffix;
	private String ftp_number;
	private String ftp_model;
	private String reduce_type;
	private String is_unzip;
	private BigDecimal ftp_id;
	private String ftp_name;
	private String start_date;

	public String getEnd_date() { return end_date; }
	public void setEnd_date(String end_date) {
		if(end_date==null) throw new BusinessException("Entity : FtpCollect.end_date must not null!");
		this.end_date = end_date;
	}

	public String getFtp_ip() { return ftp_ip; }
	public void setFtp_ip(String ftp_ip) {
		if(ftp_ip==null) throw new BusinessException("Entity : FtpCollect.ftp_ip must not null!");
		this.ftp_ip = ftp_ip;
	}

	public String getFtp_password() { return ftp_password; }
	public void setFtp_password(String ftp_password) {
		if(ftp_password==null) throw new BusinessException("Entity : FtpCollect.ftp_password must not null!");
		this.ftp_password = ftp_password;
	}

	public String getFtp_rule_path() { return ftp_rule_path; }
	public void setFtp_rule_path(String ftp_rule_path) {
		if(ftp_rule_path==null) throw new BusinessException("Entity : FtpCollect.ftp_rule_path must not null!");
		this.ftp_rule_path = ftp_rule_path;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : FtpCollect.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getFtp_port() { return ftp_port; }
	public void setFtp_port(String ftp_port) {
		if(ftp_port==null) throw new BusinessException("Entity : FtpCollect.ftp_port must not null!");
		this.ftp_port = ftp_port;
	}

	public String getChild_file_path() { return child_file_path; }
	public void setChild_file_path(String child_file_path) {
		if(child_file_path==null) addNullValueField("child_file_path");
		this.child_file_path = child_file_path;
	}

	public String getFtp_username() { return ftp_username; }
	public void setFtp_username(String ftp_username) {
		if(ftp_username==null) throw new BusinessException("Entity : FtpCollect.ftp_username must not null!");
		this.ftp_username = ftp_username;
	}

	public String getLocal_path() { return local_path; }
	public void setLocal_path(String local_path) {
		if(local_path==null) throw new BusinessException("Entity : FtpCollect.local_path must not null!");
		this.local_path = local_path;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_sendok() { return is_sendok; }
	public void setIs_sendok(String is_sendok) {
		if(is_sendok==null) throw new BusinessException("Entity : FtpCollect.is_sendok must not null!");
		this.is_sendok = is_sendok;
	}

	public String getRun_way() { return run_way; }
	public void setRun_way(String run_way) {
		if(run_way==null) throw new BusinessException("Entity : FtpCollect.run_way must not null!");
		this.run_way = run_way;
	}

	public String getFtp_dir() { return ftp_dir; }
	public void setFtp_dir(String ftp_dir) {
		if(ftp_dir==null) throw new BusinessException("Entity : FtpCollect.ftp_dir must not null!");
		this.ftp_dir = ftp_dir;
	}

	public String getChild_time() { return child_time; }
	public void setChild_time(String child_time) {
		if(child_time==null) addNullValueField("child_time");
		this.child_time = child_time;
	}

	public String getFile_suffix() { return file_suffix; }
	public void setFile_suffix(String file_suffix) {
		if(file_suffix==null) addNullValueField("file_suffix");
		this.file_suffix = file_suffix;
	}

	public String getFtp_number() { return ftp_number; }
	public void setFtp_number(String ftp_number) {
		if(ftp_number==null) throw new BusinessException("Entity : FtpCollect.ftp_number must not null!");
		this.ftp_number = ftp_number;
	}

	public String getFtp_model() { return ftp_model; }
	public void setFtp_model(String ftp_model) {
		if(ftp_model==null) throw new BusinessException("Entity : FtpCollect.ftp_model must not null!");
		this.ftp_model = ftp_model;
	}

	public String getReduce_type() { return reduce_type; }
	public void setReduce_type(String reduce_type) {
		if(reduce_type==null) addNullValueField("reduce_type");
		this.reduce_type = reduce_type;
	}

	public String getIs_unzip() { return is_unzip; }
	public void setIs_unzip(String is_unzip) {
		if(is_unzip==null) throw new BusinessException("Entity : FtpCollect.is_unzip must not null!");
		this.is_unzip = is_unzip;
	}

	public BigDecimal getFtp_id() { return ftp_id; }
	public void setFtp_id(BigDecimal ftp_id) {
		if(ftp_id==null) throw new BusinessException("Entity : FtpCollect.ftp_id must not null!");
		this.ftp_id = ftp_id;
	}

	public String getFtp_name() { return ftp_name; }
	public void setFtp_name(String ftp_name) {
		if(ftp_name==null) throw new BusinessException("Entity : FtpCollect.ftp_name must not null!");
		this.ftp_name = ftp_name;
	}

	public String getStart_date() { return start_date; }
	public void setStart_date(String start_date) {
		if(start_date==null) throw new BusinessException("Entity : FtpCollect.start_date must not null!");
		this.start_date = start_date;
	}

}