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
 * 机器学习特征过滤字段表
 */
@Table(tableName = "ml_feat_filt_column")
public class Ml_feat_filt_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_feat_filt_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习特征过滤字段表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("featfiltc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long featfiltc_id; //特征过滤字段编号
	private String featf_column; //特征过滤字段
	private Long featfilt_id; //特征过滤编号

	/** 取得：特征过滤字段编号 */
	public Long getFeatfiltc_id(){
		return featfiltc_id;
	}
	/** 设置：特征过滤字段编号 */
	public void setFeatfiltc_id(Long featfiltc_id){
		this.featfiltc_id=featfiltc_id;
	}
	/** 设置：特征过滤字段编号 */
	public void setFeatfiltc_id(String featfiltc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(featfiltc_id)){
			this.featfiltc_id=new Long(featfiltc_id);
		}
	}
	/** 取得：特征过滤字段 */
	public String getFeatf_column(){
		return featf_column;
	}
	/** 设置：特征过滤字段 */
	public void setFeatf_column(String featf_column){
		this.featf_column=featf_column;
	}
	/** 取得：特征过滤编号 */
	public Long getFeatfilt_id(){
		return featfilt_id;
	}
	/** 设置：特征过滤编号 */
	public void setFeatfilt_id(Long featfilt_id){
		this.featfilt_id=featfilt_id;
	}
	/** 设置：特征过滤编号 */
	public void setFeatfilt_id(String featfilt_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(featfilt_id)){
			this.featfilt_id=new Long(featfilt_id);
		}
	}
}
