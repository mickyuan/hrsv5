package hrds.h.biz.market;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RelationTable {
	private String tablename;
	private List<subTable> subTables = new ArrayList<>();
	public class subTable{
		public String getJointype() {
			return jointype;
		}

		public String getMaincolumn() {
			return maincolumn;
		}

		public String getSubcolumn() {
			return subcolumn;
		}

		public void setJointype(String jointype) {
			this.jointype = jointype;
		}

		public void setMaincolumn(String maincolumn) {
			this.maincolumn = maincolumn;
		}

		public void setSubcolumn(String subcolumn) {
			this.subcolumn = subcolumn;
		}

		public List<Map<String,Object>> getColumns() {
			return columns;
		}

		public void setColumns(List<Map<String,Object>> columns) {
			this.columns = columns;
		}

		private String jointype;
		private String maincolumn;
		private String subcolumn;
		private List<Map<String,Object>> columns;
	}


	public String getTablename() {
		return tablename;
	}

	public List<subTable> getSubTables() {
		return subTables;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public void setSubTables(List<subTable> subTables) {
		this.subTables = subTables;
	}
}
