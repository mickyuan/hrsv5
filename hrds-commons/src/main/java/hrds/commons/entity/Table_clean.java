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
 * 表清洗参数信息
 */
@Table(tableName = "table_clean")
public class Table_clean extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_clean";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 表清洗参数信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("c_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long c_id; //清洗参数编号
	private String clean_type; //清洗方式
	private String character_filling; //补齐字符
	private Long filling_length; //补齐长度
	private String field; //原字段
	private String replace_feild; //替换字段
	private String filling_type; //补齐方式
	private Long table_id; //表名ID

	/** 取得：清洗参数编号 */
	public Long getC_id(){
		return c_id;
	}
	/** 设置：清洗参数编号 */
	public void setC_id(Long c_id){
		this.c_id=c_id;
	}
	/** 设置：清洗参数编号 */
	public void setC_id(String c_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(c_id)){
			this.c_id=new Long(c_id);
		}
	}
	/** 取得：清洗方式 */
	public String getClean_type(){
		return clean_type;
	}
	/** 设置：清洗方式 */
	public void setClean_type(String clean_type){
		this.clean_type=clean_type;
	}
	/** 取得：补齐字符 */
	public String getCharacter_filling(){
		return character_filling;
	}
	/** 设置：补齐字符 */
	public void setCharacter_filling(String character_filling){
		this.character_filling=character_filling;
	}
	/** 取得：补齐长度 */
	public Long getFilling_length(){
		return filling_length;
	}
	/** 设置：补齐长度 */
	public void setFilling_length(Long filling_length){
		this.filling_length=filling_length;
	}
	/** 设置：补齐长度 */
	public void setFilling_length(String filling_length){
		if(!fd.ng.core.utils.StringUtil.isEmpty(filling_length)){
			this.filling_length=new Long(filling_length);
		}
	}
	/** 取得：原字段 */
	public String getField(){
		return field;
	}
	/** 设置：原字段 */
	public void setField(String field){
		this.field=field;
	}
	/** 取得：替换字段 */
	public String getReplace_feild(){
		return replace_feild;
	}
	/** 设置：替换字段 */
	public void setReplace_feild(String replace_feild){
		this.replace_feild=replace_feild;
	}
	/** 取得：补齐方式 */
	public String getFilling_type(){
		return filling_type;
	}
	/** 设置：补齐方式 */
	public void setFilling_type(String filling_type){
		this.filling_type=filling_type;
	}
	/** 取得：表名ID */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
	}
}
