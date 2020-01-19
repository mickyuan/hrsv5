package hrds.commons.hadoop.solr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrDocumentList;
import org.stringtemplate.v4.ST;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

@DocClass(desc = "solr操作接口类", author = "BY-HLL", createdate = "2020/1/9 0009 上午 11:34")
public interface ISolrOperator extends Closeable {

    /* 获取solrServer */
    SolrClient getServer();

    /* 获取语句解析字段 */
    List<String> getAnalysis(String sentence);

    /* 获取solr检索结果,全文检索 */
    List<Map<String, Object>> querySolr(String searchCondition, int start, int num);

    /* 获取solr检索结果,全文检索 flag:是否返回文本内容,true:返回;false:不返回 */
    List<Map<String, Object>> querySolrPlus(Map<String, String> params, int start, int num, boolean flag);

    /* 根据表索引全部字段 */
    void indexCreateAll(String table_name, HBaseHelper helper);

    /* 创建单个索引 */
    void increasingIndexing(String table_name, String row_key, HBaseHelper helper);

    /* 批量创建索引 */
    void increasingIndexing(String table_name, List<String> row_keys, HBaseHelper helper);

    /* 根据row_key删除索引 */
    void deleteIndexById(String row_key);

    /* 根据查询语句删除索引 */
    void deleteIndexByQuery(String query);

    /* 删除所有数据 */
    void deleteIndexAll();

    /* HDFS文件创建索引(csv) */
    void indexCreateAll4csv(String table_name, String HDFSPath, HdfsOperator operator, String columnLine);

    /* HDFS文件创建索引(parquet) */
    void indexCreateAll4Parquet(String table_name, String HDFSPath, HdfsOperator operator, String columnLine);

    /* HDFS文件创建索引(orc) */
    void indexCreateAll4Orc(String table_name, String HDFSPath, HdfsOperator operator, String columnLine);

    /* HDFS文件创建索引(sequence) */
    void indexCreateAll4Sequence(String table_name, String HDFSPath, HdfsOperator operator, String columnLine);

    /* 获取自定义的handler,不需要返回值*/
    List<Map<String, Object>> requestHandler(String... handler);

    /* 获取自定义的handler，需要返回值的情况 is:是否需要返回值 */
    List<Map<String, Object>> requestHandler(boolean is, Map<String, Object> parameters, String... temp);

    /* 根据fieldName和值自定义查询,filedName:查询字段名,value:查询字段结果,responseColumns:返回结果字段,rows返回结果行数*/
    SolrDocumentList queryByField(String fieldName, String value, String responseColumns, int rows);
}
