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
@Table(tableName = "database_set")
public class DatabaseSet extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "database_set";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("database_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String task_name;
	private String database_ip;
	private String is_load;
	private BigDecimal agent_id;
	private String analysis_signalfile;
	private String user_name;
	private BigDecimal classify_id;
	private String is_hidden;
	private String database_drive;
	private String database_separatorr;
	private String database_type;
	private String data_extract_type;
	private String plane_url;
	private String cp_or;
	private String database_code;
	private String is_header;
	private String database_name;
	private String database_pad;
	private String database_number;
	private String database_port;
	private String jdbc_url;
	private String dbfile_format;
	private String is_sendok;
	private BigDecimal database_id;
	private String system_type;
	private String file_suffix;
	private String signal_file_suffix;
	private String row_separator;
	private String host_name;
	private String db_agent;
	private String check_time;

	public String getTask_name() { return task_name; }
	public void setTask_name(String task_name) {
		if(task_name==null) addNullValueField("task_name");
		this.task_name = task_name;
	}

	public String getDatabase_ip() { return database_ip; }
	public void setDatabase_ip(String database_ip) {
		if(database_ip==null) addNullValueField("database_ip");
		this.database_ip = database_ip;
	}

	public String getIs_load() { return is_load; }
	public void setIs_load(String is_load) {
		if(is_load==null) throw new BusinessException("Entity : DatabaseSet.is_load must not null!");
		this.is_load = is_load;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) addNullValueField("agent_id");
		this.agent_id = agent_id;
	}

	public String getAnalysis_signalfile() { return analysis_signalfile; }
	public void setAnalysis_signalfile(String analysis_signalfile) {
		if(analysis_signalfile==null) throw new BusinessException("Entity : DatabaseSet.analysis_signalfile must not null!");
		this.analysis_signalfile = analysis_signalfile;
	}

	public String getUser_name() { return user_name; }
	public void setUser_name(String user_name) {
		if(user_name==null) addNullValueField("user_name");
		this.user_name = user_name;
	}

	public BigDecimal getClassify_id() { return classify_id; }
	public void setClassify_id(BigDecimal classify_id) {
		if(classify_id==null) throw new BusinessException("Entity : DatabaseSet.classify_id must not null!");
		this.classify_id = classify_id;
	}

	public String getIs_hidden() { return is_hidden; }
	public void setIs_hidden(String is_hidden) {
		if(is_hidden==null) throw new BusinessException("Entity : DatabaseSet.is_hidden must not null!");
		this.is_hidden = is_hidden;
	}

	public String getDatabase_drive() { return database_drive; }
	public void setDatabase_drive(String database_drive) {
		if(database_drive==null) addNullValueField("database_drive");
		this.database_drive = database_drive;
	}

	public String getDatabase_separatorr() { return database_separatorr; }
	public void setDatabase_separatorr(String database_separatorr) {
		if(database_separatorr==null) addNullValueField("database_separatorr");
		this.database_separatorr = database_separatorr;
	}

	public String getDatabase_type() { return database_type; }
	public void setDatabase_type(String database_type) {
		if(database_type==null) addNullValueField("database_type");
		this.database_type = database_type;
	}

	public String getData_extract_type() { return data_extract_type; }
	public void setData_extract_type(String data_extract_type) {
		if(data_extract_type==null) throw new BusinessException("Entity : DatabaseSet.data_extract_type must not null!");
		this.data_extract_type = data_extract_type;
	}

	public String getPlane_url() { return plane_url; }
	public void setPlane_url(String plane_url) {
		if(plane_url==null) addNullValueField("plane_url");
		this.plane_url = plane_url;
	}

	public String getCp_or() { return cp_or; }
	public void setCp_or(String cp_or) {
		if(cp_or==null) addNullValueField("cp_or");
		this.cp_or = cp_or;
	}

	public String getDatabase_code() { return database_code; }
	public void setDatabase_code(String database_code) {
		if(database_code==null) addNullValueField("database_code");
		this.database_code = database_code;
	}

	public String getIs_header() { return is_header; }
	public void setIs_header(String is_header) {
		if(is_header==null) throw new BusinessException("Entity : DatabaseSet.is_header must not null!");
		this.is_header = is_header;
	}

	public String getDatabase_name() { return database_name; }
	public void setDatabase_name(String database_name) {
		if(database_name==null) addNullValueField("database_name");
		this.database_name = database_name;
	}

	public String getDatabase_pad() { return database_pad; }
	public void setDatabase_pad(String database_pad) {
		if(database_pad==null) addNullValueField("database_pad");
		this.database_pad = database_pad;
	}

	public String getDatabase_number() { return database_number; }
	public void setDatabase_number(String database_number) {
		if(database_number==null) throw new BusinessException("Entity : DatabaseSet.database_number must not null!");
		this.database_number = database_number;
	}

	public String getDatabase_port() { return database_port; }
	public void setDatabase_port(String database_port) {
		if(database_port==null) addNullValueField("database_port");
		this.database_port = database_port;
	}

	public String getJdbc_url() { return jdbc_url; }
	public void setJdbc_url(String jdbc_url) {
		if(jdbc_url==null) addNullValueField("jdbc_url");
		this.jdbc_url = jdbc_url;
	}

	public String getDbfile_format() { return dbfile_format; }
	public void setDbfile_format(String dbfile_format) {
		if(dbfile_format==null) addNullValueField("dbfile_format");
		this.dbfile_format = dbfile_format;
	}

	public String getIs_sendok() { return is_sendok; }
	public void setIs_sendok(String is_sendok) {
		if(is_sendok==null) throw new BusinessException("Entity : DatabaseSet.is_sendok must not null!");
		this.is_sendok = is_sendok;
	}

	public BigDecimal getDatabase_id() { return database_id; }
	public void setDatabase_id(BigDecimal database_id) {
		if(database_id==null) throw new BusinessException("Entity : DatabaseSet.database_id must not null!");
		this.database_id = database_id;
	}

	public String getSystem_type() { return system_type; }
	public void setSystem_type(String system_type) {
		if(system_type==null) addNullValueField("system_type");
		this.system_type = system_type;
	}

	public String getFile_suffix() { return file_suffix; }
	public void setFile_suffix(String file_suffix) {
		if(file_suffix==null) addNullValueField("file_suffix");
		this.file_suffix = file_suffix;
	}

	public String getSignal_file_suffix() { return signal_file_suffix; }
	public void setSignal_file_suffix(String signal_file_suffix) {
		if(signal_file_suffix==null) addNullValueField("signal_file_suffix");
		this.signal_file_suffix = signal_file_suffix;
	}

	public String getRow_separator() { return row_separator; }
	public void setRow_separator(String row_separator) {
		if(row_separator==null) addNullValueField("row_separator");
		this.row_separator = row_separator;
	}

	public String getHost_name() { return host_name; }
	public void setHost_name(String host_name) {
		if(host_name==null) addNullValueField("host_name");
		this.host_name = host_name;
	}

	public String getDb_agent() { return db_agent; }
	public void setDb_agent(String db_agent) {
		if(db_agent==null) throw new BusinessException("Entity : DatabaseSet.db_agent must not null!");
		this.db_agent = db_agent;
	}

	public String getCheck_time() { return check_time; }
	public void setCheck_time(String check_time) {
		if(check_time==null) addNullValueField("check_time");
		this.check_time = check_time;
	}

}