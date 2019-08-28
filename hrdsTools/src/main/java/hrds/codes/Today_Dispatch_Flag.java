package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL当天调度标志  */
public enum Today_Dispatch_Flag {
	/**是(Y)<YES>  */
	YES("Y","是(Y)","112"),
	/**否(N)<NO>  */
	NO("N","否(N)","112");

	private final String code;
	private final String value;
	private final String catCode;

	Today_Dispatch_Flag(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
