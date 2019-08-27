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
@Table(tableName = "report_configuration_table")
public class ReportConfigurationTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "report_configuration_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("configuration_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String seriessort;
	private String maxvalue;
	private String wavetextcolor;
	private Integer xaxisnamegap;
	private String textcolor;
	private String fontsize;
	private String waveheight;
	private String funnelalign;
	private String backgroundcolor;
	private Integer yaxismaxinterval;
	private Integer tickpadding;
	private String fontstyle;
	private String xaxisinverse;
	private BigDecimal create_id;
	private Integer nodewidth;
	private String textsize;
	private String rosetype;
	private String ismerge;
	private Integer legendrectsize;
	private String yaxisnamelocation;
	private String isaxislabel;
	private String titlefontsize;
	private String fontcolor;
	private String create_date;
	private String xaxisposition;
	private String radius;
	private BigDecimal configuration_id;
	private Integer ticksize;
	private Integer graphheight;
	private String yaxisposition;
	private String xaxisname;
	private String flinecolor;
	private String axislabelcolor;
	private String create_time;
	private String shape;
	private String waveoffset;
	private Integer gapbetweengroups;
	private String circlefillgap;
	private String circlecolor;
	private String expendcolor;
	private Integer spaceforlabels;
	private Integer flinewidth;
	private String barcolor;
	private String toolboxshow;
	private String titilefontcolor;
	private Integer xaxismininterval;
	private Integer seriesborderwidth;
	private String positivecolor;
	private Integer barheight;
	private Integer xaxismaxinterval;
	private String configuration_name;
	private String seriesbordercolor;
	private Integer yaxismininterval;
	private String yaxisinverse;
	private String isdatavalue;
	private String avoidlabeloverlap;
	private String titletext;
	private String wavecolor;
	private Integer minangle;
	private Integer xaxisfontsize;
	private Integer yaxisfontsize;
	private String redius;
	private String titlex;
	private Integer nodepadding;
	private String titletextalign;
	private String fontweight;
	private String titletextbaseline;
	private String legendshow;
	private Integer legendspacing;
	private String collapsecolor;
	private String displaypercent;
	private String circlethickness;
	private String titlesubtext;
	private Integer titleleft;
	private String textvertposition;
	private String titlefontstyle;
	private Integer seriesleft;
	private String yaxisname;
	private String minvalue;
	private String titlelink;
	private Integer graphwidth;
	private BigDecimal report_type_id;
	private Integer seriestop;
	private String myshape;
	private String negativecolor;
	private Integer yaxisnamegap;
	private Integer spaceforlegend;
	private String radialgradientcolor1;
	private String wavecount;
	private String titletop;
	private String radialgradientcolor0;
	private String x;
	private String y;
	private String xaxisnamelocation;
	private Integer seriesbottom;
	private BigDecimal span;

	public String getSeriessort() { return seriessort; }
	public void setSeriessort(String seriessort) {
		if(seriessort==null) addNullValueField("seriessort");
		this.seriessort = seriessort;
	}

	public String getMaxvalue() { return maxvalue; }
	public void setMaxvalue(String maxvalue) {
		if(maxvalue==null) addNullValueField("maxvalue");
		this.maxvalue = maxvalue;
	}

	public String getWavetextcolor() { return wavetextcolor; }
	public void setWavetextcolor(String wavetextcolor) {
		if(wavetextcolor==null) addNullValueField("wavetextcolor");
		this.wavetextcolor = wavetextcolor;
	}

	public Integer getXaxisnamegap() { return xaxisnamegap; }
	public void setXaxisnamegap(Integer xaxisnamegap) {
		if(xaxisnamegap==null) addNullValueField("xaxisnamegap");
		this.xaxisnamegap = xaxisnamegap;
	}

	public String getTextcolor() { return textcolor; }
	public void setTextcolor(String textcolor) {
		if(textcolor==null) addNullValueField("textcolor");
		this.textcolor = textcolor;
	}

	public String getFontsize() { return fontsize; }
	public void setFontsize(String fontsize) {
		if(fontsize==null) addNullValueField("fontsize");
		this.fontsize = fontsize;
	}

	public String getWaveheight() { return waveheight; }
	public void setWaveheight(String waveheight) {
		if(waveheight==null) addNullValueField("waveheight");
		this.waveheight = waveheight;
	}

	public String getFunnelalign() { return funnelalign; }
	public void setFunnelalign(String funnelalign) {
		if(funnelalign==null) addNullValueField("funnelalign");
		this.funnelalign = funnelalign;
	}

	public String getBackgroundcolor() { return backgroundcolor; }
	public void setBackgroundcolor(String backgroundcolor) {
		if(backgroundcolor==null) addNullValueField("backgroundcolor");
		this.backgroundcolor = backgroundcolor;
	}

	public Integer getYaxismaxinterval() { return yaxismaxinterval; }
	public void setYaxismaxinterval(Integer yaxismaxinterval) {
		if(yaxismaxinterval==null) addNullValueField("yaxismaxinterval");
		this.yaxismaxinterval = yaxismaxinterval;
	}

	public Integer getTickpadding() { return tickpadding; }
	public void setTickpadding(Integer tickpadding) {
		if(tickpadding==null) addNullValueField("tickpadding");
		this.tickpadding = tickpadding;
	}

	public String getFontstyle() { return fontstyle; }
	public void setFontstyle(String fontstyle) {
		if(fontstyle==null) addNullValueField("fontstyle");
		this.fontstyle = fontstyle;
	}

	public String getXaxisinverse() { return xaxisinverse; }
	public void setXaxisinverse(String xaxisinverse) {
		if(xaxisinverse==null) addNullValueField("xaxisinverse");
		this.xaxisinverse = xaxisinverse;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : ReportConfigurationTable.create_id must not null!");
		this.create_id = create_id;
	}

	public Integer getNodewidth() { return nodewidth; }
	public void setNodewidth(Integer nodewidth) {
		if(nodewidth==null) addNullValueField("nodewidth");
		this.nodewidth = nodewidth;
	}

	public String getTextsize() { return textsize; }
	public void setTextsize(String textsize) {
		if(textsize==null) addNullValueField("textsize");
		this.textsize = textsize;
	}

	public String getRosetype() { return rosetype; }
	public void setRosetype(String rosetype) {
		if(rosetype==null) addNullValueField("rosetype");
		this.rosetype = rosetype;
	}

	public String getIsmerge() { return ismerge; }
	public void setIsmerge(String ismerge) {
		if(ismerge==null) addNullValueField("ismerge");
		this.ismerge = ismerge;
	}

	public Integer getLegendrectsize() { return legendrectsize; }
	public void setLegendrectsize(Integer legendrectsize) {
		if(legendrectsize==null) addNullValueField("legendrectsize");
		this.legendrectsize = legendrectsize;
	}

	public String getYaxisnamelocation() { return yaxisnamelocation; }
	public void setYaxisnamelocation(String yaxisnamelocation) {
		if(yaxisnamelocation==null) addNullValueField("yaxisnamelocation");
		this.yaxisnamelocation = yaxisnamelocation;
	}

	public String getIsaxislabel() { return isaxislabel; }
	public void setIsaxislabel(String isaxislabel) {
		if(isaxislabel==null) addNullValueField("isaxislabel");
		this.isaxislabel = isaxislabel;
	}

	public String getTitlefontsize() { return titlefontsize; }
	public void setTitlefontsize(String titlefontsize) {
		if(titlefontsize==null) addNullValueField("titlefontsize");
		this.titlefontsize = titlefontsize;
	}

	public String getFontcolor() { return fontcolor; }
	public void setFontcolor(String fontcolor) {
		if(fontcolor==null) addNullValueField("fontcolor");
		this.fontcolor = fontcolor;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : ReportConfigurationTable.create_date must not null!");
		this.create_date = create_date;
	}

	public String getXaxisposition() { return xaxisposition; }
	public void setXaxisposition(String xaxisposition) {
		if(xaxisposition==null) addNullValueField("xaxisposition");
		this.xaxisposition = xaxisposition;
	}

	public String getRadius() { return radius; }
	public void setRadius(String radius) {
		if(radius==null) addNullValueField("radius");
		this.radius = radius;
	}

	public BigDecimal getConfiguration_id() { return configuration_id; }
	public void setConfiguration_id(BigDecimal configuration_id) {
		if(configuration_id==null) throw new BusinessException("Entity : ReportConfigurationTable.configuration_id must not null!");
		this.configuration_id = configuration_id;
	}

	public Integer getTicksize() { return ticksize; }
	public void setTicksize(Integer ticksize) {
		if(ticksize==null) addNullValueField("ticksize");
		this.ticksize = ticksize;
	}

	public Integer getGraphheight() { return graphheight; }
	public void setGraphheight(Integer graphheight) {
		if(graphheight==null) addNullValueField("graphheight");
		this.graphheight = graphheight;
	}

	public String getYaxisposition() { return yaxisposition; }
	public void setYaxisposition(String yaxisposition) {
		if(yaxisposition==null) addNullValueField("yaxisposition");
		this.yaxisposition = yaxisposition;
	}

	public String getXaxisname() { return xaxisname; }
	public void setXaxisname(String xaxisname) {
		if(xaxisname==null) addNullValueField("xaxisname");
		this.xaxisname = xaxisname;
	}

	public String getFlinecolor() { return flinecolor; }
	public void setFlinecolor(String flinecolor) {
		if(flinecolor==null) addNullValueField("flinecolor");
		this.flinecolor = flinecolor;
	}

	public String getAxislabelcolor() { return axislabelcolor; }
	public void setAxislabelcolor(String axislabelcolor) {
		if(axislabelcolor==null) addNullValueField("axislabelcolor");
		this.axislabelcolor = axislabelcolor;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : ReportConfigurationTable.create_time must not null!");
		this.create_time = create_time;
	}

	public String getShape() { return shape; }
	public void setShape(String shape) {
		if(shape==null) addNullValueField("shape");
		this.shape = shape;
	}

	public String getWaveoffset() { return waveoffset; }
	public void setWaveoffset(String waveoffset) {
		if(waveoffset==null) addNullValueField("waveoffset");
		this.waveoffset = waveoffset;
	}

	public Integer getGapbetweengroups() { return gapbetweengroups; }
	public void setGapbetweengroups(Integer gapbetweengroups) {
		if(gapbetweengroups==null) addNullValueField("gapbetweengroups");
		this.gapbetweengroups = gapbetweengroups;
	}

	public String getCirclefillgap() { return circlefillgap; }
	public void setCirclefillgap(String circlefillgap) {
		if(circlefillgap==null) addNullValueField("circlefillgap");
		this.circlefillgap = circlefillgap;
	}

	public String getCirclecolor() { return circlecolor; }
	public void setCirclecolor(String circlecolor) {
		if(circlecolor==null) addNullValueField("circlecolor");
		this.circlecolor = circlecolor;
	}

	public String getExpendcolor() { return expendcolor; }
	public void setExpendcolor(String expendcolor) {
		if(expendcolor==null) addNullValueField("expendcolor");
		this.expendcolor = expendcolor;
	}

	public Integer getSpaceforlabels() { return spaceforlabels; }
	public void setSpaceforlabels(Integer spaceforlabels) {
		if(spaceforlabels==null) addNullValueField("spaceforlabels");
		this.spaceforlabels = spaceforlabels;
	}

	public Integer getFlinewidth() { return flinewidth; }
	public void setFlinewidth(Integer flinewidth) {
		if(flinewidth==null) addNullValueField("flinewidth");
		this.flinewidth = flinewidth;
	}

	public String getBarcolor() { return barcolor; }
	public void setBarcolor(String barcolor) {
		if(barcolor==null) addNullValueField("barcolor");
		this.barcolor = barcolor;
	}

	public String getToolboxshow() { return toolboxshow; }
	public void setToolboxshow(String toolboxshow) {
		if(toolboxshow==null) addNullValueField("toolboxshow");
		this.toolboxshow = toolboxshow;
	}

	public String getTitilefontcolor() { return titilefontcolor; }
	public void setTitilefontcolor(String titilefontcolor) {
		if(titilefontcolor==null) addNullValueField("titilefontcolor");
		this.titilefontcolor = titilefontcolor;
	}

	public Integer getXaxismininterval() { return xaxismininterval; }
	public void setXaxismininterval(Integer xaxismininterval) {
		if(xaxismininterval==null) addNullValueField("xaxismininterval");
		this.xaxismininterval = xaxismininterval;
	}

	public Integer getSeriesborderwidth() { return seriesborderwidth; }
	public void setSeriesborderwidth(Integer seriesborderwidth) {
		if(seriesborderwidth==null) addNullValueField("seriesborderwidth");
		this.seriesborderwidth = seriesborderwidth;
	}

	public String getPositivecolor() { return positivecolor; }
	public void setPositivecolor(String positivecolor) {
		if(positivecolor==null) addNullValueField("positivecolor");
		this.positivecolor = positivecolor;
	}

	public Integer getBarheight() { return barheight; }
	public void setBarheight(Integer barheight) {
		if(barheight==null) addNullValueField("barheight");
		this.barheight = barheight;
	}

	public Integer getXaxismaxinterval() { return xaxismaxinterval; }
	public void setXaxismaxinterval(Integer xaxismaxinterval) {
		if(xaxismaxinterval==null) addNullValueField("xaxismaxinterval");
		this.xaxismaxinterval = xaxismaxinterval;
	}

	public String getConfiguration_name() { return configuration_name; }
	public void setConfiguration_name(String configuration_name) {
		if(configuration_name==null) throw new BusinessException("Entity : ReportConfigurationTable.configuration_name must not null!");
		this.configuration_name = configuration_name;
	}

	public String getSeriesbordercolor() { return seriesbordercolor; }
	public void setSeriesbordercolor(String seriesbordercolor) {
		if(seriesbordercolor==null) addNullValueField("seriesbordercolor");
		this.seriesbordercolor = seriesbordercolor;
	}

	public Integer getYaxismininterval() { return yaxismininterval; }
	public void setYaxismininterval(Integer yaxismininterval) {
		if(yaxismininterval==null) addNullValueField("yaxismininterval");
		this.yaxismininterval = yaxismininterval;
	}

	public String getYaxisinverse() { return yaxisinverse; }
	public void setYaxisinverse(String yaxisinverse) {
		if(yaxisinverse==null) addNullValueField("yaxisinverse");
		this.yaxisinverse = yaxisinverse;
	}

	public String getIsdatavalue() { return isdatavalue; }
	public void setIsdatavalue(String isdatavalue) {
		if(isdatavalue==null) addNullValueField("isdatavalue");
		this.isdatavalue = isdatavalue;
	}

	public String getAvoidlabeloverlap() { return avoidlabeloverlap; }
	public void setAvoidlabeloverlap(String avoidlabeloverlap) {
		if(avoidlabeloverlap==null) addNullValueField("avoidlabeloverlap");
		this.avoidlabeloverlap = avoidlabeloverlap;
	}

	public String getTitletext() { return titletext; }
	public void setTitletext(String titletext) {
		if(titletext==null) addNullValueField("titletext");
		this.titletext = titletext;
	}

	public String getWavecolor() { return wavecolor; }
	public void setWavecolor(String wavecolor) {
		if(wavecolor==null) addNullValueField("wavecolor");
		this.wavecolor = wavecolor;
	}

	public Integer getMinangle() { return minangle; }
	public void setMinangle(Integer minangle) {
		if(minangle==null) addNullValueField("minangle");
		this.minangle = minangle;
	}

	public Integer getXaxisfontsize() { return xaxisfontsize; }
	public void setXaxisfontsize(Integer xaxisfontsize) {
		if(xaxisfontsize==null) addNullValueField("xaxisfontsize");
		this.xaxisfontsize = xaxisfontsize;
	}

	public Integer getYaxisfontsize() { return yaxisfontsize; }
	public void setYaxisfontsize(Integer yaxisfontsize) {
		if(yaxisfontsize==null) addNullValueField("yaxisfontsize");
		this.yaxisfontsize = yaxisfontsize;
	}

	public String getRedius() { return redius; }
	public void setRedius(String redius) {
		if(redius==null) addNullValueField("redius");
		this.redius = redius;
	}

	public String getTitlex() { return titlex; }
	public void setTitlex(String titlex) {
		if(titlex==null) addNullValueField("titlex");
		this.titlex = titlex;
	}

	public Integer getNodepadding() { return nodepadding; }
	public void setNodepadding(Integer nodepadding) {
		if(nodepadding==null) addNullValueField("nodepadding");
		this.nodepadding = nodepadding;
	}

	public String getTitletextalign() { return titletextalign; }
	public void setTitletextalign(String titletextalign) {
		if(titletextalign==null) addNullValueField("titletextalign");
		this.titletextalign = titletextalign;
	}

	public String getFontweight() { return fontweight; }
	public void setFontweight(String fontweight) {
		if(fontweight==null) addNullValueField("fontweight");
		this.fontweight = fontweight;
	}

	public String getTitletextbaseline() { return titletextbaseline; }
	public void setTitletextbaseline(String titletextbaseline) {
		if(titletextbaseline==null) addNullValueField("titletextbaseline");
		this.titletextbaseline = titletextbaseline;
	}

	public String getLegendshow() { return legendshow; }
	public void setLegendshow(String legendshow) {
		if(legendshow==null) addNullValueField("legendshow");
		this.legendshow = legendshow;
	}

	public Integer getLegendspacing() { return legendspacing; }
	public void setLegendspacing(Integer legendspacing) {
		if(legendspacing==null) addNullValueField("legendspacing");
		this.legendspacing = legendspacing;
	}

	public String getCollapsecolor() { return collapsecolor; }
	public void setCollapsecolor(String collapsecolor) {
		if(collapsecolor==null) addNullValueField("collapsecolor");
		this.collapsecolor = collapsecolor;
	}

	public String getDisplaypercent() { return displaypercent; }
	public void setDisplaypercent(String displaypercent) {
		if(displaypercent==null) addNullValueField("displaypercent");
		this.displaypercent = displaypercent;
	}

	public String getCirclethickness() { return circlethickness; }
	public void setCirclethickness(String circlethickness) {
		if(circlethickness==null) addNullValueField("circlethickness");
		this.circlethickness = circlethickness;
	}

	public String getTitlesubtext() { return titlesubtext; }
	public void setTitlesubtext(String titlesubtext) {
		if(titlesubtext==null) addNullValueField("titlesubtext");
		this.titlesubtext = titlesubtext;
	}

	public Integer getTitleleft() { return titleleft; }
	public void setTitleleft(Integer titleleft) {
		if(titleleft==null) addNullValueField("titleleft");
		this.titleleft = titleleft;
	}

	public String getTextvertposition() { return textvertposition; }
	public void setTextvertposition(String textvertposition) {
		if(textvertposition==null) addNullValueField("textvertposition");
		this.textvertposition = textvertposition;
	}

	public String getTitlefontstyle() { return titlefontstyle; }
	public void setTitlefontstyle(String titlefontstyle) {
		if(titlefontstyle==null) addNullValueField("titlefontstyle");
		this.titlefontstyle = titlefontstyle;
	}

	public Integer getSeriesleft() { return seriesleft; }
	public void setSeriesleft(Integer seriesleft) {
		if(seriesleft==null) addNullValueField("seriesleft");
		this.seriesleft = seriesleft;
	}

	public String getYaxisname() { return yaxisname; }
	public void setYaxisname(String yaxisname) {
		if(yaxisname==null) addNullValueField("yaxisname");
		this.yaxisname = yaxisname;
	}

	public String getMinvalue() { return minvalue; }
	public void setMinvalue(String minvalue) {
		if(minvalue==null) addNullValueField("minvalue");
		this.minvalue = minvalue;
	}

	public String getTitlelink() { return titlelink; }
	public void setTitlelink(String titlelink) {
		if(titlelink==null) addNullValueField("titlelink");
		this.titlelink = titlelink;
	}

	public Integer getGraphwidth() { return graphwidth; }
	public void setGraphwidth(Integer graphwidth) {
		if(graphwidth==null) addNullValueField("graphwidth");
		this.graphwidth = graphwidth;
	}

	public BigDecimal getReport_type_id() { return report_type_id; }
	public void setReport_type_id(BigDecimal report_type_id) {
		if(report_type_id==null) throw new BusinessException("Entity : ReportConfigurationTable.report_type_id must not null!");
		this.report_type_id = report_type_id;
	}

	public Integer getSeriestop() { return seriestop; }
	public void setSeriestop(Integer seriestop) {
		if(seriestop==null) addNullValueField("seriestop");
		this.seriestop = seriestop;
	}

	public String getMyshape() { return myshape; }
	public void setMyshape(String myshape) {
		if(myshape==null) addNullValueField("myshape");
		this.myshape = myshape;
	}

	public String getNegativecolor() { return negativecolor; }
	public void setNegativecolor(String negativecolor) {
		if(negativecolor==null) addNullValueField("negativecolor");
		this.negativecolor = negativecolor;
	}

	public Integer getYaxisnamegap() { return yaxisnamegap; }
	public void setYaxisnamegap(Integer yaxisnamegap) {
		if(yaxisnamegap==null) addNullValueField("yaxisnamegap");
		this.yaxisnamegap = yaxisnamegap;
	}

	public Integer getSpaceforlegend() { return spaceforlegend; }
	public void setSpaceforlegend(Integer spaceforlegend) {
		if(spaceforlegend==null) addNullValueField("spaceforlegend");
		this.spaceforlegend = spaceforlegend;
	}

	public String getRadialgradientcolor1() { return radialgradientcolor1; }
	public void setRadialgradientcolor1(String radialgradientcolor1) {
		if(radialgradientcolor1==null) addNullValueField("radialgradientcolor1");
		this.radialgradientcolor1 = radialgradientcolor1;
	}

	public String getWavecount() { return wavecount; }
	public void setWavecount(String wavecount) {
		if(wavecount==null) addNullValueField("wavecount");
		this.wavecount = wavecount;
	}

	public String getTitletop() { return titletop; }
	public void setTitletop(String titletop) {
		if(titletop==null) addNullValueField("titletop");
		this.titletop = titletop;
	}

	public String getRadialgradientcolor0() { return radialgradientcolor0; }
	public void setRadialgradientcolor0(String radialgradientcolor0) {
		if(radialgradientcolor0==null) addNullValueField("radialgradientcolor0");
		this.radialgradientcolor0 = radialgradientcolor0;
	}

	public String getX() { return x; }
	public void setX(String x) {
		if(x==null) addNullValueField("x");
		this.x = x;
	}

	public String getY() { return y; }
	public void setY(String y) {
		if(y==null) addNullValueField("y");
		this.y = y;
	}

	public String getXaxisnamelocation() { return xaxisnamelocation; }
	public void setXaxisnamelocation(String xaxisnamelocation) {
		if(xaxisnamelocation==null) addNullValueField("xaxisnamelocation");
		this.xaxisnamelocation = xaxisnamelocation;
	}

	public Integer getSeriesbottom() { return seriesbottom; }
	public void setSeriesbottom(Integer seriesbottom) {
		if(seriesbottom==null) addNullValueField("seriesbottom");
		this.seriesbottom = seriesbottom;
	}

	public BigDecimal getSpan() { return span; }
	public void setSpan(BigDecimal span) {
		if(span==null) addNullValueField("span");
		this.span = span;
	}

}