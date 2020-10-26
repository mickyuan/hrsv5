package hrds.agent.job.biz.dataclean;

import fd.ng.core.utils.StringUtil;
import hrds.commons.utils.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>标    题: 海云数服务</p>
 * <p>描    述: 数据清洗工厂类</p>
 * <p>版    权: Copyright (c) 2016  </p>
 * <p>公    司: 上海泓智信息科技有限公司</p>
 * <p>创建时间: 2016年9月5日 上午11:42:59</p>
 * <p>@author mashimaro</p>
 * <p>@version 1.0</p>
 * <p>CleanFactory.java</p>
 * <p></p>
 */
public class CleanFactory {

	private static final Map<String, String> mpTypeClass = new HashMap<>();
	private static final CleanFactory CF = new CleanFactory();//静态私有对象

	/**
	 * 构造函数，加载配置参数
	 */
	private CleanFactory() {
		//TODO 这个配置好需要吗？目前好像只有页面定义的清洗方式，而且不能通过sql去实现
		mpTypeClass.put("clean_database", "hrds.agent.job.biz.dataclean.DataClean_Biz");
		mpTypeClass.put("clean_db_file", "hrds.agent.job.biz.dataclean.DataClean_Biz");
//		File file = new File("FactoryConf.xml");
//		try {
//			if (file.exists()) {
//				loadCfgInfo(null, file);
//			} else {
//				InputStream is = XMLUtil.getInputStream("config/FactoryConf.xml");
//				loadCfgInfo(is, null);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 返回静态的工厂类
	 */
	public static CleanFactory getInstance() {

		return CF;
	}

	/**
	 * 获取接口的实现类
	 *
	 * @param type {@link String} 针对每一类使用的id信息
	 * @return {@link DataCleanInterface} 接口实现类
	 */
	public DataCleanInterface getObjectClean(String type) throws Exception {

		try {
			String classZ = mpTypeClass.get(type);
			if (!StringUtil.isEmpty(classZ)) {
				return (DataCleanInterface) Class.forName(classZ).newInstance();
			} else {
				throw new Exception("配置中没有找到：" + type + "的配置信息");
			}
		} catch (Exception e) {
			throw new Exception("获取配置配置类失败");
		}
	}

	/**
	 * 获取配置信息
	 *
	 * @param is   流
	 * @param file 文件
	 */
	private static void loadCfgInfo(InputStream is, File file) throws Exception {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new Exception("创建文档管理器失败", e);
			}
			Document doc;
			if (null != file) {
				doc = db.parse(file);
			} else {
				doc = db.parse(is);
			}
			Element root = (Element) doc.getElementsByTagName("beans").item(0);
			List<?> beanList = XMLUtil.getChildElements(root, "bean");
			for (Object b : beanList) {
				Element bean = (Element) b;
				String typeid = bean.getAttribute("id");
				String InfoClass = bean.getAttribute("class");
				mpTypeClass.put(typeid, InfoClass);
			}
		} catch (Exception ex) {
			throw new Exception("加载FactoryConf的配置信息异常！ ", ex);
		}
	}

}
