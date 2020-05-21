package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 数据表信息
 */
@Table(tableName = "dm_datatable")
public class Dm_datatable extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dm_datatable";
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
	@DocBean(name ="datatable_id",value="数据表id:",dataType = Long.class,required = true)
	private Long datatable_id;
	@DocBean(name ="datatable_cn_name",value="数据表中文名称:",dataType = String.class,required = true)
	private String datatable_cn_name;
	@DocBean(name ="datatable_en_name",value="数据表英文名称:",dataType = String.class,required = true)
	private String datatable_en_name;
	@DocBean(name ="datatable_desc",value="数据表描述:",dataType = String.class,required = false)
	private String datatable_desc;
	@DocBean(name ="datatable_create_date",value="数据表创建日期:",dataType = String.class,required = true)
	private String datatable_create_date;
	@DocBean(name ="datatable_create_time",value="数据表创建时间:",dataType = String.class,required = true)
	private String datatable_create_time;
	@DocBean(name ="datatable_lifecycle",value="数据表的生命周期(TableLifeCycle):1-永久<YongJiu> 2-临时<LinShi> ",dataType = String.class,required = true)
	private String datatable_lifecycle;
	@DocBean(name ="datatable_due_date",value="数据表到期日期:",dataType = String.class,required = true)
	private String datatable_due_date;
	@DocBean(name ="soruce_size",value="资源大小:",dataType = BigDecimal.class,required = true)
	private BigDecimal soruce_size;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="ddlc_date",value="DDL最后变更日期:",dataType = String.class,required = true)
	private String ddlc_date;
	@DocBean(name ="ddlc_time",value="DDL最后变更时间:",dataType = String.class,required = true)
	private String ddlc_time;
	@DocBean(name ="datac_date",value="数据最后变更日期:",dataType = String.class,required = true)
	private String datac_date;
	@DocBean(name ="datac_time",value="数据最后变更时间:",dataType = String.class,required = true)
	private String datac_time;
	@DocBean(name ="etl_date",value="跑批日期:",dataType = String.class,required = true)
	private String etl_date;
	@DocBean(name ="sql_engine",value="sql执行引擎(SqlEngine):1-JDBC<JDBC> 2-SPARK<SPARK> 3-默认<MOREN> ",dataType = String.class,required = false)
	private String sql_engine;
	@DocBean(name ="storage_type",value="进数方式(StorageType):1-增量<ZengLiang> 2-追加<ZhuiJia> 3-替换<TiHuan> ",dataType = String.class,required = true)
	private String storage_type;
	@DocBean(name ="data_mart_id",value="数据集市id:",dataType = Long.class,required = false)
	private Long data_mart_id;
	@DocBean(name ="category_id",value="集市分类id:",dataType = Long.class,required = true)
	private Long category_id;
	@DocBean(name ="table_storage",value="数据表存储方式(TableStorage):0-数据表<ShuJuBiao> 1-数据视图<ShuJuShiTu> ",dataType = String.class,required = true)
	private String table_storage;
	@DocBean(name ="repeat_flag",value="集市表是否可以重复使用(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String repeat_flag;

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
	/** 取得：数据表到期日期 */
	public String getDatatable_due_date(){
		return datatable_due_date;
	}
	/** 设置：数据表到期日期 */
	public void setDatatable_due_date(String datatable_due_date){
		this.datatable_due_date=datatable_due_date;
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
	/** 取得：进数方式 */
	public String getStorage_type(){
		return storage_type;
	}
	/** 设置：进数方式 */
	public void setStorage_type(String storage_type){
		this.storage_type=storage_type;
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
	/** 取得：集市分类id */
	public Long getCategory_id(){
		return category_id;
	}
	/** 设置：集市分类id */
	public void setCategory_id(Long category_id){
		this.category_id=category_id;
	}
	/** 设置：集市分类id */
	public void setCategory_id(String category_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(category_id)){
			this.category_id=new Long(category_id);
		}
	}
	/** 取得：数据表存储方式 */
	public String getTable_storage(){
		return table_storage;
	}
	/** 设置：数据表存储方式 */
	public void setTable_storage(String table_storage){
		this.table_storage=table_storage;
	}
	/** 取得：集市表是否可以重复使用 */
	public String getRepeat_flag(){
		return repeat_flag;
	}
	/** 设置：集市表是否可以重复使用 */
	public void setRepeat_flag(String repeat_flag){
		this.repeat_flag=repeat_flag;
	}
}
