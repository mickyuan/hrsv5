package hrds.agent.job.biz.core;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.NumberUtil;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.MetaInfoBean;
import hrds.commons.codes.FtpRule;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.TimeType;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DeCompressionUtil;
import hrds.commons.utils.MapDBHelper;
import hrds.commons.utils.jsch.SftpOperate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.beyoundsoft.mapdb.HTreeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 执行ftp采集的作业
 * date: 2019/10/12 10:29
 * author: zxz
 */
public class FtpCollectJobImpl implements JobInterface {
	//打印日志
	private static final Log log = LogFactory.getLog(FtpCollectJobImpl.class);
	//存放每次启动任务时候的线程的集合
	private static volatile ConcurrentMap<String, Thread> mapJob = new ConcurrentHashMap<>();
	//ftpId的字符串
	private volatile String ftpId = "";
	//当前程序运行的目录
	private static final String USER_DIR = System.getProperty("user.dir");
	//是否实时读取，默认为true，保证程序最少进一次循环
	private volatile boolean is_real_time = true;
	//Ftp采集设置表对象
	private Ftp_collect ftp_collect;
	//JobStatusInfo对象，表示一个作业的状态
	private final JobStatusInfo jobStatus;

	/**
	 * 完成ftp采集的作业实现.
	 *
	 * @param ftp_collect Ftp_collect
	 *                    含义：Ftp采集设置表对象
	 *                    取值范围：所有这张表不能为空的字段的值必须有，为空则会抛异常
	 * @param jobStatus   JobStatusInfo
	 *                    含义：JobStatusInfo对象，表示一个作业的状态
	 *                    取值范围：不能为空
	 */
	public FtpCollectJobImpl(Ftp_collect ftp_collect, JobStatusInfo jobStatus) {
		this.ftp_collect = ftp_collect;
		this.jobStatus = jobStatus;
	}

