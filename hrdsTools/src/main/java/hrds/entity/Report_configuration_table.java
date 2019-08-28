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
 * 报表详细配置表
 */
@Table(tableName = "report_configuration_table")
public class Report_configuration_table extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "report_configuration_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 报表详细配置表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("configuration_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long configuration_id; //配置编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String configuration_name; //配置名称
	private String minvalue; //最小值
	private String maxvalue; //最大值
	private String circlethickness; //圆圈边框厚度
	private String circlefillgap; //圆圈间隙
	private String circlecolor; //圆圈颜色
	private String waveheight; //波浪高度
	private String wavecount; //波浪个数
	private String wavecolor; //波浪颜色
	private String waveoffset; //波浪微调
	private String textvertposition; //文本位置
	private String textsize; //圆文字大小
	private String displaypercent; //是否显示百分比
	private String textcolor; //文本颜色
	private String wavetextcolor; //波浪文本颜色
	private String redius; //圆半径(百分比)
	private String y; //说明所在y轴位置
	private String rosetype; //展现类型
	private String avoidlabeloverlap; //是否不允许标注重叠
	private String fontsize; //图表文字大小
	private String fontweight; //图表文字粗细
	private String funnelalign; //漏斗图水平方向对齐布局类型
	private Integer xaxismininterval; //x轴刻度最小值
	private Integer xaxismaxinterval; //x轴刻度最大值
	private BigDecimal span; //柱条间距
	private Integer yaxismininterval; //y轴刻度最小值
	private Integer yaxismaxinterval; //y轴刻度最大值
	private String xaxisposition; //x轴位置
	private String yaxisposition; //y轴位置
	private Integer ticksize; //标记大小
	private Integer tickpadding; //标记内距
	private Integer graphwidth; //图宽
	private Integer graphheight; //图高
	private String positivecolor; //正数颜色
	private String negativecolor; //负数颜色
	private String titletext; //主标题文本
	private String titlelink; //主标题文本超链接
	private String titilefontcolor; //标题字体颜色
	private String titlefontstyle; //标题字体风格
	private String titlefontsize; //标题文字大小
	private String titletextalign; //标题文本水平对齐方式
	private String titletextbaseline; //标题文本垂直对齐方式
	private String titlesubtext; //副标题文本
	private String backgroundcolor; //背景色
	private String x; //说明所在x轴位置
	private String legendshow; //是否显示说明
	private Integer minangle; //最小扇区角度
	private String fontcolor; //图表字体颜色
	private String fontstyle; //图表字体风格
	private String titlex; //标题所在x轴对齐方式
	private String radius; //圆半径
	private String xaxisname; //x坐标轴名称
	private String xaxisnamelocation; //x坐标轴名称显示位置
	private Integer xaxisfontsize; //x轴坐标名称文字大小
	private Integer xaxisnamegap; //x轴坐标名称与轴线之间的距离
	private String xaxisinverse; //x轴是否为反向坐标轴
	private String yaxisname; //y坐标轴名称
	private String yaxisnamelocation; //y坐标轴名称显示位置
	private Integer yaxisfontsize; //y轴坐标名称文字大小
	private Integer yaxisnamegap; //y轴坐标名称与轴线之间的距离
	private String yaxisinverse; //y轴是否为反向坐标轴
	private String titletop; //标题离上侧距离
	private Integer barheight; //柱高
	private Integer gapbetweengroups; //每组柱条间距
	private Integer spaceforlabels; //标签空间
	private Integer spaceforlegend; //图例空间
	private Integer legendrectsize; //图例大小
	private Integer legendspacing; //图例间距
	private String toolboxshow; //是否显示提示框
	private Integer seriesleft; //图表左边的距离
	private Integer seriestop; //图表顶部的距离
	private Integer seriesbottom; //图表底部的距离
	private String seriessort; //漏斗图的排序
	private String seriesbordercolor; //图表的边框颜色
	private Integer seriesborderwidth; //图表的边框宽度
	private Integer flinewidth; //线宽度
	private String flinecolor; //线颜色
	private String expendcolor; //展开节点颜色
	private String collapsecolor; //折叠节点颜色
	private String myshape; //节点形状
	private Integer nodewidth; //节点宽度
	private Integer nodepadding; //节点间距
	private Integer titleleft; //主标题文本的距离左边的距离
	private String ismerge; //是否合并重复
	private String barcolor; //柱条颜色
	private Long report_type_id; //报表类型编号
	private Long create_id; //用户ID
	private String shape; //雷达绘制类型
	private String axislabelcolor; //标签颜色
	private String isaxislabel; //是否显示标签
	private String radialgradientcolor0; //0处径向渐变颜色
	private String radialgradientcolor1; //100处径向渐变颜色
	private String isdatavalue; //是否显示数据值

	/** 取得：配置编号 */
	public Long getConfiguration_id(){
		return configuration_id;
	}
	/** 设置：配置编号 */
	public void setConfiguration_id(Long configuration_id){
		this.configuration_id=configuration_id;
	}
	/** 设置：配置编号 */
	public void setConfiguration_id(String configuration_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(configuration_id)){
			this.configuration_id=new Long(configuration_id);
		}
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：配置名称 */
	public String getConfiguration_name(){
		return configuration_name;
	}
	/** 设置：配置名称 */
	public void setConfiguration_name(String configuration_name){
		this.configuration_name=configuration_name;
	}
	/** 取得：最小值 */
	public String getMinvalue(){
		return minvalue;
	}
	/** 设置：最小值 */
	public void setMinvalue(String minvalue){
		this.minvalue=minvalue;
	}
	/** 取得：最大值 */
	public String getMaxvalue(){
		return maxvalue;
	}
	/** 设置：最大值 */
	public void setMaxvalue(String maxvalue){
		this.maxvalue=maxvalue;
	}
	/** 取得：圆圈边框厚度 */
	public String getCirclethickness(){
		return circlethickness;
	}
	/** 设置：圆圈边框厚度 */
	public void setCirclethickness(String circlethickness){
		this.circlethickness=circlethickness;
	}
	/** 取得：圆圈间隙 */
	public String getCirclefillgap(){
		return circlefillgap;
	}
	/** 设置：圆圈间隙 */
	public void setCirclefillgap(String circlefillgap){
		this.circlefillgap=circlefillgap;
	}
	/** 取得：圆圈颜色 */
	public String getCirclecolor(){
		return circlecolor;
	}
	/** 设置：圆圈颜色 */
	public void setCirclecolor(String circlecolor){
		this.circlecolor=circlecolor;
	}
	/** 取得：波浪高度 */
	public String getWaveheight(){
		return waveheight;
	}
	/** 设置：波浪高度 */
	public void setWaveheight(String waveheight){
		this.waveheight=waveheight;
	}
	/** 取得：波浪个数 */
	public String getWavecount(){
		return wavecount;
	}
	/** 设置：波浪个数 */
	public void setWavecount(String wavecount){
		this.wavecount=wavecount;
	}
	/** 取得：波浪颜色 */
	public String getWavecolor(){
		return wavecolor;
	}
	/** 设置：波浪颜色 */
	public void setWavecolor(String wavecolor){
		this.wavecolor=wavecolor;
	}
	/** 取得：波浪微调 */
	public String getWaveoffset(){
		return waveoffset;
	}
	/** 设置：波浪微调 */
	public void setWaveoffset(String waveoffset){
		this.waveoffset=waveoffset;
	}
	/** 取得：文本位置 */
	public String getTextvertposition(){
		return textvertposition;
	}
	/** 设置：文本位置 */
	public void setTextvertposition(String textvertposition){
		this.textvertposition=textvertposition;
	}
	/** 取得：圆文字大小 */
	public String getTextsize(){
		return textsize;
	}
	/** 设置：圆文字大小 */
	public void setTextsize(String textsize){
		this.textsize=textsize;
	}
	/** 取得：是否显示百分比 */
	public String getDisplaypercent(){
		return displaypercent;
	}
	/** 设置：是否显示百分比 */
	public void setDisplaypercent(String displaypercent){
		this.displaypercent=displaypercent;
	}
	/** 取得：文本颜色 */
	public String getTextcolor(){
		return textcolor;
	}
	/** 设置：文本颜色 */
	public void setTextcolor(String textcolor){
		this.textcolor=textcolor;
	}
	/** 取得：波浪文本颜色 */
	public String getWavetextcolor(){
		return wavetextcolor;
	}
	/** 设置：波浪文本颜色 */
	public void setWavetextcolor(String wavetextcolor){
		this.wavetextcolor=wavetextcolor;
	}
	/** 取得：圆半径(百分比) */
	public String getRedius(){
		return redius;
	}
	/** 设置：圆半径(百分比) */
	public void setRedius(String redius){
		this.redius=redius;
	}
	/** 取得：说明所在y轴位置 */
	public String getY(){
		return y;
	}
	/** 设置：说明所在y轴位置 */
	public void setY(String y){
		this.y=y;
	}
	/** 取得：展现类型 */
	public String getRosetype(){
		return rosetype;
	}
	/** 设置：展现类型 */
	public void setRosetype(String rosetype){
		this.rosetype=rosetype;
	}
	/** 取得：是否不允许标注重叠 */
	public String getAvoidlabeloverlap(){
		return avoidlabeloverlap;
	}
	/** 设置：是否不允许标注重叠 */
	public void setAvoidlabeloverlap(String avoidlabeloverlap){
		this.avoidlabeloverlap=avoidlabeloverlap;
	}
	/** 取得：图表文字大小 */
	public String getFontsize(){
		return fontsize;
	}
	/** 设置：图表文字大小 */
	public void setFontsize(String fontsize){
		this.fontsize=fontsize;
	}
	/** 取得：图表文字粗细 */
	public String getFontweight(){
		return fontweight;
	}
	/** 设置：图表文字粗细 */
	public void setFontweight(String fontweight){
		this.fontweight=fontweight;
	}
	/** 取得：漏斗图水平方向对齐布局类型 */
	public String getFunnelalign(){
		return funnelalign;
	}
	/** 设置：漏斗图水平方向对齐布局类型 */
	public void setFunnelalign(String funnelalign){
		this.funnelalign=funnelalign;
	}
	/** 取得：x轴刻度最小值 */
	public Integer getXaxismininterval(){
		return xaxismininterval;
	}
	/** 设置：x轴刻度最小值 */
	public void setXaxismininterval(Integer xaxismininterval){
		this.xaxismininterval=xaxismininterval;
	}
	/** 设置：x轴刻度最小值 */
	public void setXaxismininterval(String xaxismininterval){
		if(!fd.ng.core.utils.StringUtil.isEmpty(xaxismininterval)){
			this.xaxismininterval=new Integer(xaxismininterval);
		}
	}
	/** 取得：x轴刻度最大值 */
	public Integer getXaxismaxinterval(){
		return xaxismaxinterval;
	}
	/** 设置：x轴刻度最大值 */
	public void setXaxismaxinterval(Integer xaxismaxinterval){
		this.xaxismaxinterval=xaxismaxinterval;
	}
	/** 设置：x轴刻度最大值 */
	public void setXaxismaxinterval(String xaxismaxinterval){
		if(!fd.ng.core.utils.StringUtil.isEmpty(xaxismaxinterval)){
			this.xaxismaxinterval=new Integer(xaxismaxinterval);
		}
	}
	/** 取得：柱条间距 */
	public BigDecimal getSpan(){
		return span;
	}
	/** 设置：柱条间距 */
	public void setSpan(BigDecimal span){
		this.span=span;
	}
	/** 设置：柱条间距 */
	public void setSpan(String span){
		if(!fd.ng.core.utils.StringUtil.isEmpty(span)){
			this.span=new BigDecimal(span);
		}
	}
	/** 取得：y轴刻度最小值 */
	public Integer getYaxismininterval(){
		return yaxismininterval;
	}
	/** 设置：y轴刻度最小值 */
	public void setYaxismininterval(Integer yaxismininterval){
		this.yaxismininterval=yaxismininterval;
	}
	/** 设置：y轴刻度最小值 */
	public void setYaxismininterval(String yaxismininterval){
		if(!fd.ng.core.utils.StringUtil.isEmpty(yaxismininterval)){
			this.yaxismininterval=new Integer(yaxismininterval);
		}
	}
	/** 取得：y轴刻度最大值 */
	public Integer getYaxismaxinterval(){
		return yaxismaxinterval;
	}
	/** 设置：y轴刻度最大值 */
	public void setYaxismaxinterval(Integer yaxismaxinterval){
		this.yaxismaxinterval=yaxismaxinterval;
	}
	/** 设置：y轴刻度最大值 */
	public void setYaxismaxinterval(String yaxismaxinterval){
		if(!fd.ng.core.utils.StringUtil.isEmpty(yaxismaxinterval)){
			this.yaxismaxinterval=new Integer(yaxismaxinterval);
		}
	}
	/** 取得：x轴位置 */
	public String getXaxisposition(){
		return xaxisposition;
	}
	/** 设置：x轴位置 */
	public void setXaxisposition(String xaxisposition){
		this.xaxisposition=xaxisposition;
	}
	/** 取得：y轴位置 */
	public String getYaxisposition(){
		return yaxisposition;
	}
	/** 设置：y轴位置 */
	public void setYaxisposition(String yaxisposition){
		this.yaxisposition=yaxisposition;
	}
	/** 取得：标记大小 */
	public Integer getTicksize(){
		return ticksize;
	}
	/** 设置：标记大小 */
	public void setTicksize(Integer ticksize){
		this.ticksize=ticksize;
	}
	/** 设置：标记大小 */
	public void setTicksize(String ticksize){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ticksize)){
			this.ticksize=new Integer(ticksize);
		}
	}
	/** 取得：标记内距 */
	public Integer getTickpadding(){
		return tickpadding;
	}
	/** 设置：标记内距 */
	public void setTickpadding(Integer tickpadding){
		this.tickpadding=tickpadding;
	}
	/** 设置：标记内距 */
	public void setTickpadding(String tickpadding){
		if(!fd.ng.core.utils.StringUtil.isEmpty(tickpadding)){
			this.tickpadding=new Integer(tickpadding);
		}
	}
	/** 取得：图宽 */
	public Integer getGraphwidth(){
		return graphwidth;
	}
	/** 设置：图宽 */
	public void setGraphwidth(Integer graphwidth){
		this.graphwidth=graphwidth;
	}
	/** 设置：图宽 */
	public void setGraphwidth(String graphwidth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(graphwidth)){
			this.graphwidth=new Integer(graphwidth);
		}
	}
	/** 取得：图高 */
	public Integer getGraphheight(){
		return graphheight;
	}
	/** 设置：图高 */
	public void setGraphheight(Integer graphheight){
		this.graphheight=graphheight;
	}
	/** 设置：图高 */
	public void setGraphheight(String graphheight){
		if(!fd.ng.core.utils.StringUtil.isEmpty(graphheight)){
			this.graphheight=new Integer(graphheight);
		}
	}
	/** 取得：正数颜色 */
	public String getPositivecolor(){
		return positivecolor;
	}
	/** 设置：正数颜色 */
	public void setPositivecolor(String positivecolor){
		this.positivecolor=positivecolor;
	}
	/** 取得：负数颜色 */
	public String getNegativecolor(){
		return negativecolor;
	}
	/** 设置：负数颜色 */
	public void setNegativecolor(String negativecolor){
		this.negativecolor=negativecolor;
	}
	/** 取得：主标题文本 */
	public String getTitletext(){
		return titletext;
	}
	/** 设置：主标题文本 */
	public void setTitletext(String titletext){
		this.titletext=titletext;
	}
	/** 取得：主标题文本超链接 */
	public String getTitlelink(){
		return titlelink;
	}
	/** 设置：主标题文本超链接 */
	public void setTitlelink(String titlelink){
		this.titlelink=titlelink;
	}
	/** 取得：标题字体颜色 */
	public String getTitilefontcolor(){
		return titilefontcolor;
	}
	/** 设置：标题字体颜色 */
	public void setTitilefontcolor(String titilefontcolor){
		this.titilefontcolor=titilefontcolor;
	}
	/** 取得：标题字体风格 */
	public String getTitlefontstyle(){
		return titlefontstyle;
	}
	/** 设置：标题字体风格 */
	public void setTitlefontstyle(String titlefontstyle){
		this.titlefontstyle=titlefontstyle;
	}
	/** 取得：标题文字大小 */
	public String getTitlefontsize(){
		return titlefontsize;
	}
	/** 设置：标题文字大小 */
	public void setTitlefontsize(String titlefontsize){
		this.titlefontsize=titlefontsize;
	}
	/** 取得：标题文本水平对齐方式 */
	public String getTitletextalign(){
		return titletextalign;
	}
	/** 设置：标题文本水平对齐方式 */
	public void setTitletextalign(String titletextalign){
		this.titletextalign=titletextalign;
	}
	/** 取得：标题文本垂直对齐方式 */
	public String getTitletextbaseline(){
		return titletextbaseline;
	}
	/** 设置：标题文本垂直对齐方式 */
	public void setTitletextbaseline(String titletextbaseline){
		this.titletextbaseline=titletextbaseline;
	}
	/** 取得：副标题文本 */
	public String getTitlesubtext(){
		return titlesubtext;
	}
	/** 设置：副标题文本 */
	public void setTitlesubtext(String titlesubtext){
		this.titlesubtext=titlesubtext;
	}
	/** 取得：背景色 */
	public String getBackgroundcolor(){
		return backgroundcolor;
	}
	/** 设置：背景色 */
	public void setBackgroundcolor(String backgroundcolor){
		this.backgroundcolor=backgroundcolor;
	}
	/** 取得：说明所在x轴位置 */
	public String getX(){
		return x;
	}
	/** 设置：说明所在x轴位置 */
	public void setX(String x){
		this.x=x;
	}
	/** 取得：是否显示说明 */
	public String getLegendshow(){
		return legendshow;
	}
	/** 设置：是否显示说明 */
	public void setLegendshow(String legendshow){
		this.legendshow=legendshow;
	}
	/** 取得：最小扇区角度 */
	public Integer getMinangle(){
		return minangle;
	}
	/** 设置：最小扇区角度 */
	public void setMinangle(Integer minangle){
		this.minangle=minangle;
	}
	/** 设置：最小扇区角度 */
	public void setMinangle(String minangle){
		if(!fd.ng.core.utils.StringUtil.isEmpty(minangle)){
			this.minangle=new Integer(minangle);
		}
	}
	/** 取得：图表字体颜色 */
	public String getFontcolor(){
		return fontcolor;
	}
	/** 设置：图表字体颜色 */
	public void setFontcolor(String fontcolor){
		this.fontcolor=fontcolor;
	}
	/** 取得：图表字体风格 */
	public String getFontstyle(){
		return fontstyle;
	}
	/** 设置：图表字体风格 */
	public void setFontstyle(String fontstyle){
		this.fontstyle=fontstyle;
	}
	/** 取得：标题所在x轴对齐方式 */
	public String getTitlex(){
		return titlex;
	}
	/** 设置：标题所在x轴对齐方式 */
	public void setTitlex(String titlex){
		this.titlex=titlex;
	}
	/** 取得：圆半径 */
	public String getRadius(){
		return radius;
	}
	/** 设置：圆半径 */
	public void setRadius(String radius){
		this.radius=radius;
	}
	/** 取得：x坐标轴名称 */
	public String getXaxisname(){
		return xaxisname;
	}
	/** 设置：x坐标轴名称 */
	public void setXaxisname(String xaxisname){
		this.xaxisname=xaxisname;
	}
	/** 取得：x坐标轴名称显示位置 */
	public String getXaxisnamelocation(){
		return xaxisnamelocation;
	}
	/** 设置：x坐标轴名称显示位置 */
	public void setXaxisnamelocation(String xaxisnamelocation){
		this.xaxisnamelocation=xaxisnamelocation;
	}
	/** 取得：x轴坐标名称文字大小 */
	public Integer getXaxisfontsize(){
		return xaxisfontsize;
	}
	/** 设置：x轴坐标名称文字大小 */
	public void setXaxisfontsize(Integer xaxisfontsize){
		this.xaxisfontsize=xaxisfontsize;
	}
	/** 设置：x轴坐标名称文字大小 */
	public void setXaxisfontsize(String xaxisfontsize){
		if(!fd.ng.core.utils.StringUtil.isEmpty(xaxisfontsize)){
			this.xaxisfontsize=new Integer(xaxisfontsize);
		}
	}
	/** 取得：x轴坐标名称与轴线之间的距离 */
	public Integer getXaxisnamegap(){
		return xaxisnamegap;
	}
	/** 设置：x轴坐标名称与轴线之间的距离 */
	public void setXaxisnamegap(Integer xaxisnamegap){
		this.xaxisnamegap=xaxisnamegap;
	}
	/** 设置：x轴坐标名称与轴线之间的距离 */
	public void setXaxisnamegap(String xaxisnamegap){
		if(!fd.ng.core.utils.StringUtil.isEmpty(xaxisnamegap)){
			this.xaxisnamegap=new Integer(xaxisnamegap);
		}
	}
	/** 取得：x轴是否为反向坐标轴 */
	public String getXaxisinverse(){
		return xaxisinverse;
	}
	/** 设置：x轴是否为反向坐标轴 */
	public void setXaxisinverse(String xaxisinverse){
		this.xaxisinverse=xaxisinverse;
	}
	/** 取得：y坐标轴名称 */
	public String getYaxisname(){
		return yaxisname;
	}
	/** 设置：y坐标轴名称 */
	public void setYaxisname(String yaxisname){
		this.yaxisname=yaxisname;
	}
	/** 取得：y坐标轴名称显示位置 */
	public String getYaxisnamelocation(){
		return yaxisnamelocation;
	}
	/** 设置：y坐标轴名称显示位置 */
	public void setYaxisnamelocation(String yaxisnamelocation){
		this.yaxisnamelocation=yaxisnamelocation;
	}
	/** 取得：y轴坐标名称文字大小 */
	public Integer getYaxisfontsize(){
		return yaxisfontsize;
	}
	/** 设置：y轴坐标名称文字大小 */
	public void setYaxisfontsize(Integer yaxisfontsize){
		this.yaxisfontsize=yaxisfontsize;
	}
	/** 设置：y轴坐标名称文字大小 */
	public void setYaxisfontsize(String yaxisfontsize){
		if(!fd.ng.core.utils.StringUtil.isEmpty(yaxisfontsize)){
			this.yaxisfontsize=new Integer(yaxisfontsize);
		}
	}
	/** 取得：y轴坐标名称与轴线之间的距离 */
	public Integer getYaxisnamegap(){
		return yaxisnamegap;
	}
	/** 设置：y轴坐标名称与轴线之间的距离 */
	public void setYaxisnamegap(Integer yaxisnamegap){
		this.yaxisnamegap=yaxisnamegap;
	}
	/** 设置：y轴坐标名称与轴线之间的距离 */
	public void setYaxisnamegap(String yaxisnamegap){
		if(!fd.ng.core.utils.StringUtil.isEmpty(yaxisnamegap)){
			this.yaxisnamegap=new Integer(yaxisnamegap);
		}
	}
	/** 取得：y轴是否为反向坐标轴 */
	public String getYaxisinverse(){
		return yaxisinverse;
	}
	/** 设置：y轴是否为反向坐标轴 */
	public void setYaxisinverse(String yaxisinverse){
		this.yaxisinverse=yaxisinverse;
	}
	/** 取得：标题离上侧距离 */
	public String getTitletop(){
		return titletop;
	}
	/** 设置：标题离上侧距离 */
	public void setTitletop(String titletop){
		this.titletop=titletop;
	}
	/** 取得：柱高 */
	public Integer getBarheight(){
		return barheight;
	}
	/** 设置：柱高 */
	public void setBarheight(Integer barheight){
		this.barheight=barheight;
	}
	/** 设置：柱高 */
	public void setBarheight(String barheight){
		if(!fd.ng.core.utils.StringUtil.isEmpty(barheight)){
			this.barheight=new Integer(barheight);
		}
	}
	/** 取得：每组柱条间距 */
	public Integer getGapbetweengroups(){
		return gapbetweengroups;
	}
	/** 设置：每组柱条间距 */
	public void setGapbetweengroups(Integer gapbetweengroups){
		this.gapbetweengroups=gapbetweengroups;
	}
	/** 设置：每组柱条间距 */
	public void setGapbetweengroups(String gapbetweengroups){
		if(!fd.ng.core.utils.StringUtil.isEmpty(gapbetweengroups)){
			this.gapbetweengroups=new Integer(gapbetweengroups);
		}
	}
	/** 取得：标签空间 */
	public Integer getSpaceforlabels(){
		return spaceforlabels;
	}
	/** 设置：标签空间 */
	public void setSpaceforlabels(Integer spaceforlabels){
		this.spaceforlabels=spaceforlabels;
	}
	/** 设置：标签空间 */
	public void setSpaceforlabels(String spaceforlabels){
		if(!fd.ng.core.utils.StringUtil.isEmpty(spaceforlabels)){
			this.spaceforlabels=new Integer(spaceforlabels);
		}
	}
	/** 取得：图例空间 */
	public Integer getSpaceforlegend(){
		return spaceforlegend;
	}
	/** 设置：图例空间 */
	public void setSpaceforlegend(Integer spaceforlegend){
		this.spaceforlegend=spaceforlegend;
	}
	/** 设置：图例空间 */
	public void setSpaceforlegend(String spaceforlegend){
		if(!fd.ng.core.utils.StringUtil.isEmpty(spaceforlegend)){
			this.spaceforlegend=new Integer(spaceforlegend);
		}
	}
	/** 取得：图例大小 */
	public Integer getLegendrectsize(){
		return legendrectsize;
	}
	/** 设置：图例大小 */
	public void setLegendrectsize(Integer legendrectsize){
		this.legendrectsize=legendrectsize;
	}
	/** 设置：图例大小 */
	public void setLegendrectsize(String legendrectsize){
		if(!fd.ng.core.utils.StringUtil.isEmpty(legendrectsize)){
			this.legendrectsize=new Integer(legendrectsize);
		}
	}
	/** 取得：图例间距 */
	public Integer getLegendspacing(){
		return legendspacing;
	}
	/** 设置：图例间距 */
	public void setLegendspacing(Integer legendspacing){
		this.legendspacing=legendspacing;
	}
	/** 设置：图例间距 */
	public void setLegendspacing(String legendspacing){
		if(!fd.ng.core.utils.StringUtil.isEmpty(legendspacing)){
			this.legendspacing=new Integer(legendspacing);
		}
	}
	/** 取得：是否显示提示框 */
	public String getToolboxshow(){
		return toolboxshow;
	}
	/** 设置：是否显示提示框 */
	public void setToolboxshow(String toolboxshow){
		this.toolboxshow=toolboxshow;
	}
	/** 取得：图表左边的距离 */
	public Integer getSeriesleft(){
		return seriesleft;
	}
	/** 设置：图表左边的距离 */
	public void setSeriesleft(Integer seriesleft){
		this.seriesleft=seriesleft;
	}
	/** 设置：图表左边的距离 */
	public void setSeriesleft(String seriesleft){
		if(!fd.ng.core.utils.StringUtil.isEmpty(seriesleft)){
			this.seriesleft=new Integer(seriesleft);
		}
	}
	/** 取得：图表顶部的距离 */
	public Integer getSeriestop(){
		return seriestop;
	}
	/** 设置：图表顶部的距离 */
	public void setSeriestop(Integer seriestop){
		this.seriestop=seriestop;
	}
	/** 设置：图表顶部的距离 */
	public void setSeriestop(String seriestop){
		if(!fd.ng.core.utils.StringUtil.isEmpty(seriestop)){
			this.seriestop=new Integer(seriestop);
		}
	}
	/** 取得：图表底部的距离 */
	public Integer getSeriesbottom(){
		return seriesbottom;
	}
	/** 设置：图表底部的距离 */
	public void setSeriesbottom(Integer seriesbottom){
		this.seriesbottom=seriesbottom;
	}
	/** 设置：图表底部的距离 */
	public void setSeriesbottom(String seriesbottom){
		if(!fd.ng.core.utils.StringUtil.isEmpty(seriesbottom)){
			this.seriesbottom=new Integer(seriesbottom);
		}
	}
	/** 取得：漏斗图的排序 */
	public String getSeriessort(){
		return seriessort;
	}
	/** 设置：漏斗图的排序 */
	public void setSeriessort(String seriessort){
		this.seriessort=seriessort;
	}
	/** 取得：图表的边框颜色 */
	public String getSeriesbordercolor(){
		return seriesbordercolor;
	}
	/** 设置：图表的边框颜色 */
	public void setSeriesbordercolor(String seriesbordercolor){
		this.seriesbordercolor=seriesbordercolor;
	}
	/** 取得：图表的边框宽度 */
	public Integer getSeriesborderwidth(){
		return seriesborderwidth;
	}
	/** 设置：图表的边框宽度 */
	public void setSeriesborderwidth(Integer seriesborderwidth){
		this.seriesborderwidth=seriesborderwidth;
	}
	/** 设置：图表的边框宽度 */
	public void setSeriesborderwidth(String seriesborderwidth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(seriesborderwidth)){
			this.seriesborderwidth=new Integer(seriesborderwidth);
		}
	}
	/** 取得：线宽度 */
	public Integer getFlinewidth(){
		return flinewidth;
	}
	/** 设置：线宽度 */
	public void setFlinewidth(Integer flinewidth){
		this.flinewidth=flinewidth;
	}
	/** 设置：线宽度 */
	public void setFlinewidth(String flinewidth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(flinewidth)){
			this.flinewidth=new Integer(flinewidth);
		}
	}
	/** 取得：线颜色 */
	public String getFlinecolor(){
		return flinecolor;
	}
	/** 设置：线颜色 */
	public void setFlinecolor(String flinecolor){
		this.flinecolor=flinecolor;
	}
	/** 取得：展开节点颜色 */
	public String getExpendcolor(){
		return expendcolor;
	}
	/** 设置：展开节点颜色 */
	public void setExpendcolor(String expendcolor){
		this.expendcolor=expendcolor;
	}
	/** 取得：折叠节点颜色 */
	public String getCollapsecolor(){
		return collapsecolor;
	}
	/** 设置：折叠节点颜色 */
	public void setCollapsecolor(String collapsecolor){
		this.collapsecolor=collapsecolor;
	}
	/** 取得：节点形状 */
	public String getMyshape(){
		return myshape;
	}
	/** 设置：节点形状 */
	public void setMyshape(String myshape){
		this.myshape=myshape;
	}
	/** 取得：节点宽度 */
	public Integer getNodewidth(){
		return nodewidth;
	}
	/** 设置：节点宽度 */
	public void setNodewidth(Integer nodewidth){
		this.nodewidth=nodewidth;
	}
	/** 设置：节点宽度 */
	public void setNodewidth(String nodewidth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(nodewidth)){
			this.nodewidth=new Integer(nodewidth);
		}
	}
	/** 取得：节点间距 */
	public Integer getNodepadding(){
		return nodepadding;
	}
	/** 设置：节点间距 */
	public void setNodepadding(Integer nodepadding){
		this.nodepadding=nodepadding;
	}
	/** 设置：节点间距 */
	public void setNodepadding(String nodepadding){
		if(!fd.ng.core.utils.StringUtil.isEmpty(nodepadding)){
			this.nodepadding=new Integer(nodepadding);
		}
	}
	/** 取得：主标题文本的距离左边的距离 */
	public Integer getTitleleft(){
		return titleleft;
	}
	/** 设置：主标题文本的距离左边的距离 */
	public void setTitleleft(Integer titleleft){
		this.titleleft=titleleft;
	}
	/** 设置：主标题文本的距离左边的距离 */
	public void setTitleleft(String titleleft){
		if(!fd.ng.core.utils.StringUtil.isEmpty(titleleft)){
			this.titleleft=new Integer(titleleft);
		}
	}
	/** 取得：是否合并重复 */
	public String getIsmerge(){
		return ismerge;
	}
	/** 设置：是否合并重复 */
	public void setIsmerge(String ismerge){
		this.ismerge=ismerge;
	}
	/** 取得：柱条颜色 */
	public String getBarcolor(){
		return barcolor;
	}
	/** 设置：柱条颜色 */
	public void setBarcolor(String barcolor){
		this.barcolor=barcolor;
	}
	/** 取得：报表类型编号 */
	public Long getReport_type_id(){
		return report_type_id;
	}
	/** 设置：报表类型编号 */
	public void setReport_type_id(Long report_type_id){
		this.report_type_id=report_type_id;
	}
	/** 设置：报表类型编号 */
	public void setReport_type_id(String report_type_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(report_type_id)){
			this.report_type_id=new Long(report_type_id);
		}
	}
	/** 取得：用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
	/** 取得：雷达绘制类型 */
	public String getShape(){
		return shape;
	}
	/** 设置：雷达绘制类型 */
	public void setShape(String shape){
		this.shape=shape;
	}
	/** 取得：标签颜色 */
	public String getAxislabelcolor(){
		return axislabelcolor;
	}
	/** 设置：标签颜色 */
	public void setAxislabelcolor(String axislabelcolor){
		this.axislabelcolor=axislabelcolor;
	}
	/** 取得：是否显示标签 */
	public String getIsaxislabel(){
		return isaxislabel;
	}
	/** 设置：是否显示标签 */
	public void setIsaxislabel(String isaxislabel){
		this.isaxislabel=isaxislabel;
	}
	/** 取得：0处径向渐变颜色 */
	public String getRadialgradientcolor0(){
		return radialgradientcolor0;
	}
	/** 设置：0处径向渐变颜色 */
	public void setRadialgradientcolor0(String radialgradientcolor0){
		this.radialgradientcolor0=radialgradientcolor0;
	}
	/** 取得：100处径向渐变颜色 */
	public String getRadialgradientcolor1(){
		return radialgradientcolor1;
	}
	/** 设置：100处径向渐变颜色 */
	public void setRadialgradientcolor1(String radialgradientcolor1){
		this.radialgradientcolor1=radialgradientcolor1;
	}
	/** 取得：是否显示数据值 */
	public String getIsdatavalue(){
		return isdatavalue;
	}
	/** 设置：是否显示数据值 */
	public void setIsdatavalue(String isdatavalue){
		this.isdatavalue=isdatavalue;
	}
}
