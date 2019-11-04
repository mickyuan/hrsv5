package hrds.commons.entity;
/**
 * Auto Created by VBScript Do not modify!
 */

import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * 数据表字段信息
 */
public class Datatable_field_info extends ProjectTableEntity {
	public static final String TableName = "datatable_field_info";
	private Long datatable_field_id; //数据表字段id
	private String field_cn_name; //字段中文名称
	private String field_en_name; //字段英文名称
	private String field_type; //字段类型
	private String field_desc; //字段描述
	private Long datatable_id; //数据表id
	private Long field_seq; //字段序号
	private String remark; //备注
	private String is_rowkey; //是否作为hbaseRowkey
	private Long rowkey_seq; //rowkey序号
	private Long redis_seq; //redis序号
	private String is_rediskey; //是否是rediskey
	private String is_solr_index; //是否作为hbaseOnsolr索引
	private String field_length; //字段长度
	private String field_process; //处理方式
	private String process_para; //处理方式对应参数
	private String is_cb; //是否carbondata聚合列
	private String is_sortcolumns; //是否为carbondata的排序列
	private String is_unique_index; //是否唯一索引
	private String is_primary_key; //是否主键
	private String is_partition; //是否分区
	private String is_normal_index; //是否普通索引

	/**
	 * 取得：数据表字段id
	 */
	public Long getDatatable_field_id() {
		return datatable_field_id;
	}

	/**
	 * 设置：数据表字段id
	 */
	public void setDatatable_field_id(Long datatable_field_id) {
		this.datatable_field_id = datatable_field_id;
	}

	/**
	 * 设置：数据表字段id
	 */
	public void setDatatable_field_id(String datatable_field_id) {
		if (!StringUtil.isEmpty(datatable_field_id))
			this.datatable_field_id = new Long(datatable_field_id);
	}

	/**
	 * 取得：字段中文名称
	 */
	public String getField_cn_name() {
		return field_cn_name;
	}

	/**
	 * 设置：字段中文名称
	 */
	public void setField_cn_name(String field_cn_name) {
		this.field_cn_name = field_cn_name;
	}

	/**
	 * 取得：字段英文名称
	 */
	public String getField_en_name() {
		return field_en_name;
	}

	/**
	 * 设置：字段英文名称
	 */
	public void setField_en_name(String field_en_name) {
		this.field_en_name = field_en_name;
	}

	/**
	 * 取得：字段类型
	 */
	public String getField_type() {
		return field_type;
	}

	/**
	 * 设置：字段类型
	 */
	public void setField_type(String field_type) {
		this.field_type = field_type;
	}

	/**
	 * 取得：字段描述
	 */
	public String getField_desc() {
		return field_desc;
	}

