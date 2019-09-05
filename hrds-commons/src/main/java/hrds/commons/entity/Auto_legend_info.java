package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 组件图例信息表
 */
@Table(tableName = "auto_legend_info")
public class Auto_legend_info extends TableEntity
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
	private Long legend_id; //图例编号
	private String type; //图例类型
	private String show; //是否显示
	private Integer z; //z值
	private String left_distance; //左侧距离
	private String top_distance; //上侧距离
	private String right_distance; //右侧距离
	private String bottom_distance; //下侧距离
	private String width; //宽度
	private String height; //高度
	private String orient; //布局朝向
	private String align; //标记和文本的对齐
	private Integer padding; //内边距
	private Integer itemgap; //图例间隔
	private Integer itemwidth; //图形宽度
	private Integer itemheight; //图形高度
	private String formatter; //格式化内容
	private String selectedmode; //图例选择
	private String inactivecolor; //图例关闭时颜色
	private String tooltip; //是否显示提示
	private String backgroundcolor; //背景色
	private String bordercolor; //边框颜色
	private Integer borderwidth; //边框线宽
	private Integer borderradius; //圆角半径
	private String animation; //图例翻页动画
	private Long component_id; //组件ID
	private Integer intervalnumber; //图例个数
	private Integer interval; //图例容量

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
	public Integer getZ(){
		return z;
	}
	/** 设置：z值 */
	public void setZ(Integer z){
		this.z=z;
	}
	/** 设置：z值 */
	public void setZ(String z){
		if(!fd.ng.core.utils.StringUtil.isEmpty(z)){
			this.z=new Integer(z);
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
	public Integer getPadding(){
		return padding;
	}
	/** 设置：内边距 */
	public void setPadding(Integer padding){
		this.padding=padding;
	}
	/** 设置：内边距 */
	public void setPadding(String padding){
		if(!fd.ng.core.utils.StringUtil.isEmpty(padding)){
			this.padding=new Integer(padding);
		}
	}
	/** 取得：图例间隔 */
	public Integer getItemgap(){
		return itemgap;
	}
	/** 设置：图例间隔 */
	public void setItemgap(Integer itemgap){
		this.itemgap=itemgap;
	}
	/** 设置：图例间隔 */
	public void setItemgap(String itemgap){
		if(!fd.ng.core.utils.StringUtil.isEmpty(itemgap)){
			this.itemgap=new Integer(itemgap);
		}
	}
	/** 取得：图形宽度 */
	public Integer getItemwidth(){
		return itemwidth;
	}
	/** 设置：图形宽度 */
	public void setItemwidth(Integer itemwidth){
		this.itemwidth=itemwidth;
	}
	/** 设置：图形宽度 */
	public void setItemwidth(String itemwidth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(itemwidth)){
			this.itemwidth=new Integer(itemwidth);
		}
	}
	/** 取得：图形高度 */
	public Integer getItemheight(){
		return itemheight;
	}
	/** 设置：图形高度 */
	public void setItemheight(Integer itemheight){
		this.itemheight=itemheight;
	}
	/** 设置：图形高度 */
	public void setItemheight(String itemheight){
		if(!fd.ng.core.utils.StringUtil.isEmpty(itemheight)){
			this.itemheight=new Integer(itemheight);
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
	public Integer getBorderwidth(){
		return borderwidth;
	}
	/** 设置：边框线宽 */
	public void setBorderwidth(Integer borderwidth){
		this.borderwidth=borderwidth;
	}
	/** 设置：边框线宽 */
	public void setBorderwidth(String borderwidth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(borderwidth)){
			this.borderwidth=new Integer(borderwidth);
		}
	}
	/** 取得：圆角半径 */
	public Integer getBorderradius(){
		return borderradius;
	}
	/** 设置：圆角半径 */
	public void setBorderradius(Integer borderradius){
		this.borderradius=borderradius;
	}
	/** 设置：圆角半径 */
	public void setBorderradius(String borderradius){
		if(!fd.ng.core.utils.StringUtil.isEmpty(borderradius)){
			this.borderradius=new Integer(borderradius);
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
	public Integer getIntervalnumber(){
		return intervalnumber;
	}
	/** 设置：图例个数 */
	public void setIntervalnumber(Integer intervalnumber){
		this.intervalnumber=intervalnumber;
	}
	/** 设置：图例个数 */
	public void setIntervalnumber(String intervalnumber){
		if(!fd.ng.core.utils.StringUtil.isEmpty(intervalnumber)){
			this.intervalnumber=new Integer(intervalnumber);
		}
	}
	/** 取得：图例容量 */
	public Integer getInterval(){
		return interval;
	}
	/** 设置：图例容量 */
	public void setInterval(Integer interval){
		this.interval=interval;
	}
	/** 设置：图例容量 */
	public void setInterval(String interval){
		if(!fd.ng.core.utils.StringUtil.isEmpty(interval)){
			this.interval=new Integer(interval);
		}
	}
}
