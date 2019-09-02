package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：数据质量校验结果  */
public enum DqcVerifyResult {
	/**检查通过<ZhengChang>  */
	ZhengChang("0","检查通过","138","数据质量校验结果"),
	/**数据异常<YiChang>  */
	YiChang("1","数据异常","138","数据质量校验结果"),
	/**执行失败<ZhiXingShiBai>  */
	ZhiXingShiBai("2","执行失败","138","数据质量校验结果");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DqcVerifyResult(String code,String value,String catCode,String catValue){
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
		for (DqcVerifyResult typeCode : DqcVerifyResult.values()) {
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
	public static DqcVerifyResult getCodeObj(String code) {
		for (DqcVerifyResult typeCode : DqcVerifyResult.values()) {
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
		return DqcVerifyResult.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return DqcVerifyResult.values()[0].getCatCode();
	}
}
