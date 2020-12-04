package hrds.k.biz.algorithms.main;

import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.exec.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SparkJobRunner {
	private static final Logger logger = LogManager.getLogger(SparkJobRunner.class);

	private static final String ALGORITHMS_SPARK_CLASSPATH = PropertyParaValue.getString(
			"algorithms_spark_classpath", "");

	private static final long SPARK_JOB_TIMEOUT_SECONDS =
			PropertyParaValue.getLong("spark.job.timeout.seconds", 24L * 60 * 60);
	private static final String SPARK_DRIVER_EXTRAJAVAOPTIONS =
			PropertyParaValue.getString("spark.driver.extraJavaOptions", "-Xss20m -Xmx49152m");


	public static void runJob(String spark_main_class, String table_name) {
		try {
			Class.forName(spark_main_class);
		} catch (ClassNotFoundException e) {
			logger.error("主类不存在");
		}
		long start = System.currentTimeMillis();

		String command = String.format("java %s -cp %s %s %s",
				SPARK_DRIVER_EXTRAJAVAOPTIONS,
				ALGORITHMS_SPARK_CLASSPATH,
				spark_main_class,
				table_name);

		logger.info(String.format("开始执行spark作业调度:[%s]", command));

		CommandLine commandLine = CommandLine.parse(command);
		DefaultExecutor executor = new DefaultExecutor();

		//创建监控时间，超过限制时间则中端执行，默认24小时
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

}
