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
@Table(tableName = "auto_legend_info")
public class AutoLegendInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_legend_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("legend_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private Integer itemgap;
	private Integer itemheight;
	private String bottom_distance;
	private String selectedmode;
	private String inactivecolor;
	private Integer intervalnumber;
	private String show;
	private String tooltip;
	private String type;
	private String align;
	private String backgroundcolor;
	private String height;
	private BigDecimal legend_id;
	private Integer padding;
	private BigDecimal component_id;
	private String orient;
	private String bordercolor;
	private String animation;
	private String formatter;
	private Integer borderwidth;
	private String left_distance;
	private String width;
	private Integer borderradius;
	private String right_distance;
	private Integer z;
	private Integer interval;
	private Integer itemwidth;
	private String top_distance;

	public Integer getItemgap() { return itemgap; }
	public void setItemgap(Integer itemgap) {
		if(itemgap==null) addNullValueField("itemgap");
		this.itemgap = itemgap;
	}

	public Integer getItemheight() { return itemheight; }
	public void setItemheight(Integer itemheight) {
		if(itemheight==null) addNullValueField("itemheight");
		this.itemheight = itemheight;
	}

	public String getBottom_distance() { return bottom_distance; }
	public void setBottom_distance(String bottom_distance) {
		if(bottom_distance==null) addNullValueField("bottom_distance");
		this.bottom_distance = bottom_distance;
	}

	public String getSelectedmode() { return selectedmode; }
	public void setSelectedmode(String selectedmode) {
		if(selectedmode==null) addNullValueField("selectedmode");
		this.selectedmode = selectedmode;
	}

	public String getInactivecolor() { return inactivecolor; }
	public void setInactivecolor(String inactivecolor) {
		if(inactivecolor==null) addNullValueField("inactivecolor");
		this.inactivecolor = inactivecolor;
	}

	public Integer getIntervalnumber() { return intervalnumber; }
	public void setIntervalnumber(Integer intervalnumber) {
		if(intervalnumber==null) addNullValueField("intervalnumber");
		this.intervalnumber = intervalnumber;
	}

	public String getShow() { return show; }
	public void setShow(String show) {
		if(show==null) addNullValueField("show");
		this.show = show;
	}

	public String getTooltip() { return tooltip; }
	public void setTooltip(String tooltip) {
		if(tooltip==null) addNullValueField("tooltip");
		this.tooltip = tooltip;
	}

	public String getType() { return type; }
	public void setType(String type) {
		if(type==null) throw new BusinessException("Entity : AutoLegendInfo.type must not null!");
		this.type = type;
	}

	public String getAlign() { return align; }
	public void setAlign(String align) {
		if(align==null) addNullValueField("align");
		this.align = align;
	}

	public String getBackgroundcolor() { return backgroundcolor; }
	public void setBackgroundcolor(String backgroundcolor) {
		if(backgroundcolor==null) addNullValueField("backgroundcolor");
		this.backgroundcolor = backgroundcolor;
	}

	public String getHeight() { return height; }
	public void setHeight(String height) {
		if(height==null) addNullValueField("height");
		this.height = height;
	}

	public BigDecimal getLegend_id() { return legend_id; }
	public void setLegend_id(BigDecimal legend_id) {
		if(legend_id==null) throw new BusinessException("Entity : AutoLegendInfo.legend_id must not null!");
		this.legend_id = legend_id;
	}

	public Integer getPadding() { return padding; }
	public void setPadding(Integer padding) {
		if(padding==null) addNullValueField("padding");
		this.padding = padding;
	}

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) addNullValueField("component_id");
		this.component_id = component_id;
	}

	public String getOrient() { return orient; }
	public void setOrient(String orient) {
		if(orient==null) addNullValueField("orient");
		this.orient = orient;
	}

	public String getBordercolor() { return bordercolor; }
	public void setBordercolor(String bordercolor) {
		if(bordercolor==null) addNullValueField("bordercolor");
		this.bordercolor = bordercolor;
	}

	public String getAnimation() { return animation; }
	public void setAnimation(String animation) {
		if(animation==null) addNullValueField("animation");
		this.animation = animation;
	}

	public String getFormatter() { return formatter; }
	public void setFormatter(String formatter) {
		if(formatter==null) addNullValueField("formatter");
		this.formatter = formatter;
	}

	public Integer getBorderwidth() { return borderwidth; }
	public void setBorderwidth(Integer borderwidth) {
		if(borderwidth==null) addNullValueField("borderwidth");
		this.borderwidth = borderwidth;
	}

	public String getLeft_distance() { return left_distance; }
	public void setLeft_distance(String left_distance) {
		if(left_distance==null) addNullValueField("left_distance");
		this.left_distance = left_distance;
	}

	public String getWidth() { return width; }
	public void setWidth(String width) {
		if(width==null) addNullValueField("width");
		this.width = width;
	}

	public Integer getBorderradius() { return borderradius; }
	public void setBorderradius(Integer borderradius) {
		if(borderradius==null) addNullValueField("borderradius");
		this.borderradius = borderradius;
	}

	public String getRight_distance() { return right_distance; }
	public void setRight_distance(String right_distance) {
		if(right_distance==null) addNullValueField("right_distance");
		this.right_distance = right_distance;
	}

	public Integer getZ() { return z; }
	public void setZ(Integer z) {
		if(z==null) addNullValueField("z");
		this.z = z;
	}

	public Integer getInterval() { return interval; }
	public void setInterval(Integer interval) {
		if(interval==null) addNullValueField("interval");
		this.interval = interval;
	}

	public Integer getItemwidth() { return itemwidth; }
	public void setItemwidth(Integer itemwidth) {
		if(itemwidth==null) addNullValueField("itemwidth");
		this.itemwidth = itemwidth;
	}

	public String getTop_distance() { return top_distance; }
	public void setTop_distance(String top_distance) {
		if(top_distance==null) addNullValueField("top_distance");
		this.top_distance = top_distance;
	}

}