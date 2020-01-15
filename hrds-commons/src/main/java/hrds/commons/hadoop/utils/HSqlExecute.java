package hrds.commons.hadoop.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.util.Dbo;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

@DocClass(desc = "sql执行的工具类", author = "zxz", createdate = "2019/10/25 11:05")
public class HSqlExecute {
	//打印日志
	private static final Log logger = LogFactory.getLog(HSqlExecute.class);

	@Method(desc = "根据指定的数据库名称连接执行sql",
			logicStep = "1.创建db连接对象" +
					"2.遍历需要执行的sql执行" +
					"3.提交事务")
	@Param(name = "sqlList", desc = "需要执行的sql的合集", range = "不可为空")
	@Param(name = "engineName", desc = "指定的数据库名称", range = "不可为空")
	public static void executeSql(List<String> sqlList, String engineName) {
		//1.创建db连接对象
		try (DatabaseWrapper db = Dbo.db(engineName)) {
			//2.遍历需要执行的sql执行
			for (String sql : sqlList) {
				logger.info("执行 " + engineName + " 的sql为： " + sql);
				db.execute(sql);
			}
			//3.提交事务
			db.commit();
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw (BusinessException) e;
			} else {
				throw new AppSystemException(e);
			}
		}
	}

	@Method(desc = "使用传入的db执行sql",
			logicStep = "1.使用传入的db执行sql")
	@Param(name = "sql", desc = "需要执行的sql的", range = "不可为空")
	@Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
	public static void executeSql(String sql, DatabaseWrapper db) {
		logger.info("执行的sql为： " + sql);
		db.execute(sql);
	}

	@Method(desc = "使用传入的db依次执行集合中sql",
			logicStep = "1.使用传入的db依次执行集合中sql")
	@Param(name = "sql", desc = "需要执行的sql的合集", range = "不可为空")
	@Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
	public static void executeSql(List<String> sqlList, DatabaseWrapper db) {
		for (String sql : sqlList) {
			logger.info("执行的sql为： " + sql);
			db.execute(sql);
		}
	}
}
