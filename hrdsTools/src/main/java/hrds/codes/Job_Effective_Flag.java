package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETl作业有效标志  */
public enum Job_Effective_Flag {
	/**有效(Y)<YES>  */
	YES("Y","有效(Y)","110"),
	/**无效(N)<NO>  */
	NO("N","无效(N)","110"),
	/**空跑(V)<VIRTUAL>  */
	VIRTUAL("V","空跑(V)","110");

	private final String code;
	private final String value;
	private final String catCode;

	Job_Effective_Flag(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
