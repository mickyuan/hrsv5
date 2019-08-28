package hrds.codes;
/**Created by automatic  */
/**代码类型名：t检验样本类型  */
public enum THypothesisTestType {
	/**单样本T检验<DanYangBenTJianYan>  */
	DanYangBenTJianYan("1","单样本T检验","85"),
	/**双样本T检验<ShuangYangBenTJianYan>  */
	ShuangYangBenTJianYan("2","双样本T检验","85");

	private final String code;
	private final String value;
	private final String catCode;

	THypothesisTestType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
