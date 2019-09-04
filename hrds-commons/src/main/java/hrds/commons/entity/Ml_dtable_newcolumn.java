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
 * 机器学习预处理新字段信息表
 */
@Table(tableName = "ml_dtable_newcolumn")
public class Ml_dtable_newcolumn extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_dtable_newcolumn";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习预处理新字段信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long table_column_id; //数据源表字段编号
	private String column_cn_name; //字段中文名
	private String ocolumn_en_name; //原始字段英文名
	private String ocolumn_type; //字段类型
	private String newcolumn_name; //新字段英文名称
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String use_isflag; //是否使用
	private Long datapo_info_id; //数据预处理其它信息编号

	/** 取得：数据源表字段编号 */
	public Long getTable_column_id(){
		return table_column_id;
	}
	/** 设置：数据源表字段编号 */
	public void setTable_column_id(Long table_column_id){
		this.table_column_id=table_column_id;
	}
	/** 设置：数据源表字段编号 */
	public void setTable_column_id(String table_column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_column_id)){
			this.table_column_id=new Long(table_column_id);
		}
	}
	/** 取得：字段中文名 */
	public String getColumn_cn_name(){
		return column_cn_name;
	}
	/** 设置：字段中文名 */
	public void setColumn_cn_name(String column_cn_name){
		this.column_cn_name=column_cn_name;
	}
	/** 取得：原始字段英文名 */
	public String getOcolumn_en_name(){
		return ocolumn_en_name;
	}
	/** 设置：原始字段英文名 */
	public void setOcolumn_en_name(String ocolumn_en_name){
		this.ocolumn_en_name=ocolumn_en_name;
	}
	/** 取得：字段类型 */
	public String getOcolumn_type(){
		return ocolumn_type;
	}
	/** 设置：字段类型 */
	public void setOcolumn_type(String ocolumn_type){
		this.ocolumn_type=ocolumn_type;
	}
	/** 取得：新字段英文名称 */
	public String getNewcolumn_name(){
		return newcolumn_name;
	}
	/** 设置：新字段英文名称 */
	public void setNewcolumn_name(String newcolumn_name){
		this.newcolumn_name=newcolumn_name;
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
	/** 取得：是否使用 */
	public String getUse_isflag(){
		return use_isflag;
	}
	/** 设置：是否使用 */
	public void setUse_isflag(String use_isflag){
		this.use_isflag=use_isflag;
	}
	/** 取得：数据预处理其它信息编号 */
	public Long getDatapo_info_id(){
		return datapo_info_id;
	}
	/** 设置：数据预处理其它信息编号 */
	public void setDatapo_info_id(Long datapo_info_id){
		this.datapo_info_id=datapo_info_id;
	}
	/** 设置：数据预处理其它信息编号 */
	public void setDatapo_info_id(String datapo_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datapo_info_id)){
			this.datapo_info_id=new Long(datapo_info_id);
		}
	}
}
