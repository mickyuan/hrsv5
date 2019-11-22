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
 * 信号文件入库信息
 */
@Table(tableName = "signal_file")
public class Signal_file extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "signal_file";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 信号文件入库信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("signal_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="signal_id",value="信号id:",dataType = Long.class,required = true)
	private Long signal_id;
	@DocBean(name ="is_into_hbase",value="是否入hbase(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_into_hbase;
	@DocBean(name ="is_into_hive",value="是否入hive(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_into_hive;
	@DocBean(name ="is_fullindex",value="是否创建全文索引(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_fullindex;
	@DocBean(name ="file_format",value="文件格式:",dataType = String.class,required = true)
	private String file_format;
	@DocBean(name ="is_compression",value="Hbase是使用压缩(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_compression;
	@DocBean(name ="is_mpp",value="是否为MPP(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_mpp;
	@DocBean(name ="table_type",value="是内部表还是外部表(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String table_type;
	@DocBean(name ="is_solr_hbase",value="是否使用solrOnHbase(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_solr_hbase;
	@DocBean(name ="database_id",value="数据库设置id:",dataType = Long.class,required = true)
	private Long database_id;
	@DocBean(name ="is_cbd",value="是否使用carbondata(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_cbd;

	/** 取得：信号id */
	public Long getSignal_id(){
		return signal_id;
	}
	/** 设置：信号id */
	public void setSignal_id(Long signal_id){
		this.signal_id=signal_id;
	}
	/** 设置：信号id */
	public void setSignal_id(String signal_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(signal_id)){
			this.signal_id=new Long(signal_id);
		}
	}
	/** 取得：是否入hbase */
	public String getIs_into_hbase(){
		return is_into_hbase;
	}
	/** 设置：是否入hbase */
	public void setIs_into_hbase(String is_into_hbase){
		this.is_into_hbase=is_into_hbase;
	}
	/** 取得：是否入hive */
	public String getIs_into_hive(){
		return is_into_hive;
	}
	/** 设置：是否入hive */
	public void setIs_into_hive(String is_into_hive){
		this.is_into_hive=is_into_hive;
	}
	/** 取得：是否创建全文索引 */
	public String getIs_fullindex(){
		return is_fullindex;
	}
	/** 设置：是否创建全文索引 */
	public void setIs_fullindex(String is_fullindex){
		this.is_fullindex=is_fullindex;
	}
	/** 取得：文件格式 */
	public String getFile_format(){
		return file_format;
	}
	/** 设置：文件格式 */
	public void setFile_format(String file_format){
		this.file_format=file_format;
	}
	/** 取得：Hbase是使用压缩 */
	public String getIs_compression(){
		return is_compression;
	}
	/** 设置：Hbase是使用压缩 */
	public void setIs_compression(String is_compression){
		this.is_compression=is_compression;
	}
	/** 取得：是否为MPP */
	public String getIs_mpp(){
		return is_mpp;
	}
	/** 设置：是否为MPP */
	public void setIs_mpp(String is_mpp){
		this.is_mpp=is_mpp;
	}
	/** 取得：是内部表还是外部表 */
	public String getTable_type(){
		return table_type;
	}
	/** 设置：是内部表还是外部表 */
	public void setTable_type(String table_type){
		this.table_type=table_type;
	}
	/** 取得：是否使用solrOnHbase */
	public String getIs_solr_hbase(){
		return is_solr_hbase;
	}
	/** 设置：是否使用solrOnHbase */
	public void setIs_solr_hbase(String is_solr_hbase){
		this.is_solr_hbase=is_solr_hbase;
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
	/** 取得：是否使用carbondata */
	public String getIs_cbd(){
		return is_cbd;
	}
	/** 设置：是否使用carbondata */
	public void setIs_cbd(String is_cbd){
		this.is_cbd=is_cbd;
	}
}
