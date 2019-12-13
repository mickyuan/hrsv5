package hrds.commons.utils;

import hrds.commons.codes.IsFlag;

import java.io.File;

/**
 * @Description: 项目中经常用到的常量值
 * @Author: wangz
 * @CreateTime: 2019-09-19-10:13
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.commons.utils
 **/
public class Constant {

	public static final String SDATENAME = "HYREN_S_DATE";
	public static final String EDATENAME = "HYREN_E_DATE";
	public static final String MD5NAME = "HYREN_MD5_VAL";
	public static final char DATADELIMITER = '\001';//此分隔符为hive的默认分隔符
	public static final String HD = "HD";
	public static final String DB = "DB";
	public static final String MAXDATE = "99991231";
	public static final String SFTP_PORT = "22";
	public static final byte[] HBASE_COLUMN_FAMILY = "F".getBytes();
	public static final String HBASE_ROW_KEY = "hyren_key";
	//当前程序运行的目录
	private static final String USER_DIR = System.getProperty("user.dir");
	//jobInfo文件存放的顶层目录
	public static final String JOBINFOPATH = USER_DIR + File.separator + "jobInfo" + File.separator;
	//job运行程序信息存储文件名称
	public static final String JOBFILENAME = "jobInfo.json";
	//TODO 卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String JDBCUNLOADFOLDER = USER_DIR + File.separator + "JDBC" + File.separator;
	//mapDB文件存放的顶层目录
	public static final String MAPDBPATH = USER_DIR + File.separator + "mapDB" + File.separator;
	//TODO 文件采集卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String FILEUNLOADFOLDER = USER_DIR + File.separator + "dirFile" + File.separator;
	//TODO 数据库采集卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String DBFILEUNLOADFOLDER = USER_DIR + File.separator + "DbFile" + File.separator;
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
	//定义并行抽取SQL开始条数占位符
	public static final String PARALLEL_SQL_START = "#{hy_start}";
	//定义并行抽取SQL结束条数占位符
	public static final String PARALLEL_SQL_END = "#{hy_end}";
}
