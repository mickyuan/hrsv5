package hrds.commons.utils;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.DatabaseType;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.xlstoxml.XmlCreater;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Platform {

	private static final Log logger = LogFactory.getLog(Platform.class);
	private static XmlCreater xmlCreater = null;
	public static Element table = null;
	public static Element root = null;
	public static Element column = null;

	private static void createXml(String path, String name) {
		xmlCreater = new XmlCreater(path);
		root = xmlCreater.createRootElement("database");
		xmlCreater.createAttribute(root, "xmlns", "http://db.apache.org/ddlutils/schema/1.1");
		xmlCreater.createAttribute(root, "name", name);
	}

	private static void openXml(String path) {

		xmlCreater = new XmlCreater(path);
		xmlCreater.open();
		root = xmlCreater.getElement();
	}

	private static void addTable(String en_table_name, String cn_table_name) {

		table = xmlCreater.createElement(root, "table");
		xmlCreater.createAttribute(table, "name", en_table_name.toLowerCase());
		xmlCreater.createAttribute(table, "description", cn_table_name);
		xmlCreater.createAttribute(table, "storage_type", "1");
	}

	private static void addColumn(String colName, String primaryKey, String column_type, String typeName, String precision, String isNull,
	                              String dataType, String scale) {

		column = xmlCreater.createElement(table, "column");
		xmlCreater.createAttribute(column, "name", colName);
		xmlCreater.createAttribute(column, "primaryKey", primaryKey);
		xmlCreater.createAttribute(column, "column_type", column_type);
		xmlCreater.createAttribute(column, "typeName", typeName);
		xmlCreater.createAttribute(column, "length", precision);
		xmlCreater.createAttribute(column, "column_null", isNull);
		xmlCreater.createAttribute(column, "dataType", dataType);
		xmlCreater.createAttribute(column, "scale", scale);
	}

	public static void readModelFromDatabase(Connection conn, String xmlName, String type, String database_name) {

		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			// 调用方法生成xml文件
			String userName = dbmd.getUserName();
			createXml(xmlName, userName);

			String schemaPattern = "%";

			boolean isSchem = true;
			if (DatabaseType.Oracle10g.getCode().equals(type) || DatabaseType.Oracle9i.getCode().equals(type)) {
				schemaPattern = database_name;
			} else if (DatabaseType.SqlServer2000.getCode().equals(type) || DatabaseType.SqlServer2005.getCode().equals(type)
					|| DatabaseType.Postgresql.getCode().equals(type)) {
				isSchem = false;
			} else if (DatabaseType.TeraData.getCode().equals(type)) {
				schemaPattern = database_name;
				isSchem = false;
			}

			if (DatabaseType.TeraData.getCode().equals(type)) {
				ResultSet columnSet = dbmd.getColumns(null, schemaPattern, "%", "%");
				logger.info("数据库连接成功,已获取数据库表信息");
				//获取所有表
				String tableNameTemp = "";
				while (columnSet.next()) {
					String tableName = columnSet.getString("TABLE_NAME");
					/*
					 * TODO 1、第二个参数，DB2添加%
						2、第二个参数，oracle 是否可以为  OPS$AIMSADM，也可以是getUserName().toUpperCase()都待测试。
							oracle 貌似需要大写
						3、第二个参数，mysql可以加上面所有的都是ok的
					 */
					Map<String, String> isPrimaryKey = new HashMap<>();
					if (!tableName.equals(tableNameTemp)) {
						tableNameTemp = tableName;
						//添加表名
						addTable(tableName, tableName);
						ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName.toUpperCase());
						while (rs.next()) {
							isPrimaryKey.put(rs.getString("COLUMN_NAME"), "true");
						}
						rs.close();
					}
					String colName = columnSet.getString("COLUMN_NAME");//列名
					String typeName = columnSet.getString("TYPE_NAME");//类型名称
					int precision = columnSet.getInt("COLUMN_SIZE");//长度
					int isNull = columnSet.getInt("NULLABLE");//是否为空
					int dataType = columnSet.getInt("DATA_TYPE");//类型
					int scale = columnSet.getInt("DECIMAL_DIGITS");// 小数的位数

					String column_type = getColType(dataType, typeName, precision, scale);
					String primaryKey = (isPrimaryKey.get(colName) == null) ? "false" : "true";
					addColumn(colName, primaryKey, column_type, typeName, precision + "", isNull + "", dataType + "", scale + "");
					isPrimaryKey.clear();
				}
				columnSet.close();
				logger.info("写数据库表信息完成");
			} else {
				//获取所有表
				ResultSet tableSet = dbmd.getTables(null, schemaPattern, "%", new String[]{"TABLE"});
				while (tableSet.next()) {
					String tableSchem = tableSet.getString("TABLE_SCHEM");
					//Debug.info(logger, "val=" + userName + ";equalsIgoreCase=" + tableSchem + ";dbmd.getUserName():" + dbmd.getUserName());
					boolean flag;
					if (DatabaseType.Oracle10g.getCode().equals(type) || DatabaseType.Oracle9i.getCode().equals(type)) {
						flag = true;
					} else {
						flag = StringUtil.isEmpty(tableSchem) || dbmd.getUserName().equalsIgnoreCase(tableSchem) || !isSchem;
					}
					if (flag) {
						String tableName = tableSet.getString("TABLE_NAME");
						String tableComment = tableSet.getString("REMARKS");
						//添加表名
						addTable(tableName, tableComment);

						/*
						 * TODO 1、第二个参数，DB2添加%
							2、第二个参数，oracle 是否可以为  OPS$AIMSADM，也可以是getUserName().toUpperCase()都待测试。
								oracle 貌似需要大写
							3、第二个参数，mysql可以加上面所有的都是ok的
						 */
						ResultSet columnSet = dbmd.getColumns(null, schemaPattern, tableName, "%");

						List<Map<String, String>> list = new ArrayList<>();

						while (columnSet.next()) {
							Map<String, String> mapColumn = new HashMap<>();
							String colName = columnSet.getString("COLUMN_NAME");//列名
							String typeName = columnSet.getString("TYPE_NAME");//类型名称
							int precision = columnSet.getInt("COLUMN_SIZE");//长度
							int isNull = columnSet.getInt("NULLABLE");//是否为空
							int dataType = columnSet.getInt("DATA_TYPE");//类型
							int scale = columnSet.getInt("DECIMAL_DIGITS");// 小数的位数

							String column_type = getColType(dataType, typeName, precision, scale);

							mapColumn.put("column_type", column_type);
							mapColumn.put("colName", colName);
							mapColumn.put("typeName", typeName);
							mapColumn.put("precision", precision + "");
							mapColumn.put("isNull", isNull + "");
							mapColumn.put("dataType", dataType + "");
							mapColumn.put("scale", scale + "");
							list.add(mapColumn);
						}
						columnSet.close();
						ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName.toUpperCase());
						Map<String, String> isPrimaryKey = new HashMap<>();
						while (rs.next()) {
							isPrimaryKey.put(rs.getString("COLUMN_NAME"), "true");
						}
						for (Map<String, String> mapColumn : list) {
							String colName = mapColumn.get("colName");
							String typeName = mapColumn.get("typeName");
							String precision = mapColumn.get("precision");
							String isNull = mapColumn.get("isNull");
							String dataType = mapColumn.get("dataType");
							String scale = mapColumn.get("scale");
							String column_type = mapColumn.get("column_type");

							String primaryKey = (isPrimaryKey.get(colName) == null) ? "false" : "true";

							addColumn(colName, primaryKey, column_type, typeName, precision, isNull, dataType, scale);
						}
						rs.close();
					}
				}
				tableSet.close();
			}
			// 生成xml文档
			xmlCreater.buildXmlFile();
			logger.info("写xml信息完成");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException("获取表信息失败");
		}
	}

	public static void readModelFromDatabase(Connection conn, String xmlName, String type, String database_name, String table) {

		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			// 调用方法生成xml文件
			String userName = dbmd.getUserName();
			File file = FileUtils.getFile(xmlName);
			if (!file.exists()) {
				createXml(xmlName, userName);
			} else {
				//写文件之前先删除以前的文件
				if (!file.delete()) {
					throw new AppSystemException("删除文件失败");
				}
				createXml(xmlName, userName);
//				openXml(new File(xmlName).getAbsolutePath());
			}

			String schemaPattern = "%";

			if (DatabaseType.Oracle10g.getCode().equals(type) || DatabaseType.Oracle9i.getCode().equals(type)) {
				schemaPattern = database_name;
			} else if (DatabaseType.TeraData.getCode().equals(type)) {
				schemaPattern = database_name;
			}
			//table是多张表
			List<String> names = StringUtil.split(table, "|");
			for (String name : names) {
				ResultSet columnSet = dbmd.getColumns(null, schemaPattern, "%" + name + "%"
						, "%");
				//获取所有表
				String tableNameTemp = "";
				Map<String, String> isPrimaryKey = new HashMap<>();
				while (columnSet.next()) {
					String tableName = columnSet.getString("TABLE_NAME");
					if (!tableName.equals(tableNameTemp)) {
						tableNameTemp = tableName;
						xmlCreater.removeElement(tableName);
						//添加表名
						addTable(tableName, tableName);
						ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName.toUpperCase());
						while (rs.next()) {
							isPrimaryKey.put(rs.getString("COLUMN_NAME"), "true");
						}
						rs.close();
					}
					String colName = columnSet.getString("COLUMN_NAME");//列名
					String typeName = columnSet.getString("TYPE_NAME");//类型名称
					int precision = columnSet.getInt("COLUMN_SIZE");//长度
					int isNull = columnSet.getInt("NULLABLE");//是否为空
					int dataType = columnSet.getInt("DATA_TYPE");//类型
					int scale = columnSet.getInt("DECIMAL_DIGITS");// 小数的位数

					String column_type = getColType(dataType, typeName, precision, scale);
					String primaryKey = (isPrimaryKey.get(colName) == null) ? "false" : "true";
					addColumn(colName, primaryKey, column_type, typeName, precision + "", isNull + "", dataType + "", scale + "");
					isPrimaryKey.clear();
				}
				columnSet.close();
			}
			logger.info("写数据库表信息完成");
			// 生成xml文档
			xmlCreater.buildXmlFile();
			logger.info("写xml信息完成");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException("获取表信息失败");
		}
	}

	/**
	 * 获取数据库直连的数据类型
	 *
	 * @param dataType  {@link Integer} sql.Types的数据类型
	 * @param typeName  {@link String} 数据类型
	 * @param precision {@link String} 数据长度
	 * @param scale     {@link String} 数据精度
	 */
	public static String getColType(int dataType, String typeName, int precision, int scale) {

		//TODO 各数据库不同遇到在修改
		typeName = StringUtil.replace(typeName, "UNSIGNED", "");
		//考虑到有些类型在数据库中获取到就会带有(),同时还能获取到数据的长度，修改方式为如果本事类型带有长度，去掉长度，使用数据库读取的长度
		if (precision != 0) {
			int ic = typeName.indexOf("(");
			if (ic != -1) {
				typeName = typeName.substring(0, ic);
			}
		}
		String column_type;
		if (Types.INTEGER == dataType || Types.TINYINT == dataType || Types.SMALLINT == dataType || Types.BIGINT == dataType) {
			column_type = typeName;
		} else if (Types.NUMERIC == dataType || Types.FLOAT == dataType || Types.DOUBLE == dataType || Types.DECIMAL == dataType) {
			//1、当一个数的整数部分的长度 > p-s 时，Oracle就会报错
			//2、当一个数的小数部分的长度 > s 时，Oracle就会舍入。
			//3、当s(scale)为负数时，Oracle就对小数点左边的s个数字进行舍入。
			//4、当s > p 时, p表示小数点后第s位向左最多可以有多少位数字，如果大于p则Oracle报错，小数点后s位向右的数字被舍入
			if (precision > precision - Math.abs(scale) || scale > precision || precision == 0) {
				precision = 38;
				scale = 15;
			}
			column_type = typeName + "(" + precision + "," + scale + ")";
		} else {
			if ("char".equalsIgnoreCase(typeName) && precision > 255) {
				typeName = "varchar";
			}
			column_type = typeName + "(" + precision + ")";
		}
		return column_type;
	}

	public static List<Map<String, String>> getSqlInfo(String sql, Connection conn) {

		Statement statement = null;
		ResultSet executeQuery = null;
		try {
			List<Map<String, String>> columnList = new ArrayList<>();
			statement = conn.createStatement();
			executeQuery = statement.executeQuery(sql);
			ResultSetMetaData metaData = executeQuery.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				Map<String, String> hashMap = new HashMap<>();
				hashMap.put("column_name", metaData.getColumnName(i));
				hashMap.put("type", getColType(metaData.getColumnType(i), metaData.getColumnTypeName(i), metaData.getPrecision(i), metaData.getScale(i)));
				columnList.add(hashMap);
			}
			return columnList;
		} catch (SQLException e) {
			throw new BusinessException("sql错误" + e.getMessage());
		} finally {
			try {
				if (executeQuery != null)
					executeQuery.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				logger.error("关闭statement错误", e);
			}
		}
	}

