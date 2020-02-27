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
 * 对象采集数据处理类型对应表
 */
@Table(tableName = "object_handle_type")
public class Object_handle_type extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_handle_type";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对象采集数据处理类型对应表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("object_handle_id");
		__tmpPKS.add("ocs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="object_handle_id",value="处理编号:",dataType = Long.class,required = true)
	private Long object_handle_id;
	@DocBean(name ="handle_type",value="处理类型(OperationType):0-INSERT 1-UPDATE 2-DELETE ",dataType = String.class,required = true)
	private String handle_type;
	@DocBean(name ="handle_value",value="处理值:",dataType = String.class,required = true)
	private String handle_value;
	@DocBean(name ="ocs_id",value="对象采集任务编号:",dataType = Long.class,required = true)
	private Long ocs_id;

	/** 取得：处理编号 */
	public Long getObject_handle_id(){
		return object_handle_id;
	}
	/** 设置：处理编号 */
	public void setObject_handle_id(Long object_handle_id){
		this.object_handle_id=object_handle_id;
	}
	/** 设置：处理编号 */
	public void setObject_handle_id(String object_handle_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(object_handle_id)){
			this.object_handle_id=new Long(object_handle_id);
		}
	}
	/** 取得：处理类型 */
	public String getHandle_type(){
		return handle_type;
	}
	/** 设置：处理类型 */
	public void setHandle_type(String handle_type){
		this.handle_type=handle_type;
	}
	/** 取得：处理值 */
	public String getHandle_value(){
		return handle_value;
	}
	/** 设置：处理值 */
	public void setHandle_value(String handle_value){
		this.handle_value=handle_value;
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
