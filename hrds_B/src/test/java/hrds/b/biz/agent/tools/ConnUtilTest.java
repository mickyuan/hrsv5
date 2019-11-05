package hrds.b.biz.agent.tools;

import hrds.b.biz.agent.bean.DBConnectionProp;
import hrds.commons.codes.DatabaseType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @Description: ConnUtil测试类
 * @Author: wangz
 * @CreateTime: 2019-09-24-10:33
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.b.biz.agent.tools
 **/
public class ConnUtilTest {

	/**
	 * 测试根据数据库类型获取数据库连接信息填写模板
	 *
	 * 正确的使用场景1、构建mysql数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景2、构建oracle9i数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景3、构建oracle10g数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景4、构建DB2数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景5、构建SqlServer2000数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景6、构建SqlServer2005数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景7、构建Postgresql数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景8、构建SybaseASE125数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景9、构建ApacheDerby数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景10、构建GBase数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景11、构建TeraData数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景12、构建Informatic数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景13、构建H2数据库访问场景，断言得到的数据是否正确
	 *
	 * 错误的使用场景1、构建错误的数据库访问场景，断言代码对错误逻辑的处理是否符合预期
	 *
	 * 错误的使用场景未达到三项原因：一个错误的使用场景即可代表所有的不合法访问方式
	 *
	 * @Param: 无
	 *
	 * @return: 无
	 *
	 * */
	@Test
	public void getDBConnectionProp(){
		//正确的使用场景1、构建mysql数据库访问场景，断言得到的数据是否正确
		DBConnectionProp mysqlConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.MYSQL.getCode(), "");
		assertThat(mysqlConnDBConnectionProp == null, is(false));
		assertThat(mysqlConnDBConnectionProp.getUrlPrefix(), is("jdbc:mysql://"));
		assertThat(mysqlConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(mysqlConnDBConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(mysqlConnDBConnectionProp.getUrlSuffix(), is("?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"));

		//正确的使用场景2、构建oracle9i数据库访问场景，断言得到的数据是否正确
		DBConnectionProp oracle9IConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.Oracle9i.getCode(), "");
		assertThat(oracle9IConnDBConnectionProp == null, is(false));
		assertThat(oracle9IConnDBConnectionProp.getUrlPrefix(), is("jdbc:oracle:thin:@"));
		assertThat(oracle9IConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(oracle9IConnDBConnectionProp.getPortPlaceholder(), is(":"));
		assertThat(oracle9IConnDBConnectionProp.getUrlSuffix(), is(""));

	    //正确的使用场景3、构建oracle10g数据库访问场景，断言得到的数据是否正确
		DBConnectionProp oracle10GConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.Oracle10g.getCode(), "");
		assertThat(oracle10GConnDBConnectionProp == null, is(false));
		assertThat(oracle10GConnDBConnectionProp.getUrlPrefix(), is("jdbc:oracle:thin:@"));
		assertThat(oracle10GConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(oracle10GConnDBConnectionProp.getPortPlaceholder(), is(":"));
		assertThat(oracle10GConnDBConnectionProp.getUrlSuffix(), is(""));

		//正确的使用场景4、构建DB2数据库访问场景，断言得到的数据是否正确
		DBConnectionProp db2ConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.DB2.getCode(), "");
		assertThat(db2ConnDBConnectionProp == null, is(false));
		assertThat(db2ConnDBConnectionProp.getUrlPrefix(), is("jdbc:db2://"));
		assertThat(db2ConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(db2ConnDBConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(db2ConnDBConnectionProp.getUrlSuffix(), is(""));

		//正确的使用场景5、构建SqlServer2000数据库访问场景，断言得到的数据是否正确
		DBConnectionProp sqlServer2000ConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.SqlServer2000.getCode(), "");
		assertThat(sqlServer2000ConnDBConnectionProp == null, is(false));
		assertThat(sqlServer2000ConnDBConnectionProp.getUrlPrefix(), is("jdbc:sqlserver://"));
		assertThat(sqlServer2000ConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(sqlServer2000ConnDBConnectionProp.getPortPlaceholder(), is(";DatabaseName="));
		assertThat(sqlServer2000ConnDBConnectionProp.getUrlSuffix(), is(""));

		//正确的使用场景6、构建SqlServer2005数据库访问场景，断言得到的数据是否正确
		DBConnectionProp sqlServer2005ConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.SqlServer2005.getCode(), "");
		assertThat(sqlServer2005ConnDBConnectionProp == null, is(false));
		assertThat(sqlServer2005ConnDBConnectionProp.getUrlPrefix(), is("jdbc:sqlserver://"));
		assertThat(sqlServer2005ConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(sqlServer2005ConnDBConnectionProp.getPortPlaceholder(), is(";DatabaseName="));
		assertThat(sqlServer2005ConnDBConnectionProp.getUrlSuffix(), is(""));

		//正确的使用场景7、构建Postgresql数据库访问场景，断言得到的数据是否正确
		DBConnectionProp postgresqlConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.Postgresql.getCode(), "");
		assertThat(postgresqlConnDBConnectionProp == null, is(false));
		assertThat(postgresqlConnDBConnectionProp.getUrlPrefix(), is("jdbc:postgresql://"));
		assertThat(postgresqlConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(postgresqlConnDBConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(postgresqlConnDBConnectionProp.getUrlSuffix(), is(""));

		//正确的使用场景8、构建SybaseASE125数据库访问场景，断言得到的数据是否正确
		DBConnectionProp sybaseASE125ConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.SybaseASE125.getCode(), "");
		assertThat(sybaseASE125ConnDBConnectionProp == null, is(false));
		assertThat(sybaseASE125ConnDBConnectionProp.getUrlPrefix(), is("jdbc:sybase:Tds:"));
		assertThat(sybaseASE125ConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(sybaseASE125ConnDBConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(sybaseASE125ConnDBConnectionProp.getUrlSuffix(), is(""));

		//正确的使用场景9、构建ApacheDerby数据库访问场景，断言得到的数据是否正确
		DBConnectionProp apacheDerbyConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.ApacheDerby.getCode(), "");
		assertThat(apacheDerbyConnDBConnectionProp == null, is(false));
		assertThat(apacheDerbyConnDBConnectionProp.getUrlPrefix(), is("jdbc:derby://"));
		assertThat(apacheDerbyConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(apacheDerbyConnDBConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(apacheDerbyConnDBConnectionProp.getUrlSuffix(), is(";create=true"));

		//正确的使用场景10、构建GBase数据库访问场景，断言得到的数据是否正确
		DBConnectionProp gBaseConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.GBase.getCode(), "");
		assertThat(gBaseConnDBConnectionProp == null, is(false));
		assertThat(gBaseConnDBConnectionProp.getUrlPrefix(), is("jdbc:gbase://"));
		assertThat(gBaseConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(gBaseConnDBConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(gBaseConnDBConnectionProp.getUrlSuffix(), is(""));

		//正确的使用场景11、构建TeraData数据库访问场景，断言得到的数据是否正确
		DBConnectionProp teraDataConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.TeraData.getCode(), "");
		assertThat(teraDataConnDBConnectionProp == null, is(false));
		assertThat(teraDataConnDBConnectionProp.getUrlPrefix(), is("jdbc:teradata://"));
		assertThat(teraDataConnDBConnectionProp.getIpPlaceholder(), is("/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE="));
		assertThat(teraDataConnDBConnectionProp.getPortPlaceholder(), is(""));
		assertThat(teraDataConnDBConnectionProp.getUrlSuffix(), is(",lob_support=off"));

		DBConnectionProp teraDataConnDBConnectionPropTwo = ConnUtil.getConnURLProp(DatabaseType.TeraData.getCode(), "8080");
		assertThat(teraDataConnDBConnectionPropTwo == null, is(false));
		assertThat(teraDataConnDBConnectionPropTwo.getUrlPrefix(), is("jdbc:teradata://"));
		assertThat(teraDataConnDBConnectionPropTwo.getIpPlaceholder(), is("/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE="));
		assertThat(teraDataConnDBConnectionPropTwo.getPortPlaceholder(), is(""));
		assertThat(teraDataConnDBConnectionPropTwo.getUrlSuffix(), is(",lob_support=off,DBS_PORT="));

		//正确的使用场景12、构建Informatic数据库访问场景，断言得到的数据是否正确
		DBConnectionProp informaticConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.Informatic.getCode(), "");
		assertThat(informaticConnDBConnectionProp == null, is(false));
		assertThat(informaticConnDBConnectionProp.getUrlPrefix(), is("jdbc:informix-sqli://"));
		assertThat(informaticConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(informaticConnDBConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(informaticConnDBConnectionProp.getUrlSuffix(), is(":INFORMIXSERVER=myserver"));

		//正确的使用场景13、构建H2数据库访问场景，断言得到的数据是否正确
		DBConnectionProp h2ConnDBConnectionProp = ConnUtil.getConnURLProp(DatabaseType.H2.getCode(), "");
		assertThat(h2ConnDBConnectionProp == null, is(false));
		assertThat(h2ConnDBConnectionProp.getUrlPrefix(), is("jdbc:h2:tcp://"));
		assertThat(h2ConnDBConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(h2ConnDBConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(h2ConnDBConnectionProp.getUrlSuffix(), is(""));

		//错误的使用场景14、构建错误的数据库访问场景，断言代码对错误逻辑的处理是否符合预期，该场景在DBConfStepActionTest测试用例的getJDBCDriver中有了体现
	}

	/**
	 * 测试根据数据库类型获取数据库驱动
	 *
	 * 正确的使用场景1、构建mysql数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景2、构建oracle9i数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景3、构建oracle10g数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景4、构建DB2数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景5、构建SqlServer2000数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景6、构建SqlServer2005数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景7、构建Postgresql数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景8、构建SybaseASE125数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景9、构建ApacheDerby数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景10、构建GBase数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景11、构建TeraData数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景12、构建Informatic数据库访问场景，断言得到的数据是否正确
	 * 正确的使用场景13、构建H2数据库访问场景，断言得到的数据是否正确
	 *
	 * 错误的使用场景1、构建错误的数据库访问场景，断言代码对错误逻辑的处理是否符合预期
	 *
	 * 错误的使用场景未达到三项原因：一个错误的使用场景即可代表所有的不合法访问方式
	 *
	 * @Param: 无
	 *
	 * @return: 无
	 *
	 * */
	@Test
	public void getJDBCDriver(){
		//正确的使用场景1、构建mysql数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.MYSQL.getCode()),is("com.mysql.jdbc.Driver"));
		//正确的使用场景2、构建Oracle9i数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.Oracle9i.getCode()),is("oracle.jdbc.driver.OracleDriver"));
		//正确的使用场景3、构建Oracle10g数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.Oracle10g.getCode()),is("oracle.jdbc.OracleDriver"));
		//正确的使用场景4、构建DB2数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.DB2.getCode()),is("com.ibm.db2.jcc.DB2Driver"));
		//正确的使用场景5、构建SqlServer2000数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.SqlServer2000.getCode()),is("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
		//正确的使用场景6、构建SqlServer2005数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.SqlServer2005.getCode()),is("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
		//正确的使用场景7、构建Postgresql数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.Postgresql.getCode()),is("org.postgresql.Driver"));
		//正确的使用场景8、构建SybaseASE125数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.SybaseASE125.getCode()),is("com.sybase.jdbc2.jdbc.SybDriver"));
		//正确的使用场景9、构建ApacheDerby数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.ApacheDerby.getCode()),is("org.apache.derby.jdbc.EmbeddedDriver"));
		//正确的使用场景10、构建GBase数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.GBase.getCode()),is("com.gbase.jdbc.Driver"));
		//正确的使用场景11、构建TeraData数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.TeraData.getCode()),is("com.teradata.jdbc.TeraDriver"));
		//正确的使用场景12、构建Informatic数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.Informatic.getCode()),is("com.informix.jdbc.IfxDriver"));
		//正确的使用场景13、构建H2数据库访问场景，断言得到的数据是否正确
		assertThat(ConnUtil.getJDBCDriver(DatabaseType.H2.getCode()),is("org.h2.Driver"));

		//错误的使用场景1、构建错误的数据库访问场景，断言代码对错误逻辑的处理是否符合预期，这个场景在DBConfStepActionTest中的getJDBCDriver()中有体现
	}

}
