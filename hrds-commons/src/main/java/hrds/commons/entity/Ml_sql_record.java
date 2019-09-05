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
 * 机器学习SQL记录表
 */
@Table(tableName = "ml_sql_record")
public class Ml_sql_record extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_sql_record";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习SQL记录表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sql_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sql_id; //SQL编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String select_as_column; //字段与别名分析结果
	private String sql_original; //原始的sql语句
	private String table_analyse; //sql表分析结果
	private String condition_analyse; //sql条件分析结果
	private String groupby_analyse; //sql分组分析结果
	private String orderby_analyse; //sql排序分析结果
	private String join_analyse; //sql连接分析结果
	private String limit_analyse; //sql限数分析结果
	private String having_analyse; //sql连接条件分析结果
	private Long project_id; //项目编号
	private Long user_id; //用户ID

	/** 取得：SQL编号 */
	public Long getSql_id(){
		return sql_id;
	}
	/** 设置：SQL编号 */
	public void setSql_id(Long sql_id){
		this.sql_id=sql_id;
	}
	/** 设置：SQL编号 */
	public void setSql_id(String sql_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sql_id)){
			this.sql_id=new Long(sql_id);
		}
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
	/** 取得：字段与别名分析结果 */
	public String getSelect_as_column(){
		return select_as_column;
	}
	/** 设置：字段与别名分析结果 */
	public void setSelect_as_column(String select_as_column){
		this.select_as_column=select_as_column;
	}
	/** 取得：原始的sql语句 */
	public String getSql_original(){
		return sql_original;
	}
	/** 设置：原始的sql语句 */
	public void setSql_original(String sql_original){
		this.sql_original=sql_original;
	}
	/** 取得：sql表分析结果 */
	public String getTable_analyse(){
		return table_analyse;
	}
	/** 设置：sql表分析结果 */
	public void setTable_analyse(String table_analyse){
		this.table_analyse=table_analyse;
	}
	/** 取得：sql条件分析结果 */
	public String getCondition_analyse(){
		return condition_analyse;
	}
	/** 设置：sql条件分析结果 */
	public void setCondition_analyse(String condition_analyse){
		this.condition_analyse=condition_analyse;
	}
	/** 取得：sql分组分析结果 */
	public String getGroupby_analyse(){
		return groupby_analyse;
	}
	/** 设置：sql分组分析结果 */
	public void setGroupby_analyse(String groupby_analyse){
		this.groupby_analyse=groupby_analyse;
	}
	/** 取得：sql排序分析结果 */
	public String getOrderby_analyse(){
		return orderby_analyse;
	}
	/** 设置：sql排序分析结果 */
	public void setOrderby_analyse(String orderby_analyse){
		this.orderby_analyse=orderby_analyse;
	}
	/** 取得：sql连接分析结果 */
	public String getJoin_analyse(){
		return join_analyse;
	}
	/** 设置：sql连接分析结果 */
	public void setJoin_analyse(String join_analyse){
		this.join_analyse=join_analyse;
	}
	/** 取得：sql限数分析结果 */
	public String getLimit_analyse(){
		return limit_analyse;
	}
	/** 设置：sql限数分析结果 */
	public void setLimit_analyse(String limit_analyse){
		this.limit_analyse=limit_analyse;
	}
	/** 取得：sql连接条件分析结果 */
	public String getHaving_analyse(){
		return having_analyse;
	}
	/** 设置：sql连接条件分析结果 */
	public void setHaving_analyse(String having_analyse){
		this.having_analyse=having_analyse;
	}
	/** 取得：项目编号 */
	public Long getProject_id(){
		return project_id;
	}
	/** 设置：项目编号 */
	public void setProject_id(Long project_id){
		this.project_id=project_id;
	}
	/** 设置：项目编号 */
	public void setProject_id(String project_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(project_id)){
			this.project_id=new Long(project_id);
		}
	}
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
}
