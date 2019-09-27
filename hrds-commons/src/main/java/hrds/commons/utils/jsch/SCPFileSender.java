package hrds.commons.utils.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SCPFileSender {

	public SFTPChannel getSFTPChannel() {

		return new SFTPChannel();
	}

	public static String HOST = "HOST";
	public static String USERNAME = "USERNAME";
	public static String PASSWORD = "PASSWORD";
	public static String PORT = "PORT";
	public static String SOURCEPATH = "SOURCEPATH";
	public static String AGENT_GZ = "AGENT_GZ";
	public static String _MSGCONF = "_MSGCONF";
	public static String _DBINFO = "_DBINFO";
	public static String _LOG4J = "_LOG4J";
	public static String HADOOPCONF = "HADOOPCONF";
	public static String TARGETDIR = "TARGETDIR";
	public static String ISSTART = "ISSTART";
	public static String LIBLIST = "LIBLIST";
	public static String OLDTARGETDIR = "OLDTARGETDIR";
	public static String OLDLOG4JLOGDIR = "OLDLOG4JLOGDIR";
	private static final Logger logger = LogManager.getLogger();

	public static String scpToFrom(Map<String, String> sftpDetails, String agentPY_post) {

		try {
			String sourcepath = sftpDetails.get(SOURCEPATH);//本地文件路径
			String agent_gz = sftpDetails.get(AGENT_GZ);//本地文件名称
			String _msgconf = sftpDetails.get(_MSGCONF);//msgconf文件
			String _dbinfo = sftpDetails.get(_DBINFO);//dbinfo文件
			String _log4j = sftpDetails.get(_LOG4J);//log4j文件

			String hadoopconf = sftpDetails.get(HADOOPCONF);//集群conf配置文件
			String targetdir = sftpDetails.get(TARGETDIR);//目标路径
			String isstart = sftpDetails.get(ISSTART);//是否启动

			String liblist = sftpDetails.get(LIBLIST);//tomcat下的lib

			String src = sourcepath + agent_gz;
			String sourcemsgconf = sourcepath + _msgconf;
			String sourcemsgdbinfo = sourcepath + _dbinfo;
			String sourcemsgdblog4j = sourcepath + _log4j;
			String dst = targetdir + agent_gz;

			Session shellSession = SFTPChannel.getJSchSession(sftpDetails, 0);

			String oldtargetdir = sftpDetails.get(OLDTARGETDIR);//原来的地址
			String oldlog4jlogdir = sftpDetails.get(OLDLOG4JLOGDIR);//原来的日志文件
			String old_dir = oldtargetdir + File.separator + agentPY_post;
			if( !StringUtil.isEmpty(old_dir) ) {
				String killPid = "kill -9 $(ps -ef |grep HYRENAgentReceive|grep " + old_dir + "|grep -v grep| awk '{print $2}'| xargs -n 1)";
				logger.info("KILL命令==========>" + killPid);
				SFTPChannel.execCommandByJSch(shellSession, killPid);
				String deldir = "rm -rf " + oldtargetdir + File.separator + agentPY_post;
				SFTPChannel.execCommandByJSch(shellSession, deldir);
				logger.info("###########kill部署旧的进程，旧的目录地址===");
			}
			if( !StringUtil.isEmpty(oldlog4jlogdir) ) {new File("", "main");
				String filePath = FileUtils.getFile(oldlog4jlogdir).getAbsolutePath();//文件的完整路径
				String deldir = "rm -rf " + filePath;
				SFTPChannel.execCommandByJSch(shellSession, deldir);
				logger.info("###########删除旧的日志目录===");
			}

			String agentDir = agentPY_post + File.separator + ".bin";
			String agentPath = targetdir + File.separator + agentDir + File.separator;

			String killPid = "kill -9 $(ps -ef |grep HYRENAgentReceive|grep " + agentPath + "|grep -v grep| awk '{print $2}'| xargs -n 1)";
			SFTPChannel.execCommandByJSch(shellSession, killPid);
			logger.info("###########检查是否启动，然后kill完成===");

			String deldir = "rm -rf " + targetdir + File.separator + agentPY_post + File.separator;
			SFTPChannel.execCommandByJSch(shellSession, deldir);
			logger.info("###########是否之前部署过，如果目录存在先删除===");
			String mkdir = "mkdir -p " + agentPath + "";
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

			String tarxvf = "tar -zxvf " + dst + " -C " + agentPath + "";
			SFTPChannel.execCommandByJSch(shellSession, tarxvf);
			logger.info("###########解压tar.gz的agent压缩包===");
			String delgz = "rm -rf " + dst + "";
			SFTPChannel.execCommandByJSch(shellSession, delgz);
			logger.info("###########删除tar.gz的agent包===");

			String mkdirconf = "mkdir -p " + agentPath + "/conf/";
			SFTPChannel.execCommandByJSch(shellSession, mkdirconf);
			logger.info("###########建立集群conf文件夹===");
			String mkdirlib = "mkdir -p " + agentPath + "/lib/";
			SFTPChannel.execCommandByJSch(shellSession, mkdirlib);
			logger.info("###########建立lib文件夹===");

			SCPFileSender test_properties = new SCPFileSender();
			SFTPChannel channel_properties = test_properties.getSFTPChannel();
			ChannelSftp chSftp_properties = channel_properties.getChannel(sftpDetails, 60000);
			File fileMsgConf = new File(sourcemsgconf);
			long fileSizeMsgConf = fileMsgConf.length();
			chSftp_properties.put(sourcemsgconf, agentPath + "msgConf.xml", new FileProgressMonitor(fileSizeMsgConf), ChannelSftp.OVERWRITE);
			logger.info("###########替换msgconf文件===");

			File fileDbInfo = new File(sourcemsgdbinfo);
			long fileSizeDbInfo = fileDbInfo.length();
			chSftp_properties.put(sourcemsgdbinfo, agentPath + "/config/dbinfo.properties", new FileProgressMonitor(fileSizeDbInfo),
							ChannelSftp.OVERWRITE);
			logger.info("###########替换dbinfo文件===");

			File fileLog4J = new File(sourcemsgdblog4j);
			long fileSizeLog4J = fileLog4J.length();
			chSftp_properties.put(sourcemsgdblog4j, agentPath + "/config/log4j.properties", new FileProgressMonitor(fileSizeLog4J),
							ChannelSftp.OVERWRITE);
			logger.info("###########替换log4j文件===");
			//这里如果是DB版本的,可能tomcat的bin目录下conf目录不存在,所以跳过
			if( "HD".equals(PropertyParaValue.getString("ver_type","HD")) ) {//TODO
				File fileHadoopConf = new File(hadoopconf);
				File[] list = fileHadoopConf.listFiles();
				if( list != null ) {
					for(int i = 0; i < list.length; i++) {
						long fileSizeConf = list[i].length();
						chSftp_properties.put(list[i].toString(), agentPath + "/conf/", new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
					}
				}
				logger.info("###########替换集群配置conf文件===");
			}

			File fileLibList = new File(liblist);
			File[] libList = fileLibList.listFiles();
			if( libList != null ) {
				for(int i = 0; i < libList.length; i++) {
					long fileSizeLib = libList[i].length();
					chSftp_properties.put(libList[i].toString(), agentPath + "/lib/", new FileProgressMonitor(fileSizeLib), ChannelSftp.OVERWRITE);
				}
			}
			logger.info("###########部署tomcat-shared下的所有lib文件===");

			chSftp_properties.quit();
			channel_properties.closeChannel();

			if( "0".equals(isstart) ) {
				//加入linux环境变量，防止远程调用java命令无效
				String startAgent = "source /etc/profile;source ~/.bash_profile;source ~/.bashrc;cd " + agentPath + ";sh startAgent.sh";
				SFTPChannel.execCommandByJSchNoRs(shellSession, startAgent);
				logger.info("###########启动agent成功===");
			}
			shellSession.disconnect();
			return "0";
		}
		catch(JSchException e) {
			logger.error("连接失败，请确认用户名密码正确");
			return "99";
		}
		catch(IOException e) {
			logger.error("网络异常，请确认网络正常");
			return "98";
		}
		catch(SftpException e) {
			logger.error("数据传输失败，请联系管理员");
			return "97";
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new BusinessException("部署失败，请重新部署");
		}
	}

	public static String ETLscpToFrom(Map<String, String> sftpDetails) {

		try {
			String sourcepath = sftpDetails.get(SCPFileSender.SOURCEPATH);//本地文件路径
			String agent_gz = sftpDetails.get(SCPFileSender.AGENT_GZ);//本地文件名称
			String dbproperties = sftpDetails.get(SCPFileSender._MSGCONF);//db文件
			String redisproperties = sftpDetails.get(SCPFileSender._DBINFO);//redis文件=

			String hadoopConf = sftpDetails.get(SCPFileSender.HADOOPCONF);//集群conf配置文件
			String targetdir = sftpDetails.get(SCPFileSender.TARGETDIR);//目标路径

			String src = sourcepath + agent_gz;
			String dst = targetdir + agent_gz;

			Session shellSession = SFTPChannel.getJSchSession(sftpDetails, 0);

			String deldir = "rm -rf " + targetdir;
			SFTPChannel.execCommandByJSch(shellSession, deldir);
			logger.info("###########是否之前部署过，如果目录存在先删除===");
			String mkdir = "mkdir -p " + targetdir;
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

			String tarxvf = "tar -zxvf " + dst + " -C " + targetdir + "";
			SFTPChannel.execCommandByJSch(shellSession, tarxvf);
			logger.info("###########解压tar.gz的agent压缩包===");
			String delgz = "rm -rf " + dst + "";
			SFTPChannel.execCommandByJSch(shellSession, delgz);
			logger.info("###########删除tar.gz的agent包===");

			String mkdirconf = "mkdir -p " + targetdir + "/control/conf/";
			SFTPChannel.execCommandByJSch(shellSession, mkdirconf);
			String mkdirconftrigger = "mkdir -p " + targetdir + "/trigger/conf/";
			SFTPChannel.execCommandByJSch(shellSession, mkdirconftrigger);
			logger.info("###########建立集群conf文件夹===");

			SCPFileSender test_properties = new SCPFileSender();
			SFTPChannel channel_properties = test_properties.getSFTPChannel();
			ChannelSftp chSftp_properties = channel_properties.getChannel(sftpDetails, 60000);
			File fileMsgConf = new File(sourcepath + "db.properties");
			long fileSizeMsgConf = fileMsgConf.length();
			chSftp_properties.put(sourcepath + dbproperties, targetdir + "/control/property/db.properties", new FileProgressMonitor(fileSizeMsgConf),
							ChannelSftp.OVERWRITE);
			chSftp_properties.put(sourcepath + dbproperties, targetdir + "/trigger/property/db.properties", new FileProgressMonitor(fileSizeMsgConf),
							ChannelSftp.OVERWRITE);
			logger.info("###########替换msgconf文件===");

			File fileDbInfo = new File(sourcepath + "redis.properties");
			long fileSizeDbInfo = fileDbInfo.length();
			chSftp_properties.put(sourcepath + redisproperties, targetdir + "/control/property/redis.properties",
							new FileProgressMonitor(fileSizeDbInfo), ChannelSftp.OVERWRITE);
			chSftp_properties.put(sourcepath + redisproperties, targetdir + "/trigger/property/redis.properties",
							new FileProgressMonitor(fileSizeDbInfo), ChannelSftp.OVERWRITE);
			logger.info("###########替换dbinfo文件===");

			File fileHadoopconf = new File(hadoopConf);
			File[] list = fileHadoopconf.listFiles();
			for(int i = 0; i < list.length; i++) {
				long fileSizeConf = list[i].length();
				chSftp_properties.put(list[i].toString(), targetdir + "/control/conf/", new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
				chSftp_properties.put(list[i].toString(), targetdir + "/trigger/conf/", new FileProgressMonitor(fileSizeConf), ChannelSftp.OVERWRITE);
			}
			logger.info("###########替换集群配置conf文件===");

			chSftp_properties.quit();
			channel_properties.closeChannel();
			shellSession.disconnect();
			return "0";
		}
		catch(JSchException e) {
			logger.error("连接失败，请确认用户名密码正确", e);
			return "99";
		}
		catch(IOException e) {
			logger.error("网络异常，请确认网络正常", e);
			return "98";
		}
		catch(SftpException e) {
			logger.error("数据传输失败，请联系管理员", e);
			return "97";
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new BusinessException("部署失败，请重新部署" + e);
		}
	}

	public static void main(String[] args) throws Exception {

		Map<String, String> sftpDetails = new HashMap<>();
		sftpDetails.put(HOST, args[0]);
		sftpDetails.put(USERNAME, args[1]);
		sftpDetails.put(PASSWORD, args[2]);
		sftpDetails.put(PORT, args[3]);

		sftpDetails.put(SOURCEPATH, args[4]);//本地文件路径
		sftpDetails.put(AGENT_GZ, args[5]);//本地文件名称
		sftpDetails.put(_MSGCONF, args[6]);//msgconf文件
		sftpDetails.put(_DBINFO, args[7]);//dbinfo文件
		sftpDetails.put(_LOG4J, args[8]);//log4j文件

		sftpDetails.put(HADOOPCONF, args[9]);//集群conf配置文件
		sftpDetails.put(TARGETDIR, args[10]);//目标路径
		sftpDetails.put(ISSTART, args[11]);//是否启动
		sftpDetails.put(LIBLIST, args[12]);//lib

		scpToFrom(sftpDetails, "sdfdsf");
	}
}
