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
 * 流数据管理消费至druid
 */
@Table(tableName = "sdm_con_druid")
public class Sdm_con_druid extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_druid";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据管理消费至druid */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("druid_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long druid_id; //druid编号
	private String table_name; //druid英文表名
	private String timestamp_colum; //时间戳字段
	private String timestamp_format; //时间戳字段格式
	private String timestamp_pat; //时间戳转换表达式
	private String data_type; //数据格式类型
	private String data_columns; //数据字段
	private String data_pattern; //数据格式转换表达式
	private String data_fun; //数据格式转换函数
	private Long sdm_des_id; //配置id
	private String is_topicasdruid; //是否使用topic名作为druid表名
	private String druid_servtype; //druid服务类型
	private String table_cname; //druid中文表名

	/** 取得：druid编号 */
	public Long getDruid_id(){
		return druid_id;
	}
	/** 设置：druid编号 */
	public void setDruid_id(Long druid_id){
		this.druid_id=druid_id;
	}
	/** 设置：druid编号 */
	public void setDruid_id(String druid_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(druid_id)){
			this.druid_id=new Long(druid_id);
		}
	}
	/** 取得：druid英文表名 */
	public String getTable_name(){
		return table_name;
	}
	/** 设置：druid英文表名 */
	public void setTable_name(String table_name){
		this.table_name=table_name;
	}
	/** 取得：时间戳字段 */
	public String getTimestamp_colum(){
		return timestamp_colum;
	}
	/** 设置：时间戳字段 */
	public void setTimestamp_colum(String timestamp_colum){
		this.timestamp_colum=timestamp_colum;
	}
	/** 取得：时间戳字段格式 */
	public String getTimestamp_format(){
		return timestamp_format;
	}
	/** 设置：时间戳字段格式 */
	public void setTimestamp_format(String timestamp_format){
		this.timestamp_format=timestamp_format;
	}
	/** 取得：时间戳转换表达式 */
	public String getTimestamp_pat(){
		return timestamp_pat;
	}
	/** 设置：时间戳转换表达式 */
	public void setTimestamp_pat(String timestamp_pat){
		this.timestamp_pat=timestamp_pat;
	}
	/** 取得：数据格式类型 */
	public String getData_type(){
		return data_type;
	}
	/** 设置：数据格式类型 */
	public void setData_type(String data_type){
		this.data_type=data_type;
	}
	/** 取得：数据字段 */
	public String getData_columns(){
		return data_columns;
	}
	/** 设置：数据字段 */
	public void setData_columns(String data_columns){
		this.data_columns=data_columns;
	}
	/** 取得：数据格式转换表达式 */
	public String getData_pattern(){
		return data_pattern;
	}
	/** 设置：数据格式转换表达式 */
	public void setData_pattern(String data_pattern){
		this.data_pattern=data_pattern;
	}
	/** 取得：数据格式转换函数 */
	public String getData_fun(){
		return data_fun;
	}
	/** 设置：数据格式转换函数 */
	public void setData_fun(String data_fun){
		this.data_fun=data_fun;
	}
	/** 取得：配置id */
	public Long getSdm_des_id(){
		return sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(Long sdm_des_id){
		this.sdm_des_id=sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(String sdm_des_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_des_id)){
			this.sdm_des_id=new Long(sdm_des_id);
		}
	}
	/** 取得：是否使用topic名作为druid表名 */
	public String getIs_topicasdruid(){
		return is_topicasdruid;
	}
	/** 设置：是否使用topic名作为druid表名 */
	public void setIs_topicasdruid(String is_topicasdruid){
		this.is_topicasdruid=is_topicasdruid;
	}
	/** 取得：druid服务类型 */
	public String getDruid_servtype(){
		return druid_servtype;
	}
	/** 设置：druid服务类型 */
	public void setDruid_servtype(String druid_servtype){
		this.druid_servtype=druid_servtype;
	}
	/** 取得：druid中文表名 */
	public String getTable_cname(){
		return table_cname;
	}
	/** 设置：druid中文表名 */
	public void setTable_cname(String table_cname){
		this.table_cname=table_cname;
	}
}
