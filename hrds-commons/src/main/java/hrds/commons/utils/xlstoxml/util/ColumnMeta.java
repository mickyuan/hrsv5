package hrds.commons.utils.xlstoxml.util;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.Column_split;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.xlstoxml.Xls2xml;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cool
 */
public class ColumnMeta {

	private static final Log logger = LogFactory.getLog(ColumnMeta.class);

	/**
	 * 写表的字段信息及生成文件信息
	 *
	 * @param tableName
	 * @param file
	 * @param columns
	 * @param liner
	 * @param list
	 * @param lengths
	 * @param meta_filesize
	 */
	public static void writeFileMeta(String tableName, File file, String columns, long liner, StringBuilder list, StringBuilder lengths, long meta_filesize, String mr) {

		BufferedWriter bw = null;
		String metaFile = file.getAbsolutePath() + "/tabledata.meta";
		metaFile = FilenameUtils.normalize(metaFile);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metaFile, true), "utf-8"));

			StringBuilder sb = new StringBuilder();
			sb.append(tableName);
			JSONObject jsonSon = new JSONObject();
			jsonSon.put("tablename", sb.toString());
			jsonSon.put("column", columns.toString());
			jsonSon.put("length", lengths.toString());
			jsonSon.put("records", String.valueOf(liner));
			jsonSon.put("filesize", meta_filesize);
			jsonSon.put("type", list.toString());
			jsonSon.put("mr", mr);

			bw.write(jsonSon + "\n");
			bw.flush();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage());
				}
			}
		}

	}

	/**
	 * 查找表的所有信息
	 *
	 * @param tName         表名
	 * @param xml_file_path xml路径
	 * @return 表的所有信息
	 */
	public static List<String> getColumnList(String tName, String xml_file_path) {
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document doc;
		NodeList table_list;
		NodeList column_list;
		String table_name;
		List<String> clist = new ArrayList<String>();
		try {
			File f = new File(xml_file_path);
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(f);
			table_list = doc.getElementsByTagName("table");
			for (int i = 0; i < table_list.getLength(); i++) {
				table_name = table_list.item(i).getAttributes().getNamedItem("table_name").getNodeValue();
				table_name = table_name.toLowerCase();
				tName = tName.toLowerCase();
				if (table_name.equals(tName)) {
					column_list = table_list.item(i).getChildNodes();
					for (int j = 0; j < column_list.getLength(); j++) {
						//只取column的属性,表属性只做页面选择使用
						if("column".equals(column_list.item(j).getNodeName())){
							String column_name = column_list.item(j).getAttributes().getNamedItem("column_name").getNodeValue();
							String primaryKey = column_list.item(j).getAttributes().getNamedItem("is_primary_key").getNodeValue();
							String length = column_list.item(j).getAttributes().getNamedItem("length").getNodeValue();
							String column_type = column_list.item(j).getAttributes().getNamedItem("column_type").getNodeValue();
							clist.add(column_name + "^" + primaryKey + "^" + length + "^" + column_type);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
		return clist;

	}

	public static String updateColumn(Map<String, String> mergeIng, Map<String, Map<String, Column_split>> splitIng, StringBuilder columns,
	                                  StringBuilder colType) {

		return updateColumn(mergeIng, splitIng, columns, colType, new StringBuilder());
	}

	/**
	 * 更新因为合并字段或者字段拆分而生成新字段的数据meta信息
	 *
	 * @param mergeIng 合并字段信息
	 * @param splitIng 拆分字段信息
	 * @param columns  被更新的列
	 * @param colType  对应的类型
	 * @param lengths  对应的类型的长度
	 * @return 更新后的信息
	 */
	public static String updateColumn(Map<String, String> mergeIng, Map<String, Map<String, Column_split>> splitIng, StringBuilder columns,
	                                  StringBuilder colType, StringBuilder lengths) {

		if (!mergeIng.isEmpty()) {
			for (String key : mergeIng.keySet()) {
				//获取表名和类型
				List<String> split = StringUtil.split(key, Clean_ParseJson.STRSPLIT);
				columns.append(',').append(split.get(0));
				colType.append('|').append(split.get(1));
				lengths.append(',').append(Xls2xml.getLength(split.get(1)));
			}
		}

		String colmeta = columns.toString();

		if (!splitIng.isEmpty()) {
			for (String key : splitIng.keySet()) {
				StringBuilder newColumn = new StringBuilder();
				StringBuilder newColty = new StringBuilder();
				StringBuilder newCollen = new StringBuilder();
				Map<String, Column_split> map = splitIng.get(key);
				if (map != null) {
					if (true) {//默认保留原字段
						newColumn.append(key).append(',');
					}
					//找到列所在分隔符位置
					int findColIndex = findColIndex(colmeta, key, ",");
					for (String newName : map.keySet()) {
						//获取表名和类型
						Column_split column_split = map.get(newName);
						newColumn.append(newName).append(',');
						newColty.append(column_split.getCol_type()).append('|');
						newCollen.append(Xls2xml.getLength(column_split.getCol_type())).append(',');
					}

					newColumn = newColumn.deleteCharAt(newColumn.length() - 1);
					newColty = newColty.deleteCharAt(newColty.length() - 1);
					newCollen = newCollen.deleteCharAt(newCollen.length() - 1);
					//获取对应列类型的位置插入拆分后的列类型
					int searchIndex = searchIndex(colType.toString(), findColIndex, "|");
					int lenIndex = searchIndex(lengths.toString(), findColIndex, ",");
					//	Debug.info(logger, "searchIndex----------------------------------------------------------------------------"+searchIndex);
					//	Debug.info(logger, "colType---------------------------------------------------------------------------------"+colType.length());
					//插入新加的类型
					if (searchIndex != -1) {
						colType.insert(searchIndex, "|" + newColty.toString());
					} else {
						//拆分的为表的最后一个字段colType执行insert改为直接追加
						//	Debug.info(logger, "我是最后一个字段做拆分啊----------------------------------------------------------------------------");
						colType.append("|" + newColty.toString());
					}
					if (lenIndex > 0) {
						lengths.insert(lenIndex, "," + newCollen.toString());
					}
					colmeta = StringUtil.replace(colmeta.toUpperCase(), key.toUpperCase(), newColumn.toString().toUpperCase());
				}
			}
		}
		return colmeta;
	}

	/**
	 * 获取指定位置指定字符串的下标
	 *
	 * @param str
	 * @param n
	 * @return
	 */
	private static int searchIndex(String str, int n, String key) {

		int a = 0;//*第一个出现的索引位置
		int num = 0;
		while (a != -1) {
			num++;
			a = str.indexOf(key, a + 1);//*从这个索引往后开始第一个出现的位置
			if (num == n) {
				break;
			}
		}
		return a;
	}

	private static int findColIndex(String columns, String str, String key) {

		List<String> columnList = StringUtil.split(columns, key);
		int index = 0;
		for (int j = 0; j < columnList.size(); j++) {
			if (columnList.get(j).equalsIgnoreCase(str)) {
				index = j + 1;
				break;
			}
		}
		return index;
	}

}
