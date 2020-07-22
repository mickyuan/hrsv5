package hrds.agent.job.biz.core.increasement.impl;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.increasement.HBaseIncreasement;
import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HashChoreWoker;
import hrds.commons.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.sql.ResultSet;

/**
 * HBaseIncreasementByHive
 * date: 2020/7/20 15:14
 * author: zxz
 */
public class HBaseIncreasementByHive extends HBaseIncreasement {
	private static final Log logger = LogFactory.getLog(HBaseIncreasementByHive.class);

	public HBaseIncreasementByHive(TableBean tableBean, String hbase_name,
	                               String sysDate, String dsl_name, DatabaseWrapper db) {
		super(tableBean, hbase_name, sysDate, dsl_name, db);
	}

	@Override
	public void calculateIncrement() throws Exception {

	}

	@Override
	public void mergeIncrement() throws Exception {

	}

	@Override
	public void append() {
		try {
			//1.创建hive映射HBase的映射表
			hiveMapHBase(todayTableName);
			//2.判断HBase的历史表是否存在，不存在则创建
			if (!helper.existsTable(tableNameInHBase)) {
				// 预分区建表
				HashChoreWoker worker = new HashChoreWoker(1000000, 10);
				byte[][] splitKeys = worker.calcSplitKeys();
				//默认是不压缩   TODO 是否压缩需要从页面配置
				helper.createTable(tableNameInHBase, splitKeys, false,
						Bytes.toString(Constant.HBASE_COLUMN_FAMILY));
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

	/**
	 * 恢复数据
	 *
	 * @param storageType 拉链算法存储模式
	 */
	@Override
	public void restore(String storageType) {
		String join = "hyren_key" + "," + StringUtils.join(columns, ',').toLowerCase();
		try {
			if (StorageType.ZengLiang.getCode().equals(storageType)) {
				//增量恢复数据
				if (!haveTodayData(yesterdayTableName)) {
					return;
				}
				//创建HBase临时表，来接受历史表中不是今天的数据
				// 预分区建表
				HashChoreWoker worker = new HashChoreWoker(1000000, 10);
				byte[][] splitKeys = worker.calcSplitKeys();
				//默认是不压缩   TODO 是否压缩需要从页面配置
				helper.createTable(yesterdayTableName + "_restore", splitKeys, false,
						Bytes.toString(Constant.HBASE_COLUMN_FAMILY));
				hiveMapHBase(yesterdayTableName + "_restore");
				//找出不是今天的数据,来实现恢复数据
				String after = "case " + Constant.EDATENAME + " when '" + sysDate + "' then '" + Constant.MAXDATE + "' else "
						+ Constant.EDATENAME + " end " + Constant.EDATENAME;
				join = StringUtils.replace(join, Constant.EDATENAME, after);
				//将历史表中的不是今天的数据插入临时表
				db.execute("INSERT INTO TABLE " + yesterdayTableName + "_restore (" + join + ") SELECT " + join
						+ " FROM " + yesterdayTableName
						+ " where " + Constant.SDATENAME + "<>'" + sysDate + "'");
				//删除临时表
				db.execute("DROP TABLE IF EXISTS " + yesterdayTableName + "_restore");
				//删除HBase历史表，同时将HBase临时表名重命名为历史表名
				helper.dropTable(yesterdayTableName);
				helper.renameTable(yesterdayTableName + "_restore", yesterdayTableName);
			} else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
				//追加恢复数据
				if (!haveAppendTodayData(yesterdayTableName)) {
					return;
				}
				//找出不是今天的数据,来实现恢复数据
				//创建HBase临时表，来接受历史表中不是今天的数据
				// 预分区建表
				HashChoreWoker worker = new HashChoreWoker(1000000, 10);
				byte[][] splitKeys = worker.calcSplitKeys();
				//默认是不压缩   TODO 是否压缩需要从页面配置
				helper.createTable(yesterdayTableName + "_restore", splitKeys, false,
						Bytes.toString(Constant.HBASE_COLUMN_FAMILY));
				hiveMapHBase(yesterdayTableName + "_restore");
				//将历史表中的不是今天的数据插入临时表
				db.execute("INSERT INTO TABLE " + yesterdayTableName + "_restore (" + join + ") SELECT " + join
						+ " FROM " + yesterdayTableName
						+ " where " + Constant.SDATENAME + "<>'" + sysDate + "'");
				//删除临时表
				db.execute("drop table if exists " + yesterdayTableName + "_restore");
				//删除HBase历史表，同时将HBase临时表名重命名为历史表名
				helper.dropTable(yesterdayTableName);
				helper.renameTable(yesterdayTableName + "_restore", yesterdayTableName);
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
	 * 创建hbase表的hive映射表
	 *
	 * @param tableName 表名
	 */
	private void hiveMapHBase(String tableName) {
		try {
			StringBuilder sql = new StringBuilder(1024);
			//表存在则删除
			db.execute("drop table if exists " + tableName);
			sql.append("create external table if not exists ").append(tableName).append(" ( hyren_key string , ");
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
		insertDataSql.append("(").append("hyren_key,");
		for (String col : columns) {
			insertDataSql.append(col.toLowerCase()).append(",");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1); //将最后的逗号删除
		insertDataSql.append(" ) ");
		insertDataSql.append(" select ").append("concat(").append(sourceTableName).append(".hyren_key,'_")
				.append(sysDate).append("'").append("),");
		for (String col : columns) {
			insertDataSql.append(sourceTableName).append(".").append(col.toLowerCase()).append(",");
		}
		insertDataSql.deleteCharAt(insertDataSql.length() - 1); //将最后的逗号删除
		insertDataSql.append(" from ");
		insertDataSql.append(sourceTableName);
		return insertDataSql.toString();
	}
}
