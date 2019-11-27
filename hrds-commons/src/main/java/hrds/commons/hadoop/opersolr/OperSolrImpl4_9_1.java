package hrds.commons.hadoop.opersolr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;

/**
 * @Description:
 * @author: jack zhou
 * @Date: 20160225 9:00:00
 * @version: 1.0
 * @Context: Solrj 4.9.1
 */
@SuppressWarnings("deprecation")
public class OperSolrImpl4_9_1 extends OperSolrImpl implements OperSolr {

	private HttpSolrServer server$ = null;

	public OperSolrImpl4_9_1() {

		connectSolr();
		setServer(server$);
	}

	public OperSolrImpl4_9_1(SolrParam solrParam) {

		connectSolr(solrParam);
		setServer(server$);
	}

	private void connectSolr() {

		try {
			String url = PropertyParaUtil.getString("solrUrl", "http://10.0.168.103:18983/solr/");
			url = url + PropertyParaUtil.getString("collection", "fullTextIndexing");
			server$ = new HttpSolrServer(url);
			logger.info("[info]solr server is connected successfully!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void connectSolr(SolrParam solrParam) {

		try {
			String collection;
			if (solrParam != null && !StringUtil.isEmpty(solrParam.getCollection())) {
				collection = solrParam.getCollection();
			} else {
				throw new BusinessException("collection 连接为空！！！");
			}
			String url = PropertyParaUtil.getString("solrUrl", "http://10.0.168.103:18983/solr/");
			url = url + collection;
			server$ = new HttpSolrServer(url);
			logger.info("[info]solr server is connected successfully!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 针对请求solr的自定义handler，并且不需要返回值的情况
	 *
	 * @param temp 可以为多个或空，默认："/reloadDictionary"
	 * @return 返回运行情况，key为输入参数temp，value为执行状态或者错误信息
	 */
	@Override
	public JSONArray requestHandler(String... temp) {

		temp = temp.length < 1 ? new String[]{handler} : temp;

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		SolrQuery sq = new SolrQuery();
		QueryResponse response = null;

		((HttpSolrServer) server).setParser(new QESXMLResponseParser());

		for (String handler : temp) {

			sq.setRequestHandler(handler);

			try {
				response = server.query(sq);
			} catch (SolrServerException | IOException e) {

				jsonObject.put(handler, e);

				e.printStackTrace();
			}
			if (response != null) {
				jsonObject.put(handler, response.getStatus());
				jsonArray.add(jsonObject);
				logger.info("[INFO] Spend time on request to custom handler    " + handler + " : " + response.getQTime() + " ms");
			}
		}

		return jsonArray;
	}

	@Override
	public void close() {

		try {
			logger.info("[info]solr server is disconnected successfully!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {

	}

}
