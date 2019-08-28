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
 * 机器学习支持向量机分类模型自变量表
 */
@Table(tableName = "ml_svmclassi_iv")
public class Ml_svmclassi_iv extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_svmclassi_iv";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习支持向量机分类模型自变量表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("iv_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long iv_id; //自变量编号
	private String iv_column; //自变量字段
	private Long model_id; //模型编号

	/** 取得：自变量编号 */
	public Long getIv_id(){
		return iv_id;
	}
	/** 设置：自变量编号 */
	public void setIv_id(Long iv_id){
		this.iv_id=iv_id;
	}
	/** 设置：自变量编号 */
	public void setIv_id(String iv_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(iv_id)){
			this.iv_id=new Long(iv_id);
		}
	}
	/** 取得：自变量字段 */
	public String getIv_column(){
		return iv_column;
	}
	/** 设置：自变量字段 */
	public void setIv_column(String iv_column){
		this.iv_column=iv_column;
	}
	/** 取得：模型编号 */
	public Long getModel_id(){
		return model_id;
	}
	/** 设置：模型编号 */
	public void setModel_id(Long model_id){
		this.model_id=model_id;
	}
	/** 设置：模型编号 */
	public void setModel_id(String model_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(model_id)){
			this.model_id=new Long(model_id);
		}
	}
}
