package hrds.commons.hadoop.hbaseindexer.configure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.PropertyParaUtil;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * hbase on solr 的一些配置
 *
 * @author Mick
 *
 */
public class ConfigurationUtil {
	// tomcat bin/conf/hbase-site.xml
	private static final String HBASESITE = "./conf/hbase-site.xml";
	public static final String SOLR_ON_HBASE_COLLECTION = PropertyParaValue.getString("solrOnHbase", "hbase_solr");
	public static final String SOLR_ON_HBASE_URL = PropertyParaValue.getString("solrUrl", "http://10.0.168.103:18983/solr/")
			+ SOLR_ON_HBASE_COLLECTION;
	public static final String ZKHOST = PropertyParaValue.getString("zkHost", "0.0.0.0/solr").replace("/solr", "");
	// CDH所使用同步hbase与solr的mr jar包可能所在路径
	private static final String[] MR_JAR_MAYBE_PATHS = new String[] { "../shared/hbase-indexer-mr-job.jar",
			"./lib/hbase-indexer-mr-job.jar" };
	// 需要在solr中添加的tablename field名称
	public static final String TABLE_NAME_FIELD = "F-TABLE_NAME";

	/**
	 * 添加 indexer 的 command
	 *
	 * @param indexerName
	 * @return
	 */
	public static String deleteIndexerCommand(String indexerName) {
		String command = "bash hbase-indexer delete-indexer -n %s -z %s";
		return String.format(command, indexerName, ZKHOST);
	}

	/**
	 * 删除 indexer 的 command
	 *
	 * @param indexerName
	 * @param xmlPath
	 * @return
	 */
	public static String addIndexerCommand(String indexerName, String xmlPath) {
		String command = "bash hbase-indexer add-indexer -n %s -c %s -z %s -cp solr.collection=%s";
		return String.format(command, indexerName, xmlPath, ZKHOST, SOLR_ON_HBASE_COLLECTION);
	}

	/**
	 * 同步 indexer 的 command
	 *
	 * @param indexerName
	 * @param xmlPath
	 * @return
	 */
	public static String syncIndexerCommand(String indexerName, String xmlPath) {
		// 当前平台 ：开源或FI
		String platform = PropertyParaValue.getString("platform", "normal");
		String command = "";
		// FI
		if (platform.equals(ConfigReader.PlatformType.fic60.toString())
				|| platform.equals(ConfigReader.PlatformType.fic60.toString())) {

			command = "bash hbase-indexer batch-indexer --hbase-indexer-zk %s --hbase-indexer-name %s "
					+ "--hbase-indexer-file %s --output-dir hdfs://hacluster/tmp/solr --go-live "
					+ "--overwrite-output-dir -v --reducers 3 --zk-host %s";
			command = String.format(command, ZKHOST, indexerName, xmlPath, ZKHOST + "/solr");
			// cdh
		} else if (platform.equals(ConfigReader.PlatformType.normal.toString())
				|| platform.equals(ConfigReader.PlatformType.cdh5_13.toString())) {

			// 因为web这边和agent这边的lib包的相对路径不同，所以要找一下
			String syncJarPath = "";
			for (String jarPath : MR_JAR_MAYBE_PATHS) {
				if (new File(jarPath).exists()) {
					syncJarPath = jarPath;
				}
			}
			if (StringUtils.isBlank(syncJarPath)) {
				throw new BusinessException("File not found: " + StringUtils.join(MR_JAR_MAYBE_PATHS, ','));
			}
			command = "bash /usr/bin/hadoop jar %s --conf %s --hbase-indexer-zk %s --hbase-indexer-name %s --reducers 0";
			command = String.format(command, syncJarPath, HBASESITE, ZKHOST, indexerName);
		}
		return command;
	}

	/**
	 * 获取一整套的 indexer 的 commands
	 *
	 * @param indexerName
	 * @return
	 */
	public static List<String> indexerStepCommands(String indexerName, String xmlPath) {

		List<String> stepCommands = new ArrayList<String>();
		stepCommands.add(deleteIndexerCommand(indexerName));
		stepCommands.add(addIndexerCommand(indexerName, xmlPath));
		stepCommands.add(syncIndexerCommand(indexerName, xmlPath));
		return stepCommands;
	}

	public static String randomXmlFilePath() {
		return FileUtils.getTempDirectoryPath() + File.separator + "datatmp" + File.separator
				+ System.currentTimeMillis() + ".xml";
	}

}
