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
 * SQL记录表
 */
@Table(tableName = "sql_record_table")
public class Sql_record_table extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sql_record_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** SQL记录表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sql_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sql_id; //SQL编号
	private String sql_name; //sql别名
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String update_date; //更新日期
	private String update_time; //更新时间
	private String sql_details; //执行的sql语句
	private String select_as_column; //字段别名
	private String sql_original; //原始的sql语句
	private String select_analyse; //sql选择分析结果
	private String table_analyse; //sql表分析结果
	private String condition_analyse; //sql条件分析结果
	private String groupby_analyse; //sql分组分析结果
	private String orderby_analyse; //sql排序分析结果
	private String join_analyse; //sql连接分析结果
	private String limit_analyse; //sql限数分析结果
	private Long create_id; //用户ID
	private String clean_sql; //无条件的sql语句

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
	/** 取得：sql别名 */
	public String getSql_name(){
		return sql_name;
	}
	/** 设置：sql别名 */
	public void setSql_name(String sql_name){
		this.sql_name=sql_name;
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
	/** 取得：更新日期 */
	public String getUpdate_date(){
		return update_date;
	}
	/** 设置：更新日期 */
	public void setUpdate_date(String update_date){
		this.update_date=update_date;
	}
	/** 取得：更新时间 */
	public String getUpdate_time(){
		return update_time;
	}
	/** 设置：更新时间 */
	public void setUpdate_time(String update_time){
		this.update_time=update_time;
	}
	/** 取得：执行的sql语句 */
	public String getSql_details(){
		return sql_details;
	}
	/** 设置：执行的sql语句 */
	public void setSql_details(String sql_details){
		this.sql_details=sql_details;
	}
	/** 取得：字段别名 */
	public String getSelect_as_column(){
		return select_as_column;
	}
	/** 设置：字段别名 */
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
	/** 取得：sql选择分析结果 */
	public String getSelect_analyse(){
		return select_analyse;
	}
	/** 设置：sql选择分析结果 */
	public void setSelect_analyse(String select_analyse){
		this.select_analyse=select_analyse;
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
	/** 取得：无条件的sql语句 */
	public String getClean_sql(){
		return clean_sql;
	}
	/** 设置：无条件的sql语句 */
	public void setClean_sql(String clean_sql){
		this.clean_sql=clean_sql;
	}
}
