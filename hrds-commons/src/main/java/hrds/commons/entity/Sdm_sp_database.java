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
 * StreamingPro数据库数据信息表
 */
@Table(tableName = "sdm_sp_database")
public class Sdm_sp_database extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_database";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** StreamingPro数据库数据信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ssd_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long ssd_info_id; //数据库信息表id
	private String ssd_database_type; //数据库类型
	private String ssd_database_drive; //数据库驱动
	private String ssd_database_name; //数据库名称
	private String ssd_ip; //数据库ip
	private String ssd_port; //端口
	private String ssd_user_name; //数据库用户名
	private String ssd_user_password; //用户密码
	private String ssd_table_name; //表名称
	private String ssd_jdbc_url; //数据库jdbc连接的url
	private Long sdm_info_id; //作业输入信息表id

	/** 取得：数据库信息表id */
	public Long getSsd_info_id(){
		return ssd_info_id;
	}
	/** 设置：数据库信息表id */
	public void setSsd_info_id(Long ssd_info_id){
		this.ssd_info_id=ssd_info_id;
	}
	/** 设置：数据库信息表id */
	public void setSsd_info_id(String ssd_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ssd_info_id)){
			this.ssd_info_id=new Long(ssd_info_id);
		}
	}
	/** 取得：数据库类型 */
	public String getSsd_database_type(){
		return ssd_database_type;
	}
	/** 设置：数据库类型 */
	public void setSsd_database_type(String ssd_database_type){
		this.ssd_database_type=ssd_database_type;
	}
	/** 取得：数据库驱动 */
	public String getSsd_database_drive(){
		return ssd_database_drive;
	}
	/** 设置：数据库驱动 */
	public void setSsd_database_drive(String ssd_database_drive){
		this.ssd_database_drive=ssd_database_drive;
	}
	/** 取得：数据库名称 */
	public String getSsd_database_name(){
		return ssd_database_name;
	}
	/** 设置：数据库名称 */
	public void setSsd_database_name(String ssd_database_name){
		this.ssd_database_name=ssd_database_name;
	}
	/** 取得：数据库ip */
	public String getSsd_ip(){
		return ssd_ip;
	}
	/** 设置：数据库ip */
	public void setSsd_ip(String ssd_ip){
		this.ssd_ip=ssd_ip;
	}
	/** 取得：端口 */
	public String getSsd_port(){
		return ssd_port;
	}
	/** 设置：端口 */
	public void setSsd_port(String ssd_port){
		this.ssd_port=ssd_port;
	}
	/** 取得：数据库用户名 */
	public String getSsd_user_name(){
		return ssd_user_name;
	}
	/** 设置：数据库用户名 */
	public void setSsd_user_name(String ssd_user_name){
		this.ssd_user_name=ssd_user_name;
	}
	/** 取得：用户密码 */
	public String getSsd_user_password(){
		return ssd_user_password;
	}
	/** 设置：用户密码 */
	public void setSsd_user_password(String ssd_user_password){
		this.ssd_user_password=ssd_user_password;
	}
	/** 取得：表名称 */
	public String getSsd_table_name(){
		return ssd_table_name;
	}
	/** 设置：表名称 */
	public void setSsd_table_name(String ssd_table_name){
		this.ssd_table_name=ssd_table_name;
	}
	/** 取得：数据库jdbc连接的url */
	public String getSsd_jdbc_url(){
		return ssd_jdbc_url;
	}
	/** 设置：数据库jdbc连接的url */
	public void setSsd_jdbc_url(String ssd_jdbc_url){
		this.ssd_jdbc_url=ssd_jdbc_url;
	}
	/** 取得：作业输入信息表id */
	public Long getSdm_info_id(){
		return sdm_info_id;
	}
	/** 设置：作业输入信息表id */
	public void setSdm_info_id(Long sdm_info_id){
		this.sdm_info_id=sdm_info_id;
	}
	/** 设置：作业输入信息表id */
	public void setSdm_info_id(String sdm_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_info_id)){
			this.sdm_info_id=new Long(sdm_info_id);
		}
	}
}
