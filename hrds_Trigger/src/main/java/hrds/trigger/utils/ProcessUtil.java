package hrds.trigger.utils;

import java.lang.reflect.Field;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.trigger.task.executor.TaskExecutor;

/**
 * ClassName: ProcessUtil<br>
 * Description: 用于控制作业进程的工具类。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 11:56<br>
 * Since: JDK 1.8
 **/
public class ProcessUtil {

	private final static long SLEEP_TIME = 3000;
	private final static String WINDOWS_PID_FLAG = "handle";
	private final static String LINUX_PID_FLAG = "pid";

	interface Kernel32 extends Library {

		Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class);

		int GetProcessId(Long hProcess);
	}

	/**
	 * 获取操作系统为windows或linux中进程的进程号。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param process
	 *          含义：进程对象。
	 *          取值范围：不能为null。
	 * @return int
	 *          含义：进程号。
	 *          取值范围：int范围数组。
	 */
	public static int getPid(final Process process) {

		try {
			if(Platform.isWindows()) {
				Field f = process.getClass().getDeclaredField(WINDOWS_PID_FLAG);
				f.setAccessible(true);
				return Kernel32.INSTANCE.GetProcessId((long)f.get(process));
			}else if(Platform.isLinux()) {
				Field f = process.getClass().getDeclaredField(LINUX_PID_FLAG);
				f.setAccessible(true);
				return (int)(Integer)f.get(process);
			}else {
				throw new AppSystemException("不支持的操作系统，目前仅支持Windows、Linux " +
						Platform.getOSType());
			}
		}catch(Exception ex) {
			throw new AppSystemException("获取进程编号发生异常" + ex);
		}
	}

	/**
	 * 获取Yarn中作业的作业编号。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param yarnName
	 *          含义：yarn中的作业名。
	 *          取值范围：不能为null。
	 * @return java.lang.String
	 *          含义：Yarn中的作业编号。
	 *          取值范围：不会为null。
	 */
	public static String getYarnAppID(final String yarnName) throws InterruptedException {

		while(true) {

			String yarnNameByAppID = YarnUtil.getApplicationIdByJobName(yarnName);

			if(StringUtil.isNotEmpty(yarnNameByAppID)) {
				return yarnNameByAppID;
			}

			Thread.sleep(SLEEP_TIME);
		}
	}

	/**
	 * 根据Yarn中的作业编号，获取该作业的运行状态。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param appId
	 *          含义：Yarn中的作业编号。
	 *          取值范围：不能为null。
	 * @return int
	 *          含义：作业运行状态。
	 *          取值范围：TaskExecutor.PROGRAM_DONE_FLAG/TaskExecutor.PROGRAM_ERROR_FLAG。
	 */
	public static int getStatusOnYarn(final String appId) throws InterruptedException {

		while(true) {

			YarnUtil.YarnApplicationReport yarnApplicationReport =
					YarnUtil.getApplicationReportByAppId(appId);

			String status = yarnApplicationReport.getStatus();
			//NEW, NEW_SAVING, SUBMITTED, ACCEPTED, RUNNING, FINISHED, FAILED, KILLED
//			if( "NEW_SAVING".equals(status) || "NEW".equals(status) || "SUBMITTED".equals(status) || "ACCEPTED".equals(status) ) {
//				continue;
//			}
			if(YarnStatus.FINISHED.getValue().equals(status)) {
				return TaskExecutor.PROGRAM_DONE_FLAG;
			}else if(YarnStatus.FAILED.getValue().equals(status) ||
					YarnStatus.KILLED.getValue().equals(status)) {
				return TaskExecutor.PROGRAM_ERROR_FLAG;
			}

			Thread.sleep(SLEEP_TIME);
		}
	}

	/**
	 * 表示Yarn中作业的作业状态。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 */
	private enum YarnStatus {
		NEW("NEW"), NEW_SAVING("NEW_SAVING"), SUBMITTED("SUBMITTED"),
		ACCEPTED("ACCEPTED"), RUNNING("RUNNING"), FINISHED("FINISHED"),
		FAILED("FAILED"), KILLED("KILLED");

		private final String value;
		YarnStatus(String value) { this.value = value; }

		public String getValue() { return this.value; }

		@Override
		public String toString() {
			return this.value;
		}
	}
}
