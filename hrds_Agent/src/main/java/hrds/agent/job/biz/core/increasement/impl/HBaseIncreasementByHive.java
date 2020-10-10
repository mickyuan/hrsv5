package hrds.agent.job.biz.core.increasement.impl;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.increasement.HBaseIncreasement;
import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.ResultSet;

/**
 * HBaseIncreasementByHive
 * date: 2020/7/20 15:14
 * author: zxz
 */
public class HBaseIncreasementByHive extends HBaseIncreasement {
	//打印日志
	private static final Logger logger = LogManager.getLogger();

	public HBaseIncreasementByHive(TableBean tableBean, String hbase_name, String sysDate, String dsl_name,
								   String hadoop_user_name, String platform, String prncipal_name, DatabaseWrapper db) {
		super(tableBean, hbase_name, sysDate, dsl_name, hadoop_user_name, platform, prncipal_name, db);
	}

	@Override
	public void calculateIncrement() throws Exception {
		//1.创建当天加载的HBase表在hive上的映射表
		hiveMapHBase(todayTableName);
		//2.为了防止第一次执行，tableNameInHBase表不存在，创建空表
		if (!helper.existsTable(tableNameInHBase)) {
			//默认是不压缩   TODO 是否压缩需要从页面配置
			createDefaultPrePartTable(helper, tableNameInHBase, false);
		}
		//3.创建hive映射HBase的表
		hiveMapHBase(tableNameInHBase);
		//4.创建增量表
		getCreateDeltaSql();
		//5.把今天的卸载数据映射成一个表，这里在上传数据的时候加载到了todayTableName这张表。
		//6.为了可以重跑，这边需要把今天（如果今天有进数的话）的数据清除
		restore(StorageType.ZengLiang.getCode());
		//7.将比较之后的要insert的结果插入到增量表中
		getInsertDataSql();
		//8.将比较之后的要delete(拉链中的闭链)的结果插入到临时表中
		getDeleteDataSql();
		//9.把全量数据中的除了有效数据且关链的数据以外的所有数据插入到临时表中
		getDeltaDataSql();
	}

	@Override
	public void mergeIncrement() throws Exception {
		//删除历史表
		helper.dropTable(tableNameInHBase);
		//将增量表重命名为历史表
		helper.renameTable(deltaTableName, tableNameInHBase);
		//删除hive增量表的映射
		db.execute("DROP TABLE IF EXISTS " + deltaTableName);
	}

