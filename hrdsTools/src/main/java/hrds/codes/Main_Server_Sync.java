package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL主服务器同步  */
public enum Main_Server_Sync {
	/**锁定<LOCK>  */
	LOCK("L","锁定","111","ETL主服务器同步"),
	/**不同步<NO>  */
	NO("N","不同步","111","ETL主服务器同步"),
	/**同步<YES>  */
	YES("Y","同步","111","ETL主服务器同步"),
	/**备份中<BACKUP>  */
	BACKUP("B","备份中","111","ETL主服务器同步");

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

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String getValue(String code) {
		for (Main_Server_Sync typeCode : Main_Server_Sync.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		return null;
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static Main_Server_Sync getCodeObj(String code) {
		for (Main_Server_Sync typeCode : Main_Server_Sync.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		return null;
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return Main_Server_Sync.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return Main_Server_Sync.values()[0].getCatCode();
	}
}
