package hrds.commons.hadoop.opersolr;

import org.apache.solr.common.SolrInputDocument;

public class HSolrInputDocument {

	SolrInputDocument sd = null;

	public HSolrInputDocument() {

		this.sd = new SolrInputDocument();
	}
	
	public SolrInputDocument getSd() {
	
		return sd;
	}
	
	public void setSd(SolrInputDocument sd) {
	
		this.sd = sd;
	}

	public void addField(String name, Object value) {

		sd.addField(name, value);
	}

	public Object removeField(String name) {

		return sd.removeField(name);
	}


	public boolean isEmpty() {

		return sd.isEmpty();
	}
	
	public int size() {
		return sd.size();
	}

}
