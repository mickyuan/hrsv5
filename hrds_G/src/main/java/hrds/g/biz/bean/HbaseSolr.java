package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "HBaseSolr查询参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "hbase_solr")
public class HbaseSolr extends ProjectTableEntity {
	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "hbase_solr";

	@DocBean(name = "tableName", value = "表名称:", dataType = String.class)
	private String tableName;
	@DocBean(name = "whereColumn", value = "条件Column:", dataType = String.class)
	private String whereColumn;
	@DocBean(name = "selectColumn", value = "查询Column:", dataType = String.class)
	private String selectColumn;
	@DocBean(name = "start", value = "开始取数位置:", dataType = Integer.class)
	private Integer start;
	@DocBean(name = "num", value = "显示条数:", dataType = Integer.class)
	private Integer num;
	@DocBean(name = "dataType", value = "数据输出形式:( json / csv)只能选择一种", dataType = String.class)
	private String dataType;
	@DocBean(name = "outType", value = "数据输出形式:( stream / file)只能选择一种", dataType = String.class)
	private String outType;
	@DocBean(name = "asynType", value = "异步标识:outType为file时使用", dataType = String.class)
	private String asynType;
	@DocBean(name = "backurl", value = "回调路径:与参数asynType一起使用(如果asynType为1,则必填回调URL)",
			dataType = String.class, required = false)
	private String backurl;
	@DocBean(name = "filename", value = "文件名:与参数asynType一起使用(如果asynType为2,则必填轮询返回文件名称)",
			dataType = String.class, required = false)
	private String filename;
	@DocBean(name = "filepath", value = "轮询ok文件路径:与参数asynType一起使用(如果asynType为2,则必填轮询返回文件路径)",
			dataType = String.class, required = false)
	private String filepath;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getWhereColumn() {
		return whereColumn;
	}

	public void setWhereColumn(String whereColumn) {
		this.whereColumn = whereColumn;
	}

	public String getSelectColumn() {
		return selectColumn;
	}

	public void setSelectColumn(String selectColumn) {
		this.selectColumn = selectColumn;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getOutType() {
		return outType;
	}

	public void setOutType(String outType) {
		this.outType = outType;
	}

	public String getAsynType() {
		return asynType;
	}

	public void setAsynType(String asynType) {
		this.asynType = asynType;
	}

	public String getBackurl() {
		return backurl;
	}

	public void setBackurl(String backurl) {
		this.backurl = backurl;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
