package hrds.commons.hadoop.solr;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.CommonVariables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@DocClass(desc = "solr登录验证", author = "BY-HLL", createdate = "2020/1/14 0014 上午 11:32")
public class SolrLogin {

    public enum Module {
        STORM("StormClient"),
        KAFKA("KafkaClient"),
        ZOOKEEPER("Client"),
        SolrJClient("SolrJClient ");

        private String name;

        Module(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /* line operator string */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /* jaas file postfix */
    private static final String JAAS_POSTFIX = ".jaas.conf";
    /* is IBM jdk or not */
    private static final boolean IS_IBM_JDK = System.getProperty("java.vendor").contains("IBM");
    /* IBM jdk login module */
    private static final String IBM_LOGIN_MODULE = "com.ibm.security.auth.module.Krb5LoginModule required";
    /* oracle jdk login module */
    private static final String SUN_LOGIN_MODULE = "com.sun.security.auth.module.Krb5LoginModule required";
    /* Zookeeper quorum principal */
    public static final String ZOOKEEPER_AUTH_PRINCIPAL = "zookeeper.server.principal";
    /* java security krb5 file path */
    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    /* java security login file path */
    public static final String JAVA_SECURITY_LOGIN_CONF = "java.security.auth.login.config";

    @Method(desc = "设置jaas.conf文件",
            logicStep = "1.设置jaas.conf文件的全路径地址" +
                    "2.windows路径下分隔符替换" +
                    "3.删除原有jaas文件" +
                    "4.写入新jaas.conf文件")
    @Param(name = "keyTabPath", desc = "keyTab文件路径", range = "keyTab文件路径")
    public static void setJaasFile(String keyTabPath) {
        //1.设置jaas.conf文件的全路径地址
        String jaasPath = new File(System.getProperty("java.io.tmpdir"))
                + File.separator + System.getProperty("user.name") + JAAS_POSTFIX;
        //2.windows路径下分隔符替换
        jaasPath = jaasPath.replace("\\", "\\\\");
        //3.删除原有jaas文件
        deleteJaasFile(jaasPath);
        //4.写入新jaas.conf文件
        writeJaasFile(jaasPath, keyTabPath);
        System.setProperty(JAVA_SECURITY_LOGIN_CONF, jaasPath);
    }

    @Method(desc = "设置zookeeper服务端principal",
            logicStep = "1.设置zookeeper服务端principal")
    @Param(name = "zkServerPrincipal", desc = "zookeeper服务认证实例名", range = "zookeeper")
    public static void setZookeeperServerPrincipal(String zkServerPrincipal) {
        //1.设置zookeeper服务端principal
        System.setProperty(ZOOKEEPER_AUTH_PRINCIPAL, zkServerPrincipal);
        String ret = System.getProperty(ZOOKEEPER_AUTH_PRINCIPAL);
        if (ret == null) {
            throw new BusinessException(ZOOKEEPER_AUTH_PRINCIPAL + " is null.");
        }
        if (!ret.equals(zkServerPrincipal)) {
            throw new BusinessException(ZOOKEEPER_AUTH_PRINCIPAL + " is " + ret
                    + " is not " + zkServerPrincipal + ".");
        }
    }

    @Method(desc = "设置krb5文件",
            logicStep = "1.设置krb5文件")
    @Param(name = "krb5ConfFile", desc = "krb5配置文件路径", range = "krb5配置文件路径")
    public static void setKrb5Config(String krb5ConfFile) {
        System.setProperty(JAVA_SECURITY_KRB5_CONF, krb5ConfFile);
        String ret = System.getProperty(JAVA_SECURITY_KRB5_CONF);
        if (ret == null) {
            throw new BusinessException(JAVA_SECURITY_KRB5_CONF + " is null.");
        }
        if (!ret.equals(krb5ConfFile)) {
            throw new BusinessException(JAVA_SECURITY_KRB5_CONF + " is " + ret + " is not " + krb5ConfFile + ".");
        }
    }

    /* 写入jaas文件 */
    private static void writeJaasFile(String jaasPath, String keytabPath) {
        try (FileWriter writer = new FileWriter(new File(jaasPath))) {
            writer.write(getJaasConfContext(keytabPath));
            writer.flush();
        } catch (IOException e) {
            throw new BusinessException("Failed to create jaas.conf File.");
        }
    }

    /* 删除jaas文件 */
    private static void deleteJaasFile(String jaasPath) {
        File jaasFile = new File(jaasPath);
        if (jaasFile.exists()) {
            if (!jaasFile.delete()) {
                throw new BusinessException("Failed to delete exists jaas file.");
            }
        }
    }

    /* 获取jaas文件内容 */
    private static String getJaasConfContext(String keytabPath) {
        Module[] allModule = Module.values();
        StringBuilder builder = new StringBuilder();
        for (Module module : allModule) {
            builder.append(getModuleContext(keytabPath, module));
        }
        return builder.toString();
    }

    /* 根据module名,获取jaas内容 */
    private static String getModuleContext(String keyTabPath, Module module) {
        StringBuilder builder = new StringBuilder();
        if (IS_IBM_JDK) {
            builder.append(module.getName()).append(" {").append(LINE_SEPARATOR);
            builder.append(IBM_LOGIN_MODULE).append(LINE_SEPARATOR);
            builder.append("credsType=both").append(LINE_SEPARATOR);
            builder.append("principal=\"").append(CommonVariables.PRINCIPLE_NAME).append("\"").append(LINE_SEPARATOR);
            builder.append("useKeytab=\"").append(keyTabPath).append("\"").append(LINE_SEPARATOR);
            builder.append("debug=true;").append(LINE_SEPARATOR);
            builder.append("};").append(LINE_SEPARATOR);
        } else {
            builder.append(module.getName()).append(" {").append(LINE_SEPARATOR);
            builder.append(SUN_LOGIN_MODULE).append(LINE_SEPARATOR);
            builder.append("useKeyTab=true").append(LINE_SEPARATOR);
            builder.append("keyTab=\"").append(keyTabPath).append("\"").append(LINE_SEPARATOR);
            builder.append("principal=\"").append(CommonVariables.PRINCIPLE_NAME).append("\"").append(LINE_SEPARATOR);
            builder.append("useTicketCache=false").append(LINE_SEPARATOR);
            builder.append("storeKey=true").append(LINE_SEPARATOR);
            builder.append("debug=false;").append(LINE_SEPARATOR);
            builder.append("};").append(LINE_SEPARATOR);
        }
        return builder.toString();
    }
}
