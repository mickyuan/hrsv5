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
 * 集市分类信息
 */
@Table(tableName = "dm_category")
public class Dm_category extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dm_category";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 集市分类信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("category_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="category_id",value="集市分类id:",dataType = Long.class,required = true)
	private Long category_id;
	@DocBean(name ="category_name",value="分类名称:",dataType = String.class,required = true)
	private String category_name;
	@DocBean(name ="category_desc",value="分类描述:",dataType = String.class,required = false)
	private String category_desc;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="create_id",value="创建用户:",dataType = Long.class,required = true)
	private Long create_id;
	@DocBean(name ="category_num",value="分类编号:",dataType = String.class,required = true)
	private String category_num;
	@DocBean(name ="category_seq",value="分类序号:",dataType = String.class,required = false)
	private String category_seq;
	@DocBean(name ="parent_category_id",value="集市分类id:",dataType = Long.class,required = true)
	private Long parent_category_id;
	@DocBean(name ="data_mart_id",value="数据集市id:",dataType = Long.class,required = true)
	private Long data_mart_id;

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
	/** 取得：分类名称 */
	public String getCategory_name(){
		return category_name;
	}
	/** 设置：分类名称 */
	public void setCategory_name(String category_name){
		this.category_name=category_name;
	}
	/** 取得：分类描述 */
	public String getCategory_desc(){
		return category_desc;
	}
	/** 设置：分类描述 */
	public void setCategory_desc(String category_desc){
		this.category_desc=category_desc;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：创建用户 */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：创建用户 */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：创建用户 */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
	/** 取得：分类编号 */
	public String getCategory_num(){
		return category_num;
	}
	/** 设置：分类编号 */
	public void setCategory_num(String category_num){
		this.category_num=category_num;
	}
	/** 取得：分类序号 */
	public String getCategory_seq(){
		return category_seq;
	}
	/** 设置：分类序号 */
	public void setCategory_seq(String category_seq){
		this.category_seq=category_seq;
	}
	/** 取得：集市分类id */
	public Long getParent_category_id(){
		return parent_category_id;
	}
	/** 设置：集市分类id */
	public void setParent_category_id(Long parent_category_id){
		this.parent_category_id=parent_category_id;
	}
	/** 设置：集市分类id */
	public void setParent_category_id(String parent_category_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(parent_category_id)){
			this.parent_category_id=new Long(parent_category_id);
		}
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
}
