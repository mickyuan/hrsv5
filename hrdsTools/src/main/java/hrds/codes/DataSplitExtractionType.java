package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据拆分抽取类型  */
public enum DataSplitExtractionType {
	/**简单随机抽取<JianDanSuiJiChouQu>  */
	JianDanSuiJiChouQu("1","简单随机抽取","68"),
	/**分层抽取<FenCengChouQu>  */
	FenCengChouQu("2","分层抽取","68");

	private final String code;
	private final String value;
	private final String catCode;

	DataSplitExtractionType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
