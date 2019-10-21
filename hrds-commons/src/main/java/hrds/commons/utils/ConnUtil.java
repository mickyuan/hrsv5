package hrds.commons.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.BusinessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.functions.T;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>标    题: 海云数服务</p>
 * <p>描    述: 获取数据看连接</p>
 * <p>版    权: Copyright (c) 2016  </p>
 * <p>公    司: 上海泓智信息科技有限公司</p>
 * <p>创建时间: 2015-12-30 上午10:08:05</p>
 * <p>@author mashimaro</p>
 * <p>@version 1.0</p>
 * <p>ConnUtil.java</p>
 * <p></p>
 */
public class ConnUtil {

	private static final Log logger = LogFactory.getLog(ConnUtil.class);

	@SuppressWarnings("unused")
	public static JSONObject getConn_url(String type, String... database_port) {

		JSONObject jdbcObj = new JSONObject(true);
		String conn_url = "";
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			//			conn_url = "jdbc:mysql://" + database_ip + ":" + database_port + "/" + database_name
			//							+ "?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
			jdbcObj.put("jdbcPrefix", "jdbc:mysql://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", '/');
			jdbcObj.put("jdbcBase", "?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull");
		} else if (DatabaseType.Oracle9i.getCode().equals(type) || DatabaseType.Oracle10g.getCode().equals(type)) {
			//			conn_url = "jdbc:oracle:thin:@" + database_ip + ":" + database_port + ":" + database_name;
			jdbcObj.put("jdbcPrefix", "jdbc:oracle:thin:@");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", ':');
			jdbcObj.put("jdbcBase", "");
		} else if (DatabaseType.DB2.getCode().equals(type)) {
			//			conn_url = "jdbc:db2://" + database_ip + ":" + database_port + "/" + database_name;
			jdbcObj.put("jdbcPrefix", "jdbc:db2://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", '/');
			jdbcObj.put("jdbcBase", "");
		} else if (DatabaseType.SqlServer2005.getCode().equals(type)) {
			//			conn_url = "jdbc:sqlserver://" + database_ip + ":" + database_port + ";DatabaseName=" + database_name;
			jdbcObj.put("jdbcPrefix", "jdbc:sqlserver://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", ";DatabaseName=");
			jdbcObj.put("jdbcBase", "");
		} else if (DatabaseType.SqlServer2000.getCode().equals(type)) {
			//			conn_url = "jdbc:sqlserver://" + database_ip + ":" + database_port + ";DatabaseName=" + database_name;
			jdbcObj.put("jdbcPrefix", "jdbc:sqlserver://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", ";DatabaseName=");
			jdbcObj.put("jdbcBase", "");
		} else if (DatabaseType.Postgresql.getCode().equals(type)) {
			//			conn_url = "jdbc:postgresql://" + database_ip + ":" + database_port + "/" + database_name;
			jdbcObj.put("jdbcPrefix", "jdbc:postgresql://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", '/');
			jdbcObj.put("jdbcBase", "");
		} else if (DatabaseType.SybaseASE125.getCode().equals(type)) {
			//			conn_url = "jdbc:sybase:Tds:" + database_ip + ":" + database_port + "/" + database_name;
			jdbcObj.put("jdbcPrefix", "jdbc:sybase:Tds:");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", '/');
			jdbcObj.put("jdbcBase", "");
		} else if (DatabaseType.ApacheDerby.getCode().equals(type)) {
			//			conn_url = "jdbc:derby://" + database_ip + ":" + database_port + "/" + database_name + ";create=true";
			jdbcObj.put("jdbcPrefix", "jdbc:derby://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", '/');
			jdbcObj.put("jdbcBase", ";create=true");
		} else if (DatabaseType.GBase.getCode().equals(type)) {
			//			conn_url = "jdbc:gbase://" + database_ip + ":" + database_port + "/" + database_name + "";
			jdbcObj.put("jdbcPrefix", "jdbc:gbase://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", '/');
			jdbcObj.put("jdbcBase", "");
		} else if (DatabaseType.TeraData.getCode().equals(type)) {
			for (int i = 0; i < database_port.length; i++) {
				if (database_port.length > -1 || !"".equals(database_port[i].trim())) {
					//					conn_url = "jdbc:teradata://" + database_ip + "/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE=" + database_name
					//									+ ",lob_support=off,DBS_PORT=" + database_port;
					jdbcObj.put("jdbcPrefix", "jdbc:teradata://");
					jdbcObj.put("jdbcIp", "/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE=");
					jdbcObj.put("jdbcBase", ",lob_support=off,DBS_PORT=");
					jdbcObj.put("jdbcPort", "");
				} else {
					//					conn_url = "jdbc:teradata://" + database_ip + "/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE=" + database_name
					//									+ ",lob_support=off";
					jdbcObj.put("jdbcPrefix", "jdbc:teradata://");
					jdbcObj.put("jdbcIp", "/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE=");
					jdbcObj.put("jdbcBase", ",lob_support=off");
					jdbcObj.put("jdbcPort", "");
				}
			}

		} else if (DatabaseType.Informatic.getCode().equals(type)) {
			//			jdbc:informix-sqli://127.0.0.1:1533/testDB:INFORMIXSERVER=myserver;user=testuser;password=testpassword"
			jdbcObj.put("jdbcPrefix", "jdbc:informix-sqli://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", '/');
			jdbcObj.put("jdbcBase", ":INFORMIXSERVER=myserver");
		} else if (DatabaseType.H2.getCode().equals(type)) {
			//			jdbc:h2:tcp://localhost:port/databaseName
			jdbcObj.put("jdbcPrefix", "jdbc:h2:tcp://");
			jdbcObj.put("jdbcIp", ':');
			jdbcObj.put("jdbcPort", '/');
			jdbcObj.put("jdbcBase", "");
		} else {
			logger.info("目前不支持该数据，请联系管理员");
		}
		jdbcObj.put("jdbcDriver", getJDBCDriver(type));
		return jdbcObj;
	}

	/**
	 * 获取connection
	 *
	 * @param database_drive
	 * @param user_name
	 * @param database_pad
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(String database_drive, String jdbc_url, String user_name, String database_pad) throws Exception {

		Connection conn = null;
		try {
			//			String conn_url = getConn_url(type, database_ip, database_port, database_name);
			logger.info("the connection url :" + jdbc_url);
			Class.forName(database_drive);
			conn = DriverManager.getConnection(jdbc_url, user_name, database_pad);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return conn;
	}

	/**
	 * 获取connection
	 *
	 * @return
	 * @throws Exception
	 */
	public static void close(Connection conn) {

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 返回JDBCURL中的IP,端口,dataBase
	 *
	 * @param jdbcUrl
	 * @return
	 */
	public static List<String> getJDBCUrlInfo(String jdbcUrl, String jdbcType) {

		String[] regx = {"(\\d+\\.\\d+\\.\\d+\\.\\d+|\\w+):\\d+", ".*:(\\d+)", "DBS_PORT=(\\d+)", "\\d+:\\d+:(.*)", ";DatabaseName=(.*)",
				":\\d+/(\\w+)", "DATABASE=(\\w+)"};
		List<String> list = new ArrayList<>();
		if (DatabaseType.TeraData.getCode().equals(jdbcType)) {
			String teraDaraFromUrl = getTeraDaraFromUrl(jdbcUrl);
			list.add(teraDaraFromUrl);
		}
		Pattern pattern = null;
		Matcher m = null;
		for (int i = 0; i < regx.length; i++) {
			pattern = Pattern.compile(regx[i]);// 匹配的模式
			m = pattern.matcher(jdbcUrl);
			while (m.find()) {
				list.add(m.group(1));
				break;
			}
		}

		if (DatabaseType.TeraData.getCode().equals(jdbcType) && list.size() == 2) {
			list.add(1, "");
		}
		return list;
	}

	public static String getJDBCDriver(String type) {

		String jdbcDriver = "";
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			jdbcDriver = "com.mysql.jdbc.Driver";
		} else if (DatabaseType.Oracle9i.getCode().equals(type)) {
			jdbcDriver = "oracle.jdbc.driver.OracleDriver";
		} else if (DatabaseType.Oracle10g.getCode().equals(type)) {
			jdbcDriver = "oracle.jdbc.OracleDriver";
		} else if (DatabaseType.SqlServer2000.getCode().equals(type)) {
			jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (DatabaseType.SqlServer2005.getCode().equals(type)) {
			jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (DatabaseType.DB2.getCode().equals(type)) {
			jdbcDriver = "com.ibm.db2.jcc.DB2Driver";
		} else if (DatabaseType.SybaseASE125.getCode().equals(type)) {
			jdbcDriver = "com.sybase.jdbc2.jdbc.SybDriver";
		} else if (DatabaseType.Informatic.getCode().equals(type)) {
			jdbcDriver = "com.informix.jdbc.IfxDriver";
		} else if (DatabaseType.H2.getCode().equals(type)) {
			jdbcDriver = "org.h2.Driver";
		} else if (DatabaseType.ApacheDerby.getCode().equals(type)) {
			jdbcDriver = "org.apache.derby.jdbc.EmbeddedDriver";
		} else if (DatabaseType.GBase.getCode().equals(type)) {
			jdbcDriver = "com.gbase.jdbc.Driver";
		} else if (DatabaseType.TeraData.getCode().equals(type)) {
			jdbcDriver = "com.teradata.jdbc.TeraDriver";
		} else {
			jdbcDriver = "org.postgresql.Driver";
		}
		return jdbcDriver;
	}

	/**
	 * 获取database
	 *
	 * @param database_ip
	 * @param database_port
	 * @param database_name
	 * @param user_name
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	/*public static Database getDataBase(String database_drive, String database_ip, String database_port, String database_name, String user_name,
					String database_pad, String type) throws ClassNotFoundException, SQLException {
	
		String conn_url = getConn_url(type, database_ip, database_port, database_name);
		Connection conn = getConnection(database_drive, database_ip, database_port, database_name, user_name, database_pad, type);
		conn.setAutoCommit(false);
		Platform platform = PlatformFactory.createNewPlatformInstance(database_drive, conn_url);
		String[] _defaultTableTypes = { "TABLE" };
		Database db = platform.readModelFromDatabase(conn, user_name, database_name, user_name, _defaultTableTypes);
		return db;
	}*/
	public static String getDataBaseFile(String database_ip, String database_port, String database_name, String user_name) {

		return Math.abs((database_name + database_ip + database_port + user_name).hashCode()) + ".xml";
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> getColumnByTable(String filename, String tablename) {

		List<Map<String, String>> columnList = new ArrayList<>();
		Map<?, ?> retMap = loadStoreInfo(filename);
		Map<String, Element> mapCol = (Map<String, Element>) retMap.get("mapCol");
		Element colList = mapCol.get(tablename);
		//当xml树中没有节点的时候跳出
		if (null == colList) {
			return columnList;
		}
		List<?> acctTypes = XMLUtil.getChildElements(colList, "column");
		for (Object object : acctTypes) {
			Element type = (Element) object;
			Map<String, String> hashMap = new HashMap<>();
			hashMap.put("column_name", type.getAttribute("name"));
			hashMap.put("is_primary_key", "true".equals(type.getAttribute("primaryKey")) ? "0" : "1");
			hashMap.put("required", type.getAttribute("required"));
			hashMap.put("type", type.getAttribute("column_type"));
			hashMap.put("column_id", "");
			hashMap.put("size", type.getAttribute("length"));
			hashMap.put("autoIncrement", type.getAttribute("autoIncrement"));
			hashMap.put("column_ch_name", type.getAttribute("column_cn_name"));
			columnList.add(hashMap);
		}
		return columnList;
	}

	/**
	 * 获取表信息
	 *
	 * @param filename 文件名
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> getTable(String filename) {
		Map<String, Object> retMap = loadStoreInfo(filename);
		return (List<Object>) retMap.get("tableNameList");
	}

	private static Map<String, Object> loadStoreInfo(String filename) {
		List<?> tableList = getXmlToList(filename);
		Map<String, Element> mapCol = new HashMap<String, Element>();
		List<String> tableNameList = new ArrayList<>();
		for (Object element : tableList) {
			Element table = (Element) element;
			String tableName = table.getAttribute("name");
			mapCol.put(tableName, table);
			tableNameList.add(tableName);
		}
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("tableNameList", tableNameList);
		retMap.put("mapCol", mapCol);
		return retMap;
	}

	private static List<Object> loadStoreInfoXML(String filename) {
		List<?> tableList = getXmlToList(filename);
		List<Object> tables = new ArrayList<>();
		for (Object element : tableList) {
			Element table = (Element) element;
			String tableName = table.getAttribute("name");
			String description = table.getAttribute("description");
			String storage_type = table.getAttribute("storage_type");
			Map<String, String> map = new HashMap<>();
			map.put("tableName", tableName);
			map.put("description", description);
			map.put("storage_type", storage_type);
			tables.add(map);
		}
		return tables;

	}

	private static List<?> getXmlToList(String filename) {
		try {
			File f = new File(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new BusinessException("创建文档管理器失败" + e.getMessage());
			}
			Document doc = db.parse(f);
			Element root = (Element) doc.getElementsByTagName("database").item(0);
			return XMLUtil.getChildElements(root, "table");
		} catch (Exception ex) {
			throw new BusinessException("加载信息异常！ " + ex.getMessage());
		}
	}

	public static List<Object> getTableToXML(String filename) {

		return loadStoreInfoXML(filename);
	}

	public static String getTeraDaraFromUrl(String jdbcUrl) {

		String cleanURI = jdbcUrl.substring(5);
		URI uri = URI.create(cleanURI);
		return uri.getHost();
	}

}
