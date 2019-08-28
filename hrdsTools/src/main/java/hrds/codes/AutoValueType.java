package hrds.codes;
/**Created by automatic  */
/**代码类型名：值类型  */
public enum AutoValueType {
	/**字符串<ZiFuChuan>  */
	ZiFuChuan("01","字符串","162"),
	/**数值<ShuZhi>  */
	ShuZhi("02","数值","162"),
	/**日期<RiQi>  */
	RiQi("03","日期","162"),
	/**枚举<MeiJu>  */
	MeiJu("04","枚举","162");

	private final String code;
	private final String value;
	private final String catCode;

	AutoValueType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
