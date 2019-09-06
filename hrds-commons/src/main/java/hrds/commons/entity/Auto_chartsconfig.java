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
 * 图表配置信息表
 */
@Table(tableName = "auto_chartsconfig")
public class Auto_chartsconfig extends TableEntity
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
	private Long config_id; //配置编号
	private String type; //图表类型
	private Integer xaxisindex; //x轴索引号
	private Integer yaxisindex; //y轴索引号
	private String symbol; //标记图形
	private Integer symbolsize; //标记大小
	private Integer symbolrotate; //标记旋转角度
	private String showsymbol; //显示标记
	private String stack; //数据堆叠
	private String connectnulls; //连接空数据
	private String step; //是阶梯线图
	private String smooth; //平滑曲线显示
	private Integer z; //z值
	private String silent; //触发鼠标事件
	private String legendhoverlink; //启用图例联动高亮
	private String clockwise; //是顺时针排布
	private String rosetype; //是南丁格尔图
	private String center; //圆心坐标
	private String radius; //半径
	private Integer left_distance; //左侧距离
	private Integer top_distance; //上侧距离
	private Integer right_distance; //右侧距离
	private Integer bottom_distance; //下侧距离
	private Integer width; //宽度
	private Integer height; //高度
	private Integer leafdepth; //下钻层数
	private String nodeclick; //点击节点行为
	private Integer visiblemin; //最小面积阈值
	private String sort; //块数据排序方式
	private String layout; //布局方式
	private String polyline; //是多段线
	private Long component_id; //组件ID
	private String provincename; //地图省份

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
	public Integer getXaxisindex(){
		return xaxisindex;
	}
	/** 设置：x轴索引号 */
	public void setXaxisindex(Integer xaxisindex){
		this.xaxisindex=xaxisindex;
	}
	/** 设置：x轴索引号 */
	public void setXaxisindex(String xaxisindex){
		if(!fd.ng.core.utils.StringUtil.isEmpty(xaxisindex)){
			this.xaxisindex=new Integer(xaxisindex);
		}
	}
	/** 取得：y轴索引号 */
	public Integer getYaxisindex(){
		return yaxisindex;
	}
	/** 设置：y轴索引号 */
	public void setYaxisindex(Integer yaxisindex){
		this.yaxisindex=yaxisindex;
	}
	/** 设置：y轴索引号 */
	public void setYaxisindex(String yaxisindex){
		if(!fd.ng.core.utils.StringUtil.isEmpty(yaxisindex)){
			this.yaxisindex=new Integer(yaxisindex);
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
	public Integer getSymbolsize(){
		return symbolsize;
	}
	/** 设置：标记大小 */
	public void setSymbolsize(Integer symbolsize){
		this.symbolsize=symbolsize;
	}
	/** 设置：标记大小 */
	public void setSymbolsize(String symbolsize){
		if(!fd.ng.core.utils.StringUtil.isEmpty(symbolsize)){
			this.symbolsize=new Integer(symbolsize);
		}
	}
	/** 取得：标记旋转角度 */
	public Integer getSymbolrotate(){
		return symbolrotate;
	}
	/** 设置：标记旋转角度 */
	public void setSymbolrotate(Integer symbolrotate){
		this.symbolrotate=symbolrotate;
	}
	/** 设置：标记旋转角度 */
	public void setSymbolrotate(String symbolrotate){
		if(!fd.ng.core.utils.StringUtil.isEmpty(symbolrotate)){
			this.symbolrotate=new Integer(symbolrotate);
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
	public Integer getLeft_distance(){
		return left_distance;
	}
	/** 设置：左侧距离 */
	public void setLeft_distance(Integer left_distance){
		this.left_distance=left_distance;
	}
	/** 设置：左侧距离 */
	public void setLeft_distance(String left_distance){
		if(!fd.ng.core.utils.StringUtil.isEmpty(left_distance)){
			this.left_distance=new Integer(left_distance);
		}
	}
	/** 取得：上侧距离 */
	public Integer getTop_distance(){
		return top_distance;
	}
	/** 设置：上侧距离 */
	public void setTop_distance(Integer top_distance){
		this.top_distance=top_distance;
	}
	/** 设置：上侧距离 */
	public void setTop_distance(String top_distance){
		if(!fd.ng.core.utils.StringUtil.isEmpty(top_distance)){
			this.top_distance=new Integer(top_distance);
		}
	}
	/** 取得：右侧距离 */
	public Integer getRight_distance(){
		return right_distance;
	}
	/** 设置：右侧距离 */
	public void setRight_distance(Integer right_distance){
		this.right_distance=right_distance;
	}
	/** 设置：右侧距离 */
	public void setRight_distance(String right_distance){
		if(!fd.ng.core.utils.StringUtil.isEmpty(right_distance)){
			this.right_distance=new Integer(right_distance);
		}
	}
	/** 取得：下侧距离 */
	public Integer getBottom_distance(){
		return bottom_distance;
	}
	/** 设置：下侧距离 */
	public void setBottom_distance(Integer bottom_distance){
		this.bottom_distance=bottom_distance;
	}
	/** 设置：下侧距离 */
	public void setBottom_distance(String bottom_distance){
		if(!fd.ng.core.utils.StringUtil.isEmpty(bottom_distance)){
			this.bottom_distance=new Integer(bottom_distance);
		}
	}
	/** 取得：宽度 */
	public Integer getWidth(){
		return width;
	}
	/** 设置：宽度 */
	public void setWidth(Integer width){
		this.width=width;
	}
	/** 设置：宽度 */
	public void setWidth(String width){
		if(!fd.ng.core.utils.StringUtil.isEmpty(width)){
			this.width=new Integer(width);
		}
	}
	/** 取得：高度 */
	public Integer getHeight(){
		return height;
	}
	/** 设置：高度 */
	public void setHeight(Integer height){
		this.height=height;
	}
	/** 设置：高度 */
	public void setHeight(String height){
		if(!fd.ng.core.utils.StringUtil.isEmpty(height)){
			this.height=new Integer(height);
		}
	}
	/** 取得：下钻层数 */
	public Integer getLeafdepth(){
		return leafdepth;
	}
	/** 设置：下钻层数 */
	public void setLeafdepth(Integer leafdepth){
		this.leafdepth=leafdepth;
	}
	/** 设置：下钻层数 */
	public void setLeafdepth(String leafdepth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(leafdepth)){
			this.leafdepth=new Integer(leafdepth);
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
	public Integer getVisiblemin(){
		return visiblemin;
	}
	/** 设置：最小面积阈值 */
	public void setVisiblemin(Integer visiblemin){
		this.visiblemin=visiblemin;
	}
	/** 设置：最小面积阈值 */
	public void setVisiblemin(String visiblemin){
		if(!fd.ng.core.utils.StringUtil.isEmpty(visiblemin)){
			this.visiblemin=new Integer(visiblemin);
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
