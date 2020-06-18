package hrds.util;

import fd.ng.core.utils.StringUtil;
import fd.ng.test.junit.ParallelRunner;
import hrds.commons.exception.AppSystemException;
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
import java.util.List;
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
		if (args.length < 2) {
			logger.info("请传递正确的参数");
			logger.info("参数一：需要测试的测试用例的配置文件绝对路径");
			logger.info("参数二：测试结果写文件，文件夹路径");
		}
		final String classNameFilePath = args[0];
		final String path = args[1];
		//读取文件，遍历需要测试的类
		try (Stream<String> stream = Files.lines(Paths.get(classNameFilePath))) {
			stream.skip(1).filter(line -> !StringUtil.isEmpty(line)).forEach(line -> {
				List<String> lineList = StringUtil.split(line, "|");
				if (lineList.size() != 3) {
					throw new AppSystemException(classNameFilePath + "文件内" + line + "定义不正确");
				}
				String className = lineList.get(0);
				int concurrent_number = Integer.parseInt(lineList.get(1));
				int execute_count = Integer.parseInt(lineList.get(2));
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
								logger.error(e);
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
					logger.error("执行" + lineList.get(0) + "类测试用例错误", e);
				}
			});
		} catch (IOException e) {
			logger.error(e);
			System.exit(-1);
		}
	}
}
