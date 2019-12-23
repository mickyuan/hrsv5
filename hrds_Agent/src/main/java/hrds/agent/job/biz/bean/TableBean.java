package hrds.agent.job.biz.bean;

import java.util.Map;

public class TableBean {
	private String columnMetaInfo;//生成的元信息列名
	private String allColumns;//要采集的列名
	private String colTypeMetaInfo;//生成的元信息列类型
	private String allType;//要采集的列类型
	private String colLengthInfo;//生成的元信息列长度
	private Map<String, Object> parseJson; //清洗配置
	private String collectSQL;//最后拼接的执行sql
	private int[] typeArray;

	public String getCollectSQL() {
		return collectSQL;
	}

	public void setCollectSQL(String collectSQL) {
		this.collectSQL = collectSQL;
	}

	public Map<String, Object> getParseJson() {

		return parseJson;
	}


	public void setParseJson(Map<String, Object> parseJson) {

		this.parseJson = parseJson;
	}

	public int[] getTypeArray() {

		return typeArray;
	}


	public void setTypeArray(int[] typeArray) {

		this.typeArray = typeArray;
	}

	public String getColumnMetaInfo() {
		return columnMetaInfo;
	}

	public void setColumnMetaInfo(String columnMetaInfo) {
		this.columnMetaInfo = columnMetaInfo;
	}

	public String getAllColumns() {
		return allColumns;
	}

	public void setAllColumns(String allColumns) {
		this.allColumns = allColumns;
	}

	public String getColTypeMetaInfo() {
		return colTypeMetaInfo;
	}

	public void setColTypeMetaInfo(String colTypeMetaInfo) {
		this.colTypeMetaInfo = colTypeMetaInfo;
	}

	public String getAllType() {
		return allType;
	}

	public void setAllType(String allType) {
		this.allType = allType;
	}

	public String getColLengthInfo() {
		return colLengthInfo;
	}

	public void setColLengthInfo(String colLengthInfo) {
		this.colLengthInfo = colLengthInfo;
	}
}
