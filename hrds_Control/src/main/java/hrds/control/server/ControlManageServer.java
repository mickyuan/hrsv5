package hrds.control.server;

import hrds.control.task.helper.TaskSqlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hrds.control.task.TaskManager;

/**
 *
 * ClassName: ControlManageServer <br/>
 * Function: 用于控制及管理任务，该类决定具体任务以及任务的执行及执行时间间隔。<br/>
 * Date: 2019/7/30 16:58 <br/>
 * Author Tiger.Wang
 * Version 1.0
 * Since JDK 1.8
 **/
public class ControlManageServer {

	private static final Logger logger = LogManager.getLogger();
	private static final CMServerThread cmThread = new CMServerThread();
	private static TaskManager taskManager;

	/**
	 * ControlManageServer类构造器。<br>
	 * 1.初始化TaskManager对象。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 * @param strSystemCode
	 *          含义：调度系统代码。
	 *          取值范围：不能为null。
	 * @param bathDate
	 *          含义：跑批批次日期 。
	 *          取值范围：yyyyMMdd格式的日期字符串，不能为null。
	 * @param isResumeRun
	 *          含义：是否续跑。
	 *          取值范围：不能为null。
	 * @param isAutoShift
	 *          含义：是否自动日切。
	 *          取值范围：不能为null。
	 */
	public ControlManageServer(String strSystemCode, String bathDate,
	                           boolean isResumeRun, boolean isAutoShift) {
		//1.初始化TaskManager对象。
		taskManager = new TaskManager(strSystemCode, bathDate, isResumeRun, isAutoShift);
	}

	/**
	 * 初始化CM服务。<br>
	 * 1.初始化调度系统。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 */
	public void initCMServer() {
		//1.初始化调度系统。
		taskManager.initEtlSystem();
	}

	/**
	 * 线程方式启动服务。<br>
	 * 1.以线程的方式启动CM服务；
	 * 2.以线程的方式启动监测信息文件的服务。
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 */
	public void runCMServer() {
		//1.以线程的方式启动CM服务；
		cmThread.start();
		//2.以线程的方式启动监测信息文件的服务。
		taskManager.startCheckWaitFileThread();
		logger.info("调度服务启动成功");
	}

	/**
	 * 停止服务，最终会停止线程。<br>
	 * 1.停止监测信息文件的服务；
	 * 2.停止CM服务.
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 */
	public void stopCMServer() {
		//1.停止监测信息文件的服务；
		taskManager.stopCheckWaitFileThread();
		//2.停止CM服务。
		cmThread.stopThread();
		logger.info("调度服务停止成功");
	}

	/**
	 *
	 * ClassName: CMServerThread <br/>
	 * Function: 用于以线程方式启动CM服务。<br/>
	 * Date: 2019/7/30 16:58 <br/>
	 * Author Tiger.Wang
	 * Version 1.0
	 * Since JDK 1.8
	 **/
	private static class CMServerThread extends Thread {

		private volatile boolean run = true;

		/**
		 * 停止CM服务。<br>
		 * 1.线程持续运行标识置为[停止]。
		 * @author Tiger.Wang
		 * @date 2019/10/8
		 */
		void stopThread() {
			//1.线程持续运行标识置为[停止]。
			this.run = false;
		}

		/**
		 * 执行作业调度服务。注意，该方法为线程的执行（run）方法。<br>
		 * 1、加载各种作业的资源到内存中（包括MAP等成员变量）；<br>
		 * 2、死循环方式处理当前批次的所有作业（目的是写入 redis）；<br>
		 * 3、日切处理（继续还是整个程序退出） 。
		 * @author Tiger.Wang
		 * @date 2019/10/8
		 */
		@Override
		public void run() {
			try {
				while(run) {
					//1、加载各种作业的资源到内存中（包括MAP等成员变量）；
					taskManager.loadReadyJob();
					//2、死循环方式处理当前批次的所有作业（目的是写入 redis）；
					taskManager.publishReadyJob();
					//3、日切处理（继续还是整个程序退出） 。
					if(!taskManager.needDailyShift()){
						logger.info("------ 系统无日切信号，系统退出 ------");
						break;
						//FIXME 作业能配置成：每天某个时刻执行，但是不日切吗？或者等待信号文件到达就执行，执行完继续等待，且没有日切的概念
					}
				}
			}catch(Exception ex) {
				logger.error("Exception happened!", ex);
			}finally {
				TaskSqlHelper.closeDbConnector();   //关闭数据库连接
			}
		}
	}
}
