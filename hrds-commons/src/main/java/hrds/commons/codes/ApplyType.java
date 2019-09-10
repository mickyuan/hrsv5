package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：数据申请类型  */
public enum ApplyType {
	/**查看<ChaKan>  */
	ChaKan("1","查看","18","数据申请类型"),
	/**下载<XiaZai>  */
	XiaZai("2","下载","18","数据申请类型"),
	/**发布<FaBu>  */
	FaBu("3","发布","18","数据申请类型"),
	/**重命名<ChongMingMing>  */
	ChongMingMing("4","重命名","18","数据申请类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ApplyType(String code,String value,String catCode,String catValue){
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
		for (ApplyType typeCode : ApplyType.values()) {
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
	public static ApplyType getCodeObj(String code) {
		for (ApplyType typeCode : ApplyType.values()) {
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
		return ApplyType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ApplyType.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
