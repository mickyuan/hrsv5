package hrds.trigger.utils;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hrds.trigger.hadoop.readConfig.ClassPathResLoader;

/**
 * ClassName: YarnUtil<br>
 * Description: 用于控制Yarn中作业的工具类。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 11:56<br>
 * Since: JDK 1.8
 **/
class YarnUtil {

	private static final Logger logger = LogManager.getLogger();

	private static final String confDir =
			System.getProperty("user.dir") + File.separator + "conf" + File.separator;
	private static final String YARN_PARA_SEPARATOR = "_";

	static {
		System.setProperty("SPARK_YARN_MODE", "true");
		ClassPathResLoader.loadResourceDir(confDir);//加载spark配置文件到classpath中
	}

	/**
	 * 通过作业名称，获取Yarn中作业的作业编号。若无法获取到作业编号，则返回空字符串。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param jobName
	 *          含义：作业名称。
	 *          取值范围：不能为null。
	 * @return java.lang.String
	 *          含义：作业编号。
	 *          取值范围：作业编号/空字符串。
	 */
	static String getApplicationIdByJobName(final String jobName) {

		EnumSet<YarnApplicationState> appStates = EnumSet.noneOf(YarnApplicationState.class);
		List<ApplicationReport> appsReport;
		try(YarnClient yarnClient = YarnM.instance.getYarnClient()) {

			appsReport = yarnClient.getApplications(appStates);
			for(ApplicationReport app : appsReport) {
				if( app.getName().equalsIgnoreCase(jobName) ) {
					return app.getApplicationId().toString();
				}
			}
		}
		catch(YarnException | IOException e) {
			logger.warn("无法获得Yarn中作业的作业编号 {}", e.getMessage());
		}

		return "";
	}

	/**
	 * 通过Yarn中的作业编号，获取该作业的运行状况。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param id
	 *          含义：Yarn中的作业编号。
	 *          取值范围：不能为null。
	 * @return hrds.trigger.utils.YarnUtil.YarnApplicationReport
	 *          含义：表示作业的运行状况。
	 *          取值范围：不会为null。
	 */
	static YarnApplicationReport getApplicationReportByAppId(final String id) {

		String[] split = id.split(YARN_PARA_SEPARATOR);
		ApplicationId appId = ApplicationId.newInstance(Long.parseLong(split[1]),
				Integer.parseInt(split[2]));

		YarnApplicationReport yarnApplicationReport = new YarnApplicationReport();
		try(YarnClient yarnClient = YarnM.instance.getYarnClient()) {

			ApplicationReport report = yarnClient.getApplicationReport(appId);
			yarnApplicationReport.setAppId(report.getApplicationId().toString());
			yarnApplicationReport.setAppName(report.getName());
			yarnApplicationReport.setClientToAMToken(
					null == report.getClientToAMToken() ? "":
							report.getClientToAMToken().toString());
			yarnApplicationReport.setAppDiagnostics(report.getDiagnostics());
			yarnApplicationReport.setAppMasterHost(report.getHost());
			yarnApplicationReport.setAppQueue(report.getQueue());
			yarnApplicationReport.setAppMasterRpcPort(report.getRpcPort());
			yarnApplicationReport.setAppStartTime(report.getStartTime());
			yarnApplicationReport.setProgress(report.getProgress());
			yarnApplicationReport.setAppUser(report.getUser());
			yarnApplicationReport.setAppFinishTime(report.getFinishTime());
			yarnApplicationReport.setAppcostTime(report.getFinishTime() - report.getStartTime());
			yarnApplicationReport.setApplicationType(report.getApplicationType());
			yarnApplicationReport.setAppTrackingUrl(report.getTrackingUrl());
			yarnApplicationReport.setYarnAppState(String.valueOf(report.getYarnApplicationState()));
			yarnApplicationReport.setDistributedFinalState(
					String.valueOf(report.getFinalApplicationStatus()));
			yarnApplicationReport.setStatus(String.valueOf(report.getYarnApplicationState()));

			logger.info("Got application report {}", yarnApplicationReport.toString());
		}
		catch(YarnException | IOException e) {
			e.printStackTrace();
		}

		return yarnApplicationReport;
	}

