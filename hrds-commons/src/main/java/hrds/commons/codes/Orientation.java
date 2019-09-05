package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：上下左右  */
public enum Orientation {
	/**上<top>  */
	top("0","上","34","上下左右"),
	/**下<bottom>  */
	bottom("1","下","34","上下左右"),
	/**左<left>  */
	left("2","左","34","上下左右"),
	/**右<right>  */
	right("3","右","34","上下左右");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	Orientation(String code,String value,String catCode,String catValue){
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
		for (Orientation typeCode : Orientation.values()) {
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
	public static Orientation getCodeObj(String code) {
		for (Orientation typeCode : Orientation.values()) {
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
		return Orientation.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return Orientation.values()[0].getCatCode();
	}
}
