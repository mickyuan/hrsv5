package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "auto_chartsconfig")
public class AutoChartsconfig extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_chartsconfig";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("config_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String symbol;
	private String stack;
	private Integer bottom_distance;
	private Integer symbolrotate;
	private String connectnulls;
	private String type;
	private Integer leafdepth;
	private String provincename;
	private String rosetype;
	private Integer xaxisindex;
	private String showsymbol;
	private String radius;
	private String smooth;
	private Integer height;
	private Integer yaxisindex;
	private String silent;
	private BigDecimal component_id;
	private String clockwise;
	private String center;
	private String sort;
	private String layout;
	private Integer symbolsize;
	private BigDecimal config_id;
	private Integer left_distance;
	private Integer width;
	private Integer right_distance;
	private String step;
	private Integer z;
	private String legendhoverlink;
	private Integer visiblemin;
	private String nodeclick;
	private String polyline;
	private Integer top_distance;

	public String getSymbol() { return symbol; }
	public void setSymbol(String symbol) {
		if(symbol==null) addNullValueField("symbol");
		this.symbol = symbol;
	}

	public String getStack() { return stack; }
	public void setStack(String stack) {
		if(stack==null) addNullValueField("stack");
		this.stack = stack;
	}

	public Integer getBottom_distance() { return bottom_distance; }
	public void setBottom_distance(Integer bottom_distance) {
		if(bottom_distance==null) addNullValueField("bottom_distance");
		this.bottom_distance = bottom_distance;
	}

	public Integer getSymbolrotate() { return symbolrotate; }
	public void setSymbolrotate(Integer symbolrotate) {
		if(symbolrotate==null) addNullValueField("symbolrotate");
		this.symbolrotate = symbolrotate;
	}

	public String getConnectnulls() { return connectnulls; }
	public void setConnectnulls(String connectnulls) {
		if(connectnulls==null) addNullValueField("connectnulls");
		this.connectnulls = connectnulls;
	}

	public String getType() { return type; }
	public void setType(String type) {
		if(type==null) throw new BusinessException("Entity : AutoChartsconfig.type must not null!");
		this.type = type;
	}

	public Integer getLeafdepth() { return leafdepth; }
	public void setLeafdepth(Integer leafdepth) {
		if(leafdepth==null) addNullValueField("leafdepth");
		this.leafdepth = leafdepth;
	}

	public String getProvincename() { return provincename; }
	public void setProvincename(String provincename) {
		if(provincename==null) addNullValueField("provincename");
		this.provincename = provincename;
	}

	public String getRosetype() { return rosetype; }
	public void setRosetype(String rosetype) {
		if(rosetype==null) addNullValueField("rosetype");
		this.rosetype = rosetype;
	}

	public Integer getXaxisindex() { return xaxisindex; }
	public void setXaxisindex(Integer xaxisindex) {
		if(xaxisindex==null) addNullValueField("xaxisindex");
		this.xaxisindex = xaxisindex;
	}

	public String getShowsymbol() { return showsymbol; }
	public void setShowsymbol(String showsymbol) {
		if(showsymbol==null) addNullValueField("showsymbol");
		this.showsymbol = showsymbol;
	}

	public String getRadius() { return radius; }
	public void setRadius(String radius) {
		if(radius==null) addNullValueField("radius");
		this.radius = radius;
	}

	public String getSmooth() { return smooth; }
	public void setSmooth(String smooth) {
		if(smooth==null) addNullValueField("smooth");
		this.smooth = smooth;
	}

	public Integer getHeight() { return height; }
	public void setHeight(Integer height) {
		if(height==null) addNullValueField("height");
		this.height = height;
	}

	public Integer getYaxisindex() { return yaxisindex; }
	public void setYaxisindex(Integer yaxisindex) {
		if(yaxisindex==null) addNullValueField("yaxisindex");
		this.yaxisindex = yaxisindex;
	}

	public String getSilent() { return silent; }
	public void setSilent(String silent) {
		if(silent==null) addNullValueField("silent");
		this.silent = silent;
	}

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) addNullValueField("component_id");
		this.component_id = component_id;
	}

	public String getClockwise() { return clockwise; }
	public void setClockwise(String clockwise) {
		if(clockwise==null) addNullValueField("clockwise");
		this.clockwise = clockwise;
	}

	public String getCenter() { return center; }
	public void setCenter(String center) {
		if(center==null) addNullValueField("center");
		this.center = center;
	}

	public String getSort() { return sort; }
	public void setSort(String sort) {
		if(sort==null) addNullValueField("sort");
		this.sort = sort;
	}

	public String getLayout() { return layout; }
	public void setLayout(String layout) {
		if(layout==null) addNullValueField("layout");
		this.layout = layout;
	}

	public Integer getSymbolsize() { return symbolsize; }
	public void setSymbolsize(Integer symbolsize) {
		if(symbolsize==null) addNullValueField("symbolsize");
		this.symbolsize = symbolsize;
	}

	public BigDecimal getConfig_id() { return config_id; }
	public void setConfig_id(BigDecimal config_id) {
		if(config_id==null) throw new BusinessException("Entity : AutoChartsconfig.config_id must not null!");
		this.config_id = config_id;
	}

	public Integer getLeft_distance() { return left_distance; }
	public void setLeft_distance(Integer left_distance) {
		if(left_distance==null) addNullValueField("left_distance");
		this.left_distance = left_distance;
	}

	public Integer getWidth() { return width; }
	public void setWidth(Integer width) {
		if(width==null) addNullValueField("width");
		this.width = width;
	}

	public Integer getRight_distance() { return right_distance; }
	public void setRight_distance(Integer right_distance) {
		if(right_distance==null) addNullValueField("right_distance");
		this.right_distance = right_distance;
	}

	public String getStep() { return step; }
	public void setStep(String step) {
		if(step==null) addNullValueField("step");
		this.step = step;
	}

	public Integer getZ() { return z; }
	public void setZ(Integer z) {
		if(z==null) addNullValueField("z");
		this.z = z;
	}

	public String getLegendhoverlink() { return legendhoverlink; }
	public void setLegendhoverlink(String legendhoverlink) {
		if(legendhoverlink==null) addNullValueField("legendhoverlink");
		this.legendhoverlink = legendhoverlink;
	}

	public Integer getVisiblemin() { return visiblemin; }
	public void setVisiblemin(Integer visiblemin) {
		if(visiblemin==null) addNullValueField("visiblemin");
		this.visiblemin = visiblemin;
	}

	public String getNodeclick() { return nodeclick; }
	public void setNodeclick(String nodeclick) {
		if(nodeclick==null) addNullValueField("nodeclick");
		this.nodeclick = nodeclick;
	}

	public String getPolyline() { return polyline; }
	public void setPolyline(String polyline) {
		if(polyline==null) addNullValueField("polyline");
		this.polyline = polyline;
	}

	public Integer getTop_distance() { return top_distance; }
	public void setTop_distance(Integer top_distance) {
		if(top_distance==null) addNullValueField("top_distance");
		this.top_distance = top_distance;
	}

}