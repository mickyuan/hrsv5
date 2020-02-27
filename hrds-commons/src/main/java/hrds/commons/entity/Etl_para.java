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
 * 参数登记
 */
@Table(tableName = "etl_para")
public class Etl_para extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_para";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 参数登记 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("para_cd");
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="para_cd",value="变量代码:",dataType = String.class,required = true)
	private String para_cd;
	@DocBean(name ="para_val",value="变量值:",dataType = String.class,required = false)
	private String para_val;
	@DocBean(name ="para_type",value="变量类型(ParamType):url-路径<LuJing> param-参数<CanShu> ",dataType = String.class,required = false)
	private String para_type;
	@DocBean(name ="etl_sys_cd",value="工程代码:",dataType = String.class,required = true)
	private String etl_sys_cd;
	@DocBean(name ="para_desc",value="作业描述:",dataType = String.class,required = false)
	private String para_desc;

	/** 取得：变量代码 */
	public String getPara_cd(){
		return para_cd;
	}
	/** 设置：变量代码 */
	public void setPara_cd(String para_cd){
		this.para_cd=para_cd;
	}
	/** 取得：变量值 */
	public String getPara_val(){
		return para_val;
	}
	/** 设置：变量值 */
	public void setPara_val(String para_val){
		this.para_val=para_val;
	}
	/** 取得：变量类型 */
	public String getPara_type(){
		return para_type;
	}
	/** 设置：变量类型 */
	public void setPara_type(String para_type){
		this.para_type=para_type;
	}
	/** 取得：工程代码 */
	public String getEtl_sys_cd(){
		return etl_sys_cd;
	}
	/** 设置：工程代码 */
	public void setEtl_sys_cd(String etl_sys_cd){
		this.etl_sys_cd=etl_sys_cd;
	}
	/** 取得：作业描述 */
	public String getPara_desc(){
		return para_desc;
	}
	/** 设置：作业描述 */
	public void setPara_desc(String para_desc){
		this.para_desc=para_desc;
	}
}
