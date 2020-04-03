package hrds.control.utils;

import java.io.IOException;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hrds.commons.exception.AppSystemException;

/**
 * @ClassName: YarnUtil
 * @Description: 大数据组件Yarn操作的工具类。
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 11:41
 **/
public class YarnUtil {

	private static final Logger logger = LogManager.getLogger();

	static {
		System.setProperty("SPARK_YARN_MODE", "true");
	}

	/**
	 * 通过在Yarn上的作业编号杀死该作业。<br>
	 * 1、对传入的作业编号字符串进行格式转换，将其转化为可在Yarn上操作的样式；<br>
	 * 2、根据传入的作业编号获得该作业的状态，若作业状态不为[已结束、已杀死、已失败]，则会杀死该作业。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 * @param applicationId <br>
	 *          含义：Yarn上的作业编号。 <br>
	 *          取值范围：任意字符串。
	 */
	public static void killApplicationByid(String applicationId) {

		//1、对传入的作业编号字符串进行格式转换，将其转化为可在Yarn上操作的样式；
		ApplicationId appId = ConverterUtils.toApplicationId(applicationId);
		YarnClient yarnClient = YarnM.instance.getYarnClient();
		//2、根据传入的作业编号获得该作业的状态，若作业状态不为[已结束、已杀死、已失败]，则会杀死该作业。
		ApplicationReport appReport = null;
		try {
			appReport = yarnClient.getApplicationReport(appId);
		}
		catch(ApplicationNotFoundException e) {
			logger.warn("Application with id {} doesn't exist in RM.", applicationId);
		}
		catch(YarnException | IOException e) {
			e.printStackTrace();
		}
		if(null == appReport) {
			throw new AppSystemException("Application with id '" +
					applicationId + "' doesn't exist in RM.");
		}
		if(appReport.getYarnApplicationState() == YarnApplicationState.FINISHED) {
			logger.warn("Application {} has already FINISHED ", applicationId);
		}
		else if(appReport.getYarnApplicationState() == YarnApplicationState.KILLED) {
			logger.warn("Application {} has already KILLED ", applicationId);
		}
		else if(appReport.getYarnApplicationState() == YarnApplicationState.FAILED) {
			logger.warn("Application {} has already FAILED ", applicationId);
		}
		else {
			try {
				yarnClient.killApplication(appId);
			}
			catch(YarnException | IOException e) {
				e.printStackTrace();
			}

			logger.info("Killing application {} SUCCEEDED ", applicationId);
		}
	}
}
