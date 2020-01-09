package hrds.commons.hadoop.opersolr;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import fd.ng.core.annotation.DocClass;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrDocumentList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@DocClass(desc = "solr服务接口类", author = "博彦科技", createdate = "2020/1/9 0009 上午 11:34")
public interface OperSolr extends Closeable {

	public abstract SolrClient getServer();

	public abstract List<String> getAnalysis(String sentence);

	public abstract List<JSONObject> QuerySolr(String sql, int start, int num);

	/**
	 * @param params 检索内容
	 * @param start
	 * @param num
	 * @param flag   是否返回文本内容true 不返回 ;FALSE 返回
	 * @return
	 * @throws Exception
	 */
	public abstract List<JSONObject> QuerySolrPlus(Map<String, String> params, int start, int num, boolean flag) throws Exception;

	public abstract void indexCreateAll(String table_name, HBaseHelper helper);

	public abstract void increasingIndexing(String table_name, String row_key, HBaseHelper helper);

	public abstract void increasingIndexing(String table_name, List<String> row_keys, HBaseHelper helper);

	public abstract void deleteIndexById(String row_key);

	public abstract void deleteIndexByQuery(String query);

	public abstract void deleteIndexAll();

	public abstract void indexCreateAll4csv(String table_name, String hdfspath, HdfsOperator operater, String columnline);

	public abstract void indexCreateAll4Parquet(String table_name, String hdfspath, HdfsOperator operater, String columnline);

	public abstract void indexCreateAll4Orc(String table_name, String hdfspath, HdfsOperator operater, String columnline);

	public abstract void indexCreateAll4Sequence(String table_name, String hdfspath, HdfsOperator operater, String columnline);

	public abstract JSONArray requestHandler(String... temp);

	public abstract JSONArray requestHandler(boolean is, JSONObject parameters, String... temp);

	public abstract SolrDocumentList queryByField(String fieldName, String value, String responseColumns, int rows);
}
