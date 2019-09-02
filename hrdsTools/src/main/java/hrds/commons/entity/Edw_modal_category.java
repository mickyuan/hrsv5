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
 * 模型分类信息
 */
@Table(tableName = "edw_modal_category")
public class Edw_modal_category extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_modal_category";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模型分类信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("category_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long category_id; //模型分类id
	private String category_name; //分类名称
	private String category_desc; //分类描述
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long create_id; //创建用户
	private Long parent_category_id; //模型分类id
	private Long modal_pro_id; //模型工程id
	private String category_num; //分类编号
	private String category_seq; //分类序号

	/** 取得：模型分类id */
	public Long getCategory_id(){
		return category_id;
	}
	/** 设置：模型分类id */
	public void setCategory_id(Long category_id){
		this.category_id=category_id;
	}
	/** 设置：模型分类id */
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
	/** 取得：模型分类id */
	public Long getParent_category_id(){
		return parent_category_id;
	}
	/** 设置：模型分类id */
	public void setParent_category_id(Long parent_category_id){
		this.parent_category_id=parent_category_id;
	}
	/** 设置：模型分类id */
	public void setParent_category_id(String parent_category_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(parent_category_id)){
			this.parent_category_id=new Long(parent_category_id);
		}
	}
	/** 取得：模型工程id */
	public Long getModal_pro_id(){
		return modal_pro_id;
	}
	/** 设置：模型工程id */
	public void setModal_pro_id(Long modal_pro_id){
		this.modal_pro_id=modal_pro_id;
	}
	/** 设置：模型工程id */
	public void setModal_pro_id(String modal_pro_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(modal_pro_id)){
			this.modal_pro_id=new Long(modal_pro_id);
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
}
