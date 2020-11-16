package hrds.commons.hadoop.readconfig;


import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.loginAuth.ILoginAuth;
import hrds.commons.hadoop.loginAuth.LoginAuthFactory;
import hrds.commons.utils.PropertyParaValue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;


/**
 * HDFS文件系统类
 */
public class HDFSFileSystem {

	private static final Logger logger = LogManager.getLogger();
	private FileSystem fileSystem;
	private Configuration conf;

	public HDFSFileSystem() {
		this(null);
	}

	public HDFSFileSystem(String configPath) {
		this(configPath, null);
	}

	public HDFSFileSystem(String configPath, String platform) {
		this(configPath, platform, null);
	}

	public HDFSFileSystem(String configPath, String platform,
	                      String prncipal_name) {
		this(configPath, platform, prncipal_name, null);
	}

	public HDFSFileSystem(String configPath, String platform, String prncipal_name, String hadoop_user_name) {
		if (configPath == null || StringUtil.isBlank(configPath)) {
			configPath = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
		}
		if (platform == null || StringUtil.isBlank(platform)) {
			platform = PropertyParaValue.getString("platform", ConfigReader.PlatformType.normal.toString());
		}
		if (prncipal_name == null || StringUtil.isBlank(prncipal_name)) {
			prncipal_name = PropertyParaValue.getString("principle.name", "admin@HADOOP.COM");
		}
		if (hadoop_user_name == null || StringUtil.isBlank(hadoop_user_name)) {
			hadoop_user_name = PropertyParaValue.getString("HADOOP_USER_NAME", "hyshf");
		}
		//登录认证
		ILoginAuth iLoginAuth = LoginAuthFactory.getInstance(platform, configPath);
		conf = iLoginAuth.login(prncipal_name);
		//设置hadoop_user_name,如果配置参数未设置,取默认值"hyshf"
		System.setProperty("HADOOP_USER_NAME", hadoop_user_name);
		//初始化文件系统实现对象
		try {
			fileSystem = FileSystem.get(conf);
			logger.info("platform: " + platform + " fileSystem inited success!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException("platform: " + platform + " fileSystem inited failed!");
		}
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}

	public Configuration getConfig() {
		return conf;
	}

	public void close() {
		try {
			if (null != fileSystem) {
				fileSystem.close();
				logger.debug("FileSystem closed ");
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
