package hrds.trigger.hadoop.readConfig;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

/**
 * @ClassName: ConfigReader
 * @Description: 用于读取Hadoop相关组件客户端配置的类。
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 11:41
 **/
public class ConfigReader {

	private static final String confDir = System.getProperty("user.dir") +
			File.separator + "conf" + File.separator;
	private static final String PATH_TO_CORE_SITE_XML = confDir + "core-site.xml";
	private static final String PATH_TO_HDFS_SITE_XML = confDir + "hdfs-site.xml";
	private static final String PATH_TO_HBASE_SITE_XML = confDir + "hbase-site.xml";
	private static final String PATH_TO_MAPRED_SITE_XML = confDir + "mapred-site.xml";
	private static final String PATH_TO_YARN_SITE_XML = confDir + "yarn-site.xml";

	static {
		ClassPathResLoader.loadResourceDir(ConfigReader.confDir);//加载spark配置文件到classpath中
	}

	private ConfigReader() {}

	/**
	 * 加载Hadoop环境下的配置文件, 将这些配置信息转换为一个对象。配置文件包括：
	 * core-site.xml、hdfs-site.xml、hbase-site.xml、mapred-site.xml、yarn-site.xml <br>
	 * 1.加载conf文件。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 * @return org.apache.hadoop.conf.Configuration <br>
	 *          含义：表示一系列的配置文件，将这些配置文件中的配置信息统一表示为一个对象。 <br>
	 *          取值范围：不会为null。
	 */
	public static Configuration getConfiguration() {
		//1.加载conf文件。
		Configuration conf = HBaseConfiguration.create();
		conf.addResource(new Path(PATH_TO_CORE_SITE_XML));
		conf.addResource(new Path(PATH_TO_HDFS_SITE_XML));
		conf.addResource(new Path(PATH_TO_HBASE_SITE_XML));
		conf.addResource(new Path(PATH_TO_MAPRED_SITE_XML));
		conf.addResource(new Path(PATH_TO_YARN_SITE_XML));

		return conf;
	}
}
