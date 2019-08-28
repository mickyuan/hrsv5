package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理druid时间戳格式  */
public enum SdmTimestampFormat {
	/**iso<ISO>  */
	ISO("1","iso","134"),
	/**millis<Millis>  */
	Millis("2","millis","134"),
	/**posix<Posix>  */
	Posix("3","posix","134"),
	/**jodaTime<JodaTime>  */
	JodaTime("4","jodaTime","134");

	private final String code;
	private final String value;
	private final String catCode;

	SdmTimestampFormat(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
