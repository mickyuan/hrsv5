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
 * 流数据管理消费至数据库表
 */
@Table(tableName = "sdm_con_to_db")
public class Sdm_con_to_db extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_to_db";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据管理消费至数据库表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_con_db_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sdm_con_db_id; //数据库设置id
	private String sdm_db_name; //数据库名称
	private String sdm_db_pwd; //数据库密码
	private String sdm_db_driver; //数据库驱动
	private String sdm_db_type; //数据库类型
	private String sdm_db_user; //用户名称
	private String sdm_db_ip; //数据库服务器IP
	private String sdm_db_port; //数据库端口
	private String sdm_db_num; //数据库设置编号
	private String sdm_sys_type; //操作系统类型
	private String sdm_db_code; //数据使用编码格式
	private String remark; //备注
	private Long sdm_consum_id; //消费端配置id
	private String sdm_tb_name_en; //英文表名
	private String sdm_tb_name_cn; //中文表名
	private Long sdm_des_id; //配置id
	private String db_bus_class; //数据库业务处理类
	private String db_bus_type; //数据库业务类类型

	/** 取得：数据库设置id */
	public Long getSdm_con_db_id(){
		return sdm_con_db_id;
	}
	/** 设置：数据库设置id */
	public void setSdm_con_db_id(Long sdm_con_db_id){
		this.sdm_con_db_id=sdm_con_db_id;
	}
	/** 设置：数据库设置id */
	public void setSdm_con_db_id(String sdm_con_db_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_con_db_id)){
			this.sdm_con_db_id=new Long(sdm_con_db_id);
		}
	}
	/** 取得：数据库名称 */
	public String getSdm_db_name(){
		return sdm_db_name;
	}
	/** 设置：数据库名称 */
	public void setSdm_db_name(String sdm_db_name){
		this.sdm_db_name=sdm_db_name;
	}
	/** 取得：数据库密码 */
	public String getSdm_db_pwd(){
		return sdm_db_pwd;
	}
	/** 设置：数据库密码 */
	public void setSdm_db_pwd(String sdm_db_pwd){
		this.sdm_db_pwd=sdm_db_pwd;
	}
	/** 取得：数据库驱动 */
	public String getSdm_db_driver(){
		return sdm_db_driver;
	}
	/** 设置：数据库驱动 */
	public void setSdm_db_driver(String sdm_db_driver){
		this.sdm_db_driver=sdm_db_driver;
	}
	/** 取得：数据库类型 */
	public String getSdm_db_type(){
		return sdm_db_type;
	}
	/** 设置：数据库类型 */
	public void setSdm_db_type(String sdm_db_type){
		this.sdm_db_type=sdm_db_type;
	}
	/** 取得：用户名称 */
	public String getSdm_db_user(){
		return sdm_db_user;
	}
	/** 设置：用户名称 */
	public void setSdm_db_user(String sdm_db_user){
		this.sdm_db_user=sdm_db_user;
	}
	/** 取得：数据库服务器IP */
	public String getSdm_db_ip(){
		return sdm_db_ip;
	}
	/** 设置：数据库服务器IP */
	public void setSdm_db_ip(String sdm_db_ip){
		this.sdm_db_ip=sdm_db_ip;
	}
	/** 取得：数据库端口 */
	public String getSdm_db_port(){
		return sdm_db_port;
	}
	/** 设置：数据库端口 */
	public void setSdm_db_port(String sdm_db_port){
		this.sdm_db_port=sdm_db_port;
	}
	/** 取得：数据库设置编号 */
	public String getSdm_db_num(){
		return sdm_db_num;
	}
	/** 设置：数据库设置编号 */
	public void setSdm_db_num(String sdm_db_num){
		this.sdm_db_num=sdm_db_num;
	}
	/** 取得：操作系统类型 */
	public String getSdm_sys_type(){
		return sdm_sys_type;
	}
	/** 设置：操作系统类型 */
	public void setSdm_sys_type(String sdm_sys_type){
		this.sdm_sys_type=sdm_sys_type;
	}
	/** 取得：数据使用编码格式 */
	public String getSdm_db_code(){
		return sdm_db_code;
	}
	/** 设置：数据使用编码格式 */
	public void setSdm_db_code(String sdm_db_code){
		this.sdm_db_code=sdm_db_code;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：消费端配置id */
	public Long getSdm_consum_id(){
		return sdm_consum_id;
	}
	/** 设置：消费端配置id */
	public void setSdm_consum_id(Long sdm_consum_id){
		this.sdm_consum_id=sdm_consum_id;
	}
	/** 设置：消费端配置id */
	public void setSdm_consum_id(String sdm_consum_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_consum_id)){
			this.sdm_consum_id=new Long(sdm_consum_id);
		}
	}
	/** 取得：英文表名 */
	public String getSdm_tb_name_en(){
		return sdm_tb_name_en;
	}
	/** 设置：英文表名 */
	public void setSdm_tb_name_en(String sdm_tb_name_en){
		this.sdm_tb_name_en=sdm_tb_name_en;
	}
	/** 取得：中文表名 */
	public String getSdm_tb_name_cn(){
		return sdm_tb_name_cn;
	}
	/** 设置：中文表名 */
	public void setSdm_tb_name_cn(String sdm_tb_name_cn){
		this.sdm_tb_name_cn=sdm_tb_name_cn;
	}
	/** 取得：配置id */
	public Long getSdm_des_id(){
		return sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(Long sdm_des_id){
		this.sdm_des_id=sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(String sdm_des_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_des_id)){
			this.sdm_des_id=new Long(sdm_des_id);
		}
	}
	/** 取得：数据库业务处理类 */
	public String getDb_bus_class(){
		return db_bus_class;
	}
	/** 设置：数据库业务处理类 */
	public void setDb_bus_class(String db_bus_class){
		this.db_bus_class=db_bus_class;
	}
	/** 取得：数据库业务类类型 */
	public String getDb_bus_type(){
		return db_bus_type;
	}
	/** 设置：数据库业务类类型 */
	public void setDb_bus_type(String db_bus_type){
		this.db_bus_type=db_bus_type;
	}
}
