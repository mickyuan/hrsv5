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
 * 源系统编码信息
 */
@Table(tableName = "orig_code_info")
public class Orig_code_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "orig_code_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 源系统编码信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("orig_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="orig_value",value="源系统编码值:",dataType = String.class,required = true)
	private String orig_value;
	@DocBean(name ="code_remark",value="系统编码描述:",dataType = String.class,required = false)
	private String code_remark;
	@DocBean(name ="code_classify",value="编码分类:",dataType = String.class,required = true)
	private String code_classify;
	@DocBean(name ="code_value",value="编码类型值:",dataType = String.class,required = true)
	private String code_value;
	@DocBean(name ="orig_sys_code",value="码值系统编码:",dataType = String.class,required = false)
	private String orig_sys_code;
	@DocBean(name ="orig_id",value="源系统编码主键:",dataType = Long.class,required = true)
	private Long orig_id;

	/** 取得：源系统编码值 */
	public String getOrig_value(){
		return orig_value;
	}
	/** 设置：源系统编码值 */
	public void setOrig_value(String orig_value){
		this.orig_value=orig_value;
	}
	/** 取得：系统编码描述 */
	public String getCode_remark(){
		return code_remark;
	}
	/** 设置：系统编码描述 */
	public void setCode_remark(String code_remark){
		this.code_remark=code_remark;
	}
	/** 取得：编码分类 */
	public String getCode_classify(){
		return code_classify;
	}
	/** 设置：编码分类 */
	public void setCode_classify(String code_classify){
		this.code_classify=code_classify;
	}
	/** 取得：编码类型值 */
	public String getCode_value(){
		return code_value;
	}
	/** 设置：编码类型值 */
	public void setCode_value(String code_value){
		this.code_value=code_value;
	}
	/** 取得：码值系统编码 */
	public String getOrig_sys_code(){
		return orig_sys_code;
	}
	/** 设置：码值系统编码 */
	public void setOrig_sys_code(String orig_sys_code){
		this.orig_sys_code=orig_sys_code;
	}
	/** 取得：源系统编码主键 */
	public Long getOrig_id(){
		return orig_id;
	}
	/** 设置：源系统编码主键 */
	public void setOrig_id(Long orig_id){
		this.orig_id=orig_id;
	}
	/** 设置：源系统编码主键 */
	public void setOrig_id(String orig_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(orig_id)){
			this.orig_id=new Long(orig_id);
		}
	}
}
