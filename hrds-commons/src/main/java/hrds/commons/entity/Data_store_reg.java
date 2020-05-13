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
 * 数据存储登记
 */
@Table(tableName = "data_store_reg")
public class Data_store_reg extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_store_reg";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据存储登记 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("file_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="file_id",value="表文件ID:",dataType = String.class,required = true)
	private String file_id;
	@DocBean(name ="original_name",value="原始表中文名称:",dataType = String.class,required = true)
	private String original_name;
	@DocBean(name ="original_update_date",value="原文件最后修改日期:",dataType = String.class,required = true)
	private String original_update_date;
	@DocBean(name ="hyren_name",value="系统内对应表名:",dataType = String.class,required = true)
	private String hyren_name;
	@DocBean(name ="storage_date",value="入库日期:",dataType = String.class,required = true)
	private String storage_date;
	@DocBean(name ="file_size",value="文件大小:",dataType = Long.class,required = true)
	private Long file_size;
	@DocBean(name ="original_update_time",value="原文件最后修改时间:",dataType = String.class,required = true)
	private String original_update_time;
	@DocBean(name ="storage_time",value="入库时间:",dataType = String.class,required = true)
	private String storage_time;
	@DocBean(name ="table_name",value="采集的原始表名:",dataType = String.class,required = false)
	private String table_name;
	@DocBean(name ="collect_type",value="采集类型(AgentType):1-数据库Agent<ShuJuKu> 2-文件系统Agent<WenJianXiTong> 3-FtpAgent<FTP> 4-数据文件Agent<DBWenJian> 5-对象Agent<DuiXiang> ",dataType = String.class,required = true)
	private String collect_type;
	@DocBean(name ="meta_info",value="META元信息:",dataType = String.class,required = false)
	private String meta_info;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = true)
	private Long source_id;
	@DocBean(name ="database_id",value="数据库设置id:",dataType = Long.class,required = true)
	private Long database_id;
	@DocBean(name ="table_id",value="表名ID:",dataType = Long.class,required = true)
	private Long table_id;

	/** 取得：表文件ID */
	public String getFile_id(){
		return file_id;
	}
	/** 设置：表文件ID */
	public void setFile_id(String file_id){
		this.file_id=file_id;
	}
	/** 取得：原始表中文名称 */
	public String getOriginal_name(){
		return original_name;
	}
	/** 设置：原始表中文名称 */
	public void setOriginal_name(String original_name){
		this.original_name=original_name;
	}
	/** 取得：原文件最后修改日期 */
	public String getOriginal_update_date(){
		return original_update_date;
	}
	/** 设置：原文件最后修改日期 */
	public void setOriginal_update_date(String original_update_date){
		this.original_update_date=original_update_date;
	}
	/** 取得：系统内对应表名 */
	public String getHyren_name(){
		return hyren_name;
	}
	/** 设置：系统内对应表名 */
	public void setHyren_name(String hyren_name){
		this.hyren_name=hyren_name;
	}
	/** 取得：入库日期 */
	public String getStorage_date(){
		return storage_date;
	}
	/** 设置：入库日期 */
	public void setStorage_date(String storage_date){
		this.storage_date=storage_date;
	}
	/** 取得：文件大小 */
	public Long getFile_size(){
		return file_size;
	}
	/** 设置：文件大小 */
	public void setFile_size(Long file_size){
		this.file_size=file_size;
	}
	/** 设置：文件大小 */
	public void setFile_size(String file_size){
		if(!fd.ng.core.utils.StringUtil.isEmpty(file_size)){
			this.file_size=new Long(file_size);
		}
	}
	/** 取得：原文件最后修改时间 */
	public String getOriginal_update_time(){
		return original_update_time;
	}
	/** 设置：原文件最后修改时间 */
	public void setOriginal_update_time(String original_update_time){
		this.original_update_time=original_update_time;
	}
	/** 取得：入库时间 */
	public String getStorage_time(){
		return storage_time;
	}
	/** 设置：入库时间 */
	public void setStorage_time(String storage_time){
		this.storage_time=storage_time;
	}
	/** 取得：采集的原始表名 */
	public String getTable_name(){
		return table_name;
	}
	/** 设置：采集的原始表名 */
	public void setTable_name(String table_name){
		this.table_name=table_name;
	}
	/** 取得：采集类型 */
	public String getCollect_type(){
		return collect_type;
	}
	/** 设置：采集类型 */
	public void setCollect_type(String collect_type){
		this.collect_type=collect_type;
	}
	/** 取得：META元信息 */
	public String getMeta_info(){
		return meta_info;
	}
	/** 设置：META元信息 */
	public void setMeta_info(String meta_info){
		this.meta_info=meta_info;
	}
	/** 取得：Agent_id */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
	/** 取得：数据源ID */
	public Long getSource_id(){
		return source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(Long source_id){
		this.source_id=source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(String source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(source_id)){
			this.source_id=new Long(source_id);
		}
	}
	/** 取得：数据库设置id */
	public Long getDatabase_id(){
		return database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(Long database_id){
		this.database_id=database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(String database_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(database_id)){
			this.database_id=new Long(database_id);
		}
	}
	/** 取得：表名ID */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
	}
}
