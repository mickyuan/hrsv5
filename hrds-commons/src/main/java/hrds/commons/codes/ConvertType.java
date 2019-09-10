package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：转换类型  */
public enum ConvertType {
	/**不需要转换<BuXuYaoZhuanHuan>  */
	BuXuYaoZhuanHuan("0","不需要转换","94","转换类型"),
	/**转换为日期<ZhuanHuanWeiRiQi>  */
	ZhuanHuanWeiRiQi("1","转换为日期","94","转换类型"),
	/**转换为时间<ZhuanHuanWeiShiJian>  */
	ZhuanHuanWeiShiJian("2","转换为时间","94","转换类型"),
	/**参数转换<CanShuZhuanHuan>  */
	CanShuZhuanHuan("3","参数转换","94","转换类型"),
	/**格式转换<GeShiZhuanHuan>  */
	GeShiZhuanHuan("4","格式转换","94","转换类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ConvertType(String code,String value,String catCode,String catValue){
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
		for (ConvertType typeCode : ConvertType.values()) {
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
	public static ConvertType getCodeObj(String code) {
		for (ConvertType typeCode : ConvertType.values()) {
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
		return ConvertType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ConvertType.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
