package hrds.commons.utils.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaValue;
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
	/**
	 * 启动control的脚本名称
	 */
	public static final String CONTROLSHELL = "startEngineBatchControl.sh";
	/**
	 * 启动trigger的脚本名称
	 */
	public static final String TRIGGERSHELL = "startEngineBatchTrigger.sh";

	public static void etlScpToFrom(SFTPDetails sftpDetails) {

		try {
//			String hadoopConf = sftpDetails.getHADOOP_CONF(); // 集群conf配置文件
			String targetDir = sftpDetails.getTarget＿dir(); // 目标路径
			String tmp_conf_path = sftpDetails.getTmp_conf_path(); // 存放临时文件路径

			// 部署前先删除原来的目录
			Session shellSession = SFTPChannel.getJSchSession(sftpDetails, 0);
			SFTPChannel.execCommandByJSch(shellSession, "rm -rf " + targetDir);
			logger.info("###########是否之前部署过，如果目录存在先删除###########");
			SFTPChannel.execCommandByJSch(shellSession, "mkdir -p " + targetDir);
			logger.info("###########建立agent存放目录###########");
			// 创建etl工程远程目录
			mkdirToEtlTarget(shellSession, targetDir);

			SCPFileSender test = new SCPFileSender();
			SFTPChannel channel = test.getSFTPChannel();
			ChannelSftp chSftp = channel.getChannel(sftpDetails, 60000);
			// 开始传输control程序的jar包以及启动脚本
			logger.info("开始传输control程序的jar包以及启动脚本。。。。。。。");
			String controlTarget = targetDir + "control" + SEPARATOR;
			File controlFile = new File(PropertyParaValue.getString("controlPath", ""));
			if (!controlFile.exists()) {
				throw new BusinessException("etl工程control的jar包(" + controlFile.getAbsolutePath() + ")未找到!!!");
			}
			chSftp.put(
					controlFile.getAbsolutePath(),
					controlTarget,
					new FileProgressMonitor(controlFile.length()),
					ChannelSftp.OVERWRITE);
			String controlShell = controlFile.getParent() + SEPARATOR + CONTROLSHELL;
			if (!new File(controlShell).exists()) {
				throw new BusinessException("etl工程的脚本(" + CONTROLSHELL + ")未找到!!!");
			}
			// 传输启动control的脚本
			chSftp.put(
					controlShell,
					controlTarget,
					new FileProgressMonitor(new File(controlShell).length()),
					ChannelSftp.OVERWRITE);
			logger.info("传输control程序的jar包以及启动脚本结束。。。。。。。");
			// 开始传输trigger程序的jar包以及启动脚本
			logger.info("开始传输trigger程序的jar包以及启动脚本。。。。。。。");
			String triggerTarget = targetDir + "trigger" + SEPARATOR;
			File triggerFile = new File(PropertyParaValue.getString("triggerPath", ""));
			if (!triggerFile.exists()) {
				throw new BusinessException("etl工程trigger的jar包(" + triggerFile.getAbsolutePath() + ")未找到!!!");
			}
			chSftp.put(
					triggerFile.getAbsolutePath(),
					triggerTarget,
					new FileProgressMonitor(triggerFile.length()),
					ChannelSftp.OVERWRITE);
			String triggerShell = triggerFile.getParent() + SEPARATOR + TRIGGERSHELL;
			if (!new File(triggerShell).exists()) {
				throw new BusinessException("etl工程的脚本(" + TRIGGERSHELL + ")未找到!!!");
			}
			// 传输启动trigger的脚本
			chSftp.put(
					triggerShell,
					triggerTarget,
					new FileProgressMonitor(new File(triggerShell).length()),
					ChannelSftp.OVERWRITE);
			logger.info("传输trigger程序的jar包以及启动脚本结束。。。。。。。");
			// 本地当前工程下的配置文件信息dbinfo.conf,上传到目标机器
			String localPath = System.getProperty("user.dir") + SEPARATOR + "resources" + SEPARATOR;
			String fdConfigPath = localPath + "fdconfig" + SEPARATOR;
			logger.info("=======localPath========" + localPath);
			// control/trigger配置文件远程目录
			String controlResourceDir = controlTarget + "resources" + SEPARATOR;
			String triggerResourceDir = triggerTarget + "resources" + SEPARATOR;
			// 国际化配置文件远程目录
			String i18nPath = localPath + "i18n" + SEPARATOR;
			// 将国际化配置文件sftp复制到etl工程部署的目标机器
			AgentDeploy.sftpFiles(i18nPath, chSftp, controlResourceDir);
			// 将日志文件sftp复制到etl工程部署的目标机器
			chSftp.put(localPath + LOGINFONAME, controlResourceDir + LOGINFONAME,
					ChannelSftp.OVERWRITE);
			AgentDeploy.sftpFiles(i18nPath, chSftp, triggerResourceDir);
			chSftp.put(localPath + LOGINFONAME, triggerResourceDir + LOGINFONAME,
					ChannelSftp.OVERWRITE);
			// 本地当前工程下的配置文件信息dbinfo.conf,上传到目标机器
			sftpConfFile(chSftp, fdConfigPath, controlResourceDir, triggerResourceDir, DBINFOCONFNAME);
			logger.info("###########替换 dbinfo.conf文件###########");

			// 本地当前工程下的配置文件信息appinfo.conf,上传到目标机器
			sftpConfFile(chSftp, fdConfigPath, controlResourceDir, triggerResourceDir, APPINFOCONfNAME);
			logger.info("###########替换 appinfo.conf文件###########");

			// 将本地临时配置文件control.conf,sftp复制到agent部署的目标机器
			File controlInfo = new File(tmp_conf_path + CONTROLCONFNAME);
			long fileSizeControlInfo = controlInfo.length();
			chSftp.put(tmp_conf_path + CONTROLCONFNAME, controlResourceDir + "fdconfig"
							+ SEPARATOR + CONTROLCONFNAME,
					new FileProgressMonitor(fileSizeControlInfo), ChannelSftp.OVERWRITE);
			logger.info("###########将临时配置文件control.conf,sftp复制到agent部署的目标机器###########");
			// 将本地临时配置文件trigger.conf,sftp复制到agent部署的目标机器
			File triggerInfo = new File(tmp_conf_path + TRIGGERCONFNAME);
			long fileSizeTriggerInfo = triggerInfo.length();
			chSftp.put(tmp_conf_path + TRIGGERCONFNAME, triggerResourceDir + "fdconfig"
							+ SEPARATOR + TRIGGERCONFNAME,
					new FileProgressMonitor(fileSizeTriggerInfo), ChannelSftp.OVERWRITE);
			logger.info("###########将本地临时配置文件trigger.conf,sftp复制到agent部署的目标机器###########");

			// 将需要的jar包 SFTP 到etl工程部署目录下
			AgentDeploy.sftpFiles(
					new File(System.getProperty("user.dir")).getParent() + SEPARATOR + "lib",
					chSftp, targetDir);
			// fixme 集群配置文件暂时不知如何弄
//			String mkdirConf = "mkdir -p " + targetDir + "/control/hadoopconf/";
//			SFTPChannel.execCommandByJSch(shellSession, mkdirConf);
//			String mkdirConfTrigger = "mkdir -p " + targetDir + "/trigger/hadoopconf/";
//			SFTPChannel.execCommandByJSch(shellSession, mkdirConfTrigger);
//			logger.info("###########建立集群conf文件夹###########");
			// hadoop配置文件
//			File fileHadoopConf = new File(hadoopConf);
//			File[] list = fileHadoopConf.listFiles();
//			if (list == null || list.length == 0) {
//				throw new BusinessException("集群配置文件不能为空");
//			}
//			for (int i = 0; i < list.length; i++) {
//				long fileSizeConf = list[i].length();
//				chSftp.put(list[i].toString(), targetDir + "/control/conf/",
//						new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
//				chSftp.put(list[i].toString(), targetDir + "/trigger/conf/",
//						new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
//			}
//			logger.info("###########替换集群配置conf文件===");
			chSftp.quit();
			channel.closeChannel();
			shellSession.disconnect();
		} catch (
				JSchException e) {
			logger.error("连接失败，请确认用户名密码正确", e);
			throw new BusinessException("连接失败，请确认用户名密码正确" + e.getMessage());
		} catch (
				IOException e) {
			logger.error("网络异常，请确认网络正常", e);
			throw new BusinessException("网络异常，请确认网络正常" + e.getMessage());
		} catch (
				SftpException e) {
			logger.error("数据传输失败，请检查数据目录是否有权限，请联系管理员", e);
			throw new BusinessException("数据传输失败，请检查数据目录是否有权限，请联系管理员" + e.getMessage());
		} catch (
				Exception e) {
			e.printStackTrace();
			throw new BusinessException("部署失败，请重新部署" + e);
		}

	}

	private static void sftpConfFile(ChannelSftp chSftp, String fdConfigPath, String controlResourceDir,
	                                 String triggerResourceDir, String confName) throws SftpException {
		long fileSizeAppInfo = new File(fdConfigPath + confName).length();
		logger.info("fdConfigPath目录" + fdConfigPath);
		logger.info("control/resource目录" + controlResourceDir);
		logger.info("trigger/resource目录" + triggerResourceDir);
		logger.info("开始传输" + confName + "到etl工程部署目录");
		chSftp.put(fdConfigPath + confName, controlResourceDir + "fdconfig"
						+ SEPARATOR + confName,
				new FileProgressMonitor(fileSizeAppInfo), ChannelSftp.OVERWRITE);
		chSftp.put(fdConfigPath + confName, triggerResourceDir + "fdconfig"
						+ SEPARATOR + confName,
				new FileProgressMonitor(fileSizeAppInfo), ChannelSftp.OVERWRITE);
	}

