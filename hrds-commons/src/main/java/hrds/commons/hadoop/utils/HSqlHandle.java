package hrds.commons.hadoop.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.utils.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "拼接执行sql工具类", author = "zxz", createdate = "2019/10/25 9:57")
public class HSqlHandle {
	//打印日志
	private static final Log logger = LogFactory.getLog(HSqlHandle.class);

	@Method(desc = "创建hbase表的hive映射表",
			logicStep = "1.表已经存在hive，则先删除该表" +
					"2.将sql添加到需要依次执行的sql的合集" +
					"3.拼接hbase映射hive表的sql语句" +
					"4.添加拼接好的sql并返回")
	@Param(name = "tableName", desc = "表名", range = "不可为空")
	@Param(name = "hiveColumns", desc = "多条字段名称拼接的字符串，逗号隔开", range = "不可为空")
	@Param(name = "hiveTypes", desc = "多条字段类型拼接的字符串，逗号隔开", range = "不可为空")
	@Return(desc = "需要依次执行的sql的合集", range = "不会为空")
	public static List<String> hiveMapHBase(String tableName, String hiveColumns, String hiveTypes) {
		StringBuilder sql = new StringBuilder(1024);
		//1.表已经存在hive，则先删除该表
		sql.append("drop table if exists ").append(tableName);
		List<String> sqlList = new ArrayList<>();
		//2.将sql添加到需要依次执行的sql的合集
		sqlList.add(sql.toString());
		sql.delete(0, sql.length());
		//3.拼接hbase映射hive表的sql语句
		sql.append("create external table if not exists ").append(tableName);
		sql.append(" ( ").append(Constant.HBASE_ROW_KEY).append(" string , ");
		String[] csvStructVal = hiveColumns.split(",");
		String[] csvStructType = hiveTypes.split(",");
		for (int i = 0; i < csvStructVal.length; i++) {
			sql.append("`").append(csvStructVal[i]).append("` ").append(csvStructType[i]).append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") row format delimited fields terminated by '\\t' ");
		sql.append("STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' ");
		sql.append("WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key , ");
		for (String col : csvStructVal) {
			sql.append(Bytes.toString(Constant.HBASE_COLUMN_FAMILY)).append(":").append(col).append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append("\") TBLPROPERTIES (\"hbase.table.name\" = \"").append(tableName).append("\")");
		//4.添加拼接好的sql并返回
		sqlList.add(sql.toString());
		logger.info("拼接hbase映射hive表的sql为=====" + sql.toString());
		return sqlList;
	}
}