	/**
	 * ftp采集执行的主方法
	 * <p>
	 * 1.获取ftp_id根据ftp_id判断任务是否是重复发送，实时的仍然在继续运行，是则中断上一个实时线程
	 * 2.开始执行ftp采集，根据当前任务id将线程放入存放线程的集合
	 * 3.判断是否是实时读取，如果不是实时读取，只进一次此循环就会退出
	 * 4.根据ftp表的信息初始化sftp对象
	 * 5.根据下级目录类型定义ftp拉取或者推送的下级目录
	 * 6.判断是推送还是拉取，根据不同的模式建立目录，并推送或拉取文件
	 * 7.判断实时读取间隔时间为0或为空时为防止循环死读，默认线程休眠1秒
	 * 8.任务结束，根据当前任务id移除线程
	 */
	@Override
	public JobStatusInfo runJob() {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		//1.获取ftp_id根据ftp_id判断任务是否是重复发送，实时的仍然在继续运行，是则中断上一个实时线程
		this.ftpId = ftp_collect.getFtp_id().toString();
		Thread thread = mapJob.get(ftpId);
		if (thread != null && !thread.isInterrupted()) {
			log.info("重复发送，中断上一个实时线程");
			thread.interrupt();
			mapJob.remove(ftpId);
		}
		String is_read_realtime = ftp_collect.getIs_read_realtime();
		Long realtime_interval = ftp_collect.getRealtime_interval();
		String ftpDir = ftp_collect.getFtp_dir();
		String localPath = ftp_collect.getLocal_path();
		String ftpRulePath = ftp_collect.getFtp_rule_path();
		String fileSuffix = ftp_collect.getFile_suffix();
		//2.开始执行ftp采集，根据当前任务id将线程放入存放线程的集合
		log.info("开始执行ftp采集，根据当前任务id放入线程");
		mapJob.put(ftpId, Thread.currentThread());
		while (is_real_time) {
			//3.判断是否是实时读取，如果不是实时读取，只进一次此循环就会退出
			if (IsFlag.Fou.getCode().equals(is_read_realtime)) {
				is_real_time = false;
			}
			try (SftpOperate sftp = new SftpOperate(ftp_collect.getFtp_ip(), ftp_collect.getFtp_username(),
					StringUtil.unicode2String(ftp_collect.getFtp_password()),
					Integer.valueOf(ftp_collect.getFtp_port()))) {
				//4.根据ftp表的信息初始化sftp对象
				//5.根据下级目录类型定义ftp拉取或者推送的下级目录
				String ftpFolderName;
				if (ftpRulePath.equals(FtpRule.LiuShuiHao.getCode())) {
					ftpFolderName = currentDateDir(localPath);
				} else if (ftpRulePath.equals(FtpRule.GuDingMuLu.getCode())) {
					ftpFolderName = ftp_collect.getChild_file_path();
				} else if (ftpRulePath.equals(FtpRule.AnShiJian.getCode())) {
					ftpFolderName = getDateDir(ftp_collect.getChild_time());
				} else {
					throw new BusinessException("FTP rule 不存在：" + ftpRulePath);
				}
				//6.判断是推送还是拉取，根据不同的模式建立目录，并推送或拉取文件
				if (IsFlag.Shi.getCode().equals(ftp_collect.getFtp_model())) {
					String currentFTPDir = ftpDir + "/" + ftpFolderName;
					sftp.scpMkdir(currentFTPDir);
					transferPut(currentFTPDir, localPath, sftp, fileSuffix);
				} else {
					String currentLoadDir = localPath + "/" + ftpFolderName;
					boolean flag = validateDirectory(currentLoadDir);//验证并保证目录文件是存在的
					if (!flag) {
						throw new BusinessException("创建文件夹失败");
					}
					transferGet(ftpDir, currentLoadDir, sftp, ftp_collect.getIs_unzip()
							, ftp_collect.getReduce_type(), fileSuffix);
				}
			} catch (Exception e) {
				log.error("FTP传输失败！！！", e);
				throw new BusinessException("ftp传输失败！");
			}
			//7.判断实时读取间隔时间为0或为空时为防止循环死读，默认线程休眠1秒
			if (realtime_interval == null || realtime_interval == 0) {
				realtime_interval = 1L;
			}
			try {
				TimeUnit.SECONDS.sleep(realtime_interval);
			} catch (InterruptedException e) {
				log.error("线程休眠异常", e);
			}
		}
		//8.任务结束，根据当前任务id移除线程
		log.info("任务结束，根据当前任务id移除线程");
		mapJob.remove(ftpId);
		//TODO ftp采集只有一个步骤，这里面的状态该怎么设置
		return jobStatus;
	}

	/**
	 * 验证目录是否存在，不存在则创建目录
	 * <p>
	 * 1.判断文件是否存在，存在返回true
	 * 2.不存在，创建目录并返回
	 *
	 * @param filePath String
	 *                 含义：需要验证的路径
	 *                 取值范围：不能为空
	 * @return boolean
	 * 含义：目录是否存在的返回值，true表示存在
	 * 取值范围：不会为空
	 */
	private boolean validateDirectory(String filePath) {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		File file = new File(filePath);
		//1.判断文件是否存在，存在返回true
		if (file.exists()) {
			return true;
		}
		//2.不存在，创建目录并返回
		return file.mkdirs();
	}

	/**
	 * 下级目录规则如果是采取按时间，则根据用户定义的时间区间来建立目录
	 * <p>
	 * 1.获取当前服务器的日期时间
	 * 2.根据时间的精确度来截取时间
	 *
	 * @param childTime String
	 *                  含义：按时间建立的下级文件夹类型
	 *                  取值范围：不能为空
	 * @return String
	 * 含义：需要创建的时间文件夹名称
	 * 取值范围：不会为空
	 */
	private String getDateDir(String childTime) {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		//1.获取当前服务器的日期时间
		String dateDir = DateUtil.getSysDate() + DateUtil.getSysTime();
		//2.根据时间的精确度来截取时间
		if (childTime.equals(TimeType.Day.getCode())) {
			//到天
			return dateDir.substring(0, 8);
		} else if (childTime.equals(TimeType.Hour.getCode())) {
			//到小时
			return dateDir.substring(0, 10);
		} else if (childTime.equals(TimeType.Minute.getCode())) {
			//到分钟
			return dateDir.substring(0, 12);
		} else if (childTime.equals(TimeType.Second.getCode())) {
			//到秒
			return dateDir;
		} else {
			throw new BusinessException("下级目录时间错误 ：" + childTime);
		}
	}

