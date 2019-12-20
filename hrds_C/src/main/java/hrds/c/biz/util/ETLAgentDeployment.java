package hrds.c.biz.util;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.db.conf.DbinfosConf;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaValue;
import hrds.commons.utils.jsch.SCPFileSender;
import hrds.commons.utils.jsch.SFTPChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "ETL部署类", author = "dhw", createdate = "2019/12/19 14:57")
public class ETLAgentDeployment {

    private static final Logger logger = LogManager.getLogger(ETLAgentDeployment.class);

    @Method(desc = "ETL部署",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.获取ETL下载地址" +
                    "3.根据文件路径获取文件信息" +
                    "4.判断文件是否存在" +
                    "5.获取数据库连接配置信息" +
                    "6.获取redis连接配置信息" +
                    "7.获得程序当前路径" +
                    "8.根据程序当前路径获取文件" +
                    "9.集群conf配置文件目录" +
                    "10.创建存放部署ETL连接信息的集合并封装属性" +
                    "11.ETL部署" +
                    "12.部署完成后删除db与redis配置文件")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新建工程时生成")
    @Param(name = "etl_serv_ip", desc = "ETL部署agent的IP地址", range = "如：127.0.0.1")
    @Param(name = "etl_serv_port", desc = "ETL部署agent的端口", range = "默认22")
    @Param(name = "redisIP", desc = "ETL部署redis的ip", range = "如：127.0.0.1")
    @Param(name = "redisPort", desc = "ETL部署redis的端口", range = "默认56379")
    @Param(name = "userName", desc = "ETL部署agent机器的用户名", range = "服务器用户名")
    @Param(name = "password", desc = "ETL部署agent机器的密码", range = "服务器密码")
    @Param(name = "targetDir", desc = "ETL部署服务器目录地址", range = "无限制")
    public static void scpETLAgent(String etl_sys_cd, String etl_serv_ip, String etl_serv_port,
                                   String redisIP, String redisPort, String userName, String password,
                                   String targetDir) {
        BufferedWriter bw = null;
        try {
            // 1.数据可访问权限处理方式，该方法不需要权限控制
            // 2.获取ETL下载地址
            String agentPath = PropertyParaValue.getString("ETLpath", "");
            String separator = File.separator;
            // 3.根据文件路径获取文件信息
            File file = FileUtil.getFile(agentPath);
            // 4.判断文件是否存在
            if (!file.exists()) {
                throw new BusinessException("ETL下载地址目录下文件不存在！");
            }
            String pathAbsolute = file.getParent();
            String agent_zip = file.getName();
            String pathFile = pathAbsolute + separator;
            // 5.获取数据库连接配置信息
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathFile
                    + "db.properties", false), CodecUtil.UTF8_CHARSET));
            DbinfosConf.Dbinfo dbInfo = DbinfosConf.getDatabase("default");
            String dbConf = "jdbc.url=" + dbInfo.getUrl() + System.lineSeparator()
                    + "jdbc.driver=" + dbInfo.getDriver() + System.lineSeparator()
                    + "jdbc.username=" + dbInfo.getName() + System.lineSeparator()
                    + "jdbc.password=" + dbInfo.getPassword() + System.lineSeparator();
            bw.write(dbConf);
            bw.flush();
            bw.close();
            // 6.获取redis连接配置信息
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathFile
                    + "redis.properties", false), CodecUtil.UTF8_CHARSET));
            String redisConf = "redis.ip=" + redisIP + System.lineSeparator()
                    + "redis.port=" + redisPort + System.lineSeparator();
            bw.write(redisConf);
            bw.flush();
            bw.close();
            // 7.获得程序当前路径
            String userDir = System.getProperty("user.dir");
            // 8.根据程序当前路径获取文件
            File userDirFile = FileUtil.getFile(userDir);
            // 9.集群conf配置文件目录
            String hadoopConf = userDirFile + separator + "conf" + separator;
            // 10.创建存放部署ETL连接信息的集合并封装属性
            Map<String, String> sftpDetails = new HashMap<>();
            // 部署agent服务器IP
            sftpDetails.put(SCPFileSender.HOST, etl_serv_ip);
            // 部署agent服务器用户名
            sftpDetails.put(SCPFileSender.USERNAME, userName);
            // 部署agent服务器密码
            sftpDetails.put(SCPFileSender.PASSWORD, password);
            // 部署agent服务器端口
            sftpDetails.put(SCPFileSender.PORT, etl_serv_port);
            // 本地文件路径
            sftpDetails.put(SCPFileSender.SOURCEPATH, pathFile);
            // 本地文件名称
            sftpDetails.put(SCPFileSender.AGENT_GZ, agent_zip);
            // db配置文件
            sftpDetails.put(SCPFileSender._MSGCONF, "db.properties");
            // redis配置文件
            sftpDetails.put(SCPFileSender._DBINFO, "redis.properties");
            // 集群conf配置文件
            sftpDetails.put(SCPFileSender.HADOOPCONF, hadoopConf);
            // 目标路径
            sftpDetails.put(SCPFileSender.TARGETDIR, targetDir + separator + etl_sys_cd + separator);
            // 11.ETL部署
            SCPFileSender.ETLscpToFrom(sftpDetails);
            // 12.部署完成后删除db与redis配置文件
            FileUtil.deleteDirectoryFiles(pathFile + separator + "db.properties");
            FileUtil.deleteDirectoryFiles(pathFile + separator + "redis.properties");
        } catch (Exception e) {
            throw new AppSystemException(e);
        }
    }

    @Method(desc = "启动Control",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.设置连接服务器属性信息" +
                    "3.与远端服务器进行交互，建立连接，发送数据到远端并且接收远端发来的数据" +
                    "4.启动CONTROL脚本命令" +
                    "5.执行命令启动CONTROL" +
                    "6.断开连接")
    @Param(name = "batch_date", desc = "跑批日期", range = "yyyy-MM-dd格式的年月日")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "isResumeRun", desc = "是否续跑", range = "使用（IsFlag）代码项，1代表是，0代表否")
    @Param(name = "isAutoShift", desc = "是否日切", range = "使用（IsFlag）代码项，1代表是，0代表否")
    @Param(name = "etl_serv_ip", desc = "部署服务器IP", range = "如：127.0.0.1")
    @Param(name = "etl_serv_port", desc = "部署服务器端口", range = "默认22")
    @Param(name = "userName", desc = "部署服务器用户名", range = "无限制")
    @Param(name = "userName", desc = "部署服务器密码", range = "无限制")
    @Param(name = "deploymentPath", desc = "部署服务器路径", range = "无限制")
    public static void startEngineBatchControl(String batch_date, String etl_sys_cd, String isResumeRun,
                                               String isAutoShift, String etl_serv_ip, String etl_serv_port,
                                               String userName, String password, String deploymentPath) {
        try {
            // 1.数据可访问权限处理方式，该方法不需要权限控制
            // 2.设置连接服务器属性信息
            Map<String, String> sftpDetails = new HashMap<>();
            sftpDetails.put(SCPFileSender.HOST, etl_serv_ip);
            sftpDetails.put(SCPFileSender.USERNAME, userName);
            sftpDetails.put(SCPFileSender.PASSWORD, password);
            sftpDetails.put(SCPFileSender.PORT, etl_serv_port);
            // 3.与远端服务器进行交互，建立连接，发送数据到远端并且接收远端发来的数据
            Session shellSession = SFTPChannel.getJSchSession(sftpDetails, 0);
            // 4.启动CONTROL脚本命令
            String startAgent = "source /etc/profile;source ~/.bash_profile;source ~/.bashrc; cd "
                    + deploymentPath + "/" + etl_sys_cd + "/control/ ;" + "sh startEngineBatchControl.sh"
                    + " " + batch_date + " " + etl_sys_cd + " " + isResumeRun + " " + isAutoShift;
            logger.info("######################" + startAgent);
            // 5.执行命令启动CONTROL
            SFTPChannel.execCommandByJSchNoRs(shellSession, startAgent);
            logger.info("###########启动startEngineBatchControl成功===");
            // 6.断开连接
            shellSession.disconnect();
        } catch (JSchException e) {
            logger.error("连接失败，请确认用户名密码正确", e);
            throw new BusinessException("连接失败，请确认用户名密码正确");
        } catch (IOException e) {
            logger.error("网络异常，请确认网络正常", e);
            throw new BusinessException("网络异常，请确认网络正常");
        } catch (Exception e) {
            throw new AppSystemException("部署失败，请重新部署" + e);
        }
    }

    @Method(desc = "启动trigger",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.设置连接服务器属性信息" +
                    "3.与远端服务器进行交互，建立连接，发送数据到远端并且接收远端发来的数据" +
                    "4.启动TRIGGER脚本命令" +
                    "5.执行启动TRIGGER脚本命令启动TRIGGER" +
                    "6.断开连接")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_serv_ip", desc = "部署服务器IP", range = "如：127.0.0.1")
    @Param(name = "etl_serv_port", desc = "部署服务器端口", range = "默认22")
    @Param(name = "userName", desc = "服务器用户名", range = "无限制")
    @Param(name = "password", desc = "服务器密码", range = "无限制")
    @Param(name = "deploymentPath", desc = "部署服务器路径", range = "无限制")
    public static void startEngineBatchTrigger(String etl_sys_cd, String etl_serv_ip, String etl_serv_port,
                                               String userName, String password, String deploymentPath) {
        try {
            // 1.数据可访问权限处理方式，该方法不需要权限控制
            // 2.设置连接服务器属性信息
            Map<String, String> sftpDetails = new HashMap<>();
            sftpDetails.put(SCPFileSender.HOST, etl_serv_ip);
            sftpDetails.put(SCPFileSender.USERNAME, userName);
            sftpDetails.put(SCPFileSender.PASSWORD, password);
            sftpDetails.put(SCPFileSender.PORT, etl_serv_port);
            // 3.与远端服务器进行交互，建立连接，发送数据到远端并且接收远端发来的数据
            Session shellSession = SFTPChannel.getJSchSession(sftpDetails, 0);
            // 4.启动TRIGGER脚本命令
            String startAgent = "source /etc/profile;source ~/.bash_profile;source ~/.bashrc; cd " +
                    deploymentPath + "/" + etl_sys_cd + "/trigger/ ;" + "sh startEngineBatchTrigger.sh"
                    + " " + etl_sys_cd;
            logger.info("##############" + startAgent);
            // 5.执行启动TRIGGER脚本命令启动TRIGGER
            SFTPChannel.execCommandByJSchNoRs(shellSession, startAgent);
            logger.info("###########启动startEngineBatchTrigger成功===");
            // 6.断开连接
            shellSession.disconnect();
        } catch (JSchException e) {
            logger.error("连接失败，请确认用户名密码正确", e);
            throw new BusinessException("连接失败，请确认用户名密码正确");
        } catch (IOException e) {
            logger.error("网络异常，请确认网络正常", e);
            throw new BusinessException("网络异常，请确认网络正常");
        } catch (Exception e) {
            throw new AppSystemException("部署失败，请重新部署" + e);
        }
    }

}
