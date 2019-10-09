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
	/**
	 * Hive根据对应的数据库分页类型，获取分页SQL
	 *
	 * 1、调用封装好的已有实现获得分页SQL
	 *
	 * @Param: strSql String
	 *         含义：采集SQL
	 *         取值范围：不为空
	 * @Param: start int
	 *         含义：当前页开始条数
	 *         取值范围：不限
	 * @Param: limit int
	 *         含义：当前页结束条数
	 *         取值范围：不限
	 *
	 * @return: String
	 *          含义：根据参数组装成的用于分页查询的SQL
	 *          取值范围：不会为null
	 *
	 * */
	@Override
	public String createPageSql(String strSql, int start, int limit) {
		return DB2V2.toPagedSql(strSql, start, limit).getSql();
	}
}
