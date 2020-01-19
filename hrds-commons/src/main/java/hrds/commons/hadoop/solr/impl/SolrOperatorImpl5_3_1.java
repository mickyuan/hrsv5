package hrds.commons.hadoop.solr.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
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
        connectSolr(solrParam);
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


    private void connectSolr(SolrParam solrParam) {
        //1.solr服务验证
        setSecConfig();
        try {
            String collection;
            if (solrParam != null && !StringUtil.isEmpty(solrParam.getCollection())) {
                collection = solrParam.getCollection();
            } else {
                throw new BusinessException("远程连接为空！！！");
            }
            logger.info("zookeeper address:" + CommonVariables.ZK_HOST);
            logger.info("collection's name:" + collection);
            server$ = new CloudSolrClient(CommonVariables.ZK_HOST);
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

    /**
     * 针对请求solr的自定义handler，并且不需要返回值的情况
     *
     * @param temp 可以为多个或空，默认："/reloadDictionary"
     * @return 返回运行情况，key为输入参数temp，value为执行状态或者错误信息
     * @throws IOException
     * @throws SolrServerException
     */
    @Override
    public List<Map<String, Object>> requestHandler(String... temp) {

        temp = temp.length < 1 ? new String[]{Constant.HANDLER} : temp;

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

                e.printStackTrace();
            }

            jsonObject.put(handler, response.getStatus());
            jsonArray.add(jsonObject);

            logger.info("[INFO] Spend time on request to custom handler    " + handler + " : " + response.getQTime() + " ms");
        }
        return null;
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
