package hrds.agent.job.biz.constant;


import hrds.commons.codes.IsFlag;
import hrds.commons.utils.PropertyParaUtil;

public class JobConstant {
	//作业跑批结束时间
	public static final String MAX_DATE = "99991231";
	public static final String MAX_DATE_NAME = "HYREN_END_DATE";
	public static final String START_DATE_NAME = "HYREN_START_DATE";
	public static final String MD5_NAME = "HYREN_MD5";

	public static final String COLUMN_SEPARATOR = ",";
	public static final String COLUMN_TYPE_SEPARATOR = "|";
	//此分隔符为拼接MD5的默认分隔符
	public static final String DATADELIMITER = "`@`";
	//此分隔符为卸数成SequenceFile时的默认分隔符，SequenceFile不允许页面自定义
	public static final String SEQUENCEDELIMITER = String.valueOf('\001');
	public static final String CLEAN_SEPARATOR = "`";
	private static final String HD = "HD";

	//TODO 下面这些是从配置文件取，需要放到agent下面
	//XXX 这里文件采集上传到本地还是HDFS如果是页面上选的，下面这个参数就不需要了
	public static final boolean HAS_HADOOP_ENV = HD.equalsIgnoreCase(PropertyParaUtil.
			getString("ver_type", HD));//是否有大数据环境
	public static final long FILE_BLOCKSIZE = Long.parseLong(PropertyParaUtil.getString("file_blocksize",
			"1024")) * 1024 * 1024L;//卸数写文件大小默认1024M file_blocksize
	public static final String SYS_DATEFORMAT = PropertyParaUtil.getString("SYS_DATEFORMAT",
			"yyyyMMdd");
	//是否写多文件
	public static final boolean WriteMultipleFiles = IsFlag.Shi.getCode().equals(PropertyParaUtil.getString(
			"writemultiplefiles", IsFlag.Fou.getCode()));
	//判断文件变化的类型是否是MD5
	public static final boolean FILECHANGESTYPEMD5 = "md5".equals(PropertyParaUtil.getString(
			"determineFileChangesType", ""));
	//文件上传到hdfs的顶层目录
	public static final String PREFIX = PropertyParaUtil.getString("pathprefix", "/hrds");
	//自定义sql或者自定义并行抽取sql时使用的分隔符
	public static final String SQLDELIMITER = "`@^";

	public static final String DEFAULTLINESEPARATOR = "\n";
}

