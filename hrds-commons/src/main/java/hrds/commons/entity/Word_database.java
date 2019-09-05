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
 * 词条库表
 */
@Table(tableName = "word_database")
public class Word_database extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "word_database";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 词条库表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("word_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long word_id; //词条编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String word; //词条
	private String word_nature; //词性
	private String is_flag_product; //是否已生成
	private String is_flag_select; //是否已选择
	private Long word_file_id; //词条文件编号
	private Long create_id; //用户ID
	private String word_property; //词的属性
	private String world_frequency; //词的频率

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
	/** 取得：是否已生成 */
	public String getIs_flag_product(){
		return is_flag_product;
	}
	/** 设置：是否已生成 */
	public void setIs_flag_product(String is_flag_product){
		this.is_flag_product=is_flag_product;
	}
	/** 取得：是否已选择 */
	public String getIs_flag_select(){
		return is_flag_select;
	}
	/** 设置：是否已选择 */
	public void setIs_flag_select(String is_flag_select){
		this.is_flag_select=is_flag_select;
	}
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
	/** 取得：用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
	/** 取得：词的属性 */
	public String getWord_property(){
		return word_property;
	}
	/** 设置：词的属性 */
	public void setWord_property(String word_property){
		this.word_property=word_property;
	}
	/** 取得：词的频率 */
	public String getWorld_frequency(){
		return world_frequency;
	}
	/** 设置：词的频率 */
	public void setWorld_frequency(String world_frequency){
		this.world_frequency=world_frequency;
	}
}
