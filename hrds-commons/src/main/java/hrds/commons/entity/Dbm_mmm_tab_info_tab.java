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
 * 数据对标表信息表
 */
@Table(tableName = "dbm_mmm_tab_info_tab")
public class Dbm_mmm_tab_info_tab extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_mmm_tab_info_tab";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标表信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sys_class_code");
		__tmpPKS.add("table_schema");
		__tmpPKS.add("table_code");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="sys_class_code",value="系统分类编码:",dataType = String.class,required = true)
	private String sys_class_code;
	@DocBean(name ="table_schema",value="表所属schema:",dataType = String.class,required = true)
	private String table_schema;
	@DocBean(name ="table_code",value="表编码:",dataType = String.class,required = true)
	private String table_code;
	@DocBean(name ="table_name",value="表中文名:",dataType = String.class,required = false)
	private String table_name;
	@DocBean(name ="table_comment",value="表注释:",dataType = String.class,required = false)
	private String table_comment;
	@DocBean(name ="table_owner",value="表拥有者:",dataType = String.class,required = false)
	private String table_owner;
	@DocBean(name ="st_tm",value="开始时间:",dataType = String.class,required = true)
	private String st_tm;
	@DocBean(name ="end_tm",value="结束时间:",dataType = String.class,required = true)
	private String end_tm;
	@DocBean(name ="node_code",value="节点编码:",dataType = String.class,required = false)
	private String node_code;
	@DocBean(name ="data_src",value="数据来源:",dataType = String.class,required = false)
	private String data_src;
	@DocBean(name ="col_num",value="表字段数:",dataType = Integer.class,required = true)
	private Integer col_num;

	/** 取得：系统分类编码 */
	public String getSys_class_code(){
		return sys_class_code;
	}
	/** 设置：系统分类编码 */
	public void setSys_class_code(String sys_class_code){
		this.sys_class_code=sys_class_code;
	}
	/** 取得：表所属schema */
	public String getTable_schema(){
		return table_schema;
	}
	/** 设置：表所属schema */
	public void setTable_schema(String table_schema){
		this.table_schema=table_schema;
	}
	/** 取得：表编码 */
	public String getTable_code(){
		return table_code;
	}
	/** 设置：表编码 */
	public void setTable_code(String table_code){
		this.table_code=table_code;
	}
	/** 取得：表中文名 */
	public String getTable_name(){
		return table_name;
	}
	/** 设置：表中文名 */
	public void setTable_name(String table_name){
		this.table_name=table_name;
	}
	/** 取得：表注释 */
	public String getTable_comment(){
		return table_comment;
	}
	/** 设置：表注释 */
	public void setTable_comment(String table_comment){
		this.table_comment=table_comment;
	}
	/** 取得：表拥有者 */
	public String getTable_owner(){
		return table_owner;
	}
	/** 设置：表拥有者 */
	public void setTable_owner(String table_owner){
		this.table_owner=table_owner;
	}
	/** 取得：开始时间 */
	public String getSt_tm(){
		return st_tm;
	}
	/** 设置：开始时间 */
	public void setSt_tm(String st_tm){
		this.st_tm=st_tm;
	}
	/** 取得：结束时间 */
	public String getEnd_tm(){
		return end_tm;
	}
	/** 设置：结束时间 */
	public void setEnd_tm(String end_tm){
		this.end_tm=end_tm;
	}
	/** 取得：节点编码 */
	public String getNode_code(){
		return node_code;
	}
	/** 设置：节点编码 */
	public void setNode_code(String node_code){
		this.node_code=node_code;
	}
	/** 取得：数据来源 */
	public String getData_src(){
		return data_src;
	}
	/** 设置：数据来源 */
	public void setData_src(String data_src){
		this.data_src=data_src;
	}
	/** 取得：表字段数 */
	public Integer getCol_num(){
		return col_num;
	}
	/** 设置：表字段数 */
	public void setCol_num(Integer col_num){
		this.col_num=col_num;
	}
	/** 设置：表字段数 */
	public void setCol_num(String col_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_num)){
			this.col_num=new Integer(col_num);
		}
	}
}
