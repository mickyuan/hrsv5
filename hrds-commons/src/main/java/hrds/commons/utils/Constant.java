package hrds.commons.utils;

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
	public static final String HD = "HD";
	public static final String DB = "DB";
	public static final String MAXDATE = "99991231";
	public static final String SFTP_PORT = "22";
	public static final byte[] HBASE_COLUMN_FAMILY = "F".getBytes();
	public static final String HBASE_ROW_KEY = "hyren_key";
	//XXX 这里文件采集上传到本地还是HDFS如果是页面上选的，下面这个参数就不需要了
	public static final boolean hasHadoopEnv = HD.equalsIgnoreCase(PropertyParaUtil.
			getString("ver_type", HD));//是否有大数据环境
}
