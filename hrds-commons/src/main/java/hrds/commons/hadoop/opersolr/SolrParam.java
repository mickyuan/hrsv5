package hrds.commons.hadoop.opersolr;
/**
 *连接远程solr库使用的url需要的参数
 */
public class SolrParam {
	
	private String solrUrl ;	//使用CHD需要的url
	
	private String collection;  //使用需要的colletion's name
	
	public String getSolrUrl() {
		return solrUrl;
	}
	
	/**
	 * 
	 * @param solrUrl 使用CHD\FI需要的url
	 */
	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}
	
	/**
	 * 
	 * @return 使用需要的colletion's name
	 */
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	
}
