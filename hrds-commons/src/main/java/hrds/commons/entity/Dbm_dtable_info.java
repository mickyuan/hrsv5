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
	@DocBean(name ="source_type",value="数据来源类型(DataSourceType):ISL-贴源层_01<ISL> DCL-贴源层<DCL> DPL-加工层<DPL> DML-集市层<DML> SFL-系统层<SFL> AML-AI模型层<AML> DQC-管控层<DQC> UDL-自定义层<UDL> ",dataType = String.class,required = true)
	private String source_type;
	@DocBean(name ="dsl_id",value="存储层配置ID:",dataType = Long.class,required = true)
	private Long dsl_id;

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
	/** 取得：数据来源类型 */
	public String getSource_type(){
		return source_type;
	}
	/** 设置：数据来源类型 */
	public void setSource_type(String source_type){
		this.source_type=source_type;
	}
	/** 取得：存储层配置ID */
	public Long getDsl_id(){
		return dsl_id;
	}
	/** 设置：存储层配置ID */
	public void setDsl_id(Long dsl_id){
		this.dsl_id=dsl_id;
	}
	/** 设置：存储层配置ID */
	public void setDsl_id(String dsl_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dsl_id)){
			this.dsl_id=new Long(dsl_id);
		}
	}
}
