package hrds.commons.utils.xlstoxml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UnloadType;
import hrds.commons.exception.BusinessException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.w3c.dom.Element;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Xls2xml {

	public static XmlCreater xmlCreater = null;
	public static Element table = null;
	public static Element root = null;
	public static Element column = null;
	public static Element storage = null;
	public static Element handleType = null;
	private static final Log logger = LogFactory.getLog(Xls2xml.class);
	private static final String NONEED_ = "NONEED_";

	private static Map<String, Integer> colType = new HashMap<String, Integer>();

	static {
//		String jsonType = "";
//		try {
//			jsonType = PropertyUtil.getMessage("fixedType");
//		}
//		catch(Exception e) {}
//		if( !ObjectUtil.isEmpty(jsonType) ) {
//			JSONObject jsonTypeObj = JSON.parseObject(jsonType);
//			Set<String> keys = jsonTypeObj.keySet();
//			for(String key : keys) {
//				colType.put(key, jsonTypeObj.getInteger(key));
//			}
//		}
//		JSONObject jsonTypeObj = null;
//		try {
//			String jsonType = PropertyUtil.getMessage("fixedType");
//			jsonTypeObj = JSON.parseObject(jsonType);
//		} catch (Exception e) {
//			logger.info("配置文件中加载不到取默认值");
//		}
/*		if (null != jsonTypeObj) {
			Set<String> keys = jsonTypeObj.keySet();
			for (String key : keys) {
				colType.put(key, jsonTypeObj.getInteger(key));
			}
		} else*/
		{
			colType.put("INTEGER", 12);
			colType.put("BIGINT", 22);
			colType.put("SMALLINT", 8);
			colType.put("DOUBLE", 24);
			colType.put("REAL", 16);
			colType.put("TIMESTAMP", 14);
			colType.put("DATE", 8);
			colType.put("LONGVARCHAR", 4000);
			colType.put("CLOB", 4000);
			colType.put("BLOB", 4000);
			colType.put("DECFLOAT", 34);

			colType.put("VARCHAR", 0);
			colType.put("CHARACTER", 0);
			colType.put("DECIMAL", 0);
		}
	}

	public static String getheadCellValue(Cell cell) {

		String cellvalue = "";
		if (cell == null) {
			return cellvalue;
		}
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC: // 数字
				cellvalue = Double.toString(cell.getNumericCellValue()).trim();
				break;
			case Cell.CELL_TYPE_STRING: // 字符串
				cellvalue = cell.getStringCellValue().trim();
				break;
			case Cell.CELL_TYPE_BOOLEAN: // Boolean
				cellvalue = Boolean.toString(cell.getBooleanCellValue()).trim();
				break;
			case Cell.CELL_TYPE_FORMULA: // 公式
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cellvalue = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BLANK: // 空值
				cellvalue = "";
				break;
			case Cell.CELL_TYPE_ERROR: // 故障
				cellvalue = "error";
				break;
			default:
				cellvalue = "unknown value";
				break;
		}
		return cellvalue;
	}

	public static void toXml(String db_path, String xml_path) {

		String path_cd = pathToUnEscape(db_path + "~dd_data.json");
		File file = FileUtils.getFile(path_cd);
		if (file.exists()) {
			jsonToXml(path_cd, xml_path);
		} else {
			path_cd = pathToUnEscape(db_path + "~dd_data.xls");
			file = FileUtils.getFile(db_path);
			if (!file.exists()) {
				throw new BusinessException("没有找到相应的数据字典定义文件！");
			}
			XlsToXml(path_cd, xml_path);
		}
	}

	public static void toXmlForObjectCollect(String db_path, String xml_path) {

		String path_cd = pathToUnEscape(db_path + "~dd_data.json");
		File file = FileUtils.getFile(path_cd);
		if (file.exists()) {
			jsonToXmlForObjectCollect(path_cd, xml_path);
		} else {
			throw new BusinessException("没有找到相应的数据字典定义文件！");
		}
	}

	public static void jsonToXmlForObjectCollect(String json_path, String xml_path) {
		// 调用方法生成xml文件
		createXml(xml_path);
		String info = "";
		BufferedReader br = null;
		try {
			StringBuilder result = new StringBuilder();
			br = new BufferedReader(new FileReader(json_path));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append('\n').append(s);
			}
			JSONArray jsonArray = JSONArray.parseArray(result.toString());
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				String table_name = json.getString("table_name");// 表名
				String table_cn_name = json.getString("table_cn_name");// 中文表名
				String updatetype = json.getString("updatetype");// 数据存储方式
				addTable(table_name.toLowerCase(), table_cn_name, updatetype);
				JSONObject handletype = json.getJSONObject("handletype");
				String insert = handletype.getString("insert");
				String update = handletype.getString("update");
				String delete = handletype.getString("delete");
				addHandleType(insert, update, delete);
				JSONArray columns = json.getJSONArray("columns");// 列信息
				for (int j = 0; j < columns.size(); j++) {
					JSONObject column = columns.getJSONObject(j);
					String column_id = column.getString("column_id");// 列ID
					String column_name = column.getString("column_name").toLowerCase();// 字段名
					String column_cn_name = column.getString("column_cn_name");// 字段中文名
					String column_type = column.getString("column_type");// 字段类型
					String is_key = column.getString("is_key");// 是否为主键
					String column_remark = column.getString("column_remark");// 备注信息
					String columnposition = column.getString("columnposition");// 字段位置
					String is_hbase = column.getString("is_hbase");// 是否进入hbase
					String is_rowkey = column.getString("is_rowkey");// 是否为rowkey
					String is_solr = column.getString("is_solr");// 是否进solr
					String is_operate = column.getString("is_operate");// 是否为操作标识字段
					int length = getLength(column_type);
					addColumnForObjectCollect(column_id, column_name, column_cn_name, column_type, length,
							column_remark, is_key,
							columnposition, is_hbase, is_rowkey, is_solr, is_operate);
				}
			}
			xmlCreater.buildXmlFile();// 生成xml文档
			br.close();
		} catch (FileNotFoundException e) {
			throw new BusinessException("文件不存在," + e.getMessage());
		} catch (IOException e) {
			throw new BusinessException("读取文件失败," + e.getMessage());
		}
	}

	public static void addColumnForObjectCollect(String column_id, String column_name, String column_cn_name,
	                                             String column_type, int length, String column_remark,
	                                             String is_key, String columnposition, String is_hbase,
	                                             String is_rowkey, String is_solr, String is_operate) {

		column = xmlCreater.createElement(table, "column");
		xmlCreater.createAttribute(column, "column_id", column_id);
		xmlCreater.createAttribute(column, "name", column_name);
		xmlCreater.createAttribute(column, "column_cn_name", column_cn_name);
		xmlCreater.createAttribute(column, "column_type", column_type);
		xmlCreater.createAttribute(column, "length", String.valueOf(length));
		xmlCreater.createAttribute(column, "is_key", is_key);
		xmlCreater.createAttribute(column, "column_remark", column_remark);
		xmlCreater.createAttribute(column, "columnposition", columnposition);
		xmlCreater.createAttribute(column, "is_hbase", is_hbase);
		xmlCreater.createAttribute(column, "is_rowkey", is_rowkey);
		xmlCreater.createAttribute(column, "is_solr", is_solr);
		xmlCreater.createAttribute(column, "is_operate", is_operate);
	}

	public static void addHandleType(String insert, String update, String delete) {

		handleType = xmlCreater.createElement(table, "handletype");
		xmlCreater.createAttribute(handleType, "insert", insert);
		xmlCreater.createAttribute(handleType, "update", update);
		xmlCreater.createAttribute(handleType, "delete", delete);
	}

	/**
	 * 根据系统将~修改成对应路径地址
	 *
	 * @param path 文件路径
	 * @return 转换后的路径
	 */
	private static String pathToUnEscape(String path) {

		if (SystemUtils.OS_NAME.toLowerCase().contains("win")) {
			return StringUtil.replace(path, "~", "\\");
		} else {
			return StringUtil.replace(path, "~", "/");
		}
	}

	public static void jsonToXml(String json_path, String xml_path) {

		InputStream xlsFileInputStream = null;
		createXml(xml_path);// 调用方法生成xml文件
		String info = "";
		BufferedReader br = null;
		try {
			StringBuilder result = new StringBuilder();
			br = new BufferedReader(new FileReader(json_path));//构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
				result.append('\n').append(s);
			}
			JSONArray jsonArray = JSONArray.parseArray(result.toString());
			String primaryKey = "1";
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				String table_name = json.getString("table_name");//表名
				String table_cn_name = json.getString("table_cn_name");//中文表名
				String storage_type = json.getString("storage_type");//数据存储方式
				addTable(table_name.toLowerCase(), table_cn_name, storage_type);
				JSONArray columns = json.getJSONArray("columns");//列信息
				for (int j = 0; j < columns.size(); j++) {
					JSONObject column = columns.getJSONObject(j);
					String column_id = column.getString("column_id");//列ID
					String column_name = column.getString("column_name").toLowerCase();//字段名
					String column_cn_name = column.getString("column_cn_name");//字段中文名
					String column_type = column.getString("column_type");//字段类型
					String column_key = column.getString("column_key");//是否为主键
					String column_null = column.getString("column_null");//是否可空
					String column_remark = column.getString("column_remark");//备注信息

					if ("pk".equalsIgnoreCase(column_key)) {
						primaryKey = "0";
					}
					int length = getLength(column_type);
					addColumn(column_id, column_name, column_cn_name, column_type, length, column_key, column_null, column_remark, primaryKey);
				}
				JSONArray storages = json.getJSONArray("storage");
				for (int j = 0; j < storages.size(); j++) {
					JSONObject storageJson = storages.getJSONObject(j);
					String file_format = storageJson.getString("file_format");//文件格式
					String is_header = storageJson.getString("is_header");//是否有表头
					String row_separator = storageJson.getString("row_separator");//行分隔符
					String column_separator = storageJson.getString("column_separator");//列分隔符
					String root_path = storageJson.getString("root_path");//采集文件的跟目录
					String file_code = storageJson.getString("file_code");//采集文件的编码
					addStorage(file_format, is_header, row_separator, column_separator, root_path, file_code);
				}
			}
			xmlCreater.buildXmlFile();// 生成xml文档
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				//如果继续错误，将已经身
				File file = new File(xml_path);
				if (file.exists()) {
					file.delete();
				}
			}
			logger.info(info + "json定义错误数据错误");
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(xlsFileInputStream);
		}
	}

	/**
	 * 将xls转换成xml
	 *
	 * @param xls_path {@link String} xls的路径
	 * @param xml_path {@link String} 吐出xml的路径
	 */
	public static void XlsToXml(String xls_path, String xml_path) {

		InputStream xlsFileInputStream = null;
		createXml(xml_path);// 调用方法生成xml文件
		int number = 0;
		String info = "";
		try {
			File file = new File(xls_path);
			xlsFileInputStream = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(xlsFileInputStream);
			if (workbook == null) {
				throw new BusinessException("没有找到相应的xml");
			}
			Sheet sheet = workbook.getSheetAt(1);
			if (sheet == null) {
				throw new BusinessException("没有找到表单对象");
			}
			int frstrow = sheet.getFirstRowNum(); //首行
			int lastrow = sheet.getLastRowNum(); //尾行
			logger.info("首行：" + frstrow);
			logger.info("尾行：" + lastrow);
			if (lastrow == 0) {
				throw new BusinessException("数据字典必须有数据！");
			}
			//得到表头
			Row rowhead = sheet.getRow(frstrow);
			int frstcell = rowhead.getFirstCellNum(); //首列
			int lastcell = rowhead.getLastCellNum(); //尾列
			logger.info("首列：" + frstcell);
			logger.info("尾列：" + lastcell);
			int tableIndex = 1;
			boolean isNoneed = false;
			for (int i = frstrow + 1; i <= lastrow; i++) {
				Row row = sheet.getRow(i);
				number = row.getRowNum();
				if (row != null) {
					String isTable = "";
					if (i == tableIndex) {//如果整行为空,下一行一定是表名
						Cell cell = row.getCell(0);
						if (cell == null) {
							continue;
						}
						String value = getheadCellValue(cell);
						if (StringUtil.isEmpty(value)) {
							isNoneed = true;
							continue;
						} else {
							isNoneed = false;
						}
						logger.info(value);
						List<String> table_name = StringUtil.split(value, "-");
						String en_table_name = "";
						String cn_table_name = "";
//						String storage_type = "1";
						if (table_name.get(0) != null) {
							en_table_name = subString(table_name.get(0), 100);
						}
						if (table_name.size() >= 2) {
							cn_table_name = subString(table_name.get(1), 200);
						}
						//添加表的存储方式
//						if (table_name.size() >= 3) {
//							storage_type = UnloadType.QuanLiangXieShu.getCode();
//						}
						//如果表名开头为neneed_不作为数据加载
						if (StringUtil.isEmpty(en_table_name) || en_table_name.toUpperCase().startsWith(NONEED_)) {
							isNoneed = true;
							continue;
						} else {
							isNoneed = false;
						}
						info = en_table_name;
						//TODO xls需要重新设计，这里待修改 这里的卸数方式默认为全量方式,
						addTable(en_table_name.toLowerCase(), cn_table_name, UnloadType.QuanLiangXieShu.getCode());
						continue;
					}

					if (i == tableIndex + 1) {//表名下一行一定是表头，不用进行读取
						continue;
					}
					String primaryKey = IsFlag.Fou.getCode();
					String column_id = "", column_name = "", column_cn_name = "", column_type = "", column_key = "", column_null = "", column_remark = "";
					for (int j = 0; j < 7; j++) {
						Cell cell = row.getCell(j);
						if (cell == null) {
							continue;
						}
						String value = getheadCellValue(cell).replaceAll(":", "-");
						if (j == 0) {
							column_id = value;
						}
						if (j == 1) {
							column_name = subString(value, 200);
						}
						if (j == 2) {
							column_cn_name = subString(value, 200);
						}
						if (j == 3) {
							column_type = value;
						}
						if (j == 4) {
							column_key = value;
						}
						if (j == 5) {
							column_null = value;
						}
						if (j == 6) {
							column_remark = subString(value, 200);
							;
						}
						isTable += value;
					}
					//i不等于表名且不等于全空额，且表头continue后，其他肯定是列内容
					if (!StringUtil.isEmpty(isTable)) {
						if ("pk".equalsIgnoreCase(column_key)) {
							primaryKey = IsFlag.Shi.getCode();
						}
						if (!isNoneed) {
							int length = getLength(column_type);
							addColumn(column_id, column_name.toLowerCase(), column_cn_name, column_type, length, column_key, column_null,
									column_remark, primaryKey);
						}
						primaryKey = IsFlag.Fou.getCode();
					}
					//如果整行为空,下一行一定是表名
					if (StringUtil.isEmpty(isTable)) {
						tableIndex = 1;
						tableIndex = i + 1;
					}
				}
			}
			xmlCreater.buildXmlFile();// 生成xml文档
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				//如果继续错误，将已经生成的删除
				File file = new File(xml_path);
				if (file.exists()) {
					file.delete();
				}
			}
			logger.info(info + " 表的第 " + number + "数据错误");
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(xlsFileInputStream);
		}
	}

	/**
	 * 从字符串首位开始获取指定字符串的指定长度的子串，不考虑中文情况造成的字符串长度变化问题
	 *
	 * @param sourceString 源字符串
	 * @param maxLength    截取最大长度
	 * @return 剪切后的结果，如果源字符串为null，返回空串
	 */
	public static String subString(String sourceString, int maxLength) {

		String innerSourceString = sourceString;
		if (null == sourceString) {//如果为null，返回空串

			innerSourceString = "";
		}
		String endString = "";
		int trueLength = innerSourceString.length();
		if (trueLength > maxLength) {//实际长度大于需要的长度
			endString = innerSourceString.substring(0, maxLength);
		} else {
			endString = innerSourceString;
		}
		return endString;
	}

	public static void createXml(String path) {

		xmlCreater = new XmlCreater(path);
		root = xmlCreater.createRootElement("database");
		xmlCreater.createAttribute(root, "xmlns", "http://db.apache.org/ddlutils/schema/1.1");
		xmlCreater.createAttribute(root, "name", "dict_params");
	}

	public static void addTable(String en_table_name, String cn_table_name, String unload_type) {

		table = xmlCreater.createElement(root, "table");
		xmlCreater.createAttribute(table, "table_name", en_table_name);
		xmlCreater.createAttribute(table, "table_ch_name", cn_table_name);
		xmlCreater.createAttribute(table, "unload_type", unload_type);

	}

	public static void addColumn(String column_id, String column_name, String column_cn_name, String
			column_type, int length, String column_key, String column_null, String column_remark,
	                             String primaryKey) {

		column = xmlCreater.createElement(table, "column");
		xmlCreater.createAttribute(column, "column_id", column_id);
		xmlCreater.createAttribute(column, "column_name", column_name);
		xmlCreater.createAttribute(column, "column_ch_name", column_cn_name);
		xmlCreater.createAttribute(column, "column_type", column_type);
		xmlCreater.createAttribute(column, "length", String.valueOf(length));
		xmlCreater.createAttribute(column, "column_key", column_key);
		xmlCreater.createAttribute(column, "column_null", column_null);
		xmlCreater.createAttribute(column, "column_remark", column_remark);
		xmlCreater.createAttribute(column, "is_primary_key", primaryKey);
		xmlCreater.createAttribute(column, "is_get", IsFlag.Shi.getCode());
		xmlCreater.createAttribute(column, "is_alive", IsFlag.Shi.getCode());
		xmlCreater.createAttribute(column, "is_new", IsFlag.Fou.getCode());

	}

	private static void addStorage(String file_format, String is_header, String row_separator, String column_separator,
	                               String root_path, String file_code) {
		storage = xmlCreater.createElement(table, "storage");
		xmlCreater.createAttribute(storage, "file_format", file_format);
		xmlCreater.createAttribute(storage, "is_header", is_header);
		xmlCreater.createAttribute(storage, "row_separator", row_separator);
		xmlCreater.createAttribute(storage, "column_separator", column_separator);
		xmlCreater.createAttribute(storage, "root_path", String.valueOf(root_path));
		xmlCreater.createAttribute(storage, "file_code", file_code);
	}

	/**
	 * 获取每个字典的长度
	 *
	 * @param column_type
	 * @return
	 */
	public static int getLength(String column_type) {
		column_type = column_type.trim();
		int length = colType.get(column_type.toUpperCase()) == null ? 0 : colType.get(column_type.toUpperCase());
		if (length == 0) {
			int start = column_type.indexOf("(");
			int end = column_type.indexOf(")");
			String substring = column_type.substring(start + 1, end);
			if (substring.indexOf(",") != -1) {
				return Integer.parseInt(StringUtil.split(substring, ",").get(0)) + 2;
			}
			return Integer.parseInt(substring);
		}
		return length;
	}

	public static void main(String[] args) {

		//System.out.println(getLength("varchar(50)"));
		jsonToXml("D:\\dd_data.json", "d:\\c11.xml");
	}
}
