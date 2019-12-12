package hrds.agent.trans.biz;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.conf.ConnWay;
import fd.ng.db.conf.DbinfosConf;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;

@DocClass(desc = "数据库直连采集获取数据库连接", author = "WangZhengcheng", createdate = "2019/10/28 14:26")
public class ConnetionTool {

	@Method(desc = "根据数据库配置信息获取数据库连接", logicStep = "" +
			"1、将DBConfigBean对象中的内容封装到dbInfo中" +
			"2、获取数据库类型" +
			"3、根据数据库类型获取对应数据库的数据库连接")
	@Param(name = "dbConfigBean", desc = "该对象封装了海云应用服务端发过来的数据库采集连接数据库的信息", range = "DBConfigBean类型对象")
	@Return(desc = "项目中常用的DatabaseWrapper对象", range = "DatabaseWrapper类型对象")
	public static DatabaseWrapper getDBWrapper(SourceDataConfBean dbConfigBean) {
		//1、将DBConfigBean对象中的内容封装到dbInfo中
		DbinfosConf.Dbinfo dbInfo = new DbinfosConf.Dbinfo();
		dbInfo.setName(DbinfosConf.DEFAULT_DBNAME);
		dbInfo.setDriver(dbConfigBean.getDatabase_drive());
		dbInfo.setUrl(dbConfigBean.getJdbc_url());
		dbInfo.setUsername(dbConfigBean.getUser_name());
		dbInfo.setPassword(dbConfigBean.getDatabase_pad());
		dbInfo.setWay(ConnWay.JDBC);
		//2、获取数据库类型
		DatabaseType typeConstant = DatabaseType.ofEnumByCode(dbConfigBean.getDatabase_type());
		if (typeConstant == DatabaseType.MYSQL) {
			dbInfo.setDbtype(Dbtype.MYSQL);
		} else if (typeConstant == DatabaseType.Oracle9i) {
			throw new AppSystemException("系统不支持Oracle9i及以下");
		} else if (typeConstant == DatabaseType.Oracle10g) {
			dbInfo.setDbtype(Dbtype.ORACLE);
		} else if (typeConstant == DatabaseType.SqlServer2000) {
			dbInfo.setDbtype(Dbtype.SQLSERVER);
		} else if (typeConstant == DatabaseType.SqlServer2005) {
			dbInfo.setDbtype(Dbtype.SQLSERVER);
		} else if (typeConstant == DatabaseType.DB2) {
			//TODO 使用db2v1还是db2v2
		} else if (typeConstant == DatabaseType.SybaseASE125) {
			dbInfo.setDbtype(Dbtype.SYBASE);
		} else if (typeConstant == DatabaseType.Informatic) {
			//Informatic
		} else if (typeConstant == DatabaseType.H2) {
			//H2
		} else if (typeConstant == DatabaseType.ApacheDerby) {
			//ApacheDerBy
		} else if (typeConstant == DatabaseType.Postgresql) {
			dbInfo.setDbtype(Dbtype.POSTGRESQL);
		} else if (typeConstant == DatabaseType.GBase) {
			dbInfo.setDbtype(Dbtype.GBASE);
		} else if (typeConstant == DatabaseType.TeraData) {
			dbInfo.setDbtype(Dbtype.TERADATA);
		} else {
			throw new AppSystemException("系统不支持该数据库类型");
		}
		dbInfo.setShow_conn_time(true);
		dbInfo.setShow_sql(true);
		//3、根据数据库类型获取对应数据库的数据库连接
		return new DatabaseWrapper.Builder().dbconf(dbInfo).create();
	}
}
