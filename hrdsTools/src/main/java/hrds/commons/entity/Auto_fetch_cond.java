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
 * 取数条件表
 */
@Table(tableName = "auto_fetch_cond")
public class Auto_fetch_cond extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_fetch_cond";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 取数条件表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fetch_cond_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long fetch_cond_id; //取数条件ID
	private String cond_value; //条件值
	private Long fetch_sum_id; //取数汇总ID
	private Long template_cond_id; //模板条件ID

	/** 取得：取数条件ID */
	public Long getFetch_cond_id(){
		return fetch_cond_id;
	}
	/** 设置：取数条件ID */
	public void setFetch_cond_id(Long fetch_cond_id){
		this.fetch_cond_id=fetch_cond_id;
	}
	/** 设置：取数条件ID */
	public void setFetch_cond_id(String fetch_cond_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(fetch_cond_id)){
			this.fetch_cond_id=new Long(fetch_cond_id);
		}
	}
	/** 取得：条件值 */
	public String getCond_value(){
		return cond_value;
	}
	/** 设置：条件值 */
	public void setCond_value(String cond_value){
		this.cond_value=cond_value;
	}
	/** 取得：取数汇总ID */
	public Long getFetch_sum_id(){
		return fetch_sum_id;
	}
	/** 设置：取数汇总ID */
	public void setFetch_sum_id(Long fetch_sum_id){
		this.fetch_sum_id=fetch_sum_id;
	}
	/** 设置：取数汇总ID */
	public void setFetch_sum_id(String fetch_sum_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(fetch_sum_id)){
			this.fetch_sum_id=new Long(fetch_sum_id);
		}
	}
	/** 取得：模板条件ID */
	public Long getTemplate_cond_id(){
		return template_cond_id;
	}
	/** 设置：模板条件ID */
	public void setTemplate_cond_id(Long template_cond_id){
		this.template_cond_id=template_cond_id;
	}
	/** 设置：模板条件ID */
	public void setTemplate_cond_id(String template_cond_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(template_cond_id)){
			this.template_cond_id=new Long(template_cond_id);
		}
	}
}
