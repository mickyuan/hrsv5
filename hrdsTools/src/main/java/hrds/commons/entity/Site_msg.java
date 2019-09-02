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
 * 站点管理
 */
@Table(tableName = "site_msg")
public class Site_msg extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "site_msg";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 站点管理 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("site_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long site_id; //站点ID
	private String site_name; //名称
	private String site_url; //站点URL
	private String site_remark; //备注

	/** 取得：站点ID */
	public Long getSite_id(){
		return site_id;
	}
	/** 设置：站点ID */
	public void setSite_id(Long site_id){
		this.site_id=site_id;
	}
	/** 设置：站点ID */
	public void setSite_id(String site_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(site_id)){
			this.site_id=new Long(site_id);
		}
	}
	/** 取得：名称 */
	public String getSite_name(){
		return site_name;
	}
	/** 设置：名称 */
	public void setSite_name(String site_name){
		this.site_name=site_name;
	}
	/** 取得：站点URL */
	public String getSite_url(){
		return site_url;
	}
	/** 设置：站点URL */
	public void setSite_url(String site_url){
		this.site_url=site_url;
	}
	/** 取得：备注 */
	public String getSite_remark(){
		return site_remark;
	}
	/** 设置：备注 */
	public void setSite_remark(String site_remark){
		this.site_remark=site_remark;
	}
}
