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
 * 对象采集结构信息
 */
@Table(tableName = "object_collect_struct")
public class Object_collect_struct extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_collect_struct";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对象采集结构信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("struct_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="struct_id",value="结构信息id:",dataType = Long.class,required = true)
	private Long struct_id;
	@DocBean(name ="column_name",value="字段名称:",dataType = String.class,required = true)
	private String column_name;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="column_type",value="字段类型:",dataType = String.class,required = true)
	private String column_type;
	@DocBean(name ="data_desc",value="中文描述信息:",dataType = String.class,required = false)
	private String data_desc;
	@DocBean(name ="col_seq",value="字段序号:",dataType = Long.class,required = true)
	private Long col_seq;
	@DocBean(name ="is_rowkey",value="是否rowkey(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_rowkey;
	@DocBean(name ="columnposition",value="字段位置:",dataType = String.class,required = true)
	private String columnposition;
	@DocBean(name ="is_key",value="是否主键(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_key;
	@DocBean(name ="is_solr",value="是否solr(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_solr;
	@DocBean(name ="is_operate",value="是否操作标识字段(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_operate;
	@DocBean(name ="is_hbase",value="是否hbase(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_hbase;
	@DocBean(name ="ocs_id",value="对象采集任务编号:",dataType = Long.class,required = true)
	private Long ocs_id;

	/** 取得：结构信息id */
	public Long getStruct_id(){
		return struct_id;
	}
	/** 设置：结构信息id */
	public void setStruct_id(Long struct_id){
		this.struct_id=struct_id;
	}
	/** 设置：结构信息id */
	public void setStruct_id(String struct_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(struct_id)){
			this.struct_id=new Long(struct_id);
		}
	}
	/** 取得：字段名称 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：字段名称 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：字段类型 */
	public String getColumn_type(){
		return column_type;
	}
	/** 设置：字段类型 */
	public void setColumn_type(String column_type){
		this.column_type=column_type;
	}
	/** 取得：中文描述信息 */
	public String getData_desc(){
		return data_desc;
	}
	/** 设置：中文描述信息 */
	public void setData_desc(String data_desc){
		this.data_desc=data_desc;
	}
	/** 取得：字段序号 */
	public Long getCol_seq(){
		return col_seq;
	}
	/** 设置：字段序号 */
	public void setCol_seq(Long col_seq){
		this.col_seq=col_seq;
	}
	/** 设置：字段序号 */
	public void setCol_seq(String col_seq){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_seq)){
			this.col_seq=new Long(col_seq);
		}
	}
	/** 取得：是否rowkey */
	public String getIs_rowkey(){
		return is_rowkey;
	}
	/** 设置：是否rowkey */
	public void setIs_rowkey(String is_rowkey){
		this.is_rowkey=is_rowkey;
	}
	/** 取得：字段位置 */
	public String getColumnposition(){
		return columnposition;
	}
	/** 设置：字段位置 */
	public void setColumnposition(String columnposition){
		this.columnposition=columnposition;
	}
	/** 取得：是否主键 */
	public String getIs_key(){
		return is_key;
	}
	/** 设置：是否主键 */
	public void setIs_key(String is_key){
		this.is_key=is_key;
	}
	/** 取得：是否solr */
	public String getIs_solr(){
		return is_solr;
	}
	/** 设置：是否solr */
	public void setIs_solr(String is_solr){
		this.is_solr=is_solr;
	}
	/** 取得：是否操作标识字段 */
	public String getIs_operate(){
		return is_operate;
	}
	/** 设置：是否操作标识字段 */
	public void setIs_operate(String is_operate){
		this.is_operate=is_operate;
	}
	/** 取得：是否hbase */
	public String getIs_hbase(){
		return is_hbase;
	}
	/** 设置：是否hbase */
	public void setIs_hbase(String is_hbase){
		this.is_hbase=is_hbase;
	}
	/** 取得：对象采集任务编号 */
	public Long getOcs_id(){
		return ocs_id;
	}
	/** 设置：对象采集任务编号 */
	public void setOcs_id(Long ocs_id){
		this.ocs_id=ocs_id;
	}
	/** 设置：对象采集任务编号 */
	public void setOcs_id(String ocs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ocs_id)){
			this.ocs_id=new Long(ocs_id);
		}
	}
}
