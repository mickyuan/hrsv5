package hrds.commons.hadoop.readconfig;

import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

public class ConfigReader {

	public enum PlatformType {
		normal, fic50, fic60, fic80, cdh5_13
	}

	private static final Log log = LogFactory.getLog(ConfigReader.class);

	private static Configuration conf;

	private static LoginUtil lg;

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
		return getConfiguration(configPath, PlatformType.normal.toString());
	}

	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration(String configPath, String platform) {

		try {
			log.info("configReader  " + configPath);
			if (StringUtil.isEmpty(configPath)) {
				lg = new LoginUtil();
			} else {
				lg = new LoginUtil(configPath);
			}
			conf = lg.confLoad(conf);
			//平台验证
			return initAuth(conf, platform);
		} catch (Exception e) {
			log.error("Failed to verify the platform...", e);
			return null;
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
//				throw new BusinessException("The platform is a wrong type ,please check the syspara table for the argument <platform>...");
//			}
//			return conf;
//		} catch (Exception e) {
//			log.error("external_cluster Failed to initAuth...", e);
//			return null;
//		}
//	}

	private static Configuration initAuth(Configuration conf, String platform) {
		log.info("initAuth platform: " + platform);
		if (null == lg) {
			lg = new LoginUtil();
		}
		try {
			if (PlatformType.normal.toString().equals(platform)) {
				log.info("Do nothing");
			} else if (PlatformType.cdh5_13.toString().equals(platform)) {
				conf = CDHLoginUtil.login(conf);
			} else if (PlatformType.fic50.toString().equals(platform)) {
				conf = SecurityUtils.HBaselogin(conf, "user.keytab", "admin@Hadoop");
				if (!SecurityUtils.Maplogin(conf)) {// Security login
					log.error("Login system failed");
				}
			} else if (PlatformType.fic60.toString().equals(platform)) {
				conf = lg.hbaseLogin(conf);
			} else if (PlatformType.fic80.toString().equals(platform)) {
				conf = C80LoginUtil.login(conf);
			} else {
				throw new BusinessException("The platform is a wrong type ,please check <platform>...");
			}
			return conf;
		} catch (Exception e) {
			log.error("Failed to initAuth...", e);
			return null;
		}
	}

	public static void main(String[] args) {

		ConfigReader.getConfiguration();
	}
}
