package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "列组成参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "column_composition")
public class ColumnComposition extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "column_composition";

	@DocBean(name = "en_column", value = "列中文名:", dataType = String.class, required = true)
	private String en_column;
	@DocBean(name = "cn_column", value = "列英文名:", dataType = String.class, required = true)
	private String cn_column;

	public String getEn_column() {
		return en_column;
	}

	public void setEn_column(String en_column) {
		this.en_column = en_column;
	}

	public String getCn_column() {
		return cn_column;
	}

	public void setCn_column(String cn_column) {
		this.cn_column = cn_column;
	}
}
