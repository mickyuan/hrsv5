package hrds.commons.hadoop.readconfig;

import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

public class ConfigReader {

	public enum PlatformType {
		normal, fic50, fic60, fic80, cdh5_13
	}

	private static final Log log = LogFactory.getLog(ConfigReader.class);

	private ConfigReader() {

	}

	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration() {
		//平台验证
		return getConfiguration(null);
	}

	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration(String configPath) {
		return getConfiguration(configPath, null);
	}

	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration(String configPath, String platform) {
		return getConfiguration(configPath, platform, null);
	}

	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration(String configPath, String platform, String prncipal_name) {
		return getConfiguration(configPath, platform, prncipal_name, null);
	}

	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration(String configPath, String platform,
	                                             String prncipal_name, String hadoop_user_name) {
		try {
			log.info("configReader  " + configPath);
			LoginUtil lg;
			if (StringUtil.isEmpty(configPath)) {
				lg = new LoginUtil();
			} else {
				lg = new LoginUtil(configPath);
			}
			Configuration conf = lg.confLoad();
			if (platform == null) {
				platform = PropertyParaValue.getString("platform", ConfigReader.PlatformType.normal.toString());
			}
			if (prncipal_name == null) {
				prncipal_name = PropertyParaValue.getString("principle.name", "admin@HADOOP.COM");
			}
			if (hadoop_user_name == null) {
				hadoop_user_name = PropertyParaValue.getString("HADOOP_USER_NAME", "hyshf");
			}
			//平台验证
			return initAuth(conf, platform, prncipal_name, hadoop_user_name, lg);
		} catch (Exception e) {
			log.error("Failed to verify the platform...", e);
			throw new AppSystemException("认证获取conf失败", e);
		}
	}

//	public static Configuration initAuthExternal(Configuration conf, String platform) {
//
//		log.info("initAuth external_cluster: " + external_cluster);
//
//		try {
//			if (external_cluster.equals(PlatformType.normal.toString())) {
//
//			} else if (external_cluster.equals(PlatformType.fic50.toString())) {
//				conf = SecurityUtils.HBaselogin(conf, "user.keytab", "admin@Hadoop");
//				if (!SecurityUtils.Maplogin(conf)) {// Security login
//					log.error("Login system failed");
//				}
//			} else if (external_cluster.equals(PlatformType.fic60.toString())) {
//				conf = lg.hbaseLogin(conf);
//			} else {
//				throw new AppSystemException("The platform is a wrong type ,please check the syspara table for the argument <platform>...");
//			}
//			return conf;
//		} catch (Exception e) {
//			log.error("external_cluster Failed to initAuth...", e);
//			return null;
//		}
//	}

	private static Configuration initAuth(Configuration conf, String platform,
	                                      String prncipal_name, String hadoop_user_name, LoginUtil lg) {
		log.info("initAuth platform: " + platform);
		try {
			if (PlatformType.normal.toString().equals(platform)) {
				log.info("Do nothing");
			} else if (PlatformType.cdh5_13.toString().equals(platform)) {
				conf = CDHLoginUtil.login(conf, prncipal_name);
			} else if (PlatformType.fic50.toString().equals(platform)) {
				conf = SecurityUtils.HBaselogin(conf, "user.keytab", "admin@Hadoop");
				if (!SecurityUtils.Maplogin(conf)) {// Security login
					log.error("Login system failed");
				}
			} else if (PlatformType.fic60.toString().equals(platform)) {
				conf = lg.hbaseLogin(conf,prncipal_name);
			} else if (PlatformType.fic80.toString().equals(platform)) {
				conf = C80LoginUtil.login(conf, prncipal_name);
			} else {
				throw new AppSystemException("The platform is a wrong type ,please check <platform>...");
			}
			//设置hadoop_user_name
			conf.set("HADOOP_USER_NAME", hadoop_user_name);
			return conf;
		} catch (Exception e) {
			log.error("Failed to initAuth...", e);
			throw new AppSystemException("认证获取conf失败", e);
		}
	}

	public static void main(String[] args) {

		ConfigReader.getConfiguration();
	}
}
