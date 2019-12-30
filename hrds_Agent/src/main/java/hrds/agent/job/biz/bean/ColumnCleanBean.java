package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import hrds.commons.entity.Column_split;

import java.io.Serializable;
import java.util.List;

@DocClass(desc = "列清洗参数bean", author = "zxz")
public class ColumnCleanBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@DocBean(name = "col_clean_id", value = "清洗参数编号:", dataType = Long.class, required = true)
	private Long col_clean_id;
	@DocBean(name = "clean_type", value = "清洗方式(CleanType):1-字符补齐<ZiFuBuQi> 2-字符替换<ZiFuTiHuan> 3-时间转换<ShiJianZhuanHuan> 4-码值转换<MaZhiZhuanHuan> 5-字符合并<ZiFuHeBing> 6-字符拆分<ZiFuChaiFen> 7-字符trim<ZiFuTrim> ", dataType = String.class, required = true)
	private String clean_type;
	@DocBean(name = "character_filling", value = "补齐字符:", dataType = String.class, required = false)
	private String character_filling;
	@DocBean(name = "filling_length", value = "补齐长度:", dataType = Long.class, required = false)
	private Long filling_length;
	@DocBean(name = "field", value = "原字段:", dataType = String.class, required = false)
	private String field;
	@DocBean(name = "replace_feild", value = "替换字段:", dataType = String.class, required = false)
	private String replace_feild;
	@DocBean(name = "filling_type", value = "补齐方式(FillingType):1-前补齐<QianBuQi> 2-后补齐<HouBuQi> ", dataType = String.class, required = false)
	private String filling_type;
	@DocBean(name = "convert_format", value = "转换格式:", dataType = String.class, required = false)
	private String convert_format;
	@DocBean(name = "old_format", value = "原始格式:", dataType = String.class, required = false)
	private String old_format;
	@DocBean(name = "codeTransform", value = "码值转换:json格式的字符串", dataType = String.class, required = false)
	private String codeTransform;
	@DocBean(name = "column_split", value = "列分割参数信息:", dataType = Column_split.class, required = false)
	private List<Column_split> column_split_list;

	public Long getCol_clean_id() {
		return col_clean_id;
	}

	public void setCol_clean_id(Long col_clean_id) {
		this.col_clean_id = col_clean_id;
	}

	public String getClean_type() {
		return clean_type;
	}

	public void setClean_type(String clean_type) {
		this.clean_type = clean_type;
	}

	public String getCharacter_filling() {
		return character_filling;
	}

	public void setCharacter_filling(String character_filling) {
		this.character_filling = character_filling;
	}

	public Long getFilling_length() {
		return filling_length;
	}

	public void setFilling_length(Long filling_length) {
		this.filling_length = filling_length;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getReplace_feild() {
		return replace_feild;
	}

	public void setReplace_feild(String replace_feild) {
		this.replace_feild = replace_feild;
	}

	public String getFilling_type() {
		return filling_type;
	}

	public void setFilling_type(String filling_type) {
		this.filling_type = filling_type;
	}

	public String getConvert_format() {
		return convert_format;
	}

	public void setConvert_format(String convert_format) {
		this.convert_format = convert_format;
	}

	public String getOld_format() {
		return old_format;
	}

	public void setOld_format(String old_format) {
		this.old_format = old_format;
	}

	public String getCodeTransform() {
		return codeTransform;
	}

	public void setCodeTransform(String codeTransform) {
		this.codeTransform = codeTransform;
	}

	public List<Column_split> getColumn_split_list() {
		return column_split_list;
	}

	public void setColumn_split_list(List<Column_split> column_split_list) {
		this.column_split_list = column_split_list;
	}
}
