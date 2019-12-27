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
	//TODO 卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String JDBCUNLOADFOLDER = USER_DIR + File.separator + "JDBC" + File.separator;
	//mapDB文件存放的顶层目录
	public static final String MAPDBPATH = USER_DIR + File.separator + "mapDB" + File.separator;
	//TODO 文件采集卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String FILEUNLOADFOLDER = USER_DIR + File.separator + "dirFile" + File.separator;
	//TODO 数据库采集卸数文件存放的顶层目录,这里应该是根据系统参数来的
	public static final String DBFILEUNLOADFOLDER = USER_DIR + File.separator + "DbFile" + File.separator;
	//定义并行抽取SQL开始条数占位符
	public static final String PARALLEL_SQL_START = "#{hy_start}";
	//定义并行抽取SQL结束条数占位符
	public static final String PARALLEL_SQL_END = "#{hy_end}";

	//贴源层下实时数据
	public static final String DCLKFK = DataSourceType.DCL.toString() + "_REALTIME";
	//贴源层下实时数据类型
	public static final String REALTIME_TYPE = "kafka";
	//贴源层下批量采集数据
	public static final String DCLBATCH = DataSourceType.DCL.toString() + "_BATCH";
	//系统数据表
	public static final String SYSDATATABLE = "sysDataTable";
	//系统数据备份
	public static final String SYSDATABAK = "sysDataBak";
}
