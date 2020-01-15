package hrds.commons.hadoop.hadoop_helper;

import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.readconfig.HDFSFileSystem;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class HdfsOperator implements Closeable {

	private static final Log logger = LogFactory.getLog(HdfsOperator.class);

	private FileSystem fs;

	private HDFSFileSystem hdfsFileSystem;

	public HdfsOperator() throws IOException {
		this(null);
	}

	public HdfsOperator(String configPath) throws IOException {
		this(configPath, ConfigReader.PlatformType.normal.toString(), null);
	}

	public HdfsOperator(String configPath, String platform, String hadoop_user_name) throws IOException {
		hdfsFileSystem = new HDFSFileSystem(configPath, platform, hadoop_user_name);
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
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public boolean exists(String path) throws IllegalArgumentException, IOException {

		return exists(new Path(path));
	}

	public boolean exists(Path path) throws IOException {

		return fs.exists(path);
	}

	/**
	 * 上传文件到hdfs
	 *
	 * @param srcPath   本地路径
	 * @param hdfsPath  hdfs路径
	 * @param overWrite 是否覆盖已经存在的文件
	 * @return 是否成功
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public boolean upLoad(Path srcPath, Path hdfsPath, boolean overWrite) throws IllegalArgumentException, IOException {

		if (!overWrite) {
			if (exists(hdfsPath)) {
				logger.debug(hdfsPath + "is already exsit!");
				return false;
			}
		}
		fs.copyFromLocalFile(srcPath, hdfsPath);
		return true;

	}

	public boolean upLoad(String srcPath, String hdfsPath, boolean overWrite) throws IllegalArgumentException, IOException {

		return upLoad(new Path(srcPath), new Path(hdfsPath), overWrite);
	}

	/**
	 * 递归删除hdfs上的目录
	 *
	 * @param path 要删除的目录
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @author yuanqi E-mail: 384077767@qq.com
	 * @date 创建时间：2019年4月22日 下午9:43:14
	 * @since jdk 1.8
	 */
	public boolean deletePath(String path) throws IllegalArgumentException, IOException {

		return deletePath(path, true);
	}

	/**
	 * 删除hdfs上的目录
	 *
	 * @param path      要删除的目录
	 * @param recursive 是否递归删除
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @author yuanqi E-mail: 384077767@qq.com
	 * @date 创建时间：2019年4月22日 下午9:43:14
	 * @since jdk 1.8
	 */
	public boolean deletePath(String path, boolean recursive) throws IllegalArgumentException, IOException {

		return deletePath(new Path(path), recursive);
	}

	/**
	 * 删除hdfs上的目录
	 *
	 * @param path      要删除的目录
	 * @param recursive 是否递归删除
	 * @return
	 * @throws IOException
	 * @author yuanqi E-mail: 384077767@qq.com
	 * @date 创建时间：2019年4月22日 下午9:43:14
	 * @since jdk 1.8
	 */
	public boolean deletePath(Path path, boolean recursive) throws IOException {

		return fs.delete(path, recursive);
	}

	/**
	 * 从hdfs下载数据到本地
	 *
	 * @param hdfsPath    hdfs路径
	 * @param srcFileName 本地路径
	 * @return true false
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void downFromCloud(String hdfsPath, String srcFileName) throws IllegalArgumentException, IOException {

		try (InputStream HDFS_IN = fs.open(new Path(hdfsPath)); OutputStream OutToLOCAL = new FileOutputStream(srcFileName)) {
			IOUtils.copyBytes(HDFS_IN, OutToLOCAL, 1024, true);
		}
	}

	/**
	 * 从hdfs下载数据到本地
	 *
	 * @param hdfsPath    hdfs路径
	 * @param srcFileName 本地路径
	 * @return true false
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void fromHdfsToLocal(String hdfsPath, String srcFileName) throws IllegalArgumentException, IOException {

		fromHdfsToLocal(new Path(hdfsPath), new Path(hdfsPath));
	}

	public void fromHdfsToLocal(Path hdfsPath, Path srcFileName) throws IOException {

		fs.copyToLocalFile(hdfsPath, hdfsPath);
	}

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
		for (int i = 0; i < srcFilePath.length; i++) {
			String srcFile = srcFilePath[i].toString();
			String fileName = FilenameUtils.getName(srcFile);
			download(srcPath + '/' + fileName, dstPath + '/' + folderName + '/' + fileName);
		}
	}

	public void download(String srcPath, String dstPath) throws IllegalArgumentException, IOException, Exception {

		if (fs.isFile(new Path(srcPath))) {
			downFromCloud(srcPath, dstPath);
		} else {
			downloadFolder(srcPath, dstPath);
		}
	}

	/**
	 * 复制 hdfs文件到另一个目录
	 *
	 * @param srcPath
	 * @param destPath
	 * @param overWrite 是否覆盖原文件
	 * @return true false
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public boolean copy(String srcPath, String destPath, boolean overWrite) throws IllegalArgumentException, IOException {

		return FileUtil.copy(fs, new Path(srcPath), fs, new Path(destPath), false, overWrite, getConfiguration());
	}

	/**
	 * 移动 hdfs文件到另一个目录
	 *
	 * @param srcPath
	 * @param destPath
	 * @param overWrite 是否覆盖原文件
	 * @return true false
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public boolean move(String srcPath, String destPath, boolean overWrite) throws IllegalArgumentException, IOException {

		return FileUtil.copy(fs, new Path(srcPath), fs, new Path(destPath), true, overWrite, fs.getConf());
	}

	public List<Path> listFiles(String path, boolean isContainDir) throws IOException {

		return listFiles(new Path(path), isContainDir);
	}

	/**
	 * 列出当前目录下的内容
	 *
	 * @param path         查看目录
	 * @param isContainDir 是否返回文件夹
	 * @return List<Path>
	 * @throws IOException
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

	public long getDirectoryCount(Path directory) throws IOException {

		return fs.getContentSummary(directory).getDirectoryCount();
	}

	public long getDirectoryLength(Path directory) throws IOException {

		return fs.getContentSummary(directory).getLength();
	}

	public long getDirectoryCount(String directory) throws IOException {

		return getDirectoryCount(new Path(directory));
	}

	public long getDirectoryLength(String directory) throws IOException {

		return getDirectoryLength(new Path(directory));
	}

	public void copyMerge(Path srcDir, Path dstFile, boolean deleteSource) throws IOException {
		FileUtil.copy(fs, srcDir, fs, dstFile, deleteSource, this.getConfiguration());
	}

	public boolean isDirectory(Path path) throws IOException {

		return fs.isDirectory(path);
	}

	public boolean isDirectory(String path) throws IOException {

		return isDirectory(new Path(path));
	}

	public boolean isFile(Path path) throws IOException {

		return fs.isFile(path);
	}

	public boolean isFile(String path) throws IOException {

		return isFile(new Path(path));
	}

	public FSDataInputStream open(Path path) throws IOException {

		return fs.open(path);
	}

	public FSDataInputStream open(String path) throws IOException {
		return open(new Path(path));
	}

	public FSDataOutputStream create(Path path) throws IOException {

		return fs.create(path);
	}

	public static final int defaultBufferedSize = 8192;
	public static final Charset defaultCharset = Charset.forName("UTF-8");

	public BufferedReader toBufferedReader(Path path) throws IOException {

		return toBufferedReader(path, defaultCharset, defaultBufferedSize);
	}

	public BufferedReader toBufferedReader(Path path, Charset charset, int bufferSize) throws IOException {

		return new BufferedReader(new InputStreamReader(open(path), charset), bufferSize);
	}

	public void ensureDirectory(Path path) throws IOException {

		if (!exists(path)) {
			mkdir(path);
		}
	}

	public void ensureDirectory(String path) throws IOException {

		ensureDirectory(new Path(path));
	}

	public static void main(String[] args) {

		System.setProperty("HADOOP_USER_NAME", PropertyParaValue.getString("HADOOP_USER_NAME", "hyshf"));
		String hdfsPath = "/hrds/hrds_G01/WEB-INF/src/log4j.properties";
		try (HdfsOperator operator = new HdfsOperator()) {
			System.out.println(operator.getDirectoryCount(hdfsPath));
			System.out.println(operator.getDirectoryLength(hdfsPath));
		} catch (Exception e) {
			logger.error("写hdfs文件并下载到本地失败:", e);
		}
	}
}
