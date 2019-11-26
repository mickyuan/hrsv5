package hrds.commons.hadoop.opersolr;

import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;

/**
 * Description: 获取solr类实例的工厂方法
 * Company: 博彦科技  
 * @author yuanqi
 * @date 2016年10月26日 下午3:05:34
 */
public class SolrFactory {

	private static final Log log = LogFactory.getLog(SolrFactory.class);
	//获取solr具体实现类全名
	private static final String CLASS_NAME = PropertyParaValue.getString("solrclassname", "opersolr.OperSolrImpl4_9_1");

	/**
	 * 
	 * Description: 获取solr具体实现类实例 
	 * @author yuanqi
	 * @date 2016年10月26日 下午3:02:12
	 * @param ClassName 实现类全名
	 * @return solr具体实现类实例
	 */
	public static OperSolr getInstance(String ClassName) {

		OperSolr solr = null;

		try {
			solr = (OperSolr)Class.forName(ClassName).newInstance();
		}
		catch(Exception e) {
			log.error( "Failed to initialize SolrInterace implementation class...", e);
		}
		return solr;
	}
	
	public static OperSolr getInstance(String ClassName,SolrParam solrParam) {
		OperSolr solr = null;
		
		try {
			Class<?> cl = Class.forName(ClassName);
			Constructor<?> cc = cl.getConstructor(SolrParam.class);
			solr = (OperSolr)cc.newInstance(solrParam);
			
		}
		catch(Exception e) {
			log.error( "Failed to initialize SolrInterace implementation class...", e);
		}
		return solr;
	}

	/**
	 * 
	 * Description: 通过数据库（sys_para）读取实现类全类名来获取类实例
	 * @author yuanqi
	 * @date 2016年10月26日 下午3:04:18
	 * @return solr具体实现类实例
	 */
	public static OperSolr getInstance(SolrParam solrParam) {

		return getInstance(CLASS_NAME,solrParam);
	}
	
	public static OperSolr getInstance() {

		return getInstance(CLASS_NAME);
	}

}
