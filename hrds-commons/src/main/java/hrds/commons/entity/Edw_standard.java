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
 * 数据仓库字段标准化表
 */
@Table(tableName = "edw_standard")
public class Edw_standard extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_standard";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据仓库字段标准化表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("colname");
		__tmpPKS.add("table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String coltra; //转换类型
	private String colformat; //字段格式
	private String tratype; //转换后数据类型
	private String colname; //列名
	private Long table_id; //表名ID

	/** 取得：转换类型 */
	public String getColtra(){
		return coltra;
	}
	/** 设置：转换类型 */
	public void setColtra(String coltra){
		this.coltra=coltra;
	}
	/** 取得：字段格式 */
	public String getColformat(){
		return colformat;
	}
	/** 设置：字段格式 */
	public void setColformat(String colformat){
		this.colformat=colformat;
	}
	/** 取得：转换后数据类型 */
	public String getTratype(){
		return tratype;
	}
	/** 设置：转换后数据类型 */
	public void setTratype(String tratype){
		this.tratype=tratype;
	}
	/** 取得：列名 */
	public String getColname(){
		return colname;
	}
	/** 设置：列名 */
	public void setColname(String colname){
		this.colname=colname;
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
