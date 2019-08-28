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
 * 配置与解析关系
 */
@Table(tableName = "cp_relation_cm")
public class Cp_relation_cm extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "cp_relation_cm";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 配置与解析关系 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cmsg_id");
		__tmpPKS.add("cp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cmsg_id; //配置ID
	private Long cp_id; //自定义解析id

	/** 取得：配置ID */
	public Long getCmsg_id(){
		return cmsg_id;
	}
	/** 设置：配置ID */
	public void setCmsg_id(Long cmsg_id){
		this.cmsg_id=cmsg_id;
	}
	/** 设置：配置ID */
	public void setCmsg_id(String cmsg_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cmsg_id)){
			this.cmsg_id=new Long(cmsg_id);
		}
	}
	/** 取得：自定义解析id */
	public Long getCp_id(){
		return cp_id;
	}
	/** 设置：自定义解析id */
	public void setCp_id(Long cp_id){
		this.cp_id=cp_id;
	}
	/** 设置：自定义解析id */
	public void setCp_id(String cp_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cp_id)){
			this.cp_id=new Long(cp_id);
		}
	}
}
