package hrds.agent.job.biz.core.increasement;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.utils.HSqlExecute;
import hrds.commons.utils.Constant;

import java.io.Closeable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "通过jdbc使用sql算增量", author = "zxz", createdate = "2019/12/24 09:41")
public abstract class JDBCIncreasement implements Closeable {

	List<String> columns;// csv中存有的字段
	List<String> types;// csv中存有的字段类型
	String sysDate;//任务跑批日期
	String tableNameInHBase; //hbase的表名
	String deltaTableName; //增量表的名字
	List<String> sqlList = new ArrayList<>();
	String yesterdayTableName;//上次的表
	DatabaseWrapper db;
	String todayTableName;

	JDBCIncreasement(TableBean tableBean, String hbase_name, String sysDate, DatabaseWrapper db, String dsl_name) {
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
	 * 比较出所有的增量数据入增量表
	 */
	public abstract void calculateIncrement() throws Exception;

	/**
	 * 根据临时增量表合并出新的增量表，删除以前的增量表
	 */
	public abstract void mergeIncrement() throws Exception;

	/**
	 * 表存在先删除该表，这里因为Oracle不支持DROP TABLE IF EXISTS
	 */
	public static void dropTableIfExists(String tableName, DatabaseWrapper db, List<String> sqlList) {
		if (Dbtype.ORACLE.equals(db.getDbtype())) {
			//如果有数据则表明该表存在，创建表
			if (tableIsExistsOracle(tableName, db)) {
				sqlList.add("DROP TABLE " + tableName);
			}
		} else {
			sqlList.add("DROP TABLE IF EXISTS " + tableName);
		}
	}

	/**
	 * 创建表，如果表不存在
	 */
	String createTableIfNotExists(String tableName, DatabaseWrapper db, List<String> columns, List<String> types) {
		StringBuilder create = new StringBuilder(1024);
		if (Dbtype.ORACLE.equals(db.getDbtype())) {
			//如果有数据则表明该表存在，创建表
			if (!tableIsExistsOracle(tableName, db)) {
				create.append("CREATE TABLE ");
			}
		} else {
			create.append("CREATE TABLE IF NOT EXISTS ");
		}
		//当StringBuilder中有值
		if (create.length() > 10) {
			create.append(tableName);
			create.append("(");
			for (int i = 0; i < columns.size(); i++) {
				create.append(columns.get(i)).append(" ").append(types.get(i)).append(",");
			}
			//将最后的逗号删除
			create.deleteCharAt(create.length() - 1);
			create.append(")");
			return create.toString();
		} else {
			return "";
		}
	}

	/**
	 * 判断oracle数据库是否存在该表
	 */
	private static boolean tableIsExistsOracle(String tableName, DatabaseWrapper db) {
		ResultSet resultSet;
		try {
			resultSet = db.queryGetResultSet("SELECT * FROM user_objects where lower(object_name) = ? "
					, tableName.toLowerCase());
			//如果有数据则表明该表存在，创建表
			if (resultSet.next()) {
				return true;
			}
		} catch (Exception e) {
			throw new AppSystemException("查询表名是否在oracle数据库中存在出现异常", e);
		}
		return false;
	}

	@Override
	public void close() {
		dropAllTmpTable();
		if (db != null) {
			db.close();
		}
	}

	/**
	 * 删除临时增量表
	 */
	private void dropAllTmpTable() {
		List<String> deleteInfo = new ArrayList<>();
		//删除临时增量表
		dropTableIfExists(deltaTableName, db, deleteInfo);
		//清空表数据
		HSqlExecute.executeSql(deleteInfo, db);
	}

	/**
	 * 追加
	 */
	public abstract void append();

	/**
	 * 替换
	 */
	public void replace() {
		dropTableIfExists(yesterdayTableName, db, sqlList);
		sqlList.add("CREATE TABLE " + yesterdayTableName + " AS SELECT * FROM " + todayTableName);
		//执行sql
		HSqlExecute.executeSql(sqlList, db);
	}
}
