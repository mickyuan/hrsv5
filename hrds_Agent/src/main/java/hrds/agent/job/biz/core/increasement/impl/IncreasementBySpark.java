package hrds.agent.job.biz.core.increasement.impl;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.increasement.JDBCIncreasement;
import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.utils.HSqlExecute;
import hrds.commons.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 通过spark sql跑增量
 */
public class IncreasementBySpark extends JDBCIncreasement {
	//打印日志
	private static final Logger logger = LogManager.getLogger();

	public IncreasementBySpark(TableBean tableBean, String hbase_name, String sysDate, DatabaseWrapper db,
							   String dsl_name) {
		super(tableBean, hbase_name, sysDate, db, dsl_name);
	}

	/**
	 * 这个方法跑完，带有所有增量数据的完整的表就生成了carbondata
	 */
	@Override
	public void calculateIncrement() {
		ArrayList<String> sqlList = new ArrayList<>();
		//1.为了防止第一次执行，yesterdayTableName表不存在，创建空表
		HSqlExecute.executeSql(createTableIfNotExists(yesterdayTableName), db);
		//2、创建增量表
		getCreateDeltaSql(sqlList);
		//3、把今天的卸载数据映射成一个表，这里在上传数据的时候加载到了todayTableName这张表。
		//4、为了可以重跑，这边需要把今天（如果今天有进数的话）的数据清除
		restore(StorageType.QuanLiang.getCode());
		//5、将比较之后的要insert的结果插入到临时表中
		getInsertDataSql(sqlList);
		//6、将比较之后的要delete(拉链中的闭链)的结果插入到临时表中
		getDeleteDataSql(sqlList);
		//7、把全量数据中的除了有效数据且关链的数据以外的所有数据插入到临时表中
		getDeltaDataSql(sqlList);
		HSqlExecute.executeSql(sqlList, db);
	}

	/**
	 * 根据临时增量表合并出新的增量表，删除以前的增量表
	 */
	@Override
	public void mergeIncrement() {
		List<String> sqlList = new ArrayList<>();
		sqlList.add("DROP TABLE IF EXISTS " + yesterdayTableName);
		sqlList.add("alter table " + deltaTableName + " rename to " + yesterdayTableName);
		HSqlExecute.executeSql(sqlList, db);
	}

	@Override
	public void append() {
		//1.为了防止第一次执行，yesterdayTableName表不存在，创建空表
		HSqlExecute.executeSql(createTableIfNotExists(yesterdayTableName), db);
		//2、为了可以重跑，这边需要把今天（如果今天有进数的话）的数据清除
		restore(StorageType.ZhuiJia.getCode());
		//3.插入今天新增的数据,执行sql
		HSqlExecute.executeSql(insertDeltaDataSql(yesterdayTableName, todayTableName), db);
	}

