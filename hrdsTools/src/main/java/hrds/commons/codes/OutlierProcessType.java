package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：异常值处理处理类型  */
public enum OutlierProcessType {
	/**平均值<PingJunZhi>  */
	PingJunZhi("1","平均值","76","异常值处理处理类型"),
	/**中位值<ZhongWeiZhi>  */
	ZhongWeiZhi("2","中位值","76","异常值处理处理类型"),
	/**自定义<ZiDingYi>  */
	ZiDingYi("3","自定义","76","异常值处理处理类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	OutlierProcessType(String code,String value,String catCode,String catValue){
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
		for (OutlierProcessType typeCode : OutlierProcessType.values()) {
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
	public static OutlierProcessType getCodeObj(String code) {
		for (OutlierProcessType typeCode : OutlierProcessType.values()) {
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
		return OutlierProcessType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return OutlierProcessType.values()[0].getCatCode();
	}
}
