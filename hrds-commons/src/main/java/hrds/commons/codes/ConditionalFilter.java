package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：报表条件过滤方式  */
public enum ConditionalFilter {
	/**文本<WenBen>  */
	WenBen("1","文本","93","报表条件过滤方式"),
	/**单选下拉<DanXuanXiaLa>  */
	DanXuanXiaLa("2","单选下拉","93","报表条件过滤方式"),
	/**多选下拉<DuoXuanXiaLa>  */
	DuoXuanXiaLa("3","多选下拉","93","报表条件过滤方式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ConditionalFilter(String code,String value,String catCode,String catValue){
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
		for (ConditionalFilter typeCode : ConditionalFilter.values()) {
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
	public static ConditionalFilter getCodeObj(String code) {
		for (ConditionalFilter typeCode : ConditionalFilter.values()) {
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
		return ConditionalFilter.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ConditionalFilter.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
