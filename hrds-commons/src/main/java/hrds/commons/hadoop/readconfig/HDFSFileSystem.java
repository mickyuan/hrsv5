package hrds.commons.hadoop.readconfig;


import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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

	public HDFSFileSystem() throws IOException {
		this(null);
	}

	public HDFSFileSystem(String configPath) throws IOException {
		this(configPath, null);
	}

	public HDFSFileSystem(String configPath, String platform) throws IOException {
		this(configPath, platform, null);
	}

	public HDFSFileSystem(String configPath, String platform,
	                      String prncipal_name) throws IOException {
		this(configPath, platform, prncipal_name, null);
	}

	public HDFSFileSystem(String configPath, String platform,
	                      String prncipal_name, String hadoop_user_name) throws IOException {
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
		LoginUtil loginUtil = new LoginUtil(configPath);
		if (ConfigReader.PlatformType.normal.toString().equals(platform)) {
			conf = ConfigReader.getConfiguration(configPath, platform, prncipal_name, hadoop_user_name);
			fileSystem = FileSystem.get(conf);
			logger.info("normal FileSystem inited ");
		} else if (ConfigReader.PlatformType.cdh5_13.toString().equals(platform)) {
			conf = loginUtil.confLoad();
			conf = loginUtil.authentication(conf, prncipal_name);
			fileSystem = FileSystem.get(conf);
			logger.info("cdh5_13 FileSystem inited ");
		} else if (ConfigReader.PlatformType.fic50.toString().equals(platform)) {
			conf = SecurityUtils.confLoad();
			conf = SecurityUtils.authentication(conf);
			fileSystem = FileSystem.get(conf);
			logger.info("fi FileSystem inited ");
		} else if (ConfigReader.PlatformType.fic80.toString().equals(platform)) {
			conf = loginUtil.confLoad();
			conf = loginUtil.authentication(conf, prncipal_name);
			fileSystem = FileSystem.get(conf);
			logger.info("fic60 FileSystem inited ");
		} else if (ConfigReader.PlatformType.fic60.toString().equals(platform)) {
			conf = loginUtil.confLoad();
			conf = C80LoginUtil.login(conf, prncipal_name);
			fileSystem = FileSystem.get(conf);
			logger.info("fic80 FileSystem inited ");
		} else {
			throw new AppSystemException("The platform is a wrong type ,please check the syspara table for the argument <platform>...");
		}
	}

	public FileSystem getFileSystem() {

		return fileSystem;
	}

	public Configuration getConfig() {
		return conf;
	}

	public Path getWorkingDirectory(String directory) {

		if (StringUtils.isBlank(directory))
			return fileSystem.getWorkingDirectory();
		fileSystem.setWorkingDirectory(new Path(fileSystem.getConf().get("fs.default.name") + Path.SEPARATOR + directory + Path.SEPARATOR));
		return fileSystem.getWorkingDirectory();
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
