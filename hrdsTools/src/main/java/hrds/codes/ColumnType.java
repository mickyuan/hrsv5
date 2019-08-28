package hrds.codes;
/**Created by automatic  */
/**代码类型名：字段类型  */
public enum ColumnType {
	/**字符型<ZiFuXing>  */
	ZiFuXing("1","字符型","81"),
	/**数值型<ShuZhiXing>  */
	ShuZhiXing("2","数值型","81");

	private final String code;
	private final String value;
	private final String catCode;

	ColumnType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
