package hrds.trigger.task.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * ClassName: WatchThread<br>
 * Description: 用于监控进程的执行日志的类。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 14:54<br>
 * Since: JDK 1.8
 **/
class WatchThread extends Thread {

	private static final Logger logger = LogManager.getLogger();

	private static final String ENCODING = "UTF-8";
	private InputStream inputStream;
	private String logDirc;

	WatchThread(InputStream inputStream, String logDirc) {

		this.inputStream = inputStream;
		this.logDirc = logDirc;
	}

	/**
	 * 监控进程的执行日志，将日志写到指定文件中。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 */
	@Override
	public void run() {

		File file = new File(logDirc);

		try(InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		    BufferedReader br = new BufferedReader(inputStreamReader);
		    OutputStream outputStream = new FileOutputStream(file, true);
		    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, ENCODING);
		    PrintWriter pw = new PrintWriter(outputStreamWriter, true)) {

			if(!file.exists() && !file.createNewFile()) {
				logger.warn("日志文件创建失败 {}", logDirc);
			}

			String line;
			while((line = br.readLine()) != null ) {
				pw.write(line + "\r\n");
				pw.flush();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
