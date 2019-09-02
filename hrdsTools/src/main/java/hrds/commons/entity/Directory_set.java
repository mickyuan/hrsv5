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
 * 目录监控配置表
 */
@Table(tableName = "directory_set")
public class Directory_set extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "directory_set";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 目录监控配置表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("set_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long set_id; //配置id
	private String watch_dir; //要监控的目录
	private String watch_type; //监控类型
	private String remark; //备注

	/** 取得：配置id */
	public Long getSet_id(){
		return set_id;
	}
	/** 设置：配置id */
	public void setSet_id(Long set_id){
		this.set_id=set_id;
	}
	/** 设置：配置id */
	public void setSet_id(String set_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(set_id)){
			this.set_id=new Long(set_id);
		}
	}
	/** 取得：要监控的目录 */
	public String getWatch_dir(){
		return watch_dir;
	}
	/** 设置：要监控的目录 */
	public void setWatch_dir(String watch_dir){
		this.watch_dir=watch_dir;
	}
	/** 取得：监控类型 */
	public String getWatch_type(){
		return watch_type;
	}
	/** 设置：监控类型 */
	public void setWatch_type(String watch_type){
		this.watch_type=watch_type;
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
