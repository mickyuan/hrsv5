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
 * 存储层数据类型对照表
 */
@Table(tableName = "type_contrast")
public class Type_contrast extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "type_contrast";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 存储层数据类型对照表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dtc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dtc_id",value="类型对照主键:",dataType = Long.class,required = true)
	private Long dtc_id;
	@DocBean(name ="source_type",value="源表数据类型:",dataType = String.class,required = true)
	private String source_type;
	@DocBean(name ="target_type",value="目标表数据类型:",dataType = String.class,required = true)
	private String target_type;
	@DocBean(name ="dtc_remark",value="备注:",dataType = String.class,required = false)
	private String dtc_remark;
	@DocBean(name ="dtcs_id",value="类型对照ID:",dataType = Long.class,required = true)
	private Long dtcs_id;

	/** 取得：类型对照主键 */
	public Long getDtc_id(){
		return dtc_id;
	}
	/** 设置：类型对照主键 */
	public void setDtc_id(Long dtc_id){
		this.dtc_id=dtc_id;
	}
	/** 设置：类型对照主键 */
	public void setDtc_id(String dtc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtc_id)){
			this.dtc_id=new Long(dtc_id);
		}
	}
	/** 取得：源表数据类型 */
	public String getSource_type(){
		return source_type;
	}
	/** 设置：源表数据类型 */
	public void setSource_type(String source_type){
		this.source_type=source_type;
	}
	/** 取得：目标表数据类型 */
	public String getTarget_type(){
		return target_type;
	}
	/** 设置：目标表数据类型 */
	public void setTarget_type(String target_type){
		this.target_type=target_type;
	}
	/** 取得：备注 */
	public String getDtc_remark(){
		return dtc_remark;
	}
	/** 设置：备注 */
	public void setDtc_remark(String dtc_remark){
		this.dtc_remark=dtc_remark;
	}
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
}
