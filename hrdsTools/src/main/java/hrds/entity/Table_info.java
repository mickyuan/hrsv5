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
 * 数据库对应表
 */
@Table(tableName = "table_info")
public class Table_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据库对应表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long table_id; //表名ID
	private String table_name; //表名
	private String table_ch_name; //中文名称
	private String table_count; //记录数
	private String source_tableid; //源表ID
	private String valid_s_date; //有效开始日期
	private String valid_e_date; //有效结束日期
	private String storage_type; //储存方式
	private String sql; //自定义sql语句
	private String remark; //备注
	private String is_user_defined; //是否自定义sql采集
	private Long database_id; //数据库设置id
	private String ti_or; //清洗顺序
	private String is_md5; //是否使用MD5

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
	/** 取得：表名 */
	public String getTable_name(){
		return table_name;
	}
	/** 设置：表名 */
	public void setTable_name(String table_name){
		this.table_name=table_name;
	}
	/** 取得：中文名称 */
	public String getTable_ch_name(){
		return table_ch_name;
	}
	/** 设置：中文名称 */
	public void setTable_ch_name(String table_ch_name){
		this.table_ch_name=table_ch_name;
	}
	/** 取得：记录数 */
	public String getTable_count(){
		return table_count;
	}
	/** 设置：记录数 */
	public void setTable_count(String table_count){
		this.table_count=table_count;
	}
	/** 取得：源表ID */
	public String getSource_tableid(){
		return source_tableid;
	}
	/** 设置：源表ID */
	public void setSource_tableid(String source_tableid){
		this.source_tableid=source_tableid;
	}
	/** 取得：有效开始日期 */
	public String getValid_s_date(){
		return valid_s_date;
	}
	/** 设置：有效开始日期 */
	public void setValid_s_date(String valid_s_date){
		this.valid_s_date=valid_s_date;
	}
	/** 取得：有效结束日期 */
	public String getValid_e_date(){
		return valid_e_date;
	}
	/** 设置：有效结束日期 */
	public void setValid_e_date(String valid_e_date){
		this.valid_e_date=valid_e_date;
	}
	/** 取得：储存方式 */
	public String getStorage_type(){
		return storage_type;
	}
	/** 设置：储存方式 */
	public void setStorage_type(String storage_type){
		this.storage_type=storage_type;
	}
	/** 取得：自定义sql语句 */
	public String getSql(){
		return sql;
	}
	/** 设置：自定义sql语句 */
	public void setSql(String sql){
		this.sql=sql;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：是否自定义sql采集 */
	public String getIs_user_defined(){
		return is_user_defined;
	}
	/** 设置：是否自定义sql采集 */
	public void setIs_user_defined(String is_user_defined){
		this.is_user_defined=is_user_defined;
	}
	/** 取得：数据库设置id */
	public Long getDatabase_id(){
		return database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(Long database_id){
		this.database_id=database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(String database_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(database_id)){
			this.database_id=new Long(database_id);
		}
	}
	/** 取得：清洗顺序 */
	public String getTi_or(){
		return ti_or;
	}
	/** 设置：清洗顺序 */
	public void setTi_or(String ti_or){
		this.ti_or=ti_or;
	}
	/** 取得：是否使用MD5 */
	public String getIs_md5(){
		return is_md5;
	}
	/** 设置：是否使用MD5 */
	public void setIs_md5(String is_md5){
		this.is_md5=is_md5;
	}
}
