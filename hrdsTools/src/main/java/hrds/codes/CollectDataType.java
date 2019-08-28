package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据类型  */
public enum CollectDataType {
	/**xml<XML>  */
	XML("1","xml","122"),
	/**json<JSON>  */
	JSON("2","json","122");

	private final String code;
	private final String value;
	private final String catCode;

	CollectDataType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
