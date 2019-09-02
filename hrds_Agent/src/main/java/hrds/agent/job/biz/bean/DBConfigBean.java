package hrds.agent.job.biz.bean;

import java.io.Serializable;

/**
 * ClassName: DBConfigBean <br/>
 * Function: 数据库配置信息实习 <br/>
 * Reason: 数据库直连采集需要使用
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DBConfigBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String task_name;
    private String database_ip;
    private String is_load;
    private String agent_id;
    private String analysis_signalfile;
    private String user_name;
    private String classify_id;
    private String is_hidden;
    private String datasource_number;
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
    private String classify_num;
    private String database_id;
    private String system_type;
    private String file_suffix;
    private String signal_file_suffix;
    private String row_separator;
    private String host_name;
    private String db_agent;
    private String check_time;

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getDatabase_ip() {
        return database_ip;
    }

    public void setDatabase_ip(String database_ip) {
        this.database_ip = database_ip;
    }

    public String getIs_load() {
        return is_load;
    }

    public void setIs_load(String is_load) {
        this.is_load = is_load;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getAnalysis_signalfile() {
        return analysis_signalfile;
    }

    public void setAnalysis_signalfile(String analysis_signalfile) {
        this.analysis_signalfile = analysis_signalfile;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getClassify_id() {
        return classify_id;
    }

    public void setClassify_id(String classify_id) {
        this.classify_id = classify_id;
    }

    public String getIs_hidden() {
        return is_hidden;
    }

    public void setIs_hidden(String is_hidden) {
        this.is_hidden = is_hidden;
    }

    public String getDatasource_number() {
        return datasource_number;
    }

    public void setDatasource_number(String datasource_number) {
        this.datasource_number = datasource_number;
    }

    public String getDatabase_drive() {
        return database_drive;
    }

    public void setDatabase_drive(String database_drive) {
        this.database_drive = database_drive;
    }

    public String getDatabase_separatorr() {
        return database_separatorr;
    }

    public void setDatabase_separatorr(String database_separatorr) {
        this.database_separatorr = database_separatorr;
    }

    public String getDatabase_type() {
        return database_type;
    }

    public void setDatabase_type(String database_type) {
        this.database_type = database_type;
    }

    public String getData_extract_type() {
        return data_extract_type;
    }

    public void setData_extract_type(String data_extract_type) {
        this.data_extract_type = data_extract_type;
    }

    public String getPlane_url() {
        return plane_url;
    }

    public void setPlane_url(String plane_url) {
        this.plane_url = plane_url;
    }

    public String getCp_or() {
        return cp_or;
    }

    public void setCp_or(String cp_or) {
        this.cp_or = cp_or;
    }

    public String getDatabase_code() {
        return database_code;
    }

    public void setDatabase_code(String database_code) {
        this.database_code = database_code;
    }

    public String getIs_header() {
        return is_header;
    }

    public void setIs_header(String is_header) {
        this.is_header = is_header;
    }

    public String getDatabase_name() {
        return database_name;
    }

    public void setDatabase_name(String database_name) {
        this.database_name = database_name;
    }

    public String getDatabase_pad() {
        return database_pad;
    }

    public void setDatabase_pad(String database_pad) {
        this.database_pad = database_pad;
    }

    public String getDatabase_number() {
        return database_number;
    }

    public void setDatabase_number(String database_number) {
        this.database_number = database_number;
    }

    public String getDatabase_port() {
        return database_port;
    }

    public void setDatabase_port(String database_port) {
        this.database_port = database_port;
    }

    public String getJdbc_url() {
        return jdbc_url;
    }

    public void setJdbc_url(String jdbc_url) {
        this.jdbc_url = jdbc_url;
    }

    public String getDbfile_format() {
        return dbfile_format;
    }

    public void setDbfile_format(String dbfile_format) {
        this.dbfile_format = dbfile_format;
    }

    public String getIs_sendok() {
        return is_sendok;
    }

    public void setIs_sendok(String is_sendok) {
        this.is_sendok = is_sendok;
    }

    public String getClassify_num() {
        return classify_num;
    }

    public void setClassify_num(String classify_num) {
        this.classify_num = classify_num;
    }

    public String getDatabase_id() {
        return database_id;
    }

    public void setDatabase_id(String database_id) {
        this.database_id = database_id;
    }

    public String getSystem_type() {
        return system_type;
    }

    public void setSystem_type(String system_type) {
        this.system_type = system_type;
    }

    public String getFile_suffix() {
        return file_suffix;
    }

    public void setFile_suffix(String file_suffix) {
        this.file_suffix = file_suffix;
    }

    public String getSignal_file_suffix() {
        return signal_file_suffix;
    }

    public void setSignal_file_suffix(String signal_file_suffix) {
        this.signal_file_suffix = signal_file_suffix;
    }

    public String getRow_separator() {
        return row_separator;
    }

    public void setRow_separator(String row_separator) {
        this.row_separator = row_separator;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getDb_agent() {
        return db_agent;
    }

    public void setDb_agent(String db_agent) {
        this.db_agent = db_agent;
    }

    public String getCheck_time() {
        return check_time;
    }

    public void setCheck_time(String check_time) {
        this.check_time = check_time;
    }

    @Override
    public String toString() {
        return "DBConfigBean [task_name=" + task_name + ", database_ip=" + database_ip + ", is_load=" + is_load
                + ", agent_id=" + agent_id + ", analysis_signalfile=" + analysis_signalfile + ", user_name=" + user_name
                + ", classify_id=" + classify_id + ", is_hidden=" + is_hidden + ", datasource_number="
                + datasource_number + ", database_drive=" + database_drive + ", database_separatorr="
                + database_separatorr + ", database_type=" + database_type + ", data_extract_type=" + data_extract_type
                + ", plane_url=" + plane_url + ", cp_or=" + cp_or + ", database_code=" + database_code + ", is_header="
                + is_header + ", database_name=" + database_name + ", database_pad=" + database_pad
                + ", database_number=" + database_number + ", database_port=" + database_port + ", jdbc_url=" + jdbc_url
                + ", dbfile_format=" + dbfile_format + ", is_sendok=" + is_sendok + ", classify_num=" + classify_num
                + ", database_id=" + database_id + ", system_type=" + system_type + ", file_suffix=" + file_suffix
                + ", signal_file_suffix=" + signal_file_suffix + ", row_separator=" + row_separator + ", host_name="
                + host_name + ", db_agent=" + db_agent + ", check_time=" + check_time + "]";
    }
}
