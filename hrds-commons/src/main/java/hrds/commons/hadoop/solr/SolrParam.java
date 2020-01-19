package hrds.commons.hadoop.solr;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "solr库的参数配置类", author = "博彦科技", createdate = "2020/1/9 0009 上午 11:26")
public class SolrParam {

    //solr服务的url
    private String solrUrl;
    //使用需要的Connection
    private String collection;

    /* 获取solr服务的url */
    public String getSolrUrl() {
        return solrUrl;
    }

    /* 设置solr服务的url */
    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl;
    }

    /* 获取Connection */
    public String getCollection() {
        return collection;
    }

    /* 设置Connection */
    public void setCollection(String collection) {
        this.collection = collection;
    }
}
