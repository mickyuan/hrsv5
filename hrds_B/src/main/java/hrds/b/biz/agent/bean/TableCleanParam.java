package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "表清洗配置实体类", author = "WangZhengcheng")
public class TableCleanParam {

	//表ID,Table_info表主键，table_clean、column_merge表外键
	private Long tableId;
	//表名
	private String tableName;
	//是否进行字符补齐，true：是，false：否
	private boolean complementFlag;
	//是否进行字符替换，true：是，false：否
	private boolean replaceFlag;
	//是否进行首尾去空，true：是，false：否
	private boolean trimFlag;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public boolean isComplementFlag() {
		return complementFlag;
	}

	public void setComplementFlag(boolean complementFlag) {
		this.complementFlag = complementFlag;
	}

	public boolean isReplaceFlag() {
		return replaceFlag;
	}

	public void setReplaceFlag(boolean replaceFlag) {
		this.replaceFlag = replaceFlag;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isTrimFlag() {
		return trimFlag;
	}

	public void setTrimFlag(boolean trimFlag) {
		this.trimFlag = trimFlag;
	}
}
