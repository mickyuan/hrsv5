package hrds.agent.job.biz.core.dbstage.dbdialect.strategy;

import fd.ng.db.jdbc.nature.PostgreSQL;


/**
 * ClassName: PgSQLDialectStrategy <br/>
 * Function: 数据库方言策略接口PostgreSQL数据库实现类. <br/>
 * Reason:
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class PgSQLDialectStrategy implements DataBaseDialectStrategy {
	/**
	 * @Description: 根据对应的数据库分页类型，获取分页SQL
	 * @Param: [strSql : 取数SQL, 取值范围 : String]
	 * @Param: [start : 当前页开始条数, 取值范围 : int]
	 * @Param: [limit : 当前页结束条数, 取值范围 : int]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 * 步骤：
	 * 1、调用封装好的已有实现获得分页SQL
	 */
	@Override
	public String createPageSql(String strSql, int start, int limit) {
		return PostgreSQL.toPagedSql(strSql, start, limit).getSql();
	}
}
