package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;

import java.io.File;

@DocClass(desc = "项目中经常用到的常量值", author = "", createdate = "2019/12/25 0025 下午 04:28")
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
	//TODO 数据库采集卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String JDBCUNLOADFOLDER = USER_DIR + File.separator + "jdbc" + File.separator;
	//mapDB文件存放的顶层目录
	public static final String MAPDBPATH = USER_DIR + File.separator + "mapDb" + File.separator;
	//TODO 文件采集卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String FILEUNLOADFOLDER = USER_DIR + File.separator + "dirFile" + File.separator;
	//TODO DB文件采集卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String DBFILEUNLOADFOLDER = USER_DIR + File.separator + "dbFile" + File.separator;
	//TODO 存储层上传的配置文件存放的顶层目录
	public static final String STORECONFIGPATH = USER_DIR + File.separator + "storeConfigPath" + File.separator;
	//TODO 存储文件上传脚本的顶层目录
	public static final String HDFSSHELLFILE = USER_DIR + File.separator + "hdfsShellFile" + File.separator;
	//TODO 存放采集页面配置的数据文件
	public static final String MESSAGEFILE = USER_DIR + File.separator + "messageFile" + File.separator;
	//定义并行抽取SQL开始条数占位符
	public static final String PARALLEL_SQL_START = "#{hy_start}";
	//定义并行抽取SQL结束条数占位符
	public static final String PARALLEL_SQL_END = "#{hy_end}";

	//是否有大数据环境
	public static final boolean HAS_HADOOP_ENV = HD.equalsIgnoreCase(PropertyParaValue.getString("ver_type", HD));
	//贴源层下实时数据
	public static final String DCL_REALTIME = DataSourceType.DCL.getCode() + "_REALTIME";
	//贴源层下实时数据类型
	public static final String REALTIME_TYPE = "kafka";
	//贴源层下批量采集数据
	public static final String DCL_BATCH = DataSourceType.DCL.getCode() + "_BATCH";
	//系统数据表
	public static final String SYS_DATA_TABLE = "sysDataTable";
	//系统数据备份
	public static final String SYS_DATA_BAK = "sysDataBak";
}
