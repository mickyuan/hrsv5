package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理druid数据处理格式  */
public enum SdmDataFormat {
	/**json<Json>  */
	Json("1","json","135"),
	/**csv<CSV>  */
	CSV("2","csv","135"),
	/**regex<Regex>  */
	Regex("3","regex","135"),
	/**javascript<JavaScript>  */
	JavaScript("4","javascript","135");

	private final String code;
	private final String value;
	private final String catCode;

	SdmDataFormat(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
