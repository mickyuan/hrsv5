package hrds.codes;
/**Created by automatic  */
/**代码类型名：StreamingPro输入的数据模式  */
public enum SdmSpDataMode {
	/**批量表<PILIANGBIAO>  */
	PILIANGBIAO("1","批量表","150"),
	/**流表<LIUBIAO>  */
	LIUBIAO("2","流表","150");

	private final String code;
	private final String value;
	private final String catCode;

	SdmSpDataMode(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
