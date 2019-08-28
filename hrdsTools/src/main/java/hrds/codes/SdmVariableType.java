package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理变量类型  */
public enum SdmVariableType {
	/**整数<ZhengShu>  */
	ZhengShu("1","整数","100"),
	/**字符串<ZiFuChuan>  */
	ZiFuChuan("2","字符串","100"),
	/**浮点数<FuDianShu>  */
	FuDianShu("3","浮点数","100"),
	/**字节数组<ZiJieShuZu>  */
	ZiJieShuZu("4","字节数组","100");

	private final String code;
	private final String value;
	private final String catCode;

	SdmVariableType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
