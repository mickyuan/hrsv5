package hrds.commons.hadoop.readconfig;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.loginAuth.ILoginAuth;
import hrds.commons.hadoop.loginAuth.LoginAuthFactory;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PropertyParaValue;
import hrds.commons.utils.StorageTypeKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Configuration
 */
public class ConfigReader {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * 支持的平台类型
	 */
	public enum PlatformType {
		normal, fic80, cdh5
	}

	/**
	 * Configuration main
	 *
	 * @param args args
	 */
	public static void main(String[] args) throws IOException {
		Configuration conf = ConfigReader.getConfiguration();
		HBaseHelper helper = HBaseHelper.getHelper(conf);
		if (helper.existsTable("a")) {
			System.out.println("table: a, exist!");
		} else {
			System.out.println("table: a, is not exist!");
		}
		if (helper.existsTable("t1")) {
			System.out.println("table: t1, exist!");
		} else {
			System.out.println("table: t1, is not exist!");
		}
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
	 * @param configPath    配置文件目录
	 * @param platform      平台类型
	 * @param prncipal_name 认证名称
	 * @return Configuration
	 */
	public static Configuration getConfiguration(String configPath, String platform, String prncipal_name, String hadoop_user_name) {
		try {
			//参数校验
			if (platform == null) {
				//平台类型,如果未配置则去默认值"normal"
				platform = PropertyParaValue.getString("platform", PlatformType.normal.toString());
			}
			if (prncipal_name == null) {
				//认证实体名,如果未配置则去默认值"admin@HADOOP.COM"
				prncipal_name = PropertyParaValue.getString("principle.name", "admin@HADOOP.COM");
			}
			if (hadoop_user_name == null || StringUtil.isBlank(hadoop_user_name)) {
				//hadoop_user_name,如果配置参数未设置,取默认值"hyshf"
				hadoop_user_name = PropertyParaValue.getString("HADOOP_USER_NAME", "hyshf");
			}
			//平台验证
			ILoginAuth iLoginAuth;
			if (StringUtil.isEmpty(configPath)) {
				//未配置conf文件路径,取程序运行家目录下的conf,设置平台登录实例
				iLoginAuth = LoginAuthFactory.getInstance(platform);
			} else {
				//指定conf文件,设置平台登录实例
				iLoginAuth = LoginAuthFactory.getInstance(platform, configPath);
			}
			//设置hadoop_user_name,如果配置参数未设置,取默认值"hyshf"
			System.setProperty("HADOOP_USER_NAME", hadoop_user_name);
			return iLoginAuth.login(prncipal_name);
		} catch (Exception e) {
			logger.error("platform:" + platform + " verification failed ...", e);
			throw new AppSystemException("platform: " + platform + " ,认证失败! " + e.getMessage());
		}
	}
}
