package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;

import java.io.Serializable;
import java.util.List;

@DocClass(desc = "DB文件采集,数据库采集选择表字段配置信息", author = "zxz", createdate = "2019/11/29 14:52")
public class CollectTableColumnBean implements Serializable {
	@DocBean(name = "column_id", value = "字段ID:", dataType = Long.class, required = true)
	private Long column_id;
	@DocBean(name = "is_primary_key", value = "是否为主键(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_primary_key;
	@DocBean(name = "column_name", value = "列名:", dataType = String.class, required = true)
	private String column_name;
	@DocBean(name = "column_ch_name", value = "列中文名称:", dataType = String.class, required = false)
	private String column_ch_name;
	@DocBean(name = "valid_s_date", value = "有效开始日期:", dataType = String.class, required = true)
	private String valid_s_date;
	@DocBean(name = "valid_e_date", value = "有效结束日期:", dataType = String.class, required = true)
	private String valid_e_date;
	@DocBean(name = "is_get", value = "是否采集(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = false)
	private String is_get;
	@DocBean(name = "column_type", value = "列字段类型:", dataType = String.class, required = false)
	private String column_type;
	@DocBean(name = "tc_remark", value = "备注:", dataType = String.class, required = false)
	private String tc_remark;
	@DocBean(name = "is_alive", value = "是否保留原字段(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_alive;
	@DocBean(name = "is_new", value = "是否为变化生成(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_new;
	@DocBean(name = "tc_or", value = "清洗顺序:", dataType = String.class, required = false)
	private String tc_or;
	@DocBean(name = "column_clean_list", value = "列清洗参数信息:", dataType = List.class, required = false)
	private List<ColumnCleanBean> columnCleanBeanList;

	public Long getColumn_id() {
		return column_id;
	}

	public void setColumn_id(Long column_id) {
		this.column_id = column_id;
	}

	public String getIs_primary_key() {
		return is_primary_key;
	}

	public void setIs_primary_key(String is_primary_key) {
		this.is_primary_key = is_primary_key;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}

	public String getColumn_ch_name() {
		return column_ch_name;
	}

	public void setColumn_ch_name(String column_ch_name) {
		this.column_ch_name = column_ch_name;
	}

	public String getValid_s_date() {
		return valid_s_date;
	}

	public void setValid_s_date(String valid_s_date) {
		this.valid_s_date = valid_s_date;
	}

	public String getValid_e_date() {
		return valid_e_date;
	}

	public void setValid_e_date(String valid_e_date) {
		this.valid_e_date = valid_e_date;
	}

	public String getIs_get() {
		return is_get;
	}

	public void setIs_get(String is_get) {
		this.is_get = is_get;
	}

	public String getColumn_type() {
		return column_type;
	}

	public void setColumn_type(String column_type) {
		this.column_type = column_type;
	}

	public String getTc_remark() {
		return tc_remark;
	}

	public void setTc_remark(String tc_remark) {
		this.tc_remark = tc_remark;
	}

	public String getIs_alive() {
		return is_alive;
	}

	public void setIs_alive(String is_alive) {
		this.is_alive = is_alive;
	}

	public String getIs_new() {
		return is_new;
	}

	public void setIs_new(String is_new) {
		this.is_new = is_new;
	}

	public String getTc_or() {
		return tc_or;
	}

	public void setTc_or(String tc_or) {
		this.tc_or = tc_or;
	}

	public List<ColumnCleanBean> getColumnCleanBeanList() {
		return columnCleanBeanList;
	}

	public void setColumnCleanBeanList(List<ColumnCleanBean> columnCleanBeanList) {
		this.columnCleanBeanList = columnCleanBeanList;
	}
}
