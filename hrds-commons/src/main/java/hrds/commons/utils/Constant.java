package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import hrds.commons.codes.FileFormat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "项目中经常用到的常量值", author = "zxz", createdate = "2019/12/25 0025 下午 04:28")
public class Constant {

	public static final String SDATENAME = "HYREN_S_DATE";
	public static final String EDATENAME = "HYREN_E_DATE";
	public static final String MD5NAME = "HYREN_MD5_VAL";
	public static final String MAXDATE = "99991231";
	public static final String SFTP_PORT = "22";
	public static final byte[] HBASE_COLUMN_FAMILY = "F".getBytes();
	public static final String HBASE_ROW_KEY = "hyren_key";
	//当前程序运行的目录
	private static final String USER_DIR = System.getProperty("user.dir");
	//jobInfo文件存放的顶层目录
	public static final String JOBINFOPATH = USER_DIR + File.separator + "jobInfo" + File.separator;
	//agent读取数据字典转换为xml保存的文件目录
	public static final String XMLPATH = USER_DIR + File.separator + "xmlPath" + File.separator;
	//job运行程序信息存储文件名称
	public static final String JOBFILENAME = "jobInfo.json";
	//TODO 数据库采集数据字典存放的顶层目录
	public static final String DICTIONARY = USER_DIR + File.separator + "dictionary" + File.separator;
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

	//贴源层下批量数据 01:批量类型数据
	public static final String DCL_BATCH = "dcl_batch";
	//贴源层下实时数据 02:实时类型数据
	public static final String DCL_REALTIME = "dcl_realtime";
	//集市层
	public static final String DML = "DML";
	//系统数据表 11:系统数据表
	public static final String SYS_DATA_TABLE = "sys_data_table";
	//系统数据备份 12:系统数据备份
	public static final String SYS_DATA_BAK = "sys_data_bak";
	//数据管控失效标记前缀
	public static final String DQC_INVALID_TABLE = "dqc_invalid_table_";
	//数据管控失效标记后缀
	public static final String _HYREN = "_hyren";
	//数据管控表操作类型 set_invalid:设置为无效,restore:恢复
	public static final String DM_SET_INVALID_TABLE = "set_invalid";
	public static final String DM_RESTORE_TABLE = "restore";
	//oracle外部表，采集操作的目录对象名称
	// XXX 需要强制要求oracle数据库创建目录对象，存储配置external_root_path的文件夹路径必须为数据库创建对象的parent文件夹
	public static final String HYSHF_DCL = "HYSHF_DCL";

	//solr自定义的handler
	public static final String HANDLER = "/reloadDictionary";
	//solr自定义的分隔符
	public static final char SOLR_DATA_DELIMITER = '\001';
	// 日志审查需要加入日志管理的功能
	public static final String USERLOGIN = "用户登入";
	public static final String USERSIGNOUT = "用户登出";
	public static final String INVALIDUSERINFO = "用户信息失效";
	public static final String DELETEDBTASK = "采集数据库直连任务的删除";
	public static final String DELETEDFTASK = "采集db文件任务的删除记录";
	public static final String DELETEHALFSTRUCTTASK = "采集半结构任务的删除记录";
	public static final String DELETENONSTRUCTTASK = "采集非结构任务的删除记录";
	public static final String DELETEFTPTASK = "采集ftp任务的删除记录";
	public static final String ADDDMDATATABLE = "集市新建数据表";
	public static final String DELETEDMDATATABLE = "集市删除数据表";
	//数据库采集，获取元数据信息拼接的分隔符
	public static final String METAINFOSPLIT = "^";
	//自定义sql或者自定义并行抽取sql时使用的分隔符
	public static final String SQLDELIMITER = "`@^";
	//DB文件转存默认的行分隔符
	public static final String DEFAULTLINESEPARATOR = "\n";
	//多线程指定线程池的默认线程数
	public static final int AVAILABLEPROCESSORS = Integer.parseInt(PropertyParaUtil.getString("availableProcessors",
			String.valueOf(Runtime.getRuntime().availableProcessors())));
	//拼接sql字段的分隔符
	public static final String COLUMN_SEPARATOR = ",";
	//此分隔符为拼接MD5的默认分隔符
	public static final String DATADELIMITER = "`@^";
	//此分隔符为卸数成SequenceFile时的默认分隔符，SequenceFile不允许页面自定义
	public static final String SEQUENCEDELIMITER = String.valueOf('\001');
	//mysql建表语句的转义符
	public static final String MYSQL_ESCAPES = "`";

	//数据库抽取不同文件格式对应的默认路径名称的映射
	public static final Map<String, String> fileFormatMap = new HashMap<>();

	/*
	 * 数据库抽取卸数下来文件格式对应路径的关系
	 */
	static {
		fileFormatMap.put(FileFormat.FeiDingChang.getCode(), "NONFIXEDFILE");
		fileFormatMap.put(FileFormat.DingChang.getCode(), "FIXEDFILE");
		fileFormatMap.put(FileFormat.CSV.getCode(), FileFormat.CSV.getValue());
		fileFormatMap.put(FileFormat.PARQUET.getCode(), FileFormat.PARQUET.getValue());
		fileFormatMap.put(FileFormat.ORC.getCode(), FileFormat.ORC.getValue());
		fileFormatMap.put(FileFormat.SEQUENCEFILE.getCode(), FileFormat.SEQUENCEFILE.getValue());
	}

}