	@Override
	public void append() {
		try {
			//1.创建当天加载的HBase表在hive上的映射表
			hiveMapHBase(todayTableName);
			//2.为了防止第一次执行，yesterdayTableName表不存在，创建空表
			if (!helper.existsTable(tableNameInHBase)) {
				//默认是不压缩   TODO 是否压缩需要从页面配置
				createDefaultPrePartTable(helper, tableNameInHBase, false);
			}
			//3.创建hive映射HBase的表
			hiveMapHBase(tableNameInHBase);
			//4.恢复历史表的数据，防止重跑
			restore(StorageType.ZhuiJia.getCode());
			//5.将今天的数据插入历史表
			db.execute(insertAppendSql(tableNameInHBase, todayTableName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void replace() {
		try {
			//历史表存在，删除历史表
			if (helper.existsTable(tableNameInHBase)) {
				helper.dropTable(tableNameInHBase);
			}
			//将当天加载的表的数据克隆到历史表
			helper.cloneTable(todayTableName, tableNameInHBase);
			//3.创建hive映射HBase的表
			hiveMapHBase(tableNameInHBase);
		} catch (IOException e) {
			throw new AppSystemException("替换HBase表失败", e);
		}
	}

	/**
	 * 采集增量数据算拉链
	 */
	@Override
	public void incrementalDataZipper() throws Exception {
		//1.创建当天加载的HBase表在hive上的映射表
		hiveMapHBase(todayTableName);
		//1.为了防止第一次执行，tableNameInHBase表不存在，创建空表
		if (!helper.existsTable(tableNameInHBase)) {
			//默认是不压缩   TODO 是否压缩需要从页面配置
			createDefaultPrePartTable(helper, tableNameInHBase, false);
		}
		//2.创建hive映射HBase的表
		hiveMapHBase(tableNameInHBase);
		//3.创建增量临时表
		getCreateDeltaSql();
		//3.防止重跑恢复今天入库的数据
		restore(StorageType.ZengLiang.getCode());
		//4.找出需要关链的数据，插入临时表
		insertInvalidDataSql();
		//5.插入有效数据
		insertValidDataSql();
		//6.插入增量数据
		// 拼接查找增量并插入增量表
		String insertDataSql = "INSERT INTO " + deltaTableName + "(" + Constant.HIVEMAPPINGROWKEY + ","
				+ StringUtils.join(columns, ',').toLowerCase() + ") SELECT "
				+ getConcatSelectColumn(todayTableName) + "  FROM " + todayTableName;
		db.execute(insertDataSql);
		//7.删除上次采集的数据表
		//删除历史表
		helper.dropTable(tableNameInHBase);
		//将增量表重命名为历史表
		helper.renameTable(deltaTableName, tableNameInHBase);
		//删除hive增量表的映射
		db.execute("DROP TABLE IF EXISTS " + deltaTableName);
	}

	private void insertValidDataSql() {
		// 拼接查找增量并插入增量表
		String deleteDatasql = "INSERT INTO " +
				this.deltaTableName +
				" select *  from " +
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
		db.execute(deleteDatasql);
		// 拼接查找增量并插入增量表
		String deleteDatasql2 = "INSERT INTO " +
				this.deltaTableName +
				" select *  from " +
				this.yesterdayTableName +
				" WHERE " + this.yesterdayTableName + "." + Constant.EDATENAME +
				" <> '" + Constant.MAXDATE + "'";
		db.execute(deleteDatasql2);
	}

	private void insertInvalidDataSql() {
		StringBuilder deleteDatasql = new StringBuilder(120);
		StringBuilder join = new StringBuilder(120);
		join.append(this.yesterdayTableName).append(".").append(Constant.HIVEMAPPINGROWKEY).append(",");
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
		db.execute(deleteDatasql.toString());
	}

	/**
	 * 恢复数据
	 *
	 * @param storageType 拉链算法存储模式
	 */
	@Override
	public void restore(String storageType) {
		String join = Constant.HIVEMAPPINGROWKEY + "," + StringUtils.join(columns, ',').toLowerCase();
		try {
			if (StorageType.ZengLiang.getCode().equals(storageType)) {
				//增量恢复数据
				if (!haveTodayData(yesterdayTableName)) {
					return;
				}
				//创建HBase临时表，来接受历史表中不是今天的数据
				//默认是不压缩   TODO 是否压缩需要从页面配置
				createDefaultPrePartTable(helper, yesterdayTableName + "_restore", false);
				hiveMapHBase(yesterdayTableName + "_restore");
				//找出不是今天的数据,来实现恢复数据
				String after = "case " + Constant.EDATENAME + " when '" + sysDate + "' then '" + Constant.MAXDATE + "' else "
						+ Constant.EDATENAME + " end " + Constant.EDATENAME;
				join = StringUtils.replace(join, Constant.EDATENAME, after);
				//将历史表中的不是今天的数据插入临时表
				db.execute("INSERT INTO TABLE " + yesterdayTableName + "_restore (" + join + ") SELECT " + join
						+ " FROM " + yesterdayTableName
						+ " where " + Constant.SDATENAME + "<>'" + sysDate + "'");
				//删除HBase历史表，同时将HBase临时表名重命名为历史表名
				helper.dropTable(yesterdayTableName);
				helper.renameTable(yesterdayTableName + "_restore", yesterdayTableName);
				//删除hive临时表的映射表
				db.execute("DROP TABLE IF EXISTS " + yesterdayTableName + "_restore");
			} else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
				//追加恢复数据
				if (!haveAppendTodayData(yesterdayTableName)) {
					return;
				}
				//找出不是今天的数据,来实现恢复数据
				//创建HBase临时表，来接受历史表中不是今天的数据
				//默认是不压缩   TODO 是否压缩需要从页面配置
				createDefaultPrePartTable(helper, yesterdayTableName + "_restore", false);
				hiveMapHBase(yesterdayTableName + "_restore");
				//将历史表中的不是今天的数据插入临时表
				db.execute("INSERT INTO TABLE " + yesterdayTableName + "_restore (" + join + ") SELECT " + join
						+ " FROM " + yesterdayTableName
						+ " where " + Constant.SDATENAME + "<>'" + sysDate + "'");
				//删除HBase历史表，同时将HBase临时表名重命名为历史表名
				helper.dropTable(yesterdayTableName);
				helper.renameTable(yesterdayTableName + "_restore", yesterdayTableName);
				//删除hive临时表的映射表
				db.execute("DROP TABLE IF EXISTS " + yesterdayTableName + "_restore");
			} else if (StorageType.TiHuan.getCode().equals(storageType)) {
				logger.info("替换，不需要恢复当天数据");
			} else {
				throw new AppSystemException("错误的增量拉链参数代码项");
			}
		} catch (Exception e) {
			throw new AppSystemException("恢复当天表的数据失败");
		}
	}

	/**
	 * 把全量数据中的除了有效数据且关链的数据以外的所有数据插入到临时表中
	 */
	private void getDeltaDataSql() {
		String insertColumn = Constant.HIVEMAPPINGROWKEY + "," + StringUtils.join(columns, ',').toLowerCase();
		String deleteDataSql = "INSERT INTO " + deltaTableName + "(" + insertColumn + ") SELECT " + getSelectColumn(yesterdayTableName)
				+ " FROM " + yesterdayTableName + " where " + yesterdayTableName + "." + Constant.EDATENAME + " <> '"
				+ Constant.MAXDATE + "'";
		db.execute(deleteDataSql);

		String deleteDataSql2 = "INSERT INTO " + deltaTableName + "(" + insertColumn + ") SELECT " + getSelectColumn(yesterdayTableName)
				+ " FROM " + yesterdayTableName + " where (  not exists ( select " + deltaTableName + "." + Constant.MD5NAME
				+ " from " + deltaTableName + " where " + deltaTableName + "." + Constant.EDATENAME + " <> '"
				+ Constant.MAXDATE + "' and " + yesterdayTableName + "." + Constant.MD5NAME + "="
				+ deltaTableName + "." + Constant.MD5NAME + ") and " + yesterdayTableName + "."
				+ Constant.EDATENAME + " = '" + Constant.MAXDATE + "')";
		db.execute(deleteDataSql2);
	}

	/**
	 * 将比较之后的要delete(拉链中的闭链)的结果插入到临时表中
	 */
	private void getDeleteDataSql() {
		StringBuilder deleteDataSql = new StringBuilder(120);
		String insertColumn = Constant.HIVEMAPPINGROWKEY + "," + StringUtils.join(columns, ',').toLowerCase();
		String selectColumn = getSelectColumn(yesterdayTableName);
		selectColumn = StringUtils.replace(selectColumn, yesterdayTableName + "." + Constant.EDATENAME,
				"'" + sysDate + "'");
		// 拼接查找增量并插入增量表
		deleteDataSql.append("INSERT INTO ").append(deltaTableName).append("(").append(insertColumn).append(")");
		deleteDataSql.append(" SELECT ").append(selectColumn).append(" FROM ").append(yesterdayTableName);
		deleteDataSql.append(" WHERE NOT EXISTS ").append(" ( SELECT ").append(getSelectColumn(todayTableName))
				.append(" FROM ").append(todayTableName);
		deleteDataSql.append(" WHERE ").append(yesterdayTableName).append(".").append(Constant.MD5NAME);
		deleteDataSql.append(" = ").append(todayTableName).append(".").append(Constant.MD5NAME);
		deleteDataSql.append(") AND ").append(yesterdayTableName).append(".").append(Constant.EDATENAME);
		deleteDataSql.append(" = '").append(Constant.MAXDATE).append("'");
		db.execute(deleteDataSql.toString());
	}

	/**
	 * 将比较之后的要insert的结果插入到增量表中
	 */
	private void getInsertDataSql() {
		String insertColumn = Constant.HIVEMAPPINGROWKEY + "," + StringUtils.join(columns, ',').toLowerCase();
		// 拼接查找增量并插入增量表
		String insertDataSql = "INSERT INTO " + deltaTableName + "(" + insertColumn + ") SELECT "
				+ getConcatSelectColumn(todayTableName) + "  FROM " + todayTableName + " WHERE NOT EXISTS  ( SELECT "
				+ getSelectColumn(yesterdayTableName) + " FROM " + yesterdayTableName + " where "
				+ todayTableName + "." + Constant.MD5NAME + " = " + yesterdayTableName + "."
				+ Constant.MD5NAME + " AND " + yesterdayTableName + "." + Constant.EDATENAME + " = '"
				+ Constant.MAXDATE + "'" + " ) ";
		db.execute(insertDataSql);
	}

	/**
	 * 获取表查询的字段拼接的sql
	 *
	 * @param todayTableName 表名
	 */
	private String getConcatSelectColumn(String todayTableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("concat(").append(todayTableName).append(".").append(Constant.HIVEMAPPINGROWKEY).append(",'_")
				.append(sysDate).append("'").append(") as ").append(Constant.HIVEMAPPINGROWKEY).append(",");
		for (String column : columns) {
			sb.append(todayTableName).append(".").append(column).append(" as ").
					append(column).append(",");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	private String getSelectColumn(String todayTableName) {
		StringBuilder sb = new StringBuilder();
		sb.append(todayTableName).append(".").append(Constant.HIVEMAPPINGROWKEY).append(" as ").
				append(Constant.HIVEMAPPINGROWKEY).append(",");
		for (String column : columns) {
			sb.append(todayTableName).append(".").append(column).append(" as ").
					append(column).append(",");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	/**
	 * 创建增量表
	 */
	private void getCreateDeltaSql() throws Exception {
		// 如果表已存在则删除
		if (helper.existsTable(deltaTableName)) {
			helper.dropTable(deltaTableName);
		}
		createDefaultPrePartTable(helper, deltaTableName, false);
		hiveMapHBase(deltaTableName);

	}

	/**
	 * 创建hbase表的hive映射表
	 *
	 * @param tableName 表名
	 */
	private void hiveMapHBase(String tableName) {
		try {
			StringBuilder sql = new StringBuilder(1024);
			//表存在则删除，这里外部表删除并不会删除HBase的表
			db.execute("DROP TABLE IF EXISTS " + tableName);
			sql.append("CREATE EXTERNAL TABLE IF NOT EXISTS ").append(tableName).append(" ( ").
					append(Constant.HIVEMAPPINGROWKEY).append(" string , ");
			for (int i = 0; i < columns.size(); i++) {
				sql.append("`").append(columns.get(i)).append("` ").append(types.get(i)).append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(") row format delimited fields terminated by '\\t' ");
			sql.append("STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' ");
			sql.append("WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key , ");

			for (String column : columns) {
				sql.append(Bytes.toString(Constant.HBASE_COLUMN_FAMILY)).append(":").append(column).append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append("\") TBLPROPERTIES (\"hbase.table.name\" = \"").append(tableName).append("\")");
			db.execute(sql.toString());
		} catch (Exception e) {
			throw new AppSystemException("hive映射HBase表失败", e);
		}
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
	 * 拼接追加插入到另一张表的sql
	 *
	 * @param targetTableName 目标表名
	 * @param sourceTableName 源表名
	 * @return 拼接的sql
	 */
	private String insertAppendSql(String targetTableName, String sourceTableName) {
		StringBuilder insertDataSql = new StringBuilder(120);
		//拼接查找增量并插入增量表
		insertDataSql.append("INSERT INTO ");
		insertDataSql.append(targetTableName);
		insertDataSql.append("(").append(Constant.HIVEMAPPINGROWKEY).append(",");
		for (String col : columns) {
			insertDataSql.append(col.toLowerCase()).append(",");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1); //将最后的逗号删除
		insertDataSql.append(" ) ");
		insertDataSql.append(" select ").append("concat(").append(sourceTableName).append(".").
				append(Constant.HIVEMAPPINGROWKEY).append(",'_").append(sysDate).append("'").append("),");
		for (String col : columns) {
			insertDataSql.append(sourceTableName).append(".").append(col.toLowerCase()).append(",");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1); //将最后的逗号删除
		insertDataSql.append(" from ");
		insertDataSql.append(sourceTableName);
		return insertDataSql.toString();
	}
}
