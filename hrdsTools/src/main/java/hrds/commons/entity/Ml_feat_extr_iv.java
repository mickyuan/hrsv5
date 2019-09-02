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
 * 机器学习特征抽取自变量表
 */
@Table(tableName = "ml_feat_extr_iv")
public class Ml_feat_extr_iv extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_feat_extr_iv";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习特征抽取自变量表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("featextr_iv_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long featextr_iv_id; //特征抽取自变量编号
	private String iv_column; //自变量字段
	private Long featextr_id; //特征抽取编号

	/** 取得：特征抽取自变量编号 */
	public Long getFeatextr_iv_id(){
		return featextr_iv_id;
	}
	/** 设置：特征抽取自变量编号 */
	public void setFeatextr_iv_id(Long featextr_iv_id){
		this.featextr_iv_id=featextr_iv_id;
	}
	/** 设置：特征抽取自变量编号 */
	public void setFeatextr_iv_id(String featextr_iv_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(featextr_iv_id)){
			this.featextr_iv_id=new Long(featextr_iv_id);
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
	/** 取得：特征抽取编号 */
	public Long getFeatextr_id(){
		return featextr_id;
	}
	/** 设置：特征抽取编号 */
	public void setFeatextr_id(Long featextr_id){
		this.featextr_id=featextr_id;
	}
	/** 设置：特征抽取编号 */
	public void setFeatextr_id(String featextr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(featextr_id)){
			this.featextr_id=new Long(featextr_id);
		}
	}
}