	/**
	 * 通过ftp获取远程目录下的文件，根据MapDB中存储的已经被传输的文件过滤出需要被传输到本地目录的文件
	 * <p>
	 * 1.根据文件后缀拉取远程目录下的文件
	 * 2.遍历拉取到的远程文件对象
	 * 3.判断是文件夹，递归调用本方法
	 * 4.不是文件夹，判断文件有没有被拉取过，没有拉取过放到需要被拉取的文件对象的集合
	 *
	 * @param ftpDir           String
	 *                         含义：待传输的文件所在远程机器的目录
	 *                         取值范围：不能为空
	 * @param fileSuffix       String
	 *                         含义：文件后缀名
	 *                         取值范围：可以为空
	 * @param sftp             SftpOperate
	 *                         含义：sftp操作类
	 *                         取值范围：不能为空
	 * @param fileNameHTreeMap HTreeMap<String, String>
	 *                         含义：已经传输过的文件的键值对集合
	 *                         取值范围：可以为空对象
	 * @param fileToBeTransfer List<LsEntry>
	 *                         含义：需要被拉取的远程的文件的集合
	 *                         取值范围：可以为空对象
	 */
	private void getFileNameToBeTransfer(String ftpDir, String fileSuffix, SftpOperate sftp, HTreeMap<String, String>
			fileNameHTreeMap, List<LsEntry> fileToBeTransfer) {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		try {
			Vector<LsEntry> listDir;
			//1.根据文件后缀拉取远程目录下的文件
			if (StringUtil.isBlank(fileSuffix)) {
				//目录下文件全部获取
				listDir = sftp.listDir(ftpDir);
			} else {
				//以fileSuffix为后缀的文件才获取回来
				listDir = sftp.listDir(ftpDir, "*." + fileSuffix);
			}
			//2.遍历拉取到的远程文件对象
			for (LsEntry lsEntry : listDir) {
				//3.判断是文件夹，递归调用本方法
				if (lsEntry.getAttrs().isDir()) {
					if (ftpDir.endsWith("/")) {
						ftpDir = ftpDir + lsEntry.getFilename();
					} else {
						ftpDir = ftpDir + "/" + lsEntry.getFilename();
					}
					getFileNameToBeTransfer(ftpDir, fileSuffix, sftp, fileNameHTreeMap, fileToBeTransfer);
				} else {
					//4.不是文件夹，判断文件有没有被拉取过，没有拉取过放到需要被拉取的文件对象的集合
					if (!fileNameHTreeMap.containsKey(lsEntry.getFilename())
							|| (fileNameHTreeMap.containsKey(lsEntry.getFilename())
							&& !fileNameHTreeMap.get(lsEntry.getFilename())
							.equals(lsEntry.getAttrs().getMtimeString()))) {
						fileToBeTransfer.add(lsEntry);
					}
				}
			}
		} catch (Exception e) {
			log.error("远程拉取文件失败", e);
			throw new BusinessException("远程拉取文件失败");
		}
	}

