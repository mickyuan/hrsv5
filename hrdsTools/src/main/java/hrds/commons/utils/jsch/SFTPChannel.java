package hrds.commons.utils.jsch;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class SFTPChannel {

	Channel channel = null;
	Session session = null;
	private static final String ENCODING = "UTF-8";

	private static final Logger logger = LogManager.getLogger();

	public ChannelSftp getChannel(Map<String, String> sftpDetails, int timeout) throws JSchException {

		String ftpHost = sftpDetails.get(SCPFileSender.HOST);
		String ftpUserName = sftpDetails.get(SCPFileSender.USERNAME);

		session = getJSchSession(sftpDetails, timeout);

		logger.debug("Opening Channel.");
		// 打开SFTP通道
		channel = session.openChannel("sftp");
		// 建立SFTP通道的连接
		channel.connect();
		logger.debug("Connected successfully to ftpHost = " + ftpHost + ",as ftpUserName = " + ftpUserName + ", returning: " + channel);
		return (ChannelSftp)channel;
	}

	/**
	 * 获取session
	 *
	 * @param sftpDetails
	 * @param timeout
	 * @return
	 * @throws JSchException
	 */
	public static Session getJSchSession(Map<String, String> sftpDetails, int timeout) throws JSchException {

		String ftpHost = sftpDetails.get(SCPFileSender.HOST);
		String port = sftpDetails.get(SCPFileSender.PORT);
		String ftpUserName = sftpDetails.get(SCPFileSender.USERNAME);
		String ftpPassword = sftpDetails.get(SCPFileSender.PASSWORD);

		int ftpPort = Integer.valueOf(port);

		// 创建JSch对象
		JSch jsch = new JSch();
		// 根据用户名，主机ip，端口获取一个Session对象
		Session session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
		logger.debug("Session created.");
		if( ftpPassword != null ) {
			// 设置密码
			session.setPassword(ftpPassword);
		}
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		// 为Session对象设置properties
		session.setConfig(config);
		// 设置timeout时间
		session.setTimeout(timeout);
		// 通过Session建立链接
		session.connect();
		logger.debug("Session connected.");

		return session;
	}

	public static void execCommandByJSchNoRs(Session session, String command) throws Exception {

		logger.info("执行命令为 : ", command);
		ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
		channelExec.getInputStream();
		channelExec.setCommand(command);
		channelExec.setErrStream(System.err);
		channelExec.connect();
		Thread.sleep(1000);
		channelExec.disconnect();

	}

	public static String execCommandByJSch(Session session, String command) throws JSchException, IOException {

		logger.info("执行命令为 : ", command);
		ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
		InputStream in = channelExec.getInputStream();
		channelExec.setCommand(command);
		channelExec.setErrStream(System.err);
		channelExec.connect();
		String result = IOUtils.toString(in, ENCODING);
		channelExec.disconnect();

		return result;
	}

	public static String execCommandByJSchToReadLine(Session session, String command) throws JSchException, Exception {

		logger.info("执行命令为 : ", command);
		ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
		//从远程端到达的所有数据都能从这个流中读取到
		InputStream inputStream = channelExec.getInputStream();
		//写入该流的所有数据都将发送到远程端。
		OutputStream outputStream = channelExec.getOutputStream();
		//使用PrintWriter流的目的就是为了使用println这个方法
		//好处就是不需要每次手动给字符串加\n
		PrintWriter printWriter = new PrintWriter(outputStream);
		printWriter.println(command);
		Thread.sleep(3000);
		//加上个就是为了，结束本次交互
		printWriter.println("exit");
		printWriter.flush();
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String msg = null;
		StringBuffer result = new StringBuffer();
		while( (msg = in.readLine()) != null ) {
			result.append(msg);
		}
		in.close();
		channelExec.disconnect();
		return result.toString();
	}

	public void closeChannel() throws Exception {

		if( channel != null ) {
			channel.disconnect();
		}
		if( session != null ) {
			session.disconnect();
		}
	}
}
