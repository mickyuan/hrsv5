package hrds.commons.hadoop.hbaseindexer.type;

import hrds.commons.hadoop.hbaseindexer.bean.HbaseSolrField;

/**
 * hbase列名 与 solr 列名 映射规则
 *
 * @author Mick
 *
 */
public interface FieldNameMapper {

	/*
	 * 根据HbaseSolrField对象，生成solr列名，并set到该对象中
	 */
	void map(HbaseSolrField hsf);
}
