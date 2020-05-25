package hrds.agent.job.biz.utils;

import hrds.commons.exception.AppSystemException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

	public static final String ENCODING = "UTF-8";
	private static List<String> pathList = new ArrayList<String>();

	static {
		pathList.add("/");
		pathList.add("/bin");
		pathList.add("/boot");
		pathList.add("/dev");
		pathList.add("/etc");
		pathList.add("/home");
		pathList.add("/lib");
		pathList.add("/lib64");
		pathList.add("/media");
		pathList.add("/mnt");
		pathList.add("/opt");
		pathList.add("/proc");
		pathList.add("/root");
		pathList.add("/run");
		pathList.add("/sbin");
		pathList.add("/srv");
		pathList.add("/sys");
		pathList.add("/usr");
		pathList.add("/var");
	}

	private FileUtil() {
	}

	/**
	 * 用于在文件系统创建文件
	 *
	 * @param filePath 文件路径
	 * @param context  文件内容，可为空字符
	 * @return boolean    是否创建成功
	 * @author 13616
	 * @date 2019/7/31 9:52
	 */
	public static boolean createFile(String filePath, String context) {

		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				throw new AppSystemException("创建" + filePath + "父类文件夹失败");
			}
		}

		try {
			FileUtils.write(file, context, ENCODING);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage());
		}

		return true;
	}

	/**
	 * 用于在文件系统创建目录
	 *
	 * @param dirPath 目录地址
	 * @return boolean    目录是否创建成功
	 * @author 13616
	 * @date 2019/7/31 9:55
	 */
	public static boolean createDir(String dirPath) {

		File file = new File(dirPath);

		if (!file.exists()) {
			return file.mkdirs();
		}

		return true;
	}

	/**
	 * 检查目录是否有自己的可读、可写、可执行权限
	 *
	 * @param dirPath 目录地址
	 * @return boolean    是否有三种权限
	 * @author 13616
	 * @date 2019/7/31 9:57
	 */
	public static boolean checkDirWithAllAuth(String dirPath) {

		File file = new File(dirPath);
		if (!file.getParentFile().canRead() || !file.getParentFile().canWrite() ||
				!file.getParentFile().canExecute()) {
			return false;
		}

		return true;
	}

	/**
	 * 根据文件后缀获取某个目录下的所有文件
	 *
	 * @param dirPath    目录地址
	 * @param fileSuffix 文件后缀
	 * @return java.util.List<java.io.File>	文件List集合
	 * @author 13616
	 * @date 2019/7/31 9:58
	 */
	public static List<File> getAllFilesByFileSuffix(String dirPath, String fileSuffix) {

		File file_root = new File(dirPath);
		List<File> file_result = new ArrayList<>();
		File[] files = file_root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					List<File> deep_files = FileUtil.getAllFilesByFileSuffix(pathname.getAbsolutePath(),
							fileSuffix);
					file_result.addAll(deep_files);
				} else if (pathname.isFile() && pathname.getName().endsWith(fileSuffix)) {
					return true;
				}
				return false;
			}
		});

		file_result.addAll(Arrays.asList(files));
		return file_result;
	}

	/**
	 * 将文件内容读为字符串，编码为UTF-8
	 *
	 * @param file File对象
	 * @return java.lang.String    文件内容
	 * @author 13616
	 * @date 2019/7/31 10:01
	 */
	public static String readFile2String(File file) {

		try {
			if (file.exists() && file.isFile()) {
				return FileUtils.readFileToString(file, ENCODING);
			} else {
				throw new IllegalArgumentException(file.getName() + "：不是一个可读的文件");
			}
		} catch (Exception e) {
			throw new AppSystemException("读取任务前端配置生成的文件失败");
		}
	}

	/**
	 * 将文件内容读为字符串，指定编码
	 *
	 * @param file File对象
	 * @return java.lang.String    文件内容
	 * @author 13616
	 * @date 2019/7/31 10:01
	 */
	public static String readFile2String(File file, String charset) throws IOException, IllegalArgumentException {

		if (file.exists() && file.isFile()) {
			return FileUtils.readFileToString(file, charset);
		} else {
			throw new IllegalArgumentException(file.getName() + "：不是一个可读的文件");
		}
	}

	public static void writeString2File(File file, String context, String encoding) throws IOException {
		FileUtils.write(file, context, encoding);
	}

	/**
	 * 根据文件路径判断文件是否存在
	 *
	 * @param filePath 文件路径
	 * @return boolean true : exist, false : not exist
	 */
	public static boolean decideFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	public static long getFileSize(String filePath) {

		return new File(filePath).length();
	}

	public static void initPath(String task_id, String[] paths) {
		for (String path : paths) {
			File file = new File(path + task_id);
			if (!file.exists()) {
				if (!file.mkdirs()) {
					throw new AppSystemException("创建文件夹" + file.getAbsolutePath() + "失败！");
				}
			}
		}
	}

	/**
	 * 判断目录是不是系统目录
	 *
	 * @param path
	 * @return
	 */
	public static boolean isSysDir(String path) {
		if (path.endsWith("/") || path.endsWith("\\")) {
			path = path.substring(0, path.length() - 1);
		}
		boolean flag = false;
		if (pathList.contains(path)) {
			return true;
		}
		return flag;
	}

}
