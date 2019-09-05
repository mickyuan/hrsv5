package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 数据表信息
 */
@Table(tableName = "datatable_info")
public class Datatable_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datatable_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据表信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datatable_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long datatable_id; //数据表id
	private String datatable_cn_name; //数据表中文名称
	private String datatable_en_name; //数据表英文名称
	private String datatable_desc; //数据表描述
	private String datatable_create_date; //数据表创建日期
	private String datatable_create_time; //数据表创建时间
	private String datatable_lifecycle; //数据表的生命周期
	private String is_partition; //是否分区
	private String is_hyren_db; //是否为HyrenDB
	private String datatable_due_date; //数据表到期日期
	private Long data_mart_id; //数据集市id
	private BigDecimal soruce_size; //资源大小
	private String remark; //备注
	private String ddlc_date; //DDL最后变更日期
	private String ddlc_time; //DDL最后变更时间
	private String datac_date; //数据最后变更日期
	private String datac_time; //数据最后变更时间
	private String etl_date; //跑批日期
	private String sql_engine; //sql执行引擎
	private String is_kv_db; //是否为KeyValueDB
	private String is_solr_db; //是否为solrDB
	private String rowkey_separator; //rowkey分隔符
	private String is_elk_db; //是否为elkDB
	private String hy_success; //HyrenDB是否成功
	private String kv_success; //KeyValueDB是否成功
	private String solr_success; //solrDB是否成功
	private String elk_success; //elk是否成功
	private String datatype; //存储类型
	private String is_current_cluster; //是否是本集群表
	private String pre_partition; //预分区
	private String is_append; //数据存储方式
	private String is_solr_hbase; //是否solrOnHbase
	private String solrbase_success; //solrOnHbase是否完成
	private String is_carbondata_db; //是否入carbondata
	private String carbondata_success; //carbondata是否成功
	private String is_data_file; //是否是数据文件
	private String exfile_success; //导出文件是否成功

	/** 取得：数据表id */
	public Long getDatatable_id(){
		return datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(Long datatable_id){
		this.datatable_id=datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(String datatable_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datatable_id)){
			this.datatable_id=new Long(datatable_id);
		}
	}
	/** 取得：数据表中文名称 */
	public String getDatatable_cn_name(){
		return datatable_cn_name;
	}
	/** 设置：数据表中文名称 */
	public void setDatatable_cn_name(String datatable_cn_name){
		this.datatable_cn_name=datatable_cn_name;
	}
	/** 取得：数据表英文名称 */
	public String getDatatable_en_name(){
		return datatable_en_name;
	}
	/** 设置：数据表英文名称 */
	public void setDatatable_en_name(String datatable_en_name){
		this.datatable_en_name=datatable_en_name;
	}
	/** 取得：数据表描述 */
	public String getDatatable_desc(){
		return datatable_desc;
	}
	/** 设置：数据表描述 */
	public void setDatatable_desc(String datatable_desc){
		this.datatable_desc=datatable_desc;
	}
	/** 取得：数据表创建日期 */
	public String getDatatable_create_date(){
		return datatable_create_date;
	}
	/** 设置：数据表创建日期 */
	public void setDatatable_create_date(String datatable_create_date){
		this.datatable_create_date=datatable_create_date;
	}
	/** 取得：数据表创建时间 */
	public String getDatatable_create_time(){
		return datatable_create_time;
	}
	/** 设置：数据表创建时间 */
	public void setDatatable_create_time(String datatable_create_time){
		this.datatable_create_time=datatable_create_time;
	}
	/** 取得：数据表的生命周期 */
	public String getDatatable_lifecycle(){
		return datatable_lifecycle;
	}
	/** 设置：数据表的生命周期 */
	public void setDatatable_lifecycle(String datatable_lifecycle){
		this.datatable_lifecycle=datatable_lifecycle;
	}
	/** 取得：是否分区 */
	public String getIs_partition(){
		return is_partition;
	}
	/** 设置：是否分区 */
	public void setIs_partition(String is_partition){
		this.is_partition=is_partition;
	}
	/** 取得：是否为HyrenDB */
	public String getIs_hyren_db(){
		return is_hyren_db;
	}
	/** 设置：是否为HyrenDB */
	public void setIs_hyren_db(String is_hyren_db){
		this.is_hyren_db=is_hyren_db;
	}
	/** 取得：数据表到期日期 */
	public String getDatatable_due_date(){
		return datatable_due_date;
	}
	/** 设置：数据表到期日期 */
	public void setDatatable_due_date(String datatable_due_date){
		this.datatable_due_date=datatable_due_date;
	}
	/** 取得：数据集市id */
	public Long getData_mart_id(){
		return data_mart_id;
	}
	/** 设置：数据集市id */
	public void setData_mart_id(Long data_mart_id){
		this.data_mart_id=data_mart_id;
	}
	/** 设置：数据集市id */
	public void setData_mart_id(String data_mart_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(data_mart_id)){
			this.data_mart_id=new Long(data_mart_id);
		}
	}
	/** 取得：资源大小 */
	public BigDecimal getSoruce_size(){
		return soruce_size;
	}
	/** 设置：资源大小 */
	public void setSoruce_size(BigDecimal soruce_size){
		this.soruce_size=soruce_size;
	}
	/** 设置：资源大小 */
	public void setSoruce_size(String soruce_size){
		if(!fd.ng.core.utils.StringUtil.isEmpty(soruce_size)){
			this.soruce_size=new BigDecimal(soruce_size);
		}
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：DDL最后变更日期 */
	public String getDdlc_date(){
		return ddlc_date;
	}
	/** 设置：DDL最后变更日期 */
	public void setDdlc_date(String ddlc_date){
		this.ddlc_date=ddlc_date;
	}
	/** 取得：DDL最后变更时间 */
	public String getDdlc_time(){
		return ddlc_time;
	}
	/** 设置：DDL最后变更时间 */
	public void setDdlc_time(String ddlc_time){
		this.ddlc_time=ddlc_time;
	}
	/** 取得：数据最后变更日期 */
	public String getDatac_date(){
		return datac_date;
	}
	/** 设置：数据最后变更日期 */
	public void setDatac_date(String datac_date){
		this.datac_date=datac_date;
	}
	/** 取得：数据最后变更时间 */
	public String getDatac_time(){
		return datac_time;
	}
	/** 设置：数据最后变更时间 */
	public void setDatac_time(String datac_time){
		this.datac_time=datac_time;
	}
	/** 取得：跑批日期 */
	public String getEtl_date(){
		return etl_date;
	}
	/** 设置：跑批日期 */
	public void setEtl_date(String etl_date){
		this.etl_date=etl_date;
	}
	/** 取得：sql执行引擎 */
	public String getSql_engine(){
		return sql_engine;
	}
	/** 设置：sql执行引擎 */
	public void setSql_engine(String sql_engine){
		this.sql_engine=sql_engine;
	}
	/** 取得：是否为KeyValueDB */
	public String getIs_kv_db(){
		return is_kv_db;
	}
	/** 设置：是否为KeyValueDB */
	public void setIs_kv_db(String is_kv_db){
		this.is_kv_db=is_kv_db;
	}
	/** 取得：是否为solrDB */
	public String getIs_solr_db(){
		return is_solr_db;
	}
	/** 设置：是否为solrDB */
	public void setIs_solr_db(String is_solr_db){
		this.is_solr_db=is_solr_db;
	}
	/** 取得：rowkey分隔符 */
	public String getRowkey_separator(){
		return rowkey_separator;
	}
	/** 设置：rowkey分隔符 */
	public void setRowkey_separator(String rowkey_separator){
		this.rowkey_separator=rowkey_separator;
	}
	/** 取得：是否为elkDB */
	public String getIs_elk_db(){
		return is_elk_db;
	}
	/** 设置：是否为elkDB */
	public void setIs_elk_db(String is_elk_db){
		this.is_elk_db=is_elk_db;
	}
	/** 取得：HyrenDB是否成功 */
	public String getHy_success(){
		return hy_success;
	}
	/** 设置：HyrenDB是否成功 */
	public void setHy_success(String hy_success){
		this.hy_success=hy_success;
	}
	/** 取得：KeyValueDB是否成功 */
	public String getKv_success(){
		return kv_success;
	}
	/** 设置：KeyValueDB是否成功 */
	public void setKv_success(String kv_success){
		this.kv_success=kv_success;
	}
	/** 取得：solrDB是否成功 */
	public String getSolr_success(){
		return solr_success;
	}
	/** 设置：solrDB是否成功 */
	public void setSolr_success(String solr_success){
		this.solr_success=solr_success;
	}
	/** 取得：elk是否成功 */
	public String getElk_success(){
		return elk_success;
	}
	/** 设置：elk是否成功 */
	public void setElk_success(String elk_success){
		this.elk_success=elk_success;
	}
	/** 取得：存储类型 */
	public String getDatatype(){
		return datatype;
	}
	/** 设置：存储类型 */
	public void setDatatype(String datatype){
		this.datatype=datatype;
	}
	/** 取得：是否是本集群表 */
	public String getIs_current_cluster(){
		return is_current_cluster;
	}
	/** 设置：是否是本集群表 */
	public void setIs_current_cluster(String is_current_cluster){
		this.is_current_cluster=is_current_cluster;
	}
	/** 取得：预分区 */
	public String getPre_partition(){
		return pre_partition;
	}
	/** 设置：预分区 */
	public void setPre_partition(String pre_partition){
		this.pre_partition=pre_partition;
	}
	/** 取得：数据存储方式 */
	public String getIs_append(){
		return is_append;
	}
	/** 设置：数据存储方式 */
	public void setIs_append(String is_append){
		this.is_append=is_append;
	}
	/** 取得：是否solrOnHbase */
	public String getIs_solr_hbase(){
		return is_solr_hbase;
	}
	/** 设置：是否solrOnHbase */
	public void setIs_solr_hbase(String is_solr_hbase){
		this.is_solr_hbase=is_solr_hbase;
	}
	/** 取得：solrOnHbase是否完成 */
	public String getSolrbase_success(){
		return solrbase_success;
	}
	/** 设置：solrOnHbase是否完成 */
	public void setSolrbase_success(String solrbase_success){
		this.solrbase_success=solrbase_success;
	}
	/** 取得：是否入carbondata */
	public String getIs_carbondata_db(){
		return is_carbondata_db;
	}
	/** 设置：是否入carbondata */
	public void setIs_carbondata_db(String is_carbondata_db){
		this.is_carbondata_db=is_carbondata_db;
	}
	/** 取得：carbondata是否成功 */
	public String getCarbondata_success(){
		return carbondata_success;
	}
	/** 设置：carbondata是否成功 */
	public void setCarbondata_success(String carbondata_success){
		this.carbondata_success=carbondata_success;
	}
	/** 取得：是否是数据文件 */
	public String getIs_data_file(){
		return is_data_file;
	}
	/** 设置：是否是数据文件 */
	public void setIs_data_file(String is_data_file){
		this.is_data_file=is_data_file;
	}
	/** 取得：导出文件是否成功 */
	public String getExfile_success(){
		return exfile_success;
	}
	/** 设置：导出文件是否成功 */
	public void setExfile_success(String exfile_success){
		this.exfile_success=exfile_success;
	}
}
