package hrds.commons.hadoop.readconfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosUtil;

public class C80LoginUtil {

	public enum Module {
		STORM("StormClient"), KAFKA("KafkaClient"), ZOOKEEPER("Client");

		private String name;

		private Module(String name) {

			this.name = name;
		}

		public String getName() {

			return name;
		}
	}

	private static final Log log = LogFactory.getLog(C80LoginUtil.class);

	/**
	 * line operator string
	 */
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * jaas file postfix
	 */
	private static final String JAAS_POSTFIX = ".jaas.conf";

	/**
	 * IBM jdk login module
	 */
	private static final String IBM_LOGIN_MODULE = "com.ibm.security.auth.module.Krb5LoginModule required";

	/**
	 * oracle jdk login module
	 */
	private static final String SUN_LOGIN_MODULE = "com.sun.security.auth.module.Krb5LoginModule required";

	/**
	 * java security login file path
	 */
	public static final String JAVA_SECURITY_LOGIN_CONF_KEY = "java.security.auth.login.config";

	private static final String JAVA_SECURITY_KRB5_CONF_KEY = "java.security.krb5.conf";

	private static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";

	private static final String LOGIN_FAILED_CAUSE_PASSWORD_WRONG = "(wrong password) keytab file and user not match, you can kinit -k -t keytab user in client server to check";

	private static final String LOGIN_FAILED_CAUSE_TIME_WRONG = "(clock skew) time of local server and remote server not match, please check ntp to remote server";

	private static final String LOGIN_FAILED_CAUSE_AES256_WRONG = "(aes256 not support) aes256 not support by default jdk/jre, need copy local_policy.jar and US_export_policy.jar from remote server in path /opt/huawei/Bigdata/jdk/jre/lib/security";

	private static final String LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG = "(no rule) principal format not support by default, need add property hadoop.security.auth_to_local(in core-site.xml) value RULE:[1:$1] RULE:[2:$1]";

	private static final String LOGIN_FAILED_CAUSE_TIME_OUT = "(time out) can not connect to kdc server or there is fire wall in the network";

	private static final boolean IS_IBM_JDK = System.getProperty("java.vendor").contains("IBM");

	private static final String PRNCIPAL_NAME = PropertyParaValue.getString("principle.name", "admin@HADOOP.COM");

	private static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop.hadoop.com";

	private static String PRINCIPAL = "username.client.kerberos.principal";
	private static String KEYTAB = "username.client.keytab.file";

	private static String confDir;
	public static String PATH_TO_KEYTAB;
	public static String PATH_TO_KRB5_CONF;
	public static String PATH_TO_JAAS;

