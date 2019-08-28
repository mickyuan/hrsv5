package hrds.codes;
/**Created by automatic  */
/**代码类型名：组件类型  */
public enum CompType {
	/**系统内置组件<NeiZhiZuJian>  */
	NeiZhiZuJian("1","系统内置组件","21"),
	/**系统运行组件<YunXingZuJian>  */
	YunXingZuJian("2","系统运行组件","21");

	private final String code;
	private final String value;
	private final String catCode;

	CompType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
