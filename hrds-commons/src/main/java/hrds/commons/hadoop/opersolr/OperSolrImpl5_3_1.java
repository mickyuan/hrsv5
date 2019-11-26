package hrds.commons.hadoop.opersolr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.readconfig.LoginUtil;
import hrds.commons.utils.PropertyParaValue;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @author: jack zhou
 * @Date: 20160225 9:00:00
 * @version: 1.0
 * @Context: Solrj 5.3.1
 */

public class OperSolrImpl5_3_1 extends OperSolrImpl implements OperSolr {

	private static final String ZKHOST = PropertyParaValue.getString("zkHost", "");

	private CloudSolrClient server$ = null;

	public OperSolrImpl5_3_1() {

		connectSolr();
		setServer(server$);
	}

	public OperSolrImpl5_3_1(SolrParam solrParam) {

		connectSolr(solrParam);
		setServer(server$);
	}

	@Override
	public void close() {

		try {
			server.close();
			logger.info("[info]solr server is disconnected successfully!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void connectSolr() {

		setSecConfig();//FI平台验证
		try {
			String collection = PropertyParaValue.getString("collection", "");
			logger.info("zookeeper address:" + ZKHOST);
			logger.info("colletion's name:" + collection);
			server$ = new CloudSolrClient(ZKHOST);
			server$.setDefaultCollection(collection);
			server$.setZkClientTimeout(60000);
			server$.setZkConnectTimeout(60000);
			server$.connect();
			logger.info("Solr server is connected successfully!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}


	private void connectSolr(SolrParam solrParam) {

		setSecConfig();//FI平台验证
		try {
			String collection = "";
			if (solrParam != null && !StringUtil.isEmpty(solrParam.getCollection())) {
				collection = solrParam.getCollection();
			} else {
				throw new BusinessException("远程连接为空！！！");
			}
			logger.info("zookeeper address:" + ZKHOST);
			logger.info("colletion's name:" + collection);
			server$ = new CloudSolrClient(ZKHOST);
			server$.setDefaultCollection(collection);
			server$.setZkClientTimeout(60000);
			server$.setZkConnectTimeout(60000);
			server$.connect();
			logger.info("Solr server is connected successfully!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void setSecConfig() {

		String path = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
		path = path.replace("\\", "\\\\");
//		System.out.println(path);
		try {
			SolrLogin.setJaasFile(path + "user.keytab");
			SolrLogin.setKrb5Config(path + "krb5.conf");
			SolrLogin.setZookeeperServerPrincipal(LoginUtil.ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 针对请求solr的自定义handler，并且不需要返回值的情况
	 *
	 * @param temp 可以为多个或空，默认："/reloadDictionary"
	 * @return 返回运行情况，key为输入参数temp，value为执行状态或者错误信息
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@Override
	public JSONArray requestHandler(String... temp) {

		temp = temp.length < 1 ? temp = new String[]{handler} : temp;

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		SolrQuery sq = new SolrQuery();
		QueryResponse response = null;

		((CloudSolrClient) server).setParser(new QESXMLResponseParser());

		for (String handler : temp) {

			sq.setRequestHandler(handler);

			try {
				response = server.query(sq);
			} catch (SolrServerException | IOException e) {

				jsonObject.put(handler, e);

				e.printStackTrace();
			}

			jsonObject.put(handler, response.getStatus());
			jsonArray.add(jsonObject);

			logger.info("[INFO] Spend time on request to custom handler    " + handler + " : " + response.getQTime() + " ms");
		}

		return jsonArray;
	}

	public static void main(String[] args) {
		try {
			System.setProperty("HADOOP_USER_NAME", "hyrenserv");
			OperSolr os = SolrFactory.getInstance();
			SolrQuery query = new SolrQuery();
			query.setQuery("*:*");
			SolrClient server2 = os.getServer();
			server2.query(query);

			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
