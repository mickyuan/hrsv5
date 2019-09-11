package hrds.control.main;

import java.time.LocalDate;

import hrds.commons.codes.Job_Status;
import hrds.commons.entity.Etl_sys;
import hrds.commons.exception.AppSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.control.server.ControlManageServer;
import hrds.control.task.helper.TaskSqlHelper;

/**
 *
 * ClassName: Main <br/>
 * Function: 调度系统Control程序启动类. <br/>
 * Date: 2019年7月26日 下午5:49:25 <br/>
 *
 * @author Tiger.Wang
 * @version	1.0
 * @since JDK 1.8
 */
public class Main {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * 主程序入口，会验证传递的参数是否正常，调度系统是否存在
	 * @note 方法逻辑为： 1、验证参数正确性合法性；
	 * 					 2、启动调度服务。
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param args	String数组，四个参数：跑批批次日期（8位字符）、调度系统代码、
	 *              是否续跑（true/false）、是否自动日切（true/false）
	 */
	public static void main(String[] args) {
		//1、验证参数正确性合法性。
		if(args.length != 4) {
			throw new AppSystemException("参数个数错误，需要4个参数");
		}

		String strBathDate = args[0];	//跑批批次日期
		LocalDate bathDate = DateUtil.parseStr2DateWith8Char(strBathDate);//此处隐式的验证字符串日期是否格式正确
		if(null == bathDate){
			throw new AppSystemException("非法[跑批日期]参数：" + strBathDate);
		}

		String strResumeRun = args[2];
		if(StringUtil.isEmpty(strResumeRun)) {
			throw new AppSystemException("非法[是否续跑]参数（true/false）：" + strResumeRun);
		}
		boolean isResumeRun = Boolean.parseBoolean(strResumeRun);  //是否续跑，true/false值

		String strAutoShift = args[3];
		if(StringUtil.isEmpty(strAutoShift)) {
			throw new AppSystemException("非法[是否自动日切]参数（true/false）：" + strAutoShift);
		}
		boolean isAutoShift = Boolean.parseBoolean(strAutoShift);  //是否自动日切，true/false值

		String strSystemCode = args[1]; //调度系统代码
		Etl_sys etlSys = TaskSqlHelper.getEltSysBySysCode(strSystemCode).orElseThrow(() ->
						new AppSystemException("无法根据[调度系统编号]获取系统信息：" + strSystemCode));
		/*
		 * 一、若调度服务启动时，调度系统已经在运行，则抛出异常；
		 * 二、若以续跑的方式启动调度服务，续跑日期与当前批量日期不一致，则抛出异常。
		 */
		if(!Job_Status.STOP.getCode().equals(etlSys.getSys_run_status())) {
			throw new AppSystemException("调度系统不在停止状态：" + strSystemCode);
		}else if(isResumeRun){
			LocalDate currBathDate = DateUtil.parseStr2DateWith8Char(etlSys.getCurr_bath_date());
			if(!currBathDate.equals(bathDate)){
				throw new AppSystemException("续跑日期与当前批量日期不一致：" + strSystemCode);
			}
		}

		logger.info(String.format("开始启动Agent服务，跑批日期：%s，系统代码：%s，是否续跑：%s，是否自动日切：%s",
						strBathDate, strSystemCode, isResumeRun, isAutoShift));

		//2、启动调度服务。
		ControlManageServer cm = new ControlManageServer(false, strSystemCode, bathDate, isResumeRun,
				isAutoShift);
		cm.runCMServer();

		logger.info("-------------- Agent服务启动完成 --------------");
	}
}
