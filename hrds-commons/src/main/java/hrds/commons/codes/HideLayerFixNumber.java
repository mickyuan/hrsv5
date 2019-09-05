package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：隐藏固定数  */
public enum HideLayerFixNumber {
	/**一层<YiCeng>  */
	YiCeng("1","一层","78","隐藏固定数"),
	/**二层<ErCeng>  */
	ErCeng("2","二层","78","隐藏固定数");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	HideLayerFixNumber(String code,String value,String catCode,String catValue){
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
		for (HideLayerFixNumber typeCode : HideLayerFixNumber.values()) {
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
	public static HideLayerFixNumber getCodeObj(String code) {
		for (HideLayerFixNumber typeCode : HideLayerFixNumber.values()) {
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
		return HideLayerFixNumber.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return HideLayerFixNumber.values()[0].getCatCode();
	}
}
