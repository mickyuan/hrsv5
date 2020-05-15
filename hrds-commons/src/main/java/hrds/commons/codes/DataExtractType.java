package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：数据文件源头  */
public enum DataExtractType {
	/**数据库抽取落地<ShuJuKuChouQuLuoDi>  */
	ShuJuKuChouQuLuoDi("1","数据库抽取落地","53","数据文件源头"),
	/**原数据格式<YuanShuJuGeShi>  */
	YuanShuJuGeShi("2","原数据格式","53","数据文件源头"),
	/**数据加载格式<ShuJuJiaZaiGeShi>  */
	ShuJuJiaZaiGeShi("3","数据加载格式","53","数据文件源头");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DataExtractType(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "DataExtractType";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (DataExtractType typeCode : DataExtractType.values()) {
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
	public static DataExtractType ofEnumByCode(String code) {
		for (DataExtractType typeCode : DataExtractType.values()) {
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
		return DataExtractType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return DataExtractType.values()[0].getCatCode();
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
