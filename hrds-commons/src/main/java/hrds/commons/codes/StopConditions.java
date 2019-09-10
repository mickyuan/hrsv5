package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：停止抓取条件  */
public enum StopConditions {
	/**按网页数量停止抓取<AnShuLiang>  */
	AnShuLiang("1","按网页数量停止抓取","20","停止抓取条件"),
	/**按下载量停止抓取<AnXiaZaiLiang>  */
	AnXiaZaiLiang("2","按下载量停止抓取","20","停止抓取条件"),
	/**按时间停止抓取<AnShiJian>  */
	AnShiJian("3","按时间停止抓取","20","停止抓取条件");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	StopConditions(String code,String value,String catCode,String catValue){
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
		for (StopConditions typeCode : StopConditions.values()) {
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
	public static StopConditions getCodeObj(String code) {
		for (StopConditions typeCode : StopConditions.values()) {
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
		return StopConditions.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return StopConditions.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
