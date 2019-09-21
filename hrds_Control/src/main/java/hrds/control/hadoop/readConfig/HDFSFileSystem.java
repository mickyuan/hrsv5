package hrds.control.hadoop.readConfig;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HDFSFileSystem {

	private static final Logger logger = LogManager.getLogger();

	private Configuration conf;

	private FileSystem fileSystem;

	public HDFSFileSystem() {

		try {
			conf = LoginUtil.confLoad(conf);
			conf = LoginUtil.authentication(conf);
			fileSystem = FileSystem.get(conf);
			logger.info("fic60 FileSystem inited ");
		}
		catch(IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public FileSystem getFileSystem() {

		return fileSystem;
	}

	public Configuration getConfig() {

		return conf;
	}

	public Path getWorkingDirectory(String directory) {

		if( directory != null && directory.length() != 0 ) {
			return fileSystem.getWorkingDirectory();
		}
		fileSystem.setWorkingDirectory(new Path(fileSystem.getConf().get("fs.default.name") + Path.SEPARATOR + directory + Path.SEPARATOR));
		return fileSystem.getWorkingDirectory();
	}

	public void close() {

		try {
			if( null != fileSystem )
				fileSystem.close();
			logger.info("FileSystem closed ");
		}
		catch(IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
