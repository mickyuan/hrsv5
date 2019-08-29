package hrds.codes;
/**Created by automatic  */
/**代码类型名：机器学习图表统计量类型  */
public enum ChartStatisticType {
	/**均值<JunZhi>  */
	JunZhi("1","均值","106","机器学习图表统计量类型"),
	/**中位数<ZhongWeiShu>  */
	ZhongWeiShu("2","中位数","106","机器学习图表统计量类型"),
	/**众数<ZhongShu>  */
	ZhongShu("3","众数","106","机器学习图表统计量类型"),
	/**计数<JiShu>  */
	JiShu("4","计数","106","机器学习图表统计量类型"),
	/**标准差<BiaoZhunCha>  */
	BiaoZhunCha("5","标准差","106","机器学习图表统计量类型"),
	/**最小值<ZuiXiaoZhi>  */
	ZuiXiaoZhi("6","最小值","106","机器学习图表统计量类型"),
	/**最大值<ZuiDaZhi>  */
	ZuiDaZhi("7","最大值","106","机器学习图表统计量类型"),
	/**方差<FangCha>  */
	FangCha("8","方差","106","机器学习图表统计量类型"),
	/**不处理<BuChuLi>  */
	BuChuLi("9","不处理","106","机器学习图表统计量类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ChartStatisticType(String code,String value,String catCode,String catValue){
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
		for (ChartStatisticType typeCode : ChartStatisticType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		return null;
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static ChartStatisticType getCodeObj(String code) {
		for (ChartStatisticType typeCode : ChartStatisticType.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		return null;
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return ChartStatisticType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ChartStatisticType.values()[0].getCatCode();
	}
}