	/**
	 * 替换
	 */
	@Override
	public void replace() {
		ArrayList<String> sqlList = new ArrayList<>();
		//创建临时表存本次采集的数据
		getCreateDeltaSql(sqlList);
		//将本次采集的数据存入临时表
		sqlList.add(insertDeltaDataSql(deltaTableName, todayTableName));
		//删除上次采集的数据表
		sqlList.add("DROP TABLE IF EXISTS " + yesterdayTableName);
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
		if (StorageType.QuanLiang.getCode().equals(storageType) || StorageType.ZengLiang.getCode().equals(storageType)) {
			//增量恢复数据
			if (!haveTodayData(yesterdayTableName)) {
				return;
			}
			//找出不是今天的数据,来实现恢复数据
			String after = "case " + Constant.EDATENAME + " when '" + sysDate + "' then '" + Constant.MAXDATE + "' else "
					+ Constant.EDATENAME + " end " + Constant.EDATENAME;
			String join = StringUtils.join(columns, ',');
			join = StringUtils.replace(join, Constant.EDATENAME, after);
			String sql = "create table " + yesterdayTableName + "_restore as select  " + join + " from " +
					yesterdayTableName + " where " + Constant.SDATENAME + "<>'" + sysDate + "'";
			sqlList.add(sql);
			sqlList.add("drop table if exists " + yesterdayTableName);
			sqlList.add("alter table " + yesterdayTableName + "_restore rename to " + yesterdayTableName);
		} else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
			//追加恢复数据
			if (!haveAppendTodayData(yesterdayTableName)) {
				return;
			}
			//找出不是今天的数据,来实现恢复数据
			String join = StringUtils.join(columns, ',');
			String sql = "create table " + yesterdayTableName + "_restore as select  " + join + " from " +
					yesterdayTableName + " where " + Constant.SDATENAME + "<>'" + sysDate + "'";
			sqlList.add(sql);
			sqlList.add("drop table if exists " + yesterdayTableName);
			sqlList.add("alter table " + yesterdayTableName + "_restore rename to " + yesterdayTableName);
		} else if (StorageType.TiHuan.getCode().equals(storageType)) {
			logger.info("替换，不需要恢复当天数据");
		} else {
			throw new AppSystemException("错误的增量拉链参数代码项");
		}
		//执行数据恢复
		HSqlExecute.executeSql(sqlList, db);
	}

	/**
	 * 采集增量数据算拉链
	 */
	@Override
	public void incrementalDataZipper() {
		//1.为了防止第一次执行，yesterdayTableName表不存在，创建空表
		HSqlExecute.executeSql(createTableIfNotExists(yesterdayTableName), db);
		ArrayList<String> sqlList = new ArrayList<>();
		//2.创建增量临时表
		sqlList.add(db.getDbtype().ofCopyTableSchemasSql(yesterdayTableName, deltaTableName));
		//3.防止重跑恢复今天入库的数据
		restore(StorageType.ZengLiang.getCode());
		//4.找出需要关链的数据，插入临时表
		insertInvalidDataSql(sqlList);
		//5.插入有效数据
		insertValidDataSql(sqlList);
		//6.插入增量数据
		sqlList.add(insertDeltaDataSql(deltaTableName, todayTableName));
		//7.删除上次采集的数据表
		sqlList.add("DROP TABLE IF EXISTS " + yesterdayTableName);
		//8.将临时表改名为进数之后的表
		sqlList.add(db.getDbtype().ofRenameSql(deltaTableName, yesterdayTableName));
		HSqlExecute.executeSql(sqlList, db);
	}

	private void insertValidDataSql(ArrayList<String> sqlList) {
		// 拼接查找增量并插入增量表
		String deleteDatasql = "INSERT INTO " +
				this.deltaTableName +
				" select " +
				"*" +
				" from " +
				this.yesterdayTableName +
				" WHERE NOT EXISTS " +
				" ( select " + Constant.MD5NAME + " from " +
				this.todayTableName +
				" where " +
				this.yesterdayTableName + "." + Constant.MD5NAME +
				" = " +
				this.todayTableName + "." + Constant.MD5NAME +
				") AND " + this.yesterdayTableName + "." + Constant.EDATENAME +
				" = '" + Constant.MAXDATE + "'";
		sqlList.add(deleteDatasql);
		// 拼接查找增量并插入增量表
		String deleteDatasql2 = "INSERT INTO " +
				this.deltaTableName +
				" select " +
				"*" +
				" from " +
				this.yesterdayTableName +
				" WHERE " + this.yesterdayTableName + "." + Constant.EDATENAME +
				" <> '" + Constant.MAXDATE + "'";
		sqlList.add(deleteDatasql2);
	}

	private void insertInvalidDataSql(ArrayList<String> sqlList) {
		StringBuilder deleteDatasql = new StringBuilder(120);
		StringBuilder join = new StringBuilder(120);
		for (String column : columns) {
			join.append(this.yesterdayTableName).append(".").append(column).append(",");
		}
		join.delete(join.length() - 1, join.length());
		String select_sql = StringUtils.replace(join.toString(), this.yesterdayTableName + "."
				+ Constant.EDATENAME, "'" + sysDate + "'");
		// 拼接查找增量并插入增量表
		deleteDatasql.append("INSERT INTO ");
		deleteDatasql.append(this.deltaTableName);
		deleteDatasql.append(" select ");
		deleteDatasql.append(select_sql);
		deleteDatasql.append(" from ");
		deleteDatasql.append(this.yesterdayTableName);
		deleteDatasql.append(" WHERE EXISTS ");
		deleteDatasql.append(" ( select ").append(Constant.MD5NAME).append(" from ");
		deleteDatasql.append(this.todayTableName);
		deleteDatasql.append(" where ");
		deleteDatasql.append(this.yesterdayTableName).append(".").append(Constant.MD5NAME);
		deleteDatasql.append(" = ");
		deleteDatasql.append(this.todayTableName).append(".").append(Constant.MD5NAME);
		deleteDatasql.append(") AND ").append(this.yesterdayTableName).append(".").append(Constant.EDATENAME);
		deleteDatasql.append(" = '").append(Constant.MAXDATE).append("'");
		sqlList.add(deleteDatasql.toString());
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

	/*
	 * 创建临时表
	 */
	private void getCreateDeltaSql(ArrayList<String> sqlList) {
		// 如果表已存在则删除
		sqlList.add("DROP TABLE IF EXISTS " + deltaTableName);
		sqlList.add(createTableIfNotExists(deltaTableName));
	}

	/**
	 * 拼接sql建表语句
	 *
	 * @param tableName 表名
	 * @return sql建表语句
	 */
	private String createTableIfNotExists(String tableName) {
		StringBuilder sql = new StringBuilder(120); // 拼接创表sql语句
		sql.append("CREATE TABLE IF NOT EXISTS ");
		sql.append(tableName);
		sql.append("(");
		for (int i = 0; i < columns.size(); i++) {
			sql.append(columns.get(i)).append(' ').append(types.get(i)).append(',');
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") stored as parquet ");
		return sql.toString();
	}

	private void getDeltaDataSql(ArrayList<String> sqlList) {
//		String deltaDatasql = "insert into " + deltaTableName;
//		deltaDatasql += " select * from " + yesterdayTableName;
//		deltaDatasql += " where ";
//		deltaDatasql += yesterdayTableName + "." + Constant.EDATENAME + " <> '" + Constant.MAXDATE + "'";
//		deltaDatasql += " or ";
//		deltaDatasql += "( ";
//		deltaDatasql += " not exists ";
//		deltaDatasql += "(";
//		deltaDatasql += " select " + deltaTableName + "." + Constant.MD5NAME + " from " + deltaTableName + " where "
//				+ deltaTableName + "." + Constant.EDATENAME + " <> '" + Constant.MAXDATE + "'";
//		deltaDatasql += " and " + yesterdayTableName + "." + Constant.MD5NAME + "=" + deltaTableName + "."
//				+ Constant.MD5NAME + ")";
//		deltaDatasql += " and " + yesterdayTableName + "." + Constant.EDATENAME + " = '" + Constant.MAXDATE + "'";
//		deltaDatasql += ")";
//		sqlList.add(deltaDatasql);
		//TODO 上面的sql只能在spark-sql上跑，hive的要拆成下面的两个sql
		String deltaDatasql = "insert into " + deltaTableName;
		deltaDatasql += " select * from " + yesterdayTableName;
		deltaDatasql += " where ";
		deltaDatasql += yesterdayTableName + "." + Constant.EDATENAME + " <> '" + Constant.MAXDATE + "'";
		sqlList.add(deltaDatasql);

		deltaDatasql = "insert into " + deltaTableName;
		deltaDatasql += " select * from " + yesterdayTableName;
		deltaDatasql += " where ( ";
		deltaDatasql += " not exists ";
		deltaDatasql += "(";
		deltaDatasql += " select " + deltaTableName + "." + Constant.MD5NAME + " from " + deltaTableName + " where "
				+ deltaTableName + "." + Constant.EDATENAME + " <> '" + Constant.MAXDATE + "'";
		deltaDatasql += " and " + yesterdayTableName + "." + Constant.MD5NAME + "=" + deltaTableName + "."
				+ Constant.MD5NAME + ")";
		deltaDatasql += " and " + yesterdayTableName + "." + Constant.EDATENAME + " = '" + Constant.MAXDATE + "'";
		deltaDatasql += ")";
		sqlList.add(deltaDatasql);
	}

	/*
	 * 调用insertData的sql，在临时表中插入增量数据
	 */
	private void getInsertDataSql(ArrayList<String> sqlList) {
		String insertDataSql = "";
		// 拼接查找增量并插入增量表
		insertDataSql += "INSERT INTO ";
		insertDataSql += deltaTableName;
		insertDataSql += " select * ";
		insertDataSql += " from ";
		insertDataSql += todayTableName;
		insertDataSql += " WHERE NOT EXISTS ";
		insertDataSql += " ( select * from ";
		insertDataSql += yesterdayTableName;
		insertDataSql += " where ";
		insertDataSql += todayTableName;
		insertDataSql += ".";
		insertDataSql += Constant.MD5NAME;
		insertDataSql += " = ";
		insertDataSql += yesterdayTableName;
		insertDataSql += ".";
		insertDataSql += Constant.MD5NAME;
		insertDataSql += " AND ";
		insertDataSql += yesterdayTableName;
		insertDataSql += ".";
		insertDataSql += Constant.EDATENAME;
		insertDataSql += " = '99991231'";
		insertDataSql += " ) ";
		sqlList.add(insertDataSql);

	}

	/*
	 * 调用deleteData的sql，在增量表中插入增量数据
	 */
	private void getDeleteDataSql(ArrayList<String> sqlList) {

		StringBuilder deleteDatasql = new StringBuilder(120);
		StringBuilder join = new StringBuilder(120);
		for (String column : columns) {
			join.append(this.yesterdayTableName).append(".").append(column).append(",");
		}
		join.delete(join.length() - 1, join.length());
		String select_sql = StringUtils.replace(join.toString(), this.yesterdayTableName + "."
				+ Constant.EDATENAME, "'" + sysDate + "'");
		// 拼接查找增量并插入增量表
		deleteDatasql.append("INSERT INTO ");
		deleteDatasql.append(this.deltaTableName);
		deleteDatasql.append(" select ");
		deleteDatasql.append(select_sql);
		deleteDatasql.append(" from ");
		deleteDatasql.append(this.yesterdayTableName);
		deleteDatasql.append(" WHERE NOT EXISTS ");
		deleteDatasql.append(" ( select * from ");
		deleteDatasql.append(this.todayTableName);
		deleteDatasql.append(" where ");
		deleteDatasql.append(this.yesterdayTableName).append(".").append(Constant.MD5NAME);
		deleteDatasql.append(" = ");
		deleteDatasql.append(this.todayTableName).append(".").append(Constant.MD5NAME);
		deleteDatasql.append(") AND ").append(this.yesterdayTableName).append(".").append(Constant.EDATENAME);
		deleteDatasql.append(" = '99991231'");

		sqlList.add(deleteDatasql.toString());
	}

	/**
	 * 判断是否今天已经跑过且有增量或者关链数据，如果有需要恢复数据到昨天的
	 */
	private boolean haveTodayData(String tableName) {
		ResultSet resultSet;
		ResultSet resultSet2;
		try {
			resultSet = db.queryGetResultSet("select " + Constant.SDATENAME + " from " + tableName
					+ " where " + Constant.SDATENAME + " = '" + sysDate + "' limit 1");
			resultSet2 = db.queryGetResultSet("select " + Constant.EDATENAME + " from " + tableName
					+ " where " + Constant.EDATENAME + " = '" + sysDate + "' limit 1");
			return resultSet.next() || resultSet2.next();
		} catch (Exception e) {
			throw new AppSystemException("执行查询当天增量是否有进数");
		}
	}

	/**
	 * 判断是否今天已经跑过数据，如果有需要恢复数据到昨天的
	 */
	private boolean haveAppendTodayData(String tableName) {
		ResultSet resultSet;
		try {
			resultSet = db.queryGetResultSet("select " + Constant.SDATENAME + " from " + tableName
					+ " where " + Constant.SDATENAME + " = '" + sysDate + "' limit 1");
			return resultSet.next();
		} catch (Exception e) {
			throw new AppSystemException("执行查询当天增量是否有进数");
		}
	}

	/**
	 * 删除临时增量表
	 */
	private void dropAllTmpTable() {
		List<String> deleteInfo = new ArrayList<>();
		//删除临时增量表
		deleteInfo.add("DROP TABLE IF EXISTS " + deltaTableName);
		//清空表数据
		HSqlExecute.executeSql(deleteInfo, db);
	}

	/**
	 * 对象采集，没有数据保留天数，删除当天卸数下来的数据
	 */
	@Override
	public void dropTodayTable() {
		//删除映射表
		List<String> deleteInfo = new ArrayList<>();
		//删除临时增量表
		JDBCIncreasement.dropTableIfExists(todayTableName, db, deleteInfo);
		//清空表数据
		HSqlExecute.executeSql(deleteInfo, db);
	}
}
