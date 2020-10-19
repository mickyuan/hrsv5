package hrds.commons.hadoop.readconfig;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PropertyParaValue;
import hrds.commons.utils.StorageTypeKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;

public class ConfigReader {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * 支持的平台类型
	 */
	public enum PlatformType {
		normal, fic50, fic60, fic80, cdh5_13
	}

	/**
	 * load conf to Configuration
	 *
	 * @return Configuration
	 */
	public static Configuration getConfiguration() {
		return getConfiguration((String) null);
	}

	/**
	 * load conf to Configuration
	 *
	 * @param configPath 配置文件目录
	 * @return Configuration
	 */
	public static Configuration getConfiguration(String configPath) {
		return getConfiguration(configPath, null);
	}

	/**
	 * load conf to Configuration
	 *
	 * @param configPath 配置文件目录
	 * @param platform   平台类型
	 * @return Configuration
	 */
	public static Configuration getConfiguration(String configPath, String platform) {
		return getConfiguration(configPath, platform, null);
	}

	/**
	 * load conf to Configuration
	 *
	 * @param configPath    配置文件目录
	 * @param platform      平台类型
	 * @param prncipal_name 认证名称
	 * @return Configuration
	 */
	public static Configuration getConfiguration(String configPath, String platform, String prncipal_name) {
		return getConfiguration(configPath, platform, prncipal_name, null);
	}

	/**
	 * load conf to Configuration, 根据存储层实体 layerBean 创建 Configuration
	 *
	 * @param layerBean 存储层实体
	 * @return Configuration
	 */
	public static Configuration getConfiguration(LayerBean layerBean) {
		//配置文件路径
		String configPath = FileNameUtils.normalize(Constant.STORECONFIGPATH + layerBean.getDsl_name() + File.separator, true);
		//配置属性
		Map<String, String> layerAttr = layerBean.getLayerAttr();
		//平台版本
		String platform = layerAttr.get(StorageTypeKey.platform);
		//prncipal_name
		String prncipal_name = layerAttr.get(StorageTypeKey.prncipal_name);
		//操作hdfs的用户名
		String hadoop_user_name = layerAttr.get(StorageTypeKey.hadoop_user_name);
		return getConfiguration(configPath, platform, prncipal_name, hadoop_user_name);
	}

	/**
	 * load conf to Configuration
	 *
	 * @param configPath       配置文件目录
	 * @param platform         平台类型
	 * @param prncipal_name    认证名称
	 * @param hadoop_user_name 用户
	 * @return Configuration
	 */
	public static Configuration getConfiguration(String configPath, String platform, String prncipal_name, String hadoop_user_name) {
		try {
			logger.info("configReader  " + configPath);
			LoginUtil loginUtil;
			if (StringUtil.isEmpty(configPath)) {
				loginUtil = new LoginUtil();
			} else {
				loginUtil = new LoginUtil(configPath);
			}
			Configuration conf = loginUtil.confLoad();
			if (platform == null) {
				platform = PropertyParaValue.getString("platform", PlatformType.normal.toString());
			}
			if (prncipal_name == null) {
				prncipal_name = PropertyParaValue.getString("principle.name", "admin@HADOOP.COM");
			}
			if (hadoop_user_name == null) {
				hadoop_user_name = PropertyParaValue.getString("HADOOP_USER_NAME", "hyshf");
			}
			//平台验证
			return initAuth(conf, platform, prncipal_name, hadoop_user_name, loginUtil);
		} catch (Exception e) {
			logger.error("Failed to verify the platform...", e);
			throw new AppSystemException("认证获取conf失败", e);
		}
	}

	/**
	 * initial authentication
	 *
	 * @param conf             Configuration
	 * @param platform         平台类型
	 * @param prncipal_name    认证名称
	 * @param hadoop_user_name 用户
	 * @param lg               LoginUtil
	 * @return Configuration
	 */
	private static Configuration initAuth(Configuration conf, String platform, String prncipal_name, String hadoop_user_name, LoginUtil lg) {
		logger.info("initAuth platform: " + platform);
		try {
			if (PlatformType.normal.toString().equals(platform)) {
				logger.info("Do nothing");
			} else if (PlatformType.cdh5_13.toString().equals(platform)) {
				conf = CDHLoginUtil.login(conf, prncipal_name);
			} else if (PlatformType.fic50.toString().equals(platform)) {
				conf = SecurityUtils.HBaselogin(conf, "user.keytab", "admin@Hadoop");
				// Security login
				if (!SecurityUtils.Maplogin(conf)) {
					logger.error("Login system failed");
				}
			} else if (PlatformType.fic60.toString().equals(platform)) {
				conf = lg.hbaseLogin(conf, prncipal_name);
			} else if (PlatformType.fic80.toString().equals(platform)) {
				conf = C80LoginUtil.login(conf, prncipal_name);
			} else {
				throw new AppSystemException("The platform is a wrong type ,please check <platform>...");
			}
			//设置hadoop_user_name
			conf.set("HADOOP_USER_NAME", hadoop_user_name);
			return conf;
		} catch (Exception e) {
			logger.error("Failed to initAuth...", e);
			throw new AppSystemException("认证获取conf失败", e);
		}
	}
}
