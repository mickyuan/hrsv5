package hrds.commons.utils.xlstoxml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// 用于操作XML文件，包括查找、新增、删除、修改结点
public class XmlOper {
	private static final Log logger = LogFactory.getLog(XmlOper.class);

	private XmlOper() {

	}

	// 功能：获取父结点parent的所有子结点
	public static NodeList getNodeList(Element parent) {

		return parent.getChildNodes();
	}

	// 功能：在父结点中查询指定名称的结点集
	public static Element[] getElementsByName(Element parent, String name) {

		List<Node> resList = new ArrayList<Node>();
		NodeList nl = getNodeList(parent);
		for (int i = 0; i < nl.getLength(); i++) {
			Node nd = nl.item(i);
			if (nd.getNodeName().equals(name)) {
				resList.add(nd);
			}
		}
		Element[] res = new Element[resList.size()];
		for (int i = 0; i < resList.size(); i++) {
			res[0] = (Element) resList.get(i);
		}
		return res;
	}

	// 功能：获取指定Element的名称
	public static String getElementName(Element element) {

		return element.getNodeName();
	}

	// 功能：获取指定Element的值
	public static String getElementValue(Element element) {

		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.TEXT_NODE)// 是一个Text Node
			{
				return element.getFirstChild().getNodeValue();
			}
		}
		return null;
	}

	// 功能：获取指定Element的属性attr的值
	public static String getElementAttr(Element element, String attr) {

		return element.getAttribute(attr);
	}

	// 功能：设置指定Element的值
	public static void setElementValue(Element element, String val) {

		Node node = element.getOwnerDocument().createTextNode(val);
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node nd = nl.item(i);
			if (nd.getNodeType() == Node.TEXT_NODE)// 是一个Text Node
			{
				nd.setNodeValue(val);
				return;
			}
		}
		element.appendChild(node);
	}

	// 功能：设置结点Element的属性
	public static void setElementAttr(Element element, String attr, String attrVal) {

		element.setAttribute(attr, attrVal);

	}

	// 功能：设置结点Element的属性
	public static void removeElementAttr(Element element, String attr) {

		element.removeAttribute(attr);

	}

	// 功能：在parent下增加结点child
	public static void addElement(Element parent, Element child) {

		parent.appendChild(child);
	}

	// 功能：在parent下增加字符串tagName生成的结点
	public static void addElement(Element parent, String tagName) {

		Document doc = parent.getOwnerDocument();
		Element child = doc.createElement(tagName);
		parent.appendChild(child);
	}

	// 功能：在parent下增加tagName的Text结点，且值为text
	public static void addElement(Element parent, String tagName, String text) {

		Document doc = parent.getOwnerDocument();
		Element child = doc.createElement(tagName);
		setElementValue(child, text);
		parent.appendChild(child);
	}

	// 功能：将父结点parent下的名称为tagName的结点移除
	public static void removeElement(Element parent, String tagName) {

		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node nd = nl.item(i);
			if (nd.getNodeName().equals(tagName)) {
				parent.removeChild(nd);
			}
		}
	}

	public static void main(String[] args) {

		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;
		NodeList table_list = null;
		NodeList column_list = null;
		String table_name = "";
		String column_name = "";
		List<String> clist = new ArrayList<String>();

		try {
			File f = new File("C:/数据字典_S01_V1.00.xml");
			// InputStream is =
			// getClass().getClassLoader().getResourceAsStream("message_"+i18+".properties");
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(f);
			table_list = doc.getElementsByTagName("table");

			for (int i = 0; i < table_list.getLength(); i++) {

				table_name = table_list.item(i).getAttributes().getNamedItem("name").getNodeValue();
				logger.info("[info]table_name的值:	" + table_name);

				column_list = table_list.item(i).getChildNodes();

				for (int j = 0; j < column_list.getLength(); j++) {

					column_name = column_list.item(j).getAttributes().getNamedItem("name").getNodeValue();
					logger.info("[info]column_name的值:	" + column_name);
					clist.add(column_name);

				}

			}

			logger.info("Done");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}