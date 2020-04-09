package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;

@DocClass(desc = "表数据批量更新参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "data_batch_update")
public class DataBatchUpdate {
	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "data_batch_update";

	@DocBean(name = "tableName", value = "表名:", dataType = String.class, required = true)
	private String tableName;
	@DocBean(name = "tableType", value = "表类型(hbase|carbon|mpp):", dataType = String.class, required = true)
	private String tableType;
	@DocBean(name = "guideFilePath", value = "数据字典标识文件路径:", dataType = String.class, required = true)
	private String guideFilePath;
	@DocBean(name = "exactTextFilePath", value = "数据文件路径:", dataType = String.class, required = true)
	private String exactTextFilePath;
	@DocBean(name = "isRowkey", value = "条件列是否为主键(tableType为hbase时才生效):", dataType = String.class, required = false)
	private String isRowkey;

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getGuideFilePath() {
		return guideFilePath;
	}

	public void setGuideFilePath(String guideFilePath) {
		this.guideFilePath = guideFilePath;
	}

	public String getExactTextFilePath() {
		return exactTextFilePath;
	}

	public void setExactTextFilePath(String exactTextFilePath) {
		this.exactTextFilePath = exactTextFilePath;
	}

	public String getIsRowkey() {
		return isRowkey;
	}

	public void setIsRowkey(String isRowkey) {
		this.isRowkey = isRowkey;
	}
}
