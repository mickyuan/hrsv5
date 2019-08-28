package hrds.codes;
/**Created by automatic  */
/**代码类型名：StreamingPro输出模式  */
public enum SdmSpOutputMode {
	/**Append<APPEND>  */
	APPEND("1","Append","154"),
	/**Overwrite<OVERWRITE>  */
	OVERWRITE("2","Overwrite","154");

	private final String code;
	private final String value;
	private final String catCode;

	SdmSpOutputMode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
