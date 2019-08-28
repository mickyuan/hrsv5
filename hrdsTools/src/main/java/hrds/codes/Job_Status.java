package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL作业状态  */
public enum Job_Status {
	/**完成<DONE>  */
	DONE("D","完成","109"),
	/**错误<ERROR>  */
	ERROR("E","错误","109"),
	/**挂起<PENDING>  */
	PENDING("P","挂起","109"),
	/**运行<RUNNING>  */
	RUNNING("R","运行","109"),
	/**停止<STOP>  */
	STOP("S","停止","109"),
	/**等待<WAITING>  */
	WAITING("W","等待","109");

	private final String code;
	private final String value;
	private final String catCode;

	Job_Status(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
