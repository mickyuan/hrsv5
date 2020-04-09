package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "表字段参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "column_filed")
public class ColumnField extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "column_filed";

	@DocBean(name = "encolname", value = "字段名称:", dataType = String.class, required = true)
	private String encolname;
	@DocBean(name = "cncolname", value = "字段中文描述:", dataType = String.class, required = true)
	private String cncolname;
	@DocBean(name = "coltype", value = "字段类型:", dataType = String.class, required = true)
	private String coltype;

	public String getEncolname() {
		return encolname;
	}

	public void setEncolname(String encolname) {
		this.encolname = encolname;
	}

	public String getCncolname() {
		return cncolname;
	}

	public void setCncolname(String cncolname) {
		this.cncolname = cncolname;
	}

	public String getColtype() {
		return coltype;
	}

	public void setColtype(String coltype) {
		this.coltype = coltype;
	}
}
