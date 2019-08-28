package hrds.codes;
/**Created by automatic  */
/**代码类型名：独立样本类型  */
public enum IndependentSampleStyle {
	/**配对样本t检验<PeiDuiYangBenTJianYan>  */
	PeiDuiYangBenTJianYan("1","配对样本t检验","50"),
	/**等方差双样本检验<DengFangChaShuangYangBenJianYan>  */
	DengFangChaShuangYangBenJianYan("2","等方差双样本检验","50"),
	/**异方差双样本检验<YiFangChaShuangYangBenJianYan>  */
	YiFangChaShuangYangBenJianYan("3","异方差双样本检验","50");

	private final String code;
	private final String value;
	private final String catCode;

	IndependentSampleStyle(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
