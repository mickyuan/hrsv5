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
 * 机器学习用户脚本结果字段表
 */
@Table(tableName = "ml_custscricol")
public class Ml_custscricol extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_custscricol";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习用户脚本结果字段表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long column_id; //字段编号
	private String column_name; //字段英文名称
	private String column_type; //字段类型
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long script_id; //脚本编号

	/** 取得：字段编号 */
	public Long getColumn_id(){
		return column_id;
	}
	/** 设置：字段编号 */
	public void setColumn_id(Long column_id){
		this.column_id=column_id;
	}
	/** 设置：字段编号 */
	public void setColumn_id(String column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(column_id)){
			this.column_id=new Long(column_id);
		}
	}
	/** 取得：字段英文名称 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：字段英文名称 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：字段类型 */
	public String getColumn_type(){
		return column_type;
	}
	/** 设置：字段类型 */
	public void setColumn_type(String column_type){
		this.column_type=column_type;
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
	/** 取得：脚本编号 */
	public Long getScript_id(){
		return script_id;
	}
	/** 设置：脚本编号 */
	public void setScript_id(Long script_id){
		this.script_id=script_id;
	}
	/** 设置：脚本编号 */
	public void setScript_id(String script_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(script_id)){
			this.script_id=new Long(script_id);
		}
	}
}
