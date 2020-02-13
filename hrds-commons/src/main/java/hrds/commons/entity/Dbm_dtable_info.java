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
 * 数据对标标准对标检测表信息表
 */
@Table(tableName = "dbm_dtable_info")
public class Dbm_dtable_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_dtable_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标标准对标检测表信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dbm_tableid");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dbm_tableid",value="检测表主键:",dataType = Long.class,required = true)
	private Long dbm_tableid;
	@DocBean(name ="table_cname",value="表中文名称:",dataType = String.class,required = true)
	private String table_cname;
	@DocBean(name ="table_ename",value="表英文名称:",dataType = String.class,required = true)
	private String table_ename;
	@DocBean(name ="table_remark",value="表描述信息:",dataType = String.class,required = false)
	private String table_remark;
	@DocBean(name ="is_external",value="是否为外部数据源(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_external;
	@DocBean(name ="detect_id",value="检测主键:",dataType = String.class,required = true)
	private String detect_id;
	@DocBean(name ="table_id",value="表名ID:",dataType = Long.class,required = false)
	private Long table_id;
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = false)
	private Long source_id;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = false)
	private Long agent_id;
	@DocBean(name ="database_id",value="数据库设置id:",dataType = Long.class,required = false)
	private Long database_id;

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
	/** 取得：表中文名称 */
	public String getTable_cname(){
		return table_cname;
	}
	/** 设置：表中文名称 */
	public void setTable_cname(String table_cname){
		this.table_cname=table_cname;
	}
	/** 取得：表英文名称 */
	public String getTable_ename(){
		return table_ename;
	}
	/** 设置：表英文名称 */
	public void setTable_ename(String table_ename){
		this.table_ename=table_ename;
	}
	/** 取得：表描述信息 */
	public String getTable_remark(){
		return table_remark;
	}
	/** 设置：表描述信息 */
	public void setTable_remark(String table_remark){
		this.table_remark=table_remark;
	}
	/** 取得：是否为外部数据源 */
	public String getIs_external(){
		return is_external;
	}
	/** 设置：是否为外部数据源 */
	public void setIs_external(String is_external){
		this.is_external=is_external;
	}
	/** 取得：检测主键 */
	public String getDetect_id(){
		return detect_id;
	}
	/** 设置：检测主键 */
	public void setDetect_id(String detect_id){
		this.detect_id=detect_id;
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
