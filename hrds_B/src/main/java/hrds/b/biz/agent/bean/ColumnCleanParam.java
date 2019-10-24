package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "列清洗参数实体类", author = "WangZhengcheng")
public class ColumnCleanParam {

	//列ID,Table_column表主键，column_clean、column_split表外键
	private Long columnId;
	//是否进行字符补齐，true：是，false：否
	private boolean complementFlag;
	//是否进行字符替换，true：是，false：否
	private boolean replaceFlag;
	//是否进行日期格式化，true：是，false：否
	private boolean formatFlag;
	//是否进行码值转换，true：是，false：否
	private boolean conversionFlag;
	//是否进行列拆分，true：是，false：否
	private boolean spiltFlag;
	//是否进行首尾去空，true：是，false：否
	private boolean trimFlag;

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
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

	public boolean isFormatFlag() {
		return formatFlag;
	}

	public void setFormatFlag(boolean formatFlag) {
		this.formatFlag = formatFlag;
	}

	public boolean isConversionFlag() {
		return conversionFlag;
	}

	public void setConversionFlag(boolean conversionFlag) {
		this.conversionFlag = conversionFlag;
	}

	public boolean isSpiltFlag() {
		return spiltFlag;
	}

	public void setSpiltFlag(boolean spiltFlag) {
		this.spiltFlag = spiltFlag;
	}

	public boolean isTrimFlag() {
		return trimFlag;
	}

	public void setTrimFlag(boolean trimFlag) {
		this.trimFlag = trimFlag;
	}
}
