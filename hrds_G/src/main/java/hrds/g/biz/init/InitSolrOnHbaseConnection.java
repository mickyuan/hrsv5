package hrds.g.biz.init;

import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.solr.ISolrOperator;
import hrds.commons.hadoop.solr.SolrFactory;
import hrds.commons.hadoop.solr.SolrParam;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

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
	private static HBaseHelper helper = null;

	/**
	 * 获取solr连接
	 */
	static {

		//初始化solr连接
		SolrParam param = new SolrParam();
		String collection = PropertyParaValue.getString("solrOnHbase", "HrdsHbaseOverSolr");
		param.setCollection(collection);
		os = SolrFactory.getInstance(param);
		logger.info("solr service " + collection + " start");
		try {
			helper = HBaseHelper.getHelper();
		} catch (IOException e) {
			logger.error("获取 HBaseHelper 失败:" + e);
		}

	}

	/**
	 * Get OperSolr Connection Instance
	 *
	 * @return
	 */
	public static ISolrOperator getOperSolr() {

		return os;

	}

	/**
	 * Get HBase Connection Instance
	 *
	 * @return
	 */
	public static HBaseHelper getHelp() {

		return helper;
	}
}
