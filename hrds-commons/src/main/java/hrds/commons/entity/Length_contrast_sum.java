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
 * 存储层数据类型长度对照主表
 */
@Table(tableName = "length_contrast_sum")
public class Length_contrast_sum extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "length_contrast_sum";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 存储层数据类型长度对照主表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dlcs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dlcs_id",value="长度对照表ID:",dataType = Long.class,required = true)
	private Long dlcs_id;
	@DocBean(name ="dlcs_name",value="长度对照名称:",dataType = String.class,required = true)
	private String dlcs_name;
	@DocBean(name ="dlcs_remark",value="备注:",dataType = String.class,required = false)
	private String dlcs_remark;

	/** 取得：长度对照表ID */
	public Long getDlcs_id(){
		return dlcs_id;
	}
	/** 设置：长度对照表ID */
	public void setDlcs_id(Long dlcs_id){
		this.dlcs_id=dlcs_id;
	}
	/** 设置：长度对照表ID */
	public void setDlcs_id(String dlcs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dlcs_id)){
			this.dlcs_id=new Long(dlcs_id);
		}
	}
	/** 取得：长度对照名称 */
	public String getDlcs_name(){
		return dlcs_name;
	}
	/** 设置：长度对照名称 */
	public void setDlcs_name(String dlcs_name){
		this.dlcs_name=dlcs_name;
	}
	/** 取得：备注 */
	public String getDlcs_remark(){
		return dlcs_remark;
	}
	/** 设置：备注 */
	public void setDlcs_remark(String dlcs_remark){
		this.dlcs_remark=dlcs_remark;
	}
}
