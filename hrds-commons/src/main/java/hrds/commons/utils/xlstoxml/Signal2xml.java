package hrds.commons.utils.xlstoxml;

import com.alibaba.fastjson.JSONObject;
import hrds.commons.utils.xlstoxml.util.SignalParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Signal2xml {

	private static final Log logger = LogFactory.getLog(Signal2xml.class);

	private Signal2xml() {}

	public static boolean isColChange(String signal_path, String xml_path) throws Exception {

		SignalParser parser = new SignalParser(signal_path);
		String table_name = parser.getFilename().substring(0, parser.getFilename().lastIndexOf("_")).toLowerCase();
		List<String> old_cols = new ArrayList<String>();
		List<String> new_cols = new ArrayList<String>();
		
		File xml_file = new File(xml_path);
		if( xml_file.exists() ) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xml_file);
			NodeList table_list = doc.getElementsByTagName("table");
			for(int i = 0, tables_len = table_list.getLength(); i < tables_len; i++) {
				Node table = table_list.item(i);
				String name = table.getAttributes().getNamedItem("name").getNodeValue().toLowerCase();
				if( table_name.equalsIgnoreCase(name) ) {
					NodeList column_list = table.getChildNodes();
					for(int j = 0, columns_len = column_list.getLength(); j < columns_len; j++) {
						String col_name = column_list.item(j).getAttributes().getNamedItem("name").getNodeValue();
						old_cols.add(col_name);
					}
				}
			}
		}
		List<JSONObject> columns = parser.getColumndescription();
		for(int i = 0; i < columns.size(); i++) {
			JSONObject col = columns.get(i);
			String col_name = col.getString("name");
			new_cols.add(col_name);
		}
		
		int old_cols_size = old_cols.size();
		if(old_cols_size > 0){
			if(old_cols_size != new_cols.size()){
				return true;
			}else{
				old_cols.retainAll(new_cols);
				if(old_cols.size() != old_cols_size){
					return true;
				}
			}
		}

		return false;
	}

	public static void toXml(String signal_path, String xml_path) {

		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;
		Element root = null;
		NodeList table_list = null;
		File xml_file = new File(xml_path);
		XmlCreater xmlCreater = new XmlCreater(xml_file.getAbsolutePath());

		try {
			SignalParser parser = new SignalParser(signal_path);
			String table_name = parser.getFilename().substring(0, parser.getFilename().lastIndexOf("_")).toLowerCase();
			List<JSONObject> columns = parser.getColumndescription();
			String storage_type = "1";

			if( xml_file.exists() ) {
				factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				doc = builder.parse(xml_file);
				root = doc.getDocumentElement();
				table_list = doc.getElementsByTagName("table");
				for(int i = 0; i < table_list.getLength(); i++) {
					Node item = table_list.item(i);
					String name = item.getAttributes().getNamedItem("name").getNodeValue().toLowerCase();
					if( table_name.equals(name) ) {
						root.removeChild(item);
					}
				}
				xml_file.delete();
			}
			else {
				doc = xmlCreater.getDoc();
				root = xmlCreater.createRootElement("database");
				xmlCreater.createAttribute(root, "xmlns", "http://db.apache.org/ddlutils/schema/1.1");
				xmlCreater.createAttribute(root, "name", "dict_params");
			}

			Element table = xmlCreater.createElement(root, "table");
			xmlCreater.createAttribute(table, "name", table_name);
			xmlCreater.createAttribute(table, "description", table_name);
			xmlCreater.createAttribute(table, "storage_type", storage_type);

			for(int i = 0; i < columns.size(); i++) {
				JSONObject col = columns.get(i);
				int length = Integer.parseInt(col.getString("end")) - Integer.parseInt(col.getString("start")) + 1;
				Element column = xmlCreater.createElement(table, "column");
				xmlCreater.createAttribute(column, "column_id", col.getString("index"));
				xmlCreater.createAttribute(column, "name", col.getString("name"));
				xmlCreater.createAttribute(column, "column_cn_name", "");
				xmlCreater.createAttribute(column, "column_type", col.getString("type"));
				xmlCreater.createAttribute(column, "length", String.valueOf(length));
				xmlCreater.createAttribute(column, "column_key", "");
				xmlCreater.createAttribute(column, "column_null", "");
				xmlCreater.createAttribute(column, "column_remark", "");
				xmlCreater.createAttribute(column, "primaryKey", "");
			}

			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xml_file);
			transformer.setOutputProperty("encoding", "UTF-8");
			transformer.transform(source, result);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
