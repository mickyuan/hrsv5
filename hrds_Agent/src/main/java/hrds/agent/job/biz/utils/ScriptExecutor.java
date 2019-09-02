package hrds.agent.job.biz.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * ClassName: ScriptExecutor <br/>
 * Function: 脚本执行工具 <br/>
 * Reason: 用于在java中执行各种类型的脚本. <br/>
 * Date: 2019/8/6 10:40 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
public class ScriptExecutor {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScriptExecutor.class);

    private final static String SCRIPTS_DIR = "scripts";
    private final static String SHELL_COMMAND = "sh";
    private final static String PUTHDFS_SCRIPTS = "uploadHDFS.sh";

    /**
     * 上传单个文件至hdfs
     * @author   13616
     * @date     2019/8/7 11:17
     * @param localFile 本地文件地址
     * @param remoteDir hdfs目录地址
     * @throws InterruptedException 当执行脚本的进程无法启动时抛出该异常
     * @throws IllegalStateException 当脚本执行错误时抛出该异常
     * @return   void
     */
    public void executeUpload2Hdfs(String localFile, String remoteDir) throws InterruptedException, IllegalStateException {

        service(PUTHDFS_SCRIPTS, localFile, remoteDir);
    }

    /**
     * 上传多个文件至hdfs
     * @param localFiles 本地文件地址数组
     * @param remoteDir hdfs目录地址
     * @throws InterruptedException 当执行脚本的进程无法启动时抛出该异常
     * @throws IllegalStateException 当脚本执行错误时抛出该异常
     * @return   void
     */
    public void executeUpload2Hdfs(String[] localFiles, String remoteDir) throws InterruptedException, IllegalStateException {

        service(PUTHDFS_SCRIPTS, localFiles, remoteDir);
    }

    /**
     * 用于寻找脚本路径，单文件上传使用
     * @author   13616
     * @date     2019/8/7 11:25
     * @param shellName 脚本文件名
     * @param args  执行参数
     * @throws InterruptedException 当执行脚本的进程无法启动时抛出该异常
     * @throws IllegalStateException 当脚本执行错误时抛出该异常
     * @return   void
     */
    private void service(String shellName, String... args) throws InterruptedException, IllegalStateException {
        //获取脚本所在的目录
        String shellPath = ProductFileUtil.getProjectPath() + File.separatorChar
                + StringUtils.join(new String[]{"src", "main", "resources"}, File.separatorChar)
                + File.separatorChar + SCRIPTS_DIR + File.separatorChar + shellName;
        //构建命令并执行
        buildCommandAndExe(shellPath, args);
    }

    /**
     * 用于寻找脚本路径，多文件上传使用
     * @param shellName 脚本文件名
     * @param localFiles  文件路径数组
     * @param remoteDir  上传目录
     * @throws InterruptedException 当执行脚本的进程无法启动时抛出该异常
     * @throws IllegalStateException 当脚本执行错误时抛出该异常
     * @return   void
     */
    private void service(String shellName, String[] localFiles, String remoteDir) throws InterruptedException, IllegalStateException {
        //获取脚本所在的目录
        String shellPath = ProductFileUtil.getProjectPath() + File.separatorChar
                + StringUtils.join(new String[]{"src", "main", "resources"}, File.separatorChar)
                + File.separatorChar + SCRIPTS_DIR + File.separatorChar + shellName;
        //构建命令并执行
        buildCommandAndExe(shellPath, localFiles, remoteDir);
    }

    /**
     * 用于构建命令并执行，单文件上传使用
     * @param script 脚本文件名
     * @param args  执行参数
     * @throws InterruptedException 当执行脚本的进程无法启动时抛出该异常
     * @throws IllegalStateException 当脚本执行错误时抛出该异常
     * @return   void
     */
    private void buildCommandAndExe(String script, String... args) throws InterruptedException, IllegalStateException{
        String cmd = SHELL_COMMAND + " " + script  + " " + StringUtils.join(args, " ");
        callScript(script, cmd);
    }

    /**
     * 用于构建命令并执行，多文件上传使用
     * @param script 脚本文件名
     * @param localFiles 文件路径数组
     * @param remoteDir 上传目录
     * @throws InterruptedException 当执行脚本的进程无法启动时抛出该异常
     * @throws IllegalStateException 当脚本执行错误时抛出该异常
     * @return   void
     */
    private void buildCommandAndExe(String script, String[] localFiles, String remoteDir) throws InterruptedException, IllegalStateException{
        String cmd = SHELL_COMMAND + " " + script  + " " + StringUtils.join(localFiles, " ") + " " + remoteDir;
        callScript(script, cmd);
    }

    /**
     * 用于构造脚本执行命令，并且执行
     * @author   13616
     * @date     2019/8/6 11:32 
     * @param script    脚本地址
     * @param cmd  执行命令
     * @throws   InterruptedException   当执行脚本的进程无法启动时抛出该异常
     * @throws   IllegalStateException  当脚本执行错误时抛出该异常
     * @return   void
     */
    private void callScript(String script, String cmd) throws InterruptedException, IllegalStateException {

        //启动独立线程等待process执行完成
        CommandWaitForThread commandThread = new CommandWaitForThread(cmd);
        commandThread.start();

        while (!commandThread.isFinish()) {
            LOGGER.info("shell " + script + " 还未执行完毕, 5s后重新探测");
            Thread.sleep(5000);
        }

        //检查脚本执行结果状态码
        if(commandThread.getExitCode() != 0){
            throw new IllegalStateException(cmd + " 执行失败, exitCode = " + commandThread.getExitCode());
        }
        LOGGER.info(cmd + " 执行成功,exitValue = " + commandThread.getExitCode());
    }

    private class CommandWaitForThread extends Thread {

        private String cmd;
        private boolean finish = false;
        private int exitValue = -1;

        public CommandWaitForThread(String cmd) {
            this.cmd = cmd;
        }

        public void run(){

            BufferedReader infoInput = null;
            BufferedReader errorInput = null;
            try {
                //执行脚本并等待脚本执行完成
                Process process = Runtime.getRuntime().exec(cmd);

                //写出脚本执行中的过程信息
                infoInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = "";
                while ((line = infoInput.readLine()) != null) {
                    LOGGER.info(line);
                }
                while ((line = errorInput.readLine()) != null) {
                    LOGGER.error(line);
                }
                infoInput.close();
                errorInput.close();

                //阻塞执行线程直至脚本执行完成后返回
                this.exitValue = process.waitFor();
            } catch (Throwable e) {
                LOGGER.error("CommandWaitForThread accure exception,shell " + cmd, e);
            } finally {
                if(null != infoInput){
                    try {
                        infoInput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(null != errorInput){
                    try {
                        errorInput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                setFinish(true);
            }
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }

        public int getExitCode() {
            return exitValue;
        }
    }
}