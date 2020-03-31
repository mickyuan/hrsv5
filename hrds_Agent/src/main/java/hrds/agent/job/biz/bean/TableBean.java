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
	private String file_format; //文件格式
	private String is_header;   //是否有表头
	private String row_separator;   //行分隔符
	private String column_separator;    //列分隔符
	private String root_path;   //采集文件根路径
	private String file_code;   //文件编码

	public String getFile_code() {
		return file_code;
	}

	public void setFile_code(String file_code) {
		this.file_code = file_code;
	}

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

	public String getFile_format() {
		return file_format;
	}

	public void setFile_format(String file_format) {
		this.file_format = file_format;
	}

	public String getIs_header() {
		return is_header;
	}

	public void setIs_header(String is_header) {
		this.is_header = is_header;
	}

	public String getRow_separator() {
		return row_separator;
	}

	public void setRow_separator(String row_separator) {
		this.row_separator = row_separator;
	}

	public String getColumn_separator() {
		return column_separator;
	}

	public void setColumn_separator(String column_separator) {
		this.column_separator = column_separator;
	}

	public String getRoot_path() {
		return root_path;
	}

	public void setRoot_path(String root_path) {
		this.root_path = root_path;
	}
}
