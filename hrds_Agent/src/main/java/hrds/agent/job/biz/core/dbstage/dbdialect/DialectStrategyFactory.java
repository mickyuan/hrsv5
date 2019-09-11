package hrds.agent.job.biz.core.dbstage.dbdialect;

import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.DataBaseDialectStrategy;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.MySQLDialectStrategy;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.OracleDialectStrategy;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.PgSQLDialectStrategy;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;

/**
 * ClassName: DialectStrategyFactory <br/>
 * Function: 数据库方言策略工厂 <br/>
 * Reason: 根据数据库类型获取数据库方言策略(简单工厂模式)
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DialectStrategyFactory {

	private static boolean flag;

	private DialectStrategyFactory() {
		if (!flag) {
			flag = true;
		} else {
			throw new AppSystemException("不能多次创建单例对象");
		}
	}

	/* 此处使用一个内部类来维护单例 */
	private static class Inner {
		private static DialectStrategyFactory instance = new DialectStrategyFactory();
	}

	/* 外部用这个方法获取实例 */
	public static DialectStrategyFactory getInstance() {
		return Inner.instance;
	}

	/**
	 * @Description: 根据数据库类型获取对应的数据库分页策略
	 * @Param: dbType：数据库类型, 取值范围 : String (01-13)
	 * @return: DataBaseDialectStrategy
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 * 步骤：
	 *      1、判断数据库类型
	 *      2、根据不同的类型，返回对应数据库的分页方言策略
	 */
	public DataBaseDialectStrategy createDialectStrategy(String dbType) {
		if (StringUtil.isNotEmpty(dbType)) {
			throw new AppSystemException("数据库类型不能为空");
		}
		//1、判断数据库类型
		DatabaseType typeConstant = DatabaseType.getCodeObj(dbType);
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
