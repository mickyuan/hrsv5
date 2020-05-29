package hrds.h.biz.spark.running;

import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.exec.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SparkJobRunner {

    private static final Logger logger = LogManager.getLogger(SparkJobRunner.class);

    private static final String SPARK_MAIN_CLASS =
            "hrds.h.biz.spark.running.MarketSparkMain";
//        private static final String SPARK_CLASSPATH =
//            ".:hrds_H/build/libs/hrds_H-5.0.jar:hrds_H/src/main/resources/:" +
//                    "../spark/jars/*:lib/fd-*:lib/hrds-commons-5.0.jar";
    private static final String SPARK_CLASSPATH =
            ".:hrds_H-5.0.jar:resources/:" +
                    "../spark/jars/*:../lib/fd-*:../lib/hrds-commons-5.0.jar";

    private static final long SPARK_JOB_TIMEOUT_SECONDS =
            PropertyParaValue.getLong("spark.job.timeout.seconds", 2L * 60 * 60);
    private static final String SPARK_DRIVER_EXTRAJAVAOPTIONS =
            PropertyParaValue.getString("spark.driver.extraJavaOptions", "-Xss20m -Xmx4096m");

    static {
        try {
            Class.forName(SPARK_MAIN_CLASS);
        } catch (ClassNotFoundException e) {
            logger.error("主类不存在");
        }
    }

    public static void runJob(String dataTableId, SparkHandleArgument handleArgument) {

        long start = System.currentTimeMillis();

        String command = String.format("java %s -cp %s %s %s %s",
                SPARK_DRIVER_EXTRAJAVAOPTIONS,
                SPARK_CLASSPATH,
                SPARK_MAIN_CLASS,
                dataTableId,
                convertStringSatisfyShell(handleArgument.toString()));

        logger.info(String.format("开始执行spark作业调度:[%s]", command));

        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();

        //创建监控时间，超过限制时间则中端执行，默认2小时
        ExecuteWatchdog watchdog = new ExecuteWatchdog(SPARK_JOB_TIMEOUT_SECONDS * 1000);
        executor.setWatchdog(watchdog);

        //接收执行结果流
        executor.setStreamHandler(new PumpStreamHandler(new LogOutputStream() {
            @Override
            protected void processLine(String line, int logLevel) {
                logger.info(line);
            }
        }));

        //spark 进程执行过程中，JVM退出，直接关闭进程
        //获取到 SIGTERM & SIGINT 会自动关闭进程 （yarn-client/lcal模式下生效）
        Thread shutdownThread = new Thread(watchdog::destroyProcess);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
        try {
            //提起进程
            executor.execute(commandLine);
        } catch (Exception e) {
            throw new AppSystemException("调度spark作业失败");
        } finally {
            logger.info("Spark作业执行时间：" + (System.currentTimeMillis() - start) / 1000 + "s");
            //进程完成后，删除关闭钩子
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
        }

    }

    /**
     * 带有双引号的字符串参数被执行时，参数会消失掉
     * 这里处理一下
     *
     * @param shellArg 可能带有双引号的参数字符串
     * @return 处理后的不带有双引号的字符串
     */
    static String convertStringSatisfyShell(String shellArg) {
        return '"' + StringUtil.replace(shellArg, '"', '^') + '"';
    }

    /**
     * 从shell中获取的参数转换成合法参数字符串
     *
     * @param convertedShellArg 从shell中获取的参数
     * @return 合法参数字符串
     */
    static String obtainSatisfyShellString(String convertedShellArg) {
        return convertedShellArg.replace("^", "\"");
    }

}
