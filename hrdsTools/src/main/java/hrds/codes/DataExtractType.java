package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据抽取方式  */
public enum DataExtractType {
	/**仅数据抽取<JinShuJuChouQu>  */
	JinShuJuChouQu("1","仅数据抽取","145"),
	/**数据抽取及入库<ShuJuChouQuJiRuKu>  */
	ShuJuChouQuJiRuKu("2","数据抽取及入库","145");

	private final String code;
	private final String value;
	private final String catCode;

	DataExtractType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
