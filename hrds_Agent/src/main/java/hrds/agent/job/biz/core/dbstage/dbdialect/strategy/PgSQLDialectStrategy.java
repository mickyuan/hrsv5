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
public class PgSQLDialectStrategy implements DataBaseDialectStrategy{
    @Override
    public String createPageSql(String strSql, int start, int limit) {
        return PostgreSQL.toPagedSql(strSql, start, limit).getSql();
    }
}
