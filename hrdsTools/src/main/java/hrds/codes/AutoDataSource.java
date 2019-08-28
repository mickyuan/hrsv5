package hrds.codes;
/**Created by automatic  */
/**代码类型名：自主取数数据来源  */
public enum AutoDataSource {
	/**内部<NeiBu>  */
	NeiBu("01","内部","163"),
	/**外部<WaiBu>  */
	WaiBu("02","外部","163");

	private final String code;
	private final String value;
	private final String catCode;

	AutoDataSource(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
