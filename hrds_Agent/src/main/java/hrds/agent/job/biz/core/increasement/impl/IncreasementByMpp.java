package hrds.agent.job.biz.core.increasement.impl;

import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.increasement.JDBCIncreasement;
import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.utils.HSqlExecute;
import hrds.commons.utils.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Mpp数据库通过sql方式来处理增量问题
 */
public class IncreasementByMpp extends JDBCIncreasement {
	private static final Log logger = LogFactory.getLog(IncreasementByMpp.class);

	public IncreasementByMpp(TableBean tableBean, String hbase_name, String sysDate, DatabaseWrapper db,
	                         String dsl_name) {
		super(tableBean, hbase_name, sysDate, db, dsl_name);
	}

	/**
	 * 执行事务总方法（包括：创建表，比较增量结果入临时表，将结果入hbase
	 */
	@Override
	public void calculateIncrement() {
		ArrayList<String> sqlList = new ArrayList<>();
		//1.为了防止第一次执行，yesterdayTableName表不存在，创建空表
		HSqlExecute.executeSql(createTableIfNotExists(yesterdayTableName, db, columns, types), db);
		//2、创建增量临时表
		getCreateDeltaSql(sqlList);
		//3、把今天的卸载数据映射成一个表，这里在上传数据的时候加载到了todayTableName这张表。
		//4、为了可以重跑，这边需要把今天（如果今天有进数的话）的数据清除掉
		restore(StorageType.ZengLiang.getCode());
		//5.将比较之后的需要insert的结果插入到临时表中
		getInsertDataSql(sqlList);
		//6.将比较之后的需要delete(拉链中的闭链)的结果插入到临时表中
		getDeleteDataSql(sqlList);
		//7.执行所有sql语句
		HSqlExecute.executeSql(sqlList, db);
	}

	/**
	 * 根据临时增量表合并出新的增量表，删除以前的增量表
	 */
	@Override
	public void mergeIncrement() {
		List<String> list = new ArrayList<>();
		String tmpDelTa = tableNameInHBase + "hyt";
		try {
			dropTableIfExists(tmpDelTa, db, list);
			//创建临时表
			list.add(createInvalidDataSql(tmpDelTa));
			//插入有效数据
			list.add(insertInvalidDataSql(tmpDelTa));
			//插入所有数据
			list.add(insertDeltaDataSql(tmpDelTa, deltaTableName));
			//删除原始表
			dropTableIfExists(tableNameInHBase, db, list);
			//重命名
			list.add("ALTER TABLE  " + tmpDelTa + " RENAME TO  " + tableNameInHBase);
			HSqlExecute.executeSql(list, db);
		} catch (Exception e) {
			logger.error("根据临时表对mpp表做增量操作时发生错误！！", e);
			throw new AppSystemException("根据临时表对mpp表做增量操作时发生错误！！", e);
		}
	}

	/**
	 * 追加
	 */
	@Override
	public void append() {
		ArrayList<String> sqlList = new ArrayList<>();
		//1.为了防止第一次执行，yesterdayTableName表不存在，创建空表
		HSqlExecute.executeSql(createTableIfNotExists(yesterdayTableName, db, columns, types), db);
		//2.恢复今天的数据，防止重跑
		restore(StorageType.ZhuiJia.getCode());
		//3.插入今天新增的数据
		insertAppendData(sqlList);
		//4.执行sql
		HSqlExecute.executeSql(sqlList, db);
	}

	/**
	 * 替换
	 */
	@Override
	public void replace() {
		ArrayList<String> sqlList = new ArrayList<>();
		//临时表存在删除临时表
		dropTableIfExists(deltaTableName, db, sqlList);
		//将本次采集的数据复制到临时表
		sqlList.add("CREATE TABLE " + deltaTableName + " AS SELECT * FROM " + todayTableName);
		//删除上次采集的数据表
		dropTableIfExists(yesterdayTableName, db, sqlList);
		//将临时表改名为进数之后的表
		sqlList.add("ALTER TABLE " + deltaTableName + " RENAME TO " + yesterdayTableName);
		//执行sql
		HSqlExecute.executeSql(sqlList, db);
	}

