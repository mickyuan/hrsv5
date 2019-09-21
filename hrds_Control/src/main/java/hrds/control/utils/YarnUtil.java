package hrds.control.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.nativeio.Errno;
import org.apache.hadoop.io.nativeio.NativeIOException;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;

import com.alibaba.fastjson.JSONObject;

import hrds.control.hadoop.readConfig.ClassPathResLoader;
import hrds.control.hadoop.readConfig.ConfigReader;

public class YarnUtil {

	private static final Logger logger = LogManager.getLogger();
	private static final String confDir = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
	private static SparkConf sparkConf = null;
	private static YarnClient client;
	private static final String APPLICATIONS_PATTERN = "%30s\t%20s\t%20s\t%10s\t%10s\t%18s\t%18s\t%15s\t%35s" + System.getProperty("line.separator");

	static {
		System.setProperty("SPARK_YARN_MODE", "true");
		ClassPathResLoader.loadResourceDir(confDir);//加载spark配置文件到classpath中
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

	/**
	 * 设置任务提交时用来做认证的用户
	 * @param name <hyshfAdmin> 无认证时默认hyshf
	 */
	public static void setHadoopUserName(String name) {

		System.setProperty("HADOOP_USER_NAME", name);
	}

	/**
	 * 杀掉任务 通过作业ApplicationId
	 * @param applicationId 作业ApplicationId
	 * @return 
	 */
	public static void killApplicationByid(String applicationId) {

		ApplicationId appId = ApplicationId.fromString(applicationId);
		YarnClient yarnClient = YarnM.instance.getYarnClient();
		ApplicationReport appReport = null;
		try {
			appReport = yarnClient.getApplicationReport(appId);
		}
		catch(ApplicationNotFoundException e) {
			logger.info("Application with id '" + applicationId + "' doesn't exist in RM.");
		}
		catch(YarnException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		if( appReport.getYarnApplicationState() == YarnApplicationState.FINISHED ) {
			logger.info("Application " + applicationId + " has already FINISHED ");
		}
		else if( appReport.getYarnApplicationState() == YarnApplicationState.KILLED ) {
			logger.info("Application " + applicationId + " has already KILLED ");
		}
		else if( appReport.getYarnApplicationState() == YarnApplicationState.FAILED ) {
			logger.info("Application " + applicationId + " has already FAILED ");
		}
		else {
			try {
				yarnClient.killApplication(appId);
			}
			catch(YarnException e) {
				e.printStackTrace();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			logger.info("Killing application " + applicationId + " SUCCEEDED ");
		}
	}

	/**
	 * 获取任务的ApplicationId通过作业名jobName
	 * @param jobName 
	 * @return appId
	 */
	public static String getApplicationIdByJobName(String jobName) {

		YarnClient yarnClient = YarnM.instance.getYarnClient();
		EnumSet<YarnApplicationState> appStates = EnumSet.noneOf(YarnApplicationState.class);
		List<ApplicationReport> appsReport = null;
		ApplicationId appId = null;
		try {
			appsReport = yarnClient.getApplications(appStates);
			for(ApplicationReport app : appsReport) {
				if( app.getName().equalsIgnoreCase(jobName) ) {
					appId = app.getApplicationId();
					break;
				}
			}
			yarnClient.close();
		}
		catch(YarnException | IOException e) {
			e.printStackTrace();
		}
		return appId == null ? "" : appId.toString();
	}

	/**
	 * 获取任务状态通过ApplicationId
	 * @param id ApplicationId String
	 * @return json.toString
	 */
	public static String getStatusByAppId(String id) {

		YarnClient yarnClient = YarnM.instance.getYarnClient();
		String[] split = id.split("_");
		ApplicationId appId = ApplicationId.newInstance(Long.parseLong(split[1]), Integer.parseInt(split[2]));
		System.out.println(appId);
		JSONObject json = new JSONObject();
		try {
			ApplicationReport report = yarnClient.getApplicationReport(appId);
			logger.info("Got application report " + ", appId=" + report.getApplicationId() + ", appName=" + report.getName() + ", clientToAMToken="
							+ report.getClientToAMToken() + ", appDiagnostics=" + report.getDiagnostics() + ", appMasterHost=" + report.getHost()
							+ ", appQueue=" + report.getQueue() + ", appMasterRpcPort=" + report.getRpcPort() + ", appStartTime="
							+ report.getStartTime() + ", appFinishTime=" + report.getFinishTime() + ", ApplicationType="
							+ report.getApplicationType() + ", yarnAppState=" + report.getYarnApplicationState().toString()
							+ ", distributedFinalState=" + report.getFinalApplicationStatus().toString() + ", appTrackingUrl="
							+ report.getTrackingUrl() + ", appUser=" + report.getUser() + ",  Progress= " + report.getProgress() + ",  status= "
							+ report.getYarnApplicationState());
			//拼接返回的 JSON 
			json.put("appId", report.getApplicationId().toString());
			json.put("appName", report.getName().toString());
			json.put("clientToAMToken", null == report.getClientToAMToken()?"":report.getClientToAMToken().toString());
			json.put("appDiagnostics", report.getDiagnostics().toString());
			json.put("appMasterHost", report.getHost().toString());
			json.put("appQueue", report.getQueue().toString());
			json.put("appMasterRpcPort", report.getRpcPort());
			json.put("appStartTime", report.getStartTime());
			json.put("progress", report.getProgress());
			json.put("appUser", report.getUser().toString());
			json.put("appFinishTime", report.getFinishTime());
			json.put("appcostTime", report.getFinishTime() - report.getStartTime());
			json.put("applicationType", report.getApplicationType().toString());
			json.put("appTrackingUrl", report.getTrackingUrl().toString());
			json.put("yarnAppState", report.getYarnApplicationState().toString());
			json.put("distributedFinalState", report.getFinalApplicationStatus().toString());
			json.put("status", report.getYarnApplicationState().toString());
			yarnClient.close();
		}
		catch(YarnException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return JSONObject.toJSONString(json);
	}

	/**
	 * 获取任务状态通过ApplicationId
	 * @param report Application
	 * @return json.toString
	 */
	private static String getStatusByAppId(ApplicationReport report) {

		YarnClient yarnClient = YarnM.instance.getYarnClient();
		JSONObject json = new JSONObject();
		try {
			logger.info("Got application report " + ", appName=" + report.getName() + ", clientToAMToken=" + report.getClientToAMToken()
							+ ", appDiagnostics=" + report.getDiagnostics() + ", appMasterHost=" + report.getHost() + ", appQueue="
							+ report.getQueue() + ", appMasterRpcPort=" + report.getRpcPort() + ", appStartTime=" + report.getStartTime()
							+ ", appFinishTime=" + report.getFinishTime() + ", ApplicationType=" + report.getApplicationType() + ", yarnAppState="
							+ report.getYarnApplicationState().toString() + ", distributedFinalState="
							+ report.getFinalApplicationStatus().toString() + ", appTrackingUrl=" + report.getTrackingUrl() + ", appUser="
							+ report.getUser() + ",  Progress= " + report.getProgress() + ",  status= " + report.getYarnApplicationState());

			//			ApplicationResourceUsageReport applicationResourceUsageReport = report.getApplicationResourceUsageReport();
			//			System.out.println(applicationResourceUsageReport);
			//拼接返回的 JSON 
			json.put("appId", report.getApplicationId().toString());
			json.put("appName", report.getName().toString());
			json.put("clientToAMToken", null == report.getClientToAMToken()?"":report.getClientToAMToken().toString());
			json.put("appDiagnostics", report.getDiagnostics().toString());
			json.put("appMasterHost", report.getHost().toString());
			json.put("appQueue", report.getQueue().toString());
			json.put("appMasterRpcPort", report.getRpcPort());
			json.put("appStartTime", report.getStartTime());
			json.put("progress", report.getProgress());
			json.put("appUser", report.getUser().toString());
			json.put("appFinishTime", report.getFinishTime());
			json.put("appcostTime", report.getFinishTime() - report.getStartTime());
			json.put("applicationType", report.getApplicationType().toString());
			json.put("appTrackingUrl", report.getTrackingUrl().toString());
			json.put("yarnAppState", report.getYarnApplicationState().toString());
			json.put("distributedFinalState", report.getFinalApplicationStatus().toString());
			json.put("status", report.getYarnApplicationState().toString());
			yarnClient.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	/**
	 * 清除任务目录通过appId
	 * @param appId
	 */
	public static void cleanupStagingDir(ApplicationId appId) {

		String appStagingDir = Client.SPARK_STAGING() + Path.SEPARATOR + appId.toString();

		try {
			Path stagingDirPath = new Path(appStagingDir);
			FileSystem fs = FileSystem.get(ConfigReader.getConfiguration());
			if( fs.exists(stagingDirPath) ) {
				fs.delete(stagingDirPath, true);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unused
	 * @param jobName 作业名
	 * @param runJarPath 运行jar的路径
	 * @param clazz 运行jar的main方法
	 * @param args 参数
	 * @return appId
	 */
	public static String submitToYarn(String jobName, String runJarPath, String clazz, List<String> args) {

		System.setProperty("SPARK_YARN_MODE", "true");
		String[] arg0 = new String[] { "--jar", runJarPath, "--class", clazz };
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
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
		Client client = new Client(cArgs, getSparkConf().setAppName(jobName));
		try {
			ApplicationId appId = client.submitApplication();
			logger.info("提交任务成功，appID：" + appId.toString());
			return appId.toString();
		}
		catch(Exception e) {
			logger.error("提交spark任务失败", e);
			return null;
		}
		finally {
			if( client != null ) {
				client.stop();
			}
		}
	}

	/**
	 * Unused
	 * 使用spark-submit的API向集群提交作业。hadoop2.X引入yarn作为资源管理器mapreduce计算框架也
	 * 变为mr2;所有作业mr都有yarn管理，如果使用API提交依然会生成一个新的application(我还没用解决方法)
	 * 因此区分mr和spark作业分别提交。(如有好的建议)
	 * @param jobName 作业名字
	 * @param runJarPath local jars to include on the driver and executor classpaths.
	 * @param clazz 作业main方法
	 * @param args 参数
	 * @param jobType 作业类型
	 * @return
	 */
	private static String submitToYarn(String jobName, String runJarPath, String clazz, List<String> args, String jobType) {

		String appId = null;
		if( jobType.equalsIgnoreCase("MR") ) {
			appId = submitMR(jobName, runJarPath, clazz, args);
		}
		else {
			System.setProperty("SPARK_YARN_MODE", "true");

			String[] arg0 = new String[] { "--name", jobName, "--class", clazz, runJarPath };
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
			conf.setBoolean("mapreduce.app-submission.cross-platform", cross_platform);
			conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
			conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
			Client client = new Client(cArgs,getSparkConf());

			//如果是Mr默认取下一个任务id,同时提交多个任务会不会找错id?
			try {
				if( jobType.equalsIgnoreCase("MR") ) {
					appId = ApplicationId.newInstance(client.submitApplication().getClusterTimestamp(), client.submitApplication().getId() + 1)
									.toString();
				}
				else {
					appId = client.submitApplication().toString();
				}
				logger.info("提交任务成功，appID：" + appId);
			}
			catch(Throwable e) {
				e.printStackTrace();
			}
		}
		return appId;
	}

	/**
	 * 提交MR任务通过yarn jar 
	 * @param jobName 任务名
	 * @param runJarPath jar包路径
	 * @param clazz jar包main方法
	 * @param args 参数
	 * @return appId 任务的ApplicationId
	 */
	private static String submitMR(String jobName, String runJarPath, String clazz, List<String> args) {

		logger.info(jobName);
		//yarn jar <jar> [mainClass] Args...
		final CommandLine cmdLine = new CommandLine("yarn jar ");
		cmdLine.addArgument(runJarPath);
		cmdLine.addArgument(clazz);
		for(String str : args) {
			cmdLine.addArgument(str);
		}

		DefaultExecutor executor = new DefaultExecutor();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(Integer.MAX_VALUE);
		executor.setWatchdog(watchdog);
		try {
			executor.execute(cmdLine);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return getApplicationIdByCustomSign(jobName);

	}

	/**
	 * 修改目录权限
	 * @param path
	 * @param mode
	 * @throws IOException
	 */
	public static void chmod(String path, int mode) throws IOException {

		if( !Shell.WINDOWS ) {
			System.out.printf(path);
			chmodImpl(path, mode);
		}
		else {
			try {
				System.out.printf(path);
				chmodImpl(path, mode);
			}
			catch(NativeIOException nioe) {
				if( nioe.getErrorCode() == 3 ) {
					throw new NativeIOException("No such file or directory", Errno.ENOENT);
				}
				else {
					throw new NativeIOException("Unknown error", Errno.UNKNOWN);
				}
			}
		}
	}

	private static native void chmodImpl(String path, int mode) throws IOException;

	/**
	 * Unused
	 * 杀掉任务 通过作业ApplicationId
	 * @param id 作业ApplicationId
	 */
	private static void killApplication(String id) {

		YarnClient yarnClient = YarnM.instance.getYarnClient();
		String[] split = id.split("_");
		ApplicationId appId = ApplicationId.newInstance(Long.parseLong(split[1]), Integer.parseInt(split[2]));
		try {
			yarnClient.killApplication(appId);
			yarnClient.close();
		}
		catch(YarnException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试方法
	 */
	private static void testAppState() {

		YarnClient yarnClient = YarnM.instance.getYarnClient();

		EnumSet<YarnApplicationState> appStates = EnumSet.noneOf(YarnApplicationState.class);
		//		if (appStates.isEmpty()) {
		//			appStates.add(YarnApplicationState.RUNNING);
		//			appStates.add(YarnApplicationState.ACCEPTED);
		//			appStates.add(YarnApplicationState.SUBMITTED);
		//			appStates.add(YarnApplicationState.FINISHED);
		//		}
		List<ApplicationReport> appsReport = null;
		try {
			appsReport = yarnClient.getApplications(appStates);
		}
		catch(YarnException | IOException e) {
			e.printStackTrace();
		}

		PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out, Charset.forName("UTF-8")));
		for(ApplicationReport appReport : appsReport) {
			DecimalFormat formatter = new DecimalFormat("###.##%");
			String progress = formatter.format(appReport.getProgress());
			writer.printf(APPLICATIONS_PATTERN, appReport.getApplicationId(), appReport.getName(), appReport.getApplicationType(),
							appReport.getUser(), appReport.getQueue(), appReport.getYarnApplicationState(), appReport.getFinalApplicationStatus(),
							progress, appReport.getOriginalTrackingUrl());
		}
		writer.flush();
		for(ApplicationReport appReport : appsReport) {
			getStatusByAppId(appReport);
		}
	}

	/**
	 * Unused
	 * 获取任务的ApplicationId通过作业名jobName
	 * @param jobName 
	 * @return appId
	 */
	public static String getApplicationIdByCustomSign(String jobName) {

		YarnClient yarnClient = YarnM.instance.getYarnClient();
		long startTime = System.currentTimeMillis();
		boolean noFind = true;
		ApplicationId appId = null;
		try {
			while( noFind ) {
				List<ApplicationReport> applications = yarnClient.getApplications();
				for(ApplicationReport app : applications) {
					System.out.println("============" + app.getName() + "--> " + app.getApplicationType() + "--> " + app + "--> "
									+ app.getYarnApplicationState() + "--> " + app.getProgress());
					if( app.getName().equalsIgnoreCase(jobName) && startTime < app.getStartTime() ) {
						appId = app.getApplicationId();
						System.out.println(appId);
						noFind = false;
						break;
					}
				}
			}
			yarnClient.close();
		}
		catch(YarnException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return appId == null ? "" : appId.toString();
	}

	/**
	 * Main方法
	 * @param args
	 */
	public static void main(String[] args) {

	}
}
