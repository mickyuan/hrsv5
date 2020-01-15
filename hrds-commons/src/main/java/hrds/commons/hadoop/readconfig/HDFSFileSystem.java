package hrds.commons.hadoop.readconfig;


import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
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

	private Configuration conf;

	private FileSystem fileSystem;

	public HDFSFileSystem() {
		this(System.getProperty("user.dir") + File.separator + "conf" + File.separator);
	}

	public HDFSFileSystem(String configPath) {
		this(configPath, ConfigReader.PlatformType.normal.toString(), null);
	}

	public HDFSFileSystem(String configPath, String platform, String hadoop_user_name) {
		try {
			if (ConfigReader.PlatformType.normal.toString().equals(platform)) {
				conf = ConfigReader.getConfiguration(configPath);
				//XXX 在集群没有认证的情况下，agent在windows下测试可以通过，需要下面这一行代码，执行运行的hadoop用户
				if (!StringUtil.isEmpty(hadoop_user_name)) {
					conf.set("HADOOP_USER_NAME", "hyshf");
				}
				fileSystem = FileSystem.get(conf);
				log.info("normal FileSystem inited ");
			} else if (ConfigReader.PlatformType.cdh5_13.toString().equals(platform)) {
				LoginUtil lg = new LoginUtil(configPath);
				conf = lg.confLoad(conf);
				conf = lg.authentication(conf);
				fileSystem = FileSystem.get(conf);
				log.info("cdh5_13 FileSystem inited ");
			} else if (ConfigReader.PlatformType.fic50.toString().equals(platform)) {
				conf = SecurityUtils.confLoad(conf);
				conf = SecurityUtils.authentication(conf);
				fileSystem = FileSystem.get(conf);
				log.info("fi FileSystem inited ");
			} else if (ConfigReader.PlatformType.fic80.toString().equals(platform)) {
				LoginUtil lg = new LoginUtil(configPath);
				conf = lg.confLoad(conf);
				conf = lg.authentication(conf);
				fileSystem = FileSystem.get(conf);
				log.info("fic60 FileSystem inited ");
			} else if (ConfigReader.PlatformType.fic60.toString().equals(platform)) {
				LoginUtil lg = new LoginUtil(configPath);
				conf = lg.confLoad(conf);
				conf = C80LoginUtil.login(conf);
				fileSystem = FileSystem.get(conf);
				log.info("fic80 FileSystem inited ");
			} else {
				throw new BusinessException("The platform is a wrong type ,please check the syspara table for the argument <platform>...");
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
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
