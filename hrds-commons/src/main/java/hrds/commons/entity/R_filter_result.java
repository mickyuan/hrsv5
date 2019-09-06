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
 * 报表过滤结果表
 */
@Table(tableName = "r_filter_result")
public class R_filter_result extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "r_filter_result";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 报表过滤结果表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("filterresu_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long filterresu_id; //过滤结果编号
	private Long filter_id; //过滤编号
	private String newcolumn_value; //新字段值
	private String newcolumn_cond; //新字段条件
	private String newinci_rela; //新关联关系
	private Long cond_anal_id; //条件分析编号

	/** 取得：过滤结果编号 */
	public Long getFilterresu_id(){
		return filterresu_id;
	}
	/** 设置：过滤结果编号 */
	public void setFilterresu_id(Long filterresu_id){
		this.filterresu_id=filterresu_id;
	}
	/** 设置：过滤结果编号 */
	public void setFilterresu_id(String filterresu_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(filterresu_id)){
			this.filterresu_id=new Long(filterresu_id);
		}
	}
	/** 取得：过滤编号 */
	public Long getFilter_id(){
		return filter_id;
	}
	/** 设置：过滤编号 */
	public void setFilter_id(Long filter_id){
		this.filter_id=filter_id;
	}
	/** 设置：过滤编号 */
	public void setFilter_id(String filter_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(filter_id)){
			this.filter_id=new Long(filter_id);
		}
	}
	/** 取得：新字段值 */
	public String getNewcolumn_value(){
		return newcolumn_value;
	}
	/** 设置：新字段值 */
	public void setNewcolumn_value(String newcolumn_value){
		this.newcolumn_value=newcolumn_value;
	}
	/** 取得：新字段条件 */
	public String getNewcolumn_cond(){
		return newcolumn_cond;
	}
	/** 设置：新字段条件 */
	public void setNewcolumn_cond(String newcolumn_cond){
		this.newcolumn_cond=newcolumn_cond;
	}
	/** 取得：新关联关系 */
	public String getNewinci_rela(){
		return newinci_rela;
	}
	/** 设置：新关联关系 */
	public void setNewinci_rela(String newinci_rela){
		this.newinci_rela=newinci_rela;
	}
	/** 取得：条件分析编号 */
	public Long getCond_anal_id(){
		return cond_anal_id;
	}
	/** 设置：条件分析编号 */
	public void setCond_anal_id(Long cond_anal_id){
		this.cond_anal_id=cond_anal_id;
	}
	/** 设置：条件分析编号 */
	public void setCond_anal_id(String cond_anal_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cond_anal_id)){
			this.cond_anal_id=new Long(cond_anal_id);
		}
	}
}
