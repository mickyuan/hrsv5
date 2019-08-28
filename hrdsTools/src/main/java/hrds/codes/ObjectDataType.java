package hrds.codes;
/**Created by automatic  */
/**代码类型名：对象数据类型  */
public enum ObjectDataType {
	/**数组<ShuZu>  */
	ShuZu("1","数组","124"),
	/**字符串<ZiFuChuan>  */
	ZiFuChuan("2","字符串","124");

	private final String code;
	private final String value;
	private final String catCode;

	ObjectDataType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
