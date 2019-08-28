package hrds.codes;
/**Created by automatic  */
/**代码类型名：分类变量编码类型  */
public enum EncodeTypeClassificationVariables {
	/**一位有效编码<YiWeiYouXiaoBianMa>  */
	YiWeiYouXiaoBianMa("1","一位有效编码","52"),
	/**标签编码<BiaoQianBianMa>  */
	BiaoQianBianMa("2","标签编码","52");

	private final String code;
	private final String value;
	private final String catCode;

	EncodeTypeClassificationVariables(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
