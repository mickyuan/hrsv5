package hrds.agent.job.biz.core.dbstage.dbdialect.strategy;

import fd.ng.db.jdbc.nature.Oracle9iAbove;

/**
 * ClassName: OracleDialectStrategy <br/>
 * Function: 数据库方言策略接口Oracle数据库实现类. <br/>
 * Reason: Oracle9i以上
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class OracleDialectStrategy implements DataBaseDialectStrategy{
    @Override
    public String createPageSql(String strSql, int start, int limit) {
        return Oracle9iAbove.toPagedSql(strSql, start, limit).getSql();
    }
}
