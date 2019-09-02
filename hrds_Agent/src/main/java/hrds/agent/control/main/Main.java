package hrds.agent.control.main;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.agent.control.server.ControlManageServer;
import hrds.constans.JobStatus;
import hrds.entity.Etl_sys;

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
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param args	String数组，四个参数：跑批批次日期（8位字符）、调度系统代码、
	 *              是否续跑（true/false）、是否自动日切（true/false）
	 */
	public static void main(String[] args) {

		if(args.length != 4) {
			throw new IllegalArgumentException("参数个数错误，需要4个参数");
		}

		String strBathDate = args[0];	//跑批批次日期
		LocalDate bathDate = DateUtil.parseStr2DateWith8Char(strBathDate);//此处隐式的验证字符串日期是否格式正确
		if(null == bathDate){
			throw new IllegalArgumentException("非法[跑批日期]参数：" + strBathDate);
		}

		String strResumeRun = args[2];
		if(StringUtil.isEmpty(strResumeRun)) {
			throw new IllegalArgumentException("非法[是否续跑]参数（true/false）：" + strResumeRun);
		}
		boolean isResumeRun = Boolean.valueOf(strResumeRun);  //是否续跑，true/false值

		String strAutoShift = args[3];
		if(StringUtil.isEmpty(strAutoShift)) {
			throw new IllegalArgumentException("非法[是否自动日切]参数（true/false）：" + strAutoShift);
		}
		boolean isAutoShift = Boolean.valueOf(strAutoShift);  //是否自动日切，true/false值

		String strSystemCode = args[1]; //调度系统代码
		Etl_sys etlSys;
		//TODO get() 不会抛出想要的异常，改成orElseThrow()
		try(DatabaseWrapper db = new DatabaseWrapper()) {
			etlSys = SqlOperator.queryOneObject(db, Etl_sys.class,
						"SELECT sys_run_status, curr_bath_date FROM etl_sys WHERE etl_sys_cd = ?",
						strSystemCode).get();
		}
		/*
		 * 1、若通过调度系统代码（etl_sys_cd）无法查询到调度系统信息，则抛出异常；
		 * 2、若调度服务启动时，调度系统已经在运行，则抛出异常；
		 * 3、若以续跑的方式启动调度服务，续跑日期与当前批量日期不一致，则抛出异常。
		 */
		//TODO 使用自动生成的代码项，etlSys.getSys_run_status()要转换为枚举类，用AppException
		if(!JobStatus.STOP.getCode().equals(etlSys.getSys_run_status())) {
			throw new IllegalArgumentException("调度系统不在停止状态：" + strSystemCode);
		}else if(isResumeRun){
			LocalDate currBathDate = DateUtil.parseStr2DateWith8Char(etlSys.getCurr_bath_date());
			if(!currBathDate.equals(bathDate)){
				throw new IllegalArgumentException("续跑日期与当前批量日期不一致：" + strSystemCode);
			}
		}

		logger.info(String.format("开始启动Agent服务，跑批日期：%s，系统代码：%s，是否续跑：%s，是否自动日切：%s",
						strBathDate, strSystemCode, isResumeRun, isAutoShift));

		//启动调度服务
		ControlManageServer cm = new ControlManageServer(bathDate, strSystemCode, isResumeRun, isAutoShift);
		cm.runCMServer();



		logger.info("-------------- Agent服务启动完成 --------------");
	}
}
