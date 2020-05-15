package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：对标-数据类别  */
public enum DbmDataType {
	/**编码类<BianMaLei>  */
	BianMaLei("101","编码类","67","对标-数据类别"),
	/**标识类<BiaoShiLei>  */
	BiaoShiLei("102","标识类","67","对标-数据类别"),
	/**代码类<DaiMaLei>  */
	DaiMaLei("103","代码类","67","对标-数据类别"),
	/**金额类<JinELei>  */
	JinELei("104","金额类","67","对标-数据类别"),
	/**日期类<RiQiLei>  */
	RiQiLei("105","日期类","67","对标-数据类别"),
	/**日期时间类<RiQiShiJianLei>  */
	RiQiShiJianLei("106","日期时间类","67","对标-数据类别"),
	/**时间类<ShiJianLei>  */
	ShiJianLei("107","时间类","67","对标-数据类别"),
	/**数值类<ShuZhiLei>  */
	ShuZhiLei("108","数值类","67","对标-数据类别"),
	/**文本类<WenBenLei>  */
	WenBenLei("109","文本类","67","对标-数据类别");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	DbmDataType(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "DbmDataType";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (DbmDataType typeCode : DbmDataType.values()) {
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
	public static DbmDataType ofEnumByCode(String code) {
		for (DbmDataType typeCode : DbmDataType.values()) {
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
		return DbmDataType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return DbmDataType.values()[0].getCatCode();
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
