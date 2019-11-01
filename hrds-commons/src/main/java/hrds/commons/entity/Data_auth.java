package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 数据权限设置表
 */
@Table(tableName = "data_auth")
public class Data_auth extends ProjectTableEntity
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
	@DocBean(name ="da_id",value="数据权限设置ID:",dataType = Long.class,required = true)
	private Long da_id;
	@DocBean(name ="apply_date",value="申请日期:",dataType = String.class,required = true)
	private String apply_date;
	@DocBean(name ="apply_time",value="申请时间:",dataType = String.class,required = true)
	private String apply_time;
	@DocBean(name ="apply_type",value="申请类型(ApplyType):1-查看<ChaKan> 2-下载<XiaZai> 3-发布<FaBu> 4-重命名<ChongMingMing> ",dataType = String.class,required = true)
	private String apply_type;
	@DocBean(name ="auth_type",value="权限类型(AuthType):1-允许<YunXu> 2-不允许<BuYunXu> 3-一次<YiCi> 0-申请<ShenQing> ",dataType = String.class,required = true)
	private String auth_type;
	@DocBean(name ="audit_date",value="审核日期:",dataType = String.class,required = false)
	private String audit_date;
	@DocBean(name ="audit_time",value="审核时间:",dataType = String.class,required = false)
	private String audit_time;
	@DocBean(name ="audit_userid",value="审核人ID:",dataType = Long.class,required = false)
	private Long audit_userid;
	@DocBean(name ="audit_name",value="审核人名称:",dataType = String.class,required = false)
	private String audit_name;
	@DocBean(name ="file_id",value="文件编号:",dataType = String.class,required = true)
	private String file_id;
	@DocBean(name ="dep_id",value="部门ID:",dataType = Long.class,required = true)
	private Long dep_id;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = true)
	private Long source_id;
	@DocBean(name ="collect_set_id",value="数据库设置id:",dataType = Long.class,required = true)
	private Long collect_set_id;

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
}
