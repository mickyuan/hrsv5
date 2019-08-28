package hrds.codes;
/**Created by automatic  */
/**代码类型名：用户优先级  */
public enum UserPriority {
	/**高<Gao>  */
	Gao("1","高","14"),
	/**中<Zhong>  */
	Zhong("2","中","14"),
	/**低<Di>  */
	Di("3","低","14");

	private final String code;
	private final String value;
	private final String catCode;

	UserPriority(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
