package hrds.commons.hadoop.readconfig;


import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;


public class HDFSFileSystem {

	private static final Log log = LogFactory.getLog(HDFSFileSystem.class);

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
		if (ConfigReader.PlatformType.normal.toString().equals(platform)) {
			conf = ConfigReader.getConfiguration(configPath, platform, prncipal_name, hadoop_user_name);
			fileSystem = FileSystem.get(conf);
			log.info("normal FileSystem inited ");
		} else if (ConfigReader.PlatformType.cdh5_13.toString().equals(platform)) {
			LoginUtil lg = new LoginUtil(configPath);
			conf = lg.confLoad();
			conf = lg.authentication(conf,prncipal_name);
			fileSystem = FileSystem.get(conf);
			log.info("cdh5_13 FileSystem inited ");
		} else if (ConfigReader.PlatformType.fic50.toString().equals(platform)) {
			conf = SecurityUtils.confLoad();
			conf = SecurityUtils.authentication(conf);
			fileSystem = FileSystem.get(conf);
			log.info("fi FileSystem inited ");
		} else if (ConfigReader.PlatformType.fic80.toString().equals(platform)) {
			LoginUtil lg = new LoginUtil(configPath);
			conf = lg.confLoad();
			conf = lg.authentication(conf,prncipal_name);
			fileSystem = FileSystem.get(conf);
			log.info("fic60 FileSystem inited ");
		} else if (ConfigReader.PlatformType.fic60.toString().equals(platform)) {
			LoginUtil lg = new LoginUtil(configPath);
			conf = lg.confLoad();
			conf = C80LoginUtil.login(conf, prncipal_name);
			fileSystem = FileSystem.get(conf);
			log.info("fic80 FileSystem inited ");
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
			if (null != fileSystem)
				fileSystem.close();
			log.debug("FileSystem closed ");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
