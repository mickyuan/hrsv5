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
 * 数据类型对照主表
 */
@Table(tableName = "type_contrast_sum")
public class Type_contrast_sum extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "type_contrast_sum";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据类型对照主表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dtcs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dtcs_id",value="类型对照ID:",dataType = Long.class,required = true)
	private Long dtcs_id;
	@DocBean(name ="dtcs_name",value="类型对照名称:",dataType = String.class,required = true)
	private String dtcs_name;
	@DocBean(name ="dtcs_remark",value="备注:",dataType = String.class,required = false)
	private String dtcs_remark;

	/** 取得：类型对照ID */
	public Long getDtcs_id(){
		return dtcs_id;
	}
	/** 设置：类型对照ID */
	public void setDtcs_id(Long dtcs_id){
		this.dtcs_id=dtcs_id;
	}
	/** 设置：类型对照ID */
	public void setDtcs_id(String dtcs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtcs_id)){
			this.dtcs_id=new Long(dtcs_id);
		}
	}
	/** 取得：类型对照名称 */
	public String getDtcs_name(){
		return dtcs_name;
	}
	/** 设置：类型对照名称 */
	public void setDtcs_name(String dtcs_name){
		this.dtcs_name=dtcs_name;
	}
	/** 取得：备注 */
	public String getDtcs_remark(){
		return dtcs_remark;
	}
	/** 设置：备注 */
	public void setDtcs_remark(String dtcs_remark){
		this.dtcs_remark=dtcs_remark;
	}
}
