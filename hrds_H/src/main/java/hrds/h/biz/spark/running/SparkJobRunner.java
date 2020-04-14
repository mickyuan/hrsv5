package hrds.h.biz.spark.running;

import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SparkJobRunner {

    private static final Log logger = LogFactory.getLog(SparkJobRunner.class);

    private static final String SPARK_MAIN_CLASS = "hrds.h.biz.spark.running.MarketSparkMain";
    private static final String SPARK_CLASSPATH = ".:hrds_H-5.0.jar:../spark/jars/*";

    private static final long SPARK_JOB_TIMEOUT_SECONDS = PropertyParaValue.getLong("spark.job.timeout.seconds", 2L * 60 * 60);
    private static final String SPARK_DRIVER_EXTRAJAVAOPTIONS = PropertyParaValue.getString("spark.driver.extraJavaOptions", "-Xss20m -Xmx4096m");

    static {
        if (!isClassExist(SPARK_MAIN_CLASS)) {
            throw new AppSystemException("主类" + SPARK_MAIN_CLASS + "不存在!");
        }
    }

    public static void runJob(String dataTableId, SparkHandleArgument handleArgument) {

        long start = System.currentTimeMillis();
        ByteArrayOutputStream out = null;
        ByteArrayOutputStream err = null;
        try {
            String command = String.format("java %s -cp %s %s %s %s",
                    SPARK_DRIVER_EXTRAJAVAOPTIONS,
                    SPARK_CLASSPATH, SPARK_MAIN_CLASS, dataTableId, handleArgument);
            logger.info(String.format("开始执行spark作业调度:[%s]", command));
            CommandLine commandLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();

            //创建监控时间，超过限制时间则中端执行，默认2小时
            ExecuteWatchdog watchdog = new ExecuteWatchdog(SPARK_JOB_TIMEOUT_SECONDS * 1000);
            executor.setWatchdog(watchdog);

            //接收执行结果流
            out = new ByteArrayOutputStream();
            err = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(out, err);
            executor.setStreamHandler(streamHandler);
            executor.execute(commandLine);
            logger.info("执行时间：" + (System.currentTimeMillis() - start) / 1000 + "s");
        } catch (Exception e) {
            throw new AppSystemException("调度spark作业失败：", e);
        } finally {
            if (err != null) {
                try {
                    err.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static boolean isClassExist(String className) {

        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void runJob(Long dataTableId, SparkHandleArgument handleArgument) {

        runJob(String.valueOf(dataTableId), handleArgument);
    }

}
