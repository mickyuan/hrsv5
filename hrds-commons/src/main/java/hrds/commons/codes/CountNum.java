package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：记录总数  */
public enum CountNum {
	/**1万左右<YiWan>  */
	YiWan("10000","1万左右","43","记录总数"),
	/**10万左右<ShiWan>  */
	ShiWan("100000","10万左右","43","记录总数"),
	/**100万左右<BaiWan>  */
	BaiWan("1000000","100万左右","43","记录总数"),
	/**1000万左右<Qianwan>  */
	Qianwan("10000000","1000万左右","43","记录总数"),
	/**亿左右<Yi>  */
	Yi("100000000","亿左右","43","记录总数"),
	/**亿以上<YiYiShang>  */
	YiYiShang("100000001","亿以上","43","记录总数");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	CountNum(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "CountNum";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (CountNum typeCode : CountNum.values()) {
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
	public static CountNum ofEnumByCode(String code) {
		for (CountNum typeCode : CountNum.values()) {
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
		return CountNum.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return CountNum.values()[0].getCatCode();
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
