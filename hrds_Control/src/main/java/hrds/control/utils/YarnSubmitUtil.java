package hrds.control.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;

import hrds.control.hadoop.readConfig.ConfigReader;

public class YarnSubmitUtil {

	private static final Logger logger = LogManager.getLogger();
	private static SparkConf sparkConf = null;
	
	public static void main(String... args) throws Exception {

	}
	/**
	 * 
	 * @param jobName 作业名
	 * @param runJarPath 运行jar的路径
	 * @param clazz 运行jar的main方法
	 * @param args 参数
	 * @return appId
	 */
	public static String submitToYarn(String jobName, String runJarPath, String clazz, List<String> args ){
		System.setProperty("SPARK_YARN_MODE", "true");
		String[] arg0 = new String[] {"--jar",runJarPath,"--class",clazz};
		List<String> list = new ArrayList<>(Arrays.asList(arg0));
		for(String str : args) {
			list.add("--arg");
			list.add(str);
		}
		logger.info("提交任务参数：" + list.toString());
		ClientArguments cArgs = new ClientArguments(list.toArray(new String[0]));
		boolean cross_platform = false;
		String os = System.getProperty("os.name");
		if( os.contains("Windows") ) {
			cross_platform = true;
		}
		Configuration conf = ConfigReader.getConfiguration();
		conf.setBoolean("mapreduce.app-submission.cross-platform", cross_platform);// 配置使用跨平台提交任务
		conf.set("fs.hdfs.impl",org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()); 
		conf.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName());
		Client client = new Client(cArgs,getSparkConf().setAppName(jobName));
		try { 
			ApplicationId appId = client.submitApplication(); 
			logger.info( "提交任务成功，appID：" + appId.toString());
			return appId.toString();
		} catch (Exception e) { 
			logger.error("提交spark任务失败", e);
			return null; 
		} finally {
			if (client != null) { 
				client.stop();
			}
		}
	}

	/**
	 * 获取sparkConf配置
	 * @return sparkConf 
	 */
	public static SparkConf getSparkConf() {
		if( sparkConf == null ) {
			sparkConf = new SparkConf();
		}
		return sparkConf;
	}
}
