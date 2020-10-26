package hrds.l.biz.autoanalysis.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class AutoOperateCommon {
	private static final Logger logger = LogManager.getLogger();
	public static long lineCounter = 0;

	public static void writeFile(Map<String, Object> map, String fileName) {
		StringBuffer sbCol = new StringBuffer();
		StringBuffer sbVal = new StringBuffer();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName), true))) {
			// map的key为列名称，value为列名称对应的对象信息
			lineCounter++;
			map.forEach((k, v) -> {
				sbCol.append(k).append(",");
				sbVal.append(v).append(",");
			});
			// 如果文件是CSV则第一行为列信息
			if (lineCounter == 1) {
				writer.write(sbCol.deleteCharAt(sbCol.length() - 1).toString());
				writer.newLine();
			}
			// 如果文件写了24608行进行一次刷新并打印日志
			if (lineCounter % 24608 == 0) {
				logger.info("已经处理了 ：" + lineCounter + " 行数据！");
				writer.flush();
			}
			// 写列对应值数据
			writer.write(sbVal.deleteCharAt(sbVal.length() - 1).toString());
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
