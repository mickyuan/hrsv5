package hrds.trigger.server;

import hrds.trigger.beans.EtlJobParaAnaly;
import hrds.trigger.task.TaskManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hrds.trigger.task.helper.TaskSqlHelper;

/**
 * ClassName: TriggerManageServer<br>
 * Description: <br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 11:34<br>
 * Since: JDK 1.8
 **/
public class TriggerManageServer {

	private static final Logger logger = LogManager.getLogger();

	private static final long SLEEP_TIME = 1000;

	private final TaskManager taskManager;
	private final CMServerThread cmThread;

	public TriggerManageServer(String etlSysCode) {

		this.taskManager = new TaskManager(etlSysCode);
		this.cmThread = new CMServerThread();
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
	private class CMServerThread extends Thread {

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
		 * @author Tiger.Wang
		 * @date 2019/10/8
		 */
		@Override
		public void run() {

			try {
				while(run) {

					if(!taskManager.checkSysGoRun()){ return; }

					EtlJobParaAnaly etlJobParaAnaly = taskManager.getEtlJob();
					if(etlJobParaAnaly.isHasEtlJob()) {
						taskManager.runEtlJob(etlJobParaAnaly.getEtlJobCur(),
								etlJobParaAnaly.isHasHandle());
					}

					Thread.sleep(SLEEP_TIME);
				}
			}catch(Exception ex) {
				TaskSqlHelper.closeDbConnector();//关闭数据库连接
				logger.error("Exception happened!", ex);
			}
		}
	}
}
