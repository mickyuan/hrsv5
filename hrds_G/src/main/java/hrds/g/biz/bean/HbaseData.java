package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "新增hbase数据参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "hbase_data")
public class HbaseData extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "hbase_data";

	@DocBean(name = "enTable", value = "表英文名:", dataType = String.class, required = true)
	private String enTable;
	@DocBean(name = "rowkey", value = "HBase rowkey:", dataType = String.class, required = true)
	private String rowkey;
	@DocBean(name = "insertVersion", value = "用户ID:", dataType = String.class, required = false)
	private String insertVersion;

	public String getEnTable() {
		return enTable;
	}

	public void setEnTable(String enTable) {
		this.enTable = enTable;
	}

	public String getRowkey() {
		return rowkey;
	}

	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}

	public String getInsertVersion() {
		return insertVersion;
	}

	public void setInsertVersion(String insertVersion) {
		this.insertVersion = insertVersion;
	}
}
