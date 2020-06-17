package hrds.util;

import fd.ng.core.utils.StringUtil;
import fd.ng.test.junit.ParallelRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ParallerTest
 * date: 2020/6/8 18:35
 * author: zxz
 */
public class ParallerTest {
	private static final Logger logger = LogManager.getLogger(ParallerTest.class);

	public static void main(String[] args) {
		if (args.length < 4) {
			logger.info("请传递正确的参数");
			logger.info("参数一：并发数，必须为数字类型");
			logger.info("参数二：每个测试用例并发执行次数，必须为数字类型");
			logger.info("参数三：需要测试的测试用例的配置文件绝对路径");
			logger.info("参数四：测试结果写文件，文件夹路径");
		}
		final int concurrent_number = Integer.parseInt(args[0]);
		final int execute_count = Integer.parseInt(args[1]);
		final String classNameFilePath = args[2];
		final String path = args[3];
		//读取文件，遍历需要测试的类
		try (Stream<String> stream = Files.lines(Paths.get(classNameFilePath))) {
			stream.filter(line -> !StringUtil.isEmpty(line)).forEach(className -> {
				//获取className的名称
				String name = className.substring(className.lastIndexOf(".") + 1);
				try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(path + name + concurrent_number + ".log"),
						StandardCharsets.UTF_8))) {
					for (int i = 0; i < execute_count; i++) {
						Class[] cls = new Class[concurrent_number];
						for (int j = 0; j < concurrent_number; j++) {
							try {
								cls[j] = Class.forName(className);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
						// 多个类并发执行
						Result rt = JUnitCore.runClasses(ParallelRunner.classes(), cls);
						bufferedWriter.write("wasSuccessful=" + rt.wasSuccessful() + ", " +
								"getIgnoreCount=" + rt.getIgnoreCount());
						bufferedWriter.newLine();
						bufferedWriter.write("getRunCount=" + rt.getRunCount() + ", getRunTime="
								+ (rt.getRunTime() / 1000) + "秒");
						bufferedWriter.newLine();
						bufferedWriter.write("getFailureCount=" + rt.getFailureCount() + ", getFailures="
								+ rt.getFailures().stream().map(Failure::toString).collect(Collectors.joining(" | ")));
						bufferedWriter.newLine();
						bufferedWriter.write("==========================================================================");
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}
				} catch (Exception e) {
					logger.error(e);
					System.exit(-1);
				}
			});
		} catch (IOException e) {
			logger.error(e);
			System.exit(-1);
		}
	}
}