	/**
	 * 将远程目录下的指定后缀名下的文件ftp拉取到本地的机器目录
	 * <p>
	 * 1.根据ftpId获取MapDB操作类的对象
	 * 2.通过ftp获取远程目录下的文件，根据MapDB中存储的已经被传输的文件过滤出需要被传输到本地目录的文件
	 * 3.拉取文件到本地
	 * 4.判断是否需要解压，需要解压则根据对应的压缩方式将文件解压到本地
	 * 5.将拉取成功的文件放到MapDB
	 * 6.提交MapDB
	 *
	 * @param ftpDir        String
	 *                      含义：待传输的文件所在远程机器的目录
	 *                      取值范围：不能为空
	 * @param destDir       String
	 *                      含义：需要拉取到的本地目录
	 *                      取值范围：不能为空
	 * @param sftp          SftpOperate
	 *                      含义：sftp操作类
	 *                      取值范围：不能为空
	 * @param isUnzip       String
	 *                      含义：是否需要解压缩
	 *                      取值范围：不能为空
	 * @param deCompressWay String
	 *                      含义：解压缩的方式
	 *                      取值范围：可以为空
	 * @param fileSuffix    String
	 *                      含义：文件后缀名
	 *                      取值范围：可以为空
	 */
	private void transferGet(String ftpDir, String destDir, SftpOperate sftp, String isUnzip,
	                         String deCompressWay, String fileSuffix) {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		//1.根据ftpId获取MapDB操作类的对象
		try (MapDBHelper mapDBHelper = new MapDBHelper(USER_DIR + File.separator
				+ this.ftpId, this.ftpId + ".db")) {
			HTreeMap<String, String> fileNameHTreeMap = mapDBHelper.htMap(this.ftpId, 24 * 60);
			//2.通过ftp获取远程目录下的文件，根据MapDB中存储的已经被传输的文件过滤出需要被传输到本地目录的文件
			//需要被传输的文件
			List<LsEntry> fileNameToBeTransfer = new ArrayList<>();
			getFileNameToBeTransfer(destDir, fileSuffix, sftp, fileNameHTreeMap, fileNameToBeTransfer);
			for (LsEntry lsEntry : fileNameToBeTransfer) {
				String fileName = lsEntry.getFilename();
				String ftpFile = ftpDir + "/" + fileName;
				//3.拉取文件到本地
				sftp.transferFile(ftpFile, destDir);
				String destFilePath = destDir + "/" + fileName;
				boolean isSuccessful;
				//4.判断是否需要解压，需要解压则根据对应的压缩方式将文件解压到本地
				if (IsFlag.Shi.getCode().equals(isUnzip)) {
					//将文件根据对应的解压缩方式进行解压
					isSuccessful = DeCompressionUtil.deCompression(destFilePath, deCompressWay);
					File file = new File(destFilePath);
					if (file.exists()) {
						if (!file.delete()) {
							throw new BusinessException("删除文件失败");
						}
					}
				} else {
					isSuccessful = true;
				}
				//5.将拉取成功的文件放到MapDB
				if (isSuccessful) {
					fileNameHTreeMap.put(fileName, lsEntry.getAttrs().getMtimeString());
				} else {
					throw new BusinessException("解压文件失败！！！");
				}
			}
			//6.提交MapDB
			mapDBHelper.commit();
		} catch (Exception e) {
			log.error("FTP传输失败！！！", e);
			throw new BusinessException("ftp传输失败！");
		}
	}

	/**
	 * 将本地目录下的指定后缀名下的文件ftp推送到远程的机器目录
	 * <p>
	 * 1.根据ftpId获取MapDB操作类的对象
	 * 2.获取需要进行ftp传输的文件夹及其子文件夹下的文件
	 * 3.进行ftp传输，将传输成功的文件放到MapDB
	 * 4.提交MapDB
	 *
	 * @param ftpDir     String
	 *                   含义：ftp推送的远程机器的目录
	 *                   取值范围：不能为空
	 * @param localPath  String
	 *                   含义：本地目录
	 *                   取值范围：不能为空
	 * @param sftp       SftpOperate
	 *                   含义：sftp操作类
	 *                   取值范围：不能为空
	 * @param fileSuffix String
	 *                   含义：文件后缀名
	 *                   取值范围：可以为空
	 */
	private void transferPut(String ftpDir, String localPath, SftpOperate sftp, String fileSuffix) {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		//1.根据ftpId获取MapDB操作类的对象
		try (MapDBHelper mapDBHelper = new MapDBHelper(USER_DIR + File.separator
				+ this.ftpId, this.ftpId + ".db")) {
			HTreeMap<String, String> fileNameHTreeMap = mapDBHelper.htMap(this.ftpId, 24 * 60);
			List<File> fileList = new ArrayList<>();
			//2.获取需要进行ftp传输的文件夹及其子文件夹下的文件
			getFileList(localPath, fileSuffix, fileList, fileNameHTreeMap);
			for (File fileName : fileList) {
				//3.进行ftp传输，将传输成功的文件放到MapDB
				sftp.transferPutFile(fileName.getAbsolutePath(), ftpDir + "/" + fileName.getName());
				fileNameHTreeMap.put(fileName.getName(), String.valueOf(fileName.lastModified()));
			}
			//4.提交MapDB
			mapDBHelper.commit();
		} catch (Exception e) {
			log.error("FTP传输失败！！！", e);
			throw new BusinessException("ftp传输失败！");
		}
	}

