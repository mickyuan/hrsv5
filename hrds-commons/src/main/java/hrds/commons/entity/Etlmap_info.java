package hrds.commons.entity;
/**
 * Auto Created by VBScript Do not modify!
 */

import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * 结果映射信息表
 */
public class Etlmap_info extends ProjectTableEntity {

	public static final String TableName = "etlmap_info";
	private Long etl_id; //表id
	private String targetfield_name; //目标字段名称
	private String sourcefields_name; //来源字段名称
	private Long datatable_id; //数据表id
	private String remark; //备注
	private Long own_dource_table_id; //已选数据源表id

	/**
	 * 取得：表id
	 */
	public Long getEtl_id() {
		return etl_id;
	}

	/**
	 * 设置：表id
	 */
	public void setEtl_id(Long etl_id) {
		this.etl_id = etl_id;
	}

	/**
	 * 设置：表id
	 */
	public void setEtl_id(String etl_id) {
		if (!StringUtil.isEmpty(etl_id))
			this.etl_id = new Long(etl_id);
	}

	/**
	 * 取得：目标字段名称
	 */
	public String getTargetfield_name() {
		return targetfield_name;
	}

	/**
	 * 设置：目标字段名称
	 */
	public void setTargetfield_name(String targetfield_name) {
		this.targetfield_name = targetfield_name;
	}

	/**
	 * 取得：来源字段名称
	 */
	public String getSourcefields_name() {
		return sourcefields_name;
	}

	/**
	 * 设置：来源字段名称
	 */
	public void setSourcefields_name(String sourcefields_name) {
		this.sourcefields_name = sourcefields_name;
	}

	/**
	 * 取得：数据表id
	 */
	public Long getDatatable_id() {
		return datatable_id;
	}

	/**
	 * 设置：数据表id
	 */
	public void setDatatable_id(Long datatable_id) {
		this.datatable_id = datatable_id;
	}

	/**
	 * 设置：数据表id
	 */
	public void setDatatable_id(String datatable_id) {
		if (!StringUtil.isEmpty(datatable_id))
			this.datatable_id = new Long(datatable_id);
	}

	/**
	 * 取得：备注
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置：备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 取得：已选数据源表id
	 */
	public Long getOwn_dource_table_id() {
		return own_dource_table_id;
	}

	/**
	 * 设置：已选数据源表id
	 */
	public void setOwn_dource_table_id(Long own_dource_table_id) {
		this.own_dource_table_id = own_dource_table_id;
	}

	/**
	 * 设置：已选数据源表id
	 */
	public void setOwn_dource_table_id(String own_dource_table_id) {
		if (!StringUtil.isEmpty(own_dource_table_id))
			this.own_dource_table_id = new Long(own_dource_table_id);
	}

	private Set primaryKeys = new HashSet();

	public boolean isPrimaryKey(String name) {
		return primaryKeys.contains(name);
	}

	public String getPrimaryKey() {
		return primaryKeys.iterator().next().toString();
	}

	/**
	 * 结果映射信息表
	 */
	public Etlmap_info() {
		primaryKeys.add("etl_id");
	}
}
