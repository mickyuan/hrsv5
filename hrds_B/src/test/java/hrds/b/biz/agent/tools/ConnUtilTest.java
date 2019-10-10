package hrds.b.biz.agent.tools;

import hrds.b.biz.agent.bean.URLTemplate;
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
	public void getConnURL(){
		//正确的使用场景1、构建mysql数据库访问场景，断言得到的数据是否正确
		URLTemplate mysqlConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.MYSQL.getCode());
		assertThat(mysqlConnURLTemplate == null, is(false));
		assertThat(mysqlConnURLTemplate.getUrlPrefix(), is("jdbc:mysql://"));
		assertThat(mysqlConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(mysqlConnURLTemplate.getPortPlaceholder(), is("/"));
		assertThat(mysqlConnURLTemplate.getUrlSuffix(), is("?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"));

		//正确的使用场景2、构建oracle9i数据库访问场景，断言得到的数据是否正确
		URLTemplate oracle9iConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.Oracle9i.getCode());
		assertThat(oracle9iConnURLTemplate == null, is(false));
		assertThat(oracle9iConnURLTemplate.getUrlPrefix(), is("jdbc:oracle:thin:@"));
		assertThat(oracle9iConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(oracle9iConnURLTemplate.getPortPlaceholder(), is(":"));
		assertThat(oracle9iConnURLTemplate.getUrlSuffix(), is(""));

	    //正确的使用场景3、构建oracle10g数据库访问场景，断言得到的数据是否正确
		URLTemplate oracle10gConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.Oracle10g.getCode());
		assertThat(oracle10gConnURLTemplate == null, is(false));
		assertThat(oracle10gConnURLTemplate.getUrlPrefix(), is("jdbc:oracle:thin:@"));
		assertThat(oracle10gConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(oracle10gConnURLTemplate.getPortPlaceholder(), is(":"));
		assertThat(oracle10gConnURLTemplate.getUrlSuffix(), is(""));

		//正确的使用场景4、构建DB2数据库访问场景，断言得到的数据是否正确
		URLTemplate db2ConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.DB2.getCode());
		assertThat(db2ConnURLTemplate == null, is(false));
		assertThat(db2ConnURLTemplate.getUrlPrefix(), is("jdbc:db2://"));
		assertThat(db2ConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(db2ConnURLTemplate.getPortPlaceholder(), is("/"));
		assertThat(db2ConnURLTemplate.getUrlSuffix(), is(""));

		//正确的使用场景5、构建SqlServer2000数据库访问场景，断言得到的数据是否正确
		URLTemplate sqlServer2000ConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.SqlServer2000.getCode());
		assertThat(sqlServer2000ConnURLTemplate == null, is(false));
		assertThat(sqlServer2000ConnURLTemplate.getUrlPrefix(), is("jdbc:sqlserver://"));
		assertThat(sqlServer2000ConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(sqlServer2000ConnURLTemplate.getPortPlaceholder(), is(";DatabaseName="));
		assertThat(sqlServer2000ConnURLTemplate.getUrlSuffix(), is(""));

		//正确的使用场景6、构建SqlServer2005数据库访问场景，断言得到的数据是否正确
		URLTemplate sqlServer2005ConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.SqlServer2005.getCode());
		assertThat(sqlServer2005ConnURLTemplate == null, is(false));
		assertThat(sqlServer2005ConnURLTemplate.getUrlPrefix(), is("jdbc:sqlserver://"));
		assertThat(sqlServer2005ConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(sqlServer2005ConnURLTemplate.getPortPlaceholder(), is(";DatabaseName="));
		assertThat(sqlServer2005ConnURLTemplate.getUrlSuffix(), is(""));

		//正确的使用场景7、构建Postgresql数据库访问场景，断言得到的数据是否正确
		URLTemplate postgresqlConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.Postgresql.getCode());
		assertThat(postgresqlConnURLTemplate == null, is(false));
		assertThat(postgresqlConnURLTemplate.getUrlPrefix(), is("jdbc:postgresql://"));
		assertThat(postgresqlConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(postgresqlConnURLTemplate.getPortPlaceholder(), is("/"));
		assertThat(postgresqlConnURLTemplate.getUrlSuffix(), is(""));

		//正确的使用场景8、构建SybaseASE125数据库访问场景，断言得到的数据是否正确
		URLTemplate sybaseASE125ConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.SybaseASE125.getCode());
		assertThat(sybaseASE125ConnURLTemplate == null, is(false));
		assertThat(sybaseASE125ConnURLTemplate.getUrlPrefix(), is("jdbc:sybase:Tds:"));
		assertThat(sybaseASE125ConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(sybaseASE125ConnURLTemplate.getPortPlaceholder(), is("/"));
		assertThat(sybaseASE125ConnURLTemplate.getUrlSuffix(), is(""));

		//正确的使用场景9、构建ApacheDerby数据库访问场景，断言得到的数据是否正确
		URLTemplate apacheDerbyConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.ApacheDerby.getCode());
		assertThat(apacheDerbyConnURLTemplate == null, is(false));
		assertThat(apacheDerbyConnURLTemplate.getUrlPrefix(), is("jdbc:derby://"));
		assertThat(apacheDerbyConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(apacheDerbyConnURLTemplate.getPortPlaceholder(), is("/"));
		assertThat(apacheDerbyConnURLTemplate.getUrlSuffix(), is(";create=true"));

		//正确的使用场景10、构建GBase数据库访问场景，断言得到的数据是否正确
		URLTemplate gBaseConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.GBase.getCode());
		assertThat(gBaseConnURLTemplate == null, is(false));
		assertThat(gBaseConnURLTemplate.getUrlPrefix(), is("jdbc:gbase://"));
		assertThat(gBaseConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(gBaseConnURLTemplate.getPortPlaceholder(), is("/"));
		assertThat(gBaseConnURLTemplate.getUrlSuffix(), is(""));

		//正确的使用场景11、构建TeraData数据库访问场景，断言得到的数据是否正确
		URLTemplate teraDataConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.TeraData.getCode());
		assertThat(teraDataConnURLTemplate == null, is(false));
		assertThat(teraDataConnURLTemplate.getUrlPrefix(), is("jdbc:teradata://"));
		assertThat(teraDataConnURLTemplate.getIpPlaceholder(), is("/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE="));
		assertThat(teraDataConnURLTemplate.getPortPlaceholder(), is(""));
		assertThat(teraDataConnURLTemplate.getUrlSuffix(), is(",lob_support=off"));

		//正确的使用场景12、构建Informatic数据库访问场景，断言得到的数据是否正确
		URLTemplate informaticConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.Informatic.getCode());
		assertThat(informaticConnURLTemplate == null, is(false));
		assertThat(informaticConnURLTemplate.getUrlPrefix(), is("jdbc:informix-sqli://"));
		assertThat(informaticConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(informaticConnURLTemplate.getPortPlaceholder(), is("/"));
		assertThat(informaticConnURLTemplate.getUrlSuffix(), is(":INFORMIXSERVER=myserver"));

		//正确的使用场景13、构建H2数据库访问场景，断言得到的数据是否正确
		URLTemplate h2ConnURLTemplate = ConnUtil.getConnURLTemplate(DatabaseType.H2.getCode());
		assertThat(h2ConnURLTemplate == null, is(false));
		assertThat(h2ConnURLTemplate.getUrlPrefix(), is("jdbc:h2:tcp://"));
		assertThat(h2ConnURLTemplate.getIpPlaceholder(), is(":"));
		assertThat(h2ConnURLTemplate.getPortPlaceholder(), is("/"));
		assertThat(h2ConnURLTemplate.getUrlSuffix(), is(""));

		//错误的使用场景14、构建错误的数据库访问场景，断言代码对错误逻辑的处理是否符合预期，该场景在DBConfStepActionTest测试用例的getJDBCDriver中有了体现
	}

}
