package hrds.codes;
/**Created by automatic  */
/**代码类型名：StreamingPro流式数据的版本  */
public enum SdmSpStreamVer {
	/**kafka8<KAFKA8>  */
	KAFKA8("1","kafka8","153"),
	/**kafka9<KAFKA9>  */
	KAFKA9("2","kafka9","153"),
	/**kafka<KAFKA>  */
	KAFKA("3","kafka","153");

	private final String code;
	private final String value;
	private final String catCode;

	SdmSpStreamVer(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
