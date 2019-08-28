package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL变类型  */
public enum ParamType {
	/**路径<LuJing>  */
	LuJing("url","路径","116"),
	/**参数<CanShu>  */
	CanShu("param","参数","116");

	private final String code;
	private final String value;
	private final String catCode;

	ParamType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
