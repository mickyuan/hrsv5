package hrds.agent.job.biz.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@DocClass(desc = "脚本执行工具,用于在java中执行各种类型的脚本", author = "WangZhengcheng")
public class ScriptExecutor {
	private final static Logger LOGGER = LoggerFactory.getLogger(ScriptExecutor.class);

	private final static String SCRIPTS_DIR = "scripts";
	private final static String SHELL_COMMAND = "sh";
	private final static String PUTHDFS_SCRIPTS = "uploadHDFS.sh";

	@Method(desc = "上传单个文件至hdfs", logicStep = "" +
			"1、对入参进行合法性校验，校验不通过，抛异常" +
			"2、调用本类中的service方法完成处理逻辑")
	@Param(name = "localFile", desc = "本地文件地址", range = "不为空")
	@Param(name = "remoteDir", desc = "hdfs目录地址", range = "不为空")
	public void executeUpload2Hdfs(String localFile, String remoteDir)
			throws InterruptedException, IllegalStateException {
		//1、对入参进行合法性校验，校验不通过，抛异常
		if(StringUtil.isBlank(localFile)){
			throw new AppSystemException("本地文件地址不能为空");
		}
		if(StringUtil.isBlank(remoteDir)){
			throw new AppSystemException("hdfs目录地址不能为空");
		}
		//2、调用本类中的service方法完成处理逻辑
		service(PUTHDFS_SCRIPTS, localFile, remoteDir);
	}

	@Method(desc = "上传多个文件至hdfs", logicStep = "" +
			"1、对入参进行合法性校验，校验不通过，抛异常" +
			"2、调用本类中的service方法完成处理逻辑")
	@Param(name = "localFiles", desc = "本地文件地址数组", range = "不为空")
	@Param(name = "remoteDir", desc = "hdfs目录地址", range = "不为空")
	public void executeUpload2Hdfs(String[] localFiles, String remoteDir)
			throws InterruptedException, IllegalStateException {
		//1、对入参进行合法性校验，校验不通过，抛异常
		if(localFiles.length == 0){
			throw new AppSystemException("本地文件地址不能为空");
		}
		if(StringUtil.isBlank(remoteDir)){
			throw new AppSystemException("hdfs目录地址不能为空");
		}
		//2、调用本类中的service方法完成处理逻辑
		service(PUTHDFS_SCRIPTS, localFiles, remoteDir);
	}

	/**
	 * @throws InterruptedException  当执行脚本的进程无法启动时抛出该异常
	 * @throws IllegalStateException 当脚本执行错误时抛出该异常
	 * */
	@Method(desc = "用于寻找脚本路径，单文件上传使用", logicStep = "" +
			"1、获取脚本所在的目录" +
			"2、构建命令并执行")
	@Param(name = "shellName", desc = "脚本文件名", range = "不为空")
	@Param(name = "args", desc = "执行参数", range = "可变参数，可能有多个，传入本地文件地址，hdfs目录地址不能为空")
	private void service(String shellName, String... args)
			throws InterruptedException, IllegalStateException {
		//1、获取脚本所在的目录
		String shellPath = ProductFileUtil.getProjectPath() + File.separatorChar
				+ StringUtils.join(new String[]{"src", "main", "resources"}, File.separatorChar)
				+ File.separatorChar + SCRIPTS_DIR + File.separatorChar + shellName;
		//2、构建命令并执行
		buildCommandAndExe(shellPath, args);
	}

	/**
	 * @throws InterruptedException  当执行脚本的进程无法启动时抛出该异常
	 * @throws IllegalStateException 当脚本执行错误时抛出该异常
	 * */
	@Method(desc = "用于寻找脚本路径，多文件上传使用", logicStep = "" +
			"1、获取脚本所在的目录" +
			"2、构建命令并执行")
	@Param(name = "shellName", desc = "脚本文件名", range = "不为空")
	@Param(name = "localFiles", desc = "文件路径数组", range = "不为空")
	@Param(name = "remoteDir", desc = "上传目录", range = "不为空")
	private void service(String shellName, String[] localFiles, String remoteDir)
			throws InterruptedException, IllegalStateException {
		//1、获取脚本所在的目录
		String shellPath = ProductFileUtil.getProjectPath() + File.separatorChar
				+ StringUtils.join(new String[]{"src", "main", "resources"}, File.separatorChar)
				+ File.separatorChar + SCRIPTS_DIR + File.separatorChar + shellName;
		//2、构建命令并执行
		buildCommandAndExe(shellPath, localFiles, remoteDir);
	}

