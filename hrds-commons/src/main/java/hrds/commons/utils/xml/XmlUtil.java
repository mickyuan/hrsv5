package hrds.commons.utils.xml;

import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * XML工具类<br> 此工具使用w3c dom工具，不需要依赖第三方包。<br> 工具类封装了XML文档的创建、读取、写出和部分XML操作
 *
 * @author xiaoleilu
 */
public class XmlUtil {

	/**
	 * 在XML中无效的字符 正则
	 */
	public static final String INVALID_REGEX = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";

	/**
	 * 默认的DocumentBuilderFactory实现
	 */
	private static String defaultDocumentBuilderFactory = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";

	/**
	 * 是否打开命名空间支持
	 */
	private static boolean namespaceAware = true;

	/**
	 * 是否打开命名空间支持
	 */
	public final static String UTF_8 = "UTF-8";

	/**
	 * XML格式化输出默认缩进量
	 */
	public static final int INDENT_DEFAULT = 2;

	/**
	 * 读取解析XML文件<br> 如果给定内容以“&lt;”开头，表示这是一个XML内容，直接读取，否则按照路径处理<br> 路径可以为相对路径，也可以是绝对路径，相对路径相对于ClassPath
	 *
	 * @param pathOrContent 内容或路径
	 * @return XML文档对象
	 * @since 3.0.9
	 */
	public static Document readXML(String pathOrContent) {
		if (pathOrContent.startsWith("<")) {
			return parseXml(pathOrContent);
		}
		return readXML(new File(pathOrContent));
	}


	/**
	 * 将String类型的XML转换为XML文档
	 *
	 * @param xmlStr XML字符串
	 * @return XML文档
	 */
	public static Document parseXml(String xmlStr) {
		if (StringUtil.isBlank(xmlStr)) {
			throw new IllegalArgumentException("XML content string is empty !");
		}
		xmlStr = cleanInvalid(xmlStr);
		return readXML(new File(xmlStr));
	}

