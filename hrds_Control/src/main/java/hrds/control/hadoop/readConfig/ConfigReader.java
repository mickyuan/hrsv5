package hrds.control.hadoop.readConfig;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ConfigReader {

	private static final Logger logger = LogManager.getLogger();

	public enum PlatformType {
		normal;
	}

	private static Configuration conf;

	private ConfigReader() {
	}
	/**
	 * load HDFS_properties to HadoopConstant
	 */
	public static Configuration getConfiguration() {
		try {
			conf = LoginUtil.confLoad(conf);
			return initAuth(conf);
		}
		catch(Exception e) {
			logger.error("Failed to verify the FI platform...", e);
			return null;
		}
	}
	public static Configuration initAuth(Configuration conf) {
		logger.info("initAuth platform:normal");
		try {
			return conf;
		}
		catch(Exception e) {
			logger.error("Failed to initAuth...", e);
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
