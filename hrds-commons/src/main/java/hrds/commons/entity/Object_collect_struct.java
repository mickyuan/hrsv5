package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.docannotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 对象采集结构信息
 */
@Table(tableName = "object_collect_struct")
public class Object_collect_struct extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_collect_struct";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对象采集结构信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("struct_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="struct_id",value="结构信息id",dataType = Long.class,required = true)
	private Long struct_id;
	@DocBean(name ="coll_name",value="采集结构名称",dataType = String.class,required = true)
	private String coll_name;
	@DocBean(name ="remark",value="备注",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="ocs_id",value="对象采集任务编号",dataType = Long.class,required = true)
	private Long ocs_id;
	@DocBean(name ="struct_type",value="对象数据类型",dataType = String.class,required = true)
	private String struct_type;
	@DocBean(name ="data_desc",value="中文描述信息",dataType = String.class,required = false)
	private String data_desc;

	/** 取得：结构信息id */
	public Long getStruct_id(){
		return struct_id;
	}
	/** 设置：结构信息id */
	public void setStruct_id(Long struct_id){
		this.struct_id=struct_id;
	}
	/** 设置：结构信息id */
	public void setStruct_id(String struct_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(struct_id)){
			this.struct_id=new Long(struct_id);
		}
	}
	/** 取得：采集结构名称 */
	public String getColl_name(){
		return coll_name;
	}
	/** 设置：采集结构名称 */
	public void setColl_name(String coll_name){
		this.coll_name=coll_name;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
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
	/** 取得：对象数据类型 */
	public String getStruct_type(){
		return struct_type;
	}
	/** 设置：对象数据类型 */
	public void setStruct_type(String struct_type){
		this.struct_type=struct_type;
	}
	/** 取得：中文描述信息 */
	public String getData_desc(){
		return data_desc;
	}
	/** 设置：中文描述信息 */
	public void setData_desc(String data_desc){
		this.data_desc=data_desc;
	}
}
