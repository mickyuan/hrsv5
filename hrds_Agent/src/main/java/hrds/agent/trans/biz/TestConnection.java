package hrds.agent.trans.biz;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;

@DocClass(desc = "测试连接相关接口", author = "xchao", createdate = "2019-09-05 11:18")
public class TestConnection extends AgentBaseAction {

	@Method(desc = "测试连接的方法",
			logicStep = "1、测试连接")
	@Param(name = "dbSet", desc = "数据库连接设置表对象，此对象不能为空的字段必须有值",
			range = "不能为空", isBean = true)
	@Return(desc = "是否连接成功的判断", range = "不会为空")
	public boolean testConn(Database_set dbSet) {
		//1、测试连接
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dbSet)) {
			return db.isConnected();
		}
	}

	@Method(desc = "测试并行抽取SQL", logicStep = "" +
			"1、创建DatabaseWrapper，并执行SQL语句" +
			"2、将并行抽取SQL中的占位符替换为0和1，类似于select * from XXX limit 0 offset 1" +
			"3、如果根据SQL获取到了数据，返回true，否则返回false")
	@Param(name = "dbSet", desc = "数据库连接设置表对象，此对象不能为空的字段必须有值", range = "不能为空", isBean = true)
	@Param(name = "pageSql", desc = "并行抽取使用的分页SQL", range = "不为空")
	@Return(desc = "根据并行抽取是否成功获取数据的判断", range = "不会为空")
	public boolean testParallelSQL(Database_set dbSet, String pageSql) {
		//TODO 这里需要修改
		//1、创建DatabaseWrapper，并执行SQL语句
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dbSet)) {
			//2、将并行抽取SQL中的占位符替换为0和1，类似于select * from XXX limit 0 offset 1
			pageSql = pageSql.replace(Constant.PARALLEL_SQL_START, "0").replace(Constant.PARALLEL_SQL_END, "1");
			//3、如果根据SQL获取到了数据，返回true,否则返回false
			String countSQL = "select count(1) as count from ( " + pageSql + " ) tmp";
			ResultSet resultSet = db.queryGetResultSet(countSQL);
			int rowCount = 0;
			while (resultSet.next()) {
				rowCount = resultSet.getInt("count");
			}
			return rowCount != 0;
		} catch (SQLException e) {
			throw new AppSystemException(e);
		}
	}

	@Method(desc = "根据表名获取该表数据量", logicStep = "" +
			"1、创建DatabaseWrapper，并执行SQL语句" +
			"2、如果根据SQL获取到了数据，返回获取到的数据量")
	@Param(name = "dbSet", desc = "数据库连接设置表对象，此对象不能为空的字段必须有值", range = "不能为空", isBean = true)
	@Param(name = "tableName", desc = "表名", range = "不能为空")
	@Return(desc = "表数据量", range = "查询成功返回表数据量，不成功则返回异常信息，获取到的数据量转为字符串，应用管理端使用字符串获取数目")
	public String getTableCount(Database_set dbSet, String tableName) {
		//1、创建DatabaseWrapper，并执行SQL语句
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dbSet)) {
			//2、如果根据SQL获取到了数据，返回获取到的数据量
			String countSQL = "select count(1) as count from " + tableName;
			ResultSet resultSet = db.queryGetResultSet(countSQL);
			long rowCount = 0;
			while (resultSet.next()) {
				rowCount = resultSet.getLong("count");
			}
			return String.valueOf(rowCount);
		} catch (SQLException e) {
			throw new AppSystemException(e);
		}
	}
}
