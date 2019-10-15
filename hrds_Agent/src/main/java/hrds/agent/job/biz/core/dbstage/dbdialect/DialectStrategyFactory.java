package hrds.agent.job.biz.core.dbstage.dbdialect;

import fd.ng.core.annotation.Class;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.DataBaseDialectStrategy;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.MySQLDialectStrategy;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.OracleDialectStrategy;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.PgSQLDialectStrategy;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;

@Class(desc = "数据库方言策略工厂，根据数据库类型获取数据库方言策略(简单工厂模式)", author = "WangZhengcheng")
public class DialectStrategyFactory {

	private static boolean flag;

	//私有化构造器，在单例模式中，构造器只能被调用一次，再次调用直接抛异常
	private DialectStrategyFactory() {
		if (!flag) {
			flag = true;
		} else {
			throw new AppSystemException("DialectStrategyFactory类的构造器只能调用一次");
		}
	}

	//此处使用一个内部类来维护单例
	private static class Inner {
		private static DialectStrategyFactory instance = new DialectStrategyFactory();
	}

	//外部用这个方法获取实例
	public static DialectStrategyFactory getInstance() {
		return Inner.instance;
	}

	@Method(desc = "根据数据库类型获取对应的数据库分页策略", logicStep = "" +
			"1、判断数据库类型" +
			"2、根据不同的类型，返回对应数据库的分页方言策略")
	@Param(name = "dbType", desc = "数据库类型", range = "DatabaseType代码项code值")
	@Return(desc = "具体的某种数据库方言策略实例", range = "不会为null")
	public DataBaseDialectStrategy createDialectStrategy(String dbType) {
		if (StringUtil.isNotEmpty(dbType)) {
			throw new AppSystemException("数据库类型不能为空");
		}
		//1、判断数据库类型
		DatabaseType typeConstant = DatabaseType.ofEnumByCode(dbType);
		//2、根据不同的类型，返回对应数据库的分页方言策略
		if (typeConstant == DatabaseType.MYSQL) {
			return new MySQLDialectStrategy();
		} else if (typeConstant == DatabaseType.Oracle9i) {
			//TODO 目前fdcode只有oracle9iAbove
			throw new AppSystemException("系统不支持Oracle9i及以下");
		} else if (typeConstant == DatabaseType.Oracle10g) {
			return new OracleDialectStrategy();
		} else if (typeConstant == DatabaseType.SqlServer2000) {
			//TODO 返回SQLSERVER2000的策略
		} else if (typeConstant == DatabaseType.SqlServer2000) {
			//TODO 返回SQLSERVER2005的策略
		} else if (typeConstant == DatabaseType.DB2) {
			//TODO 返回DB2的策略，但是目前DB2的策略有两个，分别是dbv1,dbv2，需要决定
		} else if (typeConstant == DatabaseType.SybaseASE125) {
			//TODO 返回Sybase的策略
		} else if (typeConstant == DatabaseType.Informatic) {
			//TODO 返回Informatic的策略
		} else if (typeConstant == DatabaseType.H2) {
			//TODO 返回H2的策略
		} else if (typeConstant == DatabaseType.ApacheDerby) {
			//TODO 返回ApacheDervy的策略
		} else if (typeConstant == DatabaseType.Postgresql) {
			return new PgSQLDialectStrategy();
		} else if (typeConstant == DatabaseType.GBase) {
			//TODO 返回GBase策略
		} else if (typeConstant == DatabaseType.TeraData) {
			//TODO 返回TeraData策略
		} else {
			throw new AppSystemException("系统不支持的数据库类型");
		}
		return null;
	}
}
