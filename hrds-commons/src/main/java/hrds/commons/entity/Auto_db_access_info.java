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
 * 外部数据库访问信息表
 */
@Table(tableName = "auto_db_access_info")
public class Auto_db_access_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_db_access_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 外部数据库访问信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("access_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="db_type",value="数据库类型:",dataType = Long.class,required = true)
	private Long db_type;
	@DocBean(name ="db_name",value="数据库名称:",dataType = String.class,required = true)
	private String db_name;
	@DocBean(name ="db_ip",value="数据库服务ip:",dataType = String.class,required = true)
	private String db_ip;
	@DocBean(name ="db_user",value="数据库访问用户名:",dataType = String.class,required = true)
	private String db_user;
	@DocBean(name ="db_password",value="数据库访问密码:",dataType = String.class,required = false)
	private String db_password;
	@DocBean(name ="db_port",value="数据服访问端口:",dataType = String.class,required = true)
	private String db_port;
	@DocBean(name ="jdbcurl",value="jdbcurl:",dataType = String.class,required = true)
	private String jdbcurl;
	@DocBean(name ="access_info_id",value="数据库访问信息表id:",dataType = Long.class,required = true)
	private Long access_info_id;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = false)
	private Long component_id;

	/** 取得：数据库类型 */
	public Long getDb_type(){
		return db_type;
	}
	/** 设置：数据库类型 */
	public void setDb_type(Long db_type){
		this.db_type=db_type;
	}
	/** 设置：数据库类型 */
	public void setDb_type(String db_type){
		if(!fd.ng.core.utils.StringUtil.isEmpty(db_type)){
			this.db_type=new Long(db_type);
		}
	}
	/** 取得：数据库名称 */
	public String getDb_name(){
		return db_name;
	}
	/** 设置：数据库名称 */
	public void setDb_name(String db_name){
		this.db_name=db_name;
	}
	/** 取得：数据库服务ip */
	public String getDb_ip(){
		return db_ip;
	}
	/** 设置：数据库服务ip */
	public void setDb_ip(String db_ip){
		this.db_ip=db_ip;
	}
	/** 取得：数据库访问用户名 */
	public String getDb_user(){
		return db_user;
	}
	/** 设置：数据库访问用户名 */
	public void setDb_user(String db_user){
		this.db_user=db_user;
	}
	/** 取得：数据库访问密码 */
	public String getDb_password(){
		return db_password;
	}
	/** 设置：数据库访问密码 */
	public void setDb_password(String db_password){
		this.db_password=db_password;
	}
	/** 取得：数据服访问端口 */
	public String getDb_port(){
		return db_port;
	}
	/** 设置：数据服访问端口 */
	public void setDb_port(String db_port){
		this.db_port=db_port;
	}
	/** 取得：jdbcurl */
	public String getJdbcurl(){
		return jdbcurl;
	}
	/** 设置：jdbcurl */
	public void setJdbcurl(String jdbcurl){
		this.jdbcurl=jdbcurl;
	}
	/** 取得：数据库访问信息表id */
	public Long getAccess_info_id(){
		return access_info_id;
	}
	/** 设置：数据库访问信息表id */
	public void setAccess_info_id(Long access_info_id){
		this.access_info_id=access_info_id;
	}
	/** 设置：数据库访问信息表id */
	public void setAccess_info_id(String access_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(access_info_id)){
			this.access_info_id=new Long(access_info_id);
		}
	}
	/** 取得：组件ID */
	public Long getComponent_id(){
		return component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(Long component_id){
		this.component_id=component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(String component_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(component_id)){
			this.component_id=new Long(component_id);
		}
	}
}