	/**
	 * 表示Yarn中作业的运行状况
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 */
	public static class YarnApplicationReport {

		private String appId;
		private String appName;
		private String clientToAMToken;
		private String appDiagnostics;
		private String appMasterHost;
		private String appQueue;
		private int appMasterRpcPort;
		private long appStartTime;
		private float progress;
		private String appUser;
		private long appFinishTime;
		private long appcostTime;
		private String applicationType;
		private String appTrackingUrl;
		private String yarnAppState;
		private String distributedFinalState;
		private String status;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getClientToAMToken() {
			return clientToAMToken;
		}

		public void setClientToAMToken(String clientToAMToken) {
			this.clientToAMToken = clientToAMToken;
		}

		public String getAppDiagnostics() {
			return appDiagnostics;
		}

		public void setAppDiagnostics(String appDiagnostics) {
			this.appDiagnostics = appDiagnostics;
		}

		public String getAppMasterHost() {
			return appMasterHost;
		}

		public void setAppMasterHost(String appMasterHost) {
			this.appMasterHost = appMasterHost;
		}

		public String getAppQueue() {
			return appQueue;
		}

		public void setAppQueue(String appQueue) {
			this.appQueue = appQueue;
		}

		public int getAppMasterRpcPort() {
			return appMasterRpcPort;
		}

		public void setAppMasterRpcPort(int appMasterRpcPort) {
			this.appMasterRpcPort = appMasterRpcPort;
		}

		public long getAppStartTime() {
			return appStartTime;
		}

		public void setAppStartTime(long appStartTime) {
			this.appStartTime = appStartTime;
		}

		public float getProgress() {
			return progress;
		}

		public void setProgress(float progress) {
			this.progress = progress;
		}

		public String getAppUser() {
			return appUser;
		}

		public void setAppUser(String appUser) {
			this.appUser = appUser;
		}

		public long getAppFinishTime() {
			return appFinishTime;
		}

		public void setAppFinishTime(long appFinishTime) {
			this.appFinishTime = appFinishTime;
		}

		public long getAppcostTime() {
			return appcostTime;
		}

		public void setAppcostTime(long appcostTime) {
			this.appcostTime = appcostTime;
		}

		public String getApplicationType() {
			return applicationType;
		}

		public void setApplicationType(String applicationType) {
			this.applicationType = applicationType;
		}

		public String getAppTrackingUrl() {
			return appTrackingUrl;
		}

		public void setAppTrackingUrl(String appTrackingUrl) {
			this.appTrackingUrl = appTrackingUrl;
		}

		public String getYarnAppState() {
			return yarnAppState;
		}

		public void setYarnAppState(String yarnAppState) {
			this.yarnAppState = yarnAppState;
		}

		public String getDistributedFinalState() {
			return distributedFinalState;
		}

		public void setDistributedFinalState(String distributedFinalState) {
			this.distributedFinalState = distributedFinalState;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		@Override
		public String toString() {
			return "appId=" + appId + ", appName=" + appName + ", clientToAMToken=" +
					clientToAMToken + ", appDiagnostics=" + appDiagnostics + ", appMasterHost=" +
					appMasterHost + ", appQueue=" + appQueue + ", appMasterRpcPort=" +
					appMasterRpcPort + ", " + "appStartTime=" + appStartTime + ", appFinishTime=" +
					appFinishTime + ", ApplicationType=" + applicationType + ", yarnAppState=" +
					yarnAppState + ", distributedFinalState=" + distributedFinalState + ", " +
					"appTrackingUrl=" + appTrackingUrl + ", appUser=" + appUser + ",  Progress= " +
					progress + ",  status= " + status;
		}
	}
}

