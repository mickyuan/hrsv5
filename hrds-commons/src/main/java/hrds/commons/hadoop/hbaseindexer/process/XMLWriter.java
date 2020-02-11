package hrds.commons.hadoop.hbaseindexer.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hrds.commons.hadoop.hbaseindexer.bean.HbaseSolrField;
import hrds.commons.hadoop.hbaseindexer.configure.ConfigurationUtil;
import hrds.commons.hadoop.hbaseindexer.type.DataTypeTransformSolr;
import hrds.commons.utils.Constant;
import org.apache.commons.io.FileUtils;


/**
 * 写临时xml配置文件
 *
 * @author Mick
 *
 */
public class XMLWriter {

	//自定义类实现 solr中的id为 ${table name}@${rowkey}
	private static final String UNIQUE_KEY_FORMATTER = "customization.hyren.formatter.UniqueTableKeyFormatterImpl";
	private static final String CF = new String(Constant.HBASE_COLUMN_FAMILY);

	/**
	 * 根据表信息 写xml配置文件
	 *
	 * @param hbaseTableName 数据映射的hbase表名
	 * @param xmlFilePath    xml配置文件的路径
	 * @param fields         hbase对应solr的基本字段信息
	 * @throws IOException
	 */
	public static void write(String hbaseTableName, String xmlFilePath, List<HbaseSolrField> fields)
			throws IOException {
		/*
		 * 拼接xml配置文件的内容
		 */
		List<String> xmlContents = new ArrayList<>();
		xmlContents.add("<?xml version=\"1.0\"?>");
		xmlContents.add(
				"<indexer table=\"" + hbaseTableName + "\" mapping-type=\"row\" read-row=\"never\" table-name-field=\""
						+ ConfigurationUtil.TABLE_NAME_FIELD + "\" unique-key-formatter=\"" + UNIQUE_KEY_FORMATTER + "\" >");

		for (HbaseSolrField field : fields) {
			xmlContents.add("<field name=\"" + field.getSolrColumnName() + "\" value=\"" + CF + ":"
					+ field.getHbaseColumnName() + "\" source=\"value\" type=\""
					+ DataTypeTransformSolr.transformToRealTypeInSolr(field.getType()) + "\"/>");
		}
		xmlContents.add("</indexer>");

		// 写文件到临时文件
		FileUtils.writeLines(new File(xmlFilePath), xmlContents);

	}
}
