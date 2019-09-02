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
 * 更新信息表
 */
@Table(tableName = "update_info")
public class Update_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "update_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 更新信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("update_type");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String update_type; //update_type
	private String is_update; //is_update
	private String remark; //备注

	/** 取得：update_type */
	public String getUpdate_type(){
		return update_type;
	}
	/** 设置：update_type */
	public void setUpdate_type(String update_type){
		this.update_type=update_type;
	}
	/** 取得：is_update */
	public String getIs_update(){
		return is_update;
	}
	/** 设置：is_update */
	public void setIs_update(String is_update){
		this.is_update=is_update;
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
