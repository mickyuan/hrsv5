package hrds.agent.trans.biz;

import fd.ng.db.conf.ConnWay;
import fd.ng.db.conf.DbinfosConf;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.DBConfigBean;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;

/**
 * ClassName: ConnetionTool <br/>
 * Function: 数据库直连采集获取数据库连接 <br/>
 * Reason: 这个类还可以用于测试数据库连接
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ConnetionTool {

	/**
	 * @Description: 根据数据库配置信息获取数据库连接
	 * @Param: dbInfo：数据库连接配置信息，取值范围 : DBConfigBean类型对象
	 * @return: DatabaseWrapper
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 */
	public static DatabaseWrapper getDBWrapper(DBConfigBean dbInfo) {
		DbinfosConf.Dbinfo dbinfo = new DbinfosConf.Dbinfo();
		dbinfo.setName(DbinfosConf.DEFAULT_DBNAME);
		dbinfo.setDriver(dbInfo.getJdbc_url());
		dbinfo.setUsername(dbInfo.getUser_name());
		dbinfo.setPassword(dbInfo.getDatabase_pad());
		dbinfo.setWay(ConnWay.JDBC);
		DatabaseType typeConstant = DatabaseType.ofEnumByCode(dbInfo.getDatabase_type());
		if (typeConstant == DatabaseType.MYSQL) {
			dbinfo.setDbtype(Dbtype.MYSQL);
		} else if (typeConstant == DatabaseType.Oracle9i) {
			throw new AppSystemException("系统不支持Oracle9i及以下");
		} else if (typeConstant == DatabaseType.Oracle10g) {
			dbinfo.setDbtype(Dbtype.ORACLE);
		} else if (typeConstant == DatabaseType.SqlServer2000) {
			dbinfo.setDbtype(Dbtype.SQLSERVER);
		} else if (typeConstant == DatabaseType.SqlServer2005) {
			dbinfo.setDbtype(Dbtype.SQLSERVER);
		} else if (typeConstant == DatabaseType.DB2) {
			//TODO 使用db2v1还是db2v2
		} else if (typeConstant == DatabaseType.SybaseASE125) {
			dbinfo.setDbtype(Dbtype.SYBASE);
		} else if (typeConstant == DatabaseType.Informatic) {
			//Informatic
		} else if (typeConstant == DatabaseType.H2) {
			//H2
		} else if (typeConstant == DatabaseType.ApacheDerby) {
			//ApacheDerBy
		} else if (typeConstant == DatabaseType.Postgresql) {
			dbinfo.setDbtype(Dbtype.POSTGRESQL);
		} else if (typeConstant == DatabaseType.GBase) {
			dbinfo.setDbtype(Dbtype.GBASE);
		} else if (typeConstant == DatabaseType.TeraData) {
			dbinfo.setDbtype(Dbtype.TERADATA);
		} else {
			throw new AppSystemException("系统不支持该数据库类型");
		}
		dbinfo.setShow_conn_time(true);
		dbinfo.setShow_sql(true);
		return new DatabaseWrapper.Builder().dbconf(dbinfo).create();
	}
}
