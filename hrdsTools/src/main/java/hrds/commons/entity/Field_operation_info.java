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
 * 字段操作类型信息表
 */
@Table(tableName = "field_operation_info")
public class Field_operation_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "field_operation_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 字段操作类型信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long info_id; //序号
	private Long own_field_id; //字段id
	private String operation_type; //操作类型
	private String operation_detail; //具体操作
	private String remark; //备注

	/** 取得：序号 */
	public Long getInfo_id(){
		return info_id;
	}
	/** 设置：序号 */
	public void setInfo_id(Long info_id){
		this.info_id=info_id;
	}
	/** 设置：序号 */
	public void setInfo_id(String info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(info_id)){
			this.info_id=new Long(info_id);
		}
	}
	/** 取得：字段id */
	public Long getOwn_field_id(){
		return own_field_id;
	}
	/** 设置：字段id */
	public void setOwn_field_id(Long own_field_id){
		this.own_field_id=own_field_id;
	}
	/** 设置：字段id */
	public void setOwn_field_id(String own_field_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(own_field_id)){
			this.own_field_id=new Long(own_field_id);
		}
	}
	/** 取得：操作类型 */
	public String getOperation_type(){
		return operation_type;
	}
	/** 设置：操作类型 */
	public void setOperation_type(String operation_type){
		this.operation_type=operation_type;
	}
	/** 取得：具体操作 */
	public String getOperation_detail(){
		return operation_detail;
	}
	/** 设置：具体操作 */
	public void setOperation_detail(String operation_detail){
		this.operation_detail=operation_detail;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
}
