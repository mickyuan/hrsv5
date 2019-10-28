package hrds.agent.job.biz.core;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileNameUtils;
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

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@DocClass(desc = "执行ftp采集的作业", author = "zxz", createdate = "2019/10/12 10:29")
public class FtpCollectJobImpl implements JobInterface {
	//打印日志
	private static final Log log = LogFactory.getLog(FtpCollectJobImpl.class);
	//存放每次启动任务时候的线程的集合
	private static volatile ConcurrentMap<String, Thread> mapJob = new ConcurrentHashMap<>();
	//当前程序运行的目录
	private static final String USER_DIR = System.getProperty("user.dir");
	//是否实时读取，默认为true，保证程序最少进一次循环
	private volatile boolean is_real_time = true;
	//Ftp采集设置表对象
	private Ftp_collect ftp_collect;
	//JobStatusInfo对象，表示一个作业的状态
	private final JobStatusInfo jobStatus;

	/**
	 * ftp采集的作业实现类构造方法.
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

	@Method(desc = "ftp采集执行的主方法",
			logicStep = "1.获取ftp_id根据ftp_id判断任务是否是重复发送，实时的仍然在继续运行，是则中断上一个实时线程" +
					"2.开始执行ftp采集，根据当前任务id将线程放入存放线程的集合" +
					"3.判断是否是实时读取，如果不是实时读取，只进一次此循环就会退出" +
					"4.根据ftp表的信息初始化sftp对象" +
					"5.根据下级目录类型定义ftp拉取或者推送的下级目录" +
					"6.判断是推送还是拉取，根据不同的模式建立目录，并推送或拉取文件" +
					"7.判断实时读取间隔时间为0或为空时为防止循环死读，默认线程休眠1秒" +
					"8.任务结束，根据当前任务id移除线程")
	@Return(desc = "作业执行信息对象", range = "不能为空")
	@Override
	public JobStatusInfo runJob() {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		//1.获取ftp_id根据ftp_id判断任务是否是重复发送，实时的仍然在继续运行，是则中断上一个实时线程
		String ftpId = ftp_collect.getFtp_id().toString();
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
		ftpDir = FileNameUtils.normalize(ftpDir, true);
		localPath = FileNameUtils.normalize(localPath, true);
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
					if (IsFlag.Shi.getCode().equals(ftp_collect.getFtp_model())) {
						//推模式，获取远程目录下文件夹流水号
						ftpFolderName = remoteNumberDir(ftpDir, sftp);
					} else {
						//拉模式，获取本地目录下文件夹流水号
						ftpFolderName = localNumberDir(localPath);
					}
				} else if (ftpRulePath.equals(FtpRule.GuDingMuLu.getCode())) {
					ftpFolderName = ftp_collect.getChild_file_path();
				} else if (ftpRulePath.equals(FtpRule.AnShiJian.getCode())) {
					ftpFolderName = getDateDir(ftp_collect.getChild_time());
				} else {
					throw new BusinessException("FTP rule 不存在：" + ftpRulePath);
				}
				//6.判断是推送还是拉取，根据不同的模式建立目录，并推送或拉取文件
				//根据ftpId获取MapDB操作类的对象
				try (MapDBHelper mapDBHelper = new MapDBHelper(USER_DIR + File.separator + ftpId,
						ftpId + ".db")) {
					ConcurrentMap<String, String> fileNameHTreeMap = mapDBHelper.htMap(ftpId, 25 * 12);
					if (IsFlag.Shi.getCode().equals(ftp_collect.getFtp_model())) {
						String currentFTPDir;
						if (ftpDir.endsWith("/")) {
							currentFTPDir = ftpDir + ftpFolderName;
						} else {
							currentFTPDir = ftpDir + "/" + ftpFolderName;
						}
						transferPut(currentFTPDir, localPath, sftp, fileSuffix, mapDBHelper, fileNameHTreeMap);
					} else {
						String currentLoadDir;
						if (localPath.endsWith("/")) {
							currentLoadDir = localPath + ftpFolderName;
						} else {
							currentLoadDir = localPath + "/" + ftpFolderName;
						}
						transferGet(ftpDir, currentLoadDir, sftp, ftp_collect.getIs_unzip(),
								ftp_collect.getReduce_type(), fileSuffix, mapDBHelper, fileNameHTreeMap);
					}
				} catch (Exception e) {
					log.error("创建或打开mapDB文件失败，ftp传输失败", e);
					throw new BusinessException("创建或打开mapDB文件失败，ftp传输失败");
				}
			} catch (Exception e) {
				log.error("FTP传输失败！！！", e);
				//TODO 运行失败需要给什么值，需要讨论
				jobStatus.setRunStatus(-1);
				//异常退出
				break;
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

	@Method(desc = "验证目录是否存在，不存在则创建目录",
			logicStep = "1.判断文件是否存在，存在返回true" +
					"2.不存在，创建目录并返回")
	@Param(name = "filePath", desc = "需要验证的路径", range = "不能为空")
	@Return(desc = "目录是否存在的返回值，true表示存在", range = "不会为空")
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

	@Method(desc = "下级目录规则如果是采取按时间，则根据用户定义的时间区间来建立目录",
			logicStep = "1.获取当前服务器的日期时间" +
					"2.根据时间的精确度来截取时间")
	@Param(name = "childTime", desc = "按时间建立的下级文件夹类型", range = "不能为空")
	@Return(desc = "需要创建的时间文件夹名称", range = "不会为空")
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

	@Method(desc = "将远程目录下的指定后缀名下的文件ftp拉取到本地的机器目录",
			logicStep = "1.验证本地需要传输的目录文件是否存在，不存在则创建" +
					"2.根据文件后缀拉取远程目录下的文件" +
					"3.遍历拉取到的远程文件对象" +
					"4.判断是文件夹，递归调用本方法" +
					"5.不是文件夹，判断文件有没有被拉取过，没有拉取过则调用sftp方法拉取文件" +
					"6.判断是否需要解压，需要解压则根据对应的压缩方式将文件解压到本地" +
					"7.将拉取成功的文件放到MapDB，提交mapDB")
	@Param(name = "ftpDir", desc = "待传输的文件所在远程机器的目录", range = "不能为空")
	@Param(name = "destDir", desc = "需要拉取到的本地目录", range = "不能为空")
	@Param(name = "sftp", desc = "sftp操作类", range = "不能为空")
	@Param(name = "isUnzip", desc = "是否需要解压缩", range = "不能为空")
	@Param(name = "deCompressWay", desc = "解压缩的方式", range = "可以为空")
	@Param(name = "fileSuffix", desc = "文件后缀名", range = "可以为空")
	@Param(name = "mapDBHelper", desc = "mapDB数据库操作类", range = "不可为空")
	@Param(name = "fileNameHTreeMap", desc = "mapDB数据库表的操作类", range = "不可为空")
	private void transferGet(String ftpDir, String destDir, SftpOperate sftp, String isUnzip, String deCompressWay,
	                         String fileSuffix, MapDBHelper mapDBHelper, ConcurrentMap<String, String> fileNameHTreeMap) {
		//1.验证本地需要传输的目录文件是否存在，不存在则创建
		boolean flag = validateDirectory(destDir);
		if (!flag) {
			throw new BusinessException("创建文件夹失败");
		}
		try {
			Vector<LsEntry> listDir;
			//2.根据文件后缀拉取远程目录下的文件
			if (StringUtil.isEmpty(fileSuffix)) {
				//目录下文件全部获取
				listDir = sftp.listDir(ftpDir);
			} else {
				//以fileSuffix为后缀的文件才获取回来
				listDir = sftp.listDir(ftpDir, "*." + fileSuffix);
			}
			//3.遍历拉取到的远程文件对象
			for (LsEntry lsEntry : listDir) {
				String tmpDestDir;
				String tmpFtpDir;
				if (ftpDir.endsWith("/")) {
					tmpFtpDir = ftpDir + lsEntry.getFilename();
				} else {
					tmpFtpDir = ftpDir + "/" + lsEntry.getFilename();
				}
				if (destDir.endsWith("/")) {
					tmpDestDir = destDir + lsEntry.getFilename();
				} else {
					tmpDestDir = destDir + "/" + lsEntry.getFilename();
				}
				//4.判断是文件夹，递归调用本方法
				if (lsEntry.getAttrs().isDir()) {
					transferGet(tmpFtpDir, tmpDestDir, sftp, isUnzip, deCompressWay, fileSuffix,
							mapDBHelper, fileNameHTreeMap);
				} else {
					//5.不是文件夹，判断文件有没有被拉取过，没有拉取过则调用sftp方法拉取文件
					if (!fileNameHTreeMap.containsKey(tmpFtpDir)
							|| (fileNameHTreeMap.containsKey(tmpFtpDir)
							&& !fileNameHTreeMap.get(tmpFtpDir)
							.equals(lsEntry.getAttrs().getMtimeString()))) {
						sftp.transferFile(tmpFtpDir, destDir);
						boolean isSuccessful;
						//6.判断是否需要解压，需要解压则根据对应的压缩方式将文件解压到本地
						if (IsFlag.Shi.getCode().equals(isUnzip)) {
							//将文件根据对应的解压缩方式进行解压
							isSuccessful = DeCompressionUtil.deCompression(tmpDestDir, deCompressWay);
							File file = new File(tmpDestDir);
							if (file.exists()) {
								if (!file.delete()) {
									throw new BusinessException("删除文件失败");
								}
							}
						} else {
							isSuccessful = true;
						}
						//7.将拉取成功的文件放到MapDB，提交mapDB
						if (isSuccessful) {
							fileNameHTreeMap.put(tmpFtpDir, lsEntry.getAttrs().getMtimeString());
							mapDBHelper.commit();
						} else {
							throw new BusinessException("解压文件失败！！！");
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("FTP传输失败！！！", e);
			throw new BusinessException("ftp传输失败！");
		}
	}

	@Method(desc = "将本地目录下的指定后缀名下的文件ftp推送到远程的机器目录",
			logicStep = "1.创建远程需要ftp的目录" +
					"2.根据mapDB的记录和该目录下文件属性过滤文件" +
					"3.判断是文件还是文件夹" +
					"4.文件夹则将此目录作为ftp目录递归调用本方法" +
					"5.是文件则调用sftp，推送文件到远程服务器，存到mapDB，提交mapDB")
	@Param(name = "ftpDir", desc = "ftp推送的远程机器的目录", range = "不能为空")
	@Param(name = "localPath", desc = "本地目录", range = "不能为空")
	@Param(name = "sftp", desc = "sftp操作类", range = "不能为空")
	@Param(name = "fileSuffix", desc = "文件后缀名", range = "可以为空")
	@Param(name = "mapDBHelper", desc = "mapDB数据库操作类", range = "不可为空")
	@Param(name = "fileNameHTreeMap", desc = "mapDB数据库表的操作类", range = "不可为空")
	private void transferPut(String ftpDir, String localPath, SftpOperate sftp, String fileSuffix,
	                         MapDBHelper mapDBHelper, ConcurrentMap<String, String> fileNameHTreeMap) {
		//数据可访问权限处理方式：此方法不需要对数据可访问权限处理
		try {
			//1.创建远程需要ftp的目录
			sftp.scpMkdir(ftpDir);
			//2.根据mapDB的记录和该目录下文件属性过滤文件
			File[] files = new File(localPath).listFiles((file) -> (!fileNameHTreeMap.containsKey(
					file.getAbsolutePath()) || file.isDirectory() || (fileNameHTreeMap.containsKey(
					file.getAbsolutePath()) && !fileNameHTreeMap.get(file.getAbsolutePath()).
					equals(String.valueOf(file.lastModified())))));
			if (files != null && files.length > 0) {
				for (File file : files) {
					String fileName = file.getName();
					String tmpFtpDir;
					if (ftpDir.endsWith("/")) {
						tmpFtpDir = ftpDir + fileName;
					} else {
						tmpFtpDir = ftpDir + "/" + fileName;
					}
					//3.判断是文件还是文件夹
					if (file.isDirectory()) {
						//4.文件夹则将此目录作为ftp目录递归调用本方法
						transferPut(tmpFtpDir, file.getAbsolutePath(), sftp, fileSuffix, mapDBHelper,
								fileNameHTreeMap); // 获取文件绝对路径
					} else {
						//5.是文件则调用sftp，推送文件到远程服务器，存到mapDB，提交mapDB
						if (StringUtil.isBlank(fileSuffix) || fileName.endsWith(fileSuffix)) {
							sftp.transferPutFile(file.getAbsolutePath(), tmpFtpDir);
							fileNameHTreeMap.put(file.getAbsolutePath(), String.valueOf(file.lastModified()));
							mapDBHelper.commit();
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("FTP传输失败！！！", e);
			throw new BusinessException("ftp传输失败！");
		}
	}

	@Method(desc = "获取远程目录下数字文件夹，取数字最大的文件夹加一",
			logicStep = "1.取远程文件夹下的所有文件夹的集合" +
					"2.遍历集合，取为数字的文件夹的最大值" +
					"3.取不到则返回0" +
					"4.取到则返回最大文件夹数字加1")
	@Param(name = "dir", desc = "文件夹路径", range = "不能为空")
	@Param(name = "sftp", desc = "远程操作类", range = "远程操作类")
	@Return(desc = "该文件夹下数值最大的文件夹加一", range = "不会为空")
	private String remoteNumberDir(String dir, SftpOperate sftp) throws SftpException {
		//1.取远程文件夹下的所有文件夹的集合
		Vector<LsEntry> listDir = sftp.listDir(dir);
		int max = -1;
		//2.遍历集合，取为数字的文件夹的最大值
		for (LsEntry lsEntry : listDir) {
			String filename = lsEntry.getFilename();
			if (lsEntry.getAttrs().isDir() && NumberUtil.isNumberic(filename)) {
				//取最大数字的文件夹
				int parseInt = Integer.parseInt(filename);
				if (parseInt > max) {
					max = parseInt;
				}
			}
		}
		//3.取不到则从零开始
		if (max == -1) {
			return "0";
		}
		//4.取到则返回最大文件夹数字加1
		return String.valueOf(max + 1);
	}

	@Method(desc = "获取目录下数字文件夹，取数字最大的文件夹加一",
			logicStep = "1.取当前文件夹下的所有为数字的文件夹的集合" +
					"2.判断集合是否为空，为空则返回字符串0" +
					"3.不为空则遍历集合，取最大值" +
					"4.返回最大值加一")
	@Param(name = "dir", desc = "文件夹路径", range = "不能为空")
	@Return(desc = "该文件夹下数值最大的文件夹加一", range = "不会为空")
	private String localNumberDir(String dir) {

		File pmFile = new File(dir);
		//1.取当前文件夹下的所有为数字的文件夹的集合
		File[] listFiles = pmFile.listFiles((file) -> NumberUtil.isNumberic(file.getName()) && file.isDirectory());
		//2.判断集合是否为空，为空则返回字符串0
		if (listFiles == null || listFiles.length == 0) {
			return "0";
		}
		int max = 0;
		//3.不为空则遍历集合，取最大值
		for (File file : listFiles) {
			String name = file.getName();
			// 取最大数字的文件夹
			int parseInt = Integer.parseInt(name);
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
