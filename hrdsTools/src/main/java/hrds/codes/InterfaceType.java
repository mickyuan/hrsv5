package hrds.codes;
/**Created by automatic  */
/**代码类型名：接口类型  */
public enum InterfaceType {
	/**数据类<ShuJuLei>  */
	ShuJuLei("1","数据类","30"),
	/**功能类<GongNengLei>  */
	GongNengLei("2","功能类","30"),
	/**报表类<BaoBiaoLei>  */
	BaoBiaoLei("3","报表类","30"),
	/**监控类<JianKongLei>  */
	JianKongLei("4","监控类","30");

	private final String code;
	private final String value;
	private final String catCode;

	InterfaceType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
