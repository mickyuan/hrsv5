package hrds.commons.collection.bean;

import fd.ng.core.annotation.DocBean;

/**
 * @program: hrsv5
 * @description:
 * @author: xchao
 * @create: 2020-04-01 14:40
 */
public class DbConfBean {
	@DocBean(name = "user_name", value = "用户名称:", dataType = String.class, required = false)
	private String user_name;
	@DocBean(name = "database_pad", value = "数据库密码:", dataType = String.class, required = false)
	private String database_pad;
	@DocBean(name = "database_drive", value = "数据库驱动:", dataType = String.class, required = false)
	private String database_drive;
	@DocBean(name = "database_type", value = "数据库类型(DatabaseType):01-MYSQL<MYSQL> 02-Oracle9i及一下<Oracle9i> " +
			"03-Oracle10g及以上<Oracle10g> 04-SQLSERVER2000<SqlServer2000> 05-SQLSERVER2005<SqlServer2005> " +
			"06-DB2<DB2> 07-SybaseASE12.5及以上<SybaseASE125> 08-Informatic<Informatic> 09-H2<H2> " +
			"10-ApacheDerby<ApacheDerby> 11-Postgresql<Postgresql> 12-GBase<GBase> 13-TeraData<TeraData> ",
			dataType = String.class, required = true)
	private String database_type;
	@DocBean(name = "jdbc_url", value = "数据库连接地址:", dataType = String.class, required = false)
	private String jdbc_url;


	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getDatabase_pad() {
		return database_pad;
	}

	public void setDatabase_pad(String database_pad) {
		this.database_pad = database_pad;
	}

	public String getDatabase_drive() {
		return database_drive;
	}

	public void setDatabase_drive(String database_drive) {
		this.database_drive = database_drive;
	}

	public String getDatabase_type() {
		return database_type;
	}

	public void setDatabase_type(String database_type) {
		this.database_type = database_type;
	}

	public String getJdbc_url() {
		return jdbc_url;
	}

	public void setJdbc_url(String jdbc_url) {
		this.jdbc_url = jdbc_url;
	}
}
