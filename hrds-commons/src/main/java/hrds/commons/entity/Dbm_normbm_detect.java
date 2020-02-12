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
 * 数据对标标准对标检测记录表
 */
@Table(tableName = "dbm_normbm_detect")
public class Dbm_normbm_detect extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_normbm_detect";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标标准对标检测记录表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("detect_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="detect_id",value="检测主键:",dataType = String.class,required = true)
	private String detect_id;
	@DocBean(name ="detect_name",value="检测记名:",dataType = String.class,required = true)
	private String detect_name;
	@DocBean(name ="source_type",value="数据来源类型(DataSourceType):ISL-贴源层_01<ISL> DCL-贴源层<DCL> DPL-加工层<DPL> DML-集市层<DML> SFL-系统层<SFL> AML-AI模型层<AML> DQC-管控层<DQC> UDL-自定义层<UDL> ",dataType = String.class,required = true)
	private String source_type;
	@DocBean(name ="detect_status",value="检测状态(是否发布)(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String detect_status;
	@DocBean(name ="detect_sdate",value="检测开始日期:",dataType = String.class,required = true)
	private String detect_sdate;
	@DocBean(name ="detect_stime",value="检测开始时间:",dataType = String.class,required = true)
	private String detect_stime;
	@DocBean(name ="detect_edate",value="检测结束日期:",dataType = String.class,required = true)
	private String detect_edate;
	@DocBean(name ="detect_etime",value="检测结束时间:",dataType = String.class,required = true)
	private String detect_etime;
	@DocBean(name ="create_user",value="创建人:",dataType = String.class,required = true)
	private String create_user;
	@DocBean(name ="dnd_remark",value="备注:",dataType = String.class,required = false)
	private String dnd_remark;
	@DocBean(name ="is_import",value="是否为外部导入数据(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_import;
	@DocBean(name ="dbm_mode",value="对标方式(DbmMode):1-数据对标<ShuJuDuiBiao> 2-表结构对标<BiaoJieGouDuiBiao> ",dataType = String.class,required = false)
	private String dbm_mode;

	/** 取得：检测主键 */
	public String getDetect_id(){
		return detect_id;
	}
	/** 设置：检测主键 */
	public void setDetect_id(String detect_id){
		this.detect_id=detect_id;
	}
	/** 取得：检测记名 */
	public String getDetect_name(){
		return detect_name;
	}
	/** 设置：检测记名 */
	public void setDetect_name(String detect_name){
		this.detect_name=detect_name;
	}
	/** 取得：数据来源类型 */
	public String getSource_type(){
		return source_type;
	}
	/** 设置：数据来源类型 */
	public void setSource_type(String source_type){
		this.source_type=source_type;
	}
	/** 取得：检测状态(是否发布) */
	public String getDetect_status(){
		return detect_status;
	}
	/** 设置：检测状态(是否发布) */
	public void setDetect_status(String detect_status){
		this.detect_status=detect_status;
	}
	/** 取得：检测开始日期 */
	public String getDetect_sdate(){
		return detect_sdate;
	}
	/** 设置：检测开始日期 */
	public void setDetect_sdate(String detect_sdate){
		this.detect_sdate=detect_sdate;
	}
	/** 取得：检测开始时间 */
	public String getDetect_stime(){
		return detect_stime;
	}
	/** 设置：检测开始时间 */
	public void setDetect_stime(String detect_stime){
		this.detect_stime=detect_stime;
	}
	/** 取得：检测结束日期 */
	public String getDetect_edate(){
		return detect_edate;
	}
	/** 设置：检测结束日期 */
	public void setDetect_edate(String detect_edate){
		this.detect_edate=detect_edate;
	}
	/** 取得：检测结束时间 */
	public String getDetect_etime(){
		return detect_etime;
	}
	/** 设置：检测结束时间 */
	public void setDetect_etime(String detect_etime){
		this.detect_etime=detect_etime;
	}
	/** 取得：创建人 */
	public String getCreate_user(){
		return create_user;
	}
	/** 设置：创建人 */
	public void setCreate_user(String create_user){
		this.create_user=create_user;
	}
	/** 取得：备注 */
	public String getDnd_remark(){
		return dnd_remark;
	}
	/** 设置：备注 */
	public void setDnd_remark(String dnd_remark){
		this.dnd_remark=dnd_remark;
	}
	/** 取得：是否为外部导入数据 */
	public String getIs_import(){
		return is_import;
	}
	/** 设置：是否为外部导入数据 */
	public void setIs_import(String is_import){
		this.is_import=is_import;
	}
	/** 取得：对标方式 */
	public String getDbm_mode(){
		return dbm_mode;
	}
	/** 设置：对标方式 */
	public void setDbm_mode(String dbm_mode){
		this.dbm_mode=dbm_mode;
	}
}
