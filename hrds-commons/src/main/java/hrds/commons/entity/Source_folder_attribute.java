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
 * 源文件夹属性表
 */
@Table(tableName = "source_folder_attribute")
public class Source_folder_attribute extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "source_folder_attribute";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 源文件夹属性表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("folder_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="folder_id",value="文件夹编号:",dataType = Long.class,required = true)
	private Long folder_id;
	@DocBean(name ="folder_name",value="文件夹名:",dataType = String.class,required = true)
	private String folder_name;
	@DocBean(name ="folder_size",value="文件夹大小:",dataType = BigDecimal.class,required = true)
	private BigDecimal folder_size;
	@DocBean(name ="original_create_time",value="文件夹生成时间:",dataType = String.class,required = true)
	private String original_create_time;
	@DocBean(name ="storage_date",value="文件夹入库日期:",dataType = String.class,required = true)
	private String storage_date;
	@DocBean(name ="folders_in_no",value="文件夹内文件夹数量:",dataType = Long.class,required = true)
	private Long folders_in_no;
	@DocBean(name ="location_in_hdfs",value="hdfs中存储位置:",dataType = String.class,required = true)
	private String location_in_hdfs;
	@DocBean(name ="original_create_date",value="文件夹生产日期:",dataType = String.class,required = true)
	private String original_create_date;
	@DocBean(name ="storage_time",value="文件夹入库时间:",dataType = String.class,required = true)
	private String storage_time;
	@DocBean(name ="super_id",value="文件夹编号:",dataType = Long.class,required = false)
	private Long super_id;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = true)
	private Long source_id;

	/** 取得：文件夹编号 */
	public Long getFolder_id(){
		return folder_id;
	}
	/** 设置：文件夹编号 */
	public void setFolder_id(Long folder_id){
		this.folder_id=folder_id;
	}
	/** 设置：文件夹编号 */
	public void setFolder_id(String folder_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(folder_id)){
			this.folder_id=new Long(folder_id);
		}
	}
	/** 取得：文件夹名 */
	public String getFolder_name(){
		return folder_name;
	}
	/** 设置：文件夹名 */
	public void setFolder_name(String folder_name){
		this.folder_name=folder_name;
	}
	/** 取得：文件夹大小 */
	public BigDecimal getFolder_size(){
		return folder_size;
	}
	/** 设置：文件夹大小 */
	public void setFolder_size(BigDecimal folder_size){
		this.folder_size=folder_size;
	}
	/** 设置：文件夹大小 */
	public void setFolder_size(String folder_size){
		if(!fd.ng.core.utils.StringUtil.isEmpty(folder_size)){
			this.folder_size=new BigDecimal(folder_size);
		}
	}
	/** 取得：文件夹生成时间 */
	public String getOriginal_create_time(){
		return original_create_time;
	}
	/** 设置：文件夹生成时间 */
	public void setOriginal_create_time(String original_create_time){
		this.original_create_time=original_create_time;
	}
	/** 取得：文件夹入库日期 */
	public String getStorage_date(){
		return storage_date;
	}
	/** 设置：文件夹入库日期 */
	public void setStorage_date(String storage_date){
		this.storage_date=storage_date;
	}
	/** 取得：文件夹内文件夹数量 */
	public Long getFolders_in_no(){
		return folders_in_no;
	}
	/** 设置：文件夹内文件夹数量 */
	public void setFolders_in_no(Long folders_in_no){
		this.folders_in_no=folders_in_no;
	}
	/** 设置：文件夹内文件夹数量 */
	public void setFolders_in_no(String folders_in_no){
		if(!fd.ng.core.utils.StringUtil.isEmpty(folders_in_no)){
			this.folders_in_no=new Long(folders_in_no);
		}
	}
	/** 取得：hdfs中存储位置 */
	public String getLocation_in_hdfs(){
		return location_in_hdfs;
	}
	/** 设置：hdfs中存储位置 */
	public void setLocation_in_hdfs(String location_in_hdfs){
		this.location_in_hdfs=location_in_hdfs;
	}
	/** 取得：文件夹生产日期 */
	public String getOriginal_create_date(){
		return original_create_date;
	}
	/** 设置：文件夹生产日期 */
	public void setOriginal_create_date(String original_create_date){
		this.original_create_date=original_create_date;
	}
	/** 取得：文件夹入库时间 */
	public String getStorage_time(){
		return storage_time;
	}
	/** 设置：文件夹入库时间 */
	public void setStorage_time(String storage_time){
		this.storage_time=storage_time;
	}
	/** 取得：文件夹编号 */
	public Long getSuper_id(){
		return super_id;
	}
	/** 设置：文件夹编号 */
	public void setSuper_id(Long super_id){
		this.super_id=super_id;
	}
	/** 设置：文件夹编号 */
	public void setSuper_id(String super_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(super_id)){
			this.super_id=new Long(super_id);
		}
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
}
