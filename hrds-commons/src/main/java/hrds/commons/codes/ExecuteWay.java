package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：启动方式  */
public enum ExecuteWay {
	/**按时启动<AnShiQiDong>  */
	AnShiQiDong("1","按时启动","37","启动方式"),
	/**命令触发<MingLingChuFa>  */
	MingLingChuFa("2","命令触发","37","启动方式"),
	/**信号文件触发<QianZhiTiaoJian>  */
	QianZhiTiaoJian("3","信号文件触发","37","启动方式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ExecuteWay(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "ExecuteWay";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (ExecuteWay typeCode : ExecuteWay.values()) {
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
	public static ExecuteWay ofEnumByCode(String code) {
		for (ExecuteWay typeCode : ExecuteWay.values()) {
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
		return ExecuteWay.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return ExecuteWay.values()[0].getCatCode();
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
