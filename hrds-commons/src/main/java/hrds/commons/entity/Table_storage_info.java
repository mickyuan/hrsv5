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
 * 表存储信息
 */
@Table(tableName = "table_storage_info")
public class Table_storage_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_storage_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 表存储信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("storage_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long storage_id; //储存编号
	private String is_into_hbase; //是否入hbase
	private String is_into_hive; //是否入hive
	private String is_fullindex; //是否创建全文索引
	private String file_format; //文件格式
	private Long table_id; //表名ID
	private String is_compression; //Hbase是使用压缩
	private String is_mpp; //是否为MPP
	private String table_type; //是内部表还是外部表
	private String is_solr_hbase; //是否使用solrOnHbase
	private String is_cbd; //是否使用carbondata

	/** 取得：储存编号 */
	public Long getStorage_id(){
		return storage_id;
	}
	/** 设置：储存编号 */
	public void setStorage_id(Long storage_id){
		this.storage_id=storage_id;
	}
	/** 设置：储存编号 */
	public void setStorage_id(String storage_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(storage_id)){
			this.storage_id=new Long(storage_id);
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
	/** 取得：是否使用carbondata */
	public String getIs_cbd(){
		return is_cbd;
	}
	/** 设置：是否使用carbondata */
	public void setIs_cbd(String is_cbd){
		this.is_cbd=is_cbd;
	}
}
