package hrds.agent.job.biz.core.dbstage.increasement;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dbstage.service.CollectTableHandleParse;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "通过jdbc使用sql算增量", author = "zxz", createdate = "2019/12/24 09:41")
public abstract class JDBCIncreasement implements Closeable {
	private static final Log logger = LogFactory.getLog(JDBCIncreasement.class);

	List<String> columns;// csv中存有的字段
	List<String> types;// csv中存有的字段类型
	String sysDate;//任务跑批日期
	String tableNameInHBase; //hbase的表名
	String deltaTableName; //增量表的名字
	List<String> sqlList = new ArrayList<>();
	String yesterdayTableName;//上次的表
	DatabaseWrapper db;
	String todayTableName;

	JDBCIncreasement(TableBean tableBean, String hbase_name, String sysDate, DatabaseWrapper db, String dtcs_name) {
		this.columns = StringUtil.split(tableBean.getColumnMetaInfo(), CollectTableHandleParse.STRSPLIT);
		this.types = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo(),
				CollectTableHandleParse.STRSPLIT), dtcs_name);
		this.sysDate = sysDate;
		this.tableNameInHBase = hbase_name;
		this.deltaTableName = hbase_name + "_tmp_hyren";
		this.yesterdayTableName = hbase_name;
		this.todayTableName = hbase_name + "_" + sysDate;
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
	void dropTableIfExists(String tableName, DatabaseWrapper db, List<String> sqlList) {
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
	void createTableIfNotExists(String tableName, DatabaseWrapper db, List<String> columns, List<String> types,
	                            List<String> sqlList) {
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
			sqlList.add(create.toString());
		}
	}

	/**
	 * 判断oracle数据库是否存在该表
	 */
	private boolean tableIsExistsOracle(String tableName, DatabaseWrapper db) {
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

	/**
	 * 执行sql
	 */
	public static void executeSql(List<String> sqlList, DatabaseWrapper db) {
		for (String sql : sqlList) {
			logger.info("执行的sql为： " + sql);
			db.execute(sql);
		}
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
		executeSql(deleteInfo, db);
	}

	/**
	 * 追加
	 */
	public void append() {
		//1.为了防止第一次执行，yesterdayTableName表不存在，创建空表
		createTableIfNotExists(yesterdayTableName, db, columns, types, sqlList);
		//2.恢复今天的数据，防止重跑
		restoreAppendData();
		//3.插入今天新增的数据
		insertAppendData();
		//4.执行sql
		executeSql(sqlList, db);
	}

	private void insertAppendData() {
		StringBuilder insertDataSql = new StringBuilder(120);
		//拼接查找增量并插入增量表
		insertDataSql.append("INSERT INTO ");
		insertDataSql.append(yesterdayTableName);
		insertDataSql.append("(");
		for (String col : columns) {
			insertDataSql.append(col).append(", ");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1);
		insertDataSql.append(" ) ");
		insertDataSql.append(" select ");
		for (String col : columns) {
			insertDataSql.append(todayTableName).append(".").append(col).append(",");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1);
		insertDataSql.append(" from ");
		insertDataSql.append(todayTableName);
		sqlList.add(insertDataSql.toString());
	}

	private void restoreAppendData() {
		sqlList.add("DELETE FROM " + yesterdayTableName + " WHERE " + Constant.SDATENAME + "='" + sysDate + "'");
	}

	/**
	 * 替换
	 */
	public void replace() {
		dropTableIfExists(yesterdayTableName, db, sqlList);
		sqlList.add("CREATE TABLE " + yesterdayTableName + "AS SELECT * FROM " + todayTableName);
		//执行sql
		executeSql(sqlList, db);
	}
}
