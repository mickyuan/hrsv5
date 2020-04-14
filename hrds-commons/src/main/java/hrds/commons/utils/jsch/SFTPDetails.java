package hrds.commons.utils.jsch;

import fd.ng.core.utils.StringUtil;

public class SFTPDetails {

	/**
	 * 登陆密码
	 */
	private String pwd;
	/**
	 * 登陆地址IP
	 */
	private String host;
	/**
	 * 登陆用户
	 */
	private String user_name;
	/**
	 * 登陆端口
	 */
	private int port;
	/**
	 * 本地文件路径
	 */
	private String source_path;
	/**
	 * 本地文件名称
	 */
	private String agent_gz;
	/**
	 * 集群conf配置文件
	 */
	private String HADOOP_CONF;
	/**
	 * 目标路径
	 */
	private String target＿dir;
	/**
	 * redis配置文件
	 */
	private String redis_info;
	/**
	 * 数据库配置文件
	 */
	private String db_info;
	/**
	 * 配置文件临时存放路径
	 */
	private String tmp_conf_path;

	public SFTPDetails() {
	}

	public SFTPDetails(String host, String user_name, String pwd, int port) {
		this.host = host;
		this.user_name = user_name;
		this.pwd = pwd;
		this.port = port;
	}

	public SFTPDetails(String host, String user_name, String pwd, String port) {
		this.host = host;
		this.user_name = user_name;
		this.pwd = pwd;
		if (StringUtil.isEmpty(port)) {
			this.port = 22;
		} else {
			this.port = Integer.parseInt(port);
		}
	}

	public String getTmp_conf_path() {
		return tmp_conf_path;
	}

	public void setTmp_conf_path(String tmp_conf_path) {
		this.tmp_conf_path = tmp_conf_path;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAgent_gz() {
		return agent_gz;
	}

	public String getHADOOP_CONF() {
		return HADOOP_CONF;
	}

	public String getRedis_info() {
		return redis_info;
	}

	public String getSource_path() {
		return source_path;
	}

	public String getTarget＿dir() {
		return target＿dir;
	}

	public void setTarget＿dir(String target＿dir) {
		this.target＿dir = target＿dir;
	}

	public void setAgent_gz(String agent_gz) {
		this.agent_gz = agent_gz;
	}

	public void setHADOOP_CONF(String HADOOP_CONF) {
		this.HADOOP_CONF = HADOOP_CONF;
	}

	public void setRedis_info(String redis_info) {
		this.redis_info = redis_info;
	}

	public void setSource_path(String source_path) {
		this.source_path = source_path;
	}

	public String getDb_info() {
		return db_info;
	}

	public void setDb_info(String db_info) {
		this.db_info = db_info;
	}
}