//	/**
//	 * 判断目录是否存在，不存在则创建
//	 *
//	 * @param sftp      sftp传输文件对象
//	 * @param directory 文件目录
//	 */
//	public static void makeDirectoryIfNotExist(ChannelSftp sftp, String directory) {
//		// 判断目录文件夹是否存在，不存在即创建
//		try {
//			// 目录不存在，则创建文件夹
//			String[] dirs = directory.split(SEPARATOR);
//			String tempPath = "";
//			for (String dir : dirs) {
//				if (null == dir || "".equals(dir))
//					continue;
//				tempPath += SEPARATOR + dir;
//				try {
//					sftp.cd(tempPath);
//				} catch (SftpException ex) {
//					try {
//						logger.error("创建目录[" + tempPath + "]");
//						sftp.mkdir(tempPath);
//						sftp.cd(tempPath);
//					} catch (SftpException e1) {
//						throw new BusinessException("创建目录失败" + e1.getMessage());
//					}
//				} catch (Exception e1) {
//					throw new BusinessException("创建目录失败" + e1.getMessage());
//
//				}
//			}
//		} catch (Exception e1) {
//			throw new BusinessException("创建目录失败" + e1.getMessage());
//		}
//	}

	private static void mkdirToEtlTarget(Session shellSession, String targetDir)
			throws IOException, JSchException {
		/**
		 * lib : 需要的依赖jar包目录 resources : 配置文件根目录
		 * fdconfig : 配置信息文件 i18n : 国际化配置文件
		 */
		String[] targetDir_machine = {"control", "trigger", "lib", "resources", "fdconfig", "i18n"};
		// 建立lib 目录
		logger.info("创建远程目录 lib: " + targetDir + SEPARATOR + targetDir_machine[2]);
		SFTPChannel.execCommandByJSch(
				shellSession, "mkdir -p " + targetDir + SEPARATOR + targetDir_machine[2]);

		// 建立control/resource/fdconfig 目录
		String controlDir = targetDir + SEPARATOR + targetDir_machine[0] + SEPARATOR + targetDir_machine[3]
				+ SEPARATOR + targetDir_machine[4];
		logger.info("创建远程目录control/resource/fdconfig目录: " + controlDir);
		SFTPChannel.execCommandByJSch(shellSession, "mkdir -p " + controlDir);

		// 建立trigger/resource/fdconfig 目录
		String triggerDir = targetDir + SEPARATOR + targetDir_machine[1] + SEPARATOR + targetDir_machine[3]
				+ SEPARATOR + targetDir_machine[4];
		logger.info("创建远程目录trigger/resource/fdconfig目录: " + triggerDir);
		SFTPChannel.execCommandByJSch(shellSession, "mkdir -p " + triggerDir);

		// 建立control/resource/i18n 目录
		String i18nControlDir =
				targetDir + SEPARATOR + targetDir_machine[0] + SEPARATOR + targetDir_machine[3]
						+ SEPARATOR + targetDir_machine[5];
		logger.info("创建远程目录control/resource/i18n:" + triggerDir);
		SFTPChannel.execCommandByJSch(shellSession, "mkdir -p " + i18nControlDir);

		// 建立trigger/resource/i18n 目录
		String i18nTriggerDir =
				targetDir + SEPARATOR + targetDir_machine[1] + SEPARATOR + targetDir_machine[3]
						+ SEPARATOR + targetDir_machine[5];
		logger.info("创建远程目录trigger/resource/i18n:" + triggerDir);
		SFTPChannel.execCommandByJSch(shellSession, "mkdir -p " + i18nTriggerDir);
	}
}
