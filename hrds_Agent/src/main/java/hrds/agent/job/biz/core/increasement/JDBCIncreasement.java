package hrds.agent.job.biz.core.increasement;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.commons.utils.Constant;

import java.io.Closeable;
import java.util.List;

@DocClass(desc = "通过jdbc使用sql算增量", author = "zxz", createdate = "2019/12/24 09:41")
public abstract class JDBCIncreasement implements Closeable, Increasement {

	protected List<String> columns;// csv中存有的字段
	protected List<String> types;// csv中存有的字段类型
	protected String sysDate;//任务跑批日期
	protected String tableNameInHBase; //hbase的表名
	protected String deltaTableName; //增量表的名字
	protected String yesterdayTableName;//上次的表
	protected DatabaseWrapper db;
	protected String todayTableName;

	protected JDBCIncreasement(TableBean tableBean, String hbase_name, String sysDate, DatabaseWrapper db, String dsl_name) {
		this.columns = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		this.types = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo(),
				Constant.METAINFOSPLIT), dsl_name);
		this.sysDate = sysDate;
		this.tableNameInHBase = hbase_name;
		this.deltaTableName = hbase_name + "_hy";
		this.yesterdayTableName = hbase_name;
		//当天的数据为拼接后的表名加序号1。例如：默认保留数据的天数为4，则会有四张表，从当天跑批往后依次加下标1、2、3、4
		this.todayTableName = hbase_name + "_" + 1;
		this.db = db;
	}

	/**
	 * 拼接查询表插入到另一张表的sql
	 *
	 * @param targetTableName 目标表名
	 * @param sourceTableName 源表名
	 * @return 拼接的sql
	 */
	protected String insertDeltaDataSql(String targetTableName, String sourceTableName) {
		StringBuilder insertDataSql = new StringBuilder(120);
		//拼接查找增量并插入增量表
		insertDataSql.append("INSERT INTO ");
		insertDataSql.append(targetTableName);
//		insertDataSql.append("(");
//		for (String col : columns) {
//			insertDataSql.append(col).append(",");
//		}
//		insertDataSql.deleteCharAt(insertDataSql.length() - 1); //将最后的逗号删除
//		insertDataSql.append(" ) ");
		insertDataSql.append(" select ");
		for (String col : columns) {
			insertDataSql.append(sourceTableName).append(".").append(col).append(",");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1); //将最后的逗号删除
		insertDataSql.append(" from ");
		insertDataSql.append(sourceTableName);
		return insertDataSql.toString();
	}

	/**
	 * 表存在先删除该表，这里因为Oracle不支持DROP TABLE IF EXISTS
	 */
	public static void dropTableIfExists(String tableName, DatabaseWrapper db, List<String> sqlList) {
		//如果有数据则表明该表存在，创建表
		if (db.isExistTable(tableName)) {
			sqlList.add("DROP TABLE " + tableName);
		}
	}
}
