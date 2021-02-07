package hrds.k.biz.tdbresult.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "search_join_pk_analysis_bean")
public class SearchJoinPKAnalysisBean extends ProjectTableEntity {
	private static final long serialVersionUID = -3741603791275809710L;

	public static final String TableName = "search_join_pk_analysis_bean";

	@DocBean(name = "table_name", value = "表名", dataType = String.class, required = false)
	private String table_name; //表名

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
}
