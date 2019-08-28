package hrds.codes;
/**Created by automatic  */
/**代码类型名：日志等级  */
public enum LogLevel {
	/**所有<All>  */
	All("1","所有","16"),
	/**成功<Succeed>  */
	Succeed("2","成功","16"),
	/**失败<Failure>  */
	Failure("3","失败","16");

	private final String code;
	private final String value;
	private final String catCode;

	LogLevel(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
