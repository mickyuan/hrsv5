package hrds.codes;
/**Created by automatic  */
/**代码类型名：sql执行引擎  */
public enum SqlEngine {
	/**JDBC<JDBC>  */
	JDBC("1","JDBC","46","sql执行引擎"),
	/**SPARK<SPARK>  */
	SPARK("2","SPARK","46","sql执行引擎"),
	/**默认<MOREN>  */
	MOREN("3","默认","46","sql执行引擎");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	SqlEngine(String code,String value,String catCode,String catValue){
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
		for (SqlEngine typeCode : SqlEngine.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		throw new RuntimeException("根据"+code+"没有找到对应的代码项");
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static SqlEngine getCodeObj(String code) {
		for (SqlEngine typeCode : SqlEngine.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据code没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return SqlEngine.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return SqlEngine.values()[0].getCatCode();
	}
}
