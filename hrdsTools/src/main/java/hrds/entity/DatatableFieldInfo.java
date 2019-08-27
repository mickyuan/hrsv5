package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "datatable_field_info")
public class DatatableFieldInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datatable_field_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datatable_field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String is_primary_key;
	private String field_cn_name;
	private String is_unique_index;
	private String is_partition;
	private String is_cb;
	private String field_length;
	private String remark;
	private String field_desc;
	private BigDecimal datatable_field_id;
	private String is_normal_index;
	private BigDecimal rowkey_seq;
	private BigDecimal datatable_id;
	private BigDecimal field_seq;
	private String is_rediskey;
	private String field_process;
	private BigDecimal redis_seq;
	private String field_en_name;
	private String is_solr_index;
	private String is_sortcolumns;
	private String field_type;
	private String process_para;
	private String is_rowkey;

	public String getIs_primary_key() { return is_primary_key; }
	public void setIs_primary_key(String is_primary_key) {
		if(is_primary_key==null) throw new BusinessException("Entity : DatatableFieldInfo.is_primary_key must not null!");
		this.is_primary_key = is_primary_key;
	}

	public String getField_cn_name() { return field_cn_name; }
	public void setField_cn_name(String field_cn_name) {
		if(field_cn_name==null) throw new BusinessException("Entity : DatatableFieldInfo.field_cn_name must not null!");
		this.field_cn_name = field_cn_name;
	}

	public String getIs_unique_index() { return is_unique_index; }
	public void setIs_unique_index(String is_unique_index) {
		if(is_unique_index==null) throw new BusinessException("Entity : DatatableFieldInfo.is_unique_index must not null!");
		this.is_unique_index = is_unique_index;
	}

	public String getIs_partition() { return is_partition; }
	public void setIs_partition(String is_partition) {
		if(is_partition==null) throw new BusinessException("Entity : DatatableFieldInfo.is_partition must not null!");
		this.is_partition = is_partition;
	}

	public String getIs_cb() { return is_cb; }
	public void setIs_cb(String is_cb) {
		if(is_cb==null) throw new BusinessException("Entity : DatatableFieldInfo.is_cb must not null!");
		this.is_cb = is_cb;
	}

	public String getField_length() { return field_length; }
	public void setField_length(String field_length) {
		if(field_length==null) addNullValueField("field_length");
		this.field_length = field_length;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getField_desc() { return field_desc; }
	public void setField_desc(String field_desc) {
		if(field_desc==null) addNullValueField("field_desc");
		this.field_desc = field_desc;
	}

	public BigDecimal getDatatable_field_id() { return datatable_field_id; }
	public void setDatatable_field_id(BigDecimal datatable_field_id) {
		if(datatable_field_id==null) throw new BusinessException("Entity : DatatableFieldInfo.datatable_field_id must not null!");
		this.datatable_field_id = datatable_field_id;
	}

	public String getIs_normal_index() { return is_normal_index; }
	public void setIs_normal_index(String is_normal_index) {
		if(is_normal_index==null) throw new BusinessException("Entity : DatatableFieldInfo.is_normal_index must not null!");
		this.is_normal_index = is_normal_index;
	}

	public BigDecimal getRowkey_seq() { return rowkey_seq; }
	public void setRowkey_seq(BigDecimal rowkey_seq) {
		if(rowkey_seq==null) throw new BusinessException("Entity : DatatableFieldInfo.rowkey_seq must not null!");
		this.rowkey_seq = rowkey_seq;
	}

	public BigDecimal getDatatable_id() { return datatable_id; }
	public void setDatatable_id(BigDecimal datatable_id) {
		if(datatable_id==null) throw new BusinessException("Entity : DatatableFieldInfo.datatable_id must not null!");
		this.datatable_id = datatable_id;
	}

	public BigDecimal getField_seq() { return field_seq; }
	public void setField_seq(BigDecimal field_seq) {
		if(field_seq==null) throw new BusinessException("Entity : DatatableFieldInfo.field_seq must not null!");
		this.field_seq = field_seq;
	}

	public String getIs_rediskey() { return is_rediskey; }
	public void setIs_rediskey(String is_rediskey) {
		if(is_rediskey==null) throw new BusinessException("Entity : DatatableFieldInfo.is_rediskey must not null!");
		this.is_rediskey = is_rediskey;
	}

	public String getField_process() { return field_process; }
	public void setField_process(String field_process) {
		if(field_process==null) throw new BusinessException("Entity : DatatableFieldInfo.field_process must not null!");
		this.field_process = field_process;
	}

	public BigDecimal getRedis_seq() { return redis_seq; }
	public void setRedis_seq(BigDecimal redis_seq) {
		if(redis_seq==null) throw new BusinessException("Entity : DatatableFieldInfo.redis_seq must not null!");
		this.redis_seq = redis_seq;
	}

	public String getField_en_name() { return field_en_name; }
	public void setField_en_name(String field_en_name) {
		if(field_en_name==null) throw new BusinessException("Entity : DatatableFieldInfo.field_en_name must not null!");
		this.field_en_name = field_en_name;
	}

	public String getIs_solr_index() { return is_solr_index; }
	public void setIs_solr_index(String is_solr_index) {
		if(is_solr_index==null) throw new BusinessException("Entity : DatatableFieldInfo.is_solr_index must not null!");
		this.is_solr_index = is_solr_index;
	}

	public String getIs_sortcolumns() { return is_sortcolumns; }
	public void setIs_sortcolumns(String is_sortcolumns) {
		if(is_sortcolumns==null) throw new BusinessException("Entity : DatatableFieldInfo.is_sortcolumns must not null!");
		this.is_sortcolumns = is_sortcolumns;
	}

	public String getField_type() { return field_type; }
	public void setField_type(String field_type) {
		if(field_type==null) throw new BusinessException("Entity : DatatableFieldInfo.field_type must not null!");
		this.field_type = field_type;
	}

	public String getProcess_para() { return process_para; }
	public void setProcess_para(String process_para) {
		if(process_para==null) addNullValueField("process_para");
		this.process_para = process_para;
	}

	public String getIs_rowkey() { return is_rowkey; }
	public void setIs_rowkey(String is_rowkey) {
		if(is_rowkey==null) throw new BusinessException("Entity : DatatableFieldInfo.is_rowkey must not null!");
		this.is_rowkey = is_rowkey;
	}

}