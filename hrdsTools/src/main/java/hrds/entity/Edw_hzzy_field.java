package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 模型后置作业表字段
 */
@Table(tableName = "edw_hzzy_field")
public class Edw_hzzy_field extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_hzzy_field";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模型后置作业表字段 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hzf_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long hzf_id; //字段id
	private String field_name; //字段英文名称
	private String field_cn_name; //字段中文名称
	private String field_type; //字段类型
	private String remark; //备注
	private Long hzzy_id; //后置作业表id

	/** 取得：字段id */
	public Long getHzf_id(){
		return hzf_id;
	}
	/** 设置：字段id */
	public void setHzf_id(Long hzf_id){
		this.hzf_id=hzf_id;
	}
	/** 设置：字段id */
	public void setHzf_id(String hzf_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(hzf_id)){
			this.hzf_id=new Long(hzf_id);
		}
	}
	/** 取得：字段英文名称 */
	public String getField_name(){
		return field_name;
	}
	/** 设置：字段英文名称 */
	public void setField_name(String field_name){
		this.field_name=field_name;
	}
	/** 取得：字段中文名称 */
	public String getField_cn_name(){
		return field_cn_name;
	}
	/** 设置：字段中文名称 */
	public void setField_cn_name(String field_cn_name){
		this.field_cn_name=field_cn_name;
	}
	/** 取得：字段类型 */
	public String getField_type(){
		return field_type;
	}
	/** 设置：字段类型 */
	public void setField_type(String field_type){
		this.field_type=field_type;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：后置作业表id */
	public Long getHzzy_id(){
		return hzzy_id;
	}
	/** 设置：后置作业表id */
	public void setHzzy_id(Long hzzy_id){
		this.hzzy_id=hzzy_id;
	}
	/** 设置：后置作业表id */
	public void setHzzy_id(String hzzy_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(hzzy_id)){
			this.hzzy_id=new Long(hzzy_id);
		}
	}
}
