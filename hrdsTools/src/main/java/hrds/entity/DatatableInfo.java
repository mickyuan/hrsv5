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
@Table(tableName = "datatable_info")
public class DatatableInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datatable_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datatable_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String is_kv_db;
	private String is_append;
	private String datatable_cn_name;
	private String is_carbondata_db;
	private String is_partition;
	private String rowkey_separator;
	private String datac_date;
	private String is_elk_db;
	private String solrbase_success;
	private String remark;
	private String is_solr_db;
	private String is_solr_hbase;
	private String etl_date;
	private String datac_time;
	private String datatable_en_name;
	private String datatable_due_date;
	private BigDecimal data_mart_id;
	private String datatype;
	private String solr_success;
	private String datatable_lifecycle;
	private String datatable_create_time;
	private String is_current_cluster;
	private String ddlc_date;
	private String carbondata_success;
	private String hy_success;
	private String ddlc_time;
	private String sql_engine;
	private String kv_success;
	private String pre_partition;
	private String is_hyren_db;
	private String exfile_success;
	private String datatable_create_date;
	private BigDecimal datatable_id;
	private BigDecimal soruce_size;
	private String datatable_desc;
	private String is_data_file;
	private String elk_success;

	public String getIs_kv_db() { return is_kv_db; }
	public void setIs_kv_db(String is_kv_db) {
		if(is_kv_db==null) throw new BusinessException("Entity : DatatableInfo.is_kv_db must not null!");
		this.is_kv_db = is_kv_db;
	}

	public String getIs_append() { return is_append; }
	public void setIs_append(String is_append) {
		if(is_append==null) throw new BusinessException("Entity : DatatableInfo.is_append must not null!");
		this.is_append = is_append;
	}

	public String getDatatable_cn_name() { return datatable_cn_name; }
	public void setDatatable_cn_name(String datatable_cn_name) {
		if(datatable_cn_name==null) throw new BusinessException("Entity : DatatableInfo.datatable_cn_name must not null!");
		this.datatable_cn_name = datatable_cn_name;
	}

	public String getIs_carbondata_db() { return is_carbondata_db; }
	public void setIs_carbondata_db(String is_carbondata_db) {
		if(is_carbondata_db==null) throw new BusinessException("Entity : DatatableInfo.is_carbondata_db must not null!");
		this.is_carbondata_db = is_carbondata_db;
	}

	public String getIs_partition() { return is_partition; }
	public void setIs_partition(String is_partition) {
		if(is_partition==null) throw new BusinessException("Entity : DatatableInfo.is_partition must not null!");
		this.is_partition = is_partition;
	}

	public String getRowkey_separator() { return rowkey_separator; }
	public void setRowkey_separator(String rowkey_separator) {
		if(rowkey_separator==null) addNullValueField("rowkey_separator");
		this.rowkey_separator = rowkey_separator;
	}

	public String getDatac_date() { return datac_date; }
	public void setDatac_date(String datac_date) {
		if(datac_date==null) throw new BusinessException("Entity : DatatableInfo.datac_date must not null!");
		this.datac_date = datac_date;
	}

	public String getIs_elk_db() { return is_elk_db; }
	public void setIs_elk_db(String is_elk_db) {
		if(is_elk_db==null) throw new BusinessException("Entity : DatatableInfo.is_elk_db must not null!");
		this.is_elk_db = is_elk_db;
	}

	public String getSolrbase_success() { return solrbase_success; }
	public void setSolrbase_success(String solrbase_success) {
		if(solrbase_success==null) addNullValueField("solrbase_success");
		this.solrbase_success = solrbase_success;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_solr_db() { return is_solr_db; }
	public void setIs_solr_db(String is_solr_db) {
		if(is_solr_db==null) throw new BusinessException("Entity : DatatableInfo.is_solr_db must not null!");
		this.is_solr_db = is_solr_db;
	}

	public String getIs_solr_hbase() { return is_solr_hbase; }
	public void setIs_solr_hbase(String is_solr_hbase) {
		if(is_solr_hbase==null) throw new BusinessException("Entity : DatatableInfo.is_solr_hbase must not null!");
		this.is_solr_hbase = is_solr_hbase;
	}

	public String getEtl_date() { return etl_date; }
	public void setEtl_date(String etl_date) {
		if(etl_date==null) throw new BusinessException("Entity : DatatableInfo.etl_date must not null!");
		this.etl_date = etl_date;
	}

	public String getDatac_time() { return datac_time; }
	public void setDatac_time(String datac_time) {
		if(datac_time==null) throw new BusinessException("Entity : DatatableInfo.datac_time must not null!");
		this.datac_time = datac_time;
	}

	public String getDatatable_en_name() { return datatable_en_name; }
	public void setDatatable_en_name(String datatable_en_name) {
		if(datatable_en_name==null) throw new BusinessException("Entity : DatatableInfo.datatable_en_name must not null!");
		this.datatable_en_name = datatable_en_name;
	}

	public String getDatatable_due_date() { return datatable_due_date; }
	public void setDatatable_due_date(String datatable_due_date) {
		if(datatable_due_date==null) throw new BusinessException("Entity : DatatableInfo.datatable_due_date must not null!");
		this.datatable_due_date = datatable_due_date;
	}

	public BigDecimal getData_mart_id() { return data_mart_id; }
	public void setData_mart_id(BigDecimal data_mart_id) {
		if(data_mart_id==null) throw new BusinessException("Entity : DatatableInfo.data_mart_id must not null!");
		this.data_mart_id = data_mart_id;
	}

	public String getDatatype() { return datatype; }
	public void setDatatype(String datatype) {
		if(datatype==null) throw new BusinessException("Entity : DatatableInfo.datatype must not null!");
		this.datatype = datatype;
	}

	public String getSolr_success() { return solr_success; }
	public void setSolr_success(String solr_success) {
		if(solr_success==null) addNullValueField("solr_success");
		this.solr_success = solr_success;
	}

	public String getDatatable_lifecycle() { return datatable_lifecycle; }
	public void setDatatable_lifecycle(String datatable_lifecycle) {
		if(datatable_lifecycle==null) throw new BusinessException("Entity : DatatableInfo.datatable_lifecycle must not null!");
		this.datatable_lifecycle = datatable_lifecycle;
	}

	public String getDatatable_create_time() { return datatable_create_time; }
	public void setDatatable_create_time(String datatable_create_time) {
		if(datatable_create_time==null) throw new BusinessException("Entity : DatatableInfo.datatable_create_time must not null!");
		this.datatable_create_time = datatable_create_time;
	}

	public String getIs_current_cluster() { return is_current_cluster; }
	public void setIs_current_cluster(String is_current_cluster) {
		if(is_current_cluster==null) throw new BusinessException("Entity : DatatableInfo.is_current_cluster must not null!");
		this.is_current_cluster = is_current_cluster;
	}

	public String getDdlc_date() { return ddlc_date; }
	public void setDdlc_date(String ddlc_date) {
		if(ddlc_date==null) throw new BusinessException("Entity : DatatableInfo.ddlc_date must not null!");
		this.ddlc_date = ddlc_date;
	}

	public String getCarbondata_success() { return carbondata_success; }
	public void setCarbondata_success(String carbondata_success) {
		if(carbondata_success==null) addNullValueField("carbondata_success");
		this.carbondata_success = carbondata_success;
	}

	public String getHy_success() { return hy_success; }
	public void setHy_success(String hy_success) {
		if(hy_success==null) addNullValueField("hy_success");
		this.hy_success = hy_success;
	}

	public String getDdlc_time() { return ddlc_time; }
	public void setDdlc_time(String ddlc_time) {
		if(ddlc_time==null) throw new BusinessException("Entity : DatatableInfo.ddlc_time must not null!");
		this.ddlc_time = ddlc_time;
	}

	public String getSql_engine() { return sql_engine; }
	public void setSql_engine(String sql_engine) {
		if(sql_engine==null) addNullValueField("sql_engine");
		this.sql_engine = sql_engine;
	}

	public String getKv_success() { return kv_success; }
	public void setKv_success(String kv_success) {
		if(kv_success==null) addNullValueField("kv_success");
		this.kv_success = kv_success;
	}

	public String getPre_partition() { return pre_partition; }
	public void setPre_partition(String pre_partition) {
		if(pre_partition==null) addNullValueField("pre_partition");
		this.pre_partition = pre_partition;
	}

	public String getIs_hyren_db() { return is_hyren_db; }
	public void setIs_hyren_db(String is_hyren_db) {
		if(is_hyren_db==null) throw new BusinessException("Entity : DatatableInfo.is_hyren_db must not null!");
		this.is_hyren_db = is_hyren_db;
	}

	public String getExfile_success() { return exfile_success; }
	public void setExfile_success(String exfile_success) {
		if(exfile_success==null) addNullValueField("exfile_success");
		this.exfile_success = exfile_success;
	}

	public String getDatatable_create_date() { return datatable_create_date; }
	public void setDatatable_create_date(String datatable_create_date) {
		if(datatable_create_date==null) throw new BusinessException("Entity : DatatableInfo.datatable_create_date must not null!");
		this.datatable_create_date = datatable_create_date;
	}

	public BigDecimal getDatatable_id() { return datatable_id; }
	public void setDatatable_id(BigDecimal datatable_id) {
		if(datatable_id==null) throw new BusinessException("Entity : DatatableInfo.datatable_id must not null!");
		this.datatable_id = datatable_id;
	}

	public BigDecimal getSoruce_size() { return soruce_size; }
	public void setSoruce_size(BigDecimal soruce_size) {
		if(soruce_size==null) throw new BusinessException("Entity : DatatableInfo.soruce_size must not null!");
		this.soruce_size = soruce_size;
	}

	public String getDatatable_desc() { return datatable_desc; }
	public void setDatatable_desc(String datatable_desc) {
		if(datatable_desc==null) addNullValueField("datatable_desc");
		this.datatable_desc = datatable_desc;
	}

	public String getIs_data_file() { return is_data_file; }
	public void setIs_data_file(String is_data_file) {
		if(is_data_file==null) throw new BusinessException("Entity : DatatableInfo.is_data_file must not null!");
		this.is_data_file = is_data_file;
	}

	public String getElk_success() { return elk_success; }
	public void setElk_success(String elk_success) {
		if(elk_success==null) addNullValueField("elk_success");
		this.elk_success = elk_success;
	}

}