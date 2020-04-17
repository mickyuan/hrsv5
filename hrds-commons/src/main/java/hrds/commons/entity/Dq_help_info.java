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
 * 系统帮助提示信息表
 */
@Table(tableName = "dq_help_info")
public class Dq_help_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_help_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 系统帮助提示信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("help_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="help_info_id",value="帮助提示编号:",dataType = String.class,required = true)
	private String help_info_id;
	@DocBean(name ="help_info_desc",value="帮助提示描述:",dataType = String.class,required = false)
	private String help_info_desc;
	@DocBean(name ="help_info_dtl",value="帮助提示详细信息:",dataType = String.class,required = false)
	private String help_info_dtl;

	/** 取得：帮助提示编号 */
	public String getHelp_info_id(){
		return help_info_id;
	}
	/** 设置：帮助提示编号 */
	public void setHelp_info_id(String help_info_id){
		this.help_info_id=help_info_id;
	}
	/** 取得：帮助提示描述 */
	public String getHelp_info_desc(){
		return help_info_desc;
	}
	/** 设置：帮助提示描述 */
	public void setHelp_info_desc(String help_info_desc){
		this.help_info_desc=help_info_desc;
	}
	/** 取得：帮助提示详细信息 */
	public String getHelp_info_dtl(){
		return help_info_dtl;
	}
	/** 设置：帮助提示详细信息 */
	public void setHelp_info_dtl(String help_info_dtl){
		this.help_info_dtl=help_info_dtl;
	}
}
