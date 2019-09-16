package hrds.agent.trans.biz;

import fd.ng.db.conf.ConnWay;
import fd.ng.db.conf.DbinfosConf;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.annotation.RequestBean;
import hrds.commons.codes.DatabaseType;
import hrds.commons.entity.Database_set;

/**
 * @program: hrsv5
 * @description: 测试连接
 * @author: xchao
 * @create: 2019-09-05 11:18
 */
public class TestConnection extends AbstractWebappBaseAction {

	/**
	 * 1、通过request获取服务发过来的数据
	 * 2、使用dbinfo将需要测试连接的内容填充
	 * 3、测试连接
	 *
	 * @return
	 */
	public boolean testConn(@RequestBean Database_set dbSet) {
		//2、使用dbinfo将需要测试连接的内容填充
		DbinfosConf.Dbinfo dbinfo = new DbinfosConf.Dbinfo();
		dbinfo.setName(DbinfosConf.DEFAULT_DBNAME);
		dbinfo.setDriver(dbSet.getDatabase_drive());
		dbinfo.setUrl(dbSet.getDatabase_drive());
		dbinfo.setUsername(dbSet.getUser_name());
		dbinfo.setPassword(dbSet.getDatabase_pad());
		dbinfo.setWay(ConnWay.JDBC);
		if (dbSet.getDatabase_type().equals(DatabaseType.Postgresql.getCatCode()))
			dbinfo.setDbtype(Dbtype.POSTGRESQL);
		dbinfo.setShow_conn_time(true);
		dbinfo.setShow_sql(true);
		//3、测试连接
		try (DatabaseWrapper db = new DatabaseWrapper.Builder().dbconf(dbinfo).create()) {
			return db.isConnected();
		}
	}
}
