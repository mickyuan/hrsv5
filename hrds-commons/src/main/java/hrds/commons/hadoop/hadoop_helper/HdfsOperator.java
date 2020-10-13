package hrds.commons.hadoop.hadoop_helper;

import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.readconfig.HDFSFileSystem;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * HDFS 操作类
 */
public class HdfsOperator implements Closeable {

	private static final Logger logger = LogManager.getLogger();
	public static final int defaultBufferedSize = 8192;
	public static final Charset defaultCharset = StandardCharsets.UTF_8;
	private FileSystem fs;

	private HDFSFileSystem hdfsFileSystem;

	public HdfsOperator() throws IOException {
		this(null);
	}

	public HdfsOperator(String configPath) throws IOException {
		this(configPath, null);
	}

	public HdfsOperator(String configPath, String platform) throws IOException {
		this(configPath, platform, null);
	}

	public HdfsOperator(String configPath, String platform,
	                    String prncipal_name) throws IOException {
		this(configPath, platform, prncipal_name, null);
	}

	public HdfsOperator(String configPath, String platform,
	                    String prncipal_name, String hadoop_user_name) throws IOException {
		hdfsFileSystem = new HDFSFileSystem(configPath, platform, prncipal_name, hadoop_user_name);
		fs = hdfsFileSystem.getFileSystem();
	}

	public Configuration getConfiguration() {
		return hdfsFileSystem.getConfig();
	}

	public FileSystem getFileSystem() {
		return fs;
	}

	public HDFSFileSystem getHDFSFileSystem() {
		return hdfsFileSystem;
	}

	public Path getWorkingDirectory(String directory) {
		return hdfsFileSystem.getWorkingDirectory(directory);
	}

	public boolean mkdir(Path path) throws IOException {
		boolean isok = fs.mkdirs(path);
		if (isok) {
			logger.debug("create " + path + " ok!");
		} else {
			logger.debug("create " + path + " failure");
		}
		return isok;

	}

	public boolean mkdir(String path) throws IllegalArgumentException, IOException {
		return mkdir(new Path(path));
	}

	public boolean renamedir(Path oldpath, Path newpath) throws IOException {
		boolean isok = fs.rename(oldpath, newpath);
		if (isok) {
			logger.debug("modify " + oldpath + "-->" + newpath + " ok!");
		} else {
			logger.debug("modify " + oldpath + "-->" + newpath + " failure!");
		}
		return isok;
	}

	public boolean renamedir(String oldpath, String newpath) throws IOException {
		return renamedir(new Path(oldpath), new Path(newpath));
	}

