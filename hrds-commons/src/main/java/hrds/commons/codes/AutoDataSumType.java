package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：可视化数据汇总类型  */
public enum AutoDataSumType {
	/**求和<QiuHe>  */
	QiuHe("01","求和","116","可视化数据汇总类型"),
	/**求平均<QiuPingJun>  */
	QiuPingJun("02","求平均","116","可视化数据汇总类型"),
	/**求最大值<QiuZuiDaZhi>  */
	QiuZuiDaZhi("03","求最大值","116","可视化数据汇总类型"),
	/**求最小值<QiuZuiXiaoZhi>  */
	QiuZuiXiaoZhi("04","求最小值","116","可视化数据汇总类型"),
	/**总行数<ZongHangShu>  */
	ZongHangShu("05","总行数","116","可视化数据汇总类型"),
	/**原始数据<YuanShiShuJu>  */
	YuanShiShuJu("06","原始数据","116","可视化数据汇总类型"),
	/**查看全部<ChaKanQuanBu>  */
	ChaKanQuanBu("07","查看全部","116","可视化数据汇总类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	AutoDataSumType(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "AutoDataSumType";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (AutoDataSumType typeCode : AutoDataSumType.values()) {
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
	public static AutoDataSumType ofEnumByCode(String code) {
		for (AutoDataSumType typeCode : AutoDataSumType.values()) {
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
	public static String ofCatValue(){
		return AutoDataSumType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return AutoDataSumType.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	@Override
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
