package hrds.trigger.utils;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;

import com.alibaba.fastjson.JSONObject;

import hrds.trigger.hadoop.readConfig.ClassPathResLoader;

class YarnUtil {

	private static final Log logger = LogFactory.getLog(YarnUtil.class);
	private static final String confDir = System.getProperty("user.dir") + File.separator + "conf" + File.separator;

	static {
		System.setProperty("SPARK_YARN_MODE", "true");
		ClassPathResLoader.loadResourceDir(confDir);//加载spark配置文件到classpath中
	}

//	/**
//	 * 获取任务的ApplicationId通过作业名jobName
//	 * @param jobName
//	 * @return appId
//	 */
	static String getApplicationIdByJobName(String jobName) {

		YarnClient yarnClient = YarnM.instance.getYarnClient();
		EnumSet<YarnApplicationState> appStates = EnumSet.noneOf(YarnApplicationState.class);
		List<ApplicationReport> appsReport;
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
	static String getStatusByAppId(String id) {
		//TODO 待修改
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
			json.put("appName", report.getName());
			json.put("clientToAMToken", null == report.getClientToAMToken()?"":report.getClientToAMToken().toString());
			json.put("appDiagnostics", report.getDiagnostics());
			json.put("appMasterHost", report.getHost());
			json.put("appQueue", report.getQueue());
			json.put("appMasterRpcPort", report.getRpcPort());
			json.put("appStartTime", report.getStartTime());
			json.put("progress", report.getProgress());
			json.put("appUser", report.getUser());
			json.put("appFinishTime", report.getFinishTime());
			json.put("appcostTime", report.getFinishTime() - report.getStartTime());
			json.put("applicationType", report.getApplicationType());
			json.put("appTrackingUrl", report.getTrackingUrl());
			json.put("yarnAppState", report.getYarnApplicationState().toString());
			json.put("distributedFinalState", report.getFinalApplicationStatus().toString());
			json.put("status", report.getYarnApplicationState().toString());
			yarnClient.close();
		}
		catch(YarnException | IOException e) {
			e.printStackTrace();
		}

		return JSONObject.toJSONString(json);
	}
}
