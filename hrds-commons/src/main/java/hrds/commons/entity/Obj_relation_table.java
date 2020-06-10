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
 * 对象表存储关系表
 */
@Table(tableName = "obj_relation_table")
public class Obj_relation_table extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "obj_relation_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对象表存储关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dsl_id");
		__tmpPKS.add("ocs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="is_successful",value="是否入库成功(JobExecuteState):100-等待<DengDai> 101-运行<YunXing> 102-暂停<ZanTing> 103-中止<ZhongZhi> 104-完成<WanCheng> 105-失败<ShiBai> ",dataType = String.class,required = false)
	private String is_successful;
	@DocBean(name ="dsl_id",value="存储层配置ID:",dataType = Long.class,required = true)
	private Long dsl_id;
	@DocBean(name ="ocs_id",value="对象采集任务编号:",dataType = Long.class,required = true)
	private Long ocs_id;

	/** 取得：是否入库成功 */
	public String getIs_successful(){
		return is_successful;
	}
	/** 设置：是否入库成功 */
	public void setIs_successful(String is_successful){
		this.is_successful=is_successful;
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
	/** 取得：对象采集任务编号 */
	public Long getOcs_id(){
		return ocs_id;
	}
	/** 设置：对象采集任务编号 */
	public void setOcs_id(Long ocs_id){
		this.ocs_id=ocs_id;
	}
	/** 设置：对象采集任务编号 */
	public void setOcs_id(String ocs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ocs_id)){
			this.ocs_id=new Long(ocs_id);
		}
	}
}
