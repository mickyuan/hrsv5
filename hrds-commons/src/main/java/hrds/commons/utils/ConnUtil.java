package hrds.commons.utils;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.OperationType;
import hrds.commons.entity.Object_handle_type;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 标 题: 海云数服务
 *
 * <p>描 述: 获取数据看连接
 *
 * <p>版 权: Copyright (c) 2016
 *
 * <p>公 司: 上海泓智信息科技有限公司
 *
 * <p>创建时间: 2015-12-30 上午10:08:05
 *
 * <p>@author mashimaro
 *
 * <p>@version 1.0
 *
 * <p>ConnUtil.java
 *
 * <p>
 */
public class ConnUtil {

	private static final Log logger = LogFactory.getLog(ConnUtil.class);

	/**
	 * 获取jdbc连接
	 *
	 * @param database_drive 驱动
	 * @param jdbc_url       jdbc连接url
	 * @param user_name      用户名
	 * @param database_pad   密码
	 * @return jdbc连接
	 */
	public static Connection getConnection(
			String database_drive, String jdbc_url, String user_name, String database_pad) {
		Connection conn;
		try {
			logger.info("开始连接 :" + jdbc_url);
			Class.forName(database_drive);
			conn = DriverManager.getConnection(jdbc_url, user_name, database_pad);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw new AppSystemException("创建数据库连接失败", e);
		}
		return conn;
	}

	/**
	 * 获取connection
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
	 */
	public static Map<String, String> getJDBCUrlInfo(String jdbcUrl, String jdbcType) {
		String[] ipRegxs = {"(\\d+\\.\\d+\\.\\d+\\.\\d+|\\w+):\\d+"};
		String[] portRegxs = {
				".*?:(\\d+)",
				"DBS_PORT=(\\d+)"};
		String[] databaseRegxs = {
				"\\d+:\\d+:(.*)",
				":\\d+/(\\w+)",
				";DatabaseName=(.*)",
				"DATABASE=(\\w+)"};
		Map<String, String> map = new HashMap<>();
		Pattern pattern;
		Matcher m;
		//获取ip
		for (String regx : ipRegxs) {
			pattern = Pattern.compile(regx); // 匹配的模式
			m = pattern.matcher(jdbcUrl);
			if (m.find()) {
				map.put("ip", m.group(1));
			}
		}
		//获取端口
		for (String regx : portRegxs) {
			pattern = Pattern.compile(regx); // 匹配的模式
			m = pattern.matcher(jdbcUrl);
			if (m.find()) {
				map.put("port", m.group(1));
			}
		}
		//获取数据库名称
		for (String regx : databaseRegxs) {
			pattern = Pattern.compile(regx); // 匹配的模式
			m = pattern.matcher(jdbcUrl);
			if (m.find()) {
				map.put("database", m.group(1));
			}
		}
		if (DatabaseType.TeraData.getCode().equals(jdbcType)) {
			String teraDaraFromUrl = getTeraDaraFromUrl(jdbcUrl);
			map.put("ip", teraDaraFromUrl);
		}
		return map;
	}


