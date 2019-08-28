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
 * 机器学习卡方检验字段表
 */
@Table(tableName = "ml_chisquared_column")
public class Ml_chisquared_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_chisquared_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习卡方检验字段表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("chisquatc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long chisquatc_id; //卡方检验字段编号
	private String chisquatestc; //卡方检验字段
	private Long chisquared_id; //卡方检验编号

	/** 取得：卡方检验字段编号 */
	public Long getChisquatc_id(){
		return chisquatc_id;
	}
	/** 设置：卡方检验字段编号 */
	public void setChisquatc_id(Long chisquatc_id){
		this.chisquatc_id=chisquatc_id;
	}
	/** 设置：卡方检验字段编号 */
	public void setChisquatc_id(String chisquatc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(chisquatc_id)){
			this.chisquatc_id=new Long(chisquatc_id);
		}
	}
	/** 取得：卡方检验字段 */
	public String getChisquatestc(){
		return chisquatestc;
	}
	/** 设置：卡方检验字段 */
	public void setChisquatestc(String chisquatestc){
		this.chisquatestc=chisquatestc;
	}
	/** 取得：卡方检验编号 */
	public Long getChisquared_id(){
		return chisquared_id;
	}
	/** 设置：卡方检验编号 */
	public void setChisquared_id(Long chisquared_id){
		this.chisquared_id=chisquared_id;
	}
	/** 设置：卡方检验编号 */
	public void setChisquared_id(String chisquared_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(chisquared_id)){
			this.chisquared_id=new Long(chisquared_id);
		}
	}
}
