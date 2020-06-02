package hrds.commons.utils.jsch;

import com.jcraft.jsch.*;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class SFTPChannel {

	private Channel channel = null;
	private Session session = null;
	private static final String ENCODING = "UTF-8";

	private static final Logger logger = LogManager.getLogger(SFTPChannel.class.getName());

	// FIXME 不要使用Map做参数！使用多个明确的参数，或者提供一个构建参数BEAN：本类的一个public static class
	public ChannelSftp getChannel(SFTPDetails sftpDetails, int timeout) throws JSchException {

		session = getJSchSession(sftpDetails, timeout);

		logger.debug("Opening Channel.");
		channel = session.openChannel("sftp"); // 打开SFTP通道
		channel.connect(); // 建立SFTP通道的连接
		logger.debug(
				"Connected successfully to ftpHost = "
						+ sftpDetails.getHost()
						+ ",as ftpUserName = "
						+ sftpDetails.getUser_name()
						+ ", returning: "
						+ channel);
		return (ChannelSftp) channel;
	}

	public ChannelSftp getChannel(Session session, int timeout) throws JSchException {
		channel = session.openChannel("sftp"); // 打开SFTP通道
		channel.connect(); // 建立SFTP通道的连接
		return (ChannelSftp) channel;
	}

	public Session getJSchSession(
			String ftpHost, String ftpUserName, String ftpPassword, int ftpPort, int timeout)
			throws JSchException {
		return getJSchSession(getSftpDetails(ftpHost, ftpUserName, ftpPassword, ftpPort), timeout);
	}

	/**
	 * 获取session
	 *
	 * @param sftpDetails Map<String, String>
	 * @param timeout     int
	 * @return Session
	 */
	// FIXME 不要使用Map做参数！使用多个明确的参数，或者提供一个构建参数BEAN：本类的一个public static class
	public static Session getJSchSession(SFTPDetails sftpDetails, int timeout) throws JSchException {

		JSch jsch = new JSch(); // 创建JSch对象
		Session session =
				jsch.getSession(
						sftpDetails.getUser_name(),
						sftpDetails.getHost(),
						sftpDetails.getPort()); // 根据用户名，主机ip，端口获取一个Session对象
		logger.debug("Session created.");
		String ftpPassword = sftpDetails.getPwd();
		if (ftpPassword != null) {
			session.setPassword(ftpPassword); // 设置密码
		}
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config); // 为Session对象设置properties
		session.setTimeout(timeout); // 设置timeout时间
		session.connect(); // 通过Session建立链接
		logger.debug("Session connected.");

		return session;
	}

	public static void execCommandByJSchNoRs(Session session, String command) throws Exception {

		logger.info("执行命令为 : ", command);
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.getInputStream();
		channelExec.setCommand(command);
		channelExec.setErrStream(System.err);
		channelExec.connect();
		Thread.sleep(1000);
		channelExec.disconnect();
	}

	public static String execCommandByJSch(Session session, String command)
			throws JSchException, IOException {
		command = FileNameUtils.normalize(command, true);
		logger.info("执行命令为 : " + command);
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		InputStream in = channelExec.getInputStream();
		channelExec.setCommand(command);
		channelExec.setErrStream(System.err);
		channelExec.connect();
		//TODO 这里可能会写出一个巨大返回值，造成内存溢出。
		String result = IOUtils.toString(in, ENCODING);
		channelExec.disconnect();

		return result;
	}

	/**
	 * 执行本地shell命令
	 *
	 * @param executeShell shell命令
	 */
	public static void executeLocalShell(String executeShell) {
		try {
			logger.info("执行命令为 ：" + executeShell);
			//executeShell linux命令  多个命令可用 " ; " 隔开
			Process ps = Runtime.getRuntime().exec((new String[]{"sh", "-l", "-c", executeShell}));
			ps.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append(System.lineSeparator());
			}
			if (!StringUtil.isEmpty(sb.toString())) {
				throw new AppSystemException("Linux命令" + executeShell + "执行失败，" + sb.toString());
			}
		} catch (Exception e) {
			throw new AppSystemException("Linux命令" + executeShell + "执行失败");
		}
	}

	public static String execCommandByJSchToReadLine(Session session, String command)
			throws JSchException, Exception {

		logger.info("执行命令为 : ", command);
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		InputStream inputStream = channelExec.getInputStream(); // 从远程端到达的所有数据都能从这个流中读取到
		OutputStream outputStream = channelExec.getOutputStream(); // 写入该流的所有数据都将发送到远程端。
		// 使用PrintWriter流的目的就是为了使用println这个方法
		// 好处就是不需要每次手动给字符串加\n
		PrintWriter printWriter = new PrintWriter(outputStream);
		printWriter.println(command);
		Thread.sleep(3000);
		printWriter.println("exit"); // 加上个就是为了，结束本次交互
		printWriter.flush();
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String msg = null;
		StringBuilder result = new StringBuilder();
		while ((msg = in.readLine()) != null) {
			result.append(msg);
		}
		in.close();
		channelExec.disconnect();
		return result.toString();
	}

	public void closeChannel() throws Exception {
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}

	/**
	 * @param ftpHost     String
	 * @param ftpUserName String
	 * @param ftpPassword String
	 * @param ftpPort     int
	 * @return Map<String, String>
	 */
	private static SFTPDetails getSftpDetails(
			String ftpHost, String ftpUserName, String ftpPassword, int ftpPort) {
		return new SFTPDetails(ftpHost, ftpUserName, ftpPassword, ftpPort);
	}
}
