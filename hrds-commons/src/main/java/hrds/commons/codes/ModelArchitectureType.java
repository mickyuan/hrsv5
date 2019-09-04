package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：模型体系结构类型  */
public enum ModelArchitectureType {
	/**自动体系结构选择<ZiDongTiXiJieGouXuanZe>  */
	ZiDongTiXiJieGouXuanZe("1","自动体系结构选择","64","模型体系结构类型"),
	/**自定义体系结构<ZiDingYiTiXiJieGou>  */
	ZiDingYiTiXiJieGou("2","自定义体系结构","64","模型体系结构类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ModelArchitectureType(String code,String value,String catCode,String catValue){
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
		for (ModelArchitectureType typeCode : ModelArchitectureType.values()) {
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
	public static ModelArchitectureType getCodeObj(String code) {
		for (ModelArchitectureType typeCode : ModelArchitectureType.values()) {
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
		return ModelArchitectureType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ModelArchitectureType.values()[0].getCatCode();
	}
}