	public static String getDataBaseFile(
			String database_ip, String database_port, String database_name, String user_name) {

		return Math.abs((database_name + database_ip + database_port + user_name).hashCode()) + ".xml";
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> getColumnByTable(String filename, String tablename) {
		List<Map<String, String>> columnList = new ArrayList<>();
		Map<?, ?> retMap = loadStoreInfo(filename);
		Map<String, Element> mapCol = (Map<String, Element>) retMap.get("mapCol");
		Element colList = mapCol.get(tablename);
		// 当xml树中没有节点的时候跳出
		if (null == colList) {
			return columnList;
		}
		List<?> acctTypes = XMLUtil.getChildElements(colList, "column");
		for (Object object : acctTypes) {
			Element type = (Element) object;
			Map<String, String> hashMap = new HashMap<>();
			hashMap.put("column_name", type.getAttribute("column_name"));
			hashMap.put("is_primary_key", type.getAttribute("is_primary_key"));
			hashMap.put("column_ch_name", type.getAttribute("column_ch_name"));
			hashMap.put("column_type", type.getAttribute("column_type"));
			hashMap.put("column_remark", type.getAttribute("column_remark"));
			hashMap.put("is_get", type.getAttribute("is_get"));
			hashMap.put("is_alive", type.getAttribute("is_alive"));
			hashMap.put("is_new", type.getAttribute("is_new"));
			columnList.add(hashMap);
		}
		return columnList;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, List<Map<String, String>>> getColumnByXml(String xmlName) {
		Map<String, List<Map<String, String>>> columnMap = new HashMap<>();
		Map<?, ?> retMap = loadStoreInfo(xmlName);
		Map<String, Element> mapCol = (Map<String, Element>) retMap.get("mapCol");
		// 当xml树中没有节点的时候跳出
		if (null == mapCol) {
			return columnMap;
		}
		for (String table_name : mapCol.keySet()) {
			List<Map<String, String>> columnList = new ArrayList<>();
			List<?> acctTypes = XMLUtil.getChildElements(mapCol.get(table_name), "column");
			for (Object object : acctTypes) {
				Element type = (Element) object;
				Map<String, String> hashMap = new HashMap<>();
				hashMap.put("column_name", type.getAttribute("column_name"));
				hashMap.put("is_primary_key", type.getAttribute("is_primary_key"));
				hashMap.put("column_ch_name", type.getAttribute("column_ch_name"));
				hashMap.put("column_type", type.getAttribute("column_type"));
				hashMap.put("column_remark", type.getAttribute("column_remark"));
				hashMap.put("is_get", type.getAttribute("is_get"));
				hashMap.put("is_alive", type.getAttribute("is_alive"));
				hashMap.put("is_new", type.getAttribute("is_new"));
				columnList.add(hashMap);
			}
			columnMap.put(table_name, columnList);
		}
		return columnMap;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getStorageByXml(String xmlName) {
		List<Map<String, Object>> allStorageList = new ArrayList<>();
		Map<?, ?> retMap = loadStoreInfo(xmlName);
		Map<String, Element> mapCol = (Map<String, Element>) retMap.get("mapCol");
		Map<String, String> tableNameMap = (Map<String, String>) retMap.get("tableNameMap");
		// 当xml树中没有节点的时候跳出
		if (null == mapCol) {
			return allStorageList;
		}
		for (String table_name : mapCol.keySet()) {
			Map<String, Object> storageMap = new HashMap<>();
			List<Map<String, String>> storageList = new ArrayList<>();
			List<?> acctTypes = XMLUtil.getChildElements(mapCol.get(table_name), "storage");
			for (Object object : acctTypes) {
				Element type = (Element) object;
				Map<String, String> hashMap = new HashMap<>();
				hashMap.put("dbfile_format", type.getAttribute("dbfile_format"));
				hashMap.put("is_header", type.getAttribute("is_header"));
				hashMap.put("row_separator", type.getAttribute("row_separator"));
				hashMap.put("database_separatorr", type.getAttribute("database_separatorr"));
				hashMap.put("plane_url", type.getAttribute("plane_url"));
				hashMap.put("database_code", type.getAttribute("database_code"));
				storageList.add(hashMap);
			}
			storageMap.put("storage", storageList);
			storageMap.put("table_name", table_name);
			storageMap.put("table_ch_name", tableNameMap.get(table_name));
			allStorageList.add(storageMap);
		}
		return allStorageList;
	}

	/**
	 * 获取表信息
	 *
	 * @param filename 文件名
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> getTable(String filename, String searchTableName) {
		//读取xml的表数据信息
		Map<String, Object> retMap = loadStoreInfo(filename);
		//获取的表名称集合
		List<Object> xmlTableNameList = (List<Object>) retMap.get("tableNameList");
		/*
		 *如果搜索的表名存在,将表名进行分割,然后从xml的结果集中删除掉和搜索表名不一致的表
		 */
		if (StringUtil.isNotBlank(searchTableName)) {
			//处理的表数据信息集合
			List<Object> processTableList = new ArrayList<>();
			List<String> searchTableNames = StringUtil.split(searchTableName, "|");
			for (String searchTable : searchTableNames) {
				for (Object xmlTable : xmlTableNameList) {
					if (xmlTable.toString().contains(searchTable)) {
						//防止有重复的表名出现
						if (!processTableList.contains(xmlTable)) {
							processTableList.add(xmlTable);
						}
					}
				}
			}
			return processTableList;
		}
		return xmlTableNameList;
	}

	private static Map<String, Object> loadStoreInfo(String filename) {
		List<?> tableList = getXmlToList(filename);
		Map<String, Element> mapCol = new HashMap<>();
		List<String> tableNameList = new ArrayList<>();
		Map<String, String> tableNameMap = new HashMap<>();
		for (Object element : tableList) {
			Element table = (Element) element;
			String tableName = table.getAttribute("table_name");
			String table_cn_name = table.getAttribute("table_ch_name");
			mapCol.put(tableName, table);
			tableNameList.add(tableName);
			tableNameMap.put(tableName, table_cn_name);
		}
		HashMap<String, Object> retMap = new HashMap<>();
		retMap.put("tableNameMap", tableNameMap);
		retMap.put("tableNameList", tableNameList);
		retMap.put("mapCol", mapCol);
		return retMap;
	}

	private static List<Object> loadStoreInfoXML(String filename) {
		List<?> tableList = getXmlToList(filename);
		List<Object> tables = new ArrayList<>();
		for (Object element : tableList) {
			Element table = (Element) element;
			Map<String, String> map = new HashMap<>();
			map.put("table_name", table.getAttribute("table_name"));
			map.put("table_ch_name", table.getAttribute("table_ch_name"));
			map.put("unload_type", table.getAttribute("unload_type"));
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

	/**
	 * 半结构化采集获取数据字典表信息
	 *
	 * @param filename 文件路径
	 */
	public static List<Map<String, String>> getDicTable(String filename) {
		List<?> xmlToList = getXmlToList(filename);
		List<Map<String, String>> tableList = new ArrayList<>();
		for (Object o : xmlToList) {
			Element table = (Element) o;
			Map<String, String> tableJson = new HashMap<>();
			tableJson.put("en_name", table.getAttribute("table_name"));
			tableJson.put("zh_name", table.getAttribute("table_ch_name"));
			// 数据更新方式：0--直接更新，1--拉链更新
			tableJson.put("updatetype", table.getAttribute("unload_type"));
			tableList.add(tableJson);
		}
		return tableList;
	}

	@Method(desc = "半结构化采集通过表名获取处理方式值", logicStep = "")
	@Param(name = "xmlName", desc = "xml文件名称", range = "无限制")
	@Return(desc = "返回半结构化采集获取数据字典所有表对应列信息", range = "无限制")
	public static Map<String, List<Object_handle_type>> getAllHandleType(String xmlName) {
		List<?> xmlToList = getXmlToList(xmlName);
		Map<String, List<Object_handle_type>> handleTypeMap = new HashMap<>();
		for (Object o : xmlToList) {
			Element table = (Element) o;
			Element handleType = XMLUtil.getChildElement(table, "handle_type");
			Validator.notNull(handleType, "处理类型不能为空,生成xml数据或者数据字典有误");
			List<Object_handle_type> handleTypeList = new ArrayList<>();

			Object_handle_type object_handle_type = new Object_handle_type();
			object_handle_type.setHandle_type(OperationType.INSERT.getCode());
			object_handle_type.setHandle_value(handleType.getAttribute("insert"));
			handleTypeList.add(object_handle_type);

			Object_handle_type object_handle_type2 = new Object_handle_type();
			object_handle_type2.setHandle_type(OperationType.UPDATE.getCode());
			object_handle_type2.setHandle_value(handleType.getAttribute("update"));
			handleTypeList.add(object_handle_type2);

			Object_handle_type object_handle_type3 = new Object_handle_type();
			object_handle_type3.setHandle_type(OperationType.DELETE.getCode());
			object_handle_type3.setHandle_value(handleType.getAttribute("delete"));
			handleTypeList.add(object_handle_type3);

			handleTypeMap.put(table.getAttribute("table_name"), handleTypeList);
		}
		return handleTypeMap;
	}

	@Method(desc = "半结构化采集获取数据字典所有表对应列信息", logicStep = "")
	@Param(name = "xmlName", desc = "xml文件名称", range = "无限制")
	@Param(name = "table_name", desc = "表名称", range = "无限制")
	@Return(desc = "返回半结构化采集获取数据字典所有表对应列信息", range = "无限制")
	public static Map<String, List<Map<String, String>>> getColumnByXml2(String xmlName) {
		List<?> xmlToList = getXmlToList(xmlName);
		Map<String, List<Map<String, String>>> allColumnMap = new HashMap<>();
		for (Object value : xmlToList) {
			List<Map<String, String>> columnList = new ArrayList<>();
			Element table = (Element) value;
			List<?> columns = XMLUtil.getChildElements(table, "columns");
			for (Object object : columns) {
				Map<String, String> columnMap = new HashMap<>();
				Element column = (Element) object;
				columnMap.put("column_name", column.getAttribute("column_name"));
				columnMap.put("data_desc", column.getAttribute("column_ch_name"));
				columnMap.put("column_type", column.getAttribute("column_type"));
				columnMap.put("is_operate", column.getAttribute("is_operate"));
				columnMap.put("columnposition", column.getAttribute("columnposition"));
				columnList.add(columnMap);
			}
			allColumnMap.put(table.getAttribute("table_name"), columnList);
		}
		return allColumnMap;
	}

	@Method(desc = "没有数据字典时读取数据文件", logicStep = "")
	@Param(desc = "文件路径", name = "file_path", range = "无限制")
	@Param(desc = "数据日期", name = "data_date", range = "无限制")
	@Param(desc = "文件后缀名", name = "file_suffix", range = "无限制")
	@Return(desc = "半结构化采集查看表时，对于不提供数据字典的情况，解析返回表名", range = "无限制")
	public static List<Map<String, String>> getTableByNoDictionary(String file_path, String data_date, String file_suffix) {
		JSONObject resultObject = new JSONObject();
		File pathFile = new File(file_path);
		File[] fileList = Objects.requireNonNull(pathFile.listFiles());
		List<String> fileNameList = new ArrayList<>();
		for (File file : fileList) {
			fileNameList.add(file.getName());
		}
		if (!fileNameList.contains(data_date)) {
			resultObject.put("ErrorMessage", "文件路径不存在，请检查:" + file_path + " 下是否存在 " + data_date + " 文件夹");
			throw new BusinessException(resultObject.getString("ErrorMessage"));
		}
		List<String> tableNameList = new ArrayList<>();
		String filepath = file_path + File.separator + data_date;
		File targetFileDirectory = new File(filepath);
		if (!targetFileDirectory.isDirectory()) {
			resultObject.put("ErrorMessage", "文件路径：" + filepath + " 不是一个文件夹，请检查");
			throw new BusinessException(resultObject.getString("ErrorMessage"));
		}
		List<File> files =
				new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(filepath).listFiles())));
		if (files.size() == 0) {
			resultObject.put("ErrorMessage", "数据路径的日期目录：" + filepath + " 下没有文件");
			throw new BusinessException(resultObject.getString("ErrorMessage"));
		}
		List<Map<String, String>> tableList = new ArrayList<>();
		for (File file : files) {
			String filename = file.getName();
			if (filename.endsWith("_" + data_date + "." + file_suffix)) {
				String tablename = filename.split("_" + data_date + "." + file_suffix)[0];
				if (!tableNameList.contains(tablename)) {
					try {
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String readLine = reader.readLine();
						tableNameList.add(tablename);
						Map<String, String> tableMap = new HashMap<>();
						tableMap.put("zh_name", tablename);
						tableMap.put("en_name", tablename);
						tableMap.put("firstline", readLine);
						tableList.add(tableMap);
					} catch (Exception e) {
						throw new BusinessException("没有数据字典时读取数据文件失败！");
					}
				}
			}
		}
		if (tableList.size() == 0) {
			resultObject.put("ErrorMessage", "数据路径的日期目录下没有后缀名为：" + file_suffix + "的文件");
			throw new BusinessException(resultObject.getString("ErrorMessage"));
		}
		return tableList;
	}

	public static void main(String[] args) {
//		Map<String, String> jdbcUrlInfo = getJDBCUrlInfo("jdbc:oracle:thin:@47.103.83.1:1521:hyshf",
//				DatabaseType.Oracle10g.getCode());
		Map<String, String> jdbcUrlInfo = getJDBCUrlInfo("jdbc:teradata://hdp001/hyshf",
				DatabaseType.TeraData.getCode());
		jdbcUrlInfo.forEach((k, v) -> System.out.println(k + "=========" + v));
	}
}
