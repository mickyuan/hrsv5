package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import hrds.commons.exception.BusinessException;

@DocClass(desc = "路径处理工具类", author = "BY-HLL", createdate = "2019/10/17 0017")
public class PathUtil {

	private static final String PREFIX = PropertyParaValue.getString("pathprefix", "/hrds");

	@Method(desc = "将本地路径转换为HDFS路径", logicStep = "将本地路径转换为HDFS路径")
	@Param(name = "localPath", desc = "本地文件路径", range = "本地文件的全路径")
	@Return(desc = "HDFSPath", range = "HDFS的数据存储路径")
	public static String convertLocalPathToHDFSPath(String localPath) {
		localPath = FileNameUtils.normalize(localPath).replace("\\", "/");
		String HDFSPath;
		try {
			HDFSPath = localPath.substring(localPath.lastIndexOf(PREFIX));
		} catch (StringIndexOutOfBoundsException e) {
			throw new BusinessException("本地文件路径转HDFS路径失败! localPath=" + localPath);
		}
		return HDFSPath;
	}
}
