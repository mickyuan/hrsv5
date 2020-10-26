package hrds.commons.hadoop.readconfig;

import hrds.commons.exception.AppSystemException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;
import java.io.IOException;

/**
 * CDH User Authentication
 *
 * @author BY-HLL
 */
public class CDHLoginUtil {

	private static final Log log = LogFactory.getLog(CDHLoginUtil.class);
	/**
	 * java security login file path
	 */
	public static final String JAVA_SECURITY_LOGIN_CONF_KEY = "java.security.auth.login.config";
	private static final String JAVA_SECURITY_KRB5_CONF_KEY = "java.security.krb5.conf";
	private static final String LOGIN_FAILED_CAUSE_PASSWORD_WRONG = "(wrong password) keytab file and user not match, you can kinit -k -t keytab user in client server to check";
	private static final String LOGIN_FAILED_CAUSE_TIME_WRONG = "(clock skew) time of local server and remote server not match, please check ntp to remote server";
	private static final String LOGIN_FAILED_CAUSE_AES256_WRONG = "(aes256 not support) aes256 not support by default jdk/jre, need copy local_policy.jar and US_export_policy.jar from remote server in path /opt/huawei/Bigdata/jdk/jre/lib/security";
	private static final String LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG = "(no rule) principal format not support by default, need add property hadoop.security.auth_to_local(in core-site.xml) value RULE:[1:$1] RULE:[2:$1]";
	private static final String LOGIN_FAILED_CAUSE_TIME_OUT = "(time out) can not connect to kdc server or there is fire wall in the network";

	public static String PATH_TO_KEYTAB;
	public static String PATH_TO_KRB5_CONF;
	public static String PATH_TO_JAAS;

	/**
	 * 读取配置文件并认证
	 *
	 * @param conf 配置文件
	 */
	public synchronized static Configuration login(Configuration conf, String prncipal_name) throws IOException {
		if (conf == null) {
			throw new AppSystemException("初始化配置为空！");
		}
		String confDir = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
		PATH_TO_KEYTAB = confDir + "user.keytab";
		PATH_TO_KRB5_CONF = confDir + "krb5.conf";
		PATH_TO_JAAS = confDir + "jaas.conf";

		String userKeytabPath = PATH_TO_KEYTAB;
		String krb5ConfPath = PATH_TO_KRB5_CONF;

		// 1.check input parameters
		if ((prncipal_name == null) || (prncipal_name.length() <= 0)) {
			log.error("input userPrincipal is invalid.");
			throw new IOException("input userPrincipal is invalid.");
		}
		// 2.check file exsits
		File userKeytabFile = new File(userKeytabPath);
		if (!userKeytabFile.exists()) {
			log.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
			throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
		}
		if (!userKeytabFile.isFile()) {
			log.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
			throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
		}
		File krb5ConfFile = new File(krb5ConfPath);
		if (!krb5ConfFile.exists()) {
			log.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
			throw new IOException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
		}
		if (!krb5ConfFile.isFile()) {
			log.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
			throw new IOException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
		}

		System.setProperty("java.security.krb5.conf", PATH_TO_KRB5_CONF);

		String KEYTAB = "username.client.keytab.file";
		conf.set(KEYTAB, PATH_TO_KEYTAB);
		String PRINCIPAL = "username.client.kerberos.principal";
		conf.set(PRINCIPAL, prncipal_name);

		// 3.set and check krb5config
		setKrb5Config(krb5ConfFile.getAbsolutePath());
		setConfiguration(conf);

		// 4.login and check for hadoop
		loginHadoop(prncipal_name, userKeytabFile.getAbsolutePath());
		log.info("Login success!!!!!!!!!!!!!!");
		return conf;
	}

	private static void setConfiguration(Configuration conf) {
		UserGroupInformation.setConfiguration(conf);
	}

	public static void setKrb5Config(String krb5ConfFile) throws IOException {

		System.setProperty(JAVA_SECURITY_KRB5_CONF_KEY, krb5ConfFile);
		String ret = System.getProperty(JAVA_SECURITY_KRB5_CONF_KEY);
		if (ret == null) {
			log.error(JAVA_SECURITY_KRB5_CONF_KEY + " is null.");
			throw new IOException(JAVA_SECURITY_KRB5_CONF_KEY + " is null.");
		}
		if (!ret.equals(krb5ConfFile)) {
			log.error(JAVA_SECURITY_KRB5_CONF_KEY + " is " + ret + " is not " + krb5ConfFile + ".");
			throw new IOException(JAVA_SECURITY_KRB5_CONF_KEY + " is " + ret + " is not " + krb5ConfFile + ".");
		}
	}

	private static void loginHadoop(String principal, String keytabFile) throws IOException {

		try {
			UserGroupInformation.loginUserFromKeytab(principal, keytabFile);
		} catch (IOException e) {
			log.error("login failed with " + principal + " and " + keytabFile + ".");
			log.error("perhaps cause 1 is " + LOGIN_FAILED_CAUSE_PASSWORD_WRONG + ".");
			log.error("perhaps cause 2 is " + LOGIN_FAILED_CAUSE_TIME_WRONG + ".");
			log.error("perhaps cause 3 is " + LOGIN_FAILED_CAUSE_AES256_WRONG + ".");
			log.error("perhaps cause 4 is " + LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG + ".");
			log.error("perhaps cause 5 is " + LOGIN_FAILED_CAUSE_TIME_OUT + ".");

			throw e;
		}
	}

}
