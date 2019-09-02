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
 * 机器学习项目状态表
 */
@Table(tableName = "ml_project_status")
public class Ml_project_status extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_project_status";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习项目状态表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("status_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long status_id; //状态编号
	private Long project_id; //项目编号
	private String project_status; //项目状态
	private String start_is_flag; //是否开始
	private String end_is_flag; //是否结束
	private String update_date; //修改日期
	private String update_time; //修改时间

	/** 取得：状态编号 */
	public Long getStatus_id(){
		return status_id;
	}
	/** 设置：状态编号 */
	public void setStatus_id(Long status_id){
		this.status_id=status_id;
	}
	/** 设置：状态编号 */
	public void setStatus_id(String status_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(status_id)){
			this.status_id=new Long(status_id);
		}
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
	/** 取得：项目状态 */
	public String getProject_status(){
		return project_status;
	}
	/** 设置：项目状态 */
	public void setProject_status(String project_status){
		this.project_status=project_status;
	}
	/** 取得：是否开始 */
	public String getStart_is_flag(){
		return start_is_flag;
	}
	/** 设置：是否开始 */
	public void setStart_is_flag(String start_is_flag){
		this.start_is_flag=start_is_flag;
	}
	/** 取得：是否结束 */
	public String getEnd_is_flag(){
		return end_is_flag;
	}
	/** 设置：是否结束 */
	public void setEnd_is_flag(String end_is_flag){
		this.end_is_flag=end_is_flag;
	}
	/** 取得：修改日期 */
	public String getUpdate_date(){
		return update_date;
	}
	/** 设置：修改日期 */
	public void setUpdate_date(String update_date){
		this.update_date=update_date;
	}
	/** 取得：修改时间 */
	public String getUpdate_time(){
		return update_time;
	}
	/** 设置：修改时间 */
	public void setUpdate_time(String update_time){
		this.update_time=update_time;
	}
}
