package hrds.codes;
/**Created by automatic  */
/**代码类型名：模型类型  */
public enum ModelType {
	/**分类模型<FenLeiMoXing>  */
	FenLeiMoXing("1","分类模型","91"),
	/**回归模型<JuLeiMoXing>  */
	JuLeiMoXing("2","回归模型","91"),
	/**预测模型<YuCeMoXing>  */
	YuCeMoXing("3","预测模型","91"),
	/**其它算法<QiTaSuanFa>  */
	QiTaSuanFa("4","其它算法","91");

	private final String code;
	private final String value;
	private final String catCode;

	ModelType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
