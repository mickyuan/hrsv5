package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：报表类型  */
public enum ReportType {
	/**折叠树图<Flare_flare>  */
	Flare_flare("001","折叠树图","84","报表类型"),
	/**关系图<Link_link>  */
	Link_link("002","关系图","84","报表类型"),
	/**序列圆图<Sequences_sequencees>  */
	Sequences_sequencees("003","序列圆图","84","报表类型"),
	/**桑基图<Sankey_sankey>  */
	Sankey_sankey("004","桑基图","84","报表类型"),
	/**Hierarchical<Fourth_hierarchical>  */
	Fourth_hierarchical("005","Hierarchical","84","报表类型"),
	/**气泡图<LiquidFillGauge_liquidFillGauge>  */
	LiquidFillGauge_liquidFillGauge("006","气泡图","84","报表类型"),
	/**气泡菜单图<BubbleMenu_bubbleMenu>  */
	BubbleMenu_bubbleMenu("007","气泡菜单图","84","报表类型"),
	/**趋势图<Trend_trendChart>  */
	Trend_trendChart("008","趋势图","84","报表类型"),
	/**柱形图<Bar_barChart>  */
	Bar_barChart("009","柱形图","84","报表类型"),
	/**柱形价值图<Bar_barChartValue>  */
	Bar_barChartValue("010","柱形价值图","84","报表类型"),
	/**柱形频次图<Bar_barChartTime>  */
	Bar_barChartTime("011","柱形频次图","84","报表类型"),
	/**动态散点图<Ball_ball>  */
	Ball_ball("012","动态散点图","84","报表类型"),
	/**饼图<Pie_echartPie>  */
	Pie_echartPie("013","饼图","84","报表类型"),
	/**堆叠柱状图<Bar_echartStackBar>  */
	Bar_echartStackBar("014","堆叠柱状图","84","报表类型"),
	/**散点图<Ball_echartScatter>  */
	Ball_echartScatter("015","散点图","84","报表类型"),
	/**雷达图<Radar_echartRadar>  */
	Radar_echartRadar("016","雷达图","84","报表类型"),
	/**k线图<Line_echartLine>  */
	Line_echartLine("017","k线图","84","报表类型"),
	/**漏斗图<Funnel_echartFunnel>  */
	Funnel_echartFunnel("018","漏斗图","84","报表类型"),
	/**平行坐标图<Parallel_echartParallel>  */
	Parallel_echartParallel("019","平行坐标图","84","报表类型"),
	/**柱状图<Bar_echartBar>  */
	Bar_echartBar("020","柱状图","84","报表类型"),
	/**Miserables<Link_miserables>  */
	Link_miserables("021","Miserables","84","报表类型"),
	/**直方图<Bar_Ihistogram>  */
	Bar_Ihistogram("022","直方图","84","报表类型"),
	/**气泡散点图<Ball_IscatterGram>  */
	Ball_IscatterGram("023","气泡散点图","84","报表类型"),
	/**正负条形图<Bar_echartBarChart>  */
	Bar_echartBarChart("024","正负条形图","84","报表类型"),
	/**箱线图<Boxplot_IBoxplot>  */
	Boxplot_IBoxplot("025","箱线图","84","报表类型"),
	/**折线图<Line_ILinez>  */
	Line_ILinez("026","折线图","84","报表类型"),
	/**统计柱状图<Bar_IbarGraph>  */
	Bar_IbarGraph("027","统计柱状图","84","报表类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ReportType(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String getValue(String code) {
		for (ReportType typeCode : ReportType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		throw new AppSystemException("根据"+code+"没有找到对应的代码项");
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static ReportType getCodeObj(String code) {
		for (ReportType typeCode : ReportType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new AppSystemException("根据"+code+"没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return ReportType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ReportType.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
