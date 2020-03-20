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
 * 用户信息表
 */
@Table(tableName = "sys_user")
public class Sys_user extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_user";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 用户信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("user_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="user_name",value="用户名称:",dataType = String.class,required = true)
	private String user_name;
	@DocBean(name ="user_password",value="用户密码:",dataType = String.class,required = true)
	private String user_password;
	@DocBean(name ="user_email",value="邮箱:",dataType = String.class,required = false)
	private String user_email;
	@DocBean(name ="user_mobile",value="移动电话:",dataType = String.class,required = false)
	private String user_mobile;
	@DocBean(name ="user_type",value="用户类型(UserType):00-系统管理员<XiTongGuanLiYuan> 01-采集管理<CaijiGuanLiYuan> 02-数据采集<CaiJiYongHu> 03-数据查询<YeWuYongHu> 04-作业调度<ZuoYeGuanLiYuan> 05-作业操作员<ZuoYeCaoZuoYuan> 06-数据可视化管理<ShuJuKSHGuanLiYuan> 07-可视化数据源<ShuJuKSHSJY> 08-数据可视化分析<ShuJuKSHBianJI> 09-数据可视化查看<ShuJuKSHChaKan> 10-监控管理<JianKongGuanLiYuan> 11-服务接口管理<RESTJieKouGuanLiYuan> 12-服务接口用户<RESTYongHu> 13-分词器管理<FenCiQiGuanLiYuan> 14-数据集市<JiShiGuanLiYuan> 15-数据加工<JiShiJiaGongGuanLiYuan> 16-机器学习工作台<JiQiXueXiGuanLiYuan> 17-机器学习业务<JiQiXueXiYongHu> 18-流数据管理<LiuShuJuGuanLiYuan> 19-流数据生产<LiuShuJuShengChanYongHu> 20-数据库配置(永洪)<ShuJuKuPeiZhi> 21-报表创建(永洪)<BaoBiaoChuanJian> 22-报表查看(永洪)<BaoBiaoChaKan> 23-流数据消费<LiuShuJuXiaoFeiYongHu> 24-数据管控<ShuJuGuanKongGuanLiYuan> 25-自主分析管理<ZiZhuFenXiGuanLi> 27-资源管理<ZiYuanGuanLi> 26-自主分析操作<ZiZhuFenXiCaoZuo> 37-数据对标操作<ShuJuDuiBiaoCaoZuo> 55-数据对标管理<ShuJuDuiBiaoGuanLi> 99-用户管理<YongHuGuanLi> 98-部门管理<BuMenGuanLi> 97-系统参数管理<XiTongCanShuGuanLi> 96-数据整理<ShuJuZhengLi> 95-数据存储层定义<ShuJunCunChuCengDingYi> ",dataType = String.class,required = false)
	private String user_type;
	@DocBean(name ="login_ip",value="登录IP:",dataType = String.class,required = false)
	private String login_ip;
	@DocBean(name ="login_date",value="最后登录时间:",dataType = String.class,required = false)
	private String login_date;
	@DocBean(name ="user_state",value="用户状态(UserState):1-正常<ZhengChang> 2-禁用<JinYong> 3-删除<ShanChu> 4-正在使用<ZhengZaiShiYong> ",dataType = String.class,required = true)
	private String user_state;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = false)
	private String create_time;
	@DocBean(name ="update_date",value="更新日期:",dataType = String.class,required = false)
	private String update_date;
	@DocBean(name ="update_time",value="更新时间:",dataType = String.class,required = false)
	private String update_time;
	@DocBean(name ="user_remark",value="备注:",dataType = String.class,required = false)
	private String user_remark;
	@DocBean(name ="create_id",value="建立用户ID:",dataType = Long.class,required = true)
	private Long create_id;
	@DocBean(name ="token",value="token:",dataType = String.class,required = true)
	private String token;
	@DocBean(name ="valid_time",value="token有效时间:",dataType = String.class,required = true)
	private String valid_time;
	@DocBean(name ="usertype_group",value="用户类型组:",dataType = String.class,required = false)
	private String usertype_group;
	@DocBean(name ="useris_admin",value="是否为管理员(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String useris_admin;
	@DocBean(name ="role_id",value="角色ID:",dataType = Long.class,required = true)
	private Long role_id;
	@DocBean(name ="dep_id",value="部门ID:",dataType = Long.class,required = true)
	private Long dep_id;

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
	/** 取得：用户名称 */
	public String getUser_name(){
		return user_name;
	}
	/** 设置：用户名称 */
	public void setUser_name(String user_name){
		this.user_name=user_name;
	}
	/** 取得：用户密码 */
	public String getUser_password(){
		return user_password;
	}
	/** 设置：用户密码 */
	public void setUser_password(String user_password){
		this.user_password=user_password;
	}
	/** 取得：邮箱 */
	public String getUser_email(){
		return user_email;
	}
	/** 设置：邮箱 */
	public void setUser_email(String user_email){
		this.user_email=user_email;
	}
	/** 取得：移动电话 */
	public String getUser_mobile(){
		return user_mobile;
	}
	/** 设置：移动电话 */
	public void setUser_mobile(String user_mobile){
		this.user_mobile=user_mobile;
	}
	/** 取得：用户类型 */
	public String getUser_type(){
		return user_type;
	}
	/** 设置：用户类型 */
	public void setUser_type(String user_type){
		this.user_type=user_type;
	}
	/** 取得：登录IP */
	public String getLogin_ip(){
		return login_ip;
	}
	/** 设置：登录IP */
	public void setLogin_ip(String login_ip){
		this.login_ip=login_ip;
	}
	/** 取得：最后登录时间 */
	public String getLogin_date(){
		return login_date;
	}
	/** 设置：最后登录时间 */
	public void setLogin_date(String login_date){
		this.login_date=login_date;
	}
	/** 取得：用户状态 */
	public String getUser_state(){
		return user_state;
	}
	/** 设置：用户状态 */
	public void setUser_state(String user_state){
		this.user_state=user_state;
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
	/** 取得：备注 */
	public String getUser_remark(){
		return user_remark;
	}
	/** 设置：备注 */
	public void setUser_remark(String user_remark){
		this.user_remark=user_remark;
	}
	/** 取得：建立用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：建立用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：建立用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
	/** 取得：token */
	public String getToken(){
		return token;
	}
	/** 设置：token */
	public void setToken(String token){
		this.token=token;
	}
	/** 取得：token有效时间 */
	public String getValid_time(){
		return valid_time;
	}
	/** 设置：token有效时间 */
	public void setValid_time(String valid_time){
		this.valid_time=valid_time;
	}
	/** 取得：用户类型组 */
	public String getUsertype_group(){
		return usertype_group;
	}
	/** 设置：用户类型组 */
	public void setUsertype_group(String usertype_group){
		this.usertype_group=usertype_group;
	}
	/** 取得：是否为管理员 */
	public String getUseris_admin(){
		return useris_admin;
	}
	/** 设置：是否为管理员 */
	public void setUseris_admin(String useris_admin){
		this.useris_admin=useris_admin;
	}
	/** 取得：角色ID */
	public Long getRole_id(){
		return role_id;
	}
	/** 设置：角色ID */
	public void setRole_id(Long role_id){
		this.role_id=role_id;
	}
	/** 设置：角色ID */
	public void setRole_id(String role_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(role_id)){
			this.role_id=new Long(role_id);
		}
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
}
