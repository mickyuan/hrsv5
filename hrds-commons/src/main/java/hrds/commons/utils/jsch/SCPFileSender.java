package hrds.commons.utils.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import hrds.commons.exception.BusinessException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCPFileSender {

  public SFTPChannel getSFTPChannel() {

    return new SFTPChannel();
  }

  private static final Log logger = LogFactory.getLog(SCPFileSender.class);

  public static String etlScpToFrom(SFTPDetails sftpDetails) {

    try {
      String sourcepath = sftpDetails.getSource_path(); // 本地文件路径
      String agent_gz = sftpDetails.getAgent_gz(); // 本地文件名称
      String dbproperties = sftpDetails.getDb_info(); // db文件
      String redisproperties = sftpDetails.getRedis_info(); // redis文件=

      String hadoopConf = sftpDetails.getHADOOP_CONF(); // 集群conf配置文件
      String targetdir = sftpDetails.getTarget＿dir(); // 目标路径

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
      chSftp_properties.put(
          sourcepath + dbproperties,
          targetdir + "/control/property/db.properties",
          new FileProgressMonitor(fileSizeMsgConf),
          ChannelSftp.OVERWRITE);
      chSftp_properties.put(
          sourcepath + dbproperties,
          targetdir + "/trigger/property/db.properties",
          new FileProgressMonitor(fileSizeMsgConf),
          ChannelSftp.OVERWRITE);
      logger.info("###########替换msgconf文件===");

      File fileDbInfo = new File(sourcepath + "redis.properties");
      long fileSizeDbInfo = fileDbInfo.length();
      chSftp_properties.put(
          sourcepath + redisproperties,
          targetdir + "/control/property/redis.properties",
          new FileProgressMonitor(fileSizeDbInfo),
          ChannelSftp.OVERWRITE);
      chSftp_properties.put(
          sourcepath + redisproperties,
          targetdir + "/trigger/property/redis.properties",
          new FileProgressMonitor(fileSizeDbInfo),
          ChannelSftp.OVERWRITE);
      logger.info("###########替换dbinfo文件===");

      File fileHadoopconf = new File(hadoopConf);
      File[] list = fileHadoopconf.listFiles();
      for (int i = 0; i < list.length; i++) {
        long fileSizeConf = list[i].length();
        chSftp_properties.put(
            list[i].toString(),
            targetdir + "/control/conf/",
            new FileProgressMonitor(fileSizeConf),
            ChannelSftp.OVERWRITE);
        chSftp_properties.put(
            list[i].toString(),
            targetdir + "/trigger/conf/",
            new FileProgressMonitor(fileSizeConf),
            ChannelSftp.OVERWRITE);
      }
      logger.info("###########替换集群配置conf文件===");

      chSftp_properties.quit();
      channel_properties.closeChannel();
      shellSession.disconnect();
      return "0";
    } catch (JSchException e) {
      logger.error("连接失败，请确认用户名密码正确", e);
      return "99";
    } catch (IOException e) {
      logger.error("网络异常，请确认网络正常", e);
      return "98";
    } catch (SftpException e) {
      logger.error("数据传输失败，请联系管理员", e);
      return "97";
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("部署失败，请重新部署" + e);
    }
  }
}
