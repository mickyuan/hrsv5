package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL调度频率  */
public enum Dispatch_Frequency {
	/**天(D)<DAILY>  */
	DAILY("D","天(D)","107"),
	/**月(M)<MONTHLY>  */
	MONTHLY("M","月(M)","107"),
	/**周(W)<WEEKLY>  */
	WEEKLY("W","周(W)","107"),
	/**旬(X)<TENDAYS>  */
	TENDAYS("X","旬(X)","107"),
	/**年(Y)<YEARLY>  */
	YEARLY("Y","年(Y)","107"),
	/**频率(F)<PinLv>  */
	PinLv("F","频率(F)","107");

	private final String code;
	private final String value;
	private final String catCode;

	Dispatch_Frequency(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
