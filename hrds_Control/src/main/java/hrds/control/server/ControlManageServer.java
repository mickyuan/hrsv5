package hrds.control.server;

import java.time.LocalDate;

import hrds.control.task.TaskManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * ClassName: ControlManageServer <br/>
 * Function: 用于控制及管理任务，该类决定具体任务以及任务的执行及执行时间间隔。<br/>
 * Date: 2019/7/30 16:58 <br/>
 *
 * Author Tiger.Wang
 * Version 1.0
 * Since JDK 1.8
 **/
public class ControlManageServer {

	private static final Logger logger = LogManager.getLogger();
	private final CMServerThread cmThread = new CMServerThread();
	private static TaskManager taskManager;

	/**
	 * ControlManageServer类构造器
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param sysRunning	系统是否在运行中
	 * @param bathDate	跑批批次日期
	 * @param strSystemCode	调度系统代码
	 * @param isResumeRun	是否续跑
	 * @param isAutoShift	是否自动日切
	 */
	public ControlManageServer(boolean sysRunning, String strSystemCode, LocalDate bathDate, boolean isResumeRun, boolean isAutoShift) {

		taskManager = TaskManager.newInstance(sysRunning, strSystemCode, bathDate, isResumeRun, isAutoShift);
	}

	/**
	 * 线程方式启动服务
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 */
	public void runCMServer() {

		cmThread.start();
		logger.info("调度服务启动成功");
	}

	/**
	 * 停止服务，最终会停止线程
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 */
	public void stopCMServer() {

		cmThread.stopThread();
		logger.info("调度服务停止成功");
	}

	private class CMServerThread extends Thread {

		private volatile boolean run = true;

		void stopThread() {

			this.run = false;
		}

		@Override
		public void run() {

			while( run ) {
                taskManager.loadReadyJob();
                taskManager.handleReadyJob();
				try {
					Thread.sleep(500);
				}
				catch(InterruptedException e) {
					logger.warn("系统出现异常：{}，但是继续执行", e.getMessage());
				}
			}
		}
	}
}
