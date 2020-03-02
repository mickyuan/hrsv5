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
 * 数据对标元管理标准分类信息表
 */
@Table(tableName = "dbm_sort_info")
public class Dbm_sort_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_sort_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标元管理标准分类信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sort_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="sort_id",value="分类主键:",dataType = Long.class,required = true)
	private Long sort_id;
	@DocBean(name ="parent_id",value="父级分类主键:",dataType = Long.class,required = true)
	private Long parent_id;
	@DocBean(name ="sort_level_num",value="分类层级数:",dataType = Long.class,required = true)
	private Long sort_level_num;
	@DocBean(name ="sort_name",value="分类名称:",dataType = String.class,required = true)
	private String sort_name;
	@DocBean(name ="sort_remark",value="分类描述:",dataType = String.class,required = false)
	private String sort_remark;
	@DocBean(name ="sort_status",value="分类状态（是否发布）(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String sort_status;
	@DocBean(name ="create_user",value="创建人:",dataType = String.class,required = true)
	private String create_user;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;

	/** 取得：分类主键 */
	public Long getSort_id(){
		return sort_id;
	}
	/** 设置：分类主键 */
	public void setSort_id(Long sort_id){
		this.sort_id=sort_id;
	}
	/** 设置：分类主键 */
	public void setSort_id(String sort_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sort_id)){
			this.sort_id=new Long(sort_id);
		}
	}
	/** 取得：父级分类主键 */
	public Long getParent_id(){
		return parent_id;
	}
	/** 设置：父级分类主键 */
	public void setParent_id(Long parent_id){
		this.parent_id=parent_id;
	}
	/** 设置：父级分类主键 */
	public void setParent_id(String parent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(parent_id)){
			this.parent_id=new Long(parent_id);
		}
	}
	/** 取得：分类层级数 */
	public Long getSort_level_num(){
		return sort_level_num;
	}
	/** 设置：分类层级数 */
	public void setSort_level_num(Long sort_level_num){
		this.sort_level_num=sort_level_num;
	}
	/** 设置：分类层级数 */
	public void setSort_level_num(String sort_level_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sort_level_num)){
			this.sort_level_num=new Long(sort_level_num);
		}
	}
	/** 取得：分类名称 */
	public String getSort_name(){
		return sort_name;
	}
	/** 设置：分类名称 */
	public void setSort_name(String sort_name){
		this.sort_name=sort_name;
	}
	/** 取得：分类描述 */
	public String getSort_remark(){
		return sort_remark;
	}
	/** 设置：分类描述 */
	public void setSort_remark(String sort_remark){
		this.sort_remark=sort_remark;
	}
	/** 取得：分类状态（是否发布） */
	public String getSort_status(){
		return sort_status;
	}
	/** 设置：分类状态（是否发布） */
	public void setSort_status(String sort_status){
		this.sort_status=sort_status;
	}
	/** 取得：创建人 */
	public String getCreate_user(){
		return create_user;
	}
	/** 设置：创建人 */
	public void setCreate_user(String create_user){
		this.create_user=create_user;
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
}
