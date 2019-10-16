package hrds.commons.utils.xlstoxml;

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

public class XmlCreater {

	private Document doc = null;//新创建的DOM
	private String path = null;//生成的XML文件绝对路径
	private static final Log logger = LogFactory.getLog(XmlCreater.class);

	public XmlCreater(String path) {

		this.path = path;
		init();
	}

	private void init() {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();//新建DOM
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	//功能：创建根结点，并返回   
	public Element createRootElement(String rootTagName) {

		if (doc.getDocumentElement() == null) {
			Element root = doc.createElement(rootTagName);
			doc.appendChild(root);
			return root;
		}
		return doc.getDocumentElement();
	}

	//功能：创建根结点，并返回
	public void open() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(path);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	//功能：在parent结点下增加子结点tagName
	public Element createElement(Element parent, String tagName) {

		Document doc = parent.getOwnerDocument();
		Element child = doc.createElement(tagName);
		parent.appendChild(child);
		return child;
	}

	//功能：在parent结点下增加子结点tagName
	public Element getElement() {

		Element root = doc.getDocumentElement();
		return root;
	}

	//功能：在parent结点下增加值为value的子结点tabName
	public Element createElement(Element parent, String tagName, String value) {

		Document doc = parent.getOwnerDocument();
		Element child = doc.createElement(tagName);
		XmlOper.setElementValue(child, value);
		parent.appendChild(child);
		return child;
	}

	//功能：在parent结点下增加值为value的子结点tabName
	public void removeElement(String taName) {
		Element root = getElement();
		NodeList table_list = doc.getElementsByTagName("table");
		for (int i = 0; i < table_list.getLength(); i++) {
			Node item = table_list.item(i);
			String name = item.getAttributes().getNamedItem("name").getNodeValue().toLowerCase();
			if (taName.equalsIgnoreCase(name)) {
				root.removeChild(item);
			}
		}
	}

	//功能：在parent结点下增加属性
	public void createAttribute(Element parent, String attrName, String attrValue) {

		XmlOper.setElementAttr(parent, attrName, attrValue);
	}

	//功能：在parent结点下删除属性
	public void removeAttribute(Element parent, String attrName, String attrValue) {

		XmlOper.removeElementAttr(parent, attrName);
	}

	//功能：根据DOM生成XML文件
	public void buildXmlFile() {

		TransformerFactory tfactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = tfactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			transformer.setOutputProperty("encoding", "UTF-8");
			transformer.transform(source, result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Document getDoc() {

		return doc;
	}

	public void setDoc(Document doc) {

		this.doc = doc;
	}

	public String getPath() {

		return path;
	}

	public void setPath(String path) {

		this.path = path;
	}
}
