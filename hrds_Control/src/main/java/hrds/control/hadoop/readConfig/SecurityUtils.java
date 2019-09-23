package hrds.control.hadoop.readConfig;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.security.UserGroupInformation;

public class SecurityUtils {

	/**
	 * log process object
	 */
	private static final Log log = LogFactory.getLog(SecurityUtils.class);
	private static String PRINCIPAL = "username.client.kerberos.principal";
	private static String KEYTAB = "username.client.keytab.file";

	/**
	 * get conf object
	 * 
	 * @return Configuration
	 */
	public static Configuration getConfiguration() {

		// Default load from conf directory
		Configuration conf = HBaseConfiguration.create();

		String userdir = System.getProperty("user.dir") + File.separator + "conf" + File.separator;

		conf.addResource(new Path(userdir + "yarn-site.xml"));
		conf.addResource(new Path(userdir + "mapred-site.xml"));
		conf.addResource(new Path(userdir + "core-site.xml"));
		conf.addResource(new Path(userdir + "hbase-site.xml"));
		conf.addResource(new Path(userdir + "hdfs-site.xml"));
		conf.addResource(new Path(userdir + "user-mapred.xml"));

		return conf;
	}

	/**
	 * security login
	 * 
	 * @return boolean
	 */
	public static Boolean Maplogin(Configuration conf) {

		boolean flag = true;

		try {
			// security mode
			if( "kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication")) ) {
				conf.set(PRINCIPAL, conf.get(PRINCIPAL));
				// keytab file
				conf.set(KEYTAB, conf.get(KEYTAB));
				// kerberos path
				String krbfilepath = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "krb5.conf";
				System.setProperty("java.security.krb5.conf", krbfilepath);
				flag = loginFromKeytab(conf);
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;

	}

	/**
	 * HBase login
	 * @param keytabFileName
	 * @param principalName
	 * @return
	 * @throws Exception
	 */
	public static Configuration HBaselogin(Configuration conf, String keytabFileName, String principalName) throws Exception {

		if( User.isHBaseSecurityEnabled(conf) ) {
			log.info("Start to login HBase...");
			String confDirPath = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
			// jaas.conf file, it is included in the client pakcage file
			System.setProperty("java.security.auth.login.config", confDirPath + "jaas.conf");
			// set the kerberos server info,point to the kerberosclient
			System.setProperty("java.security.krb5.conf", confDirPath + "krb5.conf");
			// set the keytab file name
			conf.set(KEYTAB, confDirPath + keytabFileName);
			// set the user's principal
			conf.set(PRINCIPAL, principalName);
			try {
				User.login(conf, KEYTAB, PRINCIPAL, InetAddress.getLocalHost().getCanonicalHostName());
			}
			catch(Exception e) {
				throw new Exception("Login failed.");
			}
		}
		return conf;
	}
	
	/**
	* kerberos security authentication
	*/
	public static Configuration authentication(Configuration conf) throws IOException {

		// security mode
		if( "kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication")) ) {

			String principalName = "username.client.kerberos.principal";
			String keytabFileName = "username.client.keytab.file";
			//注[1]
			conf.set(principalName, "admin@HADOOP.COM");
			// keytab file
			conf.set(keytabFileName, System.getProperty("user.dir") + File.separator + "conf" + File.separator + "user.keytab");
			// kerberos path
			String krbfilepath = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "krb5.conf";
			System.setProperty("java.security.krb5.conf", krbfilepath);
			//login
			UserGroupInformation.setConfiguration(conf);
			try {
				UserGroupInformation.loginUserFromKeytab(conf.get(principalName), conf.get(keytabFileName));
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return conf;
	}

	/**
	*
	* Add configuration file
	*/
	public static Configuration confLoad(Configuration conf) throws IOException {

		conf = new Configuration();
		// conf file
		conf.addResource(new Path(System.getProperty("user.dir") + File.separator + "conf" + File.separator + "hdfs-site.xml"));
		conf.addResource(new Path(System.getProperty("user.dir") + File.separator + "conf" + File.separator + "core-site.xml"));
		return conf;
	}

	public static Boolean loginFromKeytab(Configuration conf) {

		boolean flag = false;
		UserGroupInformation.setConfiguration(conf);

		try {
			System.out.println(conf.get(KEYTAB));
			UserGroupInformation.loginUserFromKeytab(conf.get(PRINCIPAL), conf.get(KEYTAB));
			log.info("Login successfully.");

			flag = true;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return flag;

	}

}
