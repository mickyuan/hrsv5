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
 * 数据权限设置表
 */
@Table(tableName = "data_auth")
public class Data_auth extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_auth";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据权限设置表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("da_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long da_id; //数据权限设置ID
	private Long user_id; //用户ID
	private String apply_date; //申请日期
	private String apply_time; //申请时间
	private String apply_type; //申请类型
	private String auth_type; //权限类型
	private String audit_date; //审核日期
	private String audit_time; //审核时间
	private Long audit_userid; //审核人ID
	private String audit_name; //审核人名称
	private String file_id; //文件编号
	private Long dep_id; //部门ID
	private Long agent_id; //Agent_id
	private Long collect_set_id; //数据库设置id
	private Long source_id; //数据源ID

	/** 取得：数据权限设置ID */
	public Long getDa_id(){
		return da_id;
	}
	/** 设置：数据权限设置ID */
	public void setDa_id(Long da_id){
		this.da_id=da_id;
	}
	/** 设置：数据权限设置ID */
	public void setDa_id(String da_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(da_id)){
			this.da_id=new Long(da_id);
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
	/** 取得：申请日期 */
	public String getApply_date(){
		return apply_date;
	}
	/** 设置：申请日期 */
	public void setApply_date(String apply_date){
		this.apply_date=apply_date;
	}
	/** 取得：申请时间 */
	public String getApply_time(){
		return apply_time;
	}
	/** 设置：申请时间 */
	public void setApply_time(String apply_time){
		this.apply_time=apply_time;
	}
	/** 取得：申请类型 */
	public String getApply_type(){
		return apply_type;
	}
	/** 设置：申请类型 */
	public void setApply_type(String apply_type){
		this.apply_type=apply_type;
	}
	/** 取得：权限类型 */
	public String getAuth_type(){
		return auth_type;
	}
	/** 设置：权限类型 */
	public void setAuth_type(String auth_type){
		this.auth_type=auth_type;
	}
	/** 取得：审核日期 */
	public String getAudit_date(){
		return audit_date;
	}
	/** 设置：审核日期 */
	public void setAudit_date(String audit_date){
		this.audit_date=audit_date;
	}
	/** 取得：审核时间 */
	public String getAudit_time(){
		return audit_time;
	}
	/** 设置：审核时间 */
	public void setAudit_time(String audit_time){
		this.audit_time=audit_time;
	}
	/** 取得：审核人ID */
	public Long getAudit_userid(){
		return audit_userid;
	}
	/** 设置：审核人ID */
	public void setAudit_userid(Long audit_userid){
		this.audit_userid=audit_userid;
	}
	/** 设置：审核人ID */
	public void setAudit_userid(String audit_userid){
		if(!fd.ng.core.utils.StringUtil.isEmpty(audit_userid)){
			this.audit_userid=new Long(audit_userid);
		}
	}
	/** 取得：审核人名称 */
	public String getAudit_name(){
		return audit_name;
	}
	/** 设置：审核人名称 */
	public void setAudit_name(String audit_name){
		this.audit_name=audit_name;
	}
	/** 取得：文件编号 */
	public String getFile_id(){
		return file_id;
	}
	/** 设置：文件编号 */
	public void setFile_id(String file_id){
		this.file_id=file_id;
	}
	/** 取得：部门ID */
	public Long getDep_id(){
		return dep_id;
	}
	/** 设置：部门ID */
	public void setDep_id(Long dep_id){
		this.dep_id=dep_id;
	}
	/** 设置：部门ID */
	public void setDep_id(String dep_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dep_id)){
			this.dep_id=new Long(dep_id);
		}
	}
	/** 取得：Agent_id */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
	/** 取得：数据库设置id */
	public Long getCollect_set_id(){
		return collect_set_id;
	}
	/** 设置：数据库设置id */
	public void setCollect_set_id(Long collect_set_id){
		this.collect_set_id=collect_set_id;
	}
	/** 设置：数据库设置id */
	public void setCollect_set_id(String collect_set_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(collect_set_id)){
			this.collect_set_id=new Long(collect_set_id);
		}
	}
	/** 取得：数据源ID */
	public Long getSource_id(){
		return source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(Long source_id){
		this.source_id=source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(String source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(source_id)){
			this.source_id=new Long(source_id);
		}
	}
}