	@Override
	public void close() {
		try {
			if (fs != null)
				fs.close();
			if (hdfsFileSystem != null)
				hdfsFileSystem.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 判断hdfs上的路径是不是存在
	 *
	 * @param path 路径
	 * @return true 存在 false 不存在
	 * @throws IOException IO异常
	 */
	public boolean exists(String path) throws IOException {
		return exists(new Path(path));
	}

	public boolean exists(Path path) throws IOException {
		return fs.exists(path);
	}

	/**
	 * 上传文件到hdfs
	 *
	 * @param srcPath   本地路径 String
	 * @param hdfsPath  hdfs路径 String
	 * @param overWrite 是否覆盖已经存在的文件
	 * @return 是否成功
	 * @throws IOException IO异常
	 */
	public boolean upLoad(String srcPath, String hdfsPath, boolean overWrite) throws IOException {
		return upLoad(new Path(srcPath), new Path(hdfsPath), overWrite);
	}

	/**
	 * 上传文件到hdfs
	 *
	 * @param srcPath   本地路径 Path
	 * @param hdfsPath  hdfs路径 Path
	 * @param overWrite 是否覆盖已经存在的文件
	 * @return 是否成功
	 * @throws IOException IO异常
	 */
	public boolean upLoad(Path srcPath, Path hdfsPath, boolean overWrite) throws IOException {

		if (!overWrite) {
			if (exists(hdfsPath)) {
				logger.debug(hdfsPath + "is already exsit!");
				return false;
			}
		}
		fs.copyFromLocalFile(srcPath, hdfsPath);
		return true;

	}

	/**
	 * 递归删除hdfs上的目录
	 *
	 * @param path 要删除的目录
	 * @throws IOException IO异常
	 */
	public boolean deletePath(String path) throws IllegalArgumentException, IOException {
		return deletePath(path, true);
	}

	/**
	 * 删除hdfs上的目录
	 *
	 * @param path      要删除的目录 String
	 * @param recursive 是否递归删除
	 * @throws IOException IO异常
	 */
	public boolean deletePath(String path, boolean recursive) throws IOException {
		return deletePath(new Path(path), recursive);
	}

	/**
	 * 删除hdfs上的目录
	 *
	 * @param path      要删除的目录 Path
	 * @param recursive 是否递归删除
	 * @throws IOException IO异常
	 */
	public boolean deletePath(Path path, boolean recursive) throws IOException {
		return fs.delete(path, recursive);
	}

	/**
	 * 从hdfs下载数据到本地
	 *
	 * @param hdfsPath    hdfs路径
	 * @param srcFileName 本地路径
	 * @throws IOException IO异常
	 */
	public void fromHdfsToLocal(String hdfsPath, String srcFileName) throws IOException {
		fromHdfsToLocal(new Path(hdfsPath), new Path(hdfsPath));
	}

	/**
	 * 从hdfs下载数据到本地
	 *
	 * @param hdfsPath    hdfs路径 Path
	 * @param srcFileName 本地路径  Path
	 * @throws IOException IO异常
	 */
	public void fromHdfsToLocal(Path hdfsPath, Path srcFileName) throws IOException {
		fs.copyToLocalFile(hdfsPath, hdfsPath);
	}

	/**
	 * @param srcPath hdfs路径 String
	 * @param dstPath 目标路径  String
	 * @throws Exception Exception
	 */
	public void download(String srcPath, String dstPath) throws Exception {
		if (fs.isFile(new Path(srcPath))) {
			downFromCloud(srcPath, dstPath);
		} else {
			downloadFolder(srcPath, dstPath);
		}
	}

	/**
	 * 从hdfs下载数据到本地
	 *
	 * @param hdfsPath    hdfs路径
	 * @param srcFileName 本地路径
	 * @throws IOException IO异常
	 */
	public void downFromCloud(String hdfsPath, String srcFileName) throws IOException {
		try (InputStream HDFS_IN = fs.open(new Path(hdfsPath)); OutputStream OutToLOCAL = new FileOutputStream(srcFileName)) {
			IOUtils.copyBytes(HDFS_IN, OutToLOCAL, 1024, true);
		}
	}

	/**
	 * @param srcPath 需要下载的目录
	 * @param dstPath 保存的本地目录
	 * @throws Exception Exception
	 */
	public void downloadFolder(String srcPath, String dstPath) throws Exception {
		logger.info("下载 " + srcPath + " 到本地目录 " + dstPath + " 下");
		String folderName = FilenameUtils.getName(srcPath);
		File dstDir = new File(dstPath + File.separator + folderName);
		if (!dstDir.exists()) {
			if (!dstDir.mkdirs()) {
				throw new BusinessException("创建目录" + dstDir.getCanonicalPath() + "失败！");
			}
		}
		FileStatus[] srcFileStatus = fs.listStatus(new Path(srcPath));
		Path[] srcFilePath = FileUtil.stat2Paths(srcFileStatus);
		for (Path path : srcFilePath) {
			String srcFile = path.toString();
			String fileName = FilenameUtils.getName(srcFile);
			download(srcPath + '/' + fileName, dstPath + '/' + folderName + '/' + fileName);
		}
	}

	/**
	 * 复制 hdfs文件到另一个目录
	 *
	 * @param srcPath   hdfs路径 String
	 * @param destPath  目标路径  String
	 * @param overWrite 是否覆盖原文件
	 * @return true false
	 * @throws IOException IO异常
	 */
	public boolean copy(String srcPath, String destPath, boolean overWrite) throws IOException {

		return FileUtil.copy(fs, new Path(srcPath), fs, new Path(destPath), false, overWrite, getConfiguration());
	}

	/**
	 * 移动 hdfs文件到另一个目录
	 *
	 * @param srcPath   hdfs路径 String
	 * @param destPath  目标路径  String
	 * @param overWrite 是否覆盖原文件
	 * @return true false
	 * @throws IOException IO异常
	 */
	public boolean move(String srcPath, String destPath, boolean overWrite) throws IllegalArgumentException, IOException {

		return FileUtil.copy(fs, new Path(srcPath), fs, new Path(destPath), true, overWrite, fs.getConf());
	}

	/**
	 * @param path         目录 String
	 * @param isContainDir 是否包含目录 boolean
	 * @return 文件路径的list List
	 * @throws IOException IOException
	 */
	public List<Path> listFiles(String path, boolean isContainDir) throws IOException {

		return listFiles(new Path(path), isContainDir);
	}

	/**
	 * 列出当前目录下的内容
	 *
	 * @param path         查看目录
	 * @param isContainDir 是否返回文件夹
	 * @return List<Path>
	 * @throws IOException IO异常
	 */
	public List<Path> listFiles(Path path, boolean isContainDir) throws IOException {

		List<Path> pathList = new ArrayList<>();
		if (!fs.exists(path)) {
			logger.info(path + " does not exsit...");
			return null;
		}
		if (!fs.isDirectory(path)) {
			logger.info(path + " is not a directory, can not be listed...");
			return null;
		}
		FileStatus[] status = fs.listStatus(path);
		Path p;
		for (FileStatus fileStatus : status) {
			p = fileStatus.getPath();
			if (isContainDir) {
				pathList.add(p);
			} else {
				if (!fs.isDirectory(p)) {
					pathList.add(p);
				}
			}
		}
		return pathList;
	}

	/**
	 * 清空文件目录
	 *
	 * @param path Path
	 * @throws IOException IOException
	 */
	public boolean emptyFolder(Path path) throws IOException {
		if (exists(path.toString()) && fs.isDirectory(path)) {
			List<Path> list = listFiles(path.toString(), true);
			for (Path path2 : list) {
				deletePath(path2.toString());
			}
			return true;
		}
		return false;

	}

	/**
	 * 目录下文件统计数
	 *
	 * @param directory 文件目录 String
	 * @return 目录下文件统计数
	 * @throws IOException IOException
	 */
	public long getDirectoryCount(String directory) throws IOException {

		return getDirectoryCount(new Path(directory));
	}

	/**
	 * 目录下文件统计数
	 *
	 * @param directory 文件目录 Path
	 * @return 目录下文件统计数
	 * @throws IOException IOException
	 */
	public long getDirectoryCount(Path directory) throws IOException {

		return fs.getContentSummary(directory).getDirectoryCount();
	}

	/**
	 * 返回文件目录路径长度
	 *
	 * @param directory 文件目录 String
	 * @return 目录路径长度
	 */
	public long getDirectoryLength(String directory) throws IOException {
		return getDirectoryLength(new Path(directory));
	}

	/**
	 * 返回文件目录路径长度
	 *
	 * @param directory 文件目录 Path
	 */
	public long getDirectoryLength(Path directory) throws IOException {
		return fs.getContentSummary(directory).getLength();
	}


	/**
	 * Copy files between FileSystems.
	 *
	 * @param srcDir       源
	 * @param dstFile      目标
	 * @param deleteSource 是否删除源
	 * @throws IOException IOException
	 */
	public void copyMerge(Path srcDir, Path dstFile, boolean deleteSource) throws IOException {
		FileUtil.copy(fs, srcDir, fs, dstFile, deleteSource, this.getConfiguration());
	}

	/**
	 * 判断是否是文件目录
	 *
	 * @param path 文件路径 String
	 * @return boolean
	 * @throws IOException IOException
	 */
	public boolean isDirectory(String path) throws IOException {
		return isDirectory(new Path(path));
	}

	/**
	 * 判断是否是文件目录
	 *
	 * @param path 文件路径 Path
	 * @return boolean
	 * @throws IOException IOException
	 */
	public boolean isDirectory(Path path) throws IOException {
		return fs.isDirectory(path);
	}

	/**
	 * 判断是否是文件
	 *
	 * @param path 文件路径 String
	 * @return boolean
	 * @throws IOException IOException
	 */
	public boolean isFile(String path) throws IOException {
		return isFile(new Path(path));
	}

	/**
	 * 判断是否是文件
	 *
	 * @param path 文件路径 Path
	 * @return boolean
	 * @throws IOException IOException
	 */
	public boolean isFile(Path path) throws IOException {
		return fs.isFile(path);
	}

	/**
	 * 打开文件
	 *
	 * @param path 文件路径 String
	 * @return FSDataInputStream
	 * @throws IOException IOException
	 */
	public FSDataInputStream open(String path) throws IOException {
		return open(new Path(path));
	}

	/**
	 * 打开文件
	 *
	 * @param path 文件路径 Path
	 * @return FSDataInputStream
	 * @throws IOException IOException
	 */
	public FSDataInputStream open(Path path) throws IOException {

		return fs.open(path);
	}

	/**
	 * 创建文件
	 *
	 * @param path 文件路径 String
	 * @return FSDataOutputStream
	 * @throws IOException IOException
	 */
	public FSDataOutputStream create(String path) throws IOException {
		return fs.create(new Path(path));
	}

	/**
	 * 创建文件
	 *
	 * @param path 文件路径 Path
	 * @return FSDataOutputStream
	 * @throws IOException IOException
	 */
	public FSDataOutputStream create(Path path) throws IOException {
		return fs.create(path);
	}

	/**
	 * BufferedReader 读取文件
	 *
	 * @param path 文件路径 Path
	 * @return BufferedReader
	 * @throws IOException IOException
	 */
	public BufferedReader toBufferedReader(Path path) throws IOException {
		return toBufferedReader(path, defaultCharset, defaultBufferedSize);
	}

	/**
	 * BufferedReader 读取文件
	 *
	 * @param path       文件路径 Path
	 * @param charset    Charset
	 * @param bufferSize bufferSize
	 * @return BufferedReader
	 * @throws IOException IOException
	 */
	public BufferedReader toBufferedReader(Path path, Charset charset, int bufferSize) throws IOException {

		return new BufferedReader(new InputStreamReader(open(path), charset), bufferSize);
	}

	/**
	 * 确保目录存在,如果不存在则创建
	 *
	 * @param path 目录 Stirng
	 * @throws IOException IOException
	 */
	public void ensureDirectory(String path) throws IOException {
		ensureDirectory(new Path(path));
	}

	/**
	 * 确保目录存在,如果不存在则创建
	 *
	 * @param path 目录 Path
	 * @throws IOException IOException
	 */
	public void ensureDirectory(Path path) throws IOException {
		if (!exists(path)) {
			mkdir(path);
		}
	}
}
