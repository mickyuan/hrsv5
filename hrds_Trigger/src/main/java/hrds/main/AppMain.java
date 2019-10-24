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

	public static void main(String[] args) {

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

		TriggerManageServer triggerManageServer = new TriggerManageServer(strSystemCode);
		triggerManageServer.runCMServer();

		System.out.println("-------------- Trigger服务启动完成 --------------");
	}
}
