package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import hrds.commons.entity.Signal_file;

import java.io.Serializable;
import java.util.List;

@DocClass(desc = "数据库采集、db文件采集源数据读取配置信息", author = "zxz", createdate = "2019/11/29 10:52")
public class SourceDataConfBean implements Serializable {
	@DocBean(name = "agent_id", value = "Agent_id:", dataType = Long.class, required = false)
	private Long agent_id;
	@DocBean(name = "database_id", value = "数据库设置id:", dataType = Long.class, required = true)
	private String database_id;
	@DocBean(name = "task_name", value = "数据库采集任务名称:", dataType = String.class, required = false)
	private String task_name;
	@DocBean(name = "database_name", value = "数据库名称:", dataType = String.class, required = false)
	private String database_name;
	@DocBean(name = "database_pad", value = "数据库密码:", dataType = String.class, required = false)
	private String database_pad;
	@DocBean(name = "database_drive", value = "数据库驱动:", dataType = String.class, required = false)
	private String database_drive;
	@DocBean(name = "database_type", value = "数据库类型(DatabaseType):01-MYSQL<MYSQL> 02-Oracle9i及一下<Oracle9i> " +
			"03-Oracle10g及以上<Oracle10g> 04-SQLSERVER2000<SqlServer2000> 05-SQLSERVER2005<SqlServer2005> " +
			"06-DB2<DB2> 07-SybaseASE12.5及以上<SybaseASE125> 08-Informatic<Informatic> 09-H2<H2> " +
			"10-ApacheDerby<ApacheDerby> 11-Postgresql<Postgresql> 12-GBase<GBase> 13-TeraData<TeraData> ",
			dataType = String.class, required = true)
	private String database_type;
	@DocBean(name = "user_name", value = "用户名称:", dataType = String.class, required = false)
	private String user_name;
	@DocBean(name = "database_ip", value = "数据库服务器IP:", dataType = String.class, required = false)
	private String database_ip;
	@DocBean(name = "database_port", value = "数据库端口:", dataType = String.class, required = false)
	private String database_port;
	@DocBean(name = "host_name", value = "主机名:", dataType = String.class, required = false)
	private String host_name;
	@DocBean(name = "system_type", value = "操作系统类型:", dataType = String.class, required = false)
	private String system_type;
	@DocBean(name = "is_sendok", value = "是否设置完成并发送成功(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_sendok;
	@DocBean(name = "database_number", value = "数据库设置编号:", dataType = String.class, required = true)
	private String database_number;
	@DocBean(name = "db_agent", value = "是否为平面DB数据采集(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String db_agent;
	@DocBean(name = "plane_url", value = "DB文件源数据路径:", dataType = String.class, required = false)
	private String plane_url;
	@DocBean(name = "database_separatorr", value = "数据采用分隔符:", dataType = String.class, required = false)
	private String database_separatorr;
	@DocBean(name = "database_code", value = "数据使用编码格式(DataBaseCode):1-UTF-8<UTF_8> 2-GBK<GBK> " +
			"3-UTF-16<UTF_16> 4-GB2312<GB2312> 5-ISO-8859-1<ISO_8859_1> ", dataType = String.class, required = false)
	private String database_code;
	@DocBean(name = "dbfile_format", value = "DB文件格式(FileFormat):0-定长<DingChang> 1-非定长<FeiDingChang> " +
			"2-CSV<CSV> 3-SEQUENCEFILE<SEQUENCEFILE> 4-PARQUET<PARQUET> 5-ORC<ORC> ",
			dataType = String.class, required = false)
	private String dbfile_format;
	@DocBean(name = "is_hidden", value = "分隔符是否为ASCII隐藏字符(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_hidden;
	@DocBean(name = "file_suffix", value = "采集文件名后缀:", dataType = String.class, required = false)
	private String file_suffix;
	@DocBean(name = "is_load", value = "是否直接加载数据(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_load;
	@DocBean(name = "row_separator", value = "数据行分隔符:", dataType = String.class, required = false)
	private String row_separator;
	@DocBean(name = "classify_id", value = "分类id:", dataType = Long.class, required = true)
	private Long classify_id;
	@DocBean(name = "is_header", value = "是否有表头(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_header;
	@DocBean(name = "jdbc_url", value = "数据库连接地址:", dataType = String.class, required = false)
	private String jdbc_url;
	@DocBean(name = "datasource_number", value = "数据源编号", dataType = String.class, required = true)
	private String datasource_number;
	@DocBean(name = "classify_num", value = "分类编号", dataType = String.class, required = true)
	private String classify_num;
	@DocBean(name = "collectDir", value = "采集日期", dataType = String.class, required = true)
	private String collectDir;
	@DocBean(name = "collectTableBeanArray", value = "采集表配置信息数组", dataType = String.class, required = true)
	private List<CollectTableBean> collectTableBeanArray;
	@DocBean(name = "signal_file_list", value = "信号文件入库信息集合", dataType = List.class, required = false)
	private List<Signal_file> signal_file_list;

	public Long getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(Long agent_id) {
		this.agent_id = agent_id;
	}

	public String getDatabase_id() {
		return database_id;
	}

	public void setDatabase_id(String database_id) {
		this.database_id = database_id;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public String getCollectDir() {
		return collectDir;
	}

	public void setCollectDir(String collectDir) {
		this.collectDir = collectDir;
	}

	public List<CollectTableBean> getCollectTableBeanArray() {
		return collectTableBeanArray;
	}

	public void setCollectTableBeanArray(List<CollectTableBean> collectTableBeanArray) {
		this.collectTableBeanArray = collectTableBeanArray;
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

	public String getDatabase_drive() {
		return database_drive;
	}

	public void setDatabase_drive(String database_drive) {
		this.database_drive = database_drive;
	}

	public String getDatabase_type() {
		return database_type;
	}

	public void setDatabase_type(String database_type) {
		this.database_type = database_type;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getDatabase_ip() {
		return database_ip;
	}

	public void setDatabase_ip(String database_ip) {
		this.database_ip = database_ip;
	}

	public String getDatabase_port() {
		return database_port;
	}

	public void setDatabase_port(String database_port) {
		this.database_port = database_port;
	}

	public String getHost_name() {
		return host_name;
	}

	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}

	public String getSystem_type() {
		return system_type;
	}

	public void setSystem_type(String system_type) {
		this.system_type = system_type;
	}

	public String getIs_sendok() {
		return is_sendok;
	}

	public void setIs_sendok(String is_sendok) {
		this.is_sendok = is_sendok;
	}

	public String getDatabase_number() {
		return database_number;
	}

	public void setDatabase_number(String database_number) {
		this.database_number = database_number;
	}

	public String getDb_agent() {
		return db_agent;
	}

	public void setDb_agent(String db_agent) {
		this.db_agent = db_agent;
	}

	public String getPlane_url() {
		return plane_url;
	}

	public void setPlane_url(String plane_url) {
		this.plane_url = plane_url;
	}

	public String getDatabase_separatorr() {
		return database_separatorr;
	}

	public void setDatabase_separatorr(String database_separatorr) {
		this.database_separatorr = database_separatorr;
	}

	public String getDatabase_code() {
		return database_code;
	}

	public void setDatabase_code(String database_code) {
		this.database_code = database_code;
	}

	public String getDbfile_format() {
		return dbfile_format;
	}

	public void setDbfile_format(String dbfile_format) {
		this.dbfile_format = dbfile_format;
	}

	public String getIs_hidden() {
		return is_hidden;
	}

	public void setIs_hidden(String is_hidden) {
		this.is_hidden = is_hidden;
	}

	public String getFile_suffix() {
		return file_suffix;
	}

	public void setFile_suffix(String file_suffix) {
		this.file_suffix = file_suffix;
	}

	public String getIs_load() {
		return is_load;
	}

	public void setIs_load(String is_load) {
		this.is_load = is_load;
	}

	public String getRow_separator() {
		return row_separator;
	}

	public void setRow_separator(String row_separator) {
		this.row_separator = row_separator;
	}

	public Long getClassify_id() {
		return classify_id;
	}

	public void setClassify_id(Long classify_id) {
		this.classify_id = classify_id;
	}

	public String getIs_header() {
		return is_header;
	}

	public void setIs_header(String is_header) {
		this.is_header = is_header;
	}

	public String getJdbc_url() {
		return jdbc_url;
	}

	public void setJdbc_url(String jdbc_url) {
		this.jdbc_url = jdbc_url;
	}

	public String getDatasource_number() {
		return datasource_number;
	}

	public void setDatasource_number(String datasource_number) {
		this.datasource_number = datasource_number;
	}

	public String getClassify_num() {
		return classify_num;
	}

	public void setClassify_num(String classify_num) {
		this.classify_num = classify_num;
	}

	public List<Signal_file> getSignal_file_list() {
		return signal_file_list;
	}

	public void setSignal_file_list(List<Signal_file> signal_file_list) {
		this.signal_file_list = signal_file_list;
	}
}
