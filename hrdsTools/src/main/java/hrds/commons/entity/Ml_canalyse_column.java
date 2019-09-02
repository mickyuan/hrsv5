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
 * 机器学习数据探索相关性分析字段表
 */
@Table(tableName = "ml_canalyse_column")
public class Ml_canalyse_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_canalyse_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习数据探索相关性分析字段表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("corranalc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long corranalc_id; //相关性分析字段编号
	private String corranal_colu; //相关性分析字段
	private Long correlation_id; //相关性表编号

	/** 取得：相关性分析字段编号 */
	public Long getCorranalc_id(){
		return corranalc_id;
	}
	/** 设置：相关性分析字段编号 */
	public void setCorranalc_id(Long corranalc_id){
		this.corranalc_id=corranalc_id;
	}
	/** 设置：相关性分析字段编号 */
	public void setCorranalc_id(String corranalc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(corranalc_id)){
			this.corranalc_id=new Long(corranalc_id);
		}
	}
	/** 取得：相关性分析字段 */
	public String getCorranal_colu(){
		return corranal_colu;
	}
	/** 设置：相关性分析字段 */
	public void setCorranal_colu(String corranal_colu){
		this.corranal_colu=corranal_colu;
	}
	/** 取得：相关性表编号 */
	public Long getCorrelation_id(){
		return correlation_id;
	}
	/** 设置：相关性表编号 */
	public void setCorrelation_id(Long correlation_id){
		this.correlation_id=correlation_id;
	}
	/** 设置：相关性表编号 */
	public void setCorrelation_id(String correlation_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(correlation_id)){
			this.correlation_id=new Long(correlation_id);
		}
	}
}
