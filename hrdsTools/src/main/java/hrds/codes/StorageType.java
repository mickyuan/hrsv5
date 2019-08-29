package hrds.codes;
/**Created by automatic  */
/**代码类型名：储存方式  */
public enum StorageType {
	/**增量<ZengLiang>  */
	ZengLiang("1","增量","25","储存方式"),
	/**追加<ZhuiJia>  */
	ZhuiJia("2","追加","25","储存方式"),
	/**替换<TiHuan>  */
	TiHuan("3","替换","25","储存方式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	StorageType(String code,String value,String catCode,String catValue){
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
		for (StorageType typeCode : StorageType.values()) {
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
	public static StorageType getCodeObj(String code) {
		for (StorageType typeCode : StorageType.values()) {
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
		return StorageType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return StorageType.values()[0].getCatCode();
	}
}
