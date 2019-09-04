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
 * 模型工程
 */
@Table(tableName = "edw_modal_project")
public class Edw_modal_project extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_modal_project";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模型工程 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("modal_pro_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long modal_pro_id; //模型工程id
	private String pro_name; //工程名称
	private String pro_desc; //工程描述
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long create_id; //创建用户
	private String pro_number; //工程编号

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
	/** 取得：工程名称 */
	public String getPro_name(){
		return pro_name;
	}
	/** 设置：工程名称 */
	public void setPro_name(String pro_name){
		this.pro_name=pro_name;
	}
	/** 取得：工程描述 */
	public String getPro_desc(){
		return pro_desc;
	}
	/** 设置：工程描述 */
	public void setPro_desc(String pro_desc){
		this.pro_desc=pro_desc;
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
	/** 取得：工程编号 */
	public String getPro_number(){
		return pro_number;
	}
	/** 设置：工程编号 */
	public void setPro_number(String pro_number){
		this.pro_number=pro_number;
	}
}
