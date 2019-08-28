package hrds.codes;
/**Created by automatic  */
/**代码类型名：消费方向  */
public enum ConsDirection {
	/**内部<NeiBu>  */
	NeiBu("1","内部","129"),
	/**外部<WaiBu>  */
	WaiBu("2","外部","129");

	private final String code;
	private final String value;
	private final String catCode;

	ConsDirection(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
