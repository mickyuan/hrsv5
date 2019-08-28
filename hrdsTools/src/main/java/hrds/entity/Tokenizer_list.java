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
 * 分词器列表
 */
@Table(tableName = "tokenizer_list")
public class Tokenizer_list extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "tokenizer_list";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 分词器列表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tokenizer_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long tokenizer_id; //分词器编号
	private String tokenizer_name; //分词器名称
	private String is_flag; //是否标识

	/** 取得：分词器编号 */
	public Long getTokenizer_id(){
		return tokenizer_id;
	}
	/** 设置：分词器编号 */
	public void setTokenizer_id(Long tokenizer_id){
		this.tokenizer_id=tokenizer_id;
	}
	/** 设置：分词器编号 */
	public void setTokenizer_id(String tokenizer_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(tokenizer_id)){
			this.tokenizer_id=new Long(tokenizer_id);
		}
	}
	/** 取得：分词器名称 */
	public String getTokenizer_name(){
		return tokenizer_name;
	}
	/** 设置：分词器名称 */
	public void setTokenizer_name(String tokenizer_name){
		this.tokenizer_name=tokenizer_name;
	}
	/** 取得：是否标识 */
	public String getIs_flag(){
		return is_flag;
	}
	/** 设置：是否标识 */
	public void setIs_flag(String is_flag){
		this.is_flag=is_flag;
	}
}
