package hrds.commons.hadoop.readconfig;

import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.PropertyParaUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosUtil;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginUtil {

    private static final Log log = LogFactory.getLog(LoginUtil.class);

    private String confDir;
    public String PATH_TO_KEYTAB;
    public String PATH_TO_KRB5_CONF;
    public String PATH_TO_JAAS;
    private String PATH_TO_CORE_SITE_XML;
    private String PATH_TO_HDFS_SITE_XML;
    private String PATH_TO_HBASE_SITE_XML;
    private String PATH_TO_MAPRED_SITE_XML;
    private String PATH_TO_YARN_SITE_XML;

    private static final String ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME = "Client";
    public static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";
    public static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop";

    private static final String JAVA_SECURITY_KRB5_CONF_KEY = "java.security.krb5.conf";

    private static final String LOGIN_FAILED_CAUSE_PASSWORD_WRONG = "(wrong password) keytab file and user not match, you can kinit -k -t keytab user in client server to check";

    private static final String LOGIN_FAILED_CAUSE_TIME_WRONG = "(clock skew) time of local server and remote server not match, please check ntp to remote server";

    private static final String LOGIN_FAILED_CAUSE_AES256_WRONG = "(aes256 not support) aes256 not support by default jdk/jre, need copy local_policy.jar and US_export_policy.jar from remote server in path /opt/huawei/Bigdata/jdk/jre/lib/security";

    private static final String LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG = "(no rule) principal format not support by default, need add property hadoop.security.auth_to_local(in core-site.xml) value RULE:[1:$1] RULE:[2:$1]";

    private static final String LOGIN_FAILED_CAUSE_TIME_OUT = "(time out) can not connect to kdc server or there is fire wall in the network";

    private static final boolean IS_IBM_JDK = System.getProperty("java.vendor").contains("IBM");

    public LoginUtil(String configPath) {
        this.confDir = configPath;
        PATH_TO_KEYTAB = confDir + "user.keytab";
        PATH_TO_KRB5_CONF = confDir + "krb5.conf";
        PATH_TO_JAAS = confDir + "jaas.conf";
        PATH_TO_CORE_SITE_XML = confDir + "core-site.xml";
        PATH_TO_HDFS_SITE_XML = confDir + "hdfs-site.xml";
        PATH_TO_HBASE_SITE_XML = confDir + "hbase-site.xml";
        PATH_TO_MAPRED_SITE_XML = confDir + "mapred-site.xml";
        PATH_TO_YARN_SITE_XML = confDir + "yarn-site.xml";
    }

    public LoginUtil() {
        this(System.getProperty("user.dir") + File.separator + "conf" + File.separator);
    }

    public synchronized static void login(String userPrincipal, String userKeytabPath, String krb5ConfPath, Configuration conf) throws IOException {

        // 1.check input parameters
        if ((userPrincipal == null) || (userPrincipal.length() <= 0)) {
            log.error("input userPrincipal is invalid.");
            throw new IOException("input userPrincipal is invalid.");
        }

        if ((userKeytabPath == null) || (userKeytabPath.length() <= 0)) {
            log.error("input userKeytabPath is invalid.");
            throw new IOException("input userKeytabPath is invalid.");
        }

        if ((krb5ConfPath == null) || (krb5ConfPath.length() <= 0)) {
            log.error("input krb5ConfPath is invalid.");
            throw new IOException("input krb5ConfPath is invalid.");
        }

        if ((conf == null)) {
            log.error("input conf is invalid.");
            throw new IOException("input conf is invalid.");
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

        // 3.set and check krb5config
        setKrb5Config(krb5ConfFile.getAbsolutePath());
        setConfiguration(conf);

        // 4.login and check for hadoop
        loginHadoop(userPrincipal, userKeytabFile.getAbsolutePath());
        log.info("Login success!!!!!!!!!!!!!!");
    }

    private static void setConfiguration(Configuration conf) throws IOException {

        UserGroupInformation.setConfiguration(conf);
    }

    /**
     * Add configuration file if the application run on the linux ,then need
     * make the path of the core-site.xml and hdfs-site.xml to in the linux
     * client file
     */
    public Configuration confLoad(Configuration conf) throws IOException {

        conf = HBaseConfiguration.create();
        conf.setBoolean("fs.hdfs.impl.disable.cache", true);
        // conf file
        conf.addResource(new Path(PATH_TO_CORE_SITE_XML));
        conf.addResource(new Path(PATH_TO_HDFS_SITE_XML));
        conf.addResource(new Path(PATH_TO_HBASE_SITE_XML));
        conf.addResource(new Path(PATH_TO_MAPRED_SITE_XML));
        conf.addResource(new Path(PATH_TO_YARN_SITE_XML));

        return conf;
    }

    /**
     * kerberos security authentication if the application running on Linux,need
     * the path of the krb5.conf and keytab to edit to absolute path in Linux.
     * make the keytab and principal in example to current user's keytab and
     * username
     */
    public Configuration authentication(Configuration conf) throws IOException {
        System.setProperty("java.security.krb5.conf", PATH_TO_KRB5_CONF);
        login(CommonVariables.PRINCIPLE_NAME, PATH_TO_KEYTAB, PATH_TO_KRB5_CONF, conf);
        return conf;
    }

    public Configuration hbaseLogin(Configuration conf) throws IOException {

        System.setProperty("java.security.auth.login.config", confDir + "jaas.conf");

        //		if( User.isHBaseSecurityEnabled(conf) ) {
        /*
         * if need to connect zk, please provide jaas info about zk. of course,
         * you can do it as below:
         * System.setProperty("java.security.auth.login.config", confDirPath +
         * "jaas.conf"); but the demo can help you more : Note: if this process
         * will connect more than one zk cluster, the demo may be not proper. you
         * can contact us for more help
         */
        LoginUtil.setJaasConf(ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME, CommonVariables.PRINCIPLE_NAME, PATH_TO_KEYTAB);
        LoginUtil.setZookeeperServerPrincipal(ZOOKEEPER_SERVER_PRINCIPAL_KEY, ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
        login(CommonVariables.PRINCIPLE_NAME, PATH_TO_KEYTAB, PATH_TO_KRB5_CONF, conf);
        return conf;
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

    /**
     * copy from hbase zkutil 0.94&0.98 A JAAS configuration that defines the
     * login modules that we want to use for login.
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
}