	/**
	 * 读取解析XML文件
	 *
	 * @param file XML文件
	 * @return XML文档对象
	 */
	public static Document readXML(File file) {
		if (false == file.exists()) {
			throw new AppSystemException("File [{" + file.getAbsolutePath() + "}] not a exist!");
		}
		if (false == file.isFile()) {
			throw new AppSystemException("[{" + file.getAbsolutePath() + "}] not a file!");
		}

		try {
			file = file.getCanonicalFile();
		} catch (IOException e) {
			// ignore
		}

		BufferedInputStream in = null;
		try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
			return readXML(buffer);
		} catch (IOException e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 读取解析XML文件<br> 编码在XML中定义
	 *
	 * @param inputStream XML流
	 * @return XML文档对象
	 * @throws AppSystemException IO异常或转换异常
	 * @since 3.0.9
	 */
	public static Document readXML(InputStream inputStream) throws AppSystemException {
		return readXML(new InputSource(inputStream));
	}

	/**
	 * 读取解析XML文件<br> 编码在XML中定义
	 *
	 * @param source {@link InputSource}
	 * @return XML文档对象
	 * @since 3.0.9
	 */
	public static Document readXML(InputSource source) {
		final DocumentBuilder builder = createDocumentBuilder();
		try {
			return builder.parse(source);
		} catch (Exception e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 创建 DocumentBuilder
	 *
	 * @return DocumentBuilder
	 * @since 4.1.2
	 */
	public static DocumentBuilder createDocumentBuilder() {
		DocumentBuilder builder;
		try {
			builder = createDocumentBuilderFactory().newDocumentBuilder();
		} catch (Exception e) {
			throw new AppSystemException(e);
		}
		return builder;
	}

	/**
	 * 创建{@link DocumentBuilderFactory}
	 * <p>
	 * 默认使用"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"<br> 如果使用第三方实现，请调用{@link
	 * #disableDefaultDocumentBuilderFactory()}
	 * </p>
	 *
	 * @return {@link DocumentBuilderFactory}
	 */
	public static DocumentBuilderFactory createDocumentBuilderFactory() {
		final DocumentBuilderFactory factory;
		if (StringUtil.isNotEmpty(defaultDocumentBuilderFactory)) {
			factory = DocumentBuilderFactory.newInstance(defaultDocumentBuilderFactory, null);
		} else {
			factory = DocumentBuilderFactory.newInstance();
		}
		// 默认打开NamespaceAware，getElementsByTagNameNS可以使用命名空间
		factory.setNamespaceAware(namespaceAware);
		return disableXXE(factory);
	}

	/**
	 * 关闭XXE，避免漏洞攻击<br> see: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#JAXP_DocumentBuilderFactory.2C_SAXParserFactory_and_DOM4J
	 *
	 * @param dbf DocumentBuilderFactory
	 * @return DocumentBuilderFactory
	 */
	private static DocumentBuilderFactory disableXXE(DocumentBuilderFactory dbf) {
		String feature;
		try {
			// This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
			// Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
			feature = "http://apache.org/xml/features/disallow-doctype-decl";
			dbf.setFeature(feature, true);
			// If you can't completely disable DTDs, then at least do the following:
			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
			// JDK7+ - http://xml.org/sax/features/external-general-entities
			feature = "http://xml.org/sax/features/external-general-entities";
			dbf.setFeature(feature, false);
			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
			// JDK7+ - http://xml.org/sax/features/external-parameter-entities
			feature = "http://xml.org/sax/features/external-parameter-entities";
			dbf.setFeature(feature, false);
			// Disable external DTDs as well
			feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
			dbf.setFeature(feature, false);
			// and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);
		} catch (ParserConfigurationException e) {
			// ignore
		}
		return dbf;
	}

	/**
	 * 去除XML文本中的无效字符
	 *
	 * @param xmlContent XML文本
	 * @return 当传入为null时返回null
	 */
	public static String cleanInvalid(String xmlContent) {
		if (xmlContent == null) {
			return null;
		}
		return xmlContent.replaceAll(INVALID_REGEX, "");
	}


	public static void main(String[] args) throws IOException {
		Document doc = XmlUtil.readXML("test.xml");
		final String s = XmlUtil.toStr(doc);

		NodeList node = doc.getElementsByTagName("Property");
		System.out.println(node.getLength());
		for (int i = 0; i < node.getLength(); i++) {
			Node item = node.item(i);
			NodeList childNodes = item.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childItem = childNodes.item(j);
				childItem.setNodeValue("/a33333" + j);
			}
		}
		System.out.println("=======================================================");
		XmlUtil.toFile(doc, "D:/a2222aa.xml", UTF_8);
	}

	/**
	 * 将XML文档转换为String<br> 字符编码使用XML文档中的编码，获取不到则使用UTF-8<br> 默认非格式化输出，若想格式化请使用{@link #format(Document)}
	 *
	 * @param doc XML文档
	 * @return XML字符串
	 */
	public static String toStr(Document doc) {
		return toStr(doc, UTF_8, false);
	}

	/**
	 * 将XML文档转换为String<br> 字符编码使用XML文档中的编码，获取不到则使用UTF-8
	 *
	 * @param doc      XML文档
	 * @param charset  编码
	 * @param isPretty 是否格式化输出
	 * @return XML字符串
	 * @since 3.0.9
	 */
	public static String toStr(Document doc, String charset, boolean isPretty) {
		return toStr(doc, charset, isPretty, false);
	}

	/**
	 * 将XML文档转换为String<br> 字符编码使用XML文档中的编码，获取不到则使用UTF-8
	 *
	 * @param doc                XML文档
	 * @param charset            编码
	 * @param isPretty           是否格式化输出
	 * @param omitXmlDeclaration 是否输出 xml Declaration
	 * @return XML字符串
	 * @since 5.1.2
	 */
	public static String toStr(Document doc, String charset, boolean isPretty, boolean omitXmlDeclaration) {
		StringWriter writer = new StringWriter();
		try {
			write(doc, writer, charset, isPretty ? INDENT_DEFAULT : 0, omitXmlDeclaration);
		} catch (Exception e) {
			throw new AppSystemException(e);
		}
		return writer.toString();
	}

	/**
	 * 将XML文档写出
	 *
	 * @param node               {@link Node} XML文档节点或文档本身
	 * @param writer             写出的Writer，Writer决定了输出XML的编码
	 * @param charset            编码
	 * @param indent             格式化输出中缩进量，小于1表示不格式化输出
	 * @param omitXmlDeclaration 是否输出 xml Declaration
	 * @since 5.1.2
	 */
	public static void write(Node node, Writer writer, String charset, int indent, boolean omitXmlDeclaration) {
		transform(new DOMSource(node), new StreamResult(writer), charset, indent, omitXmlDeclaration);
	}

	/**
	 * 将XML文档写出<br> 格式化输出逻辑参考：https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
	 *
	 * @param source             源
	 * @param result             目标
	 * @param charset            编码
	 * @param indent             格式化输出中缩进量，小于1表示不格式化输出
	 * @param omitXmlDeclaration 是否输出 xml Declaration
	 * @since 5.1.2
	 */
	public static void transform(Source source, Result result, String charset, int indent, boolean omitXmlDeclaration) {
		final TransformerFactory factory = TransformerFactory.newInstance();
		try {
			final Transformer xformer = factory.newTransformer();
			if (indent > 0) {
				xformer.setOutputProperty(OutputKeys.INDENT, "yes");
				xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
			}
			if (StringUtil.isNotBlank(charset)) {
				xformer.setOutputProperty(OutputKeys.ENCODING, charset);
			}
			if (omitXmlDeclaration) {
				xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			}
			xformer.transform(source, result);
		} catch (Exception e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 将XML文档写入到文件<br>
	 *
	 * @param doc     XML文档
	 * @param path    文件路径绝对路径或相对ClassPath路径，不存在会自动创建
	 * @param charset 自定义XML文件的编码，如果为{@code null} 读取XML文档中的编码，否则默认UTF-8
	 */
	public static void toFile(Document doc, String path, String charset) {
		if (StringUtil.isBlank(charset)) {
			charset = doc.getXmlEncoding();
		}
		if (StringUtil.isBlank(charset)) {
			charset = UTF_8;
		}

		try (BufferedWriter writer = FileUtil.newBufferedWriter(Paths.get(path), Charset.defaultCharset().newEncoder());) {
			write(doc, writer, charset, INDENT_DEFAULT, true);
		}catch (IOException e) {
			throw new AppSystemException(e);
		}
	}
}