	/**
	 * @throws InterruptedException  当执行脚本的进程无法启动时抛出该异常
	 * @throws IllegalStateException 当脚本执行错误时抛出该异常
	 * */
	@Method(desc = "用于构建命令并执行，单文件上传使用", logicStep = "" +
			"1、拼接最终执行的linux命令" +
			"2、调用本类的callScript()方法执行命令")
	@Param(name = "script", desc = "脚本文件名", range = "不为空")
	@Param(name = "args", desc = "执行参数", range = "不为空")
	private void buildCommandAndExe(String script, String... args)
			throws InterruptedException, IllegalStateException {
		//1、拼接最终执行的linux命令
		String cmd = SHELL_COMMAND + " " + script + " " + StringUtils.join(args, " ");
		//2、调用本类的callScript()方法执行命令
		callScript(script, cmd);
	}

	/**
	 * @throws InterruptedException  当执行脚本的进程无法启动时抛出该异常
	 * @throws IllegalStateException 当脚本执行错误时抛出该异常
	 * */
	@Method(desc = "用于构建命令并执行，多文件上传使用", logicStep = "" +
			"1、拼接最终执行的linux命令" +
			"2、调用本类的callScript()方法执行命令")
	@Param(name = "script", desc = "脚本文件名", range = "不为空")
	@Param(name = "localFiles", desc = "文件路径数组", range = "不为空")
	@Param(name = "remoteDir", desc = "上传目录", range = "不为空")
	private void buildCommandAndExe(String script, String[] localFiles, String remoteDir)
			throws InterruptedException, IllegalStateException {
		//1、拼接最终执行的linux命令
		String cmd = SHELL_COMMAND + " " + script + " " + StringUtils.join(localFiles, " ") + " " + remoteDir;
		//2、调用本类的callScript()方法执行命令
		callScript(script, cmd);
	}

	/**
	 * @throws InterruptedException  当执行脚本的进程无法启动时抛出该异常
	 * @throws IllegalStateException 当脚本执行错误时抛出该异常
	 * */
	@Method(desc = "用于构造脚本执行命令，并且执行", logicStep = "" +
			"1、启动独立线程等待process执行完成" +
			"2、检查脚本执行结果状态码")
	@Param(name = "script", desc = "脚本地址", range = "不为空")
	@Param(name = "cmd", desc = "执行命令", range = "不为空")
	private void callScript(String script, String cmd)
			throws InterruptedException, IllegalStateException {

		//1、启动独立线程等待process执行完成
		CommandWaitForThread commandThread = new CommandWaitForThread(cmd);
		commandThread.start();

		while (!commandThread.isFinish()) {
			LOGGER.info("shell " + script + " 还未执行完毕, 5s后重新探测");
			Thread.sleep(5000);
		}

		//2、检查脚本执行结果状态码
		if (commandThread.getExitCode() != 0) {
			throw new IllegalStateException(cmd + " 执行失败, exitCode = " + commandThread.getExitCode());
		}
		LOGGER.info(cmd + " 执行成功,exitValue = " + commandThread.getExitCode());
	}

	private class CommandWaitForThread extends Thread {

		private String cmd;
		private boolean finish = false;
		private int exitValue = -1;

		CommandWaitForThread(String cmd) {
			this.cmd = cmd;
		}

		public void run() {

			BufferedReader infoInput = null;
			BufferedReader errorInput = null;
			try {
				//执行脚本并等待脚本执行完成
				Process process = Runtime.getRuntime().exec(cmd);

				//写出脚本执行中的过程信息
				infoInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line;
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
				if (null != infoInput) {
					try {
						infoInput.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (null != errorInput) {
					try {
						errorInput.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				setFinish(true);
			}
		}

		//成员变量的getter/setter，没有业务处理逻辑
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