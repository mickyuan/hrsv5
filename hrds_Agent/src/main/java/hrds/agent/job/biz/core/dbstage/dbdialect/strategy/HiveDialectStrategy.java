package hrds.agent.job.biz.core.dbstage.dbdialect.strategy;

import fd.ng.db.jdbc.nature.DB2V2;

/**
 * ClassName: MySQLDialectStrategy <br/>
 * Function: 数据库方言策略接口Hive数据库实现类. <br/>
 * Reason:
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class HiveDialectStrategy implements DataBaseDialectStrategy {
	@Override
	public String createPageSql(String strSql, int start, int limit) {
		return DB2V2.toPagedSql(strSql, start, limit).getSql();
	}
}
