package hrds.k.biz.tdbresult.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "search_fk_analysis_bean")
public class SearchFKAnalysisBean extends ProjectTableEntity {
	private static final long serialVersionUID = -2727689833798758656L;

	public static final String TableName = "search_fk_analysis_bean";


	@DocBean(name = "table_name", value = "主表表名", dataType = String.class, required = false)
	private String table_name; //主表表名
	@DocBean(name = "table_field_name", value = "主表字段名", dataType = String.class, required = false)
	private String table_field_name; //主表字段名
	@DocBean(name = "fk_table_name", value = "外表表名", dataType = String.class, required = false)
	private String fk_table_name; //外表表名
	@DocBean(name = "fk_table_field_name", value = "外表字段名", dataType = String.class, required = false)
	private String fk_table_field_name; //外表字段名

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getTable_field_name() {
		return table_field_name;
	}

	public void setTable_field_name(String table_field_name) {
		this.table_field_name = table_field_name;
	}

	public String getFk_table_name() {
		return fk_table_name;
	}

	public void setFk_table_name(String fk_table_name) {
		this.fk_table_name = fk_table_name;
	}

	public String getFk_table_field_name() {
		return fk_table_field_name;
	}

	public void setFk_table_field_name(String fk_table_field_name) {
		this.fk_table_field_name = fk_table_field_name;
	}
}
