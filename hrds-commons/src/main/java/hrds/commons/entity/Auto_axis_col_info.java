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
 * 横轴纵轴字段信息表
 */
@Table(tableName = "auto_axis_col_info")
public class Auto_axis_col_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_axis_col_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 横轴纵轴字段信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("axis_column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="axis_column_id",value="横轴纵轴字段ID:",dataType = Long.class,required = true)
	private Long axis_column_id;
	@DocBean(name ="column_name",value="字段名称:",dataType = String.class,required = false)
	private String column_name;
	@DocBean(name ="serial_number",value="序号:",dataType = Integer.class,required = true)
	private Integer serial_number;
	@DocBean(name ="show_type",value="字段显示类型:",dataType = String.class,required = true)
	private String show_type;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = false)
	private Long component_id;

	/** 取得：横轴纵轴字段ID */
	public Long getAxis_column_id(){
		return axis_column_id;
	}
	/** 设置：横轴纵轴字段ID */
	public void setAxis_column_id(Long axis_column_id){
		this.axis_column_id=axis_column_id;
	}
	/** 设置：横轴纵轴字段ID */
	public void setAxis_column_id(String axis_column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(axis_column_id)){
			this.axis_column_id=new Long(axis_column_id);
		}
	}
	/** 取得：字段名称 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：字段名称 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：序号 */
	public Integer getSerial_number(){
		return serial_number;
	}
	/** 设置：序号 */
	public void setSerial_number(Integer serial_number){
		this.serial_number=serial_number;
	}
	/** 设置：序号 */
	public void setSerial_number(String serial_number){
		if(!fd.ng.core.utils.StringUtil.isEmpty(serial_number)){
			this.serial_number=new Integer(serial_number);
		}
	}
	/** 取得：字段显示类型 */
	public String getShow_type(){
		return show_type;
	}
	/** 设置：字段显示类型 */
	public void setShow_type(String show_type){
		this.show_type=show_type;
	}
	/** 取得：组件ID */
	public Long getComponent_id(){
		return component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(Long component_id){
		this.component_id=component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(String component_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(component_id)){
			this.component_id=new Long(component_id);
		}
	}
}
