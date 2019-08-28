package hrds.codes;
/**Created by automatic  */
/**代码类型名：StreamingPro文本文件格式  */
public enum SdmSpFileType {
	/**Csv<CSV>  */
	CSV("1","Csv","152"),
	/**Parquent<PARQUENT>  */
	PARQUENT("2","Parquent","152"),
	/**Json<JSON>  */
	JSON("3","Json","152");

	private final String code;
	private final String value;
	private final String catCode;

	SdmSpFileType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
