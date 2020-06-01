package hrds.commons.utils.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import hrds.commons.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class SCPFileSender {

	public SFTPChannel getSFTPChannel() {

		return new SFTPChannel();
	}

	private static final Logger logger = LogManager.getLogger();

	// 系统路径的符号
	public static final String SEPARATOR = File.separator;
	// appinfo配置文件名称
	public static final String APPINFOCONfNAME = "appinfo.conf";
	// dbinfo配置文件名称
	public static final String DBINFOCONFNAME = "dbinfo.conf";
	// control配置文件名称
	public static final String CONTROLCONFNAME = "control.conf";
	public static final String TRIGGERCONFNAME = "trigger.conf";
	// 日志配置文件名称
	public static final String LOGINFONAME = "log4j2.xml";

	public static void etlScpToFrom(SFTPDetails sftpDetails) {

		try {
			String source_path = sftpDetails.getSource_path(); // 本地文件路径
			String localFileName = sftpDetails.getAgent_gz(); // 本地文件名称
//			String hadoopConf = sftpDetails.getHADOOP_CONF(); // 集群conf配置文件
			String targetDir = sftpDetails.getTarget＿dir(); // 目标路径
			String tmp_conf_path = sftpDetails.getTmp_conf_path(); // 存放临时文件路径
			if (!source_path.endsWith(SEPARATOR)) {
				source_path = source_path + SEPARATOR;
			}
			String src = source_path + localFileName;
			if (!targetDir.endsWith(SEPARATOR)) {
				targetDir = targetDir + SEPARATOR;
			}
			String dst = targetDir + localFileName;
			logger.info(dst + "==============" + src);

			Session shellSession = SFTPChannel.getJSchSession(sftpDetails, 0);

			String delDir = "rm -rf " + targetDir;
			SFTPChannel.execCommandByJSch(shellSession, delDir);
			logger.info("###########是否之前部署过，如果目录存在先删除###########");
			String mkdir = "mkdir -p " + targetDir;
			SFTPChannel.execCommandByJSch(shellSession, mkdir);
			logger.info("###########建立agent存放目录###########");

			SCPFileSender test = new SCPFileSender();
			SFTPChannel channel = test.getSFTPChannel();

			ChannelSftp chSftp = channel.getChannel(sftpDetails, 60000);
			File file = new File(src);
			long fileSize = file.length();
			// 传输当前agent压缩包到指定目录
			chSftp.put(src, dst, new FileProgressMonitor(fileSize), ChannelSftp.OVERWRITE);
			chSftp.quit();
			channel.closeChannel();
			logger.info("###########tar.gz上传完成###########");

			// 解压当前agent压缩包当指定目录
			String tarCommand = "tar -zxvf " + dst + " -C " + targetDir + "";
			SFTPChannel.execCommandByJSch(shellSession, tarCommand);
			logger.info("###########解压tar.gz的agent压缩包###########");
			// 删除当前目录下的agent压缩包
			String delCommand = "rm -rf " + dst + "";
			SFTPChannel.execCommandByJSch(shellSession, delCommand);
			logger.info("###########删除tar.gz的agent包###########");

			// fixme 集群配置文件暂时不知如何弄
//			String mkdirConf = "mkdir -p " + targetDir + "/control/hadoopconf/";
//			SFTPChannel.execCommandByJSch(shellSession, mkdirConf);
//			String mkdirConfTrigger = "mkdir -p " + targetDir + "/trigger/hadoopconf/";
//			SFTPChannel.execCommandByJSch(shellSession, mkdirConfTrigger);
//			logger.info("###########建立集群conf文件夹###########");

			SCPFileSender test_properties = new SCPFileSender();
			SFTPChannel channel_properties = test_properties.getSFTPChannel();
			ChannelSftp chSftp_properties = channel_properties.getChannel(sftpDetails, 60000);
			// 本地当前工程下的配置文件信息dbinfo.conf,上传到目标机器
			String localPath = System.getProperty("user.dir") + SEPARATOR + "resources" + SEPARATOR;
			String fdConfigPath = localPath + "fdconfig" + SEPARATOR;
			logger.info("=======localPath========" + localPath);
			// 判断文件control/trigger配置文件目录是否存在，不存在则创建
			String controlDirectory = targetDir + "control" + SEPARATOR + "resources" + SEPARATOR;
			String triggerDirectory = targetDir + "trigger" + SEPARATOR + "resources" + SEPARATOR;

			// 如果不存在则创建control,trigger配置文件目录
			makeDirectoryIfNotExist(chSftp_properties, controlDirectory + "fdconfig" + SEPARATOR);
			makeDirectoryIfNotExist(chSftp_properties, triggerDirectory + "fdconfig" + SEPARATOR);
			makeDirectoryIfNotExist(chSftp_properties, controlDirectory + "i18n" + SEPARATOR);
			makeDirectoryIfNotExist(chSftp_properties, triggerDirectory + "i18n" + SEPARATOR);

			String i18nPath = localPath + "i18n" + SEPARATOR;
			// 将日志文件sftp复制到agent部署的目标机器
			AgentDeploy.sftpFiles(i18nPath, chSftp_properties, controlDirectory);
			chSftp_properties.put(localPath + LOGINFONAME, controlDirectory + LOGINFONAME,
					ChannelSftp.OVERWRITE);
			AgentDeploy.sftpFiles(i18nPath, chSftp_properties, triggerDirectory);
			chSftp_properties.put(localPath + LOGINFONAME, triggerDirectory + LOGINFONAME,
					ChannelSftp.OVERWRITE);

			// 本地当前工程下的配置文件信息dbinfo.conf,上传到目标机器
			sftpConfFile(chSftp_properties, fdConfigPath, controlDirectory, triggerDirectory, DBINFOCONFNAME);
			logger.info("###########替换 dbinfo.conf文件###########");

			// 本地当前工程下的配置文件信息appinfo.conf,上传到目标机器
			sftpConfFile(chSftp_properties, fdConfigPath, controlDirectory, triggerDirectory, APPINFOCONfNAME);
			logger.info("###########替换 appinfo.conf文件###########");

			// 将本地临时配置文件control.conf,sftp复制到agent部署的目标机器
			File controlInfo = new File(tmp_conf_path + CONTROLCONFNAME);
			long fileSizeControlInfo = controlInfo.length();
			chSftp_properties.put(tmp_conf_path + CONTROLCONFNAME, controlDirectory + "fdconfig"
							+ SEPARATOR + CONTROLCONFNAME,
					new FileProgressMonitor(fileSizeControlInfo), ChannelSftp.OVERWRITE);
			logger.info("###########将临时配置文件control.conf,sftp复制到agent部署的目标机器###########");
			// 将本地临时配置文件trigger.conf,sftp复制到agent部署的目标机器
			File triggerInfo = new File(tmp_conf_path + TRIGGERCONFNAME);
			long fileSizeTriggerInfo = triggerInfo.length();
			chSftp_properties.put(tmp_conf_path + TRIGGERCONFNAME, triggerDirectory + "fdconfig"
							+ SEPARATOR + TRIGGERCONFNAME,
					new FileProgressMonitor(fileSizeTriggerInfo), ChannelSftp.OVERWRITE);
			logger.info("###########将本地临时配置文件trigger.conf,sftp复制到agent部署的目标机器###########");
			// hadoop配置文件
//			File fileHadoopConf = new File(hadoopConf);
//			File[] list = fileHadoopConf.listFiles();
//			if (list == null || list.length == 0) {
//				throw new BusinessException("集群配置文件不能为空");
//			}
//			for (int i = 0; i < list.length; i++) {
//				long fileSizeConf = list[i].length();
//				chSftp_properties.put(list[i].toString(), targetDir + "/control/conf/",
//						new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
//				chSftp_properties.put(list[i].toString(), targetDir + "/trigger/conf/",
//						new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
//			}
//			logger.info("###########替换集群配置conf文件===");
			chSftp_properties.quit();
			channel_properties.closeChannel();
			shellSession.disconnect();
		} catch (JSchException e) {
			logger.error("连接失败，请确认用户名密码正确", e);
			throw new BusinessException("连接失败，请确认用户名密码正确" + e.getMessage());
		} catch (IOException e) {
			logger.error("网络异常，请确认网络正常", e);
			throw new BusinessException("网络异常，请确认网络正常" + e.getMessage());
		} catch (SftpException e) {
			logger.error("数据传输失败，请检查数据目录是否有权限，请联系管理员", e);
			throw new BusinessException("数据传输失败，请检查数据目录是否有权限，请联系管理员" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("部署失败，请重新部署" + e);
		}
	}

	private static void sftpConfFile(ChannelSftp chSftp_properties, String fdConfigPath, String controlDirectory,
	                                 String triggerDirectory, String appinfocoNfNAME) throws SftpException {
		long fileSizeAppInfo = new File(fdConfigPath + appinfocoNfNAME).length();
		chSftp_properties.put(fdConfigPath + appinfocoNfNAME, controlDirectory + "fdconfig"
						+ SEPARATOR + appinfocoNfNAME,
				new FileProgressMonitor(fileSizeAppInfo), ChannelSftp.OVERWRITE);
		chSftp_properties.put(fdConfigPath + appinfocoNfNAME, triggerDirectory + "fdconfig"
						+ SEPARATOR + appinfocoNfNAME,
				new FileProgressMonitor(fileSizeAppInfo), ChannelSftp.OVERWRITE);
	}

	/**
	 * 判断目录是否存在，不存在则创建
	 *
	 * @param sftp      sftp传输文件对象
	 * @param directory 文件目录
	 */
	public static void makeDirectoryIfNotExist(ChannelSftp sftp, String directory) {
		// 判断目录文件夹是否存在，不存在即创建
		try {
			// 目录不存在，则创建文件夹
			String[] dirs = directory.split(SEPARATOR);
			String tempPath = "";
			for (String dir : dirs) {
				if (null == dir || "".equals(dir))
					continue;
				tempPath += SEPARATOR + dir;
				try {
					sftp.cd(tempPath);
				} catch (SftpException ex) {
					try {
						logger.error("创建目录[" + tempPath + "]");
						sftp.mkdir(tempPath);
						sftp.cd(tempPath);
					} catch (SftpException e1) {
						throw new BusinessException("创建目录失败" + e1.getMessage());
					}
				} catch (Exception e1) {
					throw new BusinessException("创建目录失败" + e1.getMessage());

				}
			}
		} catch (Exception e1) {
			throw new BusinessException("创建目录失败" + e1.getMessage());
		}
	}
}
