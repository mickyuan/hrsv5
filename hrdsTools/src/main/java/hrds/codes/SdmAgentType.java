package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理Agent类别  */
public enum SdmAgentType {
	/**文本流Agent<WenBenLiu>  */
	WenBenLiu("1","文本流Agent","98"),
	/**Rest接收Agent<RestJieShou>  */
	RestJieShou("2","Rest接收Agent","98");

	private final String code;
	private final String value;
	private final String catCode;

	SdmAgentType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
