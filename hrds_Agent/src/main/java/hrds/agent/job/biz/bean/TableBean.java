package hrds.agent.job.biz.bean;

import java.util.Map;

public class TableBean {
	private StringBuilder columnMetaInfo = new StringBuilder();//生成的元信息列名
	private StringBuilder allColumns = new StringBuilder();//要采集的列名
	private StringBuilder colTypeMetaInfo = new StringBuilder();//生成的元信息列类型
	private StringBuilder allType = new StringBuilder();//要采集的列类型
	private StringBuilder colLengthInfo = new StringBuilder();//生成的元信息列长度
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

	public StringBuilder getColumnMetaInfo() {

		return columnMetaInfo;
	}

	public void setColumnMetaInfo(StringBuilder columnMetaInfo) {

		this.columnMetaInfo = columnMetaInfo;
	}

	public StringBuilder getAllColumns() {

		return allColumns;
	}

	public void setAllColumns(StringBuilder allColumns) {

		this.allColumns = allColumns;
	}

	public StringBuilder getColTypeMetaInfo() {

		return colTypeMetaInfo;
	}

	public void setColTypeMetaInfo(StringBuilder colTypeMetaInfo) {

		this.colTypeMetaInfo = colTypeMetaInfo;
	}

	public StringBuilder getAllType() {

		return allType;
	}

	public void setAllType(StringBuilder allType) {

		this.allType = allType;
	}

	public StringBuilder getColLengthInfo() {

		return colLengthInfo;
	}

	public void setColLengthInfo(StringBuilder colLengthInfo) {

		this.colLengthInfo = colLengthInfo;
	}
}
