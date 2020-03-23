package hrds.commons.utils.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import hrds.commons.exception.BusinessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class SCPFileSender {

	public SFTPChannel getSFTPChannel() {

		return new SFTPChannel();
	}

	private static final Log logger = LogFactory.getLog(SCPFileSender.class);

	public static void etlScpToFrom(SFTPDetails sftpDetails) {

		try {
			String source_path = sftpDetails.getSource_path(); // 本地文件路径
			String localFileName = sftpDetails.getAgent_gz(); // 本地文件名称
			String dbInfo = sftpDetails.getDb_info(); // db文件
			String redis_conf = sftpDetails.getRedis_info(); // redis文件=
			String hadoopConf = sftpDetails.getHADOOP_CONF(); // 集群conf配置文件
			String targetDir = sftpDetails.getTarget＿dir(); // 目标路径
			String tmp_conf_path = sftpDetails.getTmp_conf_path(); // 存放临时文件路径

			String src = source_path + localFileName;
			String dst = targetDir + localFileName;

			Session shellSession = SFTPChannel.getJSchSession(sftpDetails, 0);

			String delDir = "rm -rf " + targetDir;
			SFTPChannel.execCommandByJSch(shellSession, delDir);
			logger.info("###########是否之前部署过，如果目录存在先删除===");
			String mkdir = "mkdir -p " + targetDir;
			SFTPChannel.execCommandByJSch(shellSession, mkdir);
			logger.info("###########建立agent存放目录===");

			SCPFileSender test = new SCPFileSender();
			SFTPChannel channel = test.getSFTPChannel();

			ChannelSftp chSftp = channel.getChannel(sftpDetails, 60000);
			File file = new File(src);
			long fileSize = file.length();
			chSftp.put(src, dst, new FileProgressMonitor(fileSize), ChannelSftp.OVERWRITE); // 代码段2
			chSftp.quit();
			channel.closeChannel();
			logger.info("###########tar.gz上传完成===");

			String tarCommand = "tar -zxvf " + dst + " -C " + targetDir + "";
			SFTPChannel.execCommandByJSch(shellSession, tarCommand);
			logger.info("###########解压tar.gz的agent压缩包===");
			String delCommand = "rm -rf " + dst + "";
			SFTPChannel.execCommandByJSch(shellSession, delCommand);
			logger.info("###########删除tar.gz的agent包===");

			// fixme 集群配置文件暂时不知如何弄
			String mkdirConf = "mkdir -p " + targetDir + "/control/conf/";
			SFTPChannel.execCommandByJSch(shellSession, mkdirConf);
			String mkdirConfTrigger = "mkdir -p " + targetDir + "/trigger/conf/";
			SFTPChannel.execCommandByJSch(shellSession, mkdirConfTrigger);
			logger.info("###########建立集群conf文件夹===");

			SCPFileSender test_properties = new SCPFileSender();
			SFTPChannel channel_properties = test_properties.getSFTPChannel();
			ChannelSftp chSftp_properties = channel_properties.getChannel(sftpDetails, 60000);
			File fileDbInfo = new File(source_path + "dbinfo.conf");
			long fileSizeDbInfo = fileDbInfo.length();
			chSftp_properties.put(source_path + dbInfo, targetDir + "/control/resources/dbinfo.conf",
					new FileProgressMonitor(fileSizeDbInfo), ChannelSftp.OVERWRITE);
			logger.info("###########替换control dbinfo.conf文件###########");
			chSftp_properties.put(source_path + dbInfo, targetDir + "/trigger/resources/dbinfo.conf",
					new FileProgressMonitor(fileSizeDbInfo), ChannelSftp.OVERWRITE);
			logger.info("###########替换trigger dbinfo.conf文件###########");
			// 将本地写的临时配置文件,sftp复制到agent部署的目标机器
			AgentDeploy.sftpFiles(tmp_conf_path, chSftp, targetDir + File.separator + "resources");
			File fileRedisInfo = new File(source_path + "redis.conf");
			long fileSizeRedisInfo = fileRedisInfo.length();
			chSftp_properties.put(source_path + redis_conf, targetDir + "/control/resources/redis.conf",
					new FileProgressMonitor(fileSizeRedisInfo),
					ChannelSftp.OVERWRITE);
			chSftp_properties.put(source_path + redis_conf,
					targetDir + "/trigger/resources/redis.conf", new FileProgressMonitor(fileSizeRedisInfo),
					ChannelSftp.OVERWRITE);
			logger.info("###########替换dbinfo文件===");

			// hadoop配置文件
			File fileHadoopConf = new File(hadoopConf);
			File[] list = fileHadoopConf.listFiles();
			if (list == null || list.length == 0) {
				throw new BusinessException("集群配置文件不能为空");
			}
			for (int i = 0; i < list.length; i++) {
				long fileSizeConf = list[i].length();
				chSftp_properties.put(list[i].toString(), targetDir + "/control/conf/",
						new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
				chSftp_properties.put(list[i].toString(), targetDir + "/trigger/conf/",
						new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
			}
			logger.info("###########替换集群配置conf文件===");
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
			logger.error("数据传输失败，请联系管理员", e);
			throw new BusinessException("数据传输失败，请联系管理员" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("部署失败，请重新部署" + e);
		}
	}
}
