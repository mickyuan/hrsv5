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
 * 报表数据表
 */
@Table(tableName = "report_data_table")
public class Report_data_table extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "report_data_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 报表数据表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("report_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long report_id; //报表编号
	private String report_name; //报表名称
	private String sql_choice_details; //SQL选择内容
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long create_id; //用户ID
	private Long report_type_id; //报表类型编号
	private String report_source; //报表来源
	private Long folder_id; //文件夹编号
	private Long configuration_id; //配置编号
	private Long sql_id; //SQL编号
	private String r_publish_status; //报表发布状态

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
	/** 取得：报表名称 */
	public String getReport_name(){
		return report_name;
	}
	/** 设置：报表名称 */
	public void setReport_name(String report_name){
		this.report_name=report_name;
	}
	/** 取得：SQL选择内容 */
	public String getSql_choice_details(){
		return sql_choice_details;
	}
	/** 设置：SQL选择内容 */
	public void setSql_choice_details(String sql_choice_details){
		this.sql_choice_details=sql_choice_details;
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
	/** 取得：报表类型编号 */
	public Long getReport_type_id(){
		return report_type_id;
	}
	/** 设置：报表类型编号 */
	public void setReport_type_id(Long report_type_id){
		this.report_type_id=report_type_id;
	}
	/** 设置：报表类型编号 */
	public void setReport_type_id(String report_type_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(report_type_id)){
			this.report_type_id=new Long(report_type_id);
		}
	}
	/** 取得：报表来源 */
	public String getReport_source(){
		return report_source;
	}
	/** 设置：报表来源 */
	public void setReport_source(String report_source){
		this.report_source=report_source;
	}
	/** 取得：文件夹编号 */
	public Long getFolder_id(){
		return folder_id;
	}
	/** 设置：文件夹编号 */
	public void setFolder_id(Long folder_id){
		this.folder_id=folder_id;
	}
	/** 设置：文件夹编号 */
	public void setFolder_id(String folder_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(folder_id)){
			this.folder_id=new Long(folder_id);
		}
	}
	/** 取得：配置编号 */
	public Long getConfiguration_id(){
		return configuration_id;
	}
	/** 设置：配置编号 */
	public void setConfiguration_id(Long configuration_id){
		this.configuration_id=configuration_id;
	}
	/** 设置：配置编号 */
	public void setConfiguration_id(String configuration_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(configuration_id)){
			this.configuration_id=new Long(configuration_id);
		}
	}
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
	/** 取得：报表发布状态 */
	public String getR_publish_status(){
		return r_publish_status;
	}
	/** 设置：报表发布状态 */
	public void setR_publish_status(String r_publish_status){
		this.r_publish_status=r_publish_status;
	}
}
