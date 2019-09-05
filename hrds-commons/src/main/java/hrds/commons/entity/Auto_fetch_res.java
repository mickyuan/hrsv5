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
 * 取数结果表
 */
@Table(tableName = "auto_fetch_res")
public class Auto_fetch_res extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_fetch_res";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 取数结果表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fetch_res_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String fetch_res_name; //取数结果名称
	private Integer show_num; //显示顺序
	private Long fetch_res_id; //取数结果ID
	private Long template_res_id; //模板结果ID
	private Long fetch_sum_id; //取数汇总ID

	/** 取得：取数结果名称 */
	public String getFetch_res_name(){
		return fetch_res_name;
	}
	/** 设置：取数结果名称 */
	public void setFetch_res_name(String fetch_res_name){
		this.fetch_res_name=fetch_res_name;
	}
	/** 取得：显示顺序 */
	public Integer getShow_num(){
		return show_num;
	}
	/** 设置：显示顺序 */
	public void setShow_num(Integer show_num){
		this.show_num=show_num;
	}
	/** 设置：显示顺序 */
	public void setShow_num(String show_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(show_num)){
			this.show_num=new Integer(show_num);
		}
	}
	/** 取得：取数结果ID */
	public Long getFetch_res_id(){
		return fetch_res_id;
	}
	/** 设置：取数结果ID */
	public void setFetch_res_id(Long fetch_res_id){
		this.fetch_res_id=fetch_res_id;
	}
	/** 设置：取数结果ID */
	public void setFetch_res_id(String fetch_res_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(fetch_res_id)){
			this.fetch_res_id=new Long(fetch_res_id);
		}
	}
	/** 取得：模板结果ID */
	public Long getTemplate_res_id(){
		return template_res_id;
	}
	/** 设置：模板结果ID */
	public void setTemplate_res_id(Long template_res_id){
		this.template_res_id=template_res_id;
	}
	/** 设置：模板结果ID */
	public void setTemplate_res_id(String template_res_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(template_res_id)){
			this.template_res_id=new Long(template_res_id);
		}
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
}
