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
 * 机器学习方差分析因变量表
 */
@Table(tableName = "ml_varianceanal_dv")
public class Ml_varianceanal_dv extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_varianceanal_dv";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习方差分析因变量表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("varianaly_dv_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String dv_column; //因变量字段
	private Long varianaly_dv_id; //方差分析因变量编号
	private Long varianal_iv_id; //方差分析自变量编号

	/** 取得：因变量字段 */
	public String getDv_column(){
		return dv_column;
	}
	/** 设置：因变量字段 */
	public void setDv_column(String dv_column){
		this.dv_column=dv_column;
	}
	/** 取得：方差分析因变量编号 */
	public Long getVarianaly_dv_id(){
		return varianaly_dv_id;
	}
	/** 设置：方差分析因变量编号 */
	public void setVarianaly_dv_id(Long varianaly_dv_id){
		this.varianaly_dv_id=varianaly_dv_id;
	}
	/** 设置：方差分析因变量编号 */
	public void setVarianaly_dv_id(String varianaly_dv_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(varianaly_dv_id)){
			this.varianaly_dv_id=new Long(varianaly_dv_id);
		}
	}
	/** 取得：方差分析自变量编号 */
	public Long getVarianal_iv_id(){
		return varianal_iv_id;
	}
	/** 设置：方差分析自变量编号 */
	public void setVarianal_iv_id(Long varianal_iv_id){
		this.varianal_iv_id=varianal_iv_id;
	}
	/** 设置：方差分析自变量编号 */
	public void setVarianal_iv_id(String varianal_iv_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(varianal_iv_id)){
			this.varianal_iv_id=new Long(varianal_iv_id);
		}
	}
}
