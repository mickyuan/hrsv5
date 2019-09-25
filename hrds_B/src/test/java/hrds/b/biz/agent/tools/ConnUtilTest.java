package hrds.b.biz.agent.tools;

import hrds.commons.codes.DatabaseType;
import org.junit.Test;

import java.util.Map;

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

	private static final String WRONG_DATABASE_TYPE = "10086";

	/**
	 * 测试根据数据库类型获取数据库连接信息填写模板
	 *
	 * 1、构建mysql数据库访问场景，断言得到的数据是否正确
	 * 2、构建oracle9i数据库访问场景，断言得到的数据是否正确
	 * 3、构建oracle10g数据库访问场景，断言得到的数据是否正确
	 * 4、构建DB2数据库访问场景，断言得到的数据是否正确
	 * 5、构建SqlServer2000数据库访问场景，断言得到的数据是否正确
	 * 6、构建SqlServer2005数据库访问场景，断言得到的数据是否正确
	 * 7、构建Postgresql数据库访问场景，断言得到的数据是否正确
	 * 8、构建SybaseASE125数据库访问场景，断言得到的数据是否正确
	 * 9、构建ApacheDerby数据库访问场景，断言得到的数据是否正确
	 * 10、构建GBase数据库访问场景，断言得到的数据是否正确
	 * 11、构建TeraData数据库访问场景，断言得到的数据是否正确
	 * 12、构建Informatic数据库访问场景，断言得到的数据是否正确
	 * 13、构建H2数据库访问场景，断言得到的数据是否正确
	 * 14、构建错误的数据库访问场景，断言代码对错误逻辑的处理是否符合预期
	 *
	 * @Param: 无
	 *
	 * @return: 无
	 *
	 * */
	@Test
	public void getConnURL(){
		//1、构建mysql数据库访问场景，断言得到的数据是否正确
		Map<String, String> mysqlConnURL = ConnUtil.getConnURL(DatabaseType.MYSQL.getCode());
		assertThat(mysqlConnURL.isEmpty(), is(false));
		assertThat(mysqlConnURL.get("jdbcPrefix"), is("jdbc:mysql://"));
		assertThat(mysqlConnURL.get("jdbcIp"), is(":"));
		assertThat(mysqlConnURL.get("jdbcPort"), is("/"));
		assertThat(mysqlConnURL.get("jdbcBase"), is("?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"));

		//2、构建oracle9i数据库访问场景，断言得到的数据是否正确
		Map<String, String> oracle9iConnURL = ConnUtil.getConnURL(DatabaseType.Oracle9i.getCode());
		assertThat(oracle9iConnURL.isEmpty(), is(false));
		assertThat(oracle9iConnURL.get("jdbcPrefix"), is("jdbc:oracle:thin:@"));
		assertThat(oracle9iConnURL.get("jdbcIp"), is(":"));
		assertThat(oracle9iConnURL.get("jdbcPort"), is(":"));
		assertThat(oracle9iConnURL.get("jdbcBase"), is(""));

	    //3、构建oracle10g数据库访问场景，断言得到的数据是否正确
		Map<String, String> oracle10gConnURL = ConnUtil.getConnURL(DatabaseType.Oracle10g.getCode());
		assertThat(oracle10gConnURL.isEmpty(), is(false));
		assertThat(oracle10gConnURL.get("jdbcPrefix"), is("jdbc:oracle:thin:@"));
		assertThat(oracle10gConnURL.get("jdbcIp"), is(":"));
		assertThat(oracle10gConnURL.get("jdbcPort"), is(":"));
		assertThat(oracle10gConnURL.get("jdbcBase"), is(""));

		//4、构建DB2数据库访问场景，断言得到的数据是否正确
		Map<String, String> db2ConnURL = ConnUtil.getConnURL(DatabaseType.DB2.getCode());
		assertThat(db2ConnURL.isEmpty(), is(false));
		assertThat(db2ConnURL.get("jdbcPrefix"), is("jdbc:db2://"));
		assertThat(db2ConnURL.get("jdbcIp"), is(":"));
		assertThat(db2ConnURL.get("jdbcPort"), is("/"));
		assertThat(db2ConnURL.get("jdbcBase"), is(""));

		//5、构建SqlServer2000数据库访问场景，断言得到的数据是否正确
		Map<String, String> sqlserver2000ConnURL = ConnUtil.getConnURL(DatabaseType.SqlServer2000.getCode());
		assertThat(sqlserver2000ConnURL.isEmpty(), is(false));
		assertThat(sqlserver2000ConnURL.get("jdbcPrefix"), is("jdbc:sqlserver://"));
		assertThat(sqlserver2000ConnURL.get("jdbcIp"), is(":"));
		assertThat(sqlserver2000ConnURL.get("jdbcPort"), is(";DatabaseName="));
		assertThat(sqlserver2000ConnURL.get("jdbcBase"), is(""));

		//6、构建SqlServer2005数据库访问场景，断言得到的数据是否正确
		Map<String, String> sqlserver2005URL = ConnUtil.getConnURL(DatabaseType.SqlServer2005.getCode());
		assertThat(sqlserver2005URL.isEmpty(), is(false));
		assertThat(sqlserver2005URL.get("jdbcPrefix"), is("jdbc:sqlserver://"));
		assertThat(sqlserver2005URL.get("jdbcIp"), is(":"));
		assertThat(sqlserver2005URL.get("jdbcPort"), is(";DatabaseName="));
		assertThat(sqlserver2005URL.get("jdbcBase"), is(""));

		//7、构建Postgresql数据库访问场景，断言得到的数据是否正确
		Map<String, String> postgresqlConnURL = ConnUtil.getConnURL(DatabaseType.Postgresql.getCode());
		assertThat(postgresqlConnURL.isEmpty(), is(false));
		assertThat(postgresqlConnURL.get("jdbcPrefix"), is("jdbc:postgresql://"));
		assertThat(postgresqlConnURL.get("jdbcIp"), is(":"));
		assertThat(postgresqlConnURL.get("jdbcPort"), is("/"));
		assertThat(postgresqlConnURL.get("jdbcBase"), is(""));

		//8、构建SybaseASE125数据库访问场景，断言得到的数据是否正确
		Map<String, String> sybaseASE125ConnURL = ConnUtil.getConnURL(DatabaseType.SybaseASE125.getCode());
		assertThat(sybaseASE125ConnURL.isEmpty(), is(false));
		assertThat(sybaseASE125ConnURL.get("jdbcPrefix"), is("jdbc:sybase:Tds:"));
		assertThat(sybaseASE125ConnURL.get("jdbcIp"), is(":"));
		assertThat(sybaseASE125ConnURL.get("jdbcPort"), is("/"));
		assertThat(sybaseASE125ConnURL.get("jdbcBase"), is(""));

		//9、构建ApacheDerby数据库访问场景，断言得到的数据是否正确
		Map<String, String> apacheDerbyConnURL = ConnUtil.getConnURL(DatabaseType.ApacheDerby.getCode());
		assertThat(apacheDerbyConnURL.isEmpty(), is(false));
		assertThat(apacheDerbyConnURL.get("jdbcPrefix"), is("jdbc:derby://"));
		assertThat(apacheDerbyConnURL.get("jdbcIp"), is(":"));
		assertThat(apacheDerbyConnURL.get("jdbcPort"), is("/"));
		assertThat(apacheDerbyConnURL.get("jdbcBase"), is(";create=true"));

		//10、构建GBase数据库访问场景，断言得到的数据是否正确
		Map<String, String> gbaseConnURL = ConnUtil.getConnURL(DatabaseType.GBase.getCode());
		assertThat(gbaseConnURL.isEmpty(), is(false));
		assertThat(gbaseConnURL.get("jdbcPrefix"), is("jdbc:gbase://"));
		assertThat(gbaseConnURL.get("jdbcIp"), is(":"));
		assertThat(gbaseConnURL.get("jdbcPort"), is("/"));
		assertThat(gbaseConnURL.get("jdbcBase"), is(""));

		//11、构建TeraData数据库访问场景，断言得到的数据是否正确
		Map<String, String> teradataConnURL = ConnUtil.getConnURL(DatabaseType.TeraData.getCode());
		assertThat(teradataConnURL.isEmpty(), is(false));
		assertThat(teradataConnURL.get("jdbcPrefix"), is("jdbc:teradata://"));
		assertThat(teradataConnURL.get("jdbcIp"), is("/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE="));
		assertThat(teradataConnURL.get("jdbcPort"), is(""));
		assertThat(teradataConnURL.get("jdbcBase"), is(",lob_support=off"));

		//12、构建Informatic数据库访问场景，断言得到的数据是否正确
		Map<String, String> informaticConnURL = ConnUtil.getConnURL(DatabaseType.Informatic.getCode());
		assertThat(informaticConnURL.isEmpty(), is(false));
		assertThat(informaticConnURL.get("jdbcPrefix"), is("jdbc:informix-sqli://"));
		assertThat(informaticConnURL.get("jdbcIp"), is(":"));
		assertThat(informaticConnURL.get("jdbcPort"), is("/"));
		assertThat(informaticConnURL.get("jdbcBase"), is(":INFORMIXSERVER=myserver"));

		//13、构建H2数据库访问场景，断言得到的数据是否正确
		Map<String, String> h2ConnURL = ConnUtil.getConnURL(DatabaseType.H2.getCode());
		assertThat(h2ConnURL.isEmpty(), is(false));
		assertThat(h2ConnURL.get("jdbcPrefix"), is("jdbc:h2:tcp://"));
		assertThat(h2ConnURL.get("jdbcIp"), is(":"));
		assertThat(h2ConnURL.get("jdbcPort"), is("/"));
		assertThat(h2ConnURL.get("jdbcBase"), is(""));

		//14、构建错误的数据库访问场景，断言代码对错误逻辑的处理是否符合预期
		Map<String, String> wrongConnURL = ConnUtil.getConnURL(WRONG_DATABASE_TYPE);

	}

}
