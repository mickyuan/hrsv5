package hrds.agent.job.biz.bean;

import java.util.Map;

public class TableBean {
	private String columnMetaInfo;//生成的元信息列名（这里跟要采集的列名不一样，这里包括列合并、拆分出来的新列）
	private String allColumns;//要采集的列名 （页面选择的使用jdbc查询的列，解析db文件采集的列）
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
	private String primaryKeyInfo; //是否为主键
	private String operate; //操作类型
	private String is_archived; //是否转存
	private String insertColumnInfo = ""; //获取新增数据的sql涉及的列
	private String updateColumnInfo = ""; //获取更新数据的sql涉及的列
	private String deleteColumnInfo = ""; //获取删除数据的sql涉及的列
	//db文件采集，字段是否采集的映射,对新增列不做映射，默认采集(注：新增列指的是拆分和合并的列)
	//	private HashMap<String, Boolean> isCollectMap = null;
	//db文件转存的文件编码，这里的编码是主要涉及到oracle数据库如果用外部表进数，字符集必须跟文件字符集一致的问题，
	// 默认是UTF-8，针对oracle存储层，会读取oracle存储层配置的编码，根据页面传过来的值，转为代码项
	private String dbFileArchivedCode;

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

	public String getPrimaryKeyInfo() {
		return primaryKeyInfo;
	}

	public void setPrimaryKeyInfo(String primaryKeyInfo) {
		this.primaryKeyInfo = primaryKeyInfo;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getIs_archived() {
		return is_archived;
	}

	public void setIs_archived(String is_archived) {
		this.is_archived = is_archived;
	}
	//	public HashMap<String, Boolean> getIsCollectMap() {
//		return isCollectMap;
//	}
//
//	public void setIsCollectMap(HashMap<String, Boolean> isCollectMap) {
//		this.isCollectMap = isCollectMap;
//	}

	public String getDbFileArchivedCode() {
		return dbFileArchivedCode;
	}

	public void setDbFileArchivedCode(String dbFileArchivedCode) {
		this.dbFileArchivedCode = dbFileArchivedCode;
	}

	public String getInsertColumnInfo() {
		return insertColumnInfo;
	}

	public void setInsertColumnInfo(String insertColumnInfo) {
		this.insertColumnInfo = insertColumnInfo;
	}

	public String getUpdateColumnInfo() {
		return updateColumnInfo;
	}

	public void setUpdateColumnInfo(String updateColumnInfo) {
		this.updateColumnInfo = updateColumnInfo;
	}

	public String getDeleteColumnInfo() {
		return deleteColumnInfo;
	}

	public void setDeleteColumnInfo(String deleteColumnInfo) {
		this.deleteColumnInfo = deleteColumnInfo;
	}
}