	/**
	 * 获取需要进行ftp传输的文件夹及其子文件夹下的文件
	 * <p>
	 * 1.取文件夹下未被传输过的文件或者已经传输过但被修改后的文件或者文件夹
	 * 2.遍历文件的集合，判断是文件夹则递归调用当前方法，是文件则判断文件后缀名是否符合规则
	 * 3.将符合的文件放到集合中
	 *
	 * @param strPath          String
	 *                         含义：需要进行ftp传输的文件夹
	 *                         取值范围：不能为空
	 * @param fileSuffix       String
	 *                         含义：需要传输的文件的后缀名
	 *                         取值范围：可以为空
	 * @param fileList         List<File>
	 *                         含义：符合条件的文件的集合
	 *                         取值范围：可以为空
	 * @param fileNameHTreeMap HTreeMap<String, String>
	 *                         含义：已经传输过的文件的键值对集合
	 *                         取值范围：可以为空对象
	 */
	private void getFileList(String strPath, String fileSuffix, List<File> fileList,
	                         HTreeMap<String, String> fileNameHTreeMap) {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		//1.取文件夹下未被传输过的文件或者已经传输过但被修改后的文件或者文件夹
		File[] files = new File(strPath).listFiles((file) -> (!fileNameHTreeMap.containsKey(file.getName())
				|| file.isDirectory() || (fileNameHTreeMap.containsKey(file.getName())
				&& !fileNameHTreeMap.get(file.getName()).equals(String.valueOf(file.lastModified())))));
		if (files != null) {
			//2.遍历文件的集合，判断是文件夹则递归调用当前方法，是文件则判断文件后缀名是否符合规则
			for (File file : files) {
				// 判断是文件还是文件夹
				if (file.isDirectory()) {
					getFileList(file.getAbsolutePath(), fileSuffix, fileList, fileNameHTreeMap);
				} else {
					if (StringUtil.isBlank(fileSuffix)) {
						//3.将符合的文件放到集合中
						fileList.add(file);
					} else if (file.getName().endsWith(fileSuffix)) {
						fileList.add(file);
					}
				}
			}
		}
	}

	/**
	 * 获取目录下数字文件夹，取数字最大的文件夹加一
	 * <p>
	 * 1.取当前文件夹下的所有为数字的文件夹的集合
	 * 2.判断集合是否为空，为空则返回字符串0
	 * 3.不为空则遍历集合，取最大值
	 * 4.返回最大值加一
	 *
	 * @param userCustomizeDbDir String
	 *                           含义：文件夹路径
	 *                           取值范围：不能为空
	 * @return String
	 * 含义：该文件夹下数值最大的文件夹加一
	 * 取值范围：不会为空
	 */
	private String currentDateDir(String userCustomizeDbDir) {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		//1.取当前文件夹下的所有为数字的文件夹的集合
		File[] listFiles = new File(userCustomizeDbDir).listFiles((file) ->
				NumberUtil.isNumberic(file.getName()) && file.isDirectory());
		//2.判断集合是否为空，为空则返回字符串0
		if (listFiles == null || listFiles.length == 0) {
			return "0";
		}
		int max = 0;
		//3.不为空则遍历集合，取最大值
		for (File file : listFiles) {
			int parseInt = Integer.parseInt(file.getName());
			if (parseInt > max) {
				max = parseInt;
			}
		}
		//4.返回最大值加一
		return String.valueOf(max + 1);
	}

	@Override
	public List<MetaInfoBean> getMetaInfoGroup() {
		return null;
	}

	@Override
	public MetaInfoBean getMetaInfo() {
		return null;
	}
}