	public synchronized static Configuration login(Configuration conf) throws IOException {
		if (conf == null) {
			throw new AppSystemException("初始化配置为空！");
		}
		confDir = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
		PATH_TO_KEYTAB = confDir + "user.keytab";
		PATH_TO_KRB5_CONF = confDir + "krb5.conf";
		PATH_TO_JAAS = confDir + "jaas.conf";

		String userPrincipal = PRNCIPAL_NAME;
		String userKeytabPath = PATH_TO_KEYTAB;
		String krb5ConfPath = PATH_TO_KRB5_CONF;

		//		if( !checkNeedLogin(PRNCIPAL_NAME) ) {
		//			Debug.info(log, "无需验证！");
		//			return conf;
		//		}

		// 1.check input parameters
		if ((userPrincipal == null) || (userPrincipal.length() <= 0)) {
			log.error("input userPrincipal is invalid.");
			throw new IOException("input userPrincipal is invalid.");
		}

		if (userKeytabPath.length() <= 0) {
			log.error("input userKeytabPath is invalid.");
			throw new IOException("input userKeytabPath is invalid.");
		}

		if (krb5ConfPath.length() <= 0) {
			log.error("input krb5ConfPath is invalid.");
			throw new IOException("input krb5ConfPath is invalid.");
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

		conf.set(KEYTAB, PATH_TO_KEYTAB);
		conf.set(PRINCIPAL, PRNCIPAL_NAME);
		setJaasConf("Client", PRNCIPAL_NAME, PATH_TO_KEYTAB);
		setZookeeperServerPrincipal(ZOOKEEPER_SERVER_PRINCIPAL_KEY, ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);

		// 3.set and check krb5config
		setKrb5Config(krb5ConfFile.getAbsolutePath());
		setConfiguration(conf);

		// 4.login and check for hadoop
		loginHadoop(userPrincipal, userKeytabFile.getAbsolutePath());
		log.info("Login success!!!!!!!!!!!!!!");
		return conf;
	}

	private static void setConfiguration(Configuration conf) throws IOException {

		UserGroupInformation.setConfiguration(conf);
	}

	//	private static boolean checkNeedLogin(String principal) throws IOException {
	//
	//		if( !UserGroupInformation.isSecurityEnabled() ) {
	//			log.error("UserGroupInformation is not SecurityEnabled, please check if core-site.xml exists in classpath.");
	//			throw new IOException("UserGroupInformation is not SecurityEnabled, please check if core-site.xml exists in classpath.");
	//		}
	//		UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
	//		if( (currentUser != null) && (currentUser.hasKerberosCredentials()) ) {
	//			if( checkCurrentUserCorrect(principal) ) {
	//				Debug.info(log, "current user is " + currentUser + "has logined.");
	//				if( !currentUser.isFromKeytab() ) {
	//					log.error("current user is not from keytab.");
	//					throw new IOException("current user is not from keytab.");
	//				}
	//				return false;
	//			}
	//			else {
	//				log.error("current user is " + currentUser
	//								+ "has logined. please check your enviroment , especially when it used IBM JDK or kerberos for OS count login!!");
	//				throw new IOException("current user is " + currentUser + " has logined. And please check your enviroment!!");
	//			}
	//		}
	//
	//		return true;
	//	}

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

	public static void setJaasFile(String principal, String keytabPath) throws IOException {

		String jaasPath = new File(System.getProperty("java.io.tmpdir")) + File.separator + System.getProperty("user.name") + JAAS_POSTFIX;

		// windows路径下分隔符替换
		jaasPath = jaasPath.replace("\\", "\\\\");
		keytabPath = keytabPath.replace("\\", "\\\\");
		// 删除jaas文件
		deleteJaasFile(jaasPath);
		writeJaasFile(jaasPath, principal, keytabPath);
		System.setProperty(JAVA_SECURITY_LOGIN_CONF_KEY, jaasPath);
	}

	private static void writeJaasFile(String jaasPath, String principal, String keytabPath) throws IOException {

		FileWriter writer = new FileWriter(new File(jaasPath));
		try {
			writer.write(getJaasConfContext(principal, keytabPath));
			writer.flush();
		} catch (IOException e) {
			throw new IOException("Failed to create jaas.conf File");
		} finally {
			writer.close();
		}
	}

	private static void deleteJaasFile(String jaasPath) throws IOException {

		File jaasFile = new File(jaasPath);
		if (jaasFile.exists()) {
			if (!jaasFile.delete()) {
				throw new IOException("Failed to delete exists jaas file.");
			}
		}
	}

	private static String getJaasConfContext(String principal, String keytabPath) {

		Module[] allModule = Module.values();
		StringBuilder builder = new StringBuilder();
		for (Module modlue : allModule) {
			builder.append(getModuleContext(principal, keytabPath, modlue));
		}
		return builder.toString();
	}

	private static String getModuleContext(String userPrincipal, String keyTabPath, Module module) {

		StringBuilder builder = new StringBuilder();
		if (IS_IBM_JDK) {
			builder.append(module.getName()).append(" {").append(LINE_SEPARATOR);
			builder.append(IBM_LOGIN_MODULE).append(LINE_SEPARATOR);
			builder.append("credsType=both").append(LINE_SEPARATOR);
			builder.append("principal=\"" + userPrincipal + "\"").append(LINE_SEPARATOR);
			builder.append("useKeytab=\"" + keyTabPath + "\"").append(LINE_SEPARATOR);
			builder.append("debug=true;").append(LINE_SEPARATOR);
			builder.append("};").append(LINE_SEPARATOR);
		} else {
			builder.append(module.getName()).append(" {").append(LINE_SEPARATOR);
			builder.append(SUN_LOGIN_MODULE).append(LINE_SEPARATOR);
			builder.append("useKeyTab=true").append(LINE_SEPARATOR);
			builder.append("keyTab=\"" + keyTabPath + "\"").append(LINE_SEPARATOR);
			builder.append("principal=\"" + userPrincipal + "\"").append(LINE_SEPARATOR);
			builder.append("useTicketCache=false").append(LINE_SEPARATOR);
			builder.append("storeKey=true").append(LINE_SEPARATOR);
			builder.append("debug=true;").append(LINE_SEPARATOR);
			builder.append("};").append(LINE_SEPARATOR);
		}

		return builder.toString();
	}

	public static void setJaasConf(String loginContextName, String principal, String keytabFile) throws IOException {

		if ((loginContextName == null) || (loginContextName.length() <= 0)) {
			log.error("input loginContextName is invalid.");
			throw new IOException("input loginContextName is invalid.");
		}

		if ((principal == null) || (principal.length() <= 0)) {
			log.error("input principal is invalid.");
			throw new IOException("input principal is invalid.");
		}

		if ((keytabFile == null) || (keytabFile.length() <= 0)) {
			log.error("input keytabFile is invalid.");
			throw new IOException("input keytabFile is invalid.");
		}

		File userKeytabFile = new File(keytabFile);
		if (!userKeytabFile.exists()) {
			log.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
			throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
		}

		javax.security.auth.login.Configuration
				.setConfiguration(new JaasConfiguration(loginContextName, principal, userKeytabFile.getAbsolutePath()));

		javax.security.auth.login.Configuration conf = javax.security.auth.login.Configuration.getConfiguration();
		if (!(conf instanceof JaasConfiguration)) {
			log.error("javax.security.auth.login.Configuration is not JaasConfiguration.");
			throw new IOException("javax.security.auth.login.Configuration is not JaasConfiguration.");
		}

		AppConfigurationEntry[] entrys = conf.getAppConfigurationEntry(loginContextName);
		if (entrys == null) {
			log.error("javax.security.auth.login.Configuration has no AppConfigurationEntry named " + loginContextName + ".");
			throw new IOException("javax.security.auth.login.Configuration has no AppConfigurationEntry named " + loginContextName + ".");
		}

		boolean checkPrincipal = false;
		boolean checkKeytab = false;
		for (int i = 0; i < entrys.length; i++) {
			if (entrys[i].getOptions().get("principal").equals(principal)) {
				checkPrincipal = true;
			}

			if (IS_IBM_JDK) {
				if (entrys[i].getOptions().get("useKeytab").equals(keytabFile)) {
					checkKeytab = true;
				}
			} else {
				if (entrys[i].getOptions().get("keyTab").equals(keytabFile)) {
					checkKeytab = true;
				}
			}

		}

		if (!checkPrincipal) {
			log.error("AppConfigurationEntry named " + loginContextName + " does not have principal value of " + principal + ".");
			throw new IOException("AppConfigurationEntry named " + loginContextName + " does not have principal value of " + principal + ".");
		}

		if (!checkKeytab) {
			log.error("AppConfigurationEntry named " + loginContextName + " does not have keyTab value of " + keytabFile + ".");
			throw new IOException("AppConfigurationEntry named " + loginContextName + " does not have keyTab value of " + keytabFile + ".");
		}

	}

	public static void setZookeeperServerPrincipal(String zkServerPrincipal) throws IOException {

		System.setProperty(ZOOKEEPER_SERVER_PRINCIPAL_KEY, zkServerPrincipal);
		String ret = System.getProperty(ZOOKEEPER_SERVER_PRINCIPAL_KEY);
		if (ret == null) {
			log.error(ZOOKEEPER_SERVER_PRINCIPAL_KEY + " is null.");
			throw new IOException(ZOOKEEPER_SERVER_PRINCIPAL_KEY + " is null.");
		}
		if (!ret.equals(zkServerPrincipal)) {
			log.error(ZOOKEEPER_SERVER_PRINCIPAL_KEY + " is " + ret + " is not " + zkServerPrincipal + ".");
			throw new IOException(ZOOKEEPER_SERVER_PRINCIPAL_KEY + " is " + ret + " is not " + zkServerPrincipal + ".");
		}
	}

	@Deprecated
	public static void setZookeeperServerPrincipal(String zkServerPrincipalKey, String zkServerPrincipal) throws IOException {

		System.setProperty(zkServerPrincipalKey, zkServerPrincipal);
		String ret = System.getProperty(zkServerPrincipalKey);
		if (ret == null) {
			log.error(zkServerPrincipalKey + " is null.");
			throw new IOException(zkServerPrincipalKey + " is null.");
		}
		if (!ret.equals(zkServerPrincipal)) {
			log.error(zkServerPrincipalKey + " is " + ret + " is not " + zkServerPrincipal + ".");
			throw new IOException(zkServerPrincipalKey + " is " + ret + " is not " + zkServerPrincipal + ".");
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

	//	private static void checkAuthenticateOverKrb() throws IOException {
	//
	//		UserGroupInformation loginUser = UserGroupInformation.getLoginUser();
	//		UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
	//		if( loginUser == null ) {
	//			log.error("current user is " + currentUser + ", but loginUser is null.");
	//			throw new IOException("current user is " + currentUser + ", but loginUser is null.");
	//		}
	//		if( !loginUser.equals(currentUser) ) {
	//			log.error("current user is " + currentUser + ", but loginUser is " + loginUser + ".");
	//			throw new IOException("current user is " + currentUser + ", but loginUser is " + loginUser + ".");
	//		}
	//		if( !loginUser.hasKerberosCredentials() ) {
	//			log.error("current user is " + currentUser + " has no Kerberos Credentials.");
	//			throw new IOException("current user is " + currentUser + " has no Kerberos Credentials.");
	//		}
	//		if( !UserGroupInformation.isLoginKeytabBased() ) {
	//			log.error("current user is " + currentUser + " is not Login Keytab Based.");
	//			throw new IOException("current user is " + currentUser + " is not Login Keytab Based.");
	//		}
	//	}

	//	private static boolean checkCurrentUserCorrect(String principal) throws IOException {
	//
	//		UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
	//		if( ugi == null ) {
	//			log.error("current user still null.");
	//			throw new IOException("current user still null.");
	//		}
	//
	//		String defaultRealm = null;
	//		try {
	//			defaultRealm = KerberosUtil.getDefaultRealm();
	//		}
	//		catch(Exception e) {
	//			Debug.exception(log, "getDefaultRealm failed.", e);
	//			throw new IOException(e);
	//		}
	//
	//		if( (defaultRealm != null) && (defaultRealm.length() > 0) ) {
	//			StringBuilder realm = new StringBuilder();
	//			StringBuilder principalWithRealm = new StringBuilder();
	//			realm.append("@").append(defaultRealm);
	//			if( !principal.endsWith(realm.toString()) ) {
	//				principalWithRealm.append(principal).append(realm);
	//				principal = principalWithRealm.toString();
	//			}
	//		}
	//
	//		return principal.equals(ugi.getUserName());
	//	}

	/**
	 * copy from hbase zkutil 0.94&0.98 A JAAS configuration that defines the login modules that we want to use for
	 * login.
	 */
	private static class JaasConfiguration extends javax.security.auth.login.Configuration {

		private static final Map<String, String> BASIC_JAAS_OPTIONS = new HashMap<String, String>();

		static {
			String jaasEnvVar = System.getenv("HBASE_JAAS_DEBUG");
			if (jaasEnvVar != null && "true".equalsIgnoreCase(jaasEnvVar)) {
				BASIC_JAAS_OPTIONS.put("debug", "true");
			}
		}

		private static final Map<String, String> KEYTAB_KERBEROS_OPTIONS = new HashMap<String, String>();

		static {
			if (IS_IBM_JDK) {
				KEYTAB_KERBEROS_OPTIONS.put("credsType", "both");
			} else {
				KEYTAB_KERBEROS_OPTIONS.put("useKeyTab", "true");
				KEYTAB_KERBEROS_OPTIONS.put("useTicketCache", "false");
				KEYTAB_KERBEROS_OPTIONS.put("doNotPrompt", "true");
				KEYTAB_KERBEROS_OPTIONS.put("storeKey", "true");
			}

			KEYTAB_KERBEROS_OPTIONS.putAll(BASIC_JAAS_OPTIONS);
		}

		private static final AppConfigurationEntry KEYTAB_KERBEROS_LOGIN = new AppConfigurationEntry(KerberosUtil.getKrb5LoginModuleName(),
				LoginModuleControlFlag.REQUIRED, KEYTAB_KERBEROS_OPTIONS);

		private static final AppConfigurationEntry[] KEYTAB_KERBEROS_CONF = new AppConfigurationEntry[]{KEYTAB_KERBEROS_LOGIN};

		private javax.security.auth.login.Configuration baseConfig;

		private final String loginContextName;

		private final boolean useTicketCache;

		private final String keytabFile;

		private final String principal;

		public JaasConfiguration(String loginContextName, String principal, String keytabFile) throws IOException {

			this(loginContextName, principal, keytabFile, keytabFile == null || keytabFile.length() == 0);
		}

		private JaasConfiguration(String loginContextName, String principal, String keytabFile, boolean useTicketCache) throws IOException {

			try {
				this.baseConfig = javax.security.auth.login.Configuration.getConfiguration();
			} catch (SecurityException e) {
				this.baseConfig = null;
			}
			this.loginContextName = loginContextName;
			this.useTicketCache = useTicketCache;
			this.keytabFile = keytabFile;
			this.principal = principal;

			initKerberosOption();
			log.info("JaasConfiguration loginContextName=" + loginContextName + " principal=" + principal + " useTicketCache=" + useTicketCache
					+ " keytabFile=" + keytabFile);
		}

		private void initKerberosOption() throws IOException {

			if (!useTicketCache) {
				if (IS_IBM_JDK) {
					KEYTAB_KERBEROS_OPTIONS.put("useKeytab", keytabFile);
				} else {
					KEYTAB_KERBEROS_OPTIONS.put("keyTab", keytabFile);
					KEYTAB_KERBEROS_OPTIONS.put("useKeyTab", "true");
					KEYTAB_KERBEROS_OPTIONS.put("useTicketCache", useTicketCache ? "true" : "false");
				}
			}
			KEYTAB_KERBEROS_OPTIONS.put("principal", principal);
		}

		public AppConfigurationEntry[] getAppConfigurationEntry(String appName) {

			if (loginContextName.equals(appName)) {
				return KEYTAB_KERBEROS_CONF;
			}
			if (baseConfig != null)
				return baseConfig.getAppConfigurationEntry(appName);
			return (null);
		}
	}

	public static void main(String[] args) {

		//		ConfigReader.getConfiguration();
		try {
			C80LoginUtil.login(ConfigReader.getConfiguration());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
