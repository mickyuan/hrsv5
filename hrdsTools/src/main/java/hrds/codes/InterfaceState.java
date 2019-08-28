package hrds.codes;
/**Created by automatic  */
/**代码类型名：接口状态  */
public enum InterfaceState {
	/**启用<QiYong>  */
	QiYong("1","启用","31"),
	/**禁用<JinYong>  */
	JinYong("2","禁用","31");

	private final String code;
	private final String value;
	private final String catCode;

	InterfaceState(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
