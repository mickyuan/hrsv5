package hrds.commons.hadoop.solr.impl;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.readconfig.LoginUtil;
import hrds.commons.hadoop.solr.ISolrOperator;
import hrds.commons.hadoop.solr.SolrLogin;
import hrds.commons.hadoop.solr.SolrParam;
import hrds.commons.hadoop.solr.utils.QESXMLResponseParser;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.Constant;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "solr操作实现类(5.3.1)", author = "BY-HLL", createdate = "2020/1/9 0009 下午 03:14")
public class SolrOperatorImpl5_3_1 extends SolrOperatorImpl implements ISolrOperator {

	private CloudSolrClient server$;

	/* 创建并设置solr服务的实现实例 */
	public SolrOperatorImpl5_3_1() {
		connectSolr();
		setServer(server$);
	}

	/* 创建并设置solr服务的实现实例(远程) */
	public SolrOperatorImpl5_3_1(SolrParam solrParam) {
		connectSolr(solrParam, null);
		setServer(server$);
	}

	/* 创建并设置solr服务的实现实例(远程)，执行认证文件目录 */
	public SolrOperatorImpl5_3_1(SolrParam solrParam, String configPath) {
		connectSolr(solrParam, configPath);
		setServer(server$);
	}

	private void connectSolr() {
		//1.solr服务验证
		setSecConfig();
		try {
			server$ = new CloudSolrClient(CommonVariables.ZK_HOST);
			server$.setDefaultCollection(CommonVariables.SOLR_COLLECTION);
			server$.setZkClientTimeout(60000);
			server$.setZkConnectTimeout(60000);
			server$.connect();
		} catch (Exception e) {
			logger.info("Solr server is connected successfully!");
			logger.error(e.getMessage(), e);
		}
	}

	private void connectSolr(SolrParam solrParam, String configPath) {
		//1.solr服务验证
		if (configPath != null) {
			setSecConfig(configPath);
		} else {
			setSecConfig();
		}
		try {
			String collection;
			if (solrParam != null && !StringUtil.isEmpty(solrParam.getCollection())) {
				collection = solrParam.getCollection();
			} else {
				throw new AppSystemException("远程连接为空！！！");
			}
			logger.info("zookeeper address:" + solrParam.getSolrUrl());
			logger.info("collection's name:" + collection);
			server$ = new CloudSolrClient(solrParam.getSolrUrl());
			server$.setDefaultCollection(collection);
			server$.setZkClientTimeout(60000);
			server$.setZkConnectTimeout(60000);
			server$.connect();
			logger.info("Solr server is connected successfully!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Method(desc = "设置服务认证", logicStep = "设置服务认证")
	private void setSecConfig() {
		String path = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
		path = path.replace("\\", "\\\\");
		SolrLogin.setJaasFile(path + "user.keytab");
		SolrLogin.setKrb5Config(path + "krb5.conf");
		SolrLogin.setZookeeperServerPrincipal(LoginUtil.ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
	}

	@Method(desc = "设置服务认证", logicStep = "设置服务认证")
	private void setSecConfig(String path) {
		if (new File(path + "user.keytab").exists()) {
			SolrLogin.setJaasFile(path + "user.keytab");
		}
		if (new File(path + "krb5.conf").exists()) {
			SolrLogin.setKrb5Config(path + "krb5.conf");
		}
		SolrLogin.setZookeeperServerPrincipal(LoginUtil.ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
	}

	/**
	 * 针对请求solr的自定义handler，并且不需要返回值的情况
	 *
	 * @param temp 可以为多个或空，默认："/reloadDictionary"
	 * @return 返回运行情况，key为输入参数temp，value为执行状态或者错误信息
	 */
	@Override
	public List<Map<String, Object>> requestHandler(String... temp) {

		temp = temp.length < 1 ? new String[]{Constant.HANDLER} : temp;

		List<Map<String, Object>> mapArrayList = new ArrayList<>();

		SolrQuery sq = new SolrQuery();
		QueryResponse response;

		((CloudSolrClient) server).setParser(new QESXMLResponseParser());

		for (String handler : temp) {
			try {
				Map<String, Object> map = new HashMap<>();
				sq.setRequestHandler(handler);
				response = server.query(sq);
				map.put(handler, response.getStatus());
				mapArrayList.add(map);
				logger.info("[INFO] Spend time on request to custom handler    " + handler + " : " + response.getQTime() + " ms");
			} catch (SolrServerException | IOException e) {
				logger.error(e);
			}
		}
		return mapArrayList;
	}

	@Method(desc = "关闭solr服务", logicStep = "关闭solr服务")
	@Override
	public void close() {
		try {
			server.close();
			logger.info("solr server is disconnected successfully!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
