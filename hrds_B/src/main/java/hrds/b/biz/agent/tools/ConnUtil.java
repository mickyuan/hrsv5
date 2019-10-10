
package hrds.b.biz.agent.tools;

import hrds.b.biz.agent.bean.URLTemplate;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Description: 数据库直连采集获取数据库连接相关信息的工具类
 * @Author: wangz
 * @CreateTime: 2019-09-24-09:57
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.b.biz.agent.tools
 **/

public class ConnUtil {

	//FIXME getLogger 经常传入错误是参数，所以，使用无参方法吧，已修复
	private static final Logger logger = LogManager.getLogger();


	/**
	 * 根据数据库类型获取数据库连接信息填写模板
	 *
	 * 1、构建返回使用Map集合
	 * 2、判断数据库类型，根据数据库类型构建数据库连接信息填写模板并放入Map
	 * 3、返回Map集合
	 *
	 * 该方法不与数据库交互，无需限制访问权限
	 *
	 * @Param: dbType String
	 *         含义: 数据库类型
	 *         取值范围：不为空，DatabaseType代码项code值
	 *
	 * @return: Map<String, String>
	 *          含义 : 存放数据库连接信息填写模板的集合
	 *          取值范围 : 不为空，key为jdbcPrefix, jdbcIp, jdbcPort, jdbcBase
	 *
	 * */
	//FIXME 方法名字不贴切
	public static URLTemplate getConnURLTemplate(String dbType) {
		//1、构建返回使用Map集合
		URLTemplate template = new URLTemplate();
		//2、判断数据库类型，根据数据库类型构建数据库连接信息填写模板并放入Map
		//FIXME DatabaseType.ofEnumByCode(dbType) 应该拿到外面来。如果getConnURL被反复调用，就会多次查找转换枚举对象了，已修复
		DatabaseType databaseType = DatabaseType.ofEnumByCode(dbType);
		if(DatabaseType.MYSQL == databaseType){
			template.setUrlPrefix("jdbc:mysql://");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder("/");
			template.setUrlSuffix("?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull");
		}else if(DatabaseType.Oracle9i == databaseType || DatabaseType.Oracle10g == databaseType){
			template.setUrlPrefix("jdbc:oracle:thin:@");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder(":");
			template.setUrlSuffix("");
		}else if(DatabaseType.DB2 == databaseType){
			template.setUrlPrefix("jdbc:db2://");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder("/");
			template.setUrlSuffix("");
		}else if(DatabaseType.SqlServer2000 == databaseType || DatabaseType.SqlServer2005 == databaseType){
			template.setUrlPrefix("jdbc:sqlserver://");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder(";DatabaseName=");
			template.setUrlSuffix("");
		}else if(DatabaseType.Postgresql == databaseType){
			template.setUrlPrefix("jdbc:postgresql://");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder("/");
			template.setUrlSuffix("");
		}else if(DatabaseType.SybaseASE125 == databaseType){
			template.setUrlPrefix("jdbc:sybase:Tds:");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder("/");
			template.setUrlSuffix("");
		}else if(DatabaseType.ApacheDerby == databaseType){
			template.setUrlPrefix("jdbc:derby://");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder("/");
			template.setUrlSuffix(";create=true");
		}else if(DatabaseType.GBase == databaseType){
			template.setUrlPrefix("jdbc:gbase://");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder("/");
			template.setUrlSuffix("");
		}else if(DatabaseType.TeraData == databaseType){
			template.setUrlPrefix("jdbc:teradata://");
			template.setIpPlaceholder("/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE=");
			template.setPortPlaceholder("");
			template.setUrlSuffix(",lob_support=off");
		}else if(DatabaseType.Informatic == databaseType){
			template.setUrlPrefix("jdbc:informix-sqli://");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder("/");
			template.setUrlSuffix(":INFORMIXSERVER=myserver");
		}else if(DatabaseType.H2 == databaseType){
			template.setUrlPrefix("jdbc:h2:tcp://");
			template.setIpPlaceholder(":");
			template.setPortPlaceholder("/");
			template.setUrlSuffix("");
		}else{
			logger.error("目前不支持对该数据库类型进行采集，请联系管理员");
			throw new AppSystemException("目前不支持对该数据库类型进行采集，请联系管理员");
		}
		//3、返回Map集合
		//FIXME 是不是返回BEAN更好？已修复
		return template;
	}

}