//	public static Connection getConnectionTest() {
//
//		Connection conn = null;
//		//		PreparedStatement stmt;
//		try {
//			Class.forName("org.postgresql.Driver");
//			String url = "jdbc:postgresql://10.71.4.61:31001/hrsdxg";
//			String user = "hrsdxg";
//			String pass = "hrds";
//
//			//			Class.forName("com.mysql.jdbc.Driver");
//			//			String url = "jdbc:mysql://172.168.0.200:3306/hrds";
//			conn = DriverManager.getConnection(url, user, pass);
//
//			readModelFromDatabase(conn, "666666.xml", DatabaseType.Postgresql.getCode(), "hrds", "code_info");
//			//			String sql = "select * from result_table_copy limit 1";
//			//			stmt = (PreparedStatement)conn.prepareStatement(sql);
//			//
//			//			ResultSet rs = stmt.executeQuery();
//			//
//			//			ResultSetMetaData data = rs.getMetaData();
//			//
//			//			while( rs.next() ) {
//			//				for(int i = 1; i <= data.getColumnCount(); i++) {
//			//					System.out.println(Platform.getColType(data.getColumnType(i), data.getColumnTypeName(i), data.getPrecision(i), data.getScale(i)));
//			//				}
//			//			}
//
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return conn;
//	}
}
