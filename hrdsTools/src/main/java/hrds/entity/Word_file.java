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
 * 词条文件表
 */
@Table(tableName = "word_file")
public class Word_file extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "word_file";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 词条文件表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("word_file_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long word_file_id; //词条文件编号
	private String word_file_name; //词条文件名
	private String create_id; //创建人编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long tokenizer_id; //分词器编号

	/** 取得：词条文件编号 */
	public Long getWord_file_id(){
		return word_file_id;
	}
	/** 设置：词条文件编号 */
	public void setWord_file_id(Long word_file_id){
		this.word_file_id=word_file_id;
	}
	/** 设置：词条文件编号 */
	public void setWord_file_id(String word_file_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(word_file_id)){
			this.word_file_id=new Long(word_file_id);
		}
	}
	/** 取得：词条文件名 */
	public String getWord_file_name(){
		return word_file_name;
	}
	/** 设置：词条文件名 */
	public void setWord_file_name(String word_file_name){
		this.word_file_name=word_file_name;
	}
	/** 取得：创建人编号 */
	public String getCreate_id(){
		return create_id;
	}
	/** 设置：创建人编号 */
	public void setCreate_id(String create_id){
		this.create_id=create_id;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
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
