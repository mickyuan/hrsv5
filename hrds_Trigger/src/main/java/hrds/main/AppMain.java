package hrds.main;

import fd.ng.core.cmd.ArgsParser;
import hrds.commons.codes.Job_Status;
import hrds.commons.entity.Etl_sys;
import hrds.trigger.task.helper.TaskSqlHelper;
import hrds.trigger.server.TriggerManageServer;

/**
 * ClassName: AppMain<br>
 * Description: 调度系统trigger程序启动类。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 10:54<br>
 * Since: JDK 1.8
 **/
public class AppMain {

	/**
	 * trigger程序启动类的启动方法。主要逻辑点为：<br>
	 * 1、解析传递的参数（主要是调度系统编号），并检查该调度系统是否存在；<br>
	 * 2、启动核心逻辑的服务类。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param args
	 *          含义：启动trigger程序所需要的参数。
	 *          取值范围：参数数组中，必须有sys.code=xxx的元素。
	 */
	public static void main(String[] args) {

		//1、解析传递的参数（主要是调度系统编号），并检查该调度系统是否存在；
		ArgsParser CMD_ARGS = new ArgsParser()
				.defOptionPair("sys.code", true, "调度系统代码")
				.parse(args);

		String strSystemCode = CMD_ARGS.opt("sys.code").value; //调度系统代码
		System.out.println("开始启动Trigger程序，调度系统编号为：" + strSystemCode);

		//隐式检查该调度系统是否存在
		Etl_sys etlSys = TaskSqlHelper.getEltSysBySysCode(strSystemCode);

		if(Job_Status.ofEnumByCode(etlSys.getSys_run_status()) != Job_Status.RUNNING) {
			System.out.println("调度系统不为运行状态，Trigger程序启动失败。");
			return;
		}

		//2、启动核心逻辑的服务类。
		TriggerManageServer triggerManageServer = new TriggerManageServer(strSystemCode);
		triggerManageServer.runCMServer();

		System.out.println("-------------- Trigger服务启动完成 --------------");
	}
}
