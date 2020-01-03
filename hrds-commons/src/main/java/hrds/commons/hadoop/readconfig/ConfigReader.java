package hrds.commons.hadoop.readconfig;

import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaUtil;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

public class ConfigReader {

	public enum PlatformType {
		normal, fic50, fic60, fic80, cdh5_13
	}

	private static final Log log = LogFactory.getLog(ConfigReader.class);

	private static Configuration conf;

	public static String platform = PropertyParaUtil.getString("platform", "normal");

	public static final String external_cluster = PropertyParaUtil.getString("external_cluster_platform", "normal");

	public static LoginUtil lg;

	private ConfigReader() {

	}

	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration() {

		platform = PropertyParaUtil.getString("platform", "normal");
		//平台验证
		return getConfiguration(null);
	}

	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration(String configPath) {

		try {
			log.info("configReader  " + configPath);
			if (StringUtil.isEmpty(configPath)) {
				lg = new LoginUtil();
				conf = lg.confLoad(conf);
				//平台验证
				return initAuth(conf);
			} else {
				lg = new LoginUtil(configPath);
				conf = lg.confLoad(conf);
				//平台验证
				return initAuthExternal(conf);
			}
		} catch (Exception e) {
			log.error("Failed to verify the platform...", e);
			return null;
		}
	}

	public static Configuration initAuthExternal(Configuration conf) {

		log.info("initAuth external_cluster: " + external_cluster);

		try {
			if (external_cluster.equals(PlatformType.normal.toString())) {
			} else if (external_cluster.equals(PlatformType.fic50.toString())) {
				conf = SecurityUtils.HBaselogin(conf, "user.keytab", "admin@Hadoop");
				if (!SecurityUtils.Maplogin(conf)) {// Security login
					log.error("Login system failed");
				}
			} else if (external_cluster.equals(PlatformType.fic60.toString())) {
				conf = lg.hbaseLogin(conf);
			} else {
				throw new BusinessException("The platform is a wrong type ,please check the syspara table for the argument <platform>...");
			}
			return conf;
		} catch (Exception e) {
			log.error("external_cluster Failed to initAuth...", e);
			return null;
		}
	}

	public static Configuration initAuth(Configuration conf) {

		log.info("initAuth platform: " + platform);
		if (null == lg) {
			lg = new LoginUtil();
		}

		try {
			if (platform.equals(PlatformType.normal.toString())) {

			} else if (platform.equals(PlatformType.cdh5_13.toString())) {
				conf = CDHLoginUtil.login(conf);
			} else if (platform.equals(PlatformType.fic50.toString())) {
				conf = SecurityUtils.HBaselogin(conf, "user.keytab", "admin@Hadoop");
				if (!SecurityUtils.Maplogin(conf)) {// Security login
					log.error("Login system failed");
				}
			} else if (platform.equals(PlatformType.fic60.toString())) {
				conf = lg.hbaseLogin(conf);
			} else if (platform.equals(PlatformType.fic80.toString())) {
				conf = C80LoginUtil.login(conf);
			} else {
				throw new BusinessException("The platform is a wrong type ,please check the syspara table for the argument <platform>...");
			}
			return conf;
		} catch (Exception e) {
			log.error("Failed to initAuth...", e);
			return null;
		}
	}

	public static Configuration initAuth() {

		//		return initAuth(new Configuration());
		return null;
	}

	public static void main(String[] args) {

		ConfigReader.getConfiguration();
	}
}
