package hrds.agent.job.biz.constant;


import hrds.commons.codes.IsFlag;
import hrds.commons.utils.PropertyParaUtil;

public class JobConstant {
	private static final String HD = "HD";
	//写文件时缓存的行数
	public static final int BUFFER_ROW = 5000;
	//TODO 下面这些是从配置文件取，需要放到agent下面
	//XXX 这里文件采集上传到本地还是HDFS如果是页面上选的，下面这个参数就不需要了
	public static final boolean HAS_HADOOP_ENV = HD.equalsIgnoreCase(PropertyParaUtil.
			getString("ver_type", HD));//是否有大数据环境
	public static final long FILE_BLOCKSIZE = Long.parseLong(PropertyParaUtil.getString("file_blocksize",
			"1024")) * 1024 * 1024L;//卸数写文件大小默认1024M file_blocksize
	//	public static final String SYS_DATEFORMAT = PropertyParaUtil.getString("SYS_DATEFORMAT",
//			"yyyyMMdd");
	//是否写多文件
	public static final boolean WriteMultipleFiles = IsFlag.Shi.getCode().equals(PropertyParaUtil.getString(
			"writemultiplefiles", IsFlag.Fou.getCode()));
	//判断文件变化的类型是否是MD5
	public static final boolean FILECHANGESTYPEMD5 = "MD5".equals(PropertyParaUtil.getString(
			"determineFileChangesType", ""));
	//文件上传到hdfs的顶层目录
	public static final String PREFIX = PropertyParaUtil.getString("pathprefix", "/hrds");
	//是否添加isAddOperateInfo
	public static final boolean ISADDOPERATEINFO = Boolean.parseBoolean(PropertyParaUtil.
			getString("isAddOperateInfo", "false"));
	//多线程指定线程池的默认线程数
	public static final int AVAILABLEPROCESSORS = Integer.parseInt(PropertyParaUtil.getString("availableProcessors",
			String.valueOf(Runtime.getRuntime().availableProcessors())));
}

