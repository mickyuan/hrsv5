package hrds.codes;
/**Created by automatic  */
/**代码类型名：补齐方式  */
public enum FillingType {
	/**前补齐<QianBuQi>  */
	QianBuQi("1","前补齐","33"),
	/**后补齐<HouBuQi>  */
	HouBuQi("2","后补齐","33");

	private final String code;
	private final String value;
	private final String catCode;

	FillingType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
