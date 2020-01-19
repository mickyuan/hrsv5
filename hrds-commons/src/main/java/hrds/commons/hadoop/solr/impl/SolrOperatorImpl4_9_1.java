package hrds.commons.hadoop.solr.impl;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.solr.ISolrOperator;
import hrds.commons.hadoop.solr.SolrParam;
import hrds.commons.hadoop.solr.utils.QESXMLResponseParser;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PropertyParaUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "solr操作实现类(4.9.1)", author = "BY-HLL", createdate = "2020/1/19 0019 上午 10:26")
@SuppressWarnings("deprecation")
public class SolrOperatorImpl4_9_1 extends SolrOperatorImpl implements ISolrOperator {

    private HttpSolrServer server$;

    /* 创建并设置solr服务的实现实例 */
    public SolrOperatorImpl4_9_1() {
        connectSolr();
        setServer(server$);
    }

    /* 创建并设置solr服务的实现实例(远程) */
    public SolrOperatorImpl4_9_1(SolrParam solrParam) {
        connectSolr(solrParam);
        setServer(server$);
    }

    @Method(desc = "连接默认配置的solr服务", logicStep = "连接默认配置的solr服务")
    private void connectSolr() {
        try {
            server$ = new HttpSolrServer(CommonVariables.SOLR_URL + CommonVariables.SOLR_COLLECTION);
            logger.info("default solr server is connected successfully!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessException("default solr server is connected failed!!!");
        }
    }

    @Method(desc = "连接自定义配置的solr服务(远程)", logicStep = "连接自定义配置的solr服务(远程)")
    @Param(name = "solrParam", desc = "solr连接配置", range = "solrParam")
    private void connectSolr(SolrParam solrParam) {
        try {
            //获取连接的collection
            String collection;
            if (solrParam != null && !StringUtil.isEmpty(solrParam.getCollection())) {
                collection = solrParam.getCollection();
            } else {
                throw new BusinessException("collection 连接为空!!!");
            }
            //设置连接的url
            String url = PropertyParaUtil.getString("solrUrl", "http://10.0.168.103:18983/solr/");
            url = url + collection;
            //创建并设置连接
            server$ = new HttpSolrServer(url);
            logger.info("remotely solr server is connected successfully!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessException("remotely solr server is connected failed!!!");
        }
    }

    @Method(desc = "获取自定义的handler,(不带返回值)", logicStep = "获取自定义的handler,(不带返回值)")
    @Param(name = "handler", desc = "自定义handler", range = "String类型")
    @Return(desc = "handlerList", range = "返回值取值范围")
    @Override
    public List<Map<String, Object>> requestHandler(String... handler) {
        //设置handler
        handler = handler.length < 1 ? new String[]{Constant.HANDLER} : handler;
        List<Map<String, Object>> handlerList = new ArrayList<>();
        Map<String, Object> handlerMap = new HashMap<>();
        SolrQuery sq = new SolrQuery();
        QueryResponse response = new QueryResponse();
        ((HttpSolrServer) server).setParser(new QESXMLResponseParser());
        for (String h : handler) {
            sq.setRequestHandler(h);
            try {
                response = server.query(sq);
            } catch (SolrServerException | IOException e) {
                handlerMap.put(h, e);
                e.printStackTrace();
            }
            if (response != null) {
                handlerMap.put(h, response.getStatus());
                handlerList.add(handlerMap);
                logger.info("spend time on request to custom handler" + h + " : " + response.getQTime() + " ms");
            }
        }
        return handlerList;
    }

    @Method(desc = "关闭solr服务", logicStep = "关闭solr服务")
    @Override
    public void close() {

        try {
            logger.info("[info]solr server is disconnected successfully!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
