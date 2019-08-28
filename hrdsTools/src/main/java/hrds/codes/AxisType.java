package hrds.codes;
/**Created by automatic  */
/**代码类型名：可视化轴类型  */
public enum AxisType {
	/**x轴<XAxis>  */
	XAxis("1","x轴","164"),
	/**y轴<YAxis>  */
	YAxis("2","y轴","164");

	private final String code;
	private final String value;
	private final String catCode;

	AxisType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
