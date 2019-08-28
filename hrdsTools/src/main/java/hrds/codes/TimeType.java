package hrds.codes;
/**Created by automatic  */
/**代码类型名：时间类型  */
public enum TimeType {
	/**日<Day>  */
	Day("1","日","126"),
	/**小时<Hour>  */
	Hour("2","小时","126"),
	/**分钟<Minute>  */
	Minute("3","分钟","126"),
	/**秒<Second>  */
	Second("4","秒","126");

	private final String code;
	private final String value;
	private final String catCode;

	TimeType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
