package hrds.agent.trans.biz;

import fd.ng.core.utils.JsonUtil;
import fd.ng.db.conf.ConnWay;
import fd.ng.db.conf.DbinfosConf;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.util.RequestUtil;
import hrds.commons.codes.DatabaseType;

import java.util.Map;

/**
 * @program: hrsv5
 * @description: 测试连接
 * @author: xchao
 * @create: 2019-09-05 11:18
 */
public class testConnection extends AbstractWebappBaseAction {

    /**
     * 1、通过request获取服务发过来的数据
     * 2、使用dbinfo将需要测试连接的内容填充
     * 3、测试连接
     * @return
     */
    public boolean testConn() {
        // 1、通过request获取服务发过来的数据
        String json = RequestUtil.getJson();
        System.out.println("========="+json);
        Map<String, String> map = JsonUtil.toObject(json, Map.class);
        //2、使用dbinfo将需要测试连接的内容填充
        DbinfosConf.Dbinfo dbinfo = new DbinfosConf.Dbinfo();
        dbinfo.setName(DbinfosConf.DEFAULT_DBNAME);
        dbinfo.setDriver(map.get("driver"));
        dbinfo.setUrl(map.get("url"));
        dbinfo.setUsername(map.get("username"));
        dbinfo.setPassword(map.get("password"));
        dbinfo.setWay(ConnWay.JDBC);
        if (map.get("dbtype").equals(DatabaseType.Postgresql.getCatCode()))
            dbinfo.setDbtype(Dbtype.POSTGRESQL);
        dbinfo.setShow_conn_time(true);
        dbinfo.setShow_sql(true);
        //3、测试连接
        try (DatabaseWrapper db = new DatabaseWrapper.Builder().dbconf(dbinfo).create()) {
            return db.isConnected();
        }
    }
}
