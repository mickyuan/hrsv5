package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 数据对标标准对标检测字段信息表
 */
@Table(tableName = "dbm_dtcol_info")
public class Dbm_dtcol_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_dtcol_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标标准对标检测字段信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("col_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="col_id",value="字段主键:",dataType = Long.class,required = true)
	private Long col_id;
	@DocBean(name ="col_cname",value="字段中文名:",dataType = String.class,required = true)
	private String col_cname;
	@DocBean(name ="col_ename",value="字段英文名:",dataType = String.class,required = true)
	private String col_ename;
	@DocBean(name ="data_type",value="数据类型:",dataType = String.class,required = true)
	private String data_type;
	@DocBean(name ="data_len",value="数据长度:",dataType = Long.class,required = true)
	private Long data_len;
	@DocBean(name ="decimal_point",value="小数长度:",dataType = Long.class,required = true)
	private Long decimal_point;
	@DocBean(name ="is_key",value="是否作为主键(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_key;
	@DocBean(name ="is_null",value="是否可为空(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_null;
	@DocBean(name ="default_value",value="默认值:",dataType = String.class,required = false)
	private String default_value;
	@DocBean(name ="dbm_tableid",value="检测表主键:",dataType = Long.class,required = true)
	private Long dbm_tableid;
	@DocBean(name ="col_remark",value="字段描述:",dataType = String.class,required = false)
	private String col_remark;
	@DocBean(name ="detect_id",value="检测主键:",dataType = String.class,required = true)
	private String detect_id;
	@DocBean(name ="column_id",value="字段ID:",dataType = Long.class,required = false)
	private Long column_id;
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = false)
	private Long source_id;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = false)
	private Long agent_id;
	@DocBean(name ="database_id",value="数据库设置id:",dataType = Long.class,required = false)
	private Long database_id;

	/** 取得：字段主键 */
	public Long getCol_id(){
		return col_id;
	}
	/** 设置：字段主键 */
	public void setCol_id(Long col_id){
		this.col_id=col_id;
	}
	/** 设置：字段主键 */
	public void setCol_id(String col_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_id)){
			this.col_id=new Long(col_id);
		}
	}
	/** 取得：字段中文名 */
	public String getCol_cname(){
		return col_cname;
	}
	/** 设置：字段中文名 */
	public void setCol_cname(String col_cname){
		this.col_cname=col_cname;
	}
	/** 取得：字段英文名 */
	public String getCol_ename(){
		return col_ename;
	}
	/** 设置：字段英文名 */
	public void setCol_ename(String col_ename){
		this.col_ename=col_ename;
	}
	/** 取得：数据类型 */
	public String getData_type(){
		return data_type;
	}
	/** 设置：数据类型 */
	public void setData_type(String data_type){
		this.data_type=data_type;
	}
	/** 取得：数据长度 */
	public Long getData_len(){
		return data_len;
	}
	/** 设置：数据长度 */
	public void setData_len(Long data_len){
		this.data_len=data_len;
	}
	/** 设置：数据长度 */
	public void setData_len(String data_len){
		if(!fd.ng.core.utils.StringUtil.isEmpty(data_len)){
			this.data_len=new Long(data_len);
		}
	}
	/** 取得：小数长度 */
	public Long getDecimal_point(){
		return decimal_point;
	}
	/** 设置：小数长度 */
	public void setDecimal_point(Long decimal_point){
		this.decimal_point=decimal_point;
	}
	/** 设置：小数长度 */
	public void setDecimal_point(String decimal_point){
		if(!fd.ng.core.utils.StringUtil.isEmpty(decimal_point)){
			this.decimal_point=new Long(decimal_point);
		}
	}
	/** 取得：是否作为主键 */
	public String getIs_key(){
		return is_key;
	}
	/** 设置：是否作为主键 */
	public void setIs_key(String is_key){
		this.is_key=is_key;
	}
	/** 取得：是否可为空 */
	public String getIs_null(){
		return is_null;
	}
	/** 设置：是否可为空 */
	public void setIs_null(String is_null){
		this.is_null=is_null;
	}
	/** 取得：默认值 */
	public String getDefault_value(){
		return default_value;
	}
	/** 设置：默认值 */
	public void setDefault_value(String default_value){
		this.default_value=default_value;
	}
	/** 取得：检测表主键 */
	public Long getDbm_tableid(){
		return dbm_tableid;
	}
	/** 设置：检测表主键 */
	public void setDbm_tableid(Long dbm_tableid){
		this.dbm_tableid=dbm_tableid;
	}
	/** 设置：检测表主键 */
	public void setDbm_tableid(String dbm_tableid){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dbm_tableid)){
			this.dbm_tableid=new Long(dbm_tableid);
		}
	}
	/** 取得：字段描述 */
	public String getCol_remark(){
		return col_remark;
	}
	/** 设置：字段描述 */
	public void setCol_remark(String col_remark){
		this.col_remark=col_remark;
	}
	/** 取得：检测主键 */
	public String getDetect_id(){
		return detect_id;
	}
	/** 设置：检测主键 */
	public void setDetect_id(String detect_id){
		this.detect_id=detect_id;
	}
	/** 取得：字段ID */
	public Long getColumn_id(){
		return column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(Long column_id){
		this.column_id=column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(String column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(column_id)){
			this.column_id=new Long(column_id);
		}
	}
	/** 取得：数据源ID */
	public Long getSource_id(){
		return source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(Long source_id){
		this.source_id=source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(String source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(source_id)){
			this.source_id=new Long(source_id);
		}
	}
	/** 取得：Agent_id */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
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
}
