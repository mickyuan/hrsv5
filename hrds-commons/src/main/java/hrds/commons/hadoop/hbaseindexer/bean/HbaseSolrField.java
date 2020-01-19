package hrds.commons.hadoop.hbaseindexer.bean;

import hrds.commons.hadoop.hbaseindexer.type.DataTypeTransformSolr;
import hrds.commons.hadoop.hbaseindexer.type.TypeFieldNameMapper;
import hrds.commons.hadoop.hbaseindexer.type.FieldNameMapper;

/**
 * solr字段bean包含 field 信息 注意setType方法，同时做了solr字段，和类型的初始化
 *
 * @author Mick
 *
 */
public class HbaseSolrField {

	private static final FieldNameMapper FIELD_NAME_MAPPER = new TypeFieldNameMapper();

	private String solrColumnName = "";
	private String hbaseColumnName = "";
	/**
	 * default type in solr
	 */
	private String type = "string";

	public String getSolrColumnName() {

		return solrColumnName;
	}

	public void setSolrColumnName(String solrColumnName) {

		this.solrColumnName = solrColumnName;
	}

	public String getHbaseColumnName() {

		return hbaseColumnName;
	}

	public void setHbaseColumnName(String hbaseColumnName) {

		this.hbaseColumnName = hbaseColumnName;
	}

	public String getType() {

		return type;
	}

	/**
	 * 初始化solr类型和solr字段名称
	 * @param type
	 */
	public void setType(String type) {

		this.type = DataTypeTransformSolr.transform(type);
		FIELD_NAME_MAPPER.map(this);
	}

	@Override
	public String toString() {
		return "HbaseSolrField [solrColumnName=" + solrColumnName + ", hbaseColumnName=" + hbaseColumnName + ", type="
				+ type + "]";
	}

}
