package hrds.commons.utils;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import hrds.commons.utils.jsch.SFTPChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadLog {

    private static final Logger logger = LogManager.getLogger();

    public static String readAgentLog(String logPath, String ip, String port, String userName, String password,
                                      int readNum) {

        String execCommandByJSch = "";
        Session jSchSession = null;
        try {
            Map<String, String> serverInfo = new HashMap<>();
            serverInfo.put("HOST", ip);
            serverInfo.put("PORT", port);
            serverInfo.put("USERNAME", userName);
            serverInfo.put("PASSWORD", password);
            jSchSession = SFTPChannel.getJSchSession(serverInfo, 0);
            String readShell = "cat " + logPath + " |tail -n " + readNum;
            execCommandByJSch = SFTPChannel.execCommandByJSch(jSchSession, readShell);
        } catch (JSchException e) {
            execCommandByJSch = e.getMessage();
            logger.debug("登录验证失败...");
            e.printStackTrace();
        } catch (IOException e) {
            execCommandByJSch = e.getMessage();
            e.printStackTrace();
            logger.debug("读取日志文件-----" + logPath + "-----失败...");
        } finally {
            if (jSchSession != null) {
                jSchSession.disconnect();
            }
        }
        return execCommandByJSch;
    }
}
