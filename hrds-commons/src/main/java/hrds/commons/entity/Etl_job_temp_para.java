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
 * 作业模版参数表
 */
@Table(tableName = "etl_job_temp_para")
public class Etl_job_temp_para extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_job_temp_para";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 作业模版参数表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_temp_para_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="etl_temp_para_id",value="模版参数主键:",dataType = Long.class,required = true)
	private Long etl_temp_para_id;
	@DocBean(name ="etl_para_type",value="参数类型:",dataType = String.class,required = true)
	private String etl_para_type;
	@DocBean(name ="etl_job_pro_para",value="参数名称:",dataType = String.class,required = true)
	private String etl_job_pro_para;
	@DocBean(name ="etl_job_para_size",value="参数:",dataType = String.class,required = true)
	private String etl_job_para_size;
	@DocBean(name ="etl_pro_para_sort",value="参数排序:",dataType = Long.class,required = true)
	private Long etl_pro_para_sort;
	@DocBean(name ="etl_temp_id",value="模版ID:",dataType = Long.class,required = true)
	private Long etl_temp_id;

	/** 取得：模版参数主键 */
	public Long getEtl_temp_para_id(){
		return etl_temp_para_id;
	}
	/** 设置：模版参数主键 */
	public void setEtl_temp_para_id(Long etl_temp_para_id){
		this.etl_temp_para_id=etl_temp_para_id;
	}
	/** 设置：模版参数主键 */
	public void setEtl_temp_para_id(String etl_temp_para_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(etl_temp_para_id)){
			this.etl_temp_para_id=new Long(etl_temp_para_id);
		}
	}
	/** 取得：参数类型 */
	public String getEtl_para_type(){
		return etl_para_type;
	}
	/** 设置：参数类型 */
	public void setEtl_para_type(String etl_para_type){
		this.etl_para_type=etl_para_type;
	}
	/** 取得：参数名称 */
	public String getEtl_job_pro_para(){
		return etl_job_pro_para;
	}
	/** 设置：参数名称 */
	public void setEtl_job_pro_para(String etl_job_pro_para){
		this.etl_job_pro_para=etl_job_pro_para;
	}
	/** 取得：参数 */
	public String getEtl_job_para_size(){
		return etl_job_para_size;
	}
	/** 设置：参数 */
	public void setEtl_job_para_size(String etl_job_para_size){
		this.etl_job_para_size=etl_job_para_size;
	}
	/** 取得：参数排序 */
	public Long getEtl_pro_para_sort(){
		return etl_pro_para_sort;
	}
	/** 设置：参数排序 */
	public void setEtl_pro_para_sort(Long etl_pro_para_sort){
		this.etl_pro_para_sort=etl_pro_para_sort;
	}
	/** 设置：参数排序 */
	public void setEtl_pro_para_sort(String etl_pro_para_sort){
		if(!fd.ng.core.utils.StringUtil.isEmpty(etl_pro_para_sort)){
			this.etl_pro_para_sort=new Long(etl_pro_para_sort);
		}
	}
	/** 取得：模版ID */
	public Long getEtl_temp_id(){
		return etl_temp_id;
	}
	/** 设置：模版ID */
	public void setEtl_temp_id(Long etl_temp_id){
		this.etl_temp_id=etl_temp_id;
	}
	/** 设置：模版ID */
	public void setEtl_temp_id(String etl_temp_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(etl_temp_id)){
			this.etl_temp_id=new Long(etl_temp_id);
		}
	}
}
