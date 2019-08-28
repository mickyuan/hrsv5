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
 * 翻译基础表
 */
@Table(tableName = "translate_base")
public class Translate_base extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "translate_base";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 翻译基础表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("trn_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String china_name; //名称中文
	private String english_name; //名词英文
	private String eng_name; //名词英文简称
	private Long trn_id; //翻译id

	/** 取得：名称中文 */
	public String getChina_name(){
		return china_name;
	}
	/** 设置：名称中文 */
	public void setChina_name(String china_name){
		this.china_name=china_name;
	}
	/** 取得：名词英文 */
	public String getEnglish_name(){
		return english_name;
	}
	/** 设置：名词英文 */
	public void setEnglish_name(String english_name){
		this.english_name=english_name;
	}
	/** 取得：名词英文简称 */
	public String getEng_name(){
		return eng_name;
	}
	/** 设置：名词英文简称 */
	public void setEng_name(String eng_name){
		this.eng_name=eng_name;
	}
	/** 取得：翻译id */
	public Long getTrn_id(){
		return trn_id;
	}
	/** 设置：翻译id */
	public void setTrn_id(Long trn_id){
		this.trn_id=trn_id;
	}
	/** 设置：翻译id */
	public void setTrn_id(String trn_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(trn_id)){
			this.trn_id=new Long(trn_id);
		}
	}
}