	/**
	 * 设置：字段描述
	 */
	public void setField_desc(String field_desc) {
		this.field_desc = field_desc;
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
	 * 取得：字段序号
	 */
	public Long getField_seq() {
		return field_seq;
	}

	/**
	 * 设置：字段序号
	 */
	public void setField_seq(Long field_seq) {
		this.field_seq = field_seq;
	}

	/**
	 * 设置：字段序号
	 */
	public void setField_seq(String field_seq) {
		if (!StringUtil.isEmpty(field_seq))
			this.field_seq = new Long(field_seq);
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
	 * 取得：是否作为hbaseRowkey
	 */
	public String getIs_rowkey() {
		return is_rowkey;
	}

	/**
	 * 设置：是否作为hbaseRowkey
	 */
	public void setIs_rowkey(String is_rowkey) {
		this.is_rowkey = is_rowkey;
	}

	/**
	 * 取得：rowkey序号
	 */
	public Long getRowkey_seq() {
		return rowkey_seq;
	}

	/**
	 * 设置：rowkey序号
	 */
	public void setRowkey_seq(Long rowkey_seq) {
		this.rowkey_seq = rowkey_seq;
	}

	/**
	 * 设置：rowkey序号
	 */
	public void setRowkey_seq(String rowkey_seq) {
		if (!StringUtil.isEmpty(rowkey_seq))
			this.rowkey_seq = new Long(rowkey_seq);
	}

	/**
	 * 取得：redis序号
	 */
	public Long getRedis_seq() {
		return redis_seq;
	}

	/**
	 * 设置：redis序号
	 */
	public void setRedis_seq(Long redis_seq) {
		this.redis_seq = redis_seq;
	}

	/**
	 * 设置：redis序号
	 */
	public void setRedis_seq(String redis_seq) {
		if (!StringUtil.isEmpty(redis_seq))
			this.redis_seq = new Long(redis_seq);
	}

	/**
	 * 取得：是否是rediskey
	 */
	public String getIs_rediskey() {
		return is_rediskey;
	}

	/**
	 * 设置：是否是rediskey
	 */
	public void setIs_rediskey(String is_rediskey) {
		this.is_rediskey = is_rediskey;
	}

	/**
	 * 取得：是否作为hbaseOnsolr索引
	 */
	public String getIs_solr_index() {
		return is_solr_index;
	}

	/**
	 * 设置：是否作为hbaseOnsolr索引
	 */
	public void setIs_solr_index(String is_solr_index) {
		this.is_solr_index = is_solr_index;
	}

	/**
	 * 取得：字段长度
	 */
	public String getField_length() {
		return field_length;
	}

	/**
	 * 设置：字段长度
	 */
	public void setField_length(String field_length) {
		this.field_length = field_length;
	}

	/**
	 * 取得：处理方式
	 */
	public String getField_process() {
		return field_process;
	}

	/**
	 * 设置：处理方式
	 */
	public void setField_process(String field_process) {
		this.field_process = field_process;
	}

	/**
	 * 取得：处理方式对应参数
	 */
	public String getProcess_para() {
		return process_para;
	}

	/**
	 * 设置：处理方式对应参数
	 */
	public void setProcess_para(String process_para) {
		this.process_para = process_para;
	}

	/**
	 * 取得：是否carbondata聚合列
	 */
	public String getIs_cb() {
		return is_cb;
	}

	/**
	 * 设置：是否carbondata聚合列
	 */
	public void setIs_cb(String is_cb) {
		this.is_cb = is_cb;
	}

	/**
	 * 取得：是否为carbondata的排序列
	 */
	public String getIs_sortcolumns() {
		return is_sortcolumns;
	}

	/**
	 * 设置：是否为carbondata的排序列
	 */
	public void setIs_sortcolumns(String is_sortcolumns) {
		this.is_sortcolumns = is_sortcolumns;
	}

	/**
	 * 取得：是否唯一索引
	 */
	public String getIs_unique_index() {
		return is_unique_index;
	}

	/**
	 * 设置：是否唯一索引
	 */
	public void setIs_unique_index(String is_unique_index) {
		this.is_unique_index = is_unique_index;
	}

	/**
	 * 取得：是否主键
	 */
	public String getIs_primary_key() {
		return is_primary_key;
	}

	/**
	 * 设置：是否主键
	 */
	public void setIs_primary_key(String is_primary_key) {
		this.is_primary_key = is_primary_key;
	}

	/**
	 * 取得：是否分区
	 */
	public String getIs_partition() {
		return is_partition;
	}

	/**
	 * 设置：是否分区
	 */
	public void setIs_partition(String is_partition) {
		this.is_partition = is_partition;
	}

	/**
	 * 取得：是否普通索引
	 */
	public String getIs_normal_index() {
		return is_normal_index;
	}

	/**
	 * 设置：是否普通索引
	 */
	public void setIs_normal_index(String is_normal_index) {
		this.is_normal_index = is_normal_index;
	}

	private Set primaryKeys = new HashSet();

	public boolean isPrimaryKey(String name) {
		return primaryKeys.contains(name);
	}

	public String getPrimaryKey() {
		return primaryKeys.iterator().next().toString();
	}

	/**
	 * 数据表字段信息
	 */
	public Datatable_field_info() {
		primaryKeys.add("datatable_field_id");
	}
}
