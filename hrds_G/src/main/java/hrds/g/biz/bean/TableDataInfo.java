package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "表数据信息对象", author = "dhw", createdate = "2020/3/25 15:35")
@Table(tableName = "table_data_info")
public class TableDataInfo extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "table_data_info";

	@DocBean(name = "file_id", value = "表ID:", dataType = Long.class, required = true)
	private String file_id;
	@DocBean(name = "column_name", value = "字段名称:", dataType = String.class, required = true)
	private String[] column_name;

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String[] getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String[] column_name) {
		this.column_name = column_name;
	}

}
