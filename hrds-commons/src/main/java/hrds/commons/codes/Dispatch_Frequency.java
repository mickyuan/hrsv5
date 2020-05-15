package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：ETL调度频率  */
public enum Dispatch_Frequency {
	/**天(D)<DAILY>  */
	DAILY("D","天(D)","22","ETL调度频率"),
	/**月(M)<MONTHLY>  */
	MONTHLY("M","月(M)","22","ETL调度频率"),
	/**周(W)<WEEKLY>  */
	WEEKLY("W","周(W)","22","ETL调度频率"),
	/**旬(X)<TENDAYS>  */
	TENDAYS("X","旬(X)","22","ETL调度频率"),
	/**年(Y)<YEARLY>  */
	YEARLY("Y","年(Y)","22","ETL调度频率"),
	/**频率(F)<PinLv>  */
	PinLv("F","频率(F)","22","ETL调度频率");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	Dispatch_Frequency(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "Dispatch_Frequency";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (Dispatch_Frequency typeCode : Dispatch_Frequency.values()) {
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
	public static Dispatch_Frequency ofEnumByCode(String code) {
		for (Dispatch_Frequency typeCode : Dispatch_Frequency.values()) {
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
		return Dispatch_Frequency.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return Dispatch_Frequency.values()[0].getCatCode();
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
