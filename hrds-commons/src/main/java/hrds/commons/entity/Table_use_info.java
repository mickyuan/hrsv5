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
 * 表使用信息表
 */
@Table(tableName = "table_use_info")
public class Table_use_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_use_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 表使用信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("use_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="sysreg_name",value="系统登记表名:",dataType = String.class,required = true)
	private String sysreg_name;
	@DocBean(name ="table_note",value="表说明:",dataType = String.class,required = false)
	private String table_note;
	@DocBean(name ="use_id",value="表使用ID:",dataType = Long.class,required = true)
	private Long use_id;
	@DocBean(name ="table_blsystem",value="数据表所属系统(DataSourceType):ISL-贴源层_01<ISL> DCL-贴源层<DCL> DPL-加工层<DPL> DML-集市层<DML> SFL-系统层<SFL> AML-AI模型层<AML> DQC-管控层<DQC> UDL-自定义层<UDL> ",dataType = String.class,required = true)
	private String table_blsystem;
	@DocBean(name ="original_name",value="原始文件名称:",dataType = String.class,required = true)
	private String original_name;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;

	/** 取得：系统登记表名 */
	public String getSysreg_name(){
		return sysreg_name;
	}
	/** 设置：系统登记表名 */
	public void setSysreg_name(String sysreg_name){
		this.sysreg_name=sysreg_name;
	}
	/** 取得：表说明 */
	public String getTable_note(){
		return table_note;
	}
	/** 设置：表说明 */
	public void setTable_note(String table_note){
		this.table_note=table_note;
	}
	/** 取得：表使用ID */
	public Long getUse_id(){
		return use_id;
	}
	/** 设置：表使用ID */
	public void setUse_id(Long use_id){
		this.use_id=use_id;
	}
	/** 设置：表使用ID */
	public void setUse_id(String use_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(use_id)){
			this.use_id=new Long(use_id);
		}
	}
	/** 取得：数据表所属系统 */
	public String getTable_blsystem(){
		return table_blsystem;
	}
	/** 设置：数据表所属系统 */
	public void setTable_blsystem(String table_blsystem){
		this.table_blsystem=table_blsystem;
	}
	/** 取得：原始文件名称 */
	public String getOriginal_name(){
		return original_name;
	}
	/** 设置：原始文件名称 */
	public void setOriginal_name(String original_name){
		this.original_name=original_name;
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
