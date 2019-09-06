package hrds.agent.job.biz.bean;

import java.io.Serializable;
import java.util.List;

public class MetaInfoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tableName;
	private List<String> columnNames;
	private List<String> columnTypes;
	private List<String> columnLength;
	private long fileSize;
	private long rowCount;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public List<String> getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(List<String> columnTypes) {
		this.columnTypes = columnTypes;
	}

	public List<String> getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(List<String> columnLength) {
		this.columnLength = columnLength;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}
}
