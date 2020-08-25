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
 * 轴配置信息表
 */
@Table(tableName = "auto_axis_info")
public class Auto_axis_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_axis_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 轴配置信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("axis_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="axis_id",value="轴编号:",dataType = Long.class,required = true)
	private Long axis_id;
	@DocBean(name ="axis_type",value="轴类型(AxisType):1-x轴<XAxis> 2-y轴<YAxis> ",dataType = String.class,required = true)
	private String axis_type;
	@DocBean(name ="show",value="是否显示(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String show;
	@DocBean(name ="position",value="轴位置:",dataType = String.class,required = true)
	private String position;
	@DocBean(name ="axisoffset",value="轴偏移量:",dataType = Long.class,required = true)
	private Long axisoffset;
	@DocBean(name ="name",value="轴名称:",dataType = String.class,required = true)
	private String name;
	@DocBean(name ="namelocation",value="轴名称位置:",dataType = String.class,required = true)
	private String namelocation;
	@DocBean(name ="namegap",value="名称与轴线距离:",dataType = Long.class,required = true)
	private Long namegap;
	@DocBean(name ="namerotate",value="轴名字旋转角度:",dataType = Long.class,required = true)
	private Long namerotate;
	@DocBean(name ="min",value="轴刻度最小值:",dataType = Long.class,required = true)
	private Long min;
	@DocBean(name ="max",value="轴刻度最大值:",dataType = Long.class,required = true)
	private Long max;
	@DocBean(name ="silent",value="坐标轴是否静态(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String silent;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = true)
	private Long component_id;

	/** 取得：轴编号 */
	public Long getAxis_id(){
		return axis_id;
	}
	/** 设置：轴编号 */
	public void setAxis_id(Long axis_id){
		this.axis_id=axis_id;
	}
	/** 设置：轴编号 */
	public void setAxis_id(String axis_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(axis_id)){
			this.axis_id=new Long(axis_id);
		}
	}
	/** 取得：轴类型 */
	public String getAxis_type(){
		return axis_type;
	}
	/** 设置：轴类型 */
	public void setAxis_type(String axis_type){
		this.axis_type=axis_type;
	}
	/** 取得：是否显示 */
	public String getShow(){
		return show;
	}
	/** 设置：是否显示 */
	public void setShow(String show){
		this.show=show;
	}
	/** 取得：轴位置 */
	public String getPosition(){
		return position;
	}
	/** 设置：轴位置 */
	public void setPosition(String position){
		this.position=position;
	}
	/** 取得：轴偏移量 */
	public Long getAxisoffset(){
		return axisoffset;
	}
	/** 设置：轴偏移量 */
	public void setAxisoffset(Long axisoffset){
		this.axisoffset=axisoffset;
	}
	/** 设置：轴偏移量 */
	public void setAxisoffset(String axisoffset){
		if(!fd.ng.core.utils.StringUtil.isEmpty(axisoffset)){
			this.axisoffset=new Long(axisoffset);
		}
	}
	/** 取得：轴名称 */
	public String getName(){
		return name;
	}
	/** 设置：轴名称 */
	public void setName(String name){
		this.name=name;
	}
	/** 取得：轴名称位置 */
	public String getNamelocation(){
		return namelocation;
	}
	/** 设置：轴名称位置 */
	public void setNamelocation(String namelocation){
		this.namelocation=namelocation;
	}
	/** 取得：名称与轴线距离 */
	public Long getNamegap(){
		return namegap;
	}
	/** 设置：名称与轴线距离 */
	public void setNamegap(Long namegap){
		this.namegap=namegap;
	}
	/** 设置：名称与轴线距离 */
	public void setNamegap(String namegap){
		if(!fd.ng.core.utils.StringUtil.isEmpty(namegap)){
			this.namegap=new Long(namegap);
		}
	}
	/** 取得：轴名字旋转角度 */
	public Long getNamerotate(){
		return namerotate;
	}
	/** 设置：轴名字旋转角度 */
	public void setNamerotate(Long namerotate){
		this.namerotate=namerotate;
	}
	/** 设置：轴名字旋转角度 */
	public void setNamerotate(String namerotate){
		if(!fd.ng.core.utils.StringUtil.isEmpty(namerotate)){
			this.namerotate=new Long(namerotate);
		}
	}
	/** 取得：轴刻度最小值 */
	public Long getMin(){
		return min;
	}
	/** 设置：轴刻度最小值 */
	public void setMin(Long min){
		this.min=min;
	}
	/** 设置：轴刻度最小值 */
	public void setMin(String min){
		if(!fd.ng.core.utils.StringUtil.isEmpty(min)){
			this.min=new Long(min);
		}
	}
	/** 取得：轴刻度最大值 */
	public Long getMax(){
		return max;
	}
	/** 设置：轴刻度最大值 */
	public void setMax(Long max){
		this.max=max;
	}
	/** 设置：轴刻度最大值 */
	public void setMax(String max){
		if(!fd.ng.core.utils.StringUtil.isEmpty(max)){
			this.max=new Long(max);
		}
	}
	/** 取得：坐标轴是否静态 */
	public String getSilent(){
		return silent;
	}
	/** 设置：坐标轴是否静态 */
	public void setSilent(String silent){
		this.silent=silent;
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
