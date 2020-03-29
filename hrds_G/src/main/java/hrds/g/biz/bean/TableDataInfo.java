package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;
import org.stringtemplate.v4.ST;

@DocClass(desc = "表数据信息对象", author = "dhw", createdate = "2020/3/25 15:35")
@Table(tableName = "table_data_info")
public class TableDataInfo extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "table_data_info";

	@DocBean(name = "user_id", value = "用户ID:", dataType = Long.class, required = true)
	private Long user_id;
	@DocBean(name = "file_id", value = "字段ID:", dataType = Long.class, required = true)
	private String file_id;
	@DocBean(name = "column_name", value = "字段ID:", dataType = String[].class, required = true)
	private String[] column_id;
	@DocBean(name = "table_note", value = "备注:", dataType = String.class, required = true)
	private String table_note;
	@DocBean(name = "dataSourceType", value = "数据源类型:", dataType = String.class, required = false)
	private String dataSourceType;

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getTable_note() {
		return table_note;
	}

	public void setTable_note(String table_note) {
		this.table_note = table_note;
	}

	public String[] getColumn_id() {
		return column_id;
	}

	public void setColumn_id(String[] column_id) {
		this.column_id = column_id;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
}
