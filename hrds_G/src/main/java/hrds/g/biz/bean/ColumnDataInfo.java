package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;

@DocClass(desc = "表列信息对象", author = "dhw", createdate = "2020/3/25 15:35")
@Table(tableName = "column_data_info")
public class ColumnDataInfo {
	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "column_data_info";

	@DocBean(name = "table_ch_column", value = "字段中文名称:", dataType = String.class, required = true)
	private String table_ch_column;
	@DocBean(name = "table_cn_column", value = "字段英文名称:", dataType = String.class, required = true)
	private String table_cn_column;

	public String getTable_ch_column() {
		return table_ch_column;
	}

	public void setTable_ch_column(String table_ch_column) {
		this.table_ch_column = table_ch_column;
	}

	public String getTable_cn_column() {
		return table_cn_column;
	}

	public void setTable_cn_column(String table_cn_column) {
		this.table_cn_column = table_cn_column;
	}
}
