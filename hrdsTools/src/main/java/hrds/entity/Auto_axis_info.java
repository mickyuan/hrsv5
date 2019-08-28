package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 轴配置信息表
 */
@Table(tableName = "auto_axis_info")
public class Auto_axis_info extends TableEntity
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
	private Long axis_id; //轴编号
	private String axis_type; //轴类型
	private String show; //是否显示
	private String position; //轴位置
	private Integer axisoffset; //轴偏移量
	private String name; //轴名称
	private String namelocation; //轴名称位置
	private Integer namegap; //名称与轴线距离
	private Integer namerotate; //轴名字旋转角度
	private Integer min; //轴刻度最小值
	private Integer max; //轴刻度最大值
	private String silent; //坐标轴是否静态
	private Long component_id; //组件ID

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
	public Integer getAxisoffset(){
		return axisoffset;
	}
	/** 设置：轴偏移量 */
	public void setAxisoffset(Integer axisoffset){
		this.axisoffset=axisoffset;
	}
	/** 设置：轴偏移量 */
	public void setAxisoffset(String axisoffset){
		if(!fd.ng.core.utils.StringUtil.isEmpty(axisoffset)){
			this.axisoffset=new Integer(axisoffset);
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
	public Integer getNamegap(){
		return namegap;
	}
	/** 设置：名称与轴线距离 */
	public void setNamegap(Integer namegap){
		this.namegap=namegap;
	}
	/** 设置：名称与轴线距离 */
	public void setNamegap(String namegap){
		if(!fd.ng.core.utils.StringUtil.isEmpty(namegap)){
			this.namegap=new Integer(namegap);
		}
	}
	/** 取得：轴名字旋转角度 */
	public Integer getNamerotate(){
		return namerotate;
	}
	/** 设置：轴名字旋转角度 */
	public void setNamerotate(Integer namerotate){
		this.namerotate=namerotate;
	}
	/** 设置：轴名字旋转角度 */
	public void setNamerotate(String namerotate){
		if(!fd.ng.core.utils.StringUtil.isEmpty(namerotate)){
			this.namerotate=new Integer(namerotate);
		}
	}
	/** 取得：轴刻度最小值 */
	public Integer getMin(){
		return min;
	}
	/** 设置：轴刻度最小值 */
	public void setMin(Integer min){
		this.min=min;
	}
	/** 设置：轴刻度最小值 */
	public void setMin(String min){
		if(!fd.ng.core.utils.StringUtil.isEmpty(min)){
			this.min=new Integer(min);
		}
	}
	/** 取得：轴刻度最大值 */
	public Integer getMax(){
		return max;
	}
	/** 设置：轴刻度最大值 */
	public void setMax(Integer max){
		this.max=max;
	}
	/** 设置：轴刻度最大值 */
	public void setMax(String max){
		if(!fd.ng.core.utils.StringUtil.isEmpty(max)){
			this.max=new Integer(max);
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
