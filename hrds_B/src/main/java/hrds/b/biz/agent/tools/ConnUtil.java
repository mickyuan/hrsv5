
package hrds.b.biz.agent.tools;

import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 数据库直连采集获取数据库连接相关信息的工具类
 * @Author: wangz
 * @CreateTime: 2019-09-24-09:57
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.b.biz.agent.tools
 **/

public class ConnUtil {

	//FIXME getLogger 经常传入错误是参数，所以，使用无参方法吧！
	private static final Logger logger = LogManager.getLogger(LogReader.class);

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
	public static Map<String, String> getConnURL(String dbType) {
		//1、构建返回使用Map集合
		Map<String, String> connURL = new HashMap<>();
		//2、判断数据库类型，根据数据库类型构建数据库连接信息填写模板并放入Map
		//FIXME DatabaseType.ofEnumByCode(dbType) 应该拿到外面来。如果getConnURL被反复调用，就会多次查找转换枚举对象了
		if(DatabaseType.MYSQL == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:mysql://"); //FIXME 这是URL，为什么起名为 Prefix
			connURL.put("jdbcIp", ":");//FIXME 为什么是 :
			connURL.put("jdbcPort", "/"); //FIXME 为什么是 /
			connURL.put("jdbcBase", "?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"); //FIXME urlSufix更准确吧
		}else if(DatabaseType.Oracle9i == DatabaseType.ofEnumByCode(dbType) ||
				DatabaseType.Oracle10g == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:oracle:thin:@");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", ":");
			connURL.put("jdbcBase", "");
		}else if(DatabaseType.DB2 == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:db2://");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", "/");
			connURL.put("jdbcBase", "");
		}else if(DatabaseType.SqlServer2000 == DatabaseType.ofEnumByCode(dbType) ||
				DatabaseType.SqlServer2005 == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:sqlserver://");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", ";DatabaseName=");
			connURL.put("jdbcBase", "");
		}else if(DatabaseType.Postgresql == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:postgresql://");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", "/");
			connURL.put("jdbcBase", "");
		}else if(DatabaseType.SybaseASE125 == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:sybase:Tds:");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", "/");
			connURL.put("jdbcBase", "");
		}else if(DatabaseType.ApacheDerby == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:derby://");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", "/");
			connURL.put("jdbcBase", ";create=true");
		}else if(DatabaseType.GBase == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:gbase://");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", "/");
			connURL.put("jdbcBase", "");
		}else if(DatabaseType.TeraData == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:teradata://");
			connURL.put("jdbcIp", "/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE=");
			connURL.put("jdbcPort", "");
			connURL.put("jdbcBase", ",lob_support=off");
		}else if(DatabaseType.Informatic == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:informix-sqli://");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", "/");
			connURL.put("jdbcBase", ":INFORMIXSERVER=myserver");
		}else if(DatabaseType.H2 == DatabaseType.ofEnumByCode(dbType)){
			connURL.put("jdbcPrefix", "jdbc:h2:tcp://");
			connURL.put("jdbcIp", ":");
			connURL.put("jdbcPort", "/");
			connURL.put("jdbcBase", "");
		}else{
			logger.error("目前不支持对该数据库类型进行采集，请联系管理员");
			throw new AppSystemException("目前不支持对该数据库类型进行采集，请联系管理员");
		}
		//3、返回Map集合
		//FIXME 是不是返回BEAN更好？
		return connURL;
	}

}
