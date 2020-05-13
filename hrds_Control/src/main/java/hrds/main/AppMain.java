package hrds.main;

import fd.ng.core.cmd.ArgsParser;
import fd.ng.core.utils.DateUtil;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Job_Status;
import hrds.commons.entity.Etl_sys;
import hrds.commons.exception.AppSystemException;
import hrds.control.server.ControlManageServer;
import hrds.control.task.helper.TaskSqlHelper;

import java.time.LocalDate;

/**
 * ClassName: AppMain <br/>
 * Function: 调度系统Control程序启动类. <br/>
 * Date: 2019年7月26日 下午5:49:25 <br/>
 *
 * @author Tiger.Wang
 * @version 1.0
 * @since JDK 1.8
 */
public class AppMain {

    /**
     * 主程序入口，会验证传递的参数是否正常，调度系统是否存在。<br>
     * 1、初始化命令行参数；
     * 2、启动调度服务。
     *
     * @param args <br>
     *             含义：程序运行所需要的参数。 <br>
     *             取值范围：String数组，四个参数：跑批批次日期（8位字符）、
     *             调度系统代码、是否续跑（true/false）、是否自动日切（true/false）。
     * @author Tiger.Wang
     * @date 2019/10/8
     */
    public static void main(String[] args) {
        //1、初始化命令行参数。
        ArgsParser CMD_ARGS = new ArgsParser()
                .defOptionPair("etl.date", true, "跑批日期yyyyMMDD")
                .defOptionPair("sys.code", true, "调度系统代码")
                .defOptionPair("-AS", true, "是否自动日切")
                .defOptionPair("-CR", true, "是否为续跑")
                .parse(args);

        //跑批批次日期
        String bathDateStr = CMD_ARGS.opt("etl.date").value;
        boolean isResumeRun = CMD_ARGS.opt("-CR").value.equals(IsFlag.Shi.getCode()) ? true : false;
        boolean isAutoShift = CMD_ARGS.opt("-AS").value.equals(IsFlag.Shi.getCode()) ? true : false;
//		boolean isResumeRun = Boolean.parseBoolean(CMD_ARGS.opt("-CR").value);  //是否续跑
//		boolean isAutoShift = Boolean.parseBoolean(CMD_ARGS.opt("-AS").value);  //是否自动日切
        String strSystemCode = CMD_ARGS.opt("sys.code").value; //调度系统代码
        Etl_sys etlSys = TaskSqlHelper.getEltSysBySysCode(strSystemCode);
        //此处隐式的验证字符串日期是否格式正确
        LocalDate bathDate = LocalDate.parse(bathDateStr, DateUtil.DATE_DEFAULT);
        /*
         * 一、若调度服务启动时，调度系统已经在运行，则抛出异常；
         * 二、若以续跑的方式启动调度服务，续跑日期与当前批量日期不一致，则抛出异常。
         */
        if (!Job_Status.STOP.getCode().equals(etlSys.getSys_run_status())) {
            throw new AppSystemException("调度系统不在停止状态：" + strSystemCode);
        } else if (isResumeRun) {
            LocalDate currBathDate = DateUtil.parseStr2DateWith8Char(etlSys.getCurr_bath_date());
            if (!currBathDate.equals(bathDate)) {
                throw new AppSystemException("续跑日期与当前批量日期不一致：" + strSystemCode);
            }
        }

        System.out.println(String.format("开始启动Agent服务，跑批日期：%s，系统代码：%s，" +
                        "是否续跑：%s，是否自动日切：%s", bathDate.toString(), strSystemCode,
                isResumeRun, isAutoShift));

        //2、启动调度服务。
        //FIXME 讨论：启动一个HTTP SERVE，用于接收管理类通知（比如退出/暂停/DB连接等资源重置
        ControlManageServer cm = new ControlManageServer(strSystemCode, bathDateStr,
                isResumeRun, isAutoShift);
        cm.initCMServer();
        cm.runCMServer();
        //FIXME 是否有不同的任务线程被启动？如果有，在这里一个个启动。
        // 另外，如果启动的线程会很多吗？如果超过16个，要用线程池做处理
        // ------- 或者 -------
        // 在runCMServer这个方法里面做以上的事情：
        // 1）构造函数只做成员变量的轻量级赋值
        // 2）runCMServer 把每个初始化方法逐个调用，最后启动任务线程。有多种任务线程，则逐个启动
        // 3）启动管理用的HTTPSERVER

        System.out.println("-------------- Agent服务启动完成 --------------");
    }
}
