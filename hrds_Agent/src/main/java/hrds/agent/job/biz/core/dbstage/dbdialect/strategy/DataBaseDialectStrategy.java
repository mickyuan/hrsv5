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
    * @Description:根据对应的数据库分页类型，获取分页SQL
    * @Param:strSql：取数SQL
    * @Param:start：当前页开始条数
    * @Param:limit：当前页结束条数
    * @return:
    * @Author: WangZhengcheng
    * @Date: 2019/8/13
    */
    String createPageSql(String strSql, int start, int limit);
}
