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
 * 系统登记表参数信息
 */
@Table(tableName = "sysreg_parameter_info")
public class Sysreg_parameter_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sysreg_parameter_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 系统登记表参数信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("parameter_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="parameter_id",value="参数ID:",dataType = Long.class,required = true)
	private Long parameter_id;
	@DocBean(name ="is_flag",value="是否可用(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_flag;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="table_ch_column",value="表列中文名称:",dataType = String.class,required = true)
	private String table_ch_column;
	@DocBean(name ="use_id",value="表使用ID:",dataType = Long.class,required = true)
	private Long use_id;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="table_en_column",value="表列英文名称:",dataType = String.class,required = true)
	private String table_en_column;

	/** 取得：参数ID */
	public Long getParameter_id(){
		return parameter_id;
	}
	/** 设置：参数ID */
	public void setParameter_id(Long parameter_id){
		this.parameter_id=parameter_id;
	}
	/** 设置：参数ID */
	public void setParameter_id(String parameter_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(parameter_id)){
			this.parameter_id=new Long(parameter_id);
		}
	}
	/** 取得：是否可用 */
	public String getIs_flag(){
		return is_flag;
	}
	/** 设置：是否可用 */
	public void setIs_flag(String is_flag){
		this.is_flag=is_flag;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：表列中文名称 */
	public String getTable_ch_column(){
		return table_ch_column;
	}
	/** 设置：表列中文名称 */
	public void setTable_ch_column(String table_ch_column){
		this.table_ch_column=table_ch_column;
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
	/** 取得：表列英文名称 */
	public String getTable_en_column(){
		return table_en_column;
	}
	/** 设置：表列英文名称 */
	public void setTable_en_column(String table_en_column){
		this.table_en_column=table_en_column;
	}
}
