package hrds.commons.utils.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import hrds.commons.exception.BusinessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Vector;

/**
 * sftp远程创建文件拉取文件操作类
 * date: 2019/10/11 11:29
 * author: zxz
 */
public class SftpOperate implements Closeable {
	//打印日志
	private static final Log logger = LogFactory.getLog(SftpOperate.class);
	//默认超时时间
	private static final int SFTP_timeout = 6 * 1000;
	//sftp连接的session
	private Session session = null;
	//sftp连接
	private ChannelSftp sftp = null;

	/**
	 * sftp远程创建文件拉取文件操作类构造方法
	 * <p>
	 * 1.初始化sftp连接的session
	 * 2.初始化sftp连接
	 *
	 * @param ftpHost     String
	 *                    含义：ftp的连接地址
	 *                    取值范围：不能为空
	 * @param ftpUserName String
	 *                    含义：ftp连接的用户名
	 *                    取值范围：不能为空
	 * @param ftpPassword String
	 *                    含义：ftp连接的密码
	 *                    取值范围：不能为空
	 * @param ftpPort     int
	 *                    含义：ftp连接的端口
	 *                    取值范围：不能为空
	 */
	public SftpOperate(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort) {
		try {
			SFTPChannel sftpChannel = new SFTPChannel();
			//1.初始化sftp连接的session
			this.session = sftpChannel.getJSchSession(ftpHost, ftpUserName, ftpPassword, ftpPort, SFTP_timeout);
			//2.初始化sftp连接
			this.sftp = sftpChannel.getChannel(session, SFTP_timeout);
		} catch (JSchException e) {
			logger.error("获取sftp操作类失败！", e);
			throw new BusinessException("获取sftp操作类失败！");
		}
	}

	/**
	 * 获取远程目录下的文件对象集合
	 * <p>
	 * 1.调用方法全匹配远程目录下的文件对象集合
	 *
	 * @param srcDir String
	 *               含义：需要拉取的远程的目录
	 *               取值范围：不能为空
	 * @return Vector<LsEntry>
	 * 含义：拉取到的远程的ls的对象的集合
	 * 取值范围：可能为空集合
	 * @throws SftpException 含义：sftp远程连接异常
	 */
	public Vector<LsEntry> listDir(String srcDir) throws SftpException {
		//1.调用方法全匹配远程目录下的文件对象集合
		return listDir(srcDir, "*");
	}

	/**
	 * 按照正则获取远程目录下的文件对象集合
	 * <p>
	 * 1.判断需要获取的目录文件夹是否以/结尾，根据是否以/结尾拼接路径获取远程目录下文件的集合
	 *
	 * @param srcDir String
	 *               含义：需要拉取的远程的目录
	 *               取值范围：不能为空
	 * @param regex  String
	 *               含义：匹配规则
	 *               取值范围：不能为空
	 * @return Vector<LsEntry>
	 * 含义：拉取到的远程的ls的对象的集合
	 * 取值范围：可能为空集合
	 * @throws SftpException 含义：sftp远程连接异常
	 */
	@SuppressWarnings("unchecked")
	public Vector<LsEntry> listDir(String srcDir, String regex) throws SftpException {
		//1.判断需要获取的目录文件夹是否以/结尾，根据是否以/结尾拼接路径获取远程目录下文件的集合
		if (srcDir.endsWith("/")) {
			return sftp.ls(srcDir + regex);
		} else {
			return sftp.ls(srcDir + "/" + regex);
		}
	}

	/**
	 * 使用sftp拉取远程服务器上的文件到本地
	 *
	 * @param srcFile  String
	 *                 含义：远程文件全路径
	 *                 取值范围：不能为空
	 * @param destFile 含义：本地目录
	 *                 取值范围：不能为空
	 * @throws SftpException 含义：sftp远程连接异常
	 */
	public void transferFile(String srcFile, String destFile) throws SftpException {
		sftp.get(srcFile, destFile);
	}

	/**
	 * 使用sftp推送本地文件到远程服务器
	 *
	 * @param srcFile  String
	 *                 含义：本地文件全路径
	 *                 取值范围：不能为空
	 * @param destFile String
	 *                 含义：远程目录
	 *                 取值范围：不能为空
	 * @throws SftpException 含义：sftp远程连接异常
	 */
	public void transferPutFile(String srcFile, String destFile) throws SftpException {
		sftp.put(srcFile, destFile);
	}

	/**
	 * 使用sftp远程创建目录
	 * <p>
	 * 1.拼接创建文件夹的命令，使用SFTPChannel执行
	 *
	 * @param currentLoadDir 含义：需要被创建的远程目录
	 *                       取值范围：不能为空
	 * @throws JSchException 含义：sftp连接远程服务器异常
	 * @throws IOException   含义：创建文件夹IO流异常
	 */
	public void scpMkdir(String currentLoadDir) throws JSchException, IOException {
		//1.拼接创建文件夹的命令，使用SFTPChannel执行
		String mkdir = "mkdir -p " + currentLoadDir;
		SFTPChannel.execCommandByJSch(session, mkdir);
	}

	/**
	 * 实现Closeable重写的方法，try中构造这个对象，结束方法后会自动调用这个方法
	 * <p>
	 * 1.sftp不为空关闭sftp连接
	 * 2.session不为空关闭session回话连接
	 */
	@Override
	public void close() {
		//1.sftp不为空关闭sftp连接
		if (sftp != null) {
			sftp.disconnect();
		}
		//2.session不为空关闭session回话连接
		if (session != null) {
			session.disconnect();
		}
	}

	public static void main(String[] args) {

		try (SftpOperate sftpmove = new SftpOperate("47.103.86.60", "hyshf",
				"q1w2e3", 22)) {
			sftpmove.scpMkdir("/home/hyshf/zxz/0");
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
