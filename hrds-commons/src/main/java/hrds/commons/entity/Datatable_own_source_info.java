package hrds.commons.entity;
/**
 * Auto Created by VBScript Do not modify!
 */

import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * 数据表已选数据源信息
 */
public class Datatable_own_source_info extends ProjectTableEntity {
	public static final String TableName = "datatable_own_source_info";
	private Long own_dource_table_id; //已选数据源表id
	private String own_source_table_name; //已选数据源表名
	private Long datatable_id; //数据表id
	private String remark; //备注
	private String source_type; //数据源类型

	/** 取得：已选数据源表id */
	public Long getOwn_dource_table_id() {
		return own_dource_table_id;
	}

	/** 设置：已选数据源表id */
	public void setOwn_dource_table_id(Long own_dource_table_id) {
		this.own_dource_table_id = own_dource_table_id;
	}

	/** 设置：已选数据源表id */
	public void setOwn_dource_table_id(String own_dource_table_id) {
		if (!StringUtil.isEmpty(own_dource_table_id))
			this.own_dource_table_id = new Long(own_dource_table_id);
	}

	/** 取得：已选数据源表名 */
	public String getOwn_source_table_name() {
		return own_source_table_name;
	}

	/** 设置：已选数据源表名 */
	public void setOwn_source_table_name(String own_source_table_name) {
		this.own_source_table_name = own_source_table_name;
	}

	/** 取得：数据表id */
	public Long getDatatable_id() {
		return datatable_id;
	}

	/** 设置：数据表id */
	public void setDatatable_id(Long datatable_id) {
		this.datatable_id = datatable_id;
	}

	/** 设置：数据表id */
	public void setDatatable_id(String datatable_id) {
		if (!StringUtil.isEmpty(datatable_id))
			this.datatable_id = new Long(datatable_id);
	}

	/** 取得：备注 */
	public String getRemark() {
		return remark;
	}

	/** 设置：备注 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/** 取得：数据源类型 */
	public String getSource_type() {
		return source_type;
	}

	/** 设置：数据源类型 */
	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}

	private Set primaryKeys = new HashSet();

	public boolean isPrimaryKey(String name) {
		return primaryKeys.contains(name);
	}

	public String getPrimaryKey() {
		return primaryKeys.iterator().next().toString();
	}

	/** 数据表已选数据源信息 */
	public Datatable_own_source_info() {
		primaryKeys.add("own_dource_table_id");
	}
}
