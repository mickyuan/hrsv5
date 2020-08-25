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
 * 图表配置信息表
 */
@Table(tableName = "auto_chartsconfig")
public class Auto_chartsconfig extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_chartsconfig";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 图表配置信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("config_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="config_id",value="配置编号:",dataType = Long.class,required = true)
	private Long config_id;
	@DocBean(name ="type",value="图表类型:",dataType = String.class,required = false)
	private String type;
	@DocBean(name ="xaxisindex",value="x轴索引号:",dataType = Long.class,required = true)
	private Long xaxisindex;
	@DocBean(name ="yaxisindex",value="y轴索引号:",dataType = Long.class,required = true)
	private Long yaxisindex;
	@DocBean(name ="symbol",value="标记图形:",dataType = String.class,required = false)
	private String symbol;
	@DocBean(name ="symbolsize",value="标记大小:",dataType = Long.class,required = true)
	private Long symbolsize;
	@DocBean(name ="symbolrotate",value="标记旋转角度:",dataType = Long.class,required = true)
	private Long symbolrotate;
	@DocBean(name ="showsymbol",value="显示标记(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String showsymbol;
	@DocBean(name ="stack",value="数据堆叠:",dataType = String.class,required = false)
	private String stack;
	@DocBean(name ="connectnulls",value="连接空数据(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String connectnulls;
	@DocBean(name ="step",value="是阶梯线图(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String step;
	@DocBean(name ="smooth",value="平滑曲线显示(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String smooth;
	@DocBean(name ="z",value="z值:",dataType = Long.class,required = true)
	private Long z;
	@DocBean(name ="silent",value="触发鼠标事件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String silent;
	@DocBean(name ="legendhoverlink",value="启用图例联动高亮(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String legendhoverlink;
	@DocBean(name ="clockwise",value="是顺时针排布(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String clockwise;
	@DocBean(name ="rosetype",value="是南丁格尔图(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String rosetype;
	@DocBean(name ="center",value="圆心坐标:",dataType = String.class,required = false)
	private String center;
	@DocBean(name ="radius",value="半径:",dataType = String.class,required = false)
	private String radius;
	@DocBean(name ="left_distance",value="左侧距离:",dataType = Long.class,required = true)
	private Long left_distance;
	@DocBean(name ="top_distance",value="上侧距离:",dataType = Long.class,required = true)
	private Long top_distance;
	@DocBean(name ="right_distance",value="右侧距离:",dataType = Long.class,required = true)
	private Long right_distance;
	@DocBean(name ="bottom_distance",value="下侧距离:",dataType = Long.class,required = true)
	private Long bottom_distance;
	@DocBean(name ="width",value="宽度:",dataType = Long.class,required = true)
	private Long width;
	@DocBean(name ="height",value="高度:",dataType = Long.class,required = true)
	private Long height;
	@DocBean(name ="leafdepth",value="下钻层数:",dataType = Long.class,required = true)
	private Long leafdepth;
	@DocBean(name ="nodeclick",value="点击节点行为:",dataType = String.class,required = false)
	private String nodeclick;
	@DocBean(name ="visiblemin",value="最小面积阈值:",dataType = Long.class,required = true)
	private Long visiblemin;
	@DocBean(name ="sort",value="块数据排序方式:",dataType = String.class,required = false)
	private String sort;
	@DocBean(name ="layout",value="布局方式:",dataType = String.class,required = false)
	private String layout;
	@DocBean(name ="polyline",value="是多段线(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String polyline;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = false)
	private Long component_id;
	@DocBean(name ="provincename",value="地图省份:",dataType = String.class,required = false)
	private String provincename;

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
	/** 取得：图表类型 */
	public String getType(){
		return type;
	}
	/** 设置：图表类型 */
	public void setType(String type){
		this.type=type;
	}
	/** 取得：x轴索引号 */
	public Long getXaxisindex(){
		return xaxisindex;
	}
	/** 设置：x轴索引号 */
	public void setXaxisindex(Long xaxisindex){
		this.xaxisindex=xaxisindex;
	}
	/** 设置：x轴索引号 */
	public void setXaxisindex(String xaxisindex){
		if(!fd.ng.core.utils.StringUtil.isEmpty(xaxisindex)){
			this.xaxisindex=new Long(xaxisindex);
		}
	}
	/** 取得：y轴索引号 */
	public Long getYaxisindex(){
		return yaxisindex;
	}
	/** 设置：y轴索引号 */
	public void setYaxisindex(Long yaxisindex){
		this.yaxisindex=yaxisindex;
	}
	/** 设置：y轴索引号 */
	public void setYaxisindex(String yaxisindex){
		if(!fd.ng.core.utils.StringUtil.isEmpty(yaxisindex)){
			this.yaxisindex=new Long(yaxisindex);
		}
	}
	/** 取得：标记图形 */
	public String getSymbol(){
		return symbol;
	}
	/** 设置：标记图形 */
	public void setSymbol(String symbol){
		this.symbol=symbol;
	}
	/** 取得：标记大小 */
	public Long getSymbolsize(){
		return symbolsize;
	}
	/** 设置：标记大小 */
	public void setSymbolsize(Long symbolsize){
		this.symbolsize=symbolsize;
	}
	/** 设置：标记大小 */
	public void setSymbolsize(String symbolsize){
		if(!fd.ng.core.utils.StringUtil.isEmpty(symbolsize)){
			this.symbolsize=new Long(symbolsize);
		}
	}
	/** 取得：标记旋转角度 */
	public Long getSymbolrotate(){
		return symbolrotate;
	}
	/** 设置：标记旋转角度 */
	public void setSymbolrotate(Long symbolrotate){
		this.symbolrotate=symbolrotate;
	}
	/** 设置：标记旋转角度 */
	public void setSymbolrotate(String symbolrotate){
		if(!fd.ng.core.utils.StringUtil.isEmpty(symbolrotate)){
			this.symbolrotate=new Long(symbolrotate);
		}
	}
	/** 取得：显示标记 */
	public String getShowsymbol(){
		return showsymbol;
	}
	/** 设置：显示标记 */
	public void setShowsymbol(String showsymbol){
		this.showsymbol=showsymbol;
	}
	/** 取得：数据堆叠 */
	public String getStack(){
		return stack;
	}
	/** 设置：数据堆叠 */
	public void setStack(String stack){
		this.stack=stack;
	}
	/** 取得：连接空数据 */
	public String getConnectnulls(){
		return connectnulls;
	}
	/** 设置：连接空数据 */
	public void setConnectnulls(String connectnulls){
		this.connectnulls=connectnulls;
	}
	/** 取得：是阶梯线图 */
	public String getStep(){
		return step;
	}
	/** 设置：是阶梯线图 */
	public void setStep(String step){
		this.step=step;
	}
	/** 取得：平滑曲线显示 */
	public String getSmooth(){
		return smooth;
	}
	/** 设置：平滑曲线显示 */
	public void setSmooth(String smooth){
		this.smooth=smooth;
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
	/** 取得：触发鼠标事件 */
	public String getSilent(){
		return silent;
	}
	/** 设置：触发鼠标事件 */
	public void setSilent(String silent){
		this.silent=silent;
	}
	/** 取得：启用图例联动高亮 */
	public String getLegendhoverlink(){
		return legendhoverlink;
	}
	/** 设置：启用图例联动高亮 */
	public void setLegendhoverlink(String legendhoverlink){
		this.legendhoverlink=legendhoverlink;
	}
	/** 取得：是顺时针排布 */
	public String getClockwise(){
		return clockwise;
	}
	/** 设置：是顺时针排布 */
	public void setClockwise(String clockwise){
		this.clockwise=clockwise;
	}
	/** 取得：是南丁格尔图 */
	public String getRosetype(){
		return rosetype;
	}
	/** 设置：是南丁格尔图 */
	public void setRosetype(String rosetype){
		this.rosetype=rosetype;
	}
	/** 取得：圆心坐标 */
	public String getCenter(){
		return center;
	}
	/** 设置：圆心坐标 */
	public void setCenter(String center){
		this.center=center;
	}
	/** 取得：半径 */
	public String getRadius(){
		return radius;
	}
	/** 设置：半径 */
	public void setRadius(String radius){
		this.radius=radius;
	}
	/** 取得：左侧距离 */
	public Long getLeft_distance(){
		return left_distance;
	}
	/** 设置：左侧距离 */
	public void setLeft_distance(Long left_distance){
		this.left_distance=left_distance;
	}
	/** 设置：左侧距离 */
	public void setLeft_distance(String left_distance){
		if(!fd.ng.core.utils.StringUtil.isEmpty(left_distance)){
			this.left_distance=new Long(left_distance);
		}
	}
	/** 取得：上侧距离 */
	public Long getTop_distance(){
		return top_distance;
	}
	/** 设置：上侧距离 */
	public void setTop_distance(Long top_distance){
		this.top_distance=top_distance;
	}
	/** 设置：上侧距离 */
	public void setTop_distance(String top_distance){
		if(!fd.ng.core.utils.StringUtil.isEmpty(top_distance)){
			this.top_distance=new Long(top_distance);
		}
	}
	/** 取得：右侧距离 */
	public Long getRight_distance(){
		return right_distance;
	}
	/** 设置：右侧距离 */
	public void setRight_distance(Long right_distance){
		this.right_distance=right_distance;
	}
	/** 设置：右侧距离 */
	public void setRight_distance(String right_distance){
		if(!fd.ng.core.utils.StringUtil.isEmpty(right_distance)){
			this.right_distance=new Long(right_distance);
		}
	}
	/** 取得：下侧距离 */
	public Long getBottom_distance(){
		return bottom_distance;
	}
	/** 设置：下侧距离 */
	public void setBottom_distance(Long bottom_distance){
		this.bottom_distance=bottom_distance;
	}
	/** 设置：下侧距离 */
	public void setBottom_distance(String bottom_distance){
		if(!fd.ng.core.utils.StringUtil.isEmpty(bottom_distance)){
			this.bottom_distance=new Long(bottom_distance);
		}
	}
	/** 取得：宽度 */
	public Long getWidth(){
		return width;
	}
	/** 设置：宽度 */
	public void setWidth(Long width){
		this.width=width;
	}
	/** 设置：宽度 */
	public void setWidth(String width){
		if(!fd.ng.core.utils.StringUtil.isEmpty(width)){
			this.width=new Long(width);
		}
	}
	/** 取得：高度 */
	public Long getHeight(){
		return height;
	}
	/** 设置：高度 */
	public void setHeight(Long height){
		this.height=height;
	}
	/** 设置：高度 */
	public void setHeight(String height){
		if(!fd.ng.core.utils.StringUtil.isEmpty(height)){
			this.height=new Long(height);
		}
	}
	/** 取得：下钻层数 */
	public Long getLeafdepth(){
		return leafdepth;
	}
	/** 设置：下钻层数 */
	public void setLeafdepth(Long leafdepth){
		this.leafdepth=leafdepth;
	}
	/** 设置：下钻层数 */
	public void setLeafdepth(String leafdepth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(leafdepth)){
			this.leafdepth=new Long(leafdepth);
		}
	}
	/** 取得：点击节点行为 */
	public String getNodeclick(){
		return nodeclick;
	}
	/** 设置：点击节点行为 */
	public void setNodeclick(String nodeclick){
		this.nodeclick=nodeclick;
	}
	/** 取得：最小面积阈值 */
	public Long getVisiblemin(){
		return visiblemin;
	}
	/** 设置：最小面积阈值 */
	public void setVisiblemin(Long visiblemin){
		this.visiblemin=visiblemin;
	}
	/** 设置：最小面积阈值 */
	public void setVisiblemin(String visiblemin){
		if(!fd.ng.core.utils.StringUtil.isEmpty(visiblemin)){
			this.visiblemin=new Long(visiblemin);
		}
	}
	/** 取得：块数据排序方式 */
	public String getSort(){
		return sort;
	}
	/** 设置：块数据排序方式 */
	public void setSort(String sort){
		this.sort=sort;
	}
	/** 取得：布局方式 */
	public String getLayout(){
		return layout;
	}
	/** 设置：布局方式 */
	public void setLayout(String layout){
		this.layout=layout;
	}
	/** 取得：是多段线 */
	public String getPolyline(){
		return polyline;
	}
	/** 设置：是多段线 */
	public void setPolyline(String polyline){
		this.polyline=polyline;
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
	/** 取得：地图省份 */
	public String getProvincename(){
		return provincename;
	}
	/** 设置：地图省份 */
	public void setProvincename(String provincename){
		this.provincename=provincename;
	}
}
