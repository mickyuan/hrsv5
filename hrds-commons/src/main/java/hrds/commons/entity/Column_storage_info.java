package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.docannotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 字段存储信息
 */
@Table(tableName = "column_storage_info")
public class Column_storage_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "column_storage_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 字段存储信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="datasc_id",value="存储配置主键信息",dataType = Long.class,required = true)
	private Long datasc_id;
	@DocBean(name ="cs_id",value="字段存储ID",dataType = Long.class,required = true)
	private Long cs_id;
	@DocBean(name ="column_id",value="字段ID",dataType = Long.class,required = true)
	private Long column_id;
	@DocBean(name ="is_primary",value="是否为主键",dataType = String.class,required = true)
	private String is_primary;
	@DocBean(name ="is_pre",value="是否为cb预聚合列",dataType = String.class,required = true)
	private String is_pre;
	@DocBean(name ="is_sortcolumns",value="是否为cb的排序列",dataType = String.class,required = true)
	private String is_sortcolumns;
	@DocBean(name ="cs_remark",value="备注",dataType = String.class,required = false)
	private String cs_remark;
	@DocBean(name ="is_solr",value="是否solr的索引列",dataType = String.class,required = true)
	private String is_solr;
	@DocBean(name ="is_partition",value="是否为hive的分区列",dataType = String.class,required = true)
	private String is_partition;

	/** 取得：存储配置主键信息 */
	public Long getDatasc_id(){
		return datasc_id;
	}
	/** 设置：存储配置主键信息 */
	public void setDatasc_id(Long datasc_id){
		this.datasc_id=datasc_id;
	}
	/** 设置：存储配置主键信息 */
	public void setDatasc_id(String datasc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datasc_id)){
			this.datasc_id=new Long(datasc_id);
		}
	}
	/** 取得：字段存储ID */
	public Long getCs_id(){
		return cs_id;
	}
	/** 设置：字段存储ID */
	public void setCs_id(Long cs_id){
		this.cs_id=cs_id;
	}
	/** 设置：字段存储ID */
	public void setCs_id(String cs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cs_id)){
			this.cs_id=new Long(cs_id);
		}
	}
	/** 取得：字段ID */
	public Long getColumn_id(){
		return column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(Long column_id){
		this.column_id=column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(String column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(column_id)){
			this.column_id=new Long(column_id);
		}
	}
	/** 取得：是否为主键 */
	public String getIs_primary(){
		return is_primary;
	}
	/** 设置：是否为主键 */
	public void setIs_primary(String is_primary){
		this.is_primary=is_primary;
	}
	/** 取得：是否为cb预聚合列 */
	public String getIs_pre(){
		return is_pre;
	}
	/** 设置：是否为cb预聚合列 */
	public void setIs_pre(String is_pre){
		this.is_pre=is_pre;
	}
	/** 取得：是否为cb的排序列 */
	public String getIs_sortcolumns(){
		return is_sortcolumns;
	}
	/** 设置：是否为cb的排序列 */
	public void setIs_sortcolumns(String is_sortcolumns){
		this.is_sortcolumns=is_sortcolumns;
	}
	/** 取得：备注 */
	public String getCs_remark(){
		return cs_remark;
	}
	/** 设置：备注 */
	public void setCs_remark(String cs_remark){
		this.cs_remark=cs_remark;
	}
	/** 取得：是否solr的索引列 */
	public String getIs_solr(){
		return is_solr;
	}
	/** 设置：是否solr的索引列 */
	public void setIs_solr(String is_solr){
		this.is_solr=is_solr;
	}
	/** 取得：是否为hive的分区列 */
	public String getIs_partition(){
		return is_partition;
	}
	/** 设置：是否为hive的分区列 */
	public void setIs_partition(String is_partition){
		this.is_partition=is_partition;
	}
}
