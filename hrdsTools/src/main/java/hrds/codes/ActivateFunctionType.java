package hrds.codes;
/**Created by automatic  */
/**代码类型名：激活函数类型  */
public enum ActivateFunctionType {
	/**双曲正切<ShuangQuZhengQie>  */
	ShuangQuZhengQie("1","双曲正切","58"),
	/**sigmoid<sigmoid>  */
	sigmoid("2","sigmoid","58");

	private final String code;
	private final String value;
	private final String catCode;

	ActivateFunctionType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
