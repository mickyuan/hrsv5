package hrds.codes;
/**Created by automatic  */
/**代码类型名：隐藏固定数  */
public enum HideLayerFixNumber {
	/**一层<YiCeng>  */
	YiCeng("1","一层","78"),
	/**二层<ErCeng>  */
	ErCeng("2","二层","78");

	private final String code;
	private final String value;
	private final String catCode;

	HideLayerFixNumber(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
