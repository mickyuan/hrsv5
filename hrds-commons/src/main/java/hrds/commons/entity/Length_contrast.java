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
 * 存储层数据类型长度对照表
 */
@Table(tableName = "length_contrast")
public class Length_contrast extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "length_contrast";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 存储层数据类型长度对照表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dlc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dlc_id",value="存储层类型长度ID:",dataType = Long.class,required = true)
	private Long dlc_id;
	@DocBean(name ="dlc_type",value="字段类型:",dataType = String.class,required = true)
	private String dlc_type;
	@DocBean(name ="dlc_length",value="字段长度:",dataType = Integer.class,required = true)
	private Integer dlc_length;
	@DocBean(name ="dlc_remark",value="备注:",dataType = String.class,required = false)
	private String dlc_remark;
	@DocBean(name ="dlcs_id",value="长度对照表ID:",dataType = Long.class,required = true)
	private Long dlcs_id;

	/** 取得：存储层类型长度ID */
	public Long getDlc_id(){
		return dlc_id;
	}
	/** 设置：存储层类型长度ID */
	public void setDlc_id(Long dlc_id){
		this.dlc_id=dlc_id;
	}
	/** 设置：存储层类型长度ID */
	public void setDlc_id(String dlc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dlc_id)){
			this.dlc_id=new Long(dlc_id);
		}
	}
	/** 取得：字段类型 */
	public String getDlc_type(){
		return dlc_type;
	}
	/** 设置：字段类型 */
	public void setDlc_type(String dlc_type){
		this.dlc_type=dlc_type;
	}
	/** 取得：字段长度 */
	public Integer getDlc_length(){
		return dlc_length;
	}
	/** 设置：字段长度 */
	public void setDlc_length(Integer dlc_length){
		this.dlc_length=dlc_length;
	}
	/** 设置：字段长度 */
	public void setDlc_length(String dlc_length){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dlc_length)){
			this.dlc_length=new Integer(dlc_length);
		}
	}
	/** 取得：备注 */
	public String getDlc_remark(){
		return dlc_remark;
	}
	/** 设置：备注 */
	public void setDlc_remark(String dlc_remark){
		this.dlc_remark=dlc_remark;
	}
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
}
