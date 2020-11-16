package hrds.commons.hadoop.loginAuth.impl;

import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.loginAuth.ILoginAuth;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * 认证接口实现
 */
public abstract class LoginAuthImpl implements ILoginAuth {

	protected static final Logger logger = LogManager.getLogger();

	public String PATH_TO_KEYTAB;
	public String PATH_TO_KRB5_CONF;
	public String PATH_TO_JAAS;
	public String PATH_TO_CORE_SITE_XML;
	public String PATH_TO_HDFS_SITE_XML;
	public String PATH_TO_HBASE_SITE_XML;
	public String PATH_TO_MAPRED_SITE_XML;
	public String PATH_TO_YARN_SITE_XML;

	public static final String ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME = "Client";
	public static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";
	public static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop";
	public static final String JAVA_SECURITY_KRB5_CONF_KEY = "java.security.krb5.conf";
	public static final String LOGIN_FAILED_CAUSE_PASSWORD_WRONG = "(wrong password) keytab file and user not match, you can kinit -k -t keytab user in client server to check";
	public static final String LOGIN_FAILED_CAUSE_TIME_WRONG = "(clock skew) time of local server and remote server not match, please check ntp to remote server";
	public static final String LOGIN_FAILED_CAUSE_AES256_WRONG = "(aes256 not support) aes256 not support by default jdk/jre, need copy local_policy.jar and US_export_policy.jar from remote server in path /opt/huawei/Bigdata/jdk/jre/lib/security";
	public static final String LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG = "(no rule) principal format not support by default, need add property hadoop.security.auth_to_local(in core-site.xml) value RULE:[1:$1] RULE:[2:$1]";
	public static final String LOGIN_FAILED_CAUSE_TIME_OUT = "(time out) can not connect to kdc server or there is fire wall in the network";
	public static final boolean IS_IBM_JDK = System.getProperty("java.vendor").contains("IBM");

	/**
	 * 认证接口实现
	 */
	public LoginAuthImpl() {
	}

	/**
	 * 登录方法,使用指定的认证实体名
	 *
	 * @param principle_name 认证实体名
	 * @return Configuration
	 */
	public synchronized Configuration login(String principle_name) {
		return login(principle_name, loadConf());
	}

	/**
	 * 登录方法,使用指定的认证实体名和Configuration对象
	 *
	 * @param principle_name 认证实体名
	 * @param conf           Configuration
	 * @return Configuration
	 */
	public synchronized Configuration login(String principle_name, Configuration conf) {
		return login(principle_name, PATH_TO_KEYTAB, PATH_TO_KRB5_CONF, conf);
	}

