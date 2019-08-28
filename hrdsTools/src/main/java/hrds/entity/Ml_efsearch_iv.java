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
 * 机器学习穷举法特征搜索自变量表
 */
@Table(tableName = "ml_efsearch_iv")
public class Ml_efsearch_iv extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_efsearch_iv";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习穷举法特征搜索自变量表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("exhafs_iv_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long exhafs_iv_id; //穷举法特征搜索自变量编号
	private String iv_column; //自变量字段
	private Long exhafeats_id; //穷举法特征搜索编号

	/** 取得：穷举法特征搜索自变量编号 */
	public Long getExhafs_iv_id(){
		return exhafs_iv_id;
	}
	/** 设置：穷举法特征搜索自变量编号 */
	public void setExhafs_iv_id(Long exhafs_iv_id){
		this.exhafs_iv_id=exhafs_iv_id;
	}
	/** 设置：穷举法特征搜索自变量编号 */
	public void setExhafs_iv_id(String exhafs_iv_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(exhafs_iv_id)){
			this.exhafs_iv_id=new Long(exhafs_iv_id);
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
	/** 取得：穷举法特征搜索编号 */
	public Long getExhafeats_id(){
		return exhafeats_id;
	}
	/** 设置：穷举法特征搜索编号 */
	public void setExhafeats_id(Long exhafeats_id){
		this.exhafeats_id=exhafeats_id;
	}
	/** 设置：穷举法特征搜索编号 */
	public void setExhafeats_id(String exhafeats_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(exhafeats_id)){
			this.exhafeats_id=new Long(exhafeats_id);
		}
	}
}
