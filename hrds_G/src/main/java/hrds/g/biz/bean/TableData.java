package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "表数据接口参数实体", author = "dhw", createdate = "2020/7/24 9:28")
@Table(tableName = "table_data")
public class TableData extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "table_data";

	@DocBean(name = "tableName", value = "表名:", dataType = String.class)
	private String tableName;
	@DocBean(name = "rowKeys", value = "HBase rowkey数组:", dataType = String[].class)
	private String[] rowKeys;
	@DocBean(name = "whereColumn", value = "选择列:", dataType = String.class)
	private String whereColumn;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String[] getRowKeys() {
		return rowKeys;
	}

	public void setRowKeys(String[] rowKeys) {
		this.rowKeys = rowKeys;
	}

	public String getWhereColumn() {
		return whereColumn;
	}

	public void setWhereColumn(String whereColumn) {
		this.whereColumn = whereColumn;
	}
}
