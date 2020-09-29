package hrds.g.biz.init;

import hrds.commons.hadoop.solr.ISolrOperator;
import hrds.commons.hadoop.solr.SolrFactory;
import hrds.commons.hadoop.solr.SolrParam;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>标    题: 海云数服 V3.5</p>
 * <p>描    述: Interface Services Create SolrConnection Frequent Connections</p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>创建时间: 2019年5月10日11:15:34</p>
 *
 * @author XXX
 * @version 1.0
 * @since JDK 1.8
 */
public class InitSolrOnHbaseConnection {

	private static final Log logger = LogFactory.getLog(InitSolrOnHbaseConnection.class);

	private static ISolrOperator os;

	/**
	 * 获取solr连接
	 */
	static {

		//初始化solr连接
		SolrParam param = new SolrParam();
		String collection = PropertyParaValue.getString("solrOnHbase", "HrdsHbaseOverSolr");
		String solrZkUrl = PropertyParaValue.getString("zkHost", "cdh063:2181,cdh064:2181,cdh065:2181/solr");
		param.setCollection(collection);
		param.setSolrZkUrl(solrZkUrl);
		os = SolrFactory.getInstance(param);
		logger.info("solr service " + collection + " start");
	}

	/**
	 * Get OperSolr Connection Instance
	 *
	 * @return
	 */
	public static ISolrOperator getOperSolr() {

		return os;

	}

}
