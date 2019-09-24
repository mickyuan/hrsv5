package hrds.b.biz.agent.tools;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import hrds.commons.utils.jsch.SFTPChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: wangz
 * @CreateTime: 2019-09-16-15:53
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.b.biz.agent.tools
 **/
public class LogReader {

	private static final Logger logger = LogManager.getLogger(LogReader.class);

	/**
	 * 根据日志文件路径、agent_ip,agent_port,userName,password读取readNum行日志
	 *
	 * 1、构建使用jsch访问日志的Map集合
	 * 2、调用方法获取jSchSession
	 * 3、拼接读取日志的linux命令
	 * 4、读取日志
	 * 5、返回日志信息
	 *
	 * @Param: logPath String
	 *         含义: 日志文件路径
	 *         取值范围：不为空
	 * @Param: ip String
	 *         含义: agent所在服务器ip
	 *         取值范围：不为空,格式为 : XXX.XXX.XXX.XXX
	 * @Param: port String
	 *         含义: agent提供服务的端口
	 *         取值范围：不为空
	 * @Param: password String
	 *         含义: jsch使用的password
	 *         取值范围：不为空
	 * @Param: readNum int
	 *         含义: 读取日志文件的行数
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义 : 日志信息
	 *          取值范围 : 不为空，如果没有日志信息，返回值为空字符串
	 *
	 * */
	public static String readAgentLog(String logPath, String ip, String port, String userName,
	                                  String password, int readNum) {
		String execCommandByJSch;
		Session jSchSession = null;

		try {
			//1、构建使用jsch访问日志的Map集合
			Map<String, String> serverInfo = new HashMap<>();
			serverInfo.put("HOST", ip);
			serverInfo.put("PORT", port);
			serverInfo.put("USERNAME", userName);
			serverInfo.put("PASSWORD", password);
			//2、调用方法获取jSchSession
			jSchSession = SFTPChannel.getJSchSession(serverInfo, 0);
			//3、拼接读取日志的linux命令
			String readShell = "cat " + logPath + " |tail -n " + readNum;
			//4、读取日志
			execCommandByJSch = SFTPChannel.execCommandByJSch(jSchSession, readShell);
		} catch (JSchException ex) {
			execCommandByJSch = ex.getMessage();
			logger.error("登录验证失败...", ex);
		} catch (IOException ex) {
			execCommandByJSch = ex.getMessage();
			logger.error("读取日志文件-----\" + logPath + \"-----失败...", ex);
		} finally {
			if (jSchSession != null) {
				jSchSession.disconnect();
			}
		}
		//5、返回日志信息
		return execCommandByJSch;
	}
}
