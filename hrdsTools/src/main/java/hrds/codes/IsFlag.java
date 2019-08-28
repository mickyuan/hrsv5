package hrds.codes;
/**Created by automatic  */
/**代码类型名：是否标识  */
public enum IsFlag {
	/**是<Shi>  */
	Shi("0","是","2"),
	/**否<Fou>  */
	Fou("1","否","2");

	private final String code;
	private final String value;
	private final String catCode;

	IsFlag(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
