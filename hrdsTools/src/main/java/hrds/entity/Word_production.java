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
 * 词条选择结果表
 */
@Table(tableName = "word_production")
public class Word_production extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "word_production";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 词条选择结果表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("word_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String word; //词条
	private String word_nature; //词性
	private Long word_id; //词条编号
	private Long tokenizer_id; //分词器编号

	/** 取得：词条 */
	public String getWord(){
		return word;
	}
	/** 设置：词条 */
	public void setWord(String word){
		this.word=word;
	}
	/** 取得：词性 */
	public String getWord_nature(){
		return word_nature;
	}
	/** 设置：词性 */
	public void setWord_nature(String word_nature){
		this.word_nature=word_nature;
	}
	/** 取得：词条编号 */
	public Long getWord_id(){
		return word_id;
	}
	/** 设置：词条编号 */
	public void setWord_id(Long word_id){
		this.word_id=word_id;
	}
	/** 设置：词条编号 */
	public void setWord_id(String word_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(word_id)){
			this.word_id=new Long(word_id);
		}
	}
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
}
