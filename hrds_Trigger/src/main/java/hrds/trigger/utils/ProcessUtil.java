package hrds.trigger.utils;

import java.lang.reflect.Field;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.trigger.task.executor.TaskExecutor;

public class ProcessUtil {

	private final static long SLEEP_TIME = 3000;
	private final static String YARN_STATUS_FLAG = "status";
	private final static String WINDOWS_PID_FLAG = "handle";
	private final static String LINUX_PID_FLAG = "pid";

	interface Kernel32 extends Library {

		Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class);

		int GetProcessId(Long hProcess);
	}

	public static int getPid(Process p) {

		try {
			if(Platform.isWindows()) {
				Field f = p.getClass().getDeclaredField(WINDOWS_PID_FLAG);
				f.setAccessible(true);
				return Kernel32.INSTANCE.GetProcessId((long)f.get(p));
			}else if(Platform.isLinux()) {
				Field f = p.getClass().getDeclaredField(LINUX_PID_FLAG);
				f.setAccessible(true);
				return (int)(Integer)f.get(p);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		throw new AppSystemException("不支持的操作系统，目前仅支持Windows、Linux " + Platform.getOSType());
	}

	public static String getAppID(String yarnName) throws InterruptedException {

		while(true) {

			String yarnNameByAppID = YarnUtil.getApplicationIdByJobName(yarnName);

			if(StringUtil.isNotEmpty(yarnNameByAppID)) {
				return yarnNameByAppID;
			}

			Thread.sleep(SLEEP_TIME);
		}
	}

	public static int getStatusOnYarn(String appId) throws InterruptedException {

		while(true) {

			JSONObject json = JSONObject.parseObject(YarnUtil.getStatusByAppId(appId));
			String status = json.getString(YARN_STATUS_FLAG);
			//NEW, NEW_SAVING, SUBMITTED, ACCEPTED, RUNNING, FINISHED, FAILED, KILLED
//			if( "NEW_SAVING".equals(status) || "NEW".equals(status) || "SUBMITTED".equals(status) || "ACCEPTED".equals(status) ) {
//				continue;
//			}
			if(YarnStatus.FINISHED.getValue().equals(status)) {
				return TaskExecutor.PROGRAM_DONE_FLAG;
			}else if(YarnStatus.FAILED.getValue().equals(status) ||
					YarnStatus.KILLED.getValue().equals(status)) {
				return TaskExecutor.PROGRAM_FAILED_FLAG;
			}

			Thread.sleep(SLEEP_TIME);
		}
	}

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
