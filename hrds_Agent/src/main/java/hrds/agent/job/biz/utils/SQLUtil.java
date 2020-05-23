package hrds.agent.job.biz.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;

import java.sql.ResultSet;
import java.sql.SQLException;

@DocClass(desc = "根据表名和列名获取采集SQL,数据库直连采集", author = "WangZhengcheng")
public class SQLUtil {

	/**
	 * 判断oracle对象是否存在
	 *
	 * @param tableName 表名
	 * @param db        连接方式
	 */
	public static boolean objectIfExistForOracle(String tableName, DatabaseWrapper db) {
		ResultSet resultSet = null;
		try {
			resultSet = db.queryGetResultSet("SELECT * FROM user_objects where  lower(object_name) = '"
					+ tableName.toLowerCase() + "'");
			try {
				return resultSet.next();
			} catch (SQLException e) {
				throw new AppSystemException("检查数据库下表是否存在出现异常，请联系管理员", e);
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 判断Postgresql数据库表是否存在主键
	 *
	 * @param tableName 表名
	 * @param db        连接方式
	 */
	public static boolean pkIfExistForPostgresql(String tableName, DatabaseWrapper db) {
		ResultSet resultSet = null;
		try {
			resultSet = db.queryGetResultSet("SELECT " +
					" pg_constraint.conname AS pk_name " +
					" FROM " +
					" pg_constraint " +
					" INNER JOIN " +
					" pg_class " +
					" ON " +
					" pg_constraint.conrelid = pg_class.oid " +
					" WHERE " +
					" pg_class.relname = ? " +
					" AND pg_constraint.contype= ? ", tableName, "p");
			try {
				return resultSet.next();
			} catch (SQLException e) {
				throw new AppSystemException("检查数据库下表是否存在出现异常，请联系管理员", e);
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 判断Postgresql数据库表是否存在索引
	 *
	 * @param tableName 表名
	 * @param db        连接方式
	 */
	public static boolean indexIfExistForPostgresql(String tableName, DatabaseWrapper db) {
		ResultSet resultSet = null;
		try {
			resultSet = db.queryGetResultSet("select * from pg_indexes where tablename= ? ", tableName);
			try {
				return resultSet.next();
			} catch (SQLException e) {
				throw new AppSystemException("检查数据库下表是否存在出现异常，请联系管理员", e);
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
