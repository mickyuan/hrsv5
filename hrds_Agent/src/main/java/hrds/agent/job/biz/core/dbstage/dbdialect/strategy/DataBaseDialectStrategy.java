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
	 * @Description: 根据对应的数据库分页类型，获取分页SQL
	 * @Param: [strSql : 取数SQL, 取值范围 : String]
	 * @Param: [start : 当前页开始条数, 取值范围 : int]
	 * @Param: [limit : 当前页结束条数, 取值范围 : int]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 */
	String createPageSql(String strSql, int start, int limit);
}
