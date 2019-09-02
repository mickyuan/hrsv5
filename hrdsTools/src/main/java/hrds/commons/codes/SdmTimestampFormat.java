package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：流数据管理druid时间戳格式  */
public enum SdmTimestampFormat {
	/**iso<ISO>  */
	ISO("1","iso","134","流数据管理druid时间戳格式"),
	/**millis<Millis>  */
	Millis("2","millis","134","流数据管理druid时间戳格式"),
	/**posix<Posix>  */
	Posix("3","posix","134","流数据管理druid时间戳格式"),
	/**jodaTime<JodaTime>  */
	JodaTime("4","jodaTime","134","流数据管理druid时间戳格式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	SdmTimestampFormat(String code,String value,String catCode,String catValue){
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
		for (SdmTimestampFormat typeCode : SdmTimestampFormat.values()) {
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
	public static SdmTimestampFormat getCodeObj(String code) {
		for (SdmTimestampFormat typeCode : SdmTimestampFormat.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据"+code+"没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return SdmTimestampFormat.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return SdmTimestampFormat.values()[0].getCatCode();
	}
}
