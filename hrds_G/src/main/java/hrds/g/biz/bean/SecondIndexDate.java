package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "二级索引日期范围查询参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "second_index_date")
public class SecondIndexDate extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "second_index_date";

	@DocBean(name = "table", value = "表名称:", dataType = String.class, required = true)
	private String table;
	@DocBean(name = "column", value = "需要查询的日期列名:", dataType = String.class, required = true)
	private String column;
	@DocBean(name = "startdate", value = "起始日期:", dataType = String.class, required = true)
	private String startdate;
	@DocBean(name = "enddate", value = "结束日期:", dataType = String.class, required = true)
	private String enddate;
	@DocBean(name = "outType", value = "数据输出形式:( stream / file)只能选择一种", dataType = String.class, required = true)
	private String outType;
	@DocBean(name = "asynType", value = "异步标识:outType为file时使用", dataType = String.class, required = false)
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

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
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
