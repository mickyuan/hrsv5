package hrds.codes;
/**Created by automatic  */
/**代码类型名：消费者类型  */
public enum ConsumerType {
	/**consumer<Consumer>  */
	Consumer("1","consumer","143"),
	/**streams<Streams>  */
	Streams("2","streams","143");

	private final String code;
	private final String value;
	private final String catCode;

	ConsumerType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
