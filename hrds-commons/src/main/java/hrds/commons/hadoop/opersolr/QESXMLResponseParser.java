package hrds.commons.hadoop.opersolr;

import javax.xml.stream.XMLInputFactory;

import org.apache.solr.client.solrj.impl.XMLResponseParser;

/**
 * 重构solr的请求返回类型
 * @author Wang
 *
 */
class QESXMLResponseParser extends XMLResponseParser {
	static final XMLInputFactory factory = XMLInputFactory.newInstance();
	
    public QESXMLResponseParser() {
        super();
    }

    @Override
    public String getContentType() {
        return "application/xml; charset=UTF-8";
    }
    
}
