package hrds.codes;
/**Created by automatic  */
/**代码类型名：预分区规则  */
public enum PrePartition {
	/**SPLITNUM<SPLITNUM>  */
	SPLITNUM("1","SPLITNUM","133"),
	/**SPLITPOINS<SPLITPOINS>  */
	SPLITPOINS("2","SPLITPOINS","133");

	private final String code;
	private final String value;
	private final String catCode;

	PrePartition(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
