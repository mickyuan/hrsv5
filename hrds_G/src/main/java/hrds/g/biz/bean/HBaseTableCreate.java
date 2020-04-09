package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "sql查询实体", author = "dhw", createdate = "2020/4/1 17:51")
@Table(tableName = "hbase_table_create")
public class HBaseTableCreate extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "hbase_table_create";

	@DocBean(name = "enTable", value = "英文名:", dataType = String.class, required = true)
	private String enTable;
	@DocBean(name = "cnTable", value = "中文名:", dataType = String.class, required = true)
	private String cnTable;
	@DocBean(name = "colFam", value = "列族名，包含列族配置信息:", dataType = String.class, required = true)
	private String colFam;
	@DocBean(name = "partitionKeys", value = "预分区键:", dataType = String.class, required = false)
	private String[] partitionKeys;

	public String getEnTable() {
		return enTable;
	}

	public void setEnTable(String enTable) {
		this.enTable = enTable;
	}

	public String getCnTable() {
		return cnTable;
	}

	public void setCnTable(String cnTable) {
		this.cnTable = cnTable;
	}

	public String getColFam() {
		return colFam;
	}

	public void setColFam(String colFam) {
		this.colFam = colFam;
	}

	public String[] getPartitionKeys() {
		return partitionKeys;
	}

	public void setPartitionKeys(String[] partitionKeys) {
		this.partitionKeys = partitionKeys;
	}
}
