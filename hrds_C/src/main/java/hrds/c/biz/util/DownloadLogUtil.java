package hrds.c.biz.util;

import com.jcraft.jsch.ChannelSftp;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.jsch.FileProgressMonitor;
import hrds.commons.utils.jsch.SFTPChannel;
import hrds.commons.utils.jsch.SFTPDetails;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@DocClass(desc = "下载日志工具类", author = "dhw", createdate = "2019/12/19 16:50")
public class DownloadLogUtil {

    private static final Logger logger = LogManager.getLogger(DownloadLogUtil.class);

    @Method(desc = "通过SFTP删除日志文件",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.获取连接到sftp服务器的Channel" +
                    "3.使用sftp连接服务器" +
                    "4.通过SFTP删除日志文件" +
                    "5.退出SFTP连接" +
                    "6.关闭Channel连接")
    @Param(name = "directory", desc = "需要删除的文件目录", range = "取值范围")
    @Param(name = "sftpDetails", desc = "连接服务器配置信息", range = "无限制")
    public static void deleteLogFileBySFTP(String directory,SFTPDetails sftpDetails) {
        try {
            // 1.数据可访问权限处理方式，该方法不需要权限控制
//            SCPFileSender scpFileSender = new SCPFileSender();
            // 2.获取连接到sftp服务器的Channel
            SFTPChannel sftpChannel = new SFTPChannel();
            // 3.使用sftp连接服务器
            ChannelSftp channelSftp = sftpChannel.getChannel(sftpDetails, 60000);
            // 4.通过SFTP删除日志文件
            channelSftp.rm(directory);
            logger.info("###########删除文件成功===");
            // 5.退出SFTP连接
            channelSftp.quit();
            // 6.关闭Channel连接
            sftpChannel.closeChannel();
        } catch (Exception e) {
            throw new AppSystemException(e);
        }
    }

    @Method(desc = "下载日志文件",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.通过本地路径获取本地文件" +
                    "3.获取连接到sftp服务器的Channel" +
                    "4.通过sftp连接服务器" +
                    "5.通过远程路径获取本地文件" +
                    "6.通过sftp下载日志文件" +
                    "7.退出SFTP连接" +
                    "8.关闭Channel连接" +
                    "9.关闭流")
    @Param(name = "remotePath", desc = "远程路径", range = "无限制")
    @Param(name = "localPath", desc = "本地路径", range = "无限制")
    @Param(name = "sftpDetails", desc = "连接服务器配置信息", range = "无限制")
    public static void downloadLogFile(String remotePath, String localPath, SFTPDetails sftpDetails) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        OutputStream outputStream = null;
        try {
            // 2.通过本地路径以获取本地文件
            File localFile = new File(localPath);
            outputStream = new FileOutputStream(localFile);
//            SCPFileSender scpFileSender = new SCPFileSender();
//            // 3.获取连接到sftp服务器的Channel
//            SFTPChannel sftpChannel = scpFileSender.getSFTPChannel();
            SFTPChannel sftpChannel = new SFTPChannel();
            // 4.通过sftp连接服务器
            ChannelSftp channelSftp = sftpChannel.getChannel(sftpDetails, 60000);
            // 5.通过远程路径获取远程文件
            File remoteFile = new File(remotePath);
            if (!remoteFile.exists()) {
                throw new BusinessException("下载文件不存在！");
            }
            long fileSizeConf = remoteFile.length();
            // 6.通过sftp下载日志文件
            channelSftp.get(remotePath, outputStream, new FileProgressMonitor(fileSizeConf));
            logger.info("###########下载文件成功===");
            // 7.退出SFTP连接
            channelSftp.quit();
            // 8.关闭Channel连接
            sftpChannel.closeChannel();
            // 9.关闭流
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new BusinessException("找不到文件");
        } catch (Exception e) {
            logger.info("文件下载失败原因：" + e);
            throw new AppSystemException(e);
        }
    }

}
