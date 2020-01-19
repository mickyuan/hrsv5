package hrds.commons.hadoop.hbaseindexer.process;

import hrds.commons.exception.BusinessException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/*
 * 执行脚本
 */
public class CommandRunner {
	protected static final Log logger = LogFactory.getLog(CommandRunner.class);

	public static void run(String command) throws IOException, InterruptedException {
		logger.info("Executing command: [" + command + "]");
		Process process = Runtime.getRuntime().exec(command);
		String errorInfo = IOUtils.toString(process.getErrorStream(), Charset.forName("UTF-8"));
		int exitValue = process.waitFor();
		if (0 != exitValue) {
			//delete indexer的时候不存在，也属于正常
			if(!errorInfo.contains("Indexer does not exist")) {
				throw new BusinessException("Command execution error :" + errorInfo);
			}
		} else {
			logger.info("Command executed successfully!");
		}
	}

	public static void run(List<String> commands) throws IOException, InterruptedException {

		for (String command : commands) {
			run(command);
		}
	}
}
