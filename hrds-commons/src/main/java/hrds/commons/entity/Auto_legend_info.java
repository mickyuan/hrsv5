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
 * 组件图例信息表
 */
@Table(tableName = "auto_legend_info")
public class Auto_legend_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_legend_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 组件图例信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("legend_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="legend_id",value="图例编号:",dataType = Long.class,required = true)
	private Long legend_id;
	@DocBean(name ="type",value="图例类型:",dataType = String.class,required = false)
	private String type;
	@DocBean(name ="show",value="是否显示(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String show;
	@DocBean(name ="z",value="z值:",dataType = Long.class,required = false)
	private Long z;
	@DocBean(name ="left_distance",value="左侧距离:",dataType = String.class,required = false)
	private String left_distance;
	@DocBean(name ="top_distance",value="上侧距离:",dataType = String.class,required = false)
	private String top_distance;
	@DocBean(name ="right_distance",value="右侧距离:",dataType = String.class,required = false)
	private String right_distance;
	@DocBean(name ="bottom_distance",value="下侧距离:",dataType = String.class,required = false)
	private String bottom_distance;
	@DocBean(name ="width",value="宽度:",dataType = String.class,required = false)
	private String width;
	@DocBean(name ="height",value="高度:",dataType = String.class,required = false)
	private String height;
	@DocBean(name ="orient",value="布局朝向:",dataType = String.class,required = false)
	private String orient;
	@DocBean(name ="align",value="标记和文本的对齐:",dataType = String.class,required = false)
	private String align;
	@DocBean(name ="padding",value="内边距:",dataType = String.class,required = false)
	private String padding;
	@DocBean(name ="itemgap",value="图例间隔:",dataType = Long.class,required = false)
	private Long itemgap;
	@DocBean(name ="itemwidth",value="图形宽度:",dataType = Long.class,required = false)
	private Long itemwidth;
	@DocBean(name ="itemheight",value="图形高度:",dataType = Long.class,required = false)
	private Long itemheight;
	@DocBean(name ="formatter",value="格式化内容:",dataType = String.class,required = false)
	private String formatter;
	@DocBean(name ="selectedmode",value="图例选择:",dataType = String.class,required = false)
	private String selectedmode;
	@DocBean(name ="inactivecolor",value="图例关闭时颜色:",dataType = String.class,required = false)
	private String inactivecolor;
	@DocBean(name ="tooltip",value="是否显示提示(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String tooltip;
	@DocBean(name ="backgroundcolor",value="背景色:",dataType = String.class,required = false)
	private String backgroundcolor;
	@DocBean(name ="bordercolor",value="边框颜色:",dataType = String.class,required = false)
	private String bordercolor;
	@DocBean(name ="borderwidth",value="边框线宽:",dataType = Long.class,required = false)
	private Long borderwidth;
	@DocBean(name ="borderradius",value="圆角半径:",dataType = Long.class,required = false)
	private Long borderradius;
	@DocBean(name ="animation",value="图例翻页动画(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = false)
	private String animation;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = false)
	private Long component_id;
	@DocBean(name ="intervalnumber",value="图例个数:",dataType = Long.class,required = false)
	private Long intervalnumber;
	@DocBean(name ="interval",value="图例容量:",dataType = Long.class,required = false)
	private Long interval;

	/** 取得：图例编号 */
	public Long getLegend_id(){
		return legend_id;
	}
	/** 设置：图例编号 */
	public void setLegend_id(Long legend_id){
		this.legend_id=legend_id;
	}
	/** 设置：图例编号 */
	public void setLegend_id(String legend_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(legend_id)){
			this.legend_id=new Long(legend_id);
		}
	}
	/** 取得：图例类型 */
	public String getType(){
		return type;
	}
	/** 设置：图例类型 */
	public void setType(String type){
		this.type=type;
	}
	/** 取得：是否显示 */
	public String getShow(){
		return show;
	}
	/** 设置：是否显示 */
	public void setShow(String show){
		this.show=show;
	}
	/** 取得：z值 */
	public Long getZ(){
		return z;
	}
	/** 设置：z值 */
	public void setZ(Long z){
		this.z=z;
	}
	/** 设置：z值 */
	public void setZ(String z){
		if(!fd.ng.core.utils.StringUtil.isEmpty(z)){
			this.z=new Long(z);
		}
	}
	/** 取得：左侧距离 */
	public String getLeft_distance(){
		return left_distance;
	}
	/** 设置：左侧距离 */
	public void setLeft_distance(String left_distance){
		this.left_distance=left_distance;
	}
	/** 取得：上侧距离 */
	public String getTop_distance(){
		return top_distance;
	}
	/** 设置：上侧距离 */
	public void setTop_distance(String top_distance){
		this.top_distance=top_distance;
	}
	/** 取得：右侧距离 */
	public String getRight_distance(){
		return right_distance;
	}
	/** 设置：右侧距离 */
	public void setRight_distance(String right_distance){
		this.right_distance=right_distance;
	}
	/** 取得：下侧距离 */
	public String getBottom_distance(){
		return bottom_distance;
	}
	/** 设置：下侧距离 */
	public void setBottom_distance(String bottom_distance){
		this.bottom_distance=bottom_distance;
	}
	/** 取得：宽度 */
	public String getWidth(){
		return width;
	}
	/** 设置：宽度 */
	public void setWidth(String width){
		this.width=width;
	}
	/** 取得：高度 */
	public String getHeight(){
		return height;
	}
	/** 设置：高度 */
	public void setHeight(String height){
		this.height=height;
	}
	/** 取得：布局朝向 */
	public String getOrient(){
		return orient;
	}
	/** 设置：布局朝向 */
	public void setOrient(String orient){
		this.orient=orient;
	}
	/** 取得：标记和文本的对齐 */
	public String getAlign(){
		return align;
	}
	/** 设置：标记和文本的对齐 */
	public void setAlign(String align){
		this.align=align;
	}
	/** 取得：内边距 */
	public String getPadding(){
		return padding;
	}
	/** 设置：内边距 */
	public void setPadding(String padding){
		this.padding=padding;
	}
	/** 取得：图例间隔 */
	public Long getItemgap(){
		return itemgap;
	}
	/** 设置：图例间隔 */
	public void setItemgap(Long itemgap){
		this.itemgap=itemgap;
	}
	/** 设置：图例间隔 */
	public void setItemgap(String itemgap){
		if(!fd.ng.core.utils.StringUtil.isEmpty(itemgap)){
			this.itemgap=new Long(itemgap);
		}
	}
	/** 取得：图形宽度 */
	public Long getItemwidth(){
		return itemwidth;
	}
	/** 设置：图形宽度 */
	public void setItemwidth(Long itemwidth){
		this.itemwidth=itemwidth;
	}
	/** 设置：图形宽度 */
	public void setItemwidth(String itemwidth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(itemwidth)){
			this.itemwidth=new Long(itemwidth);
		}
	}
	/** 取得：图形高度 */
	public Long getItemheight(){
		return itemheight;
	}
	/** 设置：图形高度 */
	public void setItemheight(Long itemheight){
		this.itemheight=itemheight;
	}
	/** 设置：图形高度 */
	public void setItemheight(String itemheight){
		if(!fd.ng.core.utils.StringUtil.isEmpty(itemheight)){
			this.itemheight=new Long(itemheight);
		}
	}
	/** 取得：格式化内容 */
	public String getFormatter(){
		return formatter;
	}
	/** 设置：格式化内容 */
	public void setFormatter(String formatter){
		this.formatter=formatter;
	}
	/** 取得：图例选择 */
	public String getSelectedmode(){
		return selectedmode;
	}
	/** 设置：图例选择 */
	public void setSelectedmode(String selectedmode){
		this.selectedmode=selectedmode;
	}
	/** 取得：图例关闭时颜色 */
	public String getInactivecolor(){
		return inactivecolor;
	}
	/** 设置：图例关闭时颜色 */
	public void setInactivecolor(String inactivecolor){
		this.inactivecolor=inactivecolor;
	}
	/** 取得：是否显示提示 */
	public String getTooltip(){
		return tooltip;
	}
	/** 设置：是否显示提示 */
	public void setTooltip(String tooltip){
		this.tooltip=tooltip;
	}
	/** 取得：背景色 */
	public String getBackgroundcolor(){
		return backgroundcolor;
	}
	/** 设置：背景色 */
	public void setBackgroundcolor(String backgroundcolor){
		this.backgroundcolor=backgroundcolor;
	}
	/** 取得：边框颜色 */
	public String getBordercolor(){
		return bordercolor;
	}
	/** 设置：边框颜色 */
	public void setBordercolor(String bordercolor){
		this.bordercolor=bordercolor;
	}
	/** 取得：边框线宽 */
	public Long getBorderwidth(){
		return borderwidth;
	}
	/** 设置：边框线宽 */
	public void setBorderwidth(Long borderwidth){
		this.borderwidth=borderwidth;
	}
	/** 设置：边框线宽 */
	public void setBorderwidth(String borderwidth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(borderwidth)){
			this.borderwidth=new Long(borderwidth);
		}
	}
	/** 取得：圆角半径 */
	public Long getBorderradius(){
		return borderradius;
	}
	/** 设置：圆角半径 */
	public void setBorderradius(Long borderradius){
		this.borderradius=borderradius;
	}
	/** 设置：圆角半径 */
	public void setBorderradius(String borderradius){
		if(!fd.ng.core.utils.StringUtil.isEmpty(borderradius)){
			this.borderradius=new Long(borderradius);
		}
	}
	/** 取得：图例翻页动画 */
	public String getAnimation(){
		return animation;
	}
	/** 设置：图例翻页动画 */
	public void setAnimation(String animation){
		this.animation=animation;
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
	/** 取得：图例个数 */
	public Long getIntervalnumber(){
		return intervalnumber;
	}
	/** 设置：图例个数 */
	public void setIntervalnumber(Long intervalnumber){
		this.intervalnumber=intervalnumber;
	}
	/** 设置：图例个数 */
	public void setIntervalnumber(String intervalnumber){
		if(!fd.ng.core.utils.StringUtil.isEmpty(intervalnumber)){
			this.intervalnumber=new Long(intervalnumber);
		}
	}
	/** 取得：图例容量 */
	public Long getInterval(){
		return interval;
	}
	/** 设置：图例容量 */
	public void setInterval(Long interval){
		this.interval=interval;
	}
	/** 设置：图例容量 */
	public void setInterval(String interval){
		if(!fd.ng.core.utils.StringUtil.isEmpty(interval)){
			this.interval=new Long(interval);
		}
	}
}
