package hrds.agent.job.biz.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@DocClass(desc = "根据表名和列名获取采集SQL,数据库直连采集", author = "WangZhengcheng")
public class SQLUtil {
	@Method(desc = "根据表名和列名获取采集SQL, 根据不同数据库的类型对SQL语句中的列名进行处理", logicStep = "" +
			"1、获得数据库类型的枚举" +
			"2、判断数据库类型，在每一列后面加逗号" +
			"       2-1、如果数据库类型是MySQL,则对每一列用飘号包裹" +
			"       2-2、对其他类型的数据库，除了加逗号之外不做特殊处理" +
			"3、去掉最后一列的最后一个逗号" +
			"4、组装完整的SQL语句" +
			"       4-1、如果数据库类型是MySQL,则对表名用飘号包裹" +
			"       4-2、如果数据库类型是Oracle，则进行特殊处理" +
			"       4-3、除上述两种数据库，其他数据库不做特殊处理")
	@Param(name = "tableName", desc = "当前采集作业采集的数据库表名", range = "不为空")
	@Param(name = "columnName", desc = "要采集的列名",
			range = "不为空，并且Set集合中的元素不能重复，即一张表里不能有重名的字段")
	@Param(name = "dbType", desc = "数据库类型", range = "不为空，DatabaseType代码项code值")
	@Return(desc = "经过处理后生成的采集SQL语句", range = "不会为null")
	public static String getCollectSQL(String tableName, Set<String> columnName, String dbType) {
		//1、获得数据库类型的枚举
		DatabaseType typeConstant = DatabaseType.ofEnumByCode(dbType);
		StringBuilder columnSB = new StringBuilder();
		//2、判断数据库类型，在每一列后面加逗号
		for (String s : columnName) {
			//2-1、如果数据库类型是MySQL,则对每一列用飘号包裹
			if (typeConstant == DatabaseType.MYSQL) {
				columnSB.append(Constant.MYSQL_ESCAPES).append(s)
						.append(Constant.MYSQL_ESCAPES).append(Constant.COLUMN_SEPARATOR);
			} else {
				//2-2、对其他类型的数据库，除了加逗号之外不做特殊处理
				columnSB.append(s).append(Constant.COLUMN_SEPARATOR);
			}
		}
		//3、去掉最后一列的最后一个逗号
		String column = columnSB.toString().substring(0, columnSB.toString().length() - 1);
		//4、组装完整的SQL语句
		if (typeConstant == DatabaseType.MYSQL) {
			//4-1、如果数据库类型是MySQL,则对表名用飘号包裹
			return "select " + column + " from " + Constant.MYSQL_ESCAPES + tableName
					+ Constant.MYSQL_ESCAPES;
		} else if (typeConstant == DatabaseType.Oracle9i || typeConstant == DatabaseType.Oracle10g) {
			//4-2、如果数据库类型是Oracle，则进行如下处理
			return "select " + column + " from " + tableName;
		} else {
			//4-3、对其他数据库，不做特殊处理
			return "select " + column + " from " + tableName;
		}
	}

	/**
	 * 检查数据库下的表是否存在
	 *
	 * @param tableName 表名
	 * @param type      数据库类型
	 * @param database  数据库名称
	 * @param db        数据库连接
	 * @return 存在 true 或者 不存在  false
	 */
	public static boolean checkTable(String tableName, String type, String database, DatabaseWrapper db) {
		ResultSet resultSet = null;
		boolean next;
		try {
			if (DatabaseType.MYSQL.getCode().equals(type)) {
				resultSet = db.queryGetResultSet("select TABLE_NAME from INFORMATION_SCHEMA.TABLES " +
						"where TABLE_SCHEMA= ? and TABLE_NAME=?", database, tableName.toUpperCase());
			} else if (DatabaseType.Oracle10g.getCode().equals(type) || DatabaseType.Oracle9i.getCode().equals(type)) {
				resultSet = db.queryGetResultSet("select * from all_tables where TABLE_NAME = ? "
						, tableName.toUpperCase());
			} else if (DatabaseType.DB2.getCode().equals(type)) {
				resultSet = db.queryGetResultSet("select * from syscat.tables where " +
						"lower(tabname)= lower(?) ", tableName);
			} else if (DatabaseType.SqlServer2005.getCode().equals(type)
					|| DatabaseType.SqlServer2000.getCode().equals(type)) {
				resultSet = db.queryGetResultSet("select * from sysobjects where name= ? ", tableName.toUpperCase());
			} else if (DatabaseType.Postgresql.getCode().equals(type)) {
				resultSet = db.queryGetResultSet("select schemaname,tablename from pg_tables where tablename = ? ",
						tableName.toLowerCase());
			} else if (DatabaseType.SybaseASE125.getCode().equals(type)) {
				resultSet = db.queryGetResultSet("select 1 from sysobjects where name= ? and sysstat & 15 = 4 ",
						tableName.toUpperCase());
			} else {
				throw new AppSystemException("目前不支持该数据库，请联系管理员");
			}
			try {
				next = resultSet.next();
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
		return next;
	}

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
