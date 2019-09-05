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
 * 报表条件过滤表
 */
@Table(tableName = "r_cond_filt")
public class R_cond_filt extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "r_cond_filt";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 报表条件过滤表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("filter_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long filter_id; //过滤编号
	private String cond_filter; //条件过滤方式
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long report_id; //报表编号
	private Long create_id; //用户ID

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
	/** 取得：条件过滤方式 */
	public String getCond_filter(){
		return cond_filter;
	}
	/** 设置：条件过滤方式 */
	public void setCond_filter(String cond_filter){
		this.cond_filter=cond_filter;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：报表编号 */
	public Long getReport_id(){
		return report_id;
	}
	/** 设置：报表编号 */
	public void setReport_id(Long report_id){
		this.report_id=report_id;
	}
	/** 设置：报表编号 */
	public void setReport_id(String report_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(report_id)){
			this.report_id=new Long(report_id);
		}
	}
	/** 取得：用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
}
