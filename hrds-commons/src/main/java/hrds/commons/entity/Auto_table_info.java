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
 * 表格配置信息表
 */
@Table(tableName = "auto_table_info")
public class Auto_table_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_table_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 表格配置信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("config_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="config_id",value="配置编号:",dataType = Long.class,required = true)
	private Long config_id;
	@DocBean(name ="th_background",value="表头背景色:",dataType = String.class,required = false)
	private String th_background;
	@DocBean(name ="is_gridline",value="是否使用网格线(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_gridline;
	@DocBean(name ="is_zebraline",value="是否使用斑马线(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_zebraline;
	@DocBean(name ="zl_background",value="斑马线颜色:",dataType = String.class,required = false)
	private String zl_background;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = false)
	private Long component_id;

	/** 取得：配置编号 */
	public Long getConfig_id(){
		return config_id;
	}
	/** 设置：配置编号 */
	public void setConfig_id(Long config_id){
		this.config_id=config_id;
	}
	/** 设置：配置编号 */
	public void setConfig_id(String config_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(config_id)){
			this.config_id=new Long(config_id);
		}
	}
	/** 取得：表头背景色 */
	public String getTh_background(){
		return th_background;
	}
	/** 设置：表头背景色 */
	public void setTh_background(String th_background){
		this.th_background=th_background;
	}
	/** 取得：是否使用网格线 */
	public String getIs_gridline(){
		return is_gridline;
	}
	/** 设置：是否使用网格线 */
	public void setIs_gridline(String is_gridline){
		this.is_gridline=is_gridline;
	}
	/** 取得：是否使用斑马线 */
	public String getIs_zebraline(){
		return is_zebraline;
	}
	/** 设置：是否使用斑马线 */
	public void setIs_zebraline(String is_zebraline){
		this.is_zebraline=is_zebraline;
	}
	/** 取得：斑马线颜色 */
	public String getZl_background(){
		return zl_background;
	}
	/** 设置：斑马线颜色 */
	public void setZl_background(String zl_background){
		this.zl_background=zl_background;
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
