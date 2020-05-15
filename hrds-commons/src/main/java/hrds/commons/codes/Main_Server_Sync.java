package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：ETL主服务器同步  */
public enum Main_Server_Sync {
	/**锁定<LOCK>  */
	LOCK("L","锁定","27","ETL主服务器同步"),
	/**不同步<NO>  */
	NO("N","不同步","27","ETL主服务器同步"),
	/**同步<YES>  */
	YES("Y","同步","27","ETL主服务器同步"),
	/**备份中<BACKUP>  */
	BACKUP("B","备份中","27","ETL主服务器同步");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	Main_Server_Sync(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "Main_Server_Sync";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (Main_Server_Sync typeCode : Main_Server_Sync.values()) {
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
	public static Main_Server_Sync ofEnumByCode(String code) {
		for (Main_Server_Sync typeCode : Main_Server_Sync.values()) {
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
		return Main_Server_Sync.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return Main_Server_Sync.values()[0].getCatCode();
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