	/**
	 * 恢复数据
	 *
	 * @param storageType 拉链算法存储模式
	 */
	@Override
	public void restore(String storageType) {
		ArrayList<String> sqlList = new ArrayList<>();
		if (StorageType.ZengLiang.getCode().equals(storageType)) {
			//增量恢复数据
			sqlList.add("delete from " + yesterdayTableName + " where " + Constant.SDATENAME + "='" + sysDate + "'");
			sqlList.add("update " + yesterdayTableName + " set " + Constant.EDATENAME + " = "
					+ Constant.MAXDATE + " where " + Constant.EDATENAME + "='" + sysDate + "'");
		} else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
			//追加恢复数据
			sqlList.add("DELETE FROM " + yesterdayTableName + " WHERE " + Constant.SDATENAME + "='" + sysDate + "'");
		} else if (StorageType.TiHuan.getCode().equals(storageType)) {
			logger.info("替换，不需要恢复当天数据");
		} else {
			throw new AppSystemException("错误的增量拉链参数代码项");
		}
		HSqlExecute.executeSql(sqlList, db);
	}

	/**
	 * 关闭连接
	 */
	@Override
	public void close() {
		dropAllTmpTable();
		if (db != null) {
			db.close();
		}
	}

	/**
	 * 创建增量表
	 */
	private void getCreateDeltaSql(ArrayList<String> sqlList) {
		//1  创建增量表
		StringBuilder sql = new StringBuilder(120); //拼接创表sql语句
		sql.append("CREATE TABLE ");
		sql.append(deltaTableName);
		sql.append("(");
		for (int i = 0; i < columns.size(); i++) {
			sql.append(columns.get(i)).append(" ").append(types.get(i)).append(",");
		}
		sql.append(" action VARCHAR(2)");
		sql.append(")");
		//如果表已存在则删除
		dropTableIfExists(deltaTableName, db, sqlList);
		sqlList.add(sql.toString());
	}

	/**
	 * 调用deleteData的sql，在增量表中插入增量数据
	 */
	private void getDeleteDataSql(ArrayList<String> sqlList) {

		StringBuilder deleteDatasql = new StringBuilder(120);
		//拼接查找增量并插入增量表
		deleteDatasql.append("INSERT INTO ");
		deleteDatasql.append(deltaTableName);
		deleteDatasql.append("(");
		for (String col : columns) {
			deleteDatasql.append(col).append(",");
		}
		deleteDatasql.append(" action ");
		deleteDatasql.append(" ) ");
		deleteDatasql.append(" select ");

		for (String col : columns) {
			if (col.equals(Constant.EDATENAME)) {
				deleteDatasql.append(sysDate).append(" as ").append(col).append(",");
			} else {
				deleteDatasql.append(yesterdayTableName).append(".").append(col).append(",");
			}
		}
		deleteDatasql.append("'UD' ");//修改action的值为D
		deleteDatasql.append(" from ");
		deleteDatasql.append(yesterdayTableName);
		deleteDatasql.append(" WHERE NOT EXISTS ");
		deleteDatasql.append(" ( select * from ");
		deleteDatasql.append(todayTableName);
		deleteDatasql.append(" where ");
		deleteDatasql.append(yesterdayTableName).append(".").append(Constant.MD5NAME);
		deleteDatasql.append(" = ");
		deleteDatasql.append(todayTableName).append(".").append(Constant.MD5NAME);
		deleteDatasql.append(" ) AND ").append(yesterdayTableName).append(".").append(Constant.EDATENAME);
		deleteDatasql.append(" = '99991231'");
		sqlList.add(deleteDatasql.toString());
	}

	/**
	 * 追加插入数据
	 */
	private void insertAppendData(ArrayList<String> sqlList) {
		StringBuilder insertDataSql = new StringBuilder(120);
		//拼接查找增量并插入增量表
		insertDataSql.append("INSERT INTO ");
		insertDataSql.append(yesterdayTableName);
		insertDataSql.append("(");
		for (String col : columns) {
			insertDataSql.append(col).append(",");
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

	/**
	 * 调用insertData的sql，在增量表中插入增量数据
	 */
	private void getInsertDataSql(ArrayList<String> sqlList) {
		StringBuilder insertDataSql = new StringBuilder(120);
		//拼接查找增量并插入增量表
		insertDataSql.append("INSERT INTO ");
		insertDataSql.append(deltaTableName);
		insertDataSql.append("(");
		for (String col : columns) {
			insertDataSql.append(col).append(", ");
		}
		insertDataSql.append(" action ");
		insertDataSql.append(" ) ");
		insertDataSql.append(" select ");
		for (String col : columns) {
			insertDataSql.append(todayTableName).append(".").append(col).append(",");
		}
		//修改action的值为CU
		insertDataSql.append("'CU' ");
		insertDataSql.append(" from ");
		insertDataSql.append(todayTableName);
		insertDataSql.append(" WHERE NOT EXISTS ");
		insertDataSql.append(" ( select * from ");
		insertDataSql.append(yesterdayTableName);
		insertDataSql.append(" where ");
		insertDataSql.append(todayTableName).append(".").append(Constant.MD5NAME);
		insertDataSql.append(" = ");
		insertDataSql.append(yesterdayTableName).append(".").append(Constant.MD5NAME);
		insertDataSql.append(" ) ");
		sqlList.add(insertDataSql.toString());
	}

	/**
	 * 插入有效数据
	 *
	 * @param tmpDelTa 表名
	 * @return 插入有效数据sql
	 */
	private String insertInvalidDataSql(String tmpDelTa) {
		StringBuilder insertDataSql = new StringBuilder(120);
		//拼接查找增量并插入增量表
		insertDataSql.append("INSERT INTO ");
		insertDataSql.append(tmpDelTa);
		insertDataSql.append("(");
		for (String col : columns) {
			insertDataSql.append(col).append(",");
		}
		//将最后的逗号删除
		insertDataSql.deleteCharAt(insertDataSql.length() - 1);
		insertDataSql.append(" ) ");
		insertDataSql.append(" select ");
		for (String col : columns) {
			insertDataSql.append(tableNameInHBase).append(".").append(col).append(",");
		}
		//将最后的逗号删除
		insertDataSql.deleteCharAt(insertDataSql.length() - 1);
		insertDataSql.append(" from ");
		insertDataSql.append(tableNameInHBase);
		insertDataSql.append(" WHERE NOT EXISTS ");
		insertDataSql.append(" ( select * from ");
		insertDataSql.append(deltaTableName);
		insertDataSql.append(" where ");
		insertDataSql.append(deltaTableName).append(".").append(Constant.MD5NAME);
		insertDataSql.append(" = ");
		insertDataSql.append(tableNameInHBase).append(".").append(Constant.MD5NAME);
		insertDataSql.append(" ) ");
		return insertDataSql.toString();

	}

	/**
	 * 创建增量接收数据临时表
	 *
	 * @param tmpDelTa 表名
	 * @return 建表语句
	 */
	private String createInvalidDataSql(String tmpDelTa) {
		//1  创建临时表
		StringBuilder sql = new StringBuilder(120); //拼接创表sql语句
		sql.append("CREATE TABLE ");
		sql.append(tmpDelTa);
		sql.append("(");
		for (int i = 0; i < columns.size(); i++) {
			sql.append(columns.get(i)).append(" ").append(types.get(i)).append(",");
		}
		sql.deleteCharAt(sql.length() - 1); //将最后的逗号删除
		sql.append(")");
		return sql.toString();
	}

	/**
	 * 表存在先删除该表，这里因为Oracle不支持DROP TABLE IF EXISTS
	 */
	public static void dropTableIfExists(String tableName, DatabaseWrapper db, List<String> sqlList) {
		if (Dbtype.ORACLE.equals(db.getDbtype())) {
			//如果有数据则表明该表存在，创建表
			if (db.isExistTable(tableName)) {
				sqlList.add("DROP TABLE " + tableName);
			}
		} else {
			sqlList.add("DROP TABLE IF EXISTS " + tableName);
		}
	}

	/**
	 * 创建表，如果表不存在
	 */
	private String createTableIfNotExists(String tableName, DatabaseWrapper db, List<String> columns, List<String> types) {
		StringBuilder create = new StringBuilder(1024);
		if (Dbtype.ORACLE.equals(db.getDbtype())) {
			//如果有数据则表明该表存在，创建表
			if (!db.isExistTable(tableName)) {
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
	 * 删除临时增量表
	 */
	private void dropAllTmpTable() {
		List<String> deleteInfo = new ArrayList<>();
		//删除临时增量表
		dropTableIfExists(deltaTableName, db, deleteInfo);
		//清空表数据
		HSqlExecute.executeSql(deleteInfo, db);
	}
}
