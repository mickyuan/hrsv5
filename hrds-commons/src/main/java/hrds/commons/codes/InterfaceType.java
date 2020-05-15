package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：接口类型  */
public enum InterfaceType {
	/**数据类<ShuJuLei>  */
	ShuJuLei("1","数据类","72","接口类型"),
	/**功能类<GongNengLei>  */
	GongNengLei("2","功能类","72","接口类型"),
	/**报表类<BaoBiaoLei>  */
	BaoBiaoLei("3","报表类","72","接口类型"),
	/**监控类<JianKongLei>  */
	JianKongLei("4","监控类","72","接口类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	InterfaceType(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "InterfaceType";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (InterfaceType typeCode : InterfaceType.values()) {
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
	public static InterfaceType ofEnumByCode(String code) {
		for (InterfaceType typeCode : InterfaceType.values()) {
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
		return InterfaceType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return InterfaceType.values()[0].getCatCode();
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