	/**
	 * 登录认证,使用指定的认证实体名,kerberos,hrb5,Configuration对象
	 *
	 * @param principle_name 认证实体
	 * @param keytabPath     keytab文件路径
	 * @param krb5ConfPath   krb5文件路径
	 * @param conf           Configuration对象
	 */
	public synchronized Configuration login(String principle_name, String keytabPath, String krb5ConfPath, Configuration conf) {
		// 1.check input parameters
		if ((principle_name == null) || (principle_name.length() <= 0)) {
			logger.error("input principle_name is invalid.");
			throw new BusinessException("input principle_name is invalid.");
		}
		if ((keytabPath == null) || (keytabPath.length() <= 0)) {
			logger.error("input keytabPath is invalid.");
			throw new BusinessException("input keytabPath is invalid.");
		}
		if ((krb5ConfPath == null) || (krb5ConfPath.length() <= 0)) {
			logger.error("input krb5ConfPath is invalid.");
			throw new BusinessException("input krb5ConfPath is invalid.");
		}
		if ((conf == null)) {
			logger.error("input conf is invalid.");
			throw new BusinessException("input conf is invalid.");
		}
		// 2.check file exsits
		File userKeytabFile = new File(keytabPath);
		if (!userKeytabFile.exists()) {
			logger.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
			throw new BusinessException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
		}
		if (!userKeytabFile.isFile()) {
			logger.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
			throw new BusinessException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
		}
		// 3.set krb5config
		File krb5ConfFile = new File(krb5ConfPath);
		if (!krb5ConfFile.exists()) {
			logger.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
			throw new BusinessException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
		}
		if (!krb5ConfFile.isFile()) {
			logger.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
			throw new BusinessException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
		}
		setKrb5Config(krb5ConfFile.getAbsolutePath());
		// 4.set configuration
		setConfiguration(conf);
		// 5 set zookeeperServerPrincipal
		setZookeeperServerPrincipal(ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
		// 6.login and check for hadoop
		loginHDFS(principle_name, userKeytabFile.getAbsolutePath());
		logger.info("Login success!!!!!!!!!!!!!!");
		//返回conf
		return conf;
	}

	/**
	 * 设置krb5配置
	 *
	 * @param krb5ConfFile krb5配置文件
	 */
	protected void setKrb5Config(String krb5ConfFile) {
		System.setProperty(JAVA_SECURITY_KRB5_CONF_KEY, krb5ConfFile);
		String ret = System.getProperty(JAVA_SECURITY_KRB5_CONF_KEY);
		if (ret == null) {
			logger.error(JAVA_SECURITY_KRB5_CONF_KEY + " is null.");
			throw new BusinessException(JAVA_SECURITY_KRB5_CONF_KEY + " is null.");
		}
		if (!ret.equals(krb5ConfFile)) {
			logger.error(JAVA_SECURITY_KRB5_CONF_KEY + " is " + ret + " is not " + krb5ConfFile + ".");
			throw new BusinessException(JAVA_SECURITY_KRB5_CONF_KEY + " is " + ret + " is not " + krb5ConfFile + ".");
		}
	}

	/**
	 * 设置 Configuration
	 *
	 * @param conf Configuration对象
	 */
	protected void setConfiguration(Configuration conf) {
		UserGroupInformation.setConfiguration(conf);
	}

	/**
	 * 设置 ZookeeperServerPrincipal
	 *
	 * @param zkServerPrincipal zk认证实体 zkServerPrincipal
	 */
	protected void setZookeeperServerPrincipal(String zkServerPrincipal) {
		String zkServerPrincipalKey = ZOOKEEPER_SERVER_PRINCIPAL_KEY;
		System.setProperty(zkServerPrincipalKey, zkServerPrincipal);
		String ret = System.getProperty(zkServerPrincipalKey);
		if (ret == null) {
			logger.error(zkServerPrincipalKey + " is null.");
			throw new BusinessException(zkServerPrincipalKey + " is null.");
		}
		if (!ret.equals(zkServerPrincipal)) {
			logger.error(zkServerPrincipalKey + " is " + ret + " is not " + zkServerPrincipal + ".");
			throw new BusinessException(zkServerPrincipalKey + " is " + ret + " is not " + zkServerPrincipal + ".");
		}
	}

	/**
	 * 从密钥表文件登录用户。从keytab 文件加载用户身份并登录。他们成为当前登录的用户。
	 *
	 * @param principal  用户要从密钥表加载的主体名称
	 * @param keytabFile 密钥表文件的路径
	 */
	protected void loginHDFS(String principal, String keytabFile) {
		try {
			UserGroupInformation.loginUserFromKeytab(principal, keytabFile);
		} catch (IOException ioe) {
			logger.error("login failed with " + principal + " and " + keytabFile + ".");
			logger.error("perhaps cause 1 is " + LOGIN_FAILED_CAUSE_PASSWORD_WRONG + ".");
			logger.error("perhaps cause 2 is " + LOGIN_FAILED_CAUSE_TIME_WRONG + ".");
			logger.error("perhaps cause 3 is " + LOGIN_FAILED_CAUSE_AES256_WRONG + ".");
			logger.error("perhaps cause 4 is " + LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG + ".");
			logger.error("perhaps cause 5 is " + LOGIN_FAILED_CAUSE_TIME_OUT + ".");
			throw new BusinessException("keytab file authentication failed!" + ioe);
		}
	}

	/**
	 * Add configuration file if the application run on the linux ,then need
	 * make the path of the core-site.xml and hdfs-site.xml to in the linux
	 * client file
	 */
	protected Configuration loadConf() {
		Configuration conf = HBaseConfiguration.create();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		conf.addResource(new Path(PATH_TO_CORE_SITE_XML));
		conf.addResource(new Path(PATH_TO_HDFS_SITE_XML));
		conf.addResource(new Path(PATH_TO_HBASE_SITE_XML));
		conf.addResource(new Path(PATH_TO_MAPRED_SITE_XML));
		conf.addResource(new Path(PATH_TO_YARN_SITE_XML));
		return conf;
	}
}
