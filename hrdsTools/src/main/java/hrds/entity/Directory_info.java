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
 * 目录监控信息表
 */
@Table(tableName = "directory_info")
public class Directory_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "directory_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 目录监控信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long id; //目录id
	private String dir_name; //目录名称
	private String dir_path; //目录路径
	private String dir_desc; //目录描述
	private String dir_status; //目录状态
	private String remark; //备注
	private Long set_id; //配置id
	private String dir_date; //监控日期
	private String dir_time; //监控时间

	/** 取得：目录id */
	public Long getId(){
		return id;
	}
	/** 设置：目录id */
	public void setId(Long id){
		this.id=id;
	}
	/** 设置：目录id */
	public void setId(String id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(id)){
			this.id=new Long(id);
		}
	}
	/** 取得：目录名称 */
	public String getDir_name(){
		return dir_name;
	}
	/** 设置：目录名称 */
	public void setDir_name(String dir_name){
		this.dir_name=dir_name;
	}
	/** 取得：目录路径 */
	public String getDir_path(){
		return dir_path;
	}
	/** 设置：目录路径 */
	public void setDir_path(String dir_path){
		this.dir_path=dir_path;
	}
	/** 取得：目录描述 */
	public String getDir_desc(){
		return dir_desc;
	}
	/** 设置：目录描述 */
	public void setDir_desc(String dir_desc){
		this.dir_desc=dir_desc;
	}
	/** 取得：目录状态 */
	public String getDir_status(){
		return dir_status;
	}
	/** 设置：目录状态 */
	public void setDir_status(String dir_status){
		this.dir_status=dir_status;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：配置id */
	public Long getSet_id(){
		return set_id;
	}
	/** 设置：配置id */
	public void setSet_id(Long set_id){
		this.set_id=set_id;
	}
	/** 设置：配置id */
	public void setSet_id(String set_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(set_id)){
			this.set_id=new Long(set_id);
		}
	}
	/** 取得：监控日期 */
	public String getDir_date(){
		return dir_date;
	}
	/** 设置：监控日期 */
	public void setDir_date(String dir_date){
		this.dir_date=dir_date;
	}
	/** 取得：监控时间 */
	public String getDir_time(){
		return dir_time;
	}
	/** 设置：监控时间 */
	public void setDir_time(String dir_time){
		this.dir_time=dir_time;
	}
}
