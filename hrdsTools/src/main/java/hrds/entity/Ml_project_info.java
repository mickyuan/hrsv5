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
 * 机器学习项目表
 */
@Table(tableName = "ml_project_info")
public class Ml_project_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_project_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习项目表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("project_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long project_id; //项目编号
	private String project_name; //项目名称
	private String project_desc; //项目描述
	private String remark; //备注
	private String last_exec_date; //上次运行日期
	private String last_exec_time; //上次运行时间
	private String now_exec_date; //本次运行日期
	private String now_exec_time; //本次运行时间
	private String goodness_of_fit; //拟合度
	private String publish_status; //发布状态
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long user_id; //用户ID

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
	/** 取得：项目名称 */
	public String getProject_name(){
		return project_name;
	}
	/** 设置：项目名称 */
	public void setProject_name(String project_name){
		this.project_name=project_name;
	}
	/** 取得：项目描述 */
	public String getProject_desc(){
		return project_desc;
	}
	/** 设置：项目描述 */
	public void setProject_desc(String project_desc){
		this.project_desc=project_desc;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：上次运行日期 */
	public String getLast_exec_date(){
		return last_exec_date;
	}
	/** 设置：上次运行日期 */
	public void setLast_exec_date(String last_exec_date){
		this.last_exec_date=last_exec_date;
	}
	/** 取得：上次运行时间 */
	public String getLast_exec_time(){
		return last_exec_time;
	}
	/** 设置：上次运行时间 */
	public void setLast_exec_time(String last_exec_time){
		this.last_exec_time=last_exec_time;
	}
	/** 取得：本次运行日期 */
	public String getNow_exec_date(){
		return now_exec_date;
	}
	/** 设置：本次运行日期 */
	public void setNow_exec_date(String now_exec_date){
		this.now_exec_date=now_exec_date;
	}
	/** 取得：本次运行时间 */
	public String getNow_exec_time(){
		return now_exec_time;
	}
	/** 设置：本次运行时间 */
	public void setNow_exec_time(String now_exec_time){
		this.now_exec_time=now_exec_time;
	}
	/** 取得：拟合度 */
	public String getGoodness_of_fit(){
		return goodness_of_fit;
	}
	/** 设置：拟合度 */
	public void setGoodness_of_fit(String goodness_of_fit){
		this.goodness_of_fit=goodness_of_fit;
	}
	/** 取得：发布状态 */
	public String getPublish_status(){
		return publish_status;
	}
	/** 设置：发布状态 */
	public void setPublish_status(String publish_status){
		this.publish_status=publish_status;
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
