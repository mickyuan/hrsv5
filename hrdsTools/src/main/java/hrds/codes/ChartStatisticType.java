package hrds.codes;
/**Created by automatic  */
/**代码类型名：机器学习图表统计量类型  */
public enum ChartStatisticType {
	/**均值<JunZhi>  */
	JunZhi("1","均值","106"),
	/**中位数<ZhongWeiShu>  */
	ZhongWeiShu("2","中位数","106"),
	/**众数<ZhongShu>  */
	ZhongShu("3","众数","106"),
	/**计数<JiShu>  */
	JiShu("4","计数","106"),
	/**标准差<BiaoZhunCha>  */
	BiaoZhunCha("5","标准差","106"),
	/**最小值<ZuiXiaoZhi>  */
	ZuiXiaoZhi("6","最小值","106"),
	/**最大值<ZuiDaZhi>  */
	ZuiDaZhi("7","最大值","106"),
	/**方差<FangCha>  */
	FangCha("8","方差","106"),
	/**不处理<BuChuLi>  */
	BuChuLi("9","不处理","106");

	private final String code;
	private final String value;
	private final String catCode;

	ChartStatisticType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
