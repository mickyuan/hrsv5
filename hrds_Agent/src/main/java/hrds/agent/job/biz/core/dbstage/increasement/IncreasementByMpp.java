package hrds.agent.job.biz.core.dbstage.increasement;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
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
	public void calculateIncrement() {
		//1.为了防止第一次执行，yesterdayTableName表不存在，创建空表
		sqlList.add(createTableIfNotExists(yesterdayTableName, db, columns, types));
		//2、创建增量临时表
		getCreateDeltaSql();
		//3、把今天的卸载数据映射成一个表，这里在上传数据的时候加载到了todayTableName这张表。
		//4、为了可以重跑，这边需要把今天（如果今天有进数的话）的数据清除掉
		restoreData();
		//5.将比较之后的需要insert的结果插入到临时表中
		getInsertDataSql();
		//6.将比较之后的需要delete(拉链中的闭链)的结果插入到临时表中
		getDeleteDataSql();
		//7.执行所有sql语句
		HSqlExecute.executeSql(sqlList, db);

	}

	private void restoreData() {
		sqlList.add("delete from " + yesterdayTableName + " where " + Constant.SDATENAME + "='" + sysDate + "'");
		sqlList.add("update " + yesterdayTableName + " set " + Constant.EDATENAME + " = "
				+ Constant.MAXDATE + " where " + Constant.EDATENAME + "='" + sysDate + "'");
	}

	/**
	 * 创建增量表
	 */
	private void getCreateDeltaSql() {
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
	private void getDeleteDataSql() {

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

	private void insertAppendData() {
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
	private void getInsertDataSql() {
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
	 * 根据临时增量表合并出新的增量表，删除以前的增量表
	 */
	@Override
	public void mergeIncrement() {
		List<String> list = new ArrayList<>();
		String tmpDelTa = deltaTableName + "_delta";
		try {
			dropTableIfExists(tmpDelTa, db, list);
			//创建临时表
			list.add(createInvalidDataSql(tmpDelTa));
			//插入有效数据
			list.add(insertInvalidDataSql(tmpDelTa));
			//插入所有数据
			list.add(insertDeltaDataSql(tmpDelTa));
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
	public void append() {
		//1.为了防止第一次执行，yesterdayTableName表不存在，创建空表
		createTableIfNotExists(yesterdayTableName, db, columns, types);
		//2.恢复今天的数据，防止重跑
		restoreAppendData();
		//3.插入今天新增的数据
		insertAppendData();
		//4.执行sql
		HSqlExecute.executeSql(sqlList, db);
	}

	private void restoreAppendData() {
		sqlList.add("DELETE FROM " + yesterdayTableName + " WHERE " + Constant.SDATENAME + "='" + sysDate + "'");
	}

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

	private String insertDeltaDataSql(String tmpDelTa) {
		StringBuilder insertDataSql = new StringBuilder(120);
		//拼接查找增量并插入增量表
		insertDataSql.append("INSERT INTO ");
		insertDataSql.append(tmpDelTa);
		insertDataSql.append("(");
		for (String col : columns) {
			insertDataSql.append(col).append(",");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1); //将最后的逗号删除
		insertDataSql.append(" ) ");
		insertDataSql.append(" select ");
		for (String col : columns) {
			insertDataSql.append(deltaTableName).append(".").append(col).append(",");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1); //将最后的逗号删除
		insertDataSql.append(" from ");
		insertDataSql.append(deltaTableName);
		return insertDataSql.toString();
	}

}
