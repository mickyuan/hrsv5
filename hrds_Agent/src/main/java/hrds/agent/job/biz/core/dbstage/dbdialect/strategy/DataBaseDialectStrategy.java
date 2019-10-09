package hrds.agent.job.biz.core.dbstage.dbdialect.strategy;

/**
 * ClassName: DataBaseDialectStrategy <br/>
 * Function: 数据库分页方言策略接口. <br/>
 * Reason: 策略模式
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public interface DataBaseDialectStrategy {

	/**
	 * 根据对应的数据库分页类型，获取分页SQL
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
	String createPageSql(String strSql, int start, int limit);
}
